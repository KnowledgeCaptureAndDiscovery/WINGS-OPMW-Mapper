package edu.isi.disk.opmm;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Activity;
import org.openprovenance.prov.model.Agent;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
// import prov
import org.openprovenance.prov.model.Namespace;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Used;
import org.openprovenance.prov.model.WasAttributedTo;
import org.openprovenance.prov.model.WasDerivedFrom;
import org.openprovenance.prov.model.WasGeneratedBy;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.search.EntitySearcher;

import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;

/**
 * @author Maximiliano Osorio
 * 
 */
public class Mapper {
    /**
     * Most of these will be reused from the old code, because it works.
     * The mapper initializes the catalog and calls to the template exporter.
     */
    public static final String PROVBOOK_NS = "http://www.provbook.org";
    public static final String PROVBOOK_PREFIX = "provbook";

    public static final String JIM_PREFIX = "jim";
    public static final String JIM_NS = "http://www.cs.rpi.edu/~hendler/";
    public static final String RDFS_COMMENT = "http://www.w3.org/2000/01/rdf-schema#comment";
    public static final String SKOS_DEFINITION = "http://www.w3.org/2004/02/skos/core#definition";
    public static final String PROV_DEFINITION = "http://www.w3.org/ns/prov#definition";
    // 21:39:57 2022-10-02
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
    List<OWLOntology> ontologies;
    public static final List<String> DESCRIPTION_PROPERTIES = new ArrayList<>(Arrays.asList(
            RDFS_COMMENT,
            SKOS_DEFINITION,
            PROV_DEFINITION));

    public OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    public String PREFIX_EXPORT_RESOURCE = "http://www.isi.edu/disk/resource/";
    public OntModel opmwModel;
    public OntModel sourceModel;
    public Graph tLoisGraph;
    public Graph loisGraph;
    public Graph hypothesesGraph;
    public DatasetGraph diskDataset;
    public HashMap<String, IRI> diskProperties = new HashMap<>();
    public DocumentProv prov = new DocumentProv(InteropFramework.getDefaultFactory());
    public ProvFactory pFactory = prov.factory;

    /**
     * @throws OWLOntologyCreationException
     * 
     */
    public Mapper(DatasetGraph diskDataset, String tLoisGraphId, String hypothesisGraphId, String loisGraphId)
            throws OWLOntologyCreationException {
        {
            this.opmwModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            this.diskDataset = diskDataset;
            OWLOntology diskOntology = readDiskOntology();
            readDependenciesOntology();
            prepareMappingShortProperties(diskOntology);
            hypothesesGraph = loadGraphFromDataset(hypothesisGraphId);
            tLoisGraph = loadGraphFromDataset(tLoisGraphId);
            loisGraph = loadGraphFromDataset(loisGraphId);

        }

    }

    public void map(String triggerURI) throws ParseException {
        Model tLoisGraphModel = ModelFactory.createModelForGraph(tLoisGraph);
        Bundle hypothesisBundle = pFactory.newNamedBundle(prov.qn("hypothesisBundle"), null);
        Bundle loisBundle = pFactory.newNamedBundle(prov.qn("loisBundle"), null);
        Bundle triggerBundle = pFactory.newNamedBundle(prov.qn("triggerBundle"), null);
        hypothesisBundle.setNamespace(prov.ns);
        loisBundle.setNamespace(prov.ns);
        triggerBundle.setNamespace(prov.ns);

        getTriggerLineOfInquiry(tLoisGraphModel, triggerURI, hypothesisBundle, loisBundle, triggerBundle);
        prov.document.getStatementOrBundle().add(hypothesisBundle);
        prov.document.getStatementOrBundle().add(loisBundle);
        prov.document.getStatementOrBundle().add(triggerBundle);

        String file = "test";
        prov.document.setNamespace(prov.ns);
        prov.doConversions(prov.document, file);
    }

    public Entity mapHypothesis(String hypothesisURI, Bundle hypothesisBundle) throws ParseException {
        Model hypothesesGraphModel = ModelFactory.createModelForGraph(hypothesesGraph);
        Resource hypothesisResource = hypothesesGraphModel.getResource(hypothesisURI);
        String hypothesisName = hypothesisResource.getLocalName();
        String hypothesisLabel = hypothesesGraphModel.getProperty(hypothesisResource, RDFS.label).getString();
        String dateCreated = getLiteralByProperty(hypothesisResource, hypothesesGraphModel, "dateCreated");

        Entity hypothesisEntity = pFactory.newEntity(prov.qn(hypothesisName), hypothesisLabel);
        hypothesisBundle.getStatement().add(hypothesisEntity);
        mapQuestion(hypothesesGraphModel, hypothesisResource, hypothesisEntity, hypothesisBundle);

        // Get the variables binding
        // mapHypothesisVariableBinding(hypothesesGraphModel, hypothesisResource);
        //
        // opmwModel.add(hypothesisEntity.getModel());

        return hypothesisEntity;
    }

    private void mapQuestion(Model hypothesesGraphModel, Resource hypothesisResource,
            Entity hypothesisEntity, Bundle hypothesisBundle) {
        // TODO: Enigma graph is hardcoded
        Activity activity = pFactory.newActivity(prov.qn("CreateHypothesisByQuestion"),
                "Create Hypothesis By Question");

        String enigmaGraphId = "https://raw.githubusercontent.com/KnowledgeCaptureAndDiscovery/QuestionOntology/main/development/EnigmaQuestions.xml";
        String bundleName = "EnigmaQuestions";
        Graph questionGraph = loadGraphFromDataset(enigmaGraphId);
        Model questionGraphModel = ModelFactory.createModelForGraph(questionGraph);

        // Get the question
        Statement statement = getResourceByProperty(hypothesisResource, questionGraphModel, "hasQuestion");

        // Get the variables binding from the question
        String questionNode = statement.getObject().asLiteral().getString();
        Resource questionResource = questionGraphModel.getResource(questionNode);
        String questionName = questionResource.getLocalName();
        String questionLabel = questionGraphModel.getProperty(questionResource, RDFS.label).getString();

        // Create the question entity
        Entity questionEntity = pFactory.newEntity(prov.qn(questionName, DocumentProv.ENIGMA_PREFIX), questionLabel);
        Used used = pFactory.newUsed(null, activity.getId(), questionEntity.getId(), null, null);
        WasDerivedFrom wdf = pFactory.newWasDerivedFrom(null, hypothesisEntity.getId(), questionEntity.getId());
        WasGeneratedBy wgb = pFactory.newWasGeneratedBy(null, hypothesisEntity.getId(), activity.getId(), null,
                null);
        Activity activity2 = pFactory.newActivity(prov.qn("select-values", DocumentProv.DISK_PREFIX),
                "Create values of variables");
        Used used2 = pFactory.newUsed(null, activity2.getId(), questionEntity.getId(), null, null);
        // Added to the bundle
        Bundle questionBundle = pFactory.newNamedBundle(prov.qn(bundleName), null);
        questionBundle.setNamespace(prov.ns);
        questionBundle.getStatement().addAll(Arrays.asList(questionEntity, used, wgb, used2, wdf     ));

        hypothesisBundle.getStatement().addAll(Arrays.asList(hypothesisEntity, activity, activity2));

        // Hypothesis bundle
        // Bundle oldHypothesisBundle = pFactory.newNamedBundle(prov.qn("Hypothesis"),
        // null);
        // oldHypothesisBundle.setNamespace(prov.ns);
        // ldHypothesisBundle.getStatement().addAll(Arrays.asList(hypothesisEntity,
        // activity, activity2));

        // Question variables and Hypothesis variables binding mapping
        HashMap<String, Entity> questionVariablesMap = new HashMap<>();

        // [Question-ontology] Get the variables binding from the question
        mapVariableBindingFromQuestion(questionGraphModel, questionResource, questionEntity, questionBundle,
                questionVariablesMap);
        // [DISK] Get the variables binding from the hypothesis
        mapVariableFromHypothesis(hypothesesGraphModel, hypothesisResource, hypothesisEntity, hypothesisBundle,
                questionGraphModel, activity, questionEntity, activity2);

        StmtIterator getVariableBindingsOnHypothesisGraph = getResourcesByProperty(hypothesisResource,
                hypothesesGraphModel,
                "hasVariableBinding");

        getVariableBindingsOnHypothesisGraph.forEachRemaining(statementHypothesis -> {
            Resource variableBindingResource = statementHypothesis.getObject().asResource();
            // Create the variable binding entity
            String variableLocalName = variableBindingResource.getLocalName();
            String value = getLiteralByProperty(variableBindingResource, hypothesesGraphModel, "hasBindingValue");
            String hvLabel = value;
            Entity variableBindingEntity = pFactory.newEntity(prov.qn(variableLocalName), hvLabel);

            WasGeneratedBy hytpothesisWgb = pFactory.newWasGeneratedBy(null, variableBindingEntity.getId(),
                    activity2.getId(),
                    null, null);
            hypothesisBundle.getStatement().addAll(Arrays.asList(variableBindingEntity, hytpothesisWgb));
        });

        prov.document.getStatementOrBundle().add(questionBundle);
    }

    private void mapVariableFromHypothesis(Model hypothesesGraphModel, Resource hypothesisResource,
            Entity hypothesisEntity,
            Bundle diskBundle, Model questionGraphModel, Activity activity, Entity questionEntity, Activity activity2) {
        StmtIterator getVariableBindingsOnHypothesisGraph = getResourcesByProperty(hypothesisResource,
                hypothesesGraphModel,
                "hasVariableBinding");

    }

    private void mapVariableBindingFromQuestion(Model questionGraphModel, Resource questionResource,
            Entity questionEntity,
            Bundle questionBundle, HashMap<String, Entity> questionVariablesMap) {
        StmtIterator statements = questionResource
                .listProperties(questionGraphModel.getProperty("https://w3id.org/sqo#hasQuestionVariable"));
        Property hasVariableName = questionGraphModel.getProperty("https://w3id.org/sqo#hasVariableName");

        statements.forEachRemaining(s -> {
            Resource questionVariableResource = s.getObject().asResource();
            String qvName = questionVariableResource.getLocalName();
            String qvLabel = questionGraphModel.getProperty(questionVariableResource, hasVariableName).getString();
            Entity questionVariableEntity = pFactory.newEntity(prov.qn(qvName, DocumentProv.ENIGMA_PREFIX), qvLabel);
            WasDerivedFrom wdf = pFactory.newWasDerivedFrom(null, questionEntity.getId(),
                    questionVariableEntity.getId());
            questionBundle.getStatement().addAll(Arrays.asList(questionVariableEntity, wdf));
            questionVariablesMap.put(questionVariableResource.getURI(), questionVariableEntity);
        });
    }

    public Entity mapLineOfInquiry(String lineOfInquiryURI, Bundle loisBundle, Entity hypothesis)
            throws ParseException {
        Model loisGraphModel = ModelFactory.createModelForGraph(loisGraph);
        Resource lineOfInquiryResource = loisGraphModel.getResource(lineOfInquiryURI);
        String name = lineOfInquiryResource.getLocalName();
        String label = loisGraphModel.getProperty(lineOfInquiryResource, RDFS.label).getString();
        String dateCreated = getLiteralByProperty(lineOfInquiryResource, loisGraphModel, "dateCreated");
        Entity lineOfInquiryEntity = pFactory.newEntity(prov.qn(name, DocumentProv.DISK_PREFIX), label);

        Resource metaWorkflowBinding = getResourceByProperty(lineOfInquiryResource, loisGraphModel, name).getResource();
        Resource workflowBinding = getResourceByProperty(metaWorkflowBinding, loisGraphModel, "workflowBinding").getResource();


        String activityName = "LOI_creation_" + name;
        Activity activity = pFactory.newActivity(prov.qn(activityName, DocumentProv.DISK_PREFIX), null, null, null);

        WasGeneratedBy wgb = pFactory.newWasGeneratedBy(null, lineOfInquiryEntity.getId(), activity.getId(), null,
                null);
        Used used = pFactory.newUsed(null, activity.getId(), hypothesis.getId(), null, null);
        WasDerivedFrom wdf = pFactory.newWasDerivedFrom(null, lineOfInquiryEntity.getId(), hypothesis.getId(), null,
                null, null, null);
        loisBundle.getStatement().addAll(Arrays.asList(lineOfInquiryEntity, activity, wgb, used, wdf));

        Entity workflowBinding = pFactory.newEntity(prov.qn("workflowBinding", DocumentProv.DISK_PREFIX),
                "workflowBinding");
        WasGeneratedBy wgb2 = pFactory.newWasGeneratedBy(null, workflowBinding.getId(), activity.getId(), null,
                null);

        Entity dataQueryTemplate = pFactory.newEntity(prov.qn("dataQueryTemplate", DocumentProv.DISK_PREFIX),
                "dataQueryTemplate");
        WasGeneratedBy wgb3 = pFactory.newWasGeneratedBy(null, dataQueryTemplate.getId(), activity.getId(), null,
                null);

        loisBundle.getStatement().addAll(Arrays.asList(workflowBinding, wgb2, dataQueryTemplate, wgb3));

        return lineOfInquiryEntity;
        // Entity lineOfInquiryEntity = new Entity("http://provenance.isi.edu/entities/"
        // + name, label,
        // label);
        // opmwModel.add(lineOfInquiryEntity.getModel());
    }

    public void getTriggerLineOfInquiry(Model tLoisGraphModel, String triggerURI, Bundle hypothesisBundle,
            Bundle loisBundle, Bundle triggerBundle)
            throws ParseException {
        Node triggerNode = NodeFactory.createURI(triggerURI);
        /**
         * Obtain the information from the trigger
         */

        Resource triggerResource = tLoisGraphModel.getResource(triggerNode.getURI());
        if (triggerResource == null) {
            System.out.println("Trigger resource not found");
            throw new RuntimeException("Trigger resource not found");
        }
        String triggerLineName = triggerNode.getLocalName();
        String label = tLoisGraphModel.getProperty(triggerResource, RDFS.label).getString();

        String dateCreated = getLiteralByProperty(triggerResource, tLoisGraphModel, "dateCreated");
        String hasDataQuery = getLiteralByProperty(triggerResource, tLoisGraphModel, "hasDataQuery");
        String hasDataQueryDescription = getLiteralByProperty(triggerResource, tLoisGraphModel, "dataQueryDescription");
        Resource lineOfInquiry = getResourcesByProperty(triggerNode, tLoisGraphModel, "hasLineOfInquiry");
        Resource hypothesisResource = getResourcesByProperty(triggerNode, tLoisGraphModel, "hasParentHypothesis");

        Entity parentHypothesis = mapHypothesis(hypothesisResource.getURI(), hypothesisBundle);

        Entity lineOfInquiryEntity = mapLineOfInquiry(lineOfInquiry.getURI(), loisBundle, parentHypothesis);

        Entity triggerLineOfInquiryEntity = pFactory.newEntity(prov.qn(triggerLineName, DocumentProv.DISK_PREFIX),
                label);
        WasDerivedFrom wdf = pFactory.newWasDerivedFrom(triggerLineOfInquiryEntity.getId(), lineOfInquiryEntity.getId());

        Activity activity = pFactory.newActivity(prov.qn(triggerLineName + "_update", DocumentProv.DISK_PREFIX),
                label);
        Used used = pFactory.newUsed(null, activity.getId(), lineOfInquiryEntity.getId());
        WasGeneratedBy wgb = pFactory.newWasGeneratedBy(null, triggerLineOfInquiryEntity.getId(), activity.getId());

        triggerBundle.getStatement().addAll(Arrays.asList(triggerLineOfInquiryEntity, wdf, activity, used, wgb));




        // Entity dataQueryEntity = pFactory.newEntity(prov.qn(triggerLineName +
        // "data_query", DocumentProv.DISK_PREFIX),
        // hasDataQueryDescription);
        // Activity dataQueryActivity = pFactory.newActivity(prov.qn(triggerLineName +
        // "data_query_activity",
        // DocumentProv.DISK_PREFIX), hasDataQueryDescription);
        // WasDerivedFrom wdf = pFactory.newWasDerivedFrom(null,
        // triggerLineOfInquiryEntity.getId(),
        // dataQueryEntity.getId());
        // Used used = pFactory.newUsed(null, dataQueryActivity.getId(),
        // dataQueryEntity.getId());
        // diskBundle.getStatement().add(used);

        // // Create a new prov activity with name: localName + "data_query_activity"
        // String dataActivityLocalName = triggerLineName + "/data_query_activity";
        // Individual dataActivityInstance = createActivity(dateCreatedDate,
        // hasDataQueryDescription, diskAgent, diskUser,
        // dataActivityLocalName, null);

        // // Create a new entity for the data query
        // String dataQueryLocalName = triggerLineName + "/data_query";
        // createEntity(dateCreatedDate, hasDataQuery, hasDataQueryDescription,
        // entityInstance, dataActivityInstance,
        // dataQueryLocalName);

        // // Create a new prov activity with name: localName +
        // // "meta_workflow_start_activity"

        // mappingRuns(tloiNode, tloiModel, triggerLineName, dateCreatedDate, diskUser,
        // diskAgent, entityInstance);
    }

    private void mappingRuns(Node tloiNode, Model tloiModel, String triggerLineName, Date dateCreatedDate,
            Individual diskUser, Individual diskAgent, Individual entityInstance) throws ParseException {
        // Create a new entity for the execution of the meta workflow
        // Obtain the object property
        // http://disk-project.org/ontology/disk#hasMetaWorkflowBinding from the trigger
        Resource metaWorkflowBindingResource = getResourcesByProperty(tloiNode, tloiModel, "hasMetaWorkflowBinding");
        if (metaWorkflowBindingResource != null) {
            IRI metaWorkflowBindingIRI = getIRIByProperty(metaWorkflowBindingResource, tloiModel, "hasId");
            IRI metaWorkflowRunBindingIRI = getIRIByProperty(metaWorkflowBindingResource, tloiModel, "hasRunLink");
            String hasRunEndString = getLiteralByProperty(metaWorkflowBindingResource, tloiModel, "hasRunEndDate");
            String hasRunStartString = getLiteralByProperty(metaWorkflowBindingResource, tloiModel, "hasRunStartDate");
            String hasRunStatus = getLiteralByProperty(metaWorkflowBindingResource, tloiModel, "hasRunStatus");
            String hasSourceString = getLiteralByProperty(metaWorkflowBindingResource, tloiModel, "hasSource");

            Date hasRunStartDate = hasRunStartString != null ? DATE_FORMAT.parse(hasRunStartString) : null;
            Date hasRunEndDate = hasRunEndString != null ? DATE_FORMAT.parse(hasRunEndString) : null;

            String runLocalName = triggerLineName + "/executions/"
                    + metaWorkflowBindingIRI.getShortForm();
            String runActivityLocalName = runLocalName + "/start_activity";
            Individual runActivityInstance = createActivity(hasRunStartDate, runActivityLocalName,
                    diskAgent,
                    diskUser, runActivityLocalName, hasRunEndDate);

            Individual runEntity = createEntity(dateCreatedDate, metaWorkflowBindingIRI.toString(),
                    metaWorkflowBindingIRI.toString(),
                    entityInstance,
                    runActivityInstance,
                    runLocalName);

        }
        // Obtain the object property
        // http://disk-project.org/ontology/disk#hasWorkflowBinding from the trigger
        Resource workflowBindingResource = getResourcesByProperty(tloiNode, tloiModel, "hasWorkflowBinding");
        IRI workflowBindingIRI = null;
        IRI workflowRunBindingIRI = null;
        if (workflowBindingResource != null) {
            workflowBindingIRI = getIRIByProperty(workflowBindingResource, tloiModel, "hasId");
            workflowRunBindingIRI = getIRIByProperty(workflowBindingResource, tloiModel, "hasRunId");
        }

    }

    private Resource getResourcesByProperty(Node node, Model model, String propertyName) {
        IRI hasWorkflowBindingIRI = diskProperties.get(propertyName);
        Property hasWorkflowBindingProperty = model.getProperty(hasWorkflowBindingIRI.toString());
        Statement hasWorkflowBindingStatement = model.getResource(node.getURI())
                .getProperty(hasWorkflowBindingProperty);
        if (hasWorkflowBindingStatement != null) {
            return hasWorkflowBindingStatement.getObject().asResource();
        }
        return null;
    }

    private StmtIterator getResourcesByProperty(Resource resource, Model model, String propertyName) {
        IRI propertyIRI = diskProperties.get(propertyName);
        Property property = model.getProperty(propertyIRI.toString());
        return resource.listProperties(property);
    }

    private Statement getResourceByProperty(Resource resource, Model model, String propertyName) {
        IRI propertyIRI = diskProperties.get(propertyName);
        Property property = model.getProperty(propertyIRI.toString());
        return resource.getProperty(property);
    }

    private String getLiteralByProperty(Resource resource, Model tloiModel, String propertyName) {
        IRI propertyIRI = diskProperties.get(propertyName);
        if (propertyIRI == null) {
            System.out.println("Property " + propertyName + " not found");
            return null;
        }
        Property property = tloiModel.getProperty(propertyIRI.toString());
        Statement statement = resource.getProperty(property);
        if (statement != null) {
            if (statement.getObject().isLiteral()) {
                return statement.getObject().asLiteral().getString();
            }
        }
        return null;
    }

    private IRI getIRIByProperty(Resource resource, Model tloiModel, String propertyName) {
        IRI propertyIRI = diskProperties.get(propertyName);
        Property property = tloiModel.getProperty(propertyIRI.toString());
        Statement statement = resource.getProperty(property);
        if (statement != null) {
            if (statement.getObject().isURIResource()) {
                String uri = statement.getObject().asResource().getURI();
                return IRI.create(uri);
            } else if (statement.getObject().isLiteral()) {
                String literal = statement.getObject().asLiteral().getString();
                return IRI.create(literal);
            }
        }
        return null;
    }

    private Literal formatXSDDate(Date date) {
        // prov:startedAtTime "2012-04-25T01:30:00Z"^^xsd:dateTime;
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String dateStr = sdf.format(date);
        return opmwModel.createTypedLiteral(dateStr, XSDDatatype.XSDdateTime);
    }

    private Individual createEntity(Date dateCreated, String comment, String label,
            Individual entityInstance, Individual generatedBy, String localName) {
        Individual entity = createIndividual(opmwModel, PREFIX_EXPORT_RESOURCE, localName,
                Constants.PROV_ENTITY);
        // Add the prov entity type
        entity.addProperty(RDF.type, opmwModel.createResource(Constants.PROV_ENTITY));
        // Add the label
        entity.addProperty(RDFS.label, label);
        // Add the description
        entity.addProperty(RDFS.comment, comment);
        // Add prov:generatedAtTime property to the entity, use the date created

        entity.addProperty(opmwModel.createProperty(Constants.PROV_PROP_GENERATED_AT_TIME),
                formatXSDDate(dateCreated));
        // Add prov:wasGeneratedBy property to the entity, use the activity
        entity.addProperty(opmwModel.createProperty(Constants.PROV_PROP_WAS_GENERATED_BY),
                generatedBy);
        // Add prov: used property to the entity, use the trigger
        entity.addProperty(opmwModel.createProperty(Constants.PROV_PROP_USED), entityInstance);
        return entity;
    }

    private Individual createActivity(Date dateCreated, String label, Individual whoStartedIt,
            Individual whoOnBehalfOf, String activityLocalName, Date endedAtTime) {
        Individual activityInstance = createIndividual(opmwModel, PREFIX_EXPORT_RESOURCE, activityLocalName,
                Constants.PROV_ACTIVITY);
        // Add the prov activity type
        activityInstance.addProperty(RDF.type, opmwModel.createResource(Constants.PROV_ACTIVITY));
        // Add the label
        activityInstance.addProperty(RDFS.label, label);
        // Add prov:wasStartedBy property to the activity, the disk agent
        activityInstance.addProperty(opmwModel.createProperty(Constants.PROV_PROP_WAS_STARTED_BY),
                whoStartedIt);
        // add: startedAtTime
        if (dateCreated != null)
            activityInstance.addProperty(opmwModel.createProperty(Constants.PROV_PROP_STARTED_AT_TIME),
                    formatXSDDate(dateCreated));
        // add: endedAtTime
        if (endedAtTime != null)
            activityInstance.addProperty(opmwModel.createProperty(Constants.PROV_PROP_ENDED_AT_TIME),
                    formatXSDDate(endedAtTime));

        return activityInstance;
    }

    private Individual createTriggerLine(String localName, String label, Date dateCreated,
            Resource hasLineOfInquiryResource) {
        Individual entityInstance = createIndividual(opmwModel, PREFIX_EXPORT_RESOURCE, localName,
                Constants.PROV_ENTITY);
        // Add the prov entity type
        entityInstance.addProperty(RDF.type, opmwModel.createResource(Constants.PROV_ENTITY));
        // Add the label
        entityInstance.addProperty(RDFS.label, label);
        // Add prov:generatedAtTime property to the entity, use the date created
        entityInstance.addProperty(opmwModel.createProperty(Constants.PROV_PROP_GENERATED_AT_TIME),
                formatXSDDate(dateCreated));
        // Trigger Line of Inquiry was derived by the Line of Inquiry
        entityInstance.addProperty(opmwModel.createProperty(Constants.PROV_PROP_WAS_DERIVED_FROM),
                hasLineOfInquiryResource);
        return entityInstance;
    }

    private Individual createAgent(String localName, String label, Individual whoOnBehalfOf) {
        Individual agent = createIndividual(opmwModel, PREFIX_EXPORT_RESOURCE, localName,
                Constants.PROV_AGENT);
        // Add the prov agent type
        agent.addProperty(RDF.type, opmwModel.createResource(Constants.PROV_AGENT));
        // Add the label
        agent.addProperty(RDFS.label, label);
        // on behalf of the disk user
        if (whoOnBehalfOf != null) {
            agent.addProperty(opmwModel.createProperty(Constants.PROV_PROP_ACTED_ON_BEHALF_OF),
                    whoOnBehalfOf);
        }
        return agent;
    }

    public void mapExecution(OntModel model) {
        final String executionPrefix = PREFIX_EXPORT_RESOURCE + Constants.CONCEPT_WORKFLOW_EXECUTION_ACCOUNT + "/";

        // TODO: Replace with the actual execution ID
        String runID = "run1";
        String executionWorkflowURI = "http://www.isi.edu/disk/resource/Workflow/1";
        String start = "2018-01-01T00:00:00";
        String end = "2018-01-01T00:00:00";
        String status = "COMPLETED";
        String user = "user1";

        // create the execution individual
        Individual weInstance = createIndividual(model, executionPrefix, runID,
                Constants.CONCEPT_WORKFLOW_EXECUTION_ACCOUNT);
        weInstance.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_HAS_RUN_ID), runID);
        weInstance.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_HAS_ORIGINAL_EXECUTION_FILE),
                executionWorkflowURI);
        weInstance.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_OVERALL_START_TIME), start);
        weInstance.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_OVERALL_END_TIME), end);
        weInstance.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_STATUS), status);

        // add the user
        String userPrefix = PREFIX_EXPORT_RESOURCE + Constants.PROV_AGENT + "/";
        Individual userInstance = createIndividual(model, userPrefix, user, Constants.PROV_AGENT);
        userInstance.addLabel(user, null);
        weInstance.addProperty(opmwModel.createProperty(Constants.PROP_HAS_CONTRIBUTOR), userInstance);

        // add the execution to the workflow
    }

    public static Individual createIndividual(OntModel model, String iriPrefix, String localName, String type) {
        String iri = iriPrefix + localName;
        Individual i = model.createIndividual(iri, model.getResource(type));
        return i;
    }

    public static Individual createIndividual(OntModel model, Resource resource) {
        Individual i = model.createIndividual(resource);
        return i;
    }

    public static void printElement(IRI iri, String description) {
        System.out.println(iri + "&" + description);
    }

    public static void printElement(IRI iri, String description, String domain, String range) {
        System.out.println(iri + "&" + description + " Domain: " + domain + " Range: " + range);
    }

    public static void listClasses(OWLOntology o) {
        // TODO Auto-generated method stub
        for (OWLEntity c : o.getClassesInSignature()) {
            String description = getDescription(c, o);
            description = description.split("\\r?\\n")[0];
            printElement(c.getIRI(), description);
        }
    }

    public static void listObjectProperties(OWLOntology o, Boolean include) {
        // TODO Auto-generated method stub
        Set<OWLObjectProperty> allProperties;
        if (include == true) {
            allProperties = o.getObjectPropertiesInSignature(Imports.INCLUDED);
        } else {
            allProperties = o.getObjectPropertiesInSignature();
        }
        for (OWLObjectProperty p : allProperties) {
            String description = getDescription(p, o);
            description = description.split("\\r?\\n")[0];
            printElement(p.getIRI(), description);
        }

    }

    /**
     * @param o
     * @param include
     */
    public static void listDataProperties(OWLOntology o, Boolean include) {
        // TODO Auto-generated method stub
        Set<OWLDataProperty> allProperties;
        if (include == true) {
            allProperties = o.getDataPropertiesInSignature(Imports.INCLUDED);
        } else {
            allProperties = o.getDataPropertiesInSignature();
        }
        for (OWLDataProperty p : allProperties) {
            String description = getDescription(p, o);
            description = description.split("\\r?\\n")[0];
            printElement(p.getIRI(), description);
        }

    }

    /**
     * Method that given a class, property or data property, searches for the best
     * description.
     * 
     * @param entity   entity to search.
     * @param ontology ontology to be used to search descriptions.
     * @return Description String (prioritizes English language)
     */
    public static String getDescription(OWLEntity entity, OWLOntology ontology) {
        String descriptionValue = "Description not available";
        for (String description : DESCRIPTION_PROPERTIES) {
            Object[] annotationsObjects = EntitySearcher
                    .getAnnotationObjects(entity, ontology, new OWLAnnotationPropertyImpl(new IRI(description) {
                    })).toArray();
            if (annotationsObjects.length != 0) {
                Optional<OWLLiteral> descriptionLiteral;
                for (Object annotation : annotationsObjects) {
                    descriptionLiteral = ((OWLAnnotation) annotation).getValue().asLiteral();
                    if (descriptionLiteral.isPresent()) {
                        if (annotationsObjects.length == 1 || descriptionLiteral.get().getLang().equals("en")) {
                            descriptionValue = descriptionLiteral.get().getLiteral();
                        }
                    }
                }
                break;
            }
        }
        return descriptionValue;
    }

    private void prepareMappingShortProperties(OWLOntology diskOntology) {
        /**
         * We need to map the properties to their short form, because the
         * templates are using the short form.
         */
        diskOntology.getDataPropertiesInSignature().forEach(
                t -> {
                    diskProperties.put(t.getIRI().getShortForm(), t.getIRI());
                });

        diskOntology.getObjectPropertiesInSignature().forEach(
                t -> {
                    diskProperties.put(t.getIRI().getShortForm(), t.getIRI());
                });

        // TODO: Workaround, the triples are wrong
        // https://github.com/KnowledgeCaptureAndDiscovery/DISK-API/issues/93
        // http://disk-project.org/ontology/disk#hasDataQueryDescription

        String incorrectHasDataQueryDescriptionIRI = "http://disk-project.org/ontology/disk#dataQueryDescription";
        diskProperties.remove("hasDataQueryDescription");
        diskProperties.put("hasDataQueryDescription", IRI.create(incorrectHasDataQueryDescriptionIRI));
    }

    private void readDependenciesOntology() throws OWLOntologyCreationException {
        IRI opmwOntologyIRI = IRI.create("https://www.opmw.org/model/OPMW/opmw3.1.owl");
        // read a ontology written in the file system
        File opmvFile = new File(
                "/home/mosorio/repos/wings-project/DISK-OPMW-Mapper/src/main/resources/ontologies/opmv.ttl");
        File pplanFile = new File(
                "/home/mosorio/repos/wings-project/DISK-OPMW-Mapper/src/main/resources/ontologies/p-plan.owl");
        OWLOntology pplanOntology = manager.loadOntologyFromOntologyDocument(pplanFile);
        OWLOntology opmvOntology = manager.loadOntologyFromOntologyDocument(opmvFile);
        OWLOntology opmwOntology = manager.loadOntologyFromOntologyDocument(opmwOntologyIRI);
    }

    private OWLOntology readDiskOntology()
            throws OWLOntologyCreationException {

        OWLOntology diskOntology;
        IRI diskOntologyIRI = IRI.create(
                "https://knowledgecaptureanddiscovery.github.io/DISK-Ontologies/disk/release/1.2.4/ontology.ttl");
        diskOntology = manager.loadOntologyFromOntologyDocument(diskOntologyIRI);
        return diskOntology;
    }

    private Graph loadGraphFromDataset(String graphName) {
        Node tloiGraphNode = NodeFactory.createURI(graphName);
        Graph graph = diskDataset.getGraph(tloiGraphNode);
        if (graph == null) {
            throw new RuntimeException("Graph " + graphName + " not found in dataset");
        }
        // Count the number of triples in the graph
        ExtendedIterator<Triple> triples = graph.find();
        int count = 0;
        while (triples.hasNext()) {
            count++;
            triples.next();
        }
        System.out.println("Graph " + graphName + " has " + count + " triples");
        return graph;
    }
}

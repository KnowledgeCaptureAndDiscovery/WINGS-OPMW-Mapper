package edu.isi.disk.opmm;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import org.openprovenance.prov.model.ActedOnBehalfOf;
import org.openprovenance.prov.model.Activity;
import org.openprovenance.prov.model.Agent;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.HadMember;
// import prov
import org.openprovenance.prov.model.Namespace;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.StatementOrBundle;
import org.openprovenance.prov.model.Used;
import org.openprovenance.prov.model.Value;
import org.openprovenance.prov.model.WasAttributedTo;
import org.openprovenance.prov.model.WasDerivedFrom;
import org.openprovenance.prov.model.WasGeneratedBy;
import org.openprovenance.prov.model.WasInformedBy;
import org.openprovenance.prov.notation.PROV_NParser.document_return;
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
    public Graph questionGraph;
    public DatasetGraph diskDataset;
    public HashMap<String, IRI> diskProperties = new HashMap<>();
    public DocumentProv prov = new DocumentProv(InteropFramework.getDefaultFactory());
    public ProvFactory pFactory = prov.factory;
    Bundle questionBundle = pFactory.newNamedBundle(prov.qn("questionBundle"), null);
    Bundle hypothesisBundle = pFactory.newNamedBundle(prov.qn("hypothesisBundle"), null);
    Bundle loisBundle = pFactory.newNamedBundle(prov.qn("loisBundle"), null);
    Bundle triggerBundle = pFactory.newNamedBundle(prov.qn("triggerBundle"), null);
    StatementOrBundle globalBundle;
    Model tLoisGraphModel;
    Model loisGraphModel;
    Model hypothesesGraphModel;
    Model questionGraphModel;
    Integer level = 2;

    /**
     * @throws OWLOntologyCreationException
     * 
     */
    public Mapper(DatasetGraph diskDataset, String tLoisGraphId, String hypothesisGraphId, String loisGraphId,
            String questionGraphId)
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
            questionGraph = loadGraphFromDataset(questionGraphId);
            hypothesisBundle.setNamespace(prov.ns);
            loisBundle.setNamespace(prov.ns);
            triggerBundle.setNamespace(prov.ns);
            questionBundle.setNamespace(prov.ns);
            tLoisGraphModel = ModelFactory.createModelForGraph(tLoisGraph);
            loisGraphModel = ModelFactory.createModelForGraph(loisGraph);
            hypothesesGraphModel = ModelFactory.createModelForGraph(hypothesesGraph);
            questionGraphModel = ModelFactory.createModelForGraph(questionGraph);
        }

    }

    public void transform(String triggerURI) throws ParseException {
        /**
         * Create the bundle:
         * Question
         * Hypothesis
         * Lois
         * Trigger
         */
        String file = "test";
        // The map function will handle the transformation of the resources
        map(tLoisGraphModel, triggerURI);
        prov.document.getStatementOrBundle().add(questionBundle);
        prov.document.getStatementOrBundle().add(hypothesisBundle);
        prov.document.getStatementOrBundle().add(loisBundle);
        prov.document.getStatementOrBundle().add(triggerBundle);
        prov.document.setNamespace(prov.ns);
        prov.doConversions(prov.document, file);
    }

    public void map(Model tLoisGraphModel, String triggerURI) throws ParseException {
        Node triggerNode = NodeFactory.createURI(triggerURI);
        Resource triggerResource = getTriggerResource(triggerURI);
        Resource lineOfInquiry = loisGraphModel
                .getResource(getResourceByProperty(triggerResource, tLoisGraphModel, "hasLineOfInquiry").getURI());
        Resource hypothesisResource = hypothesesGraphModel
                .getResource(getResourceByProperty(triggerResource, tLoisGraphModel, "hasParentHypothesis").getURI());
        Resource questionResource = questionGraphModel
                .getResource(getIRIByProperty(hypothesisResource, hypothesesGraphModel, "hasQuestion").toString());

        Agent hvargas = createDummyAgent("hvargas", "Hernan Vargas");
        Agent neda = createDummyAgent("neda", "Neda Jahanshad");
        prov.document.getStatementOrBundle().add(hvargas);
        prov.document.getStatementOrBundle().add(neda);

        Entity questionEntity = createQuestionEntity(questionResource);
        Entity hypothesisEntity = createHypothesisEntity(hypothesisResource);
        Entity lineOfInquiryEntity = createLineOfInquiryEntity(lineOfInquiry);
        Entity triggerEntity = createTriggerEntity(triggerResource);

        Activity createQuestionActivity = linkActivityEntities(hvargas, neda, questionEntity, questionBundle,
                "createQuestion", "Create Question");
        Activity createHypothesisActivity = linkActivityEntities(hvargas, neda, hypothesisEntity, hypothesisBundle,
                "createHypothesis", "Create Hypothesis");

        level1WasDerived(questionEntity, hypothesisEntity, lineOfInquiryEntity, triggerEntity);

        if (level > 1) {
            List<Entity> questionVariablesEntities = level2QuestionAddVariables(questionResource, questionEntity, null,
                    createQuestionActivity);
            // oneActivityGeneratesMultipleEntities(hvargas, neda,
            // questionVariablesEntities, questionBundle, "createHypothesisVariables",
            // "Create Hypothesis Variables");
            level2HypothesisAddVariables(hypothesisResource, hypothesisEntity, null, createHypothesisActivity);
            level2LineAdd(lineOfInquiry, hypothesisEntity, questionEntity, lineOfInquiryEntity);
        }
    }

    private Activity linkActivityEntities(Agent delegate, Agent responsible, Entity generated, Bundle bundle,
            String activityName, String activityLabel) {
        Activity activity = pFactory.newActivity(prov.qn(activityName), activityLabel);
        WasGeneratedBy wasGeneratedBy = pFactory.newWasGeneratedBy(null, generated.getId(), activity.getId(),
                null, null);
        ActedOnBehalfOf actedOnBehalfOf = pFactory.newActedOnBehalfOf(null, delegate.getId(), responsible.getId(),
                activity.getId(), null);
        prov.document.getStatementOrBundle().add(actedOnBehalfOf);
        bundle.getStatement().add(activity);
        bundle.getStatement().add(wasGeneratedBy);
        return activity;
    }

    private void oneActivityGeneratesMultipleEntities(Agent delegate, Agent responsible, List<Entity> generatedEntities,
            Bundle bundle, String activityName, String activityLabel) {
        Activity activity = pFactory.newActivity(prov.qn(activityName), activityLabel);
        for (Entity generatedEntity : generatedEntities) {
            WasGeneratedBy wasGeneratedBy = pFactory.newWasGeneratedBy(null, generatedEntity.getId(), activity.getId(),
                    null, null);
            bundle.getStatement().add(wasGeneratedBy);
        }
        ActedOnBehalfOf actedOnBehalfOf = pFactory.newActedOnBehalfOf(null, delegate.getId(), responsible.getId(),
                activity.getId(), null);
        bundle.getStatement().add(actedOnBehalfOf);
        bundle.getStatement().add(activity);
    }

    public Agent createDummyAgent(String localName, String label) {
        Agent agent = pFactory.newAgent(prov.qn(localName, DocumentProv.DISK_PREFIX), label);
        return agent;
    }

    public void level2LineAdd(Resource lineOfInquiryResource, Entity hypothesis, Entity questionEntity,
            Entity lineOfInquiryEntity)
            throws ParseException {
        String dateCreated = getLiteralByProperty(lineOfInquiryResource, loisGraphModel, "dateCreated");
        String dataQuery = getLiteralByProperty(lineOfInquiryResource, loisGraphModel, "hasDataQuery");
        String dataQueryDescription = getLiteralByProperty(lineOfInquiryResource, loisGraphModel,
                "dataQueryDescription");

        Resource metaWorkflowBindingResource = null;
        Resource workflowBindingResource = null;
        try {
            metaWorkflowBindingResource = getResourceByProperty(lineOfInquiryResource, loisGraphModel,
                    "hasMetaWorkflowBinding");
        } catch (Exception e) {
            System.out.println("No metaWorkflowBinding for " + lineOfInquiryResource);
        }
        try {
            workflowBindingResource = getResourceByProperty(lineOfInquiryResource, loisGraphModel,
                    "hasWorkflowBinding");
        } catch (Exception e) {
            System.out.println("No workflowBinding for " + lineOfInquiryResource);
        }

        // create the activity for writing the data query
        Activity activitySelectQuestion = pFactory.newActivity(prov.qn("select_question", DocumentProv.DISK_PREFIX),
                "Select the question");
        Activity activitySelectDataSource = pFactory.newActivity(
                prov.qn("select_data_source", DocumentProv.DISK_PREFIX),
                "Select the data source");
        Activity activityWriteDataQueryTemplate = pFactory
                .newActivity(prov.qn("write_data_query_template", DocumentProv.DISK_PREFIX),
                        "Write data query template");
        Activity activitySavedLineOfInquiry = pFactory
                .newActivity(prov.qn("saved_line_of_inquiry", DocumentProv.DISK_PREFIX), "Saved line of inquiry");

        WasInformedBy wib = pFactory.newWasInformedBy(null, activitySelectDataSource.getId(),
                activitySelectQuestion.getId(), null);
        WasInformedBy wib1 = pFactory.newWasInformedBy(null, activityWriteDataQueryTemplate.getId(),
                activitySelectDataSource.getId(), null);
        loisBundle.getStatement().add(activitySelectQuestion);
        loisBundle.getStatement().add(activitySelectDataSource);
        loisBundle.getStatement().add(activityWriteDataQueryTemplate);
        loisBundle.getStatement().add(activitySavedLineOfInquiry);
        loisBundle.getStatement().add(wib);
        loisBundle.getStatement().add(wib1);

        Entity dataQueryTemplateEntity = pFactory.newEntity(prov.qn("data_query", DocumentProv.DISK_PREFIX),
                dataQueryDescription);
        // TODO: Rename the data source
        Entity dataSource = pFactory.newEntity(prov.qn("data_source", DocumentProv.DISK_PREFIX), dataQueryDescription);
        Entity workflowSystem = pFactory.newEntity(prov.qn("wings_system", DocumentProv.DISK_PREFIX), "Wings system");

        loisBundle.getStatement().add(dataQueryTemplateEntity);
        prov.document.getStatementOrBundle().add(workflowSystem);
        prov.document.getStatementOrBundle().add(dataSource);

        // Activties details
        // 1. Select the question
        Used usedSelectQuestion = pFactory.newUsed(null, activitySelectQuestion.getId(), questionEntity.getId(), null,
                null);
        // TODO: generate the variables ???
        // 2. Select the data source
        WasGeneratedBy wgbSelectDataSource = pFactory.newWasGeneratedBy(null, dataSource.getId(),
                activitySelectDataSource.getId(), null, null);
        // 3. Write data query template
        Used usedWriteDataQueryTemplate = pFactory.newUsed(null, activityWriteDataQueryTemplate.getId(),
                questionEntity.getId(), null, null);
        WasGeneratedBy wgbWriteDataQueryTemplate = pFactory.newWasGeneratedBy(null,
                dataQueryTemplateEntity.getId(), activityWriteDataQueryTemplate.getId(), null, null);
        // 8. Save the line of inquiry
        WasGeneratedBy wgbSavedLineOfInquiry = pFactory.newWasGeneratedBy(null, lineOfInquiryEntity.getId(),
                activitySavedLineOfInquiry.getId(), null, null);

        if (workflowBindingResource != null) {
            Entity workflow = pFactory.newEntity(prov.qn("workflow", DocumentProv.DISK_PREFIX), "Workflow");
            Entity variableBindingWorkflow = pFactory.newEntity(
                    prov.qn("variable_binding_workflow", DocumentProv.DISK_PREFIX),
                    "Variable binding workflow collection");
            Activity activitySelectWorkflow = pFactory
                    .newActivity(prov.qn("select_workflow", DocumentProv.DISK_PREFIX), "Select the workflow");
            WasInformedBy wib2 = pFactory.newWasInformedBy(null, activitySelectWorkflow.getId(),
                    activityWriteDataQueryTemplate.getId(), null);
            Activity activitySelectVariableWorkflow = pFactory
                    .newActivity(prov.qn("select_variable_workflow", DocumentProv.DISK_PREFIX),
                            "Select the variable workflow");
            WasGeneratedBy wgbSelectVariableWorkflow = pFactory.newWasGeneratedBy(null, variableBindingWorkflow.getId(),
                    activitySelectVariableWorkflow.getId(), null, null);
            Used usedSelectVariableWorkflow = pFactory.newUsed(null, activitySelectVariableWorkflow.getId(),
                    workflow.getId(), null,
                    null);
            WasInformedBy wib4 = pFactory.newWasInformedBy(null, activitySelectVariableWorkflow.getId(),
                    activitySelectWorkflow.getId(), null);
            WasInformedBy wib7 = pFactory.newWasInformedBy(null, activitySavedLineOfInquiry.getId(),
                    activitySelectVariableWorkflow.getId(), null);
            Used usedSelectWorkflow = pFactory.newUsed(null, activitySelectWorkflow.getId(), workflowSystem.getId(),
                    null,
                    null);
            WasGeneratedBy wgbSelectWorkflow = pFactory.newWasGeneratedBy(null, workflow.getId(),
                    activitySelectWorkflow.getId(), null, null);
            WasInformedBy wib6 = pFactory.newWasInformedBy(null, activitySavedLineOfInquiry.getId(),
                    activitySelectVariableWorkflow.getId(), null);
            Used usedSavedLineOfInquiry = pFactory.newUsed(null, activitySavedLineOfInquiry.getId(),
                    variableBindingWorkflow.getId(), null,
                    null);
            loisBundle.getStatement().add(variableBindingWorkflow);
            loisBundle.getStatement().add(workflow);
            loisBundle.getStatement().add(activitySelectWorkflow);
            loisBundle.getStatement().add(wib2);
            loisBundle.getStatement().add(wib7);
            loisBundle.getStatement().add(activitySelectVariableWorkflow);
            loisBundle.getStatement().add(activitySelectVariableWorkflow);
            loisBundle.getStatement().add(wib6);
            loisBundle.getStatement().add(wib4);
            prov.document.getStatementOrBundle().add(usedSelectWorkflow);
            loisBundle.getStatement().add(wgbSelectWorkflow);
            loisBundle.getStatement().add(wgbSelectVariableWorkflow);
            loisBundle.getStatement().add(usedSelectVariableWorkflow);
            loisBundle.getStatement().add(usedSavedLineOfInquiry);
        }
        if (metaWorkflowBindingResource != null) {
            Entity metaWorkflow = pFactory.newEntity(prov.qn("meta_workflow", DocumentProv.DISK_PREFIX),
                    "Meta workflow");
            Activity activitySelectVariableMetaWorkflow = pFactory
                    .newActivity(prov.qn("select_meta_workflow", DocumentProv.DISK_PREFIX), "Select the meta workflow");
            Used usedSelectVariableMetaWorkflow = pFactory.newUsed(null, activitySelectVariableMetaWorkflow.getId(),
                    metaWorkflow.getId(), null, null);
            Entity variableBindingMetaWorkflow = pFactory.newEntity(
                    prov.qn("variable_binding_meta_workflow", DocumentProv.DISK_PREFIX),
                    "Variable binding meta workflow");
            loisBundle.getStatement().add(variableBindingMetaWorkflow);
            Used usedSavedLineOfInquiry1 = pFactory.newUsed(null, activitySavedLineOfInquiry.getId(),
                    variableBindingMetaWorkflow.getId(), null,
                    null);
            WasGeneratedBy wgbSelectVariableMetaWorkflow = pFactory.newWasGeneratedBy(null,
                    variableBindingMetaWorkflow.getId(),
                    activitySelectVariableMetaWorkflow.getId(), null, null);
            Activity activitySelectMetaWorkflow = pFactory
                    .newActivity(prov.qn("select_meta_workflow", DocumentProv.DISK_PREFIX), "Select the meta workflow");
            WasInformedBy wib3 = pFactory.newWasInformedBy(null, activitySelectMetaWorkflow.getId(),
                    activityWriteDataQueryTemplate.getId(), null);
            WasInformedBy wib5 = pFactory.newWasInformedBy(null, activitySelectVariableMetaWorkflow.getId(),
                    activitySelectMetaWorkflow.getId(), null);
            loisBundle.getStatement().add(metaWorkflow);
            loisBundle.getStatement().add(activitySelectMetaWorkflow);
            loisBundle.getStatement().add(wib3);
            loisBundle.getStatement().add(wib5);
            loisBundle.getStatement().add(usedSelectVariableMetaWorkflow);
            loisBundle.getStatement().add(wgbSelectVariableMetaWorkflow);
            loisBundle.getStatement().add(wgbSelectVariableMetaWorkflow);
            loisBundle.getStatement().add(usedSavedLineOfInquiry1);
            // 5. Select the meta workflow
            Used usedSelectMetaWorkflow = pFactory.newUsed(null, activitySelectMetaWorkflow.getId(),
                    workflowSystem.getId(),
                    null,
                    null);
            WasGeneratedBy wgbSelectMetaWorkflow = pFactory.newWasGeneratedBy(null, metaWorkflow.getId(),
                    activitySelectMetaWorkflow.getId(), null, null);
            loisBundle.getStatement().add(usedSelectMetaWorkflow);
            loisBundle.getStatement().add(wgbSelectMetaWorkflow);
        }
        loisBundle.getStatement().add(usedSelectQuestion);
        loisBundle.getStatement().add(wgbSelectDataSource);
        loisBundle.getStatement().add(usedWriteDataQueryTemplate);
        loisBundle.getStatement().add(wgbWriteDataQueryTemplate);
        loisBundle.getStatement().add(wgbSavedLineOfInquiry);

    }

    private List<Entity> level2QuestionAddVariables(Resource questionResource,
            Entity questionEntity, HashMap<String, Entity> questionVariablesMap, Activity createQuestionActivity) {

        Entity questionVariableCollection = pFactory.newEntity(
                prov.qn("questionVariableCollection", DocumentProv.DISK_PREFIX),
                "Collection of question variables");
        StmtIterator statements = questionResource
                .listProperties(questionGraphModel.getProperty("https://w3id.org/sqo#hasQuestionVariable"));
        Property hasVariableName = questionGraphModel.getProperty("https://w3id.org/sqo#hasVariableName");
        List<Entity> questionVariablesEntities = new ArrayList<>();
        statements.forEachRemaining(s -> {
            Resource questionVariableResource = s.getObject().asResource();
            String qvName = questionVariableResource.getLocalName();
            String qvLabel = questionGraphModel.getProperty(questionVariableResource, hasVariableName).getString();
            Entity questionVariableEntity = pFactory.newEntity(prov.qn(qvName, DocumentProv.ENIGMA_PREFIX), qvLabel);
            // add member
            questionVariablesEntities.add(questionVariableEntity);
            HadMember hadMember = pFactory.newHadMember(questionVariableCollection.getId(),
                    questionVariableEntity.getId());
            questionBundle.getStatement().addAll(Arrays.asList(hadMember));
            questionBundle.getStatement().add(questionVariableEntity);
            // questionVariablesMap.put(questionVariableResource.getURI(),
            // questionVariableEntity);
        });
        WasDerivedFrom wdf = pFactory.newWasDerivedFrom(null, questionEntity.getId(),
                questionVariableCollection.getId());
        WasGeneratedBy wgb = pFactory.newWasGeneratedBy(null, questionVariableCollection.getId(),
                createQuestionActivity.getId());
        questionBundle.getStatement().addAll(Arrays.asList(wdf));
        questionBundle.getStatement().add(questionVariableCollection);
        questionBundle.getStatement().add(wgb);
        return questionVariablesEntities;
    }

    private void level2HypothesisAddVariables(Resource hypothesisResource,
            Entity hypothesisEntity, HashMap<String, Entity> hypothesisVariablesMap,
            Activity createHypothesisActivity) {
        StmtIterator getVariableBindingsOnHypothesisGraph = getResourcesByProperty(hypothesisResource,
                hypothesesGraphModel,
                "hasVariableBinding");

        Entity variableCollection = pFactory.newEntity(
                prov.qn("questionVariableCollection", DocumentProv.DISK_PREFIX),
                "Collection of question variables");
        getVariableBindingsOnHypothesisGraph.forEachRemaining(statementHypothesis -> {
            Resource variableBindingResource = statementHypothesis.getObject().asResource();
            // Create the variable binding entity
            String variableLocalName = variableBindingResource.getLocalName();
            String value = getLiteralByProperty(variableBindingResource, hypothesesGraphModel, "hasBindingValue");
            String hvLabel = value;
            Entity variableBindingEntity = pFactory.newEntity(prov.qn(variableLocalName), hvLabel);
            HadMember hadMember = pFactory.newHadMember(variableCollection.getId(),
                    variableBindingEntity.getId());
            hypothesisBundle.getStatement().addAll(Arrays.asList(variableBindingEntity, hadMember));
        });
        WasDerivedFrom wdf = pFactory.newWasDerivedFrom(null, hypothesisEntity.getId(),
                variableCollection.getId());
        WasGeneratedBy wgb = pFactory.newWasGeneratedBy(null, variableCollection.getId(),
                createHypothesisActivity.getId());
        hypothesisBundle.getStatement().addAll(Arrays.asList(wdf));
        hypothesisBundle.getStatement().add(createHypothesisActivity);
        hypothesisBundle.getStatement().add(variableCollection);
        hypothesisBundle.getStatement().add(wgb);
    }

    private void level1WasDerived(Entity questionEntity, Entity hypothesisEntity, Entity lineOfInquiryEntity,
            Entity triggerEntity) {
        // Link the trigger to the line of inquiry
        WasDerivedFrom triggerFromLine = pFactory.newWasDerivedFrom(triggerEntity.getId(), lineOfInquiryEntity.getId());
        WasDerivedFrom lineFromQuestion = pFactory.newWasDerivedFrom(lineOfInquiryEntity.getId(),
                questionEntity.getId());
        WasDerivedFrom hypothesisFromQuestion = pFactory.newWasDerivedFrom(hypothesisEntity.getId(),
                questionEntity.getId());
        triggerBundle.getStatement().add(triggerFromLine);
        loisBundle.getStatement().add(lineFromQuestion);
        questionBundle.getStatement().add(hypothesisFromQuestion);
    }

    public Entity createQuestionEntity(Resource questionResource) {
        Model questionGraphModel = ModelFactory.createModelForGraph(questionGraph);
        String questionName = questionResource.getLocalName();
        String questionLabel = questionGraphModel.getProperty(questionResource, RDFS.label).getString();
        Entity questionEntity = pFactory.newEntity(prov.qn(questionName, DocumentProv.ENIGMA_PREFIX), questionLabel);
        questionBundle.getStatement().add(questionEntity);
        return questionEntity;
    }

    public Resource getQuestionResource(Model hypothesesGraphModel, Resource hypothesisResource,
            Entity hypothesisEntity, Bundle hypothesisBundle) {

        Model questionGraphModel = ModelFactory.createModelForGraph(questionGraph);
        return getResourceByProperty(hypothesisResource, questionGraphModel, "hasQuestion");
    }

    public Entity createHypothesisEntity(Resource hypothesisResource) throws ParseException {
        Model hypothesesGraphModel = ModelFactory.createModelForGraph(hypothesesGraph);
        String hypothesisName = hypothesisResource.getLocalName();
        String hypothesisLabel = hypothesesGraphModel.getProperty(hypothesisResource, RDFS.label).getString();
        String dateCreated = getLiteralByProperty(hypothesisResource, hypothesesGraphModel, "dateCreated");

        Entity hypothesisEntity = pFactory.newEntity(prov.qn(hypothesisName), hypothesisLabel);
        hypothesisBundle.getStatement().add(hypothesisEntity);
        return hypothesisEntity;
    }

    public Resource getHypothesisResource(String hypothesisURI) {
        Model hypothesesGraphModel = ModelFactory.createModelForGraph(hypothesesGraph);
        Resource hypothesisResource = hypothesesGraphModel.getResource(hypothesisURI);
        return hypothesisResource;
    }

    public Entity createLineOfInquiryEntity(Resource loisResource) throws ParseException {
        Model loisGraphModel = ModelFactory.createModelForGraph(loisGraph);
        String loisName = loisResource.getLocalName();
        String loisLabel = loisGraphModel.getProperty(loisResource, RDFS.label).getString();
        String dateCreated = getLiteralByProperty(loisResource, loisGraphModel, "dateCreated");
        Entity loisEntity = pFactory.newEntity(prov.qn(loisName), loisLabel);
        loisBundle.getStatement().add(loisEntity);
        return loisEntity;
    }

    public Resource getLoisResource(String loisURI) {
        Model loisGraphModel = ModelFactory.createModelForGraph(loisGraph);
        Resource loisResource = loisGraphModel.getResource(loisURI);
        return loisResource;
    }

    public Entity createTriggerEntity(Resource triggerResource) throws ParseException {
        Model tLoisGraphModel = ModelFactory.createModelForGraph(tLoisGraph);
        String triggerName = triggerResource.getLocalName();
        String triggerLabel = tLoisGraphModel.getProperty(triggerResource, RDFS.label).getString();
        String dateCreated = getLiteralByProperty(triggerResource, tLoisGraphModel, "dateCreated");
        Entity triggerEntity = pFactory.newEntity(prov.qn(triggerName), triggerLabel);
        triggerBundle.getStatement().add(triggerEntity);
        return triggerEntity;
    }

    public Resource getTriggerResource(String triggerURI) {
        Model tLoisGraphModel = ModelFactory.createModelForGraph(tLoisGraph);
        Resource triggerResource = tLoisGraphModel.getResource(triggerURI);
        if (triggerResource == null) {
            System.out.println("Trigger resource not found");
            throw new RuntimeException("Trigger resource not found");
        }
        return triggerResource;
    }

    private void mapQuestion(Model hypothesesGraphModel, Resource hypothesisResource,
            Entity hypothesisEntity, Bundle hypothesisBundle, Entity questionEntity) {
        // TODO: Enigma graph is hardcoded
        Activity activity = pFactory.newActivity(prov.qn("CreateHypothesisByQuestion"),
                "Create Hypothesis By Question");

        String enigmaGraphId = "https://raw.githubusercontent.com/KnowledgeCaptureAndDiscovery/QuestionOntology/main/development/EnigmaQuestions.xml";
        String bundleName = "EnigmaQuestions";
        Graph questionGraph = loadGraphFromDataset(enigmaGraphId);
        Model questionGraphModel = ModelFactory.createModelForGraph(questionGraph);

        // Get the question
        Resource questionResource = getResourceByProperty(hypothesisResource, questionGraphModel, "hasQuestion");

        // Create the question entity
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
        questionBundle.getStatement().addAll(Arrays.asList(questionEntity, used, wgb, used2, wdf));
        hypothesisBundle.getStatement().addAll(Arrays.asList(hypothesisEntity, activity, activity2));

        // Question variables and Hypothesis variables binding mapping
        HashMap<String, Entity> questionVariablesMap = new HashMap<>();

        // [Question-ontology] Get the variables binding from the question
        // mapVariableBindingFromQuestion(questionGraphModel, questionResource,
        // questionEntity, questionBundle,
        // questionVariablesMap);
        // [DISK] Get the variables binding from the hypothesis
        mapVariableFromHypothesis(hypothesesGraphModel, hypothesisResource, hypothesisEntity, hypothesisBundle,
                questionGraphModel, activity, questionEntity, activity2);

        prov.document.getStatementOrBundle().add(questionBundle);
    }

    private void mapVariableFromHypothesis(Model hypothesesGraphModel, Resource hypothesisResource,
            Entity hypothesisEntity,
            Bundle diskBundle, Model questionGraphModel, Activity activity, Entity questionEntity, Activity activity2) {
        StmtIterator getVariableBindingsOnHypothesisGraph = getResourcesByProperty(hypothesisResource,
                hypothesesGraphModel,
                "hasVariableBinding");

    }

    public Entity mapLineOfInquiry(String lineOfInquiryURI, Bundle loisBundle, Entity hypothesis, Entity questionEntity)
            throws ParseException {
        Model loisGraphModel = ModelFactory.createModelForGraph(loisGraph);
        Resource lineOfInquiryResource = loisGraphModel.getResource(lineOfInquiryURI);
        String name = lineOfInquiryResource.getLocalName();
        String label = loisGraphModel.getProperty(lineOfInquiryResource, RDFS.label).getString();
        String dateCreated = getLiteralByProperty(lineOfInquiryResource, loisGraphModel, "dateCreated");
        Entity lineOfInquiryEntity = pFactory.newEntity(prov.qn(name, DocumentProv.DISK_PREFIX), label);
        String dataQuery = getLiteralByProperty(lineOfInquiryResource, loisGraphModel, "hasDataQuery");
        String dataQueryDescription = getLiteralByProperty(lineOfInquiryResource, loisGraphModel,
                "dataQueryDescription");

        Resource metaWorkflowBindingResource = getResourceByProperty(lineOfInquiryResource, loisGraphModel,
                "hasMetaWorkflowBinding");
        Resource workflowBindingResource = getResourceByProperty(lineOfInquiryResource, loisGraphModel,
                "hasWorkflowBinding");

        // create the activity for writing the data query

        String activityName = "LOI_creation_" + name;
        Activity activity = pFactory.newActivity(prov.qn(activityName, DocumentProv.DISK_PREFIX), null, null, null);

        // create the metaWorkflowBinding and workflowBinding entities
        if (metaWorkflowBindingResource != null) {

            String metaWorkflowURI = getLiteralByProperty(metaWorkflowBindingResource, loisGraphModel, "hasWorkflow");
            String metaWorkflowBindingName = metaWorkflowBindingResource.getProperty(RDFS.comment).getString();
            Entity metaWorkflowBinding = pFactory.newEntity(prov.qn("metaWorkflowBinding", DocumentProv.DISK_PREFIX),
                    metaWorkflowBindingName);
            Value value = pFactory.newValue(metaWorkflowURI);
            metaWorkflowBinding.setValue(value);

            Activity activitySelectQuestion = pFactory.newActivity(prov.qn("select_question", DocumentProv.DISK_PREFIX),
                    null, null, null);
            Used used = pFactory.newUsed(null, activitySelectQuestion.getId(), questionEntity.getId(), null, null);

            Activity activitySelectMetaWorkflow = pFactory
                    .newActivity(prov.qn("select_workflow", DocumentProv.DISK_PREFIX), null, null, null);
            Entity workflow = pFactory.newEntity(prov.qn("workflow", DocumentProv.DISK_PREFIX), metaWorkflowURI);
            Used used2 = pFactory.newUsed(null, activitySelectMetaWorkflow.getId(), workflow.getId(), null, null);
            WasGeneratedBy wgb = pFactory.newWasGeneratedBy(null, metaWorkflowBinding.getId(),
                    activitySelectMetaWorkflow.getId(), null, null);

            WasGeneratedBy wgb2 = pFactory.newWasGeneratedBy(null, metaWorkflowBinding.getId(), activity.getId(), null,
                    null);

            loisBundle.getStatement()
                    .addAll(Arrays.asList(metaWorkflowBinding, wgb, workflow, used, wgb2, activitySelectMetaWorkflow));
            getResourcesByProperty(metaWorkflowBindingResource, loisGraphModel, "hasVariableBinding")
                    .forEachRemaining(statement -> {
                        Resource workflowVariableBindingResource = statement.getObject().asResource();
                        // TODO: blank node
                        String variableName = workflowVariableBindingResource.getId().toString();
                        String variableValue = getLiteralByProperty(workflowVariableBindingResource, loisGraphModel,
                                "hasBindingValue");
                        String variableWorkflow = getLiteralByProperty(workflowVariableBindingResource, loisGraphModel,
                                "hasVariable");
                        Entity workflowVariableBinding = pFactory.newEntity(
                                prov.qn("variable_" + variableName, DocumentProv.DISK_PREFIX),
                                variableValue);
                        WasDerivedFrom wdf = pFactory.newWasDerivedFrom(null, workflowVariableBinding.getId(),
                                metaWorkflowBinding.getId());
                        loisBundle.getStatement().addAll(Arrays.asList(workflowVariableBinding, wdf));
                    });
            loisBundle.getStatement().addAll(Arrays.asList(metaWorkflowBinding, wgb2, workflow));

        }
        if (workflowBindingResource != null) {
            String workflowBindingName = metaWorkflowBindingResource.getProperty(RDFS.comment).getString();
            Entity workflowBinding = pFactory.newEntity(prov.qn("workflowBinding", DocumentProv.DISK_PREFIX),
                    workflowBindingName);
            WasGeneratedBy wgb2 = pFactory.newWasGeneratedBy(null, workflowBinding.getId(), activity.getId(), null,
                    null);
            loisBundle.getStatement().addAll(Arrays.asList(workflowBinding, wgb2));
        }

        // Relation with the hypothesis
        WasGeneratedBy wgb = pFactory.newWasGeneratedBy(null, lineOfInquiryEntity.getId(), activity.getId(), null,
                null);
        Used used = pFactory.newUsed(null, activity.getId(), hypothesis.getId(), null, null);
        WasDerivedFrom wdf = pFactory.newWasDerivedFrom(null, lineOfInquiryEntity.getId(), hypothesis.getId(), null,
                null, null, null);
        // Add
        loisBundle.getStatement().addAll(Arrays.asList(lineOfInquiryEntity, activity, wgb, used, wdf));

        // Data query template
        Entity dataQueryTemplate = pFactory.newEntity(prov.qn("dataQueryTemplate", DocumentProv.DISK_PREFIX),
                dataQueryDescription);
        WasGeneratedBy wgb3 = pFactory.newWasGeneratedBy(null, dataQueryTemplate.getId(), activity.getId(), null,
                null);

        loisBundle.getStatement().addAll(Arrays.asList(dataQueryTemplate, wgb3));

        return lineOfInquiryEntity;
    }

    // private void mappingRuns(Node tloiNode, Model tloiModel, String
    // triggerLineName, Date dateCreatedDate,
    // Individual diskUser, Individual diskAgent, Individual entityInstance) throws
    // ParseException {
    // // Create a new entity for the execution of the meta workflow
    // // Obtain the object property
    // // http://disk-project.org/ontology/disk#hasMetaWorkflowBinding from the
    // trigger
    // Resource metaWorkflowBindingResource = getResourcesByProperty(tloiNode,
    // tloiModel, "hasMetaWorkflowBinding");
    // if (metaWorkflowBindingResource != null) {
    // IRI metaWorkflowBindingIRI = getIRIByProperty(metaWorkflowBindingResource,
    // tloiModel, "hasId");
    // IRI metaWorkflowRunBindingIRI = getIRIByProperty(metaWorkflowBindingResource,
    // tloiModel, "hasRunLink");
    // String hasRunEndString = getLiteralByProperty(metaWorkflowBindingResource,
    // tloiModel, "hasRunEndDate");
    // String hasRunStartString = getLiteralByProperty(metaWorkflowBindingResource,
    // tloiModel, "hasRunStartDate");
    // String hasRunStatus = getLiteralByProperty(metaWorkflowBindingResource,
    // tloiModel, "hasRunStatus");
    // String hasSourceString = getLiteralByProperty(metaWorkflowBindingResource,
    // tloiModel, "hasSource");

    // Date hasRunStartDate = hasRunStartString != null ?
    // DATE_FORMAT.parse(hasRunStartString) : null;
    // Date hasRunEndDate = hasRunEndString != null ?
    // DATE_FORMAT.parse(hasRunEndString) : null;

    // String runLocalName = triggerLineName + "/executions/"
    // + metaWorkflowBindingIRI.getShortForm();
    // String runActivityLocalName = runLocalName + "/start_activity";
    // Individual runActivityInstance = createActivity(hasRunStartDate,
    // runActivityLocalName,
    // diskAgent,
    // diskUser, runActivityLocalName, hasRunEndDate);

    // Individual runEntity = createEntity(dateCreatedDate,
    // metaWorkflowBindingIRI.toString(),
    // metaWorkflowBindingIRI.toString(),
    // entityInstance,
    // runActivityInstance,
    // runLocalName);

    // }
    // // Obtain the object property
    // // http://disk-project.org/ontology/disk#hasWorkflowBinding from the trigger
    // Resource workflowBindingResource = getResourcesByProperty(tloiNode,
    // tloiModel, "hasWorkflowBinding");
    // IRI workflowBindingIRI = null;
    // IRI workflowRunBindingIRI = null;
    // if (workflowBindingResource != null) {
    // workflowBindingIRI = getIRIByProperty(workflowBindingResource, tloiModel,
    // "hasId");
    // workflowRunBindingIRI = getIRIByProperty(workflowBindingResource, tloiModel,
    // "hasRunId");
    // }

    // }

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

    private Resource getResourceByProperty(Resource resource, Model model, String propertyName) {
        if (!diskProperties.containsKey(propertyName)) {
            throw new RuntimeException("DISK properties mapping " + propertyName + " not found");
        }
        IRI propertyIRI = diskProperties.get(propertyName);
        Property property = model.getProperty(propertyIRI.toString());
        if (property == null) {
            throw new RuntimeException("Property " + propertyName + " not found");
        }
        if (resource.getProperty(property) != null) {
            if (resource.getProperty(property).getObject().isResource()) {
                return resource.getProperty(property).getResource();
            } else {
                if (resource.getProperty(property).getObject().isLiteral()) {
                    throw new RuntimeException("Property " + propertyName + " is not a literal");
                } else {
                    System.out.println("Property " + propertyName + " is not a resource");

                }
            }
        }
        throw new RuntimeException("Uncatched error");
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
            } else if (statement.getObject().isURIResource()) {
                return statement.getObject().asResource().getURI();
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

package edu.isi.disk.opmm;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDFS;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.ActedOnBehalfOf;
import org.openprovenance.prov.model.Activity;
import org.openprovenance.prov.model.Agent;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.HadMember;
import org.openprovenance.prov.model.Namespace;
// import prov
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.StatementOrBundle;
import org.openprovenance.prov.model.Used;
import org.openprovenance.prov.model.Value;
import org.openprovenance.prov.model.WasDerivedFrom;
import org.openprovenance.prov.model.WasGeneratedBy;
import org.openprovenance.prov.model.WasInformedBy;
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
        public Resource triggerResource;
        public Resource lineOfInquiry;
        public Resource hypothesisResource;
        public Resource questionResource;

        Bundle questionBundle = pFactory.newNamedBundle(prov.qn("questionBundle"), null);
        Bundle hypothesisBundle = pFactory.newNamedBundle(prov.qn("hypothesisBundle"), null);
        Bundle loisBundle = pFactory.newNamedBundle(prov.qn("loisBundle"), null);
        Bundle triggerBundle;
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
                        Namespace triggerDefaultNamespace = prov.ns;
                        triggerDefaultNamespace.setDefaultNamespace(DocumentProv.PROV_NEUROSCIENCE_TLOI_NS);
                        
                        triggerDefaultNamespace.setDefaultNamespace(DocumentProv.PROV_NEUROSCIENCE_TLOI_NS);
                        triggerBundle = pFactory.newNamedBundle(prov.qn("triggerBundle"), triggerDefaultNamespace,
                                        null);
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
                triggerResource = getTriggerResource(triggerURI);
                lineOfInquiry = loisGraphModel
                                .getResource(getResourceByProperty(triggerResource, tLoisGraphModel, "hasLineOfInquiry")
                                                .getURI());
                hypothesisResource = hypothesesGraphModel
                                .getResource(getResourceByProperty(triggerResource, tLoisGraphModel,
                                                "hasParentHypothesis").getURI());
                questionResource = questionGraphModel
                                .getResource(getIRIByProperty(hypothesisResource, hypothesesGraphModel, "hasQuestion")
                                                .toString());

                Agent hvargas = createDummyAgent("hvargas", "Hernan Vargas");
                Agent neda = createDummyAgent("neda", "Neda Jahanshad");
                prov.document.getStatementOrBundle().add(hvargas);
                prov.document.getStatementOrBundle().add(neda);

                Entity questionEntity = createQuestionEntity(questionResource);
                Entity hypothesisEntity = createHypothesisEntity(hypothesisResource);
                Entity lineOfInquiryEntity = createLineOfInquiryEntity(lineOfInquiry);
                Entity triggerEntity = createTriggerEntity(triggerResource);

                String commentText = "The agent selects a Question and the values for each of the variables of the selected question. The agent then creates a hypothesis.";
                Activity createQuestionActivity = linkActivityEntities(hvargas, neda, questionEntity, questionBundle,
                                "createQuestion", "Create Question",
                                "The agent creates a Question and each of the variables of them. The agent then creates a hypothesis.");
                Activity createHypothesisActivity = linkActivityEntities(hvargas, neda, hypothesisEntity,
                                hypothesisBundle,
                                "createHypothesis", "Create Hypothesis", commentText);

                level1WasDerived(questionEntity, hypothesisEntity, lineOfInquiryEntity, triggerEntity);

                if (level > 1) {
                        Entity questionVariablesBinding = level2QuestionAddVariables(questionResource,
                                        questionEntity, null,
                                        createQuestionActivity);
                        Entity hypothesisVariablesCollection = level2HypothesisAddVariables(hypothesisResource,
                                        hypothesisEntity, questionVariablesBinding,
                                        createHypothesisActivity);
                        level2LineAdd(lineOfInquiry, hypothesisEntity, questionEntity, lineOfInquiryEntity,
                                        triggerEntity, hypothesisVariablesCollection);
                }
        }

        private void level2AddTrigger(Entity triggerEntity, Entity lineOfInquiryEntity, Entity dataSourceLoi,
                        Entity dataQueryTemplate, Entity workflowSystem, Entity workflowLoi, Entity metaWorkflowLoi,
                        Entity variablesBindingWorkflowEntity, Entity variablesBindingMetaWorkflowEntity,
                        Entity hypothesisVariableCollection) {
                List<Resource> metaWorkflowBindingResources = getMetaOrWorkflow("hasMetaWorkflowBinding",
                                tLoisGraphModel,
                                triggerResource);
                List<Resource> workflowBindingResources = getMetaOrWorkflow("hasWorkflowBinding", tLoisGraphModel,
                                triggerResource);

                // Create entities: dataQuery
                String dataQueryFilled = getLiteralByProperty(triggerResource, tLoisGraphModel, "hasDataQuery");
                Entity dataQuery = pFactory.newEntity(prov.qn("dataQuery", DocumentProv.PROV_NEUROSCIENCE_TLOI_PREFIX),
                                dataQueryFilled);
                triggerBundle.getStatement().add(dataQuery);

                String dataSource = getLiteralByProperty(triggerResource, tLoisGraphModel, "hasDataSource");
                Entity dataSourceEntity = pFactory.newEntity(
                                prov.qn("dataSource", DocumentProv.PROV_NEUROSCIENCE_TLOI_PREFIX), dataSource);
                triggerBundle.getStatement().add(dataSourceEntity);
                WasDerivedFrom dataSourceWasDerivedFrom = pFactory.newWasDerivedFrom(null, dataSourceEntity.getId(),
                                dataSourceLoi.getId());
                triggerBundle.getStatement().add(dataSourceWasDerivedFrom);

                // Create activities: transformDataQuery, executeDataQuery
                Activity transformDataQuery = pFactory.newActivity(prov.qn("transformDataQueryTemplate"),
                                "Replace the variables in the data query template with the variables of the hypothesis");
                Used transformDataQueryActivityUsed = pFactory.newUsed(null, dataQueryTemplate.getId(),
                                transformDataQuery.getId(), null,
                                null);
                WasGeneratedBy dataQueryWasGeneratedBy = pFactory.newWasGeneratedBy(null, dataQuery.getId(),
                                transformDataQuery.getId(), null, null);
                Activity executeDataQuery = pFactory.newActivity(prov.qn("executeDataQuery"), "Execute the data query");
                WasGeneratedBy toiWasGeneratedBy = pFactory.newWasGeneratedBy(null, triggerEntity.getId(),
                                transformDataQuery.getId(), null, null);
                triggerBundle.getStatement().add(toiWasGeneratedBy);
                triggerBundle.getStatement().add(executeDataQuery);
                triggerBundle.getStatement().add(transformDataQuery);
                triggerBundle.getStatement().add(transformDataQueryActivityUsed);
                triggerBundle.getStatement().add(dataQueryWasGeneratedBy);

                // if (variablesBindingMetaWorkflowEntity != null) {
                // addVariableBindingWorkflow(variablesBindingMetaWorkflowEntity,
                // transformDataQuery);
                // }
                // if (variablesBindingWorkflowEntity != null) {
                // addVariableBindingWorkflow(variablesBindingWorkflowEntity,
                // transformDataQuery);
                // }

                // Activity createRunActivity = pFactory.newActivity(prov.qn("createRun"),
                // "Create run");
                /**
                 * Workflow Plan
                 */
                // for (Resource metaWorkflowBindingResource : metaWorkflowBindingResources) {
                // String workflowLabel = getIRIByProperty(metaWorkflowBindingResource,
                // tLoisGraphModel, "hasId")
                // .toString();
                // String workflowLocalName =
                // workflowLabel.substring(workflowLabel.lastIndexOf("#") + 1);
                // String workflowSystemName = getLiteralByProperty(metaWorkflowBindingResource,
                // tLoisGraphModel,
                // "hasSource");
                // Entity metaWorkflowRun = pFactory.newEntity(
                // prov.qn(workflowLocalName, DocumentProv.WINGS_ONTOLOGY_PREFIX),
                // workflowLabel);
                // WasGeneratedBy metaWorkflowWasGeneratedBy = pFactory.newWasGeneratedBy(null,
                // metaWorkflowRun.getId(), createRunActivity.getId(), null, null);
                // workflowSystem = pFactory.newEntity(
                // prov.qn(workflowSystemName, DocumentProv.WINGS_ONTOLOGY_PREFIX),
                // workflowSystemName);
                // triggerBundle.getStatement().add(metaWorkflowRun);
                // triggerBundle.getStatement().add(workflowSystem);
                // triggerBundle.getStatement().add(metaWorkflowWasGeneratedBy);

                // // Add the Variable Binding generated by the Data Query
                // Entity runVariableBindingMetaWorkflow = pFactory.newEntity(
                // prov.qn("triggerVariableBinding", DocumentProv.DISK_PREFIX),
                // "Stores the run parameters for the Meta Workflow");

                // getResourcesByProperty(metaWorkflowBindingResource, tLoisGraphModel,
                // "hasVariableBinding")
                // .forEachRemaining(s -> {
                // addRunBinding(runVariableBindingMetaWorkflow, s);
                // });
                // triggerBundle.getStatement().add(runVariableBindingMetaWorkflow);

                // // Execute Data Query Activity generates the Variable Binding
                // WasGeneratedBy runVariableBindingMetaWorkflowWasGeneratedBy =
                // pFactory.newWasGeneratedBy(null,
                // runVariableBindingMetaWorkflow.getId(), executeDataQuery.getId(), null,
                // null);
                // triggerBundle.getStatement().add(runVariableBindingMetaWorkflowWasGeneratedBy);

                // // Activity `createRunOnWorkflow` uses the Trigger Variable Binding
                // Used createRunUsedVariablesHypothesis = pFactory.newUsed(null,
                // runVariableBindingMetaWorkflow.getId(),
                // createRunActivity.getId(), null, null);
                // triggerBundle.getStatement().add(createRunUsedVariablesHypothesis);

                // // Activity `createRunOnWorkflow` uses the Meta Workflow
                // Used createRunUsedMetaWorkflow = pFactory.newUsed(null,
                // metaWorkflowLoi.getId(),
                // createRunActivity.getId(), null, null);
                // triggerBundle.getStatement().add(createRunUsedMetaWorkflow);

                // /*
                // * New Activity `AnalyzeWorkflowRun` analyzes the WorkflowRun
                // * `AnalysisWorkflowRun` uses the WorkflowRun
                // * `AnalysisWorkflowRun` generates the Analysis
                // */
                // Activity analyzeWorkflowRun =
                // pFactory.newActivity(prov.qn("analyzeWorkflowRun"),
                // "Analyze the Workflow Run");
                // Used analyzeWorkflowRunUsedWorkflowRun = pFactory.newUsed(null,
                // metaWorkflowRun.getId(),
                // analyzeWorkflowRun.getId(), null, null);
                // Entity analysis = pFactory.newEntity(prov.qn("analysis"), "Analysis");
                // WasGeneratedBy analysisWasGeneratedBy = pFactory.newWasGeneratedBy(null,
                // analysis.getId(),
                // analyzeWorkflowRun.getId(), null, null);
                // triggerBundle.getStatement().add(analyzeWorkflowRun);
                // triggerBundle.getStatement().add(analysis);
                // triggerBundle.getStatement().add(analyzeWorkflowRunUsedWorkflowRun);
                // triggerBundle.getStatement().add(analysisWasGeneratedBy);

                // /*
                // * Entity `metaWorkflowRun` generates a Collection of Entity
                // `outputCollection`
                // * Entity `outputItem` is a member of Entity `outputCollection`
                // */
                // // Entity runOutputCollection =
                // // pFactory.newEntity(prov.qn("runOutputCollection"),
                // // "Collection of the outputs of the Workflow Run");
                // // WasGeneratedBy runOutputCollectionWasGeneratedBy =
                // // pFactory.newWasGeneratedBy(null,
                // // runOutputCollection.getId(), metaWorkflowRun.getId(), null, null);
                // // triggerBundle.getStatement().add(runOutputCollection);
                // // triggerBundle.getStatement().add(runOutputCollectionWasGeneratedBy);

                // }

                // triggerBundle.getStatement().add(createRunActivity);

        }

        private void addRunBinding(Entity runInputsVariableMetaWorkflow, Statement s) {
                Resource variableBindingResource = s.getObject().asResource();
                String hasBindingValue = getLiteralByProperty(variableBindingResource,
                                tLoisGraphModel,
                                "hasBindingValue");
                // TODO: hack to get the name
                String hasVariable = getLiteralByProperty(variableBindingResource,
                                loisGraphModel,
                                "hasVariable");
                String[] split = hasVariable.split("#");
                String variableName = split[split.length - 1];
                Entity runInput = pFactory.newEntity(
                                prov.qn(variableName, DocumentProv.DISK_PREFIX),
                                "Variable Binding");
                runInput.setValue(pFactory.newValue(hasBindingValue));

                HadMember hm2 = pFactory.newHadMember(
                                runInputsVariableMetaWorkflow.getId(),
                                runInput.getId());

                triggerBundle.getStatement().add(runInput);
                triggerBundle.getStatement().add(hm2);
        }

        private List<Resource> getMetaOrWorkflow(String propertyName, Model model, Resource resource) {
                List<Resource> resources = new ArrayList<>();
                try {
                        StmtIterator statements = getResourcesByProperty(resource, model,
                                        propertyName);
                        if (statements.hasNext()) {
                                resources.add(statements.next().getObject().asResource());
                        }

                } catch (Exception e) {
                        System.out.println("No " + propertyName + " for " + resource);
                        return null;
                }
                return resources;
        }

        private void addVariableBindingWorkflow(Entity variablesBindingMetaWorkflowEntity,
                        Activity transformDataQuery) {
                Used dataQueryUsedVariable = pFactory.newUsed(null, variablesBindingMetaWorkflowEntity.getId(),
                                transformDataQuery.getId(), null,
                                null);
                triggerBundle.getStatement().add(dataQueryUsedVariable);
        }

        private Activity linkActivityEntities(Agent delegate, Agent responsible, Entity generated, Bundle bundle,
                        String activityName, String activityLabel, String commentText) {
                Activity activity = pFactory.newActivity(prov.qn(activityName), activityLabel);
                WasGeneratedBy wasGeneratedBy = pFactory.newWasGeneratedBy(null, generated.getId(), activity.getId(),
                                null, null);
                ActedOnBehalfOf actedOnBehalfOf = pFactory.newActedOnBehalfOf(null, delegate.getId(),
                                responsible.getId(),
                                activity.getId(), null);
                activity.getOther().add(pFactory.newOther(DocumentProv.RDFS_NS, "comment",
                                DocumentProv.RDFS_PREFIX, commentText,
                                pFactory.getName().XSD_NAME));
                prov.document.getStatementOrBundle().add(actedOnBehalfOf);
                bundle.getStatement().add(activity);
                bundle.getStatement().add(wasGeneratedBy);
                return activity;
        }

        private void oneActivityGeneratesMultipleEntities(Agent delegate, Agent responsible,
                        List<Entity> generatedEntities,
                        Bundle bundle, String activityName, String activityLabel) {
                Activity activity = pFactory.newActivity(prov.qn(activityName), activityLabel);
                for (Entity generatedEntity : generatedEntities) {
                        WasGeneratedBy wasGeneratedBy = pFactory.newWasGeneratedBy(null, generatedEntity.getId(),
                                        activity.getId(),
                                        null, null);
                        bundle.getStatement().add(wasGeneratedBy);
                }
                ActedOnBehalfOf actedOnBehalfOf = pFactory.newActedOnBehalfOf(null, delegate.getId(),
                                responsible.getId(),
                                activity.getId(), null);
                bundle.getStatement().add(actedOnBehalfOf);
                bundle.getStatement().add(activity);
        }

        public Agent createDummyAgent(String localName, String label) {
                Agent agent = pFactory.newAgent(prov.qn(localName, DocumentProv.DISK_PREFIX), label);
                return agent;
        }

        public void level2LineAdd(Resource lineOfInquiryResource, Entity hypothesis, Entity questionEntity,
                        Entity lineOfInquiryEntity, Entity triggerEntity, Entity hypothesisVariablesCollection)
                        throws ParseException {
                String dateCreated = getLiteralByProperty(lineOfInquiryResource, loisGraphModel, "dateCreated");
                String dataQuery = getLiteralByProperty(lineOfInquiryResource, loisGraphModel, "hasDataQuery");
                String dataQueryDescription = getLiteralByProperty(lineOfInquiryResource, loisGraphModel,
                                "dataQueryDescription");
                String dataSourceIRI = getLiteralByProperty(lineOfInquiryResource, loisGraphModel, "hasDataSource");

                List<Resource> metaWorkflowBindingResources = getMetaOrWorkflow("hasMetaWorkflowBinding",
                                loisGraphModel,
                                lineOfInquiryResource);
                List<Resource> workflowBindingResources = getMetaOrWorkflow("hasWorkflowBinding", loisGraphModel,
                                lineOfInquiryResource);

                // create the activity for writing the data query
                Activity activitySelectQuestion = pFactory.newActivity(
                                prov.qn("select_question", DocumentProv.DISK_PREFIX),
                                "Select the question");
                Activity activitySelectDataSource = pFactory.newActivity(
                                prov.qn("select_data_source", DocumentProv.DISK_PREFIX),
                                "Select the data source");
                Activity activityWriteDataQueryTemplate = pFactory
                                .newActivity(prov.qn("write_data_query_template", DocumentProv.DISK_PREFIX),
                                                "Write data query template");
                Activity activitySavedLineOfInquiry = pFactory
                                .newActivity(prov.qn("saved_line_of_inquiry", DocumentProv.DISK_PREFIX),
                                                "Saved line of inquiry");

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

                Entity dataQueryTemplateEntity = pFactory.newEntity(
                                prov.qn("data_query", DocumentProv.PROV_NEUROSCIENCE_LOI_PREFIX),
                                dataQuery);
                Entity dataSource = pFactory.newEntity(
                                prov.qn("data_source", DocumentProv.PROV_NEUROSCIENCE_LOI_PREFIX), dataSourceIRI);
                loisBundle.getStatement().add(dataQueryTemplateEntity);
                loisBundle.getStatement().add(dataSource);

                // Activties details
                // 1. Select the question
                Used usedSelectQuestion = pFactory.newUsed(null, activitySelectQuestion.getId(), questionEntity.getId(),
                                null,
                                null);
                // TODO: generate the variables ???
                // 2. Select the data source
                Used usedSelectDataSource = pFactory.newUsed(null, activitySelectDataSource.getId(), dataSource.getId(),
                                null,
                                null);
                // 3. Write data query template
                Used usedWriteDataQueryTemplate = pFactory.newUsed(null, activityWriteDataQueryTemplate.getId(),
                                questionEntity.getId(), null, null);
                WasGeneratedBy wgbWriteDataQueryTemplate = pFactory.newWasGeneratedBy(null,
                                dataQueryTemplateEntity.getId(), activityWriteDataQueryTemplate.getId(), null, null);
                // 8. Save the line of inquiry
                WasGeneratedBy wgbSavedLineOfInquiry = pFactory.newWasGeneratedBy(null, lineOfInquiryEntity.getId(),
                                activitySavedLineOfInquiry.getId(), null, null);
                Entity workflow = null;
                Entity metaWorkflow = null;
                Entity workflowSystem = null;
                Entity variableBindingWorkflow = null;
                Entity variableBindingMetaWorkflow = null;
                for (Resource workflowBindingResource : workflowBindingResources) {
                        String workflowLabel = workflowBindingResource
                                        .getProperty(loisGraphModel.createProperty(RDFS_COMMENT))
                                        .getLiteral().toString();
                        String workflowLocalName = "unknown";
                        String workflowSystemName = getLiteralByProperty(workflowBindingResource, hypothesesGraphModel,
                                        "hasSource");

                        workflowSystem = pFactory.newEntity(
                                        prov.qn(workflowSystemName, DocumentProv.WINGS_ONTOLOGY_PREFIX),
                                        workflowSystemName);
                        workflow = pFactory.newEntity(prov.qn(workflowLocalName, DocumentProv.WINGS_ONTOLOGY_PREFIX),
                                        workflowLabel);
                        variableBindingWorkflow = pFactory.newEntity(
                                        prov.qn("workflowVariablesBinding", DocumentProv.PROV_NEUROSCIENCE_LOI_PREFIX),
                                        "Variable binding workflow collection");
                        String activitySelectWorkflowLabel = "Select the workflow";
                        String activitySelectVariableWorkflowLabel = "Select the variable workflow";
                        String activitySelectVariableLocalName = "select_variable_workflow";
                        String activitySelectWorkflowName = "select_workflow";
                        handleWorkflowActivity(activityWriteDataQueryTemplate, activitySavedLineOfInquiry,
                                        workflowSystem,
                                        workflow,
                                        variableBindingWorkflow, activitySelectWorkflowLabel,
                                        activitySelectVariableWorkflowLabel,
                                        activitySelectVariableLocalName, activitySelectWorkflowName,
                                        workflowBindingResource);
                }
                for (Resource metaWorkflowBindingResource : metaWorkflowBindingResources) {
                        String workflowLabel = metaWorkflowBindingResource
                                        .getProperty(loisGraphModel.createProperty(RDFS_COMMENT))
                                        .getLiteral().getString();
                        String workflowLocalName = "unknown";
                        String workflowSystemName = getLiteralByProperty(metaWorkflowBindingResource, loisGraphModel,
                                        "hasSource");
                        metaWorkflow = pFactory.newEntity(
                                        prov.qn(workflowLocalName, DocumentProv.WINGS_ONTOLOGY_PREFIX),
                                        workflowLabel);
                        workflowSystem = pFactory.newEntity(
                                        prov.qn(workflowSystemName, DocumentProv.WINGS_ONTOLOGY_PREFIX),
                                        workflowSystemName);
                        variableBindingMetaWorkflow = pFactory.newEntity(
                                        prov.qn("metaWorkflowVariablesBinding",
                                                        DocumentProv.PROV_NEUROSCIENCE_LOI_PREFIX),
                                        "Stores the binding between Question or Data Queries variables and the Meta Workflow variables");
                        String activitySelectWorkflowLabel = "Select the workflow";
                        String activitySelectVariableWorkflowLabel = "Select the variable workflow";
                        String activitySelectVariableLocalName = "select_variable_workflow";
                        String activitySelectWorkflowName = "select_workflow";
                        handleWorkflowActivity(activityWriteDataQueryTemplate, activitySavedLineOfInquiry,
                                        workflowSystem,
                                        metaWorkflow, variableBindingMetaWorkflow, activitySelectWorkflowLabel,
                                        activitySelectVariableWorkflowLabel, activitySelectVariableLocalName,
                                        activitySelectWorkflowName,
                                        metaWorkflowBindingResource);
                }
                loisBundle.getStatement().add(usedSelectQuestion);
                prov.document.getStatementOrBundle().add(usedSelectDataSource);
                loisBundle.getStatement().add(usedWriteDataQueryTemplate);
                loisBundle.getStatement().add(wgbWriteDataQueryTemplate);
                loisBundle.getStatement().add(wgbSavedLineOfInquiry);

                level2AddTrigger(triggerEntity, lineOfInquiryEntity, dataSource, dataQueryTemplateEntity,
                                workflowSystem,
                                workflow, metaWorkflow, variableBindingWorkflow, variableBindingMetaWorkflow,
                                hypothesisVariablesCollection);
        }

        private void handleWorkflowActivity(Activity activityWriteDataQueryTemplate,
                        Activity activitySavedLineOfInquiry,
                        Entity workflowSystem, Entity workflow, Entity variableBindingWorkflow,
                        String activitySelectWorkflowLabel,
                        String activitySelectVariableWorkflowLabel, String activitySelectVariableLocalName,
                        String activitySelectWorkflowName, Resource workflowBindingResource) {
                prov.document.getStatementOrBundle().add(workflowSystem);
                Activity activitySelectWorkflow = pFactory
                                .newActivity(prov.qn(activitySelectWorkflowName, DocumentProv.DISK_PREFIX),
                                                activitySelectWorkflowLabel);
                WasInformedBy wib2 = pFactory.newWasInformedBy(null, activitySelectWorkflow.getId(),
                                activityWriteDataQueryTemplate.getId(), null);
                Activity activitySelectVariableWorkflow = pFactory
                                .newActivity(prov.qn(activitySelectVariableLocalName, DocumentProv.DISK_PREFIX),
                                                activitySelectVariableWorkflowLabel);
                WasGeneratedBy wgbSelectVariableWorkflow = pFactory.newWasGeneratedBy(null,
                                variableBindingWorkflow.getId(),
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
                prov.document.getStatementOrBundle().add(workflow);
                HadMember hm = pFactory.newHadMember(workflow.getId(), workflowSystem.getId());
                prov.document.getStatementOrBundle().add(hm);
                loisBundle.getStatement().add(activitySelectWorkflow);
                loisBundle.getStatement().add(wib2);
                loisBundle.getStatement().add(wib7);
                loisBundle.getStatement().add(activitySelectVariableWorkflow);
                loisBundle.getStatement().add(activitySelectVariableWorkflow);
                loisBundle.getStatement().add(wib6);
                loisBundle.getStatement().add(wib4);
                prov.document.getStatementOrBundle().add(usedSelectWorkflow);
                prov.document.getStatementOrBundle().add(wgbSelectWorkflow);
                loisBundle.getStatement().add(wgbSelectVariableWorkflow);
                prov.document.getStatementOrBundle().add(usedSelectVariableWorkflow);
                loisBundle.getStatement().add(usedSavedLineOfInquiry);

                // Add the variable bindings
                StmtIterator statements = getResourcesByProperty(workflowBindingResource, loisGraphModel,
                                "hasVariableBinding");
                statements.forEachRemaining(s -> {
                        Resource variableBindingResource = s.getObject().asResource();
                        String hasBindingValue = getLiteralByProperty(variableBindingResource, loisGraphModel,
                                        "hasBindingValue");
                        // TODO: hack to get the name
                        String hasVariable = getLiteralByProperty(variableBindingResource, loisGraphModel,
                                        "hasVariable");
                        String[] split = hasVariable.split("#");
                        String variableName = split[split.length - 1];
                        Entity variableBindingCollectionItem = pFactory.newEntity(
                                        prov.qn(variableName, DocumentProv.PROV_NEUROSCIENCE_LOI_PREFIX),
                                        hasVariable);
                        variableBindingCollectionItem.setValue(pFactory.newValue(hasBindingValue));

                        loisBundle.getStatement().add(variableBindingCollectionItem);
                        HadMember hm2 = pFactory.newHadMember(variableBindingWorkflow.getId(),
                                        variableBindingCollectionItem.getId());
                        loisBundle.getStatement().add(hm2);
                });

        }

        private Entity level2QuestionAddVariables(Resource questionResource,
                        Entity questionEntity, HashMap<String, Entity> questionVariablesMap,
                        Activity createQuestionActivity) {

                Entity questionVariableCollection = pFactory.newEntity(
                                prov.qn(Constants.QUESTION_VARIABLES_BINDING,
                                                DocumentProv.PROV_NEUROSCIENCE_QUESTION_PREFIX),
                                "Collection of question variables");
                StmtIterator statements = questionResource
                                .listProperties(questionGraphModel
                                                .getProperty("https://w3id.org/sqo#hasQuestionVariable"));
                Property hasVariableName = questionGraphModel.getProperty("https://w3id.org/sqo#hasVariableName");
                Property hasConstrainsProperty = questionGraphModel.getProperty("https://w3id.org/sqo#hasConstraints");
                statements.forEachRemaining(s -> {
                        Resource questionVariableResource = s.getObject().asResource();
                        String qvName = questionVariableResource.getLocalName();
                        String qvLabel = questionGraphModel.getProperty(questionVariableResource, hasVariableName)
                                        .getString();
                        Entity questionVariableEntity = pFactory.newEntity(prov.qn(qvName, DocumentProv.ENIGMA_PREFIX),
                                        qvLabel);
                        // add member
                        String hasConstraints = questionVariableResource.getProperty(hasConstrainsProperty).getString();
                        questionVariableEntity.getOther().add(pFactory.newOther(DocumentProv.DISK_NS, "hasConstraints",
                                        DocumentProv.DISK_PREFIX, hasConstraints, pFactory.getName().XSD_STRING));
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
                return questionVariableCollection;
        }

        private Entity level2HypothesisAddVariables(Resource hypothesisResource,
                        Entity hypothesisEntity, Entity questionVariableCollection,
                        Activity createHypothesisActivity) {
                StmtIterator getVariableBindingsOnHypothesisGraph = getResourcesByProperty(hypothesisResource,
                                hypothesesGraphModel,
                                "hasVariableBinding");

                Entity variableCollection = pFactory.newEntity(
                                prov.qn(Constants.HYPOTHESIS_VARIABLES_BINDING, DocumentProv.DISK_PREFIX),
                                "Collection of question variables");
                WasDerivedFrom derivationVariables = pFactory.newWasDerivedFrom(null,
                                variableCollection.getId(), questionVariableCollection.getId());
                hypothesisBundle.getStatement().add(variableCollection);
                hypothesisBundle.getStatement().add(derivationVariables);

                getVariableBindingsOnHypothesisGraph.forEachRemaining(statementHypothesis -> {
                        Resource variableBindingResource = statementHypothesis.getObject().asResource();
                        // Create the variable binding entity
                        String variableLocalName = variableBindingResource.getLocalName();
                        String value = getLiteralByProperty(variableBindingResource, hypothesesGraphModel,
                                        "hasBindingValue");
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
                hypothesisBundle.getStatement().add(wgb);
                return variableCollection;
        }

        private void level1WasDerived(Entity questionEntity, Entity hypothesisEntity, Entity lineOfInquiryEntity,
                        Entity triggerEntity) {
                // Link the trigger to the line of inquiry
                WasDerivedFrom triggerFromLine = pFactory.newWasDerivedFrom(triggerEntity.getId(),
                                lineOfInquiryEntity.getId());
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
                Entity questionEntity = pFactory.newEntity(prov.qn(questionName, DocumentProv.ENIGMA_PREFIX),
                                questionLabel);
                String commentLocalName = "comment";
                String commentValue = "Question class represents a scientific question. Is linked to the template, the pattern and all the variables this question uses.";
                questionEntity.getOther().add(pFactory.newOther(DocumentProv.RDFS_NS, commentLocalName,
                                DocumentProv.RDFS_PREFIX, commentValue,
                                pFactory.getName().XSD_NAME));
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
                hypothesisEntity.getOther().add(pFactory.newOther(DocumentProv.DCTERMS_NS, "created",
                                DocumentProv.DCTERMS_PREFIX, dateCreated,
                                pFactory.getName().XSD_NAME));
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

        public Entity mapLineOfInquiry(String lineOfInquiryURI, Bundle loisBundle, Entity hypothesis,
                        Entity questionEntity)
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
                Activity activity = pFactory.newActivity(prov.qn(activityName, DocumentProv.DISK_PREFIX), null, null,
                                null);

                // create the metaWorkflowBinding and workflowBinding entities
                if (metaWorkflowBindingResource != null) {

                        String metaWorkflowURI = getLiteralByProperty(metaWorkflowBindingResource, loisGraphModel,
                                        "hasWorkflow");
                        String metaWorkflowBindingName = metaWorkflowBindingResource.getProperty(RDFS.comment)
                                        .getString();
                        Entity metaWorkflowBinding = pFactory.newEntity(
                                        prov.qn("metaWorkflowBinding", DocumentProv.DISK_PREFIX),
                                        metaWorkflowBindingName);
                        Value value = pFactory.newValue(metaWorkflowURI);
                        metaWorkflowBinding.setValue(value);

                        Activity activitySelectQuestion = pFactory.newActivity(
                                        prov.qn("select_question", DocumentProv.DISK_PREFIX),
                                        null, null, null);
                        Used used = pFactory.newUsed(null, activitySelectQuestion.getId(), questionEntity.getId(), null,
                                        null);

                        Activity activitySelectMetaWorkflow = pFactory
                                        .newActivity(prov.qn("select_workflow", DocumentProv.DISK_PREFIX), null, null,
                                                        null);
                        Entity workflow = pFactory.newEntity(prov.qn("workflow", DocumentProv.DISK_PREFIX),
                                        metaWorkflowURI);
                        Used used2 = pFactory.newUsed(null, activitySelectMetaWorkflow.getId(), workflow.getId(), null,
                                        null);
                        WasGeneratedBy wgb = pFactory.newWasGeneratedBy(null, metaWorkflowBinding.getId(),
                                        activitySelectMetaWorkflow.getId(), null, null);

                        WasGeneratedBy wgb2 = pFactory.newWasGeneratedBy(null, metaWorkflowBinding.getId(),
                                        activity.getId(), null,
                                        null);

                        loisBundle.getStatement()
                                        .addAll(Arrays.asList(metaWorkflowBinding, wgb, workflow, used, wgb2,
                                                        activitySelectMetaWorkflow));
                        getResourcesByProperty(metaWorkflowBindingResource, loisGraphModel, "hasVariableBinding")
                                        .forEachRemaining(statement -> {
                                                Resource workflowVariableBindingResource = statement.getObject()
                                                                .asResource();
                                                // TODO: blank node
                                                String variableName = workflowVariableBindingResource.getId()
                                                                .toString();
                                                String variableValue = getLiteralByProperty(
                                                                workflowVariableBindingResource, loisGraphModel,
                                                                "hasBindingValue");
                                                String variableWorkflow = getLiteralByProperty(
                                                                workflowVariableBindingResource, loisGraphModel,
                                                                "hasVariable");
                                                Entity workflowVariableBinding = pFactory.newEntity(
                                                                prov.qn("variable_" + variableName,
                                                                                DocumentProv.DISK_PREFIX),
                                                                variableValue);
                                                WasDerivedFrom wdf = pFactory.newWasDerivedFrom(null,
                                                                workflowVariableBinding.getId(),
                                                                metaWorkflowBinding.getId());
                                                loisBundle.getStatement()
                                                                .addAll(Arrays.asList(workflowVariableBinding, wdf));
                                        });
                        loisBundle.getStatement().addAll(Arrays.asList(metaWorkflowBinding, wgb2, workflow));

                }
                if (workflowBindingResource != null) {
                        String workflowBindingName = metaWorkflowBindingResource.getProperty(RDFS.comment).getString();
                        Entity workflowBinding = pFactory.newEntity(
                                        prov.qn("workflowBinding", DocumentProv.DISK_PREFIX),
                                        workflowBindingName);
                        WasGeneratedBy wgb2 = pFactory.newWasGeneratedBy(null, workflowBinding.getId(),
                                        activity.getId(), null,
                                        null);
                        loisBundle.getStatement().addAll(Arrays.asList(workflowBinding, wgb2));
                }

                // Relation with the hypothesis
                WasGeneratedBy wgb = pFactory.newWasGeneratedBy(null, lineOfInquiryEntity.getId(), activity.getId(),
                                null,
                                null);
                Used used = pFactory.newUsed(null, activity.getId(), hypothesis.getId(), null, null);
                WasDerivedFrom wdf = pFactory.newWasDerivedFrom(null, lineOfInquiryEntity.getId(), hypothesis.getId(),
                                null,
                                null, null, null);
                // Add
                loisBundle.getStatement().addAll(Arrays.asList(lineOfInquiryEntity, activity, wgb, used, wdf));

                // Data query template
                Entity dataQueryTemplate = pFactory.newEntity(prov.qn("dataQueryTemplate", DocumentProv.DISK_PREFIX),
                                dataQueryDescription);
                WasGeneratedBy wgb3 = pFactory.newWasGeneratedBy(null, dataQueryTemplate.getId(), activity.getId(),
                                null,
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

        public static void printElement(IRI iri, String description) {
                System.out.println(iri + "&" + description);
        }

        public static void printElement(IRI iri, String description, String domain, String range) {
                System.out.println(iri + "&" + description + " Domain: " + domain + " Range: " + range);
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

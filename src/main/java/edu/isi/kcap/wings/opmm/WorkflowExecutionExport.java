package edu.isi.kcap.wings.opmm;

import static edu.isi.kcap.wings.opmm.Constants.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;

import edu.isi.kcap.wings.opmm.Publisher.TriplesPublisher;

/**
 * Class designed to export WINGS workflow execution traces in RDF according to
 * the OPMW-PROV model.
 * See: https://www.opmw.org/ontology/
 * <p>
 * This class also has methods for retrieving data in PROV.
 * See https://www.w3.org/TR/prov-o/ and http://purl.org/net/p-plan#
 *
 * @author Daniel Garijo, with the help of Tirth Rajen Mehta
 */
public class WorkflowExecutionExport {
    // Variable to store the execution model (source)
    private final OntModel executionModel;
    // Variable to store the opmwModel (target)
    private OntModel opmwModel;
    // Neeeded to publish the execution
    private final Catalog componentCatalog;
    //
    private final String PREFIX_EXPORT_RESOURCE;
    private final String endpointQueryURI;// URI of the endpoint where everything is published.
    private final String exportUrl;
    private String transformedExecutionURI;
    private WorkflowTemplateExport concreteTemplateExport;
    private FilePublisher filePublisher;
    private TriplesPublisher triplesPublisher;

    private String uploadURL;
    private String uploadUsername;
    private String uploadPassword;
    private String uploadDirectory;
    private long uploadMaxSize;
    // private OntModel provModel;//TO IMPLEMENT AT THE END. Can it be done with
    // constructs?
    private String domain;
    private String serialization;

    /**
     * Default constructor for exporting executions {@link #executionModel}
     * Create a OntModel: wingsExecutionModel from the executionFile
     * Create a Model to store the OPMW model from the wingsExecutionModel
     * {@link #executionModel}
     *
     * @param executionFile: The execution file to be exported
     * @param catalog        The class to deal with versioning
     * @param exportUrl:     Used to create the export URL
     *                       {exportUrl}/{exportName}/resource/
     * @param exportName:    Used to create the export URL
     *                       {exportUrl}/{exportName}/resource/
     * @param endpointURI    URI of the endpoint where everything is published.
     * @param domain:        The Wings domain where the execution is being exported
     * @param filePublisher: The class to publish the files (e.g. to S3, local,
     *                       remote)
     */
    public WorkflowExecutionExport(String executionFile, Catalog catalog, String exportUrl,
            String domain, FilePublisher filePublisher, TriplesPublisher triplesPublisher) {
        // Store the parameters as fields
        this.serialization = triplesPublisher.serialization;
        this.componentCatalog = catalog;
        this.filePublisher = filePublisher;
        this.triplesPublisher = triplesPublisher;
        this.endpointQueryURI = triplesPublisher.queryEndpoint;
        this.exportUrl = exportUrl;
        this.domain = domain;

        // Create the execution model from the execution file
        this.executionModel = ModelUtils.loadModel(executionFile);
        // Create the OPMW model to store the execution model data in OPMW format
        this.opmwModel = ModelUtils.initializeModel(opmwModel);
        // If the export url is not provided then use the default export url
        if (exportUrl == null)
            exportUrl = Constants.PREFIX_EXPORT_GENERIC;

        PREFIX_EXPORT_RESOURCE = exportUrl + "resource/";
        isExecPublished = false;
    }

    /**
     * Function that will check if an execution exists and then transforms it as RDF
     * under the OPMW model.
     * Assumption: there is a single execution per file.
     */
    public void transform() {
        try {
            // select THE execution instance
            Individual wingsExecution = (Individual) executionModel.getOntClass(Constants.WINGS_EXECUTION)
                    .listInstances().next();
            // load the plan of the execution. It has critical information such as the
            String plan = wingsExecution
                    .getPropertyValue(executionModel.getProperty(Constants.WINGS_PROP_HAS_PLAN)).asResource()
                    .getURI();
            executionModel.add(ModelUtils.loadModel(plan));
            // ask if an execution with same run id exists. If so, return URI. The local
            // name of the execution is its run id.
            String queryExec = QueriesWorkflowExecutionExport.getOPMWExecutionsWithRunID(wingsExecution.getLocalName());
            QuerySolution solution = ModelUtils.queryOnlineRepository(queryExec, endpointQueryURI);
            if (solution != null) {
                System.out.println("Execution exists!");
                this.transformedExecutionURI = (solution.getResource("?exec").getURI());
                isExecPublished = true;
            } else {
                System.out.println("Execution does not exist! Publishing new execution");
                this.transformedExecutionURI = convertExecutionToOPMW(wingsExecution);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage() + "\n The execution was not exported");
        }
    }

    /**
     * General method to convert a WINGS execution to OPMW
     *
     * @param wingsExecution instance pointer to the execution
     * @return The URI of the template being generated.
     * @throws IOException
     */
    private String convertExecutionToOPMW(Individual wingsExecution) throws IOException {
        // Create the account and its metadata.
        final String executionTemplateNS = PREFIX_EXPORT_RESOURCE + Constants.CONCEPT_WORKFLOW_EXECUTION_ACCOUNT + "/";
        String runID = wingsExecution.getLocalName();
        String we = executionTemplateNS + runID;
        Individual weInstance = opmwModel.createClass(Constants.OPMW_WORKFLOW_EXECUTION_ACCOUNT).createIndividual(we);
        ModelUtils.addClassesToIndividual(weInstance, Constants.PROV_ENTITY, opmwModel);
        // add label, run id (local name)
        weInstance.addLabel(runID, null);
        weInstance.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_HAS_RUN_ID), runID);
        // original log file
        weInstance.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_HAS_ORIGINAL_EXECUTION_FILE),
                wingsExecution.getURI());
        // hasTime, endTime and execution status.
        String queryExecutionMetadata = QueriesWorkflowExecutionExport
                .getWINGSExecutionMetadata(wingsExecution.getURI());
        ResultSet rs = ModelUtils.queryLocalRepository(queryExecutionMetadata, executionModel);
        // there is only 1 execution per file
        if (rs.hasNext()) {
            QuerySolution qs = rs.next();
            Literal start = qs.getLiteral("?start");
            Literal end = qs.getLiteral("?end");
            Literal status = qs.getLiteral("?status");
            weInstance.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_OVERALL_START_TIME), start);
            weInstance.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_OVERALL_END_TIME), end);
            weInstance.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_STATUS), status);
        }
        // link user. Info not available so it is extracted from URL.
        // TODO: Ask Varun if user can be accessed without deriving it from path
        String user = wingsExecution.getURI().split("/users/")[1];
        user = user.substring(0, user.indexOf("/"));
        String userURI = PREFIX_EXPORT_RESOURCE + Constants.CONCEPT_AGENT + "/" + user;
        Individual userInstance = opmwModel.createClass(Constants.PROV_AGENT).createIndividual(userURI);
        ModelUtils.addClassesToIndividual(userInstance, Constants.PROV_ENTITY, opmwModel);
        userInstance.addLabel(user, null);
        weInstance.addProperty(opmwModel.createProperty(Constants.PROP_HAS_CONTRIBUTOR), userInstance);

        // metadata of the execution system (WINGS)
        // TODO: Ask Varun to include extra metadata on whether the exec was on
        // Pegasus/OODT, etc.
        String wfSystemURI = PREFIX_EXPORT_RESOURCE + Constants.CONCEPT_AGENT + "/" + "WINGS";
        Individual wingsInstance = opmwModel.createClass(Constants.PROV_AGENT).createIndividual(wfSystemURI);
        ModelUtils.addClassesToIndividual(wingsInstance, Constants.PROV_ENTITY, opmwModel);
        weInstance.addProperty(opmwModel.createProperty(Constants.PROP_HAS_CREATOR), wingsInstance);
        wingsInstance.addProperty(opmwModel.createProperty(Constants.PROV_ACTED_ON_BEHALF_OF), userInstance);
        wingsInstance.addLabel("WINGS", null);

        // get expanded template loaded in local model (for parameter linking)
        String queryExpandedTemplate = QueriesWorkflowExecutionExport.getWINGSExpandedTemplate();
        rs = ModelUtils.queryLocalRepository(queryExpandedTemplate, executionModel);
        String expandedTemplateURI = null;
        if (rs.hasNext()) {
            expandedTemplateURI = rs.next().getResource("?expTemplate").getNameSpace();// the namespace is better for
                                                                                       // later.
            System.out.println("Execution expanded template " + expandedTemplateURI + " loaded successfully");
            // publish expanded template. The expanded template will publish the template if
            // necessary.
            concreteTemplateExport = new WorkflowTemplateExport(expandedTemplateURI, this.componentCatalog,
                    this.exportUrl, this.domain, this.triplesPublisher);
            concreteTemplateExport.transform();
            // concreteTemplateExport.exportAsOPMW(null, serialization);
            System.out.println(concreteTemplateExport.getTransformedTemplateIndividual());
        } else {
            System.out.println("ERROR: Could not find an expanded template!");
        }

        // transform all steps and data dependencies (params are in expanded template)
        String queryExecutionStepMetadata = QueriesWorkflowExecutionExport.getWINGSExecutionStepsAndMetadata();
        rs = ModelUtils.queryLocalRepository(queryExecutionStepMetadata, executionModel);
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            Resource wingsStep = qs.getResource("?step");
            Literal start = qs.getLiteral("?start");
            Literal end = qs.getLiteral("?end");
            Literal status = qs.getLiteral("?status");
            Literal executionScript = qs.getLiteral("?code");
            Literal invLine = qs.getLiteral("?invLine");
            String executionStepURI = PREFIX_EXPORT_RESOURCE + Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS + "/"
                    + runID + "_" + wingsStep.getLocalName();
            Individual executionStep = opmwModel.createClass(Constants.OPMW_WORKFLOW_EXECUTION_PROCESS)
                    .createIndividual(executionStepURI);
            ModelUtils.addClassesToIndividual(executionStep, Constants.PROV_ENTITY, opmwModel);
            executionStep.addLabel(wingsStep.getLocalName(), null);
            executionStep.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_HAD_INVOCATION_COMMAND),
                    invLine);
            executionStep.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_HAD_START_TIME), start);
            executionStep.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_STATUS), status);
            executionStep.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_HAD_END_TIME), end);
            // Creation of the software config. This may be moved to another class for
            // simplicity and because it
            // can be used in the catalog
            // too.http://localhost:8080/wings_portal/users/admin/CaesarCypher/data/fetch?data_id=http%3A//localhost%3A8080/wings_portal/export/users/admin/CaesarCypher/data/library.owl%23USconstitution.txt
            // TODO: if there are errors, then create a config URI based on the node and URI
            // (i.e., unique)

            /*
             * Export the component code
             * Zip the directory and upload
             */

            String configURI = PREFIX_EXPORT_RESOURCE + Constants.CONCEPT_SOFTWARE_CONFIGURATION + "/" + runID + "_"
                    + wingsStep.getLocalName() + "_config";
            Individual stepConfig = opmwModel.createClass(Constants.OPMW_SOFTWARE_CONFIGURATION)
                    .createIndividual(configURI);
            ModelUtils.addClassesToIndividual(stepConfig, Constants.PROV_ENTITY, opmwModel);
            stepConfig.addLabel(stepConfig.getLocalName(), null);

            String configLocation = executionScript.getString().replace("/run", "");
            configLocation = configLocation.replaceAll("\\s", "");
            File directory = new File(configLocation);
            StorageHandler storage = new StorageHandler();
            File tempFile;
            try {

                tempFile = storage.zipFolder(directory);
                try {
                    filePublisher.publishFile(tempFile);
                } catch (Exception e) {
                    System.out.println("Error publishing directory:" + directory + " compressed as " + tempFile);
                    System.err.println(e.getMessage());
                }
            } catch (Exception e) {
                System.out.println("Error compress directory:" + directory);
                System.err.println(e.getMessage());
            }
            stepConfig.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_HAS_LOCATION), configLocation);

            /* Export the mainscript and upload */
            // String mainScriptLocation = uploadFile(executionScript.getString());
            String mainScriptLocation = executionScript.getString().replaceAll("\\s", "");
            if (mainScriptLocation != null) {
                mainScriptLocation = filePublisher.publishFile(mainScriptLocation).getFileUrl();
            } else {
                System.out.println("Error publishing main script:" + mainScriptLocation);
            }
            String mainScriptURI = PREFIX_EXPORT_RESOURCE + Constants.CONCEPT_SOFTWARE_CONFIGURATION + "/" + runID + "_"
                    + wingsStep.getLocalName() + "_mainscript";
            Resource mainScript = ModelUtils.getIndividualFromFile(mainScriptLocation, opmwModel,
                    Constants.OPMW_SOFTWARE_SCRIPT, mainScriptURI);
            stepConfig.addProperty(opmwModel.createProperty(Constants.OPMW_PROP_HAS_MAIN_SCRIPT), mainScript);
            executionStep.addProperty(opmwModel.createProperty(Constants.OPMW_PROP_HAD_SOFTWARE_CONFIGURATION),
                    stepConfig);
            executionStep.addProperty(opmwModel.createProperty(Constants.PROV_WAS_ASSOCIATED_WITH), wingsInstance);

            // for each step get its i/o (plan)
            String stepVariables = QueriesWorkflowExecutionExport.getWINGSExecutionStepI_O(wingsStep.getURI());
            ResultSet rsVar = ModelUtils.queryLocalRepository(stepVariables, executionModel);
            while (rsVar.hasNext()) {
                QuerySolution qsVar = rsVar.next();
                String varType = qsVar.getResource("?varType").getURI();
                Resource variable = qsVar.getResource("?variable");
                Literal binding = qsVar.getLiteral("?binding");
                String executionArtifactURI = PREFIX_EXPORT_RESOURCE + Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT
                        + "/" + runID + "_" + variable.getLocalName();
                Individual executionArtifact = opmwModel.createClass(Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT)
                        .createIndividual(executionArtifactURI);
                ModelUtils.addClassesToIndividual(executionArtifact, Constants.PROV_ENTITY, opmwModel);
                executionArtifact.addLabel(variable.getLocalName(), null);
                String pathFile = binding.toString();
                // String dataLocation = uploadFile(pathFile);
                String dataLocation;
                dataLocation = filePublisher.publishFile(pathFile).getFileUrl();
                executionArtifact.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_HAS_LOCATION),
                        dataLocation);

                // add as part of the account
                executionArtifact.addProperty(opmwModel.createProperty(Constants.OPMW_PROP_IS_ARTIFACT_OF_EXECUTION),
                        weInstance);
                if (varType.equals(Constants.P_PLAN_PROP_HAS_INPUT)) {
                    // add step used artifact
                    executionStep.addProperty(opmwModel.createProperty(Constants.PROV_USED), executionArtifact);

                } else if (varType.equals(Constants.P_PLAN_PROP_HAS_OUTPUT)) {
                    executionArtifact.addProperty(opmwModel.createProperty(Constants.PROV_WGB), executionStep);
                }
                // Link to expanded template variable
                String concreteTemplateVariableURI = PREFIX_EXPORT_RESOURCE + Constants.CONCEPT_DATA_VARIABLE + "/" +
                        concreteTemplateExport.getTransformedTemplateIndividual().getLocalName() + "_"
                        + variable.getLocalName();
                Individual concreteTemplateVariable;
                // if the template has been published the resources are not loaded. So, we
                // create the individual
                if (concreteTemplateExport.isTemplatePublished()) {
                    OntClass aClass = concreteTemplateExport.getOpmwModel().createClass(OPMW_DATA_VARIABLE);

                    concreteTemplateVariable = concreteTemplateExport.getOpmwModel()
                            .createIndividual(concreteTemplateVariableURI, aClass);
                } else {
                    concreteTemplateVariable = concreteTemplateExport.getOpmwModel()
                            .getIndividual(concreteTemplateVariableURI);
                }
                ModelUtils.addClassesToIndividual(concreteTemplateVariable, Constants.PROV_ENTITY, opmwModel);
                executionArtifact.addProperty(
                        opmwModel.createAnnotationProperty(Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE_ARTIFACT),
                        concreteTemplateVariable);
            }
            // Parameters are specified in the expanded template. The current step is
            // specified with the expanded template URI, same process id.
            String wingsExpandedTempProcessURI = expandedTemplateURI + wingsStep.getLocalName();
            String queryParams = QueriesWorkflowExecutionExport.getWINGSParametersForStep(wingsExpandedTempProcessURI);
            ResultSet params = ModelUtils.queryLocalRepository(queryParams,
                    concreteTemplateExport.getWingsTemplateModel());
            while (params.hasNext()) {
                QuerySolution nextP = params.next();
                Resource param = nextP.getResource("?param");
                Literal paramValue = nextP.getLiteral("?paramValue");
                // add param, add its value and link it to execution
                String parameterURI = PREFIX_EXPORT_RESOURCE + Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT + "/"
                        + runID + "_" + param.getLocalName();
                Individual parameter = opmwModel.createClass(Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT)
                        .createIndividual(parameterURI);
                ModelUtils.addClassesToIndividual(parameter, Constants.PROV_ENTITY, opmwModel);
                parameter.addLabel(param.getLocalName(), null);
                parameter.addProperty(opmwModel.createProperty(Constants.OPMW_DATA_PROP_HAS_VALUE), paramValue);
                parameter.addProperty(opmwModel.createProperty(Constants.OPMW_PROP_IS_ARTIFACT_OF_EXECUTION),
                        weInstance);
                executionStep.addProperty(opmwModel.createProperty(Constants.PROV_USED), parameter);
                // link parameter to the concrete template
                String concreteTemplateParameterURI = PREFIX_EXPORT_RESOURCE + Constants.CONCEPT_PARAMETER_VARIABLE
                        + "/" +
                        concreteTemplateExport.getTransformedTemplateIndividual().getLocalName() + "_"
                        + param.getLocalName();

                // if the template has been published the resources are not loaded. So, we
                // create the individual
                Individual concreteTemplateParameter;
                if (concreteTemplateExport.isTemplatePublished()) {
                    OntClass aClass = concreteTemplateExport.getOpmwModel().createClass(OPMW_PARAMETER_VARIABLE);
                    concreteTemplateParameter = concreteTemplateExport.getOpmwModel()
                            .createIndividual(concreteTemplateParameterURI, aClass);
                } else {
                    concreteTemplateParameter = concreteTemplateExport.getOpmwModel()
                            .getIndividual(concreteTemplateParameterURI);
                }
                ModelUtils.addClassesToIndividual(concreteTemplateParameter, Constants.PROV_ENTITY, opmwModel);
                parameter.addProperty(
                        opmwModel.createAnnotationProperty(Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE_ARTIFACT),
                        concreteTemplateParameter);

            }
            // link step to execution
            executionStep.addProperty(opmwModel.createProperty(Constants.OPMW_PROP_IS_PROCESS_OF_EXECUTION),
                    weInstance);
            // link step to concrete template individual
            String concreteTemplateProcessURI = PREFIX_EXPORT_RESOURCE + Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS
                    + "/" +
                    concreteTemplateExport.getTransformedTemplateIndividual().getLocalName() + "_"
                    + wingsStep.getLocalName();

            // if the template has been published the resources are not loaded. So, we
            // create the individual
            Individual concreteTemplateProcess;
            if (concreteTemplateExport.isTemplatePublished()) {
                OntClass ontClass = concreteTemplateExport.getOpmwModel().createClass(OPMW_WORKFLOW_TEMPLATE_PROCESS);
                concreteTemplateProcess = concreteTemplateExport.getOpmwModel()
                        .createIndividual(concreteTemplateProcessURI, ontClass);
            } else {
                concreteTemplateProcess = concreteTemplateExport.getOpmwModel()
                        .getIndividual(concreteTemplateProcessURI);
            }
            ModelUtils.addClassesToIndividual(executionStep, Constants.PROV_ENTITY, opmwModel);
            executionStep.addProperty(
                    opmwModel.createAnnotationProperty(Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE_PROCESS),
                    concreteTemplateProcess);
        }
        // link execution account to expanded template.
        weInstance.addProperty(opmwModel.createProperty(Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE),
                concreteTemplateExport.getTransformedTemplateIndividual());
        return we;
    }

    /**
     * Function that exports the transformed template in OPMW. This function should
     * be called after
     * "transform". If not, it will call transform() automatically.
     *
     * @param filepath      path where to write the serialized model
     * @param serialization serialization of choice: RDF/XML, TTL, etc.
     * @throws IOException
     */
    public String exportAsOPMW(String filepath, String serialization) throws IOException, FileNotFoundException {
        if (transformedExecutionURI == null) {
            this.transform();
        }
        if (!isExecPublished) {
            // opmwModel.write(System.out, "TTL");
            ModelUtils.exportRDFFile(filepath, opmwModel, serialization);
            File file = new File(filepath);
            this.triplesPublisher.setGraphURI(this.transformedExecutionURI);
            this.triplesPublisher.publish(file);
        }
        return transformedExecutionURI;
    }

    /**
     * Function that exports the transformed template in P-Plan format. This
     * function should be called after
     * "transform". If not, it will call transform() automatically.
     *
     * @param outFilePath   path where to write the serialized model
     * @param serialization serialization of choice: RDF/XML, TTL, etc.
     */
    public void exportAsPROV(String outFilePath, String serialization) {
        // TO DO
        System.out.println("Not done yet!");
    }

    /**
     * Function that exports the transformed template in OPMW and P-Plan (in
     * different files)
     *
     * @param outFileDirectory path where to write the serialized model
     * @param serialization    serialization of choice: RDF/XML, TTL, etc.
     */
    public void exportAll(String outFileDirectory, String serialization) {
        // TO DO
        System.out.println("Not done yet!");
        // this.export_as_OPMW(outFilePath, serialization);
        // this.export_as_PPlan(outFilePath, serialization);
    }

    // TO DO: When exporting, do the execution inputs and output collection as I
    // discussed with Milan.?

    public WorkflowTemplateExport getConcreteTemplateExport() {
        return concreteTemplateExport;
    }

    public void setConcreteTemplateExport(WorkflowTemplateExport concreteTemplateExport) {
        this.concreteTemplateExport = concreteTemplateExport;
    }

    /**
     * Function that will return the transformed execution URI.
     *
     * @return transformed execution URI. Null if error
     */
    public String getTransformedExecutionURI() {
        if (transformedExecutionURI == null) {
            transform();
        }
        return transformedExecutionURI;
    }

    // Getters and setters
    public boolean isExecPublished() {
        return isExecPublished;
    }

    private boolean isExecPublished;

    public void setUploadURL(String uploadURL) {
        this.uploadURL = uploadURL;
    }

    public void setUploadUsername(String uploadUsername) {
        this.uploadUsername = uploadUsername;
    }

    public void setUploadPassword(String uploadPassword) {
        this.uploadPassword = uploadPassword;
    }

    public void setUploadMaxSize(long uploadMaxSize) {
        this.uploadMaxSize = uploadMaxSize;
    }

    public void setUploadDirectory(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }
}

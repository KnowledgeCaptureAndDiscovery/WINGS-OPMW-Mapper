package edu.isi.kcap.wings.opmm;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;

import edu.isi.kcap.wings.opmm.Publisher.TriplesPublisher;

/**
 * Class designed to export WINGS workflow templates in RDF according to the
 * OPMW-PROV model.
 * See: https://www.opmw.org/ontology/
 *
 * This class also has methods for retrieving data in PROV and P-Plan.
 * See https://www.w3.org/TR/prov-o/ and http://purl.org/net/p-plan#
 *
 * @author Daniel Garijo, with the help of Tirth Rajen Mehta
 */
public class WorkflowTemplateExport {

    private OntModel wingsTemplateModel;
    private OntModel opmwModel;
    private Catalog componentCatalog;
    private String exportUrl;
    private String endpointURI;// URI of the endpoint where everything is published.
    private Individual transformedTemplate = null;
    public TriplesPublisher triplesPublisher;
    private boolean isTemplatePublished;// boolean value to know if the template has already been published on the
                                        // repository
    private String exportName;// needed to pass it on to template exports

    private WorkflowTemplateExport abstractTemplateExport;// a template may implement a template, and therefore publish
                                                          // its abstract template (on a separate file)
    private String domain;

    /**
     * Basic constructor
     *
     * @param templateFile path to the template to load (may be a URI)
     * @param catalog      catalog instance, already initialized
     * @param exportName   name of the dataset to export (will be part of the URI)
     * @param endpointURI
     */
    public WorkflowTemplateExport(String templateFile, Catalog catalog, String exportUrl,
            String domain, TriplesPublisher triplesPublisher) {
        workaroundLocalhost(templateFile);
        this.triplesPublisher = triplesPublisher;
        this.opmwModel = ModelUtils.initializeModel(opmwModel);
        this.componentCatalog = catalog;
        this.exportUrl = exportUrl + "resource/";
        this.endpointURI = this.triplesPublisher.queryEndpoint;
        isTemplatePublished = false;
        this.domain = domain;
    }

    private void workaroundLocalhost(String templateFile) {
        try {
            this.wingsTemplateModel = ModelUtils.loadModel(templateFile);
        } catch (Exception e) {
            // TODO: This is a hack to make it work with the test cases. Fix it. The reason
            // is that wings has a misconfiguration on the uri of the template
            System.out.println("Error loading template " + templateFile);
            templateFile = templateFile.replace("http://localhost:8080", "https://wings.disk.isi.edu");
            this.wingsTemplateModel = ModelUtils.loadModel(templateFile);
        }
        if (this.wingsTemplateModel == null) {
            throw new NullPointerException("Error loading template " + templateFile);
        }
    }

    /**
     * Function that returns the transformed template URI.
     *
     * @return transformed template URI. Null if there was an error in the
     *         transformation process
     */
    public Individual getTransformedTemplateIndividual() {
        if (transformedTemplate == null) {
            this.transform();
        }
        return transformedTemplate;
    }

    public OntModel getWingsTemplateModel() {
        return wingsTemplateModel;
    }

    public OntModel getOpmwModel() {
        if (transformedTemplate == null) {
            this.transform();
        }
        return opmwModel;
    }

    /**
     * Function that will check if a template exists and then transforms it as RDF.
     * Assumption: there is a single template per file.
     * The function will first check that the MD5 of the template exist, and then
     * its name.
     * Different expanded templates will not be versioned.
     */
    public void transform() {
        OntClass workflowTemplateClass = wingsTemplateModel.getOntClass(Constants.WINGS_WF_TEMPLATE);
        Individual wingsTemplate = (Individual) workflowTemplateClass
                .listInstances().next();
        String wingsTemplateName = wingsTemplate.getLocalName();
        String exportedTemplateURI;
        // create hash for template
        String templateHash = HashUtils.createMD5ForTemplate(wingsTemplate, wingsTemplateModel,
                this.componentCatalog.getWINGSDomainTaxonomy());
        System.out.println("WINGS Template hash: " + templateHash);
        String queryMd5 = QueriesWorkflowTemplateExport.getOPMWTemplatesWithMD5Hash(templateHash);
        // if same MD5, return URI of the found template (there may be diferent versions
        // and found a previous one)
        QuerySolution solution = ModelUtils.queryOnlineRepository(queryMd5, endpointURI);
        if (solution != null) {
            Resource foundTemplateURI = solution.getResource("?t");
            System.out.println("Template " + wingsTemplateName + " has already been published as "
                    + foundTemplateURI.getURI());
            // no export is necessary
            transformedTemplate = opmwModel.createClass(Constants.OPMW_WORKFLOW_TEMPLATE)
                    .createIndividual(foundTemplateURI.getURI());
            ModelUtils.addClassesToIndividual(transformedTemplate, Constants.PROV_ENTITY, opmwModel);
            // String queryExec =
            // QueriesWorkflowExecutionExport.getOPMWExecutionsWithRunID(wingsExecution.getLocalName());
            // QuerySolution solution = ModelUtils.queryOnlineRepository(queryExec,
            // endpointURI);
            String queryTemplate = QueriesWorkflowTemplateExport
                    .queryRetrieveAbstractTemplateElements(foundTemplateURI.getURI());
            opmwModel.add(ModelUtils.constructOnlineRepository(queryTemplate, endpointURI));
            isTemplatePublished = true;
            return;
        } else {
            // different MD5. Could it be a version of an existing template?
            // get all templates published with the same label. All published templates will
            // have a version
            // note: union graph only works when there are more graphs than the defaul one.
            String queryT = QueriesWorkflowTemplateExport.getOPMWTemplatesWithLabel(wingsTemplateName);
            solution = ModelUtils.queryOnlineRepository(queryT, endpointURI);
            if (solution == null) {
                // template name does not exist, hence this is the first version
                System.out.println("Template name does not exist. Publishing with latest version");
                exportedTemplateURI = convertTemplateToOPMW(wingsTemplate, 1);
            } else {
                Resource latestTemplate = solution.getResource("?t");
                // check if there is a version number automatically associated to template.
                int latestTemplateVersionNumber;
                try {
                    latestTemplateVersionNumber = solution.getLiteral("?v").getInt();
                } catch (Exception e) {
                    // Existing template but no version number available, using default (1)
                    latestTemplateVersionNumber = 1;
                }
                System.out.println("A previous version of template " + wingsTemplateName
                        + " has been found, publishing as a new version ");
                exportedTemplateURI = convertTemplateToOPMW(wingsTemplate, latestTemplateVersionNumber + 1);
                // add the link between versions: exportedTemplate wasRevisionOf foundTemplate
                // This is commented because it's not always correct: If 2 users are publishing
                // templates from the same domain,
                // you don't know which one it really came from.
                // we do alternate of. It should be from each, but since it's transitive, it's
                // fine.
                Individual templateExported = opmwModel.getIndividual(exportedTemplateURI);
                templateExported.addProperty(opmwModel.createProperty(Constants.PROV_ALTERNATE_OF), latestTemplate);
            }
        }
        this.transformedTemplate = opmwModel.getIndividual(exportedTemplateURI);
    }

    /**
     * General method to convert a WINGS template to OPMW
     *
     * @param wingsTemplate pointer to the template object we want to transform.
     * @param versionNumber version number to publish the template
     * @return The URI of the template being generated.
     */
    private String convertTemplateToOPMW(Individual wingsTemplate, int versionNumber) {
        final String workflowTemplateNS = this.exportUrl + Constants.CONCEPT_WORKFLOW_TEMPLATE + "/";
        String wt = workflowTemplateNS + wingsTemplate.getLocalName() + "_v" + versionNumber;
        Individual abstractTemplateInstance;
        Individual wtInstance = opmwModel.createClass(Constants.OPMW_WORKFLOW_TEMPLATE).createIndividual(wt);
        ModelUtils.addClassesToIndividual(wtInstance, Constants.PROV_ENTITY, opmwModel);
        // add label
        wtInstance.addLabel(wingsTemplate.getLocalName(), null);
        // add version number (internal, with owlInfo)
        wtInstance.addLiteral(opmwModel.createProperty(Constants.OWL_VERSION_INFO), versionNumber);
        // template MD5
        wtInstance.addLiteral(opmwModel.createProperty(Constants.OPMW_DATA_PROP_HAS_MD5),
                HashUtils.createMD5ForTemplate(wingsTemplate, this.wingsTemplateModel,
                        this.componentCatalog.getWINGSDomainTaxonomy()));
        wtInstance.addLiteral(opmwModel.createProperty(Constants.OPMW_DATA_PROP_HAS_DOMAIN), this.domain);
        // add the native system template as a reference.
        opmwModel.add(wtInstance, opmwModel.createProperty(Constants.OPMW_DATA_PROP_HAS_NATIVE_SYSTEM_TEMPLATE),
                wingsTemplate.getURI(), XSDDatatype.XSDanyURI);
        // state that the template was created in WINGS
        opmwModel.add(wtInstance, opmwModel.createProperty(Constants.OPMW_DATA_PROP_CREATED_IN_WORKFLOW_SYSTEM),
                "http://wings.isi.edu", XSDDatatype.XSDanyURI);
        // date when we are registering the template in the provenance catalog
        opmwModel.add(wtInstance, opmwModel.createProperty(Constants.DC_ISSUED_TIME),
                java.time.LocalDateTime.now().toString(), XSDDatatype.XSDdateTime);
        // query all other metadata from WINGS template
        String queryTemplateMetadata = QueriesWorkflowTemplateExport.queryWINGSTemplateMetadata();
        ResultSet rs = ModelUtils.queryLocalRepository(queryTemplateMetadata, wingsTemplateModel);
        // there is only 1 metadata object per template
        if (rs.hasNext()) {
            // variables to extract: ?doc ?contrib ?time ?license
            QuerySolution qs = rs.next();
            Literal docContent = qs.getLiteral("?doc");
            if (docContent != null)
                wtInstance.addLiteral(opmwModel.createProperty(Constants.OPMW_DATA_PROP_HAS_DOCUMENTATION), docContent);
            Literal contrib = qs.getLiteral("?contrib");
            if (contrib != null) {
                OntClass agentClass = opmwModel.createClass(Constants.PROV_AGENT);
                try {
                    Individual contributor = agentClass.createIndividual(this.exportUrl +
                            Constants.CONCEPT_AGENT + "/" + URLEncoder.encode("" + contrib, "UTF-8"));
                    contributor.addLabel(contrib);
                    wtInstance.addProperty(opmwModel.createProperty(Constants.PROP_HAS_CONTRIBUTOR), contributor);
                } catch (UnsupportedEncodingException e) {
                    System.err.println("Unsupported encoding");
                    e.printStackTrace();
                }
            }
            Literal timeLastModified = qs.getLiteral("?time");
            if (timeLastModified != null)
                wtInstance.addLiteral(opmwModel.createProperty(Constants.DATA_PROP_MODIFIED), timeLastModified);
            Literal userVersion = qs.getLiteral("?version");
            if (userVersion != null)
                wtInstance.addLiteral(opmwModel.createProperty(Constants.OPMW_DATA_PROP_RELEASE_VERSION), userVersion);
            Literal license = qs.getLiteral("?license");
            if (license != null)
                wtInstance.addLiteral(opmwModel.createProperty(Constants.DATA_PROP_RIGHTS), license);
            else
                opmwModel.add(wtInstance, opmwModel.createProperty(Constants.DC_LICENSE),
                        "http://creativecommons.org/licenses/by/3.0/",
                        XSDDatatype.XSDanyURI);
        }
        // if there is any derivation, this template is a concrete template. Publish the
        // abstract template
        String queryDerivation = QueriesWorkflowTemplateExport.queryWINGSDerivations();
        ResultSet rsD = ModelUtils.queryLocalRepository(queryDerivation, wingsTemplateModel);
        if (rsD.hasNext()) {
            // publish abstract template with the URI taken from derivation
            QuerySolution qs = rsD.next();
            this.abstractTemplateExport = new WorkflowTemplateExport(qs.getResource("?dest").getURI(), componentCatalog,
                    exportUrl, domain, triplesPublisher);
            abstractTemplateExport.transform();
            abstractTemplateInstance = abstractTemplateExport.getTransformedTemplateIndividual();
            System.out.println("Abstract template: " + abstractTemplateInstance.getURI());
            wtInstance.addProperty(opmwModel.createProperty(Constants.OPMW_PROP_IMPLEMENTS_TEMPLATE),
                    abstractTemplateInstance);
        }

        // first we add the variables and parameters
        String queryDataVariables = QueriesWorkflowTemplateExport.queryWINGSDataVariables();
        rs = ModelUtils.queryLocalRepository(queryDataVariables, wingsTemplateModel);
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            Resource var = qs.getResource("?d");
            Resource type = qs.getResource("?type");
            Literal dim = qs.getLiteral("?hasDim");
            Literal roleId = qs.getLiteral("?roleID");
            Resource derivedFrom = qs.getResource("?derivedFrom");
            // add it as a variable or parameter
            if (type.isURIResource() &&
                    (type.getURI().equals(Constants.WINGS_DATA_VARIABLE)
                            || (type.getURI().equals(Constants.WINGS_PARAMETER_VARIABLE)))) {
                String varNS, varURI;
                Individual workflowVariable;
                if (type.getURI().equals(Constants.WINGS_DATA_VARIABLE)) {
                    varNS = this.exportUrl + Constants.CONCEPT_DATA_VARIABLE + "/";
                    varURI = varNS + wingsTemplate.getLocalName() + "_v" + versionNumber + "_" + var.getLocalName();
                    workflowVariable = opmwModel.createClass(Constants.OPMW_DATA_VARIABLE).createIndividual(varURI);
                    ModelUtils.addClassesToIndividual(workflowVariable, Constants.PROV_ENTITY, opmwModel);
                    workflowVariable.addProperty(opmwModel.createProperty(Constants.OPMW_PROP_IS_VARIABLE_OF_TEMPLATE),
                            wtInstance);
                    if (dim != null) {
                        if (dim.getInt() > 0) {
                            workflowVariable
                                    .addLiteral(opmwModel.createProperty(Constants.OPMW_DATA_PROP_IS_COLLECTION), true);
                        } else {
                            workflowVariable.addLiteral(
                                    opmwModel.createProperty(Constants.OPMW_DATA_PROP_IS_COLLECTION), false);
                        }
                    }
                    // now retrieve the type (with the catalog we have)
                    String queryDataTypes = QueriesWorkflowTemplateExport
                            .queryWINGSTypesOfArgumentID(roleId.getString());
                    ResultSet rsAux = ModelUtils.queryLocalRepository(queryDataTypes,
                            componentCatalog.getWINGSDomainTaxonomy());
                    while (rsAux.hasNext()) {
                        QuerySolution qsAux = rsAux.next();
                        Resource argument = qsAux.getResource("?argument");
                        if (!argument.isAnon()) {
                            Iterator i = componentCatalog.getWINGSDomainTaxonomy().getIndividual(argument.getURI())
                                    .listOntClasses(true);
                            while (i.hasNext()) {
                                OntClass variableType = (OntClass) i.next();
                                if (!variableType.getURI()
                                        .equals("http://www.wings-workflows.org/ontology/component.owl#DataArgument")) {
                                    String classType = componentCatalog.getCatalogTypeForDataClassURI(variableType);
                                    opmwModel.createClass(classType).createIndividual(varURI);
                                }
                            }
                        }
                    }
                } else {// parameter
                    varNS = this.exportUrl + Constants.CONCEPT_PARAMETER_VARIABLE + "/";
                    varURI = varNS + wingsTemplate.getLocalName() + "_v" + versionNumber + "_" + var.getLocalName();
                    workflowVariable = opmwModel.createClass(Constants.OPMW_PARAMETER_VARIABLE)
                            .createIndividual(varURI);
                    workflowVariable.addProperty(opmwModel.createProperty(Constants.OPMW_PROP_IS_PARAMETER_OF_TEMPLATE),
                            wtInstance);
                    ModelUtils.addClassesToIndividual(workflowVariable, Constants.PROV_ENTITY, opmwModel);
                }
                // common for both variables and parameters
                workflowVariable.addLabel(var.getLocalName(), null);
                if (dim != null) {
                    workflowVariable.addLiteral(opmwModel.createProperty(Constants.OPMW_DATA_PROP_HAS_DIMENSIONALITY),
                            dim);
                }
                // link to abstract template (if any)
                if (derivedFrom != null) {
                    Resource abstractVariable = ModelUtils.getIndividualWithLabel(derivedFrom.getLocalName(),
                            abstractTemplateExport.getOpmwModel());
                    // System.out.println(concreteVatiable.getURI());
                    if (abstractVariable != null)
                        workflowVariable.addProperty(
                                opmwModel.createProperty(Constants.OPMW_PROP_IMPLEMENTS_TEMPLATE_ARTIFACT),
                                abstractVariable);
                }
            }
        }
        // next, we publish all steps and i/o links
        String querySteps = QueriesWorkflowTemplateExport.queryWINGSTemplateSteps();
        rs = ModelUtils.queryLocalRepository(querySteps, wingsTemplateModel);
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            Resource nodeID = qs.getResource("?n");
            Resource componentBinding = qs.getResource("?cb");
            Resource derivedFrom = qs.getResource("?derivedFrom");
            Literal isConcrete = qs.getLiteral("?isConcrete");
            Literal rule = qs.getLiteral("?rule");
            String templateProcessNS = this.exportUrl + Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS + "/";
            String templateProcessURI = templateProcessNS + wingsTemplate.getLocalName() + "_v" + versionNumber + "_"
                    + nodeID.getLocalName();
            // the URI must be the node id, because if a template has 2 nodes that are of
            // the same type, that is what it's used to disambiguate.
            Individual templateProcessInstance = opmwModel.createClass(Constants.OPMW_WORKFLOW_TEMPLATE_PROCESS)
                    .createIndividual(templateProcessURI);
            ModelUtils.addClassesToIndividual(templateProcessInstance, Constants.PROV_ENTITY, opmwModel);
            templateProcessInstance.addLabel(nodeID.getLocalName(), null);
            // retrieve the right type from the component catalog, and add it as the type as
            // well
            String componentBindingURI = componentBinding.getURI();
            opmwModel.createClass(componentCatalog.getCatalogTypeForComponentInstanceURI(componentBindingURI))
                    .createIndividual(templateProcessURI);

            // add that that step belongs to the template above.
            templateProcessInstance.addProperty(opmwModel.createProperty(Constants.OPMW_PROP_IS_STEP_OF_TEMPLATE),
                    wtInstance);
            // add the binding to the component of the catalog. We use the WINGS property
            // for this.
            Resource catalogResourceForComponentInstanceURI = componentCatalog
                    .getCatalogResourceForComponentInstanceURI(componentBindingURI);
            templateProcessInstance.addProperty(opmwModel.createProperty(Constants.WINGS_PROP_HAS_COMPONENT_BINDING),
                    catalogResourceForComponentInstanceURI);
            if (isConcrete != null) {
                templateProcessInstance.addLiteral(opmwModel.createProperty(Constants.OPMW_DATA_PROP_IS_CONCRETE),
                        isConcrete);
            }
            if (rule != null) {
                templateProcessInstance.addLiteral(opmwModel.createProperty(Constants.OPMW_DATA_PROP_HAS_RULE), rule);
            }
            if (derivedFrom != null) {
                Resource abstractProcess = ModelUtils.getIndividualWithLabel(derivedFrom.getLocalName(),
                        abstractTemplateExport.getOpmwModel());
                templateProcessInstance.addProperty(
                        opmwModel.createProperty(Constants.OPMW_PROP_IMPLEMENTS_TEMPLATE_PROCESS), abstractProcess);
            }
            // retrieve all the inputs for which this is a destination node (uses)
            String queryInputsAndRoles = QueriesWorkflowTemplateExport.queryWINGSInputsOfNode(nodeID.getURI());
            ResultSet rsAux = ModelUtils.queryLocalRepository(queryInputsAndRoles, wingsTemplateModel);
            while (rsAux.hasNext()) {
                QuerySolution qsAux = rsAux.next();
                Resource inVar = qsAux.getResource("?var");
                Literal role = qsAux.getLiteral("role");
                // System.out.println("INPUT of "+nodeID.getLocalName()+" is "+ inputVar);
                // Since we don't know whether it's a parameter variable or data variable, the
                // easiest way is to try and retrieve.
                String varNS, varURI;
                varNS = this.exportUrl + Constants.CONCEPT_DATA_VARIABLE + "/";
                varURI = varNS + wingsTemplate.getLocalName() + "_v" + versionNumber + "_" + inVar.getLocalName();
                Individual inputVariable = this.opmwModel.getIndividual(varURI);
                if (inputVariable == null) {
                    varNS = this.exportUrl + Constants.CONCEPT_PARAMETER_VARIABLE + "/";
                    varURI = varNS + wingsTemplate.getLocalName() + "_v" + versionNumber + "_" + inVar.getLocalName();
                    inputVariable = this.opmwModel.getIndividual(varURI);
                }
                templateProcessInstance.addProperty(opmwModel.createProperty(Constants.OPMW_PROP_USES), inputVariable);
                // Roles: This is needed because if an input is used in 2 different ways, it
                // needs to be explicit
                if (role != null) {
                    String roleSpecialization = componentCatalog.addRoleProperty(role.getString(),
                            opmwModel.createOntProperty(Constants.OPMW_PROP_USES));
                    templateProcessInstance.addProperty(opmwModel.createProperty(roleSpecialization), inputVariable);
                }
            }
            // retrieve all the outputs for which this is an origin node (isGeneratedBy)
            String queryOutputsAndRoles = QueriesWorkflowTemplateExport.queryWINGSOutputsOfNode(nodeID.getURI());
            rsAux = ModelUtils.queryLocalRepository(queryOutputsAndRoles, wingsTemplateModel);
            while (rsAux.hasNext()) {
                QuerySolution qsAux = rsAux.next();
                Resource outVar = qsAux.getResource("?var");
                Literal role = qsAux.getLiteral("role");
                // System.out.println("OUTPUT of "+nodeID.getLocalName()+" is "+ inputVar);
                String varNS = this.exportUrl + Constants.CONCEPT_DATA_VARIABLE + "/";
                String varURI = varNS + wingsTemplate.getLocalName() + "_v" + versionNumber + "_"
                        + outVar.getLocalName();
                Individual outputVar = this.opmwModel.getIndividual(varURI);
                ModelUtils.addClassesToIndividual(outputVar, Constants.PROV_ENTITY, opmwModel);
                outputVar.addProperty(opmwModel.createProperty(Constants.OPMW_PROP_IGB), templateProcessInstance);
                if (role != null) {
                    String roleSpecialization = componentCatalog.addRoleProperty(role.getString(),
                            opmwModel.createOntProperty(Constants.OPMW_PROP_IGB));
                    outputVar.addProperty(opmwModel.createProperty(roleSpecialization), templateProcessInstance);
                }
            }
        }
        // if it has derivation, do a construct query with inferences for expanded
        // template types

        return wt;
    }

    /**
     * Function that exports the transformed template in OPMW. This function should
     * be called after
     * "transform". If not, it will call transform() automatically.
     *
     * @param filepath      path of the file where to write the template
     * @param serialization serialization of choice: RDF/XML, TTL, etc.
     * @return
     * @throws IOException
     */
    public String exportAsOPMW(String filepath, Lang serialization) throws IOException {
        if (transformedTemplate == null) {
            this.transform();
        }
        if (!isTemplatePublished) {
            // opmwModel.write(System.out, "TTL");
            if (this.abstractTemplateExport != null) {
                abstractTemplateExport.exportAsOPMW(filepath, serialization);
            }
            ModelUtils.exportRDFFile(filepath, opmwModel, serialization);
            File file = new File(filepath);
            this.triplesPublisher.setGraphURI(this.transformedTemplate.getURI());
            this.triplesPublisher.publish(file);
        }
        return transformedTemplate.getURI();
    }

    /**
     * Function that exports the transformed template in P-Plan format. This
     * function should be called after
     * "transform". If not, it will call transform() automatically.
     *
     * @param outFileDirectory path where to write the serialized model
     * @param serialization    serialization of choice: RDF/XML, TTL, etc.
     * @return URL of the template
     */
    public String exportAsPPlan(String outFileDirectory, String serialization) {
        // TO DO
        System.out.println("Not done yet!");
        return null;
    }

    /**
     * Function that exports the transformed template in OPMW and P-Plan (in
     * different files)
     *
     * @param outFileDirectory path where to write the serialized model
     * @param serialization    serialization of choice: RDF/XML, TTL, etc.
     * @return URI of the template
     * @throws IOException
     */
    public String exportAll(String outFileDirectory, Lang serialization) throws IOException {
        // TO DO
        System.out.println("Not done yet!");
        // this.export_as_OPMW(outFilePath, serialization);
        // this.export_as_PPlan(outFilePath, serialization);
        return exportAsOPMW(outFileDirectory, serialization);
    }

    public boolean isTemplatePublished() {
        return isTemplatePublished;
    }

    public WorkflowTemplateExport getAbstractTemplateExport() {
        return abstractTemplateExport;
    }

}

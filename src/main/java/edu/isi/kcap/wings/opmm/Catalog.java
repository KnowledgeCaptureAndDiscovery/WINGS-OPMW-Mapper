package edu.isi.kcap.wings.opmm;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.ResourceImpl;

/**
 * Class designed to deal with the versioning of new catalog entities.
 * The catalog should read an existing OWL file with the existing catalog (if
 * exists)
 * and then create new entries as needed according to the component taxonomy.
 * URI Scheme:
 * Classes :
 * https://w3id.org/wings/EXPORT_NAME/DOMAIN_NAME/Component#COMPONENT_NAME_VERSION
 * (each class has one canonical instance).
 * Canonical instances are defined in the component catalog.
 * Data :
 * https://w3id.org/wings/EXPORT_NAME/DOMAIN_NAME/Data#DATA_NAME
 * Versions of the data catalog (classes) are not tracked
 *
 * EXPORT_NAME indicates the name of the dataset. For example, if we have
 * different
 * versions of WINGS exporting their own vocabularies, the EXPORT_NAME should
 * differ.
 *
 * DOMAIN_NAME refers to the domain name
 *
 * @author Daniel Garijo
 */
public class Catalog {
    /**
     * Domain name needed track the components of a workflow. Each workflow belongs
     * to a concrete domain. The domain is final because every time you deal with a
     * workflow, you need to create a new instance of the catalog, loading the
     * appropriate domain
     */
    private final String domain;
    /**
     * Name of the dataset you are exporting. This name is added for consistency
     * in case different WINGS instances export the same domain.
     */
    private final String exportName;
    /**
     * Catalog model.
     */
    private final OntModel localCatalog;
    /**
     * URL of the local repository, where previous changes may have been saved.
     * The URL is expected to be a folder. In the folder, a file with each domain
     * will represent the different domains.
     */
    private final String defaultRepositoryPath;
    /**
     * Pointer to the taxonomy used by WINGS on the current workflow.
     */
    private final OntModel WINGSDomainTaxonomy;

    /**
     * URL for the components of the catalog
     */
    private final String componentCatalogURI;

    /**
     * URL for the canonical instances of the catalog (inputs, outputs, components,
     * etc.)
     */
    private final String instanceCatalogURI;

    /**
     * URL for the data catalog (only pointers to it)
     */
    private final String dataCatalogURI;

    /**
     * URL for the OPMW extensions (is generated by, uses_as, wgb_as, used_as)
     */
    private final String extensionsURI;

    public String getDomainGraphURI() {
        return domainGraphURI;
    }

    private final String domainGraphURI;

    /**
     * Default constructor. Assumption: domains are saved in Turtle.
     *
     * @param domain
     * @param exportName
     * @param defaultRepositoryFolder if does not exist, a new one will be created.
     *                                A repository folder may contain multiple
     *                                domains.
     * @param WINGStaxonomyURL
     */
    public Catalog(String domain, String exportName, String defaultRepositoryFolder,
            String WINGStaxonomyURL) {
        this.domain = domain;
        this.exportName = exportName;
        this.defaultRepositoryPath = defaultRepositoryFolder;
        this.domainGraphURI = Constants.CATALOG_URI + this.exportName + "/Domain/" + domain;

        this.componentCatalogURI = Constants.CATALOG_URI + this.exportName + "/" + domain + "/" + "Component#";
        this.dataCatalogURI = Constants.CATALOG_URI + this.exportName + "/" + domain + "/" + "Data#";
        this.instanceCatalogURI = Constants.CATALOG_URI + this.exportName + "/" + domain + "/"
                + "resource/CanonicalInstance/";
        this.extensionsURI = Constants.CATALOG_URI + this.exportName + "/" + domain + "/" + "extension#";
        // if repo is not null, create it
        File f = new File(defaultRepositoryFolder);
        if (!f.exists()) {
            f.mkdirs();
        }
        // attempt to load existing domain. If not, create it.
        File domainF = new File(defaultRepositoryFolder + File.separator + domain);
        if (domainF.exists()) {
            this.localCatalog = ModelUtils.loadModel(domainF.getPath());
        } else {
            this.localCatalog = ModelFactory.createOntologyModel();
        }
        // load WINGS taxonomy from provided URL
        this.WINGSDomainTaxonomy = ModelUtils.loadModel(WINGStaxonomyURL);
    }

    public OntModel getWINGSDomainTaxonomy() {
        return WINGSDomainTaxonomy;
    }

    /**
     * Method that gets the catalog type for a given component type in WINGS.
     *
     * @param wingsComponentType The WINGS entity type that we aim to align in the
     *                           catalog (URI)
     *                           Example:
     *                           http://www.wings-workflows.org/wings-omics-portal/export/users/alyssa/DataAbstractions/components/library.owl#IndelCallerClass
     * @return
     */
    public String getCatalogTypeForComponentURI(String wingsComponentType) {
        OntClass wingsClass = this.WINGSDomainTaxonomy.getOntClass(wingsComponentType);
        if (wingsClass == null) {
            System.out.println("The requested component does not exist in the library file provided!");
            return null;
        }
        // get entity's local name
        Resource r = new ResourceImpl(wingsComponentType);
        String entityLabel = r.getLocalName();
        // check if there is an entity in the local catalog with the same label
        ResultSet rs = ModelUtils.queryLocalRepository(QueriesCatalog.getEntityFromLabel(entityLabel), localCatalog);
        if (!rs.hasNext()) {
            // the component does not exist: copy it and copy its parent classes (if they
            // don't exist) and return.
            return copyComponentToCatalog(wingsComponentType, entityLabel, 1);// doesn't exist, first version
        } else {
            QuerySolution qs = rs.next();
            String foundCatalogComponentURI = qs.getResource("?e").getURI();
            // The component exist. But does this version exist?
            // calculate md5 for given component from WINGS taxonomy catalog.
            Iterator libInstances = wingsClass.listInstances(true);// true means only instances of class
            if (libInstances.hasNext()) {
                // some abstract components may be empty in terms of IO, and hence do not
                // generate instances.
                Individual libCanonicalInstance = (Individual) libInstances.next();
                String wingsComponentMD5 = HashUtils.createMD5ForWINGSComponent(libCanonicalInstance);
                if (libInstances.hasNext()) {
                    System.err.println("WARNING: Component " + wingsComponentType
                            + " has at least 2 canonical instances in the component catalog");
                }
                // is there any component in our local catalog with that md5?
                ResultSet rsComp = ModelUtils
                        .queryLocalRepository(QueriesCatalog.getComponentsWithMD5(wingsComponentMD5), localCatalog);
                if (rsComp.hasNext()) {
                    foundCatalogComponentURI = rsComp.next().getResource("?c").getURI();
                    System.out.println("Found an entry in local catalog that is equal to WINGS library catalog: "
                            + foundCatalogComponentURI);

                    /**
                     * Missing to do: add the location of the md5 of the code. Check if it exists
                     * (locations have URI+version) and if it's a new one. The
                     * FileManager/LocationManager component should deal with that
                     */

                    // we found a canonical instance; return the actual component
                    return localCatalog.getIndividual(foundCatalogComponentURI).getOntClass(true).getURI();
                } else {
                    String componentName = libCanonicalInstance.getLocalName();
                    // if it doesn't exist, then create a new version of the component. First
                    // retrieve the latest version number
                    ResultSet latestVersionCI = ModelUtils.queryLocalRepository(
                            QueriesCatalog.getMostRecentVersionNumber(componentName), localCatalog);
                    if (latestVersionCI.hasNext()) {
                        QuerySolution latestVersionQS = latestVersionCI.next();
                        int versionNumber = latestVersionQS.getLiteral("?number").getInt();
                        System.out.println("Latest version for " + wingsComponentType + " is " + versionNumber);
                        String newLocalComponent = copyComponentToCatalog(wingsComponentType, componentName,
                                versionNumber + 1);
                        // link the versions of the canonical instances: newCI wasRevisionOf
                        // latestVersionCI
                        Individual newCI = (Individual) localCatalog.getOntClass(newLocalComponent).listInstances()
                                .next();
                        newCI.addProperty(localCatalog.createOntProperty(Constants.PROV_WAS_REVISION_OF),
                                localCatalog.getIndividual(latestVersionQS.getResource("?ci").getURI()));
                        return newLocalComponent;
                    }
                }
            } else {
                System.out.println("Component " + wingsComponentType
                        + " does not have a canonical instance, and has been ignored.");
            }
            return foundCatalogComponentURI;
        }
    }

    /**
     * Given a WINGS data catalog type, this method returns the corresponding type
     * in the export.
     * example of input:
     * http://www.wings-workflows.org/wings-omics-portal/export/users/ravali/genomics/data/ontology.owl#OmicsFile
     * example of what should be returned:
     * https://w3id.org/wings/[exportName]/[domain]/Data#OmicsFile
     *
     * @param wingsDataClass class type to be adapted.
     * @return class type in the local catalog.
     */
    public String getCatalogTypeForDataClassURI(Resource wingsDataClass) {
        return dataCatalogURI + wingsDataClass.getLocalName();
    }

    /**
     * Method that given a WINGS canonical instance, it will query the local catalog
     * for its type.
     * This method is added because in workflow templates, components are bound to
     * canonical instances.
     *
     * @param canonicalInstanceURI instance of a component in the WINGS component
     *                             library
     * @return
     */
    public String getCatalogTypeForComponentInstanceURI(String canonicalInstanceURI) {
        // each canonical instance has 1 direct class

        Individual individual = this.WINGSDomainTaxonomy.getIndividual(canonicalInstanceURI);
        if (individual == null) {
            // TODO: This is a hack to make it work with the test cases. Fix it. The reason
            // is that wings has a misconfiguration on the uri of the template
            canonicalInstanceURI = canonicalInstanceURI.replace("http://localhost:8080", "https://wings.disk.isi.edu");
            individual = this.WINGSDomainTaxonomy.getIndividual(canonicalInstanceURI);
        }
        if (individual == null) {
            throw new RuntimeException("The requested component does not exist in the library file provided!");
        }
        String uri = individual.getOntClass(true).getURI();
        String compType = this.getCatalogTypeForComponentURI(uri);
        if (compType == null) {
            System.out.println("The requested component does not exist in the library file provided!");
            return null;
        }
        // We return the type.
        return compType;
    }

    /**
     * Method that given a WINGS canonical instance, it will query the local catalog
     * for its corresponding instance.
     *
     * @param wingsComponentCanonicalInstanceURI of the component we aim to find the
     *                                           equivalent for
     * @return
     */
    public String getCatalogURIForComponentInstanceURI(String wingsComponentCanonicalInstanceURI) {
        String catalogClass = getCatalogTypeForComponentInstanceURI(wingsComponentCanonicalInstanceURI);
        return this.localCatalog.getOntClass(catalogClass).listInstances().next().getURI();
    }

    /**
     * Method that given a WINGS canonical instance, it will query the local catalog
     * for its corresponding instance.
     *
     * @param wingsComponentCanonicalInstanceURI of the component we aim to find the
     *                                           equivalent for
     * @return a resource representing the instance in the catalog
     */
    public Resource getCatalogResourceForComponentInstanceURI(String wingsComponentCanonicalInstanceURI) {
        String catalogClass = getCatalogTypeForComponentInstanceURI(wingsComponentCanonicalInstanceURI);
        return this.localCatalog.getOntClass(catalogClass).listInstances().next();
    }

    /**
     * Copies a component to the new catalog and returns the new class name
     *
     * @param wingsComponentType
     * @param componentName:     Name of the component to add
     * @param version
     * @return the new catalog class URI for the component
     */
    private String copyComponentToCatalog(String wingsComponentType, String componentName, int version) {
        String catalogClassURI = componentCatalogURI + componentName + "_V" + version;
        OntClass newCatalogComponent = localCatalog.createClass(catalogClassURI);
        newCatalogComponent.addLabel(componentName, null);// language not specified
        newCatalogComponent.addProperty(localCatalog.createOntProperty(Constants.OWL_VERSION_INFO), "" + version);
        // copy class
        ResultSet rs = ModelUtils.queryLocalRepository(QueriesCatalog.getClassAssertions(wingsComponentType),
                WINGSDomainTaxonomy);
        // debug
        System.out.println("Copying component " + wingsComponentType + " as " + catalogClassURI);
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            Resource property = qs.getResource("?p");
            if (qs != null && qs.getResource("?o").isURIResource() && wingsComponentType != null) {
                Resource object = qs.getResource("?o");
                try {
                    if (Constants.RDFS_SUBCLASS_OF.equals(property.getURI())
                            && !object.getURI().equals(wingsComponentType)) {// we avoid asserting subclass of
                                                                             // themselves.
                        // if we reach the top level (COMPONENT), then stop
                        String superclass;
                        if (object.getURI().equals(Constants.WINGS_COMPONENT) ||
                                object.getURI().equals("http://www.w3.org/2000/01/rdf-schema#Resource")) {
                            superclass = Constants.WINGS_COMPONENT;
                        } else {
                            superclass = getCatalogTypeForComponentURI(object.getURI());
                        }
                        // add the subclass relationship
                        newCatalogComponent.addProperty(localCatalog.createOntProperty(property.getURI()),
                                localCatalog.createClass(superclass));
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Exception: " + e.getMessage());
                }
            }
            // if it's a subclass of something from the catalog, add that class.
            else {
                // if there are annotations, we copy them too.
                if (qs.contains("?o") && qs.get("?o").isLiteral()) {
                    Literal object = qs.getLiteral("?o");
                    newCatalogComponent.addProperty(localCatalog.createOntProperty(property.getURI()),
                            object.getString(),
                            object.getDatatype());
                }
            }
        }

        // create canonical instance
        Iterator cInstances = WINGSDomainTaxonomy.getOntClass(wingsComponentType).listInstances(true);
        String wingsCanonicalInstanceURI = null;
        String wingsCanonicalInstanceName = null;
        if (cInstances.hasNext()) {
            Individual aux = (Individual) cInstances.next();
            wingsCanonicalInstanceURI = aux.getURI();
            wingsCanonicalInstanceName = aux.getLocalName();
        }
        String catalogCanonicalInstanceURI;
        Individual catalogCanonicalInstance;
        // we add a canonical instance with no inputs outputs and empty MD5.
        if (wingsCanonicalInstanceURI == null || wingsCanonicalInstanceName == null) {
            // If no canonical instance exists, it means the abstract component is empty.
            catalogCanonicalInstanceURI = this.instanceCatalogURI + componentName + "_V" + version;
            catalogCanonicalInstance = newCatalogComponent.createIndividual(catalogCanonicalInstanceURI);
            catalogCanonicalInstance.addLabel(componentName, null);
            catalogCanonicalInstance.addProperty(localCatalog.createOntProperty(Constants.OWL_VERSION_INFO),
                    "" + version);
            catalogCanonicalInstance.addProperty(localCatalog.createOntProperty(Constants.OPMW_DATA_PROP_HAS_MD5),
                    HashUtils.MD5(componentName));
        } else {
            // assumption: if a class is new, the canonical instance for that class did not
            // exist.
            catalogCanonicalInstanceURI = this.instanceCatalogURI + wingsCanonicalInstanceName + "_V" + version;
            // create an instance of newCatalogComponent
            catalogCanonicalInstance = newCatalogComponent.createIndividual(catalogCanonicalInstanceURI);
            catalogCanonicalInstance.addLabel(wingsCanonicalInstanceName, null);
            catalogCanonicalInstance.addProperty(localCatalog.createOntProperty(Constants.OWL_VERSION_INFO),
                    "" + version);
            // copy inputs, outputs and hw specs. Canonical instances have the same version
            // as it class.
            rs = ModelUtils.queryLocalRepository(QueriesCatalog.getInstanceAssertions(wingsCanonicalInstanceURI),
                    WINGSDomainTaxonomy);
            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                Resource property = qs.getResource("?p");
                try {
                    Resource object = qs.getResource("?o");
                    Individual catalogObj;
                    if (object.isAnon()) {
                        switch (property.getURI()) {
                            case Constants.WINGS_PROP_HAS_SOFTWARE_DEPENDENCY:
                                catalogObj = copyObjectToCatalogAs(object,
                                        this.instanceCatalogURI + "SWDEP-" + catalogCanonicalInstance.getLocalName());
                                break;
                            case Constants.WINGS_PROP_HAS_HARDWARE_DEPENDENCY:
                                catalogObj = copyObjectToCatalogAs(object,
                                        this.instanceCatalogURI + "HWDEP-" + catalogCanonicalInstance.getLocalName());
                                break;
                            default:// no special name for the node. Possible problem if there are repeated bnodes
                                catalogObj = copyObjectToCatalogAs(object, this.instanceCatalogURI + "A-"
                                        + new Date().getTime() + catalogCanonicalInstance.getLocalName());
                                break;
                        }

                    } else {
                        catalogObj = copyObjectToCatalog(object, version);
                    }
                    catalogCanonicalInstance.addProperty(localCatalog.createOntProperty(property.getURI()), catalogObj);
                } catch (Exception e) {
                    try {
                        // copy ?p ?o to catalog
                        Literal object = qs.getLiteral("?o");
                        catalogCanonicalInstance.addProperty(localCatalog.createOntProperty(property.getURI()), object);
                    } catch (Exception e2) {
                        System.err.println(e2);
                    }
                }
            }
            System.out.println("Created canonical instance " + catalogCanonicalInstance.getURI());
            // add MD5 to component (calculated from local catalog)
            // We calculate MD5 from WINGS components because otherwise it will take local
            // renames (e.g. adding _V1)
            String componentMD5 = HashUtils
                    .createMD5ForWINGSComponent(this.WINGSDomainTaxonomy.getIndividual(wingsCanonicalInstanceURI));
            if (componentMD5 != null) {
                catalogCanonicalInstance.addProperty(localCatalog.createOntProperty(Constants.OPMW_DATA_PROP_HAS_MD5),
                        componentMD5);
            }
        }
        return catalogClassURI;
    }

    /**
     * Method designed to copy an object to the catalog. If the object is of certain
     * type
     * (like an input, etc.) its properties will be copied recursively. If it is an
     * anon
     * node, the node will be made non-anon and its properties copied as well.
     * Note that when copying objects, new URIs will be issued for canonical
     * instances.
     *
     * @param objectToCopy the object to be copied.
     * @param version      version of the component you are copying. Important for
     *                     consistency with the component
     * @return The URI of the copied object in the catalog
     */
    private Individual copyObjectToCatalog(Resource objectToCopy, int version) {
        return copyObjectToCatalogAs(objectToCopy,
                this.instanceCatalogURI + objectToCopy.getLocalName() + "V_" + version);
    }

    /**
     * Method created specifically for naming copied nodes. Useful when dealing with
     * anon nodes.
     *
     * @param objectToCopy
     * @param uriForCopiedResource
     * @return
     */
    private Individual copyObjectToCatalogAs(Resource objectToCopy, String uriForCopiedResource) {
        Resource type;
        Individual catalogObj;
        OntClass c;
        type = objectToCopy.getPropertyResourceValue(localCatalog.getOntProperty(Constants.RDF_TYPE));
        // change type to the right URI.
        c = localCatalog.createClass(getCatalogTypeForObject(type));
        catalogObj = c.createIndividual(uriForCopiedResource);
        StmtIterator it = objectToCopy.listProperties();
        while (it.hasNext()) {
            Statement s = it.next();
            switch (s.getPredicate().getURI()) {
                case Constants.RDF_TYPE:
                    catalogObj.addProperty(s.getPredicate(),
                            localCatalog.createClass(getCatalogTypeForObject(s.getObject().asResource())));
                    break;
                case Constants.WINGS_DATA_PROP_HAS_ARGUMENT_ID:
                    catalogObj.addProperty(s.getPredicate(), s.getObject());
                    catalogObj.addProperty(localCatalog.createObjectProperty(Constants.RDFS_LABEL), s.getObject());
                    break;
                case Constants.WINGS_PROP_COMP_HAS_LOCATION:
                    // here add the code to handle copying of resources to where the export is.
                    // how it should work: This is a link to a version of the code.
                    // Location should be an object with the version number, pointer to previous
                    // version (if any) and the path to the file

                    // TO DO. Should use URI: w3id.org/wings/data/exportName/. The /data (or /code)
                    // is before so we can redirect it to whatever server

                    // do not break at the moment: do default behavior after this
                default:
                    catalogObj.addProperty(s.getPredicate(), s.getObject());
                    break;
            }
        }
        return catalogObj;
    }

    /**
     * Given a type, this method will adapt it to the types used in the catalog in a
     * consistent manner
     *
     * @param wingsObjType
     * @return
     */
    private String getCatalogTypeForObject(Resource wingsObjType) {
        // if it's a component or similar, we keep it
        String type = wingsObjType.getURI();
        String name = wingsObjType.getLocalName();
        if (type.contains("http://www.wings-workflows.org/ontology/") ||
                type.contains("http://www.w3.org/2000/01/rdf-schema#")) {
            return type;
        } else if (type.contains("/data/ontology.owl")) {
            // belongs to the data ontology.
            return this.dataCatalogURI + name;
        }
        // return a generic component/poiter to the catalog
        return Constants.CATALOG_URI + this.exportName + "/" + this.domain + "/" + name;
    }

    /**
     * Probably I will need a getEntry for those files and softwares with a hash.
     */

    /**
     * Method designed to export the semantic catalog (ontology) on the given
     * defaultRepository path.
     * The catalog will be saved under its domain.
     *
     * @param directory FOLDER path where to export the catalog
     * @throws IOException
     */
    public String exportCatalog(String directory, String serialization) throws IOException {
        String exportPath;
        // create directory if it does not exist
        File dir;
        if (directory == null || directory.isEmpty()) {
            dir = new File(this.defaultRepositoryPath);
        } else {

            dir = new File(directory);
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        exportPath = dir + File.separator + this.domain;
        ModelUtils.exportRDFFile(exportPath, localCatalog, serialization);
        return exportPath;
    }

    /**
     * Given a role id and a property, this function does:
     * 1) creates the propertyURI in the catalog
     * 2) creates a new subproperty of 1) with the URI "propertyURI_as"+roleID
     * 3) returns the id of the new created subproperty
     *
     * @param roleID   the ID of the role you want to add. For example "fileToSort"
     * @param property the URI of the property that you want to specialize. E.g.,
     *                 opmw:uses
     * @return the URI of the new property. For example,
     *         https://w3id.org/wings/export/genomics/uses_asFileToSort
     */
    public String addRoleProperty(String roleID, OntProperty property) {
        OntProperty p = this.localCatalog.createOntProperty(property.getURI());
        OntProperty newP = this.localCatalog
                .createOntProperty(this.extensionsURI + property.getLocalName() + "_as_" + roleID);
        newP.addLabel(property.getLocalName() + " as " + roleID, null);
        p.addSubProperty(newP);
        return newP.getURI();
    }

    // ----------------------
    // local tests (these will be translated to unit tests)
    public static void main(String[] args) throws IOException {
        // should load a local domain for tests.
        // http://www.wings-workflows.org/wings-omics-portal/export/users/ravali/genomics/components/library.owl
        String taxonomyURL = "http://www.wings-workflows.org/wings-omics-portal/export/users/alyssa/DataAbstractions/components/library.owl#";
        Catalog c = new Catalog("genomics", "testExport", "domains", taxonomyURL);
        System.out.println(
                "Component for http://www.wings-workflows.org/wings-omics-portal/export/users/alyssa/DataAbstractions/components/library.owl#SNPcaller-PolyphredClass "
                        +
                        " is " + c.getCatalogTypeForComponentURI(
                                "http://www.wings-workflows.org/wings-omics-portal/export/users/alyssa/DataAbstractions/components/library.owl#SNPcaller-PolyphredClass"));

        System.out.println(
                "Local component type for instance http://www.wings-workflows.org/wings-omics-portal/export/users/alyssa/DataAbstractions/components/library.owl#SNPcaller-Polyphred "
                        +
                        " is " + c.getCatalogTypeForComponentInstanceURI(
                                "http://www.wings-workflows.org/wings-omics-portal/export/users/alyssa/DataAbstractions/components/library.owl#SNPcaller-Polyphred"));

        c.exportCatalog(null, "RDF/XML");
        System.out.println("Catalog exported");

        // note: local tests have been performed by modifying the catalog manually.
        // Should save different versions and implement tests.
    }

    /**
     * TESTS: all these have to be implemented to test everything properly
     * Test with collections (but this is for templates)
     * Test: Compare hash for 2 components that I know are equal. Compare 2 of
     * components that I know are different.
     * Test: load a component. Do versioning on one component.
     * Test if the versioning of the code is done correctly
     * Test if a 2 executions use the same file, the hash id used has to be the
     * same.
     * Tests: LOAD a domain.
     * There should not be canonical instances that do not belong to a component.
     * Since we only publish workflows, there should not be components without
     * canonical instances that are concrete.
     * Republish a template (no changes to domain)
     * Publish a template that is different but only creates new components, no
     * modifications.
     * modify an existing domain and extend it appropriately: component with new
     * inputs
     * new subclass of component.
     *
     */

}
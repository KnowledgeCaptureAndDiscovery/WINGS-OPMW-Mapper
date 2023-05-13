/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import edu.isi.wings.opmm.Constants;
import static edu.isi.wings.opmm.Mapper.NEW_TAXONOMY_CLASS;
import edu.isi.wings.opmm.Queries;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Resource;

/**
 *
 * @author dgarijo
 */
public class ModelUtils {
    /**
     * Funtion to insert an individual as an instance of a class. If the class does
     * not exist, it is created.
     *
     * @param individualId Instance id. If exists it won't be created.
     * @param classURL     URL of the class from which we want to create the
     *                     instance
     */
    public static void addIndividual(OntModel m, String individualId, String classURL, String label) {
        String nameOfIndividualEnc = EncodingUtils.encode(getClassName(classURL) + "/" + individualId);
        OntClass c = m.createClass(classURL);
        c.createIndividual(Constants.PREFIX_EXPORT_RESOURCE + nameOfIndividualEnc);
        if (label != null) {
            addDataProperty(m, nameOfIndividualEnc, label, Constants.RDFS_LABEL);
        }
    }

    public static void addIndividualAbstract2(OntModel m, String encodepart) {
        String nameOfIndividualEnc2 = EncodingUtils.encode(encodepart);
        OntClass c2 = m.createClass(Constants.PREFIX_OWL + "Class");
        c2.createIndividual(NEW_TAXONOMY_CLASS + nameOfIndividualEnc2);
    }

    public static void addIndividualConcrete2(OntModel m, String encodepart) {
        String nameOfIndividualEnc = EncodingUtils.encode(encodepart);
        OntClass c = m.createClass(Constants.PREFIX_OWL + "Class");
        c.createIndividual(NEW_TAXONOMY_CLASS + nameOfIndividualEnc.toUpperCase());
    }

    public static void addIndividualAbstractSubclass2(OntModel m, String encodepart) {
        OntProperty propSelec2 = m.createOntProperty(Constants.PREFIX_RDFS + "subClassOf");
        Resource source2 = m
                .getResource(NEW_TAXONOMY_CLASS.replace("#", "/") + "Component#" + EncodingUtils.encode(encodepart));
        Individual instance2 = (Individual) source2.as(Individual.class);
        if (("Component").contains("http://")) {// it is a URI
            instance2.addProperty(propSelec2, NEW_TAXONOMY_CLASS + "Component");
        } else {// it is a local resource
            instance2.addProperty(propSelec2, m.getResource(NEW_TAXONOMY_CLASS + EncodingUtils.encode("Component")));
        }
    }

    public static void addIndividualConcreteSubclass2(OntModel m, String encodepart, String containspart) {
        OntProperty propSelec1 = m.createOntProperty(Constants.PREFIX_RDFS + "subClassOf");
        Resource source1 = m.getResource(NEW_TAXONOMY_CLASS.replace("#", "/") + "Component#" + encodepart);
        Individual instance1 = (Individual) source1.as(Individual.class);
        if ((containspart).contains("http://")) {// it is a URI
            instance1.addProperty(propSelec1, NEW_TAXONOMY_CLASS + containspart);
        } else {// it is a local resource
            instance1.addProperty(propSelec1, m.getResource(NEW_TAXONOMY_CLASS + EncodingUtils.encode(containspart)));
        }
    }

    /**
     * Function to add a property between two individuals. If the property does not
     * exist, it is created.
     *
     * @param orig     Domain of the property (Id, not complete URI)
     * @param dest     Range of the property (Id, not complete URI)
     * @param property URI of the property
     */
    public static void addProperty(OntModel m, String orig, String dest, String property) {
        OntProperty propSelec = m.createOntProperty(property);
        Resource source = m.getResource(Constants.PREFIX_EXPORT_RESOURCE + EncodingUtils.encode(orig));
        Individual instance = (Individual) source.as(Individual.class);
        if (dest.contains("http://")) {// it is a URI
            instance.addProperty(propSelec, dest);
        } else {// it is a local resource
            instance.addProperty(propSelec,
                    m.getResource(Constants.PREFIX_EXPORT_RESOURCE + EncodingUtils.encode(dest)));
        }
        // System.out.println("Creada propiedad "+ propiedad+" que relaciona los
        // individuos "+ origen + " y "+ destino);
    }

    /**
     * Function to add dataProperties. Similar to addProperty
     *
     * @param origen       Domain of the property (Id, not complete URI)
     * @param literal      literal to be asserted
     * @param dataProperty URI of the data property to assign.
     */
    public static void addDataProperty(OntModel m, String origen, String literal, String dataProperty) {
        OntProperty propSelec;
        // lat y long son de otra ontologia, tienen otro prefijo distinto
        propSelec = m.createDatatypeProperty(dataProperty);
        // propSelec =
        // (modeloOntologia.getResource(dataProperty)).as(OntProperty.class);
        Resource orig = m.getResource(Constants.PREFIX_EXPORT_RESOURCE + EncodingUtils.encode(origen));
        m.add(orig, propSelec, literal);
    }

    /**
     * Function to add dataProperties. Similar to addProperty
     *
     * @param m            Model of the propery to be added
     * @param origen       Domain of the property
     * @param dato         literal to be asserted
     * @param dataProperty URI of the dataproperty to assert
     * @param tipo         type of the literal (String, int, double, etc.).
     */
    public static void addDataProperty(OntModel m, String origen, String dato, String dataProperty, RDFDatatype tipo) {
        OntProperty propSelec;
        // lat y long son de otra ontologia, tienen otro prefijo distinto
        propSelec = m.createDatatypeProperty(dataProperty);
        Resource orig = m.getResource(Constants.PREFIX_EXPORT_RESOURCE + EncodingUtils.encode(origen));
        m.add(orig, propSelec, dato, tipo);
    }

    /**
     * Function to add a property as a subproperty of the other.
     *
     * @param uriProp
     * @param uriSubProp
     */
    public static void createSubProperty(OntModel m, String uriProp, String uriSubProp) {
        if (uriProp.equals(uriSubProp))
            return;
        OntProperty propUsed = m.getOntProperty(uriProp);
        OntProperty propRole = m.getOntProperty(uriSubProp);
        propUsed.addSubProperty(propRole);
    }

    /**
     * Method to retrieve the name of a URI class object
     *
     * @param classAndVoc URI from which retrieve the name
     * @return
     */
    public static String getClassName(String classAndVoc) {
        if (classAndVoc.contains(Constants.PREFIX_DCTERMS))
            return classAndVoc.replace(Constants.PREFIX_DCTERMS, "");
        else if (classAndVoc.contains(Constants.PREFIX_FOAF))
            return classAndVoc.replace(Constants.PREFIX_FOAF, "");
        else if (classAndVoc.contains(Constants.PREFIX_OPMO))
            return classAndVoc.replace(Constants.PREFIX_OPMO, "");
        else if (classAndVoc.contains(Constants.PREFIX_OPMV))
            return classAndVoc.replace(Constants.PREFIX_OPMV, "");
        else if (classAndVoc.contains(Constants.PREFIX_RDFS))
            return classAndVoc.replace(Constants.PREFIX_RDFS, "");
        else if (classAndVoc.contains(Constants.PREFIX_OPMW))
            return classAndVoc.replace(Constants.PREFIX_OPMW, "");
        // else if(classAndVoc.contains(ACDOM))return classAndVoc.replace(ACDOM,"");
        // else if(classAndVoc.contains(DCDOM))return classAndVoc.replace(DCDOM,"");
        else
            return null;
    }

    /**
     * Function to determine whether a run has already been published or not.
     * If the run has been published, it should not be republished again.
     *
     * @param endpointURL URL of the repository where we store the runs
     * @param runURL      URL of the physical file of containing the run.
     * @return True if the run has been published. False in other case.
     */
    public static boolean isRunPublished(String endpointURL, String runURL) {
        String query = Queries.queryIsTheRunAlreadyPublished(runURL);
        QueryExecution qe = QueryExecutionFactory.sparqlService(endpointURL, query);
        ResultSet rs = qe.execSelect();
        return rs.hasNext();
    }

    /**
     * Query a local repository, specified in the second argument
     *
     * @param queryIn    sparql query to be performed
     * @param repository repository on which the query will be performed
     * @return
     */
    public static ResultSet queryLocalRepository(String queryIn, OntModel repository) {
        Query query = QueryFactory.create(queryIn);
        // Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create(query, repository);
        ResultSet rs = qe.execSelect();
        // qe.close();
        return rs;
    }

    /**
     * Function to export the stored model as an RDF file, using ttl syntax
     *
     * @param outFile name and path of the outFile must be created.
     */
    public static void exportRDFFile(String outFile, OntModel model, String mode) throws IOException {
        OutputStream out;
        out = new FileOutputStream(outFile);
        model.write(out, mode);
        // model.write(out,"RDF/XML");
        out.close();
    }

}

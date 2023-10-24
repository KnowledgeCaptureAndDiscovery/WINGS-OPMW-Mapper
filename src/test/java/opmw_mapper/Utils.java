package opmw_mapper;

import java.util.*;

import javax.xml.transform.Source;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Assert;
import org.w3c.dom.Node;
import org.xmlunit.builder.Input;
import org.xmlunit.xpath.JAXPXPathEngine;
import org.xmlunit.xpath.XPathEngine;

/**
 * Should assess if this class overlaps with the current utils class.
 *
 * @author dgarijo
 */
public class Utils {
    // given a directory, loads all the files in a local model.
    public static OntModel loadDirectory(String dirPath) {
        OntModel model = ModelFactory.createOntologyModel();
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            for (File currf : dir.listFiles()) {
                loadFileInModel(model, currf);
            }
            return model;
        }
        System.err.println("The path " + dirPath + " is not a directory");
        return null;

    }

    private static OntModel loadFileInModel(OntModel m, File f) {
        try {
            System.out.println("Loading file: " + f.getName());
            m.read(f.getAbsolutePath(), null, "TURTLE");
        } catch (Exception e) {
            System.err.println("Could not load the file in turtle. Attempting to read it in turtle...");
            try {
                m.read(f.getAbsolutePath(), null, "RDF/XML");
            } catch (Exception e1) {
                System.err.println("Could not load ontology in rdf/xml.");
            }
        }
        return m;
    }

    public static ResultSet queryLocalRepository(String queryIn, OntModel repository) {
        Query query = QueryFactory.create(queryIn);
        // Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create(query, repository);
        ResultSet rs = qe.execSelect();
        // qe.close();
        return rs;
    }

    public static String getCountOf(String query, OntModel m, String varToQuery) {
        ResultSet r = Utils.queryLocalRepository(query, m);
        String result = "";
        while (r.hasNext()) {
            QuerySolution qs = r.nextSolution();
            result += qs.getLiteral("?" + varToQuery).getString();
            System.out.println("result is " + result);
        }

        return result;
    }

    public static ArrayList<Integer> queryresult(String query, OntModel m, String varToQuery1, String varToQuery2) {
        ResultSet r = Utils.queryLocalRepository(query, m);
        HashSet<String> hs1 = new HashSet<>();
        HashSet<String> hs2 = new HashSet<>();
        while (r.hasNext()) {
            QuerySolution qs = r.nextSolution();
            String var1 = "";
            String var2 = "";
            var1 = qs.getResource("?" + varToQuery1).getLocalName();
            var2 = qs.getResource("?" + varToQuery2).getLocalName();
            System.out.println();

            System.out.println("var 1 " + var1);
            System.out.println("var 2 " + var2);
            hs1.add(var1);
            hs2.add(var2);
        }
        ArrayList<Integer> arr = new ArrayList<>();
        arr.add(hs1.size());
        arr.add(hs2.size());
        return arr;
    }

    public static ArrayList<Integer> queryresultALL(String query, OntModel m, String varToQuery1, String varToQuery2,
            String varToQuery3) {
        ResultSet r = Utils.queryLocalRepository(query, m);
        HashSet<String> hs1 = new HashSet<>();
        HashSet<String> hs2 = new HashSet<>();
        HashSet<String> hs3 = new HashSet<>();
        while (r.hasNext()) {
            QuerySolution qs = r.nextSolution();
            String var1 = "";
            String var2 = "";
            String var3 = "";
            var1 = qs.getResource("?" + varToQuery1).getLocalName();
            var2 = qs.getResource("?" + varToQuery2).getLocalName();
            var3 = qs.getResource("?" + varToQuery3).getLocalName();
            System.out.println();

            System.out.println("var 1 " + var1);
            System.out.println("var 2 " + var2);
            System.out.println("var 3 " + var3);
            hs1.add(var1);
            hs2.add(var2);
            hs3.add(var3);
        }
        ArrayList<Integer> arr = new ArrayList<>();
        arr.add(hs1.size());
        arr.add(hs2.size());
        arr.add(hs3.size());
        return arr;
    }

    public static HashSet<String> queryresult112(String query, OntModel m, String varToQuery) {
        ResultSet r = Utils.queryLocalRepository(query, m);
        HashSet<String> hs1 = new HashSet<>();
        while (r.hasNext()) {
            QuerySolution qs = r.nextSolution();
            String var1 = qs.getResource("?" + varToQuery).getLocalName();
            System.out.println("var 1 " + var1);
            hs1.add(var1);
        }

        return hs1;
    }

    public static Literal queryresult113(String query, OntModel m, String varToQuery) {
        ResultSet r = Utils.queryLocalRepository(query, m);
        Literal time = null;
        System.out.println("query113");
        if (r.hasNext()) {
            QuerySolution qs = r.nextSolution();
            Literal timer = qs.getLiteral("?" + varToQuery);
            if (timer != null) {
                System.out.println("var 1 " + timer.getString());
                time = timer;
            }
        }

        return time;
    }

    /*
     * Check if AttributeValue is in the RDF file (format: RDF/XML)
     *
     * @param attributeValue: the value of the attribute rdf:about
     *
     * @param rdfFilePath: the path of the RDF file where the attributeValue should
     * be
     */
    static void checkExecutionXML(String attributeValue, String rdfFilePath) throws IOException {
        Path xmlPath = Paths.get(rdfFilePath);
        Source source = Input.from(Files.newInputStream(xmlPath)).build();
        XPathEngine xpathEngine = new JAXPXPathEngine();
        Map<String, String> p2u = Collections.singletonMap("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        xpathEngine.setNamespaceContext(p2u);
        String xpathExpression = "//*[@rdf:about='" + attributeValue + "']";
        Iterable<Node> text = xpathEngine.selectNodes(xpathExpression, source);
        Assert.assertTrue(text.iterator().hasNext());
    }

    public static File mkdir(String directoryPath) {
        File directory = new File(directoryPath);
        if (!new File(directoryPath).exists()) {
            directory.mkdir();
        }
        return directory;
    }
    // given an online repository, perform a test against a template/run.
    // TO DO
}

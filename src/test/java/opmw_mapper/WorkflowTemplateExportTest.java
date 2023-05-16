package opmw_mapper;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Source;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.DifferenceEvaluators;
import org.xmlunit.diff.ElementSelectors;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.util.Nodes;
import org.xmlunit.xpath.JAXPXPathEngine;
import org.xmlunit.xpath.XPathEngine;

import edu.isi.kcap.wings.opmm.Catalog;
import edu.isi.kcap.wings.opmm.FilePublisher;
import edu.isi.kcap.wings.opmm.WorkflowExecutionExport;
import edu.isi.kcap.wings.opmm.WorkflowTemplateExport;
import junit.framework.Assert;

import static org.xmlunit.assertj.XmlAssert.assertThat;

public class WorkflowTemplateExportTest {

  @Test
  /**
   * Test using the CaesarCypher workflow template:
   * Triple export to OPMW works
   * Catalog export works
   *
   * @throws IOException
   */
  public void exportAsOPMWTest() throws IOException {
    String taxonomyURL = "https://gist.githubusercontent.com/mosoriob/3d161a058ef7db3abd257a18a9466046/raw/bf5c7c442ee185feaeaa5db4478aeeb8ff6986a8/library.owl";
    String templatePath = "https://gist.githubusercontent.com/mosoriob/7cb83dd2af2ff370ed6592b21fb07339/raw/f59bc8941bcbd947d54ffd69d7fe58f5f595fa63/CaesarCypherMapReduce.owl";
    String domain = "CaesarCypher";
    String turtleFile = domain + ".ttl";
    Catalog c = new Catalog(domain, "testExport", "domains", taxonomyURL);
    WorkflowTemplateExport w = new WorkflowTemplateExport(templatePath, c, "http://www.opmw.org/", "exportTest",
        "https://endpoint.mint.isi.edu/provenance/query", domain, true);
    w.exportAsOPMW(turtleFile, "TTL");

    String expectedTurtlePath = "src/test/resources/" + turtleFile;
    String lines1NoDate = removeDates(turtleFile);
    String lines2NoDate = removeDates(expectedTurtlePath);

    // assertEquals("The files differ!", lines1NoDate, lines2NoDate);

    // Catalog
    c.exportCatalog("catalog", "RDF/XML");
    String expectedCatalogPath = "src/test/resources/CatalogCaesarCypher";
    String catalogPath = "catalog/CaesarCypher";
    File f1 = new File(catalogPath);
    File f2 = new File(expectedCatalogPath);

    DifferenceEvaluator evaluator = DifferenceEvaluators
        .downgradeDifferencesToEqual(ComparisonType.CHILD_NODELIST_SEQUENCE);

    Diff diff = DiffBuilder.compare(f1)
        .withTest(f2).ignoreComments()
        .ignoreWhitespace()
        .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
        .withDifferenceEvaluator(evaluator)
        .checkForSimilar()
        .build();

    assertEquals(diff.hasDifferences(), false);

  }

  @Test
  public void testNeuroDisk() throws IOException {
    String taxonomyURL = "src/test/resources/neuro/components.owl";
    String templatePath = "src/test/resources/neuro/originalTemplate.owl";
    String seededTemplate = "src/test/resources/neuro/seededTemplate.owl";
    String executionPath = "src/test/resources/neuro/execution.owl";
    String planPath = "src/test/resources/neuro/plan.owl";
    Catalog c = new Catalog("genomics", "testExport", "domains", taxonomyURL);
    String domain = "neuroDisk";
    WorkflowTemplateExport w = new WorkflowTemplateExport(templatePath, c, "http://www.opmw.org/", "exportTest",
        "https://endpoint.mint.isi.edu/provenance/query", domain, true);
    w.exportAsOPMW("meta-regression", "TTL");
    WorkflowExecutionExport e = new WorkflowExecutionExport(planPath, c, "http://www.opmw.org/", "exportTest",
        "https://endpoint.mint.isi.edu/provenance/query", domain);
    e.exportAsOPMW("meta-regression-execution", "TTL");
    c.exportCatalog(null, "RDF/XML");

  }

  @Test
  public void testWorkflowExecutionExport() throws IOException {
    FilePublisher p = new FilePublisher("tmp/", "http://localhost");
    String components = "src/test/resources/neuro/components.owl";
    String executionPath = "src/test/resources/neuro/execution.owl";
    String expandedTemplatePath = "src/test/resources/neuro/expandedTemplate.owl";
    String originalTemplatePath = "src/test/resources/neuro/originalTemplate.owl";
    String planPath = "src/test/resources/neuro/plan.owl";
    String seededTemplate = "src/test/resources/neuro/seededTemplate.owl";
    Catalog c = new Catalog("genomics", "testExport", "domains", components);
    String domain = "neuroDisk";
    WorkflowExecutionExport e = new WorkflowExecutionExport(planPath, c, "http://www.opmw.org/", "exportTest",
        "https://endpoint.mint.isi.edu/provenance/query", domain, p);
    e.exportAsOPMW("meta-regression-execution.ttl", "TTL");
    e.exportAsOPMW("meta-regression-execution.xml", "RDF/XML");

    Path xmlPath = Paths.get("meta-regression-execution.xml");
    Source source = Input.from(Files.newInputStream(xmlPath)).build();

    // Step 2: Create XPath engine and evaluate the XPath expression
    XPathEngine xpathEngine = new JAXPXPathEngine();
    Map<String, String> p2u = Collections.singletonMap("owl", "http://www.w3.org/2002/07/owl#");
    xpathEngine.setNamespaceContext(p2u);

    String xpathExpression = "//owl:Class";
    Iterable<Node> text = xpathEngine.selectNodes(xpathExpression, source);
    Assert.assertTrue(text.iterator().hasNext());
    // c.exportCatalog(null, "RDF/XML");

  }

  private String removeDates(String turtleFile) throws IOException {
    File f1 = new File(turtleFile);
    String lines1 = FileUtils.readFileToString(f1, "utf-8");
    String lines1NoDate = removeLineMatch(lines1, "xsd:dateTime");
    return lines1NoDate;
  }

  public String removeLineMatch(String lines, String match) {
    String[] linesArray = lines.split("\n");
    for (int i = 0; i < linesArray.length; i++) {
      if (linesArray[i].contains(match)) {
        linesArray[i] = "";
      }
    }
    // Rebuild the string
    String result = "";
    for (int i = 0; i < linesArray.length; i++) {
      result += linesArray[i] + "\n";
    }
    return result;
  }

  private static class CustomNamespaceContext implements NamespaceContext {
    @Override
    public String getNamespaceURI(String prefix) {
      if ("rdf".equals(prefix)) {
        return "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
      } else if ("owl".equals(prefix)) {
        return "http://www.w3.org/2002/07/owl#";
      }
      return null;
    }

    @Override
    public String getPrefix(String namespaceURI) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
      throw new UnsupportedOperationException();
    }
  }
}

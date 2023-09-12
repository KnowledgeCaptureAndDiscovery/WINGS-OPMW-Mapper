package opmw_mapper;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import org.openprovenance.prov.model.Document;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.DifferenceEvaluators;
import org.xmlunit.diff.ElementSelectors;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.DifferenceEvaluator;
import edu.isi.kcap.wings.opmm.Catalog;
import edu.isi.kcap.wings.opmm.FilePublisher;
import edu.isi.kcap.wings.opmm.ProvNUtils;
import edu.isi.kcap.wings.opmm.WorkflowExecutionExport;
import edu.isi.kcap.wings.opmm.WorkflowTemplateExport;

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
        "https://endpoint.mint.isi.edu/provenance/query", domain);
    w.exportAsOPMW(turtleFile, "TTL");

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
    String planPath = "src/test/resources/neuro/plan.owl";
    Catalog c = new Catalog("genomics", "testExport", "domains", taxonomyURL);
    String domain = "neuroDisk";
    WorkflowTemplateExport w = new WorkflowTemplateExport(templatePath, c, "http://www.opmw.org/", "exportTest",
        "https://endpoint.mint.isi.edu/provenance/query", domain);
    w.exportAsOPMW("meta-regression", "TTL");
    WorkflowExecutionExport e = new WorkflowExecutionExport(planPath, c, "http://www.opmw.org/", "exportTest",
        "https://endpoint.mint.isi.edu/provenance/query", domain);
    e.exportAsOPMW("meta-regression-execution", "TTL");
    c.exportCatalog(null, "RDF/XML");

  }

  @Test
  public void testTheFullPipeline() throws IOException {
    // Create a new FilePublisher who is responsible to publish the inputs, code,
    // outputs, etc.
    // In this case, we aren't uploading files to a server, so we just use a local
    // directory
    // and the local directory is connected to a Web server.
    FilePublisher p = new FilePublisher(FilePublisher.Type.FILE_SYSTEM, "tmp/", "http://localhost");
    // Some directories are created to store the files to store:
    // 1. the catalog of components (Catalog),
    String components = "src/test/resources/neuro/components.owl";
    // 2. the execution of the workflow (WorkflowExecutionExport),
    String executionPath = "src/test/resources/neuro/execution.owl";
    // 3. the expanded template
    String expandedTemplatePath = "src/test/resources/neuro/expandedTemplate.owl";
    // 4. the original template
    String originalTemplatePath = "src/test/resources/neuro/originalTemplate.owl";
    // 5. the plan (WorkflowExecutionExport).
    String planPath = "src/test/resources/neuro/plan.owl";
    // 6. the seeded template
    String seededTemplate = "src/test/resources/neuro/seededTemplate.owl";

    /*
     * Fake data to test the export
     */
    // Domain: it is wings domain concept: it is the name of the domain
    String domain = "neuroDisk";
    // exportName: used by the catalog to create the URL:
    // w3id.org/opmw/wings/{exportName}/{domain}#Components
    // w3id.org/opmw/wings/{exportName}/{domain}#Data
    String exportName = "testExport";
    // catalogRepositoryDirectory: the directory where the catalog will be stored
    String catalogRepositoryDirectory = "domains";

    /**
     * The catalog is a Class designed to deal the versioning of Data and Catalog
     */
    Catalog c = new Catalog(domain, exportName, catalogRepositoryDirectory, components);

    // Variables used to export create the URL
    String exportUrl = "http://www.opmw.org/";
    String exportPrefix = "exportTest";
    String endpointURI = "https://endpoint.mint.isi.edu/provenance/query";

    // Create WorkflowExecutionExport: A class designed to export WINGS workflow
    // execution traces in RDF according to the OPMW-PROV model.
    WorkflowExecutionExport e = new WorkflowExecutionExport(planPath, c, exportUrl, exportPrefix, endpointURI, domain,
        p);

    // Function: `exportAsOPMW` that will check if an execution exists and then
    // transforms it as RDF under the OPMW model.
    String executionOPMW_TTL = "meta-regression-execution.ttl";
    String executionOPMW_XML = "meta-regression-execution.xml";
    e.exportAsOPMW(executionOPMW_TTL, "TTL");
    e.exportAsOPMW(executionOPMW_XML, "RDF/XML");

    // Checking if some entities are in the RDF file
    List<String> entitiesSearched = MockupData.metaAnalysisWorkflowExecution();
    for (String entity : entitiesSearched) {
      Utils.checkExecutionXML(entity, executionOPMW_XML);
    }
  }

  @Test
  public void convertToProvTest() {
    String source = "meta-regression-execution.ttl";
    String target = "meta-regression-execution.provn";
    Path sourcePath = Paths.get(source);
    Path targetPath = Paths.get(target);
    String documentString = ProvNUtils.convertToProvN(sourcePath);
    Document document = ProvNUtils.convertToProvN(sourcePath, targetPath);
    String entityForSearch = "Meta-Analysis-57-52f5b3c2-a970-42ed-a503-fa4dfdd62ecd_MetaAnalysisNode";
    ProvNUtils.getEntityByLocalName(document, entityForSearch);
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

}

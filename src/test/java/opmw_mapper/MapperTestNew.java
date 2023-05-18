package opmw_mapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.isi.kcap.wings.opmm.FilePublisher;
import edu.isi.kcap.wings.opmm.Mapper;

public class MapperTestNew {
  private static String webServerDirectory = "tmp/";
  private static String webServerDomain = "http://localhost";
  FilePublisher filePublisher = new FilePublisher(webServerDirectory, webServerDomain);
  // Domain: it is wings domain concept: it is the name of the domain
  String domain = "neuroDisk";
  // exportPrefix: used by the catalog to create the URL:
  // w3id.org/opmw/wings/{exportName}/{domain}#Components
  // w3id.org/opmw/wings/{exportName}/{domain}#Data
  String exportPrefix = "exportTest";
  String exportUrl = "http://www.opmw.org/";
  // catalogRepositoryDirectory: the directory where the catalog will be stored
  String catalogRepositoryDirectory = "domains";
  String endpointQueryURI = "https://endpoint.mint.isi.edu/provenance/query";
  String endpointPostURI = "https://endpoint.mint.isi.edu/provenance/query";
  String componentLibraryFilePath = "src/test/resources/neuro/components.owl";
  String planFilePath = "src/test/resources/neuro/plan.owl";

  @Test
  public void testMapperTurtle() throws IOException {
    String serialization = "turtle";
    File file = new File("tmp/" + serialization);
    if (!file.exists()) {
      file.mkdir();
    }
    String executionFilePath = "tmp/" + serialization + "/execution.ttl";
    String expandedTemplateFilePath = "tmp/" + serialization + "/expandedTemplate.ttl";
    String abstractFilePath = "tmp/" + serialization + "/abstractTemplate.ttl";
    try {
      Mapper.main(domain, exportPrefix, exportUrl, catalogRepositoryDirectory, componentLibraryFilePath, planFilePath,
          endpointQueryURI, endpointPostURI, executionFilePath, expandedTemplateFilePath, abstractFilePath,
          filePublisher, serialization);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
  }

  @Test
  public void testXMLTurtle() throws IOException {
    String serialization = "rdf/xml";
    File file = new File("tmp/" + serialization);
    if (!file.exists()) {
      file.mkdirs();
    }
    String executionFilePath = "tmp/" + serialization + "/execution.xml";
    String expandedTemplateFilePath = "tmp/" + serialization + "/expandedTemplate.xml";
    String abstractFilePath = "tmp/" + serialization + "/abstractTemplate.xml";
    try {
      Mapper.main(domain, exportPrefix, exportUrl, catalogRepositoryDirectory, componentLibraryFilePath, planFilePath,
          endpointQueryURI, endpointPostURI, executionFilePath, expandedTemplateFilePath, abstractFilePath,
          filePublisher, serialization);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    List<String> entities = MockupData.metaAnalysisWorkflowExecution();
    for (String entity : entities) {
      Utils.checkExecutionXML(entity, executionFilePath);
    }

  }
}

package opmw_mapper;

import org.apache.jena.ontology.OntModel;
import org.junit.Test;
import edu.isi.kcap.wings.opmm.ModelUtils;

public class UtilsTest {
  @Test
  public void test() {
    String resourceUri = "https://opmw.org/exportTest/resource/WorkflowExecutionAccount/Caesar_Cypher-2b-3c5e9dd8-6c44-4666-a6a2-cf572aca76db";
    OntModel model = ModelUtils.loadModel(resourceUri);
    model.getIndividual(resourceUri).listProperties().toList().forEach(System.out::println);
  }
}

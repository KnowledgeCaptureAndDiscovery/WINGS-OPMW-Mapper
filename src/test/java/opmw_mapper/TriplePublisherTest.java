package opmw_mapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Test;

import edu.isi.kcap.wings.opmm.Publisher.TriplesPublisher;
import junit.framework.Assert;

public class TriplePublisherTest {

  String updateEndpoint = "https://endpoint.mint.isi.edu/provenance/data";
  String queryEndpoint = "https://endpoint.mint.isi.edu/provenance";
  String graph = "http://localhost:3030/ds/data/opmw";

  @Test
  public void publishTriplesTestTurtle() throws IOException {
    String resourceUri = "http://example.org/example#example";
    Lang serialization = Lang.TTL;
    TriplesPublisher tp = new TriplesPublisher(queryEndpoint, updateEndpoint, graph, "", Lang.TTL);
    tp.setGraphURI(graph);
    File file = createTripleFile(resourceUri, "TTL");
    tp.publish(file);
    Assert.assertTrue(TriplesPublisher.findResourceOnRepository(resourceUri, queryEndpoint, graph));
  }

  @Test
  public void publishTriplesTestRDFXML() throws IOException {
    String resourceUri = "http://example.org/example";
    Lang serialization = Lang.RDFXML;
    TriplesPublisher tp = new TriplesPublisher(queryEndpoint, updateEndpoint, resourceUri, "test", serialization);
    tp.setGraphURI(graph);
    File file = createTripleFile(resourceUri, "RDF/XML");
    tp.publish(file);
    Assert.assertTrue(TriplesPublisher.findResourceOnRepository(resourceUri, queryEndpoint, graph));
  }

  private File createTripleFile(String resourceUri, String serialization) throws FileNotFoundException {
    // TODO Auto-generated method stub
    Model m = ModelFactory.createDefaultModel();
    Resource r = m.createResource(resourceUri);
    r.addProperty(RDF.type, OWL.Class);
    r.addProperty(RDFS.label, "example");
    File f = null;
    if (serialization == "TTL")
      f = new File("src/test/resources/example.ttl");
    else if (serialization == "RDF/XML")
      f = new File("src/test/resources/example.xml");
    else
      throw new FileNotFoundException("Serialization not supported");
    m.write(new java.io.FileOutputStream(f), serialization);
    return f;
  }

}

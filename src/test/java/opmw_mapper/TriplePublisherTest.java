package opmw_mapper;

import java.io.File;
import java.net.MalformedURLException;

import org.junit.Test;

import edu.isi.kcap.wings.opmm.Publisher.TriplesPublisher;

public class TriplePublisherTest {

  @Test
  public void publishTriplesTest() throws MalformedURLException {
    String updateEndpoint = "https://endpoint.mint.isi.edu/wings-provenance/data";
    String graph = "http://localhost:3030/ds/data/opmw";
    TriplesPublisher tp = new TriplesPublisher(updateEndpoint, graph);
    String fileName = "src/test/resources/CaesarCypher.ttl";
    File file = new File(fileName);
    tp.publish(file);
  }

}

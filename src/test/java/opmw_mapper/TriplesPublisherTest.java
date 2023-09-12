package opmw_mapper;

import java.net.MalformedURLException;

import org.junit.Test;

import edu.isi.kcap.wings.opmm.Publisher.TriplesPublisher;

public class TriplesPublisherTest {
  TriplesPublisher tp;

  public TriplesPublisherTest() {
    String updateEndpoint = "http://localhost:3030/ds/update";
    String graph = "http://localhost:3030/ds/data/opmw";
    tp = new TriplesPublisher(updateEndpoint, graph);
  }

  @Test
  public void testPublish() throws MalformedURLException {
    this.tp.publish();
  }
}

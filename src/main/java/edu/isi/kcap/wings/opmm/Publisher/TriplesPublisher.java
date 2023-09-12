package edu.isi.kcap.wings.opmm.Publisher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class TriplesPublisher {
  String updateEndpoint;
  String graph;

  public TriplesPublisher(String updateEndpoint, String graph) {
    this.updateEndpoint = updateEndpoint;
    this.graph = graph;
  }

  public void publish() throws MalformedURLException {
    String query = String.format("graph=%s", this.graph);
    URL url = new URL(this.updateEndpoint + "?" + query);
  }
}

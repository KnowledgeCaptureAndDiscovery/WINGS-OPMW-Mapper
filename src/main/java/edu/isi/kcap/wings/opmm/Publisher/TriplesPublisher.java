package edu.isi.kcap.wings.opmm.Publisher;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.QuerySolution;

import edu.isi.kcap.wings.opmm.ModelUtils;

public class TriplesPublisher {
  public String updateEndpoint;
  public String queryEndpoint;
  public String graphURI;
  DatasetAccessor accessor;

  public TriplesPublisher(String queryEndpoint, String updateEndpoint, String graph) {
    this.updateEndpoint = updateEndpoint;
    this.queryEndpoint = queryEndpoint;
    this.graphURI = graph;
    this.accessor = DatasetAccessorFactory.createHTTP(updateEndpoint);
  }

  public void publish(File file) throws IOException {
    String content = FileUtils.readFileToString(file, "UTF-8");
    HttpPost request = setUpRequest();
    CloseableHttpClient httpClient = HttpClients.createDefault();
    uploadTriples(content, request, httpClient);
  }

  private void uploadTriples(String content, HttpPost request, CloseableHttpClient httpClient)
      throws UnsupportedEncodingException, IOException, ClientProtocolException {
    if (content != null) {
      StringEntity input = new StringEntity(content);
      input.setContentType("text/turtle");
      request.setEntity(input);
      HttpResponse response = httpClient.execute(request);
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode > 299) {
        throw new IOException("Unable to upload the domain " + statusCode);
      }
    } else {
      throw new IOException("File content is null");
    }
  }

  private HttpPost setUpRequest() {
    HttpPost request = new HttpPost(this.updateEndpoint + "?graph=" + graphURI);
    int timeoutSeconds = 10;
    int CONNECTION_TIMEOUT_MS = timeoutSeconds * 1000;
    RequestConfig requestConfig = RequestConfig
        .custom()
        .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
        .setConnectTimeout(CONNECTION_TIMEOUT_MS)
        .setSocketTimeout(CONNECTION_TIMEOUT_MS)
        .build();
    request.setConfig(requestConfig);
    return request;
  }

  public static boolean findResourceOnRepository(String resourceUri, String queryEndpoint, String graph) {
    String query = "PREFIX opmw: <http://www.opmw.org/ontology/> "
        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
        + "SELECT ?s WHERE { "
        + "GRAPH <" + graph + "> { "
        + "?s ?p ?o . "
        + "FILTER (?s = <" + resourceUri + ">) "
        + "} "
        + "}";
    QuerySolution solution = ModelUtils.queryOnlineRepository(query, queryEndpoint);
    String uri = solution.getResource("?s").getURI();
    if (uri.equals(resourceUri)) {
      ;
      return true;
    }
    return false;
  }
}

package edu.isi.kcap.wings.opmm.Publisher;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.datatypes.xsd.impl.RDFLangString;
import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDF;

import edu.isi.kcap.wings.opmm.Constants;
import edu.isi.kcap.wings.opmm.ModelUtils;

public class TriplesPublisher {
  public String updateEndpoint;
  public String queryEndpoint;
  public String graphURI = null;
  DatasetAccessor accessor;
  public Lang serialization;
  public String exportUrl;

  public TriplesPublisher(String endpointQueryURI, String endpointPostURI, String exportBaseUrl,
      String exportPrefix,
      Lang serialization) {
    this.updateEndpoint = endpointPostURI;
    this.queryEndpoint = endpointQueryURI;
    this.accessor = DatasetAccessorFactory.createHTTP(updateEndpoint);
    this.serialization = serialization;
    if (exportBaseUrl == null)
      exportBaseUrl = Constants.PREFIX_EXPORT_GENERIC;
    this.exportUrl = exportBaseUrl + exportPrefix + "/";
  }

  public void publish(File file) throws IOException {
    if (this.graphURI == null)
      throw new IOException("Graph URI not set");
    byte[] data = FileUtils.readFileToByteArray(file);
    HttpPost request = setUpRequest();
    CloseableHttpClient httpClient = HttpClients.createDefault();
    uploadTriples(data, request, httpClient);
  }

  private void uploadTriples(byte[] data, HttpPost request, CloseableHttpClient httpClient)
      throws UnsupportedEncodingException, IOException, ClientProtocolException {
    ByteArrayEntity entity = new ByteArrayEntity(data);
    request.setEntity(entity);
    if (serialization == Lang.TURTLE || serialization == Lang.TTL)
      request.setHeader("Content-Type", "text/turtle");
    else if (serialization == Lang.RDFXML)
      request.setHeader("Content-Type", "application/rdf+xml");
    else
      throw new UnsupportedEncodingException("Serialization not supported");
    HttpResponse response = httpClient.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    if (statusCode > 299) {
      throw new IOException("Unable to upload the domain " + statusCode);
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

  public void setUpdateEndpoint(String updateEndpoint) {
    this.updateEndpoint = updateEndpoint;
  }

  public void setQueryEndpoint(String queryEndpoint) {
    this.queryEndpoint = queryEndpoint;
  }

  public void setGraphURI(String graphURI) {
    this.graphURI = graphURI;
  }

  public void setAccessor(DatasetAccessor accessor) {
    this.accessor = accessor;
  }

  public void setSerialization(Lang serialization) {
    this.serialization = serialization;
  }

  public void setExportUrl(String exportUrl) {
    this.exportUrl = exportUrl;
  }

}

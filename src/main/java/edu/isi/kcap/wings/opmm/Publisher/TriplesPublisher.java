package edu.isi.kcap.wings.opmm.Publisher;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;

public class TriplesPublisher {
  String updateEndpoint;
  String graphURI;
  DatasetAccessor accessor;

  public TriplesPublisher(String updateEndpoint, String graph) {
    this.updateEndpoint = updateEndpoint;
    this.graphURI = graph;
    this.accessor = DatasetAccessorFactory.createHTTP(updateEndpoint);
  }

  public HttpContext setAuth() {
    HttpContext httpContext = new BasicHttpContext();
    CredentialsProvider provider = new BasicCredentialsProvider();
    provider.setCredentials(new AuthScope(AuthScope.ANY_HOST,
        AuthScope.ANY_PORT), new UsernamePasswordCredentials("user", "pass"));
    httpContext.setAttribute(ClientContext.CREDS_PROVIDER, provider);
    return httpContext;
  }

  public void publish(File file) {
    String filepath = file.getAbsolutePath();
    try {
      CloseableHttpClient httpClient = HttpClients.createDefault();
      HttpPut putRequest = new HttpPut(this.updateEndpoint + "?graph=" + graphURI);

      // Todo: move it to configuration
      int timeoutSeconds = 10;
      int CONNECTION_TIMEOUT_MS = timeoutSeconds * 1000;
      RequestConfig requestConfig = RequestConfig
          .custom()
          .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
          .setConnectTimeout(CONNECTION_TIMEOUT_MS)
          .setSocketTimeout(CONNECTION_TIMEOUT_MS)
          .build();
      putRequest.setConfig(requestConfig);

      // read a file into a string
      String content = FileUtils.readFileToString(file, "UTF-8");
      if (content != null) {
        StringEntity input = new StringEntity(content);
        input.setContentType("text/turtle");
        putRequest.setEntity(input);
        HttpResponse response = httpClient.execute(putRequest);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode > 299) {
          System.err.println("Unable to upload the domain " + statusCode);
          System.err.println(response.getStatusLine().getReasonPhrase());
        } else {
          System.err.println("Success uploading the domain " + statusCode);
          System.err.println(response.getStatusLine().getReasonPhrase());
        }
      } else {
        System.err.println("File content is null " + filepath);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

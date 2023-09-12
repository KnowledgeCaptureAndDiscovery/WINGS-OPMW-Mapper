package edu.isi.kcap.wings.opmm.Publisher;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.apache.jena.sparql.modify.UpdateProcessRemote;
import org.apache.jena.sparql.modify.request.UpdateCreate;
import org.apache.jena.sparql.modify.request.UpdateLoad;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
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

  public void publish(File file) throws MalformedURLException {
    Model model = ModelFactory.createDefaultModel();
    RDFDataMgr.read(model, file.getAbsolutePath());
    UpdateRequest request = UpdateFactory.create();
    // request.add(new UpdateCreate(graphURI));
    StringWriter out = new StringWriter();
    model.getNsPrefixMap().forEach((k, v) -> {
      request.add("prefix " + k + ": <" + v + ">");
    });

    StmtIterator iter = model.listStatements();
    while (iter.hasNext()) {
      System.out.println(iter.next());
    }
    request.add("prefix dcterms: <http://purl.org/dc/terms/>"
        + "INSERT DATA { GRAPH <" + graphURI + ">" + "{" + out + " }}");
    request.toString();

    HttpContext httpContext = setAuth();
    UpdateProcessor processor = UpdateExecutionFactory.createRemote(request,
        updateEndpoint);
    ((UpdateProcessRemote) processor).setHttpContext(httpContext);
    processor.execute();
  }

}

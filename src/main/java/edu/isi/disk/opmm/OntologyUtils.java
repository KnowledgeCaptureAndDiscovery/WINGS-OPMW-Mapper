package edu.isi.disk.opmm;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;

public class OntologyUtils {
    public static final String[] POSSIBLE_VOCAB_SERIALIZATIONS = { "application/rdf+xml", "text/turtle", "text/n3",
            "application/ld+json" };

    /**
     * Method that will download an ontology given its URI, doing content
     * negotiation The ontology will be downloaded in the first serialization
     * available (see Constants.POSSIBLE_VOCAB_SERIALIZATIONS)
     * 
     * @param uri          the URI of the ontology
     * @param downloadPath path where the ontology will be saved locally.
     */
    public static void downloadOntology(String uri, String downloadPath) {
        for (String serialization : POSSIBLE_VOCAB_SERIALIZATIONS) {
            System.out.println("Attempting to download vocabulary in " + serialization);
            // logger is not initialized correctly and fails in tests (to fix)
            // logger.info("Attempting to download vocabulary in " + serialization);
            try {
                URL url = new URL(uri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);
                connection.setRequestProperty("Accept", serialization);
                int status = connection.getResponseCode();
                boolean redirect = false;
                if (status != HttpURLConnection.HTTP_OK) {
                    if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
                            || status == HttpURLConnection.HTTP_SEE_OTHER)
                        redirect = true;
                }
                // there are some vocabularies with multiple redirections:
                // 301 -> 303 -> owl
                while (redirect) {
                    String newUrl = connection.getHeaderField("Location");
                    connection = (HttpURLConnection) new URL(newUrl).openConnection();
                    connection.setRequestProperty("Accept", serialization);
                    status = connection.getResponseCode();
                    if (status != HttpURLConnection.HTTP_MOVED_TEMP && status != HttpURLConnection.HTTP_MOVED_PERM
                            && status != HttpURLConnection.HTTP_SEE_OTHER)
                        redirect = false;
                }
                InputStream in = (InputStream) connection.getInputStream();
                Files.copy(in, Paths.get(downloadPath), StandardCopyOption.REPLACE_EXISTING);
                in.close();
                break; // if the vocabulary is downloaded, then we don't download it for the other
                       // serializations
            } catch (Exception e) {
                final String message = "Failed to download vocabulary in RDF format [" + serialization + "]: ";
                //logger.severe(message + e.toString());
                throw new RuntimeException(message, e);
            }
        }
    }
}

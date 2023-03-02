package edu.isi.disk.opmm;

import java.io.InputStream;
import java.util.Arrays;

import org.apache.jena.iri.IRI;
import org.openprovenance.prov.configuration.Configuration;
import org.openprovenance.prov.interop.Formats;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Agent;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.Namespace;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.StatementOrBundle;
import org.openprovenance.prov.model.WasAttributedTo;
import org.openprovenance.prov.model.WasDerivedFrom;


import org.openprovenance.prov.notation.Utility;
/**
 * A little provenance goes a long way.
 * ProvToolbox Tutorial 1: creating a provenance document in Java and
 * serializing it
 * to SVG (in a file) and to PROVN (on the console).
 * 
 * @author lucmoreau
 * @see <a href=
 *      "http://blog.provbook.org/2013/10/11/a-little-provenance-goes-a-long-way/">a-little-provenance-goes-a-long-way
 *      blog post</a>
 */
public class DocumentProv {

    public static final String PROV_NEUROSCIENCE_NS = "http://provenance.isi.edu/disk/neuro/";
    public static final String PROV_NEUROSCIENCE_PREFIX = "provNeuroScience";

    public static final String PROV_NEUROSCIENCE_HYPOTHESIS_NS = "http://provenance.isi.edu/disk/neuro/hypothesis/";
    public static final String PROV_NEUROSCIENCE_HYPOTHESIS_PREFIX = "provNeuroScienceHypothesis";

    public static final String PROV_NEUROSCIENCE_TLOI_NS = "http://provenance.isi.edu/disk/neuro/tloi/";
    public static final String PROV_NEUROSCIENCE_TLOI_PREFIX = "provNeuroScienceTLOI";

    public static final String PROV_NEUROSCIENCE_LOI_NS = "http://provenance.isi.edu/disk/neuro/loi/";
    public static final String PROV_NEUROSCIENCE_LOI_PREFIX = "provNeuroScienceLOI";

    public static final String DISK_PREFIX = "neuroScienceDisk";
    public static final String DISK_NS = "http://localhost:8080/disk-server/admin/";

    public static final String ENIGMA_PREFIX = "enigmaQuestion";
    public static final String ENIGMA_NS = "https://w3id.org/sqo/resource/";

    public static final String WINGS_ONTOLOGY_PREFIX = "wings";
    public static final String WINGS_ONTOLOGY_NS = "http://www.wings-workflows.org/ontology/";

    public static final String RDF_PREFIX = "rdf";
    public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    public static final String RDFS_PREFIX = "rdfs";
    public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";

    public static final String DCTERMS_NS = "http://purl.org/dc/terms/";
    public static final String DCTERMS_PREFIX = "dcterms";


    public final ProvFactory factory;
    public final Namespace ns;
    public Document document;

    public DocumentProv(ProvFactory pFactory) {
        this.factory = pFactory;
        this.document = pFactory.newDocument();
        ns = new Namespace();
        ns.addKnownNamespaces();
        ns.register(PROV_NEUROSCIENCE_HYPOTHESIS_PREFIX, PROV_NEUROSCIENCE_HYPOTHESIS_NS);
        ns.register(PROV_NEUROSCIENCE_TLOI_PREFIX, PROV_NEUROSCIENCE_TLOI_NS);
        ns.register(PROV_NEUROSCIENCE_LOI_PREFIX, PROV_NEUROSCIENCE_LOI_NS);
        ns.register(PROV_NEUROSCIENCE_PREFIX, PROV_NEUROSCIENCE_NS);
        ns.register(DISK_PREFIX, DISK_NS);
        ns.register(ENIGMA_PREFIX, ENIGMA_NS);
        ns.register(WINGS_ONTOLOGY_PREFIX, WINGS_ONTOLOGY_NS);
        ns.register(RDFS_PREFIX, RDFS_NS);
        ns.register(RDF_PREFIX, RDF_NS);
        ns.register(DCTERMS_PREFIX, DCTERMS_NS);
    }

    public void registerNamespace(String prefix, String iri) {
        ns.register(prefix, iri);
    }

    public QualifiedName qn(String n) {
        return ns.qualifiedName(PROV_NEUROSCIENCE_PREFIX, n, factory);
    }

    
    
    public QualifiedName qn(String n, String prefix) {        return ns.qualifiedName(prefix, n, factory);
    }
    

    public QualifiedName qn(String n, String prefix_iri, String none) {
        String prefix = ns.getNamespaces().get(prefix_iri);
        return ns.qualifiedName(prefix, n, factory);
    }




    public void doConversions(Document document, String file) {
        String pngFile = file + ".png";
        String provFile = file + ".provn";
        String ttlFile = file + ".ttl";
        String jsonFile = file + ".json";
        InteropFramework intF = new InteropFramework();
        intF.writeDocument(pngFile, document);
        intF.writeDocument(provFile, document);
        intF.writeDocument(ttlFile, document);
        intF.writeDocument(jsonFile, document);
    }

    public void doFigure(Document document, String file) {
        InteropFramework intF = new InteropFramework();
        intF.writeDocument(file, document);
    }

    public void closingBanner() {
        System.out.println("*************************");
    }

    public void openingBanner() {
        System.out.println("*************************");
        System.out.println("* Converting document  ");
        System.out.println("*************************");
    }

    public static void main(String[] args) {
        if (args.length != 1)
            throw new UnsupportedOperationException("main to be called with filename");

    }

}

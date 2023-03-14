package edu.isi.disk.opmm;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Namespace;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.ProvFactory;
/**
 *
 */
public class DocumentProv {

    public static final String PROV_NEUROSCIENCE_NS = "http://provenance.isi.edu/disk/neuro/";
    public static final String PROV_NEUROSCIENCE_PREFIX = "provNeuroScience";

    public static final String PROV_NEUROSCIENCE_QUESTION_NS = "http://provenance.isi.edu/disk/neuro/question/";
    public static final String PROV_NEUROSCIENCE_QUESTION_PREFIX = "provNeuroScienceQuestion";

    public static final String PROV_NEUROSCIENCE_HYPOTHESIS_NS = "http://provenance.isi.edu/disk/neuro/hypothesis/";
    public static final String PROV_NEUROSCIENCE_HYPOTHESIS_PREFIX = "provNeuroScienceHypothesis";

    public static final String PROV_NEUROSCIENCE_TLOI_NS = "http://provenance.isi.edu/disk/neuro/tloi/";
    public static final String PROV_NEUROSCIENCE_TLOI_PREFIX = "provNeuroScienceTLOI";

    public static final String PROV_NEUROSCIENCE_LOI_NS = "http://provenance.isi.edu/disk/neuro/loi/";
    public static final String PROV_NEUROSCIENCE_LOI_PREFIX = "provNeuroScienceLOI";

    public static final String DISK_PREFIX = "neuroScienceDisk";
    public static final String DISK_NS = "http://localhost:8080/disk-server/admin/";

    public static final String DISK_ONTOLOGY_PREFIX = "disk";
    public static final String DISK_ONTOLOGY_NS = "http://disk-project.org/ontology/disk#";

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
        register(ns, PROV_NEUROSCIENCE_NS);
    }

    public static void register(Namespace localNs, String defaultNameSpace){
        localNs.addKnownNamespaces();
        localNs.register(PROV_NEUROSCIENCE_QUESTION_PREFIX, PROV_NEUROSCIENCE_QUESTION_NS);
        localNs.register(PROV_NEUROSCIENCE_HYPOTHESIS_PREFIX, PROV_NEUROSCIENCE_HYPOTHESIS_NS);
        localNs.register(PROV_NEUROSCIENCE_TLOI_PREFIX, PROV_NEUROSCIENCE_TLOI_NS);
        localNs.register(PROV_NEUROSCIENCE_LOI_PREFIX, PROV_NEUROSCIENCE_LOI_NS);
        localNs.register(PROV_NEUROSCIENCE_PREFIX, PROV_NEUROSCIENCE_NS);
        localNs.register(DISK_PREFIX, DISK_NS);
        localNs.register(DISK_ONTOLOGY_PREFIX, DISK_ONTOLOGY_NS);
        localNs.register(ENIGMA_PREFIX, ENIGMA_NS);
        localNs.register(WINGS_ONTOLOGY_PREFIX, WINGS_ONTOLOGY_NS);
        localNs.register(RDFS_PREFIX, RDFS_NS);
        localNs.register(RDF_PREFIX, RDF_NS);
        localNs.register(DCTERMS_PREFIX, DCTERMS_NS);
        localNs.setDefaultNamespace(defaultNameSpace);
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

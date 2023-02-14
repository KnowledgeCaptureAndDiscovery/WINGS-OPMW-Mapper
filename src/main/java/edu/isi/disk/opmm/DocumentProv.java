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

    public static final String PROVBOOK_NS = "http://provenance.isi.edu/disk/neuro/";
    public static final String PROVBOOK_PREFIX = "diskProv";

    public static final String JIM_PREFIX = "jim";
    public static final String JIM_NS = "http://www.cs.rpi.edu/~hendler/";

    public static final String DISK_PREFIX = "disk";
    public static final String DISK_NS = "http://localhost:8080/disk-server/admin/";

    public static final String ENIGMA_PREFIX = "enigmaQuestion";
    public static final String ENIGMA_NS = "https://w3id.org/sqo/resource/";

    public final ProvFactory factory;
    public final Namespace ns;
    public Document document;

    public DocumentProv(ProvFactory pFactory) {
        this.factory = pFactory;
        this.document = pFactory.newDocument();
        ns = new Namespace();
        ns.addKnownNamespaces();
        ns.register(DISK_PREFIX, DISK_NS);
        ns.register(JIM_PREFIX, JIM_NS);
        ns.register(PROVBOOK_PREFIX, PROVBOOK_NS);
        ns.register(ENIGMA_PREFIX, ENIGMA_NS);
    }

    public QualifiedName qn(String n) {
        return ns.qualifiedName(PROVBOOK_PREFIX, n, factory);
    }

    
    
    public QualifiedName qn(String n, String prefix) {        return ns.qualifiedName(prefix, n, factory);
    }
    

    public QualifiedName qn(String n, String prefix_iri, String none) {
        String prefix = ns.getNamespaces().get(prefix_iri);
        return ns.qualifiedName(prefix, n, factory);
    }



    public Document makeDocument() {
        Entity quote = factory.newEntity(qn("a-little-provenance-goes-a-long-way"));
        quote.setValue(factory.newValue("A little provenance goes a long way", factory.getName().XSD_STRING));

        Entity original = factory.newEntity(ns.qualifiedName(JIM_PREFIX, "LittleSemanticsWeb.html", factory));

        Agent paul = factory.newAgent(qn("Paul"), "Paul Groth");
        Agent luc = factory.newAgent(qn("Luc"), "Luc Moreau");

        WasAttributedTo attr1 = factory.newWasAttributedTo(null, quote.getId(), paul.getId());
        WasAttributedTo attr2 = factory.newWasAttributedTo(null, quote.getId(), luc.getId());

        WasDerivedFrom wdf = factory.newWasDerivedFrom(quote.getId(), original.getId());

        Document document = factory.newDocument();
        document.getStatementOrBundle().addAll(Arrays.asList(quote, paul, luc, attr1, attr2, original, wdf));
        document.setNamespace(ns);
        return document;
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

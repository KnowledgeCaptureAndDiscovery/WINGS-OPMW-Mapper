package edu.isi.disk.opmm;

import java.io.FileOutputStream;
import java.util.logging.Logger;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.sparql.core.DatasetGraph;

public class OPMM {
    static Logger logger = null;

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String diskTriples = "/home/mosorio/repos/wings-project/DISK-OPMW-Mapper/src/main/resources/sample_data/neuro-disk.nq";
        String tLoisGraphId = "http://localhost:8080/disk-server/admin/tlois";
        String hypothesisGraphId = "http://localhost:8080/disk-server/admin/hypotheses";
        String loisGraphId = "http://localhost:8080/disk-server/admin/lois";
        String questionGraphId = "https://raw.githubusercontent.com/KnowledgeCaptureAndDiscovery/QuestionOntology/main/development/EnigmaQuestions.xml";

        String tloiId = "http://localhost:8080/disk-server/admin/tlois/TriggeredLOI-GKMsFE2EfTt5";
        String hypothesisId = "http://localhost:8080/disk-server/admin/hypotheses/Hypothesis-geEzxPNJ9c8a";
        DatasetGraph diskDataset = ModelUtils.loadDatasetGraph(diskTriples);
        // List all graphs in the dataset
        diskDataset.listGraphNodes().forEachRemaining(System.out::println);
        // Get a graph from the dataset
        Node tloiGraph = NodeFactory.createURI(tLoisGraphId);
        Node tloiNode = NodeFactory.createURI(tloiId);
        Graph graphTLOIGraph = diskDataset.getGraph(tloiGraph);
    
        Mapper mapper = new Mapper(diskDataset, tLoisGraphId, hypothesisGraphId, loisGraphId, questionGraphId);
        mapper.transform(tloiId);
        mapper.opmwModel.write(System.out, "TURTLE"); 
        // write model into a file as turtle
        mapper.opmwModel.write(new FileOutputStream("/home/mosorio/repos/wings-project/DISK-OPMW-Mapper/src/main/resources/sample_data/neuro-opmw.ttl"), Lang.TURTLE.getName());


    }
}
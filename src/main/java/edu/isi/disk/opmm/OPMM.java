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
        String tloiGraphId = "http://localhost:8080/disk-server/admin/tlois";
        String tloiId = "http://localhost:8080/disk-server/admin/tlois/TriggeredLOI-usPnQQPLbwyn";
        DatasetGraph diskDataset = ModelUtils.loadDatasetGraph(diskTriples);
        // Get a graph from the dataset
        Node graphNode = NodeFactory.createURI(tloiGraphId);
        Node tloiNode = NodeFactory.createURI(tloiId);
        Graph graphTLOIGraph = diskDataset.getGraph(graphNode);
    
        Mapper mapper = new Mapper();
        mapper.mapTriggerLineOfInquiry(graphTLOIGraph, tloiNode);
        
        mapper.opmwModel.write(System.out, "TURTLE"); 

        // write model into a file as turtle
        mapper.opmwModel.write(new FileOutputStream("/home/mosorio/repos/wings-project/DISK-OPMW-Mapper/src/main/resources/sample_data/neuro-opmw.ttl"), Lang.TURTLE.getName());


    }
}
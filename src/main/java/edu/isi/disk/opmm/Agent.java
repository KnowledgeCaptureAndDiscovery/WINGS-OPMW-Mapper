package edu.isi.disk.opmm;
import java.util.Date;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.semanticweb.owlapi.model.IRI;

public class Agent {
    
    public OntModel model;
    public IRI id;
    public String label;
    public String description;
    String type = "http://www.w3.org/ns/prov#Agent";
    public String comment;

    public Entity actedOnBehalfOf;
    public Entity atLocation;
    public Entity qualifiedDelegation;
    public Entity qualifiedInfluence;
    public Entity wasAssociatedWith;
    public Entity wasAttributedTo;
    public Entity wasInfluencedBy;


    public Agent(IRI id, String label, String comment) {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.id = id;
        this.label = label;
        this.comment = comment;
    }

    public Agent(String id, String label, String comment) {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.id = IRI.create(id);
        this.label = label;
        this.comment = comment;
    }
}

package edu.isi.disk.opmm;

import java.util.Date;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.semanticweb.owlapi.model.IRI;

public class Activity {
    /**
     * Class that represents an Activity in the PROV model
     * 
     **/
    public OntModel model;

    public IRI id;
    public String label;
    public String description;
    public String type = "http://www.w3.org/ns/prov#Agent";
    public String comment;

    public Entity atLocation;
    public Date endedAtTime;
    public Date startedAtTime;

    public Entity generated;
    public Entity hadActivity;
    public Entity invalidated;
    public Entity qualifiedAssociation;
    public Entity qualifiedCommunication;
    public Entity qualifiedEnd;
    public Entity qualifiedInfluence;
    public Entity qualifiedStart;
    public Entity qualifiedUsage;

    public Entity used;
    public Entity wasAssociatedWith;
    public Entity wasEndedBy;
    public Entity wasGeneratedBy;
    public Entity wasInfluencedBy;
    public Entity wasInformedBy;
    public Entity wasInvalidatedBy;
    public Entity wasStartedBy;

    public Activity(IRI id, String label, String comment) {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.id = id;
        this.label = label;
        this.comment = comment;
    }

    public Activity(String id, String label, String comment) {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.id = IRI.create(id);
        this.label = label;
        this.comment = comment;
    }

}

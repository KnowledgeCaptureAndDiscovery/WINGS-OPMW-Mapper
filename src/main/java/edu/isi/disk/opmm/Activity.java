package edu.isi.disk.opmm;

import java.util.Date;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
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

    public Individual atLocation;
    public Date endedAtTime;
    public Date startedAtTime;

    public Individual generated;
    public Individual hadActivity;
    public Individual invalidated;
    public Individual qualifiedAssociation;
    public Individual qualifiedCommunication;
    public Individual qualifiedEnd;
    public Individual qualifiedInfluence;
    public Individual qualifiedStart;
    public Individual qualifiedUsage;

    public Individual used;
    public Individual wasAssociatedWith;
    public Individual wasEndedBy;
    public Individual wasGeneratedBy;
    public Individual wasInfluencedBy;
    public Individual wasInformedBy;
    public Individual wasInvalidatedBy;
    public Individual wasStartedBy;

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

    public OntModel populateModel() {

        String idString = id.toString();
        Resource resource = model.createResource(idString);
        // Add type
        resource.addProperty(model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                model.createResource(type));
        // Add label
        if (label != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/2000/01/rdf-schema#label"), label);
        }

        if (atLocation != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#atLocation"), atLocation);
        }

        if (endedAtTime != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#endedAtTime"), endedAtTime.toString());
        }

        if (startedAtTime != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#startedAtTime"),
                    startedAtTime.toString());
        }

        if (generated != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#generated"), generated);
        }

        if (hadActivity != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#hadActivity"), hadActivity);
        }

        if (invalidated != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#invalidated"), invalidated);
        }

        if (qualifiedAssociation != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#qualifiedAssociation"),
                    qualifiedAssociation);
        }

        if (qualifiedCommunication != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#qualifiedCommunication"),
                    qualifiedCommunication);
        }

        if (qualifiedEnd != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#qualifiedEnd"), qualifiedEnd);
        }

        if (qualifiedInfluence != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#qualifiedInfluence"), qualifiedInfluence);
        }

        if (qualifiedStart != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#qualifiedStart"), qualifiedStart);
        }

        if (qualifiedUsage != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#qualifiedUsage"), qualifiedUsage);
        }

        if (used != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#used"), used);
        }

        if (wasAssociatedWith != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasAssociatedWith"), wasAssociatedWith);
        }

        if (wasEndedBy != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasEndedBy"), wasEndedBy);
        }

        if (wasGeneratedBy != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasGeneratedBy"), wasGeneratedBy);
        }

        if (wasInfluencedBy != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasInfluencedBy"), wasInfluencedBy);
        }

        if (wasInformedBy != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasInformedBy"), wasInformedBy);
        }

        if (wasInvalidatedBy != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasInvalidatedBy"), wasInvalidatedBy);
        }

        if (wasStartedBy != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasStartedBy"), wasStartedBy);
        }

        return model;
        
    }

}

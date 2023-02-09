package edu.isi.disk.opmm;

import java.util.Date;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.semanticweb.owlapi.model.IRI;

public class Agent {

    public OntModel model;
    public IRI id;
    public String label;
    public String description;
    String type = "http://www.w3.org/ns/prov#Agent";
    public String comment;

    public Individual actedOnBehalfOf;
    public Individual atLocation;
    public Individual qualifiedDelegation;
    public Individual qualifiedInfluence;
    public Individual wasAssociatedWith;
    public Individual wasAttributedTo;
    public Individual wasInfluencedBy;

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

        if (actedOnBehalfOf != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#actedOnBehalfOf"), actedOnBehalfOf);
        }
        if (atLocation != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#atLocation"), atLocation);
        }

        if (qualifiedDelegation != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#qualifiedDelegation"),
                    qualifiedDelegation);
        }
        if (qualifiedInfluence != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#qualifiedInfluence"), qualifiedInfluence);
        }
        if (wasAssociatedWith != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasAssociatedWith"), wasAssociatedWith);
        }
        if (wasAttributedTo != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasAttributedTo"), wasAttributedTo);
        }
        if (wasInfluencedBy != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasInfluencedBy"), wasInfluencedBy);
        }

        return model;

    }
}

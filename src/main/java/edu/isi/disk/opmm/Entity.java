package edu.isi.disk.opmm;

import java.util.Date;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.semanticweb.owlapi.model.IRI;

public class Entity {
    OntModel model;
    IRI id;
    String label;
    String comment;
    String type = "http://www.w3.org/ns/prov#Entity";
    Individual alternateOf;
    Individual atLocation;
    Individual generated;
    Date generatedAtTime;
    Individual hadMember;
    Individual hadPrimarySource;
    Individual invalidated;
    Date invalidatedAtTime;
    Individual qualifiedAttribution;
    Individual qualifiedDerivation;
    Individual qualifiedGeneration;
    Individual qualifiedInfluence;
    Individual qualifiedInvalidation;
    Individual qualifiedPrimarySource;
    Individual qualifiedQuotation;
    Individual qualifiedRevision;
    Individual specializationOf;
    Individual used;
    Individual wasAttributedTo;
    Individual wasDerivedFrom;
    Individual wasEndedBy;
    Individual wasGeneratedBy;
    Individual wasInfluencedBy;
    Individual wasInvalidatedBy;
    Individual wasQuotedFrom;
    Individual wasRevisionOf;
    Individual wasStartedBy;

    public Entity(IRI id, String label, String comment) {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.id = id;
        this.label = label;
        this.comment = comment;
    }

    public Entity(String id, String label, String comment) {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.id = IRI.create(id);
        this.label = label;
        this.comment = comment;
    }

    // Constructors
    // Default constructor with all fields as null
    public Entity() {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    }

    public OntModel getModel() {
        String idString = id.toString();
        Resource resource = model.createResource(idString);
        // Add type
        resource.addProperty(model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                model.createResource(type));
        // Add label
        if (label != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/2000/01/rdf-schema#label"), label);
        }

        if (alternateOf != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#alternateOf"), alternateOf);
        }
        if (atLocation != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#atLocation"), atLocation);
        }
        if (generated != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#generated"), generated);
        }
        if (generatedAtTime != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#generatedAtTime"),
                    model.createTypedLiteral(generatedAtTime));
        }
        if (hadMember != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#hadMember"), hadMember);
        }
        if (hadPrimarySource != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#hadPrimarySource"), hadPrimarySource);
        }
        if (invalidated != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#invalidated"), invalidated);
        }
        if (invalidatedAtTime != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#invalidatedAtTime"),
                    model.createTypedLiteral(invalidatedAtTime));
        }
        if (qualifiedAttribution != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#qualifiedAttribution"),
                    qualifiedAttribution);
        }

        return model;
    }

    public void setAlternateOf(Individual alternateOf) {
        this.alternateOf = alternateOf;
    }

    public void setAtLocation(Individual atLocation) {
        this.atLocation = atLocation;
    }

    public void setGenerated(Individual generated) {
        this.generated = generated;
    }

    public void setGeneratedAtTime(Date generatedAtTime) {
        this.generatedAtTime = generatedAtTime;
    }

    public void setHadMember(Individual hadMember) {
        this.hadMember = hadMember;
    }

    public void setHadPrimarySource(Individual hadPrimarySource) {
        this.hadPrimarySource = hadPrimarySource;
    }

    public void setInvalidated(Individual invalidated) {
        this.invalidated = invalidated;
    }

    public void setInvalidatedAtTime(Date invalidatedAtTime) {
        this.invalidatedAtTime = invalidatedAtTime;
    }

    public void setQualifiedAttribution(Individual qualifiedAttribution) {
        this.qualifiedAttribution = qualifiedAttribution;
    }

    public void setQualifiedDerivation(Individual qualifiedDerivation) {
        this.qualifiedDerivation = qualifiedDerivation;
    }

    public void setQualifiedGeneration(Individual qualifiedGeneration) {
        this.qualifiedGeneration = qualifiedGeneration;
    }

    public void setQualifiedInfluence(Individual qualifiedInfluence) {
        this.qualifiedInfluence = qualifiedInfluence;
    }

    public void setQualifiedInvalidation(Individual qualifiedInvalidation) {
        this.qualifiedInvalidation = qualifiedInvalidation;
    }

    public void setQualifiedPrimarySource(Individual qualifiedPrimarySource) {
        this.qualifiedPrimarySource = qualifiedPrimarySource;
    }

    public void setQualifiedQuotation(Individual qualifiedQuotation) {
        this.qualifiedQuotation = qualifiedQuotation;
    }

    public void setQualifiedRevision(Individual qualifiedRevision) {
        this.qualifiedRevision = qualifiedRevision;
    }

    public void setSpecializationOf(Individual specializationOf) {
        this.specializationOf = specializationOf;
    }

    public void setUsed(Individual used) {
        this.used = used;
    }

    public void setWasAttributedTo(Individual wasAttributedTo) {
        this.wasAttributedTo = wasAttributedTo;
    }

    public void setWasDerivedFrom(Individual wasDerivedFrom) {
        this.wasDerivedFrom = wasDerivedFrom;
    }

    public void setWasEndedBy(Individual wasEndedBy) {
        this.wasEndedBy = wasEndedBy;
    }

    public void setWasGeneratedBy(Individual wasGeneratedBy) {
        this.wasGeneratedBy = wasGeneratedBy;
    }

    public void setWasInfluencedBy(Individual wasInfluencedBy) {
        this.wasInfluencedBy = wasInfluencedBy;
    }

    public void setWasInvalidatedBy(Individual wasInvalidatedBy) {
        this.wasInvalidatedBy = wasInvalidatedBy;
    }

    public void setWasQuotedFrom(Individual wasQuotedFrom) {
        this.wasQuotedFrom = wasQuotedFrom;
    }

    public void setWasRevisionOf(Individual wasRevisionOf) {
        this.wasRevisionOf = wasRevisionOf;
    }

    public void setWasStartedBy(Individual wasStartedBy) {
        this.wasStartedBy = wasStartedBy;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = IRI.create(id);
    }

}

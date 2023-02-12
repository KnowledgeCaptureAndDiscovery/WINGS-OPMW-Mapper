package edu.isi.disk.opmm;

import java.util.Date;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.semanticweb.owlapi.model.IRI;

public class Entity {
    OntModel model;
    Resource resource;
    IRI id;
    String label;
    String comment;
    String type = "http://www.w3.org/ns/prov#Entity";
    Resource alternateOf;
    Resource atLocation;
    Resource generated;
    Date generatedAtTime;
    Resource hadMember;
    Resource hadPrimarySource;
    Resource invalidated;
    Date invalidatedAtTime;
    Resource qualifiedAttribution;
    Resource qualifiedDerivation;
    Resource qualifiedGeneration;
    Resource qualifiedInfluence;
    Resource qualifiedInvalidation;
    Resource qualifiedPrimarySource;
    Resource qualifiedQuotation;
    Resource qualifiedRevision;
    Resource specializationOf;
    Resource used;
    Resource wasAttributedTo;
    Resource wasDerivedFrom;
    Resource wasEndedBy;
    Resource wasGeneratedBy;
    Resource wasInfluencedBy;
    Resource wasInvalidatedBy;
    Resource wasQuotedFrom;
    Resource wasRevisionOf;
    Resource wasStartedBy;

    public Entity(IRI id, String label, String comment) {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.id = id;
        this.label = label;
        this.comment = comment;
        this.resource = model.createResource(id.toString());
        resource.addProperty(model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                model.createResource(type));
    }

    public Entity(String id, String label, String comment) {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.id = IRI.create(id);
        this.label = label;
        this.comment = comment;
        this.resource = model.createResource(id.toString());
        resource.addProperty(model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                model.createResource(type));
    }

    public Entity(String id, String label, String comment, String localType) {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.id = IRI.create(id);
        this.label = label;
        this.comment = comment;
        this.resource = model.createResource(id.toString());
        resource.addProperty(model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                model.createResource("http://www.w3.org/ns/prov#" + localType));
        resource.addProperty(model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                model.createResource(type));
    }

    // Constructors
    // Default constructor with all fields as null
    public Entity() {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    }

    public OntModel getModel() {
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
        
        if (qualifiedDerivation != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#qualifiedDerivation"),
                    qualifiedDerivation);
        }
        if (qualifiedGeneration != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#qualifiedGeneration"),
                    qualifiedGeneration);
        }
        if (qualifiedInfluence != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#qualifiedInfluence"),
                    qualifiedInfluence);
        }
        if (qualifiedInvalidation != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#qualifiedInvalidation"),
                    qualifiedInvalidation);
        }
        if (qualifiedPrimarySource != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#qualifiedPrimarySource"),
                    qualifiedPrimarySource);
        }
        if (qualifiedQuotation != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#qualifiedQuotation"),
                    qualifiedQuotation);
        }
        if (qualifiedRevision != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#qualifiedRevision"),
                    qualifiedRevision);
        }
        if (specializationOf != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#specializationOf"),
                    specializationOf);
        }
        if (used != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#used"), used);
        }
        if (wasAttributedTo != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasAttributedTo"),
                    wasAttributedTo);
        }
        if (wasDerivedFrom != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasDerivedFrom"),
                    wasDerivedFrom);
        }
        if (wasEndedBy != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasEndedBy"), wasEndedBy);
        }
        if (wasGeneratedBy != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasGeneratedBy"),
                    wasGeneratedBy);
        }
        if (wasInfluencedBy != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasInfluencedBy"),
                    wasInfluencedBy);
        }
        if (wasInvalidatedBy != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasInvalidatedBy"),
                    wasInvalidatedBy);
        }
        if (wasQuotedFrom != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasQuotedFrom"),
                    wasQuotedFrom);
        }
        if (wasRevisionOf != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasRevisionOf"),
                    wasRevisionOf);
        }
        if (wasStartedBy != null) {
            resource.addProperty(model.getProperty("http://www.w3.org/ns/prov#wasStartedBy"),
                    wasStartedBy);
        }


        return model;
    }

    public void setAlternateOf(Resource alternateOf) {
        this.alternateOf = alternateOf;
    }

    public void setAtLocation(Resource atLocation) {
        this.atLocation = atLocation;
    }

    public void setGenerated(Resource generated) {
        this.generated = generated;
    }

    public void setGeneratedAtTime(Date generatedAtTime) {
        this.generatedAtTime = generatedAtTime;
    }

    public void setHadMember(Resource hadMember) {
        this.hadMember = hadMember;
    }

    public void setHadPrimarySource(Resource hadPrimarySource) {
        this.hadPrimarySource = hadPrimarySource;
    }

    public void setInvalidated(Resource invalidated) {
        this.invalidated = invalidated;
    }

    public void setInvalidatedAtTime(Date invalidatedAtTime) {
        this.invalidatedAtTime = invalidatedAtTime;
    }

    public void setQualifiedAttribution(Resource qualifiedAttribution) {
        this.qualifiedAttribution = qualifiedAttribution;
    }

    public void setQualifiedDerivation(Resource qualifiedDerivation) {
        this.qualifiedDerivation = qualifiedDerivation;
    }

    public void setQualifiedGeneration(Resource qualifiedGeneration) {
        this.qualifiedGeneration = qualifiedGeneration;
    }

    public void setQualifiedInfluence(Resource qualifiedInfluence) {
        this.qualifiedInfluence = qualifiedInfluence;
    }

    public void setQualifiedInvalidation(Resource qualifiedInvalidation) {
        this.qualifiedInvalidation = qualifiedInvalidation;
    }

    public void setQualifiedPrimarySource(Resource qualifiedPrimarySource) {
        this.qualifiedPrimarySource = qualifiedPrimarySource;
    }

    public void setQualifiedQuotation(Resource qualifiedQuotation) {
        this.qualifiedQuotation = qualifiedQuotation;
    }

    public void setQualifiedRevision(Resource qualifiedRevision) {
        this.qualifiedRevision = qualifiedRevision;
    }

    public void setSpecializationOf(Resource specializationOf) {
        this.specializationOf = specializationOf;
    }

    public void setUsed(Resource used) {
        this.used = used;
    }

    public void setWasAttributedTo(Resource wasAttributedTo) {
        this.wasAttributedTo = wasAttributedTo;
    }

    public void setWasDerivedFrom(Resource wasDerivedFrom) {
        this.wasDerivedFrom = wasDerivedFrom;
    }

    public void setWasEndedBy(Resource wasEndedBy) {
        this.wasEndedBy = wasEndedBy;
    }

    public void setWasGeneratedBy(Resource wasGeneratedBy) {
        this.wasGeneratedBy = wasGeneratedBy;
    }

    public void setWasInfluencedBy(Resource wasInfluencedBy) {
        this.wasInfluencedBy = wasInfluencedBy;
    }

    public void setWasInvalidatedBy(Resource wasInvalidatedBy) {
        this.wasInvalidatedBy = wasInvalidatedBy;
    }

    public void setWasQuotedFrom(Resource wasQuotedFrom) {
        this.wasQuotedFrom = wasQuotedFrom;
    }

    public void setWasRevisionOf(Resource wasRevisionOf) {
        this.wasRevisionOf = wasRevisionOf;
    }

    public void setWasStartedBy(Resource wasStartedBy) {
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

    public Resource getResource() {
        return resource;
    }

}

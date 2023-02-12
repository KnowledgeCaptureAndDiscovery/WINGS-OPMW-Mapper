package edu.isi.disk.opmm;

import java.util.Date;

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
    public Resource resource;
    public String label;
    public String description;
    public String type = "http://www.w3.org/ns/prov#Activity";
    public String comment;

    public Resource atLocation;
    public Date endedAtTime;
    public Date startedAtTime;

    public Resource generated;
    public Resource hadActivity;
    public Resource invalidated;
    public Resource qualifiedAssociation;
    public Resource qualifiedCommunication;
    public Resource qualifiedEnd;
    public Resource qualifiedInfluence;
    public Resource qualifiedStart;
    public Resource qualifiedUsage;

    public Resource used;
    public Resource wasAssociatedWith;
    public Resource wasEndedBy;
    public Resource wasGeneratedBy;
    public Resource wasInfluencedBy;
    public Resource wasInformedBy;
    public Resource wasInvalidatedBy;
    public Resource wasStartedBy;

    public Activity(IRI id, String label, String comment) {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.id = id;
        this.label = label;
        this.comment = comment;
        resource = model.createResource(id.toString());
        resource.addProperty(model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                model.createResource(type));
    }

    public Activity(String id, String label, String comment) {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.id = IRI.create(id);
        this.label = label;
        this.comment = comment;
        resource = model.createResource(id);
        resource.addProperty(model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                model.createResource(type));
    }

    public OntModel getModel() {
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

    public void setModel(OntModel model) {
        this.model = model;
    }

    public void setId(IRI id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setAtLocation(Resource atLocation) {
        this.atLocation = atLocation;
    }

    public void setEndedAtTime(Date endedAtTime) {
        this.endedAtTime = endedAtTime;
    }

    public void setStartedAtTime(Date startedAtTime) {
        this.startedAtTime = startedAtTime;
    }

    public void setGenerated(Resource generated) {
        this.generated = generated;
    }

    public void setHadActivity(Resource hadActivity) {
        this.hadActivity = hadActivity;
    }

    public void setInvalidated(Resource invalidated) {
        this.invalidated = invalidated;
    }

    public void setQualifiedAssociation(Resource qualifiedAssociation) {
        this.qualifiedAssociation = qualifiedAssociation;
    }

    public void setQualifiedCommunication(Resource qualifiedCommunication) {
        this.qualifiedCommunication = qualifiedCommunication;
    }

    public void setQualifiedEnd(Resource qualifiedEnd) {
        this.qualifiedEnd = qualifiedEnd;
    }

    public void setQualifiedInfluence(Resource qualifiedInfluence) {
        this.qualifiedInfluence = qualifiedInfluence;
    }

    public void setQualifiedStart(Resource qualifiedStart) {
        this.qualifiedStart = qualifiedStart;
    }

    public void setQualifiedUsage(Resource qualifiedUsage) {
        this.qualifiedUsage = qualifiedUsage;
    }

    public void setUsed(Resource used) {
        this.used = used;
    }

    public void setWasAssociatedWith(Resource wasAssociatedWith) {
        this.wasAssociatedWith = wasAssociatedWith;
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

    public void setWasInformedBy(Resource wasInformedBy) {
        this.wasInformedBy = wasInformedBy;
    }

    public void setWasInvalidatedBy(Resource wasInvalidatedBy) {
        this.wasInvalidatedBy = wasInvalidatedBy;
    }

    public void setWasStartedBy(Resource wasStartedBy) {
        this.wasStartedBy = wasStartedBy;
    }

    public Resource getResource() {
        return this.resource;
    }

}

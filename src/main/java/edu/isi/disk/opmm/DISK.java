package edu.isi.disk.opmm;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

public class DISK {
    public static final String uri = "http://disk-project.org/ontology/disk#";
    private static final Model m = ModelFactory.createDefaultModel();

    /**
     * Data Properties
     */
    public static final Property dateCreated = m.createProperty(uri, "dateCreated");
    public static final Property dateModified = m.createProperty(uri, "dateModified");
    public static final Property hasBindingValue = m.createProperty(uri, "hasBindingValue");
    public static final Property hasConfidenceType = m.createProperty(uri, "hasConfidenceType");
    public static final Property hasConfidenceValue = m.createProperty(uri, "hasConfidenceValue");
    public static final Property hasDataQuery = m.createProperty(uri, "hasDataQuery");
    public static final Property hasDataQueryDescription = m.createProperty(uri, "hasDataQueryDescription");
    public static final Property hasDataSource = m.createProperty(uri, "hasDataSource");
    public static final Property hasEmail = m.createProperty(uri, "hasEmail");
    public static final Property hasHypothesisQuery = m.createProperty(uri, "hasHypothesisQuery");
    public static final Property hasId = m.createProperty(uri, "hasId");
    public static final Property hasName = m.createProperty(uri, "hasName");
    public static final Property hasQueryDescription = m.createProperty(uri, "hasQueryDescription");
    // IRI: http://disk-project.org/ontology/disk#hasRunEndDate
    public static final Property hasRunEndDate = m.createProperty(uri, "hasRunEndDate");
    // IRI: http://disk-project.org/ontology/disk#hasRunLink
    public static final Property hasRunLink = m.createProperty(uri, "hasRunLink");
    // IRI: http://disk-project.org/ontology/disk#hasRunStartDate
    public static final Property hasRunStartDate = m.createProperty(uri, "hasRunStartDate");
    // IRI: http://disk-project.org/ontology/disk#hasRunStatus
    public static final Property hasRunStatus = m.createProperty(uri, "hasRunStatus");
    // IRI: http://disk-project.org/ontology/disk#hasSource
    public static final Property hasSource = m.createProperty(uri, "hasSource");
    // IRI: http://disk-project.org/ontology/disk#hasTableDescription
    public static final Property hasTableDescription = m.createProperty(uri, "hasTableDescription");
    // IRI: http://disk-project.org/ontology/disk#hasTableVariables
    public static final Property hasTableVariables = m.createProperty(uri, "hasTableVariables");
    // IRI: http://disk-project.org/ontology/disk#hasTriggeredLineOfInquiryStatus
    public static final Property hasTriggeredLineOfInquiryStatus = m.createProperty(uri,
            "hasTriggeredLineOfInquiryStatus");
    // IRI: http://disk-project.org/ontology/disk#hasUsageNotes
    public static final Property hasUsageNotes = m.createProperty(uri, "hasUsageNotes");
    // public static final Property hasVariable = m.createProperty(uri ,
    // "hasVariable");

    // public static final Property hasLineOfInquiry = m.createProperty(uri ,
    // "hasLineOfInquiry");
    // public static final Property hasParentHypothesis = m.createProperty(uri ,
    // "hasParentHypothesis");
    // public static final Property hasMetaWorkflowBinding = m.createProperty(uri ,
    // "hasMetaWorkflowBinding");

    // public static final Property hasTriggeredLineOfInquiryStatus = m
    // .createProperty(uri , "hasTriggeredLineOfInquiryStatus");

}
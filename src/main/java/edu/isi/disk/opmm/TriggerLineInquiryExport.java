package edu.isi.disk.opmm;

import java.util.concurrent.locks.StampedLock;

import org.apache.jena.base.Sys;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VCARD;

public class TriggerLineInquiryExport {

    public final String NQUADS = "Nquads";
    public final Model model;
    private OntModel opmwModel;
    private final Dataset dataset;
    public Resource tloiResource;
    public String tloiId;
    // private final OntModel opmwModel;

    public Dataset getDataset() {
        return dataset;
    }

    public TriggerLineInquiryExport(String datasetFilePath, String tloiGraphName, String tloiId) {
        this.dataset = RDFDataMgr.loadDataset(datasetFilePath, Lang.NQUADS);
        this.model = dataset.getNamedModel(tloiGraphName);
        this.opmwModel = ModelUtils.initializeModel(opmwModel);
        this.tloiResource = this.model.getResource(tloiId);
        this.tloiId = tloiId;
    }

    public void convertObjectProperties(){
        
    }    

    public void convertDataProperties() {
        Statement dateCreated = this.model.getProperty(this.tloiResource, DISK.dateCreated);
        Statement dateModified = this.model.getProperty(this.tloiResource, DISK.dateModified);
        Statement hasBindingValue = this.model.getProperty(this.tloiResource, DISK.hasBindingValue);
        Statement hasConfidenceType = this.model.getProperty(this.tloiResource, DISK.hasConfidenceType);
        Statement hasConfidenceValue = this.model.getProperty(this.tloiResource, DISK.hasConfidenceValue);
        Statement hasDataQuery = this.model.getProperty(this.tloiResource, DISK.hasDataQuery);
        Statement hasDataQueryDescription = this.model.getProperty(this.tloiResource, DISK.hasDataQueryDescription);
        Statement hasDataSource = this.model.getProperty(this.tloiResource, DISK.hasDataSource);
        Statement hasEmail = this.model.getProperty(this.tloiResource, DISK.hasEmail);
        Statement hasHypothesisQuery = this.model.getProperty(this.tloiResource, DISK.hasHypothesisQuery);
        Statement hasId = this.model.getProperty(this.tloiResource, DISK.hasId);
        Statement hasName = this.model.getProperty(this.tloiResource, DISK.hasName);
        Statement hasQueryDescription = this.model.getProperty(this.tloiResource, DISK.hasQueryDescription);
        Statement hasRunEndDate = this.model.getProperty(this.tloiResource, DISK.hasRunEndDate);
        Statement hasRunLink = this.model.getProperty(this.tloiResource, DISK.hasRunLink);
        Statement hasRunStartDate = this.model.getProperty(this.tloiResource, DISK.hasRunStartDate);
        Statement hasRunStatus = this.model.getProperty(this.tloiResource, DISK.hasRunStatus);
        Statement hasSource = this.model.getProperty(this.tloiResource, DISK.hasSource);
        Statement hasTableDescription = this.model.getProperty(this.tloiResource, DISK.hasTableDescription);
        Statement hasTableVariables = this.model.getProperty(this.tloiResource, DISK.hasTableVariables);
        Statement hasTriggeredLineOfInquiryStatus = this.model.getProperty(this.tloiResource,
                DISK.hasTriggeredLineOfInquiryStatus);
        Statement hasUsageNotes = this.model.getProperty(this.tloiResource, DISK.hasUsageNotes);

        // print all previous variables
        System.out.println("dateCreated: " + dateCreated);
        System.out.println("dateModified: " + dateModified);
        System.out.println("hasBindingValue: " + hasBindingValue);
        System.out.println("hasConfidenceType: " + hasConfidenceType);
        System.out.println("hasConfidenceValue: " + hasConfidenceValue);
        System.out.println("hasDataQuery: " + hasDataQuery);
        System.out.println("hasDataQueryDescription: " + hasDataQueryDescription);
        System.out.println("hasDataSource: " + hasDataSource);
        System.out.println("hasEmail: " + hasEmail);
        System.out.println("hasHypothesisQuery: " + hasHypothesisQuery);
        System.out.println("hasId: " + hasId);
        System.out.println("hasName: " + hasName);
        System.out.println("hasQueryDescription: " + hasQueryDescription);
        System.out.println("hasRunEndDate: " + hasRunEndDate);
        System.out.println("hasRunLink: " + hasRunLink);
        System.out.println("hasRunStartDate: " + hasRunStartDate);
        System.out.println("hasRunStatus: " + hasRunStatus);
        System.out.println("hasSource: " + hasSource);
        System.out.println("hasTableDescription: " + hasTableDescription);
        System.out.println("hasTableVariables: " + hasTableVariables);
        System.out.println("hasTriggeredLineOfInquiryStatus: " + hasTriggeredLineOfInquiryStatus);
        System.out.println("hasUsageNotes: " + hasUsageNotes);

    }

}

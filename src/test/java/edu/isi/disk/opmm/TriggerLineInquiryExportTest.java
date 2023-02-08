package edu.isi.disk.opmm;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.junit.Test;

public class TriggerLineInquiryExportTest {
    @Test
    public void testExportOPMW() {
        String triggerLineInquiryFilePath = "/home/mosorio/repos/wings-project/DISK-OPMW-Mapper/src/main/resources/sample_data/neuro-disk.nquads";
        String tloiId = "http://localhost:8080/disk-server/admin/tlois/TriggeredLOI-kIlPvsC0WlIO";
        String tloiGraph = "http://localhost:8080/disk-server/admin/tlois";
        TriggerLineInquiryExport t = new TriggerLineInquiryExport(triggerLineInquiryFilePath, tloiGraph, tloiId);
        t.convertDataProperties();
    }

}

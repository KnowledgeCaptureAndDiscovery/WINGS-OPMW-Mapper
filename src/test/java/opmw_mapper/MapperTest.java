/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opmw_mapper;


import edu.isi.wings.opmm.Mapper;
import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Daniel Garijo
 */
public class MapperTest {
    
    /**
     * Test of transformWINGSElaboratedTemplateToOPM method, of class Mapper.
     */
    @Test
    public void testTransformWINGSElaboratedTemplateToOPM() {
        System.out.println("Transform a template to OPMW and PROV ");
//        String template = "src\\main\\resources\\sample_data\\new\\abs_words\\template.owl";
        String mode = "RDF/XML";
        String outFile = "testTemplate";
        Mapper instance = new Mapper();
        //template with abstract nodes: abstractWords
//        System.out.println("Transforming an abstract template...");
//        assertNotSame("",instance.transformWINGSElaboratedTemplateToOPMW(template, mode, outFile));
        //template with collections
        System.out.println("Transforming a template with collections");
        String template = "src\\main\\resources\\sample_data\\new\\aquaflow_ntm\\Template.owl";
        String taxonomy_export="TestingDomain1\\Component\\TestingDomain_TaxonomyHierarchyModel.owl";
        String componentDirectory = "src\\main\\resources\\sample_data\\new\\aquaflow_ntm\\components\\";
        assertNotSame("",instance.transformWINGSElaboratedTemplateToOPMW(template, mode, outFile,taxonomy_export,"TURTLE", componentDirectory,"Water"));
        //delete output files.
        //File f = new File(outFile);
        //f.delete();
    }
    
//    @Test
//    public void testTransformWINGSElaboratedTemplateToOPM2() {
//        System.out.println("Transform a template to OPMW and PROV ");
//        String mode = "RDF/XML";
//        String outFile = "testTemplate";
//        Mapper instance = new Mapper();
//        //template with abstract nodes: abstractWords
////        System.out.println("Transforming an abstract template...");
////        assertNotSame("",instance.transformWINGSElaboratedTemplateToOPMW(template, mode, outFile));
//        //template with collections
//        System.out.println("Transforming a template with collections");
//        String template = "src\\main\\resources\\sample_data\\new\\abs_words\\template.owl";
//        String taxonomy_export="TestingDomain1\\Component\\TestingDomain_TaxonomyHierarchyModel.owl";
//        String componentDirectory = "src\\main\\resources\\sample_data\\new\\abs_words\\components\\";
//        assertNotSame("",instance.transformWINGSElaboratedTemplateToOPMW(template, mode, outFile,taxonomy_export,"TURTLE", componentDirectory,"Water"));
//        //delete output files.
//        //File f = new File(outFile);
//        //f.delete();
//    }
    
    @Test
    public void testtransformWINGSResultsToOPMW(){
        System.out.println("Transform an execution to OPMW and PROV ");
        Mapper instance = new Mapper();
        String mode = "RDF/XML";
        String outFileOPMW = "testResult";
        String outFilePROV = "testResult2";        
        System.out.println("Transforming an execution of a template with collections...");
        String lib="src\\main\\resources\\sample_data\\new\\aquaflow_ntm\\Library.owl";
        String execution="src\\main\\resources\\sample_data\\new\\aquaflow_ntm\\Execution.owl";
        String data_catalog="TestingDomain1\\Data\\TestingDomain_DataCatalog.owl";
        String data_URI="src\\main\\resources\\sample_data\\new\\aquaflow_ntm\\data\\";
        
        assertNotSame("",instance.transformWINGSResultsToOPMW(execution, lib, mode, outFileOPMW, outFilePROV,null, data_catalog, "TURTLE",data_URI,"Water"));
        //delete output files
        File f = new File(outFileOPMW);
        File f2 = new File(outFilePROV);
        //f.delete();
        //f2.delete();
    }
    
//        @Test
//    public void testtransformWINGSResultsToOPMW(){
//        System.out.println("Transform an execution to OPMW and PROV ");
//        Mapper instance = new Mapper();
//        String mode = "RDF/XML";
//        String outFileOPMW = "testResult";
//        String outFilePROV = "testResult2";        
//        //template with abstract nodes:
////        System.out.println("Transforming an execution of an abstract template...");
////        assertNotSame("",instance.transformWINGSResultsToOPMW(execution, lib, mode, outFileOPMW, outFilePROV));
//        //template with collections
//        System.out.println("Transforming an execution of a template with collections...");
//        String lib="src\\main\\resources\\sample_data\\new\\abs_words\\Library.owl";
//        String execution="src\\main\\resources\\sample_data\\new\\abs_words\\Execution.owl";
//        String data_catalog="TestingDomain1\\Data\\TestingDomain_DataCatalog.owl";
//        String data_URI="src\\main\\resources\\sample_data\\new\\abs_words\\data\\";
//        
//        assertNotSame("",instance.transformWINGSResultsToOPMW(execution, lib, mode, outFileOPMW, outFilePROV,null, data_catalog, "TURTLE",data_URI,"CompareFiles"));
//        //delete output files
//        File f = new File(outFileOPMW);
//        File f2 = new File(outFilePROV);
//        //f.delete();
//        //f2.delete();
//    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opmw_mapper;


import edu.isi.wings.opmm.Mapper;
import java.io.File;
import org.apache.jena.ontology.OntModel;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class performs different tests with different templates and executions
 * @author Daniel Garijo
 */
public class MapperTest {
    
    /**
     * Test of transformWINGSElaboratedTemplateToOPM method, of class Mapper.
     * This test runs on the Aquaflow NTM_EDM template
     */
    @Test
    public void testTransformWINGSElaboratedTemplateToOPM_TempltateWithCollections() {
        System.out.println("Transform a template to OPMW and PROV ");
        String mode = "RDF/XML";
        String outFile = "template_AquaflowNTM_EDM";
        Mapper instance = new Mapper();
        System.out.println("Transforming a template with collections: Aquaflow_NTM_EDM");
        String template = "src"+File.separator+"main"+File.separator+"resources"+File.separator+"sample_data"+File.separator+"new"+File.separator+"aquaflow_ntm"+File.separator+"Template.owl";
        String taxonomy_export="TestingDomain1"+File.separator+"Component"+File.separator+"TestingDomain_TaxonomyHierarchyModel.owl";
        String componentDirectory = "src"+File.separator+"main"+File.separator+"resources"+File.separator+"sample_data"+File.separator+"new"+File.separator+"aquaflow_ntm"+File.separator+"components"+File.separator+"";
        assertNotSame("",instance.transformWINGSElaboratedTemplateToOPMW(template, mode, outFile,taxonomy_export,"TURTLE", componentDirectory,"Water"));
        validateModel(instance.getOPMWModel());
        //delete output files.
        //File f = new File(outFile);
        //f.delete();
    }
    
//    @Test
//    public void testTransformWINGSElaboratedTemplateToOPM_AbstractAndExpandedTemplate() {
//        System.out.println("Transform a template to OPMW and PROV ");
//        String mode = "RDF/XML";
//        String outFile = "testTemplate_J1";
//        Mapper instance = new Mapper();
//        System.out.println("Transforming a simple template with an abstract component: J1");
//        String template = "src"+File.separator+"main"+File.separator+"resources"+File.separator+"sample_data"+File.separator+"new"+File.separator+"simple_abstract"+File.separator+"template.owl";
//        String taxonomy_export="TestingDomain1"+File.separator+"Component"+File.separator+"TestingDomain_TaxonomyHierarchyModel.owl";
//        String componentDirectory = "src"+File.separator+"main"+File.separator+"resources"+File.separator+"sample_data"+File.separator+"new"+File.separator+"simple_abstract"+File.separator+"components"+File.separator+"";
//        assertNotSame("",instance.transformWINGSElaboratedTemplateToOPMW(template, mode, outFile,taxonomy_export,"TURTLE", componentDirectory,"Test"));
//        //delete output files.
//        //File f = new File(outFile);
//        //f.delete();
//    }
//    
//    @Test
//    public void testtransformWINGSResultsToOPMW_ExecutionWithCollections(){
//        System.out.println("Transform an execution to OPMW and PROV ");
//        Mapper instance = new Mapper();
//        String mode = "RDF/XML";
//        String outFileOPMW = "OPMWResult_AquaflowNTM_EDM";
//        String outFilePROV = "PROVResult_AquaflowNTM_EDM";        
//        System.out.println("Transforming an execution of a template with collections: Aquaflow_NTM");
//        String lib="src"+File.separator+"main"+File.separator+"resources"+File.separator+"sample_data"+File.separator+"new"+File.separator+"aquaflow_ntm"+File.separator+"Library.owl";
//        String execution="src"+File.separator+"main"+File.separator+"resources"+File.separator+"sample_data"+File.separator+"new"+File.separator+"aquaflow_ntm"+File.separator+"Execution.owl";
//        String data_catalog="TestingDomain1"+File.separator+"Data"+File.separator+"TestingDomain_DataCatalog.owl";
//        String data_URI="src"+File.separator+"main"+File.separator+"resources"+File.separator+"sample_data"+File.separator+"new"+File.separator+"aquaflow_ntm"+File.separator+"data"+File.separator+"";
//        
//        assertNotSame("",instance.transformWINGSResultsToOPMW(execution, lib, mode, outFileOPMW, outFilePROV,null, data_catalog, "TURTLE",data_URI,"Water"));
//        //delete output files (not done for the moment until we check everything is fine)
////        File f = new File(outFileOPMW);
////        File f2 = new File(outFilePROV);
//        //f.delete();
//        //f2.delete();
//    }
//    
//    @Test
//    public void testtransformWINGSResultsToOPMW_AbstractAndExpandedExecution(){
//        System.out.println("Transform an execution to OPMW and PROV ");
//        Mapper instance = new Mapper();
//        String mode = "RDF/XML";
//        String outFileOPMW = "OPMWResult_J1";
//        String outFilePROV = "PROVResult_J1";        
//        System.out.println("Transforming an execution of a template with collections: Aquaflow_NTM");
//        String lib="src"+File.separator+"main"+File.separator+"resources"+File.separator+"sample_data"+File.separator+"new"+File.separator+"simple_abstract"+File.separator+"library.owl";
//        String execution ="src"+File.separator+"main"+File.separator+"resources"+File.separator+"sample_data"+File.separator+"new"+File.separator+"simple_abstract"+File.separator+"execution.owl";
//        String data_catalog="TestingDomain1"+File.separator+"Data"+File.separator+"TestingDomain_DataCatalog.owl";
//        String data_URI="src"+File.separator+"main"+File.separator+"resources"+File.separator+"sample_data"+File.separator+"new"+File.separator+"simple_abstract"+File.separator+"data"+File.separator;
//        
//        assertNotSame("",instance.transformWINGSResultsToOPMW(execution, lib, mode, outFileOPMW, outFilePROV,null, data_catalog, "TURTLE",data_URI,"Water"));
//        //delete output files (not done for the moment until we check everything is fine)
////        File f = new File(outFileOPMW);
////        File f2 = new File(outFilePROV);
//        //f.delete();
//        //f2.delete();
//    }
    
    
    public void validateModel(OntModel m){
        System.out.println("Validating:  ALL EXECUTION ARTIFACTS SHOULD BELONG TO AN ACCOUNT");
        int problems = Integer.parseInt(Utils.getCountOf(Queries.COUNT_ARTIFACTS_WITHOUT_ACCOUNT, m, "countArt"));
        assertTrue(problems <= 0);
        System.out.println("Validating: ALL EXECUTION ARTIFACTS SHOULD HAVE A LOCATION (VARIABLES) OR VALUE (PARAMETERS)..");
        problems = Integer.parseInt(Utils.getCountOf(Queries.COUNT_ARTIFACTS_WITHOUT_LOCATION_OR_VALUE, m, "countArt"));
        assertTrue(problems <= 0);
        System.out.println("Validating: ALL EXECUTION ARTIFACTS SHOULD BELONG TO A TEMPLATE VARIABLE OR PARAMETER THAT BELONGS TO A TEMPLATE.");
        problems = Integer.parseInt(Utils.getCountOf(Queries.COUNT_ARTIFACTS_WITHOUT_BINDING_TO_TEMPLATE_ARTIFACT, m, "countArt"));
        assertTrue(problems <= 0);
        System.out.println("Validating: ALL TEMPLATE PROCESSES MUST USE OR GENERATE A TEMPLATE ARTIFACT (test in P-PLAN).");
        problems = Integer.parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_PROCESS_WITHOUT_BINDING_TO_ARTIFACT_OPMW, m, "countProc"));
        assertTrue(problems <= 0);
        
    
    
//    result+="#TEST"+(++n)+": ALL EXECUTION ARTIFACTS SHOULD BE USED OR GENERATED BY A PROCESS.\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_ARTIFACTS_WITHOUT_BINDING_TO_PROCESS, m, "countArt")))+"\n";
//    //execution processes
//    result+="#TEST"+(++n)+": ALL PROCESSES SHOULD BELONG TO AN ACCOUNT.\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_PROCESSES_WITHOUT_ACCOUNT, m, "countProc")))+"\n";
//    result+="#TEST"+(++n)+": ALL PROCESSES SHOULD USE OR GENERATE SOME ARTIFACT.\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_PROCESSES_NOT_BOUND_TO_ARTIFACT, m, "countProc")))+"\n";
//    result+="#TEST"+(++n)+": ALL PROCESSES SHOULD HAVE AN EXECUTION CODE ASSOCIATED TO THEM.\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_PROCESSES_WITHOUT_CODE, m, "countProc")))+"\n";
//    result+="#TEST"+(++n)+": ALL PROCESSES SHOULD CORRESPOND TO A PROCESS THAT BELONGS TO A TEMPLATE.\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_PROCESSES_WITHOUT_CORRECT_TEMPLATE_BINDING, m, "countProc")))+"\n";
//    //execution accounts
//    result+="#TEST"+(++n)+": ALL EXECUTIONS MUST BELONG TO A TEMPLATE.\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_EXECUTIONS_WITHOUT_TEMPLATE, m, "countAcc")))+"\n";
//    result+="#TEST"+(++n)+": ALL EXECUTIONS MUST HAVE AN END TIME, A START TIME AND A STATUS.\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_EXECUTIONS_WITHOUT_TIME_OR_STATUS, m, "countAcc")))+"\n";
//    result+="#TEST"+(++n)+": (OPTIONAL TEST) AN ACCOUNT MAY HAVE A POINTER TO THE ORIGINAL LOG FILE.\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_EXECUTIONS_WITHOUT_LOG_FILE, m, "countAcc")))+"\n";
//    //template artifacts
//    result+="#TEST"+(++n)+": ALL TEMPLATE ARTIFCATS MUST BELONG TO A TEMPLATE.\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_ARTIFACTS_WITHOUT_TEMPLATE, m, "countArt")))+"\n";
//    result+="#TEST"+(++n)+": ALL TEMPLATE ARTIFACTS MUST BE CONNECTED TO A TEMPLATE PROCESS (testing in  OPMW).\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_ARTIFACTS_WITHOUT_PROCESS_OPMW, m, "countArt")))+"\n";
//    result+="#TEST"+(++n)+": ALL TEMPLATE ARTIFACTS MUST BE CONNECTED TO A TEMPLATE PROCESS (testing in P-PLAN).\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_ARTIFACTS_WITHOUT_PROCESS_P_PLAN, m, "countArt")))+"\n";
//    //template processes
//    result+="#TEST"+(++n)+": ALL TEMPLATE PROCESSES MUST BELONG TO A TEMPLATE (test in OPMW).\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_PROCESSES_WITHOUT_TEMPLATE_OPMW, m, "countProc")))+"\n";
//    result+="#TEST"+(++n)+": ALL TEMPLATE PROCESSES MUST BELONG TO A TEMPLATE (test in PPlan).\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_PROCESSES_WITHOUT_TEMPLATE_PPLAN, m, "countProc")))+"\n";
////    ResultSetFormatter.out(System.out,Utils.queryLocalRepository(Queries.SELECT_TEMPL_PROCESSES_WITHOUT_TEMPLATE_PPLAN, m));
//    result+="#TEST"+(++n)+": ARE THERE ANY UNDECLARED WORKFLOW TEMPLATE PROCESSES?.\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_UNDECLARED_PROCESSES, m, "countProc")))+"\n";
//    result+="#TEST"+(++n)+": ALL TEMPLATE PROCESSES MUST USE OR GENERATE A TEMPLATE ARTIFACT (test in OPMW).\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_PROCESS_WITHOUT_BINDING_TO_ARTIFACT_OPMW, m, "countProc")))+"\n";
//    result+="#TEST"+(++n)+": ALL TEMPLATE PROCESSES MUST USE OR GENERATE A TEMPLATE ARTIFACT (test in P-PLAN).\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_PROCESS_WITHOUT_BINDING_TO_ARTIFACT_PPLAN, m, "countProc")))+"\n";
//    
//    //templates
//    result+="#TEST"+(++n)+": (OPTIONAL TEST) TEMPLATES SHOULD HAVE A VERSION NUMBER.\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_WITHOUT_VERSION_NUMBER, m, "countT")))+"\n";
//    result+="#TEST"+(++n)+": (OPTIONAL TEST) TEMPLATES SHOULD HAVE A POINTER TO THE NATIVE SYSTEM TEMPLATE.\n";
//    result+="\t"+isTestFailed(Integer.parseInt(Utils.getCountOf(Queries.COUNT_TEMPL_WITHOUT_NATIVE_SYS_TEMPL, m, "countT")))+"\n";
    
    }

    /**
     * Tests from the old validator
     */
    
}

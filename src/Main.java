import java.util.*;
public class Main {
public static void main(String[] args) {
	System.out.println("Transform an template to OPMW");
    Mapper instance = new Mapper();
  String lib = "/Users/Tirthmehta/Desktop/WINGS_PROVENANCE_EXPORT_ISI/executions/spacer/ultimatelib.owl";
  //String execution = "/Users/Tirthmehta/Desktop/WINGS_PROVENANCE_EXPORT_ISI/executions/spacer/spacer-ex.owl";
  //String execution = "/Users/Tirthmehta/Desktop/WINGS_PROVENANCE_EXPORT_ISI/executions/parasimple/cpexe.owl";
  //String execution = "/Users/Tirthmehta/Documents/workspace/WINGS_PROVENANCE_EXPORT_SCENARIOS/ab2ex.owl";
  //String execution = "/Users/Tirthmehta/Desktop/WINGS_PROVENANCE_EXPORT_ISI/abex3.owl";
  String execution ="/Users/Tirthmehta/Documents/workspace/WINGS_PROVENANCE_EXPORT_SCENARIOS/ab2ex.owl";
  
   //String template="/Users/Tirthmehta/Desktop/WINGS_PROVENANCE_EXPORT_ISI/executions/parasimple/CaesarCypherParallelSimple.owl";
   //String template="/Users/Tirthmehta/Desktop/WINGS_PROVENANCE_EXPORT_ISI/executions/abstractTest1/abstractTest1.owl";
   //String template="/Users/Tirthmehta/Documents/workspace/WINGS_PROVENANCE_EXPORT_SCENARIOS/mapreduce.owl";
   //String template="/Users/Tirthmehta/Documents/workspace/WINGS_PROVENANCE_EXPORT_SCENARIOS/EXAMPLE-2_INPUT_FOR_MAPPER_SCENARIO-1/HashingConceptScenario1.owl";
   String template="/Users/Tirthmehta/Documents/workspace/WINGS_PROVENANCE_EXPORT_SCENARIOS/CtoTextModificationsScenario3.owl";
    String mode = "RDF/XML";
    String mode2="Turtle";
  String outFileOPMW = "testResultOPMW";
  String outFilePROV = "testResultPROV";
  String outFile = "testTemplateabstractTest1";
  String taxonomy_export="/Users/Tirthmehta/Documents/workspace/WINGS_PROVENANCE_EXPORT_SCENARIOS/LOCAL_FOLDER_COMPONENT_CATALOGS_OF_DIFFERENT_DOMAINS/Testing.owl";
 // /Users/Tirthmehta/Documents/workspace/WINGS_PROVENANCE_EXPORT_SCENARIOS/LOCAL_FOLDER_COMPONENT_CATALOGS_OF_DIFFERENT_DOMAINS/Testing.owl
    
  //String ans=instance.transformWINGSElaboratedTemplateToOPMW(template, mode, outFile, null,taxonomy_export,mode2);
   String ans=instance.transformWINGSResultsToOPMW(execution, lib, mode, outFileOPMW, outFilePROV, null);
    System.out.println("--------------------------");
    System.out.println("location is :"+ans);
 

    
    //http://datascience4all.org/wings-portal/export/users/tirth/TestingDomain/workflows/spacer.owl
    //http://datascience4all.org/wings-portal/export/users/tirth/TestingDomain/workflows/abstract.owl
    ///Users/Tirthmehta/Documents/workspace/WINGS_PROVENANCE_EXPORT_ISI/executions/spacer/spacer-ex.owl
    //http://datascience4all.org/wings-portal/export/users/tirth/TestingDomain/executions/spacer-64c57dfd-d9ad-44f2-b114-2cfc3d0e50a1.owl
    //http://datascience4all.org/wings-portal/export/users/tirth/TestingDomain/executions/abstractTest1-83-cdd28105-a8a5-4fe8-bfb9-c8291a31ac7a.owl
    //http://datascience4all.org/wings-portal/export/users/tirth/TestingDomain/executions/library.owl
    //http://datascience4all.org/wings-portal/export/users/tirth/CaesarCypher/workflows/CaesarCypherParallelSimple.owl
    
    //expanded template for abstractTest1
    //http://datascience4all.org/wings-portal/export/users/tirth/TestingDomain/executions/abstractTest1-83-7336709c-6108-4fc8-9ecd-810f27c8d870.owl#abstractTest1-83-7336709c-6108-4fc8-9ecd-810f27c8d870
    
}
}

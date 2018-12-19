package opmw_mapper;

import java.util.ArrayList;

import org.apache.jena.ontology.OntModel;
public class Validator{
public static String validateRepo(OntModel m){
    String result = "##########REPORT##########\n";        
    int n=0;
    
    System.out.println("VALIDATION TESTS FOR SCENARIO-1 (NUMBER OF EXECUTIONS, EXPANDED TEMPLATES AND ABSTRACT TEMPLATES)");
    //TEST 1: HOW MANY EXECUTIONS DOES THE TEMPLATE HAVE?
      result+="#TEST"+(++n)+":HOW MANY EXECUTIONS DOES THE TEMPLATE HAVE?\n";
      ArrayList<Integer> arr1=new ArrayList<>();
      
      arr1=Utils.queryresult(Queries.HOW_MANY_EXECUTIONS_DOES_THE_TEMPLATE_HAVE, m, "t","acc");
      result+="The number of executions are: "+arr1.get(1)+" and the number of templates are "+arr1.get(0)+"\n";
      
      result+="\n\n";
      
    //TEST 2: HOW MANY EXPANDED TEMPALTES DOES THE TEMPLATE HAVE? 
      result+="#TEST"+(++n)+":HOW MANY EXPANDED TEMPALTES DOES THE TEMPLATE HAVE?\n";
      ArrayList<Integer> arr2=new ArrayList<>();
      
      arr2=Utils.queryresult(Queries.HOW_MANY_EXPANDED_TEMPLATES_DOES_THE_TEMPLATE_HAVE, m, "t","et");
      result+="The number of templates are: "+arr2.get(0)+" and the number of expanded templates are "+arr2.get(1)+"\n";
      
      result+="\n\n";
      
      //TEST 3: HOW MANY EXECUTIONS DOES THE EXPANDED TEMPLATE HAVE? 
        result+="#TEST"+(++n)+":HOW MANY EXECUTIONS DOES THE EXPANDED TEMPLATE HAVE?\n";
        ArrayList<Integer> arr3=new ArrayList<>();
        
        arr3=Utils.queryresult(Queries.HOW_MANY_EXECUTIONS_DOES_THE_EXPANDED_TEMPLATES_HAVE, m, "et","acc");
        result+="The number of executions are: "+arr3.get(1)+" and the number of expanded templates are "+arr3.get(0)+"\n";
      
        result+="\n\n";
        
        
      //TEST 4: QUERY 4 ALL IN ONE--- 
        result+="#TEST"+(++n)+":QUERY 4 ALL IN ONE\n";
        ArrayList<Integer> arr4=new ArrayList<>();
        
        arr4=Utils.queryresultALL(Queries.SCENARIO_1_SUMMARY_QUERY, m, "t","et","acc");
        result+="The number of abstract templates are: "+arr4.get(0)+"\nThe number of expanded templates are: "+arr4.get(1)+
        		"\nThe number of executions are: "+arr4.get(2)+"\n";
      
        result+="\n\n";
    	
   
    return result;
}

}
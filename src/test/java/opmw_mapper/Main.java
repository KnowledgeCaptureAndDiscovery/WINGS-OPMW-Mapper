package opmw_mapper;

import java.util.*;

import org.apache.jena.ontology.OntModel;

public class Main {
public static void main(String[] args) {
	int i=0;
    String path ="";
    while(i< args.length){
        String s = args[i];
        if(s.equals("-d")){
            path = args[i+1];
            i++;
        }else{
            System.out.println("Usage: -d directory path with the OPMW RDF to validate");
            return;
        }
        i++;
    }
    OntModel m = Utils.loadDirectory("/Users/Tirthmehta/Documents/workspace/WINGS_PROVENANCE_EXPORT_SCENARIOS/VALIDATIONS/EXAMPLE2");
//    OntModel m = Utils.loadDirectory(path);
    String s = ""; 
    //s+=Statistics.getStats(m);
    s+= Validator.validateRepo(m);
    System.out.println(s);
}
}

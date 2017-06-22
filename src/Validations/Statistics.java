package Validations;

import org.apache.jena.ontology.OntModel;

public class Statistics {
	public static String getStats (OntModel m){
        String s = "######STATISTICS ON THE LOADED FILES######\n";
        s+="NUMBER OF EXECUTIONS: "+ Utils.getCountOf(Queries.NUMBER_OF_EXEC_ACCCOUNTS, m, "countAcc")+"\n";
        s+="NUMBER OF EXECUTION ARTIFACTS: "+ Utils.getCountOf(Queries.NUMBER_OF_EXEC_ARTIFACTS, m, "countArt")+"\n";
        s+="\tNUMBER OF EXEC FILES: "+ Utils.getCountOf(Queries.NUMBER_OF_EXEC_VARIABLES, m, "countArt")+"\n";
        s+="\tNUMBER OF EXEC PARAMETERS: "+ Utils.getCountOf(Queries.NUMBER_OF_EXEC_PARAMETERS, m, "countArt")+"\n";
        s+="NUMBER OF EXECUTION PROCESSES: "+ Utils.getCountOf(Queries.NUMBER_OF_EXEC_PROC, m, "countProc")+"\n";
        s+="NUMBER OF TEMPLATES: "+ Utils.getCountOf(Queries.NUMBER_OF_TEMPLATES, m, "countT")+"\n";
        s+="NUMBER OF TEMPLATE ARTIFACTS: "+ Utils.getCountOf(Queries.NUMBER_OF_TEMPLATE_ARTIFACTS, m, "countArt")+"\n";
        s+="\tNUMBER OF TEMPLATE VARIABLES: "+ Utils.getCountOf(Queries.NUMBER_OF_TEMPLATE_VARIABLES, m, "countArt")+"\n";
        s+="\tNUMBER OF TEMPLATE PARAMENTERS: "+ Utils.getCountOf(Queries.NUMBER_OF_TEMPLATE_PARAMETERS, m, "countArt")+"\n";
        s+="NUMBER OF TEMPLATE PROCESSES: "+ Utils.getCountOf(Queries.NUMBER_OF_TEMPLATE_PROCESSES, m, "countProc")+"\n";
        s+="#######################################";
        return s;
    }
}

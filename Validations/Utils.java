package Validations;

import java.io.File;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;

public class Utils {
	//given a directory, loads all the files in a local model.
    public static OntModel loadDirectory(String dirPath){
        OntModel model = ModelFactory.createOntologyModel();
        File dir = new File (dirPath);
        if(dir.isDirectory()){
            for (File currf : dir.listFiles()){
                loadFileInModel(model, currf);
            }
            return model;
        }
        System.err.println("The path "+dirPath+ " is not a directory");
        return null;
        
    }
    
    private static OntModel loadFileInModel(OntModel m, File f){
        try{
            System.out.println("Loading file: "+f.getName());
            m.read(f.getAbsolutePath(), null, "TURTLE");
        }catch(Exception e){
            System.err.println("Could not load the file in turtle. Attempting to read it in turtle...");
            try{
                m.read(f.getAbsolutePath(), null, "RDF/XML");
            }catch(Exception e1){
                System.err.println("Could not load ontology in rdf/xml.");
            }
        }
        return m;
    }
    
    public static ResultSet queryLocalRepository(String queryIn, OntModel repository){
        Query query = QueryFactory.create(queryIn);
        // Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create(query, repository);
        ResultSet rs =  qe.execSelect();
        //qe.close();
        return rs;
    }
    
    public static String getCountOf (String query, OntModel m, String varToQuery){
        ResultSet r =  Utils.queryLocalRepository(query, m);
        String result = "";
        while (r.hasNext()){
            QuerySolution qs = r.nextSolution();
            result+=qs.getLiteral("?"+varToQuery).getString();
        }
        return result;
    }
            
    
    //given an online repository, perform a test against a template/run.
    //TO DO
}

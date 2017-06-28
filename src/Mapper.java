import java.io.InputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.rulesys.impl.Generator;
import org.apache.jena.util.FileManager;






import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;


class Gen
{
	int input;
	int output;
	String className;
}

public class Mapper {
    private OntModel WINGSModelTemplate;
    private OntModel WINGSExecutionResults;
    private OntModel OPMWModel;
    private OntModel PROVModel;
    private String taxonomyURL;
    private OntModel ExpandedTemplateModel;
    private OntModel TemplateModelforCondition;
    private OntModel ComponentCatalog;
    private OntModel Taxonomy_Export;
    public Mapper(){

    }
    
    //a function for creating the new name for the expanded template
    // Takes the string as a parameter
    //Produces the corresponding hashed value as output
    public static String MD5(String text) throws NoSuchAlgorithmException
   	{
   		MessageDigest md=MessageDigest.getInstance("MD5");
   		md.update(text.getBytes());
   		byte b[]=md.digest();
   		StringBuffer sb=new StringBuffer();
   		for(byte b1:b)
   		{
   			sb.append(Integer.toHexString(b1 & 0xff).toString());
   		}
   		return sb.toString();
   		
   	}

    /**
     * Query a local repository, specified in the second argument
     * @param queryIn sparql query to be performed
     * @param repository repository on which the query will be performed
     * @return 
     */
    private ResultSet queryLocalRepository(String queryIn, OntModel repository){
        Query query = QueryFactory.create(queryIn);
        // Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create(query, repository);
        ResultSet rs =  qe.execSelect();
        //qe.close();
        return rs;
    }
    
    /**
     * Query the local OPMW repository
     * @param queryIn input query
     * @return 
     */
    private ResultSet queryLocalOPMRepository(String queryIn) {
        return queryLocalRepository(queryIn, OPMWModel);
    }
    /**
     * Query the local Wings repository
     * @param queryIn input query
     * @return 
     */
    private ResultSet queryLocalWINGSTemplateModelRepository(String queryIn) {
        return queryLocalRepository(queryIn, WINGSModelTemplate);
    }
    
  //function to query just the component catalog model
    private ResultSet queryComponentCatalog(String queryIn) {
        return queryLocalRepository(queryIn, ComponentCatalog);
    }
    
    
    //function to query just the expanded template model
    private ResultSet queryLocalExpandedTemplateRepository(String queryIn) {
        return queryLocalRepository(queryIn, ExpandedTemplateModel);
    }
    
    //function to query just the taxonomy model
    private ResultSet queryLocalTaxonomyModelRepository(String queryIn) {
        return queryLocalRepository(queryIn, Taxonomy_Export);
    }
    
    
    //function to query just the conditioned template model
    private ResultSet queryConditionTemplateModel(String queryIn) {
        return queryLocalRepository(queryIn, TemplateModelforCondition);
    }
    
    
    /**
     * Query the local results repository
     * @param queryIn input query
     * @return 
     */
    private ResultSet queryLocalWINGSResultsRepository(String queryIn) {
        return queryLocalRepository(queryIn, WINGSExecutionResults);
    }
    /**
     * Method for accessing the URl of the domain ontology.
     * @param queryIn input query
     * @return 
     */
    private String getTaxonomyURL(OntModel m) throws Exception{
        if(taxonomyURL!=null)return taxonomyURL;
        else{
            ResultSet rs = this.queryLocalRepository(Queries.queryGetTaxonomyURL(), m);
            if(rs.hasNext()){
                taxonomyURL = rs.next().getResource("?taxonomyURL").getNameSpace();
            }else{
                throw new Exception("Taxonomy is not available");
            }
        }
        return taxonomyURL;
    }

    /**
     * Loads the files to the Local repository, to prepare conversion to OPM
     * @param template. Workflow template
     * @param modeFile. syntax of the files to load: "RDF/XML", "N-TRIPLE", "TURTLE" (or "TTL") and "N3"
     * @throws java.lang.Exception
     */
    public void loadTemplateFileToLocalRepository(String template, String modeFile) throws Exception{
        WINGSModelTemplate = ModelFactory.createOntologyModel();//ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(template);
        if (in == null) {
            throw new IllegalArgumentException("File: " + template + " not found");
        }
        // read the RDF/XML file
        WINGSModelTemplate.read(in, null, modeFile);
        System.out.println("File "+template+" loaded into the model template");
//        getACDCfromModel(true);
        //load the taxonomy as well
        loadTaxonomy(WINGSModelTemplate);
    }
    
    
    public void loadExpandedTemplateFileToLocalRepository(String template, String modeFile){
        
    	InputStream in2 = FileManager.get().open(template.replaceAll("#.*$", ""));
        if (in2 == null){
            throw new IllegalArgumentException("File: " + template + " not found");
        }
        
        ExpandedTemplateModel.read(in2, null, modeFile);
        System.out.println("Expanded Template File "+template+" loaded into the execution results");

    }
    
    
public void loadedTemplateFileCondition(String template, String modeFile){
        
    	InputStream in2 = FileManager.get().open(template.replaceAll("#.*$", ""));
        if (in2 == null){
            throw new IllegalArgumentException("File: " + template + " not found");
        }
        
        TemplateModelforCondition.read(in2, null, modeFile);
        System.out.println("Template File Condition"+template+" loaded into the new template model");

    }

public void loadTaxonomyExport(String template, String modeFile){
    
	InputStream in2 = FileManager.get().open(template.replaceAll("#.*$", ""));
    if (in2 == null){
        throw new IllegalArgumentException("File: " + template + " not found");
    }
    
    Taxonomy_Export.read(in2, null, modeFile);
    System.out.println("Taxonomy_Export File Condition"+template+" loaded into the new template model");

}
    
    
    
    
    /**
     * Method to load the domain specific taxonomy. Used to determine the node types.
     * @param m model where to load the taxonomy
     * @throws Exception 
     */
    private void loadTaxonomy(OntModel m)throws Exception{
         System.out.println("Attempting to load the domain specific domain ...");
        //since this is NOT included in the template per se, we need to download it
        System.out.println("Importing taxonomy at: "+ getTaxonomyURL(m));
        m.read(getTaxonomyURL(m));
        
        //reading the taxonomy separately in a new Model
        ComponentCatalog.read(getTaxonomyURL(m));
        System.out.println("Done changes here");
        System.out.println("-------------------------");
        if(ComponentCatalog!=null)
        	System.out.println("not null");
        //ComponentCatalog.write(System.out,"RDF/XML");
        System.out.println("Exporting the componentCatalog to a separate file outside");
        exportRDFFile("componentCatalog2", ComponentCatalog);
        System.out.println("-------------------------");
    }

    /**
     * Method to load an execution file to a local model.
     * @param executionResults owl file with the execution
     * @param mode type of serialization. E.g., "RDF/XML"
     */
    public void loadResultFileToLocalRepository(String executionResults, String mode){
        //InputStream in2 = FileManager.get().open(executionResults);
        InputStream in2 = FileManager.get().open(executionResults.replaceAll("#.*$", ""));
        if (in2 == null){
            throw new IllegalArgumentException("File: " + executionResults + " not found");
        }
        
        WINGSExecutionResults.read(in2, null, mode);
        System.out.println("File "+executionResults+" loaded into the execution results");
    }

    /**
     * Method to transform a Wings template to OPMW, PROV and P-Plan
     * @param template template file
     * @param mode rdf serialization of the file
     * @param outFile output file name
     * @return Template URI assigned to identify the template
     */
    //public String transformWINGSElaboratedTemplateToOPMW(String template,String mode, String outFile){
    public String transformWINGSElaboratedTemplateToOPMW(String template,String mode, String outFile, String templateName,String taxonomy_export,String mode2){
        //clean previous transformations
    	
    	//loading the component catalog
    	if(ComponentCatalog!=null){
        	ComponentCatalog.removeAll();            
        }
        ComponentCatalog = ModelFactory.createOntologyModel();

        if(WINGSModelTemplate!=null){
            WINGSModelTemplate.removeAll();
        }
        if(OPMWModel!=null){
            OPMWModel.removeAll();            
        }
        
        //loading the taxonomy file that is also going to be exported
        if(Taxonomy_Export!=null){
        	Taxonomy_Export.removeAll();            
        }
        Taxonomy_Export = ModelFactory.createOntologyModel();
        
        //loading the existing taxonomy file
        try{
            //load the template file to WINGSModel (already loads the taxonomy as well
            this.loadTaxonomyExport(taxonomy_export, mode);            
        }catch(Exception e){
            System.err.println("Error "+e.getMessage());
            return "";
        }
        
        
        
        OPMWModel = ModelFactory.createOntologyModel(); //inicialization of the model        
        try{
            //load the template file to WINGSModel (already loads the taxonomy as well
            this.loadTemplateFileToLocalRepository(template, mode);            
        }catch(Exception e){
            System.err.println("Error "+e.getMessage());
            return "";
        }
        
        
    	
        //retrieval of the name of the workflowTemplate
        String queryNameWfTemplate = Queries.queryNameWfTemplate();
        //String templateName = null, templateName_ = null;
        String templateName_ = null;
        //System.out.println(queryNameWfTemplate);
        ResultSet r = queryLocalWINGSTemplateModelRepository(queryNameWfTemplate);
        if(r.hasNext()){//there should be just one local name per template
            QuerySolution qs = r.next();
            Resource res = qs.getResource("?name");
            Literal v = qs.getLiteral("?ver");
            if (templateName==null){
                templateName = res.getLocalName();
                if(templateName == null){
                    System.out.println("Error: No Template specified.");
                    return "";
                }
            }
            templateName_=templateName+"_";
            //add the template as a provenance graph
            this.addIndividual(OPMWModel,templateName, Constants.OPMW_WORKFLOW_TEMPLATE, templateName);
            
            OntClass cParam = OPMWModel.createClass(Constants.P_PLAN_PLAN);
            cParam.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+encode(templateName));
            
            if(v!=null){
                this.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName,""+ v.getInt(),
                        Constants.OPMW_DATA_PROP_VERSION_NUMBER, XSDDatatype.XSDint);
            }
            //add the uri of the original log file (native system template)
            this.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName, 
                    res.getURI(),Constants.OPMW_DATA_PROP_HAS_NATIVE_SYSTEM_TEMPLATE, XSDDatatype.XSDanyURI);
            
            //Prov-o interoperability : workflow template           
            OntClass plan = OPMWModel.createClass(Constants.PROV_PLAN);
            plan.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+encode(templateName));
            
            this.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName,
                res.getURI(),Constants.PROV_HAD_PRIMARY_SOURCE, XSDDatatype.XSDanyURI);
            
           
        }        
        System.out.println("main template part done"); 
        //additional metadata from the template.
        String queryMetadata = Queries.queryMetadata();
        r = null;
        r = queryLocalWINGSTemplateModelRepository(queryMetadata);
        while(r.hasNext()){
            QuerySolution qs = r.next();
            Literal doc = qs.getLiteral("?doc");
            Literal contrib = qs.getLiteral("?contrib");
            Literal time = qs.getLiteral("?time");
            Literal license = qs.getLiteral("?license");
            Resource diagram = qs.getResource("?diagram");
            //ask for diagram here: hasTemplateDiagram xsd:anyURI (png)
            if(doc!=null){
                this.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName,doc.getString(),
                        Constants.OPMW_DATA_PROP_HAS_DOCUMENTATION);
            }
            if(contrib!=null){
                this.addIndividual(OPMWModel,contrib.getString(), Constants.OPM_AGENT,"Agent "+contrib.getString());
                this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName,Constants.CONCEPT_AGENT+"/"+contrib.getString(),
                        Constants.PROP_HAS_CONTRIBUTOR);
                
                //prov-o interoperability
                String agEncoded = encode(Constants.CONCEPT_AGENT+"/"+contrib.getString());
                OntClass d = OPMWModel.createClass(Constants.PROV_AGENT);
                d.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+agEncoded);
            }
            if(license!=null){
                this.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName,license.getString(),
                        Constants.DATA_PROP_RIGHTS, XSDDatatype.XSDanyURI);
            }
            if(time!=null){
                this.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName,time.getString(),
                        Constants.DATA_PROP_MODIFIED, XSDDatatype.XSDdateTime);
            }
            if(diagram!=null){
                this.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName,diagram.getURI(),
                        Constants.OPMW_DATA_PROP_HAS_TEMPLATE_DIAGRAM, XSDDatatype.XSDanyURI);
            }
        }
        
        // retrieval of the Components (nodes, with their components and if they are concrete or not)
        String queryNodes = Queries.queryNodes();
        r = null;
        r = queryLocalWINGSTemplateModelRepository(queryNodes);
        
        //-----------------------------------------
        //Creating a replication of the result set to populate an arrayList with the number of input and output sectors:
        ResultSet replica=r;
        ResultSet replica2=r;
        ArrayList<Gen> inputOutputs=new ArrayList<>();
        
    	
  
    	
    	while(replica.hasNext()){
        QuerySolution qs = replica.next();
        Resource res = qs.getResource("?n");
        Resource comp = qs.getResource("?c");
        Resource typeComp = qs.getResource("?typeComp");
        Literal rule = qs.getLiteral("?rule");
        Literal isConcrete = qs.getLiteral("?isConcrete");
        
        Resource inport = qs.getResource("?inport");
        Resource outport = qs.getResource("?outport");
     
        //------------ADDITION BY TIRTH-----------------
        //obtaining the className
        String className="";
        int indexOf=typeComp.toString().indexOf('#');
        className=typeComp.toString().substring(indexOf+1,typeComp.toString().length());
        //System.out.println("type class is: "+className);
        
        
        replica2=null;
        replica2 = queryLocalWINGSTemplateModelRepository(queryNodes);
        Gen x=InputOutputGenerator(replica2,className);
        inputOutputs.add(x);    
    }
       System.out.println("ArrayLIST GENERATED---------"); 
        for(Gen y:inputOutputs)
        {
        	System.out.println("class: "+y.className+" inputs: "+y.input+" ouputs: "+y.output);
        }
        
       System.out.println("ArrayLIST PRINTING ENDS---------");
        //------------------------------------------
       r = null;
       r = queryLocalWINGSTemplateModelRepository(queryNodes);
       
       //created this hashset since some of the components repeat and we dont want to continuously find them in the
       //component catalog
       HashSet<String> hsforComps=new HashSet<>();

        while(r.hasNext()){
            QuerySolution qs = r.next();
            Resource res = qs.getResource("?n");
            Resource comp = qs.getResource("?c");
            Resource typeComp = qs.getResource("?typeComp");
            Literal rule = qs.getLiteral("?rule");
            Literal isConcrete = qs.getLiteral("?isConcrete");
            
            Resource inport = qs.getResource("?inport");
            Resource outport = qs.getResource("?outport");
            
            System.out.println("Node work starts here");
            System.out.println(res+" Node has component "+comp+" of type: "+ typeComp);//+ " which is concrete: "+isConcrete.getBoolean()
            System.out.println("Node has inport "+inport.getLocalName());
            System.out.println("Node has outport "+outport.getLocalName());
            //------------ADDITION BY TIRTH-----------------
            //obtaining the className
            String className="";
            int indexOf=typeComp.toString().indexOf('#');
            className=typeComp.toString().substring(indexOf+1,typeComp.toString().length());
            System.out.println("type class is: "+className);
            
            
            
            
            
            
            
          //------------ADDITION BY TIRTH-----------------
            //obtaining the className
            String domainName="";
            int indexOf2=typeComp.toString().indexOf("/components");
            System.out.println("domain name finding");
            System.out.println(typeComp.toString().substring(0,indexOf2));
            String subDomain=typeComp.toString().substring(0,indexOf2);
            domainName=subDomain.substring(subDomain.lastIndexOf('/')+1,subDomain.length()); 
            System.out.println("domain name  is: "+domainName);
            
            
            //add each of the nodes as a UniqueTemplateProcess
            this.addIndividual(OPMWModel,templateName_+res.getLocalName(),Constants.OPMW_WORKFLOW_TEMPLATE_PROCESS, "Workflow template process "+res.getLocalName());
            //p-plan interop
            OntClass cStep = OPMWModel.createClass(Constants.P_PLAN_STEP);
            cStep.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+encode(templateName_+res.getLocalName()));
            
            
            //Have to start querying the component catalog for checking the number of inputs and outputs:

            if(!hsforComps.contains(className))
            {
            	System.out.println("inside outermost condition");
            String componentCatalogQueryforSubclassCheck = Queries.componentCatalogQueryforSubclassCheckfinal(className);
            ResultSet rnew1 = null;
            rnew1 = queryComponentCatalog(componentCatalogQueryforSubclassCheck);	
            int deciderpoint=0;
            System.out.println("Finallllll");
            Resource AbstractSuperClass=null;
            while(rnew1.hasNext())
            {
            	
            	QuerySolution qsnew = rnew1.next();
                Resource nodenew = qsnew.getResource("?n");
                Resource x = qsnew.getResource("?x");
                if(x!=null)
                {

                	if(nodenew.getLocalName().equals(className))
                	{
                			AbstractSuperClass=x;
                			System.out.println("x is "+x.getLocalName());
                			deciderpoint=1;
                			break;         		
                	}
                	
                }
                else
                	System.out.println("x is null");
                

            }
            System.out.println("ULTIMATE WORTH IT SUPER CLASS IS "+AbstractSuperClass.getLocalName());
            
            
            	
            if(deciderpoint==1)
            {
            	
            //QUERY-1 FOR THE RETRIEVAL OF INPUTS AND OUTPUTS OF THE PARTICULAR COMPONENT
            String componentCatalogQueryforInputsandOutputsComponent = Queries.componentCatalogQueryforInputsandOutputs(className);
            ResultSet rnew2 = null;
            rnew2 = queryComponentCatalog(componentCatalogQueryforInputsandOutputsComponent);
           
            HashSet<String> hsi=new HashSet<>();
            HashSet<String> hso=new HashSet<>();
            while(rnew2.hasNext())
            {
            	QuerySolution qsnew = rnew2.next();
                Resource nodenew = qsnew.getResource("?n");
                Resource i = qsnew.getResource("?i");
                Resource o = qsnew.getResource("?o");
                
                if(nodenew.getLocalName().equals(className.substring(0,className.length()-5)))
                {
                hsi.add(i.getLocalName());
                hso.add(o.getLocalName());
                System.out.println("node : "+nodenew.getLocalName());
                }
            }
            
            
            
          //QUERY-2 FOR THE RETRIEVAL OF INPUTS AND OUTPUTS OF THE PARTICULAR COMPONENT'S ABSTRACT CLASS
            if(!AbstractSuperClass.getLocalName().equals("Component"))
            {
            String componentCatalogQueryforInputsandOutputsAbstractComponent = Queries.componentCatalogQueryforInputsandOutputs(AbstractSuperClass.getLocalName());
            rnew2 = null;
            rnew2 = queryComponentCatalog(componentCatalogQueryforInputsandOutputsAbstractComponent);
           
            HashSet<String> hsAi=new HashSet<>();
            HashSet<String> hsAo=new HashSet<>();
            while(rnew2.hasNext())
            {
            	QuerySolution qsnew = rnew2.next();
                Resource nodenew = qsnew.getResource("?n");
                Resource i = qsnew.getResource("?i");
                Resource o = qsnew.getResource("?o");
                
                //System.out.println("nodenew "+nodenew.getLocalName()+" abstractclass "+AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5));
                if(nodenew.getLocalName().equals(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)))
                {
                	System.out.println("node in abstract part : "+nodenew.getLocalName());
                	hsAi.add(i.getLocalName());
                	hsAo.add(o.getLocalName());
                
                }
            }
            System.out.println("component catalog for that Abstract component only:");
            System.out.println("className : "+AbstractSuperClass.getLocalName());
            System.out.println("input size: "+hsAi.size());
            System.out.println("output size: "+hsAo.size());
            
            System.out.println("component catalog for that particular component only:");
            System.out.println("className : "+className);
            System.out.println("input size: "+hsi.size());
            System.out.println("output size: "+hso.size());
            
            if(hsAi.size()==hsi.size() && hsAo.size()==hso.size())
            {
            	System.out.println("They are equal...now just export as a subclass if it does not exist");
            	//PART 1: CHECKING CRITERIA
            	//querying the Taxonomy Export Model to check whether this component already exists or not and if it 
            	//exists if its a subclass of that abstract class only
            	Taxonomy_Export.write(System.out,"RDF/XML");
            	
            	
            	
            	String checkifComponentExists = Queries.TaxonomyExportQueryforSubclassCheckfinal(className);
                rnew2 = null;
                rnew2 = queryLocalTaxonomyModelRepository(checkifComponentExists);
                boolean existsInTaxonomyModel=false;
                while(rnew2.hasNext())
                {
                	QuerySolution qsnew = rnew2.next();
                	Resource nodenew = qsnew.getResource("?n");
                    Resource x = qsnew.getResource("?x");

                    	
                    if(nodenew!=null && x!=null)
                    {
                    	System.out.println("node "+nodenew.getLocalName()+" x "+x.getLocalName());
                    	if(nodenew.getLocalName().equals(className.toUpperCase()) && x.getLocalName().equals(AbstractSuperClass.getLocalName().toUpperCase()))
                    	{
                    		System.out.println("it exists");
                    		existsInTaxonomyModel=true;
                    		break;
                    	}
                    }
  
                }
                
            	
            	
            	
            	
            	
            	
            	
            	
            	//PART2: EXPORTING CRITERIA
                if(existsInTaxonomyModel==false)
                {
                	System.out.println("you will have to export it now");
                	//starting to export now
                	//this.addIndividual(Taxonomy_Export,templateName, Constants.OPMW_WORKFLOW_TEMPLATE, templateName);
                	//this.addIndividual(Taxonomy_Export,className, Constants.TAXONOMY_CLASS, className);
                	String nameOfIndividualEnc = encode(className);
                    OntClass c = Taxonomy_Export.createClass(Constants.PREFIX_OWL+"Class");
                    c.createIndividual(Constants.TAXONOMY_CLASS+nameOfIndividualEnc);
//                    this.addProperty(Taxonomy_Export,Constants.TAXONOMY_CLASS+nameOfIndividualEnc,
//                    		Constants.TAXONOMY_CLASS+AbstractSuperClass,                    
//                                Constants.PREFIX_RDFS+"subClassOf"); 
                    
                    String nameOfIndividualEnc2 = encode(AbstractSuperClass.getLocalName());
                    OntClass c2 = Taxonomy_Export.createClass(Constants.PREFIX_OWL+"Class");
                    c2.createIndividual(Constants.TAXONOMY_CLASS+nameOfIndividualEnc2);
                    
                    //for component
                    OntProperty propSelec = Taxonomy_Export.createOntProperty(Constants.PREFIX_RDFS+"subClassOf");
                    Resource source = Taxonomy_Export.getResource(Constants.TAXONOMY_CLASS+encode(className) );
                    Individual instance = (Individual) source.as( Individual.class );
                    if((AbstractSuperClass.getLocalName()).contains("http://")){//it is a URI
                        instance.addProperty(propSelec,(Constants.TAXONOMY_CLASS+AbstractSuperClass.getLocalName()));            
                    }else{//it is a local resource
                        instance.addProperty(propSelec, Taxonomy_Export.getResource(Constants.TAXONOMY_CLASS+encode(AbstractSuperClass.getLocalName())));
                    }
                    
                    //for abstract component
                    OntProperty propSelec2 = Taxonomy_Export.createOntProperty(Constants.PREFIX_RDFS+"subClassOf");
                    Resource source2 = Taxonomy_Export.getResource(Constants.TAXONOMY_CLASS+ encode(AbstractSuperClass.getLocalName()) );
                    Individual instance2 = (Individual) source2.as( Individual.class );
                    if(("Component").contains("http://")){//it is a URI
                        instance2.addProperty(propSelec,Constants.PREFIX_COMPONENT+"Component");            
                    }else{//it is a local resource
                        instance2.addProperty(propSelec, Taxonomy_Export.getResource(Constants.PREFIX_COMPONENT+encode("Component")));
                    }
                    
                    
                }
            	
            	
            	
            	
            	
            	
            	
            }
            else
            	{
            		System.out.println("create a new class now");
            		//START EXPORTING A NEW CLASS AND LINK ITS CI's
            	}
            
            }
            
            
            
            
            
           
            }
            
            
            
            hsforComps.add(className);
            }
            
            
            if(typeComp.isURIResource()){ //only adds the type if the type is a uRI (not a blank node)
            	System.out.println("--------------------");
                String tempURI = encode(Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+res.getLocalName());
                String newIdName="http://w3id.org/wings/"+domainName+"#"+className;
                System.out.println("tempURI "+tempURI);
            	//String tempURI = "http://w3id.org/wings/"+domainName+"#"+className;
                System.out.println("temp uri :"+tempURI);
                OntClass cAux = OPMWModel.createClass(newIdName);//repeated tuples will not be duplicated
                cAux.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+tempURI);
            }else{
                System.out.println("ANON RESOURCE "+typeComp.getURI()+" ignored");
            }
            if(rule!=null){
                //rules are strings
                this.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+res.getLocalName(),
                    rule.getString(),                    
                        Constants.WINGS_PROP_HAS_RULE);
                
                this.addDataProperty(OPMWModel, Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+res.getLocalName(),rule.getString(), Constants.OPMW_COMPONENT_HAS_RULES);
            }
            if(isConcrete!=null)
            {
            	System.out.println("is component: "+comp.getLocalName()+" concrete: "+isConcrete.getBoolean());
            	this.addDataProperty(OPMWModel, Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+res.getLocalName(), isConcrete.getBoolean()+"", Constants.OPMW_DATA_PROP_IS_CONCRETE, XSDDatatype.XSDboolean);
            }
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+res.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName,                    
                        Constants.OPMW_PROP_IS_STEP_OF_TEMPLATE);            
            //p-plan interop
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+res.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName,                    
                        Constants.P_PLAN_PROP_IS_STEP_OF_PLAN);
            
            
        }
        //retrieval of the dataVariables
        String queryDataV = Queries.queryDataV2();
        r = queryLocalWINGSTemplateModelRepository(queryDataV);
        boolean isCollection;
//        ResultSetFormatter.out(r);
        while(r.hasNext()){
            QuerySolution qs = r.next();
            Resource variable = qs.getResource("?d");
            Resource type = qs.getResource("?t");
            Literal dim = qs.getLiteral("?hasDim");            
            this.addIndividual(OPMWModel,templateName_+variable.getLocalName(), Constants.OPMW_DATA_VARIABLE, "Data variable "+variable.getLocalName());
            //p-plan interop
            OntClass cVar = OPMWModel.createClass(Constants.P_PLAN_Variable);
            cVar.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_DATA_VARIABLE+"/"+encode(templateName_+variable.getLocalName()));
           
            //we add the individual as a workflowTemplateArtifact as well            
            String aux = encode(Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+variable.getLocalName());
            OntClass cAux = OPMWModel.createClass(Constants.OPMW_WORKFLOW_TEMPLATE_ARTIFACT);//repeated tuples will not be duplicated
            cAux.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+aux);
                   
            if(dim!=null){//sometimes is null, but it shouldn't
                this.addDataProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+variable.getLocalName(),
                        ""+dim.getInt(), Constants.OPMW_DATA_PROP_HAS_DIMENSIONALITY, XSDDatatype.XSDint);
                //System.out.println(res+" has dim: "+dim.getInt());
                if(dim.getInt()>0)
                	isCollection=true;
                else
                	isCollection=false;
                this.addDataProperty(OPMWModel, Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+variable.getLocalName(), ""+isCollection, Constants.OPMW_DATA_PROP_IS_COLLECTION, XSDDatatype.XSDboolean);
            }
            //types of data variables
            if(type!=null){
                //sometimes there are some blank nodes asserted as types in the ellaboration.
                //This will remove the blank nodes.
                if(type.isURIResource()){
                    System.out.println(variable+" of type "+ type);
                    //add the individual as an instance of another class, not as a new individual
                    String nameEncoded = encode(Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+variable.getLocalName());
                    OntClass c = OPMWModel.createClass(type.getURI());
                    c.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+nameEncoded);
                }else{
                    System.out.println("ANON RESOURCE "+type.getURI()+" ignored");
                }
            }else{
                System.out.println(variable);
            }
            this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+variable.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName,
                        Constants.OPMW_PROP_IS_VARIABLE_OF_TEMPLATE);
            
            
        }
        //retrieval of the parameterVariables
        String queryParameterV = Queries.querySelectParameter();
        r = null;
        r = queryLocalWINGSTemplateModelRepository(queryParameterV);
        while(r.hasNext()){
            QuerySolution qs = r.next();
            Resource res = qs.getResource("?p");
//            Literal parValue = qs.getLiteral("?parValue");
            System.out.println(res);
            this.addIndividual(OPMWModel,templateName_+res.getLocalName(), Constants.OPMW_PARAMETER_VARIABLE, "Parameter variable "+res.getLocalName());
            //p-plan interop
            OntClass cVar = OPMWModel.createClass(Constants.P_PLAN_Variable);
            cVar.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_PARAMETER_VARIABLE+"/"+encode(templateName_+res.getLocalName()));
           
            //add the parameter value as an artifact too
            String aux = encode(Constants.CONCEPT_PARAMETER_VARIABLE+"/"+templateName_+res.getLocalName());
            OntClass cAux = OPMWModel.createClass(Constants.OPMW_WORKFLOW_TEMPLATE_ARTIFACT);//repeated tuples will not be duplicated
            cAux.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+aux);
            
            this.addProperty(OPMWModel,Constants.CONCEPT_PARAMETER_VARIABLE+"/"+templateName_+res.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName,                    
                        Constants.OPMW_PROP_IS_PARAMETER_OF_TEMPLATE);
            
            
        }

        //InputLinks == Used
        String queryInputLinks = Queries.queryInputLinks();
        r = null;
        r = queryLocalWINGSTemplateModelRepository(queryInputLinks);
        while(r.hasNext()){
            QuerySolution qs = r.next();
            Resource resVar = qs.getResource("?var");
            Resource resNode = qs.getResource("?dest");
            String role = qs.getLiteral("?role").getString();            
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNode.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                            Constants.OPMW_PROP_USES);
            //p-plan interop
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNode.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_INPUT);
            this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNode.getLocalName(),
                            Constants.P_PLAN_PROP_IS_INTPUT_VAR_OF);
            if(role!=null){
                System.out.println("Node "+resNode.getLocalName() +" Uses "+ resVar.getLocalName()+ " Role: "+role);
                //add the roles as subproperty of used. This triple should be on the ontology.
                this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNode.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                            Constants.PREFIX_EXTENSION+"usesAs_"+role);
                //link the property as a subproperty of Used
                this.createSubProperty(OPMWModel,Constants.OPMW_PROP_USES, Constants.PREFIX_EXTENSION+"usesAs_"+role);
                //description of the new property
                OntProperty propUsed = OPMWModel.getOntProperty(Constants.PREFIX_EXTENSION+"usesAs_"+role);
                propUsed.addLabel("Property that indicates that a resource has been used as a "+role, "EN");
            }
        }
        String queryInputLinksP = Queries.queryInputLinksP();
        r = null;
        r = queryLocalWINGSTemplateModelRepository(queryInputLinksP);
        while(r.hasNext()){
            QuerySolution qs = r.next();
            Resource resVar = qs.getResource("?var");
            Resource resNode = qs.getResource("?dest");
            String role = qs.getLiteral("?role").getString(); 
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNode.getLocalName(),
                        Constants.CONCEPT_PARAMETER_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                            Constants.OPMW_PROP_USES);
            //p-plan interop
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNode.getLocalName(),
                        Constants.CONCEPT_PARAMETER_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_INPUT);
            this.addProperty(OPMWModel,Constants.CONCEPT_PARAMETER_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNode.getLocalName(),
                            Constants.P_PLAN_PROP_IS_INTPUT_VAR_OF);
            if(role!=null){
                System.out.println("Node "+resNode.getLocalName() +" Uses "+ resVar.getLocalName()+ " Role: "+role);
                //add the roles as subproperty of used. This triple should be on the ontology.
                this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNode.getLocalName(),
                        Constants.CONCEPT_PARAMETER_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                            Constants.PREFIX_EXTENSION+"usesAs_"+role);
                //link the property as a subproperty of Used
                this.createSubProperty(OPMWModel,Constants.OPMW_PROP_USES, Constants.PREFIX_EXTENSION+"usesAs_"+role);
                OntProperty propUsed = OPMWModel.getOntProperty(Constants.PREFIX_EXTENSION+"usesAs_"+role);
                propUsed.addLabel("Property that indicates that a resource has been used as a "+role, "EN");
//                System.out.println(resVar.getLocalName() +" type "+ qs.getResource("?t").getURI());
            }
        }

        //OutputLInks == WasGeneratedBy
        String queryOutputLinks = Queries.queryOutputLinks();
        r = null;
        r = queryLocalWINGSTemplateModelRepository(queryOutputLinks);
        while(r.hasNext()){
            QuerySolution qs = r.next();
            Resource resVar = qs.getResource("?var");
            Resource resNode = qs.getResource("?orig");
            String role = qs.getLiteral("?role").getString();             
            this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNode.getLocalName(),
                        Constants.OPMW_PROP_IGB);
            //p-plan interop
            this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNode.getLocalName(),
                            Constants.P_PLAN_PROP_IS_OUTPUT_VAR_OF);
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNode.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_OUTPUT);            
            if(role!=null){
                System.out.println("Artifact "+ resVar.getLocalName()+" Is generated by node "+resNode.getLocalName()+" Role "+role);
                //add the roles as subproperty of used. This triple should be on the ontology.
                this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNode.getLocalName(),
                            Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+role);
                //link the property as a subproperty of WGB
                this.createSubProperty(OPMWModel,Constants.OPMW_PROP_IGB, Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+role);
                OntProperty propGenerated = OPMWModel.getOntProperty(Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+role);
                propGenerated.addLabel("Property that indicates that a resource has been generated as a "+role, "EN");
            }
        }
        //InOutLink == Used and WasGeneratedBy
        String queryInOutLinks = Queries.queryInOutLinks();
        r = null;
        r = queryLocalWINGSTemplateModelRepository(queryInOutLinks);
        while(r.hasNext()){
            QuerySolution qs = r.next();
            Resource resVar = qs.getResource("?var");
            Resource resNode = qs.getResource("?orig");
            String roleOrig = qs.getLiteral("?origRole").getString();
            Resource resNodeD = qs.getResource("?dest");
            String roleDest = qs.getLiteral("?destRole").getString();
            if(roleOrig!=null && roleDest!=null){
                System.out.println("Artifact "+ resVar.getLocalName()+" is generated by node "+resNode.getLocalName()
                        +" with role "+roleOrig+" and uses node "+resNodeD.getLocalName()
                        +" with role "+ roleDest);
            }
            //they are all data variables
            this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNode.getLocalName(),
                        Constants.OPMW_PROP_IGB);
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNodeD.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                            Constants.OPMW_PROP_USES);
            //p-plan interop
            this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNode.getLocalName(),
                            Constants.P_PLAN_PROP_IS_OUTPUT_VAR_OF);
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNode.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_OUTPUT);
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNodeD.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_INPUT);
            this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNodeD.getLocalName(),
                            Constants.P_PLAN_PROP_IS_INTPUT_VAR_OF);            
            if(roleOrig!=null){                
                this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNode.getLocalName(),
                            Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+roleOrig);
                //link the property as a subproperty of WGB
                this.createSubProperty(OPMWModel,Constants.OPMW_PROP_IGB, Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+roleOrig);
                OntProperty propGenerated = OPMWModel.getOntProperty(Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+roleOrig);
                propGenerated.addLabel("Property that indicates that a resource has been generated as a "+roleOrig, "EN");
            }
            if(roleDest!=null){
                //System.out.println("created role "+ Constants.PREFIX_ONTOLOGY_PROFILE+"used_"+roleDest.getLocalName());
                this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName_+resNodeD.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+templateName_+resVar.getLocalName(),
                            Constants.PREFIX_EXTENSION+"usesAs_"+roleDest);
                //link the property as a subproperty of Used
                this.createSubProperty(OPMWModel,Constants.OPMW_PROP_USES, Constants.PREFIX_EXTENSION+"usesAs_"+roleDest);
                OntProperty propUsed = OPMWModel.getOntProperty(Constants.PREFIX_EXTENSION+"usesAs_"+roleDest);
                propUsed.addLabel("Property that indicates that a resource has been used as a "+roleDest, "EN");
            }
        }
        /******************
         * FILE EXPORT. 
         ******************/        
        exportRDFFile(outFile, OPMWModel);
        
        
        //exporting the taxonomy 
        exportRDFFile2(taxonomy_export, Taxonomy_Export);
        
        
        return Constants.PREFIX_EXPORT_RESOURCE+""+Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+encode(templateName);
    }

/**
 * Method to transform a Wings execution to OPMW, PROV and P-Plan.
 * Note that this method will load the Workflow Instance and Workflow Expanded Template
 * from the data in the execution file. It is assumed that these URLs are accessible.
 * @param resultFile the Wings Execution File for this execution.
 * @param libraryFile the library file containing all the execution metadata.
 * @param modeFile the serialization of the data (e.g., RDF/XML)
 * @param outFilenameOPMW output name for the OPMW serialization
 * @param outFilenamePROV output name for the PROV serialization
 * @param suffix id to be added to identify uniquely certain resources
 * @return 
 * @throws Exception 
 */
    //public String transformWINGSResultsToOPMW(String resultFile, String libraryFile, String modeFile, String outFilenameOPMW, String outFilenamePROV){
    public String transformWINGSResultsToOPMW(String resultFile, String libraryFile, String modeFile, 
        String outFilenameOPMW, String outFilenamePROV, String suffix){
    		
    	//NEW ADDITION BY TIRTH**************//
    	//creating a new expandedTemplateModel that has the expanded Template File only
    	 if(ExpandedTemplateModel!=null){
    		 ExpandedTemplateModel.removeAll();//where we Expanded Template
         }
    	 ExpandedTemplateModel = ModelFactory.createOntologyModel();
    	
    	//NEW ADDITION BY TIRTH**************//
     	//creating a new TemplateModelforCondition that will have the template file and hence will be used for either having an expanded template or not.
    	 if(TemplateModelforCondition!=null){
    		 TemplateModelforCondition.removeAll();//where we Expanded Template
         }
    	 TemplateModelforCondition = ModelFactory.createOntologyModel();
    	 
    	
    	
        //clean previous transformations        
        if(WINGSExecutionResults!=null){
            WINGSExecutionResults.removeAll();//where we store the RDF to query
        }
        WINGSExecutionResults = ModelFactory.createOntologyModel();//ModelFactory.createDefaultModel();
        if(OPMWModel!=null){
            OPMWModel.removeAll(); //where we store the new RDF we create
        }
        OPMWModel = ModelFactory.createOntologyModel(); //inicialization of the model
        if(PROVModel!=null){
            PROVModel.removeAll();
        }
        PROVModel=ModelFactory.createOntologyModel();
        //load the execution library file
        this.loadResultFileToLocalRepository(libraryFile, modeFile);
        //load the execution file
        this.loadResultFileToLocalRepository(resultFile, modeFile);        
        //now, extract the expanded template and the workflow instance. Load them as well
        String queryIntermediateTemplates = Queries.queryIntermediateTemplates();
        //the template is only needed to connect the execution account to itself.
        ResultSet r = queryLocalWINGSResultsRepository(queryIntermediateTemplates);
        String templateName = "", templateURI, expandedTemplateURI,expandedTemplateName="";
        if(r.hasNext()){
            QuerySolution qs = r.next();
            Resource template = qs.getResource("?template");
            templateURI = template.getURI();
            templateName = template.getLocalName();
            String wfInstance = qs.getResource("?wfInstance").getURI();
            expandedTemplateURI = qs.getResource("?expandedTemplate").getURI();
            expandedTemplateName=qs.getResource("?expandedTemplate").getLocalName();

            
            ////NEW ADDITION BY TIRTH**************//
            //loading the expandedTemplate Model here
            this.loadExpandedTemplateFileToLocalRepository(expandedTemplateURI, modeFile);
            
        ////NEW ADDITION BY TIRTH**************//
            //loading the Template Condition Model here
            this.loadedTemplateFileCondition(templateURI, modeFile);
            System.out.println("---------------------");
            System.out.println("PRINTING THE TEMPLATE FILE");
            //TemplateModelforCondition.write(System.out,"RDF/XML");
            System.out.println("ENDING THE PRINTING OF TEMPLATE FILE");
            
            System.out.println("expanded template URI : "+expandedTemplateURI);
            this.loadResultFileToLocalRepository(expandedTemplateURI, modeFile);
            System.out.println("Loaded the expanded template successfully ...");
            this.loadResultFileToLocalRepository(wfInstance, modeFile);
           // System.out.println(wfInstance);
            System.out.println("Loaded the workflow instance successfully ...");
        }else{
            System.err.println("The template, expanded template or workflow instance are not available. ");
            return "";
        }
        String date = ""+new Date().getTime();//necessary to add unique nodeId identifiers
        if(suffix == null){
          suffix = date;
        }
        //add the account of the current execution
        //this.addIndividual(OPMWModel,"Account"+date, Constants.OPMW_WORKFLOW_EXECUTION_ACCOUNT,"Execution account created on "+date);
        this.addIndividual(OPMWModel,"Account-"+suffix, Constants.OPMW_WORKFLOW_EXECUTION_ACCOUNT,"Execution account created on "+date);
        //we also assert that it is an account
        //String accname = encode(Constants.CONCEPT_WORKFLOW_EXECUTION_ACCOUNT+"/"+"Account"+date);
        String accname = encode(Constants.CONCEPT_WORKFLOW_EXECUTION_ACCOUNT+"/"+"Account-"+suffix);
        OntClass cAux = OPMWModel.createClass(Constants.OPM_ACCOUNT);
        cAux.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+accname);
        
        /*************************
         * PROV-O INTEROPERABILITY
         *************************/
        OntClass d = PROVModel.createClass(Constants.PROV_BUNDLE);
        d.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+accname);
        
        //relation between the account and the template
        this.addProperty(OPMWModel,accname,
                Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName,
                    Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE);
        
        
        boolean ans=ExportExpandedTemplate();
        System.out.println("What is the final thing should or should i not? "+ans);
        
        //p-plan interop
        this.addProperty(PROVModel,accname,
                Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName,
                    Constants.PROV_WAS_DERIVED_FROM);
        
        //AT THIS STAGE WE HAVE GOT THE EXPANDED TEMPLATE NAME AND URI AND WE ARE STARTING TO ACQUIRE THE EXPANDED TEMPLATE
        //AND TRYING TO REPLICATE THE ELABORATE TEMPLATE FROM THE WINGSEXECUTIONRESULTS MODEL THAT HAS LOADED THE EXPANDED
        //TEMPLATE FILE
        
        /********************************************************/
        /*********************ADDITION BY TIRTH***************/
        /************** EXPANDED TEMPLATE CREATION CODE **************/
        /********************************************************/
        //check the condition and only then go for creating the expanded template
        String newExpandedTemplateName="";
        if(ans==true)
        	newExpandedTemplateName=createExpandedTemplate(accname,expandedTemplateName,expandedTemplateURI,templateName);
        else
        	System.out.println("SINCE ALL THE TEMPLATE PROCESSES ARE CONCRETE, NO EXPANDED TEMPLATE IS CREATED");
              
        /********************************************************/
        /************** EXPANDED TEMPLATE CREATION CODE ENDS **************/
        /********************************************************/
        
        
        
        //account metadata: start time, end time, user, license and status.                
        String queryMetadata = Queries.queryExecutionMetadata();
        String executionFile = null, user = null,
                status = null, startT = null, endT = null, license = null, tool = null;
        
        //we need the template name to reference the nodes in the wf exec.
        /********************************************************/
        /************** EXECUTION ACCOUNT METADATA **************/
        /********************************************************/
        r = queryLocalWINGSResultsRepository(queryMetadata);
//        Resource execDiagram = null; //the newer version doesn't have this info
//        Resource templDiagram = null;
        if(r.hasNext()){
            QuerySolution qs = r.next();
            executionFile = qs.getResource("?exec").getNameSpace();
            status = qs.getLiteral("?status").getString();
            startT = qs.getLiteral("?startT").getString();
            Literal e = qs.getLiteral("?endT");
//            execDiagram = qs.getResource("?execDiagram");
//            templDiagram = qs.getResource("?templDiagram");
            Literal t = qs.getLiteral("?tool");
            Literal u = qs.getLiteral("?user");
            Literal l = qs.getLiteral("?license");
            if(e!=null){
                endT = e.getString();
            }else{
                endT="Not available";
            }
            if(t!=null){
                tool = t.getString();
            }else{
                tool = "http://wings-workflows.org/";//default
            }
            if(u!=null){
                user = u.getString();
            }else{
                //can be extracted from the execution file
                try{
                    user = executionFile;
                    user = user.substring(user.indexOf("users/"), user.length());
                    user = user.split("/",3)[1];
                }catch(Exception ex){
                    user = "unknown";
                }
            }
            if(l!=null){
                license = l.getString();
            }else{
                license = "http://creativecommons.org/licenses/by-sa/3.0/";//default
            }
            //engine = qs.getLiteral("?engine").getString();
            System.out.println("Wings results file:"+executionFile+"\n"
                   // + "User: "+user+", \n"
                    + "Workflow Template: "+templateName+"\n"
                    + "status: "+status+"\n"
                    + "startTime: "+startT+"\n"
                    + "endTime: "+endT);
        }
        
        //metadata about the execution: Agent
        if(user!=null){
            this.addIndividual(OPMWModel,user, Constants.OPM_AGENT, "Agent "+user);//user HAS to have a URI
            this.addProperty(OPMWModel,Constants.CONCEPT_AGENT+"/"+user,
                accname,
                    Constants.OPM_PROP_ACCOUNT);
            
            /*************************
            * PROV-O INTEROPERABILITY
            *************************/
           String agEncoded = encode(Constants.CONCEPT_AGENT+"/"+user);
           OntClass ag = PROVModel.createClass(Constants.PROV_AGENT);
           ag.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+agEncoded);
        }
        
        this.addDataProperty(OPMWModel,accname,
                executionFile,Constants.OPMW_DATA_PROP_HAS_ORIGINAL_LOG_FILE,
                        XSDDatatype.XSDanyURI);
        
        /*************************
         * PROV-O INTEROPERABILITY
         *************************/ 
        //hasOriginalLogFile subprop of hadPrimary Source
        this.addDataProperty(PROVModel,accname,
                executionFile,Constants.PROV_HAD_PRIMARY_SOURCE,
                        XSDDatatype.XSDanyURI);
        
        //status
        this.addDataProperty(OPMWModel,accname,
                status, Constants.OPMW_DATA_PROP_HAS_STATUS);
        //startTime
        this.addDataProperty(OPMWModel,accname,
                startT,Constants.OPMW_DATA_PROP_OVERALL_START_TIME,
                    XSDDatatype.XSDdateTime);
        //endTime
        this.addDataProperty(OPMWModel,accname,
                endT,Constants.OPMW_DATA_PROP_OVERALL_END_TIME,
                    XSDDatatype.XSDdateTime);
        if(license!=null){
            this.addDataProperty(OPMWModel,accname,
                license,Constants.DATA_PROP_RIGHTS,
                    XSDDatatype.XSDanyURI);
        }
        if(tool!=null){
            this.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName,
                tool,Constants.OPMW_DATA_PROP_CREATED_IN_WORKFLOW_SYSTEM,
                    XSDDatatype.XSDanyURI);
            /*************************
            * PROV-O INTEROPERABILITY
            *************************/ 
            //the template is a prov:Plan
            OntClass plan = PROVModel.createClass(Constants.PROV_PLAN);
            plan.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+encode(templateName));
            //createdIn wf system subprop of wasAttributedTo
            this.addDataProperty(PROVModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName,
                tool,Constants.PROV_WAS_ATTRIBUTED_TO,
                    XSDDatatype.XSDanyURI);
            //the run wasInfluencedBy the template
            this.addProperty(PROVModel,accname,
                Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName,
                    Constants.PROV_WAS_INFLUENCED_BY);
        }
       // String newexpandedtemplatename=newExpandedTemplateName.substring(0,newExpandedTemplateName.indexOf('-'));
        
        /********************************************************/
        /********************* NODE LINKING**********************/
        /********************************************************/
        //query for detecting steps, their inputs and their outputs
        ArrayList<String> arrForExpandedTemplate1=new ArrayList<>();
        ArrayList<String> arrForExpandedTemplate2=new ArrayList<>();
        String queryStepsAndIO = Queries.queryStepsAndMetadata();
        r = queryLocalWINGSResultsRepository(queryStepsAndIO);
        String stepName, sStartT = null, sEndT = null, sStatus, sCode, derivedFrom = null;        
        while (r.hasNext()){
            QuerySolution qs = r.next();
            
            //start time and end time could be optional.
            stepName = qs.getResource("?step").getLocalName();
            Literal stLiteral = qs.getLiteral("?startT");
            if (stLiteral!=null){
                sStartT = stLiteral.getString();
            }
            Literal seLiteral = qs.getLiteral("?endT");
            if(seLiteral!=null){
                sEndT = seLiteral.getString();
            }
            sStatus = qs.getLiteral("?status").getString();
            sCode = qs.getLiteral("?code").getString();
            try{
                derivedFrom = qs.getResource("?derivedFrom").getLocalName();
            }catch(Exception e){
                //if we don't have the derivedFrom relationship, we assume that
                //the node name on the template is the same as in the exp template
                derivedFrom = stepName;
            }
            System.out.println("Derived from = "+derivedFrom);
            System.out.println("after derived from "+stepName +"\n\t "+ sStartT+"\n\t "+sEndT+"\n\t "+sStatus+"\n\t "+sCode);
            //add each step with its metadata to the model Start and end time are reused from prov.
            this.addIndividual(OPMWModel,stepName+date, Constants.OPMW_WORKFLOW_EXECUTION_PROCESS, "Execution process "+stepName);
            //add type opmv:Process as well
            String auxP = encode(Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date);
            OntClass cP = OPMWModel.createClass(Constants.OPM_PROCESS);
            cP.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+auxP);
            
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date,
                Constants.CONCEPT_AGENT+"/"+user,
                    Constants.OPM_PROP_WCB);
            
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date,
                accname,
                    Constants.OPM_PROP_ACCOUNT);
            
            /*************************
             * PROV-O INTEROPERABILITY
             *************************/
            OntClass d1 = PROVModel.createClass(Constants.PROV_ACTIVITY);
            d1.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+auxP);
            
            this.addProperty(PROVModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date,
                Constants.CONCEPT_AGENT+"/"+user,
                    Constants.PROV_WAS_ASSOCIATED_WITH);

            //metadata
            if(sStartT!=null){
                this.addDataProperty(PROVModel, 
                        Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date, 
                        sStartT, 
                        Constants.PROV_STARTED_AT_TIME,
                        XSDDatatype.XSDdateTime);
            }
            if(sEndT!=null){
                this.addDataProperty(PROVModel, 
                        Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date, 
                        sEndT, 
                        Constants.PROV_ENDED_AT_TIME,
                        XSDDatatype.XSDdateTime);
            }
            this.addDataProperty(OPMWModel, 
                    Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date, 
                    sStatus, 
                    Constants.OPMW_DATA_PROP_HAS_STATUS);
            
            //add the code binding as an executable component            
            Resource blankNode = OPMWModel.createResource();
            blankNode.addProperty(OPMWModel.createOntProperty(Constants.OPMW_DATA_PROP_HAS_LOCATION),
                    sCode).
                    addProperty(OPMWModel.createOntProperty(Constants.RDFS_LABEL), 
                            "Executable Component associated to "+stepName);
            String procURI = Constants.PREFIX_EXPORT_RESOURCE+ encode(Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date);
            OPMWModel.getResource(procURI).
                    addProperty(OPMWModel.createOntProperty(Constants.OPMW_PROP_HAS_EXECUTABLE_COMPONENT), 
                            blankNode);
            /*************************
            * PROV-O INTEROPERABILITY (commented because it makes it more difficult to understand. It is done through the hasExecutableComponent relationship
            *************************/ 
            /*Resource bnodeProv = PROVModel.createResource();
            bnodeProv.addProperty(PROVModel.createOntProperty(Constants.PROV_AT_LOCATION),
                    sCode).
                    addProperty(PROVModel.createOntProperty(Constants.RDFS_LABEL), 
                            "Executable Component associated to "+stepName);
            PROVModel.getResource(procURI).
                    addProperty(PROVModel.createOntProperty(Constants.PROV_USED), 
                            bnodeProv);*/
            
            //link node  to the process templates
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date,
                    Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName+"_"+derivedFrom,
                        Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE_PROCESS);
           
            
            
            
	        //NEW ADDITIONS BY TIRTH:
            if(ans==true)
            {
	        this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date,
	                    Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+stepName,
	                        Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE_PROCESS);
            }
            

           
            //p-plan interop
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date,
                    Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+templateName+"_"+derivedFrom,
                        Constants.P_PLAN_PROP_CORRESPONDS_TO_STEP);
        }

        //annotation of inputs
        String getInputs = Queries.queryStepInputs();
        r = queryLocalWINGSResultsRepository(getInputs);
        String step, input, inputBinding;
        while(r.hasNext()){
            QuerySolution qs = r.next();
            step = qs.getResource("?step").getLocalName();
            input = qs.getResource("?input").getLocalName();
            inputBinding = qs.getLiteral("?iBinding").getString();
            System.out.println("Step: "+step+" used input "+input+" with data binding: "+inputBinding);            
            //no need to add the variable individual now because the types are going to be added later
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+step+date,
                    Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+input+date,
                        Constants.OPM_PROP_USED);
            this.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+input+date,
                    inputBinding,
                        Constants.OPMW_DATA_PROP_HAS_LOCATION, XSDDatatype.XSDanyURI);
            /*************************
            * PROV-O INTEROPERABILITY
            *************************/ 
            this.addProperty(PROVModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+step+date,
                    Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+input+date,
                        Constants.PROV_USED);
            //hasLocation subrpop of atLocation
            this.addDataProperty(PROVModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+input+date,
                    inputBinding,
                        Constants.PROV_AT_LOCATION, XSDDatatype.XSDanyURI);
            
        }
        
        //parameters are separated (in expanded template). 
        String getParams = Queries.querySelectStepParameterValues();
        r = queryLocalWINGSResultsRepository(getParams);
        String paramName, paramvalue, derived = null;
        while(r.hasNext()){
            QuerySolution qs = r.next();
            step = qs.getResource("?step").getLocalName();
            paramName = qs.getResource("?param").getLocalName();
            paramvalue = qs.getLiteral("?value").getString();
            Resource res = qs.getResource("?derivedFrom");
            if(res!=null){
                derived = res.getLocalName();
            }
            System.out.println("step "+step +"used param: "+paramName+" with value: "+paramvalue);
            this.addIndividual(OPMWModel, paramName+date,
                    Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT, "Parameter with value: "+paramvalue);
            String auxParam = encode(Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date);
            OntClass cParam = OPMWModel.createClass(Constants.OPM_ARTIFACT);
            cParam.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+auxParam);
            this.addDataProperty(OPMWModel, 
                    Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date, 
                    paramvalue, 
                    Constants.OPMW_DATA_PROP_HAS_VALUE);
            this.addProperty(OPMWModel, 
                    Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+step+date, 
                    Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date, 
                    Constants.OPM_PROP_USED);
            //link to template
            if(res!=null){
                this.addProperty(OPMWModel,
                        Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date,
                        Constants.CONCEPT_PARAMETER_VARIABLE+"/"+templateName+"_"+derived,
                        Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE_ARTIFACT);
                
                //NEW ADDITIONS BY TIRTH
                if(ans==true)
                {
                this.addProperty(OPMWModel,
                        Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date,
                        Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+paramName,
                        Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE_ARTIFACT);
                }
                

                
                this.addProperty(OPMWModel,
                        Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date,
                        Constants.CONCEPT_PARAMETER_VARIABLE+"/"+templateName+"_"+derived,
                        Constants.P_PLAN_PROP_CORRESPONDS_TO_VAR);
            }
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date,
                accname,
                    Constants.OPM_PROP_ACCOUNT);
            /*************************
            * PROV-O INTEROPERABILITY
            *************************/ 
            String auxP = encode(Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date);
            OntClass cP = PROVModel.createClass(Constants.PROV_ENTITY);
            cP.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+auxP);
            this.addDataProperty(PROVModel, 
                    Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date, 
                    paramvalue,
                    Constants.PROV_VALUE);            
            this.addProperty(PROVModel, 
                    Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+step+date, 
                    Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date, 
                    Constants.PROV_USED);            
        }
        

        
        
        
        
        
        //annotation of outputs
        String getOutputs = Queries.queryStepOutputs();
        r = queryLocalWINGSResultsRepository(getOutputs);
        String output, outputBinding;
        while(r.hasNext()){
            QuerySolution qs = r.next();
            step = qs.getResource("?step").getLocalName();
            output = qs.getResource("?output").getLocalName();
            outputBinding = qs.getLiteral("?oBinding").getString();
            System.out.println("Step: "+step+" has output "+output+" with data binding: "+outputBinding);
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+output+date,
                    Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+step+date,
                        Constants.OPM_PROP_WGB);
            this.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+output+date,
                    outputBinding,
                        Constants.OPMW_DATA_PROP_HAS_LOCATION, XSDDatatype.XSDanyURI);
            /*************************
            * PROV-O INTEROPERABILITY
            *************************/ 
            this.addProperty(PROVModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+output+date,
                    Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+step+date,
                        Constants.PROV_WGB);
            //hasLocation subrpop of atLocation
            this.addDataProperty(PROVModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+output+date,
                    outputBinding,
                        Constants.PROV_AT_LOCATION, XSDDatatype.XSDanyURI);
        }
        //annotation of variable metadata

        
        
        String getVarMetadata = Queries.queryDataVariablesMetadata();
        r = queryLocalWINGSResultsRepository(getVarMetadata);
        String var, prop, obj, objName = null;
        while(r.hasNext()){
            QuerySolution qs = r.next();
            var = qs.getResource("?variable").getLocalName();
            prop = qs.getResource("?prop").getURI();
            try{
                //types
                Resource rObj = qs.getResource("?obj");
                obj = rObj.getURI();
                objName = rObj.getLocalName();
            }catch(Exception e){
                //basic metadata
                obj = qs.getLiteral("?obj").getString();
            }
//            System.out.println("Var "+var+" <"+prop+ "> "+ obj);
            this.addIndividual(OPMWModel, var+date,
                    Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT, 
                    "Workflow execution artifact: "+var+date);
            //redundancy: add it as a opm:Artifact as well
            String auxP = encode(Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date);
            OntClass cP = OPMWModel.createClass(Constants.OPM_ARTIFACT);
            cP.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+auxP);
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date,
                accname,
                    Constants.OPM_PROP_ACCOUNT);
            //link to template
            if(prop.contains("derivedFrom")){
                //this relationship ensures that we are doing the linking correctly.
                //if it doesn't exist we avoid linking to the template.
                this.addProperty(OPMWModel,
                        Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date,
                        Constants.CONCEPT_DATA_VARIABLE+"/"+templateName+"_"+objName,
                        Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE_ARTIFACT);
                
  
                
                
              //NEW ADDITIONS BY TIRTH
                if(ans==true)
                {
                this.addProperty(OPMWModel,
                        Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date,
                        Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+var,
                        Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE_ARTIFACT);
                }
          
                
                //p-plan interop
                this.addProperty(OPMWModel,
                        Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date,
                        Constants.CONCEPT_DATA_VARIABLE+"/"+templateName+"_"+objName,
                        Constants.P_PLAN_PROP_CORRESPONDS_TO_VAR);
            }else
            //metadata
            if(prop.contains("http://www.w3.org/2000/01/rdf-schema#type")){
                //the objects are resources in this case
                //String auxP = encode(Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date);
                cP = OPMWModel.createClass(obj);
                cP.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+auxP);
            }
            else if(prop.contains("hasSize")){
                this.addDataProperty(OPMWModel,
                    Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date,
                    obj,
                    Constants.OPMW_DATA_PROP_HAS_SIZE);
            }else if(prop.contains("hasDataBinding")||prop.contains("isVariableOfPlan")){
                //do nothing! we have already dealt with data binding before
                //regarding the p-plan, i don't add it to avoid confusion
            }else{
                //custom wings property: preserve it.
                this.addDataProperty(OPMWModel,
                    Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date,
                    obj,
                    prop);
            }
            
            /*************************
            * PROV-O INTEROPERABILITY
            *************************/ 
            
            cP = PROVModel.createClass(Constants.PROV_ENTITY);
            cP.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+auxP);
        }
        

        
        /***********************************************************************************
         * FILE EXPORT 
         ***********************************************************************************/        
        exportRDFFile(outFilenameOPMW, OPMWModel);
        exportRDFFile(outFilenamePROV, PROVModel);
        return (Constants.PREFIX_EXPORT_RESOURCE+accname);
    }
    
    public String getRunUrl(String suffix) {
        String accname = encode(Constants.CONCEPT_WORKFLOW_EXECUTION_ACCOUNT+"/"+"Account-"+suffix);
        return (Constants.PREFIX_EXPORT_RESOURCE+accname);
    }
    
    public String getTemplateUrl(String templateName) {
        return Constants.PREFIX_EXPORT_RESOURCE+""+Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+
            encode(templateName);
    }
    
    public void setPublishExportPrefix(String prefix) {
      Constants.PREFIX_EXPORT_RESOURCE = prefix;
    }

    /**
     * Function to export the stored model as an RDF file, using ttl syntax
     * @param outFile name and path of the outFile must be created.
     */
    private void exportRDFFile(String outFile, OntModel model){
        OutputStream out;
        try {
            out = new FileOutputStream(outFile);
            model.write(out,"TURTLE");
            //model.write(out,"RDF/XML");
            out.close();
        } catch (Exception ex) {
            System.out.println("Error while writing the model to file "+ex.getMessage());
        }
    }
    private void exportRDFFile2(String outFile, OntModel model){
        OutputStream out;
        try {
            out = new FileOutputStream(outFile);
            //model.write(out,"TURTLE");
            model.write(out,"RDF/XML");
            out.close();
        } catch (Exception ex) {
            System.out.println("Error while writing the model to file "+ex.getMessage());
        }
    }
    /**
     * FUNCTIONS TO ADD RELATIONSHIPS TO THE MODEL
     */

    /**
     * Funtion to insert an individual as an instance of a class. If the class does not exist, it is created.
     * @param individualId Instance id. If exists it won't be created.
     * @param classURL URL of the class from which we want to create the instance
     */
    private void addIndividual(OntModel m,String individualId, String classURL, String label){
        String nameOfIndividualEnc = encode(getClassName(classURL)+"/"+individualId);
        OntClass c = m.createClass(classURL);
        c.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+nameOfIndividualEnc);
        if(label!=null){
            this.addDataProperty(m,nameOfIndividualEnc,label,Constants.RDFS_LABEL);
        }
    }
    
    

    /**
     * Funtion to add a property between two individuals. If the property does not exist, it is created.
     * @param orig Domain of the property (Id, not complete URI)
     * @param dest Range of the property (Id, not complete URI)
     * @param property URI of the property
     */
    private void addProperty(OntModel m, String orig, String dest, String property){
        OntProperty propSelec = m.createOntProperty(property);
        Resource source = m.getResource(Constants.PREFIX_EXPORT_RESOURCE+ encode(orig) );
        Individual instance = (Individual) source.as( Individual.class );
        if(dest.contains("http://")){//it is a URI
            instance.addProperty(propSelec,dest);            
        }else{//it is a local resource
            instance.addProperty(propSelec, m.getResource(Constants.PREFIX_EXPORT_RESOURCE+encode(dest)));
        }
        //System.out.println("Creada propiedad "+ propiedad+" que relaciona los individuos "+ origen + " y "+ destino);
    }

    /**
     * Function to add dataProperties. Similar to addProperty
     * @param origen Domain of the property (Id, not complete URI)
     * @param literal literal to be asserted
     * @param dataProperty URI of the data property to assign.
     */
    private void addDataProperty(OntModel m, String origen, String literal, String dataProperty){
        OntProperty propSelec;
        //lat y long son de otra ontologia, tienen otro prefijo distinto
        propSelec = m.createDatatypeProperty(dataProperty);
        //propSelec = (modeloOntologia.getResource(dataProperty)).as(OntProperty.class);
        Resource orig = m.getResource(Constants.PREFIX_EXPORT_RESOURCE+ encode(origen) );
        m.add(orig, propSelec, literal); 
    }

    /**
     * Function to add dataProperties. Similar to addProperty
     * @param m Model of the propery to be added
     * @param origen Domain of the property
     * @param dato literal to be asserted
     * @param dataProperty URI of the dataproperty to assert
     * @param tipo type of the literal (String, int, double, etc.).
     */
    private void addDataProperty(OntModel m, String origen, String dato, String dataProperty,RDFDatatype tipo) {
        OntProperty propSelec;
        //lat y long son de otra ontologia, tienen otro prefijo distinto
        propSelec = m.createDatatypeProperty(dataProperty);
        Resource orig = m.getResource(Constants.PREFIX_EXPORT_RESOURCE+ encode(origen));
        m.add(orig, propSelec, dato,tipo);
    }

    /**
     * Function to add a property as a subproperty of the other.
     * @param uriProp
     * @param uriSubProp
     */
    private void createSubProperty(OntModel m, String uriProp, String uriSubProp){
        if(uriProp.equals(uriSubProp))return;
        OntProperty propUsed = m.getOntProperty(uriProp);
        OntProperty propRole = m.getOntProperty(uriSubProp);
        propUsed.addSubProperty(propRole);
    }

    /**
     * Encoding of the name to avoid any trouble with spacial characters and spaces
     * @param name
     */
    private String encode(String name){
        name = name.replace("http://","");
        String prenom = name.substring(0, name.indexOf("/")+1);
        //remove tabs and new lines
        String nom = name.replace(prenom, "");
        if(name.length()>255){
            try {
                nom = MD5.MD5(name);
            } catch (Exception ex) {
                System.err.println("Error when encoding in MD5: "+ex.getMessage() );
            }
        }        

        nom = nom.replace("\\n", "");
        nom = nom.replace("\n", "");
        nom = nom.replace("\b", "");
        //quitamos "/" de las posibles urls
        nom = nom.replace("/","_");
        nom = nom.replace("=","_");
        nom = nom.trim();
        //espacios no porque ya se urlencodean
        //nom = nom.replace(" ","_");
        //a to uppercase
        nom = nom.toUpperCase();
        try {
            //urlencodeamos para evitar problemas de espacios y acentos
            nom = new URI(null,nom,null).toASCIIString();//URLEncoder.encode(nom, "UTF-8");
        }
        catch (Exception ex) {
            try {
                System.err.println("Problem encoding the URI:" + nom + " " + ex.getMessage() +". We encode it in MD5");
                nom = MD5.MD5(name);
                System.err.println("MD5 encoding: "+nom);
            } catch (Exception ex1) {
                System.err.println("Could not encode in MD5:" + name + " " + ex1.getMessage());
            }
        }
        return prenom+nom;
    }

    /**
     * Method to retrieve the name of a URI class objet
     * @param classAndVoc URI from which retrieve the name
     * @return 
     */
    private String getClassName(String classAndVoc){
        if(classAndVoc.contains(Constants.PREFIX_DCTERMS))return classAndVoc.replace(Constants.PREFIX_DCTERMS,"");
        else if(classAndVoc.contains(Constants.PREFIX_FOAF))return classAndVoc.replace(Constants.PREFIX_FOAF,"");
        else if(classAndVoc.contains(Constants.PREFIX_OPMO))return classAndVoc.replace(Constants.PREFIX_OPMO,"");
        else if(classAndVoc.contains(Constants.PREFIX_OPMV))return classAndVoc.replace(Constants.PREFIX_OPMV,"");
        else if(classAndVoc.contains(Constants.PREFIX_RDFS))return classAndVoc.replace(Constants.PREFIX_RDFS,"");
        else if(classAndVoc.contains(Constants.PREFIX_OPMW))return classAndVoc.replace(Constants.PREFIX_OPMW,"");
//        else if(classAndVoc.contains(ACDOM))return classAndVoc.replace(ACDOM,"");
//        else if(classAndVoc.contains(DCDOM))return classAndVoc.replace(DCDOM,"");
        else return null;
    }
    
    /**
     * Function to determine whether a run has already been published or not.
     * If the run has been published, it should not be republished again.
     * @param endpointURL URL of the repository where we store the runs
     * @param runURL URL of the physical file of containing the run.
     * @return True if the run has been published. False in other case.
     */
    public boolean isRunPublished(String endpointURL, String runURL){
        String query = Queries.queryIsTheRunAlreadyPublished(runURL);        
        QueryExecution qe = QueryExecutionFactory.sparqlService(endpointURL, query);
        ResultSet rs = qe.execSelect();
        return rs.hasNext();        
    }
    
    public Gen InputOutputGenerator(ResultSet replica,String classNamegiven)
    {
    	HashSet<String> input=new HashSet<>();
    	HashSet<String> output=new HashSet<>();
    while(replica.hasNext()){
        QuerySolution qs = replica.next();
        Resource res = qs.getResource("?n");
        Resource comp = qs.getResource("?c");
        Resource typeComp = qs.getResource("?typeComp");
        Literal rule = qs.getLiteral("?rule");
        Literal isConcrete = qs.getLiteral("?isConcrete");
        
        Resource inport = qs.getResource("?inport");
        Resource outport = qs.getResource("?outport");
        

        
        
      
        //------------ADDITION BY TIRTH-----------------
        //obtaining the className
        String className="";
        int indexOf=typeComp.toString().indexOf('#');
        className=typeComp.toString().substring(indexOf+1,typeComp.toString().length());
        
        
        if(className.equals(classNamegiven))
        {
        	
        	  input.add(inport.getLocalName());
              output.add(outport.getLocalName());
        }
      
        
        
        
      //------------ADDITION BY TIRTH-----------------
        //obtaining the domainName
        
        
        


    }
    Gen g=new Gen();
    g.input=input.size();
    g.output=output.size();
    g.className=classNamegiven;
    return g;
    
    }
    
    //function to check to export the expanded template or not 
    public boolean ExportExpandedTemplate()
    {
        String queryNodes = Queries.queryNodesforTemplateCondition();
        ResultSet r = null;
        r = queryConditionTemplateModel(queryNodes);
        System.out.println("im here");
        while(r.hasNext()){
     	   System.out.println("im inside boolean condition");
            QuerySolution qs = r.next();
            Resource res = qs.getResource("?n");
            Resource comp=qs.getResource("?c");
            Resource cb=qs.getResource("?cb");
            Literal isConcrete = qs.getLiteral("?isConcrete");
            	if(isConcrete==null)
            		return true;
        }
        return false;
    }
    
    
    public String createExpandedTemplate(String accname,String expandedTemplateName,String expandedTemplateURI,String templateName)
    {
 	   System.out.println("expanded template name: "+expandedTemplateName);

 	   //creates a new expandedtemplatename for unique identification based on the component names 
 	   ///////////////////
 	   ArrayList<String> componentNames = new ArrayList<>();
        String nodes = Queries.queryNodesforExpandedTemplate();
        ResultSet expandedComps=null;
        //ExpandedTemplateModel.write(System.out,"RDF/XML");
        expandedComps = queryLocalWINGSResultsRepository(nodes);
       
        while(expandedComps.hasNext()){
            QuerySolution qs = expandedComps.next();
            Resource res = qs.getResource("?n");
            componentNames.add(res.getLocalName());
        }
        //SORT THE ARRAYLIST FOR THE NAMES OF THE COMPONENTS FOR FURTHER CONSISTENCY
        Collections.sort(componentNames);
        StringBuilder sb=new StringBuilder();
        int indexer=expandedTemplateName.indexOf('-');
 	   //sb.append(expandedTemplateName.substring(0, indexer)+"_");
        for(String s:componentNames)
     	   sb.append(s+"_"); 
        String newExpandedTemplateName=sb.toString().substring(0,sb.toString().length()-1);
        System.out.println(newExpandedTemplateName);
        try{
        newExpandedTemplateName=MD5(newExpandedTemplateName);
        }
        catch(Exception e)
        {}
        newExpandedTemplateName=expandedTemplateName.substring(0, indexer)+"_"+newExpandedTemplateName;
        System.out.println("final expandedtemplatename is "+newExpandedTemplateName);
 	   ///////////////////
 	   
 	 //capturing the relationship between the execution account and the expanded template
        this.addProperty(OPMWModel, accname, Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName, Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE);
        this.addProperty(OPMWModel, Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName, Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName, Constants.OPMW_PROP_IS_IMPLEMENTATION_OF_TEMPLATE);
  //    
        
        //CAPTURING THE EXPANDED TEMPLATE AS A GRAPH, CAPTURING THE VERSION NUMBER, AND DATA PROP FOR NATIVE SYSTEM TEMPLATE
        //ADDED A PROPERTY FOR CONNECTING THE EXECUTION ACCOUNT TO THE EXPANDED TEMPLATE AND THE EXPANDED TEMPLATE TO THE TEMPLATE
        System.out.println("--------------------------");
        System.out.println("--------------------------");
        System.out.println("EXPANDED TEMPLATE STARTS");
        
        String queryNameWfTemplate = Queries.queryNameWfTemplate();
        String templateName_ = null;
        ResultSet r1 = queryLocalWINGSResultsRepository(queryNameWfTemplate);
        if(r1.hasNext()){//there should be just one local name per template
            QuerySolution qs = r1.next();
            
            Literal v = qs.getLiteral("?ver");
            
            //add the expanded template as a provenance graph
            this.addIndividual(OPMWModel,newExpandedTemplateName, Constants.OPMW_WORKFLOW_EXPANDED_TEMPLATE, newExpandedTemplateName);
              
            
            
            //P-PLAN FOR EXPANDED TEMPLATE
            OntClass cParam = OPMWModel.createClass(Constants.P_PLAN_PLAN);
            cParam.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+encode(newExpandedTemplateName));

            
            
            if(v!=null){
                this.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName,""+ v.getInt(),
                        Constants.OPMW_DATA_PROP_VERSION_NUMBER, XSDDatatype.XSDint);
            }
            //add the uri of the original log file (native system template)
            this.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName, 
                    expandedTemplateURI,Constants.OPMW_DATA_PROP_HAS_NATIVE_SYSTEM_TEMPLATE, XSDDatatype.XSDanyURI);
            
        }    
        System.out.println("EXPANDED TEMPLATE ENDS");
        
        
        //THE EXPANDED TEMPLATE ONLY HAS METADATA-CONTRIBUTOR WHICH IS CAPTURED BELOW
        System.out.println("--------------------------");
        System.out.println("--------------------------");
        System.out.println("METADATA STARTS");
        String queryMetadataforExandedTemplate = Queries.queryMetadataforExpandedTemplate();
        r1 = null;
        r1 = queryLocalWINGSResultsRepository(queryMetadataforExandedTemplate);
        
        while(r1.hasNext())
        {
        	System.out.println("inside for the contributor");
            QuerySolution qs = r1.next();
            Literal contrib = qs.getLiteral("?contrib");
            
            if(contrib!=null){
            	System.out.println("contributor is:"+contrib.getString());
                this.addIndividual(OPMWModel,contrib.getString(), Constants.OPM_AGENT,"Agent "+contrib.getString());
                this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName,Constants.CONCEPT_AGENT+"/"+contrib.getString(),
                        Constants.PROP_HAS_CONTRIBUTOR);
        
            }
        }
        System.out.println("METADATA ENDS");
        System.out.println("--------------------------");
        System.out.println("--------------------------");
        System.out.println("NODE LINKING STARTS");
        
        
        //NODE LINKING CODE
        String queryNodesforExpandedTemplate = Queries.queryNodesforExpandedTemplate();
        r1=null;
        //ExpandedTemplateModel.write(System.out,"RDF/XML");
        r1 = queryLocalWINGSResultsRepository(queryNodesforExpandedTemplate);
       
        while(r1.hasNext()){
            QuerySolution qs = r1.next();
            Resource res = qs.getResource("?n");
            Resource comp = qs.getResource("?c");
            Resource typeComp = qs.getResource("?cb");
            Literal rule = qs.getLiteral("?rule");
            Resource res2=qs.getResource("?derivedFrom");
            
            System.out.println("node is :"+res.getLocalName()+" derived from "+res2.getLocalName() +"the component is :"+comp);
            System.out.println("cb is :"+typeComp);
            System.out.println("this is inside the node linking new expanded");
            
           // this.addIndividual(OPMWModel,templateName_+res.getLocalName(),Constants.OPMW_WORKFLOW_TEMPLATE_PROCESS, "Workflow template process "+res.getLocalName());
           //CURRENTLY I AM COMMENTING THIS TO AVOID EXTRA STUFF BEING EXPORTED
            this.addIndividual(OPMWModel,"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(),Constants.OPMW_WORKFLOW_TEMPLATE_PROCESS, "Workflow expanded template process "+res.getLocalName());
               
            
          //p-plan interop
            OntClass cStep = OPMWModel.createClass(Constants.P_PLAN_STEP);
            cStep.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+encode("Expanded_"+newExpandedTemplateName+"_"+res.getLocalName()));
            
            
            if(typeComp.isURIResource()){ //only adds the type if the type is a uRI (not a blank node)
                String tempURI = encode(Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName());
                OntClass cAux1 = OPMWModel.createClass(typeComp.getURI());//repeated tuples will not be duplicated
                cAux1.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+tempURI);
            }else{
                System.out.println("ANON RESOURCE "+typeComp.getURI()+" ignored");
            }
            if(rule!=null){
                //rules are strings
                this.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(),
                    rule.getString(),                    
                        Constants.WINGS_PROP_HAS_RULE);
            }
            

            
            
            
            //is step of template
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName,                    
                        Constants.OPMW_PROP_IS_STEP_OF_TEMPLATE); 
            //is implementation of template process
            this.addProperty(OPMWModel, Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(), Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+res2.getLocalName(), Constants.OPMW_PROP_IS_IMPLEMENTATION_OF_TEMPLATE_PROCESS);
            
            //p-plan interop
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+"Expanded_"+newExpandedTemplateName,                    
                        Constants.P_PLAN_PROP_IS_STEP_OF_PLAN);
           
           
        }
      
        System.out.println("NODE LINKING ENDS");
        
        
        
        //data variables capturing for the expanded template
        System.out.println("--------------------------");
        System.out.println("--------------------------");
        System.out.println("DATA VARIABLES START");
        
        
        String queryDataVforExpandedTemplates = Queries.queryDataV2forExpandedTemplates();
        r1 = null;
        r1 = queryLocalWINGSResultsRepository(queryDataVforExpandedTemplates);
        System.out.println("now going for data variables");
        while(r1.hasNext()){
        	System.out.println("inside data variables");
            QuerySolution qs = r1.next();
            Resource variable = qs.getResource("?d");
            Resource databinding=qs.getResource("?db");//To be asked about
            Resource derivedFrom=qs.getResource("?derivedFrom");
            Resource type=qs.getResource("?type");
   
            System.out.println("data variable is : "+variable.getLocalName());
            this.addIndividual(OPMWModel,"Expanded_"+newExpandedTemplateName+"_"+variable.getLocalName(),Constants.OPMW_DATA_VARIABLE, "Data variable "+variable.getLocalName());

            
          //p-plan interop
            OntClass cVar = OPMWModel.createClass(Constants.P_PLAN_Variable);
            cVar.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_DATA_VARIABLE+"/"+encode("Expanded_"+newExpandedTemplateName+"_"+variable.getLocalName()));

            
            
            
            //we add the individual as a workflowExpandedTemplateArtifact as well            
            String aux = encode(Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+variable.getLocalName());
            OntClass cAux1 = OPMWModel.createClass(Constants.OPMW_WORKFLOW_TEMPLATE_ARTIFACT);//repeated tuples will not be duplicated
            cAux1.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+aux);
                   

            //types of data variables
            if(type!=null){
                //sometimes there are some blank nodes asserted as types in the ellaboration.
                //This will remove the blank nodes.
                if(type.isURIResource()){
                    System.out.println(variable+" of type "+ type);
                    //add the individual as an instance of another class, not as a new individual
                    String nameEncoded = encode(Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+variable.getLocalName());
                    OntClass c = OPMWModel.createClass(type.getURI());
                    c.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+nameEncoded);
                }else{
                    System.out.println("ANON RESOURCE "+type.getURI()+" ignored");
                }
            }else{
                System.out.println(variable);
            }
    
            
            //added is data binding of expanded template data variable dataproperty 
           // this.addDataProperty(OPMWModel, Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+expandedTemplateNewName,""+ databinding, Constants.OPMW_PROP_IS_DATA_BINDING_OF_EXPANDED_TEMPLATE_DATA_VARIABLE,XSDDatatype.XSDanyURI);
                
            
            //is variable of expanded template
            this.addProperty(OPMWModel, Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+variable.getLocalName(), Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName, Constants.OPMW_PROP_IS_VARIABLE_OF_TEMPLATE);
            //is implementation of template data variable
            this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+variable.getLocalName(),Constants.CONCEPT_DATA_VARIABLE+"/"+templateName+"/"+derivedFrom.getLocalName(),Constants.OPMW_PROP_IS_IMPLEMENTATION_OF_TEMPLATE_DATA_VARIABLE);
        }
        System.out.println("DATA VARIABLES END");
        System.out.println("--------------------------");
        System.out.println("--------------------------");
        System.out.println("PARAMETERS START");
        //parameter variables capturing for the expanded template
        String queryParameterVforExpandedTemplate = Queries.querySelectParameterforExpandedTemplate();
        r1 = null;
        r1 = queryLocalWINGSResultsRepository(queryParameterVforExpandedTemplate);
        while(r1.hasNext()){
        	System.out.println("now going for parameter variables");
            QuerySolution qs = r1.next();
            Resource res = qs.getResource("?p");
            Literal parValue = qs.getLiteral("?parValue");
            Resource derivedFrom=qs.getResource("?derivedFrom");
            System.out.println(res);
            this.addIndividual(OPMWModel,"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(), Constants.OPMW_PARAMETER_VARIABLE, "Parameter variable "+res.getLocalName());
            
            //add the parameter value as an artifact(expanded template) too
            String aux = encode(Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName());
            OntClass cAux1 = OPMWModel.createClass(Constants.OPMW_WORKFLOW_TEMPLATE_ARTIFACT);//repeated tuples will not be duplicated
            cAux1.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+aux);
            
            //is parameter of expanded template
            this.addProperty(OPMWModel,Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName,                    
                        Constants.OPMW_PROP_IS_PARAMETER_OF_TEMPLATE);
            
            //is implementation of template parameter variable
            this.addProperty(OPMWModel,Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(),Constants.CONCEPT_PARAMETER_VARIABLE+"/"+templateName+"/"+derivedFrom.getLocalName(),Constants.OPMW_PROP_IS_IMPLEMENTATION_OF_TEMPLATE_PARAMETER_VARIABLE);
        
            //par value data-property for expanded template
            this.addDataProperty(OPMWModel, Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(), parValue+"", Constants.OPMW_PROP_IS_PARVALUE_OF_EXPANDED_TEMPLATE_PARAMETER_VARIABLE);
            
        }
        System.out.println("PARAMETERS END");
        System.out.println("--------------------------");
        
        
        
        String expandedTemplateName_=newExpandedTemplateName+"_";
      //InputLinks == Used
        
        System.out.println("--------------------------");
        System.out.println("INPUT-LINKS START");
        
        String queryInputLinksforExpandedTemplate = Queries.queryInputLinksforExpandedTemplate();
        r1 = null;
        r1 = queryLocalWINGSResultsRepository(queryInputLinksforExpandedTemplate);
        while(r1.hasNext()){
        	System.out.println("now going for input links");
            QuerySolution qs = r1.next();
            Resource resVar = qs.getResource("?var");
            Resource resNode = qs.getResource("?dest");
            String role = qs.getLiteral("?role").getString();   
    
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
         		   Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.OPMW_PROP_USES);
            
          //p-plan interop for EXPANDED TEMPLATE
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_INPUT);
            this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                            Constants.P_PLAN_PROP_IS_INTPUT_VAR_OF);
            
            
            if(role!=null){
                System.out.println("Node "+resNode.getLocalName() +" Uses "+ resVar.getLocalName()+ " Role: "+role);
                //add the roles as subproperty of used. This triple should be on the ontology.
                this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
             		   Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.PREFIX_EXTENSION+"usesAs_"+role);
                //link the property as a subproperty of Used
                this.createSubProperty(OPMWModel,Constants.OPMW_PROP_USES, Constants.PREFIX_EXTENSION+"usesAs_"+role);
                //description of the new property
                OntProperty propUsed = OPMWModel.getOntProperty(Constants.PREFIX_EXTENSION+"usesAs_"+role);
                propUsed.addLabel("Property that indicates that a resource has been used as a "+role, "EN");
            }
        }
        System.out.println("INPUT-LINKS END");

        System.out.println("--------------------------");
        System.out.println("--------------------------");
        
        
        System.out.println("INPUT-P-LINKS START");
        
        
        String queryInputLinksP = Queries.queryInputLinksP();
        r1 = null;
        r1 = queryLocalWINGSResultsRepository(queryInputLinksP);
        while(r1.hasNext()){
        	System.out.println("now going for inputp links");
            QuerySolution qs = r1.next();
            Resource resVar = qs.getResource("?var");
            Resource resNode = qs.getResource("?dest");
            String role = qs.getLiteral("?role").getString(); 
            
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
         		   Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                        Constants.OPMW_PROP_USES);
           
           
          //p-plan interop
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                        Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_INPUT);
            this.addProperty(OPMWModel,Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                            Constants.P_PLAN_PROP_IS_INTPUT_VAR_OF);
            
            
            if(role!=null){
                System.out.println("Node "+resNode.getLocalName() +" Uses "+ resVar.getLocalName()+ " Role: "+role);
                //add the roles as subproperty of used. This triple should be on the ontology.
                this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
             		   Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.PREFIX_EXTENSION+"usesAs_"+role);
                
               
                //link the property as a subproperty of Used
                this.createSubProperty(OPMWModel,Constants.OPMW_PROP_USES, Constants.PREFIX_EXTENSION+"usesAs_"+role);
                OntProperty propUsed = OPMWModel.getOntProperty(Constants.PREFIX_EXTENSION+"usesAs_"+role);
                propUsed.addLabel("Property that indicates that a resource has been used as a "+role, "EN");
//                System.out.println(resVar.getLocalName() +" type "+ qs.getResource("?t").getURI());
            }
        }
        
        
        System.out.println("INPUT-P-LINKS END");
        System.out.println("--------------------------");
        System.out.println("--------------------------");
        System.out.println("OUTPUT-LINKS START");
        

        //OutputLInks == WasGeneratedBy
        String queryOutputLinks = Queries.queryOutputLinks();
        r1 = null;
        r1 = queryLocalWINGSResultsRepository(queryOutputLinks);
        while(r1.hasNext()){
        	System.out.println("now going for ouput links");
            QuerySolution qs = r1.next();
            Resource resVar = qs.getResource("?var");
            Resource resNode = qs.getResource("?orig");
            String role = qs.getLiteral("?role").getString();  
            
            this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
         		   Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                    Constants.OPMW_PROP_IGB);
            
            
          //p-plan interop
            this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                            Constants.P_PLAN_PROP_IS_OUTPUT_VAR_OF);
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_OUTPUT);   
            
            
                   
            if(role!=null){
                System.out.println("Artifact "+ resVar.getLocalName()+" Is generated by node "+resNode.getLocalName()+" Role "+role);
                //add the roles as subproperty of used. This triple should be on the ontology.
                this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
             		   Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                            Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+role);
                //link the property as a subproperty of WGB
                this.createSubProperty(OPMWModel,Constants.OPMW_PROP_IGB, Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+role);
                OntProperty propGenerated = OPMWModel.getOntProperty(Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+role);
                propGenerated.addLabel("Property that indicates that a resource has been generated as a "+role, "EN");
            }
        }
        
        
        System.out.println("OUTPUT-LINKS END");
        System.out.println("--------------------------");
        System.out.println("--------------------------");
        
        //InOutLink == Used and WasGeneratedBy
        System.out.println("INOUT-LINKS START");
       
        
        String queryInOutLinks = Queries.queryInOutLinks();
        r1 = null;
        r1 = queryLocalWINGSResultsRepository(queryInOutLinks);
        while(r1.hasNext()){
        	System.out.println("now going for inout links");
            QuerySolution qs = r1.next();
            Resource resVar = qs.getResource("?var");
            Resource resNode = qs.getResource("?orig");
            String roleOrig = qs.getLiteral("?origRole").getString();
            Resource resNodeD = qs.getResource("?dest");
            String roleDest = qs.getLiteral("?destRole").getString();
            if(roleOrig!=null && roleDest!=null){
                System.out.println("Artifact "+ resVar.getLocalName()+" is generated by node "+resNode.getLocalName()
                        +" with role "+roleOrig+" and uses node "+resNodeD.getLocalName()
                        +" with role "+ roleDest);
            }
            //they are all data variables
            this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
         		   Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                        Constants.OPMW_PROP_IGB);
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNodeD.getLocalName(),
         		   Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.OPMW_PROP_USES);
                       
            
          //p-plan interop
            this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                            Constants.P_PLAN_PROP_IS_OUTPUT_VAR_OF);
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_OUTPUT);
            this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNodeD.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_INPUT);
            this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNodeD.getLocalName(),
                            Constants.P_PLAN_PROP_IS_INTPUT_VAR_OF);     
            
            
            
            if(roleOrig!=null){                
                this.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
             		   Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                            Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+roleOrig);
                //link the property as a subproperty of WGB
                this.createSubProperty(OPMWModel,Constants.OPMW_PROP_IGB, Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+roleOrig);
                OntProperty propGenerated = OPMWModel.getOntProperty(Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+roleOrig);
                propGenerated.addLabel("Property that indicates that a resource has been generated as a "+roleOrig, "EN");
            }
            if(roleDest!=null){
                //System.out.println("created role "+ Constants.PREFIX_ONTOLOGY_PROFILE+"used_"+roleDest.getLocalName());
                this.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNodeD.getLocalName(),
             		   Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.PREFIX_EXTENSION+"usesAs_"+roleDest);
                //link the property as a subproperty of Used
                this.createSubProperty(OPMWModel,Constants.OPMW_PROP_USES, Constants.PREFIX_EXTENSION+"usesAs_"+roleDest);
                OntProperty propUsed = OPMWModel.getOntProperty(Constants.PREFIX_EXTENSION+"usesAs_"+roleDest);
                propUsed.addLabel("Property that indicates that a resource has been used as a "+roleDest, "EN");
            }

        }
        System.out.println("INOUT-LINKS END");
        System.out.println("--------------------------");
        System.out.println("--------------------------");
        
        //THIS ENDS THE EXPANDED TEMPLATE RETRIEVAL CODE
        System.out.println("EXPANDED TEMPLATE CODE ENDS HERE");
        return newExpandedTemplateName;
    }
   //*************************//
 
 }
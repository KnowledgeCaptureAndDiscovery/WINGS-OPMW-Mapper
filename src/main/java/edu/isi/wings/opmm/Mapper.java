package edu.isi.wings.opmm;

/*
 * ADD License metadata here (TO DO)
 * Author: Daniel Garijo and Tirth Rajen Mehta
 * Current Progress: Expanded Template Exported
 * Scenario 1 & 2 & 3 completed along with Data Versioning
 * 
 */



import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.bind.DatatypeConverter;
import utils.EncodingUtils;
import utils.ModelUtils;


public class Mapper {
    private OntModel WINGSModelTemplate;
    private OntModel WINGSExecutionResults;
    private OntModel OPMWModel;
    private OntModel PROVModel;
    private OntModel dataCatalog;
    private String taxonomyURL;
//    private OntModel expandedTemplateModel;
    private OntModel templateModel;
    private OntModel componentCatalog;
    private OntModel taxonomyExport;
    public static String NEW_TAXONOMY_CLASS="";
    public static String NEW_TAXONOMY_CLASS_2="";
    public static String PREFIX_COMP_CATALOG="";
    public Mapper(){
        
    }
    

    /**
     * Query the local Wings repository. These functions are here to facilitate coding
     * @param queryIn input query
     * @return 
     */
    private ResultSet queryLocalWINGSTemplateModelRepository(String queryIn) {
        return ModelUtils.queryLocalRepository(queryIn, WINGSModelTemplate);
    }
    
  //function to query just the component catalog model
    private ResultSet queryComponentCatalog(String queryIn) {
        return ModelUtils.queryLocalRepository(queryIn, componentCatalog);
    }
    
    //function to query just the taxonomy model
    private ResultSet queryLocalTaxonomyModelRepository(String queryIn) {
        return ModelUtils.queryLocalRepository(queryIn, taxonomyExport);
    }
    
    //function to query just the DataCatalog model
    private ResultSet queryLocalDataCatalogRepository(String queryIn) {
        return ModelUtils.queryLocalRepository(queryIn, dataCatalog);
    }
    
    
    //function to query just the conditioned template model
    private ResultSet queryConditionTemplateModel(String queryIn) {
        return ModelUtils.queryLocalRepository(queryIn, templateModel);
    }
    
    
    
    /**
     * Query the local results repository
     * @param queryIn input query
     * @return 
     */
    private ResultSet queryLocalWINGSResultsRepository(String queryIn) {
        return ModelUtils.queryLocalRepository(queryIn, WINGSExecutionResults);
    }
    /**
     * Method for accessing the URl of the domain ontology.
     * @param queryIn input query
     * @return 
     */
    private String getTaxonomyURL(OntModel m) throws Exception{
        if(taxonomyURL!=null)return taxonomyURL;
        else{
            ResultSet rs = ModelUtils.queryLocalRepository(Queries.queryGetTaxonomyURL(), m);
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
        
    }
    
    /**
 * This method should be replaced with one that reads a file into a model
 * @param template
 * @param modeFile 
 */
    public void loadExpandedTemplateFileToLocalRepository(String template, String modeFile){
        
    	InputStream in2 = FileManager.get().open(template.replaceAll("#.*$", ""));
        if (in2 == null){
            throw new IllegalArgumentException("File: " + template + " not found");
        }
        
//        expandedTemplateModel.read(in2, null, modeFile);
        
        System.out.println("Expanded Template File "+template+" loaded into the execution results");

    }
    

/**
 * This method should be replaced with one that reads a file into a model
 * @param template
 * @param modeFile 
 */    
public void loadedTemplateFileCondition(String template, String modeFile) throws Exception{
        
    	InputStream in2 = FileManager.get().open(template.replaceAll("#.*$", ""));
        if (in2 == null){
            throw new IllegalArgumentException("File: " + template + " not found");
        }
        
        templateModel.read(in2, null, modeFile);
        System.out.println("Template File Condition"+template+" loaded into the new template model");
}

/**
 * This method should be replaced with one that reads a file into a model
 * @param template
 * @param modeFile 
 */
public void loadTaxonomyExport(String template, String modeFile){
    
	InputStream in2 = FileManager.get().open(template.replaceAll("#.*$", ""));
    if (in2 == null){
        throw new IllegalArgumentException("File: " + template + " not found");
    }
    
    taxonomyExport.read(in2, null, modeFile);
    System.out.println("Taxonomy_Export File Condition"+template+" loaded into the new template model");

}

/**
 * This method should be replaced with one that reads a file into a model
 * @param template
 * @param modeFile 
 */
public void loadDataCatalog(String template, String modeFile){
    
	InputStream in2 = FileManager.get().open(template.replaceAll("#.*$", ""));
    if (in2 == null){
        throw new IllegalArgumentException("File: " + template + " not found");
    }
    
    dataCatalog.read(in2, null, modeFile);
    System.out.println("DataCatalog File "+template+" loaded into the new DataCatalog model");
//    DataCatalog.write(System.out,"TURTLE");

}

    /**
     * Method to load an execution file to a local model.
     * @param executionResults owl file with the execution
     * @param mode type of serialization. E.g., "RDF/XML"
     */
    public void loadFileToLocalRepository(OntModel m, String executionResults, String mode){
        //InputStream in2 = FileManager.get().open(executionResults);
        InputStream in2 = FileManager.get().open(executionResults.replaceAll("#.*$", ""));
        if (in2 == null){
            throw new IllegalArgumentException("File: " + executionResults + " not found");
        }
        System.out.println("Loading... "+executionResults);
        m.read(in2, null, mode);
        System.out.println("File "+executionResults+" loaded into: "+m.getProfile().getLabel());
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
        
//        System.out.println("Exporting the componentCatalog to a separate file outside");
        //exportRDFFile("componentCatalog2", ComponentCatalog,"TURTLE");
//        System.out.println("-------------------------");
    }

    /**
     * Method to transform a Wings template to OPMW, PROV and P-Plan
     * @param wingsInputTemplatePath template file
     * @param inputMode rdf serialization of the file
     * @param rdfOutputFile output file name
     * @param componentCatalogPath path for reading exporting the generated taxonomy.
     * @param exportMode
     * @param componentDirectory
     * @param domainName name of the domain in which the workflow is published
     * @return Template URI assigned to identify the template
     */
    //public String transformWINGSElaboratedTemplateToOPMW(String template,String mode, String outFile){
    public String transformWINGSElaboratedTemplateToOPMW(String wingsInputTemplatePath,String inputMode, String rdfOutputFile, String componentCatalogPath,String exportMode, String componentDirectory, String domainName){
        NEW_TAXONOMY_CLASS=Constants.TAXONOMY_CLASS+domainName+"#";
        NEW_TAXONOMY_CLASS_2=Constants.TAXONOMY_CLASS+domainName+"/";
    	//model initialization
        componentCatalog = initializeModel(componentCatalog);
        WINGSModelTemplate = initializeModel(WINGSModelTemplate);
        OPMWModel = initializeModel(OPMWModel);
        taxonomyExport = initializeModel(taxonomyExport);
        
        HashMap<String,String> classNames = new HashMap<>();
        
        try{
            //load the template file to WINGSModel (already loads the taxonomy as well
            this.loadTaxonomyExport(componentCatalogPath, exportMode);            
        }catch(Exception e){
            System.out.println("Error while loading the taxonomy: "+e.getMessage());
            return "";
        }
        
        String userName="";
        try{
            this.loadTemplateFileToLocalRepository(wingsInputTemplatePath, inputMode);            
            loadTaxonomy(WINGSModelTemplate);
            loadTaxonomy(componentCatalog);
        }catch(Exception e){
            System.err.println("Error "+e.getMessage());
            return "";
        }
    	
        //retrieval of the name of the workflowTemplate
        String queryNameWfTemplate = Queries.queryNameWfTemplate();
        //String templateName = null, templateName_ = null;
//        String templateName = null;
//        String templateName_ = null;
        String newTemplateName=null;
        String newTemplateName_=null;
        //System.out.println(queryNameWfTemplate);
        ResultSet r = queryLocalWINGSTemplateModelRepository(queryNameWfTemplate);
        if(r.hasNext()){//there should be just one local name per template
            QuerySolution qs = r.next();
            Resource res = qs.getResource("?name");
            Literal v = qs.getLiteral("?ver");
            newTemplateName = res.getLocalName();
            if(newTemplateName == null){
                System.out.println("Error: No Template specified.");
                return "";
            }
            newTemplateName=HashCreator.getAbstractTemplateHash(newTemplateName, this.WINGSModelTemplate);
            newTemplateName_=newTemplateName+"_";
            
//            templateName_=templateName+"_";
            //add the template as a provenance graph
            ModelUtils.addIndividual(OPMWModel,newTemplateName, Constants.OPMW_WORKFLOW_TEMPLATE, newTemplateName);
            
            OntClass cParam = OPMWModel.createClass(Constants.P_PLAN_PLAN);
            cParam.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+EncodingUtils.encode(newTemplateName));
            
            if(v!=null){
                ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+newTemplateName,""+ v.getInt(),
                        Constants.OPMW_DATA_PROP_VERSION_NUMBER, XSDDatatype.XSDint);
            }
            //add the uri of the original log file (native system template)
            ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+newTemplateName, 
                    res.getURI(),Constants.OPMW_DATA_PROP_HAS_NATIVE_SYSTEM_TEMPLATE, XSDDatatype.XSDanyURI);
            
            //Prov-o interoperability : workflow template           
            OntClass plan = OPMWModel.createClass(Constants.PROV_PLAN);
            plan.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+EncodingUtils.encode(newTemplateName));
            
            ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+newTemplateName,
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
            Resource diagram = qs.getResource("?diagram");
            //ask for diagram here: hasTemplateDiagram xsd:anyURI (png)
            String documentationContent ="";
            if(doc!=null){
                documentationContent = doc.getString();
                if(documentationContent.equals("")){
                    ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+newTemplateName,documentationContent,
                        Constants.OPMW_DATA_PROP_HAS_DOCUMENTATION);
                }
            }
            if(contrib!=null){
                userName = contrib.getString();
                ModelUtils.addIndividual(OPMWModel,contrib.getString(), Constants.OPM_AGENT,"Agent "+contrib.getString());
                ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+newTemplateName,Constants.CONCEPT_AGENT+"/"+contrib.getString(),
                        Constants.PROP_HAS_CONTRIBUTOR);
                
                //prov-o interoperability
                String agEncoded = EncodingUtils.encode(Constants.CONCEPT_AGENT+"/"+contrib.getString());
                OntClass d = OPMWModel.createClass(Constants.PROV_AGENT);
                d.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+agEncoded);
                ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+newTemplateName,Constants.CONCEPT_AGENT+"/"+contrib.getString(),
                Constants.PROV_WAS_ATTRIBUTED_TO);
            }
//            if(license!=null){
//                this.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+newTemplateName,license.getString(),
//                        Constants.DATA_PROP_RIGHTS, XSDDatatype.XSDanyURI);
//            }
            if(time!=null){
                ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+newTemplateName,time.getString(),
                        Constants.DATA_PROP_MODIFIED, XSDDatatype.XSDdateTime);
            }
            if(diagram!=null){
                ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+newTemplateName,diagram.getURI(),
                        Constants.OPMW_DATA_PROP_HAS_TEMPLATE_DIAGRAM, XSDDatatype.XSDanyURI);
            }
        }
        //LICENSE EXPORT FOR ABSTRACT TEMPLATE
        ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+newTemplateName,"http://creativecommons.org/licenses/by/3.0/",
              Constants.LICENSE, XSDDatatype.XSDanyURI);
        
        
        //extra time added (current time as ISSUED PROPERTY IN DUBLIN CORE)
        Date d=new Date();
        Calendar c143 = Calendar.getInstance();
        c143.setTime(d);
        String xmlDateTime = DatatypeConverter.printDateTime(c143);
        ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+newTemplateName,xmlDateTime,
                Constants.ISSUED_TIME,XSDDatatype.XSDdateTime);
        
        
        // retrieval of the Components (nodes, with their components and if they are concrete or not)
        String queryNodes = Queries.queryNodes();
        r = null;
        r = queryLocalWINGSTemplateModelRepository(queryNodes);
        HashSet<String> hs1Concr=new HashSet<>();
        HashSet<String> hs2Abs=new HashSet<>();
        
        HashSet<String> specialCaseforFolders=new HashSet<String>();
        
        
        //CREATING 2 HASHSETS FOR MAINTAINING THE CORRECT ORDER IN WHICH THE COMPONENTS ENTER IN THE CASES OF ABSTRACT COMPS AND CONCRETE COMPS
        while(r.hasNext()){
            QuerySolution qs = r.next();
            Resource res = qs.getResource("?n");
            Resource comp = qs.getResource("?c");
            Resource typeComp = qs.getResource("?typeComp");
            Literal rule = qs.getLiteral("?rule");
            Literal isConcrete = qs.getLiteral("?isConcrete");
            
            String className="";
            int indexOf=typeComp.toString().indexOf('#');
            className=typeComp.toString().substring(indexOf+1,typeComp.toString().length());
            System.out.println("type class is: "+className);

            
            
            String componentCatalogQueryforSubclassCheck = Queries.componentCatalogQueryforSubclassCheckfinal(className);
            ResultSet rnew1 = null;
            rnew1 = queryComponentCatalog(componentCatalogQueryforSubclassCheck);	
            int deciderpoint=0;
            System.out.println("Finallllll");
            Resource AbstractSuperClass=null;
            Resource concrComponent=null;
            String concreteness=null;
            while(rnew1.hasNext())
            {
            	
            	QuerySolution qsnew = rnew1.next();
                Resource nodenew = qsnew.getResource("?n");
                Resource x = qsnew.getResource("?x");
                Resource y = qsnew.getResource("?y");
                Literal concr=qsnew.getLiteral("?concr");
                
                if(y!=null && !nodenew.getLocalName().contains(y.getLocalName()))
                {
                	continue;
                }
                
                if(x!=null)
                {

                	if(nodenew.getLocalName().equals(className) && concr!=null)
                	{
                			AbstractSuperClass=x;
                			concrComponent=nodenew;
                			concreteness=concr.getString();
                			System.out.println("y is "+y.getLocalName()+" n is "+nodenew.getLocalName()+" x is "+x.getLocalName()+" concreteness is "+concr);
                			deciderpoint=1;
                			break;         		
                	}
                	
                }
                else
                	System.out.println("x is null");
                
            }

            if(concreteness!=null && concreteness.equals("false"))
            {
            	if(!AbstractSuperClass.getLocalName().equals("Component"))
            		specialCaseforFolders.add(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5));
            	hs2Abs.add(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5));
            }
        }
        
        System.out.println("ABS FIRST TIME CHECK");
        for(String x:hs2Abs)
        	System.out.println(x);
        
        r = null;
        r = queryLocalWINGSTemplateModelRepository(queryNodes);
        //CREATING 2 HASHSETS FOR MAINTAINING THE CORRECT ORDER IN WHICH THE COMPONENTS ENTER IN THE CASES OF ABSTRACT COMPS AND CONCRETE COMPS
        
        while(r.hasNext()){
            QuerySolution qs = r.next();
            Resource res = qs.getResource("?n");
            Resource comp = qs.getResource("?c");
            Resource typeComp = qs.getResource("?typeComp");
            Literal rule = qs.getLiteral("?rule");
            Literal isConcrete = qs.getLiteral("?isConcrete");
            
            String className="";
            int indexOf=typeComp.toString().indexOf('#');
            className=typeComp.toString().substring(indexOf+1,typeComp.toString().length());
            System.out.println("type class is: "+className);
            
            
            String componentCatalogQueryforSubclassCheck = Queries.componentCatalogQueryforSubclassCheckfinal(className);
            ResultSet rnew1 = null;
            rnew1 = queryComponentCatalog(componentCatalogQueryforSubclassCheck);	
            int deciderpoint=0;
            Resource AbstractSuperClass=null;
            Resource concrComponent=null;
            String concreteness=null;
            while(rnew1.hasNext())
            {
            	
            	QuerySolution qsnew = rnew1.next();
                Resource nodenew = qsnew.getResource("?n");
                Resource x = qsnew.getResource("?x");
                Literal concr=qsnew.getLiteral("?concr");
                
                if(x!=null)
                {

                	if(nodenew.getLocalName().equals(className) && concr!=null)
                	{
                			AbstractSuperClass=x;
                			concrComponent=nodenew;
                			concreteness=concr.getString();
                			System.out.println("x is "+x.getLocalName());
                			deciderpoint=1;
                			break;         		
                	}
                	
                }
                else
                	System.out.println("x is null");
                
            }
            


            if(concreteness!=null && concreteness.equals("true") )//&& !AbstractSuperClass.getLocalName().equals("Component")
            {
                
            	hs1Concr.add(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5));
            	hs2Abs.remove(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5));
            }

        }
        
        System.out.println("PRINTING OVERALL COMPS INORDER");
        System.out.println("CONCr");
        for(String x:hs1Concr)
        	System.out.println(x);
        System.out.println("ABS");
        for(String x:hs2Abs)
        	System.out.println(x);
        
        //-------------NODES FOR ABSTRACT TEMPLATE---------------------//
       r = null;
       r = queryLocalWINGSTemplateModelRepository(queryNodes);
       
       //created this hashset since some of the components repeat and we don't want to continuously find them in the
       //component catalog and make changes to them
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
            if(res!=null)
            {	
            	String tempp=res.toString().substring(0,res.toString().lastIndexOf("/"));
            	tempp=tempp.substring(0,tempp.lastIndexOf("/"));
            	PREFIX_COMP_CATALOG=tempp+"/components/library.owl#";
                //this should be initialized elsewhere
            }
            System.out.println("Node has inport "+inport.getLocalName());
            System.out.println("Node has outport "+outport.getLocalName());
            //------------ADDITION BY TIRTH-----------------
            //obtaining the className
            String className="";
            int indexOf=typeComp.toString().indexOf('#');
            className=typeComp.toString().substring(indexOf+1,typeComp.toString().length());
            System.out.println("type class is: "+className);
            
                //Removing "class" from components
            	if(!hs1Concr.contains(className.substring(0,className.length()-5)) && !hs2Abs.contains(className.substring(0,className.length()-5)) && !hs1Concr.contains(className) && !hs2Abs.contains(className))
                {
                	System.out.println("Class ignored because component does not have I/O: "+className);
                	continue;
                }
            
          //------------ADDITION BY TIRTH-----------------
            //obtaining the className
//            String domainName="";
//            int indexOf2=typeComp.toString().indexOf("/components");
//            System.out.println("domain name finding");
//            
//            String temp222=typeComp.toString().substring(0,indexOf2);
//            String temp333=temp222.substring(0,temp222.lastIndexOf("/"));
//            String temp444=temp333.substring(temp333.lastIndexOf("/")+1,temp333.length());
//            System.out.println("user name "+temp444);
//            
//            System.out.println(typeComp.toString().substring(0,indexOf2));
//            String subDomain=typeComp.toString().substring(0,indexOf2);
//            domainName=subDomain.substring(subDomain.lastIndexOf('/')+1,subDomain.length()); 
//            System.out.println("domain name  is: "+domainName);
//            userName=temp444;

          //creating a new EXPORT NAME FOR THE TAXONOMY CLASS
            
            
            //add each of the nodes as a UniqueTemplateProcess
            ModelUtils.addIndividual(OPMWModel,newTemplateName_+res.getLocalName(),Constants.OPMW_WORKFLOW_TEMPLATE_PROCESS, "Workflow template process "+res.getLocalName());
            //p-plan interop
            OntClass cStep = OPMWModel.createClass(Constants.P_PLAN_STEP);
            cStep.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+EncodingUtils.encode(newTemplateName_+res.getLocalName()));
            
            
            //Have to start querying the component catalog for checking the number of inputs and outputs:
            //Taxonomy_Export.write(System.out,"Turtle");
            if(!hsforComps.contains(className)){
            	System.out.println("CLASS NAME IS: "+className);

            	System.out.println("inside outermost condition");
            String componentCatalogQueryforSubclassCheck = Queries.componentCatalogQueryforSubclassCheckfinal(className);
            ResultSet rnew1 = null;
            rnew1 = queryComponentCatalog(componentCatalogQueryforSubclassCheck);	
            int deciderPoint=0;
            Resource AbstractSuperClass=null;
            Resource concrComponent=null;
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
                        concrComponent=nodenew;
                        System.out.println("x is "+x.getLocalName());
                        deciderPoint=1;
                        break;         		
                    }
                	
                }
                else
                	System.out.println("x is null");
                
            }
            System.out.println("ULTIMATE WORTH IT SUPER CLASS IS "+AbstractSuperClass.getLocalName());
            
            
            	
            if(deciderPoint==1)
            {
            	
            System.out.println("THIS MEANS THE BASIC CONDITIONS ARE MET THAT THE COMPONENT-CATALOG HAS EXPORTED THE COMPONENT---------");         	
            //QUERY-1 FOR THE RETRIEVAL OF INPUTS AND OUTPUTS OF THE PARTICULAR COMPONENT
            System.out.println("PREFIX_COMP_CATALOG "+PREFIX_COMP_CATALOG);
            String componentCatalogQueryforInputsandOutputsComponent = Queries.componentCatalogQueryforInputsandOutputs(PREFIX_COMP_CATALOG,className);
            ResultSet rnew2 = null;
            rnew2 = queryComponentCatalog(componentCatalogQueryforInputsandOutputsComponent);
           
            HashSet<String> hsi=new HashSet<>();
            HashSet<String> hso=new HashSet<>();
            Resource nodenew11=null;
            if(rnew2==null)
            	System.out.println("rnew2 is null here only");
            while(rnew2.hasNext())
            {
            	QuerySolution qsnew = rnew2.next();
                Resource nodenew12 = qsnew.getResource("?n");
                Resource i = qsnew.getResource("?i");
                Resource o = qsnew.getResource("?o");
                System.out.println("nodenew12 "+nodenew12.getLocalName());
                if(nodenew12.getLocalName().equals(className.substring(0,className.length()-5)))
                {
                	nodenew11=nodenew12;
                	hsi.add(i.getLocalName());
                	hso.add(o.getLocalName());
                	System.out.println("node : "+nodenew11.getLocalName());
                }
            }
            
            
            //FIRST CASE STARTS: THE INCOMING COMPONENT IS AN ABSTRACT COMPONENT
            //PROCEDURE:
            /*1. SEARCH THE NAME IN THE THM
             *2. IF FOUND, CHECK THE NUMBER OF INPUTS AND OUTPUTS OF IT
             *3. IF NOT FOUND, EXPORT A NEW ONE
             *4. IF FOUND BUT HAS DIFFERENT NUMBER OF INPUTS AND OUTPUTS THEN CREATE A NEW VERSION
             *AND LINK EXISTING LAST MODIFIED VERSION TO THE NEW ONE 
             *     
             */
           
            
            if(hs2Abs.contains(className) || hs2Abs.contains(className.substring(0,className.length()-5)) && nodenew11!=null)
            {
            	boolean specialFolder=false;
            	if(specialCaseforFolders.contains(className) || specialCaseforFolders.contains(className.substring(0,className.length()-5)))
            	{
            		System.out.println("SPECIAL FOLDER IS CASE IS TRUE");
            		specialFolder=true;
            	}
            	System.out.println("ABSTRACT PART HEREEE: "+nodenew11.getLocalName());

            	System.out.println("NOW UR INSIDE ABSTRACT COMPONENT CASE");
            	String taxonomyModelforAbstractComponent = Queries.TaxonomyExportQueryforSubclassCheckfinal();
                ResultSet r2new = null;
                r2new = queryLocalTaxonomyModelRepository(taxonomyModelforAbstractComponent);
               
                HashSet<String> namesOfAbstractCompswithSameNames=new HashSet<>();
              
                while(r2new.hasNext())
                {
                	QuerySolution qsnew = r2new.next();
                    Resource nodenew = qsnew.getResource("?n");
                    Resource x = qsnew.getResource("?x");
                    boolean isComponentConcrete = qsnew.getLiteral("?isConcrete").getBoolean();
                    System.out.println("What is nodenew? "+nodenew.getLocalName());
                    System.out.println("What is x? "+x.getLocalName());
                    
                    if(!isComponentConcrete)
                    {
                    	System.out.println("UR CHECKING FOR NAMES HERE");
                    	System.out.println(nodenew.getLocalName());
                    	if(nodenew.getLocalName().toLowerCase().contains(nodenew11.getLocalName().toLowerCase()))
                    	{
                    		//this means the name exists
                    		//next step is to check the inputs and outputs based on that className
                    		//saving all such cases in a HashSet
                    		if(!namesOfAbstractCompswithSameNames.contains(nodenew.getLocalName()))
                    		{
                    			namesOfAbstractCompswithSameNames.add(nodenew.getLocalName());
                    		}
                    		
                    	}
                    }
                    
                }
                
                
                //Case1: Direct Export
                if( namesOfAbstractCompswithSameNames.isEmpty()){
                	//this means that there are no similarities anywhere and we need to export it
                	System.out.println("NOW UR INSIDE EXPORTING A FRESH ONE");
                	//EXPORTING IT NOW:
                	System.out.println("ULTIMATE POINT ::::CHECK::::"+nodenew11.getLocalName());
                	
                	//abstract component individual
//                	this.addIndividualAbstract2(nodenew11.getLocalName()+"_V1");
                    
                    //subclass relation
                	ModelUtils.addIndividualConcreteSubclass2(this.taxonomyExport,nodenew11.getLocalName().toUpperCase()+"_CLASSV1","COMPONENT");

                    
                    System.out.println("EXPORTED THE BASIC ABSTRACT COMPONENT BY HERE");
                    //export of component ends
                    
                    //start exporting the inputs and outputs for the abstract component and canonical instance
                    
                    
                  //extracting the actual inputs,outputs and other factors for the Abstract component
                    System.out.println("EXTRACTION BEGINS "+nodenew11.getLocalName());
                    String componentCatalogQueryforActualInputsandOutputsforAbstractComponent = Queries.componentCatalogQueryforActualInputsandOutputsforAbstractComponent(PREFIX_COMP_CATALOG,nodenew11.getLocalName()+"Class");
                    rnew2 = null;
                    rnew2 = queryComponentCatalog(componentCatalogQueryforActualInputsandOutputsforAbstractComponent);
                   
                    HashSet<String> inputsAbsComp=new HashSet<>();
                    HashSet<String> outputsAbsComp=new HashSet<>();
                    boolean compConcreteAbs=false;
                    System.out.println("what we have "+nodenew11.getLocalName());
                    while(rnew2.hasNext())
                    {
                    	QuerySolution qsnew = rnew2.next();
                    	Resource nodenew = qsnew.getResource("?n");
                        Resource input = qsnew.getResource("?i");
                        Resource output = qsnew.getResource("?o");
                        Literal concr=qsnew.getLiteral("?concr");
                        Literal loc=qsnew.getLiteral("?loc");

                        if(nodenew.getLocalName().equals(nodenew11.getLocalName()))
                        {
                        	inputsAbsComp.add(input.getLocalName());
                        	outputsAbsComp.add(output.getLocalName());
                        	if(concr!=null)
                        	{
                        		compConcreteAbs=concr.getBoolean();
                        	}
                        }
                    }
                    System.out.println("ABSTRACT COMPONENT----------");
                    for(String i:inputsAbsComp)
                    	System.out.println("inputs are: "+i);
                    for(String o:outputsAbsComp)
                    	System.out.println("outputs are: "+o);
                    System.out.println("isConcrete is: "+compConcreteAbs);
                    System.out.println("EXTRACTED THE INPUTS AND OUTPUTS ABSTRACT COMPONENT BY HERE");
                    
                    //EXPORTING THE USER WHO CREATED THE COMPONENT
                    this.dataProps(Constants.PROV_WAS_ATTRIBUTED_TO,nodenew11.getLocalName().toUpperCase()+"_V1",userName,XSDDatatype.XSDstring);
                    
                    
                    
                  //EXPORTING THE FACT THAT CLASSNAME-CLASS IS A CLASSNAME              
                    this.classIsaClass(nodenew11.getLocalName().toUpperCase()+"_CLASSV1",nodenew11.getLocalName().toUpperCase()+"_V1");
                    classNames.put(nodenew11.getLocalName().toUpperCase(), nodenew11.getLocalName().toUpperCase()+"_CLASSV1");

                    //IS CONCRETE EXPORTED
                    this.dataProps(Constants.COMPONENT_IS_CONCRETE,nodenew11.getLocalName().toUpperCase()+"_V1",compConcreteAbs+"",XSDDatatype.XSDboolean);
                    
                    //RDFS LABEL EXPORTED for canonical instance
                    this.dataProps(Constants.RDFS_LABEL,nodenew11.getLocalName().toUpperCase()+"_V1",nodenew11.getLocalName(),XSDDatatype.XSDstring);
                    
                  //RDFS LABEL EXPORTED for class
                    this.dataProps2(Constants.RDFS_LABEL,nodenew11.getLocalName().toUpperCase()+"_CLASSV1",nodenew11.getLocalName(),XSDDatatype.XSDstring);

         
                    //INPUTS-- EXPORTED
                    this.inputsOutputs(Constants.COMPONENT_HAS_INPUT,inputsAbsComp,nodenew11.getLocalName().toUpperCase()+"_V1");

                    
                    //OUTPUTS-- EXPORTED
                    this.inputsOutputs(Constants.COMPONENT_HAS_OUTPUT,outputsAbsComp,nodenew11.getLocalName().toUpperCase()+"_V1");

                    if(specialFolder==true){
                        //this is a bit dirty, should be another constant
                        this.dataProps("http://www.w3.org/2000/01/rdf-schema#subClassOf",nodenew11.getLocalName().toUpperCase()+"_V1",AbstractSuperClass.getLocalName(),XSDDatatype.XSDstring);
                    }
                    
                    //STEP4: EXTRACT ONLY THE PARAMETERS IF THEY EXIST FOR ABSTRACT COMPONENTS
                    System.out.println("INPUT PARAMETERS EXTRACTION PRINTING FOR ABSTARCT COMPONENTS");
                    ComponentVersion.Step4(inputsAbsComp, componentCatalog, taxonomyExport);

                    
                  //STEP5: EXTRACT ONLY THE (INPUTS) DATA VARIABLES IF THEY EXIST FOR ABSTRACT COMPONENTS
                    System.out.println("INPUT DATA EXTRACTION PRINTING FOR ABSTRACT COMPONENTS ONLY");
                    ComponentVersion.Step5(inputsAbsComp,componentCatalog, taxonomyExport);
                    
                    
                  //STEP6: EXTRACT ONLY THE (OUTPUTS) DATA VARIABLES IF THEY EXIST FOR ABSTRACT COMPONENTS
                    System.out.println("OUTPUT DATA EXTRACTION PRINTING FOR ABSTRACT COMPONENTS");
                    ComponentVersion.Step6(outputsAbsComp,componentCatalog, taxonomyExport);
                    
                    
                }
              //Case2: Checking the components once for same Inputs and Outputs
                else if(!namesOfAbstractCompswithSameNames.isEmpty()){
                //this means we have found a list of similar abstract components in the Taxonomy Model
                //now there are 2 points we have to consider here
               //first we have to check the inputs and outputs of all the similar named abstract components in the Taxonomy Model
               //if there is one with the same number of inputs and outputs then just break off
               //if there is no one with the same inputs and outputs then we have to export a new one out
                
                System.out.println("NAMES FOR SIMILAR NAMED ABSTRACT COMPONENTS");
                for(String x:namesOfAbstractCompswithSameNames)
                	System.out.println(x);
                
                System.out.println("THE INPUTS AND OUTPUTS IN THE CURRENT ABSTRACT COMPONENT ARE:");
                ///
                String componentCatalogQueryforActualInputsandOutputsforAbstractComponent = Queries.componentCatalogQueryforActualInputsandOutputsforAbstractComponent(PREFIX_COMP_CATALOG,nodenew11.getLocalName()+"Class");
                rnew2 = null;
                rnew2 = queryComponentCatalog(componentCatalogQueryforActualInputsandOutputsforAbstractComponent);
               
                HashSet<String> inputsAbsComp=new HashSet<>();
                HashSet<String> outputsAbsComp=new HashSet<>();
                boolean compConcreteAbs=false;
                System.out.println("what we have "+nodenew11.getLocalName());
                while(rnew2.hasNext())
                {
                	QuerySolution qsnew = rnew2.next();
                	Resource nodenew = qsnew.getResource("?n");
                    Resource input = qsnew.getResource("?i");
                    Resource output = qsnew.getResource("?o");

                    if(nodenew.getLocalName().equals(nodenew11.getLocalName()))
                    {
                    	inputsAbsComp.add(input.getLocalName());
                    	outputsAbsComp.add(output.getLocalName());
                    }
                }
                System.out.println("ABSTRACT COMPONENT----------");
                for(String i:inputsAbsComp)
                	System.out.println("inputs are: "+i);
                for(String o:outputsAbsComp)
                	System.out.println("outputs are: "+o);
                System.out.println("isConcrete is: "+compConcreteAbs);
                System.out.println("EXTRACTED THE INPUTS AND OUTPUTS ABSTRACT COMPONENT BY HERE");
                ///
                
                
                
                //NOW WE HAVE TO FIND OUT THE NUMBER OF INPUTS AND OUTPUTS IN EACH OF THE SIMILAR NAMED ABSTRACT COMPONENTS
                boolean clarifyToExportAbstractComp=true;
                for(String whatwehave:namesOfAbstractCompswithSameNames)
                {
                	String inputsandoutputsforsimilarnamedComps = Queries.TaxonomyExportQueryforSubclassCheckfinal();
                    rnew2 = null;
                    rnew2 = queryLocalTaxonomyModelRepository(inputsandoutputsforsimilarnamedComps);
                   
                    HashSet<String> similarAbsInps=new HashSet<>();
                    HashSet<String> similarAbsOuts=new HashSet<>();
                    System.out.println("what we have "+nodenew11.getLocalName());
                    String nodeInstance="", nodeClass ="";
                    while(rnew2.hasNext())
                    {
                    	QuerySolution qsnew = rnew2.next();
                    	Resource nodenew = qsnew.getResource("?n");
                        Resource input = qsnew.getResource("?i");
                        Resource output = qsnew.getResource("?o");
                        nodeInstance = qsnew.getResource("?n").getLocalName();
                        nodeClass = qsnew.getResource("?x").getLocalName();
                        
                        System.out.println("Node inside case2: ?"+nodenew.getLocalName());
                        //System.out.println("x inside case2: ?"+x.getLocalName());
                        
                        if(nodenew.getLocalName().equals(whatwehave))
                        {
                            
                            similarAbsInps.add(input.getLocalName());
                            similarAbsOuts.add(output.getLocalName());
                            taxonomyExport.write(System.out,"TURTLE");
                        }
                    }
                    if(similarAbsInps.size()==inputsAbsComp.size() && similarAbsOuts.size()==outputsAbsComp.size())
                    {
                        
                        //add to classNames the appropriate version of the class that we have to point to.
                        classNames.put(nodeInstance, nodeClass);
                        
                        
                    	System.out.println("There is a match and hence we don't have to export anything and BREAK NOW");
                    	clarifyToExportAbstractComp=false;
                    	break;
                    }
                }
                
                
              //Case3: Export since there are no components with same number of inputs and outputs 
                if(clarifyToExportAbstractComp==true)
                {
                	//BIG TASK TO DO HERE IS TO IDENTIFY THE LATEST VERSION OF THE COMPONENT OUT OF ALL OTHER COMPONENTS
                	//SO THAT WHEN WE ARE CREATING THE NEW ABSTRACT COMPONENT
                	//WE WANT TO LINK THE LAST AND CURRENT ABSTRACT COMPONENTS WITH THE CANONICAL INSTANCES
                	//FOR TRACE MAINTAINANCE
                	String finalversionforNewAbstractComponent="";
                	String finalversionforLatestAbstractComponent="";
                	int max=Integer.MIN_VALUE;
                	for(String x:namesOfAbstractCompswithSameNames)
                	{
                		String temp=x.substring(x.lastIndexOf("_V"),x.length());
                		System.out.println("what is temp "+temp);
                		int temp2=Integer.parseInt(temp.substring(2,temp.length()));
                		if(max<temp2)
                		{
                			max=temp2;
                			
                		}
                	}
                	finalversionforLatestAbstractComponent="_V"+max;
                	max++;
                	finalversionforNewAbstractComponent="_V"+max;
                	System.out.println("THE MOST LATEST VERSION IS : "+finalversionforNewAbstractComponent);
                	//ABOVE TASK DONE BY HERE
                	
                	

                	//this means that the same Abstract Component is not present. So now we will export a fresh one.
                	
//                	//abstract component individual
//                	this.addIndividualAbstract2(nodenew11.getLocalName()+finalversionforNewAbstractComponent);
                    

                    //subclass relation
                	ModelUtils.addIndividualConcreteSubclass2(this.taxonomyExport,nodenew11.getLocalName().toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1,finalversionforNewAbstractComponent.length()),"COMPONENT");

                    
                    //EXPORTING THE PROV_WAS_REVISION_OF
                    OntProperty propSelec255 = taxonomyExport.createOntProperty(Constants.PROV_WAS_REVISION_OF);
                    Resource source255 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+ EncodingUtils.encode(nodenew11.getLocalName().toUpperCase()+finalversionforNewAbstractComponent) );
                    Individual instance255 = (Individual) source255.as( Individual.class );
                    if((nodenew11.getLocalName().toUpperCase()+finalversionforLatestAbstractComponent).contains("http://")){//it is a URI
                        instance255.addProperty(propSelec255,NEW_TAXONOMY_CLASS+nodenew11.getLocalName().toUpperCase()+finalversionforLatestAbstractComponent);            
                    }else{//it is a local resource
                        instance255.addProperty(propSelec255, taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(nodenew11.getLocalName().toUpperCase()+finalversionforLatestAbstractComponent)));
                    }
                    
                    
                    System.out.println("EXPORTED THE BASIC ABSTRACT COMPONENT BY HERE");
                    //export of component ends
                    
                    //start exporting the inputs and outputs for the abstract component and canonical instance
                    
                    
                  //extracting the actual inputs,outputs and other factors for the Abstract component
                    System.out.println("EXTRACTION BEGINS "+nodenew11.getLocalName());
                    String componentCatalogQueryforActualInputsandOutputsforAbstractComponent11 = Queries.componentCatalogQueryforActualInputsandOutputsforAbstractComponent(PREFIX_COMP_CATALOG,nodenew11.getLocalName()+"Class");
                    rnew2 = null;
                    rnew2 = queryComponentCatalog(componentCatalogQueryforActualInputsandOutputsforAbstractComponent11);
                   
                    HashSet<String> inputsAbsComp11=new HashSet<>();
                    HashSet<String> outputsAbsComp11=new HashSet<>();
                    boolean compConcreteAbs11=false;
                    System.out.println("what we have "+nodenew11.getLocalName());
                    while(rnew2.hasNext())
                    {
                    	QuerySolution qsnew = rnew2.next();
                    	Resource nodenew = qsnew.getResource("?n");
                        Resource input = qsnew.getResource("?i");
                        Resource output = qsnew.getResource("?o");
                        Literal concr=qsnew.getLiteral("?concr");
                        Literal loc=qsnew.getLiteral("?loc");

                        if(nodenew.getLocalName().equals(nodenew11.getLocalName()))
                        {
                        	inputsAbsComp11.add(input.getLocalName());
                        	outputsAbsComp11.add(output.getLocalName());
                        	if(concr!=null)
                        	{
                        		compConcreteAbs11=concr.getBoolean();
                        	}
                        }
                    }
                    System.out.println("ABSTRACT COMPONENT----------");
                    for(String i:inputsAbsComp11)
                    	System.out.println("inputs are: "+i);
                    for(String o:outputsAbsComp11)
                    	System.out.println("outputs are: "+o);
                    System.out.println("isConcrete is: "+compConcreteAbs11);
                    System.out.println("EXTRACTED THE INPUTS AND OUTPUTS ABSTRACT COMPONENT BY HERE");
                    
                    //EXPORTING THE USER WHO CREATED THE COMPONENT
                    this.dataProps(Constants.PROV_WAS_ATTRIBUTED_TO,nodenew11.getLocalName().toUpperCase()+finalversionforNewAbstractComponent,userName,XSDDatatype.XSDstring);
                    
                  //EXPORTING THE FACT THAT CLASSNAME-CLASS IS A CLASSNAME
                    this.classIsaClass(nodenew11.getLocalName().toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1, finalversionforNewAbstractComponent.length()), nodenew11.getLocalName().toUpperCase()+finalversionforNewAbstractComponent);
                    classNames.put(nodenew11.getLocalName().toUpperCase(), nodenew11.getLocalName().toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1, finalversionforNewAbstractComponent.length()));

                    //IS CONCRETE EXPORTED
                    this.dataProps(Constants.COMPONENT_IS_CONCRETE, nodenew11.getLocalName().toUpperCase()+finalversionforNewAbstractComponent, compConcreteAbs+"", XSDDatatype.XSDboolean);

                    
                  //RDFS LABEL EXPORTED for canonical instance
                    this.dataProps(Constants.RDFS_LABEL, nodenew11.getLocalName().toUpperCase()+finalversionforNewAbstractComponent, nodenew11.getLocalName(), XSDDatatype.XSDstring);
                    
                  //RDFS LABEL EXPORTED for class
                    this.dataProps2(Constants.RDFS_LABEL, nodenew11.getLocalName().toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1, finalversionforNewAbstractComponent.length()), nodenew11.getLocalName(), XSDDatatype.XSDstring);
                    
                    //INPUTS-- EXPORTED
                    this.inputsOutputs(Constants.COMPONENT_HAS_INPUT, inputsAbsComp, nodenew11.getLocalName().toUpperCase()+finalversionforNewAbstractComponent);

                    
                    //OUTPUTS-- EXPORTED
                    this.inputsOutputs(Constants.COMPONENT_HAS_OUTPUT, outputsAbsComp, nodenew11.getLocalName().toUpperCase()+finalversionforNewAbstractComponent);
                    
                    if(specialFolder==true)
                    {
                        this.dataProps(Constants.COMPONENT_HAS_SUPERCLASS_FOLDER, nodenew11.getLocalName().toUpperCase()+finalversionforNewAbstractComponent, AbstractSuperClass.getLocalName(), XSDDatatype.XSDstring);
                    }
           	
                  //STEP4: EXTRACT ONLY THE PARAMETERS IF THEY EXIST FOR ABSTRACT COMPONENTS
                    System.out.println("INPUT PARAMETERS EXTRACTION PRINTING FOR ABSTRACT COMPONENTS");
                    ComponentVersion.Step4(inputsAbsComp,componentCatalog, taxonomyExport);
                   
                    
                  //STEP5: EXTRACT ONLY THE (INPUTS) DATA VARIABLES IF THEY EXIST FOR ABSTRACT COMPONENTS  
                    System.out.println("INPUT DATA EXTRACTION PRINTING FOR ABSTRACT COMPONENTS ONLY");
                    ComponentVersion.Step5(inputsAbsComp,componentCatalog, taxonomyExport);

                    
                  //STEP6: EXTRACT ONLY THE (OUTPUTS) DATA VARIABLES IF THEY EXIST FOR ABSTRACT COMPONENTS
                    System.out.println("OUTPUT DATA EXTRACTION PRINTING FOR ABSTRACT COMPONENTS");
                    ComponentVersion.Step6(outputsAbsComp,componentCatalog, taxonomyExport);
                    

                }
             }   
                

         }
            //INCOMING COMPONENT IS A CONCRETE COMPONENT
            
            
            								/////////********STARTS***********////////////
            /////**************THIS IS THE ENTIRE PART WHERE WE THE INCOMING COMPONENT IS A CONCRETE COMPONENT**********////

            else if(hs1Concr.contains(className) || hs1Concr.contains(className.substring(0,className.length()-5)))
            {
            	
            	
            	
            	
            	
            	System.out.println("NOW UR INSIDE CONCRETE COMPONENT CASE");
            	String taxonomyModelforConcreteComponent = Queries.TaxonomyExportQueryforSubclassCheckfinalAgain(NEW_TAXONOMY_CLASS);
                ResultSet r2new = null;
                r2new = queryLocalTaxonomyModelRepository(taxonomyModelforConcreteComponent);
               
                HashSet<String> namesOfConcreteCompswithSameNames=new HashSet<>();
              
                while(r2new.hasNext())
                {
                	QuerySolution qsnew = r2new.next();
                    Resource nodenew = qsnew.getResource("?n");
                    Resource x = qsnew.getResource("?x");
                    Resource y = qsnew.getResource("?y");
                    
                    if(!y.getLocalName().equals("COMPONENT"))
                    {
                    	if(nodenew.getLocalName().toLowerCase().contains(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toLowerCase()))
                    	{
                    		//this means the name exists
                    		//next step is to check the inputs and outputs based on that className
                    		//saving all such cases in a HashSet
                    		if(!namesOfConcreteCompswithSameNames.contains(nodenew.getLocalName()) && !nodenew.getLocalName().contains("CLASS"))
                    		{
                    			System.out.println("nodenew "+nodenew.getLocalName()+" x "+x.getLocalName()+" y "+y.getLocalName());
                    			namesOfConcreteCompswithSameNames.add(nodenew.getLocalName());
                    		}
                    		
                    	}
                    }
                    
                }
                
                System.out.println("THIS WILL IDENTIFY THE CONTENTS OF THE CONCRETE HASHSET");
                System.out.println();
                for(String x:namesOfConcreteCompswithSameNames)
                	System.out.println(x);
                System.out.println();
                
                
                
                
                //PART-2: SEARCH IS ON
                System.out.println("PART-2 BEGINS OF SEARCH");
                System.out.println("FIND THE NAMES OF SIMILAR ABSTRACT COMPONENTS IN THE TAXONOMY HIERARCHY MODEL");
                
                System.out.println("NOW UR INSIDE CONCRETE COMPONENT CASE");
            	String taxonomyModelforAbstractComponent = Queries.TaxonomyExportQueryforSubclassCheckfinalAgain(NEW_TAXONOMY_CLASS);
                r2new = null;
                r2new = queryLocalTaxonomyModelRepository(taxonomyModelforAbstractComponent);
               
                HashSet<String> namesOfAbsCompswithSameNames=new HashSet<>();
              
                while(r2new.hasNext())
                {
                	QuerySolution qsnew = r2new.next();
                	Resource nodenew = qsnew.getResource("?n");
                    Resource x = qsnew.getResource("?x");
                    Resource y = qsnew.getResource("?y");
                    
                    if(y.getLocalName().equals("COMPONENT"))
                    {
                    	if(nodenew.getLocalName().toLowerCase().contains(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toLowerCase()))
                    	{
                    		//this means the name exists
                    		//next step is to check the inputs and outputs based on that className
                    		//saving all such cases in a HashSet
                    		if(!namesOfAbsCompswithSameNames.contains(nodenew.getLocalName()))
                    		{
                    			namesOfAbsCompswithSameNames.add(nodenew.getLocalName());
                    		}
                    		
                    	}
                    }
                    
                }
                
                System.out.println("THIS WILL IDENTIFY THE CONTENTS OF THE ABSTRACT HASHSET");
                System.out.println();
                for(String x:namesOfAbsCompswithSameNames)
                	System.out.println(x);
                System.out.println();
                
                
                //NOW THE IMPORTANT CLARIFICATION
                //IF THE SIZE OF THE HASHSET namesOfConcreteCompswithSameNames IS !=0 THEN ITS DEALT WITH IN THE END
                //IF THE SIZE OF BOTH HASHSETS IS 0, THIS MEANS A FRESH NEW EXPORT _V1
                //IF THE SIZE OF namesOfConcreteCompswithSameNames==0 BUT namesOfAbsCompswithSameNames!=0 THEN WE CHECK FURTHER
                //FOR INPS AND OUTPS
                if(namesOfAbsCompswithSameNames.size()!=0 && namesOfConcreteCompswithSameNames.size()==0)
                {
                	System.out.println("WE ARE HERE WHERE THE namesOfAbsCompswithSameNames.size()!=0 && namesOfConcreteCompswithSameNames.size()==0");
                	int clarifyToExportConcrComp=0;
                	Resource Abs=null;
                    for(String whatwehave:namesOfAbsCompswithSameNames)
                    {
                    	String inputsandoutputsforsimilarnamedComps = Queries.TaxonomyExportQueryforSubclassCheckfinal();
                        rnew2 = null;
                        rnew2 = queryLocalTaxonomyModelRepository(inputsandoutputsforsimilarnamedComps);
                       
                        HashSet<String> similarAbsInps=new HashSet<>();
                        HashSet<String> similarAbsOuts=new HashSet<>();
                        System.out.println("what we have "+concrComponent.getLocalName());
                        
                        while(rnew2.hasNext())
                        {
                        	QuerySolution qsnew = rnew2.next();
                        	Resource nodenew = qsnew.getResource("?n");
                        	Resource x = qsnew.getResource("?x");
//                        	Resource y = qsnew.getResource("?y");
                            Resource input = qsnew.getResource("?i");
                            Resource output = qsnew.getResource("?o");

                            
                            if(nodenew.getLocalName().toUpperCase().equals(whatwehave))
                            {
                            	System.out.println("Node inside case2:? "+nodenew.getLocalName());
                                System.out.println("x inside case2: ? "+x.getLocalName());
                                System.out.println("input is :"+input.getLocalName());
                                System.out.println("output is :"+output.getLocalName());
                            	similarAbsInps.add(input.getLocalName());
                            	similarAbsOuts.add(output.getLocalName());
                            	Abs=nodenew;
                            }
                        }
                        System.out.println("input size "+similarAbsInps.size());
                        System.out.println("output size "+similarAbsOuts.size());
                        
                        if(similarAbsInps.size()==hsi.size() && similarAbsOuts.size()==hso.size())
                        {
                        	System.out.println("There is a match AND IT MEANS WE HAVE TO EXPORT THE CONCR COMP AS A SUBCLASS OF ABS COMP");
                        	clarifyToExportConcrComp=1;
                        	break;
                        }
                    }
                    if(clarifyToExportConcrComp==1)
                    {
                    	//this means we export it as a subclass
                    	System.out.println("ABS IS: "+Abs.getLocalName());
                    	
//                    //concrete component individual
//                    this.addIndividualConcrete2(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"_V1");
//                    
//                      
//                    //subclass relation
//                    this.addIndividualConcreteSubclass2(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1", Abs.getLocalName());
//                      
                   ModelUtils.addIndividualConcreteSubclass2(this.taxonomyExport,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASSV1",Abs.getLocalName().substring(0,Abs.getLocalName().lastIndexOf("_"))+"_CLASS"+Abs.getLocalName().substring(Abs.getLocalName().lastIndexOf("_")+1,Abs.getLocalName().length()));
                    	
                    	
                  //for the component blankNode extraction for HARDWARE and SOFTWARE dependencies:
                    String componentCatalogQuerydependencies = Queries.componentCatalogQuerydependencies(PREFIX_COMP_CATALOG,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"Class");
                    rnew2 = null;
                    rnew2 = queryComponentCatalog(componentCatalogQuerydependencies);
                    Float memval=null;
                    Float stval=null;
                    boolean needsval=true;
                    String minversionval="";
                    if(rnew2.hasNext())
                    {
                    	QuerySolution qsnew = rnew2.next();
                    	Resource nodenew = qsnew.getResource("?n");
                    	Resource res11 = qsnew.getResource("?res");
                    	
                    	try{
                    		Resource minversion=qsnew.getResource("?minversion");
                    		minversionval=minversion.getLocalName();
                    	Literal needs=qsnew.getLiteral("?needs64bit");
                    	needsval=needs.getBoolean();
                    	Literal mem=qsnew.getLiteral("?mem");
                    	memval=mem.getFloat();
                    	Literal st=qsnew.getLiteral("?st");
                    	stval=st.getFloat();
                    	System.out.println("res is "+res11);
                    	System.out.println("needs64bit "+needs);
                    	System.out.println("minversion is "+minversionval);
                    		System.out.println("mem "+mem);
                    		System.out.println("st "+st);
                    	}catch(Exception e){}
                    		
                    	
                    } 
                    
                  //EXPORTING THE HARDWARE AND SOFTWARE DEPENDENCY
                   exportHardwareSoftwareDependencies(needsval, memval, stval, memval, concrComponent);
                    
                    
                    
                    //extracting the actual inputs,outputs and other factors for the component
                      
                      String componentCatalogQueryforActualInputsandOutputsforComponent = Queries.componentCatalogQueryforActualInputsandOutputsforComponent(PREFIX_COMP_CATALOG,concrComponent.getLocalName());
                      rnew2 = null;
                      rnew2 = queryComponentCatalog(componentCatalogQueryforActualInputsandOutputsforComponent);

                      HashSet<String> inputsComp=new HashSet<>();
                      HashSet<String> outputsComp=new HashSet<>();
                      String compLoc="";
                      boolean compConcrete=false;
                      String doc11="";
                      while(rnew2.hasNext())
                      {
                      	QuerySolution qsnew = rnew2.next();
                      	Resource nodenew = qsnew.getResource("?n");
                      	Resource input = qsnew.getResource("?i");
                      	Resource output = qsnew.getResource("?o");
                      	Literal concr=qsnew.getLiteral("?concr");
                      	Literal loc=qsnew.getLiteral("?loc");
                      	Literal doc=qsnew.getLiteral("?doc");
                      	
                      	if(nodenew.getLocalName().equals(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)))
                      	{
                      		inputsComp.add(input.getLocalName());
                      		outputsComp.add(output.getLocalName());
                      		if(loc!=null && compLoc.equals(""))
                      			compLoc=loc.getString();
                      		if(concr!=null)
                      		{
                      			compConcrete=concr.getBoolean();
                      		}
                      		if(doc!=null)
                      		{
                      			doc11=doc.getString();
                      		}
                      	}
                      }
                      System.out.println("MAIN COMPONENT----------");
                      for(String i:inputsComp)
                      	System.out.println("inputs are: "+i);
                      for(String o:outputsComp)
                      	System.out.println("outputs are: "+o);
                      System.out.println("Location is: "+compLoc);
                      System.out.println("isConcrete is: "+compConcrete);
                                      
                      
                      //EXPORTING THE MD5 FOR THE COMPONENT CODE
                      try{
                      this.dataProps(Constants.COMPONENT_HAS_MD5_CODE,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1",EncodingUtils.MD5ComponentCode(componentDirectory+compLoc.substring(compLoc.lastIndexOf("\\")+1, compLoc.length())+".zip"),XSDDatatype.XSDstring);
                      }catch(Exception e){System.out.println("ERROR");}
                      
                      //EXPORTING THE USER WHO CREATED THE COMPONENT
                      this.dataProps(Constants.PROV_WAS_ATTRIBUTED_TO,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1",userName,XSDDatatype.XSDstring);
                      

                    //EXPORTING THE FACT THAT CLASSNAME-CLASS IS A CLASSNAME
                      this.classIsaClass(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASSV1", concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1");
                      classNames.put(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase(), concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASSV1");
                    //DOCUMENTATION EXPORTED
                        if(!doc11.equals("")){
                          this.dataProps(Constants.COMPONENT_HAS_DOCUMENTATION, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1", doc11+"", XSDDatatype.XSDstring);
                    }
                      
                      //IS CONCRETE EXPORTED
                      this.dataProps(Constants.COMPONENT_IS_CONCRETE, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1", compConcrete+"", XSDDatatype.XSDboolean);
                      
                      
                      //RDFS LABEL EXPORTED for canonical instance
                      this.dataProps2(Constants.RDFS_LABEL, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1", concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5), XSDDatatype.XSDstring);
                     
                      
                    //RDFS LABEL EXPORTED for class
                      this.dataProps2(Constants.RDFS_LABEL, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASSV1", concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5), XSDDatatype.XSDstring);
                      
                      
                      //INPUTS-- EXPORTED
                      this.inputsOutputs(Constants.COMPONENT_HAS_INPUT, inputsComp, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1");

                      
                      //OUTPUTS-- EXPORTED
                      this.inputsOutputs(Constants.COMPONENT_HAS_OUTPUT, outputsComp, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1");

                      
                    //HAS LOCATION EXPORTED
                      this.dataProps(Constants.COMPONENT_HAS_LOCATION, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1", componentDirectory+compLoc.substring(compLoc.lastIndexOf("/")+1, compLoc.length()),XSDDatatype.XSDstring);
                      
                    	
                  //STEP1: EXTRACT ONLY THE (INPUTS) PARAMETERS IF THEY EXIST FOR COMPONENTS
                    System.out.println("INPUT PARAMETERS EXTRACTION PRINTING COMPONENTS");
                    ComponentVersion.Step1(hsi,componentCatalog, taxonomyExport);
                    
                    
                    
                   //STEP2: EXTRACT ONLY THE (INPUTS) DATA VARIABLES IF THEY EXIST FOR COMPONENTS
                    System.out.println("INPUT DATA EXTRACTION PRINTING COMPONENTS");
                    ComponentVersion.Step2(hsi,componentCatalog, taxonomyExport);
                    
                    
                  //STEP3: EXTRACT ONLY THE (OUTPUTS) DATA VARIABLES IF THEY EXIST FOR COMPONENTS
                    System.out.println("OUTPUT DATA EXTRACTION PRINTING COMPONENTS");
                    ComponentVersion.Step3(hso,componentCatalog, taxonomyExport);
                    
 	
                  }
                  if(clarifyToExportConcrComp==0)
                  {
                	  //this means we export both as a new version (LATEST VERSION) AND LINKING ALSO CANONICAL INSTANCES
                	  System.out.println("THIS STARTS THE FIRST CASE PART-2");  
                	  
                	  
                	String finalversionforNewAbstractComponent="";
                	String finalversionforLatestAbstractComponent="";
                  	int max=Integer.MIN_VALUE;
                  	for(String x:namesOfAbsCompswithSameNames)
                  	{
                  		String temp=x.substring(x.lastIndexOf("_V"),x.length());
                  		System.out.println("what is temp "+temp);
                  		int temp2=Integer.parseInt(temp.substring(2,temp.length()));
                  		if(max<temp2)
                  		{
                  			max=temp2;
                  			
                  		}
                  	}
                  	finalversionforLatestAbstractComponent="_V"+max;
                  	max++;
                  	finalversionforNewAbstractComponent="_V"+max;
                  	System.out.println("THE MOST LATEST VERSION IS : "+finalversionforNewAbstractComponent);
                  	//ABOVE TASK DONE BY HERE
                  	
//                  //concrete component individual
//                  	this.addIndividualConcrete2(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+finalversionforNewAbstractComponent);
//
//                  	
//                  //subclass relation
//                  	this.addIndividualConcreteSubclass2(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)+finalversionforNewAbstractComponent);

                  	ModelUtils.addIndividualConcreteSubclass2(this.taxonomyExport, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent,AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)+"_CLASS"+finalversionforNewAbstractComponent.substring(1,finalversionforNewAbstractComponent.length()));
                	
//                	//abstract component individual
//                  	this.addIndividualAbstractSubclass2(AbstractSuperClass.getLocalName()+finalversionforNewAbstractComponent);

                    
//                    //subclass relation for abstract component
//                  	this.addIndividualAbstractSubclass2(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);

                  	ModelUtils.addIndividualConcreteSubclass2(this.taxonomyExport, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)+"_CLASS"+finalversionforNewAbstractComponent.substring(1,finalversionforNewAbstractComponent.length()),"COMPONENT");
                    
                    System.out.println("EXPORTED THE BASIC CONCRETE COMPONENT BY HERE");
                    
                    
                    
                  //EXPORTING THE PROV_WAS_REVISION_OF
                    OntProperty propSelec255 = taxonomyExport.createOntProperty(Constants.PROV_WAS_REVISION_OF);
                    Resource source255 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+ EncodingUtils.encode(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent) );
                    Individual instance255 = (Individual) source255.as( Individual.class );
                    if((AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforLatestAbstractComponent).contains("http://")){//it is a URI
                        instance255.addProperty(propSelec255,NEW_TAXONOMY_CLASS+AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforLatestAbstractComponent);            
                    }else{//it is a local resource
                        instance255.addProperty(propSelec255, taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforLatestAbstractComponent)));
                    }
                    
                    
                    
                    
                    
                    //export of ABSTRACT component ends
                    
                  //for the component blankNode extraction for HARDWARE and SOFTWARE dependencies:
                    String componentCatalogQuerydependencies = Queries.componentCatalogQuerydependencies(PREFIX_COMP_CATALOG,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"Class");
                    rnew2 = null;
                    rnew2 = queryComponentCatalog(componentCatalogQuerydependencies);
                    Float memval=null;
                    Float stval=null;
                    boolean needsval=true;
                    String minversionval="";
                    if(rnew2.hasNext())
                    {
                    	QuerySolution qsnew = rnew2.next();
//                    	Resource nodenew = qsnew.getResource("?n");
                    	Resource res11 = qsnew.getResource("?res");
                    	
                    	try{
                    		Resource minversion=qsnew.getResource("?minversion");
                    		minversionval=minversion.getLocalName();
                    	Literal needs=qsnew.getLiteral("?needs64bit");
                    	needsval=needs.getBoolean();
                    	Literal mem=qsnew.getLiteral("?mem");
                    	memval=mem.getFloat();
                    	Literal st=qsnew.getLiteral("?st");
                    	stval=st.getFloat();
                    	System.out.println("res is "+res11);
                    	System.out.println("needs64bit "+needs);
                    	System.out.println("minversion is "+minversionval);
                    		System.out.println("mem "+mem);
                    		System.out.println("st "+st);
                    	}catch(Exception e){}
                    		
                    	
                    } 
                    
                  ///EXPORTING THE HARDWARE AND SOFTWARE DEPENDENCY
                   exportHardwareSoftwareDependencies(needsval, memval, stval, memval, concrComponent);
                  
                    
                    
                  //extracting the actual inputs,outputs and other factors for the component
                    
                    String componentCatalogQueryforActualInputsandOutputsforComponent = Queries.componentCatalogQueryforActualInputsandOutputsforComponent(PREFIX_COMP_CATALOG,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"Class");
                    rnew2 = null;
                    rnew2 = queryComponentCatalog(componentCatalogQueryforActualInputsandOutputsforComponent);

                    HashSet<String> inputsComp=new HashSet<>();
                    HashSet<String> outputsComp=new HashSet<>();
                    String compLoc="";
                    boolean compConcrete=false;

                    while(rnew2.hasNext())
                    {
                    	QuerySolution qsnew = rnew2.next();
                    	Resource nodenew = qsnew.getResource("?n");
                    	Resource input = qsnew.getResource("?i");
                    	Resource output = qsnew.getResource("?o");
                    	Literal concr=qsnew.getLiteral("?concr");
                    	Literal loc=qsnew.getLiteral("?loc");
                    	System.out.println("trying the nodenew comparison here: "+nodenew.getLocalName());
                    	if(nodenew.getLocalName().equals(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)))
                    	{
                    		inputsComp.add(input.getLocalName());
                    		outputsComp.add(output.getLocalName());
                    		if(loc!=null && compLoc.equals(""))
                    			compLoc=loc.getString();
                    		if(concr!=null)
                    		{
                    			compConcrete=concr.getBoolean();
                    		}
                    	}
                    }
                    System.out.println("MAIN COMPONENT----------");
                    for(String i:inputsComp)
                    	System.out.println("inputs are: "+i);
                    for(String o:outputsComp)
                    	System.out.println("outputs are: "+o);
                    System.out.println("Location is: "+compLoc);
                    System.out.println("isConcrete is: "+compConcrete);
                    //String compLoc2="/Users/Tirthmehta/Desktop/TestingDomain2/Component/";
                    
                  //EXPORTING THE MD5 FOR THE COMPONENT CODE
                    try{
                    this.dataProps(Constants.COMPONENT_HAS_MD5_CODE,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent,EncodingUtils.MD5ComponentCode(componentDirectory+compLoc.substring(compLoc.lastIndexOf("/")+1,compLoc.length())+".zip"),XSDDatatype.XSDstring);
                    }catch(Exception e){System.out.println("ERROR");}
                    
                    //EXPORTING THE USER WHO CREATED THE COMPONENT
                    this.dataProps(Constants.PROV_WAS_ATTRIBUTED_TO,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent,userName,XSDDatatype.XSDstring);

                  //EXPORTING THE FACT THAT CLASSNAME-CLASS IS A CLASSNAME
                    this.classIsaClass(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1, finalversionforNewAbstractComponent.length()), concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);
                    classNames.put(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase(), concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1, finalversionforNewAbstractComponent.length()));
  
                    //IS CONCRETE EXPORTED
                    this.dataProps(Constants.COMPONENT_IS_CONCRETE, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, compConcrete+"", XSDDatatype.XSDboolean);

                    
                    //RDFS LABEL EXPORTED for canonical instance
                    this.dataProps(Constants.RDFS_LABEL, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5), XSDDatatype.XSDstring);

                    
                  //RDFS LABEL EXPORTED for class
                    this.dataProps2(Constants.RDFS_LABEL, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1,finalversionforNewAbstractComponent.length()), concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5), XSDDatatype.XSDstring);
                    
                    
                    //INPUTS-- EXPORTED
                    this.inputsOutputs(Constants.COMPONENT_HAS_INPUT, inputsComp, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);

                    
                    //OUTPUTS-- EXPORTED
                    this.inputsOutputs(Constants.COMPONENT_HAS_OUTPUT, outputsComp, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);

                    
                  //HAS LOCATION EXPORTED
                    this.dataProps(Constants.COMPONENT_HAS_LOCATION, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, componentDirectory+compLoc.substring(compLoc.lastIndexOf("/")+1, compLoc.length()), XSDDatatype.XSDstring);
 

                  //extracting the actual inputs,outputs and other factors for the Abstract component
                    System.out.println("EXTRACTION BEGINS "+AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5));
                    String componentCatalogQueryforActualInputsandOutputsforAbstractComponent = Queries.componentCatalogQueryforActualInputsandOutputsforAbstractComponent(PREFIX_COMP_CATALOG,AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)+"Class");
                    rnew2 = null;
                    rnew2 = queryComponentCatalog(componentCatalogQueryforActualInputsandOutputsforAbstractComponent);
                   
                    HashSet<String> inputsAbsComp=new HashSet<>();
                    HashSet<String> outputsAbsComp=new HashSet<>();
                    boolean compConcreteAbs=false;
                    System.out.println("what we have "+AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5));
                    while(rnew2.hasNext())
                    {
                    	QuerySolution qsnew = rnew2.next();
                    	Resource nodenew = qsnew.getResource("?n");
                        Resource input = qsnew.getResource("?i");
                        Resource output = qsnew.getResource("?o");
                        Literal concr=qsnew.getLiteral("?concr");
                        Literal loc=qsnew.getLiteral("?loc");
                        System.out.println("nodenew inside now: "+nodenew.getLocalName());
                        if(nodenew.getLocalName().equals(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)))
                        {
                        	inputsAbsComp.add(input.getLocalName());
                        	outputsAbsComp.add(output.getLocalName());
                        	if(concr!=null)
                        	{
                        		compConcreteAbs=concr.getBoolean();
                        	}
                        }
                    }
                    System.out.println("ABSTRACT COMPONENT----------");
                    for(String i:inputsComp)
                    	System.out.println("inputs are: "+i);
                    for(String o:outputsComp)
                    	System.out.println("outputs are: "+o);
                    System.out.println("isConcrete is: "+compConcreteAbs);
                    System.out.println("EXTRACTED THE INPUTS AND OUTPUTS ABSTRACT COMPONENT BY HERE");
                    
                    //EXPORTING THE USER WHO CREATED THE COMPONENT
                    this.dataProps(Constants.PROV_WAS_ATTRIBUTED_TO,AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent,userName,XSDDatatype.XSDstring);
                    
                  //EXPORTING THE FACT THAT CLASSNAME-CLASS IS A CLASSNAME
                    this.classIsaClass(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1, finalversionforNewAbstractComponent.length()), AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);
                    classNames.put(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase(), AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1, finalversionforNewAbstractComponent.length()));
          
                    //IS CONCRETE EXPORTED
                    this.dataProps(Constants.COMPONENT_IS_CONCRETE, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, compConcreteAbs+"", XSDDatatype.XSDboolean);
                    
                    
                    //RDFS LABEL EXPORTED for canonical instance
                    this.dataProps(Constants.RDFS_LABEL, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5), XSDDatatype.XSDstring);

                    
                  //RDFS LABEL EXPORTED for class
                    this.dataProps2(Constants.RDFS_LABEL, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1, finalversionforNewAbstractComponent.length()), AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5), XSDDatatype.XSDstring);                 
                    
                    
                    //INPUTS-- EXPORTED
                    this.inputsOutputs(Constants.COMPONENT_HAS_INPUT, inputsComp, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);
                    
                    //OUTPUTS-- EXPORTED
                    this.inputsOutputs(Constants.COMPONENT_HAS_OUTPUT, outputsComp, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);

                    
                    /////NEW ADDITION FOR THE CONCRETE COMPONENT
                    
                  //STEP1: EXTRACT ONLY THE (INPUTS) PARAMETERS IF THEY EXIST FOR COMPONENTS
                    
                  System.out.println("INPUT PARAMETERS EXTRACTION PRINTING COMPONENTS");
                  ComponentVersion.Step1(hsi,componentCatalog, taxonomyExport);
                  
                  
                  
                 //STEP2: EXTRACT ONLY THE (INPUTS) DATA VARIABLES IF THEY EXIST FOR COMPONENTS
                  System.out.println("INPUT DATA EXTRACTION PRINTING COMPONENTS");
                  ComponentVersion.Step2(hsi,componentCatalog, taxonomyExport);
                  
                //STEP3: EXTRACT ONLY THE (OUTPUTS) DATA VARIABLES IF THEY EXIST FOR COMPONENTS
                  System.out.println("OUTPUT DATA EXTRACTION PRINTING COMPONENTS");
                  ComponentVersion.Step3(hso,componentCatalog, taxonomyExport);

                  
                    //STEP4: EXTRACT ONLY THE PARAMETERS IF THEY EXIST FOR ABSTRACT COMPONENTS
                    System.out.println("INPUT PARAMETERS EXTRACTION PRINTING FOR ABSTARCT COMPONENTS");
                    ComponentVersion.Step4(inputsComp,componentCatalog, taxonomyExport);
                    
                    
                  //STEP5: EXTRACT ONLY THE (INPUTS) DATA VARIABLES IF THEY EXIST FOR ABSTRACT COMPONENTS
                    System.out.println("INPUT DATA EXTRACTION PRINTING FOR ABSTRACT COMPONENTS ONLY");
                    ComponentVersion.Step5(inputsComp,componentCatalog, taxonomyExport);
                    
                  //STEP6: EXTRACT ONLY THE (OUTPUTS) DATA VARIABLES IF THEY EXIST FOR ABSTRACT COMPONENTS
                    System.out.println("OUTPUT DATA EXTRACTION PRINTING FOR ABSTRACT COMPONENTS");
                    ComponentVersion.Step6(outputsComp,componentCatalog, taxonomyExport);

                	System.out.println("THIS ENDS THE FIRST CASE PART-2");  
                	  

                  }
	
              }
                
                
  
              //Case2: Direct Export since the size of the found concr comps with similar names is 0
                if( namesOfConcreteCompswithSameNames.isEmpty() && namesOfAbsCompswithSameNames.isEmpty())
                {

                	//EXPORTING IT NOW:
                	System.out.println("ULTIMATE POINT ::::CHECK::::"+concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5));
                	
//                	//concrete component individual
//                	this.addIndividualConcrete2(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"_V1");
//                  
//                  //subclass relation
//                    this.addIndividualConcreteSubclass2(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1" , AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)+"_V1");

                	ModelUtils.addIndividualConcreteSubclass2(this.taxonomyExport, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASSV1", AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)+"_CLASSV1");
                	
//                	//abstract component individual
//                    this.addIndividualAbstract2(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)+"_V1");

                    
//                    //subclass relation for abstract component
//                    this.addIndividualAbstractSubclass2(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+"_V1");

                    ModelUtils.addIndividualConcreteSubclass2(this.taxonomyExport, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)+"_CLASSV1","COMPONENT");
                	
                    
                    System.out.println("EXPORTED THE BASIC CONCRETE COMPONENT BY HERE");
                    //export of ABSTRACT component ends
                    
                    
                  //for the component blankNode extraction for HARDWARE and SOFTWARE dependencies:
                    String componentCatalogQuerydependencies = Queries.componentCatalogQuerydependencies(PREFIX_COMP_CATALOG,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"Class");
                    rnew2 = null;
                    rnew2 = queryComponentCatalog(componentCatalogQuerydependencies);
                    Float memval=null;
                    Float stval=null;
                    boolean needsval=true;
                    String minversionval="";
                    if(rnew2.hasNext())
                    {
                    	QuerySolution qsnew = rnew2.next();
                    	Resource nodenew = qsnew.getResource("?n");
                    	Resource res11 = qsnew.getResource("?res");
                    	
                    	try{
                    		Resource minversion=qsnew.getResource("?minversion");
                    		minversionval=minversion.getLocalName();
                    	Literal needs=qsnew.getLiteral("?needs64bit");
                    	needsval=needs.getBoolean();
                    	Literal mem=qsnew.getLiteral("?mem");
                    	memval=mem.getFloat();
                    	Literal st=qsnew.getLiteral("?st");
                    	stval=st.getFloat();
                    	System.out.println("res is "+res11);
                    	System.out.println("needs64bit "+needs);
                    	System.out.println("minversion is "+minversionval);
                    		System.out.println("mem "+mem);
                    		System.out.println("st "+st);
                    	}catch(Exception e){}
                    		
                    	
                    } 
                  //extracting the actual inputs,outputs and other factors for the component
                    
                    String componentCatalogQueryforActualInputsandOutputsforComponent = Queries.componentCatalogQueryforActualInputsandOutputsforComponent(PREFIX_COMP_CATALOG,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"Class");
                    rnew2 = null;
                    rnew2 = queryComponentCatalog(componentCatalogQueryforActualInputsandOutputsforComponent);

                    HashSet<String> inputsComp=new HashSet<>();
                    HashSet<String> outputsComp=new HashSet<>();
                    String compLoc="";
                    boolean compConcrete=false;
                    String doc11="";
                    while(rnew2.hasNext())
                    {
                    	QuerySolution qsnew = rnew2.next();
                    	Resource nodenew = qsnew.getResource("?n");
                    	Resource input = qsnew.getResource("?i");
                    	Resource output = qsnew.getResource("?o");
                    	Literal concr=qsnew.getLiteral("?concr");
                    	Literal loc=qsnew.getLiteral("?loc");
                    	Literal doc=qsnew.getLiteral("?doc");
                    	if(nodenew.getLocalName().equals(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)))
                    	{
                    		inputsComp.add(input.getLocalName());
                    		outputsComp.add(output.getLocalName());
                    		if(loc!=null && compLoc.equals(""))
                    			compLoc=loc.getString();
                    		if(concr!=null){
                    			compConcrete=concr.getBoolean();
                    		}
                    		if(doc!=null){
                    			doc11=doc.getString();
                    		}
                    	}
                    }
                    System.out.println("MAIN COMPONENT----------");
                    for(String i:inputsComp)
                    	System.out.println("inputs are: "+i);
                    for(String o:outputsComp)
                    	System.out.println("outputs are: "+o);
                    System.out.println("Location is: "+compLoc);
                    System.out.println("isConcrete is: "+compConcrete);
                    //String compLoc2="/Users/Tirthmehta/Desktop/TestingDomain2/Component/";
                    
                    //EXPORTING THE MD5 FOR THE COMPONENT CODE
                    try{
                    this.dataProps(Constants.COMPONENT_HAS_MD5_CODE,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1",EncodingUtils.MD5ComponentCode(componentDirectory+compLoc.substring(compLoc.lastIndexOf("/")+1,compLoc.length())+".zip"),XSDDatatype.XSDstring);
                    }catch(Exception e){System.out.println("ERROR");}
                    
                    
                    //EXPORTING THE USER WHO CREATED THE COMPONENT
                    this.dataProps(Constants.PROV_WAS_ATTRIBUTED_TO,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1",userName,XSDDatatype.XSDstring);

                  //EXPORTING THE FACT THAT CLASSNAME-CLASS IS A CLASSNAME
                    this.classIsaClass(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASSV1", concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1");
                    classNames.put(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase(), concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASSV1");
                    //EXPORTING THE DOCUMENTATION
                    if (!doc11.equals("")){
                        this.dataProps(Constants.COMPONENT_HAS_DOCUMENTATION, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1",doc11,XSDDatatype.XSDstring);
                    }
           
                    //IS CONCRETE EXPORTED
                    this.dataProps(Constants.COMPONENT_IS_CONCRETE, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1",compConcrete+"", XSDDatatype.XSDboolean);

                    
                    //RDFS LABEL EXPORTED for canonical instance
                    this.dataProps(Constants.RDFS_LABEL, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1", concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5), XSDDatatype.XSDstring);
                    
                  //RDFS LABEL EXPORTED for class
                    this.dataProps2(Constants.RDFS_LABEL, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASSV1",concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5) , XSDDatatype.XSDstring);
                    
                    
                    //INPUTS-- EXPORTED
                    this.inputsOutputs(Constants.COMPONENT_HAS_INPUT, inputsComp, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1");

                    
                    //OUTPUTS-- EXPORTED
                    this.inputsOutputs(Constants.COMPONENT_HAS_OUTPUT, outputsComp, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1");

                    
                  //HAS LOCATION EXPORTED
                    this.dataProps(Constants.COMPONENT_HAS_LOCATION, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1", componentDirectory+compLoc.substring(compLoc.lastIndexOf("/")+1, compLoc.length()), XSDDatatype.XSDstring);

                    //EXPORTING THE HARDWARE AND SOFTWARE DEPENDENCY
                   exportHardwareSoftwareDependencies(needsval, memval, stval, memval, concrComponent);
                  
                    
                    
                    
                    
   
                  //extracting the actual inputs,outputs and other factors for the Abstract component
                    System.out.println("EXTRACTION BEGINS "+AbstractSuperClass.getLocalName());
                    String componentCatalogQueryforActualInputsandOutputsforAbstractComponent = Queries.componentCatalogQueryforActualInputsandOutputsforAbstractComponent(PREFIX_COMP_CATALOG,AbstractSuperClass.getLocalName());
                    rnew2 = null;
                    rnew2 = queryComponentCatalog(componentCatalogQueryforActualInputsandOutputsforAbstractComponent);
                   
                    HashSet<String> inputsAbsComp=new HashSet<>();
                    HashSet<String> outputsAbsComp=new HashSet<>();
                    boolean compConcreteAbs=false;
                    System.out.println("what we have "+AbstractSuperClass.getLocalName());
                    String docAbs="";
                    while(rnew2.hasNext())
                    {
                    	QuerySolution qsnew = rnew2.next();
                    	Resource nodenew = qsnew.getResource("?n");
                        Resource input = qsnew.getResource("?i");
                        Resource output = qsnew.getResource("?o");
                        Literal concr=qsnew.getLiteral("?concr");
                        Literal loc=qsnew.getLiteral("?loc");
                        Literal doc=qsnew.getLiteral("?doc");
                        System.out.println("nodenew inside now: "+nodenew.getLocalName());
                        if(nodenew.getLocalName().equals(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)))
                        {
                        	inputsAbsComp.add(input.getLocalName());
                        	outputsAbsComp.add(output.getLocalName());
                        	if(concr!=null)
                        	{
                        		compConcreteAbs=concr.getBoolean();
                        	}
                        	if(doc!=null)
                        	{
                        		docAbs=doc.getString();
                        	}
                        }
                    }
                    System.out.println("ABSTRACT COMPONENT----------");
                    for(String i:inputsComp)
                    	System.out.println("inputs are: "+i);
                    for(String o:outputsComp)
                    	System.out.println("outputs are: "+o);
                    System.out.println("isConcrete is: "+compConcreteAbs);
                    System.out.println("EXTRACTED THE INPUTS AND OUTPUTS ABSTRACT COMPONENT BY HERE");
                    
                    //EXPORTING THE USER WHO CREATED THE COMPONENT
                    this.dataProps(Constants.PROV_WAS_ATTRIBUTED_TO,AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+"_V1",userName,XSDDatatype.XSDstring);
                    
                    
                  //EXPORTING THE FACT THAT CLASSNAME-CLASS IS A CLASSNAME
                    this.classIsaClass(AbstractSuperClass.getLocalName().toUpperCase().substring(0,AbstractSuperClass.getLocalName().length()-5)+"_CLASSV1", AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+"_V1");
                    classNames.put(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase(),AbstractSuperClass.getLocalName().toUpperCase().substring(0,AbstractSuperClass.getLocalName().length()-5)+"_CLASSV1");
                    
                  //DOCUMENTATION EXPORTED
                  if (!docAbs.equals("")){
                    this.dataProps(Constants.COMPONENT_HAS_DOCUMENTATION, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+"_V1", docAbs, XSDDatatype.XSDstring);
                  }
                    
                    //IS CONCRETE EXPORTED
                    this.dataProps(Constants.COMPONENT_IS_CONCRETE, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+"_V1", compConcreteAbs+"", XSDDatatype.XSDboolean);

                    
                    //RDFS LABEL EXPORTED for canonical instance
                    this.dataProps(Constants.RDFS_LABEL, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+"_V1", AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5), XSDDatatype.XSDstring);

                    
                  //RDFS LABEL EXPORTED for class
                    this.dataProps2(Constants.RDFS_LABEL, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+"_CLASSV1", AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5), XSDDatatype.XSDstring);

                    
                    
                    //INPUTS-- EXPORTED
                    this.inputsOutputs(Constants.COMPONENT_HAS_INPUT, inputsComp, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+"_V1");

                    
                    //OUTPUTS-- EXPORTED
                    this.inputsOutputs(Constants.COMPONENT_HAS_OUTPUT, outputsComp, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+"_V1");

                    
                    /////NEW ADDITION FOR THE CONCRETE COMPONENT
                    
                  //STEP1: EXTRACT ONLY THE (INPUTS) PARAMETERS IF THEY EXIST FOR COMPONENTS
                  System.out.println("INPUT PARAMETERS EXTRACTION PRINTING COMPONENTS");
                  ComponentVersion.Step1(hsi, componentCatalog, taxonomyExport);
                  
                  
                 //STEP2: EXTRACT ONLY THE (INPUTS) DATA VARIABLES IF THEY EXIST FOR COMPONENTS
                  System.out.println("INPUT DATA EXTRACTION PRINTING COMPONENTS");
                  ComponentVersion.Step2(hsi, componentCatalog, taxonomyExport);
                  
                  
                //STEP3: EXTRACT ONLY THE (OUTPUTS) DATA VARIABLES IF THEY EXIST FOR COMPONENTS
                  System.out.println("OUTPUT DATA EXTRACTION PRINTING COMPONENTS");
                  ComponentVersion.Step3(hso, componentCatalog, taxonomyExport);
                    

                //STEP4: EXTRACT ONLY THE PARAMETERS IF THEY EXIST FOR ABSTRACT COMPONENTS
                  System.out.println("INPUT PARAMETERS EXTRACTION PRINTING FOR ABSTARCT COMPONENTS");
                  ComponentVersion.Step4(inputsComp, componentCatalog, taxonomyExport);

              //STEP5: EXTRACT ONLY THE (INPUTS) DATA VARIABLES IF THEY EXIST FOR ABSTRACT COMPONENTS
                  System.out.println("INPUT DATA EXTRACTION PRINTING FOR ABSTRACT COMPONENTS ONLY");
                  ComponentVersion.Step5(inputsComp, componentCatalog, taxonomyExport);

              //STEP6: EXTRACT ONLY THE (OUTPUTS) DATA VARIABLES IF THEY EXIST FOR ABSTRACT COMPONENTS
                  System.out.println("OUTPUT DATA EXTRACTION PRINTING FOR ABSTRACT COMPONENTS");
                  ComponentVersion.Step6(outputsComp, componentCatalog, taxonomyExport);
                    
                    
                }
              //Case2: Checking the components once for same Inputs and Outputs
                else if(namesOfConcreteCompswithSameNames.size()!=0 && namesOfAbsCompswithSameNames.size()!=0)
                {
                	
                	System.out.println("WE ARE IN THIS CASE NOW WHERE THE CONCRETE COMP'S INTERNAL FACTORS HAVE CHANGED-----");
                //this means we have found a list of similar abstract components in the Taxonomy Model
                //now there are 2 points we have to consider here
               //first we have to check the inputs and outputs of all the similar named abstract components in the Taxonomy Model
               //if there is one with the same number of inputs and outputs then just break off
               //if there is no one with the same inputs and outputs then we have to export a new one out
                
                System.out.println("NAMES FOR SIMILAR NAMED CONCRETE COMPONENTS");
                for(String x:namesOfConcreteCompswithSameNames)
                	System.out.println(x);
                
                System.out.println("THE INPUTS AND OUTPUTS IN THE CURRENT CONCRETE COMPONENT ARE:");
                ///
                String componentCatalogQueryforActualInputsandOutputsforConcreteComponent = Queries.componentCatalogQueryforActualInputsandOutputsforComponent(PREFIX_COMP_CATALOG,nodenew11.getLocalName()+"Class");
                rnew2 = null;
                rnew2 = queryComponentCatalog(componentCatalogQueryforActualInputsandOutputsforConcreteComponent);
               
                HashSet<String> inputsCurrentConcrComp=new HashSet<>();
                HashSet<String> outputsCurrentConcrComp=new HashSet<>();
                boolean compConcrete=false;
                String compLoc=null;
                System.out.println("what we have "+concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5));
                while(rnew2.hasNext())
                {
                	QuerySolution qsnew = rnew2.next();
                	Resource nodenew = qsnew.getResource("?n");
                    Resource input = qsnew.getResource("?i");
                    Resource output = qsnew.getResource("?o");
                    Literal loc=qsnew.getLiteral("?loc");
                    System.out.println("nodenew now case2: "+nodenew.getLocalName());
                    System.out.println("concrComp now case2: "+concrComponent.getLocalName());
                    if(nodenew.getLocalName().equals(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)))
                    {
                    	inputsCurrentConcrComp.add(input.getLocalName());
                    	outputsCurrentConcrComp.add(output.getLocalName());
                    }
                    if(loc!=null)
                    	compLoc=loc.getString();
                }
                System.out.println("CONCRETE COMPONENT----------");
                for(String i:inputsCurrentConcrComp)
                	System.out.println("inputs are: "+i);
                for(String o:outputsCurrentConcrComp)
                	System.out.println("outputs are: "+o);
                System.out.println("EXTRACTED THE INPUTS AND OUTPUTS ABSTRACT COMPONENT BY HERE");
                ///
                
                
                
                //NOW WE HAVE TO FIND OUT THE NUMBER OF INPUTS AND OUTPUTS IN EACH OF THE SIMILAR NAMED CONCRETE COMPONENTS
                int clarifyToExportConcrComp=0,codeisdifferentbutinputsandoutputsaresame=0;
                for(String whatwehave:namesOfConcreteCompswithSameNames)
                {
                	String inputsandoutputsforsimilarnamedComps = Queries.TaxonomyExportQueryforSubclassCheckfinal();
                    rnew2 = null;
                    rnew2 = queryLocalTaxonomyModelRepository(inputsandoutputsforsimilarnamedComps);
                   
                    HashSet<String> similarAbsInps=new HashSet<>();
                    HashSet<String> similarAbsOuts=new HashSet<>();
                    System.out.println("what we have "+concrComponent.getLocalName());
                    String one=null,two=null;
                    while(rnew2.hasNext())
                    {
                    	QuerySolution qsnew = rnew2.next();
                    	Resource nodenew = qsnew.getResource("?n");
                    	Resource x = qsnew.getResource("?x");
//                    	Resource y = qsnew.getResource("?y");
                        Resource input = qsnew.getResource("?i");
                        Resource output = qsnew.getResource("?o");
                        Literal codeMD5=qsnew.getLiteral("?md5");
                        
                        if(nodenew.getLocalName().toUpperCase().equals(whatwehave) && !nodenew.getLocalName().toUpperCase().equals(x.getLocalName()))
                        {
                        	System.out.println("Node inside case2:? "+nodenew.getLocalName());
                            System.out.println("x inside case2: ? "+x.getLocalName());
//                            System.out.println("y inside case2: ? "+y.getLocalName());
                            System.out.println("input is :"+input.getLocalName());
                            System.out.println("output is :"+output.getLocalName());
                        	similarAbsInps.add(input.getLocalName());
                        	similarAbsOuts.add(output.getLocalName());
                        	if(codeMD5!=null)
                        		two=codeMD5.getString();
                        }
                    }
                    System.out.println("input size "+similarAbsInps.size());
                    System.out.println("output size "+similarAbsOuts.size());
                    //String compLoc2="/Users/Tirthmehta/Desktop/TestingDomain2/Component/";
                    //added factor: check the code too
                    
                    try{
                        one=EncodingUtils.MD5ComponentCode(componentDirectory+compLoc.substring(compLoc.lastIndexOf("/")+1,compLoc.length())+".zip");
                        }catch(Exception e){System.out.println("ERROR first");}
                      
                   
                    if(similarAbsInps.size()==inputsCurrentConcrComp.size() && similarAbsOuts.size()==outputsCurrentConcrComp.size())
                    {
                    	if(one!=null && two!=null && one.equals(two))
                    	{
                    	clarifyToExportConcrComp=1;
                    	break;
                    	}
                    	else
                    		codeisdifferentbutinputsandoutputsaresame=1;
                    	//the else here means that the inputs and outputs are the same but the code is different
                    	//so we will export only subclass
                    }
                }
                if(clarifyToExportConcrComp==1)
                	System.out.println("There is a match CONCRETE COMPONENT CASE and BREAKKKKKKKKKKK!!!!!!!!!!!!!!");
                else if(clarifyToExportConcrComp==0 && codeisdifferentbutinputsandoutputsaresame==1)
                {
                	System.out.println("we are in the case where the inputs and outputs are the same but the code is different so we export as a subclass with latest version");
                	String finalversionforNewAbstractComponent="";
                	String finalversionforLatestAbstractComponent="";
                  	int max=Integer.MIN_VALUE;
                  	for(String x:namesOfConcreteCompswithSameNames)
                  	{
                  		String temp=x.substring(x.lastIndexOf("_V"),x.length());
                  		System.out.println("what is temp "+temp);
                  		int temp2=Integer.parseInt(temp.substring(2,temp.length()));
                  		if(max<temp2)
                  		{
                  			max=temp2;
                  			
                  		}
                  	}
                  	finalversionforLatestAbstractComponent="_V"+max;
                  	max++;
                  	finalversionforNewAbstractComponent="_V"+max;
                  	System.out.println("THE MOST LATEST VERSION IS : "+finalversionforNewAbstractComponent);
                	
                	//concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1,finalversionforNewAbstractComponent.length()),AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)+"_CLASS"+finalversionforNewAbstractComponent.substring(1,finalversionforNewAbstractComponent.length())
                	//this means we export it as a subclass
                	System.out.println("ABS IS: "+AbstractSuperClass.getLocalName());
                	
//                //concrete component individual
//                this.addIndividualConcrete2(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"_V1");
//                
//                  
//                //subclass relation
//                this.addIndividualConcreteSubclass2(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_V1", Abs.getLocalName());
//                  
               ModelUtils.addIndividualConcreteSubclass2(this.taxonomyExport, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1,finalversionforNewAbstractComponent.length()),AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)+"_CLASS"+finalversionforLatestAbstractComponent.substring(1,finalversionforLatestAbstractComponent.length()));
                	
                	
              //for the component blankNode extraction for HARDWARE and SOFTWARE dependencies:
                String componentCatalogQuerydependencies = Queries.componentCatalogQuerydependencies(PREFIX_COMP_CATALOG,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"Class");
                rnew2 = null;
                rnew2 = queryComponentCatalog(componentCatalogQuerydependencies);
                Float memval=null;
                Float stval=null;
                boolean needsval=true;
                String minversionval="";
                if(rnew2.hasNext())
                {
                	QuerySolution qsnew = rnew2.next();
                	Resource nodenew = qsnew.getResource("?n");
                	Resource res11 = qsnew.getResource("?res");
                	
                	try{
                		Resource minversion=qsnew.getResource("?minversion");
                		minversionval=minversion.getLocalName();
                	Literal needs=qsnew.getLiteral("?needs64bit");
                	needsval=needs.getBoolean();
                	Literal mem=qsnew.getLiteral("?mem");
                	memval=mem.getFloat();
                	Literal st=qsnew.getLiteral("?st");
                	stval=st.getFloat();
                	System.out.println("res is "+res11);
                	System.out.println("needs64bit "+needs);
                	System.out.println("minversion is "+minversionval);
                		System.out.println("mem "+mem);
                		System.out.println("st "+st);
                	}catch(Exception e){}
                		
                	
                } 
                
              //EXPORTING THE HARDWARE AND SOFTWARE DEPENDENCY
                   exportHardwareSoftwareDependencies(needsval, memval, stval, memval, concrComponent);
                  
                
                
                
                //extracting the actual inputs,outputs and other factors for the component
                  
                  String componentCatalogQueryforActualInputsandOutputsforComponent = Queries.componentCatalogQueryforActualInputsandOutputsforComponent(PREFIX_COMP_CATALOG,concrComponent.getLocalName());
                  rnew2 = null;
                  rnew2 = queryComponentCatalog(componentCatalogQueryforActualInputsandOutputsforComponent);

                  HashSet<String> inputsComp=new HashSet<>();
                  HashSet<String> outputsComp=new HashSet<>();
                  compLoc="";
                  boolean compConcrete123=false;
                  String doc11="";
                  while(rnew2.hasNext())
                  {
                  	QuerySolution qsnew = rnew2.next();
                  	Resource nodenew = qsnew.getResource("?n");
                  	Resource input = qsnew.getResource("?i");
                  	Resource output = qsnew.getResource("?o");
                  	Literal concr=qsnew.getLiteral("?concr");
                  	Literal loc=qsnew.getLiteral("?loc");
                  	Literal doc=qsnew.getLiteral("?doc");
                  	
                  	if(nodenew.getLocalName().equals(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)))
                  	{
                  		inputsComp.add(input.getLocalName());
                  		outputsComp.add(output.getLocalName());
                  		if(loc!=null && compLoc.equals(""))
                  			compLoc=loc.getString();
                  		if(concr!=null)
                  		{
                  			compConcrete123=concr.getBoolean();
                  		}
                  		if(doc!=null)
                  		{
                  			doc11=doc.getString();
                  		}
                  	}
                  }
                  System.out.println("MAIN COMPONENT----------");
                  for(String i:inputsComp)
                  	System.out.println("inputs are: "+i);
                  for(String o:outputsComp)
                  	System.out.println("outputs are: "+o);
                  System.out.println("Location is: "+compLoc);
                  System.out.println("isConcrete is: "+compConcrete);
                  
                  //EXPORTING THE PROV_WAS_REVISION_OF
                  OntProperty propSelec256 = taxonomyExport.createOntProperty(Constants.PROV_WAS_REVISION_OF);
                  Resource source256 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+ EncodingUtils.encode(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+finalversionforNewAbstractComponent) );
                  Individual instance256 = (Individual) source256.as( Individual.class );
                  if((concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+finalversionforLatestAbstractComponent).contains("http://")){//it is a URI
                      instance256.addProperty(propSelec256,NEW_TAXONOMY_CLASS+concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+finalversionforLatestAbstractComponent);            
                  }else{//it is a local resource
                      instance256.addProperty(propSelec256, taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+finalversionforLatestAbstractComponent)));
                  } 
//                  String compLoc2="/Users/Tirthmehta/Desktop/TestingDomain2/Component/";
                  //EXPORTING THE MD5 FOR THE COMPONENT CODE
                  try{
                  this.dataProps(Constants.COMPONENT_HAS_MD5_CODE,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent,EncodingUtils.MD5ComponentCode(componentDirectory+compLoc.substring(compLoc.lastIndexOf("/")+1,compLoc.length())+".zip"),XSDDatatype.XSDstring);
                  }catch(Exception e){System.out.println("ERROR");}
                  
                  //EXPORTING THE USER WHO CREATED THE COMPONENT
                  this.dataProps(Constants.PROV_WAS_ATTRIBUTED_TO,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent,userName,XSDDatatype.XSDstring);
                  

                //EXPORTING THE FACT THAT CLASSNAME-CLASS IS A CLASSNAME
                  this.classIsaClass(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1,finalversionforNewAbstractComponent.length()), concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);
                  classNames.put(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase(),concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1,finalversionforNewAbstractComponent.length()));

                //DOCUMENTATION EXPORTED
                if (!doc11.equals("")){
                  this.dataProps(Constants.COMPONENT_HAS_DOCUMENTATION, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, doc11+"", XSDDatatype.XSDstring);
                }
                  
                  //IS CONCRETE EXPORTED
                  this.dataProps(Constants.COMPONENT_IS_CONCRETE, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, compConcrete+"", XSDDatatype.XSDboolean);
                  
                  
                  //RDFS LABEL EXPORTED for canonical instance
                  this.dataProps(Constants.RDFS_LABEL, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5), XSDDatatype.XSDstring);
                 
                  
                //RDFS LABEL EXPORTED for class
                  this.dataProps2(Constants.RDFS_LABEL, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1,finalversionforNewAbstractComponent.length()), concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5), XSDDatatype.XSDstring);
                  
                  
                  //INPUTS-- EXPORTED
                  this.inputsOutputs(Constants.COMPONENT_HAS_INPUT, inputsComp, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);

                  
                  //OUTPUTS-- EXPORTED
                  this.inputsOutputs(Constants.COMPONENT_HAS_OUTPUT, outputsComp, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);

                  
                //HAS LOCATION EXPORTED
                  this.dataProps(Constants.COMPONENT_HAS_LOCATION, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, componentDirectory+compLoc.substring(compLoc.lastIndexOf("/")+1, compLoc.length()),XSDDatatype.XSDstring);
                  
                	
               //STEP1: EXTRACT ONLY THE (INPUTS) PARAMETERS IF THEY EXIST FOR COMPONENTS
                System.out.println("INPUT PARAMETERS EXTRACTION PRINTING COMPONENTS");
                ComponentVersion.Step1(hsi, componentCatalog, taxonomyExport); 
                
                //STEP2: EXTRACT ONLY THE (INPUTS) DATA VARIABLES IF THEY EXIST FOR COMPONENTS
                System.out.println("INPUT DATA EXTRACTION PRINTING COMPONENTS");
                ComponentVersion.Step2(hsi, componentCatalog, taxonomyExport);
                
                
                //STEP3: EXTRACT ONLY THE (OUTPUTS) DATA VARIABLES IF THEY EXIST FOR COMPONENTS
                System.out.println("OUTPUT DATA EXTRACTION PRINTING COMPONENTS");
                ComponentVersion.Step3(hso, componentCatalog, taxonomyExport);
                
                }
                else if(clarifyToExportConcrComp==0 && codeisdifferentbutinputsandoutputsaresame==0)
                {
                	System.out.println("NOW CHECK IF THE INPUTS AND OUTPUTS WERE THE SAME AND ONLY THE CODE HAD CHANGED...IF YES THEN WE EXPORT ONLY A SUBCLASS FACTOR");
                	
                	System.out.println("There is NO MATCH AND HENCE WE HAVE TO CREATE A VERSIONING HERE");
                	System.out.println("THIS STARTS THE FIRST CASE PART-2");  
              	  
                	String finalversionforNewAbstractComponent="";
                	String finalversionforLatestAbstractComponent="";
                  	int max=Integer.MIN_VALUE;
                  	for(String x:namesOfAbsCompswithSameNames)
                  	{
                  		String temp=x.substring(x.lastIndexOf("_V"),x.length());
                  		System.out.println("what is temp "+temp);
                  		int temp2=Integer.parseInt(temp.substring(2,temp.length()));
                  		if(max<temp2)
                  		{
                  			max=temp2;
                  			
                  		}
                  	}
                  	finalversionforLatestAbstractComponent="_V"+max;
                  	max++;
                  	finalversionforNewAbstractComponent="_V"+max;
                  	System.out.println("THE MOST LATEST VERSION IS : "+finalversionforNewAbstractComponent);
                  	//ABOVE TASK DONE BY HERE
                  	
//                  //concrete component individual
//                  	this.addIndividualConcrete2(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+finalversionforNewAbstractComponent);
//                  	
//                  	
//                  //subclass relation
//                  	this.addIndividualConcreteSubclass2(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)+finalversionforNewAbstractComponent);

                  	ModelUtils.addIndividualConcreteSubclass2(this.taxonomyExport, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1,finalversionforNewAbstractComponent.length()),AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)+"_CLASS"+finalversionforNewAbstractComponent.substring(1,finalversionforNewAbstractComponent.length()));
                	
                	//abstract component individual

                    
//                    //subclass relation for abstract component
//                  	this.addIndividualAbstractSubclass2(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);

                  	ModelUtils.addIndividualConcreteSubclass2(this.taxonomyExport, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)+"_CLASS"+finalversionforNewAbstractComponent.substring(1,finalversionforNewAbstractComponent.length()),"COMPONENT");
                    
                    System.out.println("EXPORTED THE BASIC CONCRETE COMPONENT BY HERE");
                    
                    
                    
                  //EXPORTING THE PROV_WAS_REVISION_OF
                    OntProperty propSelec255 = taxonomyExport.createOntProperty(Constants.PROV_WAS_REVISION_OF);
                    Resource source255 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+ EncodingUtils.encode(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent) );
                    Individual instance255 = (Individual) source255.as( Individual.class );
                    if((AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforLatestAbstractComponent).contains("http://")){//it is a URI
                        instance255.addProperty(propSelec255,NEW_TAXONOMY_CLASS+AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforLatestAbstractComponent);            
                    }else{//it is a local resource
                        instance255.addProperty(propSelec255, taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforLatestAbstractComponent)));
                    }
                    
                    
                  //EXPORTING THE PROV_WAS_REVISION_OF
                    OntProperty propSelec256 = taxonomyExport.createOntProperty(Constants.PROV_WAS_REVISION_OF);
                    Resource source256 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+ EncodingUtils.encode(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+finalversionforNewAbstractComponent) );
                    Individual instance256 = (Individual) source256.as( Individual.class );
                    if((concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+finalversionforLatestAbstractComponent).contains("http://")){//it is a URI
                        instance256.addProperty(propSelec256,NEW_TAXONOMY_CLASS+concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+finalversionforLatestAbstractComponent);            
                    }else{//it is a local resource
                        instance256.addProperty(propSelec256, taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+finalversionforLatestAbstractComponent)));
                    }  
                    
                    
                    
                    
                    //export of ABSTRACT component ends
                    
                    
                  //for the component blankNode extraction for HARDWARE and SOFTWARE dependencies:
                    String componentCatalogQuerydependencies = Queries.componentCatalogQuerydependencies(PREFIX_COMP_CATALOG,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"Class");
                    rnew2 = null;
                    rnew2 = queryComponentCatalog(componentCatalogQuerydependencies);
                    Float memval=null;
                    Float stval=null;
                    boolean needsval=true;
                    String minversionval="";
                    if(rnew2.hasNext())
                    {
                    	QuerySolution qsnew = rnew2.next();
                    	Resource nodenew = qsnew.getResource("?n");
                    	Resource res11 = qsnew.getResource("?res");
                    	
                    	try{
                    		Resource minversion=qsnew.getResource("?minversion");
                    		minversionval=minversion.getLocalName();
                    	Literal needs=qsnew.getLiteral("?needs64bit");
                    	needsval=needs.getBoolean();
                    	Literal mem=qsnew.getLiteral("?mem");
                    	memval=mem.getFloat();
                    	Literal st=qsnew.getLiteral("?st");
                    	stval=st.getFloat();
                    	System.out.println("res is "+res11);
                    	System.out.println("needs64bit "+needs);
                    	System.out.println("minversion is "+minversionval);
                    		System.out.println("mem "+mem);
                    		System.out.println("st "+st);
                    	}catch(Exception e){}
                    		
                    	
                    } 
                    
                  //EXPORTING THE HARDWARE AND SOFTWARE DEPENDENCY
                   exportHardwareSoftwareDependencies(needsval, memval, stval, memval, concrComponent);
                  
                    
                    
                    
                  //extracting the actual inputs,outputs and other factors for the component
                    
                    String componentCatalogQueryforActualInputsandOutputsforComponent = Queries.componentCatalogQueryforActualInputsandOutputsforComponent(PREFIX_COMP_CATALOG,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"Class");
                    rnew2 = null;
                    rnew2 = queryComponentCatalog(componentCatalogQueryforActualInputsandOutputsforComponent);

                    HashSet<String> inputsComp=new HashSet<>();
                    HashSet<String> outputsComp=new HashSet<>();
                    compLoc="";
                    boolean compConcrete112=false;
                    String doc11="";
                    while(rnew2.hasNext())
                    {
                    	QuerySolution qsnew = rnew2.next();
                    	Resource nodenew = qsnew.getResource("?n");
                    	Resource input = qsnew.getResource("?i");
                    	Resource output = qsnew.getResource("?o");
                    	Literal concr=qsnew.getLiteral("?concr");
                    	Literal loc=qsnew.getLiteral("?loc");
                    	Literal doc=qsnew.getLiteral("?doc");
                    	System.out.println("trying the nodenew comparison here: "+nodenew.getLocalName());
                    	if(nodenew.getLocalName().equals(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)))
                    	{
                    		inputsComp.add(input.getLocalName());
                    		outputsComp.add(output.getLocalName());
                    		if(loc!=null && compLoc.equals(""))
                    			compLoc=loc.getString();
                    		if(concr!=null)
                    		{
                    			compConcrete112=concr.getBoolean();
                    		}
                    		if(doc!=null)
                    		{
                    			doc11=doc.getString();
                    		}
                    	}
                    }
                    System.out.println("MAIN COMPONENT----------");
                    for(String i:inputsComp)
                    	System.out.println("inputs are: "+i);
                    for(String o:outputsComp)
                    	System.out.println("outputs are: "+o);
                    System.out.println("Location is: "+compLoc);
                    System.out.println("isConcrete is: "+compConcrete);
//                    String compLoc2="/Users/Tirthmehta/Desktop/TestingDomain2/Component/";
                    
                    //EXPORTING THE MD5 FOR THE COMPONENT CODE
                    try{
                    this.dataProps(Constants.COMPONENT_HAS_MD5_CODE,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent,EncodingUtils.MD5ComponentCode(componentDirectory+compLoc.substring(compLoc.lastIndexOf("/")+1,compLoc.length())+".zip"),XSDDatatype.XSDstring);
                    }catch(Exception e){System.out.println("ERROR");}
                    
                    
                  //EXPORTING THE USER WHO CREATED THE COMPONENT
                    this.dataProps(Constants.PROV_WAS_ATTRIBUTED_TO,concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent,userName,XSDDatatype.XSDstring);
                    
                    

                  //EXPORTING THE FACT THAT CLASSNAME-CLASS IS A CLASSNAME
                    this.classIsaClass(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1, finalversionforNewAbstractComponent.length()), concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);
                    classNames.put(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase(),concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1, finalversionforNewAbstractComponent.length()));
                    
                  //DOCUMENTATION EXPORTED
                  if (!doc11.equals("")){
                    this.dataProps(Constants.COMPONENT_HAS_DOCUMENTATION, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, doc11, XSDDatatype.XSDstring);
                  }
                    
                    //IS CONCRETE EXPORTED
                    this.dataProps(Constants.COMPONENT_IS_CONCRETE, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, compConcrete112+"", XSDDatatype.XSDboolean);

                    
                    //RDFS LABEL EXPORTED for canonical instance
                    this.dataProps(Constants.RDFS_LABEL, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5), XSDDatatype.XSDstring);

                    
                  //RDFS LABEL EXPORTED for class
                    this.dataProps2(Constants.RDFS_LABEL, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1,finalversionforNewAbstractComponent.length()), concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5), XSDDatatype.XSDstring);
                    
                    
                    //INPUTS-- EXPORTED
                    this.inputsOutputs(Constants.COMPONENT_HAS_INPUT, inputsComp, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);

                    
                    //OUTPUTS-- EXPORTED
                    this.inputsOutputs(Constants.COMPONENT_HAS_OUTPUT, outputsComp, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);

                    
                  //HAS LOCATION EXPORTED
                    this.dataProps(Constants.COMPONENT_HAS_LOCATION, concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, componentDirectory+compLoc.substring(compLoc.lastIndexOf("/")+1, compLoc.length()), XSDDatatype.XSDstring);
 

                  //extracting the actual inputs,outputs and other factors for the Abstract component
                    System.out.println("EXTRACTION BEGINS "+AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5));
                    String componentCatalogQueryforActualInputsandOutputsforAbstractComponent112 = Queries.componentCatalogQueryforActualInputsandOutputsforAbstractComponent(PREFIX_COMP_CATALOG,AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)+"Class");
                    rnew2 = null;
                    rnew2 = queryComponentCatalog(componentCatalogQueryforActualInputsandOutputsforAbstractComponent112);
                   
                    HashSet<String> inputsAbsComp=new HashSet<>();
                    HashSet<String> outputsAbsComp=new HashSet<>();
                    boolean compConcreteAbs=false;
                    String docAbs="";
                    System.out.println("what we have "+AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5));
                    while(rnew2.hasNext())
                    {
                    	QuerySolution qsnew = rnew2.next();
                    	Resource nodenew = qsnew.getResource("?n");
                        Resource input = qsnew.getResource("?i");
                        Resource output = qsnew.getResource("?o");
                        Literal concr=qsnew.getLiteral("?concr");
                        Literal loc=qsnew.getLiteral("?loc");
                        Literal doc=qsnew.getLiteral("?doc");
                        System.out.println("nodenew inside now: "+nodenew.getLocalName());
                        if(nodenew.getLocalName().equals(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5)))
                        {
                        	inputsAbsComp.add(input.getLocalName());
                        	outputsAbsComp.add(output.getLocalName());
                        	if(concr!=null)
                        	{
                        		compConcreteAbs=concr.getBoolean();
                        	}
                        	if(doc!=null)
                        	{
                        		docAbs=doc.getString();
                        	}
                        }
                    }
                    System.out.println("ABSTRACT COMPONENT----------");
                    for(String i:inputsComp)
                    	System.out.println("inputs are: "+i);
                    for(String o:outputsComp)
                    	System.out.println("outputs are: "+o);
                    System.out.println("isConcrete is: "+compConcreteAbs);
                    System.out.println("EXTRACTED THE INPUTS AND OUTPUTS ABSTRACT COMPONENT BY HERE");
                    
                    //EXPORTING THE USER WHO CREATED THE COMPONENT
                    this.dataProps(Constants.PROV_WAS_ATTRIBUTED_TO,AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent,userName,XSDDatatype.XSDstring);
                   
                    
                    
                  //EXPORTING THE FACT THAT CLASSNAME-CLASS IS A CLASSNAME
                    System.out.println("classisaclass "+AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1, finalversionforNewAbstractComponent.length()));
                    System.out.println("secondpara "+AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);
                    this.classIsaClass(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1, finalversionforNewAbstractComponent.length()), AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);
                    classNames.put(AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase(),AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1, finalversionforNewAbstractComponent.length()));
                    
                  //DOCUMENTATION EXPORTED
                  if (!docAbs.equals("")){
                    this.dataProps(Constants.COMPONENT_HAS_DOCUMENTATION, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, docAbs, XSDDatatype.XSDstring);
                  }
          
                    //IS CONCRETE EXPORTED
                    this.dataProps(Constants.COMPONENT_IS_CONCRETE, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, compConcreteAbs+"", XSDDatatype.XSDboolean);
                    
                    
                    //RDFS LABEL EXPORTED for canonical instance
                    this.dataProps(Constants.RDFS_LABEL, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5), XSDDatatype.XSDstring);

                    
                  //RDFS LABEL EXPORTED for class
                    this.dataProps2(Constants.RDFS_LABEL, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+"_CLASS"+finalversionforNewAbstractComponent.substring(1, finalversionforNewAbstractComponent.length()), AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5), XSDDatatype.XSDstring);                 
                    
                    
                    //INPUTS-- EXPORTED
                    this.inputsOutputs(Constants.COMPONENT_HAS_INPUT, inputsComp, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);
                    
                    //OUTPUTS-- EXPORTED
                    this.inputsOutputs(Constants.COMPONENT_HAS_OUTPUT, outputsComp, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent);

                    
                    /////NEW ADDITION FOR THE CONCRETE COMPONENT
                    
                  //STEP1: EXTRACT ONLY THE (INPUTS) PARAMETERS IF THEY EXIST FOR COMPONENTS
                    
                  System.out.println("INPUT PARAMETERS EXTRACTION PRINTING COMPONENTS");
                  ComponentVersion.Step1(hsi, componentCatalog, taxonomyExport);
                  
                  
                  
                 //STEP2: EXTRACT ONLY THE (INPUTS) DATA VARIABLES IF THEY EXIST FOR COMPONENTS
                  System.out.println("INPUT DATA EXTRACTION PRINTING COMPONENTS");
                  ComponentVersion.Step2(hsi, componentCatalog, taxonomyExport);
                  
                //STEP3: EXTRACT ONLY THE (OUTPUTS) DATA VARIABLES IF THEY EXIST FOR COMPONENTS
                  System.out.println("OUTPUT DATA EXTRACTION PRINTING COMPONENTS");
                  ComponentVersion.Step3(hso, componentCatalog, taxonomyExport);

                  
                    //STEP4: EXTRACT ONLY THE PARAMETERS IF THEY EXIST FOR ABSTRACT COMPONENTS
                    System.out.println("INPUT PARAMETERS EXTRACTION PRINTING FOR ABSTARCT COMPONENTS");
                    ComponentVersion.Step4(inputsComp, componentCatalog, taxonomyExport);
                    
                    
                  //STEP5: EXTRACT ONLY THE (INPUTS) DATA VARIABLES IF THEY EXIST FOR ABSTRACT COMPONENTS
                    System.out.println("INPUT DATA EXTRACTION PRINTING FOR ABSTRACT COMPONENTS ONLY");
                    ComponentVersion.Step5(inputsComp, componentCatalog, taxonomyExport);
                    
                  //STEP6: EXTRACT ONLY THE (OUTPUTS) DATA VARIABLES IF THEY EXIST FOR ABSTRACT COMPONENTS
                    System.out.println("OUTPUT DATA EXTRACTION PRINTING FOR ABSTRACT COMPONENTS");
                    ComponentVersion.Step6(outputsComp, componentCatalog, taxonomyExport);

                	System.out.println("THIS ENDS THE FIRST CASE PART-2");  
                	  

                  }//new versioning for both

                }//both names are !=0

            }//Concrete Component Case
            

          }//decider point==1
            
            hsforComps.add(className);
       }//if part for the hash-set not repeating the same names
        if(typeComp.isURIResource()){ //only adds the type if the type is a uRI (not a blank node)

            System.out.println("GOING FOR PRINTING THE COMPONENT HERE "+NEW_TAXONOMY_CLASS_2);
            System.out.println("type is "+typeComp.getURI());

            String newchangeforthetype=typeComp.getURI().substring(typeComp.getURI().lastIndexOf("/"),typeComp.getURI().length());
            String newchangeforthetype2=newchangeforthetype.substring(newchangeforthetype.lastIndexOf("#"),newchangeforthetype.length());
            String tempURI = EncodingUtils.encode(Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+res.getLocalName());
            String key = newchangeforthetype2.toUpperCase().replace("#","");
            key = key.substring(0, key.length()-5);
            if(classNames.containsKey(key)){
                newchangeforthetype2 = "#"+ classNames.get(key);
            }
            OntClass cAux = OPMWModel.createClass(NEW_TAXONOMY_CLASS_2+"Component"+newchangeforthetype2);//repeated tuples will not be duplicated
            cAux.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+tempURI);     	
        }
        else{
            System.out.println("ANON RESOURCE "+typeComp.getURI()+" ignored");
        }
        if(rule!=null){
            //rules are strings
            ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+res.getLocalName(),
                rule.getString(),                    
                    Constants.WINGS_PROP_HAS_RULE);

            //ModelUtils.addDataProperty(OPMWModel, Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+res.getLocalName(),rule.getString(), Constants.OPMW_COMPONENT_HAS_RULES);
        }
        if(isConcrete!=null)
        {
            System.out.println("is component: "+comp.getLocalName()+" concrete: "+isConcrete.getBoolean());
            ModelUtils.addDataProperty(OPMWModel, Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+res.getLocalName(), isConcrete.getBoolean()+"", Constants.OPMW_DATA_PROP_IS_CONCRETE, XSDDatatype.XSDboolean);
        }
        ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+res.getLocalName(),
                Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+newTemplateName,                    
                    Constants.OPMW_PROP_IS_STEP_OF_TEMPLATE);            
        //p-plan interop
        ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+res.getLocalName(),
                Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+newTemplateName,                    
                    Constants.P_PLAN_PROP_IS_STEP_OF_PLAN);
        }
        System.out.println("PRINTING ALL THE COMPONENTS THAT EXIST IN THE INCOMING WORKFLOW TEMPLATE");
        System.out.println();
        for(String x:hsforComps)
        	System.out.println(x);
        System.out.println();
        
        
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
            ModelUtils.addIndividual(OPMWModel,newTemplateName_+variable.getLocalName(), Constants.OPMW_DATA_VARIABLE, "Data variable "+variable.getLocalName());
            //p-plan interop
            OntClass cVar = OPMWModel.createClass(Constants.P_PLAN_Variable);
            cVar.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_DATA_VARIABLE+"/"+EncodingUtils.encode(newTemplateName_+variable.getLocalName()));
           
            //we add the individual as a workflowTemplateArtifact as well            
            String aux = EncodingUtils.encode(Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+variable.getLocalName());
            OntClass cAux = OPMWModel.createClass(Constants.OPMW_WORKFLOW_TEMPLATE_ARTIFACT);//repeated tuples will not be duplicated
            cAux.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+aux);
                   
            if(dim!=null){//sometimes is null, but it shouldn't
                ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+variable.getLocalName(),
                        ""+dim.getInt(), Constants.OPMW_DATA_PROP_HAS_DIMENSIONALITY, XSDDatatype.XSDint);
                //System.out.println(res+" has dim: "+dim.getInt());
                if(dim.getInt()>0)
                	isCollection=true;
                else
                	isCollection=false;
                ModelUtils.addDataProperty(OPMWModel, Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+variable.getLocalName(), ""+isCollection, Constants.OPMW_DATA_PROP_IS_COLLECTION, XSDDatatype.XSDboolean);
            }
            //types of data variables
            if(type!=null){
                //sometimes there are some blank nodes asserted as types in the ellaboration.
                //This will remove the blank nodes.
                if(type.isURIResource()){
                    System.out.println(variable+" of type "+ type);

                	String newchangeforthetype=type.getURI().substring(type.getURI().lastIndexOf("/"),type.getURI().length());
                	String newchangeforthetype2=newchangeforthetype.substring(newchangeforthetype.lastIndexOf("#"),newchangeforthetype.length());
                	String nameEncoded = EncodingUtils.encode(Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+variable.getLocalName());
                	OntClass c = OPMWModel.createClass(NEW_TAXONOMY_CLASS_2+"Data"+newchangeforthetype2);//repeated tuples will not be duplicated
                	c.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+nameEncoded);    
   
                }else{
                    System.out.println("ANON RESOURCE "+type.getURI()+" ignored");
                }
            }else{
                System.out.println(variable);
            }
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+variable.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+newTemplateName,
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
            ModelUtils.addIndividual(OPMWModel,newTemplateName_+res.getLocalName(), Constants.OPMW_PARAMETER_VARIABLE, "Parameter variable "+res.getLocalName());
            //p-plan interop
            OntClass cVar = OPMWModel.createClass(Constants.P_PLAN_Variable);
            cVar.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_PARAMETER_VARIABLE+"/"+EncodingUtils.encode(newTemplateName_+res.getLocalName()));
           
            //add the parameter value as an artifact too
            String aux = EncodingUtils.encode(Constants.CONCEPT_PARAMETER_VARIABLE+"/"+newTemplateName_+res.getLocalName());
            OntClass cAux = OPMWModel.createClass(Constants.OPMW_WORKFLOW_TEMPLATE_ARTIFACT);//repeated tuples will not be duplicated
            cAux.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+aux);
            
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_PARAMETER_VARIABLE+"/"+newTemplateName_+res.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+newTemplateName,                    
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
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNode.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                            Constants.OPMW_PROP_USES);
            //p-plan interop
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNode.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_INPUT);
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNode.getLocalName(),
                            Constants.P_PLAN_PROP_IS_INTPUT_VAR_OF);
            if(role!=null){
                System.out.println("Node "+resNode.getLocalName() +" Uses "+ resVar.getLocalName()+ " Role: "+role);
                //add the roles as subproperty of used. This triple should be on the ontology.
                ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNode.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                            Constants.PREFIX_EXTENSION+"usesAs_"+role);
                //link the property as a subproperty of Used
                ModelUtils.createSubProperty(OPMWModel,Constants.OPMW_PROP_USES, Constants.PREFIX_EXTENSION+"usesAs_"+role);
                //description of the new property
                OntProperty propUsed = OPMWModel.getOntProperty(Constants.PREFIX_EXTENSION+"usesAs_"+role);
                propUsed.addLabel("Property that indicates that a resource has been used as a "+role, "EN");
            }
        }
        String queryInputLinksP = Queries.queryInputLinksP();
        r = queryLocalWINGSTemplateModelRepository(queryInputLinksP);
        while(r.hasNext()){
            QuerySolution qs = r.next();
            Resource resVar = qs.getResource("?var");
            Resource resNode = qs.getResource("?dest");
            String role = qs.getLiteral("?role").getString(); 
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNode.getLocalName(),
                        Constants.CONCEPT_PARAMETER_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                            Constants.OPMW_PROP_USES);
            //p-plan interop
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNode.getLocalName(),
                        Constants.CONCEPT_PARAMETER_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_INPUT);
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_PARAMETER_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNode.getLocalName(),
                            Constants.P_PLAN_PROP_IS_INTPUT_VAR_OF);
            if(role!=null){
                System.out.println("Node "+resNode.getLocalName() +" Uses "+ resVar.getLocalName()+ " Role: "+role);
                //add the roles as subproperty of used. This triple should be on the ontology.
                ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNode.getLocalName(),
                        Constants.CONCEPT_PARAMETER_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                            Constants.PREFIX_EXTENSION+"usesAs_"+role);
                //link the property as a subproperty of Used
                ModelUtils.createSubProperty(OPMWModel,Constants.OPMW_PROP_USES, Constants.PREFIX_EXTENSION+"usesAs_"+role);
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
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNode.getLocalName(),
                        Constants.OPMW_PROP_IGB);
            //p-plan interop
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNode.getLocalName(),
                            Constants.P_PLAN_PROP_IS_OUTPUT_VAR_OF);
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNode.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_OUTPUT);            
            if(role!=null){
                System.out.println("Artifact "+ resVar.getLocalName()+" Is generated by node "+resNode.getLocalName()+" Role "+role);
                //add the roles as subproperty of used. This triple should be on the ontology.
                ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNode.getLocalName(),
                            Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+role);
                //link the property as a subproperty of WGB
                ModelUtils.createSubProperty(OPMWModel,Constants.OPMW_PROP_IGB, Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+role);
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
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNode.getLocalName(),
                        Constants.OPMW_PROP_IGB);
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNodeD.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                            Constants.OPMW_PROP_USES);
            //p-plan interop
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNode.getLocalName(),
                            Constants.P_PLAN_PROP_IS_OUTPUT_VAR_OF);
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNode.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_OUTPUT);
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNodeD.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_INPUT);
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNodeD.getLocalName(),
                            Constants.P_PLAN_PROP_IS_INTPUT_VAR_OF);            
            if(roleOrig!=null){                
                ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNode.getLocalName(),
                            Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+roleOrig);
                //link the property as a subproperty of WGB
                ModelUtils.createSubProperty(OPMWModel,Constants.OPMW_PROP_IGB, Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+roleOrig);
                OntProperty propGenerated = OPMWModel.getOntProperty(Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+roleOrig);
                propGenerated.addLabel("Property that indicates that a resource has been generated as a "+roleOrig, "EN");
            }
            if(roleDest!=null){
                //System.out.println("created role "+ Constants.PREFIX_ONTOLOGY_PROFILE+"used_"+roleDest.getLocalName());
                ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+newTemplateName_+resNodeD.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+newTemplateName_+resVar.getLocalName(),
                            Constants.PREFIX_EXTENSION+"usesAs_"+roleDest);
                //link the property as a subproperty of Used
                ModelUtils.createSubProperty(OPMWModel,Constants.OPMW_PROP_USES, Constants.PREFIX_EXTENSION+"usesAs_"+roleDest);
                OntProperty propUsed = OPMWModel.getOntProperty(Constants.PREFIX_EXTENSION+"usesAs_"+roleDest);
                propUsed.addLabel("Property that indicates that a resource has been used as a "+roleDest, "EN");
            }
        }
        
        
        /******************
         * FILE EXPORT. 
         ******************/        
        ModelUtils.exportRDFFile(rdfOutputFile, OPMWModel, exportMode);
        
        
        //exporting the taxonomy 
        String filetoexport=domainName+"_TaxonomyHierarchyModel.owl";
        System.out.println("exporting new THM to:"+componentCatalogPath+filetoexport );
        ModelUtils.exportRDFFile(componentCatalogPath, taxonomyExport, exportMode);
//    }
        
        
        return Constants.PREFIX_EXPORT_RESOURCE+""+Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+EncodingUtils.encode(newTemplateName);
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
 * @param data_catalog Path to the data catalog export file.
 * @param exportMode mode in which we want to export the produced files (TURTLE, RDF/XML, etc.)
 * @param dataCatalogDirectory path to the WINGS data directory where all data files are present
 * @param domainName name of the domain of the workflow
 * @return The URI for the template. An empty string will be returned if an error occurred (see console for details)
 */
    //public String transformWINGSResultsToOPMW(String resultFile, String libraryFile, String modeFile, String outFilenameOPMW, String outFilenamePROV){
    public String transformWINGSResultsToOPMW(String resultFile, String libraryFile, String modeFile, 
        String outFilenameOPMW, String outFilenamePROV, String suffix,String data_catalog,String exportMode, String dataCatalogDirectory, String domainName){
//    	expandedTemplateModel = initializeModel(expandedTemplateModel);
        templateModel = initializeModel(templateModel);
        WINGSExecutionResults = initializeModel(WINGSExecutionResults);
        OPMWModel = initializeModel(OPMWModel);
        PROVModel = initializeModel(PROVModel);
        dataCatalog = initializeModel(dataCatalog);
        
        try{
            this.loadDataCatalog(data_catalog, exportMode);
        }catch(Exception e){
            System.err.println("Error "+e.getMessage());
            return "";
        }
        
        //load the execution library file
        this.loadFileToLocalRepository(WINGSExecutionResults, libraryFile, modeFile);
        //load the execution file
        this.loadFileToLocalRepository(WINGSExecutionResults, resultFile, modeFile);        
        
        //now, extract the expanded template and the workflow instance. Load them as well
        String queryIntermediateTemplates = Queries.queryIntermediateTemplates();
        //the template is only needed to connect the execution account to itself.
        ResultSet r = queryLocalWINGSResultsRepository(queryIntermediateTemplates);
        String templateName = "", templateURI, expandedTemplateURI,expandedTemplateName="",hashedTemplateName="", 
                hashedExpandedTemplateName, wfInstance, templateToLink;
        
        String expanded ="";// Auxiliary string so we don't have to continually ask which template we are linking to
        
        boolean generateExpandedTemplate;
        if(r.hasNext()){
            QuerySolution qs = r.next();
            Resource template = qs.getResource("?template");
            templateURI = template.getURI();
            templateName = template.getLocalName();
            wfInstance = qs.getResource("?wfInstance").getURI();
            Resource auxExpandedTemplate = qs.getResource("?expandedTemplate");
            expandedTemplateURI = auxExpandedTemplate.getURI();
            expandedTemplateName = auxExpandedTemplate.getLocalName();
            //loading template and taxonomy (extracted from execution file)
            try{
                this.loadedTemplateFileCondition(templateURI, modeFile);
            }catch(Exception e){
                System.out.println("Error while loading template model: "+e.getMessage());
            }
            try{
                loadTaxonomy(templateModel);
            }catch(Exception e){
                System.out.println("Error while loading the taxonomy: "+e.getMessage());
            }
        }else{
            System.err.println("The template, expanded template or workflow instance are not available. ");
            return "";
        }
        String date = ""+new Date().getTime();//necessary to add unique nodeId identifiers
        if(suffix == null){
          suffix = date;
        }
        
        System.out.println("expanded template URI : "+expandedTemplateURI);
        this.loadFileToLocalRepository(WINGSExecutionResults,expandedTemplateURI, modeFile);
        System.out.println("Loaded the expanded template successfully ...");
        this.loadFileToLocalRepository(WINGSExecutionResults,wfInstance, modeFile);
        System.out.println("Loaded the workflow instance successfully ...");
        //add the account of the current execution
        //this.addIndividual(OPMWModel,"Account"+date, Constants.OPMW_WORKFLOW_EXECUTION_ACCOUNT,"Execution account created on "+date);
        ModelUtils.addIndividual(OPMWModel,"Account-"+suffix, Constants.OPMW_WORKFLOW_EXECUTION_ACCOUNT,"Execution account created on "+date);
        //we also assert that it is an account
        //String accname = EncodingUtils.encode(Constants.CONCEPT_WORKFLOW_EXECUTION_ACCOUNT+"/"+"Account"+date);
        String accname = EncodingUtils.encode(Constants.CONCEPT_WORKFLOW_EXECUTION_ACCOUNT+"/"+"Account-"+suffix);
        OntClass cAux = OPMWModel.createClass(Constants.OPM_ACCOUNT);
        cAux.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+accname);
        
        //assessing whether to generate an expanded template (needs the template model to be loaded)
        generateExpandedTemplate = isExportExpandedTemplate();
        System.out.println("Should Expanded template be generated? "+generateExpandedTemplate);
//        this.loadExpandedTemplateFileToLocalRepository(expandedTemplateURI, modeFile);
        hashedTemplateName = HashCreator.getAbstractTemplateHash(templateName, this.templateModel);
        if(generateExpandedTemplate){
            hashedExpandedTemplateName = createExpandedTemplate(accname,expandedTemplateName,expandedTemplateURI,hashedTemplateName,domainName);
            templateToLink = hashedExpandedTemplateName;
            expanded = "Expanded_";
            //all the links have to be done to the expanded template instead of the template
        }else{
            templateToLink = hashedTemplateName;
            //only add the correspondence to template if there is no expanded template
           //relation between the account and the template
           ModelUtils.addProperty(OPMWModel,accname,
                   Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+hashedTemplateName,
                       Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE);
           //p-plan interop
           ModelUtils.addProperty(PROVModel,accname,
                   Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+hashedTemplateName,
                    Constants.PROV_WAS_DERIVED_FROM);
           /*************************
            * PROV-O INTEROPERABILITY
            *************************/
           OntClass d = PROVModel.createClass(Constants.PROV_BUNDLE);
           d.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+accname);
        }
        
 
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
                license = "http://creativecommons.org/licenses/by/3.0/";//default
            }
            //engine = qs.getLiteral("?engine").getString();
            System.out.println("Wings results file:"+executionFile+"\n"
                   // + "User: "+user+", \n"
                    + "Workflow Template: "+hashedTemplateName+"\n"
                    + "status: "+status+"\n"
                    + "startTime: "+startT+"\n"
                    + "endTime: "+endT);
        }
        
        //metadata about the execution: Agent
        if(user!=null){
            ModelUtils.addIndividual(OPMWModel,user, Constants.OPM_AGENT, "Agent "+user);//user HAS to have a URI
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_AGENT+"/"+user,
                accname,
                    Constants.OPM_PROP_ACCOUNT);
            
            /*************************
            * PROV-O INTEROPERABILITY
            *************************/
           String agEncoded = EncodingUtils.encode(Constants.CONCEPT_AGENT+"/"+user);
           OntClass ag = PROVModel.createClass(Constants.PROV_AGENT);
           ag.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+agEncoded);
        }
        
        ModelUtils.addDataProperty(OPMWModel,accname,
                executionFile,Constants.OPMW_DATA_PROP_HAS_ORIGINAL_LOG_FILE,
                        XSDDatatype.XSDanyURI);
        
        /*************************
         * PROV-O INTEROPERABILITY
         *************************/ 
        //hasOriginalLogFile subprop of hadPrimary Source
        ModelUtils.addDataProperty(PROVModel,accname,
                executionFile,Constants.PROV_HAD_PRIMARY_SOURCE,
                        XSDDatatype.XSDanyURI);
        
        //status
        ModelUtils.addDataProperty(OPMWModel,accname,
                status, Constants.OPMW_DATA_PROP_HAS_STATUS);
        //startTime
        ModelUtils.addDataProperty(OPMWModel,accname,
                startT,Constants.OPMW_DATA_PROP_OVERALL_START_TIME,
                    XSDDatatype.XSDdateTime);
        //endTime
        ModelUtils.addDataProperty(OPMWModel,accname,
                endT,Constants.OPMW_DATA_PROP_OVERALL_END_TIME,
                    XSDDatatype.XSDdateTime);
        if(license!=null){
            ModelUtils.addDataProperty(OPMWModel,accname,
                license,Constants.LICENSE,
                    XSDDatatype.XSDanyURI);
        }
        if(tool!=null){
            ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+hashedTemplateName,
                tool,Constants.OPMW_DATA_PROP_CREATED_IN_WORKFLOW_SYSTEM,
                    XSDDatatype.XSDanyURI);
            /*************************
            * PROV-O INTEROPERABILITY
            *************************/ 
            //the template is a prov:Plan
            OntClass plan = PROVModel.createClass(Constants.PROV_PLAN);
            plan.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+hashedTemplateName);
            //createdIn wf system subprop of wasAttributedTo
            ModelUtils.addDataProperty(PROVModel,Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+hashedTemplateName,
                tool,Constants.PROV_WAS_ATTRIBUTED_TO,
                    XSDDatatype.XSDanyURI);
            //the run wasInfluencedBy the template
            ModelUtils.addProperty(PROVModel,accname,
                Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+hashedTemplateName,
                    Constants.PROV_WAS_INFLUENCED_BY);
        }
       // String newexpandedtemplatename=newExpandedTemplateName.substring(0,newExpandedTemplateName.indexOf('-'));
        
        /********************************************************/
        /********************* NODE LINKING**********************/
        /********************************************************/
        //query for detecting steps, their inputs and their outputs

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
            derivedFrom = derivedFrom.replace("_", "");
            System.out.println("Derived from = "+derivedFrom);
            System.out.println("after derived from "+stepName +"\n\t "+ sStartT+"\n\t "+sEndT+"\n\t "+sStatus+"\n\t "+sCode);
            //add each step with its metadata to the model Start and end time are reused from prov.
            ModelUtils.addIndividual(OPMWModel,stepName+date, Constants.OPMW_WORKFLOW_EXECUTION_PROCESS, "Execution process "+stepName);
            //add type opmv:Process as well
            String auxP = EncodingUtils.encode(Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date);
            OntClass cP = OPMWModel.createClass(Constants.OPM_PROCESS);
            cP.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+auxP);
            
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date,
                Constants.CONCEPT_AGENT+"/"+user,
                    Constants.OPM_PROP_WCB);
            
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date,
                accname,
                    Constants.OPM_PROP_ACCOUNT);
            
            /*************************
             * PROV-O INTEROPERABILITY
             *************************/
            OntClass d1 = PROVModel.createClass(Constants.PROV_ACTIVITY);
            d1.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+auxP);
            
            ModelUtils.addProperty(PROVModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date,
                Constants.CONCEPT_AGENT+"/"+user,
                    Constants.PROV_WAS_ASSOCIATED_WITH);

            //metadata
            if(sStartT!=null){
                ModelUtils.addDataProperty(PROVModel, 
                        Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date, 
                        sStartT, 
                        Constants.PROV_STARTED_AT_TIME,
                        XSDDatatype.XSDdateTime);
            }
            if(sEndT!=null){
                ModelUtils.addDataProperty(PROVModel, 
                        Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date, 
                        sEndT, 
                        Constants.PROV_ENDED_AT_TIME,
                        XSDDatatype.XSDdateTime);
            }
            ModelUtils.addDataProperty(OPMWModel, 
                    Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date, 
                    sStatus, 
                    Constants.OPMW_DATA_PROP_HAS_STATUS);
            
            //add the code binding as an executable component  
            String executableComponentName = date;
            try{
                executableComponentName = EncodingUtils.MD5ComponentCode(sCode);
            }catch(Exception e){
                System.out.println("Error while MD5ing the component: "+ stepName);
            }
            Resource blankNode = OPMWModel.createResource(Constants.OPMW_PROP_EXECUTABLE_COMPONENT+"/"+stepName+executableComponentName);
            blankNode.addProperty(OPMWModel.createOntProperty(Constants.OPMW_DATA_PROP_HAS_LOCATION),
                    sCode).
                    addProperty(OPMWModel.createOntProperty(Constants.RDFS_LABEL), 
                            "Executable Component associated to "+stepName);
            String procURI = Constants.PREFIX_EXPORT_RESOURCE+ EncodingUtils.encode(Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+executableComponentName);
            OPMWModel.getResource(procURI).
                    addProperty(OPMWModel.createOntProperty(Constants.OPMW_PROP_HAS_EXECUTABLE_COMPONENT), 
                            blankNode);
            	
          
            //link node  to the process templates
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date,
                    Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+expanded+templateToLink+"_"+stepName,
                        Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE_PROCESS);
           
            //p-plan interop
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+stepName+date,
                    Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+expanded+templateToLink+"_"+stepName,
                        Constants.P_PLAN_PROP_CORRESPONDS_TO_STEP);
        }
        
        
        //DATA VERSIONING CAPTURE
      //annotation of inputs
        //creating a hashmap to store the input vs the new name for it to make it replaceable everywhere
        boolean same=false;
        HashMap<String,String> hmapforInputstoVersions=new HashMap<>();
        
        String getInputs112 = Queries.queryStepInputs();
        r=null;
        r = queryLocalWINGSResultsRepository(getInputs112);
        String step2, input2, inputBinding2;
        while(r.hasNext()){
        	System.out.println("we entered data versioning");
            QuerySolution qs = r.next();
            step2 = qs.getResource("?step").getLocalName();
            input2 = qs.getResource("?input").getLocalName();
            inputBinding2 = qs.getLiteral("?iBinding").getString();
            System.out.println("Step: "+step2+" used input "+input2+" with data binding: "+inputBinding2);
          //need to open the file here:
            
            System.out.println("hashing the input file here ");
            File f=new File(dataCatalogDirectory+inputBinding2.substring(inputBinding2.lastIndexOf("/")+1,inputBinding2.length()));
            System.out.println("opening file"+inputBinding2.substring(inputBinding2.lastIndexOf("/")+1,inputBinding2.length()));
            ArrayList<String> arr=new ArrayList<>();
            StringBuilder sb=new StringBuilder();
            String currentMD5=null;
            //IF THE FILE Does not exist, it might be an intermediate file, of whose versioning we do not take care of
            if(!f.exists()){
            	System.out.println("FILE DOES NOT EXIST!!!");
            	continue;
            }
            try{
	        	InputStream is = new FileInputStream(f);
	        	BufferedReader buf = new BufferedReader(new InputStreamReader(is));
	        	
	        	String line = buf.readLine();
	        	while(line != null){ 
	        		if(!line.equals(""))
		        		arr.add(line+"\n");
	        		line = buf.readLine();
	        		}
	        	for(String x:arr)
	        		sb.append(x);
//	        	System.out.println("file is "+sb.toString());
            System.out.println("hashed file version is "+EncodingUtils.MD5(sb.toString()));
            try{
            currentMD5=EncodingUtils.MD5(sb.toString());
            }
            catch(Exception e)
            {
            	System.out.println("Hashing is not possible for file size and hence we will not version it");
            	continue;
            }
            }catch(Exception e){}
            
            HashMap<String,String> hsprops=new HashMap<>();
            HashSet<String> propertiesfromExecutionFile=new HashSet<>();

            String getVarMetadata = Queries.queryDataVariablesMetadata();
            ResultSet r222=null;
            r222 = queryLocalWINGSResultsRepository(getVarMetadata);
            String var, prop, obj, objName = null;
            while(r222.hasNext()){
                QuerySolution qs222 = r222.next();
                var = qs222.getResource("?variable").getLocalName();
//                System.out.println("variable "+var);
                prop = qs222.getResource("?prop").getURI();
                try{
                    //types
                    Resource rObj = qs222.getResource("?obj");
                    obj = rObj.getURI();
                    objName = rObj.getLocalName();
                }catch(Exception e){
                    //basic metadata
                    obj = qs222.getLiteral("?obj").getString();
                }
                //redundancy: add it as a opm:Artifact as well
 
                //link to template
                if(prop.contains("derivedFrom")){
                    //this relationship ensures that we are doing the linking correctly.
                    //if it doesn't exist we avoid linking to the template.

                }else
                //metadata
                if(prop.contains("rdf-schema#type")||prop.contains("http://www.w3.org/2000/01/rdf-schema#comment")||prop.contains("rdf-syntax-ns#type")){
                    //the objects are resources in this case
                    //String auxP = EncodingUtils.encode(Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date);

                }
                
                else if(prop.contains("hasDataBinding")||prop.contains("isVariableOfPlan")){
                    //do nothing! we have already dealt with data binding before
                    //regarding the p-plan, i don't add it to avoid confusion
                }
              
                else{
                    if(var.equals(input2))
                    {
	                    String temp=prop.substring(prop.lastIndexOf("/")+1,prop.length());
	                    hsprops.put(temp.substring(temp.lastIndexOf("#")+1,temp.length()),obj);
	                    propertiesfromExecutionFile.add(prop.substring(prop.lastIndexOf("/")+1,prop.length()));
                    }
                }
                
            }
            hsprops.put("hasMD5",currentMD5);

            String dataCatalogQuery = Queries.dataCatalogQuery();
            ResultSet rnew123=null;
            rnew123 = queryLocalDataCatalogRepository(dataCatalogQuery);
            
            HashSet<String> similarNamesofInputFiles=new HashSet<>();
            

            System.out.println("printing the query part");
            HashMap<String,ArrayList<String>> hmapnewone=new HashMap<>();
            while(rnew123.hasNext()){
                QuerySolution qsnew = rnew123.next();
                Resource res = qsnew.getResource("?n");
//                System.out.println("n "+res.getLocalName());
//                hmapforInputstoVersions.put(input2, res.getLocalName());
                
                if(res.getLocalName().substring(0,res.getLocalName().indexOf("_")).equals(inputBinding2.substring(inputBinding2.lastIndexOf("/")+1,inputBinding2.length()).toUpperCase()))
                {
                	similarNamesofInputFiles.add(res.getLocalName());
                }
                Resource prop112 = qsnew.getResource("?prop");
                try{
                Literal obj112=qsnew.getLiteral("?obj");
                if(prop112!=null && obj112!=null){
                    System.out.println("prop112 "+prop112.getLocalName()+" obj112 "+obj112.getString());
                    if(!prop112.getLocalName().equals("type") && !prop112.getLocalName().equals("wasRevisionOf"))
                    {
                        if(hmapnewone.containsKey(res.getLocalName()))
                        {
                            ArrayList<String> newtemp=new ArrayList<>();
                            newtemp=hmapnewone.get(res.getLocalName());
                            newtemp.add(obj112.getString());
                            hmapnewone.remove(res.getLocalName());
                            hmapnewone.put(res.getLocalName(), newtemp);
                        }
                        else
                        {
                            ArrayList<String> temp=new ArrayList<>();
                            temp.add(obj112.getString());
                            hmapnewone.put(res.getLocalName(), temp);
                        }
                    }
                }
                }catch(Exception e){
                	Resource obj112=qsnew.getResource("?obj");
                	if(prop112!=null && obj112!=null){
                    	System.out.println("prop112 "+prop112.getLocalName()+" obj112 "+obj112.getLocalName());
                    	if(!prop112.getLocalName().equals("type") && !prop112.getLocalName().equals("wasRevisionOf"))
                    	{
                    		if(hmapnewone.containsKey(res.getLocalName()))
                    		{
                    			ArrayList<String> temp=hmapnewone.get(res.getLocalName());
                    			temp.add(obj112.getLocalName());
                    			hmapnewone.put(res.getLocalName(), temp);
                    		}
                    		else
                    		{
                    			ArrayList<String> temp=new ArrayList<>();
                    			temp.add(obj112.getLocalName());
                    			hmapnewone.put(res.getLocalName(), temp);
                    		}
                    	}
                	}
                }

   
            }
            System.out.println("PRINTING THE HASHMAP QUERY");
            for(String x:hmapnewone.keySet())
            	System.out.println(x+ " "+hmapnewone.get(x));
            
            System.out.println("PRINTING THE HASHSET SIMILAR NAMES");
            for(String x:similarNamesofInputFiles)
            	System.out.println(x);
            
            
            
           if(similarNamesofInputFiles.isEmpty())
           {
        	   
            	hmapforInputstoVersions.put(input2, inputBinding2.substring(inputBinding2.lastIndexOf("/")+1,inputBinding2.length()).toUpperCase()+"_"+currentMD5+"_V1");
            	
            	System.out.println("no match and create a new version v1");
            	
            	String nameOfIndividualEnc = EncodingUtils.encode(inputBinding2.substring(inputBinding2.lastIndexOf("/")+1,inputBinding2.length()).toUpperCase()+"_"+currentMD5+"_V1");
                OntClass c = dataCatalog.createClass(Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT_EXPORT_DIRECT);
                c.createIndividual(Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT_EXPORT_DIRECT+"/"+nameOfIndividualEnc.toUpperCase());
            	
            	
            	
            	OntProperty propSelec22;
                propSelec22 = dataCatalog.createDatatypeProperty(Constants.TAXONOMY_CLASS+"DataCatalog#hasMD5");
                Resource orig22 = dataCatalog.getResource(Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT_EXPORT_DIRECT+"/"+EncodingUtils.encode(inputBinding2.substring(inputBinding2.lastIndexOf("/")+1,inputBinding2.length())+"_"+currentMD5+"_V1"));
                dataCatalog.add(orig22, propSelec22,currentMD5);	
                //RDFS LABEL EXPORTED for canonical instance
                //this.DataProps(Constants.RDFS_LABEL, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5), XSDDatatype.XSDstring);
                for(String x:hsprops.keySet())
                {
                OntProperty propSelec23;
                System.out.println("x "+x);
                propSelec23 = dataCatalog.createDatatypeProperty(Constants.TAXONOMY_CLASS+"DataCatalog#"+x);
                Resource orig23 = dataCatalog.getResource(Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT_EXPORT_DIRECT+"/"+EncodingUtils.encode(inputBinding2.substring(inputBinding2.lastIndexOf("/")+1,inputBinding2.length())+"_"+currentMD5+"_V1"));
                dataCatalog.add(orig23, propSelec23,hsprops.get(x));
                
                
                OntProperty propSelec2233;
                propSelec2233 = dataCatalog.createDatatypeProperty(Constants.RDFS_LABEL);
                Resource orig2233 = dataCatalog.getResource(Constants.TAXONOMY_CLASS+"DataCatalog#"+x);
                dataCatalog.add(orig2233, propSelec2233,x);
                }
   
            }
           else if(!similarNamesofInputFiles.isEmpty())
           {
        	   System.out.println("similar names size!=0");
        	   //now check if all the properties match
        	   //if they match no export
        	   //COMPARE hsprops & hspropsquery

        	   ArrayList<String> givenhspropsarr=new ArrayList<>();
        	   for(String x:hsprops.keySet())
        		   givenhspropsarr.add(hsprops.get(x));
        	   Collections.sort(givenhspropsarr);
        	   for(String x:similarNamesofInputFiles)
        	   {
        		   ArrayList<String> currenttemparr=new ArrayList<>();
        		   currenttemparr=hmapnewone.get(x);
        		   Collections.sort(currenttemparr);
        		   //comment this asap
//        		   currenttemparr.add("hello");
        		   if(currenttemparr.size()==givenhspropsarr.size() && currenttemparr.equals(givenhspropsarr))
        			   same=true;

	           	   if(same){
                                hmapforInputstoVersions.put(input2,x);
                                break;
	           	   }
        	   }
           	   if(!same)
           	   {
           		   System.out.println("no matches so versioning export");
           		   //the props are different somewhere so we find the latest version and link it to it
           		   String finalversionforNewAbstractComponent="";
                  	String finalversionforLatestAbstractComponent="";
                    	int max=Integer.MIN_VALUE;
                        String compareFile = "";
                    	for(String x:similarNamesofInputFiles)
                    	{
                    		String temp=x.substring(x.lastIndexOf("_V"),x.length());
                    		System.out.println("what is temp "+temp);
                    		int temp2=Integer.parseInt(temp.substring(2,temp.length()));
                    		if(max<temp2)
                    		{
                    			max=temp2;
                    			compareFile = x;
                    		}
                    	}
                    	finalversionforLatestAbstractComponent="_V"+max;
                    	max++;
                    	finalversionforNewAbstractComponent="_V"+max;
                    	
                    	System.out.println("finalversionforLatestAbstractComponent "+finalversionforLatestAbstractComponent);
                    	System.out.println("finalversionforNewAbstractComponent "+finalversionforNewAbstractComponent);
                    	
                    	
                    	hmapforInputstoVersions.put(input2, inputBinding2.substring(inputBinding2.lastIndexOf("/")+1,inputBinding2.length()).toUpperCase()+"_"+currentMD5+finalversionforNewAbstractComponent);
                   	
                   	System.out.println("create a new version "+finalversionforNewAbstractComponent);
                   	
                   	String nameOfIndividualEnc = EncodingUtils.encode(inputBinding2.substring(inputBinding2.lastIndexOf("/")+1,inputBinding2.length()).toUpperCase()+"_"+currentMD5+finalversionforNewAbstractComponent);
                       OntClass c = dataCatalog.createClass(Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT_EXPORT_DIRECT);
                       c.createIndividual(Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT_EXPORT_DIRECT+"/"+nameOfIndividualEnc.toUpperCase());
                   	                   	
   	
                       //RDFS LABEL EXPORTED for canonical instance
                       //this.DataProps(Constants.RDFS_LABEL, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5).toUpperCase()+finalversionforNewAbstractComponent, AbstractSuperClass.getLocalName().substring(0,AbstractSuperClass.getLocalName().length()-5), XSDDatatype.XSDstring);
                       for(String x:hsprops.keySet())
                       {
	                       OntProperty propSelec23;
	                       System.out.println("x "+x);
	                       propSelec23 = dataCatalog.createDatatypeProperty(Constants.TAXONOMY_CLASS+"DataCatalog#"+x);
	                       Resource orig23 = dataCatalog.getResource(Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT_EXPORT_DIRECT+"/"+EncodingUtils.encode(inputBinding2.substring(inputBinding2.lastIndexOf("/")+1,inputBinding2.length())+"_"+currentMD5+finalversionforNewAbstractComponent));
	                       dataCatalog.add(orig23, propSelec23,hsprops.get(x));
	                       
	                       OntProperty propSelec2233;
	                       propSelec2233 = dataCatalog.createDatatypeProperty(Constants.RDFS_LABEL);
	                       Resource orig2233 = dataCatalog.getResource(Constants.TAXONOMY_CLASS+"DataCatalog#"+x);
	                       dataCatalog.add(orig2233, propSelec2233,x);
                       }
                       
                     //EXPORTING THE PROV_WAS_REVISION_OF
                       OntProperty propSelec255 = dataCatalog.createOntProperty(Constants.PROV_WAS_REVISION_OF);
                       Resource source255 = dataCatalog.getResource(Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT_EXPORT_DIRECT+"/"+EncodingUtils.encode(inputBinding2.substring(inputBinding2.lastIndexOf("/")+1,inputBinding2.length())+"_"+currentMD5+finalversionforNewAbstractComponent));
                       Individual instance255 = (Individual) source255.as( Individual.class );
                       if((compareFile).contains("http://")){//it is a URI
                           instance255.addProperty(propSelec255,Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT_EXPORT_DIRECT+"/"+compareFile);            
                       }else{//it is a local resource
                           instance255.addProperty(propSelec255, dataCatalog.getResource(Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT_EXPORT_DIRECT+"/"+EncodingUtils.encode(compareFile)));
                       }   
           	   }
           }

        }
        System.out.println("PRINTING THE HASHMAP INPUT VERSIONS");
        for(String x:hmapforInputstoVersions.keySet()){
            System.out.println(x+" "+hmapforInputstoVersions.get(x));
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
            if(hmapforInputstoVersions.containsKey(input))
            {
            	ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+step+date,
                        Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+hmapforInputstoVersions.get(input),
                            Constants.OPM_PROP_USED);
            }
            else
            {
            	ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+step+date,
                        Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+input+date,
                            Constants.OPM_PROP_USED);
            }
            if(hmapforInputstoVersions.containsKey(input))
            {
            	ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+hmapforInputstoVersions.get(input),
                    inputBinding,
                        Constants.OPMW_DATA_PROP_HAS_LOCATION, XSDDatatype.XSDanyURI);
            }
            else
            {
            	ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+input+date,
                        inputBinding,
                            Constants.OPMW_DATA_PROP_HAS_LOCATION, XSDDatatype.XSDanyURI);
            }
            System.out.println("inputbinding "+ inputBinding);
            /*************************
            * PROV-O INTEROPERABILITY
            *************************/ 
            if(hmapforInputstoVersions.containsKey(input))
            {
                ModelUtils.addProperty(PROVModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+step+date,
                    Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+hmapforInputstoVersions.get(input),
                        Constants.PROV_USED);
            }
            else
            {
            	ModelUtils.addProperty(PROVModel,Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+step+date,
                        Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+input+date,
                            Constants.PROV_USED);
            }

            
            
          //hasLocation subrpop of atLocation
            if(hmapforInputstoVersions.containsKey(input))
            {
            	ModelUtils.addDataProperty(PROVModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+hmapforInputstoVersions.get(input),
                    inputBinding,
                        Constants.PROV_AT_LOCATION, XSDDatatype.XSDanyURI);
            }
            else
            {
            	ModelUtils.addDataProperty(PROVModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+input+date,
                        inputBinding,
                            Constants.PROV_AT_LOCATION, XSDDatatype.XSDanyURI);	
            }
            
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
            ModelUtils.addIndividual(OPMWModel, paramName+date,
                    Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT, "Parameter with value: "+paramvalue);
            String auxParam = EncodingUtils.encode(Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date);
            OntClass cParam = OPMWModel.createClass(Constants.OPM_ARTIFACT);
            cParam.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+auxParam);
            ModelUtils.addDataProperty(OPMWModel, 
                    Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date, 
                    paramvalue, 
                    Constants.OPMW_DATA_PROP_HAS_VALUE);
            ModelUtils.addProperty(OPMWModel, 
                    Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+step+date, 
                    Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date, 
                    Constants.OPM_PROP_USED);
            //link to template
            if(res!=null){
                ModelUtils.addProperty(OPMWModel,
                    Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date,
                    Constants.CONCEPT_PARAMETER_VARIABLE+"/"+expanded+templateToLink+"_"+derived,
                    Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE_ARTIFACT);
                
//                //NEW ADDITIONS BY TIRTH
//                if(generateExpandedTemplate){
//                    ModelUtils.addProperty(OPMWModel,
//                        Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date,
//                        Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+templateToLink+"_"+paramName,
//                        Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE_ARTIFACT);
//                }
                

                
                ModelUtils.addProperty(OPMWModel,
                        Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date,
                        Constants.CONCEPT_PARAMETER_VARIABLE+"/"+expanded+templateToLink+"_"+derived,
                        Constants.P_PLAN_PROP_CORRESPONDS_TO_VAR);
            }
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date,
                accname,
                    Constants.OPM_PROP_ACCOUNT);
            /*************************
            * PROV-O INTEROPERABILITY
            *************************/ 
            String auxP = EncodingUtils.encode(Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date);
            OntClass cP = PROVModel.createClass(Constants.PROV_ENTITY);
            cP.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+auxP);
            ModelUtils.addDataProperty(PROVModel, 
                    Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+paramName+date, 
                    paramvalue,
                    Constants.PROV_VALUE);            
            ModelUtils.addProperty(PROVModel, 
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
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+output+date,
                    Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+step+date,
                        Constants.OPM_PROP_WGB);
            ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+output+date,
                    outputBinding,
                        Constants.OPMW_DATA_PROP_HAS_LOCATION, XSDDatatype.XSDanyURI);
            /*************************
            * PROV-O INTEROPERABILITY
            *************************/ 
            ModelUtils.addProperty(PROVModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+output+date,
                    Constants.CONCEPT_WORKFLOW_EXECUTION_PROCESS+"/"+step+date,
                        Constants.PROV_WGB);
            //hasLocation subrpop of atLocation
            ModelUtils.addDataProperty(PROVModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+output+date,
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
            if(hmapforInputstoVersions.containsKey(var))
            {
            ModelUtils.addIndividual(OPMWModel, hmapforInputstoVersions.get(var),
                    Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT, 
                    "Workflow execution artifact: "+var+date);
            }
            else
            {
            	ModelUtils.addIndividual(OPMWModel, var+date,
                        Constants.OPMW_WORKFLOW_EXECUTION_ARTIFACT, 
                        "Workflow execution artifact: "+var+date);
            }
            //redundancy: add it as a opm:Artifact as well
            String auxP=null;
            OntClass cP=null;
            if(hmapforInputstoVersions.containsKey(var))
            {
            auxP = EncodingUtils.encode(Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+hmapforInputstoVersions.get(var));
            cP = OPMWModel.createClass(Constants.OPM_ARTIFACT);
            cP.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+auxP);
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+hmapforInputstoVersions.get(var),
                accname,
                    Constants.OPM_PROP_ACCOUNT);
            }
            else
            {
            	auxP = EncodingUtils.encode(Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date);
                cP = OPMWModel.createClass(Constants.OPM_ARTIFACT);
                cP.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+auxP);
                ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date,
                    accname,
                        Constants.OPM_PROP_ACCOUNT);
            }
            //link to template
            if(prop.contains("derivedFrom")){
                //this relationship ensures that we are doing the linking correctly.
                //if it doesn't exist we avoid linking to the template.
            	 if(hmapforInputstoVersions.containsKey(var))
                 {
            		 ModelUtils.addProperty(OPMWModel,
                        Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+hmapforInputstoVersions.get(var),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+expanded+templateToLink+"_"+objName,
                        Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE_ARTIFACT);
                         //p-plan interop
                         ModelUtils.addProperty(OPMWModel,
                        Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+hmapforInputstoVersions.get(var),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+expanded+templateToLink+"_"+objName,
                        Constants.P_PLAN_PROP_CORRESPONDS_TO_VAR);
                 }
            	 else
            	 {
            		 ModelUtils.addProperty(OPMWModel,
                             Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date,
                             Constants.CONCEPT_DATA_VARIABLE+"/"+expanded+templateToLink+"_"+objName,
                             Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE_ARTIFACT);
                         //p-plan interop
                         ModelUtils.addProperty(OPMWModel,
                            Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date,
                            Constants.CONCEPT_DATA_VARIABLE+"/"+expanded+templateToLink+"_"+objName,
                            Constants.P_PLAN_PROP_CORRESPONDS_TO_VAR);
            	 }
                
  
                
                
              //NEW ADDITIONS BY TIRTH
//                if(generateExpandedTemplate){
//                    if(hmapforInputstoVersions.containsKey(var)){
//                        ModelUtils.addProperty(OPMWModel,
//                            Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+hmapforInputstoVersions.get(var),
//                            Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+var,
//                            Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE_ARTIFACT);
//                     }
//                	 else
//                	 {
//                            ModelUtils.addProperty(OPMWModel,
//                                 Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date,
//                                 Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+var,
//                                 Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE_ARTIFACT);
//                	 }
//                }
          
                
                //p-plan interop
//                if(hmapforInputstoVersions.containsKey(var))
//                {
//                    ModelUtils.addProperty(OPMWModel,
//                        Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+hmapforInputstoVersions.get(var),
//                        Constants.CONCEPT_DATA_VARIABLE+"/"+hashedTemplateName+"_"+objName,
//                        Constants.P_PLAN_PROP_CORRESPONDS_TO_VAR);
//                }
//                else
//                {
//                	ModelUtils.addProperty(OPMWModel,
//                            Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date,
//                            Constants.CONCEPT_DATA_VARIABLE+"/"+hashedTemplateName+"_"+objName,
//                            Constants.P_PLAN_PROP_CORRESPONDS_TO_VAR);
//                }
            }else
            //metadata
            if(prop.contains("http://www.w3.org/2000/01/rdf-schema#type")){
                //the objects are resources in this case
                //String auxP = EncodingUtils.encode(Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date);
//                cP = OPMWModel.createClass(obj);
//                cP.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+auxP);
            }
//            else if(prop.contains("hasSize")){
//            	if(hmapforInputstoVersions.containsKey(var))
//                {
//                this.addDataProperty(OPMWModel,
//                    Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+hmapforInputstoVersions.get(var),
//                    obj,
//                    Constants.TAXONOMY_CLASS+"DataCatalog#");
//                }
//            	else
//            	{
//            		this.addDataProperty(OPMWModel,
//                            Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date,
//                            obj,
//                            Constants.OPMW_DATA_PROP_HAS_SIZE);
//            	}
//            }
            else if(prop.contains("hasDataBinding")||prop.contains("isVariableOfPlan") || prop.contains("rdf-syntax-ns#type")
            		|| prop.contains("rdf-schema#comment")){
                //do nothing! we have already dealt with data binding before
                //regarding the p-plan, i don't add it to avoid confusion
            }
            
            else{
                //custom wings property: preserve it.
            	if(hmapforInputstoVersions.containsKey(var)){
                    prop=prop.substring(prop.lastIndexOf("#")+1,prop.length());

                    OntProperty propSelec23;

                    propSelec23 = OPMWModel.createDatatypeProperty(Constants.TAXONOMY_CLASS+"DataCatalog#"+prop);
                    Resource orig23 = OPMWModel.getResource("http://www.opmw.org/export/resource/WorkflowExecutionArtifact/"+EncodingUtils.encode(hmapforInputstoVersions.get(var)));
                    OPMWModel.add(orig23, propSelec23,obj);
                }
                else{
                    ModelUtils.addDataProperty(OPMWModel,
                        Constants.CONCEPT_WORKFLOW_EXECUTION_ARTIFACT+"/"+var+date,
                        obj,
                        prop);
                }
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
        ModelUtils.exportRDFFile(outFilenameOPMW, OPMWModel, exportMode);
        ModelUtils.exportRDFFile(outFilenamePROV, PROVModel, exportMode);
        //exporting the new DATA CATALOG ALSO NOW
        ModelUtils.exportRDFFile(data_catalog, dataCatalog, exportMode);
        return (Constants.PREFIX_EXPORT_RESOURCE+accname);
    }
    
    public String getRunUrl(String suffix) {
        String accname = EncodingUtils.encode(Constants.CONCEPT_WORKFLOW_EXECUTION_ACCOUNT+"/"+"Account-"+suffix);
        return (Constants.PREFIX_EXPORT_RESOURCE+accname);
    }
    
    public String getTemplateUrl(String templateName) {
        return Constants.PREFIX_EXPORT_RESOURCE+""+Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+
            EncodingUtils.encode(templateName);
    }
    
    public void setPublishExportPrefix(String prefix) {
      Constants.PREFIX_EXPORT_RESOURCE = prefix;
    }

    
    
        private void dataProps(String dataprop,String resourcepart,String propextracted,XSDDatatype x)
        {
        	OntProperty propSelec22;
            propSelec22 = taxonomyExport.createDatatypeProperty(dataprop);
            Resource orig22 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(resourcepart));
            taxonomyExport.add(orig22, propSelec22,propextracted,x);
        }
        
        private void dataProps2(String dataprop,String resourcepart,String propextracted,XSDDatatype x)
        {
        	OntProperty propSelec22;
            propSelec22 = taxonomyExport.createDatatypeProperty(dataprop);
            Resource orig22 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS.replace("#", "")+"/Component#"+EncodingUtils.encode(resourcepart));
            taxonomyExport.add(orig22, propSelec22,propextracted,x);
        }
        
        private void classIsaClass(String classpart,String indvpart)
        {
            OntClass c21 = taxonomyExport.createClass(NEW_TAXONOMY_CLASS.replace("#", "")+"/Component#"+classpart);
            c21.createIndividual(NEW_TAXONOMY_CLASS+indvpart);
        }
        private void classIsaClassHardwareParts(String classpart,String indvpart)
        {
            OntClass c21 = taxonomyExport.createClass(classpart);
            c21.createIndividual(indvpart);
        }
        

        
        private void inputsOutputs(String prop,HashSet<String> hs,String encodepart)
        {
        	OntProperty propSelec23 = taxonomyExport.createOntProperty(prop);
            Resource source23 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(encodepart) );
            Individual instance23 = (Individual) source23.as( Individual.class );
            for(String in:hs)
            {
            if((in).contains("http://")){//it is a URI
                instance23.addProperty(propSelec23,NEW_TAXONOMY_CLASS+in);            
            }else{//it is a local resource
                instance23.addProperty(propSelec23, taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(in)));
            }
            }
        }

    
//    public Gen InputOutputGenerator(ResultSet replica,String classNamegiven)
//    {
//    	HashSet<String> input=new HashSet<>();
//    	HashSet<String> output=new HashSet<>();
//    while(replica.hasNext()){
//        QuerySolution qs = replica.next();
//        Resource res = qs.getResource("?n");
//        Resource comp = qs.getResource("?c");
//        Resource typeComp = qs.getResource("?typeComp");
//        Literal rule = qs.getLiteral("?rule");
//        Literal isConcrete = qs.getLiteral("?isConcrete");
//        
//        Resource inport = qs.getResource("?inport");
//        Resource outport = qs.getResource("?outport");
//        //------------ADDITION BY TIRTH-----------------
//        //obtaining the className
//        String className="";
//        int indexOf=typeComp.toString().indexOf('#');
//        className=typeComp.toString().substring(indexOf+1,typeComp.toString().length());
//        
//        
//        if(className.equals(classNamegiven))
//        {
//        	
//        	  input.add(inport.getLocalName());
//              output.add(outport.getLocalName());
//        }
//      
////    }
//    Gen g=new Gen();
//    g.input=input.size();
//    g.output=output.size();
//    g.className=classNamegiven;
//    return g;
//    
//    }
    
    
  //function to check to export the expanded template or not. This should be an ASK query 
    public boolean isExportExpandedTemplate()
    {
        String queryNodes = Queries.queryNodesforTemplateCondition();
        ResultSet r = queryConditionTemplateModel(queryNodes);
        while(r.hasNext()){
            QuerySolution qs = r.next();
            Literal isConcrete = qs.getLiteral("?isConcrete");
            	if(isConcrete==null)
            		return true;
        }
        return false;
    }
    
    
    public String createExpandedTemplate(String accname,String expandedTemplateName,String expandedTemplateURI,String templateName, String domainName){
        //creating a new EXPORT NAME FOR THE TAXONOMY CLASS
        NEW_TAXONOMY_CLASS=Constants.TAXONOMY_CLASS+domainName+"#";
 	   System.out.println("expanded template name: "+expandedTemplateName);
 	   //UTILIZING THE FUNCTION FOR OBTAINING THE EXPANDED TEMPLATE NAME
 	   String newExpandedTemplateName=HashCreator.getExpandedTemplateHash(expandedTemplateName, this.WINGSExecutionResults);
 	   
 	 //capturing the relationship between the execution account and the expanded template
        ModelUtils.addProperty(OPMWModel, accname, Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName, Constants.OPMW_PROP_CORRESPONDS_TO_TEMPLATE);
        ModelUtils.addProperty(OPMWModel, Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName, Constants.CONCEPT_WORKFLOW_TEMPLATE+"/"+templateName, Constants.OPMW_PROP_IS_IMPLEMENTATION_OF_TEMPLATE);
  //    
        
        //CAPTURING THE EXPANDED TEMPLATE AS A GRAPH, CAPTURING THE VERSION NUMBER, AND DATA PROP FOR NATIVE SYSTEM TEMPLATE
        //ADDED A PROPERTY FOR CONNECTING THE EXECUTION ACCOUNT TO THE EXPANDED TEMPLATE AND THE EXPANDED TEMPLATE TO THE TEMPLATE
        System.out.println("--------------------------");
        System.out.println("--------------------------");
        System.out.println("EXPANDED TEMPLATE BASICS STARTS");
        
        String queryNameWfTemplate = Queries.queryNameWfTemplate();
        String templateName_ = null;
        ResultSet r1 = queryLocalWINGSResultsRepository(queryNameWfTemplate);
        if(r1.hasNext()){//there should be just one local name per template
            QuerySolution qs = r1.next();
            
            Literal v = qs.getLiteral("?ver");
            
            //add the expanded template as a provenance graph
            ModelUtils.addIndividual(OPMWModel,newExpandedTemplateName, Constants.OPMW_WORKFLOW_EXPANDED_TEMPLATE, "Workflow Expanded Template: "+newExpandedTemplateName);
            
            //P-PLAN FOR EXPANDED TEMPLATE
            OntClass cParam = OPMWModel.createClass(Constants.P_PLAN_PLAN);
            cParam.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+EncodingUtils.encode(newExpandedTemplateName));

            if(v!=null){
                ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName,""+ v.getInt(),
                        Constants.OPMW_DATA_PROP_VERSION_NUMBER, XSDDatatype.XSDint);
            }
            //add the uri of the original log file (native system template)
            ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName, 
                    expandedTemplateURI,Constants.OPMW_DATA_PROP_HAS_NATIVE_SYSTEM_TEMPLATE, XSDDatatype.XSDanyURI);
            
        }    
        System.out.println("EXPANDED TEMPLATE BASICS ENDS");
        
        
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
                ModelUtils.addIndividual(OPMWModel,contrib.getString(), Constants.OPM_AGENT,"Agent "+contrib.getString());
                ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName,Constants.CONCEPT_AGENT+"/"+contrib.getString(),
                        Constants.PROP_HAS_CONTRIBUTOR);
        
            }
        }
        //LICENSE EXPORT FOR EXPANDED TEMPLATE
        ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName,"http://creativecommons.org/licenses/by-sa/3.0/",
              Constants.LICENSE, XSDDatatype.XSDanyURI);
        
        System.out.println("METADATA ENDS");
        System.out.println("--------------------------");
        System.out.println("--------------------------");
        System.out.println("NODE LINKING STARTS");
        
        
        //NODE LINKING CODE
        String queryNodesforExpandedTemplate = Queries.queryNodesforExpandedTemplate();
        r1=null;
        //ExpandedTemplateModel.write(System.out,"RDF/XML");
        r1 = queryLocalWINGSResultsRepository(queryNodesforExpandedTemplate);
        //MAINTAINING A COMPLETE AND FINAL DOMAIN NAME TO BE UTILIZED WHEN ITS GOING TO BE EXPORTED WITH THE TAXONOMY MODEL
        while(r1.hasNext()){
            QuerySolution qs = r1.next();
            Resource res = qs.getResource("?n");
            Resource comp = qs.getResource("?c");
            Resource typeComp = qs.getResource("?cb");
            Literal rule = qs.getLiteral("?rule");
            Resource res2=qs.getResource("?derivedFrom");
            
            
          //------------ADDITION BY TIRTH-----------------
            //obtaining the className
            String className="";
            int indexOf=typeComp.toString().indexOf('#');
            className=typeComp.toString().substring(indexOf+1,typeComp.toString().length());
            System.out.println("type class is: "+className);

          //------------ADDITION BY TIRTH-----------------
            //obtaining the className
//            String domainName="";
            int indexOf2=typeComp.toString().indexOf("/components");
            System.out.println("domain name finding");
            System.out.println(typeComp.toString().substring(0,indexOf2));
            String subDomain=typeComp.toString().substring(0,indexOf2);
//            domainName=subDomain.substring(subDomain.lastIndexOf('/')+1,subDomain.length()); 
            System.out.println("domain name  is: "+domainName);

          
            
            System.out.println("node is :"+res.getLocalName()+" derived from "+res2.getLocalName() +"the component is :"+comp);
            System.out.println("cb is :"+typeComp);
            System.out.println("this is inside the node linking new expanded");
            
           // this.addIndividual(OPMWModel,templateName_+res.getLocalName(),Constants.OPMW_WORKFLOW_TEMPLATE_PROCESS, "Workflow template process "+res.getLocalName());
           //CURRENTLY I AM COMMENTING THIS TO AVOID EXTRA STUFF BEING EXPORTED
            ModelUtils.addIndividual(OPMWModel,"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(),Constants.OPMW_WORKFLOW_TEMPLATE_PROCESS, "Workflow expanded template process "+res.getLocalName());
               
            
          //p-plan interop
            OntClass cStep = OPMWModel.createClass(Constants.P_PLAN_STEP);
            cStep.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+EncodingUtils.encode("Expanded_"+newExpandedTemplateName+"_"+res.getLocalName()));
            
            
            if(typeComp.isURIResource()){ //only adds the type if the type is a uRI (not a blank node)
//                String tempURI = EncodingUtils.encode(Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName());
//                OntClass cAux1 = OPMWModel.createClass(typeComp.getURI());//repeated tuples will not be duplicated
//                cAux1.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+tempURI);

            	String newchangeforthetype=typeComp.getURI().substring(typeComp.getURI().lastIndexOf("/"),typeComp.getURI().length());
            	String newchangeforthetype2=newchangeforthetype.substring(newchangeforthetype.lastIndexOf("#"),newchangeforthetype.length());
            	String tempURI = EncodingUtils.encode(Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName());
            	OntClass cAux1 = OPMWModel.createClass(NEW_TAXONOMY_CLASS_2+"Component"+newchangeforthetype2);//repeated tuples will not be duplicated
            	cAux1.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+tempURI);
                
            }else{
                System.out.println("ANON RESOURCE "+typeComp.getURI()+" ignored");
            }
            if(rule!=null){
                //rules are strings
                ModelUtils.addDataProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(),
                    rule.getString(),                    
                        Constants.WINGS_PROP_HAS_RULE);
            }
            
            //is step of template
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName,                    
                        Constants.OPMW_PROP_IS_STEP_OF_TEMPLATE); 
            //is implementation of template process
            ModelUtils.addProperty(OPMWModel, Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(), Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+res2.getLocalName(), Constants.OPMW_PROP_IS_IMPLEMENTATION_OF_TEMPLATE_PROCESS);
            
            //p-plan interop
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(),
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
            ModelUtils.addIndividual(OPMWModel,"Expanded_"+newExpandedTemplateName+"_"+variable.getLocalName(),Constants.OPMW_DATA_VARIABLE, "Data variable "+variable.getLocalName());

            
          //p-plan interop
            OntClass cVar = OPMWModel.createClass(Constants.P_PLAN_Variable);
            cVar.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+Constants.CONCEPT_DATA_VARIABLE+"/"+EncodingUtils.encode("Expanded_"+newExpandedTemplateName+"_"+variable.getLocalName()));

            
            
            
            //we add the individual as a workflowExpandedTemplateArtifact as well            
            String aux = EncodingUtils.encode(Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+variable.getLocalName());
            OntClass cAux1 = OPMWModel.createClass(Constants.OPMW_WORKFLOW_TEMPLATE_ARTIFACT);//repeated tuples will not be duplicated
            cAux1.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+aux);
                   

            //types of data variables
            if(type!=null){
                //sometimes there are some blank nodes asserted as types in the ellaboration.
                //This will remove the blank nodes.
                if(type.isURIResource()){
                    System.out.println(variable+" of type "+ type);
                    //add the individual as an instance of another class, not as a new individual
//                    String nameEncoded = EncodingUtils.encode(Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+variable.getLocalName());
//                    OntClass c = OPMWModel.createClass(type.getURI());
//                    c.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+nameEncoded);
                    
                    
//                    String newchangeforthetype=type.getURI().substring(type.getURI().lastIndexOf("/"),type.getURI().length());
//                    String nameEncoded = EncodingUtils.encode(Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+variable.getLocalName());
//                    OntClass c = OPMWModel.createClass(NEW_TAXONOMY_CLASS_2+"Data"+newchangeforthetype);
//                	c.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+nameEncoded);
                }else{
                    System.out.println("ANON RESOURCE "+type.getURI()+" ignored");
                }
            }else{
                System.out.println(variable);
            }
    
            
            //added is data binding of expanded template data variable dataproperty 
           // this.addDataProperty(OPMWModel, Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+expandedTemplateNewName,""+ databinding, Constants.OPMW_PROP_IS_DATA_BINDING_OF_EXPANDED_TEMPLATE_DATA_VARIABLE,XSDDatatype.XSDanyURI);
                
            
            //is variable of expanded template
            ModelUtils.addProperty(OPMWModel, Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+variable.getLocalName(), Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName, Constants.OPMW_PROP_IS_VARIABLE_OF_TEMPLATE);
            //is implementation of template data variable
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+variable.getLocalName(),Constants.CONCEPT_DATA_VARIABLE+"/"+templateName+"/"+derivedFrom.getLocalName(),Constants.OPMW_PROP_IS_IMPLEMENTATION_OF_TEMPLATE_DATA_VARIABLE);
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
            ModelUtils.addIndividual(OPMWModel,"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(), Constants.OPMW_PARAMETER_VARIABLE, "Parameter variable "+res.getLocalName());
            
            //add the parameter value as an artifact(expanded template) too
            String aux = EncodingUtils.encode(Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName());
            OntClass cAux1 = OPMWModel.createClass(Constants.OPMW_WORKFLOW_TEMPLATE_ARTIFACT);//repeated tuples will not be duplicated
            cAux1.createIndividual(Constants.PREFIX_EXPORT_RESOURCE+aux);
            
            //is parameter of expanded template
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(),
                    Constants.CONCEPT_WORKFLOW_EXPANDED_TEMPLATE+"/"+newExpandedTemplateName,                    
                        Constants.OPMW_PROP_IS_PARAMETER_OF_TEMPLATE);
            
            //is implementation of template parameter variable
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(),Constants.CONCEPT_PARAMETER_VARIABLE+"/"+templateName+"/"+derivedFrom.getLocalName(),Constants.OPMW_PROP_IS_IMPLEMENTATION_OF_TEMPLATE_PARAMETER_VARIABLE);
        
            //par value data-property for expanded template
            ModelUtils.addDataProperty(OPMWModel, Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+res.getLocalName(), parValue+"", Constants.OPMW_PROP_IS_PARVALUE_OF_EXPANDED_TEMPLATE_PARAMETER_VARIABLE);
            
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
    
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
         		   Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.OPMW_PROP_USES);
            
          //p-plan interop for EXPANDED TEMPLATE
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_INPUT);
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                            Constants.P_PLAN_PROP_IS_INTPUT_VAR_OF);
            
            
            if(role!=null){
                System.out.println("Node "+resNode.getLocalName() +" Uses "+ resVar.getLocalName()+ " Role: "+role);
                //add the roles as subproperty of used. This triple should be on the ontology.
                ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
             		   Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.PREFIX_EXTENSION+"usesAs_"+role);
                //link the property as a subproperty of Used
                ModelUtils.createSubProperty(OPMWModel,Constants.OPMW_PROP_USES, Constants.PREFIX_EXTENSION+"usesAs_"+role);
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
            
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
         		   Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                        Constants.OPMW_PROP_USES);
           
           
          //p-plan interop
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                        Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_INPUT);
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                            Constants.P_PLAN_PROP_IS_INTPUT_VAR_OF);
            
            
            if(role!=null){
                System.out.println("Node "+resNode.getLocalName() +" Uses "+ resVar.getLocalName()+ " Role: "+role);
                //add the roles as subproperty of used. This triple should be on the ontology.
                ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
             		   Constants.CONCEPT_PARAMETER_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.PREFIX_EXTENSION+"usesAs_"+role);
                
               
                //link the property as a subproperty of Used
                ModelUtils.createSubProperty(OPMWModel,Constants.OPMW_PROP_USES, Constants.PREFIX_EXTENSION+"usesAs_"+role);
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
            
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
         		   Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                    Constants.OPMW_PROP_IGB);
            
            
          //p-plan interop
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                            Constants.P_PLAN_PROP_IS_OUTPUT_VAR_OF);
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_OUTPUT);   
                   
            if(role!=null){
                System.out.println("Artifact "+ resVar.getLocalName()+" Is generated by node "+resNode.getLocalName()+" Role "+role);
                //add the roles as subproperty of used. This triple should be on the ontology.
                ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
             		   Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                            Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+role);
                //link the property as a subproperty of WGB
                ModelUtils.createSubProperty(OPMWModel,Constants.OPMW_PROP_IGB, Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+role);
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
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
         		   Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                        Constants.OPMW_PROP_IGB);
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNodeD.getLocalName(),
         		   Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.OPMW_PROP_USES);
                       
            
          //p-plan interop
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                            Constants.P_PLAN_PROP_IS_OUTPUT_VAR_OF);
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_OUTPUT);
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNodeD.getLocalName(),
                        Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.P_PLAN_PROP_HAS_INPUT);
            ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                        Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNodeD.getLocalName(),
                            Constants.P_PLAN_PROP_IS_INTPUT_VAR_OF);     
            
            
            
            if(roleOrig!=null){                
                ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
             		   Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNode.getLocalName(),
                            Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+roleOrig);
                //link the property as a subproperty of WGB
                ModelUtils.createSubProperty(OPMWModel,Constants.OPMW_PROP_IGB, Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+roleOrig);
                OntProperty propGenerated = OPMWModel.getOntProperty(Constants.PREFIX_EXTENSION+"isGeneratedByAs_"+roleOrig);
                propGenerated.addLabel("Property that indicates that a resource has been generated as a "+roleOrig, "EN");
            }
            if(roleDest!=null){
                //System.out.println("created role "+ Constants.PREFIX_ONTOLOGY_PROFILE+"used_"+roleDest.getLocalName());
                ModelUtils.addProperty(OPMWModel,Constants.CONCEPT_WORKFLOW_TEMPLATE_PROCESS+"/"+"Expanded_"+newExpandedTemplateName+"_"+resNodeD.getLocalName(),
             		   Constants.CONCEPT_DATA_VARIABLE+"/"+"Expanded_"+newExpandedTemplateName+"_"+resVar.getLocalName(),
                            Constants.PREFIX_EXTENSION+"usesAs_"+roleDest);
                //link the property as a subproperty of Used
                ModelUtils.createSubProperty(OPMWModel,Constants.OPMW_PROP_USES, Constants.PREFIX_EXTENSION+"usesAs_"+roleDest);
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
 
    private void exportHardwareSoftwareDependencies(boolean needsval, Float memval, Float stval, Float minversionval, Resource concrComponent){
        Resource blankNode112 = taxonomyExport.createResource(Constants.PREFIX_RESOURCE+"HardwareRequirements_"+concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"_V1");
        blankNode112.addProperty(taxonomyExport.createOntProperty(Constants.NEEDS_64BIT),
            needsval+"",XSDDatatype.XSDboolean);
        if(memval!=null){
            blankNode112.addProperty(taxonomyExport.createOntProperty(Constants.REQUIRES_MEMORYGB), 
                memval+"",XSDDatatype.XSDfloat);
        }
        if(stval!=null){
            blankNode112.addProperty(taxonomyExport.createOntProperty(Constants.REQUIRES_STORAGEGB), 
                stval+"",XSDDatatype.XSDfloat);
        }
        String procURI = NEW_TAXONOMY_CLASS+EncodingUtils.encode(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"_V1");
        taxonomyExport.getResource(procURI).
                addProperty(taxonomyExport.createOntProperty(Constants.HAS_HARDWARE_DEPENDENCY), 
                        blankNode112);
        this.classIsaClassHardwareParts(Constants.HARDWARE_DEPENDENCY,Constants.PREFIX_RESOURCE+"HardwareRequirements_"+concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"_V1");
        Resource blankNode113 = taxonomyExport.createResource(Constants.PREFIX_RESOURCE+"SoftwareRequirements_"+concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"_V1");
        if(minversionval!=null){
            blankNode113.addProperty(taxonomyExport.createOntProperty(Constants.REQUIRES_VERSION),
                minversionval+"");
        }
        String procURI113 = NEW_TAXONOMY_CLASS+EncodingUtils.encode(concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"_V1");
        taxonomyExport.getResource(procURI113).
                addProperty(taxonomyExport.createOntProperty(Constants.HAS_SOFTWARE_DEPENDENCY), 
                        blankNode113);
        this.classIsaClassHardwareParts(Constants.SOFTWARE_DEPENDENCY,Constants.PREFIX_RESOURCE+"SoftwareRequirements_"+concrComponent.getLocalName().substring(0,concrComponent.getLocalName().length()-5)+"_V1");
    }
    
    private OntModel initializeModel (OntModel m){
        if(m!=null){
            m.removeAll();
         }else{
            m = ModelFactory.createOntologyModel();
         }
        return m;
    }

    /**
     * Getter of the model for validation purposes
     * @return 
     */
    public OntModel getOPMWModel() {
        return OPMWModel;
    }
    
    
 }
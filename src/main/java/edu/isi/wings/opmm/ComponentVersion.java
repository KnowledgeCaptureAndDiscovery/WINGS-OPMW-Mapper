/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.isi.wings.opmm;

import static edu.isi.wings.opmm.Mapper.NEW_TAXONOMY_CLASS;
import static edu.isi.wings.opmm.Mapper.PREFIX_COMP_CATALOG;
import java.util.HashSet;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import utils.EncodingUtils;
import utils.ModelUtils;

/**
 *
 * @author dgarijo
 */
public class ComponentVersion {
    
    public static void Step1(HashSet<String> a, OntModel componentCatalog, OntModel taxonomyExport)
        {
        for(String inx:a)
        {
              System.out.println(inx);
              String componentCatalogQueryforInteriorsofParametersComponents = Queries.componentCatalogQueryforInteriorsofParametersComponents(PREFIX_COMP_CATALOG,inx);
              ResultSet rnew2 = null;
              rnew2 = ModelUtils.queryLocalRepository(componentCatalogQueryforInteriorsofParametersComponents, componentCatalog);
              boolean exists=false;
              String argIDString="",argNameString="",valString="";
              int dimInt=0;
              
              while(rnew2.hasNext())
              {
              	QuerySolution qsnew = rnew2.next();
                  Literal argID=qsnew.getLiteral("?argID");
                  Literal argName=qsnew.getLiteral("?argName");
                  Literal dim=qsnew.getLiteral("?dim");
                  Literal val=qsnew.getLiteral("?val");
                  
                  if(argID!=null)
                  {
                  	System.out.println(argID.getString());
                  	argIDString=argID.getString();
                  }
                  if(argName!=null)
                  {
                  	System.out.println(argName.getString());
                  	argNameString=argName.getString();
                  }
                  if(dim!=null)
                  {
                  	System.out.println(dim.getInt());
                  	dimInt=dim.getInt();
                  }
                  if(val!=null)
                  {
                  	System.out.println(val.getString());
                  	valString=val.getString();
                  }
                  if(argID!=null && argName!=null && dim!=null && val!=null)
                  	exists=true;
                  
              }
              if(exists==true)
              {
              	System.out.println("NOW EXPORT THE PARAMETER INTERIORS FOR COMPONENTS ONLY");
              	//EXPORTING THE FACT THAT CLASSNAME-CLASS IS A CLASSNAME
                  OntClass c31 = taxonomyExport.createClass(NEW_TAXONOMY_CLASS+"ParameterArgument");
                  c31.createIndividual(NEW_TAXONOMY_CLASS+inx.toUpperCase());
                  
                //HAS ARGUMENT ID EXPORTED
                  OntProperty propSelec31;
                  propSelec31 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_ARGUMENT_ID);
                  Resource orig31 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(inx));
                  taxonomyExport.add(orig31, propSelec31, argIDString);
                  
                //HAS ARGUMENT NAME EXPORTED
                  OntProperty propSelec32;
                  propSelec32 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_ARGUMENT_NAME);
                  Resource orig32 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(inx));
                  taxonomyExport.add(orig32, propSelec32, argNameString);
                  
                //HAS DIMENSIONALITY EXPORTED
                  OntProperty propSelec33;
                  propSelec33 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_DIMENSIONALITY);
                  Resource orig33 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(inx));
                  taxonomyExport.add(orig33, propSelec33, dimInt+"",XSDDatatype.XSDint);
                  
                //HAS VALUE EXPORTED
                  OntProperty propSelec34;
                  propSelec34 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_VALUE);
                  Resource orig34 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(inx));
                  taxonomyExport.add(orig34, propSelec34, valString);
              }
        
        
        }
}
        
        
       //STEP2: EXTRACT ONLY THE (INPUTS) DATA VARIABLES IF THEY EXIST FOR COMPONENTS

        public static void Step2(HashSet<String> a, OntModel componentCatalog, OntModel taxonomyExport)
        {
        for(String inx:a)
        {
              System.out.println(inx);
              String componentCatalogQueryforInteriorsofDataComponents = Queries.componentCatalogQueryforInteriorsofDataComponents(PREFIX_COMP_CATALOG,inx);
              ResultSet rnew2 = null;
              rnew2 = ModelUtils.queryLocalRepository(componentCatalogQueryforInteriorsofDataComponents, componentCatalog);
              boolean exists=false;
              String argIDString="",argNameString="";
              int dimInt=0;
              
              while(rnew2.hasNext())
              {
              	QuerySolution qsnew = rnew2.next();
                  Literal argID=qsnew.getLiteral("?argID");
                  Literal argName=qsnew.getLiteral("?argName");
                  Literal dim=qsnew.getLiteral("?dim");
                  
                  if(argID!=null)
                  {
                  	System.out.println(argID.getString());
                  	argIDString=argID.getString();
                  }
                  if(argName!=null)
                  {
                  	System.out.println(argName.getString());
                  	argNameString=argName.getString();
                  }
                  if(dim!=null)
                  {
                  	System.out.println(dim.getInt());
                  	dimInt=dim.getInt();
                  }

                  if(argID!=null && argName!=null && dim!=null)
                  	exists=true;
                  
              }
              if(exists==true)
              {
              	System.out.println("NOW EXPORT THE INPUT DATA INTERIORS FOR COMPONENT ONLY");
              	//EXPORTING THE FACT THAT CLASSNAME-CLASS IS A CLASSNAME
                  OntClass c31 = taxonomyExport.createClass(NEW_TAXONOMY_CLASS+"DataArgument");
                  c31.createIndividual(NEW_TAXONOMY_CLASS+inx.toUpperCase());
                  
                  OntClass c331 = taxonomyExport.createClass(NEW_TAXONOMY_CLASS+"TextFile");
                  c331.createIndividual(NEW_TAXONOMY_CLASS+inx.toUpperCase());
                  
                  
                //HAS ARGUMENT ID EXPORTED
                  OntProperty propSelec31;
                  propSelec31 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_ARGUMENT_ID);
                  Resource orig31 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(inx));
                  taxonomyExport.add(orig31, propSelec31, argIDString);
                  
                //HAS ARGUMENT NAME EXPORTED
                  OntProperty propSelec32;
                  propSelec32 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_ARGUMENT_NAME);
                  Resource orig32 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(inx));
                  taxonomyExport.add(orig32, propSelec32, argNameString);
                  
                //HAS DIMENSIONALITY EXPORTED
                  OntProperty propSelec33;
                  propSelec33 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_DIMENSIONALITY);
                  Resource orig33 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(inx));
                  taxonomyExport.add(orig33, propSelec33, dimInt+"",XSDDatatype.XSDint);
                  
              }
        
        
        }
}
        
      //STEP3: EXTRACT ONLY THE (OUTPUTS) DATA VARIABLES IF THEY EXIST FOR COMPONENTS
        public static void Step3(HashSet<String> a, OntModel componentCatalog, OntModel taxonomyExport)
        {
        System.out.println("OUTPUT DATA EXTRACTION PRINTING COMPONENTS");
        for(String outx:a)
        {
              System.out.println(outx);
              String componentCatalogQueryforInteriorsofDataComponentsOutput = Queries.componentCatalogQueryforInteriorsofDataComponents(PREFIX_COMP_CATALOG,outx);
              ResultSet rnew2 = null;
              rnew2 = ModelUtils.queryLocalRepository(componentCatalogQueryforInteriorsofDataComponentsOutput, componentCatalog);
              
              boolean exists=false;
              String argIDString="",argNameString="";
              int dimInt=0;
              
              while(rnew2.hasNext())
              {
              	QuerySolution qsnew = rnew2.next();
                  Literal argID=qsnew.getLiteral("?argID");
                  Literal argName=qsnew.getLiteral("?argName");
                  Literal dim=qsnew.getLiteral("?dim");
                  
                  if(argID!=null)
                  {
                  	System.out.println(argID.getString());
                  	argIDString=argID.getString();
                  }
                  if(argName!=null)
                  {
                  	System.out.println(argName.getString());
                  	argNameString=argName.getString();
                  }
                  if(dim!=null)
                  {
                  	System.out.println(dim.getInt());
                  	dimInt=dim.getInt();
                  }

                  if(argID!=null && argName!=null && dim!=null)
                  	exists=true;
                  
              }
              if(exists==true)
              {
              	System.out.println("NOW EXPORT THE OUTPUT DATA INTERIORS FOR COMPONENTS ONLY");
              	//EXPORTING THE FACT THAT CLASSNAME-CLASS IS A CLASSNAME
                  OntClass c31 = taxonomyExport.createClass(NEW_TAXONOMY_CLASS+"DataArgument");
                  c31.createIndividual(NEW_TAXONOMY_CLASS+outx.toUpperCase());
                  
                  OntClass c331 = taxonomyExport.createClass(NEW_TAXONOMY_CLASS+"TextFile");
                  c331.createIndividual(NEW_TAXONOMY_CLASS+outx.toUpperCase());
                  
                  
                //HAS ARGUMENT ID EXPORTED
                  OntProperty propSelec31;
                  propSelec31 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_ARGUMENT_ID);
                  Resource orig31 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(outx));
                  taxonomyExport.add(orig31, propSelec31, argIDString);
                  
                //HAS ARGUMENT NAME EXPORTED
                  OntProperty propSelec32;
                  propSelec32 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_ARGUMENT_NAME);
                  Resource orig32 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(outx));
                  taxonomyExport.add(orig32, propSelec32, argNameString);
                  
                //HAS DIMENSIONALITY EXPORTED
                  OntProperty propSelec33;
                  propSelec33 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_DIMENSIONALITY);
                  Resource orig33 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(outx));
                  taxonomyExport.add(orig33, propSelec33, dimInt+"",XSDDatatype.XSDint);
                  
              }
        
        
        }
    }
        
        
        public static void Step4(HashSet<String> a, OntModel componentCatalog, OntModel taxonomyExport)
        {
        	for(String inx:a)
            {
                System.out.println(inx);
                String componentCatalogQueryforInteriorsofParametersAbstractComponents = Queries.componentCatalogQueryforInteriorsofParametersComponents(PREFIX_COMP_CATALOG,inx);
                ResultSet rnew2 = null;
                rnew2 = ModelUtils.queryLocalRepository(componentCatalogQueryforInteriorsofParametersAbstractComponents, componentCatalog);
                
                boolean exists=false;
                String argIDString="",argNameString="",valString="";
                int dimInt=0;
                
                while(rnew2.hasNext())
                {
                	QuerySolution qsnew = rnew2.next();
                    Literal argID=qsnew.getLiteral("?argID");
                    Literal argName=qsnew.getLiteral("?argName");
                    Literal dim=qsnew.getLiteral("?dim");
                    Literal val=qsnew.getLiteral("?val");
                    
                    if(argID!=null)
                    {
                    	System.out.println(argID.getString());
                    	argIDString=argID.getString();
                    }
                    if(argName!=null)
                    {
                    	System.out.println(argName.getString());
                    	argNameString=argName.getString();
                    }
                    if(dim!=null)
                    {
                    	System.out.println(dim.getInt());
                    	dimInt=dim.getInt();
                    }
                    if(val!=null)
                    {
                    	System.out.println(val.getString());
                    	valString=val.getString();
                    }
                    if(argID!=null && argName!=null && dim!=null && val!=null)
                    	exists=true;
                    
                }
                if(exists==true)
                {
                	System.out.println("NOW EXPORT THE PARAMETER INTERIORS FOR ABSTRACT COMPONENTS ONLY");
                	//EXPORTING THE FACT THAT CLASSNAME-CLASS IS A CLASSNAME
                    OntClass c31 = taxonomyExport.createClass(NEW_TAXONOMY_CLASS+"ParameterArgument");
                    c31.createIndividual(NEW_TAXONOMY_CLASS+inx.toUpperCase());
                    
                  //HAS ARGUMENT ID EXPORTED
                    OntProperty propSelec31;
                    propSelec31 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_ARGUMENT_ID);
                    Resource orig31 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(inx));
                    taxonomyExport.add(orig31, propSelec31, argIDString);
                    
                  //HAS ARGUMENT NAME EXPORTED
                    OntProperty propSelec32;
                    propSelec32 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_ARGUMENT_NAME);
                    Resource orig32 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(inx));
                    taxonomyExport.add(orig32, propSelec32, argNameString);
                    
                  //HAS DIMENSIONALITY EXPORTED
                    OntProperty propSelec33;
                    propSelec33 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_DIMENSIONALITY);
                    Resource orig33 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(inx));
                    taxonomyExport.add(orig33, propSelec33, dimInt+"",XSDDatatype.XSDint);
                    
                  //HAS VALUE EXPORTED
                    OntProperty propSelec34;
                    propSelec34 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_VALUE);
                    Resource orig34 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(inx));
                    taxonomyExport.add(orig34, propSelec34, valString);
                }
            
            
            }
        }
        public static void Step5(HashSet<String> a, OntModel componentCatalog, OntModel taxonomyExport)
        {
        for(String inx:a)
        {
            System.out.println(inx);
            String componentCatalogQueryforInteriorsofDataAbstractComponents = Queries.componentCatalogQueryforInteriorsofDataComponents(PREFIX_COMP_CATALOG,inx);
            ResultSet rnew2 = null;
            rnew2 = ModelUtils.queryLocalRepository(componentCatalogQueryforInteriorsofDataAbstractComponents, componentCatalog);
            
            boolean exists=false;
            String argIDString="",argNameString="";
            int dimInt=0;
            
            while(rnew2.hasNext())
            {
            	QuerySolution qsnew = rnew2.next();
                Literal argID=qsnew.getLiteral("?argID");
                Literal argName=qsnew.getLiteral("?argName");
                Literal dim=qsnew.getLiteral("?dim");
                
                if(argID!=null)
                {
                	System.out.println(argID.getString());
                	argIDString=argID.getString();
                }
                if(argName!=null)
                {
                	System.out.println(argName.getString());
                	argNameString=argName.getString();
                }
                if(dim!=null)
                {
                	System.out.println(dim.getInt());
                	dimInt=dim.getInt();
                }

                if(argID!=null && argName!=null && dim!=null)
                	exists=true;
                
            }
            if(exists==true)
            {
            	System.out.println("NOW EXPORT THE INPUT DATA INTERIORS FOR ABSTRACT COMPONENTS ONLY");
            	//EXPORTING THE FACT THAT CLASSNAME-CLASS IS A CLASSNAME
            	String datatype=NEW_TAXONOMY_CLASS.substring(0, NEW_TAXONOMY_CLASS.length()-1)+"/Data#";
                OntClass c31 = taxonomyExport.createClass(datatype+"DataArgument");
                c31.createIndividual(NEW_TAXONOMY_CLASS+inx.toUpperCase());
                
                OntClass c331 = taxonomyExport.createClass(datatype+"TextFile");
                c331.createIndividual(NEW_TAXONOMY_CLASS+inx.toUpperCase());
                
                
              //HAS ARGUMENT ID EXPORTED
                OntProperty propSelec31;
                propSelec31 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_ARGUMENT_ID);
                Resource orig31 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(inx));
                taxonomyExport.add(orig31, propSelec31, argIDString);
                
              //HAS ARGUMENT NAME EXPORTED
                OntProperty propSelec32;
                propSelec32 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_ARGUMENT_NAME);
                Resource orig32 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(inx));
                taxonomyExport.add(orig32, propSelec32, argNameString);
                
              //HAS DIMENSIONALITY EXPORTED
                OntProperty propSelec33;
                propSelec33 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_DIMENSIONALITY);
                Resource orig33 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(inx));
                taxonomyExport.add(orig33, propSelec33, dimInt+"",XSDDatatype.XSDint);
                
            }
        
        
        }
     }
        
        public static void Step6(HashSet<String> a, OntModel componentCatalog, OntModel taxonomyExport)
        {
        	for(String outx:a)
            {
                System.out.println(outx);
                String componentCatalogQueryforInteriorsofDataAbstractComponentsOutput = Queries.componentCatalogQueryforInteriorsofDataComponents(PREFIX_COMP_CATALOG,outx);
                ResultSet rnew2 = null;
                rnew2 = ModelUtils.queryLocalRepository(componentCatalogQueryforInteriorsofDataAbstractComponentsOutput, componentCatalog);
                
                boolean exists=false;
                String argIDString="",argNameString="";
                int dimInt=0;
                
                while(rnew2.hasNext())
                {
                	QuerySolution qsnew = rnew2.next();
                    Literal argID=qsnew.getLiteral("?argID");
                    Literal argName=qsnew.getLiteral("?argName");
                    Literal dim=qsnew.getLiteral("?dim");
                    
                    if(argID!=null)
                    {
                    	System.out.println(argID.getString());
                    	argIDString=argID.getString();
                    }
                    if(argName!=null)
                    {
                    	System.out.println(argName.getString());
                    	argNameString=argName.getString();
                    }
                    if(dim!=null)
                    {
                    	System.out.println(dim.getInt());
                    	dimInt=dim.getInt();
                    }

                    if(argID!=null && argName!=null && dim!=null)
                    	exists=true;
                    
                }
                if(exists==true)
                {
                	System.out.println("NOW EXPORT THE INPUT DATA INTERIORS FOR ABSTRACT COMPONENTS ONLY");
                	//EXPORTING THE FACT THAT CLASSNAME-CLASS IS A CLASSNAME
                	String datatype=NEW_TAXONOMY_CLASS.substring(0, NEW_TAXONOMY_CLASS.length()-1)+"/Data/";
                    OntClass c31 = taxonomyExport.createClass(datatype+"DataArgument");
                    c31.createIndividual(NEW_TAXONOMY_CLASS+outx.toUpperCase());
                    
                    OntClass c331 = taxonomyExport.createClass(datatype+"TextFile");
                    c331.createIndividual(NEW_TAXONOMY_CLASS+outx.toUpperCase());
                    
                    
                  //HAS ARGUMENT ID EXPORTED
                    OntProperty propSelec31;
                    propSelec31 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_ARGUMENT_ID);
                    Resource orig31 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(outx));
                    taxonomyExport.add(orig31, propSelec31, argIDString);
                    
                  //HAS ARGUMENT NAME EXPORTED
                    OntProperty propSelec32;
                    propSelec32 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_ARGUMENT_NAME);
                    Resource orig32 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(outx));
                    taxonomyExport.add(orig32, propSelec32, argNameString);
                    
                  //HAS DIMENSIONALITY EXPORTED
                    OntProperty propSelec33;
                    propSelec33 = taxonomyExport.createDatatypeProperty(Constants.COMPONENT_HAS_DIMENSIONALITY);
                    Resource orig33 = taxonomyExport.getResource(NEW_TAXONOMY_CLASS+EncodingUtils.encode(outx));
                    taxonomyExport.add(orig33, propSelec33, dimInt+"",XSDDatatype.XSDint);
                    
                }
            
            
            }
     }   
    
}

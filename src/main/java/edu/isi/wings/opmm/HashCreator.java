/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.isi.wings.opmm;

import java.util.ArrayList;
import java.util.Collections;
import org.apache.jena.ontology.OntModel;
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
public class HashCreator {
    
    //THE FUNCTION FOR OBTAINING THE ABSTRACT TEMPLATE NAME
    public static String getAbstractTemplateHash(String TemplateName, OntModel wingsTemplate)
    {

          ArrayList<String> arrFinalAbstractTemplateNameRelations=new ArrayList<>();
          String queryNodes=Queries.queryNodes();
          ResultSet r = null;
          r = ModelUtils.queryLocalRepository(queryNodes, wingsTemplate);
          //COMPONENT PARTS
          while(r.hasNext()){
              QuerySolution qs = r.next();
              Resource res = qs.getResource("?n");
              Resource comp = qs.getResource("?c");
              Resource typeComp = qs.getResource("?typeComp");
              if(res!=null && comp!=null)
              {
            	  arrFinalAbstractTemplateNameRelations.add(res.getLocalName()+" has Comp "+comp.getLocalName());
              }
              
          }
          
          //DATA PARTS
          String queryDataV = Queries.queryDataV2();
          r = ModelUtils.queryLocalRepository(queryDataV, wingsTemplate);
          while(r.hasNext()){
              QuerySolution qs = r.next();
              Resource variable = qs.getResource("?d");
              Resource type = qs.getResource("?t");
              Literal dim = qs.getLiteral("?hasDim"); 
              if(variable!=null && type!=null)
              {
            	  arrFinalAbstractTemplateNameRelations.add("Variable "+ variable.getLocalName()+" has type "+type.getLocalName());
              }
              
          }
          
          //PARAMETER PARTS
          String queryParameterV = Queries.querySelectParameter();
          r = null;
          r = ModelUtils.queryLocalRepository(queryParameterV, wingsTemplate);
          while(r.hasNext()){
              QuerySolution qs = r.next();
              Resource res = qs.getResource("?p");
              arrFinalAbstractTemplateNameRelations.add("Parameter is "+res.getLocalName());  
          }
          
          //INPUT LINKS
          String queryInputLinks = Queries.queryInputLinks();
          r = null;
          r = ModelUtils.queryLocalRepository(queryInputLinks, wingsTemplate);
          while(r.hasNext()){
              QuerySolution qs = r.next();
              Resource resVar = qs.getResource("?var");
              Resource resNode = qs.getResource("?dest");
              arrFinalAbstractTemplateNameRelations.add("InputLink "+resVar.getLocalName()+" Node is "+resNode.getLocalName()); 
          }
          
          //INPUT-P LINKS
          String queryInputLinksP = Queries.queryInputLinksP();
          r = null;
          r = ModelUtils.queryLocalRepository(queryInputLinksP, wingsTemplate);
          while(r.hasNext()){
              QuerySolution qs = r.next();
              Resource resVar = qs.getResource("?var");
              Resource resNode = qs.getResource("?dest");
              arrFinalAbstractTemplateNameRelations.add("Input-plink "+resVar.getLocalName()+" Node is "+resNode.getLocalName());   
          }
          
          //OUTPUT LINKS
          String queryOutputLinks = Queries.queryOutputLinks();
          r = null;
          r = ModelUtils.queryLocalRepository(queryOutputLinks, wingsTemplate);
          while(r.hasNext()){
              QuerySolution qs = r.next();
              Resource resVar = qs.getResource("?var");
              Resource resNode = qs.getResource("?orig");
              arrFinalAbstractTemplateNameRelations.add("Outputlink "+resVar.getLocalName()+" Node is "+resNode.getLocalName());
          }
          //INOUT LINKS
          String queryInOutLinks = Queries.queryInOutLinks();
          r = null;
          r = ModelUtils.queryLocalRepository(queryInOutLinks, wingsTemplate);
          while(r.hasNext()){
              QuerySolution qs = r.next();
              Resource resVar = qs.getResource("?var");
              Resource resNode = qs.getResource("?orig");
              Resource resNodeD = qs.getResource("?dest");
              arrFinalAbstractTemplateNameRelations.add("Variable "+resVar.getLocalName()+" has origin Node "+resNode.getLocalName()+" has Destination Node "+resNodeD.getLocalName());
          }

     	  String newAbstractTemplateName="";
     	  Collections.sort(arrFinalAbstractTemplateNameRelations);
          for(String x:arrFinalAbstractTemplateNameRelations)
          {
        	  newAbstractTemplateName+=x+"\n";
          }
          try{
              System.out.println("NAME UNENCODED: "+newAbstractTemplateName);
              newAbstractTemplateName=TemplateName+"_"+EncodingUtils.MD5(newAbstractTemplateName);
          }catch(Exception e){
          //should be captured.
          }
          System.out.println("final abstracttemplatename is "+newAbstractTemplateName);

          return newAbstractTemplateName;

    }
    
    
    //THE FUNCTION FOR OBTAINING THE EXPANDED TEMPLATE NAME
    public static String getExpandedTemplateHash(String expandedTemplateName, OntModel wingsResults)
    {

     	  String NAMESOFCOMPONENTSANDRELATIONS = Queries.queryNodesforExpandedTemplate();
          ResultSet r1names=null;
          //ExpandedTemplateModel.write(System.out,"RDF/XML");
          r1names = ModelUtils.queryLocalRepository(NAMESOFCOMPONENTSANDRELATIONS, wingsResults);
         
          ArrayList<String> arrFinalExpandedTemplateNameRelations=new ArrayList<>();
          while(r1names.hasNext()){
              QuerySolution qs = r1names.next();
              Resource res = qs.getResource("?n");
              Resource derivedFrom = qs.getResource("?derivedFrom");
              
              if(res!=null && derivedFrom!=null)
              {
            	  arrFinalExpandedTemplateNameRelations.add(res.getLocalName()+" derivedFrom "+derivedFrom.getLocalName());
              }
          }

          //DATA PARTS
          String DATAPARTS = Queries.queryInputLinksforExpandedTemplate();
          r1names = null;
          r1names = ModelUtils.queryLocalRepository(DATAPARTS, wingsResults);
          while(r1names.hasNext()){
              QuerySolution qs = r1names.next();
              Resource resVar = qs.getResource("?var");
              Resource resNode = qs.getResource("?dest");
              arrFinalExpandedTemplateNameRelations.add(resVar.getLocalName()+" has Node "+resNode.getLocalName());
          }
          //PARAMETER PARTS
          String PARAMETERPARTS = Queries.querySelectParameterforExpandedTemplate();
          r1names = null;
          r1names = ModelUtils.queryLocalRepository(PARAMETERPARTS, wingsResults);
          while(r1names.hasNext()){
              QuerySolution qs = r1names.next();
              Resource res = qs.getResource("?p");
              Resource derivedFrom=qs.getResource("?derivedFrom"); 
              arrFinalExpandedTemplateNameRelations.add("Parameter "+res.getLocalName()+" derived From "+derivedFrom.getLocalName());
          }
          //INPUT LINKS
          String INPUTLINKS = Queries.queryInputLinksforExpandedTemplate();
          r1names = null;
          r1names = ModelUtils.queryLocalRepository(INPUTLINKS, wingsResults);
          while(r1names.hasNext()){
              QuerySolution qs = r1names.next();
              Resource resVar = qs.getResource("?var");
              Resource resNode = qs.getResource("?dest");
              arrFinalExpandedTemplateNameRelations.add("Variable "+resVar.getLocalName()+" has Node "+resNode.getLocalName());
          }
          //INPUT-P LINKS
          String INPUTLINKSP = Queries.queryInputLinksP();
          r1names = null;
          r1names = ModelUtils.queryLocalRepository(INPUTLINKSP,wingsResults);
          while(r1names.hasNext()){
              QuerySolution qs = r1names.next();
              Resource resVar = qs.getResource("?var");
              Resource resNode = qs.getResource("?dest");
              arrFinalExpandedTemplateNameRelations.add("Parameter "+resVar.getLocalName()+" has Node "+resNode.getLocalName());
          }
          //OUTPUT LINKS
          String OUTPUTLINKS = Queries.queryOutputLinks();
          r1names = null;
          r1names = ModelUtils.queryLocalRepository(OUTPUTLINKS,wingsResults);
          while(r1names.hasNext()){
              QuerySolution qs = r1names.next();
              Resource resVar = qs.getResource("?var");
              Resource resNode = qs.getResource("?orig");
              arrFinalExpandedTemplateNameRelations.add("Variable "+resVar.getLocalName()+" has origin Node "+resNode.getLocalName());
          }
          //INOUTLINKS
          String INOUTLINKS = Queries.queryInOutLinks();
          r1names = null;
          r1names = ModelUtils.queryLocalRepository(INOUTLINKS, wingsResults);
          while(r1names.hasNext()){
              QuerySolution qs = r1names.next();
              Resource resVar = qs.getResource("?var");
              Resource resNode = qs.getResource("?orig");
              Resource resNodeD = qs.getResource("?dest");
              arrFinalExpandedTemplateNameRelations.add("Variable "+resVar.getLocalName()+" has origin Node "+resNode.getLocalName()+" has Destination Node "+resNodeD.getLocalName());
          }
          
     	   
     	  String newExpandedTemplateName="";
     	  Collections.sort(arrFinalExpandedTemplateNameRelations);
          for(String x:arrFinalExpandedTemplateNameRelations)
          {
        	  newExpandedTemplateName+=x+"\n";
          }
            int indexer=expandedTemplateName.indexOf('-');
            try{
                System.out.println("NAME UNENCODED: "+newExpandedTemplateName);
                newExpandedTemplateName=expandedTemplateName.substring(0, indexer)+"_"+EncodingUtils.MD5(newExpandedTemplateName);
            }catch(Exception e){
                System.out.println("Error when calculating the hash "+e.getMessage());
            }
            System.out.println("final expandedtemplatename is "+newExpandedTemplateName);

            return newExpandedTemplateName;

    }
    
}

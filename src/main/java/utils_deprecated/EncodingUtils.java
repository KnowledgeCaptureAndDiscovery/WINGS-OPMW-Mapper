package utils_deprecated;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Class to group common methods used by the mapper
 * @author Daniel Garijo
 */
public class EncodingUtils {
    public static String MD5(String text) throws NoSuchAlgorithmException{
        MessageDigest md=MessageDigest.getInstance("MD5");
        md.update(text.getBytes());
        byte b[]=md.digest();
        StringBuilder sb=new StringBuilder();
        for(byte b1:b)
        {
                sb.append(Integer.toHexString(b1 & 0xff));
        }
        return sb.toString();

    }
    
    //a function for calculating the MD5 VERSION of the code of a component
    public static String MD5ComponentCode(String component) throws Exception
   	{
    	ZipFile zipFile = new ZipFile(component);
		ArrayList<String> arr=new ArrayList<>();


	    Enumeration<? extends ZipEntry> entries = zipFile.entries();
	    StringBuilder sb=new StringBuilder("");
	    while(entries.hasMoreElements()){
	        ZipEntry entry = entries.nextElement();
	        InputStream stream = zipFile.getInputStream(entry);
	        BufferedReader br = new BufferedReader (new InputStreamReader(stream));
	        String temp;
	        while((temp=br.readLine())!=null)
	        {
	        	if(!temp.equals(""))
	        		arr.add(temp+"\n");
	        }
	    }
	    Collections.sort(arr);
	    for(String x:arr)
	    {
	    	sb.append(x);
	    }
	    return(MD5(sb.toString()));
   	}
    
    /**
     * Encoding of the name to avoid any trouble with spacial characters and spaces
     * @param name Name to encode
     * @return encoded name
     */
    public static String encode(String name){
        name = name.replace("http://","");
        String prenom = name.substring(0, name.indexOf("/")+1);
        //remove tabs and new lines
        String nom = name.replace(prenom, "");
        if(name.length()>255){
            try {
                nom = EncodingUtils.MD5(name);
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
                nom = EncodingUtils.MD5(name);
                System.err.println("MD5 encoding: "+nom);
            } catch (Exception ex1) {
                System.err.println("Could not encode in MD5:" + name + " " + ex1.getMessage());
            }
        }
        return prenom+nom;
    }
}

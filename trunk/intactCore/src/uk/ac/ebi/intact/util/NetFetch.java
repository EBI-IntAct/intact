/**
 * NetFetch.java
 *
 * this class has one method getFile(), which takes one parameter (URL), then 
 * fetchs the file from the given URL and returns the file as a string.
 *
 * Created: Fri Sep  6 16:46:01 2002
 *
 * @author Dan Wu <a href="mailto: danwu@ebi.ac.uk"</a>
 * @version
 */

package uk.ac.ebi.intact.util; 

import java.io.* ;
import java.net.* ;

class NetFetch {
    
    public static String getFile(String url) {
	String where = "" ;
	BufferedReader rf = null ;
	String outFile = "" ;

	try {
	    where = url ; 
	} catch( Exception u ) {
	    System.out.println("Please supply URL to getFile() method.") ;
	    System.exit(1) ;
	} ; 

	try {
	    URL u = new URL(where) ;
	    InputStream in = u.openStream() ;
	    InputStreamReader isr = new InputStreamReader(in) ;
	    rf = new BufferedReader(isr) ;
	    String line ;

	    while ((line = rf.readLine()) != null ) {
		outFile += line + "\n" ;
	    }
	    //outFile.toString() ;
	} 
	catch (MalformedURLException e) {System.err.println(e);}
	catch (IOException e) {System.err.println(e);}
	
	return outFile ;
    }
}









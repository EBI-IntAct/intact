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

public class NetFetch {

    /**
     * fetches the file from a given URL and outputs all text as a string.
     * The fetched string is to be used by yasp/aristotle SPTR parser which 
     * takes an SPTR entry as a param (not an individual line).
     * @param url The url from which to fetch a file.
     * @return A string 
     */
    public static String getFile(String url) {
        String where = "" ;
        String outFile = "" ;

        try {
            where = url ;
        } catch( Exception u ) {
            System.out.println("Please supply URL to getFile() method.") ;
            System.exit( 1 ) ;
        }

        BufferedReader rf     = null ;
        InputStream in        = null;
        InputStreamReader isr = null;
        try {
            URL u = new URL( where ) ;
            in  = u.openStream() ;
            isr = new InputStreamReader( in ) ;
            rf  = new BufferedReader( isr ) ;
            String line ;

            while ((line = rf.readLine()) != null ) {
                outFile += line + "\n" ;
            }
            //outFile.toString() ;
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            // close opened streams.
            if( rf != null ) {
                try {
                    rf.close();
                } catch( IOException ioe) {}
            }
            if( isr != null ){
                try {
                    isr.close();
                } catch( IOException ioe) {}
            }
            if( in != null ){
                try {
                    in.close();
                } catch( IOException ioe) {}
            }
        }
        return outFile ;
    }
}









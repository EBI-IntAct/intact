package uk.ac.ebi.intact.util ; 

import java.util.*;
import java.io.*;
import gnu.regexp.* ;
import java.net.* ;

//re: for SPTR file loader. More packages called in ParseSPTR class
import uk.ac.ebi.yasp.util.*;

// re: OJB
import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.model.*;

import org.apache.ojb.broker.util.logging.Logger ;

//needed to call PersistenceBroker.open(repository, username, password) ;
import org.apache.ojb.broker.PersistenceBroker ;


/**
 * UpdateProteins.java
 *
 * It's a two step work -- parse the SPTR data (flat file or a net fetched file),
 * which is done mainly by ParseSPTR class. Then put the data into Intact db,
 * which is done by this class.
 *
 * Created: Mon Oct 21 14:38:05 2002
 *
 * @author Dan Wu, danwu@ebi.ac.uk
 */


public class UpdateProteins {
    static IntactHelper helper ;
    static DAOSource dataSource ;
    static DAO dao ;
    Logger log = null ;
    PersistenceBroker login ;

    //constructor
    public UpdateProteins() throws Exception {

        dataSource = DAOFactory.getDAOSource(
        "uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource") ;

        Map config = new HashMap() ;
        config.put("mappingfile", "config/repository.xml") ;
        dataSource.setConfig(config) ;

        //login.open("config/repository.xml", user, psword) ;

        try {
            dao = dataSource.getDAO() ;
            log = dataSource.getLogger() ;
        } catch (Exception abc) {
            abc.printStackTrace() ;
            System.exit(1) ;
        }

        //for testing the function. To be moved to main() later.
        try {
            helper = new IntactHelper(dataSource, "danwu", "") ;
            //helper = new IntactHelper(dataSource, "ops$danwu", "wedn1452") ;
        } catch (Exception hlp) {           
            System.out.println("\nUsage: java UpdateProteins <username> <password> <dataSource>" ) ;
            System.out.println("dataSource = " +  "<SPTR source file or SPTR protein AC>\n") ;
            hlp.printStackTrace() ;
        }
        
        helper.addCachedClass(Institution.class) ;
        helper.addCachedClass(Protein.class) ;
        helper.addCachedClass(Xref.class) ;
        helper.addCachedClass(BioSource.class) ;

    } //constructor


    public static void main(String[] args) throws Exception {

        String inFile = "" ;
        String source = "" ;
        ParseSPTR ps = new ParseSPTR() ;

        //Moved from UpdateProteins constructor
        /*
        try {
            helper = new IntactHelper(dataSource, args[0], args[1]) ;
        } catch (Exception hlp) {           
            System.out.println("\nUsage: java UpdateProteins <username> <psword> <dataSource>" ) ;
            System.out.println("dataSource = " +  "<SPTR source file or SPTR protein AC>\n") ;
            hlp.printStackTrace() ;
            System.exit(1) ;
        }
        */

        // check the type of input 
        try {
            REMatch[] srcFile = Match(args[2], "\\S+\\/\\S+|\\S+\\.\\S+") ; 
            if (srcFile.length != 0) {
                inFile = srcFile[0].toString() ;
            }
        } catch (Exception arg0) {
            System.out.println("\nUsage: java UpdateProteins <username> <password> <dataSource>" ) ;
            System.out.println("dataSource = " +  "<SPTR source file or SPTR protein AC>\n") ;
            System.exit(1) ;
        }

        source = ( inFile != "" )? inFile : args[2] ;
        System.out.println("\nYour data source/AC : " + source) ;

        if (inFile == "" ) {//param is a protein AC, not a flat file.
            source = getSPTRDateOnNet( source ) ;
            System.out.println("\nThe SPTR entry: \n\n" + source) ;
        }

        HashMap sptrData = new HashMap() ; 

        //source = "/tmp_mnt/net/nfs5/vol9/sp-pro3/data/trembl_wrel/fun.txl" ; 
        if (inFile != "") { //param is a file rather than a SPTR ac
            FlatFileLoader loader = new FlatFileLoader(source);
            String entry = null;  
                
            while ((entry = loader.getNextEntry()) != null) {
                sptrData = ps.parseSPTR(entry) ; //entry from flat file
                //to be modified to take a 2nd param -- taxid (e.g. 4932)

                if ( sptrData != null ) {
                    String id    = (String)sptrData.get("ID") ;
                    String ac    = (String)sptrData.get("AC") ;
                    String taxId = (String)sptrData.get("TAXID") ;
                    String seq   = (String)sptrData.get("SEQ") ;
                    String crc   = (String)sptrData.get("CRC64");
                    String gn    = (String)sptrData.get("GN") ;
                    String de    = (String)sptrData.get("DE") ;
                    String loc   = (String)sptrData.get("LOCATION") ;
                
                    toIntactDB(helper, id, ac, taxId, seq, 
                               crc, gn, de, loc) ;
                }
                //dao.close() ;
            }
        }

        else { 
            sptrData = ps.parseSPTR(source) ; //entry fetched from net (given a URL)
            
            if ( sptrData != null ) {
                String id    = (String)sptrData.get("ID") ;
                String ac    = (String)sptrData.get("AC") ;
                String taxId = (String)sptrData.get("TAXID") ;
                String seq   = (String)sptrData.get("SEQ") ;
                String crc   = (String)sptrData.get("CRC64");
                String gn    = (String)sptrData.get("GN") ;
                String de    = (String)sptrData.get("DE") ;
                String loc   = (String)sptrData.get("LOCATION") ;
            
                toIntactDB(helper, id,  ac, taxId,  seq, 
                            crc, gn, de, loc) ;
            }
            //dao.close() ;
        }
    } //main()


    /**
     * fetches a SPTR entry on net from a given SPTR protein AC, returns the entry text
     * as a string
     */
    public static String getSPTRDateOnNet(String sptrAC) {
        String url = "http://www3.ebi.ac.uk/srs7bin/cgi-bin/wgetz?-e+[SWALL-acc:" 
            + sptrAC + "]+-vn+2+-ascii" ;

        //a class in ../util
        NetFetch nf = new NetFetch() ;
        String entry = nf.getFile(url) ;
        return entry ; 
    }


    /**
     * from a given string and a given pattern(string), to find all matches. The matched are  
     * retured as a list.
     */
    public static REMatch[] Match(String textin, String pattern )  throws REException {
        
        RE magic = new RE( pattern );
        REMatch[] allMatches = magic.getAllMatches( textin );
       
        return allMatches ;
    }


    /**
     * replaces all matched strings (from a given pattern) with a given string.
     */
    public static String replace(String pattern, String textin, String subs )  
        throws REException {        
        RE magic = new RE( pattern );
        String result = magic.substituteAll( textin, subs  );       
        return result ;
    }


    /**
     * returns the first group of non-white-space chars from a string.
     * e.g. returns 'the' from an input 'the apple is red'. 
     */
    public static String getFirstElement(String original, String pattern) {
        REMatch[] allMatches = null ;

        try {
            allMatches = Match(original, pattern);
        } catch (Exception ac1) {
            ac1.printStackTrace() ;
        }
        return allMatches[0].toString() ;
    }


    public static void addNewXref(AnnotatedObject current,
                           Xref xref)  throws Exception {

        // Todo: Make sure the xref does not yet exist in the object

        current.addXref(xref);
        if (xref.getParentAc() == current.getAc()){
            dao.create(xref);
        }
    }


    /**
     * puts data parsed from a SPTR entry, into a SQL db.  
     * 
     */
    public static void toIntactDB(IntactHelper helper, 
                                  String id, String ac, 
                                  String taxid, String seq, 
                                  String crc, String gn, 
                                  String de, String loc) {

        ac = getFirstElement(ac, "[A-Za-z0-9]+") ;
        gn = (gn.equals (""))?  "" : getFirstElement(gn, "[A-Za-z0-9\\'\\-\\.]+") ;

        if (de.length() > 50) {
            de = de.substring(0, 46) + " ..." ;
        } //see Interactor table definition

        /*
        System.out.println("ID: "+ id) ;
        System.out.println("AC: "+ ac) ;
        System.out.println("TaxID: "+ taxid) ;
        System.out.println("SEQ: "+ seq) ;
        System.out.println("CRC64: "+ crc) ;
        System.out.println("ShortLabel (GN): " + gn) ;
        System.out.println("FullName (DE): "+ de) ;
        System.out.println("Place (CC): "+ loc) ;
        System.out.println() ;
        */

        UpdateProteins app = null ;
        Xref SpXref = null ;
        Protein protein  = null ;
       
        try {
            SpXref = new Xref( (Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                                  (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "SPTR"),
                                   ac, null, null ) ;

            ////next line not working
            //System.out.println("ParentAC: " + SpXref.getParentAc()) ; //all null!
            System.out.println("Primary_ID: " + SpXref.getPrimaryId() + "\n") ;
        }
        catch(Exception xr ) {
            xr.printStackTrace() ;
        }
        

        try {
            protein = (Protein) helper.getObjectByXref(Protein.class, id) ;

            ////next method may return more than one entrys for same protein, because 
            ////Shortlabel of protein can be the GN data.
            //protein = (Protein) helper.getObjectByLabel(Protein.class, ac) ;

            if ( protein != null) {
                System.out.println("Protein found ByXref(primaryID): " + protein) ;

                //if GN line null, use ID as ShortLabel (in Interactor)
                if (gn.equals("")) {
                    protein.setShortLabel(id) ; 
                }
                else protein.setShortLabel(gn) ;

                protein.setFullName(de) ;
                protein.setCrc64(crc) ;
                // protein.setFormOfAc("EBI-14") ;//just a test!
               //other data for a given protein may be updated here. 

                dao.update(protein);
             }

            
            else { //if protein not in the db.
                System.out.println("Protein ByXref(primaryID) not found in Intact DB: spAC = " + ac) ;

                protein = new Protein();
                protein.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
                
                if (gn.equals("")) {
                    protein.setShortLabel(id) ; 
                }
                else protein.setShortLabel(gn) ; 
                
                protein.setFullName(de) ;
                protein.setCrc64(crc) ;

                dao.create(protein) ;
                
                addNewXref(protein,
                           new Xref ((Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                                     (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "SPTR"),
                                     ac,
                                     null, null));
                
            }
            

            System.out.println() ;
            
        } 
        catch (Exception pr) {
            pr.printStackTrace() ;
        }
            
        
        try {
            //dao.begin() ;
            //dao.update(protein) ;            
            //dao.create(protein) ;
            //dao.commit() ;
            //dao.close() ;
        }
        catch (Exception wr) {
            wr.printStackTrace() ;
        }

    } 
}

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
 * It's a two step work -- parse the SPTR data (flat file or a net fetched file)
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

        /*
        //for testing the function. To be moved to main() later.
        try {
            //hard coded now, will change to args[0], args[1] for username and psword
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
        */
    } //constructor


    public void addNewXref(AnnotatedObject current,
                           Xref xref)  throws Exception {

        // Todo: Make sure the xref does not yet exist in the object

        current.addXref(xref);
        if (xref.getParentAc() == current.getAc()){
            dao.create(xref);
        }
    }


    /**
     * add (not update) a new BioSource to the db
     *@param orgName Organism name 
     *@param taxId Taxonomy ID 
     *@throws Exception
     */
    public void addBioSource(String orgName, String taxId) throws Exception {
        BioSource bs = new BioSource() ;
        bs.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI")) ;
        bs.setScientificName(orgName) ;
        bs.setTaxId(taxId) ;
        dao.create(bs) ;
    }


    public static void main(String[] args) throws Exception {


        UpdateProteins app = new UpdateProteins() ;
        String inFile = "" ;
        String source = "" ;
        ParseSPTR ps = new ParseSPTR() ;
        Protein protein ;
        String filterTaxId = "" ;

        try {
            //hard coded now, will change to args[0], args[1] for username and psword
            helper = new IntactHelper(dataSource, args[0], args[1]) ;
            //helper = new IntactHelper(dataSource, "ops$danwu", "wedn1452") ;
        } catch (Exception hlp) {           
            System.out.println("\nUsage: java UpdateProteins <username> <password> <dataSource> <TaxID>?" ) ;
            System.out.println("dataSource = " +  "<SPTR path/flatfile or SPTR protein AC>") ;
            System.out.println("TaxID is cumpulsary if the dataSourcce is a flatfile rather than a SPTR ac\n") ;
            System.out.println("Now, most likely your login details were not right.\n") ;
            System.exit(1) ;
        }
        
        helper.addCachedClass(Institution.class) ;
        helper.addCachedClass(Protein.class) ;
        helper.addCachedClass(Xref.class) ;
        helper.addCachedClass(BioSource.class) ;
        helper.addCachedClass(Interactor.class) ;

        // check the type of input 
        try {
            REMatch[] srcFile = match(args[2], "\\S+\\/\\S+|\\S+\\.\\S+") ; 
            if (srcFile.length != 0) {
                inFile = srcFile[0].toString() ;
            }
        } catch (Exception arg0) {
            System.out.println("\nUsage: java UpdateProteins <username> <password> <dataSource> <TaxID>?" ) ;
            System.out.println("dataSource = " +  "<SPTR path/flatfile or SPTR protein AC>.\n") ;
            System.out.println("TaxID is cumpulsary if the dataSourcce is a flatfile rather than a SPTR ac.\n") ;
            System.out.println("dataSource please.\n") ;
            System.exit(1) ;
        }

        source = ( inFile != "" )? inFile : args[2] ;

        //check, if the data source is a flatfile, a TaxId may be specified. 
        if (inFile != "") {
            try {
                filterTaxId = args[3] ;
            } catch (Exception taxidParam) {
                System.out.println("\nUsage: java UpdateProteins <username> <password> <dataSource> <TaxID>?") ;
                System.out.println("You need to specify the TaxID (e.g. 4932 for Baker's yeast).\n") ;
                taxidParam.printStackTrace() ;
                System.exit(1) ;
            }
        }


        System.out.println("\nYour data source/AC : " + source) ;

        if (inFile == "" ) {//param is a protein AC, not a flat file.
            source = getSPTRDateOnNet( source ) ;
            
            REMatch[] warnMsg = match(source, "<B>no entries found</B>") ;            

            if (warnMsg.length >0 ) {
                System.out.println("The SPTR AC doesn't exist!") ;
                System.exit(1) ;
            }
            else
            System.out.println("\nThe SPTR entry: \n\n" + source) ;
        }

        HashMap sptrData = new HashMap() ; 

        //source = "/tmp_mnt/net/nfs5/vol9/sp-pro3/data/trembl_wrel/fun.txl" ; 
        if (inFile != "") { //param is a file rather than a SPTR ac
            FlatFileLoader loader = new FlatFileLoader(source);
            String entry = null;  
                
            while ((entry = loader.getNextEntry()) != null) {
                sptrData = ps.parseSPTR(entry) ; //entry from flat file

                if ( sptrData != null ) {
                    String id    = (String)sptrData.get("ID") ;
                    String ac    = (String)sptrData.get("AC") ;
                    String taxId = (String)sptrData.get("TAXID") ;
                    String orgName = (String)sptrData.get("ORGNAME") ;
                    String seq   = (String)sptrData.get("SEQ") ;
                    String crc   = (String)sptrData.get("CRC64");
                    String gn    = (String)sptrData.get("GN") ;
                    String de    = (String)sptrData.get("DE") ;
                    String loc   = (String)sptrData.get("LOCATION") ;
                    String sgd   = (String)sptrData.get("SGD") ;                
                    
                    if (taxId.equals(filterTaxId)) {
                        app.toIntactDB(id, ac, taxId, orgName, seq, crc, gn, de, loc, sgd) ;
                    }
                }
            }
        }

        else { 

            sptrData = ps.parseSPTR(source) ; //entry fetched from net (given a URL)
            
            if ( sptrData != null ) {
                String id    = (String)sptrData.get("ID") ;
                String ac    = (String)sptrData.get("AC") ;
                String taxId = (String)sptrData.get("TAXID") ;
                String orgName = (String)sptrData.get("ORGNAME") ;
                String seq   = (String)sptrData.get("SEQ") ;
                String crc   = (String)sptrData.get("CRC64");
                String gn    = (String)sptrData.get("GN") ;
                String de    = (String)sptrData.get("DE") ;
                String loc   = (String)sptrData.get("LOCATION") ;
                String sgd   = (String)sptrData.get("SGD") ;

                if (taxId.equals(taxId)) {
                    app.toIntactDB(id, ac, taxId, orgName, seq, crc, gn, de, loc, sgd) ;
                }
            }
        }
    } //main()


    /**
     * fetches a SPTR entry on net from a given SPTR protein AC, returns the entry text
     * as a string. Since the yasp/aristotle takes an entry as parameter (rather than a individaul
     * text line), this method and the class NetFetch are needed.
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
     * retured as a list. This method uses gnu.regexp.* package, not the org.apache.regexp.*
     */
    public static REMatch[] match(String textin, String pattern )  throws REException {
        
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
            allMatches = match(original, pattern);
        } catch (Exception ac1) {
            ac1.printStackTrace() ;
        }
        return allMatches[0].toString() ;
    }



    /**
     * puts data parsed from a SPTR entry, into a SQL db.  
     * 
     */
    public void toIntactDB( String id, String ac,
                            String taxId, String orgName,
                            String seq, String crc, 
                            String gn, String de,
                            String loc, String sgd) {

        Xref SpXref = null ;
        Protein protein  = null ;
        BioSource biosrcID = null ;
        BioSource bioSrc = null ;

        ac = getFirstElement(ac, "[A-Za-z0-9]+") ;
        gn = (gn.equals (""))?  "" : getFirstElement(gn, "[A-Za-z0-9\\'\\-\\.]+") ;
        de = de.trim() ;

        if (de.length() > 50) {
            de = de.substring(0, 46) + " ..." ;
        } //see Interactor table definition

        //note: the orgName and sgd were not from yasp/aristotle.
        /*
        System.out.println("ID: "+ id) ;
        System.out.println("AC: "+ ac) ;
        System.out.println("TaxID: "+ taxId) ;
        System.out.println("OrgName (OS): "+ orgName) ;
        System.out.println("SEQ: "+ seq) ;
        System.out.println("CRC64: "+ crc) ;
        System.out.println("ShortLabel (GN): " + gn) ;
        System.out.println("FullName (DE): "+ de) ;
        System.out.println("Place (CC): "+ loc) ;
        System.out.println("SGD (DR): "+ sgd) ;
        System.out.println() ;
        */
       
        try {
            SpXref = new Xref( (Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                               (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "SPTR"),
                               ac, null, null ) ;

            ////next line not working
            //System.out.println("ParentAC: "   + SpXref.getParentAc()) ; //all null!
            System.out.println("Primary_ID: " + SpXref.getPrimaryId() + "\n") ;
        }
        catch(Exception xr ) {
            xr.printStackTrace() ;
        }
        

        try {
            //add bioSource. cannot use taxId as param.
            bioSrc = (BioSource) helper.getBioSourceByName(orgName) ;

            System.out.println("taxID: " + taxId + ", orgName: " + orgName) ;
            if (bioSrc == null) {
                addBioSource(orgName, taxId) ;
                dao.commit() ;
            }

            //add/update Protein 
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
                protein.setUpdated(new Date()) ;
                protein.setBioSource(bioSrc) ;
                //other data for a given protein may be updated here. 

                dao.begin();
                dao.update(protein);
                dao.commit() ;
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
                protein.setBioSource(bioSrc) ;
                //other data for a given protein can be added too.

                dao.begin() ;
                dao.create(protein) ;
                
                //xref in terms of SPTR db
                addNewXref(protein,
                           new Xref ((Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                                     (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "SPTR"),
                                     ac,
                                     null, null));


                //xref in terms of SGD db
                if (sgd != null) {
                    addNewXref(protein,
                               new Xref((Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                                        (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "SGD"),
                                        sgd,
                                        null, null));
                }

                //xref in terms of XXX db
                //addNewXref() ; 

                dao.commit() ;
            }
            

            System.out.println() ;
            
        } 
        catch (Exception pr) {
            pr.printStackTrace() ;
        }
    } //toIntactDB()

}

package uk.ac.ebi.intact.util ; 

import java.util.*;
import java.io.*;
import gnu.regexp.* ;
import java.net.* ;

import uk.ac.ebi.sptr.flatfile.yasp.*;
import uk.ac.ebi.sptr.flatfile.yasp.util.EmptyEntry;
import uk.ac.ebi.interfaces.sptr.*;
import java.util.Iterator;

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


public class UpdateProteins implements UpdateProteinsI {
    static IntactHelper helper ;
    static DAOSource dataSource ;
    static DAO dao ;
    Logger log = null ;
    PersistenceBroker login ;

    HashMap parsed = null ;
    static String fetched = "";
    static String url = "";
    int numProt ; 

    public final String getUrl(String sptrAC) {
        String url = "http://www3.ebi.ac.uk/srs7bin/cgi-bin/wgetz?-e+[SWALL-acc:" 
            + sptrAC + "]+-vn+2+-ascii" ;

        return url ;
    }

    
    public String getAnEntry(String url) {
	String where = "" ;
	BufferedReader rf = null ;
	String outFile = "" ;

	try {
	    where = url ; 
	} catch( Exception u ) {
	    System.out.println("Please supply URL to getAnEntry() method ...") ;
	    System.out.println("If the URL returns html file, this program won't parse it.") ;
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

        return outFile;
    }

    /**
     * from a given string and a given pattern(string), to find all matches. The matched are  
     * retured as a list. This method uses gnu.regexp.* package, not the org.apache.regexp.*
     *@param textin A string from which some pattern will be matched. 
     *@param pattern A string as a pattern.
     *@return A list of matched pattern.
     */
    public REMatch[] match(String textin, String pattern ) {
        REMatch[] allMatches = null ;
        try {
            RE magic = new RE( pattern );
            allMatches = magic.getAllMatches( textin );
        } catch (Exception e_RE) {
            e_RE.printStackTrace() ;
        }

        return allMatches ;
    }


    public int insertSPTrProteins(String sourceUrl,
                                  String taxid,
                                  IntactHelper helper,
                                  boolean update) {
        return numProt ;
    }


    public void addNewXref(AnnotatedObject current,
                           Xref xref)  {
        // Todo: Make sure the xref does not yet exist in the object
        current.addXref(xref);
        if (xref.getParentAc() == current.getAc()){
            try {
                dao.create(xref);
            } catch (Exception e_xref) {
                e_xref.printStackTrace();
            }
        }
    }


    /**
     * add (not update) a new BioSource to the db
     *@param orgName Organism name 
     *@param taxId Taxonomy ID 
     */
    public void addBioSource(String orgName, String taxId) {
        BioSource bs = new BioSource() ;
        try {
            bs.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI")) ;
            bs.setFullName(orgName) ;
            bs.setTaxId(taxId) ;
            dao.create(bs) ;
        } catch (Exception e_bioSrc) {
            e_bioSrc.printStackTrace() ;
        }
    }


    public static void main(String[] args) throws Exception {

        UpdateProteins app = new UpdateProteins() ;
        String inFile = "" ;
        String source = "" ;
        Protein protein ;
        String filterTaxId = "" ;


        System.setProperty("org.xml.sax.driver","org.apache.xerces.parsers.SAXParser");
        
        if (! args.length > 0 ) {
            System.out.println("Please supply a SPTR AC, or /Path/FlatFile or a URL\n") ;
            System.out.println("e.g. java ThisProgram P12345\n") ;
        }

        try {
            if ((app.match(args[0], "SWALL-acc:")).length > 0) { // match a AC (External access)!
                source = app.getUrl(args[0]) ;
            }
            if ((app.match(args[0], "://")).length > 0) { // match a url (External access)!
                source = args[0] ;
            }
            else { // match a flatfile (internal access)!
                source = args[0] ;
            }





            /*if args[0] a URL (not a flatfile). Bug! at the moment. Unless getEntry(url) is used*/
            //Iterator it = YASP.parseAll(new URL("http://srs.ebi.ac.uk/srsbin/cgi-bin/wgetz?-e+[swall-des:proteinase]&[swall-org:human]+-vn+2+-lv+4+-ascii")) ;
 
            /*if args[0] a file (not a string) */
            Iterator it = YASP.parseAll(new File("/homes/danwu/download/fun.txl")) ;

            /*Iterate each entry*/
            while (it.hasNext()) {
                SPTREntry entry = (SPTREntry) it.next();

                // Print out the parsed data.
                if (!((EntryIterator)it).hadException()) {
                    System.out.println(entry.getID());
                    System.out.println(entry.getCRC64());
                    //System.out.println(entry.getGenes()); //a string reference of array of array.
                    System.out.println(entry.getOrganismNames()[0]);
                    System.out.println(entry.getNCBITaxonomyID((String)entry.getOrganismNames()[0]));//can be an array
                    System.out.println("\n");
                }
            }
        

            //if the args[0] is a sptr AC entry
            /*
            //SPTREntry entry = YASP.parse(new URL(source)); //suppose to be working.
            SPTREntry entry = YASP.parse(app.getAnEntry(source));

            // Print out the ID of the entry
            System.out.println(entry.getID());
            System.out.println(entry.getCRC64());
            System.out.println(entry.getSequence());
            System.out.println(entry.getOrganismNames()[0]);
            System.out.println(entry.getNCBITaxonomyID((String)entry.getOrganismNames()[0]));//can be array
            */


        } catch (Exception e) {
            e.printStackTrace();
        }


/*

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

*/

/*
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
*/
/*
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
*/
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
        String entry = nf.getAnEntry(url) ;
        return entry ; 
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
            allMatches = new UpdateProteins().match(original, pattern);
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
                               ac, null, null, null) ;

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
                                     null, null, null));


                //xref in terms of SGD db
                if (sgd != null) {
                    addNewXref(protein,
                               new Xref((Institution) helper.getObjectByLabel(Institution.class, "EBI"),
                                        (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "SGD"),
                                        sgd,
                                        null, null, null));
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

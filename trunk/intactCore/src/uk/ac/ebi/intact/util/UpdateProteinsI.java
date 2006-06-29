/**
 * Created by IntelliJ IDEA.
 * User: hhe
 * Date: Apr 11, 2003
 * Time: 1:31:41 PM
 * To change this prediction use Options | File Templates.
 */
package uk.ac.ebi.intact.util;

import org.apache.log4j.Logger;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import static uk.ac.ebi.intact.core.CvContext.CvName;
import uk.ac.ebi.intact.core.CvContext;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Defines the functionality of protein import utilities.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public abstract class UpdateProteinsI {

    private static final Log logger = LogFactory.getLog(UpdateProteinsI.class);

    public static class UpdateException extends RuntimeException {

        public UpdateException( String message ) {
            super( message );
        }
    }

    // cache useful object to avoid redoing queries

    /**
     * The owner of the created object
     */
    protected static Institution myInstitution;

    ////////////////////
    // Xref databases

    protected static CvDatabase uniprotDatabase;
    protected static String srsUrl;
    protected static CvDatabase intactDatabase;
    protected static CvDatabase sgdDatabase;
    protected static CvDatabase goDatabase;
    protected static CvDatabase interproDatabase;
    protected static CvDatabase flybaseDatabase;
    protected static CvDatabase reactomeDatabase;
    protected static CvDatabase hugeDatabase;

    /////////////////////////////////////////////////
    // Describe wether an Xref is related the primary
    // SPTR AC (identityCrefQualifier) or
    // not (secondaryXrefQualifier)
    protected static CvXrefQualifier identityXrefQualifier;
    protected static CvXrefQualifier secondaryXrefQualifier;

    protected static CvXrefQualifier isoFormParentXrefQualifier;

    protected static CvTopic isoformComment;
    protected static CvTopic noUniprotUpdate;

    protected static CvAliasType isoformSynonym;
    protected static CvAliasType geneNameAliasType;
    protected static CvAliasType geneNameSynonymAliasType;
    protected static CvAliasType orfNameAliasType;
    protected static CvAliasType locusNameAliasType;

    protected static CvInteractorType proteinType;

    protected IntactHelper helper = null;

    protected BioSourceFactory bioSourceFactory;

    /**
     * If true, each protein is updated in a distinct transaction. If localTransactionControl is false, no local
     * transactions are initiated, control is left with the calling class. This can be used e.g. to have transactions
     * span the insertion of all proteins of an entire complex. Default is true.
     */
    protected static boolean localTransactionControl = true;

    // Keeps eventual parsing error while the processing is carried on
    protected Map<Integer,Exception> parsingExceptions = new HashMap<Integer,Exception>();


    //////////////////////////////////
    // Constructors

    public UpdateProteinsI( boolean setOutputOn )
    {
        try {
            if ( setOutputOn ) {
                HttpProxyManager.setup();
            } else {
                HttpProxyManager.setup( null );
            }

        } catch ( HttpProxyManager.ProxyConfigurationNotFound proxyConfigurationNotFound ) {
            proxyConfigurationNotFound.printStackTrace();
        }
    }

    /**
     * @param helper    IntactHelper object to access (read/write) the database.
     * @param cacheSize the number of valid biosource to cache during the update process.
     *
     * @throws UpdateException
     */
    public UpdateProteinsI( IntactHelper helper, int cacheSize ) throws UpdateException {
        this( true );
        this.helper = helper;
        collectDefaultObject( helper );

        bioSourceFactory = new BioSourceFactory( helper, myInstitution, cacheSize );
    }

    /**
     * Default constructor which initialize the bioSource cache to default.
     *
     * @param helper IntactHelper object to access (read/write) the database.
     *
     * @throws UpdateException
     */
    public UpdateProteinsI( IntactHelper helper, boolean setOutputOn ) throws UpdateException {
        this( setOutputOn );
        this.helper = helper;
        collectDefaultObject( helper );

        bioSourceFactory = new BioSourceFactory( helper, myInstitution );
    }

    /**
     * Default constructor which initialize the bioSource cache to default.
     *
     * @param helper IntactHelper object to access (read/write) the database.
     *
     * @throws UpdateException
     */
    public UpdateProteinsI( IntactHelper helper) throws UpdateException {
        this( helper, true);
    }


    //////////////////////////////////
    // Methods

    private void collectDefaultObject( IntactHelper helper ) {

        try
        {
            myInstitution = helper.getInstitution();
        }
        catch (IntactException e)
        {
            e.printStackTrace();
        }
        // Sugath: I commented out this code because getInstitution() shouldn't return
            // a null instutition. It may throw an IntactException which should be captured
            // by the try block. 
//            if( myInstitution == null ) {
//                String msg = "Unable to find the Institution: check your Institution configuration file";
//                if( logger != null ) {
//                    logger.error( msg );
//                }
//                throw new UpdateException( msg );
//            }

            CvContext cvContext = CvContext.getCurrentInstance();

            /**
             * Load CVs from the context
             */
            sgdDatabase = (CvDatabase) cvContext.getCvObject(CvName.SGD_DB); // sgd
            uniprotDatabase = (CvDatabase) cvContext.getCvObject(CvName.UNIPROT_DB); // uniprot

            // search for the SRS link.
            srsUrl = cvContext.getSrsUrl();

            intactDatabase = (CvDatabase) cvContext.getCvObject(CvName.INTACT_DB);
            goDatabase = (CvDatabase) cvContext.getCvObject(CvName.GO_DB);
            interproDatabase = (CvDatabase) cvContext.getCvObject(CvName.INTERPRO_DB);
            flybaseDatabase = (CvDatabase) cvContext.getCvObject(CvName.FLYBASE_DB);
            reactomeDatabase = (CvDatabase) cvContext.getCvObject(CvName.REACTOME_DB);
            hugeDatabase = (CvDatabase) cvContext.getCvObject(CvName.HUGE_DB);

            identityXrefQualifier = (CvXrefQualifier) cvContext.getCvObject(CvName.IDENTITY_XREF_QUALIFIER);
            secondaryXrefQualifier = (CvXrefQualifier) cvContext.getCvObject(CvName.SECONDARY_XREF_QUALIFIER);
            isoFormParentXrefQualifier = (CvXrefQualifier) cvContext.getCvObject(CvName.ISOFORM_PARENT_XREF_QUALIFIER);

            // only one search by shortlabel as it still doesn't have MI number.
            isoformComment = (CvTopic) cvContext.getCvObject(CvName.ISOFORM_COMMENT);
            noUniprotUpdate = (CvTopic) cvContext.getCvObject(CvName.NO_UNIPROT_UPDATE);


            geneNameAliasType = (CvAliasType) cvContext.getCvObject(CvName.GENE_NAME_ALIAS_TYPE);
            geneNameSynonymAliasType = (CvAliasType) cvContext.getCvObject(CvName.GENE_NAME_SYNONYM_ALIAS_TYPE);
            isoformSynonym = (CvAliasType) cvContext.getCvObject(CvName.ISOFORM_SYNONYM);
            locusNameAliasType = (CvAliasType) cvContext.getCvObject(CvName.LOCUS_NAME_ALIAS_TYPE);
            orfNameAliasType = (CvAliasType) cvContext.getCvObject(CvName.ORF_NAME_ALIAS_TYPE);

            proteinType = (CvInteractorType) cvContext.getCvObject(CvName.PROTEIN_TYPE);

    }

    /**
     * Gives all Exceptions that have been raised during the last processing.
     *
     * @return a map Entry Count ---> Exception. It can be null.
     */
    public Map getParsingExceptions() {
        return parsingExceptions;
    }

    /**
     * Sets the internal helper. This method is used by the editor to set the helper, call necessary methods and close
     * the helper again (the objective is to keep the scope of the Intact helper to minimum).
     *
     * @param helper the Intact helper to set.
     */
    public void setIntactHelper( IntactHelper helper ) {
        this.helper = helper;
        this.bioSourceFactory.setIntactHelper( helper );
    }

    /**
     * Inserts zero or more proteins created from SPTR entries which are retrieved from a Stream. IntAct Protein objects
     * represent a specific amino acid sequence in a specific organism. If a SPTr entry contains more than one organism,
     * one IntAct entry will be created for each organism, unless the taxid parameter is not null.
     *
     * @param inputStream The straem from which YASP will read the ENtries content.
     * @param taxid       Of all entries retrieved from sourceURL, insert only those which have this taxid. If taxid is
     *                    empty, insert all protein objects.
     * @param update      If true, update existing Protein objects according to the retrieved data. else, skip existing
     *                    Protein objects.
     *
     * @return Collection of protein objects created/updated.
     */
    public abstract Collection insertSPTrProteins( InputStream inputStream, String taxid, boolean update );

    /**
     * Inserts zero or more proteins created from SPTR entries which are retrieved from a URL. IntAct Protein objects
     * represent a specific amino acid sequence in a specific organism. If a SPTr entry contains more than one organism,
     * one IntAct entry will be created for each organism, unless the taxid parameter is not null.
     *
     * @param sourceUrl The URL which delivers zero or more SPTR flat file formatted entries.
     * @param taxid     Of all entries retrieved from sourceURL, insert only those which have this taxid. If taxid is
     *                  empty, insert all protein objects.
     * @param update    If true, update existing Protein objects according to the retrieved data. else, skip existing
     *                  Protein objects.
     *
     * @return The number of protein objects created.
     */
    public abstract int insertSPTrProteinsFromURL( String sourceUrl, String taxid, boolean update );

    /**
     * Inserts zero or more proteins created from SPTR entries which are retrieved from an SPTR Accession number. IntAct
     * Protein objects represent a specific amino acid sequence in a specific organism. If a SPTr entry contains more
     * than one organism, one IntAct entry will be created for each organism.
     *
     * @param proteinAc SPTR Accession number of the protein to insert/update
     *
     * @return a set of created/updated protein.
     */
    public abstract Collection insertSPTrProteins( String proteinAc );

    /**
     * Inserts zero or more proteins created from SPTR entries which are retrieved from an SPTR Accession number. IntAct
     * Protein objects represent a specific amino acid sequence in a specific organism. If a SPTr entry contains more
     * than one organism, one IntAct entry will be created for each organism.
     *
     * @param proteinAc SPTR Accession number of the protein to insert/update
     * @param taxId     The tax id the protein should have
     * @param update    If true, update existing Protein objects according to the retrieved data. else, skip existing
     *                  Protein objects.
     *
     * @return a set of created/updated protein.
     */
    public abstract Collection insertSPTrProteins( String proteinAc, String taxId, boolean update );

    /**
     * Creates a simple Protein object for entries which are not in SPTR. The Protein will more or less only contain the
     * crossreference to the source database.
     *
     * @param anAc      The primary identifier of the protein in the external database.
     * @param aDatabase The database in which the protein is listed.
     * @param aTaxId    The tax id the protein should have
     *
     * @return the protein created or retrieved from the IntAct database
     */
    public abstract Protein insertSimpleProtein( String anAc, CvDatabase aDatabase, String aTaxId )
            throws IntactException;

    /**
     * From a given sptr AC, returns a full URL from where a flatfile format SPTR entry will be fetched. Note, the SRS
     * has several format of data output, the URLs which outputs html format SPTR entry CANNOT be used, since YASP
     * does't have html parsing function.
     *
     * @param sptrAC a SPTR AC
     *
     * @return a full URL.
     */
    public abstract String getUrl( String sptrAC );

    /**
     * add (not update) a new Xref to the given Annotated object and write it in the database.
     *
     * @param current the object to which we add a new Xref
     * @param xref    the Xref to add to the AnnotatedObject
     *
     * @return true if the object as been added, else false.
     */
    public abstract boolean addNewXref( AnnotatedObject current, final Xref xref );

    /**
     * add (not update) a new Xref to the given Annotated object and write it in the database.
     *
     * @param current the object to which we add a new Xref
     * @param alias   the Alias to add to the AnnotatedObject
     */
    public abstract boolean addNewAlias( AnnotatedObject current, final Alias alias );

    /**
     * Gives the count of created protein
     *
     * @return created protein count
     */
    public abstract int getProteinCreatedCount();

    /**
     * Gives the count of updated protein
     *
     * @return updated protein count
     */
    public abstract int getProteinUpdatedCount();

    /**
     * Gives the count of up-to-date protein (i.e. existing in IntAct but don't need to be updated)
     *
     * @return up-to-date protein count
     */
    public abstract int getProteinUpToDateCount();

    /**
     * Gives the count of all potential protein (i.e. for an SPTREntry, we can create/update several IntAct protein. One
     * by entry's taxid)
     *
     * @return potential protein count
     */
    public abstract int getProteinCount();

    /**
     * Gives the count of protein which gaves us errors during the processing.
     *
     * @return protein skipped count
     */
    public abstract int getProteinSkippedCount();

    /**
     * Gives the count of created splice variant
     *
     * @return created protein count
     */
    public abstract int getSpliceVariantCreatedCount();

    /**
     * Gives the count of updated splice variant
     *
     * @return updated protein count
     */
    public abstract int getSpliceVariantUpdatedCount();

    /**
     * Gives the count of up-to-date splice variant (i.e. existing in IntAct but don't need to be updated)
     *
     * @return up-to-date protein count
     */
    public abstract int getSpliceVariantUpToDateCount();

    /**
     * Gives the count of all potential splice variant (i.e. for an SPTREntry, we can create/update several IntAct
     * protein. One by entry's taxid)
     *
     * @return potential protein count
     */
    public abstract int getSpliceVariantCount();

    /**
     * Gives the count of splice variant which gaves us errors during the processing.
     *
     * @return splice variant skipped count
     */
    public abstract int getSpliceVariantSkippedCount();

    /**
     * Gives the number of entry found in the given URL
     *
     * @return entry count
     */
    public abstract int getEntryCount();

    /**
     * Gives the number of entry successfully processed.
     *
     * @return entry successfully processed count.
     */
    public abstract int getEntryProcessededCount();

    /**
     * Gives the number of entry skipped during the process.
     *
     * @return skipped entry count.
     */
    public abstract int getEntrySkippedCount();

    /**
     * Allows to displays on the screen what's going on during the update process.
     *
     * @param debug <b>true</b> to enable, <b>false</b> to disable
     */
    public abstract void setDebugOnScreen( boolean debug );

    /**
     * return the filename in which have been saved all Entries which gaves us processing errors.
     *
     * @return the filename or null if not existing
     */
    public abstract String getErrorFileName();

    /**
     * If true, each protein is updated in a distinct transaction. If localTransactionControl is false, no local
     * transactions are initiated, control is left with the calling class. This can be used e.g. to have transctions
     * span the insertion of all proteins of an entire complex.
     *
     * @return current value of localTransactionControl
     */
    public abstract boolean isLocalTransactionControl();

    /**
     * If true, each protein is updated in a distinct transaction. If localTransactionControl is false, no local
     * transactions are initiated, control is left with the calling class. This can be used e.g. to have transctions
     * span the insertion of all proteins of an entire complex.
     *
     * @param localTransactionControl New value for localTransactionControl
     */
    public abstract void setLocalTransactionControl( boolean localTransactionControl );

    /**
     * Update all protein found in the provided collection. If none provided, update all protein that can be retreived
     * from the current intact node.
     *
     * @param proteins a Collection of protein.
     */
    public abstract int updateAllProteins( Collection<Protein> proteins ) throws IntactException;
}
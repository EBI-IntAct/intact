/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.business;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.VirtualProxy;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.proxy.IntactObjectProxy;
import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.util.PropertyLoader;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


/**
 * <p>This class implements the business logic for intact. The requests to be processed are usually obtained (in a
 * webapp environment) from a struts action class and then the business operations are carried out, via the DAO
 * interface to the data source. </p>
 *
 * @author Chris Lewington
 */
public class IntactHelper implements SearchI, Externalizable {

    /**
     * Path of the configuration file which allow to retrieve the inforamtion related to the IntAct node we are running
     * on.
     */
    private static final String INSTITUTION_CONFIG_FILE = "/config/Institution.properties";

    //initialise variables used for persistence
    private DAO dao;
    private DAOSource dataSource;

    //Logger is not serializable - but transient on its own doesn't work!
    private transient Logger pr;

    public void addCachedClass( Class clazz ) {
        if ( dao != null ) {
            dao.addCachedClass( clazz );
        }
    }

    /**
     * Wipe the whole cache.
     */
    public void clearCache() {
        if ( dao != null ) {
            dao.clearCache();
        }
    }

    /**
     * True if an object of given class and AC exists in the cache.
     *
     * @param clazz the object type.
     * @param ac    the AC
     *
     * @return true if an object is found in the cache.
     */
    public boolean isInCache( Class clazz, String ac ) {
        return dao.isInCache( clazz, ac );
    }

    /**
     * Removes given object from the cache.
     *
     * @param obj the object to clear from the OJB cache.
     */
    public void removeFromCache( Object obj ) {
        dao.removeFromCache( obj );
    }

    /**
     * Removes given object from the cache.
     *
     * @param clazz the object type.
     * @param ac    the AC
     */
    public void removeFromCache( Class clazz, String ac ) {
        dao.removeFromCache( clazz, ac );
    }

    /**
     * Method required for writing to disk via Serializable. Needed because basic serialization does not work due to the
     * use of the Log4J logger.
     *
     * @param out The object output stream.
     *
     * @throws IOException thrown if there were problems writing to disk.
     */
    public void writeExternal( ObjectOutput out ) throws IOException {
        pr = null;
        out.writeObject( this );
    }

    /**
     * Used for serialization via Externalizable. Needed because basic serialization does not work with the Log4J
     * logger.
     *
     * @param in The object input stream.
     *
     * @throws IOException            Thrwon if there were problems reading from disk.
     * @throws ClassNotFoundException thrown if the class definition needed to create an instance is not available.
     */
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        in.readObject();
        if ( dataSource != null ) {
            pr = dataSource.getLogger();
        }

    }

    public Logger getLogger() {
        if ( pr == null && dataSource != null ) {
            pr = dataSource.getLogger();
        }

        return pr;
    }

    /**
     * Constructor - requires a datasource to be provided.
     *
     * @param source - a datasource object from which a connection could be obtained. Note that use of this constructor
     *               prevents optimisation of reflection and may result in slower response times for eg initial
     *               searches.
     *
     * @throws IntactException - thrown if either the type map or source are invalid
     */
    public IntactHelper( DAOSource source ) throws IntactException {
        this( source, source.getUser(), source.getPassword() );
    }

    /**
     * Default constructor. The user and password defaults to values given in the repository file.
     *
     * @throws IntactException - thrown if either the type map or source are invalid
     */
    public IntactHelper() throws IntactException {
        try {
            DAOSource ds = DAOFactory.getDAOSource( "uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource" );
            initialize( ds, ds.getUser(), ds.getPassword() );
        }
        catch ( DataSourceException de ) {
            String msg = "intact helper: There was a problem accessing a data store";
            throw new IntactException( msg, de );
        }
    }

    /**
     * Constructor allowing a helper instance to be created with a given username and password.
     *
     * @param user     - the username to make a connection with
     * @param password - the user's password (null values allowed)
     */
    public IntactHelper( String user, String password ) throws IntactException {
        try {
            DAOSource ds = DAOFactory.getDAOSource( "uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource" );
            initialize( ds, user, password );
        }
        catch ( DataSourceException de ) {
            String msg = "intact helper: There was a problem accessing a data store";
            throw new IntactException( msg, de );
        }
    }

    /**
     * Constructor allowing a helper instance to be created with a given data source, username and password.
     *
     * @param source   - the data source to be connected to
     * @param user     - the username to make a connection with
     * @param password - the user's password (null values allowed)
     */
    public IntactHelper( DAOSource source, String user, String password ) throws IntactException {
        initialize( source, user, password );
    }

    /**
     * close the data source. NOTE: performing this operation will invalidate any outstanding transactions, and a call
     * to open() must be made before further store access can be made. Do not use this method if you wish to continue
     * data store operations in the same connection! Note that any previous data may be lost, and a subsequent call to
     * open() will result in a new connection being established to the persitent store.
     *
     * @throws IntactException if the store was unable to be closed.
     */
    public void closeStore() throws IntactException {
        try {
            dao.close();
        }
        catch ( DataSourceException dse ) {
            throw new IntactException( "failed to close data source!", dse );
        }
    }

    /**
     * Open the data source which was closed previously by {@link #closeStore()}
     *
     * @throws IntactException for any errors in opening the data source.
     */
    public void openStore() throws IntactException {
        try {
            dao.open();
        }
        catch ( DataSourceException dse ) {
            throw new IntactException( "failed to reopen the data source!", dse );
        }
    }

    /**
     * @return boolean true if a client-initiated transaction is in progress, false if not or if there is no valid
     *         connection to the datastore.
     */
    public boolean isInTransaction() {
        return dao.isActive();
    }

    /**
     * Wrapper for uk.ac.ebi.intact.persistence#DAO(Object)
     *
     * @param obj the object to check for.
     *
     * @return <code>true</code> if <code>obj</code> is persisted or it has non null primary key value (shouldn't do
     *         it). False is also returned if <code>obj</code> is null.
     */
    public boolean isPersistent( Object obj ) {
        return ( obj != null ) ? dao.isPersistent( obj ) : false;
    }

    /**
     * starts a business level transaction. This allows finer grained transaction management of business operations (eg
     * for performing a number of creates/deletes within one unit of work). You can choose, by specifying the
     * appropriate parameter, which level of transaction you want: either object level or JDBC (ie relational) level. If
     * an unkown type is supplied then the transaction type defaults to JDBC.
     *
     * @param transactionType The type of transaction you want (relational or object). Use either
     *                        BusinessConstants.OBJECT_TX or BusinessConstants.JDBC_TX.
     *
     * @throws IntactException thrown usually if a transaction is already running
     */
    public void startTransaction( int transactionType ) throws IntactException {
        // The default transaction type is JDBC.
        int txType = BusinessConstants.JDBC_TX;
        if ( transactionType == BusinessConstants.OBJECT_TX ) {
            txType = BusinessConstants.OBJECT_TX;
        }
        try {
            dao.begin( txType );
        }
        catch ( TransactionException e ) {
            throw new IntactException( "unable to start an intact transaction!", e );
        }
    }

    /**
     * Locks the given object for <b>write</b> access. <b>{@link #startTransaction(int)} must be called to prior to this
     * method.</b>
     *
     * @param cvobj the object to lock for <b>write</b> access.
     */
    public void lock( CvObject cvobj ) {
        dao.lock( cvobj );
    }

    /**
     * ends a busines transaction. This method should be used if it is required to manage business transactionas across
     * multiple operations (eg for performing a number of creates/deletes in one unit of work).
     *
     * @throws IntactException thrown usually if there is no transaction in progress
     */
    public void finishTransaction() throws IntactException {
        try {
            dao.commit();
        }
        catch ( Exception e ) {
            throw new IntactException( "unable to complete an intact transaction!", e );
        }
    }

    /**
     * unwraps a transaction. This method is of use when a "business unit of work" fails for some reason and the
     * operations within it should not affect the persistent store.
     *
     * @throws IntactException thrown usually if a transaction is not in progress
     */
    public void undoTransaction() throws IntactException {
        try {
            dao.rollback();
        }
        catch ( Exception e ) {
            throw new IntactException( "unable to undo an intact transaction!", e );
        }
    }

    /**
     * Provides the database name that is being connected to.
     *
     * @return String the database name, or an empty String if the query fails
     */
    public String getDbName() {
        return dao.getDbName();
    }

    /**
     * Provides the user name that is connecting to the DB.
     *
     * @return String the user name, or an empty String if the query fails
     *
     * @throws org.apache.ojb.broker.accesslayer.LookupException
     *                      thrown on error getting the Connection
     * @throws SQLException thrown if the metatdata can't be obtained
     */
    public String getDbUserName() throws LookupException, SQLException {
        return dao.getDbUserName();
    }


    /**
     * This method provides a create operation for intact objects.
     *
     * @param objects - a collection of intact objects to be created
     *
     * @throws IntactException - thrown if a problem arises during the creation process
     */
    public void create( Collection objects ) throws IntactException {
        try {
            dao.makePersistent( objects );
        }
        catch ( CreateException ce ) {
            String msg = "intact helper: object creation failed.. \n";
            throw new IntactException( msg, ce );
        }
        catch ( TransactionException te ) {
            String msg = "intact helper: transaction problem during object creation.. \n";
            throw new IntactException( msg, te );
        }
    }

    /**
     * This method provides a delete operation.
     *
     * @param obj -obj to be deleted
     *
     * @throws IntactException - thrown if a problem arises during the deletion
     */
    public void delete( Object obj ) throws IntactException {
        // Only delete it if it is persistent.
        if ( !isPersistent( obj ) ) {
            return;
        }
        try {
            dao.remove( obj );
        }
        catch ( TransactionException de ) {
            String msg = "intact helper: failed to delete object of type " + obj.getClass().getName();
            throw new IntactException( msg, de );
        }
    }

    /**
     * Convenience method to create a single object in persistent store.
     *
     * @param obj The object to be created
     *
     * @throws IntactException thrown if the creation failed
     */
    public void create( Object obj ) throws IntactException {
        try {
            dao.create( obj );
        }
        catch ( CreateException ce ) {
            String msg = "intact helper: single object creation failed for class " + obj.getClass().getName();
            throw new IntactException( msg, ce );
        }
    }

    /**
     * This method provides an update operation.
     *
     * @param obj -obj to be updated
     *
     * @throws IntactException - thrown if a problem arises during the update
     */
    public void update( Object obj ) throws IntactException {
        // No force updating.
        update( obj, false );
    }

    /**
     * This method forces the given object to update. This is needed because OJB does not seem to set the dirty marker
     * properly (e.g., if a collection size remains unchanged and the OJJB uses the size to work out if the object is
     * marked as dirty).
     *
     * @param obj the object to update.
     *
     * @throws IntactException - thrown if a problem arises during the update
     */
    public void forceUpdate( Object obj ) throws IntactException {
        // Force the update
        update( obj, true );
    }

    /**
     * Cancels the update for the given object.
     *
     * @param obj the object to cancel the update for.
     */
    public void cancelUpdate( Object obj ) {
        dao.removeFromCache( obj );
    }

    /*------------------------ search facilities ---------------------------------------

    //methods implemented from the SearchI interface...
    /**
    *  Not Yet Fully Implemented.
     *  @see SearchI
     */
    public Collection paramSearch( String objectType, String searchParam, String searchValue,
                                   boolean includeSubClass,
                                   boolean matchSubString ) throws IntactException {
        throw new IntactException( "Not Fully Implemented yet" );
    }


    /**
     * Not Yet Fully Implemented.
     *
     * @see SearchI
     */
    public List stringSearch( String objectType, String searchString,
                              boolean includeSubClass,
                              boolean matchSubString ) throws IntactException {
        throw new IntactException( "Not Fully Implemented yet" );
    }

    /**
     * This method provides a means of searching intact objects, within the constraints provided by the parameters to
     * the method. NB this will probably become private, and replaced for public access by paramSearch...
     *
     * @param objectType  - the object type to be searched
     * @param searchParam - the parameter to search on (eg field)
     * @param searchValue - the search value to match with the parameter
     *
     * @return Collection - the results of the search (empty Collection if no matches found)
     *
     * @throws IntactException - thrown if problems are encountered during the search process
     */
    public Collection search( String objectType, String searchParam, String searchValue ) throws IntactException {
        //now retrieve an object...
        try {

            long timer = System.currentTimeMillis();

            Collection resultList = dao.find( objectType, searchParam, searchValue );

            long tmp = System.currentTimeMillis();
            timer = tmp - timer;
            pr = getLogger();
            pr.info( "**************************************************" );
            pr.info( "intact helper: time spent in DAO find (ms): " + timer );
            pr.info( "**************************************************" );
            return resultList;
        }
        catch ( SearchException se ) {
            //return to action servlet witha forward to error page command
            String msg = "intact helper: unable to perform search operation.. \n";
            throw new IntactException( msg + "reason: " + se.getMessage(), se );
        }
    }

    /**
     * This method provides a means of searching intact objects, within the constraints provided by the parameters to
     * the method. NB this will probably become private, and replaced for public access by paramSearch...
     *
     * @param searchClass - the class to search
     * @param searchParam - the parameter to search on (eg field)
     * @param searchValue - the search value to match with the parameter
     *
     * @return Collection - the results of the search (empty Collection if no matches found)
     *
     * @throws IntactException - thrown if problems are encountered during the search process
     */
    public <T> Collection<T> search( Class<T> searchClass, String searchParam, String searchValue ) throws IntactException {
        //now retrieve an object...
        try {
            return dao.find( searchClass, searchParam, searchValue );
        }
        catch ( SearchException se ) {
            //return to action servlet witha forward to error page command
            String msg = "intact helper: unable to perform search operation.. \n";
            throw new IntactException( msg + "reason: " + se.getMessage(), se );
        }
    }

    public <T> Collection<Experiment> getExperimentsByAnnotation(String annotationAc) throws IntactException {
        try {
            Collection<Experiment> experiments = dao.findBySQL(Experiment.class.getName(),
                                                               "select e.* " +
                                                               "from ia_exp2annot e2a, ia_annotation a, ia_experiment e " +
                                                               "where a.ac = e2a.annotation_ac " +
                                                               "and e.ac = e2a.experiment_ac " +
                                                               "and a.ac = '" + annotationAc + "'");
            return experiments;
        }
        catch ( SearchException se ){
            String msg = "intact helper: unable to perform search expereriment by annotation operation.. ";
            throw new IntactException( msg + "reason: " + se.getMessage(), se );
        }
    }

    public <T> Collection<Interactor> getInteractorByAnnotation(String annotationAc) throws IntactException {
        Collection<Interactor> interactors = new ArrayList();
        Collection<Interactor> proteins = getInteractor(annotationAc, ProteinImpl.class.getName());
        interactors.addAll(proteins);

        Collection<Interactor> interactions = getInteractor(annotationAc, InteractionImpl.class.getName());
        interactors.addAll(interactions);

        return interactors;
    }

    public Collection <Interactor> getInteractor(String annotationAc, String className) throws IntactException {
        try {
        Collection<Interactor> interactors = dao.findBySQL(className,
                                                           "select i.* " +
                                                           "from ia_int2annot i2a, ia_annotation a, ia_interactor i " +
                                                           "where a.ac = i2a.annotation_ac " +
                                                           "and i.ac = i2a.interactor_ac " +
                                                           "and a.ac = '" + annotationAc + "' " +
                                                           "and objclass = '" + className + "'");
        return interactors;
        } catch ( SearchException se ){
            String msg = "intact helper: unable to perform search interactor by annotation operation.. ";
            throw new IntactException( msg + "reason: " + se.getMessage(), se );

        }
    }

    public <T> Collection<CvObject> getCvByAnnotation(String annotationAc) throws IntactException {
        Collection<CvObject> cvObjects = new ArrayList();
        cvObjects.addAll(getCvObject(annotationAc, CvAliasType.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvCellCycle.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvCellType.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvCompartment.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvComponentRole.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvDagObject.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvDatabase.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvDevelopmentalStage.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvEvidenceType.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvFeatureIdentification.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvFeatureType.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvFuzzyType.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvGoNode.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvIdentification.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvInteraction.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvInteractionType.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvInteractorType.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvJournal.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvModificationType.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvProductRole.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvProteinForm.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvReferenceQualifier.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvTissue.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvTopic.class.getName()));
        cvObjects.addAll(getCvObject(annotationAc, CvXrefQualifier.class.getName()));
        if(cvObjects.size()==0){
            System.out.println("size of interactors = 0");
        }
        return cvObjects;
    }

    public Collection<CvObject> getCvObject(String annotationAc, String className) throws IntactException {
        try{
            Collection<CvObject> cvs = dao.findBySQL(className,
                                                     "select cv.* " +
                                                             "from ia_cvobject2annot cv2a, ia_annotation a, ia_controlledvocab cv " +
                                                             "where a.ac = cv2a.annotation_ac " +
                                                             "and cv.ac = cv2a.cvobject_ac " +
                                                             "and a.ac = '" + annotationAc + "' " +
                                                             "and cv.objclass = '" + className + "'" );
            return cvs;
        }catch ( SearchException se ){
            String msg = "intact helper: unable to perform search cv by annotation operation.. ";
            throw new IntactException( msg + "reason: " + se.getMessage(), se );
        }
    }

    public <T> Collection<BioSource> getBioSourceByAnnotation(String annotationAc) throws IntactException {
        try{
            Collection<BioSource> bioSources = dao.findBySQL(BioSource.class.getName(),
                                                             "select bs.* " +
                                                                     "from ia_biosource2annot bs2a, ia_annotation a, ia_biosource bs " +
                                                                     "where a.ac = bs2a.annotation_ac " +
                                                                     "and bs.ac = bs2a.biosource_ac " +
                                                                     "and a.ac = '" + annotationAc + "'");
            return bioSources;
        } catch ( SearchException se ){
            String msg = "intact helper: unable to perform search bioSource by annotation operation.. ";
            throw new IntactException( msg + "reason: " + se.getMessage(), se );
        }
    }

    public <T> Collection<Feature> getFeatureByAnnotation(String annotationAc) throws IntactException {
        try{
            Collection<Feature> features = dao.findBySQL(BioSource.class.getName(),
                                                         "select f.* " +
                                                                 "from ia_feature2annot f2a, ia_annotation a, ia_feature f " +
                                                                 "where a.ac = f2a.annotation_ac " +
                                                                 "and f.ac = f2a.feature_ac " +
                                                                 "and a.ac = '" + annotationAc + "'");
            return features;
        }catch ( SearchException se ){
            String msg = "intact helper: unable to perform search feature by annotation operation.. ";
            throw new IntactException( msg + "reason: " + se.getMessage(), se );
        }
    }

    /**
     * Searches for objects by classname and Xref (primaryId).
     *
     * @param clazz      the class we are looking for
     * @param aPrimaryId the primaryId of the Xref
     *
     * @return a Collection of object of type clazz for which a Xref having the given primaryId has been found.
     */
    public <T> Collection<T> getObjectsByXref( Class<T> clazz,
                                        String aPrimaryId ) throws IntactException {

        // get the Xref from the database
        Collection<Xref> xrefs = this.search( Xref.class, "primaryId", aPrimaryId );
        Collection<T> results = new ArrayList<T>();

        // add all referenced objects of the searched class
        for (Xref xref : xrefs)
        {
            results.addAll(this.search(clazz, "ac", xref.getParentAc()));
        }
        return results;
    }

    /**
     * Searches for objects by classname and Xref.
     *
     * @param clazz      the class we are looking for
     * @param database   the CvDatabase of the Xref that links back to the object of type clazz
     * @param aPrimaryId the primaryId of the Xref
     *
     * @return a Collection of object of type clazz for which a Xref having the given primaryId and CvDatabase has been
     *         found.
     */
    public <T> Collection<T> getObjectsByXref( Class<T> clazz,
                                        CvDatabase database,
                                        String aPrimaryId ) throws IntactException {

        // get the Xref from the database
        Collection<Xref> xrefs = this.search( Xref.class, "primaryId", aPrimaryId );
        Collection<T> results = new ArrayList<T>();

        // add all referenced objects of the searched class
        for (Xref xref : xrefs)
        {
            // if the CvDatabase are the same (null or not), we add the parent.
            if ((null != database && database.equals(xref.getCvDatabase()))
                    ||
                    (null == database && null == xref.getCvDatabase()))
            {
                results.addAll(this.search(clazz, "ac", xref.getParentAc()));
            }
        }
        return results;
    }

    /**
     * Searches for objects by classname and Xref.
     *
     * @param clazz      the class we are looking for
     * @param database   the CvDatabase of the Xref that links back to the object of type clazz
     * @param qualifier  the CvXrefQualifier of the Xref that links back to the object of type clazz
     * @param aPrimaryId the primaryId of the Xref
     *
     * @return a Collection of object of type clazz for which a Xref having the given primaryId and CvDatabase has been
     *         found.
     */
    public <T> Collection<T> getObjectsByXref( Class<T> clazz,
                                        CvDatabase database,
                                        CvXrefQualifier qualifier,
                                        String aPrimaryId ) throws IntactException {

        // get the Xref from the database
        Collection<Xref> xrefs = this.search( Xref.class, "primaryId", aPrimaryId );
        Collection<T> results = new ArrayList<T>();

        // add all referenced objects of the searched class
        for (Xref xref : xrefs)
        {
            // if the CvDatabase are the same (null or not), we add the parent.
            if ((null != database && database.equals(xref.getCvDatabase()))
                    ||
                    (null == database && null == xref.getCvDatabase()))
            {

                if ((null != qualifier && qualifier.equals(xref.getCvXrefQualifier()))
                        ||
                        (null == qualifier && null == xref.getCvXrefQualifier()))
                {

                    results.addAll(this.search(clazz, "ac", xref.getParentAc()));
                }
            }
        }
        return results;
    }

    /**
     * Searches for a unique Object by classname and Xref. Currently this searches only by primaryId. Should search by
     * database and primaryId.
     */
    public <T> T getObjectByXref( Class<T> clazz,
                                   String aPrimaryId ) throws IntactException {

        Collection<T> results = getObjectsByXref( clazz, aPrimaryId );

        //should be unique...
        if ( results.size() > 1 ) {
            throw new IntactException( "error - more than one result returned with query by "
                                       + aPrimaryId + " " );
        } else {
            if ( results.isEmpty() ) {
                return null;
            }
            Iterator<T> it = results.iterator();
            return it.next();
        }
    }

    /**
     * Searches for a unique Object by classname and primary id.
     *
     * @param clazz     the search class type
     * @param primaryId the primary Id
     *
     * @return the object for given primary id or null if there is no object found.
     */
    public <T> T getObjectByPrimaryId( Class<T> clazz, String primaryId ) {
        Criteria crit = new Criteria();
        crit.addEqualTo( "xrefs.primaryId", primaryId );
        return (T) getObjectByQuery( QueryFactory.newQuery( clazz, crit ) );
    }

    public Collection getObjectByAnnotation(String annotationAc) throws SearchException {

        //An annotation can annotate : an Experiment, a BioSource, an Interactor (Polymer, Interaction...), a Feature,
        // or a ControlledVocab.

        Collection annotatedObjects = new ArrayList();

        //Experiment
        Collection experiments = dao.findBySQL(Experiment.class.getName(),"select * " +
                "from ia_experiment e, ia_exp2annot e2a " +
                "where e2a.experiment_ac = e.ac " +
                "and e2a.annotation_ac = '" + annotationAc + "'");
        if(!experiments.isEmpty()) annotatedObjects.addAll(experiments);

        //BioSource
        Collection bioSources = dao.findBySQL(BioSource.class.getName(),"select * " +
                "from ia_biosource b, ia_biosource2annot b2a " +
                "where b2a.biosource_ac = b.ac " +
                "and b2a.annotation_ac = '" + annotationAc + "'");
        if(!bioSources.isEmpty()) annotatedObjects.addAll(bioSources);

        //Interactor
        Collection interactors = dao.findBySQL(Interactor.class.getName(),"select * " +
                "from ia_interactor i, ia_int2annot i2a " +
                "where i2a.interactor_ac = i.ac " +
                "and i2a.annotation_ac = '" + annotationAc + "'");
        if(!interactors.isEmpty()) annotatedObjects.addAll(interactors);

        //CvObject
        Collection cvs = dao.findBySQL(CvObject.class.getName(),"select * " +
                "from ia_controlledvocab cv, ia_cvobject2annot cv2a " +
                "where cv2a.interactor_ac = cv.ac " +
                "and cv2a.annotation_ac = '" + annotationAc + "'");
        if(!cvs.isEmpty()) annotatedObjects.addAll(cvs);

        //Feature
        Collection features = dao.findBySQL(Feature.class.getName(),"select * " +
                "from ia_feature f, ia_feature2annot f2a " +
                "where f2a.feature_ac = f.ac " +
                "and f2a.annotation_ac = '" + annotationAc + "'");
        if(!features.isEmpty()) annotatedObjects.addAll(features);

        return annotatedObjects;

    }


    /** Return an Object by classname and shortLabel.
     *  For efficiency, classes which are subclasses of CvObject are cached
     *  if the label is unique.
     *
     */
    public <T> T getObjectByLabel( Class<T> clazz,
                                    String label ) throws IntactException {

        /** Algorithm sketch:
         *  if (clazz is a controlled vocabulary class){
         *     if (not (clazz is cached)){
         *        load clazz into cache
         *     }
         *     return element from cache;
         *  return element from search;
         */

        T result = null;

        /*
        if (isCachedClass(clazz)){
            result = cache.get(clazz + "-" + label);
             if (null != result){
                 return result;
             }
         }
         */

        Collection<T> resultList = this.search( clazz, "shortLabel", label );

        if (!resultList.isEmpty() )
         {
            Iterator<T> i = resultList.iterator();
            result = i.next();
            if ( i.hasNext() ) {
                IntactException ie = new DuplicateLabelException( label, clazz.getName() );
                throw( ie );
            }
        }

        /*
        if (isCachedClass(clazz)){
            cache.put(clazz + "-" + label,result);
        }
        */

        return result;
    }

    /**
     * Return an Object by classname and ac.
     */
    public <T> T getObjectByAc( Class<T> clazz,
                                 String ac ) throws IntactException {

        T result = null;

        Collection<T> resultList = this.search( clazz, "ac", ac );

        if (!resultList.isEmpty() )
         {
            Iterator<T> i = resultList.iterator();
            result = i.next();
            if ( i.hasNext() ) {
                IntactException ie = new DuplicateLabelException( ac, clazz.getName() );
                throw( ie );
            }
        }

        return result;
    }

    /**
     * Searches for a BioSource given a tax ID. Only a single BioSource is found for given tax id and null values for
     * cell type and tissue.
     *
     * @param taxId The tax ID to search on - should be unique
     *
     * @return BioSource The matching BioSource object, or null if none found (for the combination of tax id, cell and
     *         tissue)
     *
     * @throws IntactException thrown if there was a search problem.
     */
    public BioSource getBioSourceByTaxId( String taxId ) throws IntactException {

        //List of biosurce objects for given tax id
        Collection<BioSource> results = search( BioSource.class, "taxId", taxId );

        // Get the biosource with null values for cell type and tisse
        //  (there is only one of them exists).
        for (BioSource biosrc : results)
        {
            if ((biosrc.getCvCellType() == null) && (biosrc.getCvTissue() == null))
            {
                return biosrc;
            }
        }
        // None found.
        return null;
    }


    /**
     * This method is used for obtaining an interactor given a specific BioSource and the subclass of Interactor that is
     * to be searched for. NB is it true that a match would be unique??
     *
     * @param clazz  the subclass of Interactor to search on
     * @param source the BioSource to search with - must be fully defined or at least AC set
     *
     * @return Collection the list of Interactors that have the given BioSource, or empty if none found
     *
     * @throws IntactException          thrown if a search problem occurs
     * @throws NullPointerException     if source or class is null
     * @throws IllegalArgumentException if the class parameter is not assignable from Interactor
     *                                  <p/>
     *                                  NB Not tested yet - BioSource data in DB required
     */
    public <T extends Interactor> Collection<T> getInteractorBySource( Class<T> clazz, BioSource source ) throws IntactException {

        if ( source == null ) {
            throw new NullPointerException( "Need a BioSource to search by BioSource!" );
        }
        if ( clazz == null ) {
            throw new NullPointerException( "Class is null for Interactor/BioSource search!" );
        }
        if ( !Interactor.class.isAssignableFrom( clazz ) ) {
            throw new IllegalArgumentException( "Cannot do Interactor search - Class "
                                                + clazz.getName() + "is not a subclass of Interactor" );
        }

        return (Collection<T>) this.search( Interactor.class, "bioSource_ac", source.getAc() ) ;

    }

    /**
     * Delete all elements in a collection.
     */
    public void deleteAllElements( Collection aCollection ) throws IntactException {
        try {
            for (Object obj : aCollection)
            {
                dao.remove(obj);
            }
        }
        catch ( TransactionException te ) {
            String msg = "intact helper: error deleting collection elements";
            throw new IntactException( msg, te );
        }
    }

    /**
     * Gets the underlying JDBC connection. This is a 'useful method' rather than a good practice one as it returns the
     * underlying DB connection (and assumes there is one). No guarantees - if you screw up the Connection you are in
     * trouble!.
     *
     * @return Connection a JDBC Connection, or null if the DAO you are using is not an OJB one.
     *
     * @throws IntactException thrown if there was a problem getting the connection
     * @see #releaseJDBCConnection()
     */
    public Connection getJDBCConnection() throws IntactException {
        if ( dao instanceof ObjectBridgeDAO ) {
            try {
                return ( (ObjectBridgeDAO) dao ).getJDBCConnection();
            } catch ( LookupException le ) {
                throw new IntactException( "Failed to get JDBC Connection!", le );
            }
        }
        return null;
    }

    /**
     * Relases the JDBC connection obtained via {@link #getJDBCConnection()}. You must call this method to release the
     * connection properly.
     */
    public void releaseJDBCConnection() throws IntactException {
        if ( dao instanceof ObjectBridgeDAO ) {
            ( (ObjectBridgeDAO) dao ).releaseJDBCConnection();
        }
    }

    /**
     * Allow the user not to know about the it's Institution, it has to be configured once in the properties file:
     * ${INTACTCORE_HOME}/config/Institution.properties and then when calling that method, the Institution is either
     * retreived or created according to its shortlabel.
     *
     * @return the Institution to which all created object will be linked.
     */
    public Institution getInstitution() throws IntactException {
        Institution institution = null;

        Properties props = PropertyLoader.load( INSTITUTION_CONFIG_FILE );
        if ( props != null ) {
            String shortlabel = props.getProperty( "Institution.shortLabel" );
            if ( shortlabel == null || shortlabel.trim().equals( "" ) ) {
                throw new IntactException( "Your institution is not properly configured, check out the configuration file:" +
                                           INSTITUTION_CONFIG_FILE + " and set 'Institution.shortLabel' correctly" );
            }

            // search for it (force it for LC as short labels must be in LC).
            shortlabel = shortlabel.trim();
            Collection<Institution> result = search( Institution.class, "shortLabel", shortlabel );

            if ( result.size() == 0 ) {
                // doesn't exist, create it
                institution = new Institution( shortlabel );

                String fullname = props.getProperty( "Institution.fullName" );
                if ( fullname != null ) {
                    fullname = fullname.trim();
                    if ( !fullname.equals( "" ) ) {
                        institution.setFullName( fullname );
                    }
                }


                String lineBreak = System.getProperty( "line.separator" );
                StringBuffer address = new StringBuffer( 128 );
                String line = props.getProperty( "Institution.postalAddress.line1" );
                if ( line != null ) {
                    line = line.trim();
                    if ( !line.equals( "" ) ) {
                        address.append( line ).append( lineBreak );
                    }
                }

                line = props.getProperty( "Institution.postalAddress.line2" );
                if ( line != null ) {
                    line = line.trim();
                    if ( !line.equals( "" ) ) {
                        address.append( line ).append( lineBreak );
                    }
                }

                line = props.getProperty( "Institution.postalAddress.line3" );
                if ( line != null ) {
                    line = line.trim();
                    if ( !line.equals( "" ) ) {
                        address.append( line ).append( lineBreak );
                    }
                }

                line = props.getProperty( "Institution.postalAddress.line4" );
                if ( line != null ) {
                    line = line.trim();
                    if ( !line.equals( "" ) ) {
                        address.append( line ).append( lineBreak );
                    }
                }

                line = props.getProperty( "Institution.postalAddress.line5" );
                if ( line != null ) {
                    line = line.trim();
                    if ( !line.equals( "" ) ) {
                        address.append( line ).append( lineBreak );
                    }
                }

                if ( address.length() > 0 ) {
                    address.deleteCharAt( address.length() - 1 ); // delete the last line break;
                    institution.setPostalAddress( address.toString() );
                }

                String url = props.getProperty( "Institution.url" );
                if ( url != null ) {
                    url = url.trim();
                    if ( !url.equals( "" ) ) {
                        institution.setUrl( url );
                    }
                }

                this.create( institution );

            } else {
                // return the object found
                institution = result.iterator().next();
            }

        } else {
            throw new IntactException( "Unable to read the properties from " + INSTITUTION_CONFIG_FILE );
        }

        return institution;
    }

    /**
     * Returns the number of records likely to retrieve for given query. Only valid for OJB data access.
     *
     * @param query the query to run
     *
     * @return the number of records likely to retrieve for given query
     *
     * @throws IllegalStateException if the uderlying DAO is not the ObjectBridge DAO
     */
    public int getCountByQuery( Query query ) {
        verifyObjectBridgeDAO();
        return ( (ObjectBridgeDAO) dao ).getCountByQuery( query );
    }

    /**
     * Retrieve an object by query. Only valid for OJB data access.
     *
     * @param query the query to run
     *
     * @return the object retrieved by given query
     *
     * @throws IllegalStateException if the uderlying DAO is not the ObjectBridge DAO
     */
    public <T> T getObjectByQuery( Query query ) {
        verifyObjectBridgeDAO();
        return (T) ( (ObjectBridgeDAO) dao ).getObjectByQuery( query );
    }

    /**
     * Retrieve a collection by query. Only valid for OJB data access.
     *
     * @param query the query to run
     *
     * @return the collection retrieved by given query
     *
     * @throws IllegalStateException if the uderlying DAO is not the ObjectBridge DAO
     */
    public Collection getCollectionByQuery( Query query ) {
        verifyObjectBridgeDAO();
        return ( (ObjectBridgeDAO) dao ).getCollectionByQuery( query );
    }

    /**
     * Retrieve an iterator by query. Only valid for OJB data access.
     *
     * @param query the query to run
     *
     * @return the interator after running the given query
     *
     * @throws IllegalStateException if the uderlying DAO is not the ObjectBridge DAO
     */
    public Iterator getIteratorByReportQuery( Query query ) {
        verifyObjectBridgeDAO();
        return ( (ObjectBridgeDAO) dao ).getIteratorByReportQuery( query );
    }

    /**
     * Materializes an intact object, presumbly a proxy. Only valid for OJB data access.
     *
     * @param obj the object to materialize
     *
     * @return the materiliazed object
     *
     * @throws IllegalStateException if the uderlying DAO is not the ObjectBridge DAO
     */
    public <T extends IntactObject> T materializeIntactObject( T obj ) {
        verifyObjectBridgeDAO();
        PersistenceBroker broker = ( (ObjectBridgeDAO) dao ).getBroker();
        Identity oid = new Identity( obj, broker );
        return (T) broker.getObjectByIdentity( oid );
    }

    /**
     * A helper method to materialize an interaction as proteins are proxies. Only valid for OJB data access.
     *
     * @param interaction the interaction to materialize
     *
     * @throws IllegalStateException if the uderlying DAO is not the ObjectBridge DAO
     */
    public void materializeInteraction( Interaction interaction ) {
        for ( Iterator iter = interaction.getComponents().iterator(); iter.hasNext(); ) {
            Component comp = (Component) iter.next();
            Interactor interactor = comp.getInteractor();
            // Only do for Polymer subclasses as they are declared as dynamic proxies
            if ( Polymer.class.isAssignableFrom( interactor.getClass() ) ) {
                comp.setInteractor( (Interactor) materializeIntactObject( interactor ) );
            }
        }
    }

    /**
     * Returns the real object wrapped around the proxy for given object of IntactObjectProxy type.
     *
     * @param obj the object to return the real object from.
     *
     * @return the real object wrapped around the proxy for given object of IntactObjectProxy type; otherwise,
     *         <code>obj</code> is returned.
     */
    public static <T extends IntactObject> T getRealIntactObject( T obj ) {
        if ( IntactObjectProxy.class.isAssignableFrom( obj.getClass() ) ) {
            return (T) ( (IntactObjectProxy) obj ).getRealSubject();
        }
        return obj;
    }

    /**
     * Gives the Object classname, give the real object class name if this is a VirtualProxy class
     *
     * @param obj the object for which we request the real class name.
     *
     * @return the real class name.
     *
     * @see org.apache.ojb.broker.VirtualProxy
     */
    public static <T> Class<T> getRealClassName( T obj ) {
        Class name = null;

        if ( obj instanceof VirtualProxy ) {
            name = ( (IntactObjectProxy) obj ).getRealClassName();
        } else {
            name = obj.getClass();
        }

        return name;
    }

    /**
     * From the real className of an object, gets a displayable name.
     *
     * @param obj the object for which we want the class name to display - the object must not be null
     *
     * @return the classname to display in the view.
     */
    public static String getDisplayableClassName( Object obj ) {

        return getDisplayableClassName( getRealClassName( obj ) );
    }

    /**
     * From the real className of className, gets a displayable name.
     *
     * @param clazz the class for which we want the class name to display - the class must not be null
     *
     * @return the classname to display in the view.
     */
    public static String getDisplayableClassName( Class clazz ) {

        return getDisplayableClassName( clazz.getName() );
    }

    /**
     * From the real className of className, gets a displayable name. <br> 1. get the real class name. 2. Removes the
     * package name 3. try to remove an eventual Impl suffix
     *
     * @param name the class name for which we want the class name to display - the class must not be null
     *
     * @return the classname to display in the view.
     */
    public static String getDisplayableClassName( String name ) {

        int indexDot = name.lastIndexOf( "." );
        int indexImpl = name.lastIndexOf( "Impl" );
        if ( indexImpl != -1 ) {
            name = name.substring( indexDot + 1, indexImpl );
        } else {
            name = name.substring( indexDot + 1 );
        }

        return name;
    }

    //---------------- private helper methods ------------------------------------

    private void initialize( DAOSource source, String user, String password ) throws IntactException {
        dataSource = source;

        if ( source == null ) {
            //couldn't get a mapping from the context, so can't search!!
            String msg = "intact helper: unable to search for any objects - data source required";
            throw new IntactException( msg );
        }

        //set up a logger
        pr = dataSource.getLogger();

        //get a DAO using the supplied user details
        try {
            dao = dataSource.getDAO( user, password );
        }
        catch ( DataSourceException dse ) {
            String msg = "intact helper: There was a problem accessing a data store";
            throw new IntactException( msg, dse );
        }
    }

    private void update( Object obj, boolean force ) throws IntactException {

        try {
            if ( force ) {
                dao.forceUpdate( obj );
            } else {
                dao.update( obj );
            }
        } catch ( UpdateException ue ) {
            String msg = "intact helper: failed to perform update on class " + obj.getClass().getName();
            throw new IntactException( msg, ue );
        }
    }

    private void verifyObjectBridgeDAO() {
        if ( dao instanceof ObjectBridgeDAO ) {
            return;
        }
        throw new IllegalStateException( "Illegal method call: only valid for ObjectBridge DAO" );
    }
}
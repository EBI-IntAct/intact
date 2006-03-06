package uk.ac.ebi.intact.application.search3.searchEngine.business.dao;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.HashedMap;
import uk.ac.ebi.intact.application.search3.business.Constants;
import uk.ac.ebi.intact.application.search3.searchEngine.SearchEngineConstants;
import uk.ac.ebi.intact.application.search3.searchEngine.lucene.model.SearchObject;
import uk.ac.ebi.intact.application.search3.searchEngine.util.SearchObjectProvider;
import uk.ac.ebi.intact.application.search3.searchEngine.util.sql.SqlSearchObjectProvider;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This class provides methods to find Intact objects with an AC number and to get all search objects out of the
 * database to create an index on.
 *
 * @author Anja Friedrichsen
 * @version $Id$
 */
public class SearchDAOImpl implements SearchDAO {

    private SearchObjectProvider soProvider;

    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger( Constants.LOGGER_NAME );

    /**
     * Constructs a SearchDAOImpl object.
     *
     * @param helper an IntactHelper object
     */
    public SearchDAOImpl( IntactHelper helper ) {
        this.soProvider = new SqlSearchObjectProvider( helper );
    }


    public Object findObjectsbyAC( String ac, Class clazz ) throws IntactException {
        final IntactHelper helper = new IntactHelper();
        final Object result = helper.getObjectByAc( clazz, ac );
        try {
            helper.closeStore();
        } catch ( IntactException e ) {
            throw new IntactException( "Problems to close the IntactHelper", e );
        }
        return result;
    }

    public Map findObjectsbyACs( Map someACs ) throws IntactException {

        final IntactHelper helper = new IntactHelper();
        // Map to be returned
        final Map someResults = new HashedMap();
        final Collection expResults = new ArrayList();
        final Collection protResults = new ArrayList();
        final Collection interResults = new ArrayList();
        final Collection cvResults = new ArrayList();
        final Collection bioResults = new ArrayList();
        final IterableMap map = (IterableMap) someACs;

        // Iterate throuth the Map holding the ACs and search for the corresponding intact object.
        // Add the located object to that collection that fits to the objclass
        for ( MapIterator it = map.mapIterator(); it.hasNext(); ) {

            final String ac = (String) it.next();
            String objclass = (String) it.getValue();

            objclass = objclass.trim();

            if ( objclass.equalsIgnoreCase( "uk.ac.ebi.intact.model.ProteinImpl" ) ) {

                Object result = helper.getObjectByAc( Protein.class, ac );
                if ( result == null ) {
                    // this should not happen unless the Lucene index is not in synch with the database.
                    logger.warning( "Looking for AC:" + ac + " Type: Protein.class and no object was found. Index and database not in synch." );
                } else {
                    protResults.add( result );
                }

            } else if ( objclass.equalsIgnoreCase( "uk.ac.ebi.intact.model.InteractionImpl" ) ) {
                Object result = helper.getObjectByAc( Interaction.class, ac );
                if ( result == null ) {
                    // this should not happen unless the Lucene index is not in synch with the database.
                    logger.warning( "Looking for AC:" + ac + " Type: Interaction.class and no object was found. Index and database not in synch." );
                } else {
                    interResults.add( result );
                }
            } else if ( objclass.equalsIgnoreCase( "uk.ac.ebi.intact.model.Experiment" ) ) {
                Object result = helper.getObjectByAc( Experiment.class, ac );
                if ( result == null ) {
                    // this should not happen unless the Lucene index is not in synch with the database.
                    logger.warning( "Looking for AC:" + ac + " Type: Experiment.class and no object was found. Index and database not in synch." );
                } else {
                    expResults.add( result );
                }
            } else if ( objclass.equalsIgnoreCase( "uk.ac.ebi.intact.model.CvObject" ) ) {
                Object result = helper.getObjectByAc( CvObject.class, ac );
                if ( result == null ) {
                    // this should not happen unless the Lucene index is not in synch with the database.
                    logger.warning( "Looking for AC:" + ac + " Type: CvObject.class and no object was found. Index and database not in synch." );
                } else {
                    cvResults.add( result );
                }
            } else if ( objclass.equalsIgnoreCase( "uk.ac.ebi.intact.model.BioSource" ) ) {
                Object result = helper.getObjectByAc( BioSource.class, ac );
                if ( result == null ) {
                    // this should not happen unless the Lucene index is not in synch with the database.
                    logger.warning( "Looking for AC:" + ac + " Type: Protein.class and no object was found. Index and database not in synch." );
                } else {
                    bioResults.add( result );
                }
            } else {
                throw new IntactException( "that class(" + objclass + ") is not part of the IntAct model" );
            }
        }

        // In case the result-collection is not empty, add it to the Map.
        if ( !expResults.isEmpty() ) {
            someResults.put( SearchEngineConstants.EXPERIMENT, expResults );
        }
        if ( !interResults.isEmpty() ) {
            someResults.put( SearchEngineConstants.INTERACTION, interResults );
        }
        if ( !protResults.isEmpty() ) {
            someResults.put( SearchEngineConstants.PROTEIN, protResults );
        }
        if ( !cvResults.isEmpty() ) {
            someResults.put( SearchEngineConstants.CVOBJECT, cvResults );
        }
        if ( !bioResults.isEmpty() ) {
            someResults.put( SearchEngineConstants.BIOSOURCE, bioResults );
        }

        try {
            if ( helper != null ) {
                helper.closeStore();
            }
        } catch ( IntactException e ) {
            throw new IntactException( "Problems to close the IntactHelper", e );
        }
        return someResults;
    }

    public Collection getAllSearchObjects() throws IntactException {

        // join the collections of the different search object together to one collection
        // get first all experiments and interactions
        Collection searchObjects = CollectionUtils.union( soProvider.getAllExperiments( SearchEngineConstants.EXPERIMENT_QUERY ),
                                                          soProvider.getAllInteractions( SearchEngineConstants.INTERACTION_QUERY ) );
        // ... then add all CVs
        searchObjects = CollectionUtils.union( searchObjects, soProvider.getAllCvObjects( SearchEngineConstants.CV_OBJECT_QUERY ) );

        // ... then add all proteins
        searchObjects = CollectionUtils.union( searchObjects, soProvider.getAllProteins( SearchEngineConstants.PROTEIN_QUERY ) );

        //... and at last add all biosources
        searchObjects = CollectionUtils.union( searchObjects, soProvider.getAllBioSources( SearchEngineConstants.BIOSOURCE_QUERY ) );
        System.out.println( "\n\nThe total number of objects indexed: " + searchObjects.size() );

        return searchObjects;
    }

    public SearchObject getSearchObject( String ac, String objClass ) throws IntactException {
        return soProvider.getSearchObject( ac, objClass );
    }
}
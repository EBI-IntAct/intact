/*
 * Created by IntelliJ IDEA.
 * User: clewing
 * Date: Sep 19, 2002
 * Time: 4:38:49 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactObjectDao;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;


/**
 * Helper class for setting up/tearing down objects used for test cases.
 * Typical usage in a TestCase class would be to create a TestCaseHelper
 * in its constructor and
 * delegate setUp/tearDown calls to TestCaseHelper. Then you can call one of the
 * 'get' methods to obtain a collection of various intact object types that have been
 * created, and then use any of them at random to perform tests.
 * <p/>
 * NB This class needs careful revision to work with the new model and new
 * constructors.
 *
 * @author Chris Lewington
 */
public class TestCaseHelper {

    private static final Log log = LogFactory.getLog(TestCaseHelper.class);

    private Institution institution;
    private BioSource bio1;
    private BioSource bio2;
    private Protein prot1;
    private Protein prot2;
    private Protein prot3;
    private Interaction int1;
    private Interaction int2;
    private Interaction int3;
    private Experiment exp1;
    private Experiment exp2;
    private Component comp1;
    private Component comp2;
    private Component comp3;
    private Component comp4;
    private CvDatabase cvDb;
    private CvComponentRole compRole;
    private Xref xref1;
    private Xref xref2;

    //ArrayLists holding the different types of object created
    //more may be added over time if other objects are required
    //NB ArrayLists used because they have more useful methods thna straight Collections

    private ArrayList institutions = new ArrayList();
    private ArrayList bioSources = new ArrayList();
    private ArrayList experiments = new ArrayList();
    private ArrayList proteins = new ArrayList();
    private ArrayList interactions = new ArrayList();
    private ArrayList xrefs = new ArrayList();
    private ArrayList components = new ArrayList();

    public TestCaseHelper() throws IntactException {
    }

    //get methods for collections of various object types - will all return empty if setUp
    //has not yet been called successfully

    public ArrayList getInstitutions() {

        return institutions;
    }

    public ArrayList getBioSources() {

        return bioSources;
    }

    public ArrayList getExperiments() {

        return experiments;
    }

    public ArrayList getInteractions() {

        return interactions;
    }

    public ArrayList getProteins() {

        return proteins;
    }

    public ArrayList getXrefs() {

        return xrefs;
    }

    public ArrayList getComponents() {

        return components;
    }

    public void setUp() {

        try {

            //now need to create specific info in the DB to use for the tests...
            log.info( "building example test objects..." );

            /*
            * simple scenario:
            * - create an Institution
            * - creata some Proteins, Interactions, Xrefs and Experiments
            * - create a BioSource
            * - link them all up (eg Components/Proteins/Interactions, Xrefs in Proteins,
            *    Experiment in Interaction etc)
            * - persist everything
            *
            * two options: a) store and get back ACs, or b) set ACs artificially. Go for b) just now (if it works!)..
            */
            institution = new Institution( "Boss" );

            //NB if Institution is not to extend BasicObject, its created/updated need setting also
            institution.setFullName( "The Owner Of Everything" );
            institution.setPostalAddress( "1 AnySreet, AnyTown, AnyCountry" );
            institution.setUrl( "http://www.dummydomain.org" );

            bio1 = new BioSource( institution, "bio1", "1" );
            bio1.setFullName( "test biosource 1" );

            bio2 = new BioSource( institution, "bio2", "2" );
            bio2.setFullName( "test biosource 2" );

            exp1 = new Experiment( institution, "exp1", bio1 );
            exp1.setFullName( "test experiment 1" );

            exp2 = new Experiment( institution, "exp2", bio2 );
            exp2.setFullName( "test experiment 2" );

            CvInteractorType protType = DaoFactory.getCvObjectDao(CvInteractorType.class).getByXref( CvInteractorType.getProteinMI());
            prot1 = new ProteinImpl(institution, bio1, "prot1", protType);
            prot2 = new ProteinImpl(institution, bio1, "prot2", protType);
            prot3 = new ProteinImpl(institution, bio1, "prot3", protType);

            prot1.setFullName( "test protein 1" );
            prot1.setCrc64( "dummy 1 crc64" );
            prot2.setFullName( "test protein 2" );
            prot2.setCrc64( "dummy 2 crc64" );
            prot3.setFullName( "test protein 3" );
            prot3.setCrc64( "dummy 3 crc64" );

            //create some xrefs
            cvDb = new CvDatabase( institution, "testCvDb" );
            cvDb.setFullName( "dummy test cvdatabase" );
            xref1 = new Xref( institution, cvDb, "G0000000", "GAAAAAAA", "1.0", null );

            xref2 = new Xref( institution, cvDb, "GEEEEEEE", "GGGGGGGG", "1.0", null );

            //set up some collections to be added to later - needed for
            //some of the constructors..
            Collection experiments = new ArrayList();
            Collection components = new ArrayList();

            experiments.add( exp1 );
            CvInteractorType intType = DaoFactory.getCvObjectDao(CvInteractorType.class).getByXref( CvInteractorType.getInteractionMI());
            //needs exps, components, type, shortlabel, owner...
            //No need to set BioSource - taken from the Experiment...
            int1 = new InteractionImpl( experiments, components, null, intType, "int1", institution );
            int1.setBioSource( bio1 );

            int2 = new InteractionImpl( experiments, components, null, intType, "int2", institution );
            int2.setBioSource( bio1 );

            int3 = new InteractionImpl( experiments, components, null, intType, "int3", institution );
            int3.setBioSource( bio1 );

            int1.setFullName( "test interaction 1" );
            int1.setKD( new Float( 1 ) );

            int2.setFullName( "test interaction 2" );
            int2.setKD( new Float( 2 ) );

            int3.setFullName( "test interaction 3" );
            int3.setKD( new Float( 3 ) );

            //now link up interactions and proteins via some components..
            compRole = new CvComponentRole( institution, "role" );

            comp1 = new Component( institution, int1, prot1, compRole );
            comp1.setStoichiometry( 1 );

            comp2 = new Component( institution, int2, prot2, compRole );
            comp2.setStoichiometry( 2 );

            //needs owner, interaction, interactor, role
            comp3 = new Component( institution, int2, prot3, compRole );
            comp3.setStoichiometry( 3 );

            comp4 = new Component( institution, int1, prot2, compRole );
            comp4.setStoichiometry( 4 );

            int1.addComponent( comp1 );
            int2.addComponent( comp2 );
            int3.addComponent( comp3 );
            int2.addComponent( comp4 );
            int3.addComponent( comp4 );

            //add the Xrefs in.....
            prot1.addXref( xref1 );
            int1.addXref( xref2 );

            exp1.addXref( xref1 );
            exp1.addXref( xref2 );
            exp2.addXref( xref1 );
            exp2.addXref( xref2 );

            bio1.addXref( xref1 );
            bio1.addXref( xref2 );
            bio2.addXref( xref1 );
            bio2.addXref( xref2 );

            prot1.addXref( xref1 );
            prot1.addXref( xref2 );
            prot2.addXref( xref1 );
            prot2.addXref( xref2 );

            int1.addXref( xref1 );
            int1.addXref( xref2 );
            int2.addXref( xref1 );
            int2.addXref( xref2 );
            int3.addXref( xref1 );
            int3.addXref( xref2 );


            //store everything...
            Collection persistList = new ArrayList();
            persistList.add( institution );
            persistList.add( bio1 );
            persistList.add( bio2 );
            persistList.add( exp1 );
            persistList.add( exp2 );
            persistList.add( cvDb );
            persistList.add( compRole );
            persistList.add( xref1 );
            persistList.add( xref2 );
            persistList.add( prot1 );
            persistList.add( prot2 );
            persistList.add( prot3 );
            persistList.add( int1 );
            persistList.add( int2 );
            persistList.add( int3 );
            persistList.add( comp1 );
            persistList.add( comp2 );
            persistList.add( comp3 );
            persistList.add( comp4 );

            log.info( "saving examples to store..." );
            DaoFactory.getIntactObjectDao(IntactObject.class).persistAll( persistList );

            //now add an experiment and do an update
            log.info( "examples persisted - adding Experiments..." );
            int1.addExperiment( exp2 );
            int2.addExperiment( exp1 );
            int3.addExperiment( exp2 );

            log.info( "updating Interactions..." );
            DaoFactory.getInteractorDao().update((InteractorImpl) int1 );
            DaoFactory.getInteractorDao().update((InteractorImpl) int2 );
            DaoFactory.getInteractorDao().update((InteractorImpl) int3 );

            log.info( "example test data successfully created - executing tests..." );

            //now put the created objects into their relevant Collections
            institutions.add( institution );
            bioSources.add( bio1 );
            bioSources.add( bio2 );
            this.experiments.add( exp1 );
            this.experiments.add( exp2 );
            proteins.add( prot1 );
            proteins.add( prot2 );
            proteins.add( prot3 );
            interactions.add( int1 );
            interactions.add( int2 );
            interactions.add( int3 );
            xrefs.add( xref1 );
            xrefs.add( xref2 );
            components.add( comp1 );
            components.add( comp2 );
            components.add( comp3 );
            components.add( comp4 );

        } catch ( Exception ie ) {

            //something failed with datasource, or helper.create...
            String msg = "helper.create/update failed - see stack trace...";
            log.error( msg );
            ie.printStackTrace();

        }
    }

    public void tearDown() {

        //need to clean out the example object data from the DB...
        try {

            log.info( "tests complete - removing test data..." );
            log.info( "deleting test objects..." );

            IntactObjectDao dao = DaoFactory.getIntactObjectDao(IntactObject.class);

            //NB ORDER OF DELETION IS IMPORTANT!!...
            dao.delete( prot1 );
            dao.delete( prot2 );
            dao.delete( prot3 );
            dao.delete( int1 );
            dao.delete( int2 );
            dao.delete( int3 );

            dao.delete( exp1 );
            dao.delete( exp2 );

            dao.delete( bio1 );
            dao.delete( bio2 );

            dao.delete( comp1 );
            dao.delete( comp2 );
            dao.delete( comp3 );
            dao.delete( comp4 );

            dao.delete( xref1 );
            dao.delete( xref2 );

            dao.delete( cvDb );
            dao.delete( compRole );

            dao.delete( institution );

            log.info( "done - all example test objects removed successfully." );
        } catch ( Exception e ) {

            log.info( "problem deleteing examples from data store" );
            e.printStackTrace();
        }
    }


}

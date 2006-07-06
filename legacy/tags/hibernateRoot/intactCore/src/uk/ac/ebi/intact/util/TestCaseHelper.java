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
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Helper class for setting up/tearing down objects used for test cases.
 * Typical usage in a TestCase class would be to create a TestCaseHelper
 * in its constructor, optionally use the same IntactHelper instance, and
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


    private IntactHelper helper;

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
        helper = new IntactHelper();
    }

    /**
     * provides a way to use the same helper object that is used to create/remove the
     * example test data.
     *
     * @return IntactHelper a helper instance - never null unless class constructor failed
     */
    public IntactHelper getHelper() {

        return helper;
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
            System.out.println( "building example test objects..." );

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

            CvInteractorType protType = (CvInteractorType) helper.getObjectByPrimaryId(
                    CvInteractorType.class, CvInteractorType.getProteinMI());
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
            CvInteractorType intType = (CvInteractorType) helper.getObjectByPrimaryId(
                    CvInteractorType.class, CvInteractorType.getInteractionMI());
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

            System.out.println( "saving examples to store..." );
            helper.create( persistList );

            //now add an experiment and do an update
            System.out.println( "examples persisted - adding Experiments..." );
            int1.addExperiment( exp2 );
            int2.addExperiment( exp1 );
            int3.addExperiment( exp2 );

            System.out.println( "updating Interactions..." );
            helper.update( int1 );
            helper.update( int2 );
            helper.update( int3 );

            System.out.println( "example test data successfully created - executing tests..." );
            System.out.println();

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
            String msg = "error - helper.create/update failed - see stack trace...";
            System.out.println( msg );
            ie.printStackTrace();

        }
    }

    public void tearDown() {

        //need to clean out the example object data from the DB...
        try {

            System.out.println( "tests complete - removing test data..." );
            System.out.println( "deleting test objects..." );

            //NB ORDER OF DELETION IS IMPORTANT!!...
            helper.delete( prot1 );
            helper.delete( prot2 );
            helper.delete( prot3 );
            helper.delete( int1 );
            helper.delete( int2 );
            helper.delete( int3 );

            helper.delete( exp1 );
            helper.delete( exp2 );

            helper.delete( bio1 );
            helper.delete( bio2 );

            helper.delete( comp1 );
            helper.delete( comp2 );
            helper.delete( comp3 );
            helper.delete( comp4 );

            helper.delete( xref1 );
            helper.delete( xref2 );

            helper.delete( cvDb );
            helper.delete( compRole );

            helper.delete( institution );

            System.out.println( "done - all example test objects removed successfully." );
            System.out.println();
        } catch ( Exception e ) {

            System.out.println( "problem deleteing examples from data store" );
            e.printStackTrace();
        }
        helper = null;
    }


}

/*
 * Created by IntelliJ IDEA.
 * User: clewing
 * Date: Sep 19, 2002
 * Time: 4:38:49 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.model.*;

import java.util.*;


/**
 * Helper class for setting up/tearing down objects used for test cases.
 * Typical usage in a TestCase class would be to create a TestCaseHelper
 * in its constructor, optionally use the same IntactHelper instance, and
 * delegate setUp/tearDown calls to TestCaseHelper. Then you can call one of the
 * 'get' methods to obtain a collection of various intact object types that have been
 * created, and then use any of them at random to perform tests.
 *
 * @author Chris Lewington
 *
 */
public class TestCaseHelper {


    private DAOSource dataSource;
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

    public TestCaseHelper() throws Exception {

        //set up a helper object to handle the DB interactions
        try {

            dataSource = DAOFactory.getDAOSource("uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource");

            //set the config details, ie repository file for OJB in this case
            Map config = new HashMap();
            config.put("mappingfile", "config/repository.xml");
            dataSource.setConfig(config);

            helper = new IntactHelper(dataSource);

        }
        catch(Exception e) {

            throw new Exception("error - could not access the data source. Exception thrown was " + e.toString());
        }

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
            System.out.println("building example test objects...");

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
            institution = new Institution();

            //NB if Institution is not to extend BasicObject, its created/updated need setting also
            institution.setAc("EBITEST-111111");
            institution.setFullName("The Owner Of Everything");
            institution.setPostalAddress("1 AnySreet, AnyTown, AnyCountry");
            institution.setShortLabel("Boss");
            institution.setUrl("http://www.dummydomain.org");

            bio1 = new BioSource();
            bio1.setAc("EBITEST-111112");
            bio1.setOwnerAc(institution.getAc());
            bio1.setFullName("test biosource 1");
            bio1.setOwner(institution);
            bio1.setFullName("some kind of obscure Greek should go here");
            bio1.setShortLabel("bio1");
            bio1.setTaxId("1");

            bio2 = new BioSource();
            bio2.setAc("EBITEST-111113");
            bio2.setOwnerAc(institution.getAc());
            bio2.setFullName("test biosource 2");
            bio2.setOwner(institution);
            bio2.setFullName("more obscure Greek should go here");
            bio2.setShortLabel("bio2");
            bio2.setTaxId("2");

            exp1 = new Experiment();
            exp1.setAc("EBITEST-111114");
            exp1.setOwnerAc(institution.getAc());
            exp1.setFullName("test experiment 1");
            exp1.setShortLabel("exp1");
            exp1.setOwner(institution);
            exp1.setBioSource(bio1);

            exp2 = new Experiment();
            exp2.setAc("EBITEST-111115");
            exp2.setOwnerAc(institution.getAc());
            exp2.setFullName("test experiment 2");
            exp2.setShortLabel("exp2");
            exp2.setOwner(institution);
            exp2.setBioSource(bio2);

            prot1 = new Protein();
            prot2 = new Protein();
            prot3 = new Protein();

            prot1.setAc("EBITEST-111118");
            prot1.setOwnerAc(institution.getAc());
            prot1.setFullName("test protein 1");
            prot1.setOwner(institution);
            prot1.setShortLabel("prot1");
            prot1.setBioSource(bio1);
            prot1.setCrc64("dummy 1 crc64");
            prot2.setAc("EBITEST-111119");
            prot2.setOwnerAc(institution.getAc());
            prot2.setFullName("test protein 2");
            prot2.setOwner(institution);
            prot2.setShortLabel("prot2");
            prot2.setBioSource(bio1);
            prot2.setCrc64("dummy 2 crc64");
            prot3.setAc("EBITEST-111121");
            prot3.setOwnerAc(institution.getAc());
            prot3.setFullName("test protein 3");
            prot3.setOwner(institution);
            prot3.setShortLabel("prot3");
            prot3.setBioSource(bio1);
            prot3.setCrc64("dummy 3 crc64");

            int1 = new Interaction();
            int2 = new Interaction();
            int3 = new Interaction();

            int1.setAc("EBITEST-111122");
            int1.setOwnerAc(institution.getAc());
            int1.setBioSource(bio1);
            int1.setFullName("test interaction 1");
            int1.setOwner(institution);
            int1.setShortLabel("int1");
            int1.setKD(new Float(1));

            int2.setAc("EBITEST-111123");
            int2.setOwnerAc(institution.getAc());
            int2.setBioSource(bio1);
            int2.setFullName("test interaction 2");
            int2.setOwner(institution);
            int2.setShortLabel("int2");
            int2.setKD(new Float(2));

            int3.setAc("EBITEST-111124");
            int3.setOwnerAc(institution.getAc());
            int3.setBioSource(bio2);
            int3.setFullName("test interaction 3");
            int3.setOwner(institution);
            int3.setShortLabel("int3");
            int3.setKD(new Float(3));


            //create some xrefs and link to proteins/interactions
            cvDb = new CvDatabase();
            cvDb.setOwner(institution);
            cvDb.setShortLabel("testCvDb");
            cvDb.setFullName("dummy test cvdatabase");
            cvDb.setAc("EBITEST-999999");
            xref1 = new Xref();
            xref1.setAc("EBITEST-111116");
            xref1.setOwnerAc(institution.getAc());
            xref1.setOwner(institution);
            xref1.setPrimaryId("GOOOOOOO");
            xref1.setSecondaryId("GAAAAAAA");
            xref1.setDbRelease("1.0");
            xref1.setParentAc(prot1.getAc());
            xref1.setCvDatabase(cvDb);
            cvDb.addXref(xref1);

            xref2 = new Xref();
            xref2.setAc("EBITEST-111117");
            xref2.setOwnerAc(institution.getAc());
            xref2.setOwner(institution);
            xref2.setPrimaryId("GEEEEEEEE");
            xref2.setSecondaryId("GGGGGGGG");
            xref2.setDbRelease("1.0");
            xref2.setParentAc(int1.getAc());
            xref2.setCvDatabase(cvDb);
            cvDb.addXref(xref2);

            prot1.addXref(xref1);
            int1.addXref(xref2);

            //now link up interactions and proteins via some components..
            compRole = new CvComponentRole();
            compRole.setAc("EBITEST-999998");
            compRole.setOwner(institution);
            compRole.setShortLabel("role");
            comp1 = new Component();
            comp1.setAc("EBITEST-111125");
            comp1.setOwnerAc(institution.getAc());
            comp1.setOwner(institution);
            comp1.setStoichiometry(1);
            comp1.setInteraction(int1);
            comp1.setInteractor(prot1);
            comp1.setCvComponentRole(compRole);

            comp2 = new Component();
            comp2.setAc("EBITEST-111126");
            comp2.setOwnerAc(institution.getAc());
            comp2.setOwner(institution);
            comp2.setStoichiometry(2);
            comp2.setInteraction(int2);
            comp2.setInteractor(prot2);
            comp2.setCvComponentRole(compRole);

            comp3 = new Component();
            comp3.setAc("EBITEST-111127");
            comp3.setOwnerAc(institution.getAc());
            comp3.setOwner(institution);
            comp3.setStoichiometry(3);
            comp3.setInteraction(int2);
            comp3.setInteractor(prot3);
            comp3.setCvComponentRole(compRole);

            comp4 = new Component();
            comp4.setAc("EBITEST-111128");
            comp4.setOwner(institution);
            comp4.setStoichiometry(4);
            comp4.setInteraction(int1);
            comp4.setInteractor(prot2);
            comp4.setCvComponentRole(compRole);

            //store everything...
            Collection persistList = new ArrayList();
            persistList.add(institution);
            persistList.add(bio1);
            persistList.add(bio2);
            persistList.add(exp1);
            persistList.add(exp2);
            persistList.add(cvDb);
            persistList.add(compRole);
            persistList.add(xref1);
            persistList.add(xref2);
            persistList.add(prot1);
            persistList.add(prot2);
            persistList.add(prot3);
            persistList.add(int1);
            persistList.add(int2);
            persistList.add(int3);
            persistList.add(comp1);
            persistList.add(comp2);
            persistList.add(comp3);
            persistList.add(comp4);

            System.out.println("saving examples to store...");
            helper.create(persistList);

            //now add the link between experiments and interactions and do an update
            int1.addExperiment(exp1);
            int2.addExperiment(exp1);
            int3.addExperiment(exp2);

            helper.update(int1);
            helper.update(int2);
            helper.update(int3);

            System.out.println("example test data successfully created - executing tests...");
            System.out.println();

            //now put the created objects into their relevant Collections
            institutions.add(institution);
            bioSources.add(bio1);
            bioSources.add(bio2);
            experiments.add(exp1);
            experiments.add(exp2);
            proteins.add(prot1);
            proteins.add(prot2);
            proteins.add(prot3);
            interactions.add(int1);
            interactions.add(int2);
            interactions.add(int3);
            xrefs.add(xref1);
            xrefs.add(xref2);
            components.add(comp1);
            components.add(comp2);
            components.add(comp3);
            components.add(comp4);


        }
        catch (Exception ie) {

            //something failed with datasource, or helper.create...
            String msg = "error - helper.create/update failed - see stack trace...";
            System.out.println(msg);
            ie.printStackTrace();

        }
    }

    public void tearDown() {

        //need to clean out the example object data from the DB...
        try {

            System.out.println("tests complete - removing test data...");
            System.out.println("deleting test objects...");

            //NB ORDER OF DELETION IS IMPORTANT!!...
            helper.delete(prot1);
            helper.delete(prot2);
            helper.delete(prot3);
            helper.delete(int1);
            helper.delete(int2);
            helper.delete(int3);

            helper.delete(exp1);
            helper.delete(exp2);

            helper.delete(bio1);
            helper.delete(bio2);

            helper.delete(comp1);
            helper.delete(comp2);
            helper.delete(comp3);
            helper.delete(comp4);

            helper.delete(xref1);
            helper.delete(xref2);

            helper.delete(cvDb);
            helper.delete(compRole);

            helper.delete(institution);

            System.out.println("done - all example test objects removed successfully.");
            System.out.println();
        }
        catch(Exception e) {

            System.out.println("problem deleteing examples from data store");
            e.printStackTrace();
        }
        helper = null;
    }


}

/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.business.test;

import junit.framework.*;
import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.model.*;

//only used by zacharia's test
import javax.jdo.Query;
import javax.jdo.Extent;

import java.util.*;


/**
 * Class used for JUnit testing of intact search methods.
 *
 * @author Chris Lewington
 *
 */
public class IntactHelperTest extends TestCase {

    /**
     *  TestRunner needs the Class of an object, but can't access
     * it through a static (ie main) method unless it has a name...
     */
    private static Class CLASS = IntactHelperTest.class;

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

    public IntactHelperTest(String name) {

        super(name);

    }

    public void setUp() {

        try {

        dataSource = DAOFactory.getDAOSource("uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource");

        //set the config details, ie repository file for OJB in this case
        Map config = new HashMap();
        config.put("mappingfile", "config/repository.xml");
        dataSource.setConfig(config);

            helper = new IntactHelper(dataSource);

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
            bio1.setScientificName("some kind of obscure Greek should go here");
            bio1.setShortLabel("bio1");
            bio1.setTaxId("1");

            bio2 = new BioSource();
            bio2.setAc("EBITEST-111113");
            bio2.setOwnerAc(institution.getAc());
            bio2.setFullName("test biosource 2");
            bio2.setOwner(institution);
            bio2.setScientificName("more obscure Greek should go here");
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
            int3.addExperiment(exp1);

            helper.update(int1);
            helper.update(int2);
            helper.update(int3);

            System.out.println("example test data successfully created - executing tests...");
            System.out.println();


        }
        catch (Exception ie) {

            //something failed with datasource, or helper.create...
            String msg = "error - failed to crerate datasource OR helper.create failed - see stack trace...";
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

            fail("problem deleteing examples from data store - exception message: " + e.toString());
        }
        helper = null;
    }

    //test methods go here....
    //NB all protected, called via one test method (to avoid setUp/tearDown called between each test...

    /**
     *  tests simple (classname, param, value) search
     */
    protected void basicSearch() throws Exception {

        System.out.println("testing simple (class, param, value) search.....");
        System.out.println("searching for an example Protein...");
        System.out.println();

        Collection resultList = null;
        if (helper != null) {

            //NB assumes full java className supplied...
            resultList = helper.search(prot1.getClass().getName(), "ac", prot1.getAc());
            if(!resultList.isEmpty()) {

                System.out.println("results for testBasicSearch (expecting details for Protein EBITEST-111118)...");
                System.out.println();
                Iterator it = resultList.iterator();
                while(it.hasNext()) {
                    System.out.println(it.next().toString());
                }
                System.out.println();
            }
            else {
                System.out.println("testBasicSearch: completed with empty result set");
            }

        } else {

            fail("something failed - couldn't create a helper class to access the data!!");

        }

    }

    /**
     *  Test search by object only.
     */
    protected void objectSearch() throws Exception {

        System.out.println("testing simple search by object...");
        System.out.println("First checking for an Institution....");
        System.out.println();

        Collection resultList = null;
            if (helper != null) {

                resultList = helper.search(institution);
                if(!resultList.isEmpty()) {

                    System.out.println("results for testObjectSearch (expecting details for EBITEST-111111)...");
                    System.out.println();
                    Iterator it = resultList.iterator();
                    while(it.hasNext()) {
                        System.out.println(it.next().toString());
                    }
                    System.out.println();
                }
                else {
                    System.out.println("testObjectSearch: completed with empty result set");
                }

            }
            else {

                fail("something failed - couldn't create a helper class to access the data!!");

            }
    }

    /**
    *  Test name search, ie by fullName
    */
    protected void nameSearch() throws Exception {

        System.out.println("testing search by name (Interaction, with name 'test interaction 1')...");
        System.out.println();

        Object result = null;
        if (helper != null) {

            //for a given name and class, test the helper method..
            result = helper.getObjectByName(int1.getClass(), int1.getFullName());
            if(result != null) {

                System.out.println("results for testNameSearch (expecting details for EBITEST-111122)...");
                System.out.println();
                System.out.println(result.toString());
                System.out.println();
            }
            else {
                    System.out.println("testNameSearch: completed with no match found");
            }

        }
        else {

            fail("something failed - couldn't create a helper class to access the data!!");

        }


    }

    /**
    *  Test Experiment search using an Institution
    */
    protected void institutionSearch() throws Exception {

        System.out.println("testing search for Experiments by Institution");
        System.out.println("using Institution EBITEST-111111.....");
        System.out.println();


        Collection resultList = null;
        if (helper != null) {

            resultList = helper.getExperimentsByInstitution(institution);
            if(!resultList.isEmpty()) {

                System.out.println("results for testInstitutionSearch -");
                System.out.println("(expecting results for Experiments EBITEST-111114 and EBITEST-111115)...");
                System.out.println();
                Iterator it = resultList.iterator();
                while(it.hasNext()) {
                    System.out.println(it.next().toString());
                }
                System.out.println();
            }
            else {
                System.out.println("testInstitutiontSearch: completed with empty result set");
            }

        }
        else {

            fail("something failed - couldn't create a helper class to access the data!!");

        }


    }

    protected void collectionSearch() throws Exception {

        Collection results = null;

        System.out.println("testing search by example object containing a collection.....");
        System.out.println("example used: search for a Protein, given a single Xref...");
        System.out.println("building example Xref object, with AC = EBITEST-111116...");
        System.out.println();

        if(helper != null) {

            Xref xref = new Xref();
            xref.setAc("EBITEST-111116");

            //NB unset created/updated for searching...
            xref.setUpdated(null);
            xref.setCreated(null);

            System.out.println("building example (empty) Protein to search on.....");
            System.out.println();
            Protein prot = new Protein();

            //NB unset created/updated for searching...
            prot.setUpdated(null);
            prot.setCreated(null);

            System.out.println("adding Xref to Protein example....");
            System.out.println();
            prot.addXref(xref);
            System.out.println("done - example built, now performing search ..");
            System.out.println();

            results = helper.search(prot);

            if (results.isEmpty()) {

                //no matches found
                System.out.println("something went wrong - no match found!!");

            }
            else {

                //write results to console..
                System.out.println("search results: ");
                System.out.println("(expecting EBITEST-111118 details)....");
                Iterator it2 = results.iterator();
                while (it2.hasNext()) {

                    System.out.println(it2.next().toString());
                }
                System.out.println();
            }
        }
        else {

                fail("something failed - couldn't create a helper class to access the data!!");
        }


    }

    protected void collectionMtoNSearch() throws Exception {

        Collection results = null;

        System.out.println("testing search by example object with collection, (m:n relation)....");
        System.out.println("example used: search for an Interaction, given a single Experiment...");
        System.out.println("building example Experiment object, with AC = EBITEST-111114...");
        System.out.println();

        if(helper != null) {

            Experiment exp = new Experiment();

            //NB unset created/updated for searching...
            exp.setUpdated(null);
            exp.setCreated(null);

            exp.setAc("EBITEST-111114");
            System.out.println("building example (empty) Interaction to search on.....");
            System.out.println();
            Interaction interaction = new Interaction();

            //NB unset created/updated for searching...
            interaction.setUpdated(null);
            interaction.setCreated(null);

            System.out.println("adding Experiment to Interaction example....");
            interaction.addExperiment(exp);
            System.out.println("done - example built, now performing search ..");
            System.out.println();

            results = helper.search(interaction);

            if (results.isEmpty()) {

                //no matches found
                System.out.println("something went wrong - no match found!!");

            }
            else {

                //write results to console..
                System.out.println("search results:");
                System.out.println("(expecting details for EBITEST-111122, EBITEST-111123, EBITEST-111124)....");
                Iterator it2 = results.iterator();
                while (it2.hasNext()) {

                    System.out.println(it2.next().toString());
                }
                System.out.println();
            }
        }
        else {

            fail("something failed - couldn't create a helper class to access the data!!");
        }

    }

    protected void componentSearch() throws Exception {

        System.out.println("testing component search...(for a given Protein EBITEST-111118) ");

        //first get correct details for the example protein from the DB
        Collection results = helper.search("uk.ac.ebi.intact.model.Protein", "ac", "EBITEST-111118");
        if (results.isEmpty()) {

            //no matches found
            System.out.println("something went wrong - no match found!!");

        }
        else {

            //must only be one Protein with unique AC
            Iterator it = results.iterator();
            Object obj = null;

            if(it.hasNext()) {
               obj = it.next();
            }
            else {

                fail("testComponentSearch: could not retrieve valid details for Protein EBITEST-111118!!");
            }
            //create a dummy Component and add the Protein to it
            Component component = new Component();

            //unset the created/updated fields again!
            component.setUpdated(null);
            component.setCreated(null);

            component.setInteractor((Interactor)obj);

            results = helper.search(component);


            //now loop through the components and get the interactions from each one..
            System.out.println("search results...");
            System.out.println("(expecting details for EBITEST-111122)....");
            Iterator it2 = results.iterator();

            while (it2.hasNext()) {

                Object elem = it2.next();
                if(elem instanceof Component) {
                    Interaction inter = ((Component)elem).getInteraction();
                    if(inter != null) {
                        System.out.println(inter.toString());
                    }
                    else {

                        fail("error - retrieved Component's Interaction is null!!");
                    }
                }
                else {

                    fail("testComponentSearch: did not retrieve Components! retrieved objects of class " + elem.getClass().getName());
                }
            }
            System.out.println();
       }
    }

    /**
     * main test method - calls the others, which do all the work.
     * Using this method avoids repeated setUp/tearDown calls between test method calls.
     */
    public void testHelper() {

        try {

            basicSearch();
            objectSearch();
            nameSearch();
            institutionSearch();
            collectionSearch();
            collectionMtoNSearch();
            componentSearch();
        }
        catch(Exception e) {
            System.out.println("IntactHelperTest: test(s) failed...see stack trace.");
            e.printStackTrace();
        }
    }


    /**
     *   Returns a test suite consisting of this IntactHelperTest class
     */
    public static Test suite() {

        return new TestSuite(IntactHelperTest.class);
    }

    /**
     *  main method to run this test case as an application - useful for now until we get the
     * junit framework up and running properly.
     */
    public static void main(String[] args)
    {
        String[] testClasses = {CLASS.getName()};
        junit.textui.TestRunner.main(testClasses);
    }

}

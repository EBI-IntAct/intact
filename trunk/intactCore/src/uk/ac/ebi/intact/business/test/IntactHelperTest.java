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
import uk.ac.ebi.intact.util.*;

import java.util.*;


/**
 * Class used for JUnit testing of intact search methods.
 * NB This class needs complete revision for use with the new model
 * and the new constructors (can't any longer create empty objects).
 *
 * @author Chris Lewington
 * @version $Id$
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
            institution = new Institution("Boss");

            //NB if Institution is not to extend BasicObject, its created/updated need setting also
            institution.setFullName("The Owner Of Everything");
            institution.setPostalAddress("1 AnySreet, AnyTown, AnyCountry");
            institution.setUrl("http://www.dummydomain.org");

            bio1 = new BioSource(institution, "bio1", "1");
            bio1.setOwnerAc(institution.getAc());
            bio1.setFullName("test biosource 1");

            bio2 = new BioSource(institution, "bio2", "2");
            bio2.setOwnerAc(institution.getAc());
            bio2.setFullName("test biosource 2");

            exp1 = new Experiment(institution, "exp1", bio1);
            exp1.setOwnerAc(institution.getAc());
            exp1.setFullName("test experiment 1");

            exp2 = new Experiment(institution, "exp2", bio2);
            exp2.setOwnerAc(institution.getAc());
            exp2.setFullName("test experiment 2");

            prot1 = new Protein(institution, bio1, "prot1");
            prot2 = new Protein(institution, bio1, "prot2");
            prot3 = new Protein(institution, bio1, "prot3");

            prot1.setOwnerAc(institution.getAc());
            prot1.setFullName("test protein 1");
            prot1.setCrc64("dummy 1 crc64");
            prot2.setOwnerAc(institution.getAc());
            prot2.setFullName("test protein 2");
            prot2.setCrc64("dummy 2 crc64");
            prot3.setOwnerAc(institution.getAc());
            prot3.setFullName("test protein 3");
            prot3.setCrc64("dummy 3 crc64");

            //set up some collections to be added to later - needed for
            //some of the constructors..
            Collection experiments = new ArrayList();
            Collection components = new ArrayList();

            experiments.add(exp1);
            //needs exps, components, type, shortlabel, owner...
            //No need to set BioSource - taken from the Experiment...
            int1 = new Interaction(experiments, components, null, "int1", institution);
            int2 = new Interaction(experiments, components, null, "int2", institution);
            int3 = new Interaction(experiments, components, null, "int3", institution);

            int1.setOwnerAc(institution.getAc());
            int1.setFullName("test interaction 1");
            int1.setKD(new Float(1));

            int2.setOwnerAc(institution.getAc());
            int2.setFullName("test interaction 2");
            int2.setKD(new Float(2));

            int3.setOwnerAc(institution.getAc());
            int3.setFullName("test interaction 3");
            int3.setKD(new Float(3));


            //create some xrefs and link to proteins/interactions
            cvDb = new CvDatabase(institution, "testCvDb");
            cvDb.setFullName("dummy test cvdatabase");
            xref1 = new Xref(institution, cvDb, "G0000000", "GAAAAAAA", "1.0", null);
            xref1.setOwnerAc(institution.getAc());
            xref1.setParentAc(prot1.getAc());
            cvDb.addXref(xref1);

            xref2 = new Xref(institution, cvDb, "GEEEEEEE", "GGGGGGGG", "1.0", null);
            xref2.setOwnerAc(institution.getAc());
            xref2.setParentAc(int1.getAc());
            cvDb.addXref(xref2);

            prot1.addXref(xref1);
            int1.addXref(xref2);

            //now link up interactions and proteins via some components..
            compRole = new CvComponentRole(institution, "role");

            comp1 = new Component(institution, int1, prot1, compRole);
            comp1.setOwnerAc(institution.getAc());
            comp1.setStoichiometry(1);

            comp2 = new Component(institution, int2, prot2, compRole);
            comp2.setOwnerAc(institution.getAc());
            comp2.setStoichiometry(2);

            //needs owner, interaction, interactor, role
            comp3 = new Component(institution, int2, prot3, compRole);
            comp3.setOwnerAc(institution.getAc());
            comp3.setStoichiometry(3);

            comp4 = new Component(institution, int1, prot2, compRole);
            comp4.setStoichiometry(4);

            int1.addComponent(comp1);
            int2.addComponent(comp2);
            int3.addComponent(comp3);
            int2.addComponent(comp4);
            int3.addComponent(comp4);


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
            helper.startTransaction(BusinessConstants.JDBC_TX);
            System.out.println("serializing helper within a TX...");
            IntactHelper newHelper = (IntactHelper)Serializer.serializeDeserialize(helper);
            System.out.println("resetting helper connections...");
            helper = newHelper;
            helper.create(persistList);
            helper.finishTransaction();

            //now add some experiments and interactions and do an update
            helper.startTransaction(BusinessConstants.JDBC_TX);
            int2.addExperiment(exp2);
            int3.addExperiment(exp2);

            helper.update(int2);
            System.out.println("serializing helper again, within an update TX...");
            newHelper = (IntactHelper)Serializer.serializeDeserialize(helper);
            System.out.println("resetting helper connections in update TX...");
            helper = newHelper;
            helper.update(int3);
            helper.finishTransaction();

            System.out.println("example test data successfully created - executing tests...");
            System.out.println();


        }
        catch (Exception ie) {

            //something failed with datasource, or helper.create...
            String msg = "error - failed to create datasource OR helper.create failed - see stack trace...";
            System.out.println(msg);
            ie.printStackTrace();
            try{
                helper.undoTransaction();
            } catch(Exception e1) {
                System.out.println("unable to undo transaction!!");
                e1.printStackTrace();
            }

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
        try{
            helper.closeStore();
        }
        catch(IntactException e) {
            System.out.println("unable to close store on test cleanup!");
            e.printStackTrace();
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
            resultList = helper.search(prot1.getClass().getName(), "shortLabel", prot1.getShortLabel());
            if(!resultList.isEmpty()) {

                System.out.println("results for testBasicSearch (expecting details for Protein "
                        + prot1.getShortLabel() +")...");
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
//    protected void objectSearch() throws Exception {
//
//        System.out.println("testing simple search by object...");
//        System.out.println("First checking for an Institution....");
//        System.out.println();
//
//        Collection resultList = null;
//            if (helper != null) {
//
//                resultList = helper.search(institution);
//                if(!resultList.isEmpty()) {
//
//                    System.out.println("results for testObjectSearch (expecting details for "
//                            + institution.getShortLabel() +")...");
//                    System.out.println();
//                    Iterator it = resultList.iterator();
//                    while(it.hasNext()) {
//                        System.out.println(it.next().toString());
//                    }
//                    System.out.println();
//                }
//                else {
//                    System.out.println("testObjectSearch: completed with empty result set");
//                }
//
//            }
//            else {
//
//                fail("something failed - couldn't create a helper class to access the data!!");
//
//            }
//    }

    /**
    *  Test name search, ie by fullName
    */
    protected void nameSearch() throws Exception {

        System.out.println("testing search by name (Interaction, with name '"
                + int1.getFullName() + "')...");
        System.out.println();

        Object result = null;
        if (helper != null) {

            //for a given name and class, test the helper method..
            result = helper.getObjectByName(int1.getClass(), int1.getFullName());
            if(result != null) {

                System.out.println("results for testNameSearch (expecting details for "
                        + int1.getFullName() +")...");
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
        System.out.println("using Institution " + institution.getShortLabel() + ".....");
        System.out.println();


        Collection resultList = null;
        if (helper != null) {

            resultList = helper.getExperimentsByInstitution(institution);
            if(!resultList.isEmpty()) {

                System.out.println("results for testInstitutionSearch -");
                System.out.println("(expecting results for Experiments "
                        + exp1.getShortLabel() + " and " + exp2.getShortLabel() + " ...");
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

        //NB The 'example Protein' created here must of course be one we already
        //know exists - howevere we need more information than we used to require
        //due to the model changes. Thus our 'example' is just prot1, and we are
        //expecting a result with a non-null AC...
        Collection results = null;

        if(helper != null) {

            System.out.println("testing search by example object containing a collection.....");
            System.out.println("example used: search for a Protein, given a single Xref...");
            System.out.println("using example xref with primary ID "
                    + xref1.getPrimaryId() + "...");
            System.out.println();

            System.out.println("building example Protein to search on.....");
            System.out.println();

            Protein prot = new Protein(institution, bio1, "prot1");

            //NB unset created/updated for "new" object searching...
            prot.setUpdated(null);
            prot.setCreated(null);

            System.out.println("adding Xref to Protein example....");
            System.out.println();
            prot.addXref(xref1);
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
                System.out.println("(expecting Protein(s) with non-null AC)....");
                Iterator it2 = results.iterator();
                while (it2.hasNext()) {

                    System.out.println("Protein ACs found:");
                    System.out.println(((Protein)it2.next()).getAc());
                }
                System.out.println();
            }
        }
        else {

                fail("something failed - couldn't create a helper class to access the data!!");
        }

    }

    protected void collectionMtoNSearch() throws Exception {

        //NB Again we need an 'example' that we know exists and expect to find
        //one, and hence it should have something in it...

        Collection results = null;
        Collection exps = new ArrayList();
        Collection comps = new ArrayList();

        System.out.println("testing search by example object with collection, (m:n relation)....");
        System.out.println("example used: search for an Interaction, given a single Experiment...");
        System.out.println("using example Experiment object with label "
                + exp1.getShortLabel() + " ...");
        System.out.println();

        if(helper != null) {

            System.out.println("searching for interaction with label " + int1.getShortLabel());
            System.out.println();
            exps.add(exp1);
            System.out.println("building example Interaction...");
            Interaction interaction = new Interaction(exps, comps, null, "int1", institution);
            interaction.setCreated(null);
            interaction.setUpdated(null);

            results = helper.search(interaction);
            System.out.println("search returned OK...");

            if (results.isEmpty()) {

                //no matches found
                System.out.println("something went wrong - no match found!!");

            }
            else {

                //write results to console..
                System.out.println("search results:");
                System.out.println("(expecting non-null AC for interaction " + int1.getShortLabel() + ")....");
                Iterator it2 = results.iterator();
                while (it2.hasNext()) {

                    System.out.println(((Interaction)it2.next()).getAc());
                }
                System.out.println();
            }
        }
        else {

            fail("something failed - couldn't create a helper class to access the data!!");
        }

    }

    protected void componentSearch() throws Exception {

        System.out.println("testing component search...(for a given Protein with label "
                + prot2.getShortLabel() + ") ");

        //first get correct details for the example protein from the DB
        Collection results = helper.search("uk.ac.ebi.intact.model.Protein", "shortLabel", prot2.getShortLabel());
        if (results.isEmpty()) {

            //no matches found
            System.out.println("something went wrong - no match found!!");

        }
        else {

            //create a dummy Component and add the Protein to it (assumes result IS a Protein -
            //OK as that is the class we searched on.....)
            Component component = new Component(institution, int2,
                    (Protein)results.iterator().next(), compRole);

            //unset the created/updated fields for 'example object' search
            component.setUpdated(null);
            component.setCreated(null);

            results = helper.search(component);

            //now loop through the components and get the interactions from each one..
            System.out.println("search results...");
            System.out.println("(expecting component with interaction "
                    + int2.getShortLabel() + "...)");
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
     * simple check for object serialization of the helper class
     */
    protected void checkSerialization() {

        System.out.println();
        IntactHelper dummy = null;
        System.out.println("Testing serialization of helper object...");
        dummy = (IntactHelper)Serializer.serializeDeserialize(helper);
        if(dummy != null) {
            System.out.println("helper serializes OK!");
        }
        System.out.println();
    }

    protected void checkUserValidation() {

        String username = null;
        String password = null;

        try {
            System.out.println();
            System.out.println("now checking user validation....");
            System.out.println("attempting to connect as three different users in turn, each creating an object..");

            System.out.println("checking testuser1....");
            username = "testuser1";
            password = "testuser1";

            //use a local helper to aqvoid mesing up other tests...
            IntactHelper h = new IntactHelper(dataSource, username, password);
            doUserCheck(h, "EBITEST-U1");

            System.out.println("checking testuser2....");
            username = "testuser2";
            password = "testuser2";
            h = new IntactHelper(dataSource, username, password);
            doUserCheck(h, "EBITEST-U2");

            System.out.println("checking testuser3....");
            username = "testuser3";
            password = "testuser3";
            h = new IntactHelper(dataSource, username, password);
            doUserCheck(h, "EBITEST-U3");

            System.out.println();

        }
        catch(Exception e) {
            System.out.println("failed user validation check...");
            e.printStackTrace();
        }
    }

    /**
     * Used to check out user connections
     * @param h an IntactHelper that has user details set
     * @param label An example shortLabel to use
     */
    protected void doUserCheck(IntactHelper h, String label) {

        Collection results = new ArrayList();
        try {

            Institution inst = new Institution(label);
            h.create(inst);

            //now check retrieval of the above object...
            results = h.search(Institution.class.getName(), "shortLabel", inst.getShortLabel());
            if(results.isEmpty()) {
                System.out.println("an error occurred - object created by testuser1 could not be found!!");
            }
            else {
                Iterator it = results.iterator();
                Institution found = (Institution)it.next();
                if(it.hasNext()) {
                    System.out.println("error - more than one object found that was created by testuser1!");
                }
                else {
                    System.out.println("checking for retrieved object...");
                    System.out.println("object created by user (expecting Institution with label " + label + ")");
                    System.out.println("type: " + found.getClass().getName()
                            + " AC: " + found.getAc() + " label: " + found.getShortLabel());

                    System.out.println();

                }

            }

        }
        catch(Exception e) {
            System.out.println("failed user validation check...");
            e.printStackTrace();
        }
    }

    /**
     * main test method - calls the others, which do all the work.
     * Using this method avoids repeated setUp/tearDown calls between test method calls.
     */
    public void testHelper() {

        //BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        //String answer = "n";
        try {

            basicSearch();
            //objectSearch();
            nameSearch();
            institutionSearch();
            collectionSearch();
            collectionMtoNSearch();
            componentSearch();

            checkSerialization();
/*
            System.out.println("Do you wish to run the user validation test? [y/n]");
            System.out.println("Please note that this test will FAIL if you do not have " +
                        "DB users testuser1, testuser2, testuser3 defined (all with pwd = username),"
                + " and they must have write access to the Institution table");
            answer = in.readLine();
            if(answer.equals("y")) {

                checkUserValidation();
                System.out.println("to verify the objects were created by the correct user you should examine" +
                        " the userstamp column of the Institution table in your database.");
                System.out.println();
                System.out.println("The dummy objects created for this test must be deleted manually (otherwise you could not check the userstamp!!)");
                System.out.println();
            }
*/
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

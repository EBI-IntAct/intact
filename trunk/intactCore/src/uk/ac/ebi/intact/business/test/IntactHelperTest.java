/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.business.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.business.BusinessConstants;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.Serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


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

    private IntactHelper helper;

    private Institution institution;
    private BioSource bio1;
    private BioSource bio2;
    private Protein prot1;
    private Protein prot2;
    private Protein prot3;
    private Protein prot4;
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
    private CvTopic cvTopic;
    private Xref xref1;
    private Xref xref2;
    private Annotation annot;

    public IntactHelperTest(String name) {

        super(name);

    }

    public void setUp() {

        try {

            helper = new IntactHelper();

            //now need to create specific info to use for the tests...
            System.out.println("IntactHelper Test: building example test objects (in memory)...");

            /*
            * simple scenario:
            * - create an Institution
            * - creata some Proteins, Interactions, Xrefs and Experiments
            * - create a BioSource
            * - link them all up (eg Components/Proteins/Interactions, Xrefs in Proteins,
            *    Experiment in Interaction etc)
            */
            institution = new Institution("Boss");

            //NB if Institution is not to extend BasicObject, its created/updated need setting also
            institution.setFullName("The Owner Of Everything");
            institution.setPostalAddress("1 AnySreet, AnyTown, AnyCountry");
            institution.setUrl("http://www.dummydomain.org");

            bio1 = new BioSource(institution, "bio1", "1");
            bio1.setFullName("test biosource 1");

            bio2 = new BioSource(institution, "bio2", "2");
            bio2.setFullName("test biosource 2");

            exp1 = new Experiment(institution, "exp1", bio1);
            exp1.setFullName("test experiment 1");

            exp2 = new Experiment(institution, "exp2", bio2);
            exp2.setFullName("test experiment 2");

            CvInteractorType protType = (CvInteractorType) helper.getObjectByPrimaryId(
                    CvInteractorType.class, CvInteractorType.getProteinMI());
            prot1 = new ProteinImpl(institution, bio1, "prot1", protType);
            prot2 = new ProteinImpl(institution, bio1, "prot2", protType);
            prot3 = new ProteinImpl(institution, bio1, "prot3", protType);

            //this one will be standalone and used for a persistence check..
            prot4 = new ProteinImpl(institution, bio1, "prot4", protType);

            //An example annotation - used only for checking protein update
            cvTopic = new CvTopic(institution, "test topic");
            annot = new Annotation(institution, cvTopic);

            prot1.setFullName("test protein 1");
            prot1.setCrc64("dummy 1 crc64");
            prot2.setFullName("test protein 2");
            prot2.setCrc64("dummy 2 crc64");
            prot3.setFullName("test protein 3");
            prot3.setCrc64("dummy 3 crc64");

            //set up some collections to be added to later - needed for
            //some of the constructors..
            Collection experiments = new ArrayList();

            experiments.add(exp1);
            //needs exps, components (empty in this case), type, shortlabel, owner...
            //No need to set BioSource - taken from the Experiment...
            CvInteractorType intType = (CvInteractorType) helper.getObjectByPrimaryId(
                    CvInteractorType.class, CvInteractorType.getInteractionMI());
            int1 = new InteractionImpl(experiments, new ArrayList(), null, intType, "int1", institution);
            int2 = new InteractionImpl(experiments, new ArrayList(), null, intType, "int2", institution);
            int3 = new InteractionImpl(experiments, new ArrayList(), null, intType, "int3", institution);

            int1.setFullName("test interaction 1");
            int1.setKD(new Float(1f));

            int2.setFullName("test interaction 2");
            int2.setKD(new Float(2f));

            int3.setFullName("test interaction 3");
            int3.setKD(new Float(3f));


            //create some xrefs and link to proteins/interactions
            cvDb = new CvDatabase(institution, "testCvDb");
            cvDb.setFullName("dummy test cvdatabase");
            xref1 = new Xref(institution, cvDb, "xref1", "GAAAAAAA", "1.0", null);
            xref2 = new Xref(institution, cvDb, "xref2", "GGGGGGGG", "1.0", null);

            prot1.addXref(xref1);
            int1.addXref(xref2);

            //now link up interactions and proteins via some components..
            //NB the Components are linked to the Interactions internally - the
            //Component class adds itself to its Interaction parameter..
            //Thus from below we should have:
            //int1 has comp1, comp4
            //int2 has comp2, comp3
            //int3 has none
            compRole = new CvComponentRole(institution, "role");

            comp1 = new Component(institution, int1, prot1, compRole);
            comp1.setStoichiometry(1);

            comp2 = new Component(institution, int2, prot2, compRole);
            comp2.setStoichiometry(2);

            //needs owner, interaction, interactor, role
            comp3 = new Component(institution, int2, prot3, compRole);
            comp3.setStoichiometry(3);

            comp4 = new Component(institution, int1, prot2, compRole);
            comp4.setStoichiometry(4);

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

            System.out.println("tests complete - removing any remaining test data...");
            helper.delete(prot1);
            helper.delete(prot2);
            helper.delete(prot3);
            helper.delete(prot4);
            helper.delete(cvTopic);
            helper.delete(annot);
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

    //-------------------- persistence tests: create, update, delete etc ---------------

    /**
     * Tests object creation. All example objects constructed in the setUp method
     * are passed to the helper.create method to be persisted, using a JDBC
     * JDBC transaction (NB probably both types should be tested).
     */
    protected void create(int txType) {

        try {
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
            persistList.add(prot4); //used later for persistence check
            persistList.add(cvTopic);
            persistList.add(annot);
            persistList.add(int1);
            persistList.add(int2);
            persistList.add(int3);
            persistList.add(comp1);
            persistList.add(comp2);
            persistList.add(comp3);
            persistList.add(comp4);

            System.out.println("Performing create test using "
                    + this.getTxType(txType) + " transaction...");
            helper.startTransaction(txType);
//            System.out.println("trying to create institution...");
//            helper.create(institution);
            helper.create(persistList);
            helper.finishTransaction();
            System.out.println("Create transaction completed with no exceptions.");
            System.out.println("performing searches for created objects...");

            //now need to query for the objects to make sure they are there..
            if(queriesPassed("create")) System.out.println("created test objects found OK.");
            System.out.println("create test completed.");
            //components can be obtained from the interactions...
            System.out.println();
        }
        catch(IntactException ie) {

            //something failed with transaction or helper.create...
            System.out.println("error running create test - create failed!");
            ie.printStackTrace();
            try{
                helper.undoTransaction();
            } catch(Exception e1) {
                System.out.println("unable to undo transaction!!");
                e1.printStackTrace();
            }
        }

    }

    /**
     * Performs an update test (which often causes the most problems!). The test
     * will be performed agiainst the most commonly modified Intact classes -
     *  Interaction, Protein and Experiment. Note: This test is potentially huge
     * since all fields of all classes could be updated - the current test focuses
     * only on the classes mainly updated, and also on the parts of those classes
     * most commonly changed (ie some of their Collections containing in particular
     * Proxy objects).
     * Basic Structure of test:
     * 1) start a transaction;
     * 2) perform some object changes;
     * 3) re-query those objects and compare before and after details.
     *
     * IMPORTANT!! It is only possible to update an object which
     * a) is already persistent and b) has been modified with already persistent objects.
     * In other words, OJB will NOT persist any non-persistent referenced objects as part
     * of an update. This is because it does not support nested transactions.
     * @param txType  The type of transaction to use for the test - usually JDBC,
     * ODMG or 'method local'.
     */
    protected void update(int txType) {

        try{
            //now add some experiments and interactions and do an update
            System.out.println("Performing update test using "
                    + this.getTxType(txType) + " transaction...");

            //local obj refs
            Interaction interaction = null;
            Protein protein = null;
            Experiment exp = null;
            Collection results = null;

            //also want to keep track of 'before' and 'after' Collection
            //sizes because we have proxies and need to check that additions
            //do not result in existing items being overwritten
            int expsCollectionSize = 0;
            int protAnnotSize = 0;
            int intsCollectionSize = 0;
            int compSize = 0;
            int intAnnotSize = 0;
            int xrefSize = 0;

            boolean searchFailed = false; //used to print an appropriate message

            System.out.println("Starting transaction...");
            helper.startTransaction(txType);
            //NB Have to search first (INSIDE the TX) to get the objects to update -
            //This only seems to be an issue for ODMG....

            //Interaction update with Experiment, Annotation, Component, Xref
            //ie a complex update, commited in a single TX
            results = helper.search(int2.getClass().getName(), "shortLabel", "int2");
            if(!results.isEmpty()) {
                System.out.println("updating Interaction with the following objects:");
                System.out.println("Experiment, Component, Annotation, Xref...");
                interaction = (Interaction) results.iterator().next();
                //save these for later...
                expsCollectionSize = interaction.getExperiments().size();
                compSize = interaction.getComponents().size();
                intAnnotSize = interaction.getAnnotations().size();
                xrefSize = interaction.getXrefs().size();

                //add the objects, then update in one go
                interaction.addExperiment(exp2);
                interaction.addComponent(comp1);
                interaction.addAnnotation(annot);
                interaction.addXref(xref1);
                helper.update(interaction);
            }
            else {
                System.out.println("update test: no test interaction with label 'int2' in DB!");
                searchFailed = true;
            }

            //Protein update with Annotation
            results = helper.search(prot1.getClass().getName(), "shortLabel", "prot1");
            if(!results.isEmpty()) {
                System.out.println("Updating a Protein with an Annotation....");
                protein = (Protein) results.iterator().next();
                protAnnotSize = protein.getAnnotations().size();  //save for later
                protein.addAnnotation(annot);
                helper.update(protein);
            }
            else {
                System.out.println("update test: no test protein with label 'prot1' in DB!");
                searchFailed = true;
            }

            //Experiment update with Interaction
            results = helper.search(exp1.getClass().getName(), "shortLabel", "exp1");
            if(!results.isEmpty()) {
                System.out.println("Updating an Experiment with an Interaction....");
                exp = (Experiment) results.iterator().next();
                intsCollectionSize = exp.getInteractions().size();
                if(exp.getInteractions().contains(interaction)) System.out.println("WHOOPS! already there!");
                exp.addInteraction(interaction);
                helper.update(exp);
            }
            else {
                System.out.println("update test: no test experiment with label 'exp1' in DB!");
                searchFailed = true;
            }

            helper.finishTransaction();

            //check the initial searching to see if the test was performed at all
            if(searchFailed) {
                System.out.println("failed to update properly - searching returned no test objects!");
                System.out.println(" - perhaps test object creation did not work correctly?");
            }
            else {

                 System.out.println("Update Transaction completed without exceptions.");

                //now check the updated objects to see if the updates happened...
                System.out.println("update test: Querying for updated objects....");

                //NB this test works because the objects have equals defined - it would
                //FAIL for equality as object identity.....

                //Interaction check
                results = helper.search(int2.getClass().getName(), "shortLabel", "int2");
                System.out.println("Checking objects added to interaction, and comparing sizes..");

                //experiment addition
                System.out.println("expriments in interaction before update: " + expsCollectionSize);
                interaction = (Interaction) results.iterator().next();
                System.out.println("expriments in interaction after update: "
                        + interaction.getExperiments().size());

                //component addition
                System.out.println("components in interaction before update: " + compSize);
                interaction = (Interaction) results.iterator().next();
                System.out.println("components in interaction after update: "
                        + interaction.getComponents().size());

                //Annotation addition
                System.out.println("annotations in interaction before update: " + intAnnotSize);
                interaction = (Interaction) results.iterator().next();
                System.out.println("annotations in interaction after update: "
                        + interaction.getAnnotations().size());

                //Xref addition
                System.out.println("xrefs in interaction before update: " + xrefSize);
                interaction = (Interaction) results.iterator().next();
                System.out.println("xrefs in interaction after update: "
                        + interaction.getXrefs().size());

                boolean failed = false;
                if(!interaction.getExperiments().contains(exp2)) {
                    System.out.println("update test: interaction update with experiment failed!");
                    failed = true;
                }
                if(!interaction.getComponents().contains(comp1)) {
                    System.out.println("update test: interaction update with component failed!");
                    failed = true;
                }
                if(!interaction.getAnnotations().contains(annot)) {
                    System.out.println("update test: interaction update with annotation failed!");
                    failed = true;
                }
                if(!interaction.getXrefs().contains(xref1)) {
                    System.out.println("update test: interaction update with xref failed!");
                    failed = true;
                }
                if(!failed) System.out.println("interaction updated successfully.");

                //Protein check
                results = helper.search(prot1.getClass().getName(), "shortLabel", "prot1");
                System.out.println("Checking annot added to protein, and comparing sizes..");
                System.out.println("annots in protein before update: " + protAnnotSize);
                protein = (Protein) results.iterator().next();
                System.out.println("annots in protein after update: "
                        + protein.getAnnotations().size());
                if(!protein.getAnnotations().contains(annot)) {
                    System.out.println("update test: protein update failed!");
                }
                else {
                    System.out.println("protein updated successfully.");
                }

                //Experiment check
                results = helper.search(exp1.getClass().getName(), "shortLabel", "exp1");
                System.out.println("Checking interaction added to exp, and comparing sizes..");
                System.out.println("interactions in exp before update: " + intsCollectionSize);
                exp = (Experiment) results.iterator().next();
                System.out.println("interactions in exp after update: "
                        + exp.getInteractions().size());
                if(!exp.getInteractions().contains(interaction)) {
                    System.out.println("update test: experiment update failed!");
                }
                else {
                    System.out.println("experiment updated successfully.");
                }
        }

            System.out.println("update test completed.");
            System.out.println();
        }
        catch(IntactException ie) {

            //something failed with update
            String msg = "error running test - update failed!";
            System.out.println(msg);
            ie.printStackTrace();
            try{
                helper.undoTransaction();
            }
            catch(Exception e1) {
                System.out.println("unable to undo transaction!!");
                e1.printStackTrace();
            }
        }

    }

    protected void delete(int txType) {

        try {

            System.out.println("Performing delete test...");
            System.out.println("Performing delete test using "
                    + this.getTxType(txType) + " transaction...");
            //NB ORDER OF DELETION IS IMPORTANT!!...
            helper.startTransaction(txType);
            helper.delete(prot1);
            helper.delete(prot2);
            helper.delete(prot3);
            helper.delete(prot4);
            helper.delete(cvTopic);
            helper.delete(annot);
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
            helper.finishTransaction();

            System.out.println("Delete test completed successfully without exceptions.");
            System.out.println("Performing query on deleted objects (should be none)....");
            if(queriesPassed("delete")) System.out.println("deleted objects not found.");
            System.out.println("delete test completed.");
            System.out.println();
        }
        catch(IntactException ie) {

            String msg = "error running test - delete failed!";
            System.out.println(msg);
            ie.printStackTrace();
            try{
                helper.undoTransaction();
            }
            catch(Exception e1) {
                System.out.println("unable to undo transaction!!");
                e1.printStackTrace();
            }
        }

    }

    protected void isPersistent() {

        try {
            System.out.println("Performing persistence check test...");
            if(helper.isPersistent(prot4)) System.out.println("persistence check on persistent obj OK");
            //now remove one and check again...
            System.out.println("deleteing object and checking again...");
            helper.delete(prot4);
            if(helper.isPersistent(prot4)) {
                System.out.println("persistence check on deleted obj FAILED");
            }
            else {
                //check OK - create it again for later...
                System.out.println("object is correctly no longer persistent.");
                helper.create(prot4);
            }

            System.out.println();
        }
        catch(IntactException ie) {
            System.out.println("error performing persistence test!");
            ie.printStackTrace();
        }

    }

    /**
     * Test for removing an object from the cache. The criterion is that if
     * an object previously searched for is then removed from the cache, the
     * query time should be increased.
     */
    protected void cacheRemoval() {

        try {
            System.out.println("Performing cache removal test...");
            Collection result = helper.search(prot1.getClass().getName(), "shortLabel", "prot1");
            if(!result.isEmpty()) {
            Protein prot = (Protein)result.iterator().next();
            long stop = 0;
            long start = 0;
            long time1 = 0;
            long time2 = 0;
            //now do the testing by performing the same query, removing from
            //the cache and then doing it again
            System.out.println("doing cached search..");
            start = System.currentTimeMillis();
            result = helper.search(prot4.getClass().getName(), "shortLabel", "prot1");
            prot = (Protein)result.iterator().next();
            stop = System.currentTimeMillis();
            time1 = stop - start;

            System.out.println("doing search after cache removal..");
            helper.removeFromCache(prot);
            start = System.currentTimeMillis();
            result = helper.search(prot1.getClass().getName(), "shortLabel", "prot1");
            prot = (Protein)result.iterator().next();
            stop = System.currentTimeMillis();
            time2 = stop - start;

            System.out.println("((cached time - non-cached time) > a few ms)");
            System.out.println("cached search time: " + time1);
            System.out.println("non-cached search time: " + time2);
            }
            else {
                fail("cache removal test failed - could not retrieve known example object!");
            }
            System.out.println();


        }
        catch(IntactException ie) {
            System.out.println("Error performing cache removal test!");
            ie.printStackTrace();
        }

    }


    //------------------------- various search tests ----------------------------------

    /**
     *  tests simple (classname, param, value) search
     */
    protected void basicSearch() throws Exception {

        System.out.println("Performing basic (class, param, value) search test.....");
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
    *  Test name search, ie by fullName
    */
//    protected void nameSearch() throws Exception {
//
//        System.out.println("Performing search by name test (Interaction, with name '"
//                + int1.getFullName() + "')...");
//        System.out.println();
//
//        Object result = null;
//        if (helper != null) {
//
//            //for a given name and class, test the helper method..
//            result = helper.getObjectByName(int1.getClass(), int1.getFullName());
//            if(result != null) {
//
//                System.out.println("results for testNameSearch (expecting details for "
//                        + int1.getShortLabel() +")...");
//                System.out.println();
//                System.out.println(result.toString());
//                System.out.println();
//            }
//            else {
//                    System.out.println("testNameSearch: completed with no match found");
//            }
//        }
//        else {
//
//            fail("something failed - couldn't create a helper class to access the data!!");
//        }
//    }

    /**
    *  Test Experiment search using an Institution
    */
    // This test is comented out because the method getExperimentsByInstitution is
    // not used anywhere else in the src.
//    protected void institutionSearch() throws Exception {
//
//        System.out.println("Performing Experiments by Institution search test...");
//        System.out.println("using Institution '" + institution.getShortLabel() + "'.....");
//        System.out.println();
//
//
//        Collection resultList = null;
//        if (helper != null) {
//
//            resultList = helper.getExperimentsByInstitution(institution);
//            if(!resultList.isEmpty()) {
//
//                System.out.println("results for testInstitutionSearch -");
//                System.out.println("(expecting results for Experiments "
//                        + exp1.getShortLabel() + " and " + exp2.getShortLabel() + " ...");
//                System.out.println();
//                Iterator it = resultList.iterator();
//                while(it.hasNext()) {
//                    System.out.println(it.next().toString());
//                }
//                System.out.println();
//            }
//            else {
//                System.out.println("testInstitutiontSearch: completed with empty result set");
//            }
//        }
//        else {
//
//            fail("something failed - couldn't create a helper class to access the data!!");
//        }
//    }



    //-------------------- miscellaneous tests ------------------------------

    /**
     * simple check for object serialization of the helper class, and also that you can
     * still use it after you get it back again..
     */
    protected void checkSerialization() {

        System.out.println();
        IntactHelper dummy = null;
        System.out.println("Testing serialization of helper object...");
        dummy = (IntactHelper)Serializer.serializeDeserialize(helper);
        if(dummy != null) {
            System.out.println("helper serializes OK!");
        }
        System.out.println("Now checking you can still search with the rebuilt helper....");
        try {
        Collection results = dummy.search(Institution.class.getName(), "ac", "*");
            if(!results.isEmpty()) {
                System.out.println("found the following:");
                for(Iterator it = results.iterator(); it.hasNext();) {
                    System.out.println(it.next());
                }
            }
            else System.out.println("No Institutions found!!");
        }
        catch(IntactException e) {
            System.out.println("searching with deserialzed helper failed!");
            System.out.println(e.getMessage() + e.getRootCause());
            e.printStackTrace();
        }
        System.out.println();
    }

    protected void checkUserValidation() {

        String username = null;
        String password = null;

        try {
            System.out.println();
            System.out.println("Performing user validation test....");
            System.out.println("attempting to connect as three different users in turn, each creating an object..");

            System.out.println("checking testuser1....");
            username = "testuser1";
            password = "testuser1";

            //use a local helper to aqvoid mesing up other tests...
            IntactHelper h = new IntactHelper(username, password);
            doUserCheck(h, "EBITEST-U1");

            System.out.println("checking testuser2....");
            username = "testuser2";
            password = "testuser2";
            h = new IntactHelper(username, password);
            doUserCheck(h, "EBITEST-U2");

            System.out.println("checking testuser3....");
            username = "testuser3";
            password = "testuser3";
            h = new IntactHelper(username, password);
            doUserCheck(h, "EBITEST-U3");

            System.out.println();

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
             //Do two runs for the persistence checks - firstly using JDBC transactions
            //and then using ODMG transactions......

            //perform persistence checks, then searches, then deletes...
            //NB order of tests is important.

            //JDBC
            int txType = BusinessConstants.JDBC_TX;
            create(txType);
            update(txType);
            cacheRemoval();
            isPersistent();

            basicSearch();
//            nameSearch();
//            institutionSearch();

            delete(txType);

            //ODMG
            txType = BusinessConstants.OBJECT_TX;
            create(txType);
            update(txType);
            delete(txType);

            //miscellaneous...
            checkSerialization();
            System.out.println("IntactHelper test case complete.");
            System.out.println();
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

    //---------------- private helper methods ---------------------------------

    /**
     * Used to check out user connections
     * @param h an IntactHelper that has user details set
     * @param label An example shortLabel to use
     */
    private void doUserCheck(IntactHelper h, String label) {

        Collection results = new ArrayList();
        try {

            Institution inst = new Institution(label);
            h.create(inst);

            //now check retrieval of the above object...
            results = h.search(Institution.class.getName(), "shortLabel", inst.getShortLabel());
            if (results.isEmpty()) {
                System.out.println("an error occurred - object created by testuser1 could not be found!!");
            } else {
                Iterator it = results.iterator();
                Institution found = (Institution) it.next();
                if (it.hasNext()) {
                    System.out.println("error - more than one object found that was created by testuser1!");
                } else {
                    System.out.println("checking for retrieved object...");
                    System.out.println("object created by user (expecting Institution with label " + label + ")");
                    System.out.println("type: " + found.getClass().getName()
                            + " AC: " + found.getAc() + " label: " + found.getShortLabel());

                    System.out.println();

                }

            }

        } catch (Exception e) {
            System.out.println("failed user validation check...");
            e.printStackTrace();
        }
    }

    /**
     * Convenience method to get a string version of a TX type.
     * @param tx The type to check
     * @return The appropriate String, or "method local" if unknown
     */
    private String getTxType(int tx) {

            if(tx == BusinessConstants.JDBC_TX) return "JDBC";
            if(tx == BusinessConstants.OBJECT_TX ) return "ODMG";
        return "method local";

    }

    /**
     * Prints out an appropriate error message if necessary when queries on
     * objects are performed. The validity of the query result depends upon
     * the test of interest. If no errors are found, no message is printed.
     * @param testName The test of interest.
     * @return boolean true if no error messages were printed, false otherwise.
     * @throws IntactException thrown if there was a problem using the helper for searches.
     */
    private boolean queriesPassed(String testName) throws IntactException {

        Collection results = null;
        boolean checkResult = true;

        results = helper.search(institution.getClass().getName(), "shortLabel", "Boss");
        if (checkFailed(results, testName)) {
            System.out.println(getString(testName, "institution", results));
            checkResult = false;
        }

        results = helper.search(bio1.getClass().getName(), "shortLabel", "bio1");
        if (checkFailed(results, testName)) {
            System.out.println(getString(testName, "biosurce1", results));
            checkResult = false;
        }

        results = helper.search(bio2.getClass().getName(), "shortLabel", "bio2");
        if (checkFailed(results, testName)) {
            System.out.println(getString(testName, "biosource2", results));
            checkResult = false;
        }

        results = helper.search(exp1.getClass().getName(), "shortLabel", "exp1");
        if (checkFailed(results, testName)) {
            System.out.println(getString(testName, "experiment1", results));
            checkResult = false;
        }

        results = helper.search(exp2.getClass().getName(), "shortLabel", "exp2");
        if (checkFailed(results, testName)) {
            System.out.println(getString(testName, "experiment2", results));
            checkResult = false;
        }

        results = helper.search(cvDb.getClass().getName(), "shortLabel", "testCvDb");
        if (checkFailed(results, testName)) {
            System.out.println(getString(testName, "CvDb", results));
            checkResult = false;
        }

        results = helper.search(compRole.getClass().getName(), "shortLabel", "role");
        if (checkFailed(results, testName)) {
            System.out.println(getString(testName, "component role", results));
            checkResult = false;
        }

        results = helper.search(xref1.getClass().getName(), "primaryId", "xref1");
        if (checkFailed(results, testName)) {
            System.out.println(getString(testName, "xref1", results));
            checkResult = false;
        }

        results = helper.search(xref2.getClass().getName(), "primaryId", "xref2");
        if (checkFailed(results, testName)) {
            System.out.println(getString(testName, "xref2", results));
            checkResult = false;
        }

        results = helper.search(prot1.getClass().getName(), "shortLabel", "prot1");
        if (checkFailed(results, testName)) {
            System.out.println(getString(testName, "protein1", results));
            checkResult = false;
        }

        results = helper.search(prot2.getClass().getName(), "shortLabel", "prot2");
        if (checkFailed(results, testName)) {
            System.out.println(getString(testName, "protein2", results));
            checkResult = false;
        }

        results = helper.search(prot3.getClass().getName(), "shortLabel", "prot3");
        if (checkFailed(results, testName)) {
            System.out.println(getString(testName, "protein3", results));
            checkResult = false;
        }

        results = helper.search(prot4.getClass().getName(), "shortLabel", "prot4");
        if (checkFailed(results, testName)) {
            System.out.println(getString(testName, "protein4", results));
            checkResult = false;
        }

        results = helper.search(int1.getClass().getName(), "shortLabel", "int1");
        if (checkFailed(results, testName)) {
            System.out.println(getString(testName, "interaction1", results));
            checkResult = false;
        }
        else {
            //OK so far - now check the Components (not needed for delete test)
            //NB can't find them directly as we don't know what to search on
            if(testName != "delete")
                checkComponents((Interaction)results.iterator().next());
        }

        results = helper.search(int2.getClass().getName(), "shortLabel", "int2");
        if (checkFailed(results, testName)) {
            System.out.println(getString(testName, "interaction2", results));
            checkResult = false;
        }
        else {
            //OK so far - now check the Components (not needed for delete test)
            //NB can't find them directly as we don't know what to search on
            if(testName != "delete")
                checkComponents((Interaction)results.iterator().next());
        }

        results = helper.search(int3.getClass().getName(), "shortLabel", "int3");
        if (checkFailed(results, testName)) {
            System.out.println(getString(testName, "interaction3", results));
            checkResult = false;
        }
        else {
            //OK so far - now check the Components (not needed for delete test)
            //NB can't find them directly as we don't know what to search on
            if(testName != "delete")
                checkComponents((Interaction)results.iterator().next());
        }

        return checkResult;
    }

    /**
     * Helper method to perform certain boolean checks depending upon
     * the test required. The logic of the test is reversed because it is
     * used to generate an appropriate error message.
     * @param data The Collection of items to perform the check on
     * @param testName The name of the test we are using (currently create and delete)
     * @return true if the check FAILED, and false otherwise.
     */
    private boolean checkFailed(Collection data, String testName) {
        if(testName == "create") return(data.size() != 1);
        if(testName == "delete") return(!data.isEmpty());
        return false;
    }

    /**
     * Convenience method to provide a String to print out for error
     * messages. Just here to save lots of repetitive typing!.
     * @param testName The test of interest
     * @param className The class of interest
     * @param data The search items to which the test refers
     * @return
     */
    private String getString(String testName, String className, Collection data) {
        return testName + " failed for " + className +"! found "
                + data.size() + " objects.";

    }

    /**
     * Convenience method to check if components exist as they should. This is
     * used as part of the check in the create test. Note it is specific to the
     * created test objects.
     * @param interaction
     */
    private void checkComponents(Interaction interaction) {

        //For create:
        //int 1 has comp1, comp4
        //int2 has comp2, comp3
        //int3 has none
        Collection components = interaction.getComponents();
        if (interaction.getShortLabel() == "int1") {
            if (!components.contains(comp1)) System.out.println("int1 is missing component comp1!");
            if (!components.contains(comp4)) System.out.println("int1 is missing component comp4!");
            if (components.contains(comp2)) System.out.println("int1 contains component comp2 (it shouldn't)!");
            if (components.contains(comp3)) System.out.println("int1 contains component comp3 (it shouldn't)!");
        }

        if (interaction.getShortLabel() == "int2") {
            if (!components.contains(comp2)) System.out.println("int2 is missing component comp2!");
            if (!components.contains(comp3)) System.out.println("int2 is missing component comp3!");
            if (components.contains(comp1)) System.out.println("int2 contains component comp1 (it shouldn't)!");
            if (components.contains(comp4)) System.out.println("int2 contains component comp4 (it shouldn't)!");
        }

        if (interaction.getShortLabel() == "int3") {
            if (components.size() != 0)
                System.out.println("int3 has " + components.size()
                        + " components (it shouldn't have any)!");
        }

    }

}

package uk.ac.ebi.intact.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import uk.ac.ebi.intact.model.*;

import java.util.Collection;
import java.util.ArrayList;

/**
 * This class provides test cases to check the implementation of
 * equality for all of the intact model classes. The tests used here are relevant
 * to the new model with the more restrictive constructors. Implementation of the
 * equals methods can be complex due to the nature of relationships between
 * classes - however each equals method must satisfy the properties of an equivalence
 * relation, ie:
 * <li>reflexivity (for all x, x.equals(x))</li>
 * <li>symmetry (for all x, y, x.equals(y) and y.equals(x))</li>
 * <li>transitivity (for all x, y, z, if x.equals(y) and y.equals(z) then x.equals(z))</li>
 * More straightforward tests of the equals methods are those covering null, invalid
 * types and (if applicable) concrete types in different parts of the inheritance
 * hierarchy.
 * <p>
 * The <code>hashcode</code> of each class is also tested - the criterion is that if
 * two objects are equal then they should have the same <code>hashcode</code>.
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class EqualityTests extends TestCase {


    /**
     *  TestRunner needs the Class of an object, but can't access
     * it through a static (ie main) method unless it has a name...
     */
    private static Class CLASS = EqualityTests.class;
    /**
     * Standard objects - need three of each
     */
    Institution inst1, inst2, inst3;
    Experiment exp1, exp2, exp3;
    Annotation annot1, annot2, annot3;
    BioSource bio1, bio2, bio3;
    Interaction int1, int2, int3;
    Protein prot1, prot2, prot3;
    Xref xref1, xref2, xref3;


    /**
    * Constructor
    *
    * @param name the name of the test.
    */
    public EqualityTests(String name) throws Exception {
        super(name);
    }

    /**
     * Sets up the test fixture. Called before every test case method. This will
     * create all the necessary test objects to be checked. Three equal objects of each
     * type are created - individual test methods can then vary the object details when
     * checking for non-equality as well.
     *
     */
    protected void setUp() throws Exception {
        super.setUp();
        Collection exps = new ArrayList();
        Collection comps = new ArrayList();
        //three equal Institutions
        inst1 = new Institution("label1");
        inst2 = new Institution("label1");
        inst3 = new Institution("label1");

        //three equal Biosources
        bio1 = new BioSource(inst1, "bio1", "taxId1");
        bio2 = new BioSource(inst1, "bio1", "taxId1");
        bio3 = new BioSource(inst1, "bio1", "taxId1");

        //three equal Experiments
        exp1 = new Experiment(inst1, "exp1", bio1);
        exp2 = new Experiment(inst1, "exp1", bio1);
        exp3 = new Experiment(inst1, "exp1", bio1);

        //three equal Proteins
        prot1 = new ProteinImpl(inst1, bio1, "prot1");
        prot2 = new ProteinImpl(inst1, bio1, "prot1");
        prot3 = new ProteinImpl(inst1, bio1, "prot1");

        //three equal Xrefs -
        //NB only the CvDatabase and primaryId are checked for equality
        //(this means Xrefs with only different owners are equal!!)
        xref1 = new Xref(inst1, new CvDatabase(inst1, "db1"),
                "primaryId1", null, null, null);
        xref2 = new Xref(inst1, new CvDatabase(inst1, "db1"),
                "primaryId1", null, null, null);
        xref3 = new Xref(inst1, new CvDatabase(inst1, "db1"),
                "primaryId1", null, null, null);

        //Interactions are more complex......

        //NB Bit of a problem here - we need to build a Component to build a valid
        //Interaction, but we need the Interaction to build the Component (!!)
        //Also the Component cannot have a null Interaction, ad the Collection
        //of Components for the Interaction must be non-null and non-empty!!
        // ------- Need to relax some criteria here ---------------------
        //SOLUTION: relax constraint on Interaction and allow an empty Component
        //Collection...

        //create some valid Interactions (should be equal), then some Components,
        //then add the Components into the Interactions
        exps.add(exp1); //Interaction needs at least one Experiment
        int1 = new InteractionImpl(exps, comps,
                                   new CvInteractionType(inst1, "interaction type"),
                                   "interaction1", inst1);
        int2 = new InteractionImpl(exps, comps,
                                   new CvInteractionType(inst1, "interaction type"),
                                   "interaction1", inst1);
        int3 = new InteractionImpl(exps, comps,
                                   new CvInteractionType(inst1, "interaction type"),
                                   "interaction1", inst1);

        Component comp1 = new Component(inst1, int1, prot1,
                                        new CvComponentRole(inst1, "bait"));   //Components should not exist without an Interaction
        Component comp2 = new Component(inst1, int1, prot1,
                new CvComponentRole(inst1, "prey"));

        //now we can start filling the Interactions..
        comps.add(comp1);
        comps.add(comp2);
        int1.addComponent(comp1);
        int2.addComponent(comp1);
        int3.addComponent(comp1);
        int1.setKD(new Float(1.0));

        //add some more info that is used if it exists when checking equality..
        int1.setKD(new Float(1.0));
        int2.setKD(new Float(1.0));
        int3.setKD(new Float(1.0));

        //If Xrefs exist they are part of the AnnotatedObject equals check so we
        //need to include some....
        //NB all AnnotatedObjects may have them - for the initial setup we will
        //add the same ones into all AnnotatedObjects and leave the test methods
        //to vary the contents...
        exp1.addXref(xref1);
        exp1.addXref(xref2);
        exp1.addXref(xref3);
        exp2.addXref(xref1);
        exp2.addXref(xref2);
        exp2.addXref(xref3);
        exp3.addXref(xref1);
        exp3.addXref(xref2);
        exp3.addXref(xref3);

        bio1.addXref(xref1);
        bio1.addXref(xref2);
        bio1.addXref(xref3);
        bio2.addXref(xref1);
        bio2.addXref(xref2);
        bio2.addXref(xref3);
        bio3.addXref(xref1);
        bio3.addXref(xref2);
        bio3.addXref(xref3);

        prot1.addXref(xref1);
        prot1.addXref(xref2);
        prot1.addXref(xref3);
        prot2.addXref(xref1);
        prot2.addXref(xref2);
        prot2.addXref(xref3);
        prot3.addXref(xref1);
        prot3.addXref(xref2);
        prot3.addXref(xref3);

        int1.addXref(xref1);
        int1.addXref(xref2);
        int1.addXref(xref3);
        int2.addXref(xref1);
        int2.addXref(xref2);
        int2.addXref(xref3);
        int3.addXref(xref1);
        int3.addXref(xref2);
        int3.addXref(xref3);



    }

    /**
     * Tears down the test fixture. Called after every test case method. This will
     * remove all the test objects.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        //make all the objects null here
    }

    /**
     * checks Institution equality.
     */
    public void testInstitution() {

        //check the basics first....
        //NB don't use assertEquals here as when the test fails, the rest of
        //it is not done....
        System.out.println("checking basics for Institution..");
        if(inst1.equals(null)) System.out.println("non-null object equals null - test failed!!");
        if(inst1.equals(exp1)) System.out.println("object equals different type - test failed!!");

        checkEquivalence(inst1, inst2, inst3);
        checkHashCodes(inst1, inst2);

        //change something but keep equality, then test again
        //(including changing the order of the arguments)
        System.out.println("changing some details and order then re-checking...");
        inst1.setFullName("Institution 1");
        inst2.setFullName("Institution 1");
        inst3.setFullName("Institution 1");
        checkEquivalence(inst3, inst2, inst1);
        checkHashCodes(inst1, inst2);
        System.out.println("Finished Institution checks.");
        System.out.println();
    }

    /**
         * checks BioSource equality.
         */
        public void testBioSource() {

            //check the basics first....
            System.out.println("checking basics for BioSource..");

            if(bio1.equals(null)) System.out.println("non-null object equals null - test failed!!");
            if(bio1.equals(xref1)) System.out.println("object equals different type - test failed!!");

            //assertEquals(bio1, null);  //fail
            //assertEquals(bio1, xref1);  //fail

            checkEquivalence(bio1, bio2, bio3);
            checkHashCodes(bio1, bio3);

            //change something but keep equality, then test again
            //(including changing the order of the arguments)
            System.out.println("changing some details and order then re-checking...");
            Xref tmpXref = new Xref(inst2, new CvDatabase(inst2, "temp example DB"),
                    "tmpPrimaryId", null, "dummy string", null);
            bio1.addXref(tmpXref);
            bio2.addXref(tmpXref);
            bio3.addXref(tmpXref);
            checkEquivalence(bio2, bio1, bio3);
            checkHashCodes(bio2, bio1);
        System.out.println("Finished BioSource checks.");
        System.out.println();


        }


    /**
     * checks Experiment equality. NB Collections are checked also.
     */
    public void testExperiment() {

        //check the basics first....
        System.out.println("checking basics for Experiment..");
        if(exp1.equals(null)) System.out.println("non-null object equals null - test failed!!");
        if(exp1.equals(bio2)) System.out.println("object equals different type - test failed!!");

        //assertEquals(exp1, null);  //fail
        //assertEquals(exp1, bio2);  //fail

        checkEquivalence(exp1, exp2, exp3);
        checkHashCodes(exp2, exp3);

        //change something but keep equality, then test again
        //(including changing the order of the arguments)
        System.out.println("changing some details and order then re-checking...");
        Annotation annot = new Annotation(inst2, new CvTopic(inst2, "example topic"));
        exp1.addAnnotation(annot);
        exp2.addAnnotation(annot);
        exp3.addAnnotation(annot);
        checkEquivalence(exp3, exp2, exp1);
        checkHashCodes(inst1, inst3);
        System.out.println("Finished Experiment checks.");
        System.out.println();

    }


    /**
     * checks Interaction equality. NB this covers Collections and
     * other complex cases (eg where an Interactor is itself an Interactor)
     */
    public void testInteraction() {

        //check the basics first....
        System.out.println("checking basics for Interaction..");
        if(int1.equals(null)) System.out.println("non-null object equals null - test failed!!");
        if(int1.equals(bio2)) System.out.println("object equals different type - test failed!!");

        //assertEquals(int1, null);  //fail
        //assertEquals(int1, bio2);  //fail

        checkEquivalence(int1, int2, int3);
        checkHashCodes(int2, int3);

        //change something but keep equality, then test again
        //(including changing the order of the arguments)
        //NB a Components check would be good here....
        System.out.println("changing some details and order then re-checking...");
        int1.removeXref(xref1);
        int1.setBioSource(bio2);
        int2.removeXref(xref1);
        int2.setBioSource(bio2);
        int3.removeXref(xref1);
        int3.setBioSource(bio2);
        checkEquivalence(int2, int1, int3);
        checkHashCodes(int2, int1);
        System.out.println("Finished Interaction checks.");
        System.out.println();



    }

    /**
     * checks Protein equality.
     */
    public void testProtein() {

    //check the basics first....
        System.out.println("checking basics for Protein..");
        if(prot1.equals(null)) System.out.println("non-null object equals null - test failed!!");
        if(prot1.equals(exp2)) System.out.println("object equals different type - test failed!!");

        //assertEquals(prot1, null);  //fail
        //assertEquals(prot1, exp2);  //fail

        checkEquivalence(prot1, prot2, prot3);
        checkHashCodes(prot2, prot3);

        //change something but keep equality, then test again
        //(including changing the order of the arguments)
        //NB a Components check would be good here....
        System.out.println("changing some details and order then re-checking...");
        Annotation annot = new Annotation(inst3, new CvTopic(inst3, "another example topic"));
        prot1.addAnnotation(annot);
        prot2.addAnnotation(annot);
        prot3.addAnnotation(annot);
        checkEquivalence(prot2, prot1, prot3);
        checkHashCodes(prot2, prot1);
        System.out.println("Finished Protein checks.");
        System.out.println();


    }

    /**
     * checks Xref equality.
     */
    public void testXrefs() {

        //check the basics first....
        System.out.println("checking basics for Xref..");
        if(xref1.equals(null)) System.out.println("non-null object equals null - test failed!!");
        if(xref1.equals(bio1)) System.out.println("object equals different type - test failed!!");

        //assertEquals(xref1, null);  //fail
        //assertEquals(xref1, bio1);  //fail

        checkEquivalence(xref1, xref2, xref3);
        checkHashCodes(xref1, xref2);

        //change something but keep equality, then test again
        //(including changing the order of the arguments)
        //NB a Components check would be good here....
        System.out.println("changing some details and order then re-checking...");
        xref2.setDbRelease("9.99");
        xref1.setDbRelease("9.99");
        xref3.setDbRelease("9.99");
        checkEquivalence(xref2, xref1, xref3);
        checkHashCodes(xref2, xref1);
        System.out.println("Finished Xref checks.");
        System.out.println();
    }

    /**
     * helper method to do the checks on hashcodes. The method performs
     * the following checks:
     * checks the objects are equal, then compares the two objects' hashcodes;
     * calls the first one again (for consistency);
     * compares the two objects' codes again (for consistency)
     * @param obj1 First object of the test
     * @param obj2 Second object of the test
     */
    private void checkHashCodes(Object obj1, Object obj2) {

        System.out.println("checking hashcode...");
        assertEquals(obj1, obj2);  //check the objects are equal first..

        System.out.println("hashcode equality check OK - continuing hashcode test...");
        int obj1Code = obj1.hashCode();
        int obj2Code = obj2.hashCode();
        assertEquals(obj1Code, obj2Code);
        //do it again - should get the same code AND be equal
        System.out.println("checking hashcode again for consistency...");
        int obj1CodeAgain = obj1.hashCode();
        int obj2CodeAgain = obj2.hashCode();
        assertEquals(obj1Code, obj1CodeAgain); //same code
        assertEquals(obj2Code, obj2CodeAgain); //same code
        assertEquals(obj1CodeAgain, obj2CodeAgain); //same code for equal objects

    }

    /**
     * helper method to check the equivalence relation for three given
     * objects (ie that the equality relation between them does in fact satisfy
     * reflexivity, symmetry and transitivity). It is assumed that all parameters are
     * of the same type.
     * @param obj1 First object of the test
     * @param obj2 Second object of the test
     * @param obj3 third object to check
     */
    private void checkEquivalence(Object obj1, Object obj2, Object obj3) {

        System.out.println("checking equivalence...");
        assertEquals(obj1, obj1); //reflexive - any object will do
        System.out.println("reflexivity OK...");
        if(obj1.equals(obj2)) {
            assertEquals(obj2, obj1);
            System.out.println("symmetry OK...");
        }
        else {
            System.out.println("Equality (symmetry) Failure! The objects:");
            System.out.println(obj1.getClass().getName() + ": " +obj1);
            System.out.println(" and ");
            System.out.println(obj2.getClass().getName() + ": " +obj2);
            System.out.println(" are not 'equal'!!");
            fail();
        }  //symmetric
        //if here, we know obj1 equals obj2, so first check of transitivity
        //satisfied
        if((obj2.equals(obj3))) {
            assertEquals(obj1, obj3);
            System.out.println("transitivity OK...");
        }
        else {
            System.out.println("Equality (transitivity) Failure! objects " + obj2 + " and " + obj3 +
                    " are not 'equal'!!");
            fail();
        } //transitive

    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(EqualityTests.class);
    }

    /**
     *  main method to run this test case as an application - useful for now
     */
    public static void main(String[] args)
    {
        String[] testClasses = {CLASS.getName()};
        junit.textui.TestRunner.main(testClasses);
    }
}

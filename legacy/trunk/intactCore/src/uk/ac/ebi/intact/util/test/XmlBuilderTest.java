/*
 * Created by IntelliJ IDEA.
 * User: clewington
 * Date: 06-Feb-2003
 * Time: 16:24:34
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package uk.ac.ebi.intact.util.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.w3c.dom.Document;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.BasicObject;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.util.Serializer;
import uk.ac.ebi.intact.util.TestCaseHelper;
import uk.ac.ebi.intact.util.XmlBuilder;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class provides a number of test case to exercise the XmlBuilder
 * utility class.
 *
 * @author Chris Lewington
 */
public class XmlBuilderTest extends TestCase {

    /**
     *  TestRunner needs the Class of an object, but can't access
     * it through a static (ie main) method unless it has a name...
     */
    private static Class CLASS = XmlBuilderTest.class;

    /**
     * Basic items needed for the tests
     */
    IntactHelper helper;
    TestCaseHelper testHelper;
    XmlBuilder builder;
    Object item;
    Collection searchItems;
    Document doc;
    Transformer transformer;
    StreamResult out;

    /**
     * Constructor
     *
     * @param name the name of the test.
     */
    public XmlBuilderTest(String name) throws Exception {
        super(name);
        testHelper = new TestCaseHelper();
        helper = testHelper.getHelper();
        builder = new XmlBuilder();

        searchItems = helper.search("uk.ac.ebi.intact.model.Experiment", "ac", "EBI*");

        //set up a transformer to print out the results
        System.out.println("building transformer to report results...");
        TransformerFactory tfactory = TransformerFactory.newInstance();
        transformer = tfactory.newTransformer();
        out = new StreamResult(System.out);
        System.out.println("test setup done.");

    }

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws Exception {
        super.setUp();

        //get an object to play with (Experiments in this case)
        //NB maybe we should also have a 'real' data set for testing also, specified in
        //the TestCaseHelper?? Later perhaps.....
        System.out.println("test setup done.");

    }

    /**
     * Tears down the test fixture. Called after every test case method.
     */
    protected void tearDown() throws Exception {
        System.out.println("closing down XML builder test..");
        super.tearDown();

    }

    //---------------------- test cases ---------------------------------
    public void testBuildXml() throws Exception {

        doc = builder.buildXml(searchItems);

        //just dump it for now - modify shortly to print out the details better maybe...
        System.out.println("testing XML compact build.....");
        transformer.transform(new DOMSource(doc), out);
        System.out.println();
    }

    public void testExpandDoc() throws Exception {

        System.out.println("testing document expansion....");
        List keys = new ArrayList();

        //get an example from the search results and expand it
        item = searchItems.iterator().next();
        keys.add(((BasicObject)item).getAc());
        doc = builder.buildXml(searchItems);
        Document newDoc = builder.modifyDoc(doc, keys, XmlBuilder.EXPAND_NODES);
        if(newDoc == null) {
            System.out.println("Expand test failed - null document returned");
            System.out.println("Original document to be expanded was: ");
            if(doc != null) {
                transformer.transform(new DOMSource(doc), out);
            }
            else {
                System.out.println("empty!!!");
            }
        }
        else {
            transformer.transform(new DOMSource(newDoc), out);
        }
        System.out.println();
    }

    public void testNestedExpansion() throws Exception {

            System.out.println("testing nested document expansion....");
            List keys = new ArrayList();

            //get an example from the search results and expand it
            item = searchItems.iterator().next();
            keys.add(((BasicObject)item).getAc());
            doc = builder.buildXml(searchItems);
            Document newDoc = builder.modifyDoc(doc, keys, XmlBuilder.EXPAND_NODES);
            if(newDoc == null) {
                System.out.println("Expand test failed - null document returned");
                System.out.println("Original document to be expanded was: ");
                if(doc != null) {
                    transformer.transform(new DOMSource(doc), out);
                }
                else {
                    System.out.println("empty!!!");
                }
            }
            else {

                //now do a deeper expansion  - need an AC of a nested component...
                System.out.println("getting an example nested AC - assuming we have an expanded Experiment...");
                if(item instanceof Experiment) {
                    Experiment exp = (Experiment)item;
                    Collection interactions = exp.getInteractions();

                    //get an interaction at random...
                    Interaction interaction = (Interaction)interactions.iterator().next();
                    if(interaction != null) {
                        List deepKeys = new ArrayList();
                        System.out.println("expanding Interaction with AC " + interaction.getAc());
                        deepKeys.add(interaction.getAc());
                        Document deepDoc = builder.modifyDoc(newDoc, deepKeys, XmlBuilder.EXPAND_NODES);
                        transformer.transform(new DOMSource(deepDoc), out);
                    }
                    else {
                        System.out.println("test cannot proceed - no Interactions for this experiment");
                    }
                }
                else {
                    System.out.println("This test relies on an Experiment object - the one retrieved is not an Experiment.");
                }
            }
            System.out.println();
        }


    public void testContractDoc() throws Exception {

            System.out.println("testing document contraction....");
            List keys = new ArrayList();

            //get an example from the search results and expand it, then contract it
            item = searchItems.iterator().next();
            keys.add(((BasicObject)item).getAc());
            doc = builder.buildXml(searchItems);
            Document tmpDoc = builder.modifyDoc(doc, keys, XmlBuilder.EXPAND_NODES);

            //now contract it again and then transform it
            Document newDoc = builder.modifyDoc(doc, keys, XmlBuilder.CONTRACT_NODES);

            if(newDoc == null) {
                System.out.println("Contract test failed - null document returned");
                System.out.println("Original document to be contracted was: ");
                if(doc != null) {
                    transformer.transform(new DOMSource(doc), out);
                }
                else {
                    System.out.println("empty!!!");
                }
            }
            else {
                transformer.transform(new DOMSource(newDoc), out);
            }
            System.out.println();
        }


    public void testBuildCompactElem() throws Exception {

        System.out.println("testing compact object expansion (should only get strings/primitives)...");
        //get an example from the search results and expand it
        item = searchItems.iterator().next();
        transformer.transform(new DOMSource(builder.buildCompactElem(item)), out);
        System.out.println();
    }

    public void testBuildFullElem() throws Exception {

        System.out.println("testing full object expansion (should get everything)...");
        //get an example from the search results and expand it
        item = searchItems.iterator().next();
        transformer.transform(new DOMSource(builder.buildFullElem(item)), out);
        System.out.println();
    }

    /**
     * simple check for object serialization of the XML builder class
     */
    public void testSerialization() throws Exception {

        System.out.println();
        XmlBuilder dummy = null;
        System.out.println("Testing serialization of XML Builder object...");
        dummy = (XmlBuilder)Serializer.serializeDeserialize(builder);
        if(dummy != null) {
            System.out.println("XML Builder serializes OK!");
        }
        System.out.println();
        }


    /**
     *  main method to run this test case as an application - useful to run it in isolation
     */
    public static void main(String[] args)
    {
        String[] testClasses = {CLASS.getName()};
        junit.textui.TestRunner.main(testClasses);
    }


    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(XmlBuilderTest.class);
    }
}

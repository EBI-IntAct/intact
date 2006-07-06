/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework.util.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorTopicsGenerator;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

/**
 * Test class for EditorTopicsGenerator class.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditorTopicsGeneratorTest extends TestCase {

    /**
     * Constructor with the test name.
     * @param name the name of the test.
     */
    public EditorTopicsGeneratorTest(String name) {
        super(name);
    }

    /**
     * Returns this test suite.
     */
    public static Test suite() {
        return new TestSuite(EditorTopicsGeneratorTest.class);
    }

    /**
     * Test doIt() method.
     */
    public void testDoIt() {
        // The path to access resources.
        String resPath = "../../src/uk/ac/ebi/intact/application/editor/struts/framework/util/test/";

        // The test resource.
        String testResource = "../../classes/TestEditorTopics.properties";

        EditorTopicsGenerator gen =
                new EditorTopicsGenerator("../../classes", testResource);
        try {
            // Generate the Intact Types resource.
            gen.doIt();
        }
        catch (ClassNotFoundException clnfe) {
            fail(clnfe.getMessage());
        }
        catch (IOException ioe) {
            fail(ioe.getMessage());
        }
        // The expected Intact Types resource file.
        String expResource = resPath + "ExpectedEditorTopics.properties";
        assertTrue("Didn't generate EditorTopcis resource files correctly",
                compareProperties(expResource, testResource));
    }

    private boolean compareProperties(String propName1, String propName2) {
        // The first properties to load.
        Properties prop1 = null;
        try {
            prop1 = loadProperties(propName1);
        }
        catch (IOException ioex) {
            ioex.printStackTrace();
            return false;
        }

        // The second propeties to load.
        Properties prop2 = null;
        try {
            prop2 = loadProperties(propName2);
        }
        catch (IOException ioex) {
            ioex.printStackTrace();
            return false;
        }
        // Now compare their keys; keys from prop1 must contain all the keys
        // from the second properties.
        Set key1 = prop1.keySet();
        Set key2 = prop2.keySet();
        if (!key1.containsAll(key2)) {
            return false;
        }
        // We had to reverse it as well because the above returns true when
        // key2 is a subset of key1.
        if (!key2.containsAll(key1)) {
            return false;
        }
        // Do the same process for values as well.
        Collection value1 = prop1.values();
        Collection value2 = prop2.values();
        if (!value1.containsAll(value2)) {
            return false;
        }
        // We had to reverse it as well because the above returns true when
        // value2 is a subset of value1.
        if (!value2.containsAll(value1)) {
            return false;
        }
        return true;
    }

    /**
     * Loads a properties from a file.
     *
     * @param filename the name of the file to load properties.
     * @return properties loaded from <code>filename</code>.
     *
     * @exception IOException thrown for any I/O errors.
     */
    private Properties loadProperties(String filename) throws IOException {
        // The properties to return.
        Properties prop = new Properties();

        // Load the first property from the file.
        BufferedInputStream in = null;

        try {
            in = new BufferedInputStream(new FileInputStream(filename));
            // Load from the file.
            prop.load(in);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ioex) {
                }
            }
        }
        return prop;
    }
}
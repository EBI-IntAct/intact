/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search3.searchEngine.test.D002;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import uk.ac.ebi.intact.application.search3.searchEngine.business.SearchEngineImpl;
import uk.ac.ebi.intact.application.search3.searchEngine.business.dao.SearchDAO;
import uk.ac.ebi.intact.application.search3.searchEngine.business.dao.SearchDAOImpl;
import uk.ac.ebi.intact.application.search3.searchEngine.lucene.IntactAnalyzer;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.AnnotatedObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class tests the indexing of the Xrefs for all the searchobject.
 *
 * @author Anja Friedrichsen
 * @version $Id$
 */
public class SearchXrefTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public SearchXrefTest(final String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(SearchXrefTest.class);
    }

    SearchEngineImpl engine;
    IterableMap result;

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws IntactException {


        SearchDAO dao = new SearchDAOImpl();
        engine = new SearchEngineImpl(new IntactAnalyzer(), new File("indexD002"), dao, null);
    }

    /**
     * test proteins xref
     */
    public void testProteinXRef() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("xref:P02294 AND objclass:uk.*protein*"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }

        assertNotNull(result);

        MapIterator it = result.mapIterator();
        Object key = null;
        Collection value = null;
        while (it.hasNext()) {
            key = it.next();
            value = (ArrayList) it.getValue();
            System.out.println("Key" + key + " Value: " + value.toString());
            if (key.equals("Protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Protein", key);
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-8094", anObject.getAc());
            }
        }
    }

    /**
     * test protein xref with a wildcard character
     */
    public void testProteinMultiXRef() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("xref:IPR00753* AND objclass:uk.*protein*"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }

        assertNotNull(result);

        MapIterator it = result.mapIterator();
        Object key = null;
        Collection value = null;
        while (it.hasNext()) {
            key = it.next();
            value = (ArrayList) it.getValue();
            System.out.println("Key" + key + " Value: " + value.toString());
            if (key.equals("Protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Protein", key);
                int size = 5;
                assertEquals(size, value.size());
            }
        }
    }


    /**
     * test the cvobjects xref
     */
    public void testcvobjectXRef() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("xref:10200254 AND objclass:uk.*Cv*"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }

        assertNotNull(result);

        MapIterator it = result.mapIterator();
        Object key = null;
        Collection value = null;
        while (it.hasNext()) {
            key = it.next();
            value = (ArrayList) it.getValue();
            System.out.println("Key" + key + " Value: " + value.toString());
            if (key.equals("cvobject")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("cvobject", key);
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-1202", anObject.getAc());
            }
        }
    }

    /**
     * tests the cvobjects xref with a wildcard character
     */
    public void testcvobjectMultiXRef() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("xref:AA005* AND objclass:uk.*Cv*"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }

        assertNotNull(result);

        MapIterator it = result.mapIterator();
        Object key = null;
        Collection value = null;
        while (it.hasNext()) {
            key = it.next();
            value = (ArrayList) it.getValue();
            System.out.println("Key" + key + " Value: " + value.toString());
            if (key.equals("cvobject")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("cvobject", key);
                int size = 11;
                assertEquals(size, value.size());
            }
        }
    }

    /**
     * tests the interaction xref
     */
    public void testInteractionXRef() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("xref:10610414 AND objclass:uk.*interaction*"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }

        assertNotNull(result);

        MapIterator it = result.mapIterator();
        Object key = null;
        Collection value = null;
        while (it.hasNext()) {
            key = it.next();
            value = (ArrayList) it.getValue();
            System.out.println("Key" + key + " Value: " + value.toString());
            if (key.equals("Interaction")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Interaction", key);
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-77449", anObject.getAc());
            }
        }
    }

    /**
     * tests the interaction xref with a wildcard character
     */
    public void testMultiInteractionXRef() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("xref:GO:000716* AND objclass:uk.*Interaction*"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }

        assertNotNull(result);

        MapIterator it = result.mapIterator();
        Object key = null;
        Collection value = null;
        while (it.hasNext()) {
            key = it.next();
            value = (ArrayList) it.getValue();
            System.out.println("Key" + key + " Value: " + value.toString());
            if (key.equals("Interaction")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Interaction", key);
                int size = 64;
                assertEquals(size, value.size());
            }
        }
    }


    /**
     * tests the experiment xref
     */
    public void testExperimentXRef() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("xref:GO:0016887 AND objclass:uk.*Experiment*"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }

        assertNotNull(result);

        MapIterator it = result.mapIterator();
        Object key = null;
        Collection value = null;
        while (it.hasNext()) {
            key = it.next();
            value = (ArrayList) it.getValue();
            System.out.println("Key" + key + " Value: " + value.toString());
            if (key.equals("Experiment")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Experiment", key);
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-77449", anObject.getAc());
            }
        }
    }

    /**
     * tests the Experiment xref with a wildcard character
     */
    public void testMultiExperimentXRef() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("xref:123%* AND objclass:uk.*Experiment*"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }

        assertNotNull(result);

        MapIterator it = result.mapIterator();
        Object key = null;
        Collection value = null;
        while (it.hasNext()) {
            key = it.next();
            value = (ArrayList) it.getValue();
            System.out.println("Key" + key + " Value: " + value.toString());
            if (key.equals("Experiment")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Experiment", key);
                int size = 12;
                assertEquals(size, value.size());
            }
        }
    }

    /**
     * tests the biosource xref
     */
    public void testBioSourceXRef() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("xref:10030 AND objclass:uk.*BioSource*"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }

        assertNotNull(result);

        MapIterator it = result.mapIterator();
        Object key = null;
        Collection value = null;
        while (it.hasNext()) {
            key = it.next();
            value = (ArrayList) it.getValue();
            System.out.println("Key" + key + " Value: " + value.toString());
            if (key.equals("BioSource")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("BioSource", key);
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-74399", anObject.getAc());
            }
        }
    }

    /**
     * tests the BioSource xref with a wildcard character
     */
    public void testMultiBioSourceXRef() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("xref:95* AND objclass:uk.*BioSource*"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }

        assertNotNull(result);

        MapIterator it = result.mapIterator();
        Object key = null;
        Collection value = null;
        while (it.hasNext()) {
            key = it.next();
            value = (ArrayList) it.getValue();
            System.out.println("Key" + key + " Value: " + value.toString());
            if (key.equals("BioSource")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("BioSource", key);
                int size = 12;
                assertEquals(size, value.size());
            }
        }
    }

}


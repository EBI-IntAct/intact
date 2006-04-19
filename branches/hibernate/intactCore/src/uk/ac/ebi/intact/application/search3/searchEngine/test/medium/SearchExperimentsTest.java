/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search3.searchEngine.test.medium;

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
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Experiment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class tests if the experiment search works.
 *
 * @author Anja Friedrichsen
 * @version $Id$
 */
public class SearchExperimentsTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public SearchExperimentsTest(final String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(SearchExperimentsTest.class);
    }

    SearchEngineImpl engine;
    IterableMap result;

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws IntactException {
        // create the index
        IntactHelper helper = new IntactHelper();
        SearchDAO dao = new SearchDAOImpl(helper);
        engine = new SearchEngineImpl(new IntactAnalyzer(), new File("indexMedium"), dao, null);
    }

    /**
     * check if the Experiment "ruxf_yeast", which is in the index, is found
     */
    public void testExperimentShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("shortlabel:ho AND objclass:uk.*experiment*"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
        assertNotNull(result);
        MapIterator it = result.mapIterator();
        assertTrue(it.hasNext());
        Object key = null;
        Collection value = null;
        while (it.hasNext()) {
            key = it.next();
            value = (ArrayList) it.getValue();
            System.out.println("Key" + key + " Value: " + value.toString());
            if (key.equals("experiment")) {
                assertNotNull(key);
                assertNotNull(value);
                assertEquals("experiment", key);
                assertTrue(!value.isEmpty());
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("ho", anObject.getShortLabel());
            }

        }

    }

    /**
     * check if all Experiments shortlabel starting with 'ru' are found
     */
    public void testMultiExperimentShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("shortlabel:gav* AND objclass:uk.*Experiment*"));
            assertNotNull(result);
            MapIterator it = result.mapIterator();
            assertTrue(it.hasNext());
            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key" + key + " Value: " + value.toString());
                if (key.equals("experiment")) {
                    assertNotNull(key);
                    assertNotNull(value);
                    assertEquals("experiment", key);
                    int size = 1;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        Experiment prot = (Experiment) iterator.next();
                        assertNotNull(prot);
                        assertTrue(prot.getShortLabel().startsWith("gav"));

                    }
                }

            }

        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }

    }

    /**
     * tests if a specific AC is found
     */
    public void testExperimentAc() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("ac:EBI-13 AND objclass:uk.*experiment*"));
            assertNotNull(result);
            MapIterator it = result.mapIterator();
            assertTrue(it.hasNext());
            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key" + key + " Value: " + value.toString());
                if (key.equals("experiment")) {
                    assertNotNull(key);
                    assertNotNull(value);
                    assertEquals("experiment", key);
                    assertTrue(!value.isEmpty());
                    AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                    assertEquals("EBI-13", anObject.getAc());
                }

            }
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    /**
     * tests if a AC starting with 'EBI-15' are found
     */
    public void testMultiExperimentAc() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("ac:EBI-* AND objclass:uk.*experiment*"));
            assertNotNull(result);
            MapIterator it = result.mapIterator();
            assertTrue(it.hasNext());
            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key" + key + " Value: " + value.toString());
                if (key.equals("experiment")) {
                    assertNotNull(key);
                    assertNotNull(value);
                    assertEquals("experiment", key);
                    int size = 2;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        Experiment prot = (Experiment) iterator.next();
                        assertNotNull(prot);
                        assertTrue(prot.getAc().startsWith("EBI-"));

                    }
                }

            }

        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }


    }


    /**
     * tests if a specific fullname is found
     */
    public void testExperimentFullname() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("fullname:interactome AND objclass:uk.*experiment*"));
            assertNotNull(result);
            MapIterator it = result.mapIterator();
            assertTrue(it.hasNext());
            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key" + key + " Value: " + value.toString());
                if (key.equals("experiment")) {
                    assertNotNull(key);
                    assertNotNull(value);
                    assertEquals("experiment", key);
                    assertTrue(!value.isEmpty());
                    AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                    assertTrue(anObject.getFullName().matches(".*interactome.*"));
                }

            }
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    /**
     * tests if all experiments fullname containing 'affinity*' are found.
     */
    public void testMultiExperimentFullname() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("fullname:affinity* AND objclass:uk.*experiment*"));
            assertNotNull(result);
            MapIterator it = result.mapIterator();
            assertTrue(it.hasNext());
            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key" + key + " Value: " + value.toString());
                if (key.equals("experiment")) {
                    assertNotNull(key);
                    assertNotNull(value);
                    assertEquals("experiment", key);
                    int size = 1;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        Experiment prot = (Experiment) iterator.next();
                        assertNotNull(prot);
//                        assertTrue(prot.getFullName().matches(".*tand.*"));

                    }
                }

            }

        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }


    }

}

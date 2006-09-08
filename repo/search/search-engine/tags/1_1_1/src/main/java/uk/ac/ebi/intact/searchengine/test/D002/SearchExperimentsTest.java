/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.searchengine.test.D002;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.searchengine.business.SearchEngineImpl;
import uk.ac.ebi.intact.searchengine.business.dao.SearchDAO;
import uk.ac.ebi.intact.searchengine.business.dao.SearchDAOImpl;
import uk.ac.ebi.intact.searchengine.lucene.IntactAnalyzer;

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


/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/


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
        return new TestSuite(uk.ac.ebi.intact.searchengine.test.D002.SearchExperimentsTest.class);
    }

    SearchEngineImpl engine;
    IterableMap result;

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws IntactException {
        // create the index


        SearchDAO dao = new SearchDAOImpl();
        engine = new SearchEngineImpl(new IntactAnalyzer(), new File("indexD002"), dao, null);
    }

    /**
     * check if the Experiment "ruxf_yeast", which is in the index, is found
     */
    public void testExperimentShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("shortlabel:taru-2002-1 AND objclass:uk.*experiment*"));
            System.out.println(result);
        } catch (IntactException e) {
//            assertTrue(false);
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
                assertEquals("Experiment", key);
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
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("shortlabel:to* AND objclass:uk.*experiment*"));
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
                    assertEquals("Experiment", key);
                    int size = 12;
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
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("ac:EBI-1296 AND objclass:uk.*experiment*"));
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
                    assertEquals("Experiment", key);
                    AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                    assertEquals("EBI-1296", anObject.getAc());
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
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("ac:EBI-3326* AND objclass:uk.*experiment*"));
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
                    assertEquals("Experiment", key);
                    int size = 4;
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
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("fullname:\"y2h clock-bmal1\" AND objclass:uk.*experiment*"));
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
                    assertEquals("Experiment", key);
                    AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                    assertTrue(anObject.getFullName().equalsIgnoreCase("y2h clock-bmal1"));
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
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("fullname:affinit* AND objclass:uk.*experiment*"));
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
                    assertEquals("Experiment", key);
                    int size = 6;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        Experiment prot = (Experiment) iterator.next();
                        assertNotNull(prot);
                        assertTrue(prot.getFullName().matches(".*affinit.*"));
                    }
                }

            }

        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }


    }

}

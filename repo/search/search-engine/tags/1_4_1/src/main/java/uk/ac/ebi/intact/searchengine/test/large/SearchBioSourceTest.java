/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.searchengine.test.large;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.searchengine.business.SearchEngineImpl;
import uk.ac.ebi.intact.searchengine.business.dao.SearchDAO;
import uk.ac.ebi.intact.searchengine.business.dao.SearchDAOImpl;
import uk.ac.ebi.intact.searchengine.lucene.IntactAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * TODO comment that ...
 *
 * @author Anja Friedrichsen
 * @version $Id$
 */
public class SearchBioSourceTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public SearchBioSourceTest(final String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(SearchBioSourceTest.class);
    }

    SearchEngineImpl engine;
    IterableMap result;

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws IntactException {
        // create the index

        SearchDAO dao = new SearchDAOImpl();
        engine = new SearchEngineImpl(new IntactAnalyzer(), new File("indexLarge"), dao, null);
    }

    /**
     * check if the BioSource "yeast", which is in the index, is found
     */
    public void testBioSourceShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("shortlabel:yeast AND objclass:uk.*BioSource*"));
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
                assertTrue(!value.isEmpty());
                assertNotNull(key);
                assertNotNull(value);
                assertEquals("BioSource", key);
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("yeast", anObject.getShortLabel());
            }

        }

    }

    /**
     * check if all BioSources shortlabel starting with 'can' are found
     */
    public void testMultiBioSourceShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("shortlabel:can* AND objclass:uk.*BioSource*"));
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
                    assertEquals("BioSource", key);
                    int size = 2;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        BioSource prot = (BioSource) iterator.next();
                        assertNotNull(prot);
                        assertTrue(prot.getShortLabel().startsWith("can"));

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
    public void testBioSourceAc() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("ac:EBI-16 AND objclass:uk.*BioSource*"));
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
                    assertEquals("BioSource", key);
                    assertTrue(!value.isEmpty());
                    AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                    assertEquals("EBI-16", anObject.getAc());
                }
            }
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    /**
     * tests if a AC starting with 'EBI-' are found
     */
    public void testMultiBioSourceAc() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("ac:EBI-75* AND objclass:uk.*BioSource*"));
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
                    assertEquals("BioSource", key);
                    int size = 2;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        BioSource prot = (BioSource) iterator.next();
                        assertNotNull(prot);
                        assertTrue(prot.getAc().startsWith("EBI-75"));

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
    public void testBioSourceFullname() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("fullname:lactis AND objclass:uk.*BioSource*"));
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
                    assertEquals("BioSource", key);
                    assertTrue(!value.isEmpty());
                    AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                    assertTrue(anObject.getFullName().matches(".*lactis.*"));
                }
            }
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    /**
     * tests if all BioSources fullname containing 'cere*' are found.
     */
    public void testMultiBioSourceFullname() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("fullname:Sac* AND objclass:uk.*BioSource*"));
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
                    assertEquals("BioSource", key);
                    int size = 2;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        BioSource prot = (BioSource) iterator.next();
                        assertNotNull(prot);
                        assertTrue(prot.getFullName().matches(".*Sac.*"));
                    }
                }
            }
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }
}

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
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Protein;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This Test creates first the index and then tests if specific proteins, which should
 * be in the index are found.
 *
 * @author Anja Friedrichsen
 * @version $Id$
 */
public class SearchProteinTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public SearchProteinTest(final String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(SearchProteinTest.class);
    }

    SearchEngineImpl engine;
    IterableMap result;

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws IntactException {
        IntactHelper helper = new IntactHelper();
        SearchDAO dao = new SearchDAOImpl(helper);
        engine = new SearchEngineImpl(new IntactAnalyzer(), new File("indexZPRO"), dao, null);
    }

    /**
     * check if the protein "ruxf_yeast", which is in the index, is found
     */
    public void testProteinShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("shortlabel:ruxf_yeast AND objclass:uk.*protein*"));
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
                assertEquals("Protein", key);
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("ruxf_yeast", anObject.getShortLabel());
            }

        }

    }

    /**
     * check if all proteins shortlabel starting with 'ru' are found
     */
    public void testMultiProteinShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("shortlabel:rux* AND objclass:uk.*protein*"));
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
                    assertEquals("Protein", key);
                    int size = 14;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        Protein prot = (Protein) iterator.next();
                        assertNotNull(prot);
                        assertTrue(prot.getShortLabel().startsWith("rux"));

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
    public void testProteinAc() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("ac:EBI-1500 AND objclass:uk.*protein*"));
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
                    assertEquals("Protein", key);
                    AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                    assertEquals("EBI-1500", anObject.getAc());
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
    public void testMultiProteinAc() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("ac:EBI-1500* AND objclass:uk.*protein*"));
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
                    assertEquals("Protein", key);
                    int size = 13;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        Protein prot = (Protein) iterator.next();
                        assertNotNull(prot);
                        assertTrue(prot.getAc().startsWith("EBI-1500"));

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
    public void testProteinFullname() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("fullname:\"30S ribosomal protein S2\" AND objclass:uk.*protein*"));
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
                    assertEquals("Protein", key);
                    AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                    assertEquals("30S ribosomal protein S2", anObject.getFullName());
                }

            }
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    /**
     * tests if all fullname containing 'yeas*' are found
     */
    public void testMultiProteinFullname() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("fullname:MSH* AND objclass:uk.*protein*"));
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
                    assertEquals("Protein", key);
                    int size = 8;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        Protein prot = (Protein) iterator.next();
                        assertNotNull(prot);
                        assertTrue(prot.getFullName().matches(".*MSH.*"));

                    }
                }

            }

        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }


    }

}

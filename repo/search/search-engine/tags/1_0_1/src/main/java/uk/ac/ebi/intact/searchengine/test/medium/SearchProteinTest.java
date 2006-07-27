/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.searchengine.test.medium;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.searchengine.business.SearchEngineImpl;
import uk.ac.ebi.intact.searchengine.business.dao.SearchDAO;
import uk.ac.ebi.intact.searchengine.business.dao.SearchDAOImpl;
import uk.ac.ebi.intact.searchengine.lucene.IntactAnalyzer;
import uk.ac.ebi.intact.searchengine.parser.IQLParserImpl;

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
    IterableMap result2;

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws IntactException {

        SearchDAO dao = new SearchDAOImpl();
        engine = new SearchEngineImpl(new IntactAnalyzer(), new File("indexMedium"), dao, new IQLParserImpl());
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
        assertTrue(it.hasNext());
        Object key = null;
        Collection value = null;
        while (it.hasNext()) {
            key = it.next();
            value = (ArrayList) it.getValue();
            System.out.println("Key " + key + " Value: " + value.toString());
            if (key.equals("protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("protein", key);
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
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("shortlabel:ru* AND objclass:uk.*protein*"));
            System.out.println("result size:" + result.size());

            assertNotNull(result);
            MapIterator it = result.mapIterator();
            assertTrue(it.hasNext());

            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key " + key + " Value: " + value.toString());
                if (key.equals("protein")) {
                    assertNotNull(key);
                    assertNotNull(value);
                    assertEquals("protein", key);
                    int size = 5;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        Protein prot = (Protein) iterator.next();
                        assertNotNull(prot);
                        assertTrue(prot.getShortLabel().startsWith("ru"));

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
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("ac:EBI\\-1500 AND objclass:uk.*protein*"));
            result2 = (IterableMap) engine.getResult(engine.findObjectByIQL("SELECT protein FROM intact WHERE ac = 'EBI-1500'"));
            assertNotNull(result);
            assertNotNull(result2);
            assertEquals(result, result2);
            MapIterator it = result.mapIterator();
            assertTrue(it.hasNext());

            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key " + key + " Value: " + value.toString());
                if (key.equals("protein")) {
                    assertNotNull(key);
                    assertNotNull(value);
                    assertTrue(!value.isEmpty());
                    assertEquals("protein", key);
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
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("ac:EBI-15* AND objclass:uk.*protein*"));
            assertNotNull(result);
            MapIterator it = result.mapIterator();
            assertTrue(it.hasNext());

            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key " + key + " Value: " + value.toString());
                if (key.equals("protein")) {
                    assertNotNull(key);
                    assertNotNull(value);
                    assertEquals("protein", key);
                    int size = 9;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        Protein prot = (Protein) iterator.next();
                        assertNotNull(prot);
                        assertTrue(prot.getAc().startsWith("EBI-15"));

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
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("fullname:Histone AND fullname:H2B.2 AND objclass:uk.*protein*"));
            result2 = (IterableMap) engine.getResult(engine.findObjectByLucene("fullname:(Histone H2B.2) AND objclass:uk.*protein*"));
            assertNotNull(result);
            assertNotNull(result2);
            assertEquals(result, result2);
            MapIterator it = result.mapIterator();
            assertTrue(it.hasNext());

            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key " + key + " Value: " + value.toString());
                if (key.equals("protein")) {
                    assertNotNull(key);
                    assertNotNull(value);
                    assertTrue(!value.isEmpty());
                    assertEquals("protein", key);
                    AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                    assertEquals("Histone H2B.2", anObject.getFullName());
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
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("fullname:Lsm* AND objclass:uk.*protein*"));
            assertNotNull(result);
            MapIterator it = result.mapIterator();
            assertTrue(it.hasNext());

            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key " + key + " Value: " + value.toString());
                if (key.equals("protein")) {
                    assertNotNull(key);
                    assertNotNull(value);
                    assertEquals("protein", key);
                    int size = 5;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        Protein prot = (Protein) iterator.next();
                        assertNotNull(prot);
                        assertTrue(prot.getFullName().matches(".*LSm.*"));

                    }
                }

            }

        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }


    }

}

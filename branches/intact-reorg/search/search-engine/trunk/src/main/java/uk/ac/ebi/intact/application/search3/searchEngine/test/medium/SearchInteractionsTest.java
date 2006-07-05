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
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Interaction;

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
public class SearchInteractionsTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public SearchInteractionsTest(final String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(SearchInteractionsTest.class);
    }

    SearchEngineImpl engine;
    IterableMap result;

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws IntactException {
        // create the index

        SearchDAO dao = new SearchDAOImpl();
        engine = new SearchEngineImpl(new IntactAnalyzer(), new File("indexMedium"), dao, null);
    }

    /**
     * check if the Interaction "ho-521", which is in the index, is found
     */
    public void testInteractionShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("shortlabel:ho-521 AND objclass:uk.*Interaction*"));
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
            if (key.equals("interaction")) {
                assertTrue(!value.isEmpty());
                assertNotNull(key);
                assertNotNull(value);
                assertEquals("interaction", key);
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("ho-521", anObject.getShortLabel());
            }

        }

    }

    /**
     * check if all Interactions shortlabel starting with 'ho' are found
     */
    public void testMultiInteractionShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("shortlabel:ho* AND objclass:uk.*Interaction*"));
            assertNotNull(result);
            MapIterator it = result.mapIterator();
            assertTrue(it.hasNext());
            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key" + key + " Value: " + value.toString());
                if (key.equals("interaction")) {
                    assertTrue(!value.isEmpty());
                    assertNotNull(key);
                    assertNotNull(value);
                    assertEquals("interaction", key);
                    int size = 3;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        Interaction prot = (Interaction) iterator.next();
                        assertNotNull(prot);
                        assertTrue(prot.getShortLabel().startsWith("ho"));

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
    public void testInteractionAc() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("ac:EBI-2367 AND objclass:uk.*Interaction*"));
            assertNotNull(result);
            MapIterator it = result.mapIterator();
            assertTrue(it.hasNext());
            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key" + key + " Value: " + value.toString());
                if (key.equals("interaction")) {
                    assertTrue(!value.isEmpty());
                    assertNotNull(key);
                    assertNotNull(value);
                    assertEquals("interaction", key);
                    AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                    assertEquals("EBI-2367", anObject.getAc());
                }

            }
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    /**
     * tests if a AC starting with 'EBI-20' are found
     */
    public void testMultiInteractionAc() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("ac:EBI-23* AND objclass:uk.*Interaction*"));
            assertNotNull(result);
            MapIterator it = result.mapIterator();
            assertTrue(it.hasNext());
            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key" + key + " Value: " + value.toString());
                if (key.equals("interaction")) {
                    assertTrue(!value.isEmpty());
                    assertNotNull(key);
                    assertNotNull(value);
                    assertEquals("interaction", key);
                    int size = 2;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        Interaction prot = (Interaction) iterator.next();
                        assertNotNull(prot);
                        assertTrue(prot.getAc().startsWith("EBI-23"));

                    }
                }

            }

        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }


    }


//        /**
//         * tests if a specific fullname is found
//         */
//        public void testInteractionFullname() {
//            try {
//                result = (IterableMap) engine.findObjectByLucene("fullname:Histone AND fullname:H2B.2 AND objclass:uk.*Interaction*");
//                assertNotNull(result);
//                MapIterator it = result.mapIterator();
//
//                Object key = null;
//                Collection value = null;
//                while (it.hasNext()) {
//                    key = it.next();
//                    value = (ArrayList) it.getValue();
//                    System.out.println("Key" + key + " Value: " + value.toString());
//                    if (key.equals("Interaction")) {
//                        assertTrue(!value.isEmpty());
//                        assertNotNull(key);
//                        assertNotNull(value);
//                        assertEquals("Interaction", key);
//                        AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
//                        assertEquals("Histone H2B.2", anObject.getFullName());
//                    }
//
//                }
//            } catch (IntactException e) {
//                assertTrue(false);
//                e.printStackTrace();
//            }
//        }
//
//        /**
//         * tests if all fullname containing 'yeas*' are found
//         */
//        public void testMultiInteractionFullname() {
//            try {
//                result = (IterableMap) engine.findObjectByLucene("fullname:LSm* AND objclass:uk.*Interaction*");
//                assertNotNull(result);
//                MapIterator it = result.mapIterator();
//
//                Object key = null;
//                Collection value = null;
//                while (it.hasNext()) {
//                    key = it.next();
//                    value = (ArrayList) it.getValue();
//                    System.out.println("Key" + key + " Value: " + value.toString());
//                    if (key.equals("Interaction")) {
//                        assertTrue(!value.isEmpty());
//                        assertNotNull(key);
//                        assertNotNull(value);
//                        assertEquals("Interaction", key);
//                        int size = 5;
//                        assertEquals(size, value.size());
//                        for (Iterator iterator = value.iterator(); iterator.hasNext();) {
//                            Interaction prot = (Interaction) iterator.next();
//                            assertNotNull(prot);
//                            assertTrue(prot.getFullName().matches(".*LSm.*"));
//
//                        }
//                    }
//
//                }
//
//            } catch (IntactException e) {
//                assertTrue(false);
//                e.printStackTrace();
//            }
//
//
//        }
//
//    }


}

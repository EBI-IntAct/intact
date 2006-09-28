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
import uk.ac.ebi.intact.model.Interaction;
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
        return new TestSuite(uk.ac.ebi.intact.searchengine.test.D002.SearchInteractionsTest.class);
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
     * check if the Interaction "ho-521", which is in the index, is found
     */
    public void testInteractionShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("shortlabel:taf1-taf4-1 AND objclass:uk.*Interaction*"));
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
                assertTrue(!value.isEmpty());
                assertNotNull(key);
                assertNotNull(value);
                assertEquals("Interaction", key);
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("taf1-taf4-1", anObject.getShortLabel());
            }

        }

    }

    /**
     * check if all Interactions shortlabel starting with 'ho' are found
     */
    public void testMultiInteractionShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("shortlabel:kek* AND objclass:uk.*Interaction*"));
            assertNotNull(result);
            MapIterator it = result.mapIterator();

            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key" + key + " Value: " + value.toString());
                if (key.equals("Interaction")) {
                    assertTrue(!value.isEmpty());
                    assertNotNull(key);
                    assertNotNull(value);
                    assertEquals("Interaction", key);
                    int size = 4;
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
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("ac:EBI-236730 AND objclass:uk.*Interaction*"));
            assertNotNull(result);
            MapIterator it = result.mapIterator();

            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key" + key + " Value: " + value.toString());
                if (key.equals("Interaction")) {
                    assertTrue(!value.isEmpty());
                    assertNotNull(key);
                    assertNotNull(value);
                    assertEquals("Interaction", key);
                    AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                    assertEquals("EBI-236730", anObject.getAc());
                }

            }
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    /**
     * tests if a AC starting with 'EBI-1290' are found
     */
    public void testMultiInteractionAc() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("ac:EBI-1290* AND objclass:uk.*Interaction*"));
            assertNotNull(result);
            MapIterator it = result.mapIterator();

            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key" + key + " Value: " + value.toString());
                if (key.equals("Interaction")) {
                    assertTrue(!value.isEmpty());
                    assertNotNull(key);
                    assertNotNull(value);
                    assertEquals("Interaction", key);
                    int size = 2;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        Interaction prot = (Interaction) iterator.next();
                        assertNotNull(prot);
                        assertTrue(prot.getAc().startsWith("EBI-1290"));

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
    public void testInteractionFullname() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("fullname:\"CD45-AP complex\" AND objclass:uk.*Interaction*"));
            assertNotNull(result);
            MapIterator it = result.mapIterator();

            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key" + key + " Value: " + value.toString());
                if (key.equals("Interaction")) {
                    assertTrue(!value.isEmpty());
                    assertNotNull(key);
                    assertNotNull(value);
                    assertEquals("Interaction", key);
                    AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                    assertEquals("CD45-AP complex", anObject.getFullName());
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
    public void testMultiInteractionFullname() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("fullname:regulati** AND objclass:uk.*Interaction*"));
            assertNotNull(result);
            MapIterator it = result.mapIterator();

            Object key = null;
            Collection value = null;
            while (it.hasNext()) {
                key = it.next();
                value = (ArrayList) it.getValue();
                System.out.println("Key" + key + " Value: " + value.toString());
                if (key.equals("Interaction")) {
                    assertTrue(!value.isEmpty());
                    assertNotNull(key);
                    assertNotNull(value);
                    assertEquals("Interaction", key);
                    int size = 4;
                    assertEquals(size, value.size());
                    for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                        Interaction prot = (Interaction) iterator.next();
                        assertNotNull(prot);
                        assertTrue(prot.getFullName().matches(".*regulati.*"));

                    }
                }

            }

        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }


    }

}


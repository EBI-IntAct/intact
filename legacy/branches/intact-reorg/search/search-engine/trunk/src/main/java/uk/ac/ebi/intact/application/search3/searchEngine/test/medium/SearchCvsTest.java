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
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class test the CvIdentification, CvInteraction and CvInteractionType against the index
 *
 * @author Anja Friedrichsen
 * @version $Id$
 */
public class SearchCvsTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public SearchCvsTest(final String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(SearchCvsTest.class);
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
     * test the search for a cvIdentification shortlabel.
     */
    public void testCvIdentificationShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("identification.shortlabel:\"western blot\""));
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
            if (key.equals("experiment")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("experiment", key);
                int size = 2;
                assertEquals(size, value.size());
                for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                    Experiment anObject = (Experiment) iterator.next();
                    assertEquals("EBI-227", anObject.getCvIdentification().getAc());
                }
            }
        }
    }

    /**
     * test the search for a cvIdentification ac.
     */
    public void testCvIdentificationAc() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("identification.ac:EBI-227"));
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
            if (key.equals("experiment")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("experiment", key);

                int size = 2;
                assertEquals(size, value.size());
                Experiment anObject = (Experiment) value.iterator().next();
                assertEquals("EBI-227", anObject.getCvIdentification().getAc());
            }
        }
    }

    /**
     * test the search for a cvIdentification fullname.
     */
    public void testCvIdentificationFullname() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("identification.fullname:\"western\""));
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
            if (key.equals("experiment")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("experiment", key);

                int size = 2;
                assertEquals(size, value.size());
                Experiment anObject = (Experiment) value.iterator().next();
                assertEquals("EBI-227", anObject.getCvIdentification().getAc());
            }
        }
    }


    /**
     * test the search for a cvInteraction shortlabel.
     */
    public void testCvInteractionShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("interaction.shortlabel:experiment*"));
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
            if (key.equals("experiment")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("experiment", key);

                int size = 2;
                assertEquals(size, value.size());
                for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                    Experiment anObject = (Experiment) iterator.next();
                    assertEquals("EBI-401", anObject.getCvInteraction().getAc());
                }
            }
        }
    }

    /**
     * test the search for a cvInteraction ac.
     */
    public void testCvInteractionAc() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("interaction.ac:EBI-401"));
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
            if (key.equals("experiment")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("experiment", key);

                int size = 2;
                assertEquals(size, value.size());
                Experiment anObject = (Experiment) value.iterator().next();
                assertEquals("EBI-401", anObject.getCvInteraction().getAc());
            }
        }
    }

    /**
     * test the search for a cvInteraction fullname.
     */
    public void testCvInteractionFullname() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("interaction.fullname:experimental"));
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
            if (key.equals("experiment")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("experiment", key);

                int size = 2;
                assertEquals(size, value.size());
                Experiment anObject = (Experiment) value.iterator().next();
                assertEquals("EBI-401", anObject.getCvInteraction().getAc());
            }
        }
    }


    /**
     * test the search for a cvInteractionType shortlabel.
     */
    public void testCvInteractionTypeShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("interactiontype.shortlabel:aggregation"));
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
            if (key.equals("interaction")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("interaction", key);

                int size = 12;
                assertEquals(size, value.size());
                for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                    Interaction anObject = (Interaction) iterator.next();
                    assertEquals("EBI-669", anObject.getCvInteractionType().getAc());
                }
            }
        }
    }

    /**
     * test the search for a cvInteractionType ac.
     */
    public void testCvInteractionTypeAc() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("interactiontype.ac:EBI-669"));
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
            if (key.equals("interaction")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("interaction", key);

                int size = 12;
                assertEquals(size, value.size());
                Interaction anObject = (Interaction) value.iterator().next();
                assertEquals("EBI-669", anObject.getCvInteractionType().getAc());
            }
        }
    }

    /**
     * test the search for a cvInteractionType fullname.
     */
    public void testCvInteractionTypeFullname() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("interactiontype.fullname:aggregation"));
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
            if (key.equals("interaction")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("interaction", key);

                int size = 12;
                assertEquals(size, value.size());
                Interaction anObject = (Interaction) value.iterator().next();
                assertEquals("EBI-669", anObject.getCvInteractionType().getAc());
            }
        }
    }

}

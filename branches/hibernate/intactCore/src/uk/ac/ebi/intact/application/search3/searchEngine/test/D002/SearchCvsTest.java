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
        IntactHelper helper = new IntactHelper();

        SearchDAO dao = new SearchDAOImpl(helper);
        engine = new SearchEngineImpl(new IntactAnalyzer(), new File("indexD002"), dao, null);
    }

    public void testTrue() {
        assertTrue(true);
    }

    /**
     * test the search for a cvIdentification shortlabel.
     */
    public void testCvIdentificationShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("identification_shortlabel:\"de novo protein\""));
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
            if (key.equals("experiment")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("experiment", key);
                int size = 9;
                assertEquals(size, value.size());
                for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                    Experiment anObject = (Experiment) iterator.next();
                    assertEquals("de novo protein sequ", anObject.getCvIdentification().getShortLabel());
                }
            }
        }
    }

    /**
     * test the search for a cvIdentification ac.
     */
    public void testCvIdentificationAc() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("identification_ac:EBI-81"));
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
            if (key.equals("Experiment")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Experiment", key);

                int size = 2;
                assertEquals(size, value.size());
                Experiment anObject = (Experiment) value.iterator().next();
                assertEquals("EBI-81", anObject.getCvIdentification().getAc());
            }
        }
    }

    /**
     * test the search for a cvIdentification fullname.
     */
    public void testCvIdentificationFullname() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("identification_fullname:tag"));
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
            if (key.equals("Experiment")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Experiment", key);
                int size = 2;
                assertEquals(size, value.size());
                Experiment anObject = (Experiment) value.iterator().next();
                assertEquals("sequence tag identification", anObject.getCvIdentification().getFullName());
            }
        }
    }


    /**
     * test the search for a cvInteraction shortlabel.
     */
    public void testCvInteractionShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("interaction_shortlabel:flag"));
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
            if (key.equals("Experiment")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Experiment", key);
                int size = 24;
                assertEquals(size, value.size());
                for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                    Experiment anObject = (Experiment) iterator.next();
                    assertEquals("flag tag", anObject.getCvInteraction().getShortLabel());
                }
            }
        }
    }

    /**
     * test the search for a cvInteraction ac.
     */
    public void testCvInteractionAc() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("interaction_ac:EBI-112"));
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
            if (key.equals("Experiment")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Experiment", key);

                int size = 14;
                assertEquals(size, value.size());
                Experiment anObject = (Experiment) value.iterator().next();
                assertEquals("EBI-112", anObject.getCvInteraction().getAc());
            }
        }
    }

    /**
     * test the search for a cvInteraction fullname.
     */
    public void testCvInteractionFullname() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("interaction_fullname:tandem"));
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
            if (key.equals("Experiment")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Experiment", key);

                int size = 14;
                assertEquals(size, value.size());
                Experiment anObject = (Experiment) value.iterator().next();
                assertEquals("tandem affinity purification", anObject.getCvInteraction().getFullName());
            }
        }
    }


    /**
     * test the search for a cvInteractionType shortlabel.
     */
    public void testCvInteractionTypeShortlabel() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("interactiontype_shortlabel:methylation"));
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
            if (key.equals("Interaction")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Interaction", key);

                int size = 9;
                assertEquals(size, value.size());
                for (Iterator iterator = value.iterator(); iterator.hasNext();) {
                    Interaction anObject = (Interaction) iterator.next();
                    assertEquals("methylation reaction", anObject.getCvInteractionType().getShortLabel());
                }
            }
        }
    }

    /**
     * test the search for a cvInteractionType ac.
     */
    public void testCvInteractionTypeAc() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("interactiontype_ac:EBI-49847"));
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
            if (key.equals("Interaction")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Interaction", key);

                int size = 3;
                assertEquals(size, value.size());
                Interaction anObject = (Interaction) value.iterator().next();
                assertEquals("EBI-49847", anObject.getCvInteractionType().getAc());
            }
        }
    }

    /**
     * test the search for a cvInteractionType fullname.
     */
    public void testCvInteractionTypeFullname() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("interactiontype_fullname:ubiquitination"));
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
            if (key.equals("Interaction")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Interaction", key);

                int size = 1;
                assertEquals(size, value.size());
                Interaction anObject = (Interaction) value.iterator().next();
                assertEquals("ubiquitination reaction", anObject.getCvInteractionType().getFullName());
            }
        }
    }

}

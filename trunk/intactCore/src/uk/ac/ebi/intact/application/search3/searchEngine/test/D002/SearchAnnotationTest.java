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
 * This class tests if the indexing of the annotation went right.
 *
 * @author Anja Friedrichsen
 * @version $Id$
 */
public class SearchAnnotationTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public SearchAnnotationTest(final String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(SearchAnnotationTest.class);
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
    public void testProteinAnnotation() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("annotation:\"Produced by alternative splicing of isoform C\"" +
                    " AND objclass:uk.*protein*"));
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
                assertEquals("EBI-438734", anObject.getAc());
            }
        }
    }

    /**
     * test protein Annotation with a wildcard character
     */
    public void testProteinMultiAnnotation() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("annotation:Produced* AND objclass:uk.*protein*"));
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
                int size = 8;
                assertEquals(size, value.size());
            }
        }
    }


    /**
     * test the cvobjects Annotation
     */
    public void testcvobjectAnnotation() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("annotation:\"ATCC number: CRL-5803.\" AND objclass:uk.*Cv*"));
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
            assertNotNull(key);
            assertNotNull(value);
            assertTrue(!value.isEmpty());
            assertEquals("cvobject", key);
            AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
            assertEquals("EBI-399610", anObject.getAc());
        }
    }

    /**
     * tests the cvobjects Annotation with a wildcard character
     */
    public void testcvobjectMultiAnnotation() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("annotation:character% AND objclass:uk.*Cv*"));
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
                int size = 7;
                assertEquals(size, value.size());
            }
        }
    }

    /**
     * tests the interaction Annotation
     */
    public void testInteractionAnnotation() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("annotation:\"p56lck GST-tagged\" AND objclass:uk.*interaction*"));
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
                assertEquals("EBI-1375", anObject.getAc());
            }
        }
    }

    /**
     * tests the interaction Annotation with a wildcard character
     */
    public void testInteractionMultiAnnotation() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("annotation:radiolabel* AND objclass:uk.*Interaction*"));
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
                int size = 8;
                assertEquals(size, value.size());
            }
        }
    }


    /**
     * tests the experiment Annotation
     */
    public void testExperimentAnnotation() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("annotation:\"493 bait proteins Flag-tagged\" AND objclass:uk.*Experiment*"));
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
                assertEquals("EBI-13", anObject.getAc());
            }
        }
    }

    /**
     * tests the Experiment Annotation with a wildcard character
     */
    public void testExperimentMultiAnnotation() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("annotation:covalent* AND objclass:uk.*Experiment*"));
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
                int size = 4;
                assertEquals(size, value.size());
            }
        }
    }


    /**
     * tests the experiment Annotation
     */
    public void testBioSourceAnnotation() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("annotation:\"http://locus.umdnj.edu/nigms\" AND objclass:uk.*BioSource*"));
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
                assertEquals("EBI-495093", anObject.getAc());
            }
        }
    }

    /**
     * tests the BioSource Annotation with a wildcard character
     */
    public void testBioSourceMultiAnnotation() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("annotation:derive* AND objclass:uk.*BioSource*"));
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
                int size = 2;
                assertEquals(size, value.size());
            }
        }
    }


}


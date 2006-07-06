/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search3.searchEngine.test.large;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment that ...
 *
 * @author Anja Friedrichsen
 * @version $Id$
 */
public class SearchAliasTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public SearchAliasTest(final String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(SearchAliasTest.class);
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
        engine = new SearchEngineImpl(new IntactAnalyzer(), new File("indexLarge"), dao, null);
    }

    public void testProteinAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("alias:ABD1 AND objclass:uk.*protein*"));
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
            System.out.println("Key " + key + " Value: " + value.toString());
            if (key.equals("Protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Protein", key);
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-1314", anObject.getAc());
            }
        }
    }


    public void testMultiProteinAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("alias:PA* AND objclass:uk.*protein*"));
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
            System.out.println("Key " + key + " Value: " + value.toString());
            if (key.equals("Protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Protein", key);
                int size = 16;
                assertEquals(size, value.size());
            }
        }
    }

    public void testGeneNameAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("gene-name:MSL1 AND objclass:uk.*Protein*"));
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
            System.out.println("Key " + key + " Value: " + value.toString());
            if (key.equals("Protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Protein", key);
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                System.out.println("found: " + anObject.toString());
                assertEquals("EBI-21121", anObject.getAc());
            }
        }
    }


    public void testMultiGeneNameAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("gene-name:MSN* AND objclass:uk.*Protein*"));
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
            System.out.println("Key " + key + " Value: " + value.toString());
            if (key.equals("Protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Protein", key);
                int size = 2;
                assertEquals(size, value.size());
            }
        }
    }


    public void testGeneNameSynonymAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("gene-name-synonym:YP2 AND objclass:uk.*Protein*"));
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
            System.out.println("Key " + key + " Value: " + value.toString());
            if (key.equals("Protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Protein", key);
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-1960", anObject.getAc());
            }
        }
    }


    public void testMultiGeneNameSynonymAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("gene-name-synonym:SCC* AND objclass:uk.*Protein*"));
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
            System.out.println("Key " + key + " Value: " + value.toString());
            if (key.equals("Protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Protein", key);
                int size = 3;
                assertEquals(size, value.size());
            }
        }
    }


    public void testOrfNameAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("orf-name:L8003.21 AND objclass:uk.*Protein*"));
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
            System.out.println("Key " + key + " Value: " + value.toString());
            if (key.equals("Protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Protein", key);
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-12434", anObject.getAc());
            }
        }
    }

    public void testMultiOrfNameAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("orf-name:P1* AND objclass:uk.*Protein*"));
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
            System.out.println("Key " + key + " Value: " + value.toString());
            if (key.equals("Protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Protein", key);
                int size = 3;
                assertEquals(size, value.size());
            }
        }
    }

    public void testLocusNameAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("locus-name:Q0080 AND objclass:uk.*Protein*"));
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
            System.out.println("Key " + key + " Value: " + value.toString());
            if (key.equals("Protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Protein", key);
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-16298", anObject.getAc());
            }
        }
    }

    public void testMultiLocusNameAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("locus-name:YDR00* AND objclass:uk.*Protein*"));
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
            System.out.println("Key " + key + " Value: " + value.toString());
            if (key.equals("Protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("Protein", key);
                int size = 5;
                assertEquals(size, value.size());
            }
        }
    }
}

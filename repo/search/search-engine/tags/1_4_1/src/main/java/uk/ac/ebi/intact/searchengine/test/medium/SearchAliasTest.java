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
import uk.ac.ebi.intact.searchengine.business.SearchEngineImpl;
import uk.ac.ebi.intact.searchengine.business.dao.SearchDAO;
import uk.ac.ebi.intact.searchengine.business.dao.SearchDAOImpl;
import uk.ac.ebi.intact.searchengine.lucene.IntactAnalyzer;
import uk.ac.ebi.intact.searchengine.parser.IQLParserImpl;

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
    IterableMap result2;

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws IntactException {
        // create the index

        SearchDAO dao = new SearchDAOImpl();
        engine = new SearchEngineImpl(new IntactAnalyzer(), new File("indexMedium"), dao, new IQLParserImpl());
    }

    public void testProteinAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("alias:ABD1 AND objclass:uk.*protein*"));
            result2 = (IterableMap) engine.getResult(engine.findObjectByIQL("SELECT protein FROM intact WHERE alias = 'ABD1'"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
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
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-1314", anObject.getAc());
            }
        }
    }


    public void testMultiProteinAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("alias:P* AND objclass:uk.*protein*"));
            result2 = (IterableMap) engine.getResult(engine.findObjectByIQL("SELECT protein FROM intact WHERE alias like 'P*'"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
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
                int size = 27;
                assertEquals(size, value.size());
            }
        }
    }

    public void testGeneNameAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("gene-name:MSL1 AND objclass:uk.*Protein*"));
            result2 = (IterableMap) engine.getResult(engine.findObjectByIQL("SELECT protein FROM intact WHERE gene-name = 'MSL1'"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
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
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-3121", anObject.getAc());
            }
        }
    }


    public void testMultiGeneNameAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("gene-name:M* AND objclass:uk.*Protein*"));
            result2 = (IterableMap) engine.getResult(engine.findObjectByIQL("SELECT protein FROM intact WHERE gene-name like 'M*'"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
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
                int size = 10;
                assertEquals(size, value.size());
            }
        }
    }


    public void testGeneNameSynonymAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("gene-name-synonym:YP2 AND objclass:uk.*Protein*"));
            result2 = (IterableMap) engine.getResult(engine.findObjectByIQL("SELECT protein FROM intact WHERE gene-name-synonym = 'YP2'"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
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
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-1960", anObject.getAc());
            }
        }
    }


    public void testMultiGeneNameSynonymAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("gene-name-synonym:SC* AND objclass:uk.*Protein*"));
            result2 = (IterableMap) engine.getResult(engine.findObjectByIQL("SELECT protein FROM intact WHERE gene-name-synonym like 'SC*'"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
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
                int size = 2;
                assertEquals(size, value.size());
            }
        }
    }


    public void testOrfNameAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("orf-name:L8003.21 AND objclass:uk.*Protein*"));
            result2 = (IterableMap) engine.getResult(engine.findObjectByIQL("SELECT protein FROM intact WHERE orf-name = 'L8003.21'"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
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
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-3063", anObject.getAc());
            }
        }
    }

    public void testMultiOrfNameAlias() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("orf-name:P* AND objclass:uk.*Protein*"));
            result2 = (IterableMap) engine.getResult(engine.findObjectByIQL("SELECT protein FROM intact WHERE orf-name = 'P*'"));
        } catch (IntactException e) {
            assertTrue(false);
            e.printStackTrace();
        }
        assertNotNull(result);
        assertNotNull(result2);
        assertEquals(result, result2);
        MapIterator it = result.mapIterator();
        Object key = null;
        assertTrue(it.hasNext());
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
                int size = 4;
                assertEquals(size, value.size());
            }
        }
    }
}

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
public class SearchCvDatabaseTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public SearchCvDatabaseTest(final String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(SearchCvDatabaseTest.class);
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

    public void testUniprot() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("uniprot:P47* AND objclass:uk.*protein*"));
            result2 = (IterableMap) engine.getResult(engine.findObjectByIQL("SELECT protein FROM intact WHERE uniprot like 'P47*'"));
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
            System.out.println("Key" + key + " Value: " + value.toString());
            if (key.equals("protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertEquals("protein", key);
                int size = 5;
                assertEquals(size, value.size());
            }

        }

    }

    public void testGo() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("go:GO\\:0000398 AND objclass:uk.*protein*"));
            result2 = (IterableMap) engine.getResult(engine.findObjectByIQL("SELECT protein FROM intact WHERE go = 'GO:0000398'"));
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
            System.out.println("Key" + key + " Value: " + value.toString());
            if (key.equals("protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertEquals("protein", key);
                int size = 45;
                assertEquals(size, value.size());
            }

        }

    }

    public void testGo2() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("go:\"GO:0000398\" AND objclass:uk.*protein*"));
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
            if (key.equals("protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertEquals("protein", key);
                int size = 45;
                assertEquals(size, value.size());
            }

        }

    }

    public void testInterpro() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("interpro:IPR0047* AND objclass:uk.*protein*"));
            result2 = (IterableMap) engine.getResult(engine.findObjectByIQL("SELECT protein FROM intact WHERE interpro like 'IPR0047*'"));
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
            System.out.println("Key" + key + " Value: " + value.toString());
            if (key.equals("protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertEquals("protein", key);
                int size = 3;
                assertEquals(size, value.size());
            }

        }

    }

    public void testSgd() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("sgd:S0003* AND objclass:uk.*protein*"));
            result2 = (IterableMap) engine.getResult(engine.findObjectByIQL("SELECT protein FROM intact WHERE sgd like 'S0003*'"));
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
            System.out.println("Key" + key + " Value: " + value.toString());
            if (key.equals("protein")) {
                assertNotNull(key);
                assertNotNull(value);
                assertEquals("protein", key);
                int size = 5;
                assertEquals(size, value.size());
            }

        }

    }

}

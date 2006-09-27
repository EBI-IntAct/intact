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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment that ...
 *
 * @author Anja Friedrichsen
 * @version $Id$
 */
public class SearchCvTopicTest extends TestCase {
    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public SearchCvTopicTest(final String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(SearchCvTopicTest.class);
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

    public void testDefinition() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("definition:Cleavage AND objclass:uk.*cv*"));
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


    public void testComment() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("comment:experiments AND objclass:uk.*cv*"));
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
            if (key.equals("cvobject")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("cvobject", key);
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject obj = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-62", obj.getAc());
            }
        }
    }


    public void testExample() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("example:experiments AND objclass:uk.*cv*"));
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
            if (key.equals("cvobject")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("cvobject", key);
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject obj = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-85", obj.getAc());
            }
        }
    }


    public void testRemarkInternal() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("remark_internal:CABRI AND objclass:uk.*cv*"));
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
            if (key.equals("cvobject")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("cvobject", key);
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject obj = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-104", obj.getAc());
            }
        }
    }

    public void testSearchUrl() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("search-url:http\\://www.ebi.uniprot.org/entry/* AND objclass:uk.*cv*"));
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
            if (key.equals("cvobject")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("cvobject", key);
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject obj = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-157", obj.getAc());
            }
        }
    }

    public void testSearchUrlAscii() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("search-url-ascii:\"http://www.ebi.uniprot.org/entry/${ac}?format=text&ascii\" AND objclass:uk.*cv*"));
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
            if (key.equals("cvobject")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("cvobject", key);
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject obj = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-157", obj.getAc());
            }
        }
    }

    public void testUniprotDrExport() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("uniprot-dr-export:2 AND objclass:uk.*cv*"));
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
            if (key.equals("cvobject")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("cvobject", key);
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject obj = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-290", obj.getAc());
            }
        }
    }

    public void testUrl() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("url:\"http://www.uniprot.org/\" AND objclass:uk.*cv*"));
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
            if (key.equals("cvobject")) {
                assertNotNull(key);
                assertNotNull(value);
                assertTrue(!value.isEmpty());
                assertEquals("cvobject", key);
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject obj = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-157", obj.getAc());
            }
        }
    }


}

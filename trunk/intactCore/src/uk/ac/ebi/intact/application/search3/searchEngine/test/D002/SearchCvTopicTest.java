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
        IntactHelper helper = new IntactHelper();

        SearchDAO dao = new SearchDAOImpl(helper);
        engine = new SearchEngineImpl(new IntactAnalyzer(), new File("indexD002"), dao, null);
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
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("comment:producing AND objclass:uk.*cv*"));
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
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject obj = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-367132", obj.getAc());
            }
        }
    }


    public void testExample() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("example:Giot AND objclass:uk.*cv*"));
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
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject obj = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-300444", obj.getAc());
            }
        }
    }

    public void testFunction() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("function:biological AND objclass:uk.*cv*"));
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
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject obj = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-16", obj.getAc());
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
                assertEquals("EBI-306980", obj.getAc());
            }
        }
    }

    public void testSearchUrl() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("search-url:http://www.lgcpromochem.com/atcc/ AND objclass:uk.*cv*"));
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
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject obj = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-399610", obj.getAc());
            }
        }
    }

    public void testSearchUrlAscii() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("search-url-ascii:http://www.ebi.uniprot.org/entry/${ac}?format=text&ascii AND objclass:uk.*cv*"));
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
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject obj = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-31", obj.getAc());
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
                assertEquals("EBI-94", obj.getAc());
            }
        }
    }

    public void testUrl() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("url:http://www.uniprot.org/ AND objclass:uk.*cv*"));
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
                int size = 1;
                assertEquals(size, value.size());
                AnnotatedObject obj = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-31", obj.getAc());
            }
        }
    }


}

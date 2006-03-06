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
public class SearchXrefTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public SearchXrefTest(final String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(SearchXrefTest.class);
    }

    SearchEngineImpl engine;
    IterableMap result;

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws IntactException {
        IntactHelper helper = new IntactHelper();
        SearchDAO dao = new SearchDAOImpl(helper);
        engine = new SearchEngineImpl(new IntactAnalyzer(), new File("indexMedium"), dao, null);
    }

    public void testProteinXRef() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("xref:P02294 AND objclass:uk.*protein*"));
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
                assertTrue(!value.isEmpty());
                assertEquals("protein", key);
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-1500", anObject.getAc());
            }
        }
    }

    public void testProteinMultiXRef() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("xref:IPR007* AND objclass:uk.*protein*"));
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
                assertTrue(!value.isEmpty());
                assertEquals("protein", key);
                int size = 14;
                assertEquals(size, value.size());
            }
        }
    }

    public void testcvobjectXRef() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("xref:10200254 AND objclass:uk.*Cv*"));
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
                AnnotatedObject anObject = (AnnotatedObject) value.iterator().next();
                assertEquals("EBI-544", anObject.getAc());
            }
        }
    }

    public void testcvobjectMultiXRef() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("xref:AA005* AND objclass:uk.*Cv*"));
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
                int size = 11;
                assertEquals(size, value.size());
            }
        }
    }

}


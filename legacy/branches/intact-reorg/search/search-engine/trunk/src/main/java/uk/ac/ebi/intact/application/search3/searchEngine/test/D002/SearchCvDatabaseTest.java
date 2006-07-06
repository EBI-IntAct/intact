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
     * Constructs a Test instance with the specified name.
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

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws IntactException {
        // create the index


        SearchDAO dao = new SearchDAOImpl();
        engine = new SearchEngineImpl(new IntactAnalyzer(), new File("indexD002"), dao, null);
    }

    public void testUniprot() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("uniprot:P4781* AND objclass:uk.*protein*"));
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
                assertEquals("Protein", key);
                int size = 5;
                assertEquals(size, value.size());
            }

        }

    }

    public void testGo() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("go:GO\\:0001558 AND objclass:uk.*protein*"));
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
                assertEquals("Protein", key);
                int size = 15;
                assertEquals(size, value.size());
            }

        }

    }

    public void testInterpro() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("interpro:IPR00479* AND objclass:uk.*protein*"));
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
                assertEquals("Protein", key);
                int size = 10;
                assertEquals(size, value.size());
            }

        }

    }

    public void testSgd() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("sgd:S0003* AND objclass:uk.*protein*"));
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
                assertEquals("Protein", key);
                int size = 8;
                assertEquals(size, value.size());
            }

        }

    }

    public void testCabri() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("cabri:EC* AND objclass:uk.*Cv*"));
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
                assertEquals("cvobject", key);
                int size = 19;
                assertEquals(size, value.size());
            }

        }

    }


    public void testIntact() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("intact:EBI-300* AND objclass:uk.*Cv*"));
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
                assertEquals("cvobject", key);
                int size = 2;
                assertEquals(size, value.size());
            }

        }

    }

    public void testNewt() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("newt:9609 AND objclass:uk.*Cv*"));
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
                assertEquals("cvobject", key);
                int size = 3;
                assertEquals(size, value.size());
            }

        }

    }

    public void testPsiMi() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("psi-mi:MI\\:001* AND objclass:uk.*Cv*"));
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
                assertEquals("cvobject", key);
                int size = 9;
                assertEquals(size, value.size());
            }

        }

    }

    public void testPubmed() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("pubmed:95* AND objclass:uk.*Cv*"));
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
                assertEquals("cvobject", key);
                int size = 2;
                assertEquals(size, value.size());
            }

        }

    }

    public void testResid() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("resid:AA0044 AND objclass:uk.*Cv*"));
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
                assertEquals("cvobject", key);
                int size = 2;
                assertEquals(size, value.size());
            }

        }

    }

    public void testFlybase() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("flybase:FBgn003077* AND objclass:uk.*protein*"));
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
                assertEquals("Protein", key);
                int size = 11;
                assertEquals(size, value.size());
            }

        }

    }

    public void testHuge() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("huge:KIAA056* AND objclass:uk.*protein*"));
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
                assertEquals("Protein", key);
                int size = 3;
                assertEquals(size, value.size());
            }

        }

    }

    public void testReactome() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("reactome:P30* AND objclass:uk.*protein*"));
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
                assertEquals("Protein", key);
                int size = 3;
                assertEquals(size, value.size());
            }

        }

    }


    public void testPdb() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("pdb:1N0W AND objclass:uk.*interaction*"));
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
                assertEquals("Interaction", key);
                int size = 3;
                assertEquals(size, value.size());
            }

        }

    }

    public void testOmim() {
        try {
            result = (IterableMap) engine.getResult(engine.findObjectByLucene("omim:18* AND objclass:uk.*interaction*"));
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
                assertEquals("Interaction", key);
                int size = 2;
                assertEquals(size, value.size());
            }

        }

    }
}

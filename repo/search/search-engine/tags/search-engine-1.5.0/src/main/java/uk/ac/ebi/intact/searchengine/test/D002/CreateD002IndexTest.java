/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.searchengine.test.D002;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.searchengine.business.SearchEngineImpl;
import uk.ac.ebi.intact.searchengine.business.dao.SearchDAO;
import uk.ac.ebi.intact.searchengine.business.dao.SearchDAOImpl;
import uk.ac.ebi.intact.searchengine.lucene.IntactAnalyzer;
import uk.ac.ebi.intact.util.Chrono;

import java.io.File;
import java.io.IOException;

/**
 * *This class indexes the Database objects. It is not really a test,
 * but needs to be called before the other Test classes.
 * It should be called only in combination with the D002 database.
 * It creates a directory called 'indexD002' and put the index into this directory.
 * Be carefull to delete that directory before indexing, otherwise the existing index is extended.
 *
 * @author Anja Friedrichsen
 * @version $Id$
 */
public class CreateD002IndexTest extends TestCase {


    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public CreateD002IndexTest(final String name) {
        super(name);
    }


    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(CreateD002IndexTest.class);
    }


    SearchEngineImpl engine;

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws IOException, IntactException {
        // create the index

        SearchDAO dao = new SearchDAOImpl();
        engine = new SearchEngineImpl(new IntactAnalyzer(), new File("indexD002"), dao, null);

        Chrono chrono = new Chrono();
        chrono.start();
        engine.createLuceneIndex();
        chrono.stop();
        System.out.println("time to index was: " + chrono.toString());

    }

    /**
     * test if the index directory is not empty
     */
    public void testIndexing() {
        assertTrue(true);
    }

}

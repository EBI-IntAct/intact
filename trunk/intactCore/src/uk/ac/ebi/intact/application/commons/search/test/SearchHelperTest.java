/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.commons.search.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.commons.search.ResultWrapper;
import uk.ac.ebi.intact.application.commons.search.SearchHelper;
import uk.ac.ebi.intact.application.commons.search.SearchHelperI;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.model.ProteinImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The test class for SearchHelper class. These tests are only valid after the
 * database is filled with testfill script.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class SearchHelperTest extends TestCase {

    /**
     * Constructs an instance with the specified name.
     * @param name the name of the test.
     */
    public SearchHelperTest(String name) {
        super(name);
    }

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() {
        // Write setting up code for each test.
    }

    /**
     * Tears down the test fixture. Called after every test case method.
     */
    protected void tearDown() {
        // Release resources for after running a test.
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(SearchHelperTest.class);
    }

    public void testGetExperiments() {
        try {
            doTestGetExperiments(false);
            doTestGetExperiments(true);
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testGetInteractions() {
        try {
            doTestGetInteractions(false);
            doTestGetInteractions(true);
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testGetProteins() {
        try {
            doTestGetProteins(false);
            doTestGetProteins(true);
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testHelperClosing() {
        try {
            doTestHelperClosing(false);
            doTestHelperClosing(true);
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testSimpleLookup() {
        try {
            doTestSimpleLookup();
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    private void doTestGetExperiments(boolean query) throws IntactException {
        // Any valid logger will do fine here.
        Logger logger = Logger.getLogger(EditorConstants.LOGGER);
        SearchHelperI searchHelper = new SearchHelper(logger);

        ResultWrapper rw = getResultWrapper(query, searchHelper, Experiment.class,
                "ac", "*", 20);

        List results = rw.getResult();
        assertEquals(results.size(), 2);
        // Not too large
        assertFalse(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 2);

        List labels = extractShortLabels(results);
        assertTrue(labels.contains("gavin"));
        assertTrue(labels.contains("ho"));
    }

    private void doTestGetInteractions(boolean query) throws IntactException {
        Logger logger = Logger.getLogger(EditorConstants.LOGGER);
        SearchHelperI searchHelper = new SearchHelper(logger);

        // within max size
        ResultWrapper rw = getResultWrapper(query, searchHelper,InteractionImpl.class,
                "shortLabel", "ga-*", 20);

        List results = rw.getResult();
        assertEquals(results.size(), 8);
        // Not too large
        assertFalse(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 8);

        for (Iterator iter = extractShortLabels(results).iterator(); iter.hasNext();) {
            assertTrue(((String) iter.next()).startsWith("ga-"));
        }

        // equals to max size
        rw = getResultWrapper(query, searchHelper, InteractionImpl.class,
                "shortLabel", "ga-*", 8);

        results = rw.getResult();
        assertEquals(results.size(), 8);
        // Not too large
        assertFalse(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 8);

        for (Iterator iter = extractShortLabels(results).iterator(); iter.hasNext();) {
            assertTrue(((String) iter.next()).startsWith("ga-"));
        }

        // greater than max size
        rw = getResultWrapper(query, searchHelper, InteractionImpl.class,
                "shortLabel", "ga-*", 5);
        results = rw.getResult();
        assertTrue(results.isEmpty());
        // Too large
        assertTrue(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 8);
    }

    private void doTestGetProteins(boolean query) throws IntactException {
        Logger logger = Logger.getLogger(EditorConstants.LOGGER);
        SearchHelperI searchHelper = new SearchHelper(logger);

        // within max size
        ResultWrapper rw = getResultWrapper(query, searchHelper, ProteinImpl.class,
                "shortLabel", "y*", 20);
        List results = rw.getResult();
        assertEquals(results.size(), 14);
        // Not too large
        assertFalse(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 14);

        for (Iterator iter = extractShortLabels(results).iterator(); iter.hasNext();) {
            assertTrue(((String) iter.next()).startsWith("y"));
        }

        // equals to max size
        rw = getResultWrapper(query, searchHelper, ProteinImpl.class, "shortLabel",
                "y*", 14);
        results = rw.getResult();
        assertEquals(results.size(), 14);
        // Not too large
        assertFalse(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 14);

        for (Iterator iter = extractShortLabels(results).iterator(); iter.hasNext();) {
            assertTrue(((String) iter.next()).startsWith("y"));
        }

        // greater than max size
        rw = getResultWrapper(query, searchHelper, ProteinImpl.class, "shortLabel",
                "y*", 5);
        results = rw.getResult();
        assertTrue(results.isEmpty());
        // Too large
        assertTrue(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 14);
    }

    private void doTestHelperClosing(boolean query) throws IntactException {
        // Open a helper outside the method.
        IntactHelper helper = null;
        try {
            // Open a helper outside the method.
            helper = new IntactHelper();

            // Any valid logger will do fine here.
            Logger logger = Logger.getLogger(EditorConstants.LOGGER);
            SearchHelperI searchHelper = new SearchHelper(logger);

            // Calling the method which closes the internal helper.
            ResultWrapper rw = getResultWrapper(query, searchHelper,
                    InteractionImpl.class, "ac", "*", 20);

            // Use the helper (local) to do a query.
            List results = (List) helper.search(Experiment.class, "ac", "*");

            List labels = extractShortLabels(results);
            assertTrue(labels.contains("gavin"));
            assertTrue(labels.contains("ho"));
        }
        finally {
            if (helper != null) {
                helper.closeStore();
            }
        }
    }

    private void doTestSimpleLookup() throws IntactException {
        // Any valid logger will do fine here.
        Logger logger = Logger.getLogger(EditorConstants.LOGGER);
        SearchHelperI searchHelper = new SearchHelper(logger);

        ResultWrapper rw = searchHelper.doLookupSimple(Experiment.class, "*", 2);

        List results = rw.getResult();
        assertEquals(results.size(), 2);
        // Not too large
        assertFalse(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 2);

        List labels = extractShortLabels(results);
        assertTrue(labels.contains("gavin"));
        assertTrue(labels.contains("ho"));
    }

    private ResultWrapper getResultWrapper(boolean query, SearchHelperI helper, Class searchClass,
                                           String searchParam, String searchValue,
                                           int max) throws IntactException {
        return query ? helper.searchByQuery(searchClass, searchParam, searchValue, max)
                : helper.search(searchClass, searchParam, searchValue, max);

    }

    private List extractShortLabels(List annobjs) {
        List labels = new ArrayList();
        for (Iterator iter = annobjs.iterator(); iter.hasNext();) {
            labels.add(((AnnotatedObject) iter.next()).getShortLabel());
        }
        return labels;
    }
}

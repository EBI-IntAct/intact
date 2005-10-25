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
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import uk.ac.ebi.intact.application.commons.search.ResultWrapper;
import uk.ac.ebi.intact.application.commons.search.SearchHelper;
import uk.ac.ebi.intact.application.commons.search.SearchHelperI;
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
            doTestGetExperiments();
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testGetInteractions() {
        try {
            doTestGetInteractions();
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testGetProteins() {
        try {
            doTestGetProteins();
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testHelperClosing() {
        try {
            doTestHelperClosing();
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testGetInteractions2() {
        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            doTestGetInteractions2(helper);
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
        finally {
            if (helper != null) {
                try {
                    helper.closeStore();
                }
                catch (IntactException ie) {}
            }
        }
    }

    private void doTestGetExperiments() throws IntactException {
        // Any valid logger will do fine here.
        Logger logger = Logger.getLogger( getClass().getName() );
        SearchHelperI searchHelper = new SearchHelper(logger);

        ResultWrapper rw = searchHelper.searchByQuery(Experiment.class,"ac", "*", 20);

        List results = rw.getResult();
        assertEquals(results.size(), 2);
        // Not too large
        assertFalse(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 2);

        List labels = extractShortLabels(results);
        assertTrue(labels.contains("gavin"));
        assertTrue(labels.contains("ho"));
    }

    private void doTestGetInteractions() throws IntactException {
        Logger logger = Logger.getLogger( getClass().getName() );
        SearchHelperI searchHelper = new SearchHelper(logger);

        // within max size
        ResultWrapper rw = searchHelper.searchByQuery(InteractionImpl.class,
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
        rw = searchHelper.searchByQuery(InteractionImpl.class, "shortLabel", "ga-*", 8);

        results = rw.getResult();
        assertEquals(results.size(), 8);
        // Not too large
        assertFalse(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 8);

        for (Iterator iter = extractShortLabels(results).iterator(); iter.hasNext();) {
            assertTrue(((String) iter.next()).startsWith("ga-"));
        }

        // greater than max size
        rw = searchHelper.searchByQuery(InteractionImpl.class, "shortLabel", "ga-*", 5);
        results = rw.getResult();
        assertTrue(results.isEmpty());
        // Too large
        assertTrue(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 8);
    }

    private void doTestGetInteractions2(IntactHelper helper) throws IntactException {
        Logger logger = Logger.getLogger( getClass().getName() );
        SearchHelperI searchHelper = new SearchHelper(logger);

        // within max size
        Query[] queries = getSearchQueries(InteractionImpl.class, "ga-*");
        ResultWrapper rw = searchHelper.searchByQuery(queries, 20);

        // Not too large
        List results = rw.getResult();
        assertEquals(results.size(), 8);
        // Not too large
        assertFalse(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 8);

        for (Iterator iter = results.iterator(); iter.hasNext();) {
            Object[] row = (Object[]) iter.next();
            assertTrue(((String) row[1]).startsWith("ga-"));
        }

        // equals to max size
        rw = searchHelper.searchByQuery(queries, 8);
        results = rw.getResult();
        assertEquals(results.size(), 8);
        // Not too large
        assertFalse(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 8);

        for (Iterator iter = results.iterator(); iter.hasNext();) {
            Object[] row = (Object[]) iter.next();
            assertTrue(((String) row[1]).startsWith("ga-"));
        }

        // greater than max size
        rw = searchHelper.searchByQuery(queries, 5);
        // Too large
        results = rw.getResult();
        assertTrue(results.isEmpty());
        assertTrue(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 8);

        // No results
        queries = getSearchQueries(InteractionImpl.class, "xx-*");
        rw = searchHelper.searchByQuery(queries, 5);

        // None found
        assertTrue(rw.isEmpty());
        assertEquals(rw.getPossibleResultSize(), 0);
    }

    private void doTestGetProteins() throws IntactException {
        Logger logger = Logger.getLogger( getClass().getName() );
        SearchHelperI searchHelper = new SearchHelper(logger);

        // within max size
        ResultWrapper rw = searchHelper.searchByQuery(ProteinImpl.class,
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
        rw = searchHelper.searchByQuery(ProteinImpl.class, "shortLabel", "y*", 14);
        results = rw.getResult();
        assertEquals(results.size(), 14);
        // Not too large
        assertFalse(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 14);

        for (Iterator iter = extractShortLabels(results).iterator(); iter.hasNext();) {
            assertTrue(((String) iter.next()).startsWith("y"));
        }

        // greater than max size
        rw = searchHelper.searchByQuery(ProteinImpl.class, "shortLabel", "y*", 5);
        results = rw.getResult();
        assertTrue(results.isEmpty());
        // Too large
        assertTrue(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 14);
    }

    private void doTestHelperClosing() throws IntactException {
        // Open a helper outside the method.
        IntactHelper helper = null;
        try {
            // Open a helper outside the method.
            helper = new IntactHelper();

            // Any valid logger will do fine here.
            Logger logger = Logger.getLogger( getClass().getName() );
            SearchHelperI searchHelper = new SearchHelper(logger);

            // Calling the method which closes the internal helper.
            ResultWrapper rw = searchHelper.searchByQuery(InteractionImpl.class,
                    "ac", "*", 20);

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

    private List extractShortLabels(List annobjs) {
        List labels = new ArrayList();
        for (Iterator iter = annobjs.iterator(); iter.hasNext();) {
            labels.add(((AnnotatedObject) iter.next()).getShortLabel());
        }
        return labels;
    }

    // Some examples for queries (copied from editor OJBQueryFatory class).

    private Query[] getSearchQueries(Class clazz, String value) {
        Query[] queries = new Query[2];

        // Replace * with % for SQL
        String sqlValue = value.replaceAll("\\*", "%");

        Criteria crit1 = new Criteria();
        crit1.addLike("ac", sqlValue);
        Criteria crit2 = new Criteria();
        crit2.addLike("shortLabel", sqlValue);

        // Looking for either AC or shortlabel
        crit1.addOrCriteria(crit2);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(clazz, crit1);
        query.setAttributes(new String[] {"count(ac)"});

        // Set it as the first iterm.
        queries[0] = query;
        queries[1] = getSearchQuery(clazz, value);

        return queries;
    }

    private Query getSearchQuery(Class clazz, String value) {
        // Replace * with % for SQL
        String sqlValue = value.replaceAll("\\*", "%");

        Criteria crit1 = new Criteria();
        // Need all records for given class.
        crit1.addLike("ac", sqlValue);
        Criteria crit2 = new Criteria();
        crit2.addLike("shortLabel", sqlValue);

        // Looking for both ac and shortlabel
        crit1.addOrCriteria(crit2);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(clazz, crit1);
        // Limit to ac and shortlabel
        query.setAttributes(new String[] {"ac", "shortLabel"});
        // Sorts on shortlabel
        query.addOrderByAscending("shortLabel");
        return query;
    }
}

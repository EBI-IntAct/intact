/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.editor.test.pg;

import com.meterware.httpunit.SubmitButton;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.xml.sax.SAXException;

/**
 * Search test case for Postgres database.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class SearchTest extends EditorTestCase {

    /**
     * Returns this test suite.
     */
    public static Test suite() {
        return new TestSuite(SearchTest.class);
    }

    public void testSearchPage() throws Exception {
        // Login as a default user.
        WebResponse response = login("x");
        try {
            verifySidebar(response);
        }
        finally {
            logout(response);
        }
    }

    public void testExperimentSearch() throws Exception {
        // Login first.
        WebResponse response = login("x");
        try {
            // Validate the sidebar first.
            verifySidebar(response);
            expSearch(response);
        }
        finally {
            // Logout from the session.
            logout(response);
        }
    }

    public void testCvDatabaseSearch() throws Exception {
        // Login first.
        WebResponse response = login("x");
        try {
            // Validate the sidebar first.
            verifySidebar(response);
            cvDatabaseSearch(response);
        }
        finally {
            // Logout from the session.
            logout(response);
        }
    }

    public void testCvTopicSearch() throws Exception {
        // Login first.
        WebResponse response = login("x");
        try {
            // Validate the sidebar first.
            verifySidebar(response);
            cvTopicSearch(response);
        }
        finally {
            // Logout from the session.
            logout(response);
        }
    }

    private void expSearch(WebResponse webResponse) throws Exception {
        // Response after submitting the form.
        WebResponse response = submitSearch(webResponse, "Experiment");

        // Verify the basic result form.
        verifyResultForm(response);

        // Get the result table.
        WebTable tab = response.getTableStartingWith("AC");

        // Two experiments + heading.
        assertEquals(tab.getRowCount(), 3);

        // Skip the headings.
        assertEquals(tab.getCellAsText(1, 1).trim(), "gavin");
        assertEquals(tab.getCellAsText(2, 1).trim(), "ho");

        // The topic is still the selected topic.
        verifySidebar(response, "Experiment", "*");

        // Search for a single experiment.
        response = submitSearch(response, "Experiment", "ho");

        // Gone directly to the edit screen.
        WebForm expForm = response.getFormWithName("expForm");
        assertNotNull("Experiment form not found", expForm);

        // The sidebar has the searched string.
        verifySidebar(response, "Experiment", "ho");
    }

    private void cvDatabaseSearch(WebResponse webResponse) throws Exception {
        // Response after submitting the form.
        WebResponse response = submitSearch(webResponse, "CvDatabase");

        // Verify the basic result form.
        verifyResultForm(response);

        // Get the result table.
        WebTable tab = response.getTableStartingWith("AC");

        // 9 Items + heading
        assertEquals(tab.getRowCount(), 10);

        // Skip the headings.
        assertEquals(tab.getCellAsText(1, 1).trim(), "flybase");
        assertEquals(tab.getCellAsText(2, 1).trim(), "go");

        // The topic is still the selected topic.
        verifySidebar(response, "CvDatabase", "*");

        // Search for a single cvdatabase.
        response = submitSearch(response, "CvDatabase", "go");

        // Gone directly to the edit screen.
        WebForm cvForm = response.getFormWithName("cvForm");
        assertNotNull("Cv form not found", cvForm);

        // The sidebar has the searched string.
        verifySidebar(response, "CvDatabase", "go");
    }

    private void cvTopicSearch(WebResponse webResponse) throws Exception {
        // Response after submitting the form.
        WebResponse response = submitSearch(webResponse, "CvTopic");

        // Verify the basic result form.
        verifyResultForm(response);

        // Get the result table.
        WebTable tab = response.getTableStartingWith("AC");

        // 13 topics + heading.
        assertEquals(tab.getRowCount(), 14);

        // Skip the headings.
        assertEquals(tab.getCellAsText(1, 1).trim(), "caution");
        assertEquals(tab.getCellAsText(2, 1).trim(), "comment");

        // The topic is still the selected topic.
        verifySidebar(response, "CvTopic", "*");

        // Search for a single cvdatabase.
        response = submitSearch(response, "CvTopic", "remark");

        // Gone directly to the edit screen.
        WebForm cvForm = response.getFormWithName("cvForm");
        assertNotNull("Cv form not found", cvForm);

        // The sidebar has the searched string.
        verifySidebar(response, "CvTopic", "remark");
    }

    private WebResponse submitSearch(WebResponse response, String topic)
            throws Exception {
        return submitSearch(response, topic, "*");
    }

    private WebResponse submitSearch(WebResponse response, String topic,
                                     String searchStr)
            throws Exception {
        // Should have the sidebar form.
        WebForm sidebarForm = response.getFormWithName("sidebarForm");

        // Set the search topic and the search value.
        sidebarForm.setParameter("topic", topic);
        sidebarForm.setParameter("searchString", searchStr);

        // Press Search button.
        SubmitButton searchButton = sidebarForm.getSubmitButton("dispatch",
                "Search");
        assertNotNull("Search button not found", searchButton);

        // Response after search button.
        return sidebarForm.submit(searchButton);
    }

    private void verifyResultForm(WebResponse response) throws SAXException {
        // Must have the result form.
        WebForm resultForm = response.getFormWithName("resultForm");
        assertNotNull("Result form not found", resultForm);

        // Check the action.
        assertEquals(resultForm.getAction(), "/intact/editor/do/result");

        // Get the result table.
        WebTable tab = response.getTableStartingWith("AC");

        // We should have the table.
        assertNotNull("Missing results table", tab);

        // Must have columns.
        assertEquals(tab.getColumnCount(), 4);
        assertEquals(tab.getCellAsText(0, 0), "AC");
        assertEquals(tab.getCellAsText(0, 1), "Short Label");
        assertEquals(tab.getCellAsText(0, 2), "Full Name");
        assertEquals(tab.getCellAsText(0, 3), "Lock");
    }
}

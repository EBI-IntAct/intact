/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.editor.test.pg;

import com.meterware.httpunit.*;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.xml.sax.SAXException;
import uk.ac.ebi.intact.application.editor.test.EditorTestCase;

import java.io.IOException;

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
        WebResponse response = login();
        try {
            verifySidebar(response);
        }
        finally {
            logout(response);
        }
    }

    public void testExperimentSearch() throws Exception {
        // Login first.
        WebResponse response = login();
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
        WebResponse response = login();
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
        WebResponse response = login();
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

    public void testBioSourceSearch() throws Exception {
        // Login first.
        WebResponse response = login();
        try {
            // Validate the sidebar first.
            verifySidebar(response);
            bsSearch(response);
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

        // Should have the links for gavin and ho experiments.
        assertNotNull("gavin link not found", response.getLinkWith("gavin"));
        assertNotNull("gavin AC link not found", response.getLinkWith("EBI-12"));
        assertNotNull("ho link not found", response.getLinkWith("ho"));
        assertNotNull("ho AC link not found", response.getLinkWith("EBI-13"));

        verifySearchSidebar(response, "Experiment");

        // Search for a single experiment.
        response = submitSearch(response, "Experiment", "ho");

        // Gone directly to the edit screen.
        WebForm expForm = response.getFormWithName("expForm");
        assertNotNull("Experiment form not found", expForm);

        verifySidebar(response);
    }

    private void cvDatabaseSearch(WebResponse webResponse) throws Exception {
        // Response after submitting the form.
        WebResponse response = submitSearch(webResponse, "CvDatabase");

        // Verify the basic result form.
        verifyResultForm(response);

        // Should have the links for flybase and go databases.
        assertNotNull("flybase link not found", response.getLinkWith("flybase"));
        assertNotNull("flybase AC link not found", response.getLinkWith("EBI-59"));
        assertNotNull("go link not found", response.getLinkWith("go"));
        assertNotNull("go AC link not found", response.getLinkWith("EBI-62"));

        verifySearchSidebar(response, "CvDatabase");

        // Search for a single cvdatabase.
        response = submitSearch(response, "CvDatabase", "go");

        // Gone directly to the edit screen.
        WebForm cvForm = response.getFormWithName("cvForm");
        assertNotNull("Cv form not found", cvForm);

        verifySidebar(response);
    }

    private void cvTopicSearch(WebResponse webResponse) throws Exception {
        // Response after submitting the form.
        WebResponse response = submitSearch(webResponse, "CvTopic");

        // Verify the basic result form.
        verifyResultForm(response);

        // Should have the links for caution and comment.
        assertNotNull("caution link not found", response.getLinkWith("caution"));
        assertNotNull("caution AC link not found", response.getLinkWith("EBI-16"));
        assertNotNull("comment link not found", response.getLinkWith("comment"));
        assertNotNull("comment AC link not found", response.getLinkWith("EBI-15"));

        verifySearchSidebar(response, "CvTopic");

        // Search for a single cvdatabase.
        response = submitSearch(response, "CvTopic", "remark");

        // Gone directly to the edit screen.
        WebForm cvForm = response.getFormWithName("cvForm");
        assertNotNull("Cv form not found", cvForm);

        verifySidebar(response);
    }

    private void bsSearch(WebResponse webResponse) throws Exception {
        // Response after submitting the form.
        WebResponse response = submitSearch(webResponse, "BioSource");

        // Verify the basic result form.
        verifyResultForm(response);

        // Should have the links for yeast and canal.
        assertNotNull("yeast link not found", response.getLinkWith("yeast"));
        assertNotNull("yeast AC link not found", response.getLinkWith("EBI-541"));
        assertNotNull("canal link not found", response.getLinkWith("canal"));
        assertNotNull("canal AC link not found", response.getLinkWith("EBI-1026"));

        verifySearchSidebar(response, "BioSource");

        // Search for a single cvdatabase.
        response = submitSearch(response, "BioSource", "yeast");

        // Gone directly to the edit screen.
        WebForm cvForm = response.getFormWithName("bsForm");
        assertNotNull("Bs form not found", cvForm);

        verifySidebar(response);
    }

    private WebResponse submitSearch(WebResponse response, String topic)
            throws Exception {
        return submitSearch(response, topic, "*");
    }

    private void verifyResultForm(WebResponse response) throws SAXException, IOException {
        // A dummy form is associated with the result.
        WebForm resultForm = response.getFormWithName("dummyForm");
        assertNotNull("Result form not found", resultForm);

        // Check the action.
        assertEquals(resultForm.getAction(), "/intact/editor/do/result");

        // Response as a string.
        String resptxt = response.getText();

        // Must have columns.
        assertTrue(resptxt.indexOf("<th class=\"tableCellHeader\">AC</th>") != -1);
        assertTrue(resptxt.indexOf("<th class=\"tableCellHeader\">Short Label</th>") != -1);
        assertTrue(resptxt.indexOf("<th class=\"tableCellHeader\">Full Name</th>") != -1);
        assertTrue(resptxt.indexOf("<th class=\"tableCellHeader\">Lock</th>") != -1);
    }
}

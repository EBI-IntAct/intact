/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.editor.test.pg;

import com.meterware.httpunit.*;
import junit.framework.TestCase;

/**
 * Super class for all the Postgres editor test cases.
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditorTestCase extends TestCase {

    private static String ourProtocol = "http";
    private static String ourHost = "localhost";
    private static int ourPort = 8080;
    private static String ourLoginPath = "/intact/editor";

    private static String ourValidUser = "smudali";
    private static String ourDatabase = "smudali";

    /**
     * Logs in with default valid user and given password.
     * @param password the password for the default user.
     * @return the resulting page after submitting the form.
     * @throws java.lang.Exception failure to connect to the server or unable to retrieve the
     * login form to submit login details.
     */
    protected WebResponse login(String password) throws Exception {
        return login(ourValidUser, password);
    }

    /**
     * Logs in using given username and password.
     * @param username the user name to login.
     * @param password the password to login
     * @return the resulting page after submitting the form.
     * @throws java.lang.Exception failure to connect to the server or unable to retrieve the
     * login form to submit login details.
     */
    protected WebResponse login(String username, String password) throws Exception {
        String url = ourProtocol + "://" + ourHost + ":" + ourPort + ourLoginPath;
        WebRequest request = new GetMethodWebRequest(url);

        WebConversation conversation = new WebConversation();
        WebResponse response = conversation.getResponse(request);

        // Must be related to editor.
        assertEquals(response.getTitle(), "Editor");

        // The login form.
        WebForm loginForm = response.getFormWithName("loginForm");

        // We should have this form.
        assertNotNull("Login form not found", loginForm);

        // The submit button.
        SubmitButton loginButton = loginForm.getSubmitButton("submit", "Login");
        assertNotNull("Login button not found", loginButton);

        // Set the login parameters.
        loginForm.setParameter("username", username);
        loginForm.setParameter("password", password);

        // Submit the form.
        return loginForm.submit(loginButton);
    }

    /**
     * Logs out of the editor.
     * @param webResponse the response to access the logout link.
     * @throws java.lang.Exception unable to access the logout link or error with clicking
     * on the link.
     */
    protected void logout(WebResponse webResponse) throws Exception {
        WebLink link = webResponse.getLinkWith("Logout");
        webResponse = link.click();

        // Should have the login form.
        WebForm loginForm = webResponse.getFormWithName("loginForm");
        assertNotNull("Login form not found", loginForm);
    }

    protected void verifySidebar(WebResponse response) throws Exception {
        verifySidebar(response, "Experiment", "*");
    }

    protected void verifySidebar(WebResponse response, String topic,
                               String searchValue) throws Exception {
        // Should have the sidebar form.
        WebForm sidebarForm = response.getFormWithName("sidebarForm");

        // The search string must have the '*'.
        assertEquals(sidebarForm.getParameterValue("searchString"), searchValue);
        // The topic must be an experiment.
        assertEquals(sidebarForm.getParameterValue("topic"), topic);

        // Sidebar links.
        assertNotNull("Missing Logout link", response.getLinkWith("Logout"));

        // Verify the user and database on the sidebar.
        WebTable table = response.getTables()[0];

        // Must have the login user and the database in the sidebar.
        String sidebarText = table.getCellAsText(0, 0);
        assertTrue("Username missing in the sidebar",
                sidebarText.indexOf(ourValidUser) != -1);
        assertTrue("Database missing in the sidebar",
                sidebarText.indexOf(ourDatabase) != -1);

        // Footer as text.
        String footerText = table.getCellAsText(0, 1);
        assertTrue("Incorrect or missing footer",
                footerText.indexOf("Please send any questions or suggestions") != -1);
    }
}

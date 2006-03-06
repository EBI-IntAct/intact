/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.editor.test.pg.security;

import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import junit.framework.Test;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.editor.test.pg.EditorTestCase;

/**
 * Login test class for postgres database.
 * 
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class LoginTest extends EditorTestCase {

    /**
     * Returns this test suite.
     */
    public static Test suite() {
        return new TestSuite(LoginTest.class);
    }

    public void testEmptyUser() throws Exception {
        WebResponse response = login("", "");

        WebTable[] tables = response.getTables();

        // The cell as text.
        WebTable table = tables[0].getTableCell(0, 1).getTables()[0];
        String cellText = table.getCellAsText(2, 0);

        // Error must be present.
        assertTrue(cellText.indexOf("Error") != -1);
        // User name an password are required.
        assertTrue(cellText.indexOf("Username is required") != -1);
        assertTrue(cellText.indexOf("Password is required") != -1);
    }

    public void testEmptyPassword() throws Exception {
        WebResponse response = login("xxx", "");
        WebTable[] tables = response.getTables();

        // The cell as text.
        WebTable table = tables[0].getTableCell(0, 1).getTables()[0];
        String cellText = table.getCellAsText(2, 0);

        // Error must be present.
        assertTrue(cellText.indexOf("Error") != -1);
        // Username required shouldn't be present.
        assertTrue("Username is required",
                cellText.indexOf("Username is required") == -1);
        // Password is required.
        assertTrue("Password is required",
                cellText.indexOf("Password is required") != -1);
    }

    public void testInvalidUser() throws Exception {
        WebResponse response = login("xxx", "x");
        WebTable[] tables = response.getTables();

        // The cell as text.
        WebTable table = tables[0].getTableCell(0, 1).getTables()[0];
        String cellText = table.getCellAsText(2, 0);

        // Error must be present.
        assertTrue(cellText.indexOf("Error") != -1);
        // Login failed.
        assertTrue(cellText.indexOf("Login failed") != -1);
    }

    public void testValidUser() throws Exception {
        // Login with a valid user.
        WebResponse response = login("x");
        try {
            verifySidebar(response);
        }
        finally {
            logout(response);
        }
    }

//    public void testLogin() throws Exception {
//        String url = protocol + "://" + hostname + ":" + port + loginPath;
//		WebRequest webRequest = new GetMethodWebRequest(url);
//		WebResponse webResponse = webConversation.getResponse(webRequest);
//
//        // The login form.
//        WebForm loginForm = webResponse.getFormWithName("loginForm");
//
//        // We should have this form.
//        assertNotNull("Login form not found", loginForm);
//
//        // The submit button.
//        SubmitButton loginButton = loginForm.getSubmitButton("submit", "Login");
//        assertNotNull("Login button not found", loginButton);
//
//        // Set the login parameters.
//        loginForm.setParameter("username", "smudali");
//        loginForm.setParameter("password", "*");
//
//        // Username is required.
//        webResponse = loginForm.submit(loginButton);
//
//        // Should have the sidebar form.
//        WebForm sidebarForm = webResponse.getFormWithName("sidebarForm");
//
//        // The search string must have the '*'.
//        assertEquals(sidebarForm.getParameterValue("searchString"), "*");
//        // The topic must be an experiment.
//        assertEquals(sidebarForm.getParameterValue("topic"), "Experiment");
//
////        String[] elements = webResponse.getElementNames();
////        for (int i = 0; i < elements.length; i++) {
////            System.out.println("Element " + i + " - " + elements[i]);
////        }
////        assertTrue("Username is required", webResponse.);
//        // Submit the form.
////        WebTable[] tables = webResponse.getTables();
////        for (int i = 0; i < tables.length; i++) {
////            System.out.println("Table: " + i + " name: " + tables[i].getName());
////        }
////        WebLink[] links = webResponse.getLinks();
////        for (int i = 0; i < tables.length; i++) {
////            System.out.println("Link: " + i + " name: " + links[i].asText());
////        }
//        WebLink link = webResponse.getLinkWith("Logout");
//        webResponse = link.click();
//
//        // Should have the login form.
//        loginForm = webResponse.getFormWithName("loginForm");
//        assertNotNull("Login form not found", loginForm);
//
////        System.out.println("Response: " + webResponse.getText());
//    }
}

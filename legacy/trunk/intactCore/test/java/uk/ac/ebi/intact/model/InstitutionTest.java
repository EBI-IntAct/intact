/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;

import java.util.Properties;
import java.io.IOException;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-Mar-2006</pre>
 */
public class InstitutionTest extends DataAccessTest
{

    private static final String I1_LABEL = "TestInstitution";
    private static final String I1_FULLNAME = "This is a test institution";

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(InstitutionTest.class);
    }

    @Test
    public void institutionCreation() throws IntactException
    {
        Institution institution = (Institution)
                getHelper().getObjectByLabel(Institution.class, I1_LABEL);
        assertNull(institution);

        institution = new Institution(I1_LABEL);
        institution.setFullName(I1_FULLNAME);

        getHelper().create(institution);

        institution = (Institution)
                getHelper().getObjectByLabel(Institution.class, I1_LABEL);
        assertNotNull(institution);
    }

    @Test
    public void institutionLoading() throws IntactException
    {
        Institution institution = (Institution)
                getHelper().getObjectByLabel(Institution.class, I1_LABEL);

        assertNotNull(institution);
        assertEquals(I1_FULLNAME, institution.getFullName());
    }

    @Test
    public void institutionDelete() throws IntactException
    {
        Institution institution = (Institution)
                getHelper().getObjectByLabel(Institution.class, I1_LABEL);

        assertNotNull(institution);

        getHelper().delete(institution);

        assertNull(getHelper().getObjectByLabel(Institution.class, I1_LABEL));
    }

    @Test
    public void propertiesFile() throws IntactException, IOException
    {
        Properties props = new Properties();
        props.load(InstitutionTest.class.getResourceAsStream("/config/Institution.properties"));

        String shortLabel = props.getProperty("Institution.shortLabel");
        String fullName = props.getProperty("Institution.fullName");
        String address1 = props.getProperty("Institution.postalAddress.line1");
        String address2 = props.getProperty("Institution.postalAddress.line2");
        String address3 = props.getProperty("Institution.postalAddress.line3");
        String address4 = props.getProperty("Institution.postalAddress.line4");
        String address5 = props.getProperty("Institution.postalAddress.line5");
        String url = props.getProperty("Institution.url");

        Institution institution = (Institution)
                getHelper().getObjectByLabel(Institution.class, shortLabel);

        assertNotNull(institution);

        //String[] address = institution.getPostalAddress().split(System.getProperty("line.separator"));

        assertEquals(shortLabel.toLowerCase(), institution.getShortLabel().toLowerCase());
        assertEquals(fullName, institution.getFullName());
        /*
        assertEquals(address1, address[0]);
        assertEquals(address2, address[1]);
        assertEquals(address3, address[2]);
        assertEquals(address4, address[3]);
        assertEquals(address5, address[4]);   */
        assertEquals(url, institution.getUrl());

        assertEquals(institution, getHelper().getInstitution());

    }
}

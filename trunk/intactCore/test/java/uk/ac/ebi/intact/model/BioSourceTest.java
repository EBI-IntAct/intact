/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-Mar-2006</pre>
 */
public class BioSourceTest extends DataAccessTest
{
    private static final String B1_LABEL = "TestBioSource";
    private static final String B1_TAXID = "999999";
    private static final String B1_FULLNAME = "A test organism";

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(BioSourceTest.class);
    }

    @Test
    public void bioSourceCreation() throws IntactException
    {
        BioSource bioSource = TestDataUtil.getBioSource (getHelper(), B1_LABEL);
        assertNull(bioSource);

        bioSource = new BioSource(getHelper().getInstitution(), B1_LABEL, B1_TAXID);
        bioSource.setFullName(B1_FULLNAME);

        getHelper().create(bioSource);

        bioSource = TestDataUtil.createBioSource (getHelper(), B1_LABEL, B1_TAXID);
        assertNotNull(bioSource);
    }

    @Test(expected = NullPointerException.class)
    public void bioSourceCreationMissingLabel() throws IntactException
    {
         BioSource bioSource = new BioSource(getHelper().getInstitution(), null, B1_TAXID);
         getHelper().create(bioSource);

    }

    @Test(expected = NullPointerException.class)
    public void bioSourceCreationMissingTaxId() throws IntactException
    {
         BioSource bioSource = new BioSource(getHelper().getInstitution(), B1_LABEL, null);
         getHelper().create(bioSource);

    }

    @Test
    public void bioSourceLoading() throws IntactException
    {
        BioSource bioSource = TestDataUtil.getBioSource (getHelper(), B1_LABEL);

        assertNotNull(bioSource);
        assertEquals(B1_FULLNAME, bioSource.getFullName());
    }

    @Test
    public void bioSourceDelete() throws IntactException
    {
        BioSource bioSource = TestDataUtil.getBioSource (getHelper(), B1_LABEL);

        assertNotNull(bioSource);

        TestDataUtil.deleteBioSource(getHelper(), B1_LABEL);

        assertNull(TestDataUtil.getBioSource (getHelper(), B1_LABEL));
    }


}

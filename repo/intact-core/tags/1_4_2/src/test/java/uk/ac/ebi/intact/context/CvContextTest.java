/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.context;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvTopic;

/**
 * Test for <code>CvContextTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 08/29/2006
 */
public class CvContextTest extends TestCase {
    public CvContextTest(String name) {
        super(name);
    }

    private CvContext cvContext;

    public void setUp() throws Exception {
        super.setUp();
        this.cvContext = IntactContext.getCurrentInstance().getCvContext();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        cvContext = null;
    }

    public void testGetCurrentInstance() throws Exception {
        assertNotNull(cvContext);
    }

    public void testGetByAc() throws Exception {
        assertEquals(CvDatabase.UNIPROT, cvContext.getByAc("EBI-31").getShortLabel());
        assertEquals(CvDatabase.UNIPROT, cvContext.getByAc("EBI-31").getShortLabel());
    }

    public void testGetByLabel() throws Exception {
        assertEquals("EBI-375443", cvContext.getByLabel(CvTopic.CC_NOTE).getAc());
        assertEquals(CvDatabase.UNIPROT, cvContext.getByLabel(CvDatabase.UNIPROT).getShortLabel());
        assertEquals(CvDatabase.UNIPROT, cvContext.getByLabel(CvDatabase.UNIPROT).getShortLabel());
    }

    public void testGetByMiRef() throws Exception {
        assertEquals(CvDatabase.UNIPROT, cvContext.getByMiRef(CvDatabase.UNIPROT_MI_REF).getShortLabel());
        assertEquals(CvDatabase.UNIPROT, cvContext.getByMiRef(CvDatabase.UNIPROT_MI_REF).getShortLabel());
    }

    public static Test suite() {
        return new TestSuite(CvContextTest.class);
    }
}

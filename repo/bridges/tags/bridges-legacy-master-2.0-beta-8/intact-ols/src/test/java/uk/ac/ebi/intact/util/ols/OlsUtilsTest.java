/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.ols;

import junit.framework.TestCase;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class OlsUtilsTest extends TestCase {


    public void testGetMiTerm() throws Exception {
        String miTermId = "MI:0495";

        Term term = OlsUtils.getMiTerm(miTermId);
        assertNotNull(term);
        assertEquals(10, term.getChildren().size());
    }

    public void testGetOntologyTerm_singleTerm() throws Exception {
        String taxid = "9606";

        Term term = OlsUtils.getOntologyTerm(taxid, false);
        assertNotNull(term);
        assertEquals(0, term.getChildren().size());
    }

    public void testGetOntologyTerm_recursive() throws Exception {
        String taxid = "185752";    // 12884

        Term term = OlsUtils.getOntologyTerm(taxid, true);
        assertNotNull(term);
        assertEquals(3, term.getChildren().size());
    }
}
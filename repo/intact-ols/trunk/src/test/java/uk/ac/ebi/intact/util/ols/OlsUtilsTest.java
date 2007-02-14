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
        String miTermId = "MI:0001";

        Term term = OlsUtils.getMiTerm(miTermId);
        assertNotNull(term);
        assertEquals(4, term.getChildren().size());
    }

}
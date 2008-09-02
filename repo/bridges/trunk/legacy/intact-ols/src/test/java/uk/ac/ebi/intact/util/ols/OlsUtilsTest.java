/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.ols;

import junit.framework.TestCase;
import junit.framework.Assert;
import org.junit.Test;
import uk.ac.ebi.ook.web.services.Query;

import java.util.List;
import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class OlsUtilsTest extends TestCase {

    @Test
    public void testGetMiTerm() throws Exception {
        String miTermId = "MI:0495";

        Term term = OlsUtils.getMiTerm(miTermId);
        assertNotNull(term);
        assertEquals(12, term.getChildren().size());
    }


    @Test
    public void testPopulateParents() throws Exception {

        String miTermId = "MI:0499";   //unspecified role has two parents

        Term term = OlsUtils.getMiTerm( miTermId );

        assertNotNull( term );
        assertEquals( 2, term.getParents().size() );

        boolean expRole = false;
        boolean bioRole = false;
        for ( Term term_ : term.getParents() ) {
            if ( "MI:0495".equals( term_.getId() ) ) {
                expRole = true;
            }
            if ( "MI:0500".equals( term_.getId() ) ) {
                bioRole = true;
            }

        }
        Assert.assertTrue( "One of the parent must be experimental role", expRole );
        Assert.assertTrue( "One of the parent must be biological role", bioRole );
    }


    @Test
    public void testGetAllParents() throws Exception {
        OlsClient olsClient = new OlsClient();
        Query ontologyQuery = olsClient.getOntologyQuery();

        String miTermId = "MI:0407"; //direct Interaction
        Term term = OlsUtils.getMiTerm( miTermId );
        assertNotNull( term );

        List<Term> allParentsWithoutRoot = OlsUtils.getAllParents( miTermId, OlsUtils.PSI_MI_ONTOLOGY, ontologyQuery, new ArrayList<Term>(),true);
        assertEquals( 3, allParentsWithoutRoot.size() );

        List<Term> allParentsWithtRoot = OlsUtils.getAllParents( miTermId, OlsUtils.PSI_MI_ONTOLOGY, ontologyQuery, new ArrayList<Term>(),false);
        assertEquals( 4, allParentsWithtRoot.size() );

    }

    @Test
    public void testIsWithoutParent() throws Exception {
        OlsClient olsClient = new OlsClient();
        Query ontologyQuery = olsClient.getOntologyQuery();
        Assert.assertFalse(OlsUtils.hasParent( "MI:0000", OlsUtils.PSI_MI_ONTOLOGY, ontologyQuery ));
    }


    @Test
    public void testGetOntologyTerm_singleTerm() throws Exception {
        String taxid = "9606";

        Term term = OlsUtils.getOntologyTerm(taxid, false);
        assertNotNull(term);
        assertEquals(0, term.getChildren().size());
    }

    @Test
    public void testGetOntologyTerm_recursive() throws Exception {
        String taxid = "185752";    // 12884

        Term term = OlsUtils.getOntologyTerm(taxid, true);
        assertNotNull(term);
        assertEquals(3, term.getChildren().size());
    }
}
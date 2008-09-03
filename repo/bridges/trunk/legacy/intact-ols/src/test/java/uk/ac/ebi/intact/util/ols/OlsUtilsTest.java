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
import java.util.Set;
import java.util.HashSet;

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
    public void testGetGOTerm() throws Exception {
        String goTermId = "GO:0005634";

        Term term = OlsUtils.getGoTerm(goTermId);
        assertNotNull(term);
        Assert.assertEquals("GO:0005634",term.getId());
        Assert.assertEquals("nucleus",term.getName());
        
    }


    @Test
    public void testGetAllParentsForGO() throws Exception {
        OlsClient olsClient = new OlsClient();
        Query ontologyQuery = olsClient.getOntologyQuery();

        String goTermId = "GO:0005634"; //nucleus
        Term term = OlsUtils.getGoTerm( goTermId );
        assertNotNull( term );

        /*All the parents
        GO:0043226  organelle
        GO:0043227  membrane-bounded organelle
        GO:0005622  intracellular
        GO:0005623  cell
        GO:0043231  intracellular membrane-bounded organelle
        GO:0005575  cellular_component
        GO:0043229  intracellular organelle
        GO:0044464  cell part
        GO:0044424  intracellular part*/


        List<Term> allParentsWithoutRoot = OlsUtils.getAllParents( goTermId, OlsUtils.GO_ONTOLOGY, ontologyQuery, new ArrayList<Term>(), true );
        assertEquals( 8, allParentsWithoutRoot.size() );

        List<Term> allParentsWithtRoot = OlsUtils.getAllParents( goTermId, OlsUtils.GO_ONTOLOGY, ontologyQuery, new ArrayList<Term>(), false );
        assertEquals( 9, allParentsWithtRoot.size() );


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
    public void testGetAllParentsForMI() throws Exception {
        OlsClient olsClient = new OlsClient();
        Query ontologyQuery = olsClient.getOntologyQuery();

        String miTermId = "MI:0407"; //direct Interaction
        Term term = OlsUtils.getMiTerm( miTermId );
        assertNotNull( term );

        List<Term> allParentsWithoutRoot = OlsUtils.getAllParents( miTermId, OlsUtils.PSI_MI_ONTOLOGY, ontologyQuery, new ArrayList<Term>(),true);
        assertEquals( 3, allParentsWithoutRoot.size() );

        List<Term> allParentsWithRoot = OlsUtils.getAllParents( miTermId, OlsUtils.PSI_MI_ONTOLOGY, ontologyQuery, new ArrayList<Term>(),false);
        assertEquals( 4, allParentsWithRoot.size() );

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
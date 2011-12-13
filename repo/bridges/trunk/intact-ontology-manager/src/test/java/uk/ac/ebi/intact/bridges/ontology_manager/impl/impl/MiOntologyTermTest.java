package uk.ac.ebi.intact.bridges.ontology_manager.impl.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import uk.ac.ebi.intact.bridges.ontology_manager.TermAnnotation;
import uk.ac.ebi.intact.bridges.ontology_manager.TermDbXref;
import uk.ac.ebi.intact.bridges.ontology_manager.builders.MiOntologyTermBuilder;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.MIOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.IntactOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

import java.net.URL;

/**
 * Tester of MiOntologyTerm
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/11/11</pre>
 */

public class MiOntologyTermTest {
    private IntactOntology intactOntology;

    @Before
    public void parseOboTest() throws OntologyLoaderException {
        MIOboLoader parser = new MIOboLoader(null, null, null, null, new MiOntologyTermBuilder());

        URL psiMiObo = MiOntologyTermTest.class.getResource("/psi-mi.obo");
        this.intactOntology = parser.parseOboFile(psiMiObo);
    }

    @Test
    public void test_synonyms(){

        // term with alias exact and shortlabel defined in synonyms
        // this term also have 3 PMIDs
        IntactOntologyTermI term1 = intactOntology.search("MI:0018");
        Assert.assertNotNull(term1);

        Assert.assertEquals("two hybrid", term1.getFullName());
        Assert.assertEquals("2 hybrid", term1.getShortLabel());
        Assert.assertEquals(8, term1.getAliases().size());

        for (String alias : term1.getAliases()){
            if (!alias.equals("2-hybrid") && !alias.equals("2H") && !alias.equals("2h") && !alias.equals("classical two hybrid")
                    && !alias.equals("Gal4 transcription regeneration") && !alias.equals("two-hybrid") && !alias.equals("Y2H")
                    && !alias.equals("yeast two hybrid")){
                Assert.assertTrue(false);
            }
        }

        Assert.assertEquals(3, term1.getDbXrefs().size());
        for (TermDbXref xref : term1.getDbXrefs()){
            if (xref.getDatabase().equals("pubmed") && xref.getDatabaseId().equals("MI:0446")){
                if (xref.getQualifier().equals("primary-reference") && xref.getQualifierId().equals("MI:0358")){
                    if (!xref.getAccession().equals("10967325") && !xref.getAccession().equals("12634794") && !xref.getAccession().equals("1946372")){
                        Assert.assertTrue(false);
                    }
                }
                else if (xref.getQualifier().equals("method reference") && xref.getQualifierId().equals("MI:0357")){
                    if (!xref.getAccession().equals("10967325") && !xref.getAccession().equals("12634794") && !xref.getAccession().equals("1946372")){
                        Assert.assertTrue(false);
                    }
                }
                else{
                    Assert.assertTrue(false);
                }
            }
            else {
                Assert.assertTrue(false);
            }
        }

        Assert.assertEquals("The classical two-hybrid system is a method that uses transcriptional activity as a measure of protein-protein interaction. It relies on the modular nature of many site-specific transcriptional activators (GAL 4) , which consist of a DNA-binding domain and a transcriptional activation domain. The DNA-binding domain serves to target the activator to the specific genes that will be expressed, and the activation domain contacts other proteins of the transcriptional machinery to enable transcription to occur. The two-hybrid system is based on the observation that the two domains of the activator need to be non-covalently brought together by the interaction of any two proteins. The application of this system requires the expression of two hybrid. Generally this assay is performed in yeast cell, but it can also be carried out in other organism. The bait protein is fused to the DNA binding molecule, the prey to the transcriptional activator.", term1.getDefinition());

        // term with no alias shortlabel
        IntactOntologyTermI term2 = intactOntology.search("MI:0084");
        Assert.assertNotNull(term2);

        Assert.assertEquals("phage display", term2.getFullName());
        Assert.assertEquals("phage display", term2.getShortLabel());
        Assert.assertEquals(0, term2.getAliases().size());
        Assert.assertEquals("Peptide sequences or entire proteins can be displayed on phage capsids by fusion to coat proteins to generate a library of fusion phages each displaying a different peptide. Such a library can then be exploited to identify specific phages that display peptides that bind to any given bait molecule for instance an antibody. The selection is performed by a series of cycles of affinity purification known as panning. The bait protein, immobilized on a solid support (plastic, agarose, sepharose, magnetic beads and others) is soaked in the phage mixture and that phage that remains attached to the bait is amplified and carried through a further affinity purification step. Each cycle results in an approximately 1,000-fold enrichment of specific phage and after a few selection rounds (2-4), DNA sequencing of the tight-binding phage reveals only a small number of sequences. Phage display panning experiments can be carried out either on libraries of peptides of random amino acid sequence or on libraries of displaying natural peptides obtained by inserting cDNA fragments into the phage vector (cDNA libraries). Libraries have been assembled on several different phages (Fd, Lambda or T7).", term2.getDefinition());
    }

    @Test
    public void test_Xrefs(){
        // term with id-validation-regexp and search URL
        // the term also have http in definition
        // the term does have a single PMID xref
        IntactOntologyTermI term1 = intactOntology.search("MI:0446");
        Assert.assertNotNull(term1);

        Assert.assertEquals(1, term1.getDbXrefs().size());

        for (TermDbXref xref : term1.getDbXrefs()){
            if (xref.getDatabase().equals("pubmed") && xref.getDatabaseId().equals("MI:0446")){
                if (xref.getQualifier().equals("primary-reference") && xref.getQualifierId().equals("MI:0358")){
                    if (!xref.getAccession().equals("14755292")){
                        Assert.assertTrue(false);
                    }
                }
                else{
                    Assert.assertTrue(false);
                }
            }
            else {
                Assert.assertTrue(false);
            }
        }

        Assert.assertEquals(2, term1.getAnnotations().size());

        for (TermAnnotation annotation : term1.getAnnotations()){
            if (annotation.getTopic().equals("id-validation-regexp") && annotation.getTopicId().equals("MI:0628")){
                if (!annotation.getDescription().equals("[0-9]+")){
                    Assert.assertTrue(false);
                }
            }
            else if (annotation.getTopic().equals("search-url") && annotation.getTopicId().equals("MI:0615")){
                if (!annotation.getDescription().equals("http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;list_uids=${ac}&amp;dopt=Abstract")){
                    Assert.assertTrue(false);
                }
            }
            else {
                Assert.assertTrue(false);
            }
        }

        Assert.assertEquals("http://www.ncbi.nlm.nih.gov/entrez/query.fcgi", term1.getURL());
        Assert.assertEquals("PubMed is designed to provide access to citations from biomedical literature.", term1.getDefinition());
    }

    @Test
    public void test_Definition_Xrefs(){
        // term with PMID application xref
        IntactOntologyTermI term1 = intactOntology.search("MI:0012");
        Assert.assertNotNull(term1);

        Assert.assertEquals(2, term1.getDbXrefs().size());

        for (TermDbXref xref : term1.getDbXrefs()){
            if (xref.getDatabase().equals("pubmed") && xref.getDatabaseId().equals("MI:0446")){
                if (xref.getQualifier().equals("see-also") && xref.getQualifierId().equals("MI:0361")){
                    Assert.assertEquals("10725388", xref.getAccession());
                }
                else if (xref.getQualifier().equals("primary-reference") && xref.getQualifierId().equals("MI:0358")){
                    Assert.assertEquals("9874787", xref.getAccession());
                }
                else {
                    Assert.assertTrue(false);
                }
            }
            else {
                Assert.assertTrue(false);
            }
        }

        // term with GO xref and several RESID xrefs
        IntactOntologyTermI term2 = intactOntology.search("MI:0192");
        Assert.assertNotNull(term2);

        Assert.assertEquals(18, term2.getDbXrefs().size());

        for (TermDbXref xref : term2.getDbXrefs()){
            if (xref.getDatabase().equals("pubmed") && xref.getDatabaseId().equals("MI:0446")){
                if (xref.getQualifier().equals("primary-reference") && xref.getQualifierId().equals("MI:0358")){
                    Assert.assertEquals("14755292", xref.getAccession());
                }
                else {
                    Assert.assertTrue(false);
                }
            }
            else if (xref.getDatabase().equals("go") && xref.getDatabaseId().equals("MI:0448")){
                if (xref.getQualifier().equals("see-also") && xref.getQualifierId().equals("MI:0361")){
                    Assert.assertEquals("GO:0006473", xref.getAccession());
                }
                else {
                    Assert.assertTrue(false);
                }
            }
            else if (xref.getDatabase().equals("resid") && xref.getDatabaseId().equals("MI:0248")){
                if (xref.getQualifier().equals("see-also") && xref.getQualifierId().equals("MI:0361")){
                    if (!xref.getAccession().equals("AA0041") && !xref.getAccession().equals("AA0042") && !xref.getAccession().equals("AA0043") &&
                            !xref.getAccession().equals("AA0044") && !xref.getAccession().equals("AA0045") && !xref.getAccession().equals("AA0046") &&
                            !xref.getAccession().equals("AA0047") && !xref.getAccession().equals("AA0048") && !xref.getAccession().equals("AA0049") &&
                            !xref.getAccession().equals("AA0050") && !xref.getAccession().equals("AA0051") && !xref.getAccession().equals("AA0052") &&
                            !xref.getAccession().equals("AA0053") && !xref.getAccession().equals("AA0054") && !xref.getAccession().equals("AA0055")
                            && !xref.getAccession().equals("AA0056")){
                        Assert.assertTrue(false);
                    }
                }
                else {
                    Assert.assertTrue(false);
                }
            }
            else {
                Assert.assertTrue(false);
            }
        }

        // term with SO xref
        IntactOntologyTermI term3 = intactOntology.search("MI:0318");
        Assert.assertNotNull(term3);

        Assert.assertEquals(2, term3.getDbXrefs().size());

        for (TermDbXref xref : term3.getDbXrefs()){
            if (xref.getDatabase().equals("pubmed") && xref.getDatabaseId().equals("MI:0446")){
                if (xref.getQualifier().equals("primary-reference") && xref.getQualifierId().equals("MI:0358")){
                    Assert.assertEquals("14755292", xref.getAccession());
                }
                else {
                    Assert.assertTrue(false);
                }
            }
            else if (xref.getDatabase().equals("so") && xref.getDatabaseId().equals("MI:0601")){
                if (xref.getQualifier().equals("see-also") && xref.getQualifierId().equals("MI:0361")){
                    Assert.assertEquals("SO:0000348", xref.getAccession());
                }
                else {
                    Assert.assertTrue(false);
                }
            }
            else {
                Assert.assertTrue(false);
            }
        }
    }

    @Test
    public void test_comment(){
        // term with one comment
        IntactOntologyTermI term1 = intactOntology.search("MI:0108");
        Assert.assertNotNull(term1);

        Assert.assertEquals(1, term1.getComments().size());
        Assert.assertEquals("Reference not index in medline: Rosenberg, A, Griffin, K, Studier, WS, McCormick, M, Berg, J, Novy, R, Mierendorf, R inNovations, 1996, 6, 1.", term1.getComments().iterator().next());
    }

    @Test
    public void test_obsolete(){
        // term having one obsolete 'remap to' and one RESID xref
        IntactOntologyTermI term1 = intactOntology.search("MI:0189");
        Assert.assertNotNull(term1);

        Assert.assertEquals("Residue modification due to a cross-link between a lysine and a glycine from the ubiquitine protein.", term1.getDefinition());
        Assert.assertEquals("OBSOLETE remap to MOD:00134.", term1.getObsoleteMessage());
        Assert.assertEquals(0, term1.getPossibleTermsToRemapTo().size());
        Assert.assertEquals("MOD:00134", term1.getRemappedTerm());

        Assert.assertEquals(2, term1.getDbXrefs().size());

        for (TermDbXref xref : term1.getDbXrefs()){
            if (xref.getDatabase().equals("resid") && xref.getDatabaseId().equals("MI:0248")){
                if (xref.getQualifier().equals("see-also") && xref.getQualifierId().equals("MI:0361")){
                    Assert.assertEquals("AA0125", xref.getAccession());
                }
                else{
                    Assert.assertTrue(false);
                }
            }
            else if (xref.getDatabase().equals("pubmed") && xref.getDatabaseId().equals("MI:0446")){
                if (xref.getQualifier().equals("primary-reference") && xref.getQualifierId().equals("MI:0358")){
                    Assert.assertEquals("11125103", xref.getAccession());
                }
                else {
                    Assert.assertTrue(false);
                }
            }
            else {
                Assert.assertTrue(false);
            }
        }

        // term having one obsolete 'map to'
        IntactOntologyTermI term2 = intactOntology.search("MI:0309");
        Assert.assertNotNull(term2);

        Assert.assertEquals("A cassette coding for a protein tag is inserted by homologous recombination onto the genomic copy of an open reading frame. The advantage of this delivery method is that the resulting engineered protein is expressed under its natural promoter control.", term2.getDefinition());
        Assert.assertEquals("OBSOLETE redundant term. Map to feature type : tag (MI:0507).", term2.getObsoleteMessage());
        Assert.assertEquals(0, term2.getPossibleTermsToRemapTo().size());
        Assert.assertEquals("MI:0507", term2.getRemappedTerm());

        // term having one obsolete 'replace by'
        IntactOntologyTermI term3 = intactOntology.search("MI:0409");
        Assert.assertNotNull(term3);

        Assert.assertEquals("Experimental method used to identify the region of a nucleic acid involved in an interaction with a protein. One sample of a radiolabeled nucleic acid of known sequence is submitted to partial digestion. A second sample is incubated with its interacting partner and then is submitted to the same partial digestion. The two samples are then analyzed in parallel by electrophoresis on a denaturing acrylamide gel. After autoradiography the identification of the bands that correspond to fragments missing from the lane loaded with the second sample reveals the region of the nucleic acid that is protected from nuclease digestion upon binding.", term3.getDefinition());
        Assert.assertEquals("OBSOLETE because redundant with MI:0417 &apos;footprinting&apos; combined with interactor type MI:0319 &apos;DNA&apos; replace by:MI:0417", term3.getObsoleteMessage());
        Assert.assertEquals(0, term3.getPossibleTermsToRemapTo().size());
        Assert.assertEquals("MI:0417", term3.getRemappedTerm());

        // term having one obsolete and several choices
        IntactOntologyTermI term4 = intactOntology.search("MI:0021");
        Assert.assertNotNull(term4);

        Assert.assertEquals("Two proteins can be localised to cell compartments, in the same experiment, if they are expressed as chimeric proteins fused to distinct proteins fluorescing at different wavelengths (Green Fluorescent Protein and Red Fluorescent Protein for example). Using a confocal microscope the two proteins can be visualized in living cells and it can be determined whether they have the same subcellular location. Fluorescence microscopy of cells expressing a GFP fusion protein can also demonstrate dynamic processes such as its translocation from one subcellular compartment to another.", term4.getDefinition());
        Assert.assertEquals("OBSOLETE: use imaging technique (MI:0428) and specific probe as feature of each interacting protein.", term4.getObsoleteMessage());
        Assert.assertEquals(1, term4.getPossibleTermsToRemapTo().size());
        Assert.assertEquals("MI:0428", term4.getPossibleTermsToRemapTo().iterator().next());
        Assert.assertNull(term4.getRemappedTerm());

        // term having one obsolete and no choices
        IntactOntologyTermI term5 = intactOntology.search("MI:0650");
        Assert.assertNotNull(term5);

        Assert.assertEquals("10E-3 moles per liter of solution.", term5.getDefinition());
        Assert.assertEquals("OBSOLETE: term redundant with the schema exponent attribute of the parameter.", term5.getObsoleteMessage());
        Assert.assertEquals(0, term5.getPossibleTermsToRemapTo().size());
        Assert.assertNull(term5.getRemappedTerm());
    }

    @Test
    public void test_exclude_mod(){
        IntactOntologyTermI term1 = intactOntology.search("MOD:00000");
        Assert.assertNull(term1);
    }
}

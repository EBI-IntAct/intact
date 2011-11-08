package uk.ac.ebi.intact.bridges.ontology_manager.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import uk.ac.ebi.intact.bridges.ontology_manager.TermDbXref;
import uk.ac.ebi.intact.bridges.ontology_manager.builders.ModOntologyTermBuilder;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.IntactOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.IntactOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

import java.net.URL;

/**
 * Tester of ModOntologyTerm
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/11/11</pre>
 */

public class ModOntologyTermTest {
    private IntactOntology intactOntology;

    @Before
    public void parseOboTest() throws OntologyLoaderException {
        IntactOboLoader parser = new IntactOboLoader(null, null, null, null, new ModOntologyTermBuilder());

        URL psiMiObo = ModOntologyTermTest.class.getResource("/psi-mod.obo");
        this.intactOntology = parser.parseOboFile(psiMiObo);
    }

    @Test
    public void test_synonyms(){
        // term with shortlabel defined in synonyms
        // it contains 2 aliases
        // this term also have 1 Pubmed and 1 Unimod
        IntactOntologyTermI term1 = intactOntology.search("MOD:01161");
        Assert.assertNotNull(term1);

        Assert.assertEquals("deoxygenated residue", term1.getFullName());
        Assert.assertEquals("doxyres", term1.getShortLabel());
        Assert.assertEquals(2, term1.getAliases().size());

        for (String alias : term1.getAliases()){
            if (!alias.equals("Deoxy") && !alias.equals("reduction")){
                Assert.assertTrue(false);
            }
        }

        Assert.assertEquals(2, term1.getDbXrefs().size());
        for (TermDbXref xref : term1.getDbXrefs()){
            if (xref.getDatabase().equals("pubmed") && xref.getDatabaseId().equals("MI:0446")){
                if (xref.getQualifier().equals("primary-reference") && xref.getQualifierId().equals("MI:0358")){
                    Assert.assertEquals("14235557", xref.getAccession());
                }
                else{
                    Assert.assertTrue(false);
                }
            }
            else if (xref.getDatabase().equals("unimod") && xref.getDatabaseId().equals("MI:1015")){
                if (xref.getQualifier().equals("identity") && xref.getQualifierId().equals("MI:0356")){
                    Assert.assertEquals("447", xref.getAccession());
                }
                else{
                    Assert.assertTrue(false);
                }
            }
            else {
                Assert.assertTrue(false);
            }
        }

        Assert.assertEquals("A protein modification that effectively removes oxygen atoms from a residue without the removal of hydrogen atoms.", term1.getDefinition());

        // term with no alias shortlabel. This term has a comment. This term has also one pubmed xref
        IntactOntologyTermI term2 = intactOntology.search("MOD:00003");
        Assert.assertNotNull(term2);

        Assert.assertEquals("UniMod", term2.getFullName());
        Assert.assertEquals("unimod", term2.getShortLabel());
        Assert.assertEquals(0, term2.getAliases().size());
        Assert.assertEquals("Entry from UniMod.", term2.getDefinition());
        Assert.assertEquals(1, term2.getComments().size());
        Assert.assertEquals("This term is for organizational use only and should not be assigned. [JSG]", term2.getComments().iterator().next());

        Assert.assertEquals(1, term2.getDbXrefs().size());
        for (TermDbXref xref : term2.getDbXrefs()){
            if (xref.getDatabase().equals("pubmed") && xref.getDatabaseId().equals("MI:0446")){
                if (xref.getQualifier().equals("primary-reference") && xref.getQualifierId().equals("MI:0358")){
                    Assert.assertEquals("18688235", xref.getAccession());
                }
                else{
                    Assert.assertTrue(false);
                }
            }
            else {
                Assert.assertTrue(false);
            }
        }

        // term with shortlabel defined in synonyms
        // it contains 15 aliases
        // this term also have 5 Pubmeds and 1 resid and 1 deltamass and 1 chebi
        IntactOntologyTermI term3 = intactOntology.search("MOD:00014");
        Assert.assertNotNull(term3);

        Assert.assertEquals("L-cysteine residue", term3.getFullName());
        Assert.assertEquals("cys", term3.getShortLabel());
        Assert.assertEquals(15, term3.getAliases().size());

        for (String alias : term3.getAliases()){
            if (!alias.equals("(2R)-2-amino-3-sulfanylpropanoic acid") && !alias.equals("(R)-cysteine") && !alias.equals("2-amino-3-mercaptopropanoic acid")
                    && !alias.equals("2-amino-3-mercaptopropionic acid") && !alias.equals("2-azanyl-3-sulfanylpropanoic acid") && !alias.equals("3-mercapto-L-alanine")
                    && !alias.equals("alpha-amino-beta-mercaptopropanoic acid") && !alias.equals("alpha-amino-beta-mercaptopropionic acid") && !alias.equals("alpha-amino-beta-thiolpropionic acid")
                    && !alias.equals("beta-mercaptoalanine") && !alias.equals("Cysteine (C, Cys)") && !alias.equals("half-cystine")
                    && !alias.equals("L-(+)-cysteine") && !alias.equals("L-cysteine") && !alias.equals("thioserine")){
                Assert.assertTrue(false);
            }
        }

        Assert.assertEquals(8, term3.getDbXrefs().size());
        for (TermDbXref xref : term3.getDbXrefs()){
            if (xref.getDatabase().equals("pubmed") && xref.getDatabaseId().equals("MI:0446")){
                if (xref.getQualifier().equals("primary-reference") && xref.getQualifierId().equals("MI:0358")){
                    if (!xref.getAccession().equals("1310545") && !xref.getAccession().equals("15790858") && !xref.getAccession().equals("3447159")
                            && !xref.getAccession().equals("6692818") && !xref.getAccession().equals("7338899")){
                        Assert.assertTrue(false);
                    }
                }
                else if (xref.getQualifier().equals("method reference") && xref.getQualifierId().equals("MI:0357")){
                    if (!xref.getAccession().equals("1310545") && !xref.getAccession().equals("15790858") && !xref.getAccession().equals("3447159")
                            && !xref.getAccession().equals("6692818") && !xref.getAccession().equals("7338899")){
                        Assert.assertTrue(false);
                    }
                }
                else{
                    Assert.assertTrue(false);
                }
            }
            else if (xref.getDatabase().equals("deltamass") && xref.getDatabaseId().equals("MI:1014")){
                if (xref.getQualifier().equals("identity") && xref.getQualifierId().equals("MI:0356")){
                    Assert.assertEquals("0", xref.getAccession());
                }
                else{
                    Assert.assertTrue(false);
                }
            }
            else if (xref.getDatabase().equals("chebi") && xref.getDatabaseId().equals("MI:0474")){
                if (xref.getQualifier().equals("identity") && xref.getQualifierId().equals("MI:0356")){
                    Assert.assertEquals("29950", xref.getAccession());
                }
                else{
                    Assert.assertTrue(false);
                }
            }
            else if (xref.getDatabase().equals("resid") && xref.getDatabaseId().equals("MI:0248")){
                if (xref.getQualifier().equals("identity") && xref.getQualifierId().equals("MI:0356")){
                    Assert.assertEquals("AA0005", xref.getAccession());
                }
                else{
                    Assert.assertTrue(false);
                }
            }
            else {
                Assert.assertTrue(false);
            }
        }

        Assert.assertEquals("A protein modification that effectively converts a source amino acid residue to an L-cysteine.", term3.getDefinition());
        Assert.assertEquals(1, term3.getComments().size());
        Assert.assertEquals("From DeltaMass: Average Mass: 121.", term3.getComments().iterator().next());

        // term with shortlabel defined in synonyms
        // it contains 3 aliases
        // this term also have 1 Unimod
        IntactOntologyTermI term4 = intactOntology.search("MOD:00890");
        Assert.assertNotNull(term4);

        Assert.assertEquals("phosphorylated L-histidine", term4.getFullName());
        Assert.assertEquals("nphoshis", term4.getShortLabel());
        Assert.assertEquals(3, term4.getAliases().size());

        for (String alias : term4.getAliases()){
            if (!alias.equals("Phospho") && !alias.equals("phosphohistidine") && !alias.equals("mod192")){
                Assert.assertTrue(false);
            }
        }

        Assert.assertEquals(1, term4.getDbXrefs().size());
        for (TermDbXref xref : term4.getDbXrefs()){
            if (xref.getDatabase().equals("unimod") && xref.getDatabaseId().equals("MI:1015")){
                if (xref.getQualifier().equals("identity") && xref.getQualifierId().equals("MI:0356")){
                    Assert.assertEquals("21", xref.getAccession());
                }
                else{
                    Assert.assertTrue(false);
                }
            }
            else {
                Assert.assertTrue(false);
            }
        }

        Assert.assertEquals("A protein modification that effectively converts an L-histidine residue to a phosphorylated L-histidine, such as pros-phosphohistidine, or tele-phosphohistidine.", term4.getDefinition());
    }

    @Test
    public void test_xref_definition(){
        // term with shortlabel not defined in synonyms
        // it contains URL xref
        IntactOntologyTermI term5 = intactOntology.search("MOD:01603");
        Assert.assertNotNull(term5);

        Assert.assertEquals("2x(15)N labeled L-lysine", term5.getFullName());
        Assert.assertEquals("2x(15)n labeled l-lysine", term5.getShortLabel());
        Assert.assertEquals(0, term5.getAliases().size());

        Assert.assertEquals(1, term5.getDbXrefs().size());
        for (TermDbXref xref : term5.getDbXrefs()){
            if (xref.getDatabase().equals("pubmed") && xref.getDatabaseId().equals("MI:0446")){
                if (xref.getQualifier().equals("primary-reference") && xref.getQualifierId().equals("MI:0358")){
                    Assert.assertEquals("18688235", xref.getAccession());
                }
                else{
                    Assert.assertTrue(false);
                }
            }
            else {
                Assert.assertTrue(false);
            }
        }
        Assert.assertEquals("http://www.sigmaaldrich.com/catalog/ProductDetail.do?N4=609021|ALDRICH&amp;N5=SEARCH_CONCAT_PNO|BRAND_KEY&amp;F=SPEC&amp;lang=en_US0.000000E+000", term5.getURL());
        Assert.assertEquals("A protein modification that effectively converts an L-lysine residue to 2x(15)N labeled L-lysine.", term5.getDefinition());
    }
}

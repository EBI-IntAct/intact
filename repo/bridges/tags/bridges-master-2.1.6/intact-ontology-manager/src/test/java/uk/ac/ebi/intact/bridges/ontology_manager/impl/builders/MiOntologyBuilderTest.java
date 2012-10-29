package uk.ac.ebi.intact.bridges.ontology_manager.impl.builders;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import psidev.psi.tools.ontology_manager.client.OlsClient;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import uk.ac.ebi.intact.bridges.ontology_manager.TermDbXref;
import uk.ac.ebi.intact.bridges.ontology_manager.builders.MiOntologyTermBuilder;
import uk.ac.ebi.intact.bridges.ontology_manager.client.IntactFilterOlsClient;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.impl.MiOntologyTermTest;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.IntactOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.IntactOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.MIOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * Tester of MiOntologyBuilder
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10/11/11</pre>
 */

public class MiOntologyBuilderTest {

    private MiOntologyTermBuilder termBuilder;

    @Before
    public void initializeBuilder(){
        termBuilder = new MiOntologyTermBuilder();
    }

    public void test_createFromTerm() throws OntologyLoaderException {

        MIOboLoader miOboloader = new MIOboLoader(null, null, null, null, termBuilder);

        URL psiMiObo = MiOntologyTermTest.class.getResource("/psi-mi.obo");
        IntactOntology intactOntology = miOboloader.parseOboFile(psiMiObo);

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
    }

    public void test_createFromOls() throws OntologyLoaderException, MalformedURLException, ServiceException, RemoteException {

        OlsClient olsClient = new IntactFilterOlsClient();

        // term with alias exact and shortlabel defined in synonyms
        // this term also have 3 PMIDs
        IntactOntologyTermI term1 = this.termBuilder.createIntactOntologyTermFrom("MI:0018", "two hybrid",
                olsClient.getTermMetadata("MI:0018", "MI"), olsClient.getTermXrefs("MI:0018", "MI"), olsClient.isObsolete("MI:0018", "MI"));
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
    }

    @Test
    public void test_createOboLoader(){
        IntactOboLoader oboLoader = this.termBuilder.createIntactOboLoader(null);

        Assert.assertTrue(oboLoader instanceof MIOboLoader);
    }
}

package uk.ac.ebi.intact.bridges.ontology_manager.impl.builders;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import psidev.psi.tools.ontology_manager.client.OlsClient;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import uk.ac.ebi.intact.bridges.ontology_manager.TermDbXref;
import uk.ac.ebi.intact.bridges.ontology_manager.builders.ModOntologyTermBuilder;
import uk.ac.ebi.intact.bridges.ontology_manager.client.IntactFilterOlsClient;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.impl.MiOntologyTermTest;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.IntactOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.IntactOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

/**
 * Tester of Mod ontologyTerm
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10/11/11</pre>
 */

public class ModOntologyTermBuilderTest {

    private ModOntologyTermBuilder termBuilder;

    @Before
    public void initializeBuilder(){
        termBuilder = new ModOntologyTermBuilder();
    }

    public void test_createFromTerm() throws OntologyLoaderException {

        IntactOboLoader miOboloader = new IntactOboLoader(null, null, null, null, termBuilder);

        URL modurl = MiOntologyTermTest.class.getResource("/psi-mod.obo");
        IntactOntology intactOntology = miOboloader.parseOboFile(modurl);

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
    }

    public void test_createFromOls() throws OntologyLoaderException, MalformedURLException, ServiceException, RemoteException {

        OlsClient olsClient = new IntactFilterOlsClient();

        IntactOntologyTermI term1 = this.termBuilder.createIntactOntologyTermFrom("MOD:01161", "deoxygenated residue",
                olsClient.getTermMetadata("MOD:01161", "MI"), olsClient.getTermXrefs("MOD:01161", "MI"), olsClient.isObsolete("MOD:01161", "MI"));
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
    }

    @Test
    public void test_createOboLoader(){
        IntactOboLoader oboLoader = this.termBuilder.createIntactOboLoader(null);

        Assert.assertTrue(oboLoader instanceof IntactOboLoader);
    }
}

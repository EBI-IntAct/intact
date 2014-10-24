package uk.ac.ebi.intact.bridges.ontology_manager.impl.impl.ols;

import org.junit.Assert;
import org.junit.Test;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import uk.ac.ebi.intact.bridges.ontology_manager.builders.EcoOntologyTermBuilder;
import uk.ac.ebi.intact.bridges.ontology_manager.builders.MiOntologyTermBuilder;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.ols.IntactOlsOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

import java.util.Set;

/**
 * Unit tester for IntactOlsOntology
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10/11/11</pre>
 */

public class IntactOlsOntologyTest {

    @Test
    public void test_ignore_synonyms() throws OntologyLoaderException {

        IntactOlsOntology olsOntology = new IntactOlsOntology(new MiOntologyTermBuilder());

        IntactOntologyTermI term = olsOntology.getTermForAccession("MI:0018");

        Assert.assertTrue(term.getNameSynonyms().isEmpty());
    }

    @Test
    public void test_eco_parent_cyclic() throws OntologyLoaderException {

        IntactOlsOntology olsOntology = new IntactOlsOntology(new EcoOntologyTermBuilder());

        IntactOntologyTermI term = olsOntology.getTermForAccession("ECO:0000353");

        Set<IntactOntologyTermI> parents = olsOntology.getDirectParents(term);

        Assert.assertEquals(2, parents.size());
    }

    @Test
    public void test_eco_children_cyclic() throws OntologyLoaderException {

        IntactOlsOntology olsOntology = new IntactOlsOntology(new EcoOntologyTermBuilder());

        IntactOntologyTermI term = olsOntology.getTermForAccession("ECO:0000203");

        Set<IntactOntologyTermI> children = olsOntology.getDirectChildren(term);

        Assert.assertEquals(0, children.size());
    }
}

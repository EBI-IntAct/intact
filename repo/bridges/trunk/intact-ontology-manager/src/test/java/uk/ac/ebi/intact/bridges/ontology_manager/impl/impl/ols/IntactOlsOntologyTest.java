package uk.ac.ebi.intact.bridges.ontology_manager.impl.impl.ols;

import org.junit.Assert;
import org.junit.Test;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import uk.ac.ebi.intact.bridges.ontology_manager.builders.MiOntologyTermBuilder;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.ols.IntactOlsOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

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
}

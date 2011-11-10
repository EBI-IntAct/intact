package uk.ac.ebi.intact.bridges.ontology_manager.impl.impl.local;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import uk.ac.ebi.intact.bridges.ontology_manager.builders.MiOntologyTermBuilder;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.IntactOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.MIOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

import java.net.URL;

/**
 * Unit tester for MiOboLoader
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10/11/11</pre>
 */

public class MiOboLoaderTest {

    private MIOboLoader parser;

        @Before
    public void createOboTest() throws OntologyLoaderException {
        parser = new MIOboLoader(null, null, null, null, new MiOntologyTermBuilder());
    }

    @Test
    public void test_filter_mod_terms() throws OntologyLoaderException {
        URL psiMiObo = MiOboLoaderTest.class.getResource("/psi-mi.obo");
        IntactOntology intactOntology = parser.parseOboFile(psiMiObo);

        IntactOntologyTermI term1 = intactOntology.search("MOD:00001");
        Assert.assertNull(term1);
    }
}

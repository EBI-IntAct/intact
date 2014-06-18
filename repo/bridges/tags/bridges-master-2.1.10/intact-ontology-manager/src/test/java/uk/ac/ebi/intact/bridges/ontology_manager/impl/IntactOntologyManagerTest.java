package uk.ac.ebi.intact.bridges.ontology_manager.impl;

import org.junit.Assert;
import org.junit.Test;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.ontology_manager.interfaces.OntologyAccessTemplate;
import uk.ac.ebi.intact.bridges.ontology_manager.IntactOntologyManager;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.IntactLocalOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.ols.IntactOlsOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

import java.io.IOException;
import java.io.InputStream;

/**
 * Unit tester of the IntactOntologyManager
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10/11/11</pre>
 */

public class IntactOntologyManagerTest {

    @Test
    public void test_simulation() throws IOException, OntologyLoaderException {

        InputStream ontology = IntactOntologyManagerTest.class.getResource("/ontologies.xml").openStream();
        IntactOntologyManager om = new IntactOntologyManager(ontology);
        ontology.close();

        Assert.assertEquals(2, om.getOntologyIDs().size());

        OntologyAccessTemplate<IntactOntologyTermI> accessMI = om.getOntologyAccess("MI");
        Assert.assertNotNull(accessMI);
        Assert.assertTrue(accessMI instanceof IntactLocalOntology);
        Assert.assertNotNull(accessMI.getTermForAccession("MI:0018"));
        Assert.assertNull(accessMI.getTermForAccession("MOD:01161"));


        OntologyAccessTemplate<IntactOntologyTermI> accessMOD = om.getOntologyAccess("MOD");
        Assert.assertNotNull(accessMOD);
        Assert.assertTrue(accessMOD instanceof IntactOlsOntology);
        Assert.assertNotNull(accessMOD.getTermForAccession("MOD:01161"));
        Assert.assertNull(accessMOD.getTermForAccession("MI:0018"));
    }
}

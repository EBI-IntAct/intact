package uk.ac.ebi.intact.bridges.ontology_manager.impl.local;

import psidev.psi.tools.ontology_manager.impl.local.AbstractLocalOntology;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.ontology_manager.interfaces.OntologyAccessTemplate;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

import java.io.File;

/**
 * Extension of intact local ontology for MI
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/11/11</pre>
 */

public class MiLocalOntology extends AbstractLocalOntology<IntactOntologyTermI, IntactOntology, IntactMIOboLoader> implements OntologyAccessTemplate<IntactOntologyTermI> {

    @Override
    protected IntactMIOboLoader createNewOBOLoader(File ontologyDirectory) throws OntologyLoaderException {
        return new IntactMIOboLoader(ontologyDirectory);
    }
}

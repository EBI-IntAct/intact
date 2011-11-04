package uk.ac.ebi.intact.bridges.ontology_manager.impl.ols;

import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.ModOntologyTerm;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

/**
 * Extension of IntactOls ontology for PSI-MOD
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/11/11</pre>
 */

public class OlsMODOntology extends IntactOlsOntology {


    public OlsMODOntology() throws OntologyLoaderException {
        super();
    }

    @Override
    protected IntactOntologyTermI createNewOntologyTermInstance(String identifier, String name) {
        return new ModOntologyTerm(identifier, name);
    }
}

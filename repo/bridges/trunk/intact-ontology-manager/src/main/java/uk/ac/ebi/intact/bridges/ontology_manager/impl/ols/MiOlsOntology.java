package uk.ac.ebi.intact.bridges.ontology_manager.impl.ols;

import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import uk.ac.ebi.intact.bridges.ontology_manager.client.IntactOlsClient;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.MiOntologyTerm;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

/**
 * Ols service for MI ontology
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/11/11</pre>
 */

public class MiOlsOntology extends IntactOlsOntology{

    public MiOlsOntology() throws OntologyLoaderException {
        super();
        // use specific OLS client for Intact
        try {
            this.olsClient = new IntactOlsClient();
        } catch ( Exception e ) {
            log.error( "Exception setting up intact OLS query client!", e );
            throw new OntologyLoaderException( "Exception setting up intact OLS query client!", e );
        }
    }

    @Override
    protected IntactOntologyTermI createNewOntologyTermInstance(String identifier, String name) {

        return new MiOntologyTerm(identifier, name);
    }
}

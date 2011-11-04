package uk.ac.ebi.intact.bridges.ontology_manager.impl.ols;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.ontology_manager.impl.ols.AbstractOlsOntology;
import psidev.psi.tools.ontology_manager.interfaces.OntologyAccessTemplate;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

import java.rmi.RemoteException;

/**
 * abstract Intact extension for OlsOntology
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public abstract class IntactOlsOntology extends AbstractOlsOntology<IntactOntologyTermI> implements OntologyAccessTemplate<IntactOntologyTermI> {

    public static final Log log = LogFactory.getLog(IntactOlsOntology.class);

    public IntactOlsOntology() throws OntologyLoaderException {
        super();
        // we don't need to load the synonyms, we will process them in another fashion
        this.useTermSynonyms = false;
    }

    @Override
    protected IntactOntologyTermI createNewOntologyTerm(String identifier, String name){
        IntactOntologyTermI term = createNewOntologyTermInstance(identifier, name);

        try {
            // load metadata
            term.loadSynonymsFrom(getAllTermSynonyms(identifier), olsClient.isObsolete(identifier, ontologyID));

            // loadXref
            term.loadXrefsFrom(getAllTermXrefs(identifier));

            return term;
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected abstract IntactOntologyTermI createNewOntologyTermInstance(String identifier, String name);
}

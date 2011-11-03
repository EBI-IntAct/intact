package uk.ac.ebi.intact.bridges.ontology_manager.impl.ols;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.ontology_manager.impl.ols.AbstractOlsOntology;
import psidev.psi.tools.ontology_manager.interfaces.OntologyAccessTemplate;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.MiOntologyTerm;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.ModOntologyTerm;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.OntologyName;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

import java.util.Map;

/**
 * Intact extension for OlsOntology
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class IntactOlsOntology extends AbstractOlsOntology<IntactOntologyTermI> implements OntologyAccessTemplate<IntactOntologyTermI> {

    public static final Log log = LogFactory.getLog(IntactOlsOntology.class);

    private Map<OntologyName, Class<? extends IntactOntologyTermI>> ontologyTerms;
    private OntologyName currentOntology = OntologyName.MI;

    public IntactOlsOntology() throws OntologyLoaderException {
        super();
    }

    @Override
    protected IntactOntologyTermI createNewOntologyTerm(String identifier, String name) {
        if (currentOntology == null){
            throw new IllegalStateException("The current ontology cannot be null. It can be MI, MOD, etc.");
        }

        switch (currentOntology){
            case MI: return new MiOntologyTerm(identifier, name);
            case MOD: return new ModOntologyTerm(identifier, name);
            default: return null;
        }
    }
}

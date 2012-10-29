package uk.ac.ebi.intact.bridges.ontology_manager.interfaces;

import uk.ac.ebi.ols.model.interfaces.Term;

import java.util.Map;

/**
 * Interface to implement for terms possible to initialize from a term or maps
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/11/11</pre>
 */

public interface LoadableOntologyTerm {

    public void loadTermFrom (Term term);
    public void loadSynonymsFrom (Map metadata, boolean isObsolete);
    public void loadXrefsFrom (Map xrefs);
}

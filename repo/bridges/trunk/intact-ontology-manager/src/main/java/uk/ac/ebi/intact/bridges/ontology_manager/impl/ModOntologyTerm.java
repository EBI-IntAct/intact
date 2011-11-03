package uk.ac.ebi.intact.bridges.ontology_manager.impl;

import uk.ac.ebi.ols.model.interfaces.Term;

import java.util.Map;

/**
 * Ontology term for PSI-MOD
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class ModOntologyTerm extends AbstractIntactOntologyTerm{
    public ModOntologyTerm(String acc, String name) {
        super(acc, name);
    }

    public ModOntologyTerm(String acc) {
        super(acc);
    }

    @Override
    public void loadTermFrom(Term term) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadSynonymsFrom(Map metadata) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadXrefsFrom(Map xrefs) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

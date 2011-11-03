package uk.ac.ebi.intact.bridges.ontology_manager.impl;

import org.obo.datamodel.OBOObject;
import uk.ac.ebi.ols.model.interfaces.Term;

import java.util.Map;

/**
 * Ontology term for PSI-MI
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class MiOntologyTerm extends AbstractIntactOntologyTerm {
    private static final String SHORTLABEL_IDENTIFIER = "PSI-MI-short";

    public MiOntologyTerm(String acc) {
        super(acc);
    }

    public MiOntologyTerm(String acc, String name) {
        super(acc, name);
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

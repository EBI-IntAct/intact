package uk.ac.ebi.intact.bridges.ontology_manager.builders;

import uk.ac.ebi.intact.bridges.ontology_manager.impl.MiOntologyTerm;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.IntactOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.MIOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;
import uk.ac.ebi.ols.model.interfaces.Term;

import java.io.File;
import java.util.Map;

/**
 * Builder for MI ontology term
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/11/11</pre>
 */

public class MiOntologyTermBuilder implements IntactOntologyTermBuilder{
    private static final String MI_ONTOLOGY_DEFINITION = "PSI MI";
    private static final String MI_FULL_NAME = "PSI Molecular Interactions";
    private static final String MI_SHORT_NAME = "PSI-MI";

    @Override
    public IntactOntologyTermI createIntactOntologyTermFrom(Term term) {
        MiOntologyTerm miTerm = new MiOntologyTerm(term.getIdentifier(), term.getName());
        miTerm.loadTermFrom(term);

        return miTerm;
    }

    @Override
    public IntactOntologyTermI createIntactOntologyTermFrom(String accession, String name, Map metadata, Map xrefs, boolean isObsolete) {

        MiOntologyTerm miTerm = new MiOntologyTerm(accession, name);
        miTerm.loadSynonymsFrom(metadata, isObsolete);
        miTerm.loadXrefsFrom(xrefs);

        return miTerm;
    }

    @Override
    public IntactOboLoader createIntactOboLoader(File ontologyDirectory) {
        return new MIOboLoader(ontologyDirectory, MI_ONTOLOGY_DEFINITION, MI_FULL_NAME, MI_SHORT_NAME, this);
    }
}

package uk.ac.ebi.intact.bridges.ontology_manager.builders;

import uk.ac.ebi.intact.bridges.ontology_manager.impl.ModOntologyTerm;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.IntactOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.MIOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;
import uk.ac.ebi.ols.model.interfaces.Term;

import java.io.File;
import java.util.Map;

/**
 * Builder for MOD ontology term
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/11/11</pre>
 */

public class ModOntologyTermBuilder implements IntactOntologyTermBuilder{
    private static final String MOD_ONTOLOGY_DEFINITION = "PSI MOD";
    private static final String MOD_FULL_NAME = "PSI Protein Modifications";
    private static final String MOD_SHORT_NAME = "PSI-MOD";

    @Override
    public IntactOntologyTermI createIntactOntologyTermFrom(Term term) {
        ModOntologyTerm modTerm = new ModOntologyTerm(term.getIdentifier(), term.getName());
        modTerm.loadTermFrom(term);

        return modTerm;
    }

    @Override
    public IntactOntologyTermI createIntactOntologyTermFrom(String accession, String name, Map metadata, Map xrefs, boolean isObsolete) {
        ModOntologyTerm modTerm = new ModOntologyTerm(accession, name);
        modTerm.loadSynonymsFrom(metadata, isObsolete);
        modTerm.loadXrefsFrom(xrefs);

        return modTerm;
    }

    @Override
    public IntactOboLoader createIntactOboLoader(File ontologyDirectory) {
        return new MIOboLoader(ontologyDirectory, MOD_ONTOLOGY_DEFINITION, MOD_FULL_NAME, MOD_SHORT_NAME, this);
    }
}

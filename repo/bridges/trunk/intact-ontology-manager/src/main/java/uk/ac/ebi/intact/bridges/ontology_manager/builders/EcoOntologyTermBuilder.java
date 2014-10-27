package uk.ac.ebi.intact.bridges.ontology_manager.builders;

import uk.ac.ebi.intact.bridges.ontology_manager.impl.AbstractIntactOntologyTerm;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.EcoOntologyTerm;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.IntactOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;
import uk.ac.ebi.ols.model.interfaces.Term;

import java.io.File;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Builder for ECO ontology term
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/11/11</pre>
 */

public class EcoOntologyTermBuilder implements IntactOntologyTermBuilder{
    private static final String ECO_ONTOLOGY_DEFINITION = "Evidence";
    private static final String ECO_FULL_NAME = "Evidence code";
    private static final String ECO_SHORT_NAME = "eco";

    private static final String databaseIdentifier = "MI:1331";
    private static final String MIParent = "MI:1331";

    @Override
    public IntactOntologyTermI createIntactOntologyTermFrom(Term term) {
        EcoOntologyTerm ecoTerm = new EcoOntologyTerm(term.getIdentifier(), term.getName());
        ecoTerm.loadTermFrom(term);

        return ecoTerm;
    }

    @Override
    public IntactOntologyTermI createIntactOntologyTermFrom(String accession, String name, Map metadata, Map xrefs, boolean isObsolete) {
        EcoOntologyTerm modTerm = new EcoOntologyTerm(accession, name);
        modTerm.loadSynonymsFrom(metadata, isObsolete);
        modTerm.loadXrefsFrom(xrefs);

        return modTerm;
    }

    @Override
    public IntactOboLoader createIntactOboLoader(File ontologyDirectory) {
        return new IntactOboLoader(ontologyDirectory, ECO_ONTOLOGY_DEFINITION, ECO_FULL_NAME, ECO_SHORT_NAME, this);
    }

    @Override
    public String getDatabaseIdentifier() {
        return databaseIdentifier;
    }

    @Override
    public String getParentFromOtherOntology() {
        return MIParent;
    }

    @Override
    public Pattern getDatabaseRegexp() {
        return AbstractIntactOntologyTerm.ECO_REGEXP;
    }
}

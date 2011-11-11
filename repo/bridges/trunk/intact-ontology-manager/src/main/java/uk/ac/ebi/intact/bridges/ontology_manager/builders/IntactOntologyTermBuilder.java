package uk.ac.ebi.intact.bridges.ontology_manager.builders;

import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.IntactOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;
import uk.ac.ebi.ols.model.interfaces.Term;

import java.io.File;
import java.util.Map;

/**
 * Interface for IntactOntologyTerm builders
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/11/11</pre>
 */

public interface IntactOntologyTermBuilder {

    public IntactOntologyTermI createIntactOntologyTermFrom(Term term);

    public IntactOntologyTermI createIntactOntologyTermFrom(String accession, String name, Map metadata, Map xrefs, boolean isObsolete);

    public IntactOboLoader createIntactOboLoader(File ontologyDirectory);

    public String getDatabaseIdentifier();

    public String getParentFromOtherOntology();
}

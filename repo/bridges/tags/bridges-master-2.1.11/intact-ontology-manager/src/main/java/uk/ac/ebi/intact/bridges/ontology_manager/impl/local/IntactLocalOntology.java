package uk.ac.ebi.intact.bridges.ontology_manager.impl.local;

import psidev.psi.tools.ontology_manager.impl.local.AbstractLocalOntology;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import uk.ac.ebi.intact.bridges.ontology_manager.builders.IntactOntologyTermBuilder;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyAccess;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

import java.io.File;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Intact implementation of LocalOntology access
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/11/11</pre>
 */

public class IntactLocalOntology extends AbstractLocalOntology<IntactOntologyTermI, IntactOntology, IntactOboLoader> implements IntactOntologyAccess {

    protected IntactOntologyTermBuilder termBuilder;

    public IntactLocalOntology(IntactOntologyTermBuilder termBuilder){
        super();
        if (termBuilder == null){
            throw new IllegalArgumentException("The IntactOntologyTerm builder must be non null");
        }
        this.termBuilder = termBuilder;
    }
    @Override
    protected IntactOboLoader createNewOBOLoader(File ontologyDirectory) throws OntologyLoaderException {
        return this.termBuilder.createIntactOboLoader(ontologyDirectory);
    }

    @Override
    public String getOntologyID() {
        return this.ontologyID;
    }

    @Override
    public String getDatabaseIdentifier() {
        return termBuilder.getDatabaseIdentifier();
    }

    @Override
    public String getParentFromOtherOntology() {
        return termBuilder.getParentFromOtherOntology();
    }

    @Override
    public Collection<IntactOntologyTermI> getRootTerms() {
        return this.ontology.getRoots();
    }

    @Override
    public Pattern getDatabaseRegexp() {
        return termBuilder.getDatabaseRegexp();
    }

    @Override
    public IntactOntologyTermBuilder getOntologyTermBuilder() {
        return termBuilder;
    }
}

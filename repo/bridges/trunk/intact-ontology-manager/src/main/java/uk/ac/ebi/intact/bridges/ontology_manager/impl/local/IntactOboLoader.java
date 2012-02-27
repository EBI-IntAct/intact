package uk.ac.ebi.intact.bridges.ontology_manager.impl.local;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import psidev.psi.tools.ontology_manager.impl.local.AbstractOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.builders.IntactOntologyTermBuilder;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;
import uk.ac.ebi.ols.loader.parser.OBO2FormatParser;
import uk.ac.ebi.ols.model.interfaces.Term;

import java.io.File;

/**
 * Intact obo loader
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/11/11</pre>
 */

public class IntactOboLoader extends AbstractOboLoader<IntactOntologyTermI, IntactOntology> {
    /**
     * Sets up a logger for that class.
     */
    protected static final Log log = LogFactory.getLog(IntactOboLoader.class);

    protected IntactOntologyTermBuilder termBuilder;

    public IntactOboLoader(File ontologyDirectory, String ontologyDefinition, String fullName, String shortName, IntactOntologyTermBuilder termBuilder) {
        super(ontologyDirectory);

        if (ontologyDefinition == null){
            ONTOLOGY_DEFINITION = "PSI MI";
            FULL_NAME = "PSI Molecular Interactions";
            SHORT_NAME = "PSI-MI";
        }
        else {
            ONTOLOGY_DEFINITION = ontologyDefinition;
        }

        if (fullName == null){
            FULL_NAME = "PSI Molecular Interactions";
        }
        else {
            FULL_NAME = fullName;
        }

        if (shortName == null){
            SHORT_NAME = "PSI-MI";
        }
        else {
            SHORT_NAME = shortName;
        }

        if (termBuilder == null){
            throw new IllegalArgumentException("The IntactOntologyTerm builder must be non null");
        }
        this.termBuilder = termBuilder;
    }

    @Override
    protected void configure(String filePath) {
        logger = Logger.getLogger(IntactOboLoader.class);

        try {
            parser = new OBO2FormatParser(filePath);
        } catch (Exception e) {
            logger.fatal("Parse failed: " + e.getMessage(), e);
        }
    }

    @Override
    protected IntactOntology createNewOntology() {
        return new IntactOntology();
    }

    @Override
    protected IntactOntologyTermI createNewOntologyTerm(Term t) {
        return this.termBuilder.createIntactOntologyTermFrom(t);
    }
}

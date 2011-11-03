package uk.ac.ebi.intact.bridges.ontology_manager.impl.local;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import psidev.psi.tools.ontology_manager.impl.local.AbstractOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.ModOntologyTerm;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;
import uk.ac.ebi.ols.loader.parser.OBOFormatParser;

/**
 * iNTACT extension of OBOLoader for MOD ontology
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class IntactMODOboLoader extends AbstractOboLoader<IntactOntologyTermI, IntactOntology> {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog(IntactMODOboLoader.class);

    public IntactMODOboLoader( ) {
        super(null);
    }

    /////////////////////////////
    // AbstractLoader's methods

    protected void configure() {
        /**
         * ensure we get the right logger
         */
        logger = Logger.getLogger(IntactMODOboLoader.class);

        parser = new OBOFormatParser();
        ONTOLOGY_DEFINITION = "PSI MOD";
        FULL_NAME = "PSI Protein Modifications";
        SHORT_NAME = "PSI-MOD";
    }

    @Override
    protected IntactOntology createNewOntology() {
        return new IntactOntology();
    }

    @Override
    protected IntactOntologyTermI createNewOntologyTerm(String identifier, String name) {
        return new ModOntologyTerm( identifier, name );
    }
}

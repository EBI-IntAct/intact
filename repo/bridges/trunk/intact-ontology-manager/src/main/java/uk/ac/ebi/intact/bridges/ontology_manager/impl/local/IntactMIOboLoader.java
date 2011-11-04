package uk.ac.ebi.intact.bridges.ontology_manager.impl.local;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.obo.datamodel.OBOSession;
import psidev.psi.tools.ontology_manager.impl.local.AbstractOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.MiOntologyTerm;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;
import uk.ac.ebi.ols.loader.parser.OBOFormatParser;
import uk.ac.ebi.ols.model.interfaces.Term;

import java.util.ArrayList;
import java.util.List;

/**
 * Intact extension of OBOLoader
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class IntactMIOboLoader extends AbstractOboLoader<IntactOntologyTermI, IntactOntology> {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog(IntactMODOboLoader.class);
    //file location for OBO file pointing directly to psi cvs (not working! redirect to the intact web page)
    public static final String PSI_MI_OBO_LOCATION = "http://www.ebi.ac.uk/~intact/psi/mi/rel25/data/psi-mi25.obo";
    private static final String MI_ROOT_IDENTIFIER = "MI:0000";

    private OBOSession oboSession;

    public IntactMIOboLoader( ) {
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
        ONTOLOGY_DEFINITION = "PSI MI";
        FULL_NAME = "PSI Molecular Interactions";
        SHORT_NAME = "PSI-MI";
    }

    @Override
    protected IntactOntology createNewOntology() {
        return new IntactOntology();
    }

    @Override
    protected IntactOntologyTermI createNewOntologyTerm(Term t) {
        IntactOntologyTermI term = new MiOntologyTerm( t.getIdentifier(), t.getName() );

        term.loadTermFrom(t);

        return term;
    }

    @Override
    /**
     * Remove the MOD terms which could be present in the file
     */
    protected IntactOntology buildOntology() {
        List<Term> terms = new ArrayList<Term>(ontBean.getTerms());
        for ( Term t : terms ) {

            if (!SHORT_NAME.equals(t.getNamespace())){
                ontBean.getTerms().remove(t);
            }
        }

        return super.buildOntology();
    }
}

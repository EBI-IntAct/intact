package uk.ac.ebi.intact.bridges.ontology_manager.impl.local;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import psidev.psi.tools.ontology_manager.impl.local.AbstractOboLoader;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.MiOntologyTerm;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;
import uk.ac.ebi.ols.loader.parser.OBOFormatParser;
import uk.ac.ebi.ols.model.interfaces.Term;
import uk.ac.ebi.ols.model.interfaces.TermRelationship;
import uk.ac.ebi.ols.model.interfaces.TermSynonym;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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

    public IntactMIOboLoader( File ontologyDirectory) {
        super(ontologyDirectory);
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

            if (t.getNamespace() != null && !SHORT_NAME.equals(t.getNamespace())){
                ontBean.getTerms().remove(t);
            }
        }

        IntactOntology ontology = createNewOntology();

        // 1. convert and index all terms (note: at this stage we don't handle the hierarchy)
        for ( Iterator iterator = ontBean.getTerms().iterator(); iterator.hasNext(); ) {
            Term term = ( Term ) iterator.next();

            if (term.getNamespace() != null && !SHORT_NAME.equals(term.getNamespace())){
                continue;
            }
            else {
                // convert term into a OboTerm
                IntactOntologyTermI ontologyTerm = createNewOntologyTerm( term );
                final Collection<TermSynonym> synonyms = term.getSynonyms();
                if( synonyms != null ) {
                    for ( TermSynonym synonym : synonyms ) {
                        ontologyTerm.getNameSynonyms().add( synonym.getSynonym() );
                    }
                }

                ontology.addTerm( ontologyTerm );

                if ( term.isObsolete() ) {
                    ontology.addObsoleteTerm( ontologyTerm );
                }
            }
        }

        buildTermRelationships(ontology);

        return ontology;
    }

    @Override
    protected void buildTermRelationships(IntactOntology ontology) {
        // 2. build hierarchy based on the relations of the Terms
        for ( Iterator iterator = ontBean.getTerms().iterator(); iterator.hasNext(); ) {
            Term term = ( Term ) iterator.next();

            if ( term.getRelationships() != null ) {
                for ( Iterator iterator1 = term.getRelationships().iterator(); iterator1.hasNext(); ) {
                    TermRelationship relation = ( TermRelationship ) iterator1.next();

                    if (relation.getObjectTerm().getNamespace() != null && !SHORT_NAME.equals(relation.getObjectTerm().getNamespace())) {
                        continue;
                    }
                    else if (relation.getSubjectTerm().getNamespace() != null && !SHORT_NAME.equals(relation.getSubjectTerm().getNamespace())) {
                        continue;
                    }
                    else {
                        ontology.addLink( relation.getObjectTerm().getIdentifier(),
                                relation.getSubjectTerm().getIdentifier() );
                    }
                }
            }
        }
    }
}

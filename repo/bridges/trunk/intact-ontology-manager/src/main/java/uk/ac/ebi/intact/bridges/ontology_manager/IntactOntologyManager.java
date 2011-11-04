package uk.ac.ebi.intact.bridges.ontology_manager;

import psidev.psi.tools.ontology_manager.OntologyManagerTemplate;
import psidev.psi.tools.ontology_manager.interfaces.OntologyAccessTemplate;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.MiLocalOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.ModLocalOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.ols.MiOlsOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.ols.ModOlsOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

import java.util.HashMap;
import java.util.Map;

/**
 * Intact extension of the ontologyManager
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class IntactOntologyManager extends OntologyManagerTemplate<IntactOntologyTermI, OntologyAccessTemplate<IntactOntologyTermI>> {

    private static final String FILE_SOURCE = "file";
    private static final String OLS_SOURCE = "ols";
    private static final String MI = "MI";
    private static final String MOD = "MOD";

    protected static final Map<String, Class> keyword2classFile = new HashMap<String, Class>();
    protected static final Map<String, Class> keyword2classOLS = new HashMap<String, Class>();

    static {
        keyword2classFile.put( "MI", MiLocalOntology.class );
        keyword2classFile.put( "MOD", ModLocalOntology.class );

        keyword2classOLS.put( "MI", MiOlsOntology.class );
        keyword2classOLS.put( "MOD", ModOlsOntology.class );
    }

    @Override
    protected Class findLoader(String loaderClass, String lcLoaderClass, String ontologyId) throws ClassNotFoundException {
        Map<String, Class> keyword2class = null;
        Class loader;

        if (FILE_SOURCE.equalsIgnoreCase(ontologyId)){
            keyword2class = keyword2classFile;
        }
        else if (OLS_SOURCE.equalsIgnoreCase(ontologyId)){
            keyword2class = keyword2classOLS;
        }
        else {
            loader = Class.forName( loaderClass );
        }

        if ( keyword2class.containsKey( ontologyId ) ) {
            loader = keyword2class.get( ontologyId );
            if ( log.isDebugEnabled() ) {
                log.debug( "the source '" + ontologyId + "' was converted to Class: " + loader );
            }
        } else {
            loader = Class.forName( loaderClass );
        }

        return loader;
    }
}

package uk.ac.ebi.intact.bridges.ontology_manager;

import psidev.psi.tools.ontology_manager.OntologyManagerTemplate;
import psidev.psi.tools.ontology_manager.client.OlsClient;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import uk.ac.ebi.intact.bridges.ontology_manager.builders.IntactOntologyTermBuilder;
import uk.ac.ebi.intact.bridges.ontology_manager.builders.MiOntologyTermBuilder;
import uk.ac.ebi.intact.bridges.ontology_manager.builders.ModOntologyTermBuilder;
import uk.ac.ebi.intact.bridges.ontology_manager.client.IntactFilterOlsClient;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.local.IntactLocalOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.impl.ols.IntactOlsOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyAccess;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

import javax.xml.rpc.ServiceException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Intact extension of the ontologyManager
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class IntactOntologyManager extends OntologyManagerTemplate<IntactOntologyTermI, IntactOntologyAccess> {

    private static final String FILE_SOURCE = "file";
    private static final String OLS_SOURCE = "ols";
    private static final String MI = "MI";
    private static final String MOD = "MOD";

    protected final static Map<String, Class> keyword2class = new HashMap<String, Class>();

    static {
        keyword2class.put( "MI", MiOntologyTermBuilder.class );
        keyword2class.put( "MOD", ModOntologyTermBuilder.class );
    }

    public IntactOntologyManager(InputStream configFile) throws OntologyLoaderException {
        super(configFile);
    }

    @Override
    protected IntactOntologyAccess findOntologyAccess(String sourceURI, String ontologyId, String ontologyName, String ontologyVersion, String format, String loaderClass) throws ClassNotFoundException {
        Class builderClass;

        if ( keyword2class.containsKey( ontologyId ) ) {
            builderClass = keyword2class.get( ontologyId );
            if ( log.isDebugEnabled() ) {
                log.debug( "the source '" + ontologyId + "' was converted to Class: " + builderClass );
            }

            try {
                IntactOntologyTermBuilder termBuilder = (IntactOntologyTermBuilder) builderClass.newInstance();

                if (FILE_SOURCE.equalsIgnoreCase(loaderClass)){
                    IntactLocalOntology localOntology = new IntactLocalOntology(termBuilder);
                    return localOntology;
                }
                else if (OLS_SOURCE.equalsIgnoreCase(loaderClass) && MI.equals(ontologyId)){
                    OlsClient intactClient = new IntactFilterOlsClient();
                    IntactOlsOntology olsOntology = new IntactOlsOntology(termBuilder, intactClient);

                    return olsOntology;
                }
                else if (OLS_SOURCE.equalsIgnoreCase(loaderClass)){
                    IntactOlsOntology olsOntology = new IntactOlsOntology(termBuilder);

                    return olsOntology;
                }
                else {
                    throw new IllegalArgumentException("The loader class in the configuration class must be file or ols.");
                }

            } catch (InstantiationException e) {
                throw new ClassNotFoundException("Impossible to instantiate " + builderClass, e);
            } catch (IllegalAccessException e) {
                throw new ClassNotFoundException("Impossible to instantiate " + builderClass, e);
            } catch (OntologyLoaderException e) {
                e.printStackTrace();
                return null;
            } catch (ServiceException e) {
                e.printStackTrace();
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            throw new IllegalArgumentException("The ontology " + ontologyId + " is not used in Intact and does not have a ontology builder.");
        }
    }
}

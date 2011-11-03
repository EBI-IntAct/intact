package uk.ac.ebi.intact.bridges.ontology_manager.impl.local;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.tools.ontology_manager.impl.local.AbstractLocalOntology;
import psidev.psi.tools.ontology_manager.impl.local.AbstractOboLoader;
import psidev.psi.tools.ontology_manager.impl.local.LocalOntology;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.ontology_manager.interfaces.OntologyAccessTemplate;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Intact extension of local ontology access
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class IntactLocalOntology extends AbstractLocalOntology<IntactOntologyTermI, IntactOntology, AbstractOboLoader<IntactOntologyTermI, IntactOntology>> implements OntologyAccessTemplate<IntactOntologyTermI> {

    public static final Log log = LogFactory.getLog(LocalOntology.class);

    private Map<OntologyName, Class<? extends AbstractOboLoader<IntactOntologyTermI, IntactOntology>>> oboLoaders;

    private OntologyName currentOntology = OntologyName.MI;

    public IntactLocalOntology() {
        super();
        oboLoaders = new HashMap<OntologyName, Class<? extends AbstractOboLoader<IntactOntologyTermI, IntactOntology>>>();
    }

    private void initializeOboLoaders(){
        oboLoaders.put(OntologyName.MI, IntactMIOboLoader.class);
        oboLoaders.put(OntologyName.MOD, IntactMODOboLoader.class);
    }

    @Override
    protected AbstractOboLoader<IntactOntologyTermI, IntactOntology> createNewOBOLoader(File ontologyDirectory) throws OntologyLoaderException {
        if (currentOntology == null){
            throw new OntologyLoaderException("The current ontology cannot be null. It can be MI, MOD, etc.");
        }
        else if (!oboLoaders.containsKey(currentOntology)){
            throw new OntologyLoaderException(currentOntology + " does not match any OBOLoaders.");
        }
        try {
            return oboLoaders.get(currentOntology).newInstance();
        } catch (InstantiationException e) {
            throw new OntologyLoaderException("Impossible to create an OBOLoader of type " + oboLoaders.get(currentOntology), e);
        } catch (IllegalAccessException e) {
            throw new OntologyLoaderException("Impossible to create an OBOLoader of type " + oboLoaders.get(currentOntology), e);
        }
    }


    public OntologyName getCurrentOntology() {
        return currentOntology;
    }

    public void setCurrentOntology(OntologyName currentOntology) {
        this.currentOntology = currentOntology;
    }
}

package uk.ac.ebi.intact.bridges.ontology_manager.impl.ols;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.tools.ontology_manager.client.OlsClient;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.ontology_manager.impl.ols.AbstractOlsOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.builders.IntactOntologyTermBuilder;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyAccess;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * abstract Intact extension for OlsOntology
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class IntactOlsOntology extends AbstractOlsOntology<IntactOntologyTermI> implements IntactOntologyAccess {

    public static final Log log = LogFactory.getLog(IntactOlsOntology.class);

    protected IntactOntologyTermBuilder termBuilder;

    public IntactOlsOntology(IntactOntologyTermBuilder termBuilder) throws OntologyLoaderException {
        super();
        // we don't need to load the synonyms, we will process them in another fashion
        this.useTermSynonyms = false;
        if (termBuilder == null){
            throw new IllegalArgumentException("The IntactOntologyTerm builder must be non null");
        }
        this.termBuilder = termBuilder;
    }

    public IntactOlsOntology(IntactOntologyTermBuilder termBuilder, OlsClient olsClient) throws OntologyLoaderException {
        super();
        // we don't need to load the synonyms, we will process them in another fashion
        this.useTermSynonyms = false;
        if (termBuilder == null){
            throw new IllegalArgumentException("The IntactOntologyTerm builder must be non null");
        }
        this.termBuilder = termBuilder;

        if (olsClient == null){
            throw new IllegalArgumentException("The OlsClient must be non null");
        }
        this.olsClient = olsClient;
    }

    @Override
    protected IntactOntologyTermI createNewOntologyTerm(String identifier, String name){
        try {
            return termBuilder.createIntactOntologyTermFrom(identifier, name, getAllTermSynonyms(identifier), getAllTermXrefs(identifier), olsClient.isObsolete(identifier, ontologyID));
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
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
        Collection<IntactOntologyTermI> roots = new ArrayList<IntactOntologyTermI>(rootAccs.size());

        for (String acc : rootAccs){
            IntactOntologyTermI term = getTermForAccession(acc);

            if (term != null){
                roots.add(term);
            }
        }
        return roots;
    }
}

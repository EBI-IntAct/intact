package uk.ac.ebi.intact.bridges.ontology_manager.impl.ols;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.tools.ontology_manager.client.OlsClient;
import psidev.psi.tools.ontology_manager.impl.local.OntologyLoaderException;
import psidev.psi.tools.ontology_manager.impl.ols.AbstractOlsOntology;
import uk.ac.ebi.intact.bridges.ontology_manager.builders.IntactOntologyTermBuilder;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyAccess;
import uk.ac.ebi.intact.bridges.ontology_manager.interfaces.IntactOntologyTermI;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.regex.Pattern;

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

    @Override
    public Pattern getDatabaseRegexp() {
        return termBuilder.getDatabaseRegexp();
    }

    @Override
    public IntactOntologyTermBuilder getOntologyTermBuilder() {
        return termBuilder;
    }

    /**
     * This method looks up the direct parents of the given OntologyTermI.
     * Note: this method is cached and only operates directly on OLS if
     * the query has not been performed before.
     *
     * @param term the OntologyTermI for which to look up its direct parents.
     * @return a Set of OntologyTermIs of the direct parents of the given term.
     */
    @Override
    public Set<IntactOntologyTermI> getDirectParents( IntactOntologyTermI term ) {
        Set<IntactOntologyTermI> directParents = super.getDirectParents(term);
        Iterator<IntactOntologyTermI> parentIterator = directParents.iterator();

        while (parentIterator.hasNext()){
            IntactOntologyTermI parent = parentIterator.next();

            try {
                Map<String, String> relations = olsClient.getQuery().getTermRelations(parent.getTermAccession(), ontologyID);

                if (relations.containsKey(term.getTermAccession())){
                    String relation = relations.get(term.getTermAccession());
                    // remove other parents to avoid cyclic dependencies
                    if (relation == null || (!relation.equals("part_of") && !relation.equals("is_a"))){
                        parentIterator.remove();
                    }
                }
            } catch (ServiceException e) {
                throw new IllegalStateException( "RemoteException while trying to connect to OLS." );
            } catch (RemoteException e) {
                throw new IllegalStateException( "RemoteException while trying to connect to OLS." );
            }
        }

        return directParents;
    }

    /**
     * Method to retrieve only the direct child terms of the specified
     * ontology term. This method uses the getChildren(OntologyTermI, int)
     * method with a level of 1 to retrieve the direct children on the
     * specified term.
     *
     * @param term the ontology term to get the child terms for.
     * @return a Set containing the direct child terms of the specified term.
     */
    @Override
    public Set<IntactOntologyTermI> getDirectChildren( IntactOntologyTermI term ) {
        Set<IntactOntologyTermI> directChildren = super.getDirectChildren(term);
        Iterator<IntactOntologyTermI> childrenIterator = directChildren.iterator();

        try {
            Map<String, String> relations = olsClient.getQuery().getTermRelations(term.getTermAccession(), ontologyID);

            while (childrenIterator.hasNext()){
                IntactOntologyTermI child = childrenIterator.next();

                if (relations.containsKey(child.getTermAccession())){
                    String relation = relations.get(child.getTermAccession());
                    // remove other parents to avoid cyclic dependencies
                    if (relation == null || (!relation.equals("part_of") && !relation.equals("is_a"))){
                        childrenIterator.remove();
                    }
                }
            }
        } catch (ServiceException e) {
            throw new IllegalStateException( "RemoteException while trying to connect to OLS." );
        } catch (RemoteException e) {
            throw new IllegalStateException( "RemoteException while trying to connect to OLS." );
        }

        return directChildren;
    }
}

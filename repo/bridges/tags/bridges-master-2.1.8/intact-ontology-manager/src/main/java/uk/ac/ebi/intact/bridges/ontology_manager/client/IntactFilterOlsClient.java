package uk.ac.ebi.intact.bridges.ontology_manager.client;

import psidev.psi.tools.ontology_manager.client.OlsClient;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * OLS client for IntAct
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/11/11</pre>
 */

public class IntactFilterOlsClient extends OlsClient{
    public IntactFilterOlsClient() throws MalformedURLException, ServiceException {
        super();
    }

    @Override
    public Map getTermXrefs(String termAccession, String ontologyId) throws RemoteException {
        if (termAccession.startsWith(ontologyId)){
            return super.getTermXrefs(termAccession, ontologyId);
        }

        return Collections.EMPTY_MAP;
    }

    @Override
    public Map getTermMetadata(String termAccession, String ontologyId) throws RemoteException {
        if (termAccession.startsWith(ontologyId)){
            return super.getTermMetadata(termAccession, ontologyId);
        }

        return Collections.EMPTY_MAP;
    }

    @Override
    public String getTermById(String accession, String ontologyId) throws RemoteException {
        if (accession.startsWith(ontologyId)){
            return super.getTermById(accession, ontologyId);
        }

        return null;
    }

    @Override
    public Map getTermParents(String termAccession, String ontologyId) throws RemoteException {
        if (termAccession.startsWith(ontologyId)){
            Map parents = super.getTermParents(termAccession, ontologyId);

            Set<Object> keys = new HashSet<Object>(parents.keySet());
            for (Object key : keys){
                String term = (String) key;

                if (!term.startsWith(ontologyId)){
                    parents.remove(term);
                }
            }

            return parents;
        }

        return Collections.EMPTY_MAP;
    }

    @Override
    public Map getTermChildren(String termAccession, String ontologyId, int level, int[] relationships) throws RemoteException {
        if (termAccession.startsWith(ontologyId)){
            Map children = super.getTermChildren(termAccession, ontologyId, level, relationships);

            Set<Object> keys = new HashSet<Object>(children.keySet());

            for (Object key : keys){
                String term = (String) key;

                if (!term.startsWith(ontologyId)){
                    children.remove(term);
                }
            }
            return children;
        }

        return Collections.EMPTY_MAP;
    }

    @Override
    public boolean isObsolete(String termAccession, String ontologyId) throws RemoteException {
        if (termAccession.startsWith(ontologyId)){
            return super.isObsolete(termAccession, ontologyId);
        }

        return true;
    }
}

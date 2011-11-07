package uk.ac.ebi.intact.bridges.ontology_manager.client;

import psidev.psi.tools.ontology_manager.client.OlsClient;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Map;

/**
 * OLS client for IntAct
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/11/11</pre>
 */

public class IntactOlsClient extends OlsClient{
    public IntactOlsClient() throws MalformedURLException, ServiceException {
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

            for (Object key : parents.keySet()){
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

            for (Object key : children.keySet()){
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
    public Map getRootTerms(String ontologyId) throws RemoteException {
        return super.getRootTerms(ontologyId);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean isObsolete(String termAccession, String ontologyId) throws RemoteException {
        if (termAccession.startsWith(ontologyId)){
            return super.isObsolete(termAccession, ontologyId);
        }

        return false;
    }
}

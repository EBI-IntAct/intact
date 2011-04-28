package intact.solution.psicquic.universalclient;

import org.hupo.psi.mi.psicquic.wsclient.UniversalPsicquicClient;
import psidev.psi.mi.search.SearchResult;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;

import java.util.Collection;

/**
 * Download interactions from PSICQUIC, using the universal client.
 */
public class PsicquicQuery {

    public static void main(String[] args) throws Exception {
        // the universal client uses the SOAP service URL, obtainable from the Registry

        // Exercise: search for "bbc1" in IntAct

        String soapServiceAddress = "http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/psicquic";

        UniversalPsicquicClient client = new UniversalPsicquicClient(soapServiceAddress);
        SearchResult<BinaryInteraction> results = client.getByQuery("brca2", 0, Integer.MAX_VALUE);

        // Print the results in the console
        System.out.println( "Interactions found: " + results.getTotalCount() );

        for ( BinaryInteraction<?> binaryInteraction : results.getData() ) {
            String idA = getFirstIdentifier( binaryInteraction.getInteractorA().getIdentifiers() );
            String idB = getFirstIdentifier( binaryInteraction.getInteractorB().getIdentifiers() );

            String interactionAc = getFirstIdentifier( binaryInteraction.getInteractionAcs() );

            System.out.println( "\tInteraction (" + interactionAc + "): " + idA + " interacts with " + idB );
        }
    }

    private static String getFirstIdentifier( Collection<CrossReference> identifiers ) {
        if ( !identifiers.isEmpty() ) {
            return identifiers.iterator().next().getIdentifier();
        }
        return null;
    }
}

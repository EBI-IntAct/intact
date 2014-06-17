package uk.ac.ebi.intact.kickstart.psicquic;

import org.hupo.psi.mi.psicquic.wsclient.UniversalPsicquicClient;
import org.hupo.psi.mi.psicquic.wsclient.result.MitabSearchResult;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;

/**
 * This example does not need the database to work and shows how to access the EBI IntAct database remotely.
 */
public class QuerySinglePsicquic {

    public static void main( String[] args ) throws Exception {
        // change the endpoint address as needed
        UniversalPsicquicClient client =
                new UniversalPsicquicClient("http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/psicquic");

        MitabSearchResult result = client.getByInteractor("brca2", 0, 50);

        // Print the results in the console
        System.out.println("Interactions found: "+result.getTotalCount());

        for ( BinaryInteraction binaryInteraction : result.getData()) {
            String interactorIdA = binaryInteraction.getInteractorA().getIdentifiers().iterator().next().getIdentifier();
            String interactorIdB = binaryInteraction.getInteractorB().getIdentifiers().iterator().next().getIdentifier();

            CrossReference cr = (CrossReference) binaryInteraction.getInteractionAcs().iterator().next();
            String interactionAc = cr.getIdentifier();

            System.out.println("\tInteraction ("+interactionAc+"): "+interactorIdA+" interacts with "+interactorIdB);
        }
    }
}

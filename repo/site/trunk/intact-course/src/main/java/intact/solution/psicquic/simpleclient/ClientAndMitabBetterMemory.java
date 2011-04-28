package intact.solution.psicquic.simpleclient;

import org.hupo.psi.mi.psicquic.wsclient.PsicquicSimpleClient;
import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Querying a PSICQUIC service and reading the results using the MITAB library.
 */
public class ClientAndMitabBetterMemory {

    public static void main(String[] args) throws Exception {
        // get a REST URl from the registry http://www.ebi.ac.uk/Tools/webservices/psicquic/registry/registry?action=STATUS

        PsicquicSimpleClient client = new PsicquicSimpleClient("http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/");

        PsimiTabReader mitabReader = new PsimiTabReader(false);

        try {
            final InputStream result = client.getByQuery("brca2");

            BufferedReader in = new BufferedReader(new InputStreamReader(result));

            String line;

            while ((line = in.readLine()) != null) {
                BinaryInteraction binaryInteraction = mitabReader.readLine(line);

                processBinaryInteraction(binaryInteraction);
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processBinaryInteraction(BinaryInteraction<?> binaryInteraction) {
        // print first ids for interactors and interaction
        CrossReference idA = binaryInteraction.getInteractorA().getIdentifiers().iterator().next();
        CrossReference idB = binaryInteraction.getInteractorB().getIdentifiers().iterator().next();
        CrossReference interactionAc = binaryInteraction.getInteractionAcs().iterator().next();

        System.out.println("Interaction "+interactionAc.getIdentifier()+": "+idA.getIdentifier()+" - "+idB.getIdentifier());
    }

}

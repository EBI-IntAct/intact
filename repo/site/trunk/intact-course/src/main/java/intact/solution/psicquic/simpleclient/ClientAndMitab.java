package intact.solution.psicquic.simpleclient;

import org.hupo.psi.mi.psicquic.wsclient.PsicquicSimpleClient;
import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Querying a PSICQUIC service and reading the results using the MITAB library.
 */
public class ClientAndMitab {

    public static void main(String[] args) throws Exception {
        // get a REST URl from the registry http://www.ebi.ac.uk/Tools/webservices/psicquic/registry/registry?action=STATUS

        PsicquicSimpleClient client = new PsicquicSimpleClient("http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/");

        PsimiTabReader mitabReader = new PsimiTabReader(false);

        try {
            InputStream result = client.getByQuery("brca2");

            Collection<BinaryInteraction> binaryInteractions = mitabReader.read(result);

            System.out.println("Interactions found: "+binaryInteractions.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

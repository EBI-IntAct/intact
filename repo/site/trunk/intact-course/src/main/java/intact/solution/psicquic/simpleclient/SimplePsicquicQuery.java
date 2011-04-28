package intact.solution.psicquic.simpleclient;

import org.hupo.psi.mi.psicquic.wsclient.PsicquicSimpleClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Download interactions from PSICQUIC, using the simple client.
 */
public class SimplePsicquicQuery {

     public static void main(String[] args) throws Exception {
        // get a REST URl from the registry http://www.ebi.ac.uk/Tools/webservices/psicquic/registry/registry?action=STATUS

        PsicquicSimpleClient client = new PsicquicSimpleClient("http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/");

        final long count = client.countByQuery("brca2");

        System.out.println("Count: "+count);

        System.out.println("\nMITAB:\n");

        try {
            final InputStream result = client.getByQuery("brca2");

            BufferedReader in = new BufferedReader(new InputStreamReader(result));

            String line;

            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

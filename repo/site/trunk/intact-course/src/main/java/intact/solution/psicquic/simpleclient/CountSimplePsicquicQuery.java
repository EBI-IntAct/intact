package intact.solution.psicquic.simpleclient;

import org.hupo.psi.mi.psicquic.wsclient.PsicquicSimpleClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Download interactions from PSICQUIC, using the simple client.
 */
public class CountSimplePsicquicQuery {

     public static void main(String[] args) throws Exception {
        // get a REST URl from the registry http://www.ebi.ac.uk/Tools/webservices/psicquic/registry/registry?action=STATUS

        PsicquicSimpleClient client = new PsicquicSimpleClient("http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/");

        // miql query
        String miqlQuery = "pubid:16189514";

        final long count = client.countByQuery(miqlQuery);

        System.out.println("Count: "+count);
    }

}

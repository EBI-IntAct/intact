package intact.solution.psicquic.universalclient;

import org.hupo.psi.mi.psicquic.wsclient.XmlPsicquicClient;
import org.hupo.psi.mi.psicquic.wsclient.XmlSearchResult;
import psidev.psi.mi.xml.model.Entry;
import psidev.psi.mi.xml.model.EntrySet;
import psidev.psi.mi.xml.model.Interaction;
import psidev.psi.mi.xml.model.Participant;

/**
 * Download interactions from PSICQUIC, using the universal client.
 */
public class XmlPsicquicQuery {

    public static void main(String[] args) throws Exception {
        // the universal client uses the SOAP service URL, obtainable from the Registry

        // Exercise: search for "bbc1" in IntAct

        String soapServiceAddress = "http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/psicquic";

        XmlPsicquicClient client = new XmlPsicquicClient(soapServiceAddress);
        final XmlSearchResult xmlResult = client.getByQuery("brca2", 0, Integer.MAX_VALUE);

        // Print the results in the console
        System.out.println( "Interactions found: " + xmlResult.getTotalResults() );

        final EntrySet entrySet = xmlResult.getEntrySet();

        printEntrySet(entrySet);

    }

    private static void printEntrySet(EntrySet entrySet) {
        for (Entry entry : entrySet.getEntries()) {
            for (Interaction interaction : entry.getInteractions()) {
                System.out.println("Interaction: "+interaction.getXref().getPrimaryRef().getId());

                for (Participant participant : interaction.getParticipants()) {
                    System.out.println("\tParticipant: "+participant.getInteractor().getXref().getPrimaryRef().getId());
                }
            }
        }
    }
}

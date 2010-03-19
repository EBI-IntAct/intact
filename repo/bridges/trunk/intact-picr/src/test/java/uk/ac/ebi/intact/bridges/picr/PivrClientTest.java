package uk.ac.ebi.intact.bridges.picr;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.kraken.interfaces.uniparc.UniParcEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * PicrClient Tester
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16-Mar-2010</pre>
 */

public class PivrClientTest {

    private PicrClient client;

    @Before
    public void before() {
        client = new PicrClient();
    }

    @After
    public void after() {
        client = null;
    }

    @Test
    public void getSWISSPROTAccession() {
        ArrayList<String> upis = client.getSwissprotIdsForAccession("NP_417804", null);

        for (String upi : upis){
            System.out.println(upi);
        }
        Assert.assertEquals(4, upis.size());
    }

    @Test
    public void getTREMBLAccession() {
        ArrayList<String> upis = client.getTremblIdsForAccession("NP_417804", null);

        for (String upi : upis){
            System.out.println(upi);
        }
        Assert.assertEquals(2, upis.size());
    }

    @Test
    public void getSWISSPROTAccessionFromSequence() {
        ArrayList<String> upis = client.getSwissprotIdsForSequence("MRFAIVVTGPAYGTQQASSAFQFAQALIADGHELSSVFFYREGVYNANQLTSPASDEFDLVRAWQQLNAQHGVALNICVAAALRRGVVDETEAGRLGLASSNLQQGFTLSGLGALAEASLTCDRVVQF", null);

        for (String upi : upis){
            System.out.println(upi);
        }
        Assert.assertEquals(4, upis.size());
    }

    @Test
    public void getTREMBLAccessionFromSequence() {
        ArrayList<String> upis = client.getTremblIdsForSequence("MRFAIVVTGPAYGTQQASSAFQFAQALIADGHELSSVFFYREGVYNANQLTSPASDEFDLVRAWQQLNAQHGVALNICVAAALRRGVVDETEAGRLGLASSNLQQGFTLSGLGALAEASLTCDRVVQF", null);

        for (String upi : upis){
            System.out.println(upi);
        }
        Assert.assertEquals(2, upis.size());
    }

    @Test
    public void getUniprotEntryFromAccession(){
        List<UniProtEntry> uniprotEntries = client.getUniprotEntryForAccession("P45532");

        for (UniProtEntry upi : uniprotEntries){
            System.out.println(upi.getUniProtId().getValue());
        }
        Assert.assertEquals(1, uniprotEntries.size());
    }

    @Test
    public void getUniparcEntryFromAccession(){
        List<UniParcEntry> uniprotEntries = client.getUniparcEntryForAccession("P45532");

        for (UniParcEntry upi : uniprotEntries){
            System.out.println(upi.getUniParcId().getValue());
        }
        Assert.assertEquals(1, uniprotEntries.size());
    }
}

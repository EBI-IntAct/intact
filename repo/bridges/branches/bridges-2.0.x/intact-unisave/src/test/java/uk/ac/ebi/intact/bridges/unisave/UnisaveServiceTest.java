package uk.ac.ebi.intact.bridges.unisave;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.uniprot.unisave.EntryVersionInfo;

import java.util.Iterator;
import java.util.List;

/**
 * UnisaveService Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.3
 */
public class UnisaveServiceTest {

    @Test
    public void getVersions() throws Exception {
        UnisaveService service = new UnisaveService();
        final List<EntryVersionInfo> versions = service.getVersions( "P12345", false );
        Assert.assertNotNull( versions );
        Assert.assertEquals( 58, versions.size() );
    }

    /*
         Primary (citable) accession number: Q96IZ0
         Secondary accession number(s): O75796, Q6FHY9, Q8N700
                         isSecondary  isNotSecondary
         primary id          ex            49       (Q96IZ0)
         secondary id        36            18       (Q6FHY9)
     */

//    @Test
//    public void getVersions_1() throws Exception {
//        UnisaveService service = new UnisaveService();
//        try {
//            final List<EntryVersionInfo> versions = service.getVersions( "Q96IZ0", false );
//
//            System.out.println( "returned " + versions.size() + " versions" );
//            for ( EntryVersionInfo version : versions ) {
//                System.out.println( "PrimaryAccession:"+ version.getPrimaryAccession() +"  EntryId:" + version.getEntryId() + " EntryVersion:"+ version.getEntryVersion() +" seqVersion " + version.getSequenceVersion() );
//            }
//
//            Assert.fail( "Q96IZ0 is a secondary identifier" );
//        } catch ( UnisaveServiceException e ) {
//            // ok
//        }
//    }

    @Test
    public void getVersions_2() throws Exception {
        UnisaveService service = new UnisaveService();
        final List<EntryVersionInfo> versions = service.getVersions( "Q98753", false );
        Assert.assertNotNull( versions );
    }

    @Test
    public void getVersions_invalidIdentifier() throws Exception {
        UnisaveService service = new UnisaveService();
        try {
            final List<EntryVersionInfo> versions = service.getVersions( "PXXXXX", false );
            Assert.assertNotNull( versions );
            Assert.assertEquals(0, versions.size());

            Assert.fail( "An exception should be thrown when querying with an invalid identifier." );
        } catch ( UnisaveServiceException e ) {
            // ok
        }
    }

    @Test
    public void getFastaSequence() throws Exception {
        UnisaveService service = new UnisaveService();
        EntryVersionInfo info = new EntryVersionInfo();
        info.setEntryId( "82418239" );
        final FastaSequence fastaSequence = service.getFastaSequence( info );
        Assert.assertNotNull( fastaSequence );
        Assert.assertEquals( "Swiss-Prot|P12345|Release 12.0|01-OCT-1989", fastaSequence.getHeader() );
        Assert.assertEquals( "SSWWAHVEMGPPDPILGVTEAYKRDTNSKK", fastaSequence.getSequence() );
    }

    @Test
    public void getFastaSequence_unknownEntry() throws Exception {
        UnisaveService service = new UnisaveService();
        EntryVersionInfo info = new EntryVersionInfo();
        String id = String.valueOf( Integer.MAX_VALUE );
        info.setEntryId( id );
        try {
            service.getFastaSequence( info );
            Assert.fail( "Service should fail upon searching for unknown entries: " + id );
        } catch ( UnisaveServiceException e ) {
            // ok
        }
    }

    @Test
    public void getAvailableSequenceUpdate_P12345() throws Exception {

        // example of a sequence that doesn't have any update through its history

        UnisaveService service = new UnisaveService();
        final List<SequenceVersion> updates = service.getAvailableSequenceUpdate( "P12345", false, "SSWWAHVEMGPPDPILGVTEAYKRDTNSKK" );
        Assert.assertNotNull( updates );
        Assert.assertEquals( 1, updates.size() );
        final SequenceVersion sv = updates.iterator().next();
        Assert.assertNotNull( sv );
        Assert.assertNotNull( sv.getSequence() );
        Assert.assertEquals( "SSWWAHVEMGPPDPILGVTEAYKRDTNSKK", sv.getSequence().getSequence() );
        Assert.assertEquals( "UniProtKB/Swiss-Prot|P12345|Release 15.2/57.2|05-MAY-2009", sv.getSequence().getHeader() );
        Assert.assertEquals( 1, sv.getVersion() );
    }

    @Test
    public void getAvailableSequenceUpdate_Q98753_version1() throws Exception {

        // example of a sequence that has multiple updates (1 and 2) through its history, we are searching with version 1

        UnisaveService service = new UnisaveService();
        final List<SequenceVersion> updates = service.getAvailableSequenceUpdate( "Q98753", false, "XXX" );
        Assert.assertNotNull( updates );
        Assert.assertEquals( 2, updates.size() );
        SequenceVersion sv;
        final Iterator<SequenceVersion> updateIterator = updates.iterator();

        sv = updateIterator.next();
        Assert.assertNotNull( sv );
        Assert.assertNotNull( sv.getSequence() );
        Assert.assertEquals( "XPFLSKAVRCGPVIPFVIHHFNFRRVTTTKRRRNKYVLVPGYGWVLQDDYLVNSVKMTGENDLPPNQLPHDDDLLFTYAKILLYDYISYFPKFRHNNPDLLDHKTELELFPLKADSAARNKANFYARTLWNDTITDKSAFKPGTYNDTVAGLLLWQQCALMWSLPKSVINRTISGVCDALTNRTSLTLLKRISDWLKQLGLACSPIHRLFIELPTLLGRGAIPGDADKDIKHRLAFDPSITVDVPKEQLHLLIYRLLSRNLNITKVNSFEHHLEERLLWSKSGSHYYPDDKINELLPPQPTRKEFLDVVTTEYIKECKPQVFIRQSRKLEHGKERFIYNCDTVSYVYFDFILKLFETGWQDSEAILSPGDYTSERLHAKISSYKYKAMLDYTDFNSQHTIQSMRLIFETMKELLPPEATFALDWCIASFDNMQTSDGLKWMATLPSGHRATTFINTVLNWCYTQMVGLKFDSFMCAGDDVILMSQQPISLAPILTSHFKFNPSKQSTGTRGEFLRKHYSEAGVFAYPCRAIASLVSGNWLSQSLRENTPILVPIQNGIDRLRSRAGLLGVPWKLGLSELIEREAIPKEVGMALLNSHAAGPGLITRDYSSFTVTPKPPKLSSTLEYTATRYGLQDLSKHVPWKQLTTVESDKLSRQIKKISYRHCSQAKITYNCTYEVFKPRGLPTVLSGSSQPSLSMLWWQAMLKQAIQDDSTKKIDARMFAANACTSSVSGDAFLRANASMAGVLITSLITSSS", sv.getSequence().getSequence() );
        Assert.assertEquals( "TrEMBL|Q98753|Release 13|01-MAY-2000", sv.getSequence().getHeader() );
        Assert.assertEquals( 1, sv.getVersion() );

        sv = updateIterator.next();
        Assert.assertNotNull( sv );
        Assert.assertNotNull( sv.getSequence() );
        Assert.assertEquals( "VPFLSKAVRCGPVIPFVIHHFNFRRVTTTKRRRNKYVLVPGYGWVLQDDYLVNSVKMTGENDLPPNQLPHDDDLLFTYAKILLYDYISYFPKFRHNNPDLLDHKTELELFPLKADSAARNKANFYARTLWNDTITDKSAFKPGTYNDTVAGLLLWQQCALMWSLPKSVINRTISGVCDALTNRTSLTLLKRISDWLKQLGLACSPIHRLFIELPTLLGRGAIPGDADKDIKHRLAFDPSITVDVPKEQLHLLIYRLLSRNLNITKVNSFEHHLEERLLWSKSGSHYYPDDKINELLPPQPTRKEFLDVVTTEYIKECKPQVFIRQSRKLEHGKERFIYNCDTVSYVYFDFILKLFETGWQDSEAILSPGDYTSERLHAKISSYKYKAMLDYTDFNSQHTIQSMRLIFETMKELLPPEATFALDWCIASFDNMQTSDGLKWMATLPSGHRATTFINTVLNWCYTQMVGLKFDSFMCAGDDVILMSQQPISLAPILTSHFKFNPSKQSTGTRGEFLRKHYSEAGVFAYPCRAIASLVSGNWLSQSLRENTPILVPIQNGIDRLRSRAGLLGVPWKLGLSELIEREAIPKEVGMALLNSHAAGPGLITRDYSSFTVTPKPPKLSSTLEYTATRYGLQDLSKHVPWKQLTTVESDKLSRQIKKISYRHCSQAKITYNCTYEVFKPRGLPTVLSGSSQPSLSMLWWQAMLKQAIQDDSTKKIDARMFAANACTSSVSGDAFLRANASMAGVLITSLITSSS", sv.getSequence().getSequence() );
        Assert.assertEquals( "UniProtKB/TrEMBL|Q98753|Release 14.0/39.0|22-JUL-2008", sv.getSequence().getHeader() );
        Assert.assertEquals( 2, sv.getVersion() );
    }

    @Test
    public void getAvailableSequenceUpdate_sequenceMismatch() throws Exception {

        // example of a sequence that doesn't have any update through its history

        UnisaveService service = new UnisaveService();
        final List<SequenceVersion> updates = service.getAvailableSequenceUpdate( "P12345", false, "SSWWAH" );
        Assert.assertNotNull( updates );
        Assert.assertEquals( 1, updates.size() );
    }
}

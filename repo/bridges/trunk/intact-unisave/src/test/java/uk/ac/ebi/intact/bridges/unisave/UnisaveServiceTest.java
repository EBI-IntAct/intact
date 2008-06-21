package uk.ac.ebi.intact.bridges.unisave;

import static org.junit.Assert.*;
import org.junit.*;
import uk.ac.ebi.uniprot.unisave.EntryVersionInfo;

import java.util.List;

/**
 * UnisaveService Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since 2.0.3
 * @version $Id$
 */
public class UnisaveServiceTest {

    @Test
    public void getVersions() throws Exception {
        UnisaveService service = new UnisaveService();
        final List<EntryVersionInfo> versions = service.getVersions( "P12345", false );
        Assert.assertNotNull( versions );
        Assert.assertEquals( 22, versions.size() );
    }    

    @Test
    public void getFastaSequence() throws Exception {
        UnisaveService service = new UnisaveService();
        EntryVersionInfo info = new EntryVersionInfo();
        info.setEntryId( "82418239" );
        final FastaSequence fastaSequence = service.getFastaSequence( info );
        Assert.assertNotNull( fastaSequence );
        Assert.assertEquals( "Swiss-Prot|P12345|Release 12.0|01-OCT-1989", fastaSequence.getHeader());
        Assert.assertEquals( "SSWWAHVEMGPPDPILGVTEAYKRDTNSKK", fastaSequence.getSequence());
    }
}

package uk.ac.ebi.intact.bridges.imexcentral;

import org.junit.Ignore;
import org.junit.Test;

/**
 * DefaultImexCentralClient Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.1.2
 */
public class ImexCentralClientTest {

    @Test
    public void getPublicationImexAccession() throws Exception {
//        final DefaultImexCentralClient client = new DefaultImexCentralClient();
//        Assert.assertEquals( "IM-11903", client.getPublicationImexAccession( "17474147" ) );
    }

    @Test
    @Ignore
    public void updateAdminGroup() throws Exception {

        String endpoint = DefaultImexCentralClient.IC_TEST;
//        String endpoint = "http://dip.doe-mbi.ucla.edu:55508/icentral/ws";
        DefaultImexCentralClient client = new DefaultImexCentralClient( "skerrien", "240178", endpoint );

//        client.createPublicationById( "1" );
//        client.updatePublicationAdminGroup( "1", Operation.DROP, "INTACT" );
//        client.updatePublicationAdminGroup( "1", Operation.ADD, "INTACT" );

//        client.updatePublicationAdminUser( "1", Operation.DROP, "INTACT" );
        client.updatePublicationAdminUser( "1", Operation.ADD, "skerrien" );
    }
}

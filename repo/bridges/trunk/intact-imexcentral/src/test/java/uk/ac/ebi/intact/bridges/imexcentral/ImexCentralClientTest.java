package uk.ac.ebi.intact.bridges.imexcentral;

import junit.framework.Assert;
import org.junit.Test;

/**
 * ImexCentralClient Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.1.2
 */
public class ImexCentralClientTest {

    @Test
    public void getPublicationImexAccession() throws Exception {
        final ImexCentralClient client = new ImexCentralClient();
        Assert.assertEquals( "IM-11903", client.getPublicationImexAccession( "17474147" ) );
    }
}

package uk.ac.ebi.intact.bridges.citexplore.util;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.bridges.citexplore.exceptions.InvalidPubmedException;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/12/12</pre>
 */

public class PubmedIdCheckerTest {

    @Test
    public void isPubmedId() throws Exception {
        Assert.assertTrue(PubmedIdChecker.isPubmedId("1234567"));
        Assert.assertFalse(PubmedIdChecker.isPubmedId("unassigned4"));
    }

    @Test
    public void ensureValidFormat_valid() throws Exception {
        PubmedIdChecker.ensureValidFormat("1234567");

        Assert.assertTrue(true);
    }

    @Test (expected = InvalidPubmedException.class )
    public void ensureValidFormat_invalid() throws Exception {
        PubmedIdChecker.ensureValidFormat("unassigned3");
    }
}
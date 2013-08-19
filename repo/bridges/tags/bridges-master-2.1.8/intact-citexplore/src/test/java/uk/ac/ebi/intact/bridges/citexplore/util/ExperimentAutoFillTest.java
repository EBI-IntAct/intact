package uk.ac.ebi.intact.bridges.citexplore.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/12/12</pre>
 */

public class ExperimentAutoFillTest {

    @Test
    public void experimentAutoFill_default() throws Exception {

        ExperimentAutoFill eaf = new ExperimentAutoFill("15084279");

        Assert.assertEquals("Drebrin is a novel connexin-43 binding partner that links gap junctions to the submembrane cytoskeleton.",
                eaf.getFullname());

        Assert.assertEquals("butkevich-2004", eaf.getShortlabel());
    }
}

/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.cdb;

import junit.framework.JUnit4TestAdapter;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.DataAccessTest;


/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>03-Apr-2006</pre>
 */
public class ExperimentAutoFillTest extends DataAccessTest
{
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(ExperimentAutoFillTest.class);
    }

    @Test
    public void workingCaseNormal() throws UnexpectedException,
                                           PublicationNotFoundException,
                                           IntactException
    {
        IntactHelper helper = getHelper();

        ExperimentAutoFill expAuto = new ExperimentAutoFill("9010225");

        Assert.assertEquals("biesova-1997-1", expAuto.getShortlabel(helper));
        Assert.assertEquals("Isolation and characterization of e3B1, an eps8 binding protein that regulates cell growth.", expAuto.getFullname());
        Assert.assertEquals("Oncogene (0950-9232)", expAuto.getJournal());
        Assert.assertEquals("Biesova Z., Piccoli C., Wong WT.", expAuto.getAuthorList());

        Assert.assertFalse(expAuto.hasAuthorEmail());
        Assert.assertNull(expAuto.getAuthorEmail());
    }

    @Test
    public void workingCaseMoreThanOnePubPerYear() throws UnexpectedException,
                                                          PublicationNotFoundException,
                                                          IntactException
    {
        IntactHelper helper = getHelper();

        ExperimentAutoFill expAuto = new ExperimentAutoFill("12130660");

        Assert.assertEquals("li-2002-1", expAuto.getShortlabel(helper));
        Assert.assertEquals("Tissue-specific regulation of retinal and pituitary precursor cell proliferation.", expAuto.getFullname());
        Assert.assertEquals("Science (0036-8075)", expAuto.getJournal());
        Assert.assertEquals("Li X., Perissi V., Liu F., Rose DW., Rosenfeld MG.", expAuto.getAuthorList());

        Assert.assertFalse(expAuto.hasAuthorEmail());
        Assert.assertNull(expAuto.getAuthorEmail());
    }

    @Test(expected = UnexpectedException.class)
    public void invalidPubmedId1() throws UnexpectedException,
                                          PublicationNotFoundException,
                                          IntactException
    {
        ExperimentAutoFill expAuto = new ExperimentAutoFill("-1");

    }

    @Test(expected = NumberFormatException.class)
    public void invalidPubmedId2() throws UnexpectedException,
                                          PublicationNotFoundException,
                                          IntactException
    {
        ExperimentAutoFill expAuto = new ExperimentAutoFill("blabla");

    }

    @Test(expected = NullPointerException.class)
    public void invalidPubmedIdNull() throws UnexpectedException,
                                             PublicationNotFoundException,
                                             IntactException
    {
        ExperimentAutoFill expAuto = new ExperimentAutoFill(null);

    }
}

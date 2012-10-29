/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.citexplore;

import junit.framework.Assert;
import org.junit.Test;
import uk.ac.ebi.cdb.webservice.Citation;
import uk.ac.ebi.cdb.webservice.JournalIssue;

/**
 * CitexploreClient Tester.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class CitexploreClientTest {

    @Test
    public void testGetDateOfPublication() {
        CitexploreClient citexploreClient = new CitexploreClient();
        Citation c = citexploreClient.getCitationById( "1234567" );
        Assert.assertNotNull( c );
        Assert.assertNotNull( c.getTitle() );
        JournalIssue journalIssue = c.getJournalIssue();
        Assert.assertNotNull( c.getJournalIssue() );
        short date = journalIssue.getYearOfPublication();
        Assert.assertEquals( 1975, date );
    }
}
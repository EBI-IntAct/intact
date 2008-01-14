/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.citexplore;

import junit.framework.TestCase;
import uk.ac.ebi.cdb.webservice.Citation;
import uk.ac.ebi.cdb.webservice.JournalIssue;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class CitexploreClientTest extends TestCase {
    public void testGetDateOfPublication() {
        CitexploreClient citexploreClient = new CitexploreClient();
        Citation c =  citexploreClient.getCitationById("1234567");
//        Citation c =  citexploreClient.getCitationById("17634282");
        assertNotNull(c);
        JournalIssue journalIssue =  c.getJournalIssue();
        assertNotNull(journalIssue);
        String date = journalIssue.getDateOfPublication();
        assertNotNull(date);
        date = date.trim();
        System.out.println("date = [" + date + "]");
        assertEquals("1975",date);
    }
}
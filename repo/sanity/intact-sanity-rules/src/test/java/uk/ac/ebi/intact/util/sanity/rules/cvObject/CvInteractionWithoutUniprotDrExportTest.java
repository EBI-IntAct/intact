/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.rules.cvObject;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.util.sanity.exception.SanityCheckerException;
import uk.ac.ebi.intact.util.sanity.rules.messages.GeneralMessage;
import uk.ac.ebi.intact.model.CvInteraction;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.mocks.cvInteractions.CvInteractionWithNoAnnotationMock;
import uk.ac.ebi.intact.mocks.cvInteractions.CoSedimentationMock;
import uk.ac.ebi.intact.mocks.cvDatabases.PubmedMock;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class CvInteractionWithoutUniprotDrExportTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CvInteractionWithoutUniprotDrExportTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( CvInteractionWithoutUniprotDrExportTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCheck() throws SanityCheckerException {
        // Check that if we give the check method a cvInteraction with no annotation it return a message
        CvInteraction cvInteraction = CvInteractionWithNoAnnotationMock.getMock();
        CvInteractionWithoutUniprotDrExport rule = new CvInteractionWithoutUniprotDrExport();
        Collection<GeneralMessage> messages = rule.check(cvInteraction);
        assertEquals(1,messages.size());
        for(GeneralMessage message : messages){
            assertEquals(CvInteractionWithoutUniprotDrExport.getDescription(),message.getDescription());
            assertEquals(CvInteractionWithoutUniprotDrExport.getSuggestion(),message.getProposedSolution());
        }

        // Check that if we give the check method a cvInteraction with the uniprot-dr-export annotation, it does not
        // return any message
        cvInteraction = CoSedimentationMock.getMock();
        messages = rule.check(cvInteraction);
        assertEquals(0,messages.size());

        // Check that if we give the check method a cvDatabase  it does not
        // return any message
        CvDatabase pubmed = PubmedMock.getMock();
        messages = rule.check(pubmed);
        assertEquals(0,messages.size());
    }
}
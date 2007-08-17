/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.protein;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.mocks.proteins.P08050Mock;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.exception.SanityCheckerException;
import uk.ac.ebi.intact.sanity.rules.messages.GeneralMessage;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class NotValidCrc64Test extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public NotValidCrc64Test ( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( NotValidCrc64Test.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCheck() throws SanityCheckerException {
        
        Protein protein = P08050Mock.getMock();
        NotValidCrc64 rule = new NotValidCrc64();
        Collection<GeneralMessage> messages = rule.check(protein);
        assertEquals(0,messages.size());

        protein.setSequence("POISSON");
        messages = rule.check(protein);
        assertEquals(1,messages.size());
        for(GeneralMessage message : messages){
            assertEquals(NotValidCrc64.getDescription(), message.getDescription());
            assertEquals(NotValidCrc64.getSuggestion(), message.getProposedSolution());
        }


    }

}
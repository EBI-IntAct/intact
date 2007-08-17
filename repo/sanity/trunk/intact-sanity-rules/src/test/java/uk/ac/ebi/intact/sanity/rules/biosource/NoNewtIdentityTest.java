/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.biosource;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.bioSources.BioSourceWithNoXrefMock;
import uk.ac.ebi.intact.mocks.cvDatabases.NewtMock;
import uk.ac.ebi.intact.mocks.cvDatabases.PubmedMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.BioSourceXref;
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
public class NoNewtIdentityTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public NoNewtIdentityTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( NoNewtIdentityTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCheck() throws SanityCheckerException {

        // Test that we get back a message if we give to the check method a bioSource without xref at all.
        BioSource biosource = BioSourceWithNoXrefMock.getMock();
        NoNewtIdentity rule = new NoNewtIdentity();
        Collection<GeneralMessage> messages =  rule.check(biosource);

        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(NoNewtIdentity.getDescription(), message.getDescription());
            assertEquals(NoNewtIdentity.getSuggestion(), message.getProposedSolution());
        }

        // Test that we get back 1 message if we give to the check method a bioSource with 1 identity xref to pubmed.
        biosource = BioSourceWithNoXrefMock.getMock();
        BioSourceXref identityToPubmed = XrefMock.getMock(BioSourceXref.class, PubmedMock.getMock(), IdentityMock.getMock(), "1");
        biosource.addXref(identityToPubmed);
        messages =  rule.check(biosource);

        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(NoNewtIdentity.getDescription(), message.getDescription());
            assertEquals(NoNewtIdentity.getSuggestion(), message.getProposedSolution());
        }

        // Test that we get no message back if we give to the check method a bioSource without 1 identity xref to pubmed
        // and one identity xref to newt.
        biosource = BioSourceWithNoXrefMock.getMock();
        identityToPubmed = XrefMock.getMock(BioSourceXref.class, PubmedMock.getMock(), IdentityMock.getMock(), "1");
        BioSourceXref identityToNewt = XrefMock.getMock(BioSourceXref.class, NewtMock.getMock(), IdentityMock.getMock(), "9606");
        biosource.addXref(identityToPubmed);
        biosource.addXref(identityToNewt);
        messages =  rule.check(biosource);

        assertEquals(0, messages.size());


    }
}
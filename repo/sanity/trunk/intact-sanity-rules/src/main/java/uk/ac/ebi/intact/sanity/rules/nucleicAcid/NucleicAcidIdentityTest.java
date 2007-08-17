/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.nucleicAcid;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import uk.ac.ebi.intact.sanity.exception.SanityCheckerException;
import uk.ac.ebi.intact.sanity.rules.messages.GeneralMessage;
import uk.ac.ebi.intact.model.NucleicAcid;
import uk.ac.ebi.intact.model.InteractorXref;
import uk.ac.ebi.intact.mocks.nucleicAcids.AnfRatGeneMock;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.mocks.cvDatabases.DDBJMock;
import uk.ac.ebi.intact.mocks.cvDatabases.PubmedMock;

import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class NucleicAcidIdentityTest extends TestCase {


    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public NucleicAcidIdentityTest ( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( NucleicAcidIdentityTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCheck() throws SanityCheckerException {
        /***************************************************************
         Test with a right Nucleic Acid that has one identity to DDBJ
        ****************************************************************/
        NucleicAcid na = AnfRatGeneMock.getMock();
        NucleicAcidIdentity rule = new NucleicAcidIdentity();
        Collection<GeneralMessage> messages = rule.check(na);
        assertEquals(0,messages.size());

        /***************************************************************
         Test with a wrong Nucleic Acid that has 2 identities to DDBJ
         ****************************************************************/
        InteractorXref xref = XrefMock.getMock(InteractorXref.class, DDBJMock.getMock(), IdentityMock.getMock(),"K02062");
        na.addXref(xref);
        messages = rule.check(na);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(NucleicAcidIdentity.getMultipleIdentityDescription(), message.getDescription());
            assertEquals(NucleicAcidIdentity.getMultipleIdentitySuggestion(), message.getProposedSolution());
        }

        /***************************************************************
         Test with a wrong Nucleic Acid that has 1 identities to Pubmed
         ****************************************************************/
        Collection<InteractorXref> xrefs = new ArrayList<InteractorXref>();
        xref = XrefMock.getMock(InteractorXref.class, PubmedMock.getMock(), IdentityMock.getMock(),"1");
        xrefs.add(xref);
        na.setXrefs(xrefs);
        messages = rule.check(na);
        assertEquals(2,messages.size());
        boolean hasNotAllowedIdentity = false;
        boolean hasNoIdentity = false;
        for(GeneralMessage message : messages){
            if(NucleicAcidIdentity.getNonAllowedIdentityDescription().equals(message.getDescription())){
                assertEquals(NucleicAcidIdentity.getNonAllowedIdentitySuggestion(),message.getProposedSolution());
                hasNotAllowedIdentity = true;
            }
            else if(NucleicAcidIdentity.getNoIdentityDescription().equals(message.getDescription())){
                assertEquals(NucleicAcidIdentity.getNoIdentitySuggestion(),message.getProposedSolution());
                hasNoIdentity = true;
            }
        }
        assertTrue(hasNotAllowedIdentity);
        assertTrue(hasNoIdentity);

        /***************************************************************
         Test with a wrong Nucleic Acid that has no identity.
         ****************************************************************/

        na.setXrefs(new ArrayList<InteractorXref>());
        messages = rule.check(na);
        assertEquals(1,messages.size());
        for(GeneralMessage message : messages){
            assertEquals(NucleicAcidIdentity.getNoIdentityDescription(),message.getDescription());
            assertEquals(NucleicAcidIdentity.getNoIdentitySuggestion(),message.getProposedSolution());
        }

    }

}
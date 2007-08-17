/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.protein;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.sanity.exception.SanityCheckerException;
import uk.ac.ebi.intact.sanity.rules.messages.GeneralMessage;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.InteractorXref;
import uk.ac.ebi.intact.mocks.proteins.P08050Mock;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.mocks.cvDatabases.UniprotMock;

import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class ProteinIdentityTest  extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ProteinIdentityTest ( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ProteinIdentityTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCheck() throws SanityCheckerException {

        Protein protein = P08050Mock.getMock();
        ProteinIdentity rule = new ProteinIdentity();
        Collection<GeneralMessage> messages = rule.check(protein);
        assertEquals(0,messages.size());

        InteractorXref xref = XrefMock.getMock(InteractorXref.class, UniprotMock.getMock(), IdentityMock.getMock(), "P12345");
        protein.addXref(xref);
        messages = rule.check(protein);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(ProteinIdentity.getMultipleIdentityDescription(), message.getDescription());
            assertEquals(ProteinIdentity.getMultipleIdentitySuggestion(), message.getProposedSolution());
        }

        protein.setXrefs(new ArrayList<InteractorXref>());
        messages = rule.check(protein);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(ProteinIdentity.getNoUniprotDescription(), message.getDescription());
            assertEquals(ProteinIdentity.getNoUniprotSuggestion(), message.getProposedSolution());
        }
    }



}
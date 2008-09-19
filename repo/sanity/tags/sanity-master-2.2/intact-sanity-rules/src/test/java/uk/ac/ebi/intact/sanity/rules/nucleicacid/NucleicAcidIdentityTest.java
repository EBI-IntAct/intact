/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.nucleicacid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.cvDatabases.DDBJMock;
import uk.ac.ebi.intact.mocks.cvDatabases.PubmedMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.mocks.nucleicAcids.AnfRatGeneMock;
import uk.ac.ebi.intact.model.InteractorXref;
import uk.ac.ebi.intact.model.NucleicAcid;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * NucleicAcidIdentity Tester.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
public class NucleicAcidIdentityTest {

    @Test
    public void check() throws Exception {
        /***************************************************************
         Test with a right Nucleic Acid that has one identity to DDBJ
         ****************************************************************/
        NucleicAcid na = AnfRatGeneMock.getMock();
        NucleicAcidIdentity rule = new NucleicAcidIdentity();
        Collection<GeneralMessage> messages = rule.check( na );
        assertEquals( 0, messages.size() );

        /***************************************************************
         Test with a wrong Nucleic Acid that has 2 identities to DDBJ
         ****************************************************************/
        InteractorXref xref = XrefMock.getMock( InteractorXref.class, DDBJMock.getMock(), IdentityMock.getMock(), "K02062" );
        na.addXref( xref );
        messages = rule.check( na );
        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            assertEquals( MessageDefinition.NUC_ACID_IDENTITY_MULTIPLE, message.getMessageDefinition() );
        }

        /***************************************************************
         Test with a wrong Nucleic Acid that has 1 identities to Pubmed
         ****************************************************************/
        Collection<InteractorXref> xrefs = new ArrayList<InteractorXref>();
        xref = XrefMock.getMock( InteractorXref.class, PubmedMock.getMock(), IdentityMock.getMock(), "1" );
        xrefs.add( xref );
        na.setXrefs( xrefs );
        messages = rule.check( na );
        assertEquals( 2, messages.size() );
        boolean hasNotAllowedIdentity = false;
        boolean hasNoIdentity = false;
        for ( GeneralMessage message : messages ) {
            if ( MessageDefinition.NUC_ACID_IDENTITY_INVALID_DB == message.getMessageDefinition() ) {
                hasNotAllowedIdentity = true;
            } else if ( MessageDefinition.NUC_ACID_IDENTITY_MISSING == message.getMessageDefinition() ) {
                hasNoIdentity = true;
            }
        }
        assertTrue( hasNotAllowedIdentity );
        assertTrue( hasNoIdentity );

        /***************************************************************
         Test with a wrong Nucleic Acid that has no identity.
         ****************************************************************/

        na.setXrefs( new ArrayList<InteractorXref>() );
        messages = rule.check( na );
        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            assertEquals( MessageDefinition.NUC_ACID_IDENTITY_MISSING, message.getMessageDefinition() );
        }
    }
}
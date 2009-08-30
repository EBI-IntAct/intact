/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.biosource;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.biosources.BioSourceWithNoXrefMock;
import uk.ac.ebi.intact.mocks.cvDatabases.NewtMock;
import uk.ac.ebi.intact.mocks.cvDatabases.PubmedMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.XrefUtils;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;

import java.util.Collection;

/**
 * NoNewtIdentity Tester.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
public class NoNewtIdentityTest {

    @Test
    public void check() throws Exception {

        // Test that we get back a message if we give to the check method a bioSource without xref at all.
        BioSource biosource = BioSourceWithNoXrefMock.getMock();
        NoNewtIdentity rule = new NoNewtIdentity();
        Collection<GeneralMessage> messages = rule.check( biosource );

        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            assertEquals( MessageDefinition.BIOSOURCE_WITHOUT_NEWT_XREF, message.getMessageDefinition() );
        }

        // Test that we get back 1 message if we give to the check method a bioSource with 1 identity xref to pubmed.
        biosource = BioSourceWithNoXrefMock.getMock();
        BioSourceXref identityToPubmed = XrefMock.getMock( BioSourceXref.class, PubmedMock.getMock(), IdentityMock.getMock(), "1" );
        biosource.addXref( identityToPubmed );
        messages = rule.check( biosource );

        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            assertEquals( MessageDefinition.BIOSOURCE_WITHOUT_NEWT_XREF, message.getMessageDefinition() );
        }

        // Test that we get no message back if we give to the check method a bioSource without 1 identity xref to pubmed
        // and one identity xref to newt.
        biosource = BioSourceWithNoXrefMock.getMock();
        identityToPubmed = XrefMock.getMock( BioSourceXref.class, PubmedMock.getMock(), IdentityMock.getMock(), "1" );
        BioSourceXref identityToNewt = XrefMock.getMock( BioSourceXref.class, NewtMock.getMock(), IdentityMock.getMock(), "9606" );
        biosource.addXref( identityToPubmed );
        biosource.addXref( identityToNewt );
        messages = rule.check( biosource );

        assertEquals( 0, messages.size() );
    }
    
    @Test
    public void check_ignored() throws Exception {

        // Test that we get back NO message if we set the biosource to ignore this rule and give to the
        // check method a bioSource without xref at all.
        BioSource biosource = BioSourceWithNoXrefMock.getMock();
        final Institution owner = biosource.getOwner();
        biosource.addAnnotation( new Annotation( owner, new CvTopic( owner, Rule.IGNORE_SANITY_RULE ),
                                                 MessageDefinition.BIOSOURCE_WITHOUT_NEWT_XREF.getKey()) );
        NoNewtIdentity rule = new NoNewtIdentity();
        Collection<GeneralMessage> messages = rule.check( biosource );

        assertEquals( 0, messages.size() );

        // Test that we get back 1 message if we give to the check method a bioSource with 1 identity xref to pubmed.
        biosource = BioSourceWithNoXrefMock.getMock();
        biosource.addAnnotation( new Annotation( owner, new CvTopic( owner, Rule.IGNORE_SANITY_RULE ),
                                                 MessageDefinition.BIOSOURCE_WITHOUT_NEWT_XREF.getKey()) );
        BioSourceXref identityToPubmed = XrefMock.getMock( BioSourceXref.class, PubmedMock.getMock(), IdentityMock.getMock(), "1" );
        biosource.addXref( identityToPubmed );
        messages = rule.check( biosource );

        assertEquals( 0, messages.size() );
    }
}
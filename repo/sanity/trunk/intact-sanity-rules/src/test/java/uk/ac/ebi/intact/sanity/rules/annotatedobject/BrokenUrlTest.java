/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.annotatedobject;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.ac.ebi.intact.mocks.AnnotationMock;
import uk.ac.ebi.intact.mocks.ProteinMock;
import uk.ac.ebi.intact.mocks.cvTopics.UrlMock;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;

import java.util.Collection;

/**
 * BrokenUrl Tester.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
public class BrokenUrlTest {

    @Test
    public void check() throws SanityRuleException {
        Protein protein = ProteinMock.getMock();

        Annotation annotation = AnnotationMock.getMock( UrlMock.getMock(), "http://www.google.co.uk" );
        protein.addAnnotation( annotation );
        BrokenUrl rule = new BrokenUrl();

        Collection<GeneralMessage> messages = rule.check( protein );

        assertEquals( 0, messages.size() );

        protein = ProteinMock.getMock();

        annotation = AnnotationMock.getMock( UrlMock.getMock(), "http://www.sdfhsdfgklksdf.co.uk" );
        protein.addAnnotation( annotation );

        messages = rule.check( protein );
        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            assertEquals( MessageDefinition.BROKEN_URL, message.getMessageDefinition() );
        }
    }
}
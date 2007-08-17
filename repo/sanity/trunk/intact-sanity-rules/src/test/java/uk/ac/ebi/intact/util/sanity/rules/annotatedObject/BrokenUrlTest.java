/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.rules.annotatedObject;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.mocks.ProteinMock;
import uk.ac.ebi.intact.mocks.AnnotationMock;
import uk.ac.ebi.intact.mocks.cvTopics.UrlMock;
import uk.ac.ebi.intact.util.sanity.rules.messages.GeneralMessage;
import uk.ac.ebi.intact.util.sanity.exception.SanityCheckerException;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class BrokenUrlTest  extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public BrokenUrlTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( BrokenUrlTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCheck() throws SanityCheckerException {
        Protein protein = ProteinMock.getMock();

        Annotation annotation = AnnotationMock.getMock(UrlMock.getMock(),"http://www.google.co.uk");
        protein.addAnnotation(annotation);
        BrokenUrl rule = new BrokenUrl();

        Collection<GeneralMessage> messages = rule.check(protein);

        assertEquals(0, messages.size());

        protein = ProteinMock.getMock();

        annotation = AnnotationMock.getMock(UrlMock.getMock(),"http://www.sdfhsdfgklksdf.co.uk");
        protein.addAnnotation(annotation);

        messages = rule.check(protein);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(BrokenUrl.getDescription(), message.getDescription());
            assertEquals(BrokenUrl.getSuggestion(),message.getProposedSolution());
        }

    }
}
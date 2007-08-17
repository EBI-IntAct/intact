/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.annotatedobject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.mocks.AnnotationMock;
import uk.ac.ebi.intact.mocks.ProteinMock;
import uk.ac.ebi.intact.mocks.cvTopics.UrlMock;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.exception.SanityCheckerException;
import uk.ac.ebi.intact.sanity.rules.annotatedObject.AnnotationNotUsingAnAppropriateCvTopic;
import uk.ac.ebi.intact.sanity.rules.messages.GeneralMessage;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class AnnotationNotUsingAnAppropriateCvTopicTest extends TestCase {

   /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AnnotationNotUsingAnAppropriateCvTopicTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AnnotationNotUsingAnAppropriateCvTopicTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCheck()
    {
        Protein protein = ProteinMock.getMock();
        Annotation url = AnnotationMock.getMock(UrlMock.getMock(),"http://www.ebi.uniprot.org/uniprot-srv/uniProtView.do?proteinId=AATM_RABIT&pager.offset=null");
        protein.addAnnotation(url);

        AnnotationNotUsingAnAppropriateCvTopic rule = new AnnotationNotUsingAnAppropriateCvTopic();
        try {
            Collection<GeneralMessage> messages =  rule.check(protein);
            
            assertEquals(0,messages.size());
        } catch (SanityCheckerException e) {
            e.printStackTrace();
        }
    }
}
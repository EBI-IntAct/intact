/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.annotatedobject;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.mocks.AnnotationMock;
import uk.ac.ebi.intact.mocks.ProteinMock;
import uk.ac.ebi.intact.mocks.cvTopics.UrlMock;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;

import java.util.Collection;

/**
 * AnnotationNotUsingAnAppropriateCvTopic Tester.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
public class AnnotationNotUsingAnAppropriateCvTopicTest {

    @Test
    public void check()
    {
        Protein protein = ProteinMock.getMock();
        Annotation url = AnnotationMock.getMock(UrlMock.getMock(),"http://www.ebi.uniprot.org/uniprot-srv/uniProtView.do?proteinId=AATM_RABIT&pager.offset=null");
        protein.addAnnotation(url);

        AnnotationNotUsingAnAppropriateCvTopic rule = new AnnotationNotUsingAnAppropriateCvTopic();
        try {
            Collection<GeneralMessage> messages =  rule.check(protein);
            Assert.assertEquals(1,messages.size());
        } catch (SanityRuleException e) {
            e.printStackTrace();
        }
    }
}
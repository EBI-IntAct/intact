/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.feature;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.mocks.InstitutionMock;
import uk.ac.ebi.intact.mocks.components.Q9QXS6ComponentMock;
import uk.ac.ebi.intact.mocks.cvFeatureType.MutationDecreasingMock;
import uk.ac.ebi.intact.mocks.experiments.ButkevitchMock;
import uk.ac.ebi.intact.mocks.interactions.Cja1Dbn1Mock;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Range;
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
public class FeatureWithoutRangeTest extends TestCase {


    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public FeatureWithoutRangeTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( FeatureWithoutRangeTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCheck() throws SanityCheckerException {
        Interaction interaction = Cja1Dbn1Mock.getMock(ButkevitchMock.getMock());
        FeatureWithoutRange rule = new FeatureWithoutRange();

        Component component = Q9QXS6ComponentMock.getMock(interaction);
        Feature feature = new Feature(InstitutionMock.getMock(),"feature",component, MutationDecreasingMock.getMock());
        Range range = new Range(InstitutionMock.getMock(),1,1,1,1,"");
        feature.addRange(range);
        Collection<GeneralMessage> messages = rule.check(feature);
        assertEquals(0, messages.size());

        component = Q9QXS6ComponentMock.getMock(interaction);
        feature = new Feature(InstitutionMock.getMock(),"feature",component, MutationDecreasingMock.getMock());
        messages = rule.check(feature);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(FeatureWithoutRange.getDescription(), message.getDescription());
            assertEquals(FeatureWithoutRange.getSuggestion(), message.getProposedSolution());
        }

    }
}
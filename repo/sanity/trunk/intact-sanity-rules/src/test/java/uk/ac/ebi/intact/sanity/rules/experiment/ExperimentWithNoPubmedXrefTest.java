/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.experiment;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.mocks.experiments.ButkevitchMock;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.ExperimentXref;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class ExperimentWithNoPubmedXrefTest extends TestCase {


    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ExperimentWithNoPubmedXrefTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ExperimentWithNoPubmedXrefTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCheck() throws SanityRuleException {
        ExperimentWithNoPubmedXref rule = new ExperimentWithNoPubmedXref();

        Experiment experiment = ButkevitchMock.getMock();
        Collection<GeneralMessage> messages = rule.check(experiment);
        assertEquals(0, messages.size());

        Collection<ExperimentXref> xrefs = new ArrayList<ExperimentXref>();
        experiment.setXrefs(xrefs);
        messages = rule.check(experiment);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(ExperimentWithNoPubmedXref.getDescription(), message.getDescription());
            assertEquals(ExperimentWithNoPubmedXref.getSuggestion(), message.getProposedSolution());
        }
    }
}
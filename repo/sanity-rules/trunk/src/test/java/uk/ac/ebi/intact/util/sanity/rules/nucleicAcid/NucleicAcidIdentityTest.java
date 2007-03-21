/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.rules.nucleicAcid;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import uk.ac.ebi.intact.util.sanity.exception.SanityCheckerException;
import uk.ac.ebi.intact.util.sanity.rules.messages.GeneralMessage;
import uk.ac.ebi.intact.model.NucleicAcid;
import uk.ac.ebi.intact.mocks.nucleicAcids.AnfRatGeneMock;

import java.util.Collection;

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
        NucleicAcid na = AnfRatGeneMock.getMock();
        NucleicAcidIdentity rule = new NucleicAcidIdentity();
        Collection<GeneralMessage> messages = rule.check(na);
        assertEquals(0,messages.size());

        


    }

}
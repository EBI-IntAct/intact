/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.model.*;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class MethodArgumentValidatorTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MethodArgumentValidatorTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MethodArgumentValidatorTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testIsValidArgument()
    {
        Institution institution = new Institution("EBI");
        BioSource bioSource = new BioSource(institution, "human", "9606");
        CvInteractorType cvInteractorType = new CvInteractorType(institution,"protein");
        Protein protein = new ProteinImpl(institution, bioSource, "lsm7", cvInteractorType);

        // This should return true.
        assertTrue(MethodArgumentValidator.isValidArgument(protein,Protein.class) );

        // This should return true as Protein is a sub class of Interactor
        assertTrue(MethodArgumentValidator.isValidArgument(protein,Interactor.class) );

        //This should send an IllegalArgumentException
        try{
            MethodArgumentValidator.isValidArgument(protein,BioSource.class);
            fail("This should have sent and IllegalArgumentException as protein is not a BioSource");
        }catch(IllegalArgumentException e){
            assertTrue(true);
        }
        //This should send an NullPointerException
        try{
            assertTrue(MethodArgumentValidator.isValidArgument(null,Interactor.class) );
            fail("This should have sent and NullPointerException as protein is null");
        }catch( NullPointerException e){
            assertTrue(true);
        }
    }


}
/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.commons.go;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class GoXrefHelperTest extends TestCase {

     public GoXrefHelperTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( GoXrefHelperTest.class );
    }

    public void testGetQualifier(){
        //C
        GoXrefHelper goXrefHelper = new GoXrefHelper("GO:0005737");
        assertEquals( "component", goXrefHelper.getQualifier() );

        //F
        goXrefHelper = new GoXrefHelper("GO:0005520");
        assertEquals( "function", goXrefHelper.getQualifier() );

        //P
        goXrefHelper = new GoXrefHelper("GO:0045663");
        assertEquals( "process", goXrefHelper.getQualifier() );
    }

    public void testGetSecondaryId(){
        //C
        GoXrefHelper goXrefHelper = new GoXrefHelper("GO:0005737");
        assertEquals( "C:cytoplasm", goXrefHelper.getSecondaryId() );

        //F
        goXrefHelper = new GoXrefHelper("GO:0005520");
        assertEquals( "F:insulin-like growth factor binding", goXrefHelper.getSecondaryId() );

        //P
        goXrefHelper = new GoXrefHelper("GO:0045663");
        assertEquals( "P:positive regulation of myoblast differentiation", goXrefHelper.getSecondaryId() );

    }
}

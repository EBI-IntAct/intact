/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.application.dataConversion.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.dataConversion.ZipFileGenerator;

/**
 * ZipFileGenerator Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02/15/2006</pre>
 */
public class ZipFileGeneratorTest extends TestCase {

    public ZipFileGeneratorTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( ZipFileGeneratorTest.class );
    }

    /////////////////////
    // Tests

    public void testExtractPubmedId() {

        assertEquals( "123456789", ZipFileGenerator.extractPubmedId( "123456789.xml" ) );
        assertEquals( "123456789", ZipFileGenerator.extractPubmedId( "123456789_abfkabf.xml" ) );
        assertEquals( "14704431", ZipFileGenerator.extractPubmedId( "14704431_li-2004-2_03.xml" ) );
        assertEquals( "14704431", ZipFileGenerator.extractPubmedId( "14704431-li-2004-2_03.xml" ) );
        assertEquals( "14704431", ZipFileGenerator.extractPubmedId( "14704431.li-2004-2_03.xml" ) );
    }
}

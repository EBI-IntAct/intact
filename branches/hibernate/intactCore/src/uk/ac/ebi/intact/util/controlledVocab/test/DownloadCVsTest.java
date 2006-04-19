/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
 
package uk.ac.ebi.intact.util.controlledVocab.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import uk.ac.ebi.intact.util.controlledVocab.DownloadCVs;

/**
 * DownloadCVs Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since <pre>10/31/2005</pre>
 * @version $Id$
 */
public class DownloadCVsTest extends TestCase {
    public DownloadCVsTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite(DownloadCVsTest.class);
    }


    /////////////////////
    // Tests

    public void testEscapeCharacter() {

        assertEquals( "", DownloadCVs.escapeCharacter( "" ) );
        assertEquals( "abc", DownloadCVs.escapeCharacter( "abc" ) );
        assertEquals( "...\\\\...", DownloadCVs.escapeCharacter( "...\\..." ) );
        assertEquals( "...\\n...", DownloadCVs.escapeCharacter( "...\n..." ) );
        assertEquals( "...\\n...", DownloadCVs.escapeCharacter( "...\r\n..." ) );
        assertEquals( "...\\{...", DownloadCVs.escapeCharacter( "...{..." ) );
        assertEquals( "...\\}...", DownloadCVs.escapeCharacter( "...}..." ) );
        assertEquals( "...\\[...", DownloadCVs.escapeCharacter( "...[..." ) );
        assertEquals( "...\\]...", DownloadCVs.escapeCharacter( "...]..." ) );
        assertEquals( "...\\[...", DownloadCVs.escapeCharacter( "...[..." ) );
        assertEquals( "...\\[...", DownloadCVs.escapeCharacter( "...[..." ) );
        assertEquals( "...\\:...", DownloadCVs.escapeCharacter( "...:..." ) );
        assertEquals( "...\\,...", DownloadCVs.escapeCharacter( "...,..." ) );
        assertEquals( "...\\\"...", DownloadCVs.escapeCharacter( "...\"..." ) );

        // multiple escape in one string
        assertEquals( "...\\] \\] \\[...", DownloadCVs.escapeCharacter( "...] ] [..." ) );
        assertEquals( "\\]\\]\\]", DownloadCVs.escapeCharacter( "]]]" ) );
        assertEquals( "...\\\\...\\n...\\n...\\{...\\}...\\[...\\]...\\\"...\\\"...\\,...\\:...",
                      DownloadCVs.escapeCharacter( "...\\...\n...\r\n...{...}...[...]...\"...\"...,...:..." ) );
    }
}

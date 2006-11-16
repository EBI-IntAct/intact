/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.util.cdb;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * ExperimentShortlabelGenerator Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/15/2005</pre>
 */
public class ExperimentShortlabelGeneratorTest extends TestCase
{
    public ExperimentShortlabelGeneratorTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( ExperimentShortlabelGeneratorTest.class );
    }


    /////////////////////
    // Tests

    public void testGetSuffix() {
        ExperimentShortlabelGenerator esg = new ExperimentShortlabelGenerator();

        assertEquals( "-1", esg.getSuffix( "author1", 2005, "pubmed1" ) );
        assertEquals( "-2", esg.getSuffix( "author1", 2005, "pubmed1" ) );
        assertEquals( "-3", esg.getSuffix( "author1", 2005, "pubmed1" ) );

        // here author1 and 2005 are known already and the char to use should be 'a'
        assertEquals( "a-1", esg.getSuffix( "author1", 2005, "pubmed2" ) );

        assertEquals( "-4", esg.getSuffix( "author1", 2005, "pubmed1" ) );

        assertEquals( "a-2", esg.getSuffix( "author1", 2005, "pubmed2" ) );
        assertEquals( "a-3", esg.getSuffix( "author1", 2005, "pubmed2" ) );

        assertEquals( "-1", esg.getSuffix( "author2", 2005, "pubmed2" ) );
        assertEquals( "-2", esg.getSuffix( "author2", 2005, "pubmed2" ) );

        assertEquals( "a-1", esg.getSuffix( "author2", 2005, "pubmed3" ) );

        assertEquals( "-1", esg.getSuffix( "author2", 2004, "pubmed2" ) );
    }

    public void testGetCharacter() {
        ExperimentShortlabelGenerator.SuffixBean sb = new ExperimentShortlabelGenerator.SuffixBean( "f" );
        assertEquals( "f", sb.getCharacter() );
    }

    public void testGetNextCount() {

        ExperimentShortlabelGenerator.SuffixBean sb = new ExperimentShortlabelGenerator.SuffixBean( "f" );
        assertEquals( 1, sb.getNextCount() );
        assertEquals( 2, sb.getNextCount() );
        assertEquals( 3, sb.getNextCount() );
        assertEquals( 4, sb.getNextCount() );
        assertEquals( 5, sb.getNextCount() );

        sb = new ExperimentShortlabelGenerator.SuffixBean( "d" );
        assertEquals( 1, sb.getNextCount() );
        assertEquals( 2, sb.getNextCount() );
        assertEquals( 3, sb.getNextCount() );
        assertEquals( 4, sb.getNextCount() );
        assertEquals( 5, sb.getNextCount() );
    }

    public void testGetAuthor() {
        ExperimentShortlabelGenerator.SuffixKey sk = new ExperimentShortlabelGenerator.SuffixKey( "author", 2005 );
        assertEquals( "author", sk.getAuthor() );
    }

    public void testGetYear() {
        ExperimentShortlabelGenerator.SuffixKey sk = new ExperimentShortlabelGenerator.SuffixKey( "author", 2005 );
        assertEquals( 2005, sk.getYear() );
    }

    public void testGetNextChar() {
        ExperimentShortlabelGenerator.SuffixKey sk = new ExperimentShortlabelGenerator.SuffixKey( "author", 2005 );
        assertEquals( 2005, sk.getYear() );
    }

    public void testgetNextChar() {
        ExperimentShortlabelGenerator.SuffixKey sk = new ExperimentShortlabelGenerator.SuffixKey( "author", 2005 );
        assertEquals( "", sk.getNextChar() );
        assertEquals( "a", sk.getNextChar() );
        assertEquals( "b", sk.getNextChar() );
        assertEquals( "c", sk.getNextChar() );
        assertEquals( "d", sk.getNextChar() );
        assertEquals( "e", sk.getNextChar() );
        assertEquals( "f", sk.getNextChar() );

        sk = new ExperimentShortlabelGenerator.SuffixKey( "author2", 2004 );
        assertEquals( "", sk.getNextChar() );
        assertEquals( "a", sk.getNextChar() );
        assertEquals( "b", sk.getNextChar() );
        assertEquals( "c", sk.getNextChar() );
        assertEquals( "d", sk.getNextChar() );
        assertEquals( "e", sk.getNextChar() );
        assertEquals( "f", sk.getNextChar() );
    }
}

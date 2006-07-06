/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.util.SearchReplace;

public class SearchReplaceTest extends TestCase {


    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public SearchReplaceTest( String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( SearchReplaceTest.class );
    }

    public void testSearchReplace() {

        String text = "http://srs.ebi.ac.uk/srsbin/cgi-bin/wgetz?-e+([uniprot-acc:${ac}]|[uniprot-isoid:${ac}])+-vn+2+-ascii";
        String replacement = "P12345";
        String expectedResult = "http://srs.ebi.ac.uk/srsbin/cgi-bin/wgetz?-e+([uniprot-acc:P12345]|[uniprot-isoid:P12345])+-vn+2+-ascii";

        String resultOk = SearchReplace.replace( text, "${ac}", replacement );
        assertEquals( expectedResult, resultOk );

        String resultWrong = SearchReplace.replace( text, "${foo}", replacement );
        assertNotSame( expectedResult, resultWrong );
    }
}

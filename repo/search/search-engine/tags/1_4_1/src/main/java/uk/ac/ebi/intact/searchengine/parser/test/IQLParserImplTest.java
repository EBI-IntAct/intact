/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.searchengine.parser.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.searchengine.parser.IQLParserImpl;

/**
 * This class tests the class IQLParserImpl. IQLParserImpl provides a method
 * that parses an IQL statement into an lucene query statement
 *
 * @author Anja Friedrichsen
 * @version $Id:IQLParserImplTest.java 5081 2006-06-26 12:39:49 +0000 (Mon, 26 Jun 2006) baranda $
 */
public class IQLParserImplTest extends TestCase {

    /**
     * Constructs a Test instance with the specified name.
     *
     * @param name the name of the test.
     */
    public IQLParserImplTest(final String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(IQLParserImplTest.class);
    }

    public IQLParserImpl qh;

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() {
        qh = new IQLParserImpl();
    }

    /**
     * test the method getLuceneQuery
     */
    public void testGetLuceneQuery1() {
        String iql = "SELECT protein FROM intact;";
        String lucene1 = "objclass:uk.ac.ebi.*protein*";
        String lucene2 = null;
        try {
            lucene2 = qh.getLuceneQuery(iql);
        } catch (IntactException e) {
            e.printStackTrace();
        }
        assertNotNull(lucene2);
        assertEquals(lucene1, lucene2);
    }


    /**
     * test the method getLuceneQuery
     */
    public void testGetLuceneQuery2() {
        String iql = "SELECT experiment FROM intact WHERE shortlabel = 'test test';";
        String lucene1 = "objclass:uk.ac.ebi.*experiment* AND shortlabel:(test test)";
        String lucene2 = null;
        try {
            lucene2 = qh.getLuceneQuery(iql);
        } catch (IntactException e) {
            e.printStackTrace();
        }
        assertNotNull(lucene2);
        assertEquals(lucene1, lucene2);
    }

    /**
     * test the method getLuceneQuery
     */
    public void testGetLuceneQuery3() {
        String iql = "SELECT cv FROM intact WHERE (ac = 'EBI-30' and shortlabel like '*test2');";
        String lucene1 = "objclass:uk.ac.ebi.*cv* AND (ac:(EBI\\-30) AND shortlabel:(*test2))";
        String lucene2 = null;
        try {
            lucene2 = qh.getLuceneQuery(iql);
        } catch (IntactException e) {
            e.printStackTrace();
        }
        assertNotNull(lucene2);
        assertEquals(lucene1, lucene2);
    }

    /**
     * test the method getLuceneQuery
     */
    public void testGetLuceneQuery4() {
        String iql = "SELECT interaction FROM intact WHERE (ac = 'EBI-30' and (shortlabel like 'te(s)t2*' or fullname like 'te/st*'));";
        String lucene1 = "objclass:uk.ac.ebi.*interaction* AND (ac:(EBI\\-30) AND (shortlabel:(te\\(s\\)t2*) OR fullname:(te/st*)))";
        String lucene2 = null;
        try {
            lucene2 = qh.getLuceneQuery(iql);
        } catch (IntactException e) {
            e.printStackTrace();
        }
        assertNotNull(lucene2);
        assertEquals(lucene1, lucene2);
    }

    /**
     * test the method getLuceneQuery with many different special character
     * which can appear in a lucene query
     */
    public void testGetLuceneQuery5() {
        String iql = "SELECT any FROM intact WHERE ac = '+ - && || ! ( ) { } [ ] ^ \" ~ * ? : \\';";
        String lucene1 = "-objclass:uk.*.biosource* AND ac:(\\+ \\- \\&& \\|| \\! \\( \\) { } [ ] ^ \" ~ * ? \\: \\\\)";
        String lucene2 = null;
        try {
            lucene2 = qh.getLuceneQuery(iql);
        } catch (IntactException e) {
            e.printStackTrace();
        }
        assertNotNull(lucene2);
        assertEquals(lucene1, lucene2);
    }

    /**
     * test the method getLuceneQuery with a statement that has not the right grammatic
     */
    public void testGetLuceneQuery6() {
        String iql = "SELECT cv FROM intact WHERE ac = 'EBI-30'  shortlabel like 'test2'";
        try {
            String lucene = qh.getLuceneQuery(iql);
            assertNull(lucene);
            fail("here should come an IntactException");
        } catch (IntactException e) {
            // do nothing
            e.printStackTrace();
        }
    }


    /**
     * test the method getLuceneQuery with a statement that is completely rubish
     */
    public void testGetLuceneQuery7() {
        String iql = "357idj gerhte ioqu";
        try {
            String lucene = qh.getLuceneQuery(iql);
            assertNull(lucene);
            fail("here should come an IntactException");
        } catch (IntactException e) {
            // do nothing
            e.printStackTrace();
        }
    }

    /**
     * test the method getLuceneQuery with a statement that has not the right grammatic
     */
    public void testGetLuceneQuery8() {
        String iql = "SELECT cv FROM intact WHERE ac = 'EBI-30' or shortlabel like ''";
        try {
            String lucene = qh.getLuceneQuery(iql);
            assertNull(lucene);
            fail("here should come an IntactException");
        } catch (IntactException e) {
            // do nothing
            e.printStackTrace();
        }
    }

    /**
     * test the method getLuceneQuery with a statement that has not the right grammatic
     */
    public void testGetLuceneQuery9() {
        String iql = "SELECT cv FROM intact; WHERE ac = 'EBI-30' or shortlabel like 'test2'";
        try {
            String lucene = qh.getLuceneQuery(iql);
            assertNull(lucene);
            fail("here should come an IntactException");
        } catch (IntactException e) {
            // do nothing
            e.printStackTrace();
        }
    }

    /**
     * test the method getLuceneQuery
     */
    public void testSomeCvDatabaseTerms() {
        String iql = "SELECT experiment FROM intact WHERE uniprot = 'test test' AND intact = 'test test'" +
                "   AND sgd = 'test test' AND huge = 'test test' AND omim = 'test test';";
        String lucene1 = "objclass:uk.ac.ebi.*experiment* AND ((((uniprot:(test test) AND intact:(test test)) " +
                "AND sgd:(test test)) AND huge:(test test)) AND omim:(test test))";
        String lucene2 = null;
        try {
            lucene2 = qh.getLuceneQuery(iql);
        } catch (IntactException e) {
            e.printStackTrace();
        }
        assertNotNull(lucene2);
        assertEquals(lucene1, lucene2);
    }


    /**
     * test the method getLuceneQuery
     */
    public void testSomeCvTopicsTerms() {
        String iql = "SELECT experiment FROM intact WHERE isoform-comment = 'test test' AND example = 'test test'" +
                "   AND search-url = 'test test' AND kinetics = 'test test' AND submitted = 'test test';";
        String lucene1 = "objclass:uk.ac.ebi.*experiment* AND ((((isoform-comment:(test test) AND example:(test test)) " +
                "AND search-url:(test test)) AND kinetics:(test test)) AND submitted:(test test))";
        String lucene2 = null;
        try {
            lucene2 = qh.getLuceneQuery(iql);
        } catch (IntactException e) {
            e.printStackTrace();
        }
        assertNotNull(lucene2);
        assertEquals(lucene1, lucene2);
    }


    /**
     * test the method getLuceneQuery
     */
    public void testSomeCvAliasTypeTerms() {
        String iql = "SELECT experiment FROM intact WHERE gene-name = 'test test' AND go-synonym = 'test test'" +
                "   AND orf-name = 'test test' AND locus-name = 'test test' AND isoform-synonym = 'test test';";
        String lucene1 = "objclass:uk.ac.ebi.*experiment* AND ((((gene-name:(test test) AND go-synonym:(test test)) " +
                "AND orf-name:(test test)) AND locus-name:(test test)) AND isoform-synonym:(test test))";
        String lucene2 = null;
        try {
            lucene2 = qh.getLuceneQuery(iql);
        } catch (IntactException e) {
            e.printStackTrace();
        }
        assertNotNull(lucene2);
        assertEquals(lucene1, lucene2);
    }

    /**
     * test the method getLuceneQuery
     */
    public void testSomeTerms() {
        String iql = "SELECT experiment FROM intact WHERE alias = 'test test' AND annotation = 'test test'" +
                "   AND xref = 'test test';";
        String lucene1 = "objclass:uk.ac.ebi.*experiment* AND ((alias:(test test) AND annotation:(test test)) " +
                "AND xref:(test test))";
        String lucene2 = null;
        try {
            lucene2 = qh.getLuceneQuery(iql);
        } catch (IntactException e) {
            e.printStackTrace();
        }
        assertNotNull(lucene2);
        assertEquals(lucene1, lucene2);
    }

    /**
     * test the method getLuceneQuery
     */
    public void testEscapedCharacters() {
        String iql = "SELECT experiment FROM intact WHERE go = 'GO:0099';";
        String lucene1 = "objclass:uk.ac.ebi.*experiment* AND go:(GO\\:0099)";
        String lucene2 = null;
        try {
            lucene2 = qh.getLuceneQuery(iql);
        } catch (IntactException e) {
            e.printStackTrace();
        }
        assertNotNull(lucene2);
        assertEquals(lucene1, lucene2);
    }


}

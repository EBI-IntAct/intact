package uk.ac.ebi.intact.application.search3.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.search3.struts.util.SearchValidator;


/**
 * @author Michael Kleen
 * @version SearchValidatorTest.java Date: Jan 26, 2005 Time: 11:41:46 PM
 */
public class SearchValidatorTest extends TestCase {

    public static Test suite() {
        return new TestSuite(SearchValidatorTest.class);
    }

    public void testIsSearchable() {

        // check if the searchable classes are valid
        assertTrue(SearchValidator.isSearchable("Protein"));
        assertTrue(SearchValidator.isSearchable("Experiment"));
        assertTrue(SearchValidator.isSearchable("Interaction"));
        assertTrue(SearchValidator.isSearchable("CvObject"));

        // check if
        assertFalse(SearchValidator.isSearchable("foo"));
        assertFalse(SearchValidator.isSearchable("blub"));

        assertFalse(SearchValidator.isSearchable("Complex"));
        assertFalse(SearchValidator.isSearchable("BioSource"));
        assertFalse(SearchValidator.isSearchable("Annotation"));

        // check if
        assertFalse(SearchValidator.isSearchable("foo"));
        assertFalse(SearchValidator.isSearchable("blub"));


    }

}

package uk.ac.ebi.intact.application.search3.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.search3.struts.util.SearchValidator;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.Annotation;


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
        assertTrue(SearchValidator.isSearchable(ProteinImpl.class));
        assertTrue(SearchValidator.isSearchable(Experiment.class));
        assertTrue(SearchValidator.isSearchable(InteractionImpl.class));
        assertTrue(SearchValidator.isSearchable(CvObject.class));

        // check if
        //assertFalse(SearchValidator.isSearchable(Object.class));
        //assertFalse(SearchValidator.isSearchable(String.class));

        //assertFalse(SearchValidator.isSearchable("Complex"));
        assertFalse(SearchValidator.isSearchable(BioSource.class));
        assertFalse(SearchValidator.isSearchable(Annotation.class));

        // check if
        //assertFalse(SearchValidator.isSearchable("foo"));
        //assertFalse(SearchValidator.isSearchable("blub"));


    }

}

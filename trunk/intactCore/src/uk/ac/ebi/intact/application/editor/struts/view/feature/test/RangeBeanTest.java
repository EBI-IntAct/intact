/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.editor.struts.view.feature.test;

import junit.framework.*;
import uk.ac.ebi.intact.test.FirstTest;
import uk.ac.ebi.intact.application.editor.struts.view.feature.RangeBean;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditKeyBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;

/**
 * The test class for RangeBean class.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class RangeBeanTest extends TestCase {

    /**
     * Constructs a RangeBeanTest instance with the specified name.
     * @param name the name of the test.
     */
    public RangeBeanTest(String name) {
        super(name);
    }

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() {
        // Write setting up code for each test.
    }

    /**
     * Tears down the test fixture. Called after every test case method.
     */
    protected void tearDown() {
        // Release resources for after running a test.
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(RangeBeanTest.class);
    }

    /**
     * Tests validate method.
     */
    public void testValidate() {
        RangeBean bean = new RangeBean();

        // Collects errors.
        ActionErrors errors;

        // TEST: Empty values for from and to range (likely if user hasn't entered
        // anything).
        bean.setFromRange("");
        bean.setToRange("");
        errors = bean.validate();
        // Should have some errors.
        assertFalse(errors.isEmpty());
        // Must be from range.
        assertTrue(errors.get("new.fromRange").hasNext());
        // Not from to range.
        assertFalse(errors.get("new.toRange").hasNext());

        // TEST: Empty value for to range
        bean.setFromRange("2");
        assertTrue(bean.getFromRange().length() != 0);
        assertTrue(bean.getToRange().length() == 0);
        errors = bean.validate();
        // Should have some errors.
        assertFalse(errors.isEmpty());
        // Not from 'from' range.
        assertFalse(errors.get("new.fromRange").hasNext());
        // From to range.
        assertTrue(errors.get("new.toRange").hasNext());

        // TEST: Non numeric values for both from and to ranges.
        bean.setFromRange("a");
        bean.setToRange("b");
        errors = bean.validate();
        // Should have some errors.
        assertFalse(errors.isEmpty());
        // Must be from range.
        assertTrue(errors.get("new.fromRange").hasNext());
        // Not from to range.
        assertFalse(errors.get("new.toRange").hasNext());

        // TEST: Non numeric values for to range.
        bean.setFromRange("2");
        bean.setToRange("b");
        errors = bean.validate();
        // Should have some errors.
        assertFalse(errors.isEmpty());
        // Not from 'from' range.
        assertFalse(errors.get("new.fromRange").hasNext());
        // From to range.
        assertTrue(errors.get("new.toRange").hasNext());

        // TEST: Numeric values for from and to ranges.
        bean.setFromRange("2");
        bean.setToRange("2");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);

        // TEST: from is >
        bean.setFromRange(">2");
        bean.setToRange("2");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);

        // TEST: from is > but to is non numeric.
        bean.setFromRange(">2");
        bean.setToRange("a");
        errors = bean.validate();
        // Should have some errors.
        assertFalse(errors.isEmpty());
        // Not from 'from' range.
        assertFalse(errors.get("new.fromRange").hasNext());
        // From to range.
        assertTrue(errors.get("new.toRange").hasNext());

        // TEST: from is < (valid case)
        bean.setFromRange("<2");
        bean.setToRange("2");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);

        // TEST: from is < alone.
        bean.setFromRange("<");
        bean.setToRange("2");
        errors = bean.validate();
        // Should have some errors.
        assertFalse(errors.isEmpty());
        // Must be from range.
        assertTrue(errors.get("new.fromRange").hasNext());
        // Not from to range.
        assertFalse(errors.get("new.toRange").hasNext());

        // TEST: from is < and to range has a number alone.
        bean.setFromRange("<2");
        bean.setToRange("2");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);

        // TEST: to is >
        bean.setFromRange("2");
        bean.setToRange(">2");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);

        // TEST: to is > but from is non numeric.
        bean.setFromRange("a");
        bean.setToRange(">2");
        errors = bean.validate();
        // Should have some errors.
        assertFalse(errors.isEmpty());
        // From 'from' range.
        assertTrue(errors.get("new.fromRange").hasNext());
        // Not from to range.
        assertFalse(errors.get("new.toRange").hasNext());

        // TEST: from is < (valid case)
        bean.setFromRange("2");
        bean.setToRange("<2");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);

        // TEST: to is < alone.
        bean.setFromRange("2");
        bean.setToRange("<");
        errors = bean.validate();
        // Should have some errors.
        assertFalse(errors.isEmpty());
        // Not from 'from' range.
        assertFalse(errors.get("new.fromRange").hasNext());
        // From to range.
        assertTrue(errors.get("new.toRange").hasNext());

        // TEST: to and from have < (valid case)
        bean.setFromRange("<2");
        bean.setToRange("<2");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);

        // TEST: to and from have > (valid case)
        bean.setFromRange(">2");
        bean.setToRange(">2");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);

        // --------------------------------------------------------------------

        // Fuzzy range testing.

        // TEST: from has fuzzy range, to has non numerics.
        bean.setFromRange("1..2");
        bean.setToRange("a");
        errors = bean.validate();
        // Should have some errors.
        assertFalse(errors.isEmpty());
        // Not from 'from' range.
        assertFalse(errors.get("new.fromRange").hasNext());
        // From to range.
        assertTrue(errors.get("new.toRange").hasNext());

        // TEST: to and from have fuzzy ranges (valid case)
        bean.setFromRange("1..2");
        bean.setToRange("1..2");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);

        // TEST: test for incorrect 'dots' (one less).
        bean.setFromRange("1.2");
        bean.setToRange("1..2");
        errors = bean.validate();
        // Should have some errors.
        assertFalse(errors.isEmpty());
        // From 'from' range.
        assertTrue(errors.get("new.fromRange").hasNext());
        // Not from to range.
        assertFalse(errors.get("new.toRange").hasNext());

        // TEST: test for incorrect 'dots' (one extra).
        bean.setFromRange("1...2");
        bean.setToRange("1..2");
        errors = bean.validate();
        // Should have some errors.
        assertFalse(errors.isEmpty());
        // From 'from' range.
        assertTrue(errors.get("new.fromRange").hasNext());
        // Not from to range.
        assertFalse(errors.get("new.toRange").hasNext());

        // TEST: from fuzzy to is not fuzzy (valid case).
        bean.setFromRange("1..2");
        bean.setToRange("1");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);

        // TEST: from fuzzy to is not fuzzy (valid case).
        bean.setFromRange("1..2");
        bean.setToRange(">1");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);

        // TEST: to is fuzzy from is not fuzzy (valid case).
        bean.setFromRange("1");
        bean.setToRange("1..2");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);

        // TEST: to is fuzzy from is not fuzzy (valid case).
        bean.setFromRange(">1");
        bean.setToRange("1..2");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);

        // --------------------------------------------------------------------

        // Valid cases.

        // Negative numbers.
        bean.setFromRange("-3");
        bean.setToRange("-2");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);

        // Negative numbers and fuzzy types.
        bean.setFromRange("-1");
        bean.setToRange("1..2");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);

        // Negative fuzzy values.
        bean.setFromRange("-1..3");
        bean.setToRange("-2..2");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);

        // Negative fuzzy values.
        bean.setFromRange("-3..-1");
        bean.setToRange("-2..-2");
        errors = bean.validate();
        // Shouldn't have errors.
        assertNull(errors);
    }

    /**
     * Tests validate method.
     */
    public void testClone() {
        RangeBean bean = new RangeBean();
        bean.setFromRange("2");
        bean.setToRange("5");

        // The copy.
        RangeBean copy = (RangeBean) bean.clone();
        
        assertEquals(copy.getFromRange(), bean.getFromRange());
        assertEquals(copy.getToRange(), bean.getToRange());

        // Check the super class's attributes
        assertEquals(copy.getEditState(), bean.getEditState());
        assertEquals(copy.getKey(), bean.getKey());

        // Save the original's state.
        String fromRange = bean.getFromRange();
        String toRange = bean.getToRange();
        String editState = bean.getEditState();

        // Change copy.
        copy.setFromRange("5");
        copy.setEditState(AbstractEditKeyBean.SAVE);

        // Verify that values are set.
        assertEquals(copy.getFromRange(), "5");
        assertEquals(copy.getEditState(), AbstractEditKeyBean.SAVE);

        // The orginal shouldn't be affected.
        assertEquals(bean.getFromRange(), fromRange);
        assertEquals(bean.getToRange(), toRange);
        assertEquals(bean.getEditState(), editState);

        // From ranges do not match.
        assertFalse(copy.getFromRange().equals(bean.getFromRange()));
        // Edit state doesn't match.
        assertFalse(copy.getEditState().equals(bean.getEditState()));
        // to range match.
        assertTrue(copy.getToRange().equals(bean.getToRange()));
    }
}

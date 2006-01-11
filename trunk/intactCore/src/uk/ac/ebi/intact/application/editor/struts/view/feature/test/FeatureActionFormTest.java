/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.editor.struts.view.feature.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;

import java.util.ResourceBundle;

/**
 * The test class for FeatureActionForm class.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FeatureActionFormTest extends TestCase {

    private static ResourceBundle ourResourceBundle;

    /**
     * Constructs an instance with the specified name.
     * @param name the name of the test.
     */
    public FeatureActionFormTest(String name) {
        super(name);
        ourResourceBundle = ResourceBundle.getBundle(
            "uk.ac.ebi.intact.application.editor.EditorResources");
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(FeatureActionFormTest.class);
    }

    /**
     * Tests the mutation validation
     */
    public void testMutationValidation() {
        try {
            doMutationValidation();
        }
        catch (Exception ex) {
            Logger.getLogger(EditorConstants.LOGGER).error("", ex);
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    // Helper methods

    private void doMutationValidation() {
        String featureSep = ourResourceBundle.getString("mutation.feature.sep");
        String rangeSep = ourResourceBundle.getString("mutation.range.sep");

        // Reference to errors in validating the form.
        ActionErrors errors;

        // For a single error.
        ActionError error;
        // For a key.
        String key;

        // The form to validate
        FeatureActionForm form = new FeatureActionForm();

        // ---------------------------------------------------------------------

        // Empty mutation
        errors = form.testValidateMutations("", featureSep, rangeSep);

        // There are errors
        assertNotNull(errors);
        key = (String) errors.properties().next();
        assertEquals("feature.mutation.empty", key);
        error = (ActionError) errors.get(key).next();
        assertEquals("error.feature.mutation.empty", error.getKey());

        // --------------------------------------------------------------------

        // Incorrect format (uppercase) for mutations
        errors = form.testValidateMutations("K235t", featureSep, rangeSep);

        // There are errors
        assertNotNull(errors);
        key = (String) errors.properties().next();
        assertEquals("feature.mutation.invalid", key);
        error = (ActionError) errors.get(key).next();
        assertEquals("error.feature.mutation.format", error.getKey());
        assertEquals(error.getValues()[0], "K235t");

        // --------------------------------------------------------------------

        // Incorrect format (no numbers) for mutations
        errors = form.testValidateMutations("kfgt", featureSep, rangeSep);

        // There are errors
        assertNotNull(errors);
        key = (String) errors.properties().next();
        assertEquals("feature.mutation.invalid", key);
        error = (ActionError) errors.get(key).next();
        assertEquals("error.feature.mutation.format", error.getKey());
        assertEquals(error.getValues()[0], "kfgt");

        // --------------------------------------------------------------------

        // Incorrect format: second range is incorrect
        errors = form.testValidateMutations("lys235thr & ser", featureSep, rangeSep);

        // There are errors
        assertNotNull(errors);
        key = (String) errors.properties().next();
        assertEquals("feature.mutation.invalid", key);
        error = (ActionError) errors.get(key).next();
        assertEquals("error.feature.mutation.format", error.getKey());
        assertEquals(error.getValues()[0], "ser");

        // --------------------------------------------------------------------

        // Incorrect format: second feature is incorrect
        errors = form.testValidateMutations("lys235thr & ser283thr | lys",
                featureSep, rangeSep);

        // There are errors
        assertNotNull(errors);
        key = (String) errors.properties().next();
        assertEquals("feature.mutation.invalid", key);
        error = (ActionError) errors.get(key).next();
        assertEquals("error.feature.mutation.format", error.getKey());
        assertEquals(error.getValues()[0], "lys");

        // --------------------------------------------------------------------

        // Incorrect format: only once in the same construct
        errors = form.testValidateMutations("trp123ala & trp123asp | leu23ile",
                featureSep, rangeSep);

        // There are errors
        assertNotNull(errors);
        key = (String) errors.properties().next();
        assertEquals("feature.mutation.invalid", key);
        error = (ActionError) errors.get(key).next();
        assertEquals("error.feature.mutation.range", error.getKey());
        assertEquals(error.getValues()[0], "trp123asp");
        assertEquals(error.getValues()[1], "123");

        // --------------------------------------------------------------------

        // Incorrect format: xxx and yyy must be different
        errors = form.testValidateMutations("trp123trp", featureSep, rangeSep);

        // There are errors
        assertNotNull(errors);
        key = (String) errors.properties().next();
        assertEquals("feature.mutation.invalid", key);
        error = (ActionError) errors.get(key).next();
        assertEquals("error.feature.mutation.same", error.getKey());
        assertEquals(error.getValues()[0], "trp");
        assertEquals(error.getValues()[1], "trp");

        // Correct Mutations

        // --------------------------------------------------------------------

        // Single correct mutation
        assertNull(form.testValidateMutations("lys235thr", featureSep, rangeSep));

        // Two mutations
        assertNull(form.testValidateMutations("lys235thr|lys5632thr", featureSep, rangeSep));

        // Two mutations, two ranges for the first Feature
        assertNull(form.testValidateMutations("lys235thr & ser283thr|lys5632thr",
                featureSep, rangeSep));

        // Two mutations, two ranges for both features
        assertNull(form.testValidateMutations("lys235thr & ser283thr|lys5632thr & lys234xyz",
                featureSep, rangeSep));

        // Same range value may ocurr in two different featurs
        assertNull(form.testValidateMutations("trp123ala | trp123ala", featureSep, rangeSep));

        // The range must be unique among a Feature.
        assertNull(form.testValidateMutations("trp123ala & trp124ala | trp123ala",
                featureSep, rangeSep));
    }
}

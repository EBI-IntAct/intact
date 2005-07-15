/*
    Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
    All rights reserved. Please see the file LICENSE
    in the root directory of this distribution.
*/

package uk.ac.ebi.intact.persistence.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Test class for Interactions.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionTest extends TestCase {

    /**
     * Handler to the IntactHelper
     */
    private IntactHelper myHelper;

    public InteractionTest( String name ) throws Exception {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( InteractionTest.class );
    }

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws Exception {
        super.setUp();
        myHelper = new IntactHelper();
        Object obj = myHelper.getObjectByLabel(Interaction.class, "int-1");
        if (obj != null) {
            myHelper.delete(obj);
        }
    }

    /**
     * Tears down the test fixture. Called after every test case method.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        myHelper.closeStore();
    }

    public void testLoading() throws IntactException {
        Interaction interaction = (Interaction) myHelper.getObjectByLabel(Interaction.class, "ga-1");

        // The interactor type.
        CvInteractorType interactorType = (CvInteractorType) myHelper.getObjectByPrimaryId(
                CvInteractorType.class, CvInteractorType.getInteractionMI());

        assertEquals(interaction.getCvInteractorType(), interactorType);
        assertEquals(interaction.getBioSource().getShortLabel(), "yeast");
    }

    public void testCreate() throws IntactException {
        // The owner for objects.
        Institution owner = myHelper.getInstitution();

        // The interaction type
        CvInteractionType interType = (CvInteractionType) myHelper.getObjectByLabel(
                CvInteractionType.class, "cleavage");

        // The interactor type.
        CvInteractorType interactorType = (CvInteractorType) myHelper.getObjectByPrimaryId(
                CvInteractorType.class, CvInteractorType.getInteractionMI());

        // Need to have an experiment - at least
        List exps = new ArrayList();
        exps.add((Experiment) myHelper.getObjectByLabel(Experiment.class, "ho"));

        // The interaction to persist
        Interaction inter = new InteractionImpl(exps, interType, interactorType, "int-1", owner);

        // Create a protein first
        myHelper.create(inter);

        // Force it to retrieve from DB
        myHelper.removeFromCache(inter);

        // Retrieve it from the persistent system
        Interaction intRetr = (Interaction) myHelper.getObjectByLabel(Interaction.class, "int-1");
        assertNotNull(intRetr);

        // Check attributes
        assertEquals(intRetr.getShortLabel(), "int-1");
        assertEquals(intRetr.getCvInteractionType(), interType);
        assertEquals(intRetr.getCvInteractorType(), interactorType);
    }
}

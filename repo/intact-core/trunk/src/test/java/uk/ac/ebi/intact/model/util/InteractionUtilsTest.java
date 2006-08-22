/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.model.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for <code>InteractionUtilsTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 08/22/2006
 */
public class InteractionUtilsTest extends TestCase {
    public InteractionUtilsTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsBinaryInteraction() throws Exception
    {
        assertTrue(InteractionUtils.isBinaryInteraction(createBinaryInteraction()));
        assertFalse(InteractionUtils.isBinaryInteraction(createSelfInteraction()));
    }

    public void testIsSelfInteraction() throws Exception
    {
        assertTrue(InteractionUtils.isSelfInteraction(createSelfInteraction()));
        assertFalse(InteractionUtils.isSelfInteraction(createBinaryInteraction()));
    }

    public void testContainsNonProteinInteractors() throws Exception
    {
        assertTrue(InteractionUtils.containsNonProteinInteractors(createBinaryInteractionWithNonProtein()));
        assertFalse(InteractionUtils.containsNonProteinInteractors(createBinaryInteraction()));
    }

    public static Test suite() {
        return new TestSuite(InteractionUtilsTest.class);
    }

    private InteractionImpl createBinaryInteraction()
    {
        List<Component> comps = new ArrayList<Component>();
        comps.add(createProteinComponent());
        comps.add(createProteinComponent());

        return new InteractionImpl(new ArrayList(), comps, null, null, "Int-"+System.currentTimeMillis(), null);
    }

    private InteractionImpl createBinaryInteractionWithNonProtein()
    {
        List<Component> comps = new ArrayList<Component>();
        comps.add(createProteinComponent());
        comps.add(createNonProteinComponent());

        return new InteractionImpl(new ArrayList(), comps, null, null, "Int-"+System.currentTimeMillis(), null);
    }

    private InteractionImpl createSelfInteraction()
    {
        List<Component> comps = new ArrayList<Component>();

        Component c = createProteinComponent();
        CvComponentRole role = new CvComponentRole();
        role.setShortLabel(CvComponentRole.SELF);
        c.setCvComponentRole(role);
        comps.add(c);

        return new InteractionImpl(new ArrayList(), comps, null, null, "Int-"+System.currentTimeMillis(), null);
    }

    private Component createProteinComponent()
    {
        ProteinImpl prot = new ProteinImpl(null, null, "Prot-"+System.currentTimeMillis(), null);
        return new Component(null, new InteractionImpl(), prot, new CvComponentRole());
    }

    private Component createNonProteinComponent()
    {
       SmallMoleculeImpl sm = new SmallMoleculeImpl("SmallMol-"+System.currentTimeMillis(), null, null);
       return new Component(null, new InteractionImpl(), sm, new CvComponentRole());
    }
}

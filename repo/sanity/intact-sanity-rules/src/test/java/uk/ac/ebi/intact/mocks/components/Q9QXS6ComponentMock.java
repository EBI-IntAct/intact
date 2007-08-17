/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.components;

import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.mocks.InstitutionMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;
import uk.ac.ebi.intact.mocks.bioSources.MouseBrainMock;
import uk.ac.ebi.intact.mocks.cvComponentRoles.PreyMock;
import uk.ac.ebi.intact.mocks.proteins.Q9QXS6Mock;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class Q9QXS6ComponentMock {
    public static Component getMock(Interaction interaction){
        Component component = new Component(InstitutionMock.getMock(),interaction, Q9QXS6Mock.getMock(), PreyMock.getMock());
        component.setExpressedIn(MouseBrainMock.getMock());
        component.setStoichiometry(0);
        component = (Component) IntactObjectSetter.setBasicObject(component);
        component.setOwner(InstitutionMock.getMock());
        return component;
    }
}
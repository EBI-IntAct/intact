/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.cvInteractionTypes;

import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.model.CvInteractionType;
import uk.ac.ebi.intact.mocks.CvObjectMock;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.mocks.cvDatabases.PsiMiMock;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class PhysicalInteractionMock {
     public static CvInteractionType getMock(){

        CvInteractionType physicalInteraction = CvObjectMock.getMock(CvInteractionType.class,CvInteractionType.PHYSICAL_INTERACTION, CvInteractionType.PHYSICAL_INTERACTION);

        physicalInteraction = (CvInteractionType) IntactObjectSetter.setBasicObject(physicalInteraction);


        CvObjectXref xref = XrefMock.getMock(CvObjectXref.class, PsiMiMock.getMock(), IdentityMock.getMock(),CvInteractionType.PHYSICAL_INTERACTION_MI_REF);
        physicalInteraction.addXref(xref);

        return physicalInteraction;
    }
}
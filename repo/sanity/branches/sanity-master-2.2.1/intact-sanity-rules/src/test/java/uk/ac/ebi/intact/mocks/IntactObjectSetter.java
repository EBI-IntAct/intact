/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks;

import uk.ac.ebi.intact.model.IntactObject;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class IntactObjectSetter {
    public static IntactObject setBasicObject(IntactObject intactObject){
        intactObject.setAc(AcGenerator.getNextVal());
        intactObject.setCreated(DateGetter.getCreatedDate());
        intactObject.setUpdated(DateGetter.getUpdatedDate());
        intactObject.setCreator("BATMAN");
        intactObject.setUpdator("SUPERMAN");
        return intactObject;
    }
}
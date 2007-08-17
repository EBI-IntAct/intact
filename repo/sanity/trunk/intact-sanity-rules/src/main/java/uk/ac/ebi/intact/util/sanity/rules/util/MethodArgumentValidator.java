/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.rules.util;

import uk.ac.ebi.intact.model.IntactObject;

/**
 * This is used by all the rules. All the rules have to check that the given object they have to check is not null and
 * from the good class. You don't want for example an annotation rule to be given a feature to check.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class MethodArgumentValidator {
    /**
     * Check that intactObject is not null and of class clazz.
     * @param intactObject, the IntactObject to check
     * @param clazz, the class you're IntactObject is supposed to be.
     * @return true
     * @throws NullPointerException if the IntactObject is null
     *         IllegalArgumentException if the intactObject is not a class or sub-class of clazz.
     */
    public static boolean isValidArgument(IntactObject intactObject, Class clazz){
        if(intactObject == null){
            throw new NullPointerException("The given IntactObject is null");
        }
        if(!clazz.isAssignableFrom(intactObject.getClass())){
            throw new IllegalArgumentException("The given IntactObject is not a class or sub-class of " + clazz.getSimpleName());
        }
        return true;
    }
}
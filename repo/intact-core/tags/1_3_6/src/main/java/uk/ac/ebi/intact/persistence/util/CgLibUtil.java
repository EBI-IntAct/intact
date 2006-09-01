/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence.util;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class CgLibUtil {

    /**
     * Gets the correct class, removing the CGLIB enhanced part.
     * (e.g uk.ac.ebi.intact.model.CvInteraction$$EnhancerByCGLIB$$93628752 to uk.ac.ebi.intact.model.CvInteraction)
     * @return the class
     */
    public static Class removeCglibEnhanced(Class clazz)
    {
        if (clazz == null)
        {
            throw new IllegalArgumentException("You must give a non null Class");
        }

        String className = clazz.getName();

        if (className.contains("$$"))
        {
            className = className.substring(0, className.indexOf("$$"));
        }

        try
        {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return clazz;
    }

}

/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence.util;

import org.apache.ojb.broker.VirtualProxy;
import uk.ac.ebi.intact.model.proxy.IntactObjectProxy;

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
     * @return
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

    /**
     * Gives the Object classname, give the real object class name if this is a VirtualProxy class
     *
     * @param obj the object for which we request the real class name.
     *
     * @return the real class name.
     *
     * @see org.apache.ojb.broker.VirtualProxy
     */
    public static <T> Class<T> getRealClassName( T obj ) {
        Class name = null;

        if ( obj instanceof VirtualProxy ) {
            name = ( (IntactObjectProxy) obj ).getRealClassName();
        } else {
            name = obj.getClass();
        }

        return name;
    }

    /**
     * From the real className of an object, gets a displayable name.
     *
     * @param obj the object for which we want the class name to display - the object must not be null
     *
     * @return the classname to display in the view.
     */
    public static String getDisplayableClassName( Object obj ) {

        return getDisplayableClassName( getRealClassName( obj ) );
    }

    /**
     * From the real className of className, gets a displayable name.
     *
     * @param clazz the class for which we want the class name to display - the class must not be null
     *
     * @return the classname to display in the view.
     */
    public static String getDisplayableClassName( Class clazz ) {

        return getDisplayableClassName( clazz.getName() );
    }

    /**
     * From the real className of className, gets a displayable name. <br> 1. get the real class name. 2. Removes the
     * package name 3. try to remove an eventual Impl suffix
     *
     * @param name the class name for which we want the class name to display - the class must not be null
     *
     * @return the classname to display in the view.
     */
    public static String getDisplayableClassName( String name ) {

        int indexDot = name.lastIndexOf( "." );
        int indexImpl = name.lastIndexOf( "Impl" );
        if ( indexImpl != -1 ) {
            name = name.substring( indexDot + 1, indexImpl );
        } else {
            name = name.substring( indexDot + 1 );
        }

        return name;
    }

}

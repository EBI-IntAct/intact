package uk.ac.ebi.intact.core.persister;

import uk.ac.ebi.intact.model.*;

/**
 * Implementing objects allow to find AC of given object based on their properties.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
public interface Finder {

    /**
     * Finds an annotatedObject based on its properties.
     *
     * @param annotatedObject the object we are searching an AC for.
     * @return an AC or null if it couldn't be found.
     */
    public String findAc( AnnotatedObject annotatedObject );

    
}

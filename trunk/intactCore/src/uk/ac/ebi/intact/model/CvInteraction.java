/** Java class "CvInteraction.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package uk.ac.ebi.intact.model;

import java.util.*;

/**
 * <p>
 * The method by which the Interaction has been determined.
 * </p>
 * <p>
 * 
 * </p>
 * <p>
 * 
 * @example co-immunoprecipitation
 * </p>
 * @author hhe
 * </p>
 * </p>
 */
public class CvInteraction extends CvDagObject {

    /**
     * Cache a Vector of all shortLabels of the class, e.g. for menus.
     * This should not be here as it has no model functionality but is
     * related to eg user interfaces.
     */
    protected static Vector menuList = null;

    /**
     * no-arg constructor which will hopefully be removed later...
     */
    public CvInteraction() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvInteraction instance. Requires at least a shortLabel and an
     * owner to be specified.
     * @param shortLabel The memorable label to identify this CvInteraction
     * @param owner The Institution which owns this CvInteraction
     * @exception NullPointerException thrown if either parameters are not specified
     */
    public CvInteraction(String shortLabel, Institution owner) {

        //super call sets up a valid CvObject
        super(shortLabel, owner);
    }


} // end CvInteraction






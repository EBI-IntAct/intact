/** Java class "CvInteraction.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package uk.ac.ebi.intact.model;



/**
 * he method by which the Interaction has been determined.
 * example co-immunoprecipitation
 *
 * @author hhe
 * @version $Id$
 */
public class CvInteraction extends CvDagObject {

    /**
     * Cache a Vector of all shortLabels of the class, e.g. for menus.
     * This should not be here as it has no model functionality but is
     * related to eg user interfaces.
     */
    // TODO should be moved away
//    protected static Vector menuList = null;

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    private CvInteraction() {
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
    public CvInteraction(Institution owner, String shortLabel) {

        //super call sets up a valid CvObject
        super(owner, shortLabel);
    }

} // end CvInteraction






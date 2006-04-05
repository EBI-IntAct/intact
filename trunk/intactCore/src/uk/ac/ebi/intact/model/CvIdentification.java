/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;



/**
 * The method by which the Interactors have been detected.
 * 
 * example "mass spectrometry"
 * @author hhe
 * @version $Id$
 */
public class CvIdentification extends CvDagObject {

    public static final String WESTERN_BLOT = "western blot";
    
    /**
     * Cache a Vector of all shortLabels of the class, e.g. for menus.
     * This should not be here as it has no model functionality but is
     * related to eg user interfaces.
     */
//    protected static Vector menuList = null;

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    private CvIdentification() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvIdentification instance. Requires at least a shortLabel and an
     * owner to be specified.
     * @param shortLabel The memorable label to identify this CvIdentification
     * @param owner The Institution which owns this CvIdentification
     * @exception NullPointerException thrown if either parameters are not specified
     */
    public CvIdentification(Institution owner, String shortLabel) {

        //super call sets up a valid CvObject
        super(owner, shortLabel);
    }

} // end CvIdentification





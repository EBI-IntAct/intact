/**
 Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
 All rights reserved. Please see the file LICENSE
 in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

/**
 * This describes the nature of the molecule. For example, protein, DNA etc.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CvInteractorType extends CvDagObject {

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    private CvInteractorType() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvInteractorType instance. Requires at least a shortLabel and an
     * owner to be specified.
     * @param shortLabel The memorable label to identify this CvInteraction
     * @param owner The Institution which owns this CvInteraction
     * @exception NullPointerException thrown if either parameters are not specified
     */
    public CvInteractorType(Institution owner, String shortLabel) {
        //super call sets up a valid CvObject
        super(owner, shortLabel);
    }
}

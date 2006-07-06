/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 * The role of the specific substrate in the interaction.
 * <p/>
 * example bait example prey
 *
 * @author hhe
 * @version $Id$
 */
public class CvComponentRole extends CvObject implements Editable {

    //////////////////////
    // Constants

    public static final String BAIT = "bait";
    public static final String BAIT_PSI_REF = "MI:0496";

    public static final String PREY = "prey";
    public static final String PREY_PSI_REF = "MI:0498";

    public static final String TARGET = "target";
    public static final String TARGET_PSI_REF = "MI:0502";

    public static final String NEUTRAL = "neutral component";
    public static final String NEUTRAL_PSI_REF = "MI:0497";

    public static final String ENZYME = "enzyme";
    public static final String ENZYME_PSI_REF = "MI:0501";

    public static final String ENZYME_TARGET = "enzyme target";
    public static final String ENZYME_TARGET_PSI_REF = "MI:0502";

    public static final String UNSPECIFIED = "unspecified";
    public static final String UNSPECIFIED_PSI_REF = "MI:0499";

    public static final String SELF = "self";
    public static final String SELF_PSI_REF = "MI:0503";


    /**
     * This constructor should <b>not</b> be used as it could result in objects with invalid state. It is here for
     * object mapping purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    private CvComponentRole() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvComponentRole instance. Requires at least a shortLabel and an owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvComponentRole
     * @param owner      The Institution which owns this CvComponentRole
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public CvComponentRole( Institution owner, String shortLabel ) {

        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }

} // end CvComponentRole





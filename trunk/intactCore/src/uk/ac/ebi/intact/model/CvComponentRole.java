/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.annotation.EditorTopic;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;

/**
 * The role of the specific substrate in the interaction.
 * <p/>
 * example bait example prey
 *
 * @author hhe
 * @version $Id$
 */
@Entity
@DiscriminatorValue("uk.ac.ebi.intact.model.CvComponentRole")
@EditorTopic
public class CvComponentRole extends CvObject implements Editable {

    //////////////////////
    // Constants

    public static final String INHIBITOR = "inhibitor";
    public static final String INHIBITOR_PSI_REF = "MI:0586";

    public static final String INHIBITED = "inhibited";
    public static final String INHIBITED_PSI_REF = "MI:0587";

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

    public static final String ELECTRON_DONOR = "electron donor";
    public static final String ELECTRON_DONOR_MI_REF = "MI:0579";

    public static final String ELECTRON_ACCEPTOR = "electron acceptor";
    public static final String ELECTRON_ACCEPTOR_MI_REF = "MI:0580";

    public static final String FLUROPHORE_DONOR = "donor fluorophore";
    public static final String FLUROPHORE_DONOR_MI_REF = "MI:0583";

    public static final String FLUROPHORE_ACCEPTOR = "acceptor fluorophore";
    public static final String FLUROPHORE_ACCEPTOR_MI_REF = "MI:0584";


    /**
     * This constructor should <b>not</b> be used as it could result in objects with invalid state. It is here for
     * object mapping purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public CvComponentRole() {
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





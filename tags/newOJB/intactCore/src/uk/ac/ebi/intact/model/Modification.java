/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 * An amino acid modification.
 *
 * example: phosphorylation
 * todo further specification
 *
 * @author hhe
 * @version $Id$
 */
public class Modification extends BasicObjectImpl {

    ///////////////////////////////////////
    // associations


    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String rangeAc;
    public String cvModificationTypeAc;

    /**
     * TODO comments
     */
    private Range range;

    /**
     * TODO comments
     */
    private CvModificationType cvModificationType;

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    private Modification() {
        super();
    }

    public Modification(Institution owner, Range range, CvModificationType cvModificationType) {

        // TODO we assumes the modification has an owner. is it a BasicObject.
        // Owner existence here needs to be discussed.
        super (owner);
        if(range == null) throw new NullPointerException("Must define a range for a " +getClass().getName());
        if(cvModificationType == null) throw new NullPointerException("Must define a modification type for a " +getClass().getName());
        this.range = range;
        this.cvModificationType = cvModificationType;
        this.range.setModification(this);
    }


    ///////////////////////////////////////
    // access methods for associations

    public Range getRange() {
        return range;
    }

    // TODO does it make sense to modify it after creation.
    public void setRange(Range range) {
        if (this.range != range) {
            this.range = range;
            if (range != null) range.setModification(this);
        }
    }
    public CvModificationType getCvModificationType() {
        return cvModificationType;
    }

    // TODO does it make sense to modify it after creation.
    public void setCvModificationType(CvModificationType cvModificationType) {
        this.cvModificationType = cvModificationType;
    }


    /**
     * Equality for Modifications is currently based on equality for
     * <code>CvModificationType</code> and <code>Range</code>.
     * @see uk.ac.ebi.intact.model.Range
     * @see uk.ac.ebi.intact.model.CvModificationType
     * @param o The object to check
     * @return true if the parameter equlas this object, false otherwise
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Modification)) return false;

        //TODO auto-generat6ed - needs to be made more readable when we use this class..
        final Modification modification = (Modification) o;

        if (cvModificationType != null ? !cvModificationType.equals(modification.cvModificationType) : modification.cvModificationType != null) return false;
        if (range != null ? !range.equals(modification.range) : modification.range != null) return false;

        return true;
    }

    public int hashCode() {
        int result;

        //TODO auto-generat6ed - needs to be made more readable when we use this class..
        result = (range != null ? range.hashCode() : 0);
        result = 29 * result + (cvModificationType != null ? cvModificationType.hashCode() : 0);
        return result;
    }

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String getRangeAc() {
        return this.rangeAc;
    }
    public void setRangeAc(String ac) {
        this.rangeAc = ac;
    }

    public String getCvModificationTypeAc() {
        return this.cvModificationTypeAc;
    }
    public void setCvModificationTypeAc(String ac) {
        this.cvModificationTypeAc = ac;
    }

} // end Modification





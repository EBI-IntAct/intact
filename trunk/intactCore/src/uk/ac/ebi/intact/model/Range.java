/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 * TODO comments
 *
 * @author hhe
 * @version $Id$
 */
public class Range extends BasicObjectImpl {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    private String ModificationAc;

    /**
     * TODO Represents ...
     */
    private int posFrom;

    /**
     * TODO Represents ...
     */
    private int posTo;

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     */
    private Modification modification;

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    private Range() {
        super();
    }
    ////////////////////////////////////////
    // Constructor

    public Range (int from, int to) {

        if (to < from) throw new IllegalArgumentException ("End of range must be bigger that begining");
        if (from < 0) throw new IllegalArgumentException ("Start of range should be positive");
        if (to < 0) throw new IllegalArgumentException ("End of range should be positive");

        posFrom = from;
        posTo = to;
    }


    ///////////////////////////////////////
    //access methods for attributes

    public int getPosFrom() {
        return posFrom;
    }
    public void setPosFrom(int posFrom) {
        this.posFrom = posFrom;
    }
    public int getPosTo() {
        return posTo;
    }
    public void setPosTo(int posTo) {
        this.posTo = posTo;
    }

    ///////////////////////////////////////
    // access methods for associations

    public Modification getModification() {
        return modification;
    }

    public void setModification(Modification modification) {
        if (this.modification != modification) {
            this.modification = modification;
            if (modification != null) modification.setRange(this);  
        }      
    }


    /**
     * Equality for Ranges is currently based on equality for
     * <code>Modification</code>, position from and position to (ints).
     * @see uk.ac.ebi.intact.model.Modification
     * @param o The object to check
     * @return true if the parameter equlas this object, false otherwise
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Range)) return false;

        final Range range = (Range) o;

        //TODO Auto-generated - needs rewriting later to be more readable...
        if (posFrom != range.posFrom) return false;
        if (posTo != range.posTo) return false;
        if (modification != null ? !modification.equals(range.modification) : range.modification != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = posFrom;
        //TODO Auto-generated - needs rewriting later to be more readable...
        result = 29 * result + posTo;
        result = 29 * result + (modification != null ? modification.hashCode() : 0);
        return result;
    }


    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String getModificationAc() {
        return this.ModificationAc;
    }
    public void setModificationAc(String ac) {
        this.ModificationAc = ac;
    }

} // end Range





/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 * An alternative name for the object.
 *
 * @author hhe
 */
public class Alias extends BasicObject {

    ///////////////////////////////////////
    //attributes

    /**
     * Alternative name for the object.
     */
    private String name;

    ///////////////////////////////////////
    // associations

    /**
     *
     */
    private CvAliasType cvAliasType;
    public String cvAliasTypeAc; // only needed for the OJB mapping.

    /**
     * Accession id to the Object to which refers that alias.
     */
    private String parentAc;

    // MUST be declared to allow OJB to create instances of the object
    public Alias() {
        super();
    }

    /**
     * Create a new Alias for the given Annotated object
     *
     * @param annotatedObject the object to which we'll add a new Alias
     * @param cvAliasType the CvAliasType
     * @param name the name of the alias
     *
     * @see uk.ac.ebi.intact.model.CvAliasType
     * @see uk.ac.ebi.intact.model.AnnotatedObject
     */
    public Alias ( Institution anOwner, AnnotatedObject annotatedObject, CvAliasType cvAliasType, String name ) {
        this();
        setOwner(anOwner);
        String ac = annotatedObject.getAc();
        if ( ac == null ){
            throw new IllegalArgumentException( "The given Annotated object doesn't have an AC." );
        }

        this.parentAc = annotatedObject.getAc();
        this.cvAliasType = cvAliasType;
        this.name = name;
    }

    ///////////////////////////////////////
    //access methods for attributes

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getParentAc() {
        return parentAc;
    }

    public void setParentAc( String parentAc ) {
        this.parentAc = parentAc;
    }

    ///////////////////////////////////////
    // access methods for associations

    public CvAliasType getCvAliasType() {
        return cvAliasType;
    }

    public void setCvAliasType(CvAliasType cvAliasType) {
        this.cvAliasType = cvAliasType;
    }

    public boolean equals ( Object o ) {
        if ( this == o ) return true;
        if ( !(o instanceof Alias) ) return false;
        if ( !super.equals ( o ) ) return false;

        final Alias alias = (Alias) o;

        if ( !cvAliasType.equals ( alias.cvAliasType ) ) return false;
        if ( !name.equals ( alias.name ) ) return false;

        return true;
    }

    public int hashCode () {
        int result = super.hashCode ();
        result = 29 * result + name.hashCode ();
        result = 29 * result + cvAliasType.hashCode ();
        return result;
    }

} // end Alias





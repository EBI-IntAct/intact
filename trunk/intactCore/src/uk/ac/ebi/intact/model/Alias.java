/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;

/**
 * An alternative name for the object.
 * <p/>
 * <p/>
 * Currently, the name of the Alias is set to lowercase.
 * </p>
 *
 * @author hhe, Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @see uk.ac.ebi.intact.model.CvAliasType
 */
@Entity
@Table(name = "ia_alias")
public class Alias extends BasicObjectImpl {

    private static final int MAX_ALIAS_NAME_LEN = 30;

    ///////////////////////////////////////
    //attributes

    /**
     * Alternative name for the object.
     */
    private String name;

    ///////////////////////////////////////
    // associations

    /**
     * the type of that alias.
     */
    private CvAliasType cvAliasType;
    public String cvAliasTypeAc; // only needed for the OJB mapping.

    /**
     * Accession id to the Object to which refers that alias.
     */
    private String parentAc;

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public Alias() {
        super();
    }

    /**
     * Create a new Alias for the given Annotated object
     *
     * @param anOwner         The institution owning this Alias
     * @param annotatedObject the object to which we'll add a new Alias
     * @param cvAliasType     the CvAliasType (may be null)
     * @param name            the name of the alias (namy be null)
     * @see uk.ac.ebi.intact.model.CvAliasType
     * @see uk.ac.ebi.intact.model.AnnotatedObject
     */
    public Alias( Institution anOwner, AnnotatedObject annotatedObject, CvAliasType cvAliasType, String name ) {
        setOwner( anOwner );
        setParentAc( annotatedObject.getAc() );
        setCvAliasType( cvAliasType );
        setName( name );
    }

    ///////////////////////////////////////
    //access methods for attributes

    public String getName() {
        return name;
    }

    public void setName( String name ) {

        if( name != null ) {

            // delete leading and trailing spaces.
            name = name.trim();

            if( name.length() >= MAX_ALIAS_NAME_LEN ) {
                name = name.substring( 0, MAX_ALIAS_NAME_LEN );
            }
        }

        this.name = name;
    }

    @Column(name = "parent_ac")
    public String getParentAc() {
        return parentAc;
    }

    public void setParentAc( String parentAc ) {

        if( null == parentAc ) {
            throw new IllegalArgumentException( "The given Annotated object doesn't have an AC." );
        }

        this.parentAc = parentAc;
    }

    ///////////////////////////////////////
    // access methods for associations

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "aliastype_ac")
    public CvAliasType getCvAliasType() {
        return cvAliasType;
    }

    public void setCvAliasType( CvAliasType cvAliasType ) {
        this.cvAliasType = cvAliasType;
    }

    /**
     * Equality for Aliases is currently based on equality for
     * <code>CvAliasTypes</code> and names.
     *
     * @param o The object to check
     * @return true if the parameter equals this object, false otherwise
     * @see uk.ac.ebi.intact.model.CvAliasType
     */
    @Override
    public boolean equals( Object o ) {
        if( this == o ) return true;
        if( !( o instanceof Alias ) ) return false;

        //NO! BasicObject's equals is the Java Object one!!
        //if ( !super.equals ( o ) ) return false;

        final Alias alias = (Alias) o;

        //NB according to the constructor, cvAliasType and name may be null,
        //so need to handle this here....
        if( cvAliasType != null ) {
            if( !cvAliasType.equals( alias.cvAliasType ) ) return false;
        } else {
            if( alias.cvAliasType != null ) return false;
        }

        if( name != null ) {
            if( !name.equals( alias.name ) ) return false;
        } else
            return alias.name == null;

        return true;
    }

    @Override
    public int hashCode() {
        int result = 29;
        if( name != null ) result = 29 * result + name.hashCode();
        if( cvAliasType != null ) result = 29 * result + cvAliasType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Alias[name: " + name + ", type: " +
               ( cvAliasType != null ? cvAliasType.getShortLabel() : "" ) + "]";
    }

} // end Alias





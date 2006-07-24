/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.Length;

import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents an object with biological annotation.
 *
 * @author hhe
 */
@MappedSuperclass
public abstract class AnnotatedObjectImpl<T extends Xref> extends BasicObjectImpl implements AnnotatedObject<T> {

    private static final Log log = LogFactory.getLog(AnnotatedObjectImpl.class);

    /////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    protected String curatorAc;

    /**
     * Short name for the object, not necessarily unique. To be used for example
     * in minimised displays of the object.
     */
    protected String shortLabel;

    /**
     * The full name or a minimal description of the object.
     */
    protected String fullName;

    ///////////////////////////////////////
    // associations

    /**
     *
     */
    public Collection<Annotation> annotations = new ArrayList<Annotation>();

    /**
     *
     */
    public Collection<T> xrefs = new ArrayList<T>();

    /**
     * Hold aliases of an Annotated object.
     * ie. alternative name for the current object.
     */
    private Collection<Alias> aliases = new ArrayList<Alias>();

    /**
     *
     */
    public Collection<Reference> references = new ArrayList<Reference>();

    /**
     * no-arg constructor provided for compatibility with subclasses
     * that have no-arg constructors.
     */
    protected AnnotatedObjectImpl() {
        //super call sets creation time data
        super();
    }

    /**
     * Constructor for subclass use only. Ensures that AnnotatedObjects cannot be
     * created without at least a shortLabel and an owner specified.
     *
     * @param shortLabel The memorable label to identify this AnnotatedObject
     * @param owner      The Institution which owns this AnnotatedObject
     * @throws NullPointerException thrown if either parameters are not specified
     */
    protected AnnotatedObjectImpl( String shortLabel, Institution owner ) {

        //super call sets creation time data
        super();

        setShortLabel( shortLabel );
        setOwner( owner );
    }
    // Class methods

    ///////////////////////////////////////
    //access methods for attributes

    @Length(min = 1, max = MAX_SHORT_LABEL_LEN)
    public String getShortLabel() {
        return shortLabel;
    }

    public void setShortLabel( String shortLabel ) {

        if( shortLabel == null ) {

            throw new NullPointerException( "Must define a non null short label for a " + getClass().getName() );

        } else {

            // delete leading and trailing spaces.
            shortLabel = shortLabel.trim();

            if( "".equals( shortLabel ) ) {
                throw new IllegalArgumentException(
                        "Must define a non empty short label for a " + getClass().getName() );
            }

            if( shortLabel.length() >= MAX_SHORT_LABEL_LEN ) {
                shortLabel = shortLabel.substring( 0, MAX_SHORT_LABEL_LEN );
            }
        }

        this.shortLabel = shortLabel;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName( String fullName ) {

        if( fullName != null ) {
            // delete leading and trailing spaces.
            fullName = fullName.trim();
        }

        this.fullName = fullName;
    }

    ///////////////////////////////////////
    // access methods for associations
    public void setAnnotations( Collection<Annotation> someAnnotation ) {
        this.annotations = someAnnotation;
    }

    /**
     * This property must be overriden so it can have proper mappings
     * @return
     */
    @Transient
    public Collection<Annotation> getAnnotations() {
        return annotations;
    }

    public void addAnnotation( Annotation annotation ) {
        if (!this.annotations.contains(annotation))
        {
            this.annotations.add(annotation);
        }
    }

    public void removeAnnotation( Annotation annotation ) {
        this.annotations.remove( annotation );
    }

    ///////////////////
    // Xref related
    ///////////////////
    // FIXME: this method do nothing now...
    public void setXrefs( Collection<T> someXrefs ) {
        //log.warn("FIXME: This method has been hacked to make the getXrefs thing work");
        this.xrefs = someXrefs;
    }

    @OneToMany
    @JoinColumn(name = "parent_ac", referencedColumnName = "ac")
    // FIXME: using called to DAO instead hibernate mappings (not possible with the current db)
    public Collection<T> getXrefs() {
        /*
       log.warn("FIXME: Call to getXrefs method, which is not mapped and uses a DAO call instead");
        if (ac == null)
        {
            throw new IntactException("There is an ugly hack in the getXrefs methods that might have provoked this exception.");
        }

        return DaoFactory.getXrefDao().getByParentAc(ac);   */
        return xrefs;
    }

    /**
     * Adds an xref to the object. The xref will only be added
     * if an equivalent xref is not yet part of the object.
     */
    public void addXref( T aXref ) {
        //if( !this.xrefs.contains( aXref ) ) {
        //    this.xrefs.add( aXref );
            //aXref.setParent(this);
            aXref.setParentAc(this.getAc());
        //}
    }

    // FIXME: using called to DAO instead hibernate mappings (not possible with the current db)
    public void removeXref( T xref ) {
        /*
        log.warn("FIXME: Call to removeXref method, which is not mapped and uses a DAO call instead");
        if (ac == null)
        {
            throw new IntactException("There is an ugly hack in the getXrefs methods that might have provoked this exception.");
        }  */

        //DaoFactory.getXrefDao().delete(xref);
        this.xrefs.remove( xref );
    }


    ///////////////////
    // Alias related
    ///////////////////
    public void setAliases( Collection<Alias> someAliases ) {
        this.aliases = someAliases;
    }

    @OneToMany
    @JoinColumn (name = "parent_ac", referencedColumnName = "ac", insertable = false, updatable = false)
    public Collection<Alias> getAliases() {
        return aliases;
    }

    /**
     * Adds an alias to the object. The alis will only be added
     * if an equivalent alias is not yet part of the object.
     */
    public void addAlias( Alias alias ) {
        if( !this.aliases.contains( alias ) ) {
            this.aliases.add( alias );
        }
    }

    public void removeAlias( Alias alias ) {
        this.aliases.remove( alias );
    }


    public void setReferences( Collection<Reference> someReferences ) {
        this.references = someReferences;
    }

    @Transient
    public Collection<Reference> getReferences() {
        return references;
    }

    public void addReference( Reference reference ) {
        if( !this.references.contains( reference ) ) {
            this.references.add( reference );
            reference.addAnnotatedObject( this );
        }
    }

    public void removeReference( Reference reference ) {
        boolean removed = this.references.remove( reference );
        if (removed)
        {
            reference.removeAnnotatedObject(this);
        }
    }

    ///////////////////////////////////////
    // instance methods

    /**
     * Equality for AnnotatedObjects is currently based on equality for
     * <code>Xrefs</code>, shortLabels and fullNames.
     *
     * @param o The object to check
     * @return true if the parameter equals this object, false otherwise
     * @see uk.ac.ebi.intact.model.Xref
     */
    @Override
    public boolean equals( Object o ) {
        // TODO: the reviewed version of the intact model will provide a better implementation
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof AnnotatedObject))
        {
            return false;
        }

        //YUK! This ends up calling the java Object 'equals', which compares
        //on identity - NOT what we want....
//        if (!super.equals(o)) {
//            return false;
//        }

        final AnnotatedObject annotatedObject = (AnnotatedObject) o;

        //short label and full name (if it exists) must be equal also..
        if( shortLabel != null ) {
            if (!shortLabel.equals(annotatedObject.getShortLabel()))
            {
                return false;
            }
        } else {
            if (annotatedObject.getShortLabel() != null)
            {
                return false;
            }
        }

        if( fullName != null ) {
            if (!fullName.equals(annotatedObject.getFullName()))
            {
                return false;
            }
        } else {
            if (annotatedObject.getFullName() != null)
            {
                return false;
            }
        }

        return CollectionUtils.isEqualCollection( getXrefs(), annotatedObject.getXrefs() );
    }

    /**
     * This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     *
     * @return hash code of the object.
     */
    @Override
    public int hashCode() {

        //YUK AGAIN! ends up as a call to Object's hashcode, which is
        //based on indentity. Not require here...
        //int code = super.hashCode();

        int code = 29;

        for (Xref xref : getXrefs())
        {
            code = 29 * code + xref.hashCode();
        }

        if (null != shortLabel)
        {
            code = 29 * code + shortLabel.hashCode();
        }
        if (null != fullName)
        {
            code = 29 * code + fullName.hashCode();
        }

        return code;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AnnotatedObjectImpl copy = (AnnotatedObjectImpl) super.clone();

        Collection<T> xrefs = getXrefs();

        // Append the "-x" to the short label.
        copy.shortLabel += "-x";

        // Clone annotations; can't use annotations.clone here as annoatations
        // type is shown as a ListProxy (ClassCastException)
        copy.annotations = new ArrayList<Annotation>( annotations.size() );
        for (Annotation annotation : annotations)
        {
            copy.annotations.add((Annotation) annotation.clone());
        }

        // Clone xrefs.
        Collection<Xref> copiedXrefs = new ArrayList<Xref>(xrefs.size());
        // Make deep copies.
        for (Xref xref : getXrefs())
        {
            copiedXrefs.add((Xref)xref.clone());
        }

        copy.setXrefs(copiedXrefs);

        return copy;
    }

    @Override
    public String toString() {
        return this.getAc() + "; owner=" + this.getOwner().getAc()
               + "; name=" + this.shortLabel + "; fullname=" + fullName;
    }

} // end AnnotatedObject






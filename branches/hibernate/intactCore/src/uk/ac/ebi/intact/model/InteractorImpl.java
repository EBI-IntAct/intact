/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.apache.commons.collections.CollectionUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
import javax.persistence.FetchType;
import javax.persistence.Column;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Defines a generic interacting object.
 *
 * @author hhe
 * @version $Id$
 */
@Entity
@Table(name = "ia_interactor")
@DiscriminatorColumn(name="objclass")
public abstract class InteractorImpl extends AnnotatedObjectImpl implements Interactor {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    private String bioSourceAc;

    private String objClass;

    /**
     * The biological source of the Interactor.
     */
    private BioSource bioSource;

    /**
     * For OJB access
     */
    private String cvInteractorTypeAc;

    /**
     * The interactor type.
     */
    private CvInteractorType interactorType;

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     */
    private Collection<Component> activeInstances = new ArrayList<Component>();

    /**
     * TODO comments
     */
    private Collection<Product> products = new ArrayList<Product>();


    /**
     * no-arg constructor provided for compatibility with subclasses
     * that have no-arg constructors.
     */
    public InteractorImpl() {
        //super call sets creation time data
        super();
    }

    /**
     * Constructor for subclass use only. Ensures that Interactors cannot be
     * created without at least a shortLabel and an owner specified. NOTE: It is
     * assumed that subclasses of Interactor will supply a valid BioSource; this
     * is initially set to null but <b>other classes may expect it to be non-null</b>.
     *
     * @param shortLabel The memorable label to identify this Interactor
     * @param owner      The Institution which owns this Interactor
     * @throws NullPointerException thrown if either parameters are not specified
     *
     * @deprecated Use {@link #InteractorImpl(String, Institution, CvInteractorType)} instead
     */
    @Deprecated
    protected InteractorImpl( String shortLabel, Institution owner ) {
        this( shortLabel, owner, null );
    }


    /**
     * Constructor for subclass use only. Ensures that Interactors cannot be
     * created without at least a shortLabel, an owner and type specified. NOTE: It is
     * assumed that subclasses of Interactor will supply a valid BioSource; this
     * is initially set to null but <b>other classes may expect it to be non-null</b>.
     *
     * @param shortLabel The memorable label to identify this Interactor
     * @param owner      The Institution which owns this Interactor
     * @param type       The Interactor type
     * @throws NullPointerException thrown if either parameters are not specified
     */
    protected InteractorImpl( String shortLabel, Institution owner, CvInteractorType type ) {
        super( shortLabel, owner );
        setCvInteractorType(type);
    }

    ///////////////////////////////////////
    //access methods for attributes

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "biosource_ac")
    public BioSource getBioSource() {
        return bioSource;
    }

    public void setBioSource( BioSource bioSource ) {
//        if( bioSource == null ) {
//            throw new NullPointerException( "valid Interactor must have a BioSource!" );
//        }
        this.bioSource = bioSource;
    }

    ///////////////////////////////////////
    // access methods for associations
    public void setActiveInstances( Collection<Component> someActiveInstance ) {
        this.activeInstances = someActiveInstance;
    }

    @OneToMany (mappedBy = "interactor")
    public Collection<Component> getActiveInstances() {
        return activeInstances;
    }

    public void addActiveInstance( Component component ) {
        if( !this.activeInstances.contains( component ) ) {
            this.activeInstances.add( component );
            component.setInteractor( this );
        }
    }

    public void removeActiveInstance( Component component ) {
        boolean removed = this.activeInstances.remove( component );
        if (removed)
        {
            component.setInteractor(null);
        }
    }

    public void setProducts( Collection<Product> someProduct ) {
        this.products = someProduct;
    }

    @Transient
    public Collection<Product> getProducts() {
        return products;
    }

    public void addProduct( Product product ) {
        if( !this.products.contains( product ) ) {
            this.products.add( product );
            product.setInteractor( this );
        }
    }

    public void removeProduct( Product product ) {
        boolean removed = this.products.remove( product );
        if (removed)
        {
            product.setInteractor(null);
        }
    }

    public void setCvInteractorType(CvInteractorType type) {
        interactorType = type;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interactortype_ac")
    public CvInteractorType getCvInteractorType() {
        return interactorType;
    }
    
    @ManyToMany
    @JoinTable(
        name="ia_int2annot",
        joinColumns={@JoinColumn(name="interactor_ac")},
        inverseJoinColumns={@JoinColumn(name="annotation_ac")}
    )
    @Override
    public Collection<Annotation> getAnnotations()
    {
        return super.getAnnotations();
    }

    @Column(insertable = false, updatable = false)
    public String getObjClass()
    {
        return objClass;
    }

    public void setObjClass(String objClass)
    {
        this.objClass = objClass;
    }

    ///////////////////////////////////////
    // instance methods
    @Override
    public String toString() {
        String result;
        Iterator i;

        result = "AC: " + this.getAc() + " Owner: " + this.getOwner().getShortLabel()
                 + " Label: " + this.getShortLabel() + "[";

        if( null != this.getXrefs() ) {
            i = this.getXrefs().iterator();
            while( i.hasNext() ) {
                result = result + i.next();
            }
        }

        return result + "]";
    }

    /**
     * Equality for Interactors is currently based on equality for
     * <code>AnnotatedObjects</code>, BioSources and Products.
     *
     * @param o The object to check
     * @return true if the parameter equlas this object, false otherwise
     * @see uk.ac.ebi.intact.model.AnnotatedObject
     */
    @Override
    public boolean equals( Object o ) {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Interactor))
        {
            return false;
        }
        if (!super.equals(o))
        {
            return false;
        }

        final Interactor interactor = (Interactor) o;

        // possible cycle ...
        // if (!activeInstances.equals(interactor.activeInstances)) return false;

        if (bioSource != null) {
            if( !bioSource.equals( interactor.getBioSource() ) ) {
                return false;
            }
        }
        // We should remove this check later as interactor type is mandatory.
        // This is here till we migrated all our code to accept the InteractorType.
        if (interactorType != null) {
            if (!interactorType.equals(interactor.getCvInteractorType())) {
                return false;
            }
        }
        return CollectionUtils.isEqualCollection( interactor.getProducts(), products );
    }

    @Override
    public int hashCode() {

        int code = 29 * super.hashCode();

        if (bioSource != null) {
            code = 29 * code + bioSource.hashCode();
        }

        // Can't take into account activeInstances since it happens that an object
        // references himself into that collection ... it would never ends.
        if( !products.isEmpty() ) {
            //add the codes for those too....
            int productsCode = 17;
            for( Iterator it = products.iterator(); it.hasNext(); ) {
                Product prod = (Product) it.next(); //controlled here - must be valid cast
                productsCode = 29 * productsCode + prod.hashCode();
            }
            code = 29 * code + productsCode;
        }

        return code;
    }

} // end Interactor





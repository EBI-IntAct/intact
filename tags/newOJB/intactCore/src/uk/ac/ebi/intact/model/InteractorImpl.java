/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Defines a generic interacting object.
 *
 * @author hhe
 * @version $Id$
 */
public abstract class InteractorImpl extends AnnotatedObjectImpl implements Interactor {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    private String bioSourceAc;

    /**
     * The biological source of the Interactor.
     */
    private BioSource bioSource;

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     */
    private Collection activeInstances = new ArrayList();

    /**
     * TODO comments
     */
    private Collection products = new ArrayList();


    /**
     * no-arg constructor provided for compatibility with subclasses
     * that have no-arg constructors.
     */
    protected InteractorImpl() {
        //super call sets creation time data
        super();
    }

    /**
     * Constructor for subclass use only. Ensures that Interactors cannot be
     * created without at least a shortLabel and an owner specified. NOTE: It is
     * assumed that subclasses of Interactor will supply a valid BioSource; this
     * is initially set to null but <b>other classes may expect it to be non-null</b>.
     * @param shortLabel The memorable label to identify this Interactor
     * @param owner The Institution which owns this Interactor
     * @exception NullPointerException thrown if either parameters are not specified
     */
    protected InteractorImpl(String shortLabel, Institution owner) {

        //NB is more than this required to define a valid Interactor?
        super(shortLabel, owner);

    }


    ///////////////////////////////////////
    //access methods for attributes

    public BioSource getBioSource() {
        return bioSource;
    }
    public void setBioSource( BioSource bioSource ) {
        if (bioSource == null) throw new NullPointerException("valid Interactor must have a BioSource!");
        this.bioSource = bioSource;
    }

    ///////////////////////////////////////
    // access methods for associations
    public void setActiveInstances(Collection someActiveInstance) {
        this.activeInstances = someActiveInstance;
    }
    public Collection getActiveInstances() {
        return activeInstances;
    }
    public void addActiveInstance(Component component) {
        if (! this.activeInstances.contains(component)) {
            this.activeInstances.add(component);
            component.setInteractor(this);
        }
    }
    public void removeActiveInstance(Component component) {
        boolean removed = this.activeInstances.remove(component);
        if (removed) component.setInteractor(null);
    }
    public void setProducts(Collection someProduct) {
        this.products = someProduct;
    }
    public Collection getProducts() {
        return products;
    }
    public void addProduct(Product product) {
        if (! this.products.contains(product)) {
            this.products.add(product);
            product.setInteractor(this);
        }
    }
    public void removeProduct(Product product) {
        boolean removed = this.products.remove(product);
        if (removed) product.setInteractor(null);
    }

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String getBioSourceAc() {
        return this.bioSourceAc;
    }
    public void setBioSourceAc(String bioSourceAc) {
        this.bioSourceAc = bioSourceAc;
    }

    ///////////////////////////////////////
    // instance methods

    public String toString(){
        String result;
        Iterator i;

        result = "Interactor: " + this.getAc()
                + " Owner: " + this.getOwner().getShortLabel()
                + " Label: " + this.getShortLabel()
                + "[";

        if (null != this.getXrefs()){
            i = this.getXrefs().iterator();
            while(i.hasNext()){
                result = result + i.next();
            }
        }

        return result + "]";
    }

    /**
     * Equality for Interactors is currently based on equality for
     * <code>AnnotatedObjects</code>, BioSources and Products.
     * @see uk.ac.ebi.intact.model.AnnotatedObject
     * @param o The object to check
     * @return true if the parameter equlas this object, false otherwise
     */
    public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof Interactor)) return false;
        if (!super.equals(o)) return false;

        final Interactor interactor = (Interactor) o;

        // possible cycle ...
        // if (!activeInstances.equals(interactor.activeInstances)) return false;
        if(bioSource != null) {
            if (! bioSource.equals(interactor.getBioSource())) return false;
        }
        else {
            if (interactor.getBioSource() != null) return false;
        }
        return CollectionUtils.isEqualCollection(interactor.getProducts(), products);
    }

    public int hashCode() {

        int code = 29 * super.hashCode();

        //BioSource should NEVER be null as subclasses must set it -
        //however just check it here to be safe....
        if(bioSource != null) code = code + bioSource.hashCode();

   	    /*
         * Can't take into account activeInstances since it happens that an object
         * references himself into that collection ... it would never ends.
         */
        if(!products.isEmpty()) {
            //add the codes for those too....
            int productsCode = 17;
            for(Iterator it = products.iterator(); it.hasNext();) {
                Product prod = (Product)it.next(); //controlled here - must be valid cast
                productsCode = 29*productsCode + prod.hashCode();
            }
            code = 29*code + productsCode;
        }
        return code;
    }

} // end Interactor





/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**
 * Defines a generic interacting object.
 *
 * @author hhe
 */
public abstract class Interactor extends AnnotatedObject {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    protected String bioSourceAc;

    /**
     * The biological source of the Interactor.
     */
    protected BioSource bioSource;

    ///////////////////////////////////////
    // associations

    /**
     *
     */
    public Collection activeInstance = new Vector();
    /**
     *
     */
    public Collection product = new Vector();


    ///////////////////////////////////////
    //access methods for attributes

    public BioSource getBioSource() {
        return bioSource;
    }
    public void setBioSource(BioSource bioSource) {
        this.bioSource = bioSource;
    }

    ///////////////////////////////////////
    // access methods for associations
    public void setActiveInstance(Collection someActiveInstance) {
        this.activeInstance = someActiveInstance;
    }
    public Collection getActiveInstance() {
        return activeInstance;
    }
    public void addActiveInstance(Component component) {
        if (! this.activeInstance.contains(component)) {
            this.activeInstance.add(component);
            component.setInteractor(this);
        }
    }
    public void removeActiveInstance(Component component) {
        boolean removed = this.activeInstance.remove(component);
        if (removed) component.setInteractor(null);
    }
    public void setProduct(Collection someProduct) {
        this.product = someProduct;
    }
    public Collection getProduct() {
        return product;
    }
    public void addProduct(Product product) {
        if (! this.product.contains(product)) {
            this.product.add(product);
            product.setInteractor(this);
        }
    }
    public void removeProduct(Product product) {
        boolean removed = this.product.remove(product);
        if (removed) product.setInteractor(null);
    }

    //attributes used for mapping BasicObjects - project synchron
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
        if (null != this.getXref()){
            i = this.getXref().iterator();
            while(i.hasNext()){
                result = result + i.next();
            }
        }

        return result + "]\n";
    }


    public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof Interactor)) return false;
        if (!super.equals(o)) return false;

        final Interactor interactor = (Interactor) o;

        if (!activeInstance.equals(interactor.activeInstance)) return false;
        if (bioSource != null ? !bioSource.equals(interactor.bioSource) : interactor.bioSource != null) return false;
        if (bioSourceAc != null ? !bioSourceAc.equals(interactor.bioSourceAc) : interactor.bioSourceAc != null) return false;
        if (!product.equals(interactor.product)) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (bioSourceAc != null ? bioSourceAc.hashCode() : 0);
        result = 29 * result + (bioSource != null ? bioSource.hashCode() : 0);
        result = 29 * result + activeInstance.hashCode();
        result = 29 * result + product.hashCode();
        return result;
    }

} // end Interactor





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

 private String bioSourceAc;
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
} // end Interactor





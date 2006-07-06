/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.Collection;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 *
 * @see uk.ac.ebi.intact.model.Interactor
 */
public interface Interactor extends AnnotatedObject {

    public BioSource getBioSource();

    public void setBioSource( BioSource bioSource );

    ///////////////////////////////////////
    // access methods for associations
    public void setActiveInstances(Collection someActiveInstance);

    public Collection getActiveInstances();

    public void addActiveInstance(Component component);

    public void removeActiveInstance(Component component);

    public void setProducts(Collection someProduct);

    public Collection getProducts();

    public void addProduct(Product product);

    public void removeProduct(Product product);

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String getBioSourceAc();

    public void setBioSourceAc(String bioSourceAc);

    public String toString();

    public boolean equals (Object o);

    public int hashCode();

}

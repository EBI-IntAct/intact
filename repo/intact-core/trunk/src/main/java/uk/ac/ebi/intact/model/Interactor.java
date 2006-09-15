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
public interface Interactor extends AnnotatedObject<InteractorXref,InteractorAlias> {

    public BioSource getBioSource();

    public void setBioSource( BioSource bioSource );

    ///////////////////////////////////////
    // access methods for associations
    public void setActiveInstances(Collection<Component> someActiveInstance);

    public Collection<Component> getActiveInstances();

    public void addActiveInstance(Component component);

    public void removeActiveInstance(Component component);

    public void setProducts(Collection<Product> someProduct);

    public Collection<Product> getProducts();

    public void addProduct(Product product);

    public void removeProduct(Product product);

    public CvInteractorType getCvInteractorType();
    public void setCvInteractorType(CvInteractorType type);
}

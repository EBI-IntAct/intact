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
 * @see uk.ac.ebi.intact.model.InteractionImpl
 */
public interface Interaction extends Interactor {

    public Float getKD();

    public void setKD(Float kD);

    public void setComponents(Collection someComponent);

    public Collection getComponents();

    public void addComponent(Component component);

    public void removeComponent(Component component);

    public void setReleased(Collection someReleased);

    public Collection getReleased();

    public void addReleased(Product product);

    public void removeReleased(Product product);

    public void setExperiments(Collection someExperiment);

    public Collection getExperiments();

    public void addExperiment(Experiment experiment);

    public void removeExperiment(Experiment experiment);

    public CvInteractionType getCvInteractionType();

    public void setCvInteractionType(CvInteractionType cvInteractionType);

    //attributes used for mapping BasicObjects - project synchron
    public String getCvInteractionTypeAc();

    public void setCvInteractionTypeAc(String ac);

    public Component getBait();

    public boolean equals(Object o);

    public int hashCode();

    public String toString();

    /**
     * Returns a copy of the current interaction with few exceptions as utlined
     * below.
     * @return the copy of the current interaction. The copy inherits
     * values for Interaction type and host organism of the current interaction. 'x' will
     * be appneded to the short label. The copy also inherits same Proteins with
     * new components and a copy of new Xrefs. Annotations are not inherited.   
     */
    public Interaction copy();
}

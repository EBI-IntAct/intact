/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**
 * Represents an interaction.
 *
 * Interaction is derived from Interactor, therefore a given interaction
 * can participate in new interactions. This allows to build up hierachical
 * assemblies.
 *
 * An Interaction may also have other Interactions as products.
 * This allows to model decomposition of complexes into subcomplexes.
 *
 * @author hhe
 */
public class Interaction extends Interactor {

    ///////////////////////////////////////
    //attributes

    private String interactionTypeAc;
    /**
     * Represents ...
     */
    protected Float kD;

    ///////////////////////////////////////
    // associations

    /**
     *
     */
    public Collection component = new Vector();
    /**
     *
     */
    public Collection released = new Vector();
    /**
     *
     */
    public Collection experiment = new Vector();
    /**
     *
     */
    public CvInteractionType cvInteractionType;


    ///////////////////////////////////////
    //access methods for attributes

    public Float getKD() {
        return kD;
    }

    public void setKD(Float kD) {
        this.kD = kD;
    }

    ///////////////////////////////////////
    // access methods for associations

    public Collection getComponent() {
        return component;
    }

    public void addComponent(Component component) {
        if (!this.component.contains(component)) {
            this.component.add(component);
            component.setInteraction(this);
        }
    }

    public void removeComponent(Component component) {
        boolean removed = this.component.remove(component);
        if (removed) component.setInteraction(null);
    }

    public Collection getReleased() {
        return released;
    }

    public void addReleased(Product product) {
        if (!this.released.contains(product)) {
            this.released.add(product);
            product.setInteraction(this);
        }
    }

    public void removeReleased(Product product) {
        boolean removed = this.released.remove(product);
        if (removed) product.setInteraction(null);
    }

    public Collection getExperiment() {
        return experiment;
    }

    public void addExperiment(Experiment experiment) {
        if (!this.experiment.contains(experiment)) {
            this.experiment.add(experiment);
            experiment.addInteraction(this);
        }
    }

    public void removeExperiment(Experiment experiment) {
        boolean removed = this.experiment.remove(experiment);
        if (removed) experiment.removeInteraction(this);
    }

    public CvInteractionType getCvInteractionType() {
        return cvInteractionType;
    }

    public void setCvInteractionType(CvInteractionType cvInteractionType) {
        this.cvInteractionType = cvInteractionType;
    }


    ///////////////////////////////////////
    // instance methods

    /** Returns the first component marked as bait.
     *  If no such component is found, return null.
     *
     *  @return The first component marked as bait, otherwise null.
     */
    public Component getBait() {
        for (Iterator iterator = component.iterator(); iterator.hasNext();) {
            Component component = (Component) iterator.next();
            CvComponentRole role = component.getCvComponentRole();
            if (null == role) {
                return null;
            }
            if (role.getShortLabel().equals("bait")) {
                return component;
            }
        }
        return null;

    }


    public String toString() {
        String result;
        Iterator i;

        result = "Interaction: " + this.getAc() + "[\n";
        if (null != this.getComponent()) {
            i = this.getComponent().iterator();
            while (i.hasNext()) {
                result = result + ((Component) i.next()).getInteractor();
            }
        }

        return result + "] Interaction\n";
    }

} // end Interaction





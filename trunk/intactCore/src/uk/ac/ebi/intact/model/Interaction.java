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
public class Interaction extends Interactor implements Editable {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    protected String cvInteractionTypeAc;

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

    /**
     * no-arg constructor. Hope to replace with a private one as it should
     * not be used by applications because it will result in objects with invalid
     * states.
     */
    public Interaction() {
        //super call sets creation time data
        super();
    };

    /**
     * Creates a valid Interaction instance. This requires at least the following:
     * <ul>
     * <li> At least one valid Experiment</li>
     * <li>at least two Components</li>
     * <li>an Interaction type (eg covalent binding)</li>
     * <li>a dissociation constant (ie kD), as a <code>Float</code> object</li>
     * <li>a short label to refer to this instance</li>
     * <li>a biological source of the Interaction</li>
     * </ul>
     * <p>
     *  A side-effect of this constructor is to
     * set the <code>created</code> and <code>updated</code> fields of the instance
     * to the current time.
     * @param experiments A Collection of Experiments which observed this Interaction (non-empty)
     * @param components A Collection of Interaction components (eg Proteins), at least two
     * @param type  The type of Interaction observed (non-null)
     * @param dissConst The kD constant, as a <code>Float</code> object
     * @param shortLabel The short label to refer to this instance (non-null)
     * @param source The biological source that the Interaction was observed in (non-null)
     * @exception NullPointerException thrown if any of the paraneters are null
     * @exception IllegalArgumentException thrown if either of the experiment or component Collections
     * are empty, or if there are less than two components specified
     */
    public Interaction(Collection experiments, Collection components,
                       CvInteractionType type, Float dissConst, String shortLabel,
                       BioSource source, Institution owner) {

        //super call sets up a valid AnnotatedObject (Q: is Interactor more tightly defined?)
        super(shortLabel, owner);
        if(experiments == null) throw new NullPointerException("Cannot create an Interaction without an Experiment!");
        if(components == null) throw new NullPointerException("Cannot create an Interaction without any Components!");
        if(type == null) throw new NullPointerException("Cannot create an Interaction without its type (eg covalent binding)!");
        if(dissConst == null) throw new NullPointerException("Cannot create an Interaction without a (Float) dissociation constant (kD)!");
        if(source == null) throw new NullPointerException("Cannot create an Interaction without a biological source!");

        if(experiments.isEmpty()) throw new IllegalArgumentException("must have at least one Experiment to create an Interaction");
        if((components.isEmpty()) ||
           (components.size() < 2)) throw new IllegalArgumentException("must have at least two Components to create an Interaction");
        //Q: what is the difference between activeInstance and components?
        //Q: should the biosource in fact be the same as the experiment's? (doesn't
        //make sense otherwise..) If so then we can get it from any of the experiments
        //as presumably they are all the same (otherwise the biosurce of the interaction
        //would be different in some cases to that of the experiment!!)
        //Q: does it make sense to create Interactions (and Proteins) without
        //any Xrefs?

    }


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

    public void setComponent(Collection someComponent) {
        this.component = someComponent;
    }
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

    public void setReleased(Collection someReleased) {
        this.released = someReleased;
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

    public void setExperiment(Collection someExperiment) {
        this.experiment = someExperiment;
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

    //attributes used for mapping BasicObjects - project synchron
    public String getCvInteractionTypeAc() {
        return this.cvInteractionTypeAc;
    }
    public void setCvInteractionTypeAc(String ac) {
        this.cvInteractionTypeAc = ac;
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





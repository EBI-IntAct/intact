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
 * @version $Id$
 */
public class InteractionImpl extends InteractorImpl implements Editable, Interaction {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    private String cvInteractionTypeAc;

    /**
     * TODO Represents ...
     */
    private Float kD;

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     */
    private Collection components = new ArrayList();

    /**
     * TODO comments
     */
    private Collection released = new ArrayList();

    /**
     * TODO comments
     */
    private Collection experiments = new ArrayList();

    /**
     * TODO comments
     */
    private CvInteractionType cvInteractionType;

   /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    private InteractionImpl() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid Interaction instance. This requires at least the following:
     * <ul>
     * <li> At least one valid Experiment</li>
     * <li>at least two Components</li>
     * <li>an Interaction type (eg covalent binding)</li>
     * <li>a dissociation constant (ie kD), as a <code>Float</code> object</li>
     * <li>a short label to refer to this instance</li>
     * </ul>
     * <p>
     *  A side-effect of this constructor is to
     * set the <code>created</code> and <code>updated</code> fields of the instance
     * to the current time. NOTE: the BioSource value is required for this class as
     * it is not set via Interactor - this will be taken from the (first) Experiment
     * in the Collection parameter. It is tehrefore assumed that the Experiment will
     * be a valid one.
     * @param experiments A Collection of Experiments which observed this Interaction (non-empty)
     * NB The BioSource for this Interaction will be taken from the first element of this Collection.
     * @param components A Collection of Interaction components (eg Proteins). This cannot be null
     * but may be empty to allow creation of an Interaction for later population with Components
     * @param type  The type of Interaction observed - may be null if initially unkown
     * @param shortLabel The short label to refer to this instance (non-null)
     * @exception NullPointerException thrown if any of the specified paraneters are
     * null OR the Experiment does not contain a BioSource.
     * @exception IllegalArgumentException thrown if either of the experiments or components Collections
     * are empty, or if there are less than two components specified
     */
    public InteractionImpl(Collection experiments, Collection components,
                       CvInteractionType type, String shortLabel,
                       Institution owner) {

        //super call sets up a valid AnnotatedObject (Q: is Interactor more tightly defined?)
        super(shortLabel, owner);
        if(experiments == null) throw new NullPointerException("Cannot create an Interaction without an Experiment!");
        if(components == null) throw new NullPointerException("Cannot create an Interaction without any Components!");

        if((experiments.isEmpty()) ||
           (!(experiments.iterator().next() instanceof Experiment)))
            throw new IllegalArgumentException("must have at least one VALID Experiment to create an Interaction");

        //OK by now to do the assignments....
        this.experiments = experiments;
        this.components = components;
        this.cvInteractionType = type;
        //must also set a valid BioSource from the Experiments - assumed that
        //all Experiments observing this Interaction have the same BioSource
        //NB CL: I'm not sure myself if this is actually TRUE!!
        Experiment exp = (Experiment)experiments.iterator().next();
        BioSource source = exp.getBioSource();
        if(source == null) throw new NullPointerException("Can't build valid Interaction - Experiment has no BioSource");
        setBioSource(source);


        //if((components.isEmpty()) ||
         //  (components.size() < 2)) throw new IllegalArgumentException("must have at least two Components to create an Interaction");

        //TODO  Q: does it make sense to create Interactions (and Proteins) without any Xrefs?

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

    public void setComponents(Collection someComponent) {
        this.components = someComponent;
    }
    public Collection getComponents() {
        return components;
    }

    public void addComponent(Component component) {
        if (!this.components.contains(component)) {
            this.components.add(component);
            component.setInteraction(this);
        }
    }

    public void removeComponent(Component component) {
        boolean removed = this.components.remove(component);
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

    public void setExperiments(Collection someExperiment) {
        this.experiments = someExperiment;
    }
    public Collection getExperiments() {
        return experiments;
    }

    public void addExperiment(Experiment experiment) {
        if (!this.experiments.contains(experiment)) {
            this.experiments.add(experiment);
            experiment.addInteraction(this);
        }
    }

    public void removeExperiment(Experiment experiment) {
        boolean removed = this.experiments.remove(experiment);
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

    /** Returns the first components marked as bait.
     *  If no such components is found, return null.
     *
     *  @return The first components marked as bait, otherwise null.
     */
    public Component getBait() {
        for (Iterator iterator = components.iterator(); iterator.hasNext();) {
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

    /**
     * Equality for Interactions is currently based on equality for
     * <code>Interactors</code>, CvInteractionType, kD and Components.
     * @see uk.ac.ebi.intact.model.InteractorImpl
     * @see uk.ac.ebi.intact.model.Component
     * @see uk.ac.ebi.intact.model.CvInteractionType
     * @param o The object to check
     * @return true if the parameter equals this object, false otherwise
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Interaction)) return false;
        if (!super.equals(o)) return false;

        final Interaction interaction = (Interaction) o;

        if(cvInteractionType != null) {
            if (!cvInteractionType.equals(interaction.getCvInteractionType())) return false;
        }
        else {
            if (interaction.getCvInteractionType() != null) return false;
        }

        if(kD != null) {
            if (!kD.equals(interaction.getKD())) return false;
        }
        else {
            if (interaction.getKD() != null) return false;
        }

        return CollectionUtils.isEqualCollection(getComponents(), interaction.getComponents());
    }


    public int hashCode() {
        int code = super.hashCode();

        if(cvInteractionType != null) code = 29 * code + cvInteractionType.hashCode();
        if (kD != null) code = 29 * code + kD.hashCode();
//        for (Iterator iterator = components.iterator(); iterator.hasNext();) {
//            Component components = (Component) iterator.next();
//            code = 29 * code + components.hashCode();
//        }

        return code;
    }


    public String toString() {
        String result;
        Iterator i;

        result = "Interaction: " + this.getAc() + " Label: "
                + this.getShortLabel() + " [" + NEW_LINE;
        if (null != this.getComponents()) {
            i = this.getComponents().iterator();
            while (i.hasNext()) {
                result = result + ((Component) i.next()).getInteractor();
            }
        }

        return result + "] Interaction" + NEW_LINE;
    }

} // end Interaction





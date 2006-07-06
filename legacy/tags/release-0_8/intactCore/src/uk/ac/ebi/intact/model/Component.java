/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The specific instance of an interactor which
 * participates in an interaction.
 *
 * The same interactor may participate more than once,
 * for example to describe different roles of the Interactors.
 * However, simple multimers should be expressed by
 * the relativeQuantity attribute.
 *
 * @author hhe
 * @version $Id$
 */
public class Component extends BasicObjectImpl {

    ///////////////////////////////////////
    //attributes

    // Synchron
    // TODO: should be move out of the model.
    private String interactorAc;
    private String interactionAc;
    private String cvComponentRoleAc;
    private String expressedInAc;


    /**
     * Represents the relative quantitity of the interactor
     * participating in the interaction. Default is one.
     * To describe for example a homodimer, an interaction might have
     * only one substrate, but the relative quantity would be 2.
     */
    private float stoichiometry = 1;

    /**
     * TODO Represents ...
     */
    private BioSource expressedIn;

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     */
    private Interactor interactor;

    /**
     * TODO comments
     */
    private Interaction interaction;

    /**
     * The domain a Substrate binds by.
     */
    private Collection bindingDomains = new ArrayList();

    /**
     * TODO comments
     */
    private CvComponentRole cvComponentRole;

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    private Component() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid Component instance. To be valid, a Component must have at least:
     * <ul>
     * <li>An onwer (Institution)</li>
     * <li>a biological source that the interaaction was expressed in</li>
     * <li>an Interaction that this instance is a Component of</li>
     * <li>an Interactor which defines the entity (eg Protein) which takes part in the
     * Interaction and is therefore the 'core' of this Component</li>
     * <li>the biological role that this Component plays in the Interaction (eg bait/prey etc)</li>
     * </ul>
     * <p>
     * A side-effect of this constructor is to
     * set the <code>created</code> and <code>updated</code> fields of the instance
     * to the current time.
     * @param owner The Institution owner of this Component (non-null)
     * @param interaction The Interaction this Component is a part of (non-null)
     * @param interactor The 'wrapped active entity' (eg a Protein) that this Component represents
     * in the Interaction (non-null)
     * @param role The biological/experimental role played by this Component in the Interaction
     * experiment (eg bait/prey). This is a controlled vocabulary term (non-null)
     * @exception NullPointerException thrown if any of the parameters are not specified.
     */
    public Component(Institution owner, Interaction interaction,
                     Interactor interactor, CvComponentRole role) {

        //super call sets creation time data
        super (owner);
        if(interaction == null) throw new NullPointerException("valid Component must have an Interaction set!");
        if(interactor == null) throw new NullPointerException("valid Component must have an Interactor (eg Protein) set!");
        if(role == null) throw new NullPointerException("valid Component must have a role set (ie a CvComponentRole)!");

//        this.interaction = interaction;
//        this.interactor = interactor;

        setInteraction( interaction );
        setInteractor( interactor );

        this.cvComponentRole = role;
    }


    ///////////////////////////////////////
    //access methods for attributes

    public float getStoichiometry() {
        return stoichiometry;
    }
    public void setStoichiometry(float stoichiometry) {
        this.stoichiometry = stoichiometry;
    }
    public BioSource getExpressedIn() {
        return expressedIn;
    }
    public void setExpressedIn(BioSource expressedIn) {
        this.expressedIn = expressedIn;
    }

    ///////////////////////////////////////
    // access methods for associations

    public Interactor getInteractor() {
        return interactor;
    }

    /**
     * TODO document that non obvious method
     * @param interactor
     */
    public void setInteractor(Interactor interactor) {
        if (this.interactor != interactor) {
            if (this.interactor != null) this.interactor.removeActiveInstance(this);
            this.interactor = interactor;
            if (interactor != null) interactor.addActiveInstance(this);
        }
    }

    public Interaction getInteraction() {
        return interaction;
    }

    // TODO document that non obvious method
    public void setInteraction(Interaction interaction) {
        if (this.interaction != interaction) {
            if (this.interaction != null) this.interaction.removeComponent(this);
            this.interaction = interaction;
            if (interaction != null) interaction.addComponent(this);
        }
    }

    public void setBindingDomains(Collection someBindingDomain) {
        this.bindingDomains = someBindingDomain;
    }
    public Collection getBindingDomains() {
        return bindingDomains;
    }
    public void addBindingDomain(Feature feature) {
        if (! this.bindingDomains.contains(feature)) {
            this.bindingDomains.add(feature);
            feature.setComponent(this);
        }
    }
    public void removeBindingDomain(Feature feature) {
        boolean removed = this.bindingDomains.remove(feature);
        if (removed) feature.setComponent(null);
    }

    public CvComponentRole getCvComponentRole() {
        return cvComponentRole;
    }
    public void setCvComponentRole(CvComponentRole cvComponentRole) {
        this.cvComponentRole = cvComponentRole;
    }

    // instance methods

    /**
     * Equality for Components is currently based on <b>object identity</b>
     * (i.e. the references point to the same objects) for
     * Interactors, Interactions and CvComponentRoles.
     * @param o The object to check
     * @return true if the parameter equals this object, false otherwise
     */
    public boolean equals(Object o){
        /* TODO: Take features into account when they are implemented. */
        if (this == o) return true;
        if (!(o instanceof Component)) return false;
        // don't call super because that's a BasicObject !

        final Component component = (Component) o;

        // Compare if the component links the same objects.
        // This comparision can be based on reference equality,
        // so "==" can be used instead of "equals".
        return this.interactor == component.interactor &&
               this.interaction == component.interaction &&
               this.cvComponentRole == component.cvComponentRole;
    }

    /** This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     * @return  hash code of the object.
     */
    public int hashCode(){
        /* TODO: Take features into account when they are implemented. */
        int code = 29;

        //need these checks because we still have a no-arg
        //constructor at the moment.....
        if(interactor != null) code = code * 29 + interactor.hashCode();
        if(interaction != null) code = code * 29 +  interaction.hashCode();
        if(cvComponentRole != null) code = code * 29 +  cvComponentRole.hashCode();

        return code;
    }


    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String getInteractorAc(){
        return this.interactorAc;
    }
    public void setInteractorAc(String ac){
        this.interactorAc = ac;
    }

    public String getInteractionAc(){
        return this.interactionAc;
    }
    public void setInteractionAc(String ac){
        this.interactionAc = ac;
    }

    public String getCvComponentRoleAc(){
        return this.cvComponentRoleAc;
    }
    public void setCvComponentRoleAc(String ac){
        this.cvComponentRoleAc = ac;
    }

    public String getExpressedInAc(){
        return this.expressedInAc;
    }
    public void setExpressedInAc(String ac){
        this.expressedInAc = ac;
    }
} // end Component





/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**
 * The specific instance of an interactor which 
 * participates in an interaction.
 * 
 * The same interactor may participate more than once,
 * e.g. to describe different roles of the Interactors. 
 * However, simple multimers should be expressed by
 * the relativeQuantity attribute.
 * 
 * @author hhe
 */
public class Component extends BasicObject {

  ///////////////////////////////////////
  //attributes

 private String interactorAc;
    private String interactionAc;
    private String cvComponentRoleAc;
    private String expressedInAc;
/**
 * Represents the relative quantitity of the interactor
 * participating in the interaction. Default is one. 
 * To describe e.g. a homodimer, an interaction might have
 * only one substrate, but the relative quantity would be 2.
 */
    protected float stoichiometry = 1;

/**
 * Represents ...
 */
    protected BioSource expressedIn;

   ///////////////////////////////////////
   // associations

/**
 * 
 */
    public Interactor interactor;
/**
 * 
 */
    public Interaction interaction;
/**
 * The domain a Substrate binds by.
 */
    public Collection bindingDomain = new Vector();
/**
 * 
 */
    public CvComponentRole cvComponentRole;


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

    public void setInteraction(Interaction interaction) {
        if (this.interaction != interaction) {
            if (this.interaction != null) this.interaction.removeComponent(this);     
            this.interaction = interaction;
            if (interaction != null) interaction.addComponent(this);  
        }
    } 
    public Collection getBindingDomain() {
        return bindingDomain;
    }
    public void addBindingDomain(Feature feature) {
        if (! this.bindingDomain.contains(feature)) {     
            this.bindingDomain.add(feature);  
            feature.setComponent(this);
        }
    }
    public void removeBindingDomain(Feature feature) {
        boolean removed = this.bindingDomain.remove(feature);
        if (removed) feature.setComponent(null);
    }
    public CvComponentRole getCvComponentRole() {
        return cvComponentRole;
    }

    public void setCvComponentRole(CvComponentRole cvComponentRole) {
        this.cvComponentRole = cvComponentRole;
    } 

} // end Component





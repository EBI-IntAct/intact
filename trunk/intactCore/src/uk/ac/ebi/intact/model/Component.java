/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.util.Utilities;

import java.util.*;

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
 * To describe for example a homodimer, an interaction might have
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

    public void setBindingDomain(Collection someBindingDomain) {
        this.bindingDomain = someBindingDomain;
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

    // instance methods

    /** Returns true if the "important" attributes are equal.
     * Criteria are:
     * - identity of the interactor and interaction
     * - the role
     *
     * More than one component may link the same (interactor, interaction)
     * pair, but they must have different roles.
     *
     */
    public boolean equals(Object obj){
       /* TODO: Take features into account when they are implemented. */
        return (super.equals(obj) &&
                // Compare if the component links the same objects.
                // This comparision can be based on reference equality,
                // so "==" can be used instaed of "equals".
                (this.interactor == ((Component) obj).interactor) &&
                (this.interaction == ((Component) obj).interaction) &&
                (this.cvComponentRole == ((Component) obj).cvComponentRole)
                );
    }

    /** This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     * @return  hash code of the object.
     */
    public int hashCode(){
        /* TODO: Take features into account when they are implemented. */

        int code = super.hashCode();

        if (null != interactor) code += interactor.hashCode();
        if (null != interaction) code += interaction.hashCode();
        if (null != cvComponentRole) code += cvComponentRole.hashCode();

        return code;
    }


    //attributes used for mapping BasicObjects - project synchron
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





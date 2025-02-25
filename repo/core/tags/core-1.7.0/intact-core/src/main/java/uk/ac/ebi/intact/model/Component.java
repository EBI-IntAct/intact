/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The specific instance of an interactor which participates in an interaction.
 * <p/>
 * The same interactor may participate more than once, for example to describe different roles of the Interactors.
 * However, simple multimers should be expressed by the relativeQuantity attribute.
 *
 * @author hhe
 * @version $Id: Component.java 8310 2007-04-30 15:50:46Z skerrien $
 */
@Entity
@Table( name = "ia_component" )
public class Component extends AnnotatedObjectImpl<ComponentXref, ComponentAlias> {

    private static final Log log = LogFactory.getLog( Component.class );

    public static final float STOICHIOMETRY_NOT_DEFINED = 0;

    public static final String NON_APPLICABLE = "N/A";

    ///////////////////////////////////////
    //attributes

    private String interactorAc;
    private String interactionAc;
    private String expressedInAc;


    /**
     * Represents the relative quantitity of the interactor participating in the interaction. Default is one. To
     * describe for example a homodimer, an interaction might have only one substrate, but the relative quantity would
     * be 2.
     */
    private float stoichiometry = STOICHIOMETRY_NOT_DEFINED;

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
    private Collection<Feature> bindingDomains = new ArrayList<Feature>();

    private CvComponentRole componentRole;

    /**
     * Experimental role of that component (eg. bait, prey, ...).
     */
    private CvExperimentalRole experimentalRole;

    /**
     * Biological role of this component (eg. enzyme, target...).
     */
    private CvBiologicalRole biologicalRole;

    /**
     * Participant identification method that can override the one defined in the experiment.
     * If not specified, the experiment's is to be considered.
     */
    private CvIdentification participantIdentification;

    /**
     * Participant identifications method that can override the one defined in the experiment.
     * If not specified, the experiment's is to be considered.
     */
    private Collection<CvIdentification> participantDetectionMethods;

    /**
     * Experimental preparations for this component. The allowed terms can be found in this URL http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI&termId=MI%3A0346&termName=experimental%20preparation
     */
    private Collection<CvExperimentalPreparation> experimentalPreparations;

    ///////////////////////
    // Constructor

    /**
     * Necessary for hibernate, yet set to private as it should not be used for any other purpose.
     */
    public Component() {
        //super call sets creation time data
        super();
    }

    public Component( Institution owner, Interaction interaction, Interactor interactor,
                      CvExperimentalRole experimentRole, CvBiologicalRole biologicalRole ) {

        this( owner, NON_APPLICABLE, interaction, interactor, experimentRole, biologicalRole );
    }

    /**
     * Creates a valid Component instance. To be valid, a Component must have at least: <ul> <li>An onwer
     * (Institution)</li> <li>a biological source that the interaaction was expressed in</li> <li>an Interaction that
     * this instance is a Component of</li> <li>an Interactor which defines the entity (eg Protein) which takes part in
     * the Interaction and is therefore the 'core' of this Component</li> <li>the biological experimentalRole that this Component
     * plays in the Interaction (eg bait/prey etc)</li> </ul>
     * <p/>
     * A side-effect of this constructor is to set the <code>created</code> and <code>updated</code> fields of the
     * instance to the current time.
     *
     * @param owner            The Institution owner of this Component (non-null)
     * @param shortLabel       Label for this component
     * @param interaction      The Interaction this Component is a part of (non-null)
     * @param interactor       The 'wrapped active entity' (eg a Protein) that this Component represents in the Interaction
     *                         (non-null)
     * @param experimentalRole The experimental role played by this Component in the Interaction experiment (eg
     *                         bait/prey). This is a controlled vocabulary term (non-null)
     * @param biologicalRole   The biological role played by this Component in the Interaction experiment (eg
     *                         enzyme/target). This is a controlled vocabulary term (non-null)
     *
     * @throws NullPointerException thrown if any of the parameters are not specified.
     */
    public Component( Institution owner, String shortLabel, Interaction interaction, Interactor interactor,
                      CvExperimentalRole experimentalRole, CvBiologicalRole biologicalRole ) {
        //super call sets creation time data
        super( shortLabel, owner );

        this.shortLabel = NON_APPLICABLE;

        if ( interaction == null ) {
            throw new NullPointerException( "valid Component must have an Interaction set!" );
        }
        if ( interactor == null ) {
            throw new NullPointerException( "valid Component must have an Interactor (eg Protein) set!" );
        }
        if ( experimentalRole == null ) {
            throw new NullPointerException( "valid Component must have a non null experimentalRole." );
        }
        if ( biologicalRole == null ) {
            throw new NullPointerException( "valid Component must have a non null biologicalRole." );
        }

        this.interaction = interaction;
        this.interactor = interactor;

        this.experimentalRole = experimentalRole;
        this.biologicalRole = biologicalRole;
    }

    ///////////////////////////////////////
    // getters and setters

    /**
     * Getter for property 'biologicalRole'.
     *
     * @return Value for property 'biologicalRole'.
     */
    @ManyToOne
    @JoinColumn( name = "biologicalrole_ac" )
    public CvBiologicalRole getCvBiologicalRole() {
        return biologicalRole;
    }

    /**
     * Setter for property 'biologicalRole'.
     *
     * @param biologicalRole Value to set for property 'biologicalRole'.
     */
    public void setCvBiologicalRole( CvBiologicalRole biologicalRole ) {
        this.biologicalRole = biologicalRole;
    }

    /**
     * Getter for property 'experimentalRole'.
     *
     * @return Value for property 'experimentalRole'.
     */
    @ManyToOne
    @JoinColumn( name = "experimentalrole_ac" )
    public CvExperimentalRole getCvExperimentalRole() {
        return experimentalRole;
    }

    /**
     * Setter for property 'experimentalRole'.
     *
     * @param experimentalRole Value to set for property 'experimentalRole'.
     */
    public void setCvExperimentalRole( CvExperimentalRole experimentalRole ) {
        this.experimentalRole = experimentalRole;
    }

    /**
     * Getter for property 'particiantIdentification'.
     *
     * @return Value for property 'particiantIdentification'.
     */
    @Deprecated
    @Transient
    public CvIdentification getParticipantIdentification() {
        if (participantDetectionMethods == null) {
            return null;
        }

        if (participantDetectionMethods.isEmpty()) {
            return null;
        }

        return participantDetectionMethods.iterator().next();
    }

    /**
     * Setter for property 'particiantIdentification'.
     *
     * @param particiantIdentification Value to set for property 'particiantIdentification'.
     */
    @Deprecated
    public void setParticipantIdentification( CvIdentification particiantIdentification ) {
        getParticipantDetectionMethods().add(particiantIdentification);
    }

    @ManyToMany 
    @JoinTable(
            name = "ia_component2part_detect",
            joinColumns = {@JoinColumn( name = "component_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "cvobject_ac" )}
    )
    public Collection<CvIdentification> getParticipantDetectionMethods() {
        if (participantDetectionMethods == null) {
            participantDetectionMethods = new ArrayList<CvIdentification>();
        }
        return participantDetectionMethods;
    }

    public void setParticipantDetectionMethods(Collection<CvIdentification> participantDetectionMethods) {
        this.participantDetectionMethods = participantDetectionMethods;
    }

    /**
     * Answers the question: "is the stoichiometry of the component defined ?".
     *
     * @return true if the stoichiometry is defined, false otherwise.
     */
    public boolean hasStoichiometry() {
        return ( stoichiometry != STOICHIOMETRY_NOT_DEFINED );
    }

    /**
     * Getter for property 'stoichiometry'.
     *
     * @return Value for property 'stoichiometry'.
     */
    public float getStoichiometry() {
        return stoichiometry;
    }

    /**
     * Setter for property 'stoichiometry'.
     *
     * @param stoichiometry Value to set for property 'stoichiometry'.
     */
    public void setStoichiometry( float stoichiometry ) {
        this.stoichiometry = stoichiometry;
    }

    /**
     * Getter for property 'expressedIn'.
     *
     * @return Value for property 'expressedIn'.
     */
    @ManyToOne

    @JoinColumn( name = "expressedin_ac" )
    public BioSource getExpressedIn() {
        return expressedIn;
    }

    /**
     * Setter for property 'expressedIn'.
     *
     * @param expressedIn Value to set for property 'expressedIn'.
     */
    public void setExpressedIn( BioSource expressedIn ) {
        this.expressedIn = expressedIn;
    }

    ///////////////////////////////////////
    // access methods for associations

    /**
     * Getter for property 'interactor'.
     *
     * @return Value for property 'interactor'.
     */
    @ManyToOne( targetEntity = InteractorImpl.class )
    @JoinColumn( name = "interactor_ac" )
    public Interactor getInteractor() {
        return interactor;
    }

    /**
     * @param interactor
     */
    public void setInteractor( Interactor interactor ) {
        this.interactor = interactor;
    }

    /**
     * Getter for property 'interaction'.
     *
     * @return Value for property 'interaction'.
     */
    @ManyToOne( targetEntity = InteractionImpl.class )
    @JoinColumn( name = "interaction_ac" )
    public Interaction getInteraction() {
        return interaction;
    }

    /**
     * Setter for property 'interaction'.
     *
     * @param interaction Value to set for property 'interaction'.
     */
    public void setInteraction( Interaction interaction ) {
        this.interaction = interaction;
    }

    /**
     * Setter for property 'bindingDomains'.
     *
     * @param someBindingDomain Value to set for property 'bindingDomains'.
     */
    public void setBindingDomains( Collection<Feature> someBindingDomain ) {
        if ( someBindingDomain == null ) {
            throw new IllegalArgumentException( "features cannot be null." );
        }
        this.bindingDomains = someBindingDomain;
    }

    /**
     * Getter for property 'bindingDomains'.
     *
     * @return Value for property 'bindingDomains'.
     */
    @OneToMany( mappedBy = "component", cascade = {CascadeType.PERSIST, CascadeType.REMOVE} )
    public Collection<Feature> getBindingDomains() {
        return bindingDomains;
    }

    public void addBindingDomain( Feature feature ) {
        if ( !this.bindingDomains.contains( feature ) ) {
            this.bindingDomains.add( feature );
            feature.setComponent( this );
        }
    }

    public void removeBindingDomain( Feature feature ) {
        boolean removed = this.bindingDomains.remove( feature );
        if ( removed ) {
            feature.setComponent( null );
        }
    }

    /**
     * Getter for property 'cvComponentRole'.
     *
     * @return Value for property 'cvComponentRole'.
     */
    @Deprecated
    @Transient
    public CvComponentRole getCvComponentRole() {
        return componentRole;
    }

    /**
     * Setter for property 'cvComponentRole'.
     *
     * @param cvComponentRole Value to set for property 'cvComponentRole'.
     */
    @Deprecated
    public void setCvComponentRole( CvComponentRole cvComponentRole ) {
        componentRole = cvComponentRole;
    }

    /**
     * {@inheritDoc}
     */
    @ManyToMany( cascade = {CascadeType.PERSIST} )

    @JoinTable(
            name = "ia_component2annot",
            joinColumns = {@JoinColumn( name = "component_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "annotation_ac" )}
    )
    @Override
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }


    /**
     * {@inheritDoc}
     */
    @OneToMany( mappedBy = "parent" )
    @Cascade( value = org.hibernate.annotations.CascadeType.ALL )
    @Override
    public Collection<ComponentXref> getXrefs() {
        return super.getXrefs();
    }

    /**
     * {@inheritDoc}
     */
    @OneToMany( mappedBy = "parent" )
    @Cascade( value = org.hibernate.annotations.CascadeType.ALL )
    @Override
    public Collection<ComponentAlias> getAliases() {
        return super.getAliases();
    }

    // instance methods


    /**
     * Getter for property 'interactorAc'.
     *
     * @return Value for property 'interactorAc'.
     */ //attributes used for mapping BasicObjects
    @Column( name = "interactor_ac", insertable = false, updatable = false )
    public String getInteractorAc() {
        return this.interactorAc;
    }

    /**
     * Setter for property 'interactorAc'.
     *
     * @param ac Value to set for property 'interactorAc'.
     */
    public void setInteractorAc( String ac ) {
        this.interactorAc = ac;
    }

    /**
     * Getter for property 'interactionAc'.
     *
     * @return Value for property 'interactionAc'.
     */
    @Column( name = "interaction_ac", insertable = false, updatable = false )
    public String getInteractionAc() {
        return this.interactionAc;
    }

    /**
     * Setter for property 'interactionAc'.
     *
     * @param ac Value to set for property 'interactionAc'.
     */
    public void setInteractionAc( String ac ) {
        this.interactionAc = ac;
    }

    /**
     * Getter for property 'expressedInAc'.
     *
     * @return Value for property 'expressedInAc'.
     */
    @Column( name = "expressedin_ac", insertable = false, updatable = false )
    public String getExpressedInAc() {
        return this.expressedInAc;
    }

    /**
     * Setter for property 'expressedInAc'.
     *
     * @param ac Value to set for property 'expressedInAc'.
     */
    public void setExpressedInAc( String ac ) {
        this.expressedInAc = ac;
    }

    @ManyToMany (cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "ia_component2exp_preps",
            joinColumns = {@JoinColumn( name = "component_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "cvobject_ac" )}
    )
    public Collection<CvExperimentalPreparation> getExperimentalPreparations() {
        if (experimentalPreparations == null) {
            experimentalPreparations = new ArrayList<CvExperimentalPreparation>();
        }
        return experimentalPreparations;
    }

    public void setExperimentalPreparations(Collection<CvExperimentalPreparation> experimentalPreparations) {
        this.experimentalPreparations = experimentalPreparations;
    }

    /**
     * This method is specifically for the clone method of InteractionImpl class. The present setInteraction method
     * changes the argument passed, thus causing changes to the source of the clone.
     *
     * @param interaction the interaction to set. This simply replaces the existing interaction.
     */
    @Transient
    protected void setInteractionForClone( Interaction interaction ) {
        this.interaction = interaction;
    }

    /**
     * See the comments for {@link #setInteractionForClone(Interaction)} method.
     *
     * @param interactor
     */
    @Transient
    protected void setInteractorForClone( Interactor interactor ) {
        this.interactor = interactor;
    }

    /**
     * Equality for Components is currently based on <b>object identity</b> (i.e. the references point to the same
     * objects) for Interactors, Interactions and CvComponentRoles.
     *
     * @param o The object to check
     *
     * @return true if the parameter equals this object, false otherwise
     */
    @Override
    public boolean equals( Object o ) {
        /* TODO: Take features into account when they are implemented. */
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Component ) ) {
            return false;
        }
        // don't call super because that's a BasicObject !

        final Component component = ( Component ) o;

        // Compare if the component links the same objects.
        // This comparision can be based on reference equality,
        // so "==" can be used instead of "equals".
        return this.interactor == component.getInteractor() &&
               this.interaction == component.getInteraction() &&
               this.experimentalRole == component.experimentalRole &&
               this.biologicalRole == component.biologicalRole;
    }

    /**
     * This class overwrites equals. To ensure proper functioning of HashTable, hashCode must be overwritten, too.
     *
     * @return hash code of the object.
     */
    @Override
    public int hashCode() {
        /* TODO: Take features into account when they are implemented. */
        int code = 29;

        //need these checks because we still have a no-arg
        //constructor at the moment.....

        if ( interactor != null ) {
            code = code * 29 + interactor.hashCode();
        }
        if ( interaction != null ) {
            code = code * 29 + interaction.hashCode();
        }
        if ( experimentalRole != null ) {
            code = code * 29 + experimentalRole.hashCode();
        }
        if ( biologicalRole != null ) {
            code = code * 29 + biologicalRole.hashCode();
        }

        return code;
    }

    /**
     * Returns a cloned version of the current Component.
     *
     * @return a cloned version of the current Component. References to interactor and interaction are set to null.
     *         Features are deep cloned.
     *
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Component copy = ( Component ) super.clone();

        this.shortLabel = NON_APPLICABLE;

        // Reset interactor and interaction.
        copy.interaction = null;
        copy.interactor = null;
        copy.interactionAc = null;
        copy.interactorAc = null;

        // Make deep copies of Features.
        copy.bindingDomains = new ArrayList<Feature>( bindingDomains.size() );
        for ( Feature feature : bindingDomains ) {
            Feature copyFeature = ( Feature ) feature.clone();
            // Set the copy component as the component for feature copy.
            copyFeature.setComponentForClone( copy );
            // Add the cloned feature to the binding domains.
            copy.bindingDomains.add( copyFeature );
        }
        return copy;
    }
}
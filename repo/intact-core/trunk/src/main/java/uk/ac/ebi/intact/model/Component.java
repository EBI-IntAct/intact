/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
 * @version $Id$
 */
@Entity
@Table(name = "ia_component")
public class Component extends AnnotatedObjectImpl<ComponentXref,ComponentAlias> {

    private static final Log log = LogFactory.getLog(Component.class);

    public static final float STOICHIOMETRY_NOT_DEFINED = 0;

    ///////////////////////////////////////
    //attributes

    private String interactorAc;
    private String interactionAc;
    private String cvComponentRoleAc;
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

    /**
     * TODO comments
     */
    private CvComponentRole cvComponentRole;

    public Component() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid Component instance. To be valid, a Component must have at least: <ul> <li>An onwer
     * (Institution)</li> <li>a biological source that the interaaction was expressed in</li> <li>an Interaction that
     * this instance is a Component of</li> <li>an Interactor which defines the entity (eg Protein) which takes part in
     * the Interaction and is therefore the 'core' of this Component</li> <li>the biological role that this Component
     * plays in the Interaction (eg bait/prey etc)</li> </ul>
     * <p/>
     * A side-effect of this constructor is to set the <code>created</code> and <code>updated</code> fields of the
     * instance to the current time.
     *
     * @param owner       The Institution owner of this Component (non-null)
     * @param interaction The Interaction this Component is a part of (non-null)
     * @param interactor  The 'wrapped active entity' (eg a Protein) that this Component represents in the Interaction
     *                    (non-null)
     * @param role        The biological/experimental role played by this Component in the Interaction experiment (eg
     *                    bait/prey). This is a controlled vocabulary term (non-null)
     *
     * @throws NullPointerException thrown if any of the parameters are not specified.
     */
    public Component( Institution owner, Interaction interaction,
                      Interactor interactor, CvComponentRole role ) {

        this( owner, interactor.getShortLabel(), interaction, interactor, role );
    }

    /**
     * Creates a valid Component instance. To be valid, a Component must have at least: <ul> <li>An onwer
     * (Institution)</li> <li>a biological source that the interaaction was expressed in</li> <li>an Interaction that
     * this instance is a Component of</li> <li>an Interactor which defines the entity (eg Protein) which takes part in
     * the Interaction and is therefore the 'core' of this Component</li> <li>the biological role that this Component
     * plays in the Interaction (eg bait/prey etc)</li> </ul>
     * <p/>
     * A side-effect of this constructor is to set the <code>created</code> and <code>updated</code> fields of the
     * instance to the current time.
     *
     * @param owner       The Institution owner of this Component (non-null)
     * @param shortLabel  Label for this component
     * @param interaction The Interaction this Component is a part of (non-null)
     * @param interactor  The 'wrapped active entity' (eg a Protein) that this Component represents in the Interaction
     *                    (non-null)
     * @param role        The biological/experimental role played by this Component in the Interaction experiment (eg
     *                    bait/prey). This is a controlled vocabulary term (non-null)
     *
     * @throws NullPointerException thrown if any of the parameters are not specified.
     */
    public Component( Institution owner, String shortLabel, Interaction interaction,
                      Interactor interactor, CvComponentRole role ) {

        //super call sets creation time data
        super( shortLabel, owner );
        if ( interaction == null ) {
            throw new NullPointerException( "valid Component must have an Interaction set!" );
        }
        if ( interactor == null ) {
            throw new NullPointerException( "valid Component must have an Interactor (eg Protein) set!" );
        }
        if ( role == null ) {
            throw new NullPointerException( "valid Component must have a role set (ie a CvComponentRole)!" );
        }

        this.interaction = interaction;
        this.interactor = interactor;

        this.cvComponentRole = role;
    }

    ///////////////////////////////////////
    //access methods for attributes

    /**
     * Answers the question: "is the stoichiometry of the component defined ?".
     * @return true if the stoichiometry is defined, false otherwise.
     */
    public boolean hasStoichiometry() {
        return ( stoichiometry != STOICHIOMETRY_NOT_DEFINED );
    }

    public float getStoichiometry() {
        return stoichiometry;
    }

    public void setStoichiometry( float stoichiometry ) {
        this.stoichiometry = stoichiometry;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expressedin_ac")
    public BioSource getExpressedIn() {
        return expressedIn;
    }

    public void setExpressedIn( BioSource expressedIn ) {
        this.expressedIn = expressedIn;
    }

    ///////////////////////////////////////
    // access methods for associations

    @ManyToOne(targetEntity = InteractorImpl.class)
    @JoinColumn(name = "interactor_ac")
    public Interactor getInteractor() {
        return interactor;
    }

    /**
     * @param interactor
     */
    public void setInteractor( Interactor interactor ) {
        this.interactor = interactor;
    }

    @ManyToOne(targetEntity = InteractionImpl.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "interaction_ac")
    public Interaction getInteraction() {
        return interaction;
    }

    public void setInteraction( Interaction interaction ) {
        this.interaction = interaction;
    }

    public void setBindingDomains( Collection<Feature> someBindingDomain ) {
        if ( someBindingDomain == null ) {
            throw new IllegalArgumentException( "features cannot be null." );
        }
        this.bindingDomains = someBindingDomain;
    }

    @OneToMany(mappedBy = "component")
    public Collection<Feature> getBindingDomains() {
        return bindingDomains;
    }

    public void addBindingDomain( Feature feature ) {
        if ( ! this.bindingDomains.contains( feature ) ) {
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role")
    public CvComponentRole getCvComponentRole() {
        return cvComponentRole;
    }

    public void setCvComponentRole( CvComponentRole cvComponentRole ) {
        this.cvComponentRole = cvComponentRole;
    }

    @ManyToMany
    @JoinTable(
        name="ia_component2annot",
        joinColumns={@JoinColumn(name="component_ac")},
        inverseJoinColumns={@JoinColumn(name="annotation_ac")}
    )
    @Override
    public Collection<Annotation> getAnnotations()
    {
        return super.getAnnotations();
    }


    @OneToMany (mappedBy = "parent", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @Override
    public Collection<ComponentXref> getXrefs() {
        return super.getXrefs();
    }

    @OneToMany (mappedBy = "parent", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @Override
    public Collection<ComponentAlias> getAliases() {
        return super.getAliases();
    }

    // instance methods

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

        final Component component = (Component) o;

        // Compare if the component links the same objects.
        // This comparision can be based on reference equality,
        // so "==" can be used instead of "equals".
        return this.interactor == component.getInteractor() &&
               this.interaction == component.getInteraction() &&
               this.cvComponentRole == component.cvComponentRole;
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
        if ( cvComponentRole != null ) {
            code = code * 29 + cvComponentRole.hashCode();
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
        Component copy = (Component) super.clone();

        // Reset interactor and interaction.
        copy.interaction = null;
        copy.interactor = null;

        // Make deep copies of Features.
        copy.bindingDomains = new ArrayList<Feature>( bindingDomains.size() );
        for ( Feature feature : bindingDomains ) {
            Feature copyFeature = (Feature) feature.clone();
            // Set the copy component as the component for feature copy.
            copyFeature.setComponentForClone( copy );
            // Add the cloned feature to the binding domains.
            copy.bindingDomains.add( copyFeature );
        }
        return copy;
    }

    //attributes used for mapping BasicObjects
    @Column(name = "interactor_ac", insertable = false, updatable = false)
    public String getInteractorAc() {
        return this.interactorAc;
    }

    public void setInteractorAc( String ac ) {
        this.interactorAc = ac;
    }

    @Column(name = "interaction_ac", insertable = false, updatable = false)
    public String getInteractionAc() {
        return this.interactionAc;
    }

    public void setInteractionAc( String ac ) {
        this.interactionAc = ac;
    }

    @Column(name = "role", insertable = false, updatable = false)
    public String getCvComponentRoleAc() {
        return this.cvComponentRoleAc;
    }

    public void setCvComponentRoleAc( String ac ) {
        this.cvComponentRoleAc = ac;
    }

    @Column(name = "expressedin_ac", insertable = false, updatable = false)
    public String getExpressedInAc() {
        return this.expressedInAc;
    }

    public void setExpressedInAc( String ac ) {
        this.expressedInAc = ac;
    }

    /**
     * This method is specifically for the clone method of InteractionImpl class. The present setInteraction method
     * changes the argument passed, thus causing changes to the source of the clone.
     *
     * @param interaction the interaction to set. This simply replaces the existing interaction.
     */
    protected void setInteractionForClone( Interaction interaction ) {
        this.interaction = interaction;
    }

    /**
     * See the comments for {@link #setInteractionForClone(Interaction)} method.
     *
     * @param interactor
     */
    protected void setInteractorForClone( Interactor interactor ) {
        this.interactor = interactor;
    }
} // end Component





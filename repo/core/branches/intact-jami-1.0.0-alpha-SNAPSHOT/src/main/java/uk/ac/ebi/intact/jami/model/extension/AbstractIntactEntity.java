package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.listener.ParticipantInteractorChangeListener;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.Entity;
import psidev.psi.mi.jami.model.impl.DefaultStoichiometry;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract class for intact entities
 *
 * Note; all entities are in the same column because they can be referenced in many different places
 * We distinguish entities from participant with interaction property in participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@javax.persistence.Entity
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@Table(name = "ia_component")
@DiscriminatorColumn(name = "category", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractIntactEntity<F extends Feature> extends AbstractIntactPrimaryObject implements Entity<F> {

    private Interactor interactor;
    private CvTerm biologicalRole;
    private Collection<Xref> xrefs;
    private Collection<Annotation> annotations;
    private Collection<Alias> aliases;
    private Stoichiometry stoichiometry;
    private Collection<CausalRelationship> causalRelationships;
    private Collection<F> features;
    private ParticipantInteractorChangeListener changeListener;

    private String shortName;

    protected AbstractIntactEntity(){
        super();
    }

    public AbstractIntactEntity(Interactor interactor){
        super();
        if (interactor == null){
            throw new IllegalArgumentException("The interactor cannot be null.");
        }
        this.interactor = interactor;
    }

    public AbstractIntactEntity(Interactor interactor, CvTerm bioRole){
        super();
        if (interactor == null){
            throw new IllegalArgumentException("The interactor cannot be null.");
        }
        this.interactor = interactor;
        this.biologicalRole = bioRole;
    }

    public AbstractIntactEntity(Interactor interactor, Stoichiometry stoichiometry){
        this(interactor);
        this.stoichiometry = stoichiometry;
    }

    public AbstractIntactEntity(Interactor interactor, CvTerm bioRole, Stoichiometry stoichiometry){
        this(interactor, bioRole);
        this.stoichiometry = stoichiometry;
    }

    /**
     *
     * @param shortName
     * @deprecated only for backward compatibility with intact core
     */
    @Deprecated
    public void setShortLabel(String shortName) {
        this.shortName = shortName;
    }

    @ManyToOne( targetEntity = IntactInteractor.class, optional = false)
    @JoinColumn( name = "interactor_ac", referencedColumnName = "ac")
    @Target(IntactInteractor.class)
    @NotNull
    public Interactor getInteractor() {
        return this.interactor;
    }

    public void setInteractor(Interactor interactor) {
        if (interactor == null){
            throw new IllegalArgumentException("The interactor cannot be null.");
        }
        Interactor oldInteractor = this.interactor;
        this.interactor = interactor;
        if (this.changeListener != null){
            this.changeListener.onInteractorUpdate(this, oldInteractor);
        }
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "biologicalrole_ac", referencedColumnName = "ac")
    @Target(IntactCvTerm.class)
    public CvTerm getBiologicalRole() {
        if (this.biologicalRole == null){
            this.biologicalRole = IntactUtils.createMIBiologicalRole(Participant.UNSPECIFIED_ROLE, Participant.UNSPECIFIED_ROLE_MI);
        }
        return this.biologicalRole;
    }

    public void setBiologicalRole(CvTerm bioRole) {
        biologicalRole = bioRole;
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = IntactCausalRelationship.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinColumn(name="source_ac", referencedColumnName="ac")
    @Target(IntactCausalRelationship.class)
    public Collection<CausalRelationship> getCausalRelationships() {
        if (this.causalRelationships == null){
            initialiseCausalRelationships();
        }
        return this.causalRelationships;
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = EntityXref.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(EntityXref.class)
    public Collection<Xref> getXrefs() {
        if (xrefs == null){
            initialiseXrefs();
        }
        return this.xrefs;
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = EntityAnnotation.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Target(EntityAnnotation.class)
    public Collection<Annotation> getAnnotations() {
        if (annotations == null){
            initialiseAnnotations();
        }
        return this.annotations;
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = EntityAlias.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(EntityAlias.class)
    public Collection<Alias> getAliases() {
        if (aliases == null){
            initialiseAliases();
        }
        return this.aliases;
    }

    @Embedded
    @Target(IntactStoichiometry.class)
    public Stoichiometry getStoichiometry() {
        return this.stoichiometry;
    }

    public void setStoichiometry(Integer stoichiometry) {
        if (stoichiometry == null){
            this.stoichiometry = null;
        }
        else {
            this.stoichiometry = new DefaultStoichiometry(stoichiometry, stoichiometry);
        }
    }

    public void setStoichiometry(Stoichiometry stoichiometry) {
        this.stoichiometry = stoichiometry;
    }

    @Transient
    public Collection<F> getFeatures() {
        if (features == null){
            initialiseFeatures();
        }
        return this.features;
    }

    @Transient
    public ParticipantInteractorChangeListener getChangeListener() {
        return this.changeListener;
    }

    public void setChangeListener(ParticipantInteractorChangeListener listener) {
        this.changeListener = listener;
    }

    public boolean addFeature(F feature) {

        if (feature == null){
            return false;
        }

        if (getFeatures().add(feature)){
            feature.setParticipant(this);
            return true;
        }
        return false;
    }

    public boolean removeFeature(F feature) {

        if (feature == null){
            return false;
        }

        if (getFeatures().remove(feature)){
            feature.setParticipant(null);
            return true;
        }
        return false;
    }

    public boolean addAllFeatures(Collection<? extends F> features) {
        if (features == null){
            return false;
        }

        boolean added = false;
        for (F feature : features){
            if (addFeature(feature)){
                added = true;
            }
        }
        return added;
    }

    public boolean removeAllFeatures(Collection<? extends F> features) {
        if (features == null){
            return false;
        }

        boolean added = false;
        for (F feature : features){
            if (removeFeature(feature)){
                added = true;
            }
        }
        return added;
    }

    @Override
    public String toString() {
        return interactor.toString() + " ( " + biologicalRole.toString() + ")" + (stoichiometry != null ? ", stoichiometry: " + stoichiometry.toString() : "");
    }

    @Transient
    public boolean areXrefsInitialized(){
        return Hibernate.isInitialized(getXrefs());
    }

    @Transient
    public boolean areAliasesInitialized(){
        return Hibernate.isInitialized(getAliases());
    }

    @Transient
    public boolean areAnnotationsInitialized(){
        return Hibernate.isInitialized(getAnnotations());
    }

    @Transient
    public boolean areFeaturesInitialized(){
        return Hibernate.isInitialized(getFeatures());
    }

    @Transient
    public boolean areCausalRelationshipsInitialized(){
        return Hibernate.isInitialized(getCausalRelationships());
    }

    @Column(name = "shortlabel", nullable = false)
    @Size( min = 1, max = IntactUtils.MAX_SHORT_LABEL_LEN )
    @NotNull
    @Deprecated
    /**
     * @deprecated only for backward compatibility with intact core.
     */
    public String getShortLabel() {
        if (this.shortName == null){
            this.shortName = "N/A";
        }
        return shortName;
    }

    protected void initialiseXrefs() {
        this.xrefs = new ArrayList<Xref>();
    }

    protected void initialiseAnnotations() {
        this.annotations = new ArrayList<Annotation>();
    }

    protected void initialiseAliases(){
        this.aliases = new ArrayList<Alias>();
    }

    protected void initialiseFeatures(){
        this.features = new ArrayList<F>();
    }

    protected void initialiseCausalRelationships(){
        this.causalRelationships = new ArrayList<CausalRelationship>();
    }

    protected void setAliases(Collection<Alias> aliases) {
        this.aliases = aliases;
    }

    protected void setAnnotations(Collection<Annotation> annotations) {
        this.annotations = annotations;
    }

    protected void setCausalRelationships(Collection<CausalRelationship> causalRelationships) {
        this.causalRelationships = causalRelationships;
    }

    protected void setXrefs(Collection<Xref> xrefs) {
        this.xrefs = xrefs;
    }

    protected void setFeatures(Collection<F> features) {
        this.features = features;
    }
}

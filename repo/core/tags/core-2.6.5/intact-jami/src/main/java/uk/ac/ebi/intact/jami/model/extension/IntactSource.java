package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

/**
 * Intact implementation of a source. It replaces Institution from intact core
 *
 * NOTE: dbUrl and dbPostalAddress are private methods as they are deprecated and only there for backward compatibility with intact-core.
 * Only getUrl and getPostalAddress should be used. These methods are not persistent, if a URL/postal address is attached to an institution, it
 * should always be stored in the annotations of the institution.
 * NOTE: getAnnotations is not persistent. For HQL queries, the method getDbAnnotations should be used because is annotated with hibernate annotations.
 * However, getDbAnnotations should not be used directly to add/remove annotations because it could mess up with the state of the object. Only the synchronizers
 * can use it this way before persistence. The access type of DbAnnotations is private as it does not have to be used by the synchronizers neither.
 * NOTE: getIdentifiers and getXrefs are not persistent methods annotated with hibernate annotations. All the xrefs present in identifiers
 * and xrefs are persisted in the same table for backward compatibility with intact-core. So the persistent xrefs are available with the getDbXrefs method.
 * For HQL queries, the method getDbXrefs should be used because is annotated with hibernate annotations.
 * However, getDbXrefs should not be used directly to add/remove xrefs because it could mess up with the state of the object. Only the synchronizers
 * can use it this way before persistence.
 * NOTE: The source ac is automatically added as an identifier in getIdentifiers but is not persisted in getDbXrefs.
 * The getIdentifiers.remove will thrown an UnsupportedOperationException if someone tries to remove the AC identifier from the list of identifiers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */
@Entity
@Table(name = "ia_institution")
@Cacheable
public class IntactSource extends AbstractIntactCvTerm implements Source {
    private Annotation url;
    private Annotation postalAddress;
    private Publication bibRef;

    private String persistentUrl;
    private String persistentPostalAddress;

    protected IntactSource(){
        super();
    }

    public IntactSource(String shortName) {
        super(shortName);
    }

    public IntactSource(String shortName, Xref ontologyId) {
        super(shortName, ontologyId);
    }

    public IntactSource(String shortName, String fullName, Xref ontologyId) {
        super(shortName, fullName, ontologyId);
    }

    public IntactSource(String shortName, String url, String address, Publication bibRef) {
        super(shortName);
        setUrl(url);
        setPostalAddress(address);
        this.bibRef = bibRef;
    }

    public IntactSource(String shortName, Xref ontologyId, String url, String address, Publication bibRef) {
        super(shortName, ontologyId);
        setUrl(url);
        setPostalAddress(address);
        this.bibRef = bibRef;
    }

    public IntactSource(String shortName, String fullName, Xref ontologyId, String url, String address, Publication bibRef) {
        super(shortName, fullName, ontologyId);
        setUrl(url);
        setPostalAddress(address);
        this.bibRef = bibRef;
    }

    public IntactSource(String shortName, String miId) {
        super(shortName, miId);
    }

    public IntactSource(String shortName, String fullName, String miId) {
        super(shortName, fullName, miId);
    }

    public IntactSource(String shortName, String miId, String url, String address, Publication bibRef) {
        super(shortName, miId);
        setUrl(url);
        setPostalAddress(address);
        this.bibRef = bibRef;
    }

    public IntactSource(String shortName, String fullName, String miId, String url, String address, Publication bibRef) {
        super(shortName, fullName, miId);
        setUrl(url);
        setPostalAddress(address);
        this.bibRef = bibRef;
    }

    @PrePersist
    @PreUpdate
    /**
     * This method is only for backward compatibility with intact-core. Once intact-core is removed (or editor updated) and the database columns removed,
     * this method can be safely deleted
     */
    public void prePersist() {
        if (this.url != null){
            this.persistentUrl = this.url.getValue();
        }
        else{
            this.persistentUrl = null;
        }

        if (this.postalAddress != null){
            this.persistentPostalAddress = this.postalAddress.getValue();
        }
        else{
            this.persistentPostalAddress = null;
        }
    }

    @Column(name = "shortlabel", nullable = false, unique = true)
    @Size( min = 1, max = IntactUtils.MAX_SHORT_LABEL_LEN )
    @NotNull
    public String getShortName() {
        return super.getShortName();
    }

    public void setShortName(String name) {
        super.setShortName(name);
    }

    @Transient
    public String getUrl() {
        // initialise annotations if not done
        getAnnotations();
        return this.url != null ? this.url.getValue() : null;
    }

    public void setUrl(String url) {
        Collection<Annotation> sourceAnnotationList = getAnnotations();

        // add new url if not null
        if (url != null){
            CvTerm urlTopic = IntactUtils.createMITopic(Annotation.URL, Annotation.URL_MI);
            // first remove old url if not null
            if (this.url != null){
                sourceAnnotationList.remove(this.url);
            }
            this.url = new SourceAnnotation(urlTopic, url);
            sourceAnnotationList.add(this.url);
        }
        // remove all url if the collection is not empty
        else if (!sourceAnnotationList.isEmpty()) {
            AnnotationUtils.removeAllAnnotationsWithTopic(sourceAnnotationList, Annotation.URL_MI, Annotation.URL);
            this.url = null;
        }
    }

    @Transient
    public String getPostalAddress() {
        // initialise annotations if not done
        getAnnotations();
        return this.postalAddress != null ? this.postalAddress.getValue() : null;
    }

    public void setPostalAddress(String address) {
        Collection<Annotation> sourceAnnotationList = getAnnotations();

        // add new url if not null
        if (address != null){
            CvTerm addressTopic = IntactUtils.createMITopic(Annotation.POSTAL_ADDRESS, null);
            // first remove old url if not null
            if (this.postalAddress != null){
                sourceAnnotationList.remove(this.postalAddress);
            }
            this.postalAddress = new SourceAnnotation(addressTopic, address);
            sourceAnnotationList.add(this.postalAddress);
        }
        // remove all url if the collection is not empty
        else if (!sourceAnnotationList.isEmpty()) {
            AnnotationUtils.removeAllAnnotationsWithTopic(sourceAnnotationList, null, Annotation.POSTAL_ADDRESS);
            this.postalAddress = null;
        }
    }

    @ManyToOne( targetEntity = IntactPublication.class)
    @JoinColumn( name = "publication_ac", referencedColumnName = "ac")
    @Target(IntactPublication.class)
    public Publication getPublication() {
        return this.bibRef;
    }

    public void setPublication(Publication ref) {
        this.bibRef = ref;
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = SourceAlias.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(SourceAlias.class)
    public Collection<Alias> getSynonyms() {
        return super.getSynonyms();
    }

    @Transient
    public boolean areXrefsInitialized(){
        return Hibernate.isInitialized(getDbXrefs());
    }

    @Transient
    public boolean areSynonymsInitialized(){
        return Hibernate.isInitialized(getSynonyms());
    }

    @Transient
    public boolean areAnnotationsInitialized(){
        return Hibernate.isInitialized(getDbAnnotations());
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = SourceXref.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(SourceXref.class)
    /**
     * This method give direct access to the persistent collection of xrefs (identifiers and xrefs all together) for this object.
     * WARNING: It should not be used to add/remove objects as it may mess up with the state of the object (only used this way by the synchronizers).
     */
    public Collection<Xref> getDbXrefs() {
        return super.getDbXrefs();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = SourceAnnotation.class)
    @JoinTable(
            name="ia_institution2annot",
            joinColumns = @JoinColumn( name="institution_ac"),
            inverseJoinColumns = @JoinColumn( name="annotation_ac")
    )
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(SourceAnnotation.class)
    /**
     * WARNING: The join table is for backward compatibility with intact-core.
     * When intact-core will be removed, the join table would disappear wnd the relation would become
     * @JoinColumn(name="parent_ac", referencedColumnName="ac")
     *
     * This method give direct access to the persistent collection of annotations for this object.
     * WARNING: It should not be used to add/remove objects as it may mess up with the state of the object (only used this way by the synchronizers).
     *
     */
    @Override
    public Collection<Annotation> getDbAnnotations() {
        return super.getDbAnnotations();
    }

    @Override
    protected void resetFieldsLinkedToAnnotations() {
        this.url = null;
        this.postalAddress = null;
    }

    @Override
    protected void initialiseAnnotations() {
        super.initialiseAnnotationsWith(new SourceAnnotationList(null));

        for (Annotation annot : super.getDbAnnotations()){
            processAddedAnnotationEvent(annot);
        }
    }

    @Override
    protected boolean processAddedAnnotations(Annotation annot) {
        if (url == null && AnnotationUtils.doesAnnotationHaveTopic(annot, Annotation.URL_MI, Annotation.URL)){
            url = annot;
        }
        else if (postalAddress == null && AnnotationUtils.doesAnnotationHaveTopic(annot, null, Annotation.POSTAL_ADDRESS)){
            postalAddress = annot;
        }
        return false;
    }

    private void processAddedAnnotationEvent(Annotation added) {
        if (url == null && AnnotationUtils.doesAnnotationHaveTopic(added, Annotation.URL_MI, Annotation.URL)){
            url = added;
        }
        else if (postalAddress == null && AnnotationUtils.doesAnnotationHaveTopic(added, null, Annotation.POSTAL_ADDRESS)){
            postalAddress = added;
        }
    }

    private void processRemovedAnnotationEvent(Annotation removed) {
        if (url != null && url.equals(removed)){
            url = null;
        }
        else if (postalAddress != null && postalAddress.equals(removed)){
            postalAddress = null;
        }
    }

    @Column(name = "url")
    /**
     * @deprecated use getURL instead
     */
    @Deprecated
    private String getDbURL() {
        return persistentUrl;
    }

    @Column(name = "postaladdress")
    /**
     * @deprecated use getPostalAdress instead
     */
    @Deprecated
    private String getDbPostalAddress() {
        return persistentPostalAddress;
    }

    /**
     *
     * @param persistentURL
     * @deprecated this method is used only for backward compatibility with intact-core. Use setURL instead
     */
    @Deprecated
    private void setDbURL(String persistentURL) {
        this.persistentUrl = persistentURL;
    }

    /**
     *
     * @param persistentPostalAddress
     * @deprecated this method is used only for backward compatibility with intact-core. Use setPostalAddress instead
     */
    @Deprecated
    private void setDbPostalAddress(String persistentPostalAddress) {
        this.persistentPostalAddress = persistentPostalAddress;
    }


    private class SourceAnnotationList extends CvTermAnnotationList {
        public SourceAnnotationList(Collection<Annotation> annots){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Annotation added) {
            super.processAddedObjectEvent(added);
            processAddedAnnotationEvent(added);
        }

        @Override
        protected void processRemovedObjectEvent(Annotation removed) {
            super.processRemovedObjectEvent(removed);
            processRemovedAnnotationEvent(removed);
        }

        @Override
        protected void clearProperties() {
            super.clearProperties();
            url = null;
            postalAddress = null;
        }
    }
}

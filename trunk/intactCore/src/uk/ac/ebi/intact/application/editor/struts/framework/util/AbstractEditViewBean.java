/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework.util;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.DuplicateLabelException;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;

/**
 * This super bean encapsulates behaviour for a common editing session. This
 * class must be extended to provide editor specific behaviour.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractEditViewBean {

    /**
     * The Annotated object to wrap.
     */
    private AnnotatedObject myAnnotObject;

    /**
     * The short label of the current edit object.
     */
    private String myShortLabel;

    /**
     * The full name of the current edit object.
     */
    private String myFullName;

    /**
     * The annotations to display.
     */
    private ArrayList myAnnotations = new ArrayList();

    /**
     * The Xreferences to display.
     */
    private ArrayList myXrefs = new ArrayList();

    /**
     * Holds annotations to add. This collection is cleared once the user
     * commits the transaction.
     */
    private transient Collection myAnnotsToAdd = new ArrayList();

    /**
     * Holds annotations to del. This collection is cleared once the user
     * commits the transaction.
     */
    private transient Collection myAnnotsToDel = new ArrayList();

    /**
     * Holds annotations to update. This collection is cleared once the user
     * commits the transaction.
     */
    private transient Collection myAnnotsToUpdate = new ArrayList();

    /**
     * Holds xrefs to add. This collection is cleared once the user commits
     * the transaction.
     */
    private transient Collection myXrefsToAdd = new ArrayList();

    /**
     * Holds xrefs to del. This collection is cleared once the user commits
     * the transaction.
     */
    private transient Collection myXrefsToDel = new ArrayList();

    /**
     * Holds xrefs to update. This collection is cleared once the user
     * commits the transaction.
     */
    private transient Collection myXrefsToUpdate = new ArrayList();

    /**
     * Set attributes using values from an Annotated Object. A coarse-grained
     * method to avoid multiple method calls.
     * @param annot the <code>AnnotatedObject</code> to set attributes of this
     * class.
     */
    public void setAnnotatedObject(AnnotatedObject annot) {
        myAnnotObject = annot;
        this.setShortLabel(annot.getShortLabel());
        this.setFullName(annot.getFullName());

        // Cache the annotations and xrefs here to save it from loading
        // multiple times with each invocation to getAnnotations()
        // or getXrefs() methods.
        myAnnotations.clear();
        makeCommentBeans(annot.getAnnotation());
        myXrefs.clear();
        makeXrefBeans(annot.getXref());
    }

    /**
     * Returns the Annotated object.
     * @return <code>AnnotatedObject</code> this instace is wrapped around.
     */
    public AnnotatedObject getAnnotatedObject() {
        return myAnnotObject;
    }

    /**
     * Returns accession number.
     * @return the accession number as a <code>String</code> instance.
     */
    public String getAc() {
        return myAnnotObject.getAc();
    }

    /**
     * Sets the short label.
     * @param shortLabel the short label to set.
     */
    public final void setShortLabel(String shortLabel) {
        System.out.println("Setting the short label " + shortLabel);
        myShortLabel = shortLabel;
    }

    /**
     * Returns the short label.
     * @return the short label as a <code>String</code> instance.
     */
    public String getShortLabel() {
        return myShortLabel;
    }

    /**
     * Sets the full name.
     * @param fullName the full name to set for the current edit object.
     */
    public final void setFullName(String fullName) {
        myFullName = fullName;
    }

    /**
     * Return the full name.
     * @return the full name as a <code>String</code> instance.
     */
    public String getFullName() {
        return myFullName;
    }

    /**
     * Returns a collection of <code>CommentBean</code> objects.
     *
     * <pre>
     * post: return != null
     * post: return->forall(obj : Object | obj.oclIsTypeOf(CommentBean))
     * </pre>
     */
    public ArrayList getAnnotations() {
        return myAnnotations;
    }

    /**
     * Returns a <code>CommentBean</code> at given location.
     * @param index the position to return <code>CommentBean</code>.
     * @return <code>CommentBean</code> at <code>index</code>.
     *
     * <pre>
     * pre: index >=0 and index < myAnnotations->size
     * post: return != null
     * post: return = myAnnotations->at(index)
     * </pre>
     */
    public CommentBean getAnnotation(int index) {
        return (CommentBean) myAnnotations.get(index);
    }

    /**
     * Adds an annotation.
     * @param annotation the annotation to add.
     *
     * <pre>
     * post: myAnnotsToAdd = myAnnotsToAdd@pre + 1
     * post: myAnnotations = myAnnotations@pre + 1
     * </pre>
     */
    public void addAnnotation(Annotation annotation) {
        CommentBean cb = new CommentBean(annotation);
        // Annotation to add.
        myAnnotsToAdd.add(cb);
        // Add to the view as well.
        myAnnotations.add(cb);
    }

    /**
     * Removes an annotation
     * @param cb the comment bean to remove.
     *
     * <pre>
     * post: myAnnotsToDel = myAnnotsToDel@pre - 1
     * post: myAnnotations = myAnnotations@pre - 1
     * </pre>
     */
    public void delAnnotation(CommentBean cb) {
        // Add to the container to delete annotations.
        myAnnotsToDel.add(cb);
        // Remove from the view as well.
        myAnnotations.remove(cb);
    }

    /**
     * Adds a Comment bean to update.
     * @param cb a <code>CommentBean</code> object to update.
     *
     * <pre>
     * post: myAnnotsToUpdate = myAnnotsToUpdate@pre + 1
     * post: myAnnotations = myAnnotations@pre
     * </pre>
     */
    public void addAnnotationToUpdate(CommentBean cb) {
        myAnnotsToUpdate.add(cb);
    }

    /**
     * Returns a collection <code>Xref</code> objects.
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(Xref))
     * </pre>
     */
    public ArrayList getXrefs() {
        return myXrefs;
    }

    /**
     * Returns a <code>XreferenceBean</code> at given location.
     * @param index the position to return <code>XreferenceBean</code>.
     * @return <code>XreferenceBean</code> at <code>index</code>.
     *
     * <pre>
     * pre: index >=0 and index < myXrefs->size
     * post: return != null
     * post: return = myXrefs->at(index)
     * </pre>
     */
    public XreferenceBean getXref(int index) {
        return (XreferenceBean) myXrefs.get(index);
    }

    /**
     * Adds an xref.
     * @param xref the xref to add.
     *
     * <pre>
     * post: myXrefsToAdd = myXrefsToAdd@pre + 1
     * post: myXrefs = myXrefs@pre + 1
     * </pre>
     */
    public void addXref(Xref xref) {
        XreferenceBean xb = new XreferenceBean(xref);
        // Xref to add.
        myXrefsToAdd.add(xb);
        // Add to the view as well.
        myXrefs.add(xb);
    }

    /**
     * Removes a xref.
     * @param xb the X'reference bean to remove.
     *
     * <pre>
     * post: myXrefsToDel = myXrefsToDel@pre + 1
     * post: myXrefs = myXrefs@pre - 1
     * </pre>
     */
    public void delXref(XreferenceBean xb) {
        // Add to the container to delete the xref.
        myXrefsToDel.add(xb);
        // Remove from the view as well.
        myXrefs.remove(xb);
    }

    /**
     * Adds a xref bean to update.
     * @param xb an <code>XreferenceBean</code> object to update.
     *
     * <pre>
     * post: myXrefsToUpdate = myXrefsToUpdate@pre + 1
     * post: myXrefs = myXrefs@pre
     * </pre>
     */
    public void addXrefToUpdate(XreferenceBean xb) {
        myXrefsToUpdate.add(xb);
    }

    /**
     * Clears any pending xrefs and annotations stored in the transaction
     * containers.
     * <pre>
     * post: myAnnotsToAdd->isEmpty
     * post: myAnnotsToDel->isEmpty
     * post: myAnnotsToUpdate->isEmpty
     * post: myXrefsToAdd->isEmpty
     * post: myXrefsToDel->isEmpty
     * post: myXrefsToUpdate->isEmpty
     * </pre>
     */
    public void clearTransactions() {
        this.clearTransAnnotations();
        this.clearTransXrefs();
    }

    /**
     * Persists the current state to the persistent system. After this
     * method is called the persistent view is as same as the current view.
     * @param user handler to access the persistent method calls.
     * @exception IntactException for errors in updating the persistent system.
     */
    public void persist(EditUserI user) throws IntactException {
        // Update the cv info data.
        myAnnotObject.setShortLabel(getShortLabel());
        myAnnotObject.setFullName(getFullName());

        // Create annotations and add them to CV object.
        for (Iterator iter = getAnnotationsToAdd().iterator(); iter.hasNext();) {
            Annotation annot = ((CommentBean) iter.next()).getAnnotation();
            user.create(annot);
            myAnnotObject.addAnnotation(annot);
        }
        // Delete annotations and remove them from CV object.
        for (Iterator iter = getAnnotationsToDel().iterator(); iter.hasNext();) {
            Annotation annot = ((CommentBean) iter.next()).getAnnotation();
            user.delete(annot);
            myAnnotObject.removeAnnotation(annot);
        }
        // Update annotations.
        for (Iterator iter = getAnnotationsToUpdate().iterator(); iter.hasNext();) {
            Annotation annot = updateAnnotation(user, (CommentBean) iter.next());
            user.update(annot);
        }

        // Create xrefs and add them to CV object.
        for (Iterator iter = getXrefsToAdd().iterator(); iter.hasNext();) {
            Xref xref = ((XreferenceBean) iter.next()).getXref();
            user.create(xref);
            myAnnotObject.addXref(xref);
        }
        // Delete xrefs and remove them from CV object.
        for (Iterator iter = getXrefsToDel().iterator(); iter.hasNext();) {
            Xref xref = ((XreferenceBean) iter.next()).getXref();
            user.delete(xref);
            myAnnotObject.removeXref(xref);
        }
        // Update xrefs.
        for (Iterator iter = getXrefsToUpdate().iterator(); iter.hasNext();) {
            Xref xref = updateXref(user, ((XreferenceBean) iter.next()));
            user.update(xref);
        }
        // Update the cv object.
        user.update(myAnnotObject);
    }

    /**
     * Returns a string representation of this object. Mainly for debugging.
     */
    public String toString() {
        return "ac: " + getAc() + " short label: " + getShortLabel();
    }

    // Helper Methods

    /**
     * Creates a collection of <code>CommentBean</code> created from given
     * collection of annotations.
     * @param annotations a collection of <code>Annotation</code> objects.
     */
    private void makeCommentBeans(Collection annotations) {
        for (Iterator iter = annotations.iterator(); iter.hasNext();) {
            Annotation annot = (Annotation) iter.next();
            myAnnotations.add(new CommentBean(annot));
        }
    }

    /**
     * Creates a collection of <code>XrefBean</code> created from given
     * collection of xreferences.
     * @param xrefs a collection of <code>Xref</code> objects.
     */
    private void makeXrefBeans(Collection xrefs) {
        for (Iterator iter = xrefs.iterator(); iter.hasNext();) {
            Xref xref = (Xref) iter.next();
            myXrefs.add(new XreferenceBean(xref));
        }
    }

    /**
     * Returns a collection of annotations to add.
     * @return the collection of annotations to add to the current CV object.
     * Could be empty if there are no annotations to add.
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(CommentBean)
     * </pre>
     */
    private Collection getAnnotationsToAdd() {
        // Annotations common to both add and delete.
        Collection common = CollectionUtils.intersection(myAnnotsToAdd, myAnnotsToDel);
        // All the annotations only found in annotations to add collection.
        return CollectionUtils.subtract(myAnnotsToAdd, common);
    }

    /**
     * Returns a collection of annotations to remove.
     * @return the collection of annotations to remove from the current CV object.
     * Could be empty if there are no annotations to delete.
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(CommentBean)
     * </pre>
     */
    private Collection getAnnotationsToDel() {
        // Annotations common to both add and delete.
        Collection common = CollectionUtils.intersection(myAnnotsToAdd, myAnnotsToDel);
        // All the annotations only found in annotations to delete collection.
        return CollectionUtils.subtract(myAnnotsToDel, common);
    }

    /**
     * Returns a collection of annotations to update.
     * @return the collection of annotations to update from the current CV object.
     * Could be empty if there are no annotations to update.
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(CommentBean)
     * </pre>
     */
    private Collection getAnnotationsToUpdate() {
        return myAnnotsToUpdate;
    }

    /**
     * Returns a collection of xrefs to add.
     * @return the collection of xrefs to add to the current CV object.
     * Could be empty if there are no xrefs to add.
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(XreferenceBean))
     * </pre>
     */
    private Collection getXrefsToAdd() {
        // Xrefs common to both add and delete.
        Collection common = CollectionUtils.intersection(myXrefsToAdd, myXrefsToDel);
        // All the xrefs only found in xrefs to add collection.
        return CollectionUtils.subtract(myXrefsToAdd, common);
    }

    /**
     * Returns a collection of xrefs to remove.
     * @return the collection of xrefs to remove from the current CV object.
     * Could be empty if there are no xrefs to delete.
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(XreferenceBean))
     * </pre>
     */
    private Collection getXrefsToDel() {
        // Xrefs common to both add and delete.
        Collection common = CollectionUtils.intersection(myXrefsToAdd, myXrefsToDel);
        // All the xrefs only found in xrefs to delete collection.
        return CollectionUtils.subtract(myXrefsToDel, common);
    }

    /**
     * Returns a collection of xrefs to update.
     * @return the collection of xrefs to update from the current CV object.
     * Could be empty if there are no xrefs to update.
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(XreferenceBean)
     * </pre>
     */
    private Collection getXrefsToUpdate() {
        return myXrefsToUpdate;
    }

    /**
     * Clears annotations stored transaction containers.
     */
    private void clearTransAnnotations() {
        myAnnotsToAdd.clear();
        myAnnotsToDel.clear();
        myAnnotsToUpdate.clear();
    }

    /**
     * Clears xrefs stored in transaction containers.
     */
    private void clearTransXrefs() {
        myXrefsToAdd.clear();
        myXrefsToDel.clear();
        myXrefsToUpdate.clear();
    }

    private Annotation updateAnnotation(EditUserI user, CommentBean cb)
            throws DuplicateLabelException {
        // Update with the new description.
        Annotation annot = cb.getAnnotation();
        annot.setAnnotationText(cb.getDescription());

        // Only update the topic if they differ.
        String topic = cb.getTopic();
        if (!topic.equals(annot.getCvTopic().getShortLabel())) {
            // Get the topic object for the new annotation.
            CvTopic cvtopic = (CvTopic) user.getObjectByLabel(CvTopic.class, topic);
            annot.setCvTopic(cvtopic);
        }
        return annot;
    }

    private Xref updateXref(EditUserI user, XreferenceBean xb)
            throws DuplicateLabelException {
        // The xref object to update
        Xref xref = xb.getXref();

        // Only update the database if it has been changed.
        String database = xb.getDatabase();
        if (!database.equals(xref.getCvDatabase().getShortLabel())) {
            // The database the new xref belong to.
            CvDatabase db = (CvDatabase) user.getObjectByLabel(
                    CvDatabase.class, database);
            xref.setCvDatabase(db);
        }
        xref.setPrimaryId(xb.getPrimaryId());
        xref.setSecondaryId(xb.getSecondaryId());
        xref.setDbRelease(xb.getReleaseNumber());

        String qualifier = xb.getQualifier();
        // Check for null pointer.
        if (xref.getCvXrefQualifier() != null) {
            // Only update the quailier if they differ.
            if (!qualifier.equals(xref.getCvXrefQualifier().getShortLabel())) {
                CvXrefQualifier xqual = (CvXrefQualifier) user.getObjectByLabel(
                        CvXrefQualifier.class, qualifier);
                xref.setCvXrefQualifier(xqual);
            }
        }
        return xref;
    }
}

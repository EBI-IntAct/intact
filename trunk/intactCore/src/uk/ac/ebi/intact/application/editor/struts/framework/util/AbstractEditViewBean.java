/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework.util;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;
import uk.ac.ebi.intact.application.editor.struts.view.EditForm;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.ValidationException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.persistence.SearchException;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.struts.tiles.ComponentContext;

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
     * The annotations to display. Transient as it is only valid for the
     * current display.
     */
    private transient List myAnnotations = new ArrayList();

    /**
     * The Xreferences to display. Transient as it is only valid for the
     * current display.
     */
    private transient List myXrefs = new ArrayList();

    /**
     * Holds annotations to add. This collection is cleared once the user
     * commits the transaction.
     */
    private transient List myAnnotsToAdd = new ArrayList();

    /**
     * Holds annotations to del. This collection is cleared once the user
     * commits the transaction.
     */
    private transient List myAnnotsToDel = new ArrayList();

    /**
     * Holds annotations to update. This collection is cleared once the user
     * commits the transaction.
     */
    private transient List myAnnotsToUpdate = new ArrayList();

    /**
     * Holds xrefs to add. This collection is cleared once the user commits
     * the transaction.
     */
    private transient List myXrefsToAdd = new ArrayList();

    /**
     * Holds xrefs to del. This collection is cleared once the user commits
     * the transaction.
     */
    private transient List myXrefsToDel = new ArrayList();

    /**
     * Holds xrefs to update. This collection is cleared once the user
     * commits the transaction.
     */
    private transient List myXrefsToUpdate = new ArrayList();

    /**
     * The factory to create various menus.
     */
    private transient EditorMenuFactory myMenuFactory;

    /**
     * Construts an instance using an Annotated object.
     * @param annot <code>AnnotatedObject</code> object to construct this
     * instance from.
     */
    public void setAnnotatedObject(AnnotatedObject annot) {
        myAnnotObject = annot;
        setShortLabel(annot.getShortLabel());
        setFullName(annot.getFullName());

        // Clear any left overs from previous transaction.
        clearTransactions();

        // Cache the annotations and xrefs here to save it from loading
        // multiple times with each invocation to getAnnotations()
        // or getXrefs() methods.
        makeCommentBeans(annot.getAnnotation());
        makeXrefBeans(annot.getXref());
    }

    /**
     * Sets the menu factory to create menus.
     * @param factory the factory to create menus.
     */
    public void setMenuFactory(EditorMenuFactory factory) {
        myMenuFactory = factory;
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
    public List getAnnotations() {
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
        // Add to the container to add an Annotation.
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
    public List getXrefs() {
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
     * Populates the given bean with editor specific info. Override this
     * method to provide editor specific behaviour. Currently, this method
     * is empty.
     * @param form the form to fill data.
     */
    public void fillEditorSpecificInfo(DynaBean form) {
        // Empty
    }

    /**
     * Returns the edit menu for annotations.
     * @return the edit menu for annotations
     * @throws SearchException thrown for failures with database access.
     */
    public List getEditAnnotationMenus() throws SearchException {
        return getAnnotationMenus(0);
    }

    /**
     * Returns the add menu for annotations.
     * @return the add menu for annotations
     * @throws SearchException thrown for failures with database access.
     */
    public List getAddAnnotationMenus() throws SearchException {
        return getAnnotationMenus(1);
    }

    /**
     * Returns the edit menu for xrefs.
     * @return the edit menu for xrefs.
     * @throws SearchException thrown for failures with database access.
     */
    public Map getEditXrefMenus() throws SearchException {
        return getXrefMenus(0);
    }

    /**
     * Returns the add menu for xrefs.
     * @return the add menu for xrefs
     * @throws SearchException thrown for failures with database access.
     */
    public Map getAddXrefMenus() throws SearchException {
        return getXrefMenus(1);
    }

    /**
     * Returns the editor specific menus. All the editors must override this
     * method to provide its own implementation.
     * @return the editor specific menus.
     * @throws SearchException thrown for failures with database access.
     */
    public Map getEditorMenus() throws SearchException {
        return null;
    }

    /**
     * Removes the current object from the menu list. This will only succeed
     * if the current edit object is of menu type. For example, this method will
     * succeed for CvTopic as it is considered as a menu type.
     */
    public void removeMenu() {
        myMenuFactory.removeMenu(myAnnotObject.getClass());
    }

    /**
     * Populates given form with annotations
     * @param form the form to poplulate.
     */
    public void populateAnnotations(EditForm form) {
        form.setItems(myAnnotations);
    }

    /**
     * Populates given form with xrefs.
     * @param form the form to poplulate.
     */
    public void populateXrefs(EditForm form) {
        form.setItems(myXrefs);
    }

    /**
     * Sets the layout in given context. This method is currently empty as
     * the layout defaults to cv layout. Override this method to provide editor
     * specific layout.
     * @param context the Tiles context to set the layout.
     */
    public void setLayout(ComponentContext context) {
        // Empty
    }

    /**
     * Validates the data in the view bean.
     * @param user handler to the user to access the DB.
     * @throws ValidationException thrown when this bean contains invalid data.
     * For example, an experiment must contain non null values for organism,
     * interaction and identification. Currently this method is empty as no
     * validations are preformed.
     */
    public void validate(EditUserI user) throws ValidationException {
        // Need to override by the subclass.
    }

    /**
     * Allows access to menu factory.
     * @return the menu factory for sub classes to create menus.
     */
    protected EditorMenuFactory getMenuFactory() {
        return myMenuFactory;
    }

    // Helper Methods

    /**
     * Creates a collection of <code>CommentBean</code> created from given
     * collection of annotations.
     * @param annotations a collection of <code>Annotation</code> objects.
     */
    private void makeCommentBeans(Collection annotations) {
        myAnnotations.clear();
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
        myXrefs.clear();
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
            throws IntactException {
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
            throws IntactException {
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

        CvXrefQualifier xqual = (CvXrefQualifier) user.getObjectByLabel(
                CvXrefQualifier.class, xb.getQualifier());
        xref.setCvXrefQualifier(xqual);

        return xref;
    }

    private List getAnnotationMenus(int mode) throws SearchException {
        return getMenu(EditorMenuFactory.TOPICS,
                myAnnotObject.getShortLabel(), mode);
    }

    private Map getXrefMenus(int mode) throws SearchException {
        Map map = new HashMap();
        String name;
        // The short label to remove from the list.
        String label = myAnnotObject.getShortLabel();

        // The database menu.
        name = EditorMenuFactory.DATABASES;
        map.put(name, getMenu(name, label, mode));

        // The qualifier menu.
        name = EditorMenuFactory.QUALIFIERS;
        map.put(name, getMenu(name, label, mode));
        return map;
    }

    /**
     * Returns a menu for <code>name</code> without <code>label</code>; the menu
     * type is dependent on the <code>mode</code>
     * @param name the name of the menu to get.
     * @param label the label to strip off from the returning menu.
     * @param mode 0 for edit or 1 for add.
     * @return menu for <code>name</code> without <code>label</code> if applicable.
     * @throws SearchException for database search error when constructing the
     * menu.
     */
    private List getMenu(String name, String label, int mode)
            throws SearchException {
        List list;
        if (myMenuFactory.isMenuType(myAnnotObject.getClass())) {
            // Remove my short label to avoid circular reference.
            list = new ArrayList(myMenuFactory.getMenu(name, mode));
            list.remove(label);
        }
        else {
            list = myMenuFactory.getMenu(name, mode);
        }
        return list;
    }
}

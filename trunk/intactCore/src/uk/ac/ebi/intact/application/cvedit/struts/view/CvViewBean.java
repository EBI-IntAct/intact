/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.view;

import uk.ac.ebi.intact.model.*;

import java.util.*;

/**
 * Bean to display an Intact object. This bean is used by edit.jsp to display
 * and capture the data.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CvViewBean {

    /**
     * Static empty container to return to JSPs to display
     * no rows (to stop display tag library throwing an exception).
     */
    private static Collection theirEmptyCollection = Collections.EMPTY_LIST;

    /**
     * Selected topic.
     */
    private String mySelectedTopic;

    /**
     * Accession number.
     */
    private String myAc;

    /**
     * The short label.
     */
    private String myShortLabel;

    /**
     * The annotations to display.
     */
    private Collection myAnnotations = new ArrayList();

    /**
     * The Xreferences to display.
     */
    private Collection myXrefs = new ArrayList();

    /**
     * Holds the transaction (add/delete) for annotations. This set is cleared
     * once the user commits the transaction.
     */
    private Set myAnnotTransactions = new HashSet();

    /**
     * Holds the transaction (add/delete) for xrefs. This set is cleared
     * once the user commits the transaction.
     */
    private Set myXrefTransactions = new HashSet();

    /**
     * Set attributes using values from CvObject. A coarse-grained method to
     * avoid multiple method calls.
     *
     * @param cvobj the <code>CvObject</code> to set attributes of this class.
     */
    public void initialise(CvObject cvobj) {
        setAc(cvobj.getAc());
        setShortLabel(cvobj.getShortLabel());

        // Cache the annotations and xrefs here to save it from loading
        // multiple times with each invocation to getAnnotations()
        // or getXrefs() methods.
        myAnnotations.clear();
        makeCommentBeans(cvobj.getAnnotation());
        myXrefs.clear();
        makeXrefBeans(cvobj.getXref());
    }

    /**
     * Sets the selected topic.
     *
     * @param topic the selected topic.
     */
    public void setTopic(String topic) {
        mySelectedTopic = topic;
    }

    /**
     * Returns the selected topic.
     */
    public String getTopic() {
        return mySelectedTopic;
    }

    /**
     * Sets the accession number.
     *
     * @param ac the accession number; shouldn't be null.
     */
    public void setAc(String ac) {
        myAc = ac;
    }

    /**
     * Returns accession number.
     */
    public String getAc() {
        return myAc;
    }

    /**
     * Sets ther short label.
     *
     * @param shortLabel the short label to set
     */
    public void setShortLabel(String shortLabel) {
        myShortLabel = shortLabel;
    }

    /**
     * Returns the short label.
     */
    public String getShortLabel() {
        return myShortLabel;
    }

    /**
     * Returns a collection of <code>CommentBean</code> objects.
     *
     * <pre>
     * post: return != null
     * post: return->forall(obj : Object | obj.oclIsTypeOf(CommentBean))
     * </pre>
     */
    public Collection getAnnotations() {
        return myAnnotations;
    }

    /**
     * Adds an annotation.
     * @param annotation the annotation to add.
     *
     * <pre>
     * post: myAnnotTransactions = myAnnotTransactions@pre + 1
     * post: myAnnotations = myAnnotations@pre + 1
     * </pre>
     */
    public void addAnnotation(Annotation annotation) {
        CommentBean cb = TransactionalCommentBean.createCommentBeanAdd(annotation);
        myAnnotTransactions.add(cb);
        // Add to the view as well.
        myAnnotations.add(cb);
    }

    /**
     * Removes an annotation for given key.
     * @param key the primary key.
     * @param annotation the annotation to remove.
     *
     * <pre>
     * post: myAnnotTransactions = myAnnotTransactions@pre - 1
     * post: myAnnotations = myAnnotations@pre - 1
     * </pre>
     */
    public void delAnnotation(long key, Annotation annotation) {
        CommentBean cb =
                TransactionalCommentBean.createCommentBeanDel(key, annotation);
        myAnnotTransactions.add(cb);
        // Remove from the view as well.
        myAnnotations.remove(cb);
    }

    /**
     * Finds the annotation for a given key.
     * @param key the key to search for the annotation.
     * @return the <code>Annotation</code> object whose key matches with given
     * <code>key</code>. <code>null</code> is returned if no matching annotation
     * is found.
     */
    public Annotation findAnnotation(long key) {
        for (Iterator iter = myAnnotations.iterator(); iter.hasNext();) {
            CommentBean bean = (CommentBean) iter.next();
            if (bean.getKey() == key) {
                return bean.getAnnotation();
            }
        }
        // Not found the bean.
        return null;
    }

    /**
     * Returns a collection of annotations to add.
     * @return the collection of annotations to add to the current CV object.
     *
     * <pre>
     * post: return->forAll(a | a.getTransState() = TransactionalCommentBean.ADD)
     * </pre>
     */
    public Collection getAnnotationsToAdd() {
        return getTransAnnots(TransactionalCommentBean.ADD);
    }

    /**
     * Returns a collection of annotations to remove.
     * @return the collection of annotations to remove from the current CV object.
     *
     * <pre>
     * post: return->forAll(a | a.getTransState() = TransactionalCommentBean.DEL)
     * </pre>
     */
    public Collection getAnnotationsToDel() {
        return getTransAnnots(TransactionalCommentBean.DEL);
    }

    /**
     * Clears annotations stored in the transaction container.
     *
     * <pre>
     * post: myAnnotTransactions.isEmpty()
     * </pre>
     */
    public void clearTransAnnotations() {
        myAnnotTransactions.clear();
    }

    /**
     * Returns a collection <code>XRefe</code>
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(Xref))
     * </pre>
     */
    public Collection getXrefs() {
        return myXrefs;
    }

    /**
     * Adds an xref.
     * @param xref the xref to add.
     *
     * <pre>
     * post: myXrefTransactions = myXrefTransactions@pre + 1
     * post: myXrefs = myXrefs@pre + 1
     * </pre>
     */
    public void addXref(Xref xref) {
        XreferenceBean xb = TransactionalXrefBean.createXrefBeanAdd(xref);
        myXrefTransactions.add(xb);
        // Add to the view as well.
        myXrefs.add(xb);
    }

    /**
     * Removes a xref for given key.
     * @param key the primary key.
     * @param xref the annotation to remove.
     *
     * <pre>
     * post: myXrefTransactions = myXrefTransactions@pre - 1
     * post: myXrefs = myXrefs@pre - 1
     * </pre>
     */
    public void delXref(long key, Xref xref) {
        XreferenceBean xb =
                TransactionalXrefBean.createXrefBeanDel(key, xref);
        myXrefTransactions.add(xb);
        // Remove from the view as well.
        myXrefs.remove(xb);
    }

    /**
     * Finds the xref for a given key.
     * @param key the key to search for the xref.
     * @return the <code>Xref</code> object whose key matches with given
     * <code>key</code>. <code>null</code> is returned if no matching xref
     * is found.
     */
    public Xref findXref(long key) {
        for (Iterator iter = myXrefs.iterator(); iter.hasNext();) {
            XreferenceBean bean = (XreferenceBean) iter.next();
            if (bean.getKey() == key) {
                return bean.getXref();
            }
        }
        // Not found the bean.
        return null;
    }

    /**
     * Returns a collection of xrefs to add.
     * @return the collection of xrefs to add to the current CV object.
     *
     * <pre>
     * post: return->forAll(a | a.getTransState() = TransactionalXrefBean.ADD)
     * </pre>
     */
    public Collection getXrefsToAdd() {
        return getTransXrefs(TransactionalXrefBean.ADD);
    }

    /**
     * Returns a collection of xrefs to remove.
     * @return the collection of xrefs to remove from the current CV object.
     *
     * <pre>
     * post: return->forAll(a | a.getTransState() = TransactionalXrefBean.DEL)
     * </pre>
     */
    public Collection getXrefsToDel() {
        return getTransXrefs(TransactionalXrefBean.DEL);
    }

    /**
     * Clears xrefs stored in the transaction container.
     *
     * <pre>
     * post: myXrefTransactions.isEmpty()
     * </pre>
     */
    public void clearTransXrefs() {
        myXrefTransactions.clear();
    }

    /**
     * Returns an empty collection. This is to stop display tag library from
     * throwing an exception when there are no rows to display for a high page
     * number (only happens when we have two tables with different rows on
     * a single page).
     *
     * <pre>
     * post: return->isEmpty
     * </pre>
     */
    public Collection getEmptyCollection() {
        return theirEmptyCollection;
    }

    /**
     * Returns a string representation of this object. Mainly for debugging.
     */
    public String toString() {
        return "ac: " + getAc() + " short label: " + getShortLabel();
    }

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
     * Returns transactional comment bean for given transaction state.
     * @param state the transaction state; ADD or DEL
     * @return a collection of <code>Annotations</code> for given state.
     */
    private Collection getTransAnnots(int state) {
        // The collection to return.
        Collection result = new ArrayList();
        // Filter out annotations.
        for (Iterator iter = myAnnotTransactions.iterator(); iter.hasNext(); ) {
            TransactionalCommentBean tcb = (TransactionalCommentBean) iter.next();
            if (tcb.getTransState() == state) {
                result.add(tcb.getAnnotation());
            }
        }
        return result;
    }

    /**
     * Returns transactional xref bean for given transaction state.
     * @param state the transaction state; ADD or DEL
     * @return a collection of <code>Xrefs</code> for given state.
     */
    private Collection getTransXrefs(int state) {
        // The collection to return.
        Collection result = new ArrayList();
        // Filter out annotations.
        for (Iterator iter = myXrefTransactions.iterator(); iter.hasNext(); ) {
            TransactionalXrefBean txb = (TransactionalXrefBean) iter.next();
            if (txb.getTransState() == state) {
                result.add(txb.getXref());
            }
        }
        return result;
    }
}
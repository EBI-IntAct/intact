/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.view;

import org.apache.struts.action.ActionForm;
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
     * no rows (or else display tag library throws an exception).
     */
    private static Collection theirEmptyCollection = new ArrayList();

    /**
     * Reference to the CvObject as we need to this access annotations
     * and xrefs.
     */
    private CvObject myCvObject;

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
     * The annotations.
     */
    private Collection myAnnotations;

    /**
     * The Xreferences.
     */
    private Collection myXrefs;

    /**
     * Set attributes using values from CvObject. A coarse-grained method to
     * avoid multiple method calls.
     *
     * @param cvobj the <code>CvObject</code> to set attributes of this class.
     */
    public void initialise(CvObject cvobj) {
        myCvObject = cvobj;
        setAc(cvobj.getAc());
        setShortLabel(cvobj.getShortLabel());

        // Cache the annotations and xrefs here to save it from loading
        // multiple times with each invocation to getAnnotations()
        // or getXrefs() methods.
        myAnnotations = makeCommentBeans(cvobj.getAnnotation());
        myXrefs = makeXrefBeans(cvobj.getXref());
    }

    /**
     * Returns the reference to the Cv Object.
     */
    public CvObject getCvObject() {
        return myCvObject;
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
     * Returns a collection of <code>CommentBean</code> created from given
     * collection of annotations.
     * @param annotations a collection of <code>Annotation</code> objects.
     * @return a collection of code>CommentBean</code> created from
     * <code>annotations</code>
     */
    private Collection makeCommentBeans(Collection annotations) {
        // Collection of comment beans to return.
        Collection comments = new ArrayList();

        for (Iterator iter = annotations.iterator(); iter.hasNext();) {
            Annotation annot = (Annotation) iter.next();
            comments.add(new CommentBean(annot));
        }
        return comments;
    }

    /**
     * Returns a collection of <code>XrefBean</code> created from given
     * collection of xreferences.
     * @param xrefs a collection of <code>Xref</code> objects.
     * @return a collection of code>XrefBean</code> created from
     * <code>xreferences</code>
     */
    private Collection makeXrefBeans(Collection xrefs) {
        // Collection of xref beans to return.
        Collection xrefbeans = new ArrayList();

        for (Iterator iter = xrefs.iterator(); iter.hasNext();) {
            Xref xref = (Xref) iter.next();
            xrefbeans.add(new XreferenceBean(xref));
        }
        return xrefbeans;
    }

    /**
     * Returns a collection of annotations (comments) for selected CV object.
     */
//    private Collection getXrefs() {
//        // Search the obj2annot table for annotations.
//        return myIntactHelper.search(
//            Xref.class.getName(), "database_ac", myAc);
//    }

}
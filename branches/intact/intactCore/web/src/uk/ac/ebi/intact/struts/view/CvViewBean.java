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
 * Bean to display an Intact object.
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
     * Reference to the Intact Helper instance to access the business layer.
     */
    //private IntactHelper myIntactHelper;

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
     * Constructs an instance of this class with Intact Service object.
     *
     * @param service the Intact Service to accss business model.
     * @exception IntactException for error in accessing IntactHelp object.
     */
//    public CvViewBean(IntactService service) throws IntactException {
//        myIntactHelper = service.getIntactHelper();
//    }

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
        myAnnotations = makeCommentBeans(cvobj.getAnnotation());
        myXrefs = makeXrefBeans(cvobj.getXref());
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

    // Testing method which fills up dummy values for annotations.

/*
    private Collection dummyComments(int number) {
        // The collection to return.
        Collection comments = new ArrayList();

        Annotation annot;
        CvTopic topic;

        for (int i = 0; i < number; i++) {
            annot = new Annotation();
            annot.setAc("EBI-" + (i + 100));
            annot.setAnnotationText("Dummy annotations for topic " + i);
            topic = new CvTopic();
            topic.setAc("EBI-" + (i + 150));
            topic.setShortLabel("Topic" + i);
            annot.setCvTopic(topic);
            comments.add(annot);
        }
        return comments;
    }

    // Testing method which fills up dummy values for annotations.

    private Collection dummyXrefs(int number) {
        // The collection to return.
        Collection xrefs = new ArrayList();

        Xref xref;
        CvDatabase cvdb = new CvDatabase();
        cvdb.setShortLabel("InterPro");
        CvXrefQualifier cvx = new CvXrefQualifier();
        cvx.setShortLabel("Identity");

        for (int i = 0; i < number; i++) {
            xref = new Xref();
            xref.setAc("EBI-" + (i + 200));
            xref.setCvDatabase(cvdb);
            xref.setPrimaryId("PrimaryId" + i);
            xref.setSecondaryId("SecondaryId" + i);
            xref.setDbRelease("Release");
            xref.setCvXrefQualifier(cvx);
            xrefs.add(xref);
        }
        return xrefs;
    }
*/
}
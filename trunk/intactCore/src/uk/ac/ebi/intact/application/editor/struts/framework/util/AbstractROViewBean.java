/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework.util;

import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a common read only view of an edit object. This
 * class must be extended to provide editor specific behaviour.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractROViewBean {

    /**
     * The accession number for this object.
     */
    private String myAc;

    /**
     * The topic of this class.
     */
//    private String myTopic;

    /**
     * The short label for this object.
     */
    private String myShortLabel;

    /**
     * The full name of for this object.
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
     * Set attributes using values from an Annotated Object.
     * @param annobj the <code>AnnotatedObject</code> to set attributes of this
     * class.
     */
    public void setAnnotatedObject(AnnotatedObject annobj) {
        myAc = annobj.getAc();
        myShortLabel = annobj.getShortLabel();
        myFullName = annobj.getFullName();
        myAnnotations.clear();
        makeCommentBeans(annobj.getAnnotation());
        myXrefs.clear();
        makeXrefBeans(annobj.getXref());
    }

    // Only get methods as this class in immutable.

    public String getAc() {
        return myAc;
    }

//    public String getTopic() {
//        return myTopic;
//    }

    public String getShortLabel() {
        return myShortLabel;
    }

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
     * Returns a collection <code>XreferenceBean</code> objects.
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(XreferenceBean))
     * </pre>
     */
    public List getXrefs() {
        return myXrefs;
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
}

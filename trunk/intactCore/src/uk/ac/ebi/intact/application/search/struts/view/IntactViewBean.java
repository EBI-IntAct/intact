/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.view;

import uk.ac.ebi.intact.model.*;

import java.util.*;
import java.io.Serializable;

/**
 * Bean to display an Intact object. This bean is used by results.jsp to display
 * the data.
 *
 * @author Sugath Mudali, modified for basic search by Chris Lewington
 * @version $Id$
 */
public class IntactViewBean implements Serializable {

    /**
     * Static empty container to return to JSPs to display
     * no rows (or else display tag library throws an exception).
     */
    //private static Collection theirEmptyCollection = new ArrayList();


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
    private Collection myAnnotations = new ArrayList();

    /**
     * The Xreferences.
     */
    private Collection myXrefs = new ArrayList();

    /**
     * stores the object as a String for simple display
     * of everything.
     */
    private String data;

    /**
     * Holds the object as XML
     */
    private String asXml;

    /**
     * stores the object being wrapped. Mainly used for
     * alternative display options by other views.
     */
    private Object wrappedObject;

    /**
     * Set attributes using values from AnnotatedObject. A coarse-grained method to
     * avoid multiple method calls.
     *
     * @param obj the intact object to set attributes of this class.
     */
    public void initialise(Object obj) {

        if(obj instanceof Institution) {
            setAc(((Institution)obj).getAc());
        }
        else {
            if(obj instanceof AnnotatedObject) {
                setAc(((AnnotatedObject)obj).getAc());
                setShortLabel(((AnnotatedObject)obj).getShortLabel());
            }
            else {

                //something odd - unknown type!!
                //ignore for now as this should never happen...
            }
        }

        //set display info
        //String tmp = obj.toString();
        String tmp = obj.toString();

        //now replace the \n chars with a break recognised
        //in a web browser
        data = tmp.replaceAll("\n", "<BR>");

        //save the object locally in case it is needed
        wrappedObject = obj;

        // Cache the annotations and xrefs here to save it from loading
        // multiple times with each invocation to getAnnotations()
        // or getXrefs() methods.
        /*myAnnotations.clear();
        makeCommentBeans(obj.getAnnotation());
        myXrefs.clear();
        makeXrefBeans(obj.getXref());*/
    }

    public Object getWrappedObject() {

        return wrappedObject;
    }

    /**
     * This method replaces the object currently held in this bean.
     */
    public void setWrappedObject(Object obj) {
        this.initialise(obj);
    }

    /**
     * This method sets the wrapped object as XML format. NO integrity
     * check is performed!!
     */
    public void setAsXml(String xml) {
        asXml = xml;
    }

    /**
     * @return the wrapped object details in XML format, or null if not converted.
     */
    public String getAsXml() {
       return asXml;
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
     * Returns a collection <code>Xref</code>.
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
        return new ArrayList();
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
            //myAnnotations.add(new CommentBean(annot));
        }
    }

    /**
     * Creates a collection of <code>Xref</code> objects created from given
     * collection of xreferences.
     * @param xrefs a collection of <code>Xref</code> objects.
     */
    private void makeXrefBeans(Collection xrefs) {
        for (Iterator iter = xrefs.iterator(); iter.hasNext();) {
            Xref xref = (Xref) iter.next();
            //myXrefs.add(new XreferenceBean(xref));
        }
    }

    public String getData() {
        return data;
    }
}
/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework;

import org.apache.struts.validator.ValidatorForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionError;
import org.apache.struts.util.MessageResources;
import org.apache.struts.Globals;
import org.apache.commons.collections.CollectionUtils;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The form to edit cv data. This form also is the super class for other
 * editor (e.g., Experiment)
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditorActionForm extends ValidatorForm {

    /**
     * The pattern for a valid short label.
     */
    private static Pattern SHORT_LABEL_PAT =
            Pattern.compile("[a-z0-9\\-:_]+ ?[a-z0-9\\-:_]+$");

    /**
     * The short label.
     */
    private String myShortLabel;

    /**
     * The full name.
     */
    private String myFullName;

    /**
     * The accession number.
     */
    private String myAc;

    /**
     * The list of annotations.
     */
    private List myAnnotations;

    /**
     * The list of cross references.
     */
    private List myXrefs;

    /**
     * The index of the current dispatch (which button was pressed).
     */
    private int myDispatchIndex;

    /**
     * The label of the last dispatch action.
     */
    private String myDispatch;

    /**
     * The page anchor to go when there is an error. Default is none.
     */
    private String myAnchor = "";

    /**
     * The annotation to add.
     */
    private CommentBean myNewAnnotation;

    /**
     * The cross reference to add.
     */
    private XreferenceBean myNewXref;

    // Getter/Setter methods for form attributes.

    public void setShortLabel(String label) {
        myShortLabel = label;
    }

    public String getShortLabel() {
        return myShortLabel;
    }

    public void setFullName(String fullname) {
        myFullName = fullname;
    }

    public String getFullName() {
        return myFullName;
    }

    public void setAc(String ac) {
        myAc = ac;
    }

    public String getAc() {
        return myAc;
    }

    public void setAnnotations(List annotations) {
        if (myAnnotations != null) {
            // No need to create a new proteins if both collections contain same.
            // This might be the case for page refresh for other than proteins.
            if (CollectionUtils.isEqualCollection(myAnnotations, annotations)) {
                return;
            }
        }
        myAnnotations = new ArrayList(annotations);
    }

    public List getAnnotations() {
        return myAnnotations;
    }

    public void setAnnotCmd(int index, String value) {
        setDispatch(index, value);
    }

    public CommentBean getSelectedAnnotation() {
        return (CommentBean) myAnnotations.get(myDispatchIndex);
    }

    public void setXrefs(List xrefs) {
        if (myXrefs != null) {
            // See the comment for setAnnotations(List)
            if (CollectionUtils.isEqualCollection(myXrefs, xrefs)) {
                return;
            }
        }
        myXrefs = new ArrayList(xrefs);
    }

    public List getXrefs() {
        return myXrefs;
    }

    public void setXrefCmd(int index, String value) {
        myDispatchIndex = index;
        setDispatch(value);
    }

    public XreferenceBean getSelectedXref() {
        return (XreferenceBean) myXrefs.get(myDispatchIndex);
    }

    public void setNewAnnotation(CommentBean cb) {
        myNewAnnotation = cb;
    }

    public CommentBean getNewAnnotation() {
        return myNewAnnotation;
    }

    public void setNewXref(XreferenceBean xb) {
        myNewXref = xb;
    }

    public XreferenceBean getNewXref() {
        return myNewXref;
    }

    public void setDispatch(String dispatch) {
        myDispatch = dispatch;
    }

    public String getDispatch() {
        return myDispatch;
    }

    public String getAnchor() {
        return myAnchor;
    }

    public void setAnchor(String anchor) {
        myAnchor = anchor;
    }

    public void resetAnchor() {
        setAnchor("");
    }

    protected void setDispatch(int index, String value) {
        myDispatchIndex = index;
        setDispatch(value);
    }

    public int getDispatchIndex() {
        return myDispatchIndex;
    }

    /**
     * Validate the properties that have been set from the HTTP request.
     *
     * @param mapping the mapping used to select this instance
     * @param request the servlet request we are processing
     * @return <tt>ActionErrors</tt> object that contains validation errors. If
     * no errors are found, <tt>null</tt> or an empty <tt>ActionErrors</tt>
     * object is returned.
     */
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = null;

        // Validate the short label
        Matcher matcher = SHORT_LABEL_PAT.matcher(getShortLabel());
        if (!matcher.matches()) {
            // Invalid syntax for a short label.
            errors = new ActionErrors();
            errors.add("shortLabel",
                    new ActionError("error.shortlabel.mask", getShortLabel()));
            return errors;
        }
        // The dispatch parameter to find out which button was pressed.
        String dispatch = getDispatch();

        if (dispatch != null) {
            // Message resources to access button labels.
            MessageResources msgres =
                    ((MessageResources) request.getAttribute(Globals.MESSAGES_KEY));

            // Adding an annotation?
            if (dispatch.equals(msgres.getMessage("annotations.button.add"))) {
                // The bean to extract the values.
                if (getNewAnnotation().getTopic().equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
                    // Set the anchor for the page to scroll.
                    errors = new ActionErrors();
                    errors.add("annotation", new ActionError("error.annotation.topic"));
                    return errors;
                }
            }
            // Adding an Xref?
            if (dispatch.equals(msgres.getMessage("xrefs.button.add"))) {
                // The bean to extract the values.
                XreferenceBean xb = getNewXref();
                if (xb.getDatabase().equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
                    errors = new ActionErrors();
                    errors.add("xref.db", new ActionError("error.xref.database"));
                    return errors;
                }
                // Primary id is required.
                if (AbstractEditorAction.isPropertyEmpty(xb.getPrimaryId())) {
                    errors = new ActionErrors();
                    errors.add("xref.pid", new ActionError("error.xref.pid"));
                }
            }
        }
        return errors;
    }
}

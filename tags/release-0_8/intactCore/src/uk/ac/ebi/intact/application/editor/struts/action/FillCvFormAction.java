/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Fills the form with values for ac, short label and full name.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FillCvFormAction extends AbstractEditorAction {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

//        System.out.println("At the beginning of fillCVForm");
//        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
//            String para = (String) e.nextElement();
//            System.out.println("parameters: " + para + " - " + request.getParameter(para));
//        }
        // Reset the dispatch button as its value is retained for the session.
        dynaform.set("dispatch", null);

        // The view of the current object we are editing at the moment.
        AbstractEditViewBean view = getIntactUser(request).getView();

        // Reset fields for adding a new annotation/xref.
        resetAddAnnotation(dynaform);
        resetAddXref(dynaform);

        // Fill the form with values from the bean.
        dynaform.set("ac", view.getAcLink());
        dynaform.set("shortLabel", view.getShortLabel());
        dynaform.set("fullName", view.getFullName());

        // Set annotations.
        List annotations = view.getAnnotations();
        dynaform.set("annotations", annotations.toArray(new CommentBean[0]));
        String[] cmds = new String[annotations.size()];
        dynaform.set("annotCmd", cmds);

        // Set xrefs.
        List xrefs = view.getXrefs();
        dynaform.set("xrefs", xrefs.toArray(new XreferenceBean[0]));
        dynaform.set("xrefCmd", new String[xrefs.size()]);

        // Straight to the editor.
        return mapping.findForward(SUCCESS);
    }

    /**
     * Resets the annotation add form.
     * @param dynaform the form to get/set the bean.
     */
    private void resetAddAnnotation(DynaActionForm dynaform) {
        CommentBean cb = (CommentBean) dynaform.get("annotation");
        if (cb != null) {
            // Resuse the existing bean.
            cb.reset();
        }
        else {
            cb = new CommentBean();
        }
        dynaform.set("annotation", cb);
    }

    /**
     * Resets the xref add form.
     * @param dynaform the form to get/set the bean.
     */
    private void resetAddXref(DynaActionForm dynaform) {
        XreferenceBean xb = (XreferenceBean) dynaform.get("xref");
        if (xb != null) {
            // Resuse the existing bean.
            xb.reset();
        }
        else {
            xb = new XreferenceBean();
        }
        dynaform.set("xref", xb);
    }
}

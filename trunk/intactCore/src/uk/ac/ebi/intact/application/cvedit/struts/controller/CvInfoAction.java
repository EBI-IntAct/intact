/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import uk.ac.ebi.intact.application.cvedit.struts.framework.CvAbstractAction;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants;
import uk.ac.ebi.intact.application.cvedit.struts.view.CvViewBean;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.persistence.SearchException;
import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CvInfoAction extends CvAbstractAction {

    /**
     * Action for submitting the CV info form.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in searching the database; refresh
     * mapping if the screen needs to be updated (this will only happen if the
     * short label on the screen is different to the short label returned by
     * getUnqiueShortLabel method. For all other instances, success mapping is
     * returned.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward execute(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response)
            throws Exception {
        // The view of the current object we are editing at the moment.
        CvViewBean viewbean = super.getIntactUser(request).getView();

        // The form to access input data.
        DynaActionForm theForm = (DynaActionForm) form;

        // Extract the short label for us to cheque for its uniqueness.
        String formlabel = (String) theForm.get("shortLabel");

        // Handler to the current user.
        IntactUserIF user = super.getIntactUser(request);

        // The name of the class associated with the current topic or class.
        String topic = user.getSelectedTopic();
        String className = super.getIntactService().getClassName(topic);
        Class clazz = null;

        // Holds the unique short label.
        String newlabel = null;
        try {
            clazz = Class.forName(className);
            newlabel = user.getUniqueShortLabel(clazz, formlabel);
        }
        catch (ClassNotFoundException cnfe) {
            super.log(ExceptionUtils.getStackTrace(cnfe));
            // The errors to report back.
            ActionErrors errors = new ActionErrors();
            errors.add(CvAbstractAction.INTACT_ERROR,
                    new ActionError("error.class", cnfe.getMessage()));
            super.saveErrors(request, errors);
            return mapping.findForward(CvEditConstants.FORWARD_FAILURE);
        }
        catch (SearchException se) {
            super.log(ExceptionUtils.getStackTrace(se));
            // The errors to report back.
            ActionErrors errors = new ActionErrors();
            errors.add(CvAbstractAction.INTACT_ERROR,
                    new ActionError("error.search", se.getMessage()));
            super.saveErrors(request, errors);
            return mapping.findForward(CvEditConstants.FORWARD_FAILURE);
        }
        // Update the view with new values.
//        super.log("Unique label is: " + newlabel);
        viewbean.setShortLabel(newlabel);
        viewbean.setFullName((String) theForm.get("fullName"));

        // We need to refresh the view with the unique label only if it is
        // different to what is appearing on the screen.
        if (!newlabel.equals(formlabel)) {
            super.log("Refreshing the screen");
            return mapping.findForward(CvEditConstants.FORWARD_REFRESH);
        }
        return mapping.findForward(CvEditConstants.FORWARD_SUCCESS);
    }
}

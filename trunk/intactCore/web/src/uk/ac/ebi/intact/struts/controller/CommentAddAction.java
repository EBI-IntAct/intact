/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.controller;

import uk.ac.ebi.intact.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.struts.view.CommentBean;
import uk.ac.ebi.intact.struts.view.CommentAddForm;
import uk.ac.ebi.intact.struts.view.CvViewBean;
import uk.ac.ebi.intact.struts.service.IntactService;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.persistence.CreateException;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;

/**
 * The action class is called when the user adds a new comment (annotation) to
 * the edit.jsp.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CommentAddAction extends IntactBaseAction {

    /**
     * Process the specified HTTP request, and create the corresponding
     * HTTP response (or forward to another web component that will create
     * it). Return an ActionForward instance describing where and how
     * control should be forwarded, or null if the response has
     * already been completed.
     *
     * @param mapping - The <code>ActionMapping</code> used to select this instance
     * @param form - The optional <code>ActionForm</code> bean for this request (if any)
     * @param request - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     *
     * @return - represents a destination to which the controller servlet,
     * <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
     * or HttpServletResponse.sendRedirect() to, as a result of processing
     * activities of an <code>Action</code> class
     */
    public ActionForward perform (ActionMapping mapping, ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response ) {
        // Need the form to get data entered by the user.
        CommentAddForm theForm = (CommentAddForm) form;

        // Handler to the IntactService.
        IntactService service = super.getIntactService();

        // The new annotation to add to database.
        Annotation annot = null;

        // The error container.
        ActionErrors errors = null;

        try {
            // The Intact Helper to search the database.
            IntactHelper helper = service.getIntactHelper();

            // The current CV object we are editing.
            CvViewBean viewbean = (CvViewBean) super.getSessionObject(
                request, "viewbean");
            CvObject cvobj = viewbean.getCvObject();

            // Get the topic object for the new annotation.
            CvTopic cvtopic = (CvTopic) helper.getObjectByLabel(
                CvTopic.class, theForm.getTopic());

            annot = new Annotation();
            annot.setAnnotationText(theForm.getText());
            annot.setCvTopic(cvtopic);
            annot.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
            service.getDAO().create(annot);

            // Add this new annotation to the cv object and update it.
            cvobj.addAnnotation(annot);
            helper.update(cvobj);
        }
        catch (IntactException ie) {
            // Can't create a helper class.
            errors = new ActionErrors();
            errors.add(super.INTACT_ERROR, new ActionError("error.datasource"));
        }
        catch (DataSourceException dse) {
            // Can't acquire the DAO instance to create.
            errors = new ActionErrors();
            errors.add(super.INTACT_ERROR, new ActionError("error.dao"));
        }
        catch (CreateException ce) {
            // Unable to create an annotation.
            errors = new ActionErrors();
            errors.add(super.INTACT_ERROR, new ActionError("error.create",
                "an Annotation object"));
        }
        if (errors != null && !errors.empty()) {
            super.saveErrors(request, errors);
            return (mapping.findForward(WebIntactConstants.FORWARD_FAILURE));
        }

        // The annotation collection for display.
        Collection beans = (Collection) super.getSessionObject(request,
            WebIntactConstants.ANNOTATIONS);
        // We need to update on the screen as well.
        beans.add(new CommentBean(annot));

        theForm.reset();

        return mapping.findForward(WebIntactConstants.FORWARD_SUCCESS);
    }
}
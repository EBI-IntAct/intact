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
import uk.ac.ebi.intact.struts.service.IntactService;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.persistence.TransactionException;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Iterator;

/**
 * The action class is called when the user adds a new comment (annotation).
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

        // The annotation collection.
        Collection beans = (Collection) super.getSessionObject(request,
            WebIntactConstants.ANNOTATIONS);

        // The topic and the text selected/entered by the user.
        String topic = theForm.getTopic();
        String text = theForm.getText();

        // The bean for 'topic'.
        CommentBean bean = findByTopic(topic, beans);


        // Handler to the IntactService.
        IntactService service = super.getIntactService();

        // The Intact Helper to search the database.
        IntactHelper helper = null;
        try {
            helper = service.getIntactHelper();
        }
        catch (IntactException ie) {
            // Can't create a helper class.
            ActionErrors errors = new ActionErrors();
            errors.add("no.ds", new ActionError("error.no.datasource"));
            super.saveErrors(request, errors);
            return (mapping.findForward(WebIntactConstants.FORWARD_FAILURE));
        }
        // Add to the existing comment if we have the topic.
        if (bean != null) {
            bean.addText(text);
            Annotation annot = bean.getAnnotation();
            annot.setAnnotationText(bean.getText());
            try {
                 helper.update(annot);
            }
            catch (IntactException ie) {
                // Error in updating the annotation object.
                ActionErrors errors = new ActionErrors();
                errors.add("update.error", new ActionError("error.update"));
                super.saveErrors(request, errors);
                return (mapping.findForward(WebIntactConstants.FORWARD_FAILURE));
            }
        }
        else {
            // New topic to add.
            Annotation annot = new Annotation();
            annot.setAnnotationText(text);
            // There is no need to create a topic as it must exist on the
            // database but for the moment just create one.
            CvTopic cvtopic = new CvTopic();
            cvtopic.setShortLabel(topic);
            cvtopic.setAc("EBI-XXX");

            annot.setCvTopic(cvtopic);

            // Add to the collection.
            bean = new CommentBean(annot);
            beans.add(bean);
        }
        theForm.reset();

        return mapping.findForward(WebIntactConstants.FORWARD_SUCCESS);
    }

    // Return a bean from the given collection for matching topic. Null is
    // returned if no matching bean found in the collection.
    private CommentBean findByTopic(String topic, Collection collection) {
        for (Iterator iter = collection.iterator(); iter.hasNext();) {
            CommentBean bean = (CommentBean) iter.next();
            if (bean.getTopic().equals(topic)) {
                return bean;
            }
        }
        // New topic.
        return null;
    }
}
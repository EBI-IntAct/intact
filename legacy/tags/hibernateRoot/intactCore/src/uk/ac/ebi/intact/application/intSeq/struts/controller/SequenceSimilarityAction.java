/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.struts.controller;

import org.apache.struts.action.*;

import uk.ac.ebi.intact.application.intSeq.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.intSeq.struts.view.SequenceSimilarityForm;
import uk.ac.ebi.intact.application.intSeq.struts.framework.SeqIdConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;


/**
 *
 * This action provides the actions required for the Similarity Search page.
 * SequenceSimilarityAction inherits IntactBaseAction, and process the alignment results by
 *  one of the inherited methods.
 *  This action allows:
 *      --to execute an alignment algorithm from the query sequence against the IntAct Database.
 *      --to retrieve percentage identity on fragments which match together.
 * The user is forwarded in the Similarity Search results page.
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class SequenceSimilarityAction extends IntactBaseAction {


    // --------- PUBLIC METHODS --------//

    /**    Execute method with the Struts 1.1 release
     * Process the specified HTTP request, and create the corresponding
     * HTTP response (or forward to another web component that will create
     * it). Return an ActionForward instance describing where and how
     * control should be forwarded, or null if the response has
     * already been completed. (exeptions are included and signaled)
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
    public ActionForward execute (ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response)
            throws IOException, ServletException{

        // clear all previous errors
        super.clearErrors();

        String sequence = null;

        // to retrieve the user sequence from the form
        if (form != null) {
            sequence = ((SequenceSimilarityForm)form).getSequence();
        } else {
            addError ("error.seqform.null");
            saveErrors(request);
            return mapping.findForward(SeqIdConstants.FORWARD_FAILURE);
        }

        // run blast.
        doSimilaritySearch (sequence, request);

        if (false == isEmptyError()) {
            //report any previous error
            return mapping.findForward(SeqIdConstants.FORWARD_FAILURE);
        }

        //to forward the user to the next jsp path, thanks to the string refered in struts-config.xml
        return mapping.findForward(SeqIdConstants.FORWARD_SUCCESS_SEQ);

    }
}


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
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.ArrayList;


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
    * @param seqMap - The <code>ActionMapping</code> used to select this instance
    * @param seqForm - The optional <code>ActionForm</code> bean for this request (if any)
    * @param seqReq - The HTTP request we are processing
    * @param seqRep - The HTTP response we are creating
    *
    * @return - represents a destination to which the controller servlet,
    * <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
    * or HttpServletResponse.sendRedirect() to, as a result of processing
    * activities of an <code>Action</code> class
    */


    public ActionForward execute (ActionMapping seqMap, ActionForm seqForm,
                                  HttpServletRequest seqReq, HttpServletResponse seqRep)
                                 throws IOException, ServletException{

            // Validate the request parameters specified by the user
	    ActionErrors errors = new ActionErrors();

            // Save the context to avoid repeat calls.
        ServletContext ctx = super.getServlet().getServletContext();

        String sequence = null;

            // to retrieve the user sequence from the form
        if (seqForm != null) {
            sequence = ((SequenceSimilarityForm)seqForm).getSequence();
        }
        else {
            errors.add(super.INTACT_ERROR, new ActionError("error.seqform.null"));
            super.saveErrors(seqReq, errors);
            return seqMap.findForward(SeqIdConstants.FORWARD_FAILURE);
        }

            // the blast command line is define in "SeqIdConstants.java" which refer to the web.xml file
        String commLine = ctx.getInitParameter(SeqIdConstants.BLAST_COMM_INTACT);
        String perc_base = ctx.getInitParameter(SeqIdConstants.GREATER_THAN_PERCENTAGE);
        String param = ctx.getInitParameter(SeqIdConstants.SMALLER_THAN_EVALUE);

            // through the RunSimilaritySearch class
        boolean command = super.ManageBlastResult(sequence, commLine, perc_base, param);
        if (command == false) {
            errors.add(super.INTACT_ERROR, new ActionError("error.command.failed"));
            super.saveErrors(seqReq, errors);
            return seqMap.findForward(SeqIdConstants.FORWARD_FAILURE);
        }
        ArrayList dataSetTransfered = super.getToTransfer();

        if (errors.size() != 0) {
                //report any previous error
            super.saveErrors(seqReq, errors);
            return seqMap.findForward(SeqIdConstants.FORWARD_FAILURE);
        }

            // get the current session
        HttpSession session = seqReq.getSession();

        if (session == null) {
            return seqMap.findForward(SeqIdConstants.FORWARD_END_SESSION);
        }
        //else {*/
            //store the result in the session "blast" will be recognized in the jsp page
        //session.setAttribute("blast", brb);
        session.setAttribute("similarityList", dataSetTransfered);

            //to send the user to the next jsp path, thanks to the string refered in struts-config.xml
        return seqMap.findForward(SeqIdConstants.FORWARD_SUCCESS_SEQ);
    }
}


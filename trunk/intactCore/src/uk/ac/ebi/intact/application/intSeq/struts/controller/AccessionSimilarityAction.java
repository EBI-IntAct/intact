/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.struts.controller;

import uk.ac.ebi.intact.application.intSeq.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.intSeq.struts.framework.SeqIdConstants;
import uk.ac.ebi.intact.application.intSeq.business.CallingSrs;
import uk.ac.ebi.intact.application.intSeq.business.CallingSrsIF;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class provides the actions required for the SRSSearchResults page. The user choose one
 * accession number and the action process the Business logic to forward a multiple alignment
 * result in the SimilaritySearchResults page.
 * This file inherits <code>IntactBaseAction</code>, where a common method is implemented and
 * manages the multiple alignment program.
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class AccessionSimilarityAction extends IntactBaseAction {

         // --------- PUBLIC METHODS --------//

    /**    Execute method with the Struts 1.1 release
    * Process the specified HTTP request, and create the corresponding
    * HTTP response (or forward to another web component that will create
    * it). Return an ActionForward instance describing where and how
    * control should be forwarded, or null if the response has
    * already been completed. (exeptions are included and signaled)
    *
    * @param acMap - The <code>ActionMapping</code> used to select this instance
    * @param acForm - The optional <code>ActionForm</code> bean for this request (if any)
    * @param acQuery - The HTTP request we are processing
    * @param acResponse - The HTTP response we are creating
    *
    * @return - represents a destination to which the controller servlet,
    * <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
    * or HttpServletResponse.sendRedirect() to, as a result of processing
    * activities of an <code>Action</code> class
    */

    public ActionForward execute (ActionMapping acMap, ActionForm acForm,
                                  HttpServletRequest acQuery, HttpServletResponse acResponse)
    throws IOException,ServletException{

            // Validate the request parameters specified by the user
        ActionErrors errors = new ActionErrors();

            // get the current session
        HttpSession session = super.getSession(acQuery);

            // Save the context to avoid repeat calls. (common to all users...)
        ServletContext ctx = super.getServlet().getServletContext();

            // recover the ac choosen by the user
        String accessionNumber = acQuery.getParameter("acc");

        if (accessionNumber == null || accessionNumber.length() < 1) {
            errors.add(super.INTACT_ERROR, new ActionError("error.choose.ac"));
            super.saveErrors(acQuery, errors);
            return acMap.findForward(SeqIdConstants.FORWARD_FAILURE);
        }

            // construct the wgetz command and recover the sequence corresponding to this AC.
        String seqCommand = ctx.getInitParameter(SeqIdConstants.SRSGETZ_SECOND_DEB) +
                               accessionNumber + ctx.getInitParameter(SeqIdConstants.SRSGETZ_SEQ_RETRIEVED);

            // calling the business logic class
        CallingSrsIF intactSrsSeq = null;
        intactSrsSeq = new CallingSrs (accessionNumber, seqCommand);     //(user, identifier);

            // retrieve the corresponding protein sequence.
        String seq = intactSrsSeq.GetSequenceFasta ();

        if (seq != "") {

                // the blast command line is define in "SeqIdConstants.java" which refers
                //to the web.xml file
            String theBlastCommand = ctx.getInitParameter(SeqIdConstants.BLAST_COMM_INTACT);
            String perc_base = ctx.getInitParameter(SeqIdConstants.GREATER_THAN_PERCENTAGE);
            String param = ctx.getInitParameter(SeqIdConstants.SMALLER_THAN_EVALUE);


                // run blast with a common method defined in IntactBaseAction.
            boolean command = super.ManageBlastResult(seq, theBlastCommand, perc_base, param);
            ArrayList dataSetTransfered = super.getToTransfer();
            if (command == false || dataSetTransfered.isEmpty() == true) {
                errors.add(super.INTACT_ERROR, new ActionError("error.command.failed"));
                super.saveErrors(acQuery, errors);
                return acMap.findForward(SeqIdConstants.FORWARD_FAILURE);
            }

            if (errors.size() != 0) {
                    // report any previous error.
                super.saveErrors(acQuery, errors);
                return acMap.findForward(SeqIdConstants.FORWARD_FAILURE);
            }

                // store the result in the session "similarityList" will be recognized in the jsp page
            session.setAttribute("similarityList", dataSetTransfered);

                //to forward the user to the next jsp path, thanks to the string refered in struts-config.xml
            return acMap.findForward(SeqIdConstants.FORWARD_SUCCESS_SEQ);

        }
        else {
            errors.add(super.INTACT_ERROR, new ActionError("error.srs.noseq"));
            super.saveErrors(acQuery, errors);
            return acMap.findForward(SeqIdConstants.FORWARD_FAILURE);
        }
    }
}


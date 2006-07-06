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
import uk.ac.ebi.intact.application.intSeq.business.Constants;
import uk.ac.ebi.intact.business.IntactException;
import org.apache.struts.action.*;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
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

    static Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

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
            throws IOException,ServletException{

        // clear all previous errors
        super.clearErrors();

        // get the current session
        HttpSession session = super.getSession(request);

        // recover the ac choosen by the user
        String accessionNumber = request.getParameter("acc");

        if (accessionNumber == null || accessionNumber.length() < 1) {
            addError ("error.choose.ac");
            super.saveErrors(request);
            return mapping.findForward(SeqIdConstants.FORWARD_FAILURE);
        }

        // construct the wgetz command and recover the sequence corresponding to this AC.
        String seqCommand = getSequenceSrsUrl(accessionNumber);

        // calling the business logic class
        CallingSrsIF intactSrsSeq = null;
        intactSrsSeq = new CallingSrs (accessionNumber, seqCommand);     //(user, identifier);

        // retrieve the corresponding protein sequence.
        String sequence = null;
        try {
            sequence = intactSrsSeq.GetSequenceFasta ();
        } catch (IntactException e) {
            addError ("error.srs.processing", e.getMessage());
            super.saveErrors(request);
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


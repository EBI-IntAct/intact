/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.struts.controller;

import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;

import uk.ac.ebi.intact.application.intSeq.struts.view.ProteinSearchForm;
import uk.ac.ebi.intact.application.intSeq.struts.framework.SeqIdConstants;
import uk.ac.ebi.intact.application.intSeq.struts.view.utils.SrsResultBean;

import uk.ac.ebi.intact.application.intSeq.struts.framework.IntactBaseAction;

import uk.ac.ebi.intact.application.intSeq.business.CallingSrsIF;
import uk.ac.ebi.intact.application.intSeq.business.CallingSrs;
import uk.ac.ebi.intact.business.IntactException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.net.URLEncoder;

/**
 *  This action provides the actions required for the SRS Search page,
 *      --to do a request on the SP-Tr public database by SRS, if the IntAct entry doesn't exist.
 *      --to retrieve in SP-Tr all entries corresponding to the request with
 *           * the accession number and its description in the first time.
 *           * the protein sequence in a second time.
 *      --to process an alignment algorithm just after the SRS query, in case of only one entry
 *        on SRS (no need to forward a list with only one item!).
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class ProteinSearchAction extends IntactBaseAction {

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

        //to retrieve the user protein feature from the form
        String searchString = "";
        if (form != null) {
            // searchString is initialised to "" !
            searchString = ((ProteinSearchForm)form).getSearchString().trim();

            if ("".equals(searchString)) {
                addError ("error.id.required");
                super.saveErrors(request);
                return mapping.findForward(SeqIdConstants.FORWARD_FAILURE);
            }

            // get the search string available in the View
            request.setAttribute (SeqIdConstants.SEARCH_STRING, searchString);

            // spaces refused in a SRS command line

            String begin = "";
            String end = "(";

            String[] tokens = searchString.split("\\s+");
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];
                logger.info("Param: " + token);
                begin = begin.concat (token);
                end = end.concat (token).concat ("*");

                if (i < tokens.length-1) {
                    begin = begin.concat (" ");
                    end = end.concat ("&");
                }
            }

            begin = begin.concat("|");
            end = end.concat(")");

            String search = begin.concat (end);

            logger.info ("Search string = " + search);
            search = URLEncoder.encode(search, "UTF-8");
            logger.info ("Encoded Search string = " + search);

            // the encoding transform space in + ... we want %20
            StringTokenizer st = new StringTokenizer(search, "+");
            String finalSearchString = st.nextToken();
            while (st.hasMoreTokens()) {
                finalSearchString = finalSearchString.concat( "%20" ).concat( st.nextToken() );
            }

            logger.info ("Modified Encoded Search string = " + finalSearchString);
            searchString = finalSearchString;
        } else {
            addError ("error.id.required");
            super.saveErrors(request);
            return mapping.findForward(SeqIdConstants.FORWARD_FAILURE);
        }


        String srsProteinSearchUrl = getProteinsSrsUrl(searchString);

        //calling the business logic class
        CallingSrsIF intactSrs = null;
        intactSrs = new CallingSrs (searchString, srsProteinSearchUrl);

        // get the current session
        HttpSession session = super.getSession(request);
        if (session == null) {
            logger.error ("Session timeout");
            return mapping.findForward(SeqIdConstants.FORWARD_END_SESSION);
        }



        ArrayList toTransfer = new ArrayList();

        /**
         * Desc of the array ...
         */
        ArrayList accDes = null;
        try {
            accDes = intactSrs.RetrieveAccDes();
        } catch (IntactException e) {
            addError ("error.srs.processing", e.getMessage());
            saveErrors(request);
            return mapping.findForward(SeqIdConstants.FORWARD_FAILURE);
        }

        if (accDes.size() == 0) {
            addError ("error.srs.emptyfile");
            saveErrors(request);
            return mapping.findForward(SeqIdConstants.FORWARD_FAILURE);
        }

        // in this case, same process than in the AccessionSimilarityAction:
        // retrieves the protein sequence directly and run blast.
        if (accDes.size() == 1) {

            ArrayList theOnlyAcc = (ArrayList)accDes.get(0);
            String proteinAC = (String)theOnlyAcc.get(0);

            logger.info ("Only one item found, search for the sequence (AC= " + proteinAC + ").");
            String srsSequenceSearchUrl = getSequenceSrsUrl(proteinAC);

            logger.info("Calling SRS: " + srsSequenceSearchUrl);
            intactSrs = new CallingSrs (proteinAC, srsSequenceSearchUrl);

            String sequence = null;
            try {
                sequence = intactSrs.GetSequenceFasta ();
            } catch (IntactException e) {
                addError ("error.srs.processing", e.getMessage());
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
        // none or more than one line in the result list of
        // accession_number+description_field list.
        else {
            if (accDes.size() > 1) {

                final String applicationPath = "http://" +
                                               request.getServerName() + ":" +
                                               request.getServerPort() +
                                               (request).getContextPath();

                ArrayList twoFields = new ArrayList();
                for (int m = 0; m < accDes.size(); m++) {
                    twoFields = (ArrayList)accDes.get(m);
                    if (twoFields.size() != 2) {    // accession number + description = two columns.
                        break;
                    } else {
                        String acc = (String) twoFields.get(0);
                        String des = (String) twoFields.get(1);
                        // transfer these data in a bean.
                        toTransfer.add (new SrsResultBean (acc, applicationPath, des));
                        twoFields.clear();
                    }
                }
            }

            if (false == isEmptyError()) {
                // report any previous error.
                saveErrors(request);
                return mapping.findForward(SeqIdConstants.FORWARD_FAILURE);
            }

            // store the result in the session "accList" to transfer it in the jsp page
            session.setAttribute("accList", toTransfer);

            // to send the user to the next jsp page, thanks to the string refered in struts-config.xml
            return mapping.findForward(SeqIdConstants.FORWARD_SUCCESS_ID);
        }
    }
}

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
import javax.servlet.ServletContext;

import uk.ac.ebi.intact.application.intSeq.struts.view.ProteinSearchForm;
import uk.ac.ebi.intact.application.intSeq.struts.framework.SeqIdConstants;
import uk.ac.ebi.intact.application.intSeq.struts.view.utils.SrsResultBean;

import uk.ac.ebi.intact.application.intSeq.struts.framework.IntactBaseAction;

import uk.ac.ebi.intact.application.intSeq.business.CallingSrsIF;
import uk.ac.ebi.intact.application.intSeq.business.CallingSrs;

import java.io.IOException;
import java.util.ArrayList;

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

        // --------- PUBLIC METHODS --------//

    /**    Execute method with the Struts 1.1 release
    * Process the specified HTTP request, and create the corresponding
    * HTTP response (or forward to another web component that will create
    * it). Return an ActionForward instance describing where and how
    * control should be forwarded, or null if the response has
    * already been completed. (exeptions are included and signaled)
    *
    * @param idMap - The <code>ActionMapping</code> used to select this instance
    * @param idForm - The optional <code>ActionForm</code> bean for this request (if any)
    * @param idQuery - The HTTP request we are processing
    * @param idResponse - The HTTP response we are creating
    *
    * @return - represents a destination to which the controller servlet,
    * <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
    * or HttpServletResponse.sendRedirect() to, as a result of processing
    * activities of an <code>Action</code> class
    */

    public ActionForward execute (ActionMapping idMap, ActionForm idForm,
                                  HttpServletRequest idQuery, HttpServletResponse idResponse)
    throws IOException,ServletException{

            // Validate the request parameters specified by the user
	    ActionErrors errors = new ActionErrors();

            // Save the context to avoid repeat calls. (common to all users...)
        ServletContext ctx = super.getServlet().getServletContext();

            //to retrieve the user protein feature from the form
        String proteinTopic = "";
        if (idForm != null) {
            proteinTopic = ((ProteinSearchForm)idForm).getSearchString();
                // spaces refused in a SRS command line
            proteinTopic = proteinTopic.replaceAll("\\s", "");
        }
        else {
            errors.add(super.INTACT_ERROR, new ActionError("error.id.required"));
            super.saveErrors(idQuery, errors);
            return idMap.findForward(SeqIdConstants.FORWARD_FAILURE);
        }

                /*
                // Handler to the Intact User.
            IntactUserIF user = super.getIntactUser(session);
                */

                // the getz or wgetz command lines are defined in "SeqIdConstants.java" ( -> web.xml file)
                // in two parts, waiting for the protein topic needed inside to make the request.
            String wholeSrsCommand = ctx.getInitParameter(SeqIdConstants.SRSGETZ_DEB_INTACT_DB) +
                                                    proteinTopic +
                                  ctx.getInitParameter(SeqIdConstants.SRSGETZ_ACCDES_RETRIEVED);

                //calling the business logic class
            CallingSrsIF intactSrs = null;
            intactSrs = new CallingSrs (proteinTopic, wholeSrsCommand);

                // get the current session
            HttpSession session = super.getSession(idQuery);
            if (session == null) {
                return idMap.findForward(SeqIdConstants.FORWARD_END_SESSION);
            }
                // Handler to the Intact User.
            //IntactUserIF user = super.getIntactUser(session);

            ArrayList toTransfer = new ArrayList();

        /**
         * Desc of the array ...
         */
        ArrayList accDes = intactSrs.RetrieveAccDes();
            if (accDes.size() == 0) {
                    errors.add(super.INTACT_ERROR, new ActionError("error.srs.emptyfile"));
                    super.saveErrors(idQuery, errors);
                    return idMap.findForward(SeqIdConstants.FORWARD_FAILURE);
                }

                // in this case, same process than in the AccessionSimilarityAction:
                // retrieves the protein sequence directly and run blast.
            if (accDes.size() == 1) {

                ArrayList theOnlyAcc = (ArrayList)accDes.get(0);
                String otherProteinTopic = (String)theOnlyAcc.get(0);
                    // to retrieve directly the protein sequence which corresponds to theOnlyAcc.get(0).
                wholeSrsCommand = ctx.getInitParameter(SeqIdConstants.SRSGETZ_SECOND_DEB) +
                            otherProteinTopic + ctx.getInitParameter(SeqIdConstants.SRSGETZ_SEQ_RETRIEVED);

                intactSrs = new CallingSrs (otherProteinTopic, wholeSrsCommand);

                String proteinSequence = intactSrs.GetSequenceFasta ();
                if (proteinSequence == "" || proteinSequence == null) {
                    errors.add(super.INTACT_ERROR, new ActionError("error.srs.noseq"));
                    super.saveErrors(idQuery, errors);
                    return idMap.findForward(SeqIdConstants.FORWARD_FAILURE);
                }
                    // to construct the command line in "SeqIdConstants.java".
                String theBlastCommand = ctx.getInitParameter(SeqIdConstants.BLAST_COMM_INTACT);
                String perc_base = ctx.getInitParameter(SeqIdConstants.GREATER_THAN_PERCENTAGE);
                String param = ctx.getInitParameter(SeqIdConstants.SMALLER_THAN_EVALUE);

                    // run blast with a common method defined in IntactBaseAction.
                boolean commandExec = super.ManageBlastResult(proteinSequence, theBlastCommand, perc_base, param);
                ArrayList dataSetTransfered = super.getToTransfer();
                if (commandExec == false || dataSetTransfered.isEmpty() == true) {
                    errors.add(super.INTACT_ERROR, new ActionError("error.command.failed"));
                    super.saveErrors(idQuery, errors);
                    return idMap.findForward(SeqIdConstants.FORWARD_FAILURE);
                }

                    //store the result in the session "similarityList" will be recognized in the jsp page
                session.setAttribute("similarityList", dataSetTransfered);

                    // forward to the blast page
                return idMap.findForward(SeqIdConstants.FORWARD_SUCCESS_SEQ);
            }
                // none or more than one line in the result list of
                // accession_number+description_field list.
            else {
                if (accDes.size() > 1) {

                    ArrayList twoFields = new ArrayList();
                    for (int m = 0; m < accDes.size(); m++) {
                        twoFields = (ArrayList)accDes.get(m);
                        if (twoFields.size() != 2) {    // accession number + description = two columns.
                            break;
                        }
                        else {
                            String acc = (String)twoFields.get(0);
                            String des = (String)twoFields.get(1);
                                // transfer these data in a bean.
                            toTransfer.add(new SrsResultBean(acc, des));
                            twoFields.clear();
                        }
                    }
                }
                /*else if (accDes.size() == 0) {
                    errors.add(super.INTACT_ERROR, new ActionError("error.srs.emptyfile"));
                    super.saveErrors(idQuery, errors);
                    return idMap.findForward(SeqIdConstants.FORWARD_FAILURE);
                }
                  */
                if (errors.size() != 0) {
                        // report any previous error.
                    super.saveErrors(idQuery, errors);
                    return idMap.findForward(SeqIdConstants.FORWARD_FAILURE);
                }

                    // store the result in the session "accList" to transfer it in the jsp page
                session.setAttribute("accList", toTransfer);

                    // to send the user to the next jsp page, thanks to the string refered
                    //in struts-config.xml
                return idMap.findForward(SeqIdConstants.FORWARD_SUCCESS_ID);
            }
        }
    }

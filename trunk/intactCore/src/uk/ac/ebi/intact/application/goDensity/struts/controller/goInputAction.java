package uk.ac.ebi.intact.application.goDensity.struts.controller;

/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.goDensity.business.binaryInteractions.GoGoDensity;
import uk.ac.ebi.intact.application.goDensity.business.dag.FastGoDag;
import uk.ac.ebi.intact.application.goDensity.exception.GoIdNotInDagException;
import uk.ac.ebi.intact.application.goDensity.exception.SameGraphPathException;
import uk.ac.ebi.intact.application.goDensity.struts.business.GoTokenizer;
import uk.ac.ebi.intact.application.goDensity.struts.view.goInputForm;
import uk.ac.ebi.intact.application.goDensity.setupGoDensity.Config;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * goInputAction is used when user paste some goIds in the text field and
 * press "submit" button. Exactly these GoIds will be displayed then.
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

public final class goInputAction extends Action {

     static Logger logger = Logger.getLogger("goDensity");

    /**
     * Action execute
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception java.io.IOException if an input/output error occurs
     * @exception javax.servlet.ServletException if a servlet exception occurs
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws IOException, ServletException {

        String radio = (String)request.getSession().getAttribute("radio");
        logger.info("request.getParameter(\"radio\") = " + radio);

        if (radio == null) {
            request.getSession().setAttribute("radio", "color");
            logger.info("request.getSession().setAttribute(\"radio\", \"color\")");
        }

        String goIds = null;
        goInputForm goForm = (goInputForm) form;

        if (null != form) {
            goIds = goForm.getGoIds();
            logger.info("goIds read from goIdForm (Bean) = " + goIds);
        }

        if (goIds != null) {
            List x = new ArrayList(GoTokenizer.getGoIdTokens(goIds));

            FastGoDag dag = FastGoDag.getInstance();
            GoGoDensity[][] binaryInteractions = new GoGoDensity[0][];

            try {
                binaryInteractions = dag.getBinaryInteractions(x, Config.USeONLyPRECALCULATIOnDATA);
            } catch (GoIdNotInDagException e) {
                // already catched before calling this Action (goInputForm)
            } catch (SameGraphPathException e) {
                // already catched before calling this Action (goInputForm)
            }

            request.getSession().setAttribute("gogodensity", binaryInteractions);
        }

        return (mapping.findForward("success"));
    }
}





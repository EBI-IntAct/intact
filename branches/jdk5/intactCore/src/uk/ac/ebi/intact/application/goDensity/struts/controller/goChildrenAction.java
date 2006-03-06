package uk.ac.ebi.intact.application.goDensity.struts.controller;

/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.goDensity.business.binaryInteractions.GoGoDensity;
import uk.ac.ebi.intact.application.goDensity.business.dag.FastGoDag;
import uk.ac.ebi.intact.application.goDensity.exception.GoIdNotInDagException;
import uk.ac.ebi.intact.application.goDensity.exception.KeyNotFoundException;
import uk.ac.ebi.intact.application.goDensity.exception.SameGraphPathException;
import uk.ac.ebi.intact.application.goDensity.setupGoDensity.Config;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Action for discovering children of a GO term.
 * Input like <pre>/children.do?goId1=GO:0008150&goId2=GO:0008150"</pre>
 * forwards to a site where all the children of GO:0008150 can be
 * discovered. It is used for "zoom in" functionality of the image
 * (provided by the imagemap links)
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

public final class goChildrenAction extends Action {

    static Logger logger = Logger.getLogger("goDensity");

    /**
     * Action execute
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws IOException, ServletException {

        ActionErrors myErrors = new ActionErrors();

        // get parameters of URL
        String goId1 = request.getParameter("goId1");
        String goId2 = request.getParameter("goId2");

        // get color setting state
        String radio = (String) request.getSession().getAttribute("radio");

        logger.info("request.getParameter(\"goId1\") = " + goId1);
        logger.info("request.getParameter(\"goId2\") = " + goId2);
        logger.info("request.getParameter(\"radio\") = " + radio);

        // if user hasn't set yet radio button
        if (radio == null)
            request.getSession().setAttribute("radio", "color");

        // get children for the two goIds
        if (goId1 != null && goId2 != null) {

            logger.info("FastGoDag.getInstance();");
            FastGoDag dag = FastGoDag.getInstance();
            HashSet set = new HashSet();
            try {
                Collection childrenOfGoId1 = dag.getChilds(goId1);
                Collection childrenOfGoId2 = dag.getChilds(goId2);

                Iterator iterator1 = childrenOfGoId1.iterator();
                while (iterator1.hasNext()) {
                    set.add((String) iterator1.next());
                }

                Iterator iterator2 = childrenOfGoId2.iterator();
                while (iterator2.hasNext()) {
                    set.add((String) iterator2.next());
                }

            } catch (KeyNotFoundException e) {
                logger.error("KeyNotFoundException - no more children!");
                myErrors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.invalid.childs"));
                super.saveErrors(request, myErrors);
                logger.debug(mapping.getInputForward());
                return mapping.getInputForward();
            }

            List x = new ArrayList(set);

            Iterator iterator = x.iterator();
            while (iterator.hasNext())
                logger.debug("children in Set: " + (String) iterator.next());

            GoGoDensity[][] binaryInteractions = new GoGoDensity[0][];

            try {
                binaryInteractions = dag.getBinaryInteractions(x, Config.USeONLyPRECALCULATIOnDATA);
            } catch (GoIdNotInDagException e) {
            } catch (SameGraphPathException e) {
            }

            request.getSession().setAttribute("gogodensity", binaryInteractions);
        }

        return (mapping.findForward("success"));
    }
}





/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.goDensity.struts.view;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.goDensity.business.dag.FastGoDag;
import uk.ac.ebi.intact.application.goDensity.struts.business.GoTokenizer;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Form bean for the goInputAction
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

public final class goInputForm extends ActionForm {

    /**
     * textfield
     */
    private String goIds = null;

    /**
     * setter
     * @param goids
     */
    public void setGoIds(String goids) {
        goIds = goids.trim();
    }

    /**
     * getter
     * @return goIds
     */
    public String getGoIds () {
        return goIds;
    }

    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset (ActionMapping mapping,
                       HttpServletRequest request) {
        goIds = null;
    }


    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {

        ActionErrors myErrors = new ActionErrors();

        if ((goIds == null) || (goIds.trim().length() == 0)) {
            super.getServlet().log ("No goIds found in the form data");
            myErrors.add ("noGoId", new ActionError ("error.invalid.goIds"));

        } else {
            Set setGoIds = new HashSet(GoTokenizer.getGoIdTokens(goIds.toString()));
            FastGoDag dag = FastGoDag.getInstance();

            Set goIdsNotInDag = dag.isGoIdInDag(setGoIds);
            if (goIdsNotInDag.size() > 0) {
                StringBuffer sb = new StringBuffer();
                Iterator iterator = goIdsNotInDag.iterator();
                while (iterator.hasNext()) {
                    String s = (String) iterator.next();
                    sb.append("-" + s + "<br>");
                }
                myErrors.add("NotInDag", new ActionError ("error.notInDag.goIds", sb.toString()));
            }

            Set goIdsInSameGraphPath = dag.isInSameGraphPath(setGoIds);
            if (goIdsInSameGraphPath.size() > 0) {
                StringBuffer sb = new StringBuffer();
                Iterator iterator = goIdsInSameGraphPath.iterator();
                while (iterator.hasNext()) {
                    String s = (String) iterator.next();
                    sb.append("-" + s + "<br>");
                }
                myErrors.add("InSameGraphPath", new ActionError ("error.sameGraphPath.goIds", sb.toString()));
            }
        }

        return myErrors;
    }

    public String toString () {
        StringBuffer sb = new StringBuffer();
        sb.append("goInputForm[goIds=").append(goIds).append("]");
        return (sb.toString());
    }
}

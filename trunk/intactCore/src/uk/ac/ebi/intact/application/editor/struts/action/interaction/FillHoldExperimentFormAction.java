/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ProteinEditForm;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ProteinBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.List;

/**
 * Action to populate the form with experiments ob hold.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FillHoldExperimentFormAction extends AbstractEditorAction {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The view of the current object we are editing at the moment.
        InteractionViewBean view = (InteractionViewBean)
                getIntactUser(request).getView();

        // Populate the form.
        PropertyUtils.setSimpleProperty(form, "experiments", view.getHoldExperiments());

//        HttpSession session = getSession(request);
//        for (Enumeration enum = session.getAttributeNames(); enum.hasMoreElements();) {
//            String name = (String) enum.nextElement();
//            if (name.equals("intProtEditForm")) {
//                if (session.getAttribute(name) != null) {
//                    ProteinEditForm protform = (ProteinEditForm) session.getAttribute(name);
//                    List list = protform.getProteins();
//                    for (int j = 0; j < list.size(); j++) {
//                        System.out.println("Protein Bean: " + ((ProteinBean) list.get(j)).getShortLabel());
//                    }
//                }
//            }
//            System.out.println("Request: " + name +  " items " + session.getAttribute(name));
//        }
//        for (Enumeration eum = request.getParameterNames(); eum.hasMoreElements();) {
//            String name = (String) eum.nextElement();
//            System.out.print("Parameter Name: " + name);
//            System.out.println(" Parameter value is " + request.getParameter(name));
//        }
        return mapping.findForward(FORWARD_SUCCESS);
    }
}

/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import org.apache.struts.util.MessageResources;
import org.apache.struts.Globals;

/**
 * This class is a decorator of the Interaction objects that we keep in our
 * List. This class provides a number of methods for formatting data,
 * creating dynamic links, and exercising some aspects of the display:table
 * API functionality
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntDisplayWrapper extends AbstractIntDisplayWrapper {

    public String getAction() {
        // Resource to get labels.
        MessageResources msgres = (MessageResources)
                getPageContext().getServletContext().getAttribute(Globals.MESSAGES_KEY);

        return "<input type=\"submit\" name=\"dispatch\" value=\""
                + msgres.getMessage("exp.int.button.edit") + "\""
                + " onclick=\"setIntAc('" + getAc() + "');\">"
                + "<input type=\"submit\" name=\"dispatch\" value=\""
                + msgres.getMessage("exp.int.button.del") + "\""
                + " onclick=\"setIntAc('" + getAc() + "');\">";
    }

//    private int getIndex() {
//        String pageStr = getPageContext().getRequest().getParameter("page");
//        // The default current page.
//        int page = 0;
//        // Guard against the null pointer as the first page doesn't have page para.
//        if (pageStr != null) {
//            page = Integer.parseInt(pageStr);
//        }
//        // The maximum interactions per page.
//        int maxPageSize = EditorService.getInstance().getInteger(
//                "exp.interaction.page.limit");
//        // The current view index.
//        int viewIndex = getViewIndex();
//        if (page > 1) {
//            viewIndex += (page - 1) * maxPageSize;
//        }
//        return viewIndex;
//    }
}

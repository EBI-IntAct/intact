/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import org.apache.taglibs.display.TableDecorator;
import uk.ac.ebi.intact.application.editor.business.EditorService;
import uk.ac.ebi.intact.application.editor.struts.framework.util.PageValueBean;
import uk.ac.ebi.intact.model.Interaction;

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
        PageValueBean pvbEdit = new PageValueBean("interaction", "edit", getAc());
        PageValueBean pvbDel = new PageValueBean("interaction", "delete", getAc());
        return "<button type=\"submit\" name=\"intCmd\" "
                + pvbEdit + "\">Edit Interaction</button>"
                + "<button type=\"submit\" name=\"intCmd\" "
                + pvbDel + "\">Delete Interaction</button>";
//        return "<input type=\"submit\" name=\"intCmd[" + getIndex() + "]"
//                + "\" value=\"Edit Interaction\" title=\"Edit this Interaction\">"
//                + "<input type=\"submit\" name=\"intCmd[" + getIndex() + "]"
//                + "\" value=\"Delete Interaction\" "
//                + "title=\"Delete this Interaction from the Experiment\">";
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

/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import org.apache.taglibs.display.TableDecorator;
import uk.ac.ebi.intact.application.editor.business.EditorService;

/**
 * This class is a decorator of the Interaction objects that we keep in our
 * List. This class provides a number of methods for formatting data,
 * creating dynamic links, and exercising some aspects of the display:table
 * API functionality
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionDisplayWrapper extends TableDecorator {

    /**
     * Creates a new Wrapper decorator who's job is to reformat some of the
     * data located in our ListObject's.
     */
//    public InteractionDisplayWrapper() {
//        super();
//    }

    public String getAction() {
        return "<input type=\"submit\" name=\"intCmd[" + getIndex() + "]"
                + "\" value=\"Edit Interaction\" title=\"Edit this Interaction\">"
                + "<input type=\"submit\" name=\"intCmd[" + getIndex() + "]"
                + "\" value=\"Delete Interaction\" "
                + "title=\"Delete this Interaction from the Experiment\">";
    }

    /**
     * Returns the topic with a link to show its contents in a window.
     * @return the topic as a browsable link.
     */
    public String getShortLabel() {
        String label = ((InteractionBean) getObject()).getShortLabel();
        return "<a href=\"" + "javascript:show('Interaction', '"
                + label + "')\"" + ">" + label + "</a>";
    }

    private int getIndex() {
        String pageStr = getPageContext().getRequest().getParameter("page");
        // The default current page.
        int page = 0;
        // Guard against the null pointer as the first page doesn't have page para.
        if (pageStr != null) {
            page = Integer.parseInt(pageStr);
        }
        // The maximum interactions per page.
        int maxPageSize = EditorService.getInstance().getInteger(
                "exp.interaction.page.limit");
        // The current view index.
        int viewIndex = getViewIndex();
        if (page > 1) {
            viewIndex += (page - 1) * maxPageSize;
        }
        return viewIndex;
    }
}

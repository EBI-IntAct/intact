/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view;

import org.apache.taglibs.display.TableDecorator;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.util.LockManager;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.business.IntactHelper;

import java.text.SimpleDateFormat;

/**
 * This class is the wrapper class for the display library to display
 * results from a search page.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ResultDisplayWrapper extends TableDecorator {

    /**
     * The formatter for the date.
     */
    private static SimpleDateFormat ourDateFormatter =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    /**
     * @return the link to the search application; clicking on this link, the
     * user will be taken into the search application.
     */
    public String getAc() {
        AnnotatedObject annotobj = (AnnotatedObject) getObject();
        String topic = IntactHelper.getDisplayableClassName(annotobj);
        return "<a href=\"" + "javascript:show('" + topic + "', '"
                + annotobj.getShortLabel() + "')\">" + annotobj.getAc() + "</a>";
    }

    /**
     * Returns the short label.
     * @return short label.
     */
    public String getShortLabel() {
        AnnotatedObject annotbj = (AnnotatedObject) getObject();

        // The lock manager to check the lock.
        LockManager lcm = LockManager.getInstance();
        if (lcm.hasLock(annotbj.getAc())) {
            // This object has a lock. Get the current user.
            EditUserI user = (EditUserI) getPageContext().getSession().getAttribute(
                    EditorConstants.INTACT_USER);

            // The current lock associated with the object.
            LockManager.LockObject lock = lcm.getLock(annotbj.getAc());

            // Check the lock owner with the current user.
            if (lock.getOwner().equals(user.getUserName())) {
                // Locked by the same user.
                return "<span class=\"owner\">" + getEditorLink(annotbj) + "</span>";
            }
            // Locked by someone else.
            return "<span class=\"error\">" + annotbj.getShortLabel() + "</span>";
        }
        // Not locked.
        return getEditorLink(annotbj);
    }

    /**
     * Returns the full name.
     * @return the full name as a <code>String</code> for the wrapped object.
     */
    public String getFullName() {
        AnnotatedObject annotbj = (AnnotatedObject) getObject();
        return annotbj.getFullName();
    }

    /**
     * @return the owner for the current bean. "---" is returned if the current
     * bean is not locked.
     */
    public String getLock() {
        AnnotatedObject annotbj = (AnnotatedObject) getObject();
        LockManager.LockObject lock =
                LockManager.getInstance().getLock(annotbj.getAc());
        if (lock == null) {
            // No owner; no need for the title
            return "<input type=\"text\" size=\"7\" value=\"  ---  \" readonly>";
        }
        // Get the owner and the time stamp.
        String owner = lock.getOwner();
        String title = ourDateFormatter.format(lock.getLockDate());
        return "<input type=\"text\" size=\"7\" value=\"" + owner + "\" title=\""
                + title + "\" readonly>";
    }

    // Helper methods.

    /**
     * @return the link for the result page; clicking on this link, the
     * user will be taken into the edit page.
     */
    private String getEditorLink(AnnotatedObject annotobj) {
        String topic = IntactHelper.getDisplayableClassName(annotobj);
        return "<a href=result?ac=" + annotobj.getAc() + "&searchClass="
                + topic + ">" + annotobj.getShortLabel() + "</a>";
    }
}

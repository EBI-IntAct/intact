/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view;

import org.apache.taglibs.display.TableDecorator;
import uk.ac.ebi.intact.application.editor.util.LockManager;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;

import javax.servlet.ServletContext;

/**
 * This class is a decorator for the display library.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class Wrapper extends TableDecorator {

    /**
     * @return the link for the result page; clicking on this link, the
     * user will be taken into the edit page.
     */
    public String getSearchLink() {
        ResultBean bean = (ResultBean) getObject();
        String className = bean.getClassName();
        int lastPos = className.lastIndexOf('.');
        String type = className.substring(lastPos + 1);
        return "<a href=\"" + "javascript:show('" + type + "', " + "'"
                + bean.getShortLabel() + "')\"" + ">" + bean.getAc() + "</a>";
    }

    /**
     * @return the link for the result page; clicking on this link, the
     * user will be taken into the edit page.
     */
    public String getEditorLink() {
        ResultBean bean = (ResultBean) getObject();
        String ac = bean.getAc();
        String className = bean.getClassName();
        int lastPos = className.lastIndexOf('.');
        return "<a href=result?ac=" + ac + "&searchClass="
                + className.substring(lastPos + 1) + ">" + bean.getShortLabel() + "</a>";
    }

    /**
     * @return the owner for the current bean. "---" is returned if the current
     * bean is not locked.
     */
    public String getLockOwner() {
        ServletContext ctx = getPageContext().getServletContext();
        LockManager lmr = (LockManager) ctx.getAttribute(EditorConstants.LOCK_MGR);
        ResultBean bean = (ResultBean) getObject();
        return lmr.getOwner(bean.getAc());
    }
}
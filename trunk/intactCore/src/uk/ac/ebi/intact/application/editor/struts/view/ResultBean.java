/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view;

import uk.ac.ebi.intact.application.editor.util.LockManager;
import uk.ac.ebi.intact.model.AnnotatedObject;

import java.io.Serializable;

/**
 * This class contains information for a single search result.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */

public class ResultBean implements Serializable {

    /**
     * Reference to CV object.
     */
    private AnnotatedObject myAnnotObject;

    /**
     * Handler to the lock manager.
     */
    private LockManager myLmr;

    /**
     * Constructs with an Annotated object and the lock manager for it.
     * @param anobj the <code>AnnotatedObject</code> to extract information.
     * @param lmr the lock manager for the bean.
     */
    public ResultBean(AnnotatedObject anobj, LockManager lmr) {
        myAnnotObject = anobj;
        myLmr = lmr;
    }

    /**
     * Returns the AC as a string.
     * @return accession number of the CvObject.
     */
    public String getAc() {
        return myAnnotObject.getAc();
    }

    /**
     * Returns the short label.
     * @return short label.
     */
    public String getShortLabel() {
        return myAnnotObject.getShortLabel();
    }

    /**
     * Returns the full name.
     * @return the full name as a <code>String</code> for the wrapped object.
     */
    public String getFullName() {
        return myAnnotObject.getFullName();
    }

    /**
     * The class name of the wrapped object.
     * @return he class name of the wrapped object as a string object.
     */
    public String getClassName() {
        return myAnnotObject.getClass().getName();
    }

    /**
     * @return the link for the result page; clicking on this link, the
     * user will be taken into the edit page.
     */
    public String getSearchLink() {
        String className = getClassName();
        int lastPos = className.lastIndexOf('.');
        String type = className.substring(lastPos + 1);
        return "<a href=\"" + "javascript:show('" + type + "', " + "'"
                + getShortLabel() + "')\"" + ">" + getAc() + "</a>";
    }

    /**
     * @return the link for the result page; clicking on this link, the
     * user will be taken into the edit page.
     */
    public String getEditorLink() {
        String className = getClassName();
        int lastPos = className.lastIndexOf('.');
        return "<a href=result?ac=" + getAc() + "&searchClass="
                + className.substring(lastPos + 1) + ">" + getShortLabel() + "</a>";
    }

    /**
     * @return the owner for the current bean. "---" is returned if the current
     * bean is not locked.
     */
    public String getLockOwner() {
        return myLmr.getOwner(getAc());
    }

    /**
     * Get method for results.jsp
     * @return true if this bean is locked or false is returned for otherwise.
     */
    public String getLocked() {
        return myLmr.hasLock(getAc()) ? "true" : "false";
    }

    // Override Objects's equal method.

    /**
     * Compares <code>obj</code> with this object according to
     * Java's equals() contract. Only returns <tt>true</tt> if the AC for both
     * objects match.
     *
     * @param obj the object to compare.
     */
    public boolean equals(Object obj) {
        // Identical to this?
        if (obj == this) {
            return true;
        }
        if ((obj != null) && (getClass() == obj.getClass())) {
            // Can safely cast it.
            return getAc().equals(((ResultBean) obj).getAc());
        }
        return false;
    }
}

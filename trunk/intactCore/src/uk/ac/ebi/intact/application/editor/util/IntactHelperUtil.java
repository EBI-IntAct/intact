/*
Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.util;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;

/**
 * This class manages a ThreadLocal IntactHelper instance. Obtain the helper
 * for the current thread by calling IntactHelperUtil.getIntactHelper(String, String)
 * or IntactHelperUtil.getIntactHelper() method where the latter may return a null
 * instance if there is no helper found in the current thread. Instead of creating
 * and destroying many instances of a helper during a request, this class stores
 * it in a threadlocal variable to allow access throughout the thread life cycle.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntactHelperUtil {

    /**
     * Holds the current IntactHelper if one has been created.
     */
    private static final ThreadLocal ourThreadHelper = new ThreadLocal();

    /**
     * Returns an IntactHelper instance for given user and password or default
     * helper if user is null.
     * @param user the user for the helper - only used if there is no helper found
     * for the current thread; null is for a default helper.
     * @param password user the password for the helper - only used if there is
     * no helper found for the current thread.
     * @return a new IntactHelper created with <code>user</code> and
     * <code>password</code> if there is no IntactHelper found for the current
     * thread; otherwise, the IntactHelper for the current thread is returned.
     * @throws IntactException for problems in creating an IntactHelper.
     */
    public static IntactHelper getIntactHelper(String user, String password)
            throws IntactException {
        IntactHelper helper = (IntactHelper) ourThreadHelper.get();
        if (helper == null) {
            helper = (user == null) ? new IntactHelper() : new IntactHelper(user, password);
            ourThreadHelper.set(helper);
        }
        return helper;
    }

    /**
     * Returns the default IntactHelper instance.
     * @return a new default IntactHelper created only if there is no IntactHelper
     * found for the current thread; otherwise, the IntactHelper for the current
     * thread is returned. All subsequent calls (during a thread life cycle) either
     * to this method or to the {@link #getIntactHelper(String, String)} returns
     * this (default) helper.
     * @throws IntactException for problems in creating an IntactHelper.
     */
    public static IntactHelper getDefaultIntactHelper() throws IntactException {
        return getIntactHelper(null, null);
    }

    /**
     * Returns the IntactHelper stored for the current thread.
     * @return the IntactHelper stored for the current thread. This method may
     * return null if there is no IntactHelper stored for the current thread.
     */
    public static IntactHelper getIntactHelper() {
        return (IntactHelper) ourThreadHelper.get();
    }

    /**
     * Closes the IntactHelper for the current thread.
     * @throws IntactException for problems in closing the helper.
     */
    public static void closeIntactHelper() throws IntactException {
        IntactHelper helper = (IntactHelper) ourThreadHelper.get();
        try {
            if (helper != null) {
                helper.closeStore();
            }
        } finally {
            ourThreadHelper.set(null);
        }
    }
}

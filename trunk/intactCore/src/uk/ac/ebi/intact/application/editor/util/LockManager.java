/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * The lock manager keeps a track of edit objects. It uses the AC as a unique
 * identifier for edit objects. Only a single instance of this class is used
 * by multiple users. This class is thread safe.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class LockManager {

    // The lock object.
    public class LockObject {

        /**
         * The id.
         */
        private String myId;

        /**
         * The owner of the lock.
         */
        private String myOwner;

        /**
         * The time of the lock.
         */
        private Date myLockDate;

        private LockObject(String id, String owner) {
            myId = id;
            myOwner = owner;
            myLockDate = new Date();
        }

        private String getId() {
            return myId;
        }

        // These two methods are public as they are accessed from out side of
        // this class.

        public String getOwner() {
            return myOwner;
        }

        public Date getLockDate() {
            return myLockDate;
        }

        // Override Objects's equal method.

        /**
         * Compares <code>obj</code> with this object according to
         * Java's equals() contract. Only returns <tt>true</tt> if the id for both
         * objects match.
         * @param obj the object to compare.
         */
        public boolean equals(Object obj) {
            // Identical to this?
            if (obj == this) {
                return true;
            }
            if ((obj != null) && (getClass() == obj.getClass())) {
                // Can safely cast it.
                return myId.equals(((LockObject) obj).myId);
            }
            return false;
        }
    }

    // ------------------------------------------------------------------------

    // Class Data

    /**
     * The only instance of this class.
     */
    private static LockManager ourInstance = new LockManager();

    // Instance Data

    /**
     * A list of object ids (ACs) which are in use.
     */
    private List myLocks = new ArrayList();

    // Constructors

    /**
     * Default constructor; make it private to stop it from instantiating.
     */
    private LockManager() {}

    /**
     * @return the only instance of this class.
     */
    public static LockManager getInstance() {
        return ourInstance;
    }

    /**
     * Checks the existence of given lock.
     * @param ac the id to check for the lock
     * @return true if a lock exists for <code>ac</code>; false is returned for
     * all other instances.
     */
    public synchronized boolean hasLock(String ac) {
        for (Iterator iter = myLocks.iterator(); iter.hasNext();) {
            LockObject lo = (LockObject) iter.next();
            if (lo.getId().equals(ac)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the lock object for given id if it exists.
     * @param id the of the lock.
     * @return the lock object for <code>id</code> if a lock exists for it. Null
     * is returned if there is no lock object.
     */
    public synchronized LockObject getLock(String id) {
        for (Iterator iter = myLocks.iterator(); iter.hasNext();) {
            LockObject lo = (LockObject) iter.next();
            if (lo.getId().equals(id)) {
                return lo;
            }
        }
        return null;
    }
    /**
     * Returns the owner for given id if it exists.
     * @param id the of the lock.
     * @return the owner of <code>id</code> if there is an owner or an empty
     * street is returned.
     */
    public synchronized String getOwner(String id) {
        LockObject lock = getLock(id);
        if (lock != null) {
            return lock.getOwner();
        }
        return "";
    }

    /**
     * Obtains a lock.
     * @param id the id to obtain the lock for.
     * @param owner the onwer of the lock.
     * @return true if a lock was acquired successfully; false is returned for
     * all other instances.
     */
    public synchronized boolean acquire(String id, String owner) {
        // Get any existing lock.
        LockObject lo = getLock(id);

        // Have we already got the lock?
        if (lo != null) {
            if (lo.getOwner().equals(owner)) {
                // The same user is trying the same lock; allow it.
                return true;
            }
            else {
                // Different user; don't allow it.
                return false;
            }
        }
        // There is no lock associated with the id; create a new lock.
        myLocks.add(new LockObject(id, owner));
        return true;
    }

    /**
     * Removes given lock.
     * @param id the id for the lock.
     */
    public synchronized void release(String id) {
        // The lock to remove.
        LockObject lock = null;
        for (Iterator iter = myLocks.iterator(); iter.hasNext();) {
            LockObject lo = (LockObject) iter.next();
            if (lo.getId().equals(id)) {
                lock = lo;
                break;
            }
        }
        if (lock != null) {
            myLocks.remove(lock);
            System.out.println("Release locked: " + lock.getId() + " by owner: " + lock.getOwner());
        }
    }

    /**
     * Release all the locks held by given owner.
     * @param owner the owner to release the locks for.
     */
    public synchronized void releaseAllLocks(String owner) {
        // Holds locks to release; to avoid concurrent modification ex.
        List locks = new ArrayList();

        for (Iterator iter = myLocks.iterator(); iter.hasNext();) {
            LockObject lock = (LockObject) iter.next();
            if (lock.getOwner().equals(owner)) {
                System.out.println("Added lock " + lock.getId() + " to release");
                locks.add(lock);
            }
        }
        // Iterate through the temp locks and remove one by one from the cache.
        for (Iterator iter = locks.iterator(); iter.hasNext();) {
            myLocks.remove(iter.next());
        }
    }
}

/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.view;

import uk.ac.ebi.intact.model.Xref;

/**
 * A <code>XreferenceBean</code> with transactional support.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class TransactionalXrefBean extends XreferenceBean {

    // Package visible static data

    /**
     * The state for a bean created with the intention of adding to the CV object.
     */
    static final int ADD = 1;

    /**
     * The state for a bean created with the intention of deleting from the CV
     * object.
     */
    static final int DEL = 2;

    /**
     * The transaction type when this object was created.
     */
    private int myTransState;

    // Static methods

    /**
     * Returns an instance of this class created from a Xreference object with
     * transaction state set to <code>ADD</code>.
     * @param xref the <code>Xref</code> object to construct an
     * instance of this class.
     * @return an instance of XreferenceBean with its transaction state set to
     * <code>ADD</code>.
     *
     * <pre>
     * post: return.getTransState() = ADD
     * </pre>
     */
    public static final TransactionalXrefBean createXrefBeanAdd(Xref xref) {
        TransactionalXrefBean bean = new TransactionalXrefBean(xref);
        bean.myTransState = ADD;
        return bean;
    }

    /**
     * Returns an instance of this class created from an Annotation object with
     * transaction state set to <code>DEL</code>.
     * @param key the primary key for this instance.
     * @param xref the <code>Xref</code> object to construct an instance of
     *  this class.
     * @return an instance of XreferenceBean with its transaction state set to
     * <code>DEL</code>.
     *
     * <pre>
     * post: return.getTransState() = DEL
     * post: return.getKey() = key
     * </pre>
     */
    public static final TransactionalXrefBean createXrefBeanDel(long key, Xref xref) {
        TransactionalXrefBean bean = new TransactionalXrefBean(key, xref);
        bean.myTransState = DEL;
        return bean;
    }

    /**
     * Returns the transaction state.
     * @return the transaction state when this bean was created.
     *
     * <pre>
     * post: retunrn = ADD or DEL
     * </pre>
     */
    public int getTransState() {
        return myTransState;
    }

    // Private constructors, so objects are created only by factory methods.

    private TransactionalXrefBean(Xref xref) {
        super(xref);
    }

    private TransactionalXrefBean(long key, Xref xref) {
        super(key, xref);
    }
}

/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.view;

import uk.ac.ebi.intact.model.Annotation;

/**
 * A <code>CommentBean</code> with transactional support.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class TransactionalCommentBean extends CommentBean {

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
     * Returns an instance of this class created from an Annotation object with
     * transaction state set to <code>ADD</code>.
     * @param annotation the <code>Annotation</code> object to construct an
     * instance of this class.
     * @return an instance of CommentBean with its transaction state set to
     * <code>ADD</code>.
     *
     * <pre>
     * post: return.getTransState() = ADD
     * </pre>
     */
    public static final TransactionalCommentBean createCommentBeanAdd(Annotation annotation) {
        TransactionalCommentBean bean = new TransactionalCommentBean(annotation);
        bean.myTransState = ADD;
        return bean;
    }

    /**
     * Returns an instance of this class created from an Annotation object with
     * transaction state set to <code>DEL</code>.
     * @param key the primary key for this instance.
     * @param annotation the <code>Annotation</code> object to construct an
     * instance of this class.
     * @return an instance of CommentBean with its transaction state set to
     * <code>DEL</code>.
     *
     * <pre>
     * post: return.getTransState() = DEL
     * post: return.getKey() = key
     * </pre>
     */
    public static final TransactionalCommentBean createCommentBeanDel(long key,
                                                         Annotation annotation) {
        TransactionalCommentBean bean =
                new TransactionalCommentBean(key, annotation);
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

    private TransactionalCommentBean(Annotation annotation) {
        super(annotation);
    }

    private TransactionalCommentBean(long key, Annotation annotation) {
        super(key, annotation);
    }
}

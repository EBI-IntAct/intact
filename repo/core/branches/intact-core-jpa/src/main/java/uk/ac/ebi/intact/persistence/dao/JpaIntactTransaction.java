package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.business.IntactTransactionException;

import javax.persistence.EntityTransaction;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class JpaIntactTransaction extends IntactTransaction {

    private EntityTransaction transaction;
    private boolean wasCommitted = false;
    private boolean wasRolledBack = false;

    public JpaIntactTransaction(IntactSession session, EntityTransaction transaction) {
        super(session, null);
        this.transaction = transaction;
    }

    public boolean wasCommitted() {
        return wasCommitted;
    }

    public void rollback() throws IntactTransactionException {
       transaction.rollback();
        wasRolledBack = true;
    }

    public void commit() throws IntactTransactionException {
        transaction.commit();
        wasCommitted = true;
    }

    public Object getWrappedTransaction() {
        return transaction;
    }

    public boolean wasRolledBack() {
        return wasRolledBack();
    }
}

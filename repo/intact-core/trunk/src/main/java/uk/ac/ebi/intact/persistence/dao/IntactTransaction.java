/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import uk.ac.ebi.intact.business.IntactException;

/**
 * It is a wrapper for Transactions
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-Jul-2006</pre>
 */
public class IntactTransaction
{

    private static final Log log = LogFactory.getLog(IntactTransaction.class);

    private Transaction transaction;

    public IntactTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void commit() throws IntactException
    {
        log.debug("Committing transaction");

        try
        {
            transaction.commit();
        }
        catch (HibernateException e)
        {
            throw new IntactException("Commit exception", e);
        }
    }

    public void rollback() throws IntactException
    {
        log.debug("Rolling-back transaction");

        try
        {
            transaction.rollback();
        }
        catch (HibernateException e)
        {
            throw new IntactException("Rollback exception", e);
        }
    }

    public boolean wasCommitted()
    {
        return transaction.wasCommitted();
    }

    public boolean wasRolledBack()
    {
        return transaction.wasRolledBack();
    }

}

/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21-Jul-2006</pre>
 */
public abstract class AbstractIntactTest extends TestCase
{

    private static final Log log = LogFactory.getLog(AbstractIntactTest.class);

     private IntactTransaction tx;

    protected void setUp() throws Exception
    {
        super.setUp();

        tx = DaoFactory.beginTransaction();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();

        tx.commit();
        tx = null;
    }

}

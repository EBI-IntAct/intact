/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;

import java.util.Collection;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13-Jul-2006</pre>
 */
public class ProteinTest extends TestCase
{

    private static final Log log = LogFactory.getLog(ProteinTest.class);

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

    public void testUnexpectedUpdateOnXref()
    {
        Protein p = DaoFactory.getProteinDao().getByAc("EBI-493");
        assertNotNull(p);

        log.debug("Protein: "+p.getAc());

        Collection<Component> comps = p.getActiveInstances();

        for (Component c : comps)
        {
            log.debug("\tComp: "+c.getAc());
        }

        assertFalse(comps.isEmpty());

    }

}

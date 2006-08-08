/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;

import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Aug-2006</pre>
 */
public class ProteinDaoTest extends TestCase
{

    private static final Log log = LogFactory.getLog(ProteinDaoTest.class);

    public void testGetPartnersUniprotIdsByProteinAc()
    {
        List<String> uniprotIds = IntactContext.getCurrentInstance().getDataContext()
                 .getDaoFactory().getProteinDao().getPartnersUniprotIdsByProteinAc("EBI-100028");

        assertEquals(6, uniprotIds.size());
    }

}

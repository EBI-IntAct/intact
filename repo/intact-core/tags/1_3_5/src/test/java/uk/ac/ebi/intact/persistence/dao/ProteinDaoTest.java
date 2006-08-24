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
import uk.ac.ebi.intact.model.ProteinImpl;

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

    private ProteinDao proteinDao;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();

    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        proteinDao = null;
    }

    public void testGetPartnersUniprotIdsByProteinAc()
    {
        List<String> uniprotIds = proteinDao.getPartnersUniprotIdsByProteinAc("EBI-100028");

        assertEquals(6, uniprotIds.size());
    }

    public void testGetUniprotProteins()
    {
        List<ProteinImpl> uniprots = proteinDao.getUniprotProteins(0,50);
        assertEquals("Max results is 50, so we expect 50 results", 50, uniprots.size());
    }

    public void testGetByUniprotId()
    {
        List<ProteinImpl> prots = proteinDao.getByUniprotId("Q9VE54");

        ProteinImpl prot = prots.get(0);
        
        assertNotNull(prot);
        assertEquals("EBI-100018", prot.getAc());

    }

}

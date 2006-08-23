/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.CvDatabase;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

import junit.framework.TestCase;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class CvObjectDaoTest  extends TestCase {
     private static final Log log = LogFactory.getLog(DaoFactoryTest.class);

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testgetByPsiMiRefCollection()
    {
        Collection<String> psiMiRefs = new ArrayList<String>();
        psiMiRefs.add(CvDatabase.BIND_MI_REF);
        psiMiRefs.add(CvDatabase.CABRI_MI_REF);
        psiMiRefs.add(CvDatabase.IMEX_MI_REF);


        IntactTransaction tx = DaoFactory.beginTransaction();
        CvObjectDao<CvDatabase> cvObjectDao = DaoFactory.getCvObjectDao(CvDatabase.class);

        List cvObjects = cvObjectDao.getByPsiMiRefCollection(psiMiRefs);
        assertEquals(cvObjects.size(),3);
        tx.commit();
    }
}

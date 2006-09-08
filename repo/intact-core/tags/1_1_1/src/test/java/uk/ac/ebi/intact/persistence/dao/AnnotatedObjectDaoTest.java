/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.CvTopic;
import junit.framework.TestCase;

import java.util.List;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class AnnotatedObjectDaoTest extends TestCase {

     private static final Log log = LogFactory.getLog(DaoFactoryTest.class);

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testGetByAnnotationAc()
    {
        IntactTransaction tx = DaoFactory.beginTransaction();
        AnnotatedObjectDao<Experiment> annotatedObjectDao = DaoFactory.getAnnotatedObjectDao(Experiment.class);
        List annotatedObjects = annotatedObjectDao.getByAnnotationAc("EBI-648094");
        assertEquals(annotatedObjects.size(),37);
        tx.commit();
    }

    public void testGetByAnnotationTopicAndDescription()
    {

        IntactTransaction tx = DaoFactory.beginTransaction();
        CvObjectDao<CvTopic> cvObjectDao = DaoFactory.getCvObjectDao(CvTopic.class);
        CvTopic cvTopic = cvObjectDao.getByAc("EBI-821310");
        AnnotatedObjectDao<Experiment> annotatedObjectDao = DaoFactory.getAnnotatedObjectDao(Experiment.class);
        List annotatedObjects = annotatedObjectDao.getByAnnotationTopicAndDescription(cvTopic, "Mouse cardiac cell library used for yeast two-hybrid screening.");
        assertEquals(annotatedObjects.size(),37);
        tx.commit();

    }

    public void testGetByShortlabelOrAcLike(){
        IntactTransaction tx = DaoFactory.beginTransaction();
        AnnotatedObjectDao<Experiment> annotatedObjectDao = DaoFactory.getAnnotatedObjectDao(Experiment.class);
        List annotatedObjects = annotatedObjectDao.getByShortlabelOrAcLike("butkevich-2004-%");
        assertEquals(annotatedObjects.size(),4);
        tx.commit();

    }
}

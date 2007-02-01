/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence.dao;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.CvDatabase;

import java.util.List;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class AnnotatedObjectDaoTest extends TestCase {

    private static final Log log = LogFactory.getLog(DaoFactoryTest.class);

    private DaoFactory daoFactory;

    protected void setUp() throws Exception
    {
        super.setUp();
        daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
        daoFactory = null;
    }

    public void testGetByAnnotationAc()
    {
        AnnotatedObjectDao<Experiment> annotatedObjectDao = daoFactory.getAnnotatedObjectDao(Experiment.class);
        List annotatedObjects = annotatedObjectDao.getByAnnotationAc("EBI-648094");
        assertEquals(annotatedObjects.size(),37);

        ExperimentDao experimentDao = daoFactory.getExperimentDao();
        annotatedObjects = experimentDao.getByAnnotationAc("EBI-648094");
        assertEquals(annotatedObjects.size(),37);
        
    }

    public void testGetByAnnotationTopicAndDescription()
    {
        CvObjectDao<CvTopic> cvObjectDao = daoFactory.getCvObjectDao(CvTopic.class);
        CvTopic cvTopic = cvObjectDao.getByAc("EBI-821310");
        AnnotatedObjectDao<Experiment> annotatedObjectDao = daoFactory.getAnnotatedObjectDao(Experiment.class);
        List annotatedObjects = annotatedObjectDao.getByAnnotationTopicAndDescription(cvTopic, "Mouse cardiac cell library used for yeast two-hybrid screening.");
        assertEquals(annotatedObjects.size(),37);
    }

    public void testGetByShortlabelOrAcLike(){
        AnnotatedObjectDao<Experiment> annotatedObjectDao = daoFactory.getAnnotatedObjectDao(Experiment.class);
        List annotatedObjects = annotatedObjectDao.getByShortlabelOrAcLike("butkevich-2004-%");
        assertEquals(annotatedObjects.size(),4);
    }

    public void testGetByXref(){
        AnnotatedObjectDao<Experiment> annotatedObjectDao = daoFactory.getAnnotatedObjectDao(Experiment.class);
        Experiment experiment = annotatedObjectDao.getByXref("10029528");
        assertNotNull(experiment);
    }

    public void testGetByXrefLike()
    {
        CvXrefQualifier qualifier = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvXrefQualifier.class, CvXrefQualifier.PRIMARY_REFERENCE_MI_REF);
        CvDatabase pubmed = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class, CvDatabase.PUBMED_MI_REF);
        AnnotatedObjectDao<Experiment> annotatedObjectDao = daoFactory.getAnnotatedObjectDao(Experiment.class);
        List annotatedObjects = annotatedObjectDao.getByXrefLike(pubmed, qualifier, "10029528" );
        assertEquals(annotatedObjects.size(),1);
    }
      
}

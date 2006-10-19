/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.persistence.dao.impl;

import junit.framework.TestCase;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.SearchableDao;
import uk.ac.ebi.intact.persistence.dao.query.SearchableQuery;
import uk.ac.ebi.intact.util.DebugUtil;

import java.util.List;
import java.util.Map;

/**
 * Test for <code>SearchableDaoImplTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 10/10/2006
 */
public class SearchableDaoImplTest extends TestCase
{
    public SearchableDaoImplTest(String name)
    {
        super(name);
    }

    private SearchableDao dao;

    public void setUp() throws Exception
    {
        super.setUp();
        dao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getSearchableDao();
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
        dao = null;
    }

    public void testCountByQuery_ac() throws Exception
    {
        SearchableQuery query = new SearchableQuery();
        query.setAc("EBI-12345");

        int count = dao.countByQuery(InteractorImpl.class, query);
        assertEquals(1, count);
    }

    public void testCountByQuery_all_standard() throws Exception
    {
        SearchableQuery query = new SearchableQuery();

        Map<Class<? extends Searchable>, Integer> counts = dao.countByQuery(query);
        assertEquals(5, counts.entrySet().size());


        for (Map.Entry<Class<? extends Searchable>,Integer> entry : counts.entrySet())
        {
            System.out.println(entry.getKey()+": "+entry.getValue());
            assertTrue(entry.getValue() > 0);
        }

    }

    public void testGetByQuery_fullText() throws Exception
    {
        SearchableQuery query = new SearchableQuery();
        query.setFullText("bruno");

        List<InteractorImpl> results = dao.getByQuery(InteractorImpl.class, query, 0, 50);

        assertEquals(2, results.size());
    }

    public void testGetByQuery_annotation() throws Exception
    {
        SearchableQuery query = new SearchableQuery();
        query.setAnnotationText("%subcellular%");

        List<InteractorImpl> results = dao.getByQuery(InteractorImpl.class, query, 0, 50);
        assertEquals(2, results.size());
        System.out.println(DebugUtil.acList(results));
    }

    public void testGetByQuery_annotation_and_topic() throws Exception
    {
        SearchableQuery query = new SearchableQuery();
        query.setCvTopicLabel(CvTopic.COMMENT);
        query.setAnnotationText("%y2h%");

        List<? extends Searchable> results = dao.getByQuery(ProteinImpl.class, query, 0, 50);

        assertEquals(24, results.size());
    }

    public void testGetByQuery_experiments_standard_disjunction() throws Exception
    {
        String search = "bruno%";

        SearchableQuery query = new SearchableQuery();
        query.setShortLabel(search);
        query.setDescription(search);
        query.setXref(search);
        query.setAc(search);
        query.setDisjunction(true);

        List<? extends Searchable> results = dao.getByQuery(Experiment.class, query, 0, 50);

        for (Searchable searchable : results)
        {
            System.out.println(((AnnotatedObject)searchable).getAc());
        }
          /*
        List<String> acs = dao.getAcsByQuery(Experiment.class, query, 0, 50);

        for (String ac : acs)
        {
            System.out.println(ac);
        }    */
    }

    public void testGetByQuery_std() throws Exception
    {
        SearchableQuery query = new SearchableQuery();

        List<? extends Searchable> results = dao.getByQuery(query, 0, 50);

        assertEquals(50, results.size());
    }

    public void testGetByQuery_cvInteraction_withChildren()
    {
        SearchableQuery query = new SearchableQuery();
        query.setCvInteractionLabel("biophysical");
        query.setIncludeCvInteractionChildren(true);

        int count = dao.countByQuery(Experiment.class, query);
        assertTrue(count > 700);

        List<Experiment> results = dao.getByQuery(Experiment.class, query, 0, 50);

        assertFalse(results.isEmpty());
        assertEquals(50, results.size());
    }

    public void testGetByQuery_cvInteraction_cvIdentification_withChildren()
    {
        SearchableQuery query = new SearchableQuery();
        query.setCvInteractionLabel("biophysical");
        query.setIncludeCvInteractionChildren(true);
        query.setCvIdentificationLabel("mass spectrometry");
        query.setIncludeCvIdentificationChildren(true);

        int count = dao.countByQuery(Experiment.class, query);
        assertTrue(count > 0);

        List<Experiment> results = dao.getByQuery(Experiment.class, query, 0, 50);

        assertFalse(results.isEmpty());
    }

    public void testCountByQuery_interaction_label() throws Exception
    {
        String search = "lsm7%";

        SearchableQuery query = new SearchableQuery();
        query.setDisjunction(true);
        query.setShortLabel(search);
        query.setXref(search);

        assertTrue(dao.countByQuery(InteractionImpl.class, query) >= 15);

    }

}

/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.experiment;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.util.SchemaUtils;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda
 * @version $Id$
 */
public class ExperimentOnHoldTest extends IntactBasicTestCase {

    @Before
    public void before() throws Exception {
        SchemaUtils.createSchema();
    }

    @After
    public void after() throws Exception {
        commitTransaction();
    }
    
    @Test
    public void check_notOnHold() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentEmpty();

        ExperimentOnHold rule = new ExperimentOnHold();
        beginTransaction();
        Collection<GeneralMessage> messages = rule.check(experiment);
        commitTransaction();

        assertEquals(0, messages.size());
    }
    
    @Test
    public void check_onHold() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentEmpty();
        Annotation annotation = getMockBuilder().createAnnotation("", "IA:0", CvTopic.ON_HOLD);
        experiment.addAnnotation(annotation);

        ExperimentOnHold rule = new ExperimentOnHold();
        beginTransaction();
        Collection<GeneralMessage> messages = rule.check(experiment);
        commitTransaction();

        assertEquals(1, messages.size());
    }

    @Test
    public void check_notOnHold_checkingPubmed() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentEmpty("lala-2005-1", "1234");

        PersisterHelper.saveOrUpdate(experiment);

        Experiment exp2 = getMockBuilder().createExperimentEmpty("br-2009-1", "4321");
        Annotation annotation = getMockBuilder().createAnnotation("", "IA:0", CvTopic.ON_HOLD);
        experiment.addAnnotation(annotation);

        PersisterHelper.saveOrUpdate(exp2);

        ExperimentOnHold rule = new ExperimentOnHold();
        beginTransaction();
        Experiment refreshedExp = getDaoFactory().getExperimentDao().getByShortLabel("lala-2005-1");
        Collection<GeneralMessage> messages = rule.check(refreshedExp);
        commitTransaction();

        assertEquals(0, messages.size());
    }

    @Test
    public void check_onHold_checkingPubmed() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentEmpty("lala-2005-1", "1234");

        PersisterHelper.saveOrUpdate(experiment);

        Experiment exp2 = getMockBuilder().createExperimentEmpty("lala-2005-4", "1234");
        Annotation annotation = getMockBuilder().createAnnotation("", "IA:0", CvTopic.ON_HOLD);
        exp2.addAnnotation(annotation);

        PersisterHelper.saveOrUpdate(exp2);

        ExperimentOnHold rule = new ExperimentOnHold();
        beginTransaction();
        Experiment refreshedExp = getDaoFactory().getExperimentDao().getByShortLabel("lala-2005-1");
        Collection<GeneralMessage> messages = rule.check(refreshedExp);
        commitTransaction();

        assertEquals(1, messages.size());
    }
}
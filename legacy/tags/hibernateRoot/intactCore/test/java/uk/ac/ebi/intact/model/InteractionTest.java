/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import junit.framework.JUnit4TestAdapter;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Assert;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-Mar-2006</pre>
 */
public class InteractionTest extends DataAccessTest
{
    private static final String I1_LABEL = "TestInteraction";
    private static final String I1_FULLNAME = "A test interaction";

    private static String DUMMY_BIOSOURCE_LABEL = "dummyOrganism_Inter";
    private static String DUMMY_EXPERIMENT_LABEL = "dummyExperiment_Int";

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(InteractionTest.class);
    }

    @BeforeClass
    public static void createDummyExperiment() throws IntactException
    {
        IntactHelper helper = new IntactHelper();

        BioSource organism = TestDataUtil.createBioSource(helper, DUMMY_BIOSOURCE_LABEL, "000");
        TestDataUtil.createExperiment(helper, DUMMY_EXPERIMENT_LABEL, organism);
    }

    @AfterClass
    public static void deleteDummyExperiment() throws IntactException
    {
        IntactHelper helper = new IntactHelper();

        TestDataUtil.deleteExperiment(helper, DUMMY_EXPERIMENT_LABEL);
        TestDataUtil.deleteBioSource(helper, DUMMY_BIOSOURCE_LABEL);
    }


    @Test
    public void interactionCreation() throws IntactException
    {
        Interaction interaction = getHelper().getObjectByLabel(Interaction.class, InteractionTest.I1_LABEL);
        Assert.assertNull(interaction);

        Experiment experiment = TestDataUtil.getExperiment(getHelper(), DUMMY_EXPERIMENT_LABEL);
        Assert.assertNotNull(experiment);

        List<Experiment> experiments = new ArrayList<Experiment>(1);
        experiments.add(experiment);

        List components = new ArrayList();
        CvInteractionType type =  getHelper().getObjectByLabel(CvInteractionType.class, "aggregation");
        CvInteractorType interactorType =  getHelper().getObjectByLabel(CvInteractorType.class, "protein" );

        interaction = new InteractionImpl(experiments, components, type, interactorType, I1_LABEL, getHelper().getInstitution());
        interaction.setFullName(I1_FULLNAME);

        getHelper().create(interaction);

        interaction =
                getHelper().getObjectByLabel(Interaction.class, I1_LABEL);
        Assert.assertNotNull(interaction);
    }

    @Test(expected = IllegalArgumentException.class)
    public void interactionCreationNoExperiments() throws IntactException
    {
        List<Experiment> experiments = Collections.EMPTY_LIST;

        List components = new ArrayList();
        CvInteractionType type =  getHelper().getObjectByLabel(CvInteractionType.class, "aggregation");
        CvInteractorType interactorType =  getHelper().getObjectByLabel(CvInteractorType.class, "protein" );

        Interaction interaction = new InteractionImpl(experiments, components, type, interactorType, "Interaction_noExp", getHelper().getInstitution());

        getHelper().create(interaction);
    }

    @Test
    public void interactionLoading() throws IntactException
    {
        Interaction interaction =  getHelper().getObjectByLabel(Interaction.class, I1_LABEL);

        Assert.assertNotNull(interaction);
        Assert.assertEquals(I1_FULLNAME, interaction.getFullName());
    }

    @Test
    public void interactionDelete() throws IntactException
    {
        Interaction interaction = getHelper().getObjectByLabel(Interaction.class, I1_LABEL);

        Assert.assertNotNull(interaction);

        getHelper().delete(interaction);

        Assert.assertNull(getHelper().getObjectByLabel(Interaction.class, I1_LABEL));
    }


}

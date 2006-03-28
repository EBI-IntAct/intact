/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-Mar-2006</pre>
 */
public class ExperimentTest extends DataAccessTest
{
    private static final String E1_LABEL = "TestExperiment";
    private static final String E1_FULLNAME = "A test experiment";

    private static String DUMMY_BIOSOURCE_LABEL = "dummyOrganism_Exp";

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ExperimentTest.class);
    }

    @BeforeClass
    public static void createDummyOrganism() throws IntactException
    {
        IntactHelper helper = new IntactHelper();
        TestDataUtil.createBioSource(helper, DUMMY_BIOSOURCE_LABEL, "99999");
    }

    @AfterClass
    public static void deleteDummyOrganism() throws IntactException
    {
        IntactHelper helper = new IntactHelper();
        TestDataUtil.deleteBioSource(helper, DUMMY_BIOSOURCE_LABEL);
    }


    @Test
    public void experimentCreation() throws IntactException
    {
        Experiment experiment =
                getHelper().getObjectByLabel(Experiment.class, E1_LABEL);
        Assert.assertNull(experiment);

        BioSource organism = TestDataUtil.getBioSource(getHelper(), DUMMY_BIOSOURCE_LABEL);
        Assert.assertNotNull(organism);

        experiment = new Experiment(getHelper().getInstitution(), E1_LABEL, organism);
        experiment.setFullName(ExperimentTest.E1_FULLNAME);

        getHelper().create(experiment);

        experiment =
                getHelper().getObjectByLabel(Experiment.class, E1_LABEL);
        Assert.assertNotNull(experiment);
    }

    @Test(expected = IntactException.class)
    public void experimentCreationBioSourceNotInDb() throws IntactException
    {
        BioSource notPersistentOrganism = new BioSource(getHelper().getInstitution(), "NotPersistentOrg", "0000");
        Experiment experiment = new Experiment(getHelper().getInstitution(), "ShouldFailExperiment", notPersistentOrganism);
        getHelper().create(experiment);
    }

    @Test
    public void experimentLoading() throws IntactException
    {
        Experiment experiment =
                getHelper().getObjectByLabel(Experiment.class, E1_LABEL);

        Assert.assertNotNull(experiment);
        Assert.assertEquals(ExperimentTest.E1_FULLNAME, experiment.getFullName());
    }

    @Test
    public void experimentDelete() throws IntactException
    {
        Experiment experiment =
                getHelper().getObjectByLabel(Experiment.class, E1_LABEL);

        Assert.assertNotNull(experiment);

        getHelper().delete(experiment);

        Assert.assertNull(getHelper().getObjectByLabel(Experiment.class, E1_LABEL));
    }


}

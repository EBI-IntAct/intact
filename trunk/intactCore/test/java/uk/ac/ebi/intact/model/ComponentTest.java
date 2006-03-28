/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import junit.framework.JUnit4TestAdapter;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Assert;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>22-Mar-2006</pre>
 */
public class ComponentTest extends DataAccessTest
{

    private static String DUMMY_BIOSOURCE_LABEL = "dummyOrganism_Com";
    private static String DUMMY_EXPERIMENT_LABEL = "dummyExp_Com";
    private static String DUMMY_INTERACTION_LABEL = "dummyInt_Com";
    private static String DUMMY_PROTEIN1_LABEL = "dummyProt1_Com";
    private static String DUMMY_PROTEIN2_LABEL = "dummyProt2_Com";

    private static String component1Ac;
    private static String component2Ac;

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ComponentTest.class);
    }

    @BeforeClass
    public static void createDummies() throws IntactException
    {
        IntactHelper helper = new IntactHelper();
        BioSource organism = TestDataUtil.createBioSource(helper, DUMMY_BIOSOURCE_LABEL, "000");

        TestDataUtil.createProtein(helper, organism, DUMMY_PROTEIN1_LABEL);
        TestDataUtil.createProtein(helper, organism, DUMMY_PROTEIN2_LABEL);

        Experiment experiment = TestDataUtil.createExperiment(helper,DUMMY_EXPERIMENT_LABEL, organism);
        TestDataUtil.createInteraction(helper,experiment, DUMMY_INTERACTION_LABEL);
    }

    @AfterClass
    public static void deleteDummies() throws IntactException
    {
        IntactHelper helper = new IntactHelper();

        TestDataUtil.deleteInteraction(helper, DUMMY_INTERACTION_LABEL);
        TestDataUtil.deleteExperiment(helper, DUMMY_EXPERIMENT_LABEL);
        TestDataUtil.deleteProtein(helper, DUMMY_PROTEIN1_LABEL);
        TestDataUtil.deleteProtein(helper, DUMMY_PROTEIN2_LABEL);
        TestDataUtil.deleteBioSource(helper, DUMMY_BIOSOURCE_LABEL);

    }

    @Test
    public void componentCreation() throws IntactException
    {
        Interaction interaction = TestDataUtil.getInteraction(getHelper(), DUMMY_INTERACTION_LABEL);
        Assert.assertNotNull(interaction);

        Protein protein1 = TestDataUtil.getProtein(getHelper(), DUMMY_PROTEIN1_LABEL);
        Protein protein2 = TestDataUtil.getProtein(getHelper(), DUMMY_PROTEIN2_LABEL);
        Assert.assertNotNull(protein1);
        Assert.assertNotNull(protein2);
        Assert.assertNotSame(protein1, protein2);

        BioSource organism = TestDataUtil.getBioSource(getHelper(), DUMMY_BIOSOURCE_LABEL);
        Assert.assertNotNull(organism);

        CvComponentRole bait = getHelper().getObjectByLabel(CvComponentRole.class, "bait");
        CvComponentRole prey = getHelper().getObjectByLabel(CvComponentRole.class, "prey");

        Component component1 = new Component(getHelper().getInstitution(), interaction, protein1, prey);
        Component component2 = new Component(getHelper().getInstitution(), interaction, protein2, bait);

        Assert.assertNull(component1.getAc());
        Assert.assertNull(component2.getAc());

        getHelper().create(component1);
        getHelper().create(component2);

        component1Ac = component1.getAc();
        component2Ac = component2.getAc();

        Assert.assertNotNull(component1Ac);
        Assert.assertNotNull(component2Ac);
        Assert.assertNotSame(component1Ac, component2Ac);

        Component loadedComp = getHelper().getObjectByAc(Component.class, component1Ac);
        Assert.assertNotNull(loadedComp);

        Assert.assertEquals(loadedComp, component1);
    }

    @Test
    public void deleteComponent() throws IntactException
    {
        Assert.assertNotNull(component1Ac);
        Assert.assertNotNull(component2Ac);
        Assert.assertNotSame(component1Ac, component2Ac);

        Component comp1 = getHelper().getObjectByAc(Component.class, component1Ac);
        Component comp2 = getHelper().getObjectByAc(Component.class, component2Ac);

        Assert.assertNotNull(comp1);
        Assert.assertNotNull(comp2);
        Assert.assertNotSame(comp1, comp2);


    }

}

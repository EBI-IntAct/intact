package uk.ac.ebi.intact.core.unit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.XrefUtils;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactMockBuilderTest
{
    private IntactMockBuilder mockBuilder;

    @Before
    public void before() {
        mockBuilder = new IntactMockBuilder();
    }

    @After
    public void after() {
        mockBuilder = null;
    }

    @Test
    public void createInstitution() throws Exception {
        Institution institution = mockBuilder.createInstitution(Institution.MINT_REF, Institution.MINT);

        InstitutionXref xref = XrefUtils.getPsiMiIdentityXref(institution);
        String primaryId = xref.getPrimaryId();

        Assert.assertEquals(Institution.MINT_REF, primaryId);

        Assert.assertNotNull(xref.getOwner());
        Assert.assertNotNull(xref.getCvDatabase().getOwner());
        Assert.assertNotNull(xref.getCvXrefQualifier().getOwner());
    }

    @Test
    public void randomString_default() throws Exception {
        String randomString = mockBuilder.randomString(10);
        Assert.assertNotNull(randomString);
        Assert.assertEquals(10, randomString.length());
    }

    @Test
    public void createInteractionRandomBinary() throws Exception {
        Interaction interaction = mockBuilder.createInteractionRandomBinary();

        Assert.assertNotNull(interaction);
        Assert.assertNotNull(interaction.getShortLabel());
        Assert.assertEquals(2, interaction.getComponents().size());
    }

    @Test
    public void createProteinRandom() throws Exception {
        Protein protein = mockBuilder.createProteinRandom();

        Assert.assertNotNull(protein);
        Assert.assertNotNull(protein.getSequence());
        Assert.assertFalse(protein.getXrefs().isEmpty());
        Assert.assertNotNull(protein.getCrc64());
    }

    @Test
    public void createBioSourceRandom() throws Exception {
        BioSource bioSource = mockBuilder.createBioSourceRandom();

        Assert.assertNotNull(bioSource);

        String taxId = bioSource.getTaxId();
        
        Assert.assertNotNull(taxId);
        Assert.assertFalse(bioSource.getXrefs().isEmpty());
        Assert.assertEquals(taxId, bioSource.getXrefs().iterator().next().getPrimaryId());
    }

    @Test
    public void createComponentRandom() throws Exception {
        Component component = mockBuilder.createComponentRandom();

        Assert.assertNotNull(component.getCvExperimentalRole());
        Assert.assertNotNull(component.getCvBiologicalRole());
        Assert.assertFalse(component.getParticipantDetectionMethods().isEmpty());
        Assert.assertFalse(component.getExperimentalPreparations().isEmpty());
        
        for (CvExperimentalPreparation experimentalPrep : component.getExperimentalPreparations()) {
            Assert.assertNotNull(experimentalPrep.getOwner());
        }

    }
}

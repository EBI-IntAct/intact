package uk.ac.ebi.intact.model.util;

import static org.junit.Assert.*;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactAbstractTestCase;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.model.CvBiologicalRole;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvExperimentalRole;
import uk.ac.ebi.intact.model.CvObjectXref;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;

@IntactUnitDataset(dataset = PsiTestDatasetProvider.ALL_CVS, provider = PsiTestDatasetProvider.class)
public class CvObjectUtilsTest extends IntactAbstractTestCase {

    @Test
    public void testGetPsiMiIdentityXref() throws Exception{
        assertFalse(0 == getDaoFactory().getCvObjectDao().countAll());

        CvDatabase uniprotKb = getIntactContext().getCvContext().getByMiRef(CvDatabase.class,CvDatabase.UNIPROT_MI_REF);

        CvObjectXref cvObjectXref = CvObjectUtils.getPsiMiIdentityXref(uniprotKb);
        if(cvObjectXref == null){
            fail("The xref retuned should not be null");
        }
        assertEquals(CvDatabase.UNIPROT_MI_REF,cvObjectXref.getPrimaryId());
    }

    @Test
    public void createRoleInfo_relevant() throws Exception {

        String labelUnspecified = CvExperimentalRole.UNSPECIFIED;
        String miUnspecified = CvExperimentalRole.UNSPECIFIED_PSI_REF;
        String labelExpDefined = CvExperimentalRole.BAIT;
        String miExpDefined = CvExperimentalRole.BAIT_PSI_REF;
        String labelBioDefined = CvBiologicalRole.ENZYME;
        String miBioDefined = CvBiologicalRole.ENZYME_PSI_REF;

        CvExperimentalRole expUnspecified = getIntactContext().getCvContext().getByMiRef(CvExperimentalRole.class, miUnspecified);
        CvExperimentalRole expDefined = getIntactContext().getCvContext().getByMiRef(CvExperimentalRole.class, miExpDefined);
        CvBiologicalRole bioUnspecified = getIntactContext().getCvContext().getByMiRef(CvBiologicalRole.class, miUnspecified);
        CvBiologicalRole bioDefined = getIntactContext().getCvContext().getByMiRef(CvBiologicalRole.class,  miBioDefined);

        assertNotNull(expUnspecified);
        assertNotNull(expDefined);
        assertNotNull(bioDefined);
        assertNotNull(expDefined);

        // case 1: exp unspecified - bio neutral
        RoleInfo roleInfo1 = CvObjectUtils.createRoleInfo(expUnspecified, bioUnspecified);
        assertEquals(labelUnspecified, roleInfo1.getRelevantName());
        assertEquals(miUnspecified, roleInfo1.getRelevantMi());

        // case 2: exp unspecified - bio defined
        RoleInfo roleInfo2 = CvObjectUtils.createRoleInfo(expUnspecified, bioDefined);
        assertEquals(labelBioDefined, roleInfo2.getRelevantName());
        assertEquals(miBioDefined, roleInfo2.getRelevantMi());

        // case 3: exp defined - bio neutral
        RoleInfo roleInfo3 = CvObjectUtils.createRoleInfo(expDefined, bioUnspecified);
        assertEquals(labelExpDefined, roleInfo3.getRelevantName());
        assertEquals(miExpDefined, roleInfo3.getRelevantMi());

        // case 4: exp defined - bio defined
        String separator = RoleInfo.RELEVANT_SEPARATOR;
        RoleInfo roleInfo4 = CvObjectUtils.createRoleInfo(expDefined, bioDefined);
        assertEquals(labelExpDefined+separator+labelBioDefined, roleInfo4.getRelevantName());
        assertEquals(miExpDefined+separator+miBioDefined, roleInfo4.getRelevantMi());
    }
}

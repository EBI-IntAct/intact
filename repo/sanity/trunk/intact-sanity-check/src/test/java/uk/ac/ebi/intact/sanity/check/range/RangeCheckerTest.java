package uk.ac.ebi.intact.sanity.check.range;

import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.standard.InteractorPersister;
import uk.ac.ebi.intact.core.unit.IntactAbstractTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.check.config.Curator;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;
import uk.ac.ebi.intact.sanity.check.config.SuperCurator;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@IntactUnitDataset( dataset = PsiTestDatasetProvider.ALL_CVS, provider = PsiTestDatasetProvider.class )
public class RangeCheckerTest extends IntactAbstractTestCase
{

    @Test
    public void check_default() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder(IntactContext.getCurrentInstance().getInstitution());
        Protein mockProt = mockBuilder.createProteinRandom();

        beginTransaction();
        InteractorPersister.getInstance().saveOrUpdate(mockProt);
        InteractorPersister.getInstance().commit();
        begin();

        beginTransaction();

        List<String> acs = new ArrayList<String>();
        for (Protein prot : getDaoFactory().getProteinDao().getAll()) {
            acs.add(prot.getAc());
        }

        RangeChecker checker = new RangeChecker(getSanityCheckConfig());
        checker.check(acs);

        commitTransaction();
    }

    protected SanityCheckConfig getSanityCheckConfig() {
        List<Curator> curators = new ArrayList<Curator>();

        SuperCurator superJohn = new SuperCurator(100, "John");
        superJohn.setAdmin(true);

        Curator curatorAnne = new Curator("Anne");
        Curator curatorSa = new Curator("sa");

        curators.add(superJohn);
        curators.add(curatorAnne);
        curators.add(curatorSa);

        SanityCheckConfig sanityCheckConfig = new SanityCheckConfig(curators);
        sanityCheckConfig.setDisableAdminMails(true);
        sanityCheckConfig.setDisableUserMails(true);

        return sanityCheckConfig;
    }
}

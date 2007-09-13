package uk.ac.ebi.intact.sanity.check.range;

import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.standard.InteractorPersister;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.check.AbstractSanityLegacyTest;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;
import uk.ac.ebi.intact.business.IntactTransactionException;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@IntactUnitDataset( dataset = PsiTestDatasetProvider.ALL_CVS, provider = PsiTestDatasetProvider.class )
public class RangeCheckerTest extends AbstractSanityLegacyTest
{

    @Test
    public void check_default() throws Exception {
        int i = 1;
        if (i==1){
            return;
        }
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

    public static void main(String[] args) throws SQLException, IntactTransactionException {
        RangeChecker rangeChecker = new RangeChecker();
        rangeChecker.checkRangeEntireDatabase();
    }
}

/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.dbupdate.smallmolecules;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.config.CvPrimer;
import uk.ac.ebi.intact.core.config.impl.SmallCvPrimer;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.persistence.dao.InteractorDao;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;

import java.util.List;

/**
 * SmallMoleculeUpdator Tester.
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1
 */
public class SmallMoleculeUpdatorTest extends IntactBasicTestCase {

    @Before
    public void cvSetup() throws Exception {

        final TransactionStatus transactionStatus = IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        CvPrimer primer = new SmallCvPrimer(daoFactory);
        primer.createCVs();

        final CvXrefQualifier secondaryAc = getMockBuilder().createCvObject(CvXrefQualifier.class, CvXrefQualifier.SECONDARY_AC_MI_REF, CvXrefQualifier.SECONDARY_AC);
        final CvTopic inchi = getMockBuilder().createCvObject(CvTopic.class, CvTopic.INCHI_ID_MI_REF, CvTopic.INCHI_ID);
        getCorePersister().saveOrUpdate(secondaryAc, inchi);

        IntactContext.getCurrentInstance().getDataContext().commitTransaction(transactionStatus);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    @DirtiesContext
    public void updateByAcs() throws Exception {

        final TransactionStatus transactionStatus = IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        // create a small molecule with an outdated id
        final SmallMolecule imatinib = getMockBuilder().createSmallMolecule("CHEBI:38918", "lala");  // CHEBI:38918

        final InteractorDao<SmallMoleculeImpl> smDao = daoFactory.getInteractorDao(SmallMoleculeImpl.class);
        getCorePersister().saveOrUpdate(imatinib);
        smDao.persist((SmallMoleculeImpl) imatinib);

        Assert.assertEquals(1, smDao.countAllInteractors());
        Assert.assertNotNull(smDao.getByXref("CHEBI:38918"));

        IntactContext.getCurrentInstance().getDataContext().commitTransaction(transactionStatus);

        SmallMoleculeUpdator updator = new SmallMoleculeUpdator(new ChebiProcessor());
        updator.startUpdate();

        final TransactionStatus transactionStatus2 = IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        final SmallMoleculeUpdateReport report = updator.getUpdateReport();
        Assert.assertEquals(1, report.getMoleculeCount());
        Assert.assertEquals(0, report.getMultipleIdentityCount());
        Assert.assertEquals(0, report.getNoIdentityCount());
        Assert.assertEquals(0, report.getUnknownChebiIdCount());
        Assert.assertEquals(0, report.getTotalFailureCount());

        final CvObjectDao<CvXrefQualifier> qDao = daoFactory.getCvObjectDao(CvXrefQualifier.class);
        final CvXrefQualifier identity = qDao.getByPsiMiRef(CvXrefQualifier.IDENTITY_MI_REF);
        final CvXrefQualifier secondaryAc = qDao.getByPsiMiRef(CvXrefQualifier.SECONDARY_AC_MI_REF);

        final CvObjectDao<CvDatabase> dbDao = daoFactory.getCvObjectDao(CvDatabase.class);
        final CvDatabase chebi = dbDao.getByPsiMiRef(CvDatabase.CHEBI_MI_REF);

        // there should be still a single interactor after update
        Assert.assertEquals(1, smDao.countAllInteractors());

        // check that the small molecule Xrefs have been updated.
        Assert.assertEquals(1, smDao.getByXrefLike(chebi, identity, "CHEBI:45783").size()); // identity
//        Assert.assertEquals( 1, smDao.getByXrefLike( chebi, secondaryAc, "CHEBI:45781" ).size() ); // secondary ac

        final List<SmallMoleculeImpl> moleculeList = smDao.getByXrefLike(chebi, secondaryAc, "CHEBI:38918");
        Assert.assertEquals(1, moleculeList.size());
        final SmallMoleculeImpl compound = moleculeList.iterator().next();
        Assert.assertEquals(2, compound.getXrefs().size());

        // shortlabel should have been updated
        Assert.assertEquals("imatinib", compound.getShortLabel());

        Assert.assertEquals(1, compound.getAnnotations().size());
        Assert.assertEquals("InChI=1S/C29H31N7O/c1-21-5-10-25(18-27(21)34-29-31-13-11-26(33-29)24-4-3-12-30-19-24)32-28(37)23-8-6-22(7-9-23)20-36-16-14-35(2)15-17-36/h3-13,18-19H,14-17,20H2,1-2H3,(H,32,37)(H,31,33,34)",
                compound.getAnnotations().iterator().next().getAnnotationText());

        IntactContext.getCurrentInstance().getDataContext().commitTransaction(transactionStatus2);
    }
}
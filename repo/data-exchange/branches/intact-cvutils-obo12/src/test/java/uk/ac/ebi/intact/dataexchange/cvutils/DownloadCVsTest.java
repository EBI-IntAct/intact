/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.dataexchange.cvutils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.config.CvPrimer;
import uk.ac.ebi.intact.config.impl.SmallCvPrimer;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactUnit;
import uk.ac.ebi.intact.dataexchange.cvutils.model.IntactOntology;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.FileWriter;

/**
 * DownloadCVs tester.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class DownloadCVsTest extends IntactBasicTestCase {

    @Before
    public void prepare() throws Exception {
        IntactContext.getCurrentInstance().getConfig().setReadOnlyApp(false);

        new IntactUnit().createSchema();

        CvPrimer cvPrimer = new DownloadCvPrimer(getDaoFactory());
        cvPrimer.createCVs();

        beginTransaction();
    }

    @After
    public void after() throws Exception {
        commitTransaction();
    }

    @Test
    public void download_default() throws Exception {

        StringWriter writer = new StringWriter();
        BufferedWriter bufWriter = new BufferedWriter(writer);

        DownloadCVs downloadCVs = new DownloadCVs();
        downloadCVs.download(bufWriter);

        bufWriter.flush();

        String oboOutput = writer.toString();

        System.out.println( oboOutput );

        commitTransaction();

        beginTransaction();

        Assert.assertEquals(24, getDaoFactory().getCvObjectDao().countAll());

        for (CvObject cv : getDaoFactory().getCvObjectDao().getAll()) {
            Assert.assertFalse(cv.getXrefs().isEmpty());
        }

        commitTransaction();
        
        PSILoader loader = new PSILoader();
        IntactOntology ontology = loader.parseOboFile(new ByteArrayInputStream(oboOutput.getBytes()));

        Assert.assertEquals(33, ontology.getCvTerms().size());
    }

    @Test
    public void download_unexpectedCV() throws Exception {
        CvDatabase wrongDb = getMockBuilder().createCvObject(CvDatabase.class, "no", "uniprot");
        wrongDb.getXrefs().clear();

        PersisterHelper.saveOrUpdate(wrongDb);
        
        // --- testing

        StringWriter writer = new StringWriter();
        BufferedWriter bufWriter = new BufferedWriter(writer);

        beginTransaction();

        DownloadCVs downloadCVs = new DownloadCVs();
        downloadCVs.download(bufWriter);

        bufWriter.flush();

        String oboOutput = writer.toString();

        commitTransaction();


        //testing by prem
        //BufferedWriter outputStream = new BufferedWriter(new FileWriter("characteroutput.txt"));
        //outputStream.write(oboOutput);
       // outputStream.close();


        Assert.assertEquals(25, getDaoFactory().getCvObjectDao().countAll());


        PSILoader loader = new PSILoader();
        IntactOntology ontology = loader.parseOboFile(new ByteArrayInputStream(oboOutput.getBytes()));

        Assert.assertEquals(34, ontology.getCvTerms().size());
    }

    @Test (expected = IntactException.class)
    public void download_readOnly_someCvsNotExisting() throws Exception {
        IntactContext.getCurrentInstance().getConfig().setReadOnlyApp(true);

        StringWriter writer = new StringWriter();
        BufferedWriter bufWriter = new BufferedWriter(writer);

        beginTransaction();

        DownloadCVs downloadCVs = new DownloadCVs();
        downloadCVs.download(bufWriter);
    }
    
    @Test (expected = IntactException.class)
    public void download_readOnly_someCvsWithoutXrefs() throws Exception {
        CvDatabase wrongDb = getMockBuilder().createCvObject(CvDatabase.class, "no", "uniprot");
        wrongDb.getXrefs().clear();

        PersisterHelper.saveOrUpdate(wrongDb);

        // --- testing

        IntactContext.getCurrentInstance().getConfig().setReadOnlyApp(true);

        StringWriter writer = new StringWriter();
        BufferedWriter bufWriter = new BufferedWriter(writer);

        beginTransaction();

        DownloadCVs downloadCVs = new DownloadCVs();
        downloadCVs.download(bufWriter);
    }


    private class DownloadCvPrimer extends SmallCvPrimer {

        public DownloadCvPrimer(DaoFactory daoFactory) {
            super(daoFactory);
        }

        @Override
        public void createCVs() {
//            // this first block can be removed when using intact-core > 1.6.3-SNAPSHOT
//            beginTransaction();
//            try {
//                CvObjectPersister.getInstance().saveOrUpdate(getMockBuilder().createCvObject(CvXrefQualifier.class, CvXrefQualifier.GO_DEFINITION_REF_MI_REF, CvXrefQualifier.GO_DEFINITION_REF));
//                CvObjectPersister.getInstance().commit();
//            } catch (PersisterException e) {
//                e.printStackTrace();
//            }
//            commitTransaction();
//            // end of possible remove
            
            beginTransaction();

            // create the default CVs from the SmallCvPrimer
            super.createCVs();

            commitTransaction();

            // create additional CVs needed by DownloadCVs
            CvObject definition = CvObjectUtils.createCvObject(getIntactContext().getInstitution(), CvTopic.class, "IA:0241", CvTopic.DEFINITION);
            CvExperimentalRole expRoleUnspecified = CvObjectUtils.createCvObject(getIntactContext().getInstitution(), CvExperimentalRole.class,
                                                                      CvExperimentalRole.UNSPECIFIED_PSI_REF, CvExperimentalRole.UNSPECIFIED);
            CvBiologicalRole bioRoleUnspecified = CvObjectUtils.createCvObject(getIntactContext().getInstitution(), CvBiologicalRole.class,
                                                                      CvBiologicalRole.UNSPECIFIED_PSI_REF, CvBiologicalRole.UNSPECIFIED);
            CvExperimentalPreparation endogeneous = CvObjectUtils.createCvObject(getIntactContext().getInstitution(), CvExperimentalPreparation.class,
                                                                           CvExperimentalPreparation.ENDOGENOUS_LEVEL_REF, CvExperimentalPreparation.ENDOGENOUS_LEVEL);
            CvExperimentalPreparation purified = CvObjectUtils.createCvObject(getIntactContext().getInstitution(), CvExperimentalPreparation.class,
                                                                           CvExperimentalPreparation.PURIFIED_REF, CvExperimentalPreparation.PURIFIED);

            try {
                PersisterHelper.saveOrUpdate( endogeneous, purified, definition, expRoleUnspecified, bioRoleUnspecified);
            } catch (PersisterException e) {
                e.printStackTrace();
            }
        }
    }
}
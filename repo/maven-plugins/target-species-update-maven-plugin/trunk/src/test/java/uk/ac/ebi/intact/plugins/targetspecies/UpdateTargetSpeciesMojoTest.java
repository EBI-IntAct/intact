/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.plugins.targetspecies;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.lang.reflect.Method;

import uk.ac.ebi.intact.core.unit.*;
import uk.ac.ebi.intact.core.unit.mock.MockIntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.CvContext;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.persistence.dao.ExperimentDao;
import uk.ac.ebi.intact.persistence.dao.XrefDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;
import uk.ac.ebi.intact.commons.dataset.TestDataset;
import uk.ac.ebi.intact.commons.dataset.DbUnitTestDataset;
import uk.ac.ebi.intact.commons.dataset.TestDatasetProvider;

@RunWith(IntactTestRunner.class)
public class UpdateTargetSpeciesMojoTest extends AbstractMojoTestCase  {

     @Test
     @IntactUnitDataset( dataset = PsiTestDatasetProvider.INTACT_JUL_06,
                         provider = PsiTestDatasetProvider.class )
     public void export() throws Exception {

         // my test using the data here

     }

    public void testSimpleGeneration() throws Exception {
        CvObjectDao cvObjectDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao();
        Institution institution = IntactContext.getCurrentInstance().getInstitution();
        CvTopic noUniprotUpdate = new CvTopic(institution, "no-uniprot-update");
        cvObjectDao.saveOrUpdate(noUniprotUpdate);

        System.out.println("\n\n\nHello\n\n\n");

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/target-species-config.xml" );

        UpdateTargetSpeciesMojo mojo = (UpdateTargetSpeciesMojo) lookupMojo( "target-species", pluginXmlFile );
        mojo.setDryRun(false);
        mojo.setLog( new SystemStreamLog() );


        // MAKE SURE WE DELETE ALL THE XREF TARGET SPECIES IF ANY
        ExperimentDao expDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao();
        Collection<Experiment> experiments = expDao.getAll();
        CvContext cvContext = IntactContext.getCurrentInstance().getCvContext();
        CvXrefQualifier targetSpecies = cvContext.getByLabel(CvXrefQualifier.class, CvXrefQualifier.TARGET_SPECIES);
        CvDatabase newt = cvContext.getByMiRef(CvDatabase.class, CvDatabase.NEWT_MI_REF);
        XrefDao<ExperimentXref> xrefDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getXrefDao(ExperimentXref.class);
        for(Experiment exp : experiments){
            Collection<ExperimentXref> experimentXrefs = exp.getXrefs();
            for (Iterator<ExperimentXref> iterator = experimentXrefs.iterator(); iterator.hasNext();) {
                ExperimentXref experimentXref =  iterator.next();
                if(newt.equals(experimentXref.getCvDatabase()) && targetSpecies.equals(experimentXref.getCvXrefQualifier())){

                    experimentXref.setParent(null);
                    xrefDao.delete(experimentXref);
                    iterator.remove();
                }
            }
            ExperimentDao experimentDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao();
            experimentDao.saveOrUpdate(exp);
        }
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        //EXECUTE THE MOJO
        mojo.executeIntactMojo();

        //CHECK THAT THE XREF TARGET SPECIES ARE BACK
        expDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao();
        experiments = expDao.getAll();
        cvContext = IntactContext.getCurrentInstance().getCvContext();
        targetSpecies = cvContext.getByLabel(CvXrefQualifier.class, CvXrefQualifier.TARGET_SPECIES);
        newt = cvContext.getByMiRef(CvDatabase.class, CvDatabase.NEWT_MI_REF);
        xrefDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getXrefDao(ExperimentXref.class);
        int xrefCount = 0;
        assertTrue(experiments.size()>0);
        for(Experiment exp : experiments){
            Collection<ExperimentXref> experimentXrefs = exp.getXrefs();
            for (Iterator<ExperimentXref> iterator = experimentXrefs.iterator(); iterator.hasNext();) {
                ExperimentXref experimentXref =  iterator.next();
                if(newt.equals(experimentXref.getCvDatabase()) && targetSpecies.equals(experimentXref.getCvXrefQualifier())){
                    xrefCount++;
                }
            }
            ExperimentDao experimentDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao();
            experimentDao.saveOrUpdate(exp);
        }
        System.out.println("XREF COUNT IS " + xrefCount);
        assertTrue(xrefCount > 0);

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();


    }

    @Before
    public final void setUp() throws Exception {
        super.setUp();

        //getDataContext().beginTransaction();

        Method currentMethod = IntactTestRunner.getTestMethod();

        if (currentMethod == null) {
            throw new RuntimeException("This test cannot be run in IDEA");
        }

        IntactUnit iu = new IntactUnit();

        IgnoreDatabase ignoreDbAnnot = currentMethod.getAnnotation(IgnoreDatabase.class);

        IntactUnitDataset datasetAnnot = null;
        boolean loadDataset = false;

        // if the ignoreDbAnnot is present at method level, ignore the db, else
        if (ignoreDbAnnot == null) {
            // try to get the IntactUnitDataset annotation from the method, and then the class
            datasetAnnot = currentMethod.getAnnotation(IntactUnitDataset.class);

            if (datasetAnnot == null) {
                ignoreDbAnnot = currentMethod.getDeclaringClass().getAnnotation(IgnoreDatabase.class);

                if (ignoreDbAnnot == null) {
                    datasetAnnot = currentMethod.getDeclaringClass().getAnnotation(IntactUnitDataset.class);

                    if (datasetAnnot != null) {
                        loadDataset = true;
                    } else {
                        iu.createSchema();
                    }
                }
            } else {
                loadDataset = true;
            }
        }

        if (loadDataset) {
            // ensure no MockIntactContext is active
            if (getIntactContext() instanceof MockIntactContext) {
                getIntactContext().close();
            }

            if (datasetAnnot != null) {
                TestDataset testDataset = getTestDataset(datasetAnnot);

                if (testDataset instanceof DbUnitTestDataset) {
                    iu.createSchema(false);
                    getDataContext().beginTransaction();
                    iu.importTestDataset((DbUnitTestDataset) testDataset);
                } else {
                    throw new IntactTestException("Cannot import TestDatasets of type: " + testDataset.getClass().getName());
                }

                getDataContext().commitTransaction();
            } else {
                iu.createSchema();
            }
            getDataContext().commitTransaction();
        }

        getDataContext().beginTransaction();
    }

    private TestDataset getTestDataset(IntactUnitDataset datasetAnnot) throws Exception {
        TestDatasetProvider provider = datasetAnnot.provider().newInstance();
        return provider.getTestDataset(datasetAnnot.dataset());
    }

    @After
    public final void tearDown() throws Exception {
        getDataContext().commitTransaction();

        if (IntactContext.currentInstanceExists()) {
            IntactContext.getCurrentInstance().close();
        }
    }
    protected IntactContext getIntactContext() {
        return IntactContext.getCurrentInstance();
    }

    protected DataContext getDataContext() {
        return getIntactContext().getDataContext();
    }

    protected DaoFactory getDaoFactory() {
        return getDataContext().getDaoFactory();
    }

}


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

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.CvContext;
import uk.ac.ebi.intact.persistence.dao.ExperimentDao;
import uk.ac.ebi.intact.persistence.dao.XrefDao;

public class UpdateTargetSpeciesMojoTest extends AbstractMojoTestCase
{

    public void testSimpleGeneration() throws Exception {



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
        assertTrue(xrefCount > 0);
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();


    }
}

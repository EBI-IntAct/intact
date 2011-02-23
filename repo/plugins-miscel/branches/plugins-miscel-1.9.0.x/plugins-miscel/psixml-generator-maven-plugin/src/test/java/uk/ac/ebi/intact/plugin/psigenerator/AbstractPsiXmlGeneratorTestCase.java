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
package uk.ac.ebi.intact.plugin.psigenerator;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.BeforeClass;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.io.File;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractPsiXmlGeneratorTestCase extends AbstractMojoTestCase {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(AbstractPsiXmlGeneratorTestCase.class);

    @BeforeClass
    public static void removeDatabaseFromTargetDir() {
        // test-db as in the database name configured in test-hibernate.cfg.xml
        File db = new File( getTargetDirectory(), "test-db" );
        if ( db.exists() ) {
            log.info( "Deleting database directory: " + db.getAbsolutePath() );
            db.delete();
        }
    }

    protected void initializeDatabaseContent( File hibernateConfigFile ) throws PersisterException, IntactTransactionException {
        // adding mock data
        IntactContext.initStandaloneContext( hibernateConfigFile );

        // init CVs
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        PsiXmlCvPrimer primer = new PsiXmlCvPrimer( daoFactory );
        primer.createCVs();
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        for ( int i = 0; i < 3; i++ ) {
            Experiment exp = mockBuilder.createExperimentRandom( 3 );
            PersisterHelper.saveOrUpdate( exp );
        }

        IntactContext.getCurrentInstance().close();
    }

    private static File getTargetDirectory() {
        String outputDirPath = AbstractPsiXmlGeneratorTestCase.class.getResource( "/" ).getFile();
        Assert.assertNotNull( outputDirPath );
        File outputDir = new File( outputDirPath );
        // we are in test-classes, move one up
        outputDir = outputDir.getParentFile();
        Assert.assertNotNull( outputDir );
        Assert.assertTrue( outputDir.isDirectory() );
        Assert.assertEquals( "target", outputDir.getName() );
        return outputDir;
    }

}
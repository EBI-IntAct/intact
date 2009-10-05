/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.sanity.plugin;

import junit.framework.Assert;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Before;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.business.IntactTransactionException;

import java.io.File;

/**
 * Set of methods shared by sanity check tests.
 *
 * @author Samuel Kerrien
 * @version $Id$
 * @since 2.0
 */
public abstract class AbstractSanityMojoTestCase extends AbstractMojoTestCase {

    @Before
    public void removeDatabaseFromTargetDir() {
        // test-db as in the database name configured in test-hibernate.cfg.xml
        File db = new File( getTargetDirectory(), "test-db" );
        if ( db.exists() ) {
            System.out.println( "Deleting database directory: " + db.getAbsolutePath() );
            db.delete();
        }
    }

    protected void initializeDatabaseContent( File hibernateConfigFile ) throws PersisterException, IntactTransactionException {
        // adding mock data
        IntactContext.initStandaloneContext( hibernateConfigFile );

        // init CVs
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        SanityCheckCvPrimer primer = new SanityCheckCvPrimer( daoFactory );
        primer.createCVs();
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        for ( int i = 0; i < 3; i++ ) {
            Experiment exp = mockBuilder.createExperimentRandom( 3 );
            PersisterHelper.saveOrUpdate( exp );
        }

        IntactContext.getCurrentInstance().close();
    }

    private File getTargetDirectory() {
        String outputDirPath = SanityCheckMojoTest.class.getResource( "/" ).getFile();
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
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
package uk.ac.ebi.intact.plugins.fasta;

import junit.framework.Assert;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.util.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FastaExportMojoTest extends AbstractMojoTestCase {

    @Test
    public void testSimpleGeneration() throws Exception {
        File hibernateConfig = new File( FastaExportMojoTest.class.getResource( "/test-hibernate.cfg.xml" ).getFile() );
        IntactContext.initStandaloneContext( hibernateConfig );

        IntactMockBuilder mockBuilder = new IntactMockBuilder();

        PersisterHelper.saveOrUpdate( mockBuilder.createInteractionRandomBinary() );

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        Assert.assertEquals( 1, daoFactory.getInteractionDao().getAll().size() );

        // protein without interactor
        final Protein p1 = mockBuilder.createProteinRandom();
        PersisterHelper.saveOrUpdate( p1 );

        // no sequence
        final Protein p2 = mockBuilder.createProteinRandom();
        p2.setSequence( null );
        PersisterHelper.saveOrUpdate( p2 );

        // empty sequence
        final Protein p3 = mockBuilder.createProteinRandom();
        p3.setSequence( " " );
        PersisterHelper.saveOrUpdate( p3 );

        Assert.assertEquals( 5, daoFactory.getProteinDao().getAll().size() );

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/fasta-export.xml" );

        FastaExportMojo mojo = ( FastaExportMojo ) lookupMojo( "export", pluginXmlFile );
        mojo.setHibernateConfig( hibernateConfig );
        mojo.setLog( new SystemStreamLog() );

        mojo.execute();

        File file = new File( getBasedir(), "target/intact.fasta.gz" );
        Assert.assertNotNull( file );
        Assert.assertTrue( file.exists() );

        File gunzippedFile = new File( file.getParentFile(), "intact.fasta" );
        Utilities.gunzip( file, gunzippedFile );

        assertLineCount( 4, gunzippedFile );
    }

    private void assertLineCount( int expectedLineCount, File file ) throws Exception {
        Assert.assertNotNull( file );
        BufferedReader in = new BufferedReader( new FileReader( file ) );
        int i = 0;
        while ( in.readLine() != null ) {
            i++;
        }
        in.close();
        Assert.assertEquals( expectedLineCount, i );
    }
}

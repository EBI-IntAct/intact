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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.bridges.blast.mock;

import org.junit.*;
import static org.junit.Assert.assertNotNull;
import uk.ac.ebi.intact.bridges.blast.AbstractBlastService;
import uk.ac.ebi.intact.bridges.blast.BlastEbiWsTest;
import uk.ac.ebi.intact.bridges.blast.BlastServiceException;
import uk.ac.ebi.intact.bridges.blast.jdbc.BlastJobEntity;
import uk.ac.ebi.intact.bridges.blast.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test class for the balst service using a mock.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id$
 * @since
 *        <pre>
 *                             25-Oct-2007
 *                             </pre>
 */
public class MockEbiWsWUBlastTest {
    private AbstractBlastService wsBlast;
    private File testDir;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        testDir = getTargetDirectory();
        testDir = new File( testDir, "BlastStrategyTest" );
        testDir.mkdir();

        String email = "iarmean@ebi.ac.uk";
        String tableName = "jobTest";
        int nr = 20;
        File dbFolder = new File( getTargetDirectory(), "BlastDbTest" );
        dbFolder.mkdir();
        File blastWorkDir = new File( BlastEbiWsTest.class.getResource( "P12345.xml" ).getPath() );
        blastWorkDir = blastWorkDir.getParentFile();
        wsBlast = new MockEbiWsWUBlast( dbFolder, tableName, blastWorkDir, email, nr );
        wsBlast.setNrMaxTries( 2 );
        wsBlast.deleteJobsAll();
        //  wsBlast.importCsv(new File(MockEbiWsWUBlastTest.class.getResource("initDb.csv").getPath()));
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        wsBlast.close();
    }

    @Test
    public final void testSubmitJob() throws BlastServiceException {
        // always throws BlastClientException
        BlastJobEntity jobEntity = wsBlast.submitJob( new UniprotAc( "P40348" ) );

        assertNotNull( jobEntity );
        BlastResult result = wsBlast.fetchResult( jobEntity );
        while ( result == null
                && !( jobEntity.getStatus().equals( BlastJobStatus.NOT_FOUND ) || jobEntity.getStatus().equals(
                BlastJobStatus.FAILED ) ) ) {
            result = wsBlast.fetchResult( jobEntity );
        }
        Assert.assertNull( result );
    }

    @Test (expected=IllegalArgumentException.class)
    public final void testSubmitJobsOneByOne() throws BlastServiceException  {
        Set<UniprotAc> acs = new HashSet<UniprotAc>( 4 );
        acs.add( new UniprotAc( "P12345" ) );
        acs.add( new UniprotAc( "Q12345" ) );
        acs.add( new UniprotAc( "P40348" ) );
        acs.add( new UniprotAc( "S12345" ) );

        List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>( acs.size() );
        for ( UniprotAc ac : acs ) {
            BlastJobEntity job = wsBlast.submitJob( ac );
            assertNotNull( job );
            jobs.add( job );
        }

        for ( int i = 0; i < jobs.size(); i++ ) {
            BlastJobEntity blastJobEntity = jobs.get( i );
            BlastResult test = wsBlast.fetchAvailableBlast( blastJobEntity );
        }
    }

    @Test
    @Ignore
    public final void testSubmitJobs() throws BlastServiceException {
        Set<UniprotAc> acs = new HashSet<UniprotAc>( 4 );
        acs.add( new UniprotAc( "P12345" ) );
        acs.add( new UniprotAc( "Q12345" ) );
        acs.add( new UniprotAc( "P40348" ) );
        acs.add( new UniprotAc( "S12345" ) );

        List<BlastJobEntity> jobEntities = wsBlast.submitJobs( acs );

        assertNotNull( jobEntities );
        Assert.assertEquals( acs.size(), jobEntities.size() );

        for ( BlastJobEntity job : jobEntities ) {
            BlastResult result = wsBlast.fetchResult( job );
            while ( result == null
                    && !( job.getStatus().equals( BlastJobStatus.NOT_FOUND ) || job.getStatus().equals(
                    BlastJobStatus.FAILED ) ) ) {
                result = wsBlast.fetchResult( job );
            }
            assertNotNull( result );
        }
    }

    private File getTargetDirectory() {
        String outputDirPath = MockEbiWsWUBlastTest.class.getResource( "/" ).getFile();
        Assert.assertNotNull( outputDirPath );
        File outputDir = new File( outputDirPath );
        // we are in intact-blast/target/test-classes , move 1 up
        outputDir = outputDir.getParentFile();
        Assert.assertNotNull( outputDir );
        Assert.assertTrue( outputDir.getAbsolutePath(), outputDir.isDirectory() );
        Assert.assertEquals( "target", outputDir.getName() );
        return outputDir;
    }
}

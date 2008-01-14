/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast.client;

import org.junit.*;
//import uk.ac.ebi.intact.bridges.blast.BlastService;
//import uk.ac.ebi.intact.bridges.blast.EbiWsWUBlast;
//import uk.ac.ebi.intact.bridges.blast.BlastServiceException;
import uk.ac.ebi.intact.bridges.blast.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * TODO comment this ... someday
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @since <pre> 7 Sep 2007 </pre>
 */
public class BlastClientTest {

    private File testDir;
    private BlastClient bc;

    /**
     * @throws java.lang.Exception     : BlastCleintException
     */
    @Before
    public void setUp() throws Exception {
        testDir = getTargetDirectory();
        bc = new BlastClient( "iarmean@ebi.ac.uk" );
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testBlastClient() throws BlastClientException {
        Assert.assertTrue( true );

        System.out.println( "mem: " + ( Runtime.getRuntime().maxMemory() ) / ( 1024 * 1024 ) );
//        BlastClient bc = new BlastClient( "iarmean@ebi.ac.uk" );
//        // blast-20071108-14114549: Q9I7U4	null	RUNNING	2007-11-01 08:35:20.473
//        // blast-20071109-10401813	Q9D1K4
//       Job job = new Job( "blast-20071109-10401813", new BlastInput( new UniprotAc( "Q9D1K4" ) ) );
//      // Job job = bc.blast(new BlastInput(new UniprotAc( "Q9I7U4")));
//        bc.checkStatus( job );
//        while ( !BlastJobStatus.DONE.equals( job.getStatus() ) ) {
//            try {
//                Thread.sleep( 5000 );
//            } catch ( InterruptedException e ) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//            bc.checkStatus( job );
//        }
//        bc.getResult( job );
//        BlastOutput result = job.getBlastResult();
//        FileWriter fw;
//        try {
//            fw = new FileWriter( new File(  testDir, job.getBlastInput().getUniprotAc().getAcNr() + ".xml" ) );
//            System.out.println( "done." );
//            fw.append( new String( result.getResult() ) );
//            fw.close();
//        } catch ( IOException e ) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    @Test
    public final void testBlastAc() throws BlastClientException {
        Assert.assertTrue(true);

//        System.out.println( "mem: " + ( Runtime.getRuntime().maxMemory() ) / ( 1024 * 1024 ) );
//        BlastClient bc = new BlastClient( "iarmean@ebi.ac.uk" );
//        UniprotAc ac = new UniprotAc( "Q9D1K4" );
//        Sequence seq = new Sequence("mscqqnqqqcqpppkcppkcqtpkcppkcppkcppkcppvssccslgsggccgsssggccssggccssggccssggggcclshhrprrslrrhrhssgccssggssgccgssggsggccgssggssgccgssggssgccgssqqsggcc");
//        Job job = bc.blast( new BlastInput( ac , seq) );
//
//        while ( !BlastJobStatus.DONE.equals( job.getStatus() ) ) {
//            try {
//                Thread.sleep( 5000 );
//            } catch ( InterruptedException e ) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//            bc.checkStatus( job );
//        }
//        bc.getResult( job );
//        BlastOutput result = job.getBlastResult();
//        FileWriter fw;
//        try {
//            fw = new FileWriter( new File( testDir, ac.getAcNr() + ".xml" ) );
//            fw.append( new String( result.getResult() ) );
//            fw.close();
//        } catch ( IOException e ) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    @Test
    @Ignore
    public final void testBlastSeq() throws BlastClientException {
        BlastClient bc = new BlastClient( "iarmean@ebi.ac.uk" );
        String seq = "MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR";
        UniprotAc ac = new UniprotAc( "Q9VQS6-1" );
        Sequence seqObj = new Sequence( seq );
        BlastInput bi = new BlastInput( ac, seqObj );
        Job job = bc.blastSeq( bi );
        while ( !BlastJobStatus.DONE.equals( job.getStatus() ) ) {
            try {
                Thread.sleep( 5000 );
            } catch ( InterruptedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            bc.checkStatus( job );
        }
        bc.getResult( job );
        BlastOutput result = job.getBlastResult();
        FileWriter fw;
        try {
            fw = new FileWriter( new File( testDir, ac.getAcNr() + ".xml" ) );
            fw.append( new String( result.getResult() ) );
            fw.close();
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
   @Ignore
    public final void testSubmitSplicevariant() throws BlastClientException {
        BlastClient bc = new BlastClient( "iarmean@ebi.ac.uk" );
        String seq = "MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR";
        UniprotAc ac = new UniprotAc( "Q9VQS6-1" );
        BlastInput bI = new BlastInput( ac, new Sequence( seq ) );
        Job job = bc.blast( bI );
        while ( !BlastJobStatus.DONE.equals( job.getStatus() ) ) {
            try {
                Thread.sleep( 5000 );
            } catch ( InterruptedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            bc.checkStatus( job );
        }
        bc.getResult( job );
        BlastOutput result = job.getBlastResult();
        FileWriter fw;
        try {
            fw = new FileWriter( new File( testDir, ac.getAcNr() + ".xml" ) );
            fw.append( new String( result.getResult() ) );
            fw.close();
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public final void testSpecificContentWithSeq() throws BlastClientException {
        String seq = "MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR";
        UniprotAc ac = new UniprotAc( "Q9VQS6" );
        BlastInput bI = new BlastInput( ac, new Sequence( seq ) );
        String content = bc.getSpecificContent( bI );
        Assert.assertFalse( content.equals( "uniprot:Q9VQS6" ) );
        Assert.assertTrue(content.equalsIgnoreCase( seq)) ;
    }

    @Test
    public final void testSpecificContentWithoutSeqIsoform() throws BlastClientException {
        UniprotAc ac = new UniprotAc( "Q9VQS6-1" );
        BlastInput bI = new BlastInput( ac);
        String content = bc.getSpecificContent( bI );
        Assert.assertTrue( content.equals( "uniprot:Q9VQS6-1" ) );
    }

    @Test
    public final void testSpecificContentWithoutSeq() throws BlastClientException {
        UniprotAc ac = new UniprotAc( "Q9VQS6" );
        BlastInput bI = new BlastInput( ac);
        String content = bc.getSpecificContent( bI );
        Assert.assertTrue( content.equals( "uniprot:Q9VQS6" ) );
    }

     @Test
    public final void testSpecificContentIsoform() throws BlastClientException {
        String seq = "MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR";
        UniprotAc ac = new UniprotAc( "Q9VQS6-1" );
        BlastInput bI = new BlastInput( ac, new Sequence( seq ) );
        String content = bc.getSpecificContent( bI );
        Assert.assertFalse( content.equals( "uniprot:Q9VQS6-1" ) );
        Assert.assertTrue(content.equalsIgnoreCase( seq)) ;
    }

    private File getTargetDirectory() {
        String outputDirPath = BlastClientTest.class.getResource( "/" ).getFile();
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

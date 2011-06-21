/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.ncbiblast.client;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.ncbiblast.model.Job;

import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * Test class for the wswublast client.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @since <pre> 7 Sep 2007 </pre>
 */
public class NCBIBlastClientTest {

    private NCBIBlastClient bc;
    private static final String email = "marine@ebi.ac.uk";

    public NCBIBlastClientTest() {
        bc = new NCBIBlastClient();
    }

    @Test
    public final void testBlastSequenceUniprot() throws NCBIBlastClientException {
        String sequence = "MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR";
        Job job = bc.blastSequenceInUniprot(email, sequence);

        while ( BlastJobStatus.RUNNING.equals( job.getStatus() ) ) {
            try {
                Thread.sleep( 5000 );
            } catch ( InterruptedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            bc.checkStatus( job );
        }
        ByteArrayInputStream results = bc.getResultAsInputStream(job);

        Assert.assertNotNull(results);
    }

    @Test
    @Ignore
    public final void testBlastSequenceIntact() throws NCBIBlastClientException {
        String sequence = "MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR";
        Job job = bc.blastSequenceInIntact(email, sequence);

        while ( BlastJobStatus.RUNNING.equals( job.getStatus() ) ) {
            try {
                Thread.sleep( 5000 );
            } catch ( InterruptedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            bc.checkStatus( job );
        }
        ByteArrayInputStream results = bc.getResultAsInputStream(job);

        Assert.assertNotNull(results);
    }

    @Test
    @Ignore
    public final void testBlastSequenceSwissprot() throws NCBIBlastClientException {
        String sequence = "MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR";
        Job job = bc.blastSequenceInSwissprot(email, sequence);

        while ( BlastJobStatus.RUNNING.equals( job.getStatus() ) ) {
            try {
                Thread.sleep( 5000 );
            } catch ( InterruptedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            bc.checkStatus( job );
        }
        ByteArrayInputStream results = bc.getResultAsInputStream(job);

        Assert.assertNotNull(results);
    }

    @Test
    public final void testBlastSequenceUniprot_file() throws NCBIBlastClientException {
        String sequence = "GTRASKHVFEKNLRPKALKLKNAEHCSIITKETARTVLTIQSYLQSISNPEWAAAIAHKIAQELPTGPDKIHALKFCLHLAEKWKKNVSSENDAHEKADVFIKKLSVQYQRSATENVLITHKLNTPELLKQIGKPANLIVSLYEHSSVEQRIRHPTGRDYPDIHTAAKQISEVNNLNMSKICTLLLEKWICPPAVPQADKNKDVFGDIHGDEDLRRVIYLLQPYPVDYSSRMLYAIATSATS";
        Job job = bc.blastSequenceInUniprot(email, sequence);

        while ( BlastJobStatus.RUNNING.equals( job.getStatus() ) ) {
            try {
                Thread.sleep( 5000 );
            } catch ( InterruptedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            bc.checkStatus( job );
        }
        File results = bc.getResultInFile(job, "test.xml");

        Assert.assertNotNull(results);
    }

    @Test
    public void getParametersValuesOfNCBIBlast(){
        this.bc.getParametersValues();
    }
}

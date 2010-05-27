package uk.ac.ebi.intact.bridges.ncbiblast;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.ncbiblast.client.NCBIBlastClient;
import uk.ac.ebi.intact.bridges.ncbiblast.client.NCBIBlastClientException;
import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.ncbiblast.model.Job;

import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * WsWuBlast service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18-Mar-2010</pre>
 */

public class ProteinNCBIBlastService {
    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( ProteinNCBIBlastService.class );
    private static String uniprot = "uniprot";
    private static final String swissprot = "swissprot";
    private static final String intact = "intact";

    /**
     * the wswublast client
     */
    private NCBIBlastClient bc;

    /**
     * the e-mail address
     */
    private String email;

    // ///////////////
    // Constructor
    public ProteinNCBIBlastService( String email)
            throws BlastServiceException {
        this.email = email;
        bc = new NCBIBlastClient();
    }

    // ///////////////
    // Public methods

    public ByteArrayInputStream getResultsOfBlastOnUniprot(String sequence){
        ByteArrayInputStream results = getResultsOfBlast(sequence, uniprot);
        return results;
    }

    public ByteArrayInputStream getResultsOfBlastOnSwissprot(String sequence){
        ByteArrayInputStream results = getResultsOfBlast(sequence, swissprot);
        return results;
    }

    public ByteArrayInputStream getResultsOfBlastOnIntact(String sequence){
        ByteArrayInputStream results = getResultsOfBlast(sequence, intact);
        return results;
    }

    public File getResultsOfBlastOnUniprot(String sequence, String fileName){
        File results = getResultsOfBlastInFile(sequence, uniprot, fileName);
        return results;
    }

    public File getResultsOfBlastOnSwissprot(String sequence, String fileName){
        File results = getResultsOfBlastInFile(sequence, swissprot, fileName);
        
        return results;
    }

    public File getResultsOfBlastOnIntact(String sequence, String fileName){
        File results = getResultsOfBlastInFile(sequence, intact, fileName);
        return results;
    }

    // ///////////////
    // Private methods

    private File getResultsOfBlastInFile(String sequence, String databaseName, String fileName){
        Job job = runBlast(sequence, databaseName);

        if (job != null){
            try {
                File results = bc.getResultInFile(job, fileName);
                return results;
            } catch (NCBIBlastClientException e) {
                log.error(" One error has occured with the BlastClient during the wswublast job", e);
            }
        }
        return null;
    }

    private Job runBlast(String sequence, String databaseName){
        Job job = null;
        try {

            if (databaseName != null){

                if (databaseName.toLowerCase().equals(uniprot)){
                    job = this.bc.blastSequenceInUniprot(this.email, sequence);
                }
                else if (databaseName.toLowerCase().equals(swissprot)){
                    job = this.bc.blastSequenceInSwissprot(this.email, sequence);
                }
                else if (databaseName.toLowerCase().equals(intact)){
                    job = this.bc.blastSequenceInIntact(this.email, sequence);
                }
                else{
                    log.error(databaseName + " isn't a valid database name. You can only do a wswublast on uniprot, swissprot or intact.");
                }

                if (job != null){
                    while ( BlastJobStatus.RUNNING.equals( job.getStatus() ) ) {
                        try {
                            Thread.sleep( 5000 );
                        } catch ( InterruptedException e ) {
                            log.error(" The wswublast job has been interrupted.", e);
                        }
                        bc.checkStatus( job );
                    }
                }
            }
            else {
                log.error(" You didn't specify a valid database name. You can only do a wswublast on uniprot, swissprot or intact.");
            }

        } catch (NCBIBlastClientException e) {
            log.error(" One error has occured with the BlastClient during the wswublast job", e);
        }
        return job;
    }

    private ByteArrayInputStream getResultsOfBlast(String sequence, String databaseName){
        Job job = runBlast(sequence, databaseName);

        if (job != null){

            try {
                ByteArrayInputStream results = bc.getResultAsInputStream(job);
                return results;
            } catch (NCBIBlastClientException e) {
                log.error(" One error has occured with the BlastClient during the wswublast job", e);
            }
        }
        return null;
    }
}

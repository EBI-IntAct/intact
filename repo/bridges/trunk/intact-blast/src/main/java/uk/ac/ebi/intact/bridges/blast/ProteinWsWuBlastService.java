package uk.ac.ebi.intact.bridges.blast;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.blast.client.BlastClient;
import uk.ac.ebi.intact.bridges.blast.client.BlastClientException;
import uk.ac.ebi.intact.bridges.blast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.blast.model.Job;

import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * WsWuBlast service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18-Mar-2010</pre>
 */

public class ProteinWsWuBlastService {
    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( ProteinWsWuBlastService.class );

    /**
     * the blast client
     */
    private BlastClient bc;

    /**
     * the e-mail address
     */
    private String email;

    // ///////////////
    // Constructor
    public ProteinWsWuBlastService( String email)
            throws BlastServiceException {
        this.email = email;
        bc = newBlastClientInstance();
    }

    protected BlastClient newBlastClientInstance() throws BlastServiceException {
        try {
            return new BlastClient( this.email );
        } catch ( BlastClientException e ) {
            throw new BlastServiceException( e );
        }
    }

    // ///////////////
    // Public methods

    public ByteArrayInputStream getResultsOfBlastOnUniprot(String sequence){
        ByteArrayInputStream results = getResultsOfBlast(sequence, "uniprot");
        return results;
    }

    public ByteArrayInputStream getResultsOfBlastOnSwissprot(String sequence){
        ByteArrayInputStream results = getResultsOfBlast(sequence, "swissprot");
        return results;
    }

    public ByteArrayInputStream getResultsOfBlastOnIntact(String sequence){
        ByteArrayInputStream results = getResultsOfBlast(sequence, "intact");
        return results;
    }

    public File getResultsOfBlastOnUniprot(String sequence, String fileName){
        File results = getResultsOfBlastInFile(sequence, "uniprot", fileName);
        return results;
    }

    public File getResultsOfBlastOnSwissprot(String sequence, String fileName){
        File results = getResultsOfBlastInFile(sequence, "swissprot", fileName);
        return results;
    }

    public File getResultsOfBlastOnIntact(String sequence, String fileName){
        File results = getResultsOfBlastInFile(sequence, "intact", fileName);
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
            } catch (BlastClientException e) {
                log.error(" One error has occured with the BlastClient during the blast job", e);
            }
        }
        return null;
    }

    private Job runBlast(String sequence, String databaseName){
        Job job = null;
        try {

            if (databaseName != null){

                if (databaseName.toLowerCase().equals("uniprot")){
                    job = this.bc.blastSequenceInUniprot(sequence);
                }
                else if (databaseName.toLowerCase().equals("swissprot")){
                    job = this.bc.blastSequenceInSwissprot(sequence);
                }
                else if (databaseName.toLowerCase().equals("intact")){
                    job = this.bc.blastSequenceInIntact(sequence);
                }
                else{
                    log.error(databaseName + " isn't a valid database name. You can only do a blast on uniprot, swissprot or intact.");
                }

                if (job != null){
                    while ( !BlastJobStatus.DONE.equals( job.getStatus() ) ) {
                        try {
                            Thread.sleep( 5000 );
                        } catch ( InterruptedException e ) {
                            log.error(" The blast job has been interrupted.", e);
                        }
                        bc.checkStatus( job );
                    }
                }
            }
            else {
                log.error(" You didn't specify a valid database name. You can only do a blast on uniprot, swissprot or intact.");
            }

        } catch (BlastClientException e) {
            log.error(" One error has occured with the BlastClient during the blast job", e);
        }
        return job;
    }

    private ByteArrayInputStream getResultsOfBlast(String sequence, String databaseName){
        Job job = runBlast(sequence, databaseName);

        if (job != null){

            try {
                ByteArrayInputStream results = bc.getResultAsInputStream(job);
                return results;
            } catch (BlastClientException e) {
                log.error(" One error has occured with the BlastClient during the blast job", e);
            }
        }
        return null;
    }
}

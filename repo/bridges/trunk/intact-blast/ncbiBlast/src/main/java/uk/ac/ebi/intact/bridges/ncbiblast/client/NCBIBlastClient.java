package uk.ac.ebi.intact.bridges.ncbiblast.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.ncbiblast.model.Job;
import uk.ac.ebi.jdispatcher.soap.*;

import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The NCBI blast client
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13-Apr-2010</pre>
 */

public class NCBIBlastClient {

        /**
     * The log of this class
     */
    public static final Log log = LogFactory.getLog( NCBIBlastClient.class );
    private JDispatcherService service;
    private uk.ac.ebi.jdispatcher.soap.ObjectFactory objFactory;

    private final static String wsdlFile = "http://www.ebi.ac.uk/Tools/services/soap/ncbiblast?wsdl";
    private final static String jDispatcherURL = "http://soap.jdispatcher.ebi.ac.uk";
    private final static String jDispatcherName = "JDispatcherService";
    private final static String jobName = "NCBI Blast";
    private final static String resultFormat = "xml";
    private final static String uniprot = "uniprotkb";
    private final static String swissprot = "uniprotkb_swissprot";
    private final static String swissprot_sv = "uniprotkb_swissprotsv";
    private final static String intact = "intact";

    private final static String program = "blastp";
    private final static int align = 0;
    private final static int dropOff = 0;
    private final static String exp = "10";
    private final static String filter = "F";
    private final static boolean gapAlign = true;
    private final static int gapExt = 1;
    private final static int gapOpen = 11;
    private final static String matrix = "BLOSUM62";
    private final static int scores = 100;
    private final static int alignments = 50;
    private final static String sType = "protein";

    public NCBIBlastClient(){
        this(wsdlFile);
    }

    public NCBIBlastClient(String wsdlUrl){
        this.objFactory = new uk.ac.ebi.jdispatcher.soap.ObjectFactory();

        JDispatcherService_Service service_service;

        if (wsdlUrl == null){
            service_service = new JDispatcherService_Service();
        }
        else {
            try{
                service_service = new JDispatcherService_Service(new URL(wsdlUrl), new QName(jDispatcherURL, jDispatcherName));
            } catch (MalformedURLException e) {
                log.error(e.getMessage());
                log.error("Warning: problem with specified endpoint URL. Default endpoint used.");
                service_service = new JDispatcherService_Service();
            }
        }

        service = service_service.getJDispatcherServiceHttpPort();
    }

    public String runWUBlast(String email, InputParameters params){
        return this.service.run(email, jobName, params);
    }

    public String checkStatus(String jobId){
        return this.service.getStatus(jobId);
    }

    /** Get details of the available result types for a job.
     *
     * @param jobId Job identifier to check for results types.
     * @return Array of objects describing result types.
     * @throws NCBIBlastClientException
     */
    public WsResultType[] getResultTypes(String jobId) throws NCBIBlastClientException {

        WsResultType[] retVal = null;
        WsResultTypes resultTypes = this.service.getResultTypes(jobId);
        if(resultTypes != null) {
            retVal = resultTypes.getType().toArray(new WsResultType[0]);
        }
        return retVal;
    }

    public byte[] poll(String jobid) throws NCBIBlastClientException{

        // Get result types
        WsResultType[] resultTypes = getResultTypes(jobid);
        int retValN = 0;

        for(int i = 0; i < resultTypes.length; i++) {
            // Get the results
            if(resultTypes[i].getIdentifier().equals(resultFormat)) {
                byte[] resultbytes = null;
                try {
                    resultbytes = this.service.getResult(jobid, resultTypes[i].getIdentifier(), null);
                } catch (SOAPFaultException e){
                    resultbytes = null;
                    log.warn("A SOAP exception has been thrown for this job and we couldn't get any results.", e);
                }

                if(resultbytes == null) {
                    System.err.println("Null result for " + resultTypes[i].getIdentifier() + "!");
                }

                return resultbytes;
            }
        }
        return null;
    }

    public void getParametersValues(){
        WsParameters parameters = this.service.getParameters();

        for (String id : parameters.getId()){
            WsParameterDetails details = this.service.getParameterDetails(id);
            System.out.println("parameter name : " + details.getName());

            System.out.println("parameter type : " + details.getType());
            System.out.println("parameter description : " + details.getDescription());

            WsParameterValues values = details.getValues();
            System.out.println("parameter values : ");

            if (values != null){
                for (WsParameterValue value : values.getValue()){
                    System.out.println("value label : " + value.getLabel());
                    System.out.println("value : " + value.getValue());
                }
            }

        }
    }

    // ////////////////
    // public Methods

    /**
     * Run a wswublast job on Uniprot with a sequence
     * @param sequence : the sequence to wswublast
     * @return the wswublast job
     * @throws NCBIBlastClientException : throws an exception if there is a problem trying to run a wswublast job
     */
    public Job blastSequenceInUniprot(String email, String sequence) throws NCBIBlastClientException {
        return blastSequence(email, sequence, uniprot);
    }

    /**
     * Run a wswublast job on Swissprot with a sequence
     * @param sequence : the sequence to wswublast
     * @return  the wswublast job
     * @throws NCBIBlastClientException : throws an exception if there is a problem trying to run a wswublast job
     */
    public Job blastSequenceInSwissprot(String email, String sequence) throws NCBIBlastClientException {
        return blastSequence(email, sequence, swissprot, swissprot_sv);
    }

    /**
     * Run a wswublast job on Intact with a sequence
     * @param sequence : the sequence to wswublast
     * @return  the wswublast job
     * @throws NCBIBlastClientException : throws an exception if there is a problem trying to run a wswublast job
     */
    public Job blastSequenceInIntact(String email, String sequence) throws NCBIBlastClientException {
        return blastSequence(email, sequence, intact);
    }

    /**
     * Run a wswublast job in a specific database with a sequence
     * @param sequence : the sequence to wswublast
     * @param databases : the list of databases to query
     * @return the wswublast Job
     * @throws NCBIBlastClientException : throws an exception if there is a problem trying to run a wswublast job
     */
    public Job blastSequence(String email, String sequence, String ... databases) throws NCBIBlastClientException {

        InputParameters params = this.objFactory.createInputParameters();
        params.setProgram(program);

        ArrayOfString d = this.objFactory.createArrayOfString();
        for (String db : databases){
           d.getString().add(db);              
        }
        params.setDatabase(d);
        params.setAlign(this.objFactory.createInputParametersAlign(align));
        params.setDropoff(this.objFactory.createInputParametersDropoff(dropOff));
        params.setExp(this.objFactory.createInputParametersExp(exp));
        params.setFilter(this.objFactory.createInputParametersFilter(filter));
        params.setGapalign(this.objFactory.createInputParametersGapalign(gapAlign));
        params.setGapext(this.objFactory.createInputParametersGapext(gapExt));
        params.setGapopen(this.objFactory.createInputParametersGapopen(gapOpen));
        params.setMatrix(this.objFactory.createInputParametersMatrix(matrix));
        params.setScores(this.objFactory.createInputParametersScores(scores));
        params.setAlignments(this.objFactory.createInputParametersAlignments(alignments));
        params.setSequence(this.objFactory.createInputParametersSequence(sequence));

        params.setStype(sType);

        Job job = null;
        job = new Job(runWUBlast(email, params), sequence);
        checkStatus(job);
        return job;
    }

    /**
     * Blasts the specified protein (uniprot accession number) against
     * uniprotkb.
     *
     * @param blastInput : contains the input of a wswublast
     * @return the Job (job id and the blasted protein)
     * @throws NCBIBlastClientException   : wrapper for the WS-Exception
     */
    /*public Job blast(BlastInput blastInput) throws NCBIBlastClientException {
        if (blastInput == null) {
            throw new IllegalArgumentException("BlastInput mus not be null!");
        }
        if (blastInput.getUniprotAc() == null) {
            throw new IllegalArgumentException("BlastInput uniprotAc mus not be null!");
        }  //Why an uniprot AC for a wswublast?

        InputParams params = new InputParams();
        params.setProgram("blastp");
        params.setDatabase("uniprot");
        params.setEmail(email);
        params.setNumal(nr);
        params.setAsync(Boolean.TRUE); // set the submissions asynchronous

        Data inputs[] = new Data[1];
        Data input = new Data();
        input.setType("sequence");
        String content = getSpecificContent(blastInput);
        input.setContent(content);
        inputs[0] = input;

        Job job = null;
        try {
            job = new Job(blast.runWUBlast(params, inputs), blastInput);
            checkStatus(job);
        } catch (RemoteException e) {
            // FIXME: axisfault
            String message = e.getMessage();
            if (message.startsWith("could not fetch entry")) {
                job = new Job("failed " + blastInput.toString(), blastInput);
                job.setStatus(BlastJobStatus.NOT_FOUND);
            } else {
                throw new NCBIBlastClientException(e);
            }
        }
        return job;
    } */

    /**
     * @param job : the wswublast job
     * @return status of the job (RUNNING | PENDING | NOT_FOUND | FAILED | DONE)
     * DONE, RUNNING, PENDING, NOT_FOUND or FAILED
     */
    public BlastJobStatus checkStatus(Job job){

        String status = checkStatus(job.getId());
        if (status.equals("FINISHED")) {
            job.setStatus(BlastJobStatus.FINISHED);
            return job.getStatus();
        } else if (status.equals("RUNNING")) {
            job.setStatus(BlastJobStatus.RUNNING);
            return job.getStatus();
        } else if (status.equals("ERROR")) {
            job.setStatus(BlastJobStatus.ERROR);
            log.error("An error occured while running the NCBI blast job " + job.getId());
            return job.getStatus();
        } else if (status.equals("NOT_FOUND")) {
            job.setStatus(BlastJobStatus.NOT_FOUND);
            log.error("The NCBI blast job " + job.getId() + "has not been found.");
            return job.getStatus();
        } else if (status.equals("FAILURE")) {
            job.setStatus(BlastJobStatus.FAILURE);
            log.error("The NCBI blast job " + job.getId() + "has failed.");            
            return job.getStatus();
        }
        return null;
    }

    /**
     * Get the wswublast result of a specific job
     * @param job : the wswublast job
     * @return the wswublast result as an InputStream
     * @throws NCBIBlastClientException : throws an exception if there is a problem trying to get the results of a wswublast job
     */
    public ByteArrayInputStream getResultAsInputStream(Job job) throws NCBIBlastClientException {
        if (!BlastJobStatus.FINISHED.equals(job.getStatus())) {
            checkStatus(job);
        }
        else {
            byte[] resultbytes = poll(job.getId());

            if (resultbytes != null){
                return new ByteArrayInputStream(resultbytes);
            }
        }
        return null;
    }

    /**
     * Get the wswublast result of a specific job in a file with a given name
     * @param job : the wswublast job
     * @param fileName : the name of the file where the results are written in
     * @return the wswublast result in a File
     * @throws NCBIBlastClientException : throws a NCBIBlastClientException if there is a problem when getting the results of a job and when
     * a problem occurs while writing the output file
     */
    public File getResultInFile(Job job, String fileName) throws NCBIBlastClientException {
        if (!BlastJobStatus.FINISHED.equals(job.getStatus())) {
            checkStatus(job);
        }
        else {
            try {
                byte[] resultbytes = poll(job.getId());

                if (resultbytes != null){
                    String name = fileName;
                    if (name == null) {
                        name = "job:"+job.getId();
                    }

                    File resultFile = new File( name );
                    FileWriter fw = new FileWriter( resultFile );
                    fw.append( new String( resultbytes ) );
                    fw.close();
                    return new File(new String(resultbytes));
                }

            }catch ( IOException e ) {
                throw new NCBIBlastClientException(e);
            }
        }
        return null;
    }
}

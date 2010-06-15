/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package uk.ac.ebi.intact.bridges.wswublast.client;

import uk.ac.ebi.intact.bridges.wswublast.model.BlastInput;
import uk.ac.ebi.intact.bridges.wswublast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.wswublast.model.BlastOutput;
import uk.ac.ebi.intact.bridges.wswublast.model.Job;
import uk.ac.ebi.jdispatcher.soap.*;
import uk.ac.ebi.jdispatcher.soap.ObjectFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Blast client
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id: BlastClient.java 9729 2007-09-19 11:03:02Z irina-armean $
 */
public class BlastClient {
    // true for XML formated output, false otherwise
    private boolean		fileFormatXml	= true;
    private String		email;
    private WUBlast blast;
    private uk.ac.ebi.jdispatcher.soap.ObjectFactory objFactory;

    /**
     * Constructor
     *
     * @param email : email for the web service client
     * @throws WuBlastClientException   : wrapper for the ServiceException
     *
     */
    public BlastClient(String email) throws WuBlastClientException {
        if (email == null) {
            throw new IllegalArgumentException("Email must not be null!");
        }
        this.email = email;
        this.blast = new WUBlast();
        this.objFactory = new uk.ac.ebi.jdispatcher.soap.ObjectFactory();
    }

    // ////////////////
    // Getter/Setter
    /**
     * @param fileFormatXml
     *            the fileFormatXml to set
     */
    public void setFileFormatXml(boolean fileFormatXml) {
        this.fileFormatXml = fileFormatXml;
    }

    /**
     * @return the fileFormatXml
     */
    public boolean isFileFormatXml() {
        return fileFormatXml;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    // ////////////////
    // public Methods

    /**
     * Blasts the specified protein (uniprot accession number) against
     * uniprotkb.
     *
     * @param blastInput : contains the input of a wswublast
     * @return the Job (job id and the blasted protein)
     */
    public Job blast(BlastInput blastInput) throws WuBlastClientException {
        if (blastInput == null) {
            throw new IllegalArgumentException("BlastInput mus not be null!");
        }
        if (blastInput.getUniprotAc() == null) {
            throw new IllegalArgumentException("BlastInput uniprotAc mus not be null!");
        }  //Why an uniprot AC for a wswublast?

        InputParameters params = this.objFactory.createInputParameters();
        params.setProgram("blastp");

        ArrayOfString d = this.objFactory.createArrayOfString();
        d.getString().add("uniprotkb");
        params.setDatabase(d);

        params.setSequence(this.objFactory.createInputParametersSequence(getSpecificContent(blastInput)));

        params.setStype("protein");

        //params.setEmail(email);
        //params.setNumal(nr);
        // params.setAsync(Boolean.TRUE); // set the submissions asynchronous
        WsParameterDetails inputs = new WsParameterDetails();
        inputs.setType("sequence");

        Job job = null;
        job = new Job(blast.runWUBlast(this.email, params), blastInput);
        checkStatus(job);

        return job;
    }

    protected String getSpecificContent(BlastInput blastInput) {
        String content = "";
        if(blastInput.getSequence() != null && blastInput.getSequence().getSeq() != null){
            content = blastInput.getSequence().getSeq();
            return content;
        } else {
            content = "intact:" + blastInput.getUniprotAc().getAcNr();
            return content;
        }

//        String ac = blastInput.getUniprotAc().getAcNr();
//		String[] aux = ac.split("-");
//
//		if (aux.length == 2 && blastInput.getSequence() != null && blastInput.getSequence().getSeq() != null) {
//			content = blastInput.getSequence().getSeq();
//			return content;
//		} else {
//			content = "uniprot:" + blastInput.getUniprotAc().getAcNr();
//			return content;
//		}
    }

    // TODO: remove after play phase
    protected Job blastSeq(BlastInput blastInput) throws WuBlastClientException {
        if (blastInput == null) {
            throw new IllegalArgumentException("BlastInput mus not be null!");
        }
        if (blastInput.getUniprotAc() == null) {
            throw new IllegalArgumentException("BlastInput uniprotAc mus not be null!");
        }

        InputParameters params = this.objFactory.createInputParameters();
        params.setProgram("blastp");

        ArrayOfString d = this.objFactory.createArrayOfString();
        d.getString().add("intact");
        params.setDatabase(d);

        params.setSequence(this.objFactory.createInputParametersSequence(getSpecificContent(blastInput)));

        params.setStype("protein");

        Job job = null;
        job = new Job(blast.runWUBlast(this.email, params), blastInput);
        checkStatus(job);
        return job;
    }

    /**
     * Blasts a list of uniprotAc against uniprot.
     *
     * @param blastInputSet : a set of BlastInput -objects
     * @return a list of Job objects
     */
    public List<Job> blast(Set<BlastInput> blastInputSet) throws WuBlastClientException {
        if (blastInputSet == null) {
            throw new IllegalArgumentException("BlastInputSet set must not be null!");
        }
        if (blastInputSet.size() == 0) {
            throw new IllegalArgumentException("BlastInputSet must not be empty!");
        }

        List<Job> jobs = new ArrayList<Job>();
        for (BlastInput blastInput : blastInputSet) {
            Job job = blast(blastInput);
            if (job != null) {
                jobs.add(job);
            }
        }
        return jobs;
    }

    /**
     * @param job : the wswublast job
     * @return status of the job (RUNNING | PENDING | NOT_FOUND | FAILED | DONE)
     * DONE, RUNNING, PENDING, NOT_FOUND or FAILED
     */
    public BlastJobStatus checkStatus(Job job) throws WuBlastClientException {
        String status = blast.checkStatus(job.getId());
        if (status.equals("FINISHED")) {
            job.setStatus(BlastJobStatus.FINISHED);
            return job.getStatus();
        } else if (status.equals("RUNNING")) {
            job.setStatus(BlastJobStatus.RUNNING);
            return job.getStatus();
        } else if (status.equals("ERROR")) {
            job.setStatus(BlastJobStatus.ERROR);
            return job.getStatus();
        } else if (status.equals("NOT_FOUND")) {
            job.setStatus(BlastJobStatus.NOT_FOUND);
            return job.getStatus();
        } else if (status.equals("FAILURE")) {
            job.setStatus(BlastJobStatus.FAILURE);
            return job.getStatus();
        }
        return null;
    }

// --Commented out by Inspection START (24/10/07 17:15):
//	/**
//	 * Tests if the job is finished or not.
//	 *
//	 * @param job
//	 * @return true or false
//	 * @throws WuBlastClientException
//	 */
//	public boolean isFinished(Job job) throws WuBlastClientException {
//		if (BlastJobStatus.DONE.equals(job.getStatus())){
//			return true;
//		}
//		BlastJobStatus status = checkStatus(job);
//        return BlastJobStatus.DONE.equals( status );
//		}
// --Commented out by Inspection STOP (24/10/07 17:15)

    /**
     * Retrieves the result if the job is finished.
     *
     * @param job : the wswublast job
     * @return string: the output in xml format or
     */
    public BlastOutput getResult(Job job) throws WuBlastClientException {
        if (!BlastJobStatus.FINISHED.equals(job.getStatus())) {
            checkStatus(job);
        }
        if (BlastJobStatus.FINISHED.equals(job.getStatus())) {
            String type = (fileFormatXml ? "xml" : "out");
            byte[] resultbytes = blast.poll(job.getId(), "xml");
            job.setBlastResult(new BlastOutput(resultbytes, fileFormatXml));
        }
        return job.getBlastResult();
    }

    public void getParametersValues(){
        this.blast.getParametersValues();
    }
}
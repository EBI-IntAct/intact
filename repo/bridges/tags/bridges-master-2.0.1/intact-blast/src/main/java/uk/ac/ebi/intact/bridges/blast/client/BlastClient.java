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
package uk.ac.ebi.intact.bridges.blast.client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.rpc.ServiceException;

import uk.ac.ebi.intact.bridges.blast.model.BlastInput;
import uk.ac.ebi.intact.bridges.blast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.blast.model.BlastOutput;
import uk.ac.ebi.intact.bridges.blast.model.Job;
import uk.ac.ebi.www.WSWUBlast.Data;
import uk.ac.ebi.www.WSWUBlast.InputParams;
import uk.ac.ebi.www.WSWUBlast.WSWUBlast;
import uk.ac.ebi.www.WSWUBlast.WSWUBlastService;
import uk.ac.ebi.www.WSWUBlast.WSWUBlastServiceLocator;

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
	private WSWUBlast	blast;
    private int nr; // number of blasts in the ouput

    /**
	 * Constructor
	 * 
	 * @param email : email for the web service client
     * @throws BlastClientException   : wrapper for the ServiceException
	 * 
	 */
	public BlastClient(String email) throws BlastClientException {
		if (email == null) {
			throw new IllegalArgumentException("Email must not be null!");
		}
		this.email = email;
		WSWUBlastService service = new WSWUBlastServiceLocator();
		try {
			blast = service.getWSWUBlast();
		} catch (ServiceException e) {
			throw new BlastClientException(e);
		}
        // default it is 100
        nr = 100;
       // nr = 10;
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
	 * @param blastInput
	 * @return the Job (job id and the blasted protein)
	 * @throws BlastClientException   : wrapper for the WS-Exception
	 */
	public Job blast(BlastInput blastInput) throws BlastClientException {
		if (blastInput == null) {
			throw new IllegalArgumentException("BlastInput mus not be null!");
		}
		if (blastInput.getUniprotAc() == null) {
			throw new IllegalArgumentException("BlastInput uniprotAc mus not be null!");
		}

		InputParams params = new InputParams();
		params.setProgram("blastp");
		params.setDatabase("intact");
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
			// FIXME: ask sam : axisfault
			String message = e.getMessage();
			if (message.startsWith("could not fetch entry")) {
				job = new Job("failed " + blastInput.toString(), blastInput);
				job.setStatus(BlastJobStatus.NOT_FOUND);
			} else {
				throw new BlastClientException(e);
			}
		}
		return job;
	}

    protected String getSpecificContent(BlastInput blastInput) {
        String content = "";
        if(blastInput.getSequence() != null && blastInput.getSequence().getSeq() != null){
            content = blastInput.getSequence().getSeq();
            return content;
        } else {
            content = "uniprot:" + blastInput.getUniprotAc().getAcNr();
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
	protected Job blastSeq(BlastInput blastInput) throws BlastClientException {
		if (blastInput == null) {
			throw new IllegalArgumentException("BlastInput mus not be null!");
		}
		if (blastInput.getUniprotAc() == null) {
			throw new IllegalArgumentException("BlastInput uniprotAc mus not be null!");
		}

		InputParams params = new InputParams();
		params.setProgram("blastp");
		params.setDatabase("uniprot");
		params.setEmail(email);
		params.setNumal(100);

		params.setAsync(Boolean.TRUE); // set the submissions asynchronous

		Data inputs[] = new Data[1];
		Data input = new Data();
		input.setType("sequence");
		input.setContent(blastInput.getSequence().getSeq());
		inputs[0] = input;

		Job job = null;
		try {
			job = new Job(blast.runWUBlast(params, inputs), blastInput);
			checkStatus(job);
		} catch (RemoteException e) {
			// FIXME: ask sam : axisfault
			String message = e.getMessage();
			if (message.startsWith("could not fetch entry")) {
				job = new Job("could not fetch entry " + blastInput.toString(), blastInput);
				job.setStatus(BlastJobStatus.NOT_FOUND);
			} else {
				throw new BlastClientException(e);
			}
		}
		return job;
	}

	/**
	 * Blasts a list of uniprotAc against uniprot.
	 * 
	 * @param blastInputSet : a set of BlastInput -objects
	 * @return a list of Job objects
	 * @throws BlastClientException
	 */
	public List<Job> blast(Set<BlastInput> blastInputSet) throws BlastClientException {
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
	 * @return status of the job (RUNNING | PENDING | NOT_FOUND | FAILED | DONE)
	 * @throws BlastClientException
	 */
	public BlastJobStatus checkStatus(Job job) throws BlastClientException {
		try {
			String status = blast.checkStatus(job.getId());
			if (status.equals("DONE")) {
				job.setStatus(BlastJobStatus.DONE);
				return job.getStatus();
			} else if (status.equals("RUNNING")) {
				job.setStatus(BlastJobStatus.RUNNING);
				return job.getStatus();
			} else if (status.equals("PENDING")) {
				job.setStatus(BlastJobStatus.PENDING);
				return job.getStatus();
			} else if (status.equals("NOT_FOUND")) {
				job.setStatus(BlastJobStatus.NOT_FOUND);
				return job.getStatus();
			} else if (status.equals("FAILED")) {
				job.setStatus(BlastJobStatus.FAILED);
				return job.getStatus();
			}
		} catch (RemoteException e) {
			throw new BlastClientException(e);
		} 
		return null;
	}

// --Commented out by Inspection START (24/10/07 17:15):
//	/**
//	 * Tests if the job is finished or not.
//	 *
//	 * @param job
//	 * @return true or false
//	 * @throws BlastClientException
//	 */
//	public boolean isFinished(Job job) throws BlastClientException {
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
	 * @param job
	 * @return string: the output in xml format or
	 * @throws BlastClientException
	 */
	public BlastOutput getResult(Job job) throws BlastClientException {
		if (!BlastJobStatus.DONE.equals(job.getStatus())) {
			checkStatus(job);
		}
		if (BlastJobStatus.DONE.equals(job.getStatus())) {
			try {
				String type = (fileFormatXml ? "toolxml" : "tooloutput");
				byte[] resultbytes = blast.poll(job.getId(), type);
				job.setBlastResult(new BlastOutput(resultbytes, fileFormatXml));
			} catch (RemoteException e) {
				throw new BlastClientException(e);
			}
		}
		return job.getBlastResult();
	}
}
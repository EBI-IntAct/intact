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
package uk.ac.ebi.intact.bridges.blast;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.rpc.ServiceException;

import uk.ac.ebi.intact.bridges.blast.generated.Data;
import uk.ac.ebi.intact.bridges.blast.generated.InputParams;
import uk.ac.ebi.intact.bridges.blast.generated.WSWUBlastService;
import uk.ac.ebi.intact.bridges.blast.generated.WSWUBlastServiceLocator;
import uk.ac.ebi.intact.bridges.blast.generated.WSWUBlast_PortType;
import uk.ac.ebi.intact.bridges.blast.model.BlastInput;
import uk.ac.ebi.intact.bridges.blast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.blast.model.BlastResult;
import uk.ac.ebi.intact.bridges.blast.model.Job;

/**
 * Blast client
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id$
 */
public class BlastClient {
	// checks the format of the accession number
	private static String		uniprotTermExpr	= "\\w{6,6}";

	// true for xml formated output, false otherwise
	private boolean				fileFormatXml	= true;
	private String				email;
	private WSWUBlast_PortType	blast;

	/**
	 * Constructor
	 * 
	 * @throws BlastClientException
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
	 * @param uniprotAc
	 * @return the Job (job id and the blasted protein)
	 * @throws BlastClientException
	 */
	public Job blast(BlastInput blastInput) throws BlastClientException {
		if (blastInput == null) {
			throw new IllegalArgumentException("BlastInput mus not be null!");
		}
		if (blastInput.getUniprotAc() == null) {
			throw new IllegalArgumentException("BlastInput uniprotAc mus not be null!");
		}
		if (!isUniprotAcFormat(blastInput.getUniprotAc().getAcNr())) {
			throw new IllegalArgumentException("UniprotAc not in the uniprot format: '"
					+ blastInput.getUniprotAc().getAcNr() + "'");
		}

		InputParams params = new InputParams();
		params.setProgram("blastp");
		params.setDatabase("uniprot");
		params.setEmail(email);

		params.setAsync(Boolean.TRUE); // set the submissions asynchronous

		Data inputs[] = new Data[1];
		Data input = new Data();
		input.setType("sequence");
		input.setContent("uniprot:" + blastInput.getUniprotAc().getAcNr());
		inputs[0] = input;

		Job job = null;
		try {
			job = new Job(blast.runWUBlast(params, inputs), blastInput);
		} catch (RemoteException e) {
			throw new BlastClientException(e);
		}
		return job;
	}

	/**
	 * Blasts a list of uniprotAc against uniprot.
	 * 
	 * @param set
	 *            of uniprotAcs
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
	 */
	public BlastJobStatus checkStatus(Job job) {
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
			new BlastClientException(e);
		}
		return null;
	}

	/**
	 * Tests if the job is finished or not.
	 * 
	 * @param job
	 * @return true or false
	 */
	public boolean isFinished(Job job) {
		BlastJobStatus status = checkStatus(job);
		if (BlastJobStatus.DONE.equals(status)) {
			return true;
		}
		return false;
	}

	/**
	 * Retrieves the result if the job is finished.
	 * 
	 * @param job
	 * @return string: the output in xml format or
	 */
	public BlastResult getResult(Job job) {
		String result = null;
		BlastJobStatus status = checkStatus(job);
		if (BlastJobStatus.DONE.equals(status)) {
			try {
				String type = (fileFormatXml ? "toolxml" : "tooloutput");
				byte[] resultbytes = blast.poll(job.getId(), type);
				result = new String(resultbytes);
	
				job.setBlastResult(new BlastResult(result, fileFormatXml));
			} catch (RemoteException e) {
				new BlastClientException(e);
			}
		}
		return job.getBlastResult();
	}

	/**
	 * Tests if the specified string has a valid uniprot accession number
	 * format.
	 * 
	 * @param uniprotAc
	 * @return true or false
	 */
	private boolean isUniprotAcFormat(String uniprotAc) {
		if (Pattern.matches(uniprotTermExpr, uniprotAc)) {
			return true;
		}
		return false;
	}

}
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

/**
 * Blast client
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id$
 */
public class BlastClient {
	// checks the format of the accession number
	private static String		uniprotTermExpr	= "\\w{6,6}";
	private static String		done			= "DONE";
	// true for xml formated output, false otherwise
	private boolean				fileFormatXml	= true;
	private String				email;
	private WSWUBlast_PortType	blast;

	/**
	 * Constructor
	 * 
	 */
	public BlastClient() {
		email = "iarmean@ebi.ac.uk";
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

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	// ////////////////
	// public Methods
	/**
	 * Blasts the specified protein (uniprot accession number) against
	 * uniprotkb.
	 * 
	 * @param uniprotAc
	 * @return the Job (job id and the blasted protein)
	 */
	public Job blast(String uniprotAc) {

		if (!isUniprotAcFormat(uniprotAc)) {
			throw new BlastClientException(new IllegalArgumentException("UniprotAc not in the uniprot format: '"
					+ uniprotAc + "'"));
		}

		InputParams params = new InputParams();
		params.setProgram("blastp");
		params.setDatabase("uniprot");
		params.setEmail(email);

		params.setAsync(new Boolean(true)); // set the submissions asynchronous

		Data inputs[] = new Data[1];
		Data input = new Data();
		input.setType("sequence");
		input.setContent("uniprot:" + uniprotAc);
		inputs[0] = input;

		Job job = null;
		try {
			job = new Job(blast.runWUBlast(params, inputs), uniprotAc);
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
	 */
	public List<Job> blast(Set<String> uniprotAcSet) {
		if (uniprotAcSet == null) {
			throw new BlastClientException(new NullPointerException("The uniprotAc set must not be null!"));
		}
		List<Job> jobs = new ArrayList<Job>();
		for (String ac : uniprotAcSet) {
			if (!isUniprotAcFormat(ac)) {
				new BlastClientException("Ac '" + ac + "' is not in the proper/expected format!");
			}
			Job job = blast(ac);
			if (job != null) {
				jobs.add(job);
			}
		}
		return jobs;
	}

	/**
	 * @return status of the job (RUNNING | PENDING | NOT_FOUND | FAILED | DONE)
	 */
	public String checkStatus(String jobid) {
		try {
			return blast.checkStatus(jobid);
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
		try {
			String status = blast.checkStatus(job.getId());
			if (done.equals(status)) {
				return true;
			}
		} catch (RemoteException e) {
			new BlastClientException(e);
		}
		return false;
	}

	/**
	 * Retrieves the result if the job is finished.
	 * 
	 * @param job
	 * @return string
	 */
	public String getResult(Job job) {
		String result = null;
		if (isFinished(job)) {
			try {
				String type = (fileFormatXml ? "toolxml" : "tooloutput");
				byte[] resultbytes = blast.poll(job.getId(), type);
				result = new String(resultbytes);

			} catch (RemoteException e) {
				new BlastClientException(e);
			}
		}
		return result;
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
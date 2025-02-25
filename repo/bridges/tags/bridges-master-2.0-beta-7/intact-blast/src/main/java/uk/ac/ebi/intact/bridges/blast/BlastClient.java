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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
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
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class BlastClient {

	private double		threshold;
	private Set<String>	againstUniprotAc;
	// checks the format of the accession number
	static String		uniprotTermExpr	= "\\w{6,6}";

	/**
	 * Constructor
	 * 
	 * @param threshold
	 *            a double value
	 */
	public BlastClient(double threshold) {
		this.threshold = threshold;
	}

	/**
	 * the result is of the form of
	 * uniprotAcAligned,uniprotAcAlignment,uniprotAcAlignment2 ...
	 * 
	 * @param reader
	 *            where the blast output is
	 * @return
	 */
	private String processResults(String ac1, Reader reader) {
		String processedAlign = "";
		BufferedReader br = new BufferedReader(reader);
		String line;
		boolean start = false;
		boolean stop = false;
		try {
			while (((line = br.readLine()) != null) && !stop) {
				if (!start && Pattern.matches("^Sequences.*", line)) {
					start = true;
				} else if (start) {
					if (Pattern.matches("^UNIPROT.*\\w{6,6}.*", line)) {
						String[] strs = line.split("\\s+");
						if (ac1.equals(strs[1])
								|| (isUniprotAcInAgainstSet(strs[1]) && isBelowThreshold(strs[strs.length - 2]))) {
							processedAlign = processedAlign.concat(strs[1] + ",");
						}
					}
					if (Pattern.matches("^>UNIPROT", line)) {
						stop = true;
					}
				}
			}
		} catch (IOException e) {
			new BlastClientException(e);
		}
		return processedAlign;
	}

	private boolean isUniprotAcInAgainstSet(String ac) {
		if (againstUniprotAc.contains(ac)) {
			return true;
		}
		return false;
	}

	private boolean isBelowThreshold(String string) {
		Double d = Double.valueOf(string).doubleValue();

		if (d < threshold) {
			return true;
		}

		return false;
	}

	/**
	 * blasts a list of uniprotAc1 against a list of uniprotAc2
	 * 
	 * @param uniprotAc1
	 * @param uniprotAc2
	 * @return a list of strings, where each string has the format
	 *         uniprotAc1,uniprotAC_align1, uniprotAC_align2,...
	 */
	public List<String> blast(Set<String> uniprotAc1, Set<String> uniprotAc2) {
		if (uniprotAc1 == null || uniprotAc2 == null) {
			new BlastClientException(new NullPointerException("the uniprotAc lists must not be null!"));
		}
		List<String> alignments = new ArrayList<String>();
		this.againstUniprotAc = uniprotAc2;
		for (String ac1 : uniprotAc1) {
			try {
				File tmpFile = File.createTempFile("blastOutput" + ac1 + ".", "txt");
				// blasting the uniprotAc against uniprot
				FileWriter fw = new FileWriter(tmpFile);
				blastUniprot(ac1, fw);

				// processes the results
				FileReader fr = new FileReader(tmpFile);
				String align = processResults(ac1, fr);
				alignments.add(align);
			} catch (FileNotFoundException e) {
				new BlastClientException(e);
			} catch (IOException e) {
				new BlastClientException(e);
			}
		}

		return alignments;
	}

	/**
	 * blasts the specified uniprotAc against uniprot db, and writes the output
	 * to the specified writer
	 * 
	 * @param uniprotAc
	 * @param writer
	 */
	public void blastUniprot(String uniprotAc, Writer writer) {
		if (!properFormat(uniprotAc)) {
			new BlastClientException(new IllegalArgumentException("uniprotAc not in the uniprot format: >" + uniprotAc
					+ "<"));
		}
		if (writer == null) {
			new BlastClientException(new NullPointerException("writer is null!"));
		}

		InputParams params = new InputParams();

		params.setProgram("blastp");
		params.setDatabase("uniprot");
		params.setEmail("iarmean@ebi.ac.uk");

		Data inputs[] = new Data[1];

		Data input = new Data();
		input.setType("sequence");
		input.setContent("uniprot:" + uniprotAc);
		inputs[0] = input;

		WSWUBlastService service = new WSWUBlastServiceLocator();
		WSWUBlast_PortType blast;
		try {
			blast = service.getWSWUBlast();
			String jobid = blast.runWUBlast(params, inputs);

			byte[] resultbytes = blast.poll(jobid, "tooloutput");
			String result = new String(resultbytes);
			printRawResults(result, writer);
		} catch (ServiceException e) {
			new BlastClientException(e);
		} catch (RemoteException e) {
			new BlastClientException(e);
		}
	}

	private boolean properFormat(String uniprotAc) {
		if (Pattern.matches(uniprotTermExpr, uniprotAc)) {
			return true;
		}
		return false;
	}

	private void printRawResults(String result, Writer w) {
		try {
			w.append(result);
			w.close();
		} catch (IOException e) {
			new BlastClientException(e);
		}

	}

	void printResults(List<String> results, Writer w) {
		for (String result : results) {
			try {
				w.append(result + "\n");
			} catch (IOException e) {
				new BlastClientException(e);
			}
		}
		try {
			w.close();
		} catch (IOException e) {
			new BlastClientException(e);
		}
	}
}
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

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import sun.awt.GlobalCursorManager;
import uk.ac.ebi.intact.bridges.blast.generated.Data;
import uk.ac.ebi.intact.bridges.blast.generated.InputParams;
import uk.ac.ebi.intact.bridges.blast.generated.WSFile;
import uk.ac.ebi.intact.bridges.blast.generated.WSWUBlastService;
import uk.ac.ebi.intact.bridges.blast.generated.WSWUBlastServiceLocator;
import uk.ac.ebi.intact.bridges.blast.generated.WSWUBlast_PortType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * TODO comment this
 * 
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class BlastClient {

	private double	threshold;
	private String	tmpDir;
	private List<String> againstUniprotAc; 

	/**
	 * Constructor
	 * 
	 * @param threshold
	 *            a double value
	 */
	public BlastClient(double threshold) {
		this.threshold = threshold;
		this.tmpDir = "E:\\tmp\\";
	}

	

	/**
	 * 
	 * @param reader
	 *            where the blast output is
	 * @return
	 */
	public String processResults(String ac1, Reader reader) {
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
						if (ac1.equals(strs[1]) ||( isUniprotAcInHighConfidenceSet(strs[1]) && isBelowThreshold(strs[strs.length - 2]))) {
							processedAlign = processedAlign.concat(strs[1] + ",");
						}
					}
					if (Pattern.matches("^>UNIPROT", line)) {
						stop = true;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return processedAlign;
	}

	private boolean isUniprotAcInHighConfidenceSet(String ac) {
		if (againstUniprotAc.contains(ac)){
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
	public List<String> blast(List<String> uniprotAc1, List<String> uniprotAc2) {
		List<String> alignments = new ArrayList<String>();
		this.againstUniprotAc = uniprotAc2;
		for (String ac1 : uniprotAc1) {
			String filePath = tmpDir + "blastOut.txt";
			try {
				// blasting the uniprotAc against uniprot
				FileWriter fw = new FileWriter(filePath);
				blastUniprot(ac1, fw);
				
				// processes the results
				FileReader fr = new FileReader(filePath);
				String align = processResults(ac1, fr);
				alignments.add(align);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return alignments;
	}

	public void blastUniprot(String uniprotAc, Writer w) {
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
			printRawResults(result, w);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void printRawResults(String result, Writer w) {
		try {
			w.append(result);
			w.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void printResults(List<String> results, Writer w) {
		for (String result : results) {
			try {
				w.append(result +"\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		try {
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/*
	 * private AccessionMapperService accessionMapperService;
	 * 
	 * public PicrClient(){
	 * this("http://www.ebi.ac.uk/Tools/picr/service?wsdl"); }
	 * 
	 * public PicrClient(String wsdlUrl){ try { accessionMapperService = new
	 * AccessionMapperService(new URL(wsdlUrl), new
	 * QName("http://www.ebi.ac.uk/picr/AccessionMappingService",
	 * "AccessionMapperService")); } catch (MalformedURLException e) {
	 * e.printStackTrace(); } }
	 * 
	 * public AccessionMapperInterface getAccessionMapperPort() { return
	 * accessionMapperService.getAccessionMapperPort(); }
	 * 
	 * 
	 * public String getUPI(String accession, PicrSearchDatabase ... databases) {
	 * List<UPEntry> upEntries =
	 * getAccessionMapperPort().getUPIForAccession(accession, null,
	 * databaseEnumToList(databases), null, true);
	 * 
	 * if (upEntries.isEmpty()) { return null; }
	 * 
	 * return upEntries.iterator().next().getUPI(); }
	 * 
	 * private List<String> databaseEnumToList(PicrSearchDatabase ...
	 * databases) { List<String> databaseNames = new ArrayList<String>(databases.length);
	 * 
	 * for (PicrSearchDatabase database : databases) {
	 * databaseNames.add(database.toString()); }
	 * 
	 * return databaseNames; }
	 * 
	 * public static void main(String[] args) { PicrClient client = new
	 * PicrClient();
	 * 
	 * System.out.println(client.getUPI("NP_417804",
	 * PicrSearchDatabase.REFSEQ)); }
	 */
}
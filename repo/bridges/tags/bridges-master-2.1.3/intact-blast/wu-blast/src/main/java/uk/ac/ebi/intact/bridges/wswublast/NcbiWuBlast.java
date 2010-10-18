/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.wswublast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import uk.ac.ebi.intact.bridges.wswublast.client.WuBlastClientException;
import uk.ac.ebi.intact.bridges.wswublast.model.BlastInput;
import uk.ac.ebi.intact.bridges.wswublast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.wswublast.model.BlastOutput;
import uk.ac.ebi.intact.bridges.wswublast.model.BlastResult;
import uk.ac.ebi.intact.bridges.wswublast.model.Job;
import uk.ac.ebi.intact.bridges.wswublast.model.UniprotAc;

/**
 * TODO comment this ... someday + implement it
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since
 * 
 * <pre>
 * 13 Sep 2007
 * </pre>
 */
public class NcbiWuBlast extends AbstractBlastService {

	NcbiWuBlast() throws BlastServiceException {
		super(new File("testDir"), "testTableName", 20);
		// TODO Auto-generated constructor stub
	}

	private Float	threshold;
	private File	workDir;

	// TODO : this wswublast will use the cmd line wublast, and get and file/txt
	// output

	public BlastJobStatus checkStatus(Job job) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getResult(Job job, Boolean isXml) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Job> runBlast(Set<UniprotAc> uniprotAcs) {
		// TODO Auto-generated method stub
		return null;
	}

	public Job runBlast(UniprotAc uniprotAc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Job runBlast(BlastInput blastInput) throws WuBlastClientException {
		// TODO Auto-generated method stub
		return null;
	}

	public BlastOutput getResult(Job job) {
		// TODO Auto-generated method stub
		return null;
	}

	public BlastResult processOutput(File blastFile) {
		//processTxtOutput(ac, againstProteins);
		return null;
	}

	private String processTxtOutput(String ac, Set<String> againstProteins) {
		String result = "";

		try {
			FileReader fr = new FileReader(new File(workDir.getPath(), ac + ".txt"));
			BufferedReader br = new BufferedReader(fr);
			String line;
			boolean start = false;
			boolean stop = false;

			while (((line = br.readLine()) != null) && !stop) {
				if (!start && Pattern.matches("^Sequences.*", line)) {
					start = true;
				} else if (start) {
					if (Pattern.matches("^UNIPROT.*\\w{6,6}.*", line)) {
						String[] strs = line.split("\\s+");
						String accession = strs[1];
						Float evalue = new Float(strs[strs.length - 2]);

						if (ac.equals(accession)) {
							result = ac;
						} else {
							if (evalue < threshold && againstProteins.contains(accession)) {
								result += "," + accession;
							}
						}
					}
					if (Pattern.matches("^>UNIPROT", line)) {
						stop = true;
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
}

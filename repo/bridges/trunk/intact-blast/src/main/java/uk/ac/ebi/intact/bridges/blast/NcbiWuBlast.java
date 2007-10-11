/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import uk.ac.ebi.intact.bridges.blast.client.BlastClientException;
import uk.ac.ebi.intact.bridges.blast.model.BlastInput;
import uk.ac.ebi.intact.bridges.blast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.blast.model.BlastOutput;
import uk.ac.ebi.intact.bridges.blast.model.BlastResult;
import uk.ac.ebi.intact.bridges.blast.model.Job;
import uk.ac.ebi.intact.bridges.blast.model.UniprotAc;

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

	// TODO : this blast will use the cmd line wublast, and get and file/txt
	// output

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
	public Job runBlast(BlastInput blastInput) throws BlastClientException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public BlastResult processOutput(File blastFile) {
		//processTxtOutput(ac, againstProteins);
		return null;
	}

	public BlastOutput getResult(Job job) {
		// TODO Auto-generated method stub
		return null;
	}

	

//	private void runBlast(Set<String> uniprotAc) {
//		List<Job> jobs = bc.blast(uniprotAc);
//
//		try {
//			saveJobs(jobs, new FileWriter(new File(workDir, "jobList.txt")));
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		if (jobs.size() == 0) {
//			return false;
//		}
//
//		List<Job> notFinishedJobs = jobs;
//		for (Job job : jobs) {
//			String result = "";
//			String status = bc.checkStatus(job.getId());
//			while (status.equals("RUNNING")) {
//				try {
//					Thread.sleep(10000); // 10 000 millisec = 10 sec
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//				status = bc.checkStatus(job.getId()); // check for the job
//				// status
//			}
//
//			if (status.equals("DONE")) {
//				result = bc.getResult(job); // when done, get the results
//				saveResult(job,result, true);
//				
//				
//				
////				try {
////					
////					saveJobeDone(job, new FileWriter(new File(workDir, "jobsDone.txt"), true));
////				} catch (IOException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////				notFinishedJobs.remove(job);
////				result = bc.getResult(job); // when done, get the results
////				writeResultsToWorkDir(job, result);
////				processBlastOutput(job.getUniprotAc(), bc.isFileFormatXml(), againstProteins, writer);
//			} else {
//				log.info("Error with job: " + job + " (" + result + ")");
//			}
//		}
//		return true;
//	}

}

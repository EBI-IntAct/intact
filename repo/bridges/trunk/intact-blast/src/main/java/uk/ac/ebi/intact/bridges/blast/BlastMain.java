/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ebi.intact.bridges.blast.client.BlastClientException;
import uk.ac.ebi.intact.bridges.blast.jdbc.BlastJobEntity;
import uk.ac.ebi.intact.bridges.blast.model.BlastResult;
import uk.ac.ebi.intact.bridges.blast.model.Hit;
import uk.ac.ebi.intact.bridges.blast.model.UniprotAc;

/**
 * TODO comment this ... someday
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since
 * 
 * <pre>
 * 18 Sep 2007
 * </pre>
 */
@Deprecated
public class BlastMain {

	/**
	 * @param args
	 * @throws BlastClientException 
	 */
	public static void main(String[] args) throws BlastServiceException {
		
	//	getSpecificResult();
		importCsv();
		
//		
//		Set<UniprotAc> prots = new HashSet<UniprotAc>();
//		prots.add(new UniprotAc("Q94AH9"));
//
//		int total = prots.size();
//
//		System.out.println(total + " proteins to process");
//		while (prots.size() > 20) {
//			Set<UniprotAc> toGet = only20(prots, 20);
//			getBlasts(toGet);
//		}
//		if (prots.size() != 0 && prots.size() <= 20) {
//			getBlasts(prots);
//		}
	}

	private static void importCsv() throws BlastServiceException {
		File testDir = new File(getTargetDirectory().getPath(), "20071016_iarmean");
		testDir.mkdir();
		long start = System.currentTimeMillis();
		String email = "iarmean@ebi.ac.uk";
		String tableName = "job";
		int nr = 20;
		File dbFile =  new File (getTargetDirectory().getPath(), "dbFolder");
		BlastService blast = new EbiWsWUBlast(dbFile,tableName, testDir, email, nr);
		File csvFile = new File("dump.csv");
		blast.importCsv(csvFile);
		
	}
	
	private static void getSpecificResult() throws BlastServiceException {
		File testDir = new File(getTargetDirectory().getPath(), "20071016_iarmean");
		testDir.mkdir();

		long start = System.currentTimeMillis();
		String email = "iarmean@ebi.ac.uk";
		String tableName = "job";
		int nr = 20;
		File dbFolder = new File (getTargetDirectory().getPath(), "dbFolder");
		dbFolder.mkdir();
		BlastService blast = new EbiWsWUBlast(dbFolder, tableName, testDir, email, nr);
		
		Set<UniprotAc> uniprotAcs = new HashSet<UniprotAc>();
		uniprotAcs.add(new UniprotAc("P14734"));
		List<BlastResult> results = blast.fetchAvailableBlasts(uniprotAcs);
		
	}

	private static Set<UniprotAc> only20(Set<UniprotAc> prots, int nr) {
		Set<UniprotAc> protsSmall = new HashSet<UniprotAc>();
		for (int i = 0; i < nr; i++) {
			protsSmall.add((UniprotAc) prots.toArray()[i]);
		}
		prots.removeAll(protsSmall);
		return protsSmall;
	}

	private static void getBlasts(Set<UniprotAc> prots) throws BlastServiceException {
		File testDir = new File(getTargetDirectory().getPath(), "20071016_iarmean");
		testDir.mkdir();

		long start = System.currentTimeMillis();
		String email = "iarmean@ebi.ac.uk";
		String tableName = "job";
		int nr = 20;
		File dbFolder = new File (getTargetDirectory().getPath(), "dbFolder");
		BlastService blast = new EbiWsWUBlast(dbFolder, tableName, testDir, email, nr);

		List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>();

		for (UniprotAc prot : prots) {
			BlastJobEntity job = blast.submitJob(prot);
			if (job != null) {
				jobs.add(job);
			}
		}

		// List<BlastJobEntity> jobs = blast.submitJobs(prots)
		
		List<BlastJobEntity> runningJobs = blast.fetchRunningJobs();
		while (runningJobs.size() != 0) {
			try {
				Thread.sleep(5000); // 5 000 millisec = 5 sec
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			runningJobs = blast.fetchRunningJobs();
		}
		
		List<BlastResult> results = blast.fetchAvailableBlasts(jobs);
		long end = System.currentTimeMillis();
		System.out.println("time for " + prots.size() + " prots : " + (end - start) + " milisec");

	}

	private static void printResult(BlastResult result, Writer writer) {
		try {
			writer.append(result.getUniprotAc() + " - alignmenthits \n");
			for (Hit hit : result.getHits()) {
				String align = hit.getUniprotAc() + ":" + hit.getEValue() + "\n";
				writer.append(align);
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static File getTargetDirectory() {
		String outputDirPath = BlastMain.class.getResource("/").getFile();

		File outputDir = new File(outputDirPath);
		// we are in src/main/resources , move 3 up

		// TODO: for eclipse use : outputDir = outputDir.getParentFile().getParentFile().getParentFile();
		// TODO: for unix, cygwin use:
		outputDir = outputDir.getParentFile();

		// we are in confidence-score folder, move 1 down, in target folder
		String outputPath;
		// TODO: for eclipse use: outputPath = outputDir.getPath() + "/target/";
		// TODO: for unix, cygwin use:
		outputPath = outputDir.getPath();

		outputDir = new File(outputPath);
		outputDir.mkdir();

		return outputDir;
	}
}

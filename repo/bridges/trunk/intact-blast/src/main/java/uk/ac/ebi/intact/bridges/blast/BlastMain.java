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
public class BlastMain {

	/**
	 * @param args
	 * @throws BlastClientException 
	 */
	public static void main(String[] args) throws BlastServiceException {
		Set<UniprotAc> prots = new HashSet<UniprotAc>();
		prots.add(new UniprotAc(""));

		int total = prots.size();

		System.out.println(total + " proteins to process");
		while (prots.size() > 20) {
			Set<UniprotAc> toGet = only20(prots, 20);
			getBlasts(toGet);
		}
		if (prots.size() != 0 && prots.size() <= 20) {
			getBlasts(prots);
		}
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
		String testDirPath = "E:/";
		File testDir = new File(testDirPath, "20071016_iarmean");
		testDir.mkdir();

		long start = System.currentTimeMillis();
		String email = "iarmean@ebi.ac.uk";
		BlastService blast = new EbiWsWUBlast(testDir, email);

		List<BlastJobEntity> jobs = new ArrayList<BlastJobEntity>();
		for (UniprotAc prot : prots) {
			BlastJobEntity job = blast.submitJob(prot);
			if (job != null) {
				jobs.add(job);
			}
		}

		// List<BlastJobEntity> jobs = blast.submitJobs(prots);
		List<BlastJobEntity> pendingJobs = blast.fetchPendingJobs();
		while (pendingJobs.size() != 0) {
			try {
				Thread.sleep(5000); // 5 000 millisec = 5 sec
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pendingJobs = blast.fetchPendingJobs();
		}
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
}

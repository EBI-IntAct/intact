import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ebi.intact.bridges.blast.BlastClient;
import uk.ac.ebi.intact.bridges.blast.Job;

/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

/**
 * TODO comment this ... someday
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since <pre>17 Sep 2007</pre>
 */
public class BlastMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BlastClient bc = new BlastClient();
		bc.setEmail("iarmean@ebi.ac.uk");
		Job job = bc.blast("P12345");
		assertNotNull(job);
		assertEquals("P12345", job.getUniprotAc());
		if (bc.isFinished(job)) {
			String result = bc.getResult(job);
			System.out.println(result);
			assertNotNull(result);
		}
		runAsync();
	}

	private static void runAsync() {
		BlastClient bc = new BlastClient();
		Set<String> uniprotAcs = new HashSet<String>();
		uniprotAcs.add("P12345");
		uniprotAcs.add("Q12345");
		uniprotAcs.add("P17795");
		List<Job> jobs = bc.blast(uniprotAcs);

		for (Job job : jobs) {
			String result = "RUNNING";
			String status = bc.checkStatus(job.getId());
			while (status.equals("RUNNING")) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					fail();
				}

				status = bc.checkStatus(job.getId()); // check for the job
				// status
			}
			if (status.equals("DONE")) {
				result = bc.getResult(job); // whe done, get the results
				System.out.println(job);
			} else {
				System.out.println("Error with job: " + job + " (" + result + ")");
			}
		}		
	}

}

package uk.ac.ebi.intact.bridges.blast.client;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ebi.intact.bridges.blast.model.BlastInput;
import uk.ac.ebi.intact.bridges.blast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.blast.model.BlastOutput;
import uk.ac.ebi.intact.bridges.blast.model.Job;
import uk.ac.ebi.intact.bridges.blast.model.UniprotAc;

/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */

/**
 * TODO comment this ... someday
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since
 * 
 * <pre>
 * 17 Sep 2007
 * </pre>
 */
public class BlastClientMain {

	/**
	 * @param args
	 * @throws BlastClientException
	 */
	public static void main(String[] args) throws BlastClientException {
		String email = "iarmean@ebi.ac.uk";	
		runAsync(email);
	}
	
	private static void runAsync(String email) throws BlastClientException {
		BlastClient bc = new BlastClient(email);
		Set<BlastInput> blastInputs = new HashSet<BlastInput>();
		blastInputs.add(new BlastInput(new UniprotAc("P12345")));
		blastInputs.add(new BlastInput(new UniprotAc("Q12345")));
		blastInputs.add(new BlastInput(new UniprotAc("P17795")));
		List<Job> jobs = bc.blast(blastInputs);

		for (Job job : jobs) {
			BlastJobStatus status = bc.checkStatus(job);
			while (BlastJobStatus.RUNNING.equals(status)) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					fail();
				}
				// check for the job status
				status = bc.checkStatus(job);
			}
			if (BlastJobStatus.DONE.equals(status)) {
				BlastOutput result = bc.getResult(job); // whe done, get the results
				assertNotNull(result);
				System.out.println(job);
			} else {
				System.out.println("Error with job: " + job );
			}
		}
	}

}

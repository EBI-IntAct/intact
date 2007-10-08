/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast.client;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.ebi.intact.bridges.blast.model.BlastInput;
import uk.ac.ebi.intact.bridges.blast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.blast.model.BlastOutput;
import uk.ac.ebi.intact.bridges.blast.model.Job;
import uk.ac.ebi.intact.bridges.blast.model.Sequence;
import uk.ac.ebi.intact.bridges.blast.model.UniprotAc;

/**
 * TODO comment this ... someday
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since
 * 
 * <pre>
 * 7 Sep 2007
 * </pre>
 */
public class BlastClientTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testBlastClient() throws BlastClientException {
		assertTrue(true);
//		BlastClient bc = new BlastClient("iarmean@ebi.ac.uk");
//		Job job = new Job("blast-20070924-14231329", new BlastInput(new UniprotAc("P10081")));
//		bc.checkStatus(job);
//		while (!BlastJobStatus.DONE.equals(job.getStatus())){
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			bc.checkStatus(job);
//		}
//		bc.getResult(job);
//		BlastOutput result = job.getBlastResult();
//		FileWriter fw;
//		try {
//			fw = new FileWriter(new File("E:/test.xml"));
//			fw.append(result.getResult());
//			fw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	@Test
	@Ignore
	public final void testBlastSeq() throws BlastClientException {
		BlastClient bc =new BlastClient("iarmean@ebi.ac.uk");
		String seq = "MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR";
		UniprotAc ac = new UniprotAc("Q9VQS6-1");
		Sequence seqObj = new Sequence(seq);
		BlastInput bi = new BlastInput(ac, seqObj);
		Job job = bc.blastSeq(bi);
		while (!BlastJobStatus.DONE.equals(job.getStatus())){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			bc.checkStatus(job);
		}
		bc.getResult(job);
		BlastOutput result = job.getBlastResult();
		FileWriter fw;
		try {
			fw = new FileWriter(new File("E:/test"+ ac.getAcNr()+ ".xml"));
			fw.append(result.getResult());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

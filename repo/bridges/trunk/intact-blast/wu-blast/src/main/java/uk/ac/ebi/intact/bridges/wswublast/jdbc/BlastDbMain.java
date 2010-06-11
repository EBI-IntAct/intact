/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.wswublast.jdbc;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import uk.ac.ebi.intact.bridges.wswublast.model.BlastJobStatus;

/**
 * Main for the db strategy.
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since
 * 
 * <pre>
 * 12 Sep 2007
 * </pre>
 */
public class BlastDbMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String tableName = "testBlastDb";
		File dbFolder = new File("E:/tmp/testFolderDB");
		dbFolder.mkdir();
		BlastJobDao blastJobDao;
		try {
			blastJobDao = new BlastJobDao(dbFolder, tableName);
			BlastJobEntity toSave = new BlastJobEntity("3", "P12345", BlastJobStatus.FINISHED, new File("C:/tmp/"), Timestamp.valueOf("2007-10-20 10:30:25"));
			blastJobDao.saveJob(toSave);
			BlastJobEntity readJob = blastJobDao.getJobById("3");
			System.out.println(readJob);
			
			List<BlastJobEntity> jobs = blastJobDao.selectAllJobs();
			for (BlastJobEntity job : jobs) {
				System.out.println(job);
			}
		} catch (BlastJdbcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done.");
	}
}

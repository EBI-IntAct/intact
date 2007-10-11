package uk.ac.ebi.intact.bridges.blast.jdbc;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.intact.bridges.blast.jdbc.BlastDb;

public class BlastDbTest {

	BlastDb	db;
	File	dbFolder;

	@Before
	public void setUp() throws Exception {
		File testDir = getTargetDirectory();
		dbFolder = new File(testDir, "BlastDbTest");
		db = new BlastDb(dbFolder);
	}

	@After
	public void tearDown() throws Exception {
		db.closeDb();
	}

	@Test
	public final void testCreateJobTable() throws BlastJdbcException {
		Connection conn = db.getConn();
		assertNotNull(conn);
		db.createJobTable("dbTest");
		assertTrue(db.jobTableExists("dbTest"));
	}

	@Test
	public final void testDropJobTable() throws BlastJdbcException {
		db.dropJobTable("dbTest");
		assertFalse(db.jobTableExists("dbTest"));
	}

	private File getTargetDirectory() {
		String outputDirPath = BlastDbTest.class.getResource("/").getFile();
		Assert.assertNotNull(outputDirPath);
		File outputDir = new File(outputDirPath);
		// we are in intact-blast/target/test-classes , move 1 up
		outputDir = outputDir.getParentFile();
		Assert.assertNotNull(outputDir);
		Assert.assertTrue(outputDir.getAbsolutePath(), outputDir.isDirectory());
		Assert.assertEquals("target", outputDir.getName());
		return outputDir;
	}
}

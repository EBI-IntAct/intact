package uk.ac.ebi.intact.bridges.blast.jdbc;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.intact.bridges.blast.jdbc.BlastDb;

public class BlastDbTest {

	BlastDb	db;
	File	dbFolder;

	@Before
	public void setUp() throws Exception {
		dbFolder = new File("BlastDbTestFolder");
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
}

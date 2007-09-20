package uk.ac.ebi.intact.bridges.blast.jdbc;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.intact.bridges.blast.jdbc.BlastDb;

public class BlastDbTest {

	BlastDb db;
	
	@Before
	public void setUp() throws Exception {
		db = new BlastDb();
	}

	@After
	public void tearDown() throws Exception {
		db.closeDb();
	}

	@Test
	public final void testCreateJobTable() {
		Connection conn = db.getConn();
		assertNotNull(conn);
		try {
			db.createJobTable("dbTest");
			assertTrue(db.jobTableExists("dbTest"));
		} catch (BlastJdbcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	@Test
	public final void testDropJobTable() {
		try {
			db.dropJobTable("dbTest");
			assertFalse(db.jobTableExists("dbTest"));
		} catch (BlastJdbcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}

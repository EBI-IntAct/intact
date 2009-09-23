package uk.ac.ebi.intact.bridges.blast.jdbc;

import junit.framework.Assert;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class BlastDbTest {

    private BlastDb db;
    private File dbFolder;

    @Before
    public void setUp() throws Exception {
        File testDir = getTargetDirectory();
        dbFolder = new File( testDir, "BlastDbTest" );
        db = new BlastDb( dbFolder );
    }

    @After
    public void tearDown() throws Exception {
        db.closeDb();
    }

    @Test
    public final void testCreateJobTable() throws BlastJdbcException {
        Connection conn = null;
        try {
            conn = db.getConnection();
            assertNotNull( conn );
            db.createJobTable( conn, "dbTest" );
            assertTrue( db.jobTableExists( conn, "dbTest" ) );
        } finally {
            if ( conn != null ) {
                try {
                    conn.close();
                } catch ( SQLException e ) {
                    throw new BlastJdbcException( e );
                }
            }
        }
    }

    @Test
    public final void testDropJobTable() throws BlastJdbcException {
        Connection conn = null;
        try {
            conn = db.getConnection();
            db.dropJobTable( conn, "dbTest" );
            assertFalse( db.jobTableExists( conn, "dbTest" ) );
        } finally {
            if ( conn != null ) {
                try {
                    conn.close();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public final void testMultipleConnections() throws BlastJdbcException {
        Connection conn1 = null;
        Connection conn2 = null;

        try {
            conn1 = db.getConnection();
            assertNotNull( conn1 );
            conn2 = db.getConnection();
            assertNotNull( conn2 );
            assertNotSame( conn1, conn2 );
            db.createJobTable( conn1, "myTestJob1" );
            assertTrue( db.jobTableExists( conn1, "myTestJob1" ) );
            assertTrue( db.jobTableExists( conn2, "myTestJob1" ) );
            db.dropJobTable( conn2, "myTestJob1" );
            assertFalse( db.jobTableExists( conn2, "myTestJob1" ) );
        } finally {
            if ( conn1 != null ) {
                try {
                    conn1.close();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
            if ( conn2 != null ) {
                try {
                    conn2.close();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Test
    public final void testMultipleDbObjects() throws BlastJdbcException {
        BlastDb db2 = new BlastDb(dbFolder);
        assertNotNull( db2);
        assertNotSame( db2,db);
        Connection c1 = null;
        Connection c2 = null;
        try {
            c1 = db.getConnection();
            assertNotNull( c1 );
            c2 = db2.getConnection();
            assertNotNull( c2 );
            assertNotSame( c1, c2 );
            db.createJobTable( c1, "myTestJob1" );
            assertTrue( db.jobTableExists( c1, "myTestJob1" ) );
            assertTrue( db2.jobTableExists( c2, "myTestJob1" ) );
            assertTrue( db.jobTableExists( c2, "myTestJob1" ) );
            db.dropJobTable( c2, "myTestJob1" );
            assertFalse( db.jobTableExists( c2, "myTestJob1" ) );



        }finally {
            if ( c1 != null ) {
                try {
                    c1.close();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
            if ( c2 != null ) {
                try {
                    c2.close();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File getTargetDirectory() {
        String outputDirPath = BlastDbTest.class.getResource( "/" ).getFile();
        Assert.assertNotNull( outputDirPath );
        File outputDir = new File( outputDirPath );
        // we are in intact-blast/target/test-classes , move 1 up
        outputDir = outputDir.getParentFile();
        Assert.assertNotNull( outputDir );
        Assert.assertTrue( outputDir.getAbsolutePath(), outputDir.isDirectory() );
        Assert.assertEquals( "target", outputDir.getName() );
        return outputDir;
    }
}

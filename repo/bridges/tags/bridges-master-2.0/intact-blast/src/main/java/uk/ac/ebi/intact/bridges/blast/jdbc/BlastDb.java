/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for connecting to the blastDb, for storing the blastJobs.
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since
 * 
 * <pre>
 * 13 Sep 2007
 * </pre>
 */
public class BlastDb {

	private Connection	conn;
	private Statement	stat;

	public BlastDb(File dbFolder) throws BlastJdbcException {
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection("jdbc:h2:" + dbFolder.getPath() + "/blast", "sa", "");//blastDb/blast", "sa", "");
			stat = conn.createStatement();
		} catch (ClassNotFoundException e) {
			throw new BlastJdbcException(e);
		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}
	}

	public void createJobTable(String tableName) throws BlastJdbcException {
		try {
			String create = "CREATE TABLE IF NOT EXISTS "+tableName + " (" + " JobId VARCHAR(255) PRIMARY KEY, "
					+ "uniprotAc VARCHAR(255)," + "status VARCHAR(15), " + "resultPath VARCHAR(255), " + "timestamp TIMESTAMP);";

			stat.execute(create);
		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}
	}

	public boolean jobTableExists(String tableName) throws BlastJdbcException {
		try {
			DatabaseMetaData dbMeta = conn.getMetaData();

			// check if "employee" table is there
			tableName= tableName.toUpperCase();
			ResultSet checkTable = dbMeta.getTables(null, null, tableName, null);
			String tableFound = null;
			while (checkTable.next()) {
				tableFound = checkTable.getString("TABLE_NAME");
				// System.out.println(tableName);
			}
			if (tableFound != null) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}
	}

	public void dropJobTable(String tableName) throws BlastJdbcException {
		try {
			String drop = "DROP TABLE IF EXISTS "+tableName + ";";
			stat.execute(drop);
		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}
	}

	public void closeDb() throws BlastJdbcException {
		try {
			conn.close();
		} catch (SQLException e) {
			throw new BlastJdbcException(e);
		}
	}

	public Connection getConn() {
		return conn;
	}

	public Statement getStat() {
		return stat;
	}
}

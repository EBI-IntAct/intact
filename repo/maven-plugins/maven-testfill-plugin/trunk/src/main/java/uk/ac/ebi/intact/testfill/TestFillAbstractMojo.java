/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.testfill;

import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;


public abstract class TestFillAbstractMojo extends AbstractMojo
{

    /**
    * Project instance, used to add new source directory to the build.
    * @parameter default-value="${project}"
    * @required
    * @readonly
    */
    private MavenProject project;

    /**
     * @parameter default-value="postgres"
     * @required
     */
    private String database;

    /**
     * @parameter default-value="jdbc:postgres://localhost/test"
     * @required
     */
    private String url;

    /**
     * @parameter default-value="postgres"
     * @required
     */
    private String user;

    /**
     * @parameter default-value="postgres"
     * @required
     */
    private String password;

    protected void loadDriver() throws MojoExecutionException
    {
        if (isPostgresDatabase())
        {
            try
            {
                Class.forName("org.postgresql.Driver");
            }
            catch (ClassNotFoundException e)
            {
                throw new MojoExecutionException("Driver not found", e);
            }
        }

        if (isOracleDatabase())
        {
            try
            {
                Class.forName("com.oracle.Driver.PUTDRIVER");
            }
            catch (ClassNotFoundException e)
            {
                throw new MojoExecutionException("Driver not found", e);
            }
        }
    }

    protected Connection getConnection() throws MojoExecutionException
    {
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(getUrl(), getUser(), getPassword());
        }
        catch (SQLException e)
        {
            throw new MojoExecutionException("Exception getting connection", e);
        }

        return connection;
    }

    protected boolean isPostgresDatabase()
    {
        return database.equalsIgnoreCase("postgres");
    }

    protected boolean isOracleDatabase()
    {
        return database.equalsIgnoreCase("oracle");
    }

    public MavenProject getProject()
    {
        return project;
    }

    public String getDatabase()
    {
        return database;
    }

    public String getUrl()
    {
        return url;
    }

    public String getUser()
    {
        return user;
    }

    public String getPassword()
    {
        return password;
    }
}

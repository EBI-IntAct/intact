/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.testfill;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Generates a properties file with the topic names as keys and the classes as values
 *
 * @goal onlyCV
 *
 * @phase generate-sources
 */
public class OnlyCvMojo extends TestFillAbstractMojo
{
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getLog().info("OnlyCvMojo in Action");

        Connection connection = getConnection();


    }
}

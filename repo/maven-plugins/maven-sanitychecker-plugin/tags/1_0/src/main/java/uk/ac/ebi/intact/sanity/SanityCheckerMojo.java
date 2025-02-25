/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;
import uk.ac.ebi.intact.persistence.util.HibernateUtil;
import uk.ac.ebi.intact.util.sanityChecker.SanityChecker;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Generates a properties file with the topic names as keys and the classes as values
 *
 * @goal run
 * 
 * @phase compile
 */
public class SanityCheckerMojo
        extends AbstractMojo
{

    /**
    * Project instance, used to add new source directory to the build.
    * @parameter default-value="${project}"
    * @required
    * @readonly
    */
    private MavenProject project;

    /**
    * The set of dependencies required by the project
    * @parameter default-value="${project.artifacts}"
    * @required
    * @readonly
    */
    private java.util.Set<Artifact> dependencies;

    /**
    * project-helper instance, used to make addition of resources
    * simpler.
    * @component
    */
    private MavenProjectHelper helper;

    /**
     * @parameter
     * @required
     */
    private List<Curator> curators;

    /**
     * @parameter default-value="http://www.ebi.ac.uk/interpro/internal-tools/intacttest"
     * @required
     */
    private String editorBaseUrl;

    /**
     * @parameter default-value="cleroy@ebi.ac.uk"
     */
    private String mailFromAddress;

    /**
     * @parameter default-value="${project.build.outputDirectory}/hibernate.cfg.xml"
     */
    private String hibernateConfigPath;


    public void execute() throws MojoExecutionException
    {
        getLog().info("Sanity Checker Mojo in action");

        getLog().info("List of Curators");
        getLog().info("----------------");

        for (Curator curator : curators)
        {
            getLog().info("Curator: "+curator.getId()+"\t"+curator.getMail()+"\tAdmin: "+curator.isAdmin());
        }

        getLog().debug("Using hibernate config file: "+hibernateConfigPath);
        HibernateUtil.setHibernateConfig(new File(hibernateConfigPath));

        try
        {
            getLog().info("Instantiating SanityChecker");

            IntactTransaction tx = DaoFactory.beginTransaction();

            SanityChecker sanityChecker = new SanityChecker(curators, editorBaseUrl, mailFromAddress);

             getLog().info("Starting Sanity Check");
             sanityChecker.start();

            tx.commit();
        }
        catch (SQLException e)
        {
            throw new MojoExecutionException("Exception executing sanity checker", e);
        }
    }

}

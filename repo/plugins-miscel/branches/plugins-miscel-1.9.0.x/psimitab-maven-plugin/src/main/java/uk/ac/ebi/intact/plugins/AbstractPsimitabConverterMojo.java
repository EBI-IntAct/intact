package uk.ac.ebi.intact.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import uk.ac.ebi.intact.plugin.IntactAbstractMojo;

/**
 * TODO comment that class header
 *
 * @author Samuel Kerrien
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public abstract class AbstractPsimitabConverterMojo extends IntactAbstractMojo {

    public static final String DEFAULT_FILE_EXTENSION = "txt";
    
    /////////////////////////
    // Mojo parameters

    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;

    ////////////////////////
    // Abstract method

    public abstract void execute() throws MojoExecutionException;

    /////////////////////////
    // Getters

    public MavenProject getProject() {
        return project;
    }
}

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
     * Class that is going to hold the data. An extension of BinaryInteractionImpl could be used to hold extra columns that
     * the ColumnsHandler fill up.
     *
     * @parameter
     * @see psidev.psi.mi.tab.converter.xml2tab.ColumnHandler
     */
    private String binaryInteractionClass = "psidev.psi.mi.tab.model.BinaryInteractionImpl";

    /**
     * Allows to tweak the production of the columns and also to add extra columns.
     * The ColumnHandler has to be specific to a BinaryInteractionImpl.
     *
     * @parameter
     */
    private String columnHandler;

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

    public String getBinaryInteractionClass() {
        return binaryInteractionClass;
    }

    public String getColumnHandler() {
        return columnHandler;
    }

    public MavenProject getProject() {
        return project;
    }
}

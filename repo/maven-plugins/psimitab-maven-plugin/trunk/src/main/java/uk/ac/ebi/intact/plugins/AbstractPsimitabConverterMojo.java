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
     * Class that is going to hold the data. An extension of BinaryInteractionImpl could be used to hold extra
     * columns that the ColumnsHandler fill up.
     *
     * @see psidev.psi.mi.tab.converter.xml2tab.ColumnHandler
     * 
     * @parameter
     */
    private String binaryInteractionClass;

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

    public boolean hasBinaryInteractionClass() {
        return binaryInteractionClass != null && binaryInteractionClass.trim().length() > 0;
    }

    public String getColumnHandler() {
        return columnHandler;
    }

    public boolean hasColumnHandler() {
        return columnHandler != null && columnHandler.trim().length() > 0;
    }

    public MavenProject getProject() {
        return project;
    }

    public void setBinaryInteractionClass( String binaryInteractionClass ) {
        this.binaryInteractionClass = binaryInteractionClass;
    }

    public void setColumnHandler( String columnHandler ) {
        this.columnHandler = columnHandler;
    }
}

/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.log4j.*;

import java.io.*;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:IntactAbstractMojo.java 5772 2006-08-11 16:08:37 +0100 (Fri, 11 Aug 2006) baranda $
 * @since 0.1
 */
public abstract class IntactAbstractMojo extends AbstractMojo {

    protected static final String NEW_LINE = System.getProperty( "line.separator" );

    private boolean outputFileCreated;
    private boolean errorFileCreated;

    /**
     * Standard output file
     *
     * @parameter expression="${intact.outputFile}
     * default-value="${project.build.directory}/output.log"
     * @required
     */
    private File outputFile;

    /**
     * Standard error file
     *
     * @parameter expression="${intact.errorFile}
     * default-value="${project.build.directory}/error.log"
     * @required
     */
    private File errorFile;

    private Writer outputWriter;
    private Writer errorWriter;

    protected void enableLogging() {
        try {

            BasicConfigurator.configure(getLogAppender());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Layout getLogLayout() {
        return new SimpleLayout();
    }

    protected Appender getLogAppender() throws IOException {
        File logFile = new File( getDirectory(), "log.out" );
        getLog().info("Setting log4j in output: "+logFile.getAbsolutePath());

        Layout layout = getLogLayout();
        FileAppender appender = new FileAppender(layout, logFile.getAbsolutePath(), true);
        appender.setThreshold(getLogPriority());

        return appender;
    }

    protected Priority getLogPriority() {
        return Priority.INFO;
    }

    protected void writeOutputln( String line ) throws IOException {
        if ( line == null ) {
            return;
        }

        getOutputWriter().write( line + NEW_LINE );
    }

    protected void writeErrorln( String line ) throws IOException {
        if ( line == null ) {
            return;
        }

        getErrorWriter().write( line + NEW_LINE );

    }

    public Writer getOutputWriter() throws IOException {
        if ( outputWriter == null ) {
            MojoUtils.prepareFile( getOutputFile(), true );
            MojoUtils.writeStandardHeaderToFile( "Standard output", "Default error file", getProject(), outputFile );

            outputWriter = new FileWriter( outputFile );
        }
        return outputWriter;
    }

    public Writer getErrorWriter() throws IOException {
        if ( errorWriter == null ) {
            MojoUtils.prepareFile( getErrorFile(), true );
            MojoUtils.writeStandardHeaderToFile( "Standard error", "Default error file", getProject(), errorFile );

            errorWriter = new FileWriter( errorFile );
        }

        return errorWriter;
    }

    public abstract MavenProject getProject();

    public File getOutputFile() {
        if ( outputFile == null ) {
            outputFile = new File( getDirectory(), "output.log" );
        }

        return outputFile;
    }

    public File getErrorFile() {
        if ( errorFile == null ) {
            errorFile = new File( getDirectory(), "error.log" );
        }

        return errorFile;
    }

    public PrintStream getOutputPrintStream() throws IOException {
        if (!outputFileCreated) {
            MojoUtils.prepareFile(getOutputFile(), true, false);
            outputFileCreated = true;
        }
        return new PrintStream( outputFile );
    }

    public PrintStream getErrorPrintStream() throws IOException {
        if (!errorFileCreated) {
            MojoUtils.prepareFile(getErrorFile(), true, false);
            errorFileCreated = true;
        }
        return new PrintStream( errorFile );
    }

    public File getDirectory() {
        if ( getProject() != null ) {
            return new File( getProject().getBuild().getDirectory() );
        } else {
            return new File( "target" );
        }
    }

    public File getOutputDirectory() {
        if ( getProject() != null ) {
            return new File( getProject().getBuild().getOutputDirectory() );
        } else {
            return new File( "target/classes" );
        }
    }

    public File getTestOutputDirectory() {
        if ( getProject() != null ) {
            return new File( getProject().getBuild().getTestOutputDirectory() );
        } else {
            return new File( "target/classes-test" );
        }
    }
}
/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.externalservices.searchengine;

import uk.ac.ebi.intact.model.AnnotatedObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Abstract Index Exporter. Provides default hooks for exporting a complete index.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23-Nov-2006</pre>
 */
public abstract class AbstractIndexExporter<T extends AnnotatedObject> implements IndexExporter<T> {

    protected static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat( "yyyy-MMM-dd" );

    public static final String INDENT = " ";
    public static final String NEW_LINE = System.getProperty( "line.separator" );

    private BufferedWriter writer;
    private File output;
    protected String release;

    public AbstractIndexExporter( File output, String release ) {
        if ( output == null ) {
            throw new IllegalArgumentException( "output file must not be null." );
        }

        if ( !output.exists() ) {
            try {
                output.createNewFile();
            } catch ( IOException e ) {
                throw new IllegalArgumentException( "Could not create a new output file.", e );
            }
        }

        if ( !output.canWrite() ) {
            throw new IllegalArgumentException( "Cannot write on " + output.getAbsolutePath() );
        }

        if ( release == null ) {
            throw new IllegalArgumentException( "release must not be null." );
        }

        this.output = output;
    }

    ////////////////////////
    // Getters

    public String getRelease() {
        return release;
    }

    //////////////////////////
    // Utilities

    protected Writer getOutput() throws IOException {
        if ( writer == null ) {
            writer = new BufferedWriter( new FileWriter( output ) );
        }

        return writer;
    }

    protected void closeIndex() throws IOException {
        writer.flush();
        writer.close();
    }

    protected String getCurrentDate() {
        return DATE_FORMATTER.format( new Date() );
    }

    ///////////////////////////
    // Writer helper

    protected void writeRef( Writer out, String db, String id, String indent ) throws IOException {
        out.write( indent + "<ref dbname=\"" + db + "\" dbkey=\"" + id + "\" />" + NEW_LINE );
    }

    protected void writeField( Writer out, String name, String text, String indent ) throws IOException {
        out.write( indent + "<field name=\"" + name + "\">"+ text +"</field>" + NEW_LINE );
    }

    protected void writeCreationDate( Writer out, Date date, String indent ) throws IOException {
        String creation = DATE_FORMATTER.format( date );
        out.write( indent + "<date type=\"creation\" value=\"" + creation + "\" />" + NEW_LINE );
    }

    protected void writeLastUpdateDate( Writer out, Date date, String indent ) throws IOException {
        String creation = DATE_FORMATTER.format( date );
        out.write( indent + "<date type=\"last_modification\" value=\"" + creation + "\" />" + NEW_LINE );
    }


    ////////////////////////////
    // IndexExporter methods

    public void exportHeader() throws IOException {
        Writer out = getOutput();
        out.write( "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" + NEW_LINE );
        out.write( "<database>" + NEW_LINE );
    }

    public void exportEntryListStart() throws IOException {
        Writer out = getOutput();
        out.write( INDENT + "<entries>" + NEW_LINE );
    }

    public void exportEntryListEnd() throws IOException {
        Writer out = getOutput();
        out.write( INDENT + "</entries>" + NEW_LINE );
    }

    public void exportFooter() throws IOException {
        Writer out = getOutput();
        out.write( "</database>" + NEW_LINE );
    }

    ////////////////////////////
    // Abstract method

    public abstract void exportEntry( T object ) throws IOException;

    public abstract int getEntryCount();
}
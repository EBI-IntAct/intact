/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.psimitab;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.tab.PsimiTabWriter;
import psidev.psi.mi.tab.converter.xml2tab.TabConvertionException;
import psidev.psi.mi.tab.converter.xml2tab.Xml2Tab;
import psidev.psi.mi.tab.expansion.BinaryExpansionStrategy;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.processor.ClusterInteractorPairProcessor;
import psidev.psi.mi.xml.converter.ConverterException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Tool allowing to convert a set of XML file or directories succesptible to contain XML files into PSIMITAB.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02-Jan-2007</pre>
 */
public class ConvertXml2Tab {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( ConvertXml2Tab.class );

    /////////////////////////
    // Instance variables

    /**
     * Controls the clustering of interactor pair in the final PSIMITAB file.
     */
    private boolean interactorPairCluctering = true;

    /**
     * Strategy defining the behaviour of the binary expansion.
     *
     * @see psidev.psi.mi.tab.expansion.SpokeExpansion
     * @see psidev.psi.mi.tab.expansion.MatrixExpansion
     */
    private BinaryExpansionStrategy expansionStragegy;

    /**
     * Input file/directory to be converted. If directory are given, a recursive search is performed and all file having
     * an extension '.xml' are selected automatically.
     */
    private Collection<File> xmlFilesToConvert;

    /**
     * Output file resulting of the conversion.
     */
    private File outputFile;

    /**
     * Controls whever the output file can be overwriten or not.
     */
    private boolean overwriteOutputFile = false;

    ////////////////////////
    // Constructor

    public ConvertXml2Tab() {
    }

    ////////////////////////
    // Getters and Setters

    public void setInteractorPairCluctering( boolean enabled ) {
        this.interactorPairCluctering = enabled;
    }

    public boolean isInteractorPairCluctering() {
        return interactorPairCluctering;
    }

    public void setExpansionStrategy( BinaryExpansionStrategy expansionStragegy ) {
        this.expansionStragegy = expansionStragegy;
    }

    public BinaryExpansionStrategy getExpansionStragegy() {
        return expansionStragegy;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile( File outputFile ) {
        this.outputFile = outputFile;
    }

    public Collection<File> getXmlFilesToConvert() {
        return xmlFilesToConvert;
    }

    public void setXmlFilesToConvert( Collection<File> xmlFilesToConvert ) {
        this.xmlFilesToConvert = xmlFilesToConvert;
    }

    public boolean isOverwriteOutputFile() {
        return overwriteOutputFile;
    }

    public void setOverwriteOutputFile( boolean overwriteOutputFile ) {
        this.overwriteOutputFile = overwriteOutputFile;
    }

    ///////////////////////////
    // Convertion

    public void convert() throws ConverterException, IOException, TabConvertionException {

        // a few checks before to start computationally intensive operations

        if ( xmlFilesToConvert == null ) {
            throw new IllegalArgumentException( "You must give a non null Collection<File> to convert." );
        }

        if ( xmlFilesToConvert.isEmpty() ) {
            throw new IllegalArgumentException( "You must give a non empty Collection<File> to convert." );
        }

        if ( outputFile == null ) {
            throw new IllegalArgumentException( "You must give a non null output file." );
        }

        if ( outputFile.exists() && !overwriteOutputFile ) {
            throw new IllegalArgumentException( outputFile.getName() + " already exits, overwrite is set to false. abort." );
        }

        if ( outputFile.exists() && !outputFile.canWrite() ) {
            throw new IllegalArgumentException( outputFile.getName() + " is not writable. abort." );
        }

        // now start conversion
        Xml2Tab x2t = new Xml2Tab();

        x2t.setExpansionStrategy( expansionStragegy );

        if ( interactorPairCluctering ) {
            x2t.setPostProcessor( new ClusterInteractorPairProcessor() );
        } else {
            x2t.setPostProcessor( null );
        }

        Collection<BinaryInteraction> interaction = x2t.convert( xmlFilesToConvert );

        log.debug( "Starting writing file on disk..." );
        PsimiTabWriter writer = new PsimiTabWriter();
        writer.write( interaction, outputFile );
    }
}
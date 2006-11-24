/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.externalservices.searchengine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.InteractionDao;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23-Nov-2006</pre>
 */
public class InteractionIndexExporter extends AbstractIndexExporter<Interaction> {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( InteractionIndexExporter.class );

    public static final String INDEX_NAME = "IntAct.Interaction";
    public static final String DESCRIPTION = "Molecular interaction";
    public static final int CHUNK_SIZE = 50;

    ////////////////////////////
    // Instance variables

    private Integer count = null;

    //////////////////////////
    // Constructor

    public InteractionIndexExporter( File output, String release ) {
        super( output, release );
    }

    ////////////////////////////
    // AbstractIndexExporter

    public void exportHeader() throws IOException {

        Writer out = getOutput();

        writeXmlHeader( out );

        out.write( "<database>" + NEW_LINE );
        out.write( INDENT + "<name>" + INDEX_NAME + "</name>" + NEW_LINE );
        out.write( INDENT + "<description>" + DESCRIPTION + "</description>" + NEW_LINE );
        out.write( INDENT + "<release>" + release + "</release>" + NEW_LINE );
        out.write( INDENT + "<release_date>" + getCurrentDate() + "</release_date>" + NEW_LINE );
        out.write( INDENT + "<entry_count>" + getEntryCount() + "</entry_count>" + NEW_LINE );
    }

    public void exportEntry( Interaction interaction ) throws IOException {
        Writer out = getOutput();

        final String i = INDENT + INDENT;

        out.write( i + "<entry id=\"" + interaction.getAc() + "\">" + NEW_LINE );
        out.write( i + "<name>" + interaction.getShortLabel() + "</name>" + NEW_LINE );
        out.write( i + "<description>" + interaction.getFullName() + "</description>" + NEW_LINE );

        out.write( i + "<dates>" + NEW_LINE );
        writeCreationDate( out, interaction.getCreated(), i + INDENT );
        writeLastUpdateDate( out, interaction.getUpdated(), i + INDENT );
        out.write( i + "</dates>" + NEW_LINE );

        out.write( i + "<cross_references>" + NEW_LINE );

        if ( !interaction.getXrefs().isEmpty() ) {
            for ( Xref xref : interaction.getXrefs() ) {
                String db = xref.getCvDatabase().getShortLabel();
                String id = xref.getPrimaryId();
                writeRef( out, db, id, i + INDENT );
            }
        }

        // Add refs to interactions and experiments
        if ( !interaction.getActiveInstances().isEmpty() ) {

            Set<String> interactors = new HashSet<String>();
            Set<String> experimentAcs = new HashSet<String>();

            for ( Component c : interaction.getActiveInstances() ) {

                Interactor interactor = c.getInteractor();
                interactors.add( interactor.getAc() );

                for ( Experiment experiment : interaction.getExperiments() ) {
                    experimentAcs.add( experiment.getAc() );
                }
            }

            for ( String ac : experimentAcs ) {
                writeRef( out, ExperimentIndexExporter.INDEX_NAME, ac, i + INDENT );
            }

            for ( String ac : interactors ) {
                writeRef( out, InteractionIndexExporter.INDEX_NAME, ac, i + INDENT );
            }
        }

        out.write( i + "<cross_references>" + NEW_LINE );

        // TODO export Annotations ?

        // TODO export respective Components' xref and aliases and annotation

        out.write( i + "<additional_fields>" + NEW_LINE );
        for ( Alias alias : interaction.getAliases() ) {
            writeField( out, alias.getCvAliasType().getShortLabel(), alias.getName(), i + INDENT );
        }

        // export Controlled vocabularies

        // interaction detection method
        writeCvTerm( out, interaction.getCvInteractionType(), i + INDENT );

        // participant detection method and interaction type
        Set<CvObject> cvs = new HashSet<CvObject>();
        for ( Experiment e : interaction.getExperiments() ) {
            cvs.add( e.getCvInteraction() );
            cvs.add( e.getCvIdentification() );
        }
        for ( CvObject cv : cvs ) {
            writeCvTerm( out, cv, i + INDENT );
        }

        out.write( i + "</additional_fields>" + NEW_LINE );

        out.write( i + "</entry>" + NEW_LINE );
    }

    public void exportEntries() throws IOException {
        int current = 0;

        log.debug( "Starting export of " + count + " interaction(s)." );

        while ( current < count ) {
            DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
            InteractionDao idao = daoFactory.getInteractionDao();
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();

            List<InteractionImpl> interactions = idao.getAll( current, CHUNK_SIZE );

            if ( log.isDebugEnabled() ) {
                log.debug( "Exporting interaction range " + current + ".." + Math.min( count, current + CHUNK_SIZE ) + 
                           " out of " + count );
            }

            for ( Interaction interaction : interactions ) {
                current++;
                exportEntry( interaction );
            }

            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        }
    }

    public int getEntryCount() {
        if ( count == null ) {

            DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
            InteractionDao idao = daoFactory.getInteractionDao();

            IntactContext.getCurrentInstance().getDataContext().beginTransaction();
            // TODO do not take into account interactor that do not interact.

            count = idao.countAll();

            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        }

        return count;
    }
}
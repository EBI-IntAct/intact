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
import uk.ac.ebi.intact.persistence.dao.ExperimentDao;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Exports experiments for the EBI search engine.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23-Nov-2006</pre>
 */
public class ExperimentIndexExporter extends AbstractIndexExporter<Experiment> {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( ExperimentIndexExporter.class );

    public static final String INDEX_NAME = "IntAct.Experiment";
    public static final String DESCRIPTION = "Experimental procedures that allowed to discover molecular interactions";
    public static final int CHUNK_SIZE = 10;

    ////////////////////////////
    // Instance variables

    private Integer count = null;

    //////////////////////////
    // Constructor

    public ExperimentIndexExporter( File output, String release ) {
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

    public void exportEntries() throws IOException {
        int current = 0;
        log.debug( "Starting export of " + count + " experiment(s)." );
        while ( current < count ) {
            DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
            ExperimentDao edao = daoFactory.getExperimentDao();
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();

            List<Experiment> experiments = edao.getAll( current, CHUNK_SIZE );
            if ( log.isDebugEnabled() ) {
                log.debug( "Exporting experiment range " + current + ".." + Math.min( count, current + CHUNK_SIZE ) +
                           " out of " + count );
            }
            for ( Experiment experiment : experiments ) {
                current++;
                exportEntry( experiment );
            }

            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        }
    }

    public void exportEntry( Experiment experiment ) throws IOException {

        Writer out = getOutput();

        final String i = INDENT + INDENT;

        out.write( i + "<entry id=\"" + experiment.getAc() + "\">" + NEW_LINE );
        out.write( i + "<name>" + experiment.getShortLabel() + "</name>" + NEW_LINE );
        if ( experiment.getFullName() != null ) {
            out.write( i + "<description>" + escapeXml( experiment.getFullName() ) + "</description>" + NEW_LINE );
        }
        out.write( i + "<dates>" + NEW_LINE );
        writeCreationDate( out, experiment.getCreated(), i + INDENT );
        writeLastUpdateDate( out, experiment.getUpdated(), i + INDENT );
        out.write( i + "</dates>" + NEW_LINE );

        boolean hasXrefs = !experiment.getXrefs().isEmpty();
        boolean hasLinks = !experiment.getInteractions().isEmpty();

        if ( hasXrefs || hasLinks ) {
            out.write( i + "<cross_references>" + NEW_LINE );

            if ( hasXrefs ) {
                for ( Xref xref : experiment.getXrefs() ) {
                    String db = xref.getCvDatabase().getShortLabel();
                    String id = xref.getPrimaryId();
                    writeRef( out, db, id, i + INDENT );
                }
            }

            // Add refs to interactions and experiments
            if ( hasLinks ) {

                Set<String> interactors = new HashSet<String>();

                for ( Interaction interaction : experiment.getInteractions() ) {

                    writeRef( out, InteractionIndexExporter.INDEX_NAME, interaction.getAc(), i + INDENT );
                    for ( Component c : interaction.getActiveInstances() ) {
                        interactors.add( c.getInteractor().getAc() ); // Build non redundant list
                    }
                }

                for ( String ac : interactors ) {
                    writeRef( out, InteractorIndexExporter.INDEX_NAME, ac, i + INDENT );
                }
            }

            out.write( i + "</cross_references>" + NEW_LINE );
        }

        // TODO export Annotations ?

        // TODO export respective Components' xref and aliases and annotation

        out.write( i + "<additional_fields>" + NEW_LINE );
        for ( Alias alias : experiment.getAliases() ) {
            String aliasName = escapeXml( alias.getName() );
            writeField( out, alias.getCvAliasType().getShortLabel(), aliasName, i + INDENT );
        }

        writeCvTerm( out, experiment.getCvInteraction(), i + INDENT );
        writeCvTerm( out, experiment.getCvIdentification(), i + INDENT );

        Set<CvObject> cvs = new HashSet<CvObject>();
        for ( Interaction interaction : experiment.getInteractions() ) {
            cvs.add( interaction.getCvInteractionType() );
        }
        for ( CvObject cv : cvs ) {
            writeCvTerm( out, cv, i + INDENT );
        }

        out.write( i + "</additional_fields>" + NEW_LINE );

        out.write( i + "</entry>" + NEW_LINE );

    }

    public int getEntryCount() {
        if ( count == null ) {

            DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
            ExperimentDao edao = daoFactory.getExperimentDao();

            IntactContext.getCurrentInstance().getDataContext().beginTransaction();
            // TODO do not take into account interactor that do not interact.

            count = edao.countAll();

            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        }

        return count;
    }
}
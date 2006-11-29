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
import uk.ac.ebi.intact.persistence.dao.InteractorDao;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Exports interactors for the EBI search engine.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23-Nov-2006</pre>
 */
public class InteractorIndexExporter extends AbstractIndexExporter<Interactor> {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( InteractorIndexExporter.class );

    public static final int CHUNK_SIZE = 50;

    public static final String INDEX_NAME = "IntAct.Interactor";
    public static final String DESCRIPTION = "Molecule taking part in an Interaction.";

    ////////////////////////
    // Instance variables

    private Integer count = null;
    private int countPolymer = -1;
    private int countSmallMolecule = -1;

    //////////////////////////
    // Constructor

    public InteractorIndexExporter( File output, String release ) {
        super( output, release );
    }

    ////////////////////////
    // Export

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

    public int getEntryCount() {
        if ( count == null ) {


            DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
            InteractorDao<PolymerImpl> pdao = daoFactory.getInteractorDao( PolymerImpl.class );
            InteractorDao<SmallMoleculeImpl> smdao = daoFactory.getInteractorDao( SmallMoleculeImpl.class );

            IntactContext.getCurrentInstance().getDataContext().beginTransaction();
            // TODO do not take into account interactor that do not interact.

            countPolymer = pdao.countAll();
            countSmallMolecule = smdao.countAll();

            // sum up polymer + small molecule.
            count = countPolymer + countSmallMolecule;

            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        }

        return count;
    }

    public void exportEntries() throws IOException {
        exportSmallMolecule();
        exportPolymer();
    }

    public void exportEntry( Interactor interactor ) throws IOException {

        Writer out = getOutput();

        final String i = INDENT + INDENT;

        out.write( i + "<entry id=\"" + interactor.getAc() + "\">" + NEW_LINE );
        out.write( i + "<name>" + interactor.getShortLabel() + "</name>" + NEW_LINE );
        out.write( i + "<description>" + interactor.getFullName() + "</description>" + NEW_LINE );

        out.write( i + "<dates>" + NEW_LINE );
        writeCreationDate( out, interactor.getCreated(), i + INDENT );
        writeLastUpdateDate( out, interactor.getUpdated(), i + INDENT );
        out.write( i + "</dates>" + NEW_LINE );


        boolean hasXrefs = !interactor.getXrefs().isEmpty();
        boolean hasLinks = !interactor.getActiveInstances().isEmpty();

        if ( hasXrefs || hasLinks ) {
            out.write( i + "<cross_references>" + NEW_LINE );

            if ( hasXrefs ) {
                for ( Xref xref : interactor.getXrefs() ) {

                    String db = xref.getCvDatabase().getShortLabel();
                    String id = xref.getPrimaryId();
                    writeRef( out, db, id, i + INDENT );
                }
            }

            // Add refs to interactions and experiments
            if ( hasLinks ) {

                Set<String> interactionAcs = new HashSet<String>();
                Set<String> experimentAcs = new HashSet<String>();

                for ( Component c : interactor.getActiveInstances() ) {

                    Interaction interaction = c.getInteraction();
                    interactionAcs.add( interaction.getAc() );

                    for ( Experiment experiment : interaction.getExperiments() ) {
                        experimentAcs.add( experiment.getAc() );
                    }
                }

                for ( String ac : experimentAcs ) {
                    writeRef( out, ExperimentIndexExporter.INDEX_NAME, ac, i + INDENT );
                }

                for ( String ac : interactionAcs ) {
                    writeRef( out, InteractionIndexExporter.INDEX_NAME, ac, i + INDENT );
                }
            }

            out.write( i + "</cross_references>" + NEW_LINE );
        }

        // TODO export Annotations

        // TODO export respective Components' xref and aliases and annotation

        out.write( i + "<additional_fields>" + NEW_LINE );
        for ( Alias alias : interactor.getAliases() ) {
            writeField( out, alias.getCvAliasType().getShortLabel(), alias.getName(), i + INDENT );
        }

        Set<CvObject> cvs = new HashSet<CvObject>();
        for ( Component c : interactor.getActiveInstances() ) {
            Interaction interaction = c.getInteraction();
            cvs.add( interaction.getCvInteractionType() );
            for ( Experiment e : interaction.getExperiments() ) {
                cvs.add( e.getCvIdentification() );
                cvs.add( e.getCvInteraction() );
            }
        }
        for ( CvObject cv : cvs ) {
            writeCvTerm( out, cv, i + INDENT );
        }

        out.write( i + "</additional_fields>" + NEW_LINE );

        out.write( i + "</entry>" + NEW_LINE );
    }

    //////////////////////////
    // Private methods

    private void exportSmallMolecule() throws IOException {
        int current = 0;

        log.debug( "Starting export of " + countSmallMolecule + " small molecule(s)." );

        while ( current < countSmallMolecule ) {
            DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
            InteractorDao<SmallMoleculeImpl> pdao = daoFactory.getInteractorDao( SmallMoleculeImpl.class );
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();

            List<SmallMoleculeImpl> interactors = pdao.getAll( current, CHUNK_SIZE );

            if ( log.isDebugEnabled() ) {
                log.debug( "Exporting small molecule range " + current + ".." + Math.min( countSmallMolecule, current + CHUNK_SIZE ) +
                           " out of " + countSmallMolecule );
            }

            for ( Interactor interactor : interactors ) {
                current++;
                exportEntry( interactor );
            }

            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        }
    }

    private void exportPolymer() throws IOException {

        int current = 0;

        log.debug( "Starting export of " + countPolymer + " polymer(s)." );

        while ( current < countPolymer ) {
            DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
            InteractorDao<PolymerImpl> pdao = daoFactory.getInteractorDao( PolymerImpl.class );
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();

            List<PolymerImpl> interactors = pdao.getAll( current, CHUNK_SIZE );

            if ( log.isDebugEnabled() ) {
                log.debug( "Exporting polymer range " + current + ".." + Math.min( countPolymer, current + CHUNK_SIZE ) +
                           " out of " + countPolymer );
            }

            for ( Interactor interactor : interactors ) {
                current++;
                exportEntry( interactor );
            }

            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        }
    }
}
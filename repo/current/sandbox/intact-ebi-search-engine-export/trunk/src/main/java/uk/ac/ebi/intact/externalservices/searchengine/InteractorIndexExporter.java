/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.externalservices.searchengine;

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.InteractorDao;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23-Nov-2006</pre>
 */
public class InteractorIndexExporter extends AbstractIndexExporter<Interactor> {

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

    public void exportHeader() throws IOException {
        super.exportHeader();

        // Header specifics of that index
        Writer out = getOutput();
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

        out.write( i + "<cross_references>" + NEW_LINE );

        if ( !interactor.getXrefs().isEmpty() ) {
            for ( InteractorXref xref : interactor.getXrefs() ) {

                String db = xref.getCvDatabase().getShortLabel();
                String id = xref.getPrimaryId();
                writeRef( out, db, id, i + INDENT );
            }
        }

        // Add refs to interactions and experiments
        if ( !interactor.getActiveInstances().isEmpty() ) {

            Set<String> interactionAcs = new HashSet<String>( );
            Set<String> experimentAcs = new HashSet<String>( );

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

        out.write( i + "<cross_references>" + NEW_LINE );

        // TODO export Annotations

        // TODO export respective Components' xref and aliases and annotation

        out.write( i + "<additional_fields>" + NEW_LINE );
        for ( InteractorAlias alias : interactor.getAliases() ) {
            writeField( out, alias.getCvAliasType().getShortLabel(), alias.getName(), i + INDENT );
        }
        out.write( i + "</additional_fields>" + NEW_LINE );

        out.write( i + "</entry>" + NEW_LINE );
    }

    public void buildIndex() throws IOException {
        exportHeader();

        exportEntryListStart();

        exportSmallMolecule();
        exportPolymer();

        exportEntryListEnd();

        exportFooter();
    }

    private void exportSmallMolecule() throws IOException {
        int current = 0;

        while ( current < countSmallMolecule ) {
            DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
            InteractorDao<SmallMoleculeImpl> pdao = daoFactory.getInteractorDao( SmallMoleculeImpl.class );
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();

            List<SmallMoleculeImpl> interactors = pdao.getAll( current, CHUNK_SIZE );
            for ( Interactor interactor : interactors ) {

                current++;
                exportEntry( interactor );
            }

            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        }
    }

    private void exportPolymer() throws IOException {

        int current = 0;

        while ( current < countPolymer ) {
            DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
            InteractorDao<PolymerImpl> pdao = daoFactory.getInteractorDao( PolymerImpl.class );
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();

            List<PolymerImpl> interactors = pdao.getAll( current, CHUNK_SIZE );
            for ( Interactor interactor : interactors ) {

                current++;
                exportEntry( interactor );
            }

            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        }
    }
}
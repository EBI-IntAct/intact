/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.persister;

import uk.ac.ebi.intact.application.dataConversion.psiUpload.gui.Monitor;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.EntryTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.ExperimentDescriptionTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.InteractionTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.CommandLineOptions;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Interaction;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * That class make the data persitent in the Intact database.
 * <br>
 * That class takes care of an Experiment including its Interactions, Proteins, Components...
 * <br>
 * It assumes that the data are already parsed and passed the validity check successfully.
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public final class EntryPersister {

    public static void persist( final EntryTag entry,
                                final IntactHelper helper )
            throws IntactException {

        Collection keys;
        final Map experimentDescriptions = entry.getExperimentDescriptions();
        final Collection interactions = entry.getInteractions();

        keys = experimentDescriptions.keySet();
        for ( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
            final String key = (String) iterator.next();
            final ExperimentDescriptionTag experimentDescription =
                    (ExperimentDescriptionTag) experimentDescriptions.get( key );
            ExperimentDescriptionPersister.persist( experimentDescription, helper );
        }


        boolean guiEnabled = CommandLineOptions.getInstance().isGuiEnabled();
        Monitor monitor = null;
        if( guiEnabled ) {
            monitor = new Monitor( interactions.size(), "Interaction creation" );
            monitor.setStatus( "Waiting for the persister to start..." );
            monitor.show();
        }
        int current = 0;
        Interaction intactInteraction = null;
        for ( Iterator iterator = interactions.iterator(); iterator.hasNext(); ) {
            final InteractionTag interaction = (InteractionTag) iterator.next();
            try {
                intactInteraction = InteractionPersister.persist( interaction, helper );
                if( guiEnabled ) {
                    final String status;
                    if( intactInteraction == null ) {
                        status = "";
                    } else {
                        status = intactInteraction.getShortLabel() + " created";
                    }
                    monitor.setStatus( status );
                }
            } catch ( IntactException e ) {
                System.err.println( "When the error occured, we were in: " + interaction );
                throw e;

            } catch ( NullPointerException e ) {
                System.err.println( "When the error occured, we were in: " + interaction );
                throw e;
            }
            if( guiEnabled ) {
                monitor.updateProteinProcessedCound( ++current );
            }
        }
        if( guiEnabled ) {
            monitor.hide();
        }
    }
}

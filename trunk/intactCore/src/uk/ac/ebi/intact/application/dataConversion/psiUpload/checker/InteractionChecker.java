/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.checker;

import uk.ac.ebi.intact.application.dataConversion.psiUpload.gui.Monitor;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.*;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.util.BioSourceFactory;
import uk.ac.ebi.intact.util.UpdateProteinsI;

import java.util.Collection;
import java.util.Iterator;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public final class InteractionChecker {

    public static void check( final InteractionTag interaction,
                              final IntactHelper helper,
                              final UpdateProteinsI proteinFactory,
                              final BioSourceFactory bioSourceFactory,
                              final Monitor monitor ) {

        // TODO do we play with that ?
//        final String shortlabel = interaction.getShortlabel();
//        final String fullname = interaction.getFullname();

        // experiment
        Collection experiments = interaction.getExperiments();
        for ( Iterator iterator = experiments.iterator(); iterator.hasNext(); ) {
            ExperimentDescriptionTag experimentDescription = (ExperimentDescriptionTag) iterator.next();
            ExperimentDescriptionChecker.check( experimentDescription, helper, bioSourceFactory );
        }

        // participants
        final Collection participants = interaction.getParticipants();
        // TODO do we have to check that we have at least 1 bait and 1..m preys/neutrals
        for ( Iterator iterator = participants.iterator(); iterator.hasNext(); ) {
            ProteinParticipantTag proteinParticipant = (ProteinParticipantTag) iterator.next();
            if( monitor != null ) {
                ProteinInteractorTag proteinInteractor = proteinParticipant.getProteinInteractor();
                String uniprotID = null;
                String taxid = null;

                if( proteinInteractor != null && proteinInteractor.getUniprotXref() != null ) {
                    uniprotID = proteinInteractor.getUniprotXref().getId();
                }
                if( proteinInteractor != null && proteinInteractor.getOrganism() != null ) {
                    taxid = proteinInteractor.getOrganism().getTaxId();
                } else {
                    taxid = "taxid not specified";
                }
                monitor.setStatus( "Checking " + uniprotID + " (" + taxid + ")" );
            }
            ProteinParticipantChecker.check( proteinParticipant, helper, proteinFactory, bioSourceFactory );
        }

        // interactionType
        final InteractionTypeTag interactionType = interaction.getInteractionType();
        InteractionTypeChecker.check( interactionType, helper );

        // xrefs
        Collection xrefs = interaction.getXrefs();
        for ( Iterator iterator = xrefs.iterator(); iterator.hasNext(); ) {
            XrefTag xref = (XrefTag) iterator.next();
            XrefChecker.check( xref, helper );
        }

        // annotations
        final Collection annotations = interaction.getAnnotations();
        for ( Iterator iterator = annotations.iterator(); iterator.hasNext(); ) {
            AnnotationTag annotation = (AnnotationTag) iterator.next();
            AnnotationChecker.check( annotation, helper );
        }
    }
}
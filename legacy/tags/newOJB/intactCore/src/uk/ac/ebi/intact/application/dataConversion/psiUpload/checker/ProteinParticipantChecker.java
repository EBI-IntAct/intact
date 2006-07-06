/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.checker;

import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.ProteinInteractorTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.ProteinParticipantTag;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.util.BioSourceFactory;
import uk.ac.ebi.intact.util.UpdateProteinsI;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinParticipantChecker {

    public static void check( final ProteinParticipantTag proteinParticipant,
                              final IntactHelper helper,
                              final UpdateProteinsI proteinFactory,
                              final BioSourceFactory bioSourceFactory ) {

        final String role = proteinParticipant.getRole();
        RoleChecker.check( role, helper );

        final ProteinInteractorTag proteinInteractor = proteinParticipant.getProteinInteractor();
        ProteinInteractorChecker.check( proteinInteractor, helper, proteinFactory, bioSourceFactory );
    }
}

/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.persister;

import uk.ac.ebi.intact.application.dataConversion.psiUpload.checker.OrganismChecker;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.checker.ProteinInteractorChecker;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.checker.RoleChecker;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.ProteinHolder;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.ProteinInteractorTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.ProteinParticipantTag;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

/**
 * That class make the data persitent in the Intact database.
 * <br>
 * That class takes care of a Component for a specific Interaction.
 * <br>
 * It assumes that the data are already parsed and passed the validity check successfully.
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinParticipantPersister {

    public static void persist( final ProteinParticipantTag proteinParticipant,
                                final Interaction interaction,
                                final IntactHelper helper ) throws IntactException {

        final ProteinInteractorTag proteinInteractor = proteinParticipant.getProteinInteractor();

        final BioSource bioSource = OrganismChecker.getBioSource( proteinInteractor.getOrganism() );
        final String proteinId = proteinInteractor.getUniprotXref().getId();
        final ProteinHolder proteinHolder = ProteinInteractorChecker.getProtein( proteinId, bioSource );
        final CvComponentRole role = RoleChecker.getCvComponentRole( proteinParticipant.getRole() );

        final Protein protein;
        if( proteinHolder.isSpliceVariantExisting() ) {
            protein = proteinHolder.getSpliceVariant();
        } else {
            protein = proteinHolder.getProtein();
        }

        final Component component = new Component( helper.getInstitution(),
                                                   interaction,
                                                   protein,
                                                   role );
        helper.create( component );
    }
}

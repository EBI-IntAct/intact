/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.persister;

import uk.ac.ebi.intact.application.dataConversion.psiUpload.checker.*;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.*;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.util.Collection;
import java.util.Iterator;

/**
 * That class make the data persitent in the Intact database. <br> That class takes care of a Component for a specific
 * Interaction. <br> It assumes that the data are already parsed and passed the validity check successfully.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinParticipantPersister {

    public static void persist( final ProteinParticipantTag proteinParticipant,
                                final Interaction interaction,
                                final IntactHelper helper ) throws IntactException {

        ProteinInteractorTag proteinInteractor = proteinParticipant.getProteinInteractor();

        final BioSource bioSource = OrganismChecker.getBioSource( proteinInteractor.getOrganism() );
        final String proteinId = proteinInteractor.getPrimaryXref().getId();
        final String db = proteinInteractor.getPrimaryXref().getDb();
        final ProteinHolder proteinHolder = ProteinInteractorChecker.getProtein( proteinId, db, bioSource );
        final CvComponentRole role = RoleChecker.getCvComponentRole( proteinParticipant.getRole() );

        Protein protein = null;
        if ( proteinHolder.isUniprot() ) {

            if ( proteinHolder.isSpliceVariantExisting() ) {
                protein = proteinHolder.getSpliceVariant();
            } else {
                protein = proteinHolder.getProtein();
            }

        } else {

            protein = proteinHolder.getProtein();

        }


        final Component component = new Component( helper.getInstitution(), interaction, protein, role );
        helper.create( component );

        // add expressedIn if it is available.
        ExpressedInTag expressedIn = proteinParticipant.getExpressedIn();
        if ( null != expressedIn ) {
            BioSource bs = ExpressedInChecker.getBioSource( expressedIn.getBioSourceShortlabel() );
            component.setExpressedIn( bs );
            helper.update( component );
        }

        // TODO process the <confidence> tag here

        // add features if any
        Collection features = proteinParticipant.getFeatures();
        for ( Iterator iterator = features.iterator(); iterator.hasNext(); ) {
            FeatureTag featureTag = (FeatureTag) iterator.next();

            FeaturePersister.persist( featureTag,
                                      component,
                                      protein,
                                      helper );
        }
    }
}

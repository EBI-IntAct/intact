/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.feature;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */

@SanityRule( target = Feature.class )

public class MutationAnalysisFeature implements Rule<Feature> {

    private static final String DESCRIPTION = "This/these Feature(s) are characterizing deletion of more then 2 " +
                                              "amino-acid and have their CvFeatureIdentification set to " + CvFeatureIdentification.DELETION_ANALYSIS;
    private static final String SUGGESTION = "";

    public Collection<GeneralMessage> check( Feature feature ) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        if ( feature.getCvFeatureType() == null ) {
            return messages;
        }

        CvObjectXref cvFeatureTypeIdentity = CvObjectUtils.getPsiMiIdentityXref( feature.getCvFeatureType() );

        if ( CvFeatureType.MUTATION_DECREASING_MI_REF.equals( cvFeatureTypeIdentity.getPrimaryId() ) ||
             CvFeatureType.MUTATION_INCREASING_MI_REF.equals( cvFeatureTypeIdentity.getPrimaryId() ) ||
             CvFeatureType.MUTATION_MI_REF.equals( cvFeatureTypeIdentity.getPrimaryId() ) ||
             CvFeatureType.MUTATION_DISRUPTING_MI_REF.equals( cvFeatureTypeIdentity.getPrimaryId() )
                ) {
            Collection<Range> ranges = feature.getRanges();
            for ( Range range : ranges ) {
                // tointervalstart - r.fromintervalend
                int width = range.getToIntervalStart() - range.getFromIntervalEnd();
                if ( width > 2 ) {
                    messages.add( new GeneralMessage(MessageDefinition.FEATURE_DELETION_FEATURE_TOO_LONG, feature ) );
                }
            }
        }
        return messages;
    }
}
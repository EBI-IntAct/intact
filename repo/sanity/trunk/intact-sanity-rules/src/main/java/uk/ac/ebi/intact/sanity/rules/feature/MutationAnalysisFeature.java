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
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Check that when feature type is mutation, the range is no longer than 2AA.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk), Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule( target = Feature.class, group = { RuleGroup.INTACT, RuleGroup.IMEX })
public class MutationAnalysisFeature implements Rule<Feature> {

    public Collection<GeneralMessage> check( Feature feature ) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        if ( feature.getCvFeatureType() == null ) {
            return messages;
        }

        String cvFeatureTypeIdentityMi = feature.getCvFeatureType().getMiIdentifier();

        if ( CvFeatureType.MUTATION_DECREASING_MI_REF.equals( cvFeatureTypeIdentityMi ) ||
             CvFeatureType.MUTATION_INCREASING_MI_REF.equals( cvFeatureTypeIdentityMi ) ||
             CvFeatureType.MUTATION_MI_REF.equals( cvFeatureTypeIdentityMi ) ||
             CvFeatureType.MUTATION_DISRUPTING_MI_REF.equals( cvFeatureTypeIdentityMi )
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
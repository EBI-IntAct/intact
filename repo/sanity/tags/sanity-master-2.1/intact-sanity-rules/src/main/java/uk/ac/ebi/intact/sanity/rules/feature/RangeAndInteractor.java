/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.feature;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.RangeMessage;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Checks for feature's range having a scope that doesn't match the underlying protein sequence or type.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule( target = Feature.class, group = RuleGroup.INTACT )

public class RangeAndInteractor implements Rule<Feature> {

    public Collection<GeneralMessage> check( Feature feature ) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        final Component component = feature.getComponent();
        final Interactor interactor = component.getInteractor();
        final boolean isPolymer = ( interactor instanceof Polymer );


        for ( Range range : feature.getRanges() ) {

            final boolean isFromCTerminal = range.getFromCvFuzzyType().isCTerminal();
            final boolean isFromNTerminal = range.getFromCvFuzzyType().isNTerminal();
            final boolean isFromRange = range.getFromCvFuzzyType().isRange();
            final boolean isFromCertain = range.getFromCvFuzzyType().isCertain();
            final boolean isFromLess = range.getFromCvFuzzyType().isLessThan();
            final boolean isFromGreater = range.getFromCvFuzzyType().isGreaterThan();

            final boolean isToCTerminal = range.getToCvFuzzyType().isCTerminal();
            final boolean isToNTerminal = range.getToCvFuzzyType().isNTerminal();
            final boolean isToRange = range.getToCvFuzzyType().isRange();
            final boolean isToCertain = range.getToCvFuzzyType().isCertain();
            final boolean isToLess = range.getToCvFuzzyType().isLessThan();
            final boolean isToGreater = range.getToCvFuzzyType().isGreaterThan();

            final boolean hasFrom = ( range.getFromIntervalStart() != 0 && range.getFromIntervalEnd() != 0 );
            final boolean hasTo = ( range.getToIntervalStart() != 0 && range.getToIntervalEnd() != 0 );

            // Check on interactor type and range boundaries (error if the interactor has no sequence).
            if ( !isPolymer &&
                        ( hasFrom || hasTo ||
                          isFromCertain || isToCertain ||
                          isFromCTerminal || isToCTerminal ||
                          isFromNTerminal || isToNTerminal ||
                          isFromLess || isToLess ||
                          isFromGreater || isToGreater ||
                          isFromRange || isToRange
                        ) ) {

                messages.add( new RangeMessage( MessageDefinition.FEATURE_WITH_INCOMPATIBLE_INTERACTOR, feature,
                                                range, interactor ) );

            } else if ( isPolymer && ( hasFrom || hasTo ) ) {

                final String seq = ( ( Polymer ) interactor ).getSequence();
                final int length = ( seq == null ? 0 : seq.length() );

                if ( length == 0 ) {
                    messages.add( new RangeMessage( MessageDefinition.DETERMINED_RANGE_WITHOUT_SEQUENCE, feature,
                                                    range, interactor ) );
                } else {

                    if ( range.getFromIntervalStart() > length ||
                         range.getFromIntervalEnd() > length ||
                         range.getToIntervalStart() > length ||
                         range.getToIntervalEnd() > length ) {

                        messages.add( new RangeMessage( MessageDefinition.RANGE_AND_INTERACTOR_BOUNDARIES_MISMATCH,
                                                        feature, range, interactor ) );
                    }
                }
            }
        }

        return messages;
    }
}
/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.feature;

import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Range;
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
 * Checks for feature's range having errors in their boundaries and fuzzy types.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule( target = Feature.class, group = RuleGroup.INTACT )

public class RangeBoundaries implements Rule<Feature> {

    public Collection<GeneralMessage> check( Feature feature ) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        for ( Range range : feature.getRanges() ) {

            final boolean isUndertermined = range.isUndetermined();

            final boolean isFromCTerminal = range.getFromCvFuzzyType().isCTerminal();
            final boolean isFromNTerminal = range.getFromCvFuzzyType().isNTerminal();
            final boolean isFromRange = range.getFromCvFuzzyType().isRange();
            final boolean isFromCertain = range.getFromCvFuzzyType().isCertain();
            final boolean isFromLess = range.getFromCvFuzzyType().isLessThan();
            final boolean isFromGreater = range.getFromCvFuzzyType().isGreaterThan();
            final boolean isFromUndetermined = range.getFromCvFuzzyType().isUndetermined();

            final boolean isToCTerminal = range.getToCvFuzzyType().isCTerminal();
            final boolean isToNTerminal = range.getToCvFuzzyType().isNTerminal();
            final boolean isToRange = range.getToCvFuzzyType().isRange();
            final boolean isToCertain = range.getToCvFuzzyType().isCertain();
            final boolean isToLess = range.getToCvFuzzyType().isLessThan();
            final boolean isToGreater = range.getToCvFuzzyType().isGreaterThan();
            final boolean isToUndetermined = range.getToCvFuzzyType().isUndetermined();

            final boolean hasFrom = ( range.getFromIntervalStart() != 0 && range.getFromIntervalEnd() != 0 );
            final boolean hasTo = ( range.getToIntervalStart() != 0 && range.getToIntervalEnd() != 0 );

            // Check on mismatch between range boundaries and range attribute: undetermined
            if ( isUndertermined && ( hasFrom || hasTo ) ) {

                messages.add( new RangeMessage( MessageDefinition.UNDETERMINED_RANGE_WITH_BOUNDARIES, feature, range ) );

            } else if ( !isUndertermined && !( hasFrom || hasTo ) ) {

                messages.add( new RangeMessage( MessageDefinition.DETERMINED_RANGE_WITHOUT_BOUNDARIES, feature, range ) );

            } else {

                // Check on mismatch between range boundaries and respective CvFuzzyType
                if ( ( hasFrom && !( isFromCertain || isFromRange || isFromLess || isFromGreater ) ) ||
                     ( hasTo && !( isToCertain || isToRange || isToLess || isToGreater ) ) ) {

                    messages.add( new RangeMessage( MessageDefinition.DETERMINED_RANGE_WITHOUT_BOUNDARIES, feature, range ) );

                } else if ( ( !hasFrom && !( isFromCTerminal || isFromNTerminal || isFromUndetermined ) ) ||
                            ( !hasTo && !( isToCTerminal || isToNTerminal || isToUndetermined ) ) ) {

                    messages.add( new RangeMessage( MessageDefinition.UNDETERMINED_RANGE_WITH_BOUNDARIES, feature, range ) );
                }
            }
        }

        return messages;
    }
}
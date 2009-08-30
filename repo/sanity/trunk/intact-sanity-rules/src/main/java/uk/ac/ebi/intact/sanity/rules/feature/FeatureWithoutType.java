/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.feature;

import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Checks for feature without CvFeatureType.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule( target = Feature.class, group = {RuleGroup.INTACT, RuleGroup.IMEX} )

public class FeatureWithoutType extends Rule<Feature> {

    public Collection<GeneralMessage> check( Feature feature ) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        if ( !isIgnored( feature, MessageDefinition.FEATURE_WITHOUT_TYPE ) ) {
            if ( feature.getCvFeatureType() == null ) {
                messages.add( new GeneralMessage( MessageDefinition.FEATURE_WITHOUT_TYPE, feature ) );
            }
        }
        return messages;
    }
}
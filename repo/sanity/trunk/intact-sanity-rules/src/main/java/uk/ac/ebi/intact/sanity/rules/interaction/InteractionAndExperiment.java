/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.interaction;

import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Check if an interaction has exactly 1 experiment attached.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule( target = Interaction.class, group = {RuleGroup.INTACT, RuleGroup.IMEX} )

public class InteractionAndExperiment implements Rule<Interaction> {

    public Collection<GeneralMessage> check( Interaction interaction ) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        final Collection<Experiment> experiments = interaction.getExperiments();
        if ( experiments == null ) {

            messages.add( new GeneralMessage( MessageDefinition.INTERACTION_EXPERIMENT_COUNT, interaction ) );

        } else if ( experiments.size() != 1 ) {
            
            messages.add( new GeneralMessage( MessageDefinition.INTERACTION_EXPERIMENT_COUNT, interaction ) );
        }

        return messages;
    }
}
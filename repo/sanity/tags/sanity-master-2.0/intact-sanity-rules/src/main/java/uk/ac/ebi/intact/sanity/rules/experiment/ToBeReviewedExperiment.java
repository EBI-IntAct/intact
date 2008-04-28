/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.experiment;

import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.util.ExperimentUtils;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Check on experiment to-be-reviewed.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk), Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule(target = Experiment.class, group = RuleGroup.INTACT )
public class ToBeReviewedExperiment implements Rule<Experiment> {

    // TODO what about experiments imported through IMEx ?

    public Collection<GeneralMessage> check(Experiment experiment) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        if(ExperimentUtils.isToBeReviewed(experiment)){
            messages.add(new GeneralMessage( MessageDefinition.EXPERIMENT_TO_BE_REVIEWED, experiment));
        }
        return messages;
    }

    // exp
//    DEBUG - Running rule: ExperimentWithNoCvIdentification
//    DEBUG - Running rule: ExperimentOnHold
//    DEBUG - Running rule: ExperimentWithNoInteraction
//    DEBUG - Running rule: ExperimentWithNoPubmedXref
//    DEBUG - Running rule: ExperimentWithNoFullname
//    DEBUG - Running rule: ExperimentWithNoCvInteraction
//    DEBUG - Running rule: ExperimentWithNoBioSource

    // annotObj
//    DEBUG - Running rule: XrefWithNonValidPrimaryId
//    DEBUG - Running rule: BrokenUrl
}
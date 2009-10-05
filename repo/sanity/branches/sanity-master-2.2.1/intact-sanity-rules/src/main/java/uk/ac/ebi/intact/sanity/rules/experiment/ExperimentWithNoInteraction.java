/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.experiment;

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Checks on experiment without interaction.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk), Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule(target = Experiment.class, group = { RuleGroup.INTACT, RuleGroup.IMEX })
public class ExperimentWithNoInteraction implements Rule<Experiment> {

    public Collection<GeneralMessage> check(Experiment experiment) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        int interactionCount = 0;

        if (experiment.getAc() != null) {
            interactionCount = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getExperimentDao().countInteractionsForExperimentWithAc(experiment.getAc());
        } 

        if (interactionCount == 0) {
            interactionCount = experiment.getInteractions().size();
        }

        if(interactionCount == 0){
            messages.add(new GeneralMessage( MessageDefinition.EXPERIMENT_WITHOUT_INTERACTION, experiment ));
        }
        return messages;
    }
}
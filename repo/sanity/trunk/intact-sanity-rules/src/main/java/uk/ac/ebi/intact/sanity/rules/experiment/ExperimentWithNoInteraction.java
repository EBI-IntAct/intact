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
import uk.ac.ebi.intact.sanity.commons.rules.MessageLevel;
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

@SanityRule(target = Experiment.class)

public class ExperimentWithNoInteraction implements Rule<Experiment> {
    private static final String DESCRIPTION = "This/these experiments have no interaction";
    private static final String SUGGESTION = "Edit the experiment and add at least one interaction";

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
            messages.add(new GeneralMessage(DESCRIPTION, MessageLevel.ERROR, SUGGESTION, experiment ));
        }
        return messages;
    }

    public static String getDescription() {
        return DESCRIPTION;
    }

    public static String getSuggestion() {
        return SUGGESTION;
    }
}
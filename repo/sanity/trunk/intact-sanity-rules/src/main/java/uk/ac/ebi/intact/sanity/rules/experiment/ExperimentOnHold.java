/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.experiment;

import uk.ac.ebi.intact.context.IntactContext;
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
import java.util.List;

/**
 * Check is the experiment is on-hold.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk), Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule(target = Experiment.class, group = RuleGroup.INTACT_DB )
public class ExperimentOnHold extends Rule<Experiment> {

    public Collection<GeneralMessage> check(Experiment experiment) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        if( ! isIgnored( experiment, MessageDefinition.EXPERIMENT_ON_HOLD ) ) {
            if(ExperimentUtils.isOnHold(experiment)){
                messages.add(new GeneralMessage( MessageDefinition.EXPERIMENT_ON_HOLD, experiment));
            } else {
                // TODO this can be removed when all the experiments have a Publication object in the DB
                String pubmedId = ExperimentUtils.getPubmedId(experiment);

                if (pubmedId != null) {
                    final List<Experiment> experimentsSamePubmed = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                            .getExperimentDao().getByPubId(pubmedId);

                    for (Experiment experimentSamePubmed : experimentsSamePubmed) {
                        if (ExperimentUtils.isOnHold(experimentSamePubmed)) {
                            messages.add(new GeneralMessage( MessageDefinition.EXPERIMENT_ON_HOLD, experiment));
                        }
                    }
                }
            }
        }

        return messages;
    }
}
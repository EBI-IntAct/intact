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

import java.util.*;

/**
 * Checks on experiment not super-curated.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule(target = Experiment.class, group = RuleGroup.INTACT )
public class ExperimentNotSuperCurated  implements Rule<Experiment> {

    private static final Date startingDateSuperCuration;

    static{
        Calendar calendar = new GregorianCalendar();
        calendar.set(2005, Calendar.SEPTEMBER, 1);
        startingDateSuperCuration = calendar.getTime();
    }

    public Collection<GeneralMessage> check(Experiment experiment) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        if(startingDateSuperCuration.before(experiment.getCreated())){
            if(!ExperimentUtils.isAccepted(experiment) && !ExperimentUtils.isToBeReviewed(experiment)){
                messages.add(new GeneralMessage( MessageDefinition.EXPERIMENT_NOT_SUPER_CURATED, experiment));
            }
        }

        return messages;
    }
}
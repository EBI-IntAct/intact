/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.sanity.rules.experiment;

import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.ExperimentXref;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@SanityRule (target = Experiment.class, group = { RuleGroup.INTACT, RuleGroup.IMEX } )
public class ExperimentWithMultiplePrimaryRef implements Rule<Experiment> {

    public Collection<GeneralMessage> check(Experiment experiment) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        int primaryRefCount = 0;

        for (ExperimentXref xref : experiment.getXrefs()) {
            if (xref.getCvXrefQualifier() != null) {
                String mi = CvObjectUtils.getPsiMiIdentityXref(xref.getCvXrefQualifier()).getPrimaryId();
                
                if (CvXrefQualifier.PRIMARY_REFERENCE_MI_REF.equals(mi)) {
                    primaryRefCount++;
                }
            }
        }

        if (primaryRefCount > 1) {
            messages.add(new GeneralMessage(MessageDefinition.EXPERIMENT_WITH_MULTIPLE_PRIMARY_REF, experiment));
        }

        return messages;
    }
}
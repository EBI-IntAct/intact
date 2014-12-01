/**
 * Copyright 2010 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.editor.controller.curate.cloner;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.XrefUtils;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.model.extension.*;

/**
 * Editor specific cloning routine for complex participants.
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id: InteractionIntactCloner.java 14783 2010-07-29 12:52:28Z brunoaranda $
 * @since 2.0.1-SNAPSHOT
 */
public class InteractionEvidenceCloner extends EditorCloner{

    public static InteractionEvidence cloneInteraction(InteractionEvidence evidence, IntactDao dao) {
        IntactInteractionEvidence clone = new IntactInteractionEvidence(evidence.getShortName());

        initAuditProperties(clone, dao);

        clone.setInteractionType(evidence.getInteractionType());
        clone.setExperiment(evidence.getExperiment());
        clone.setAvailability(evidence.getAvailability());
        clone.setInferred(evidence.isInferred());
        clone.setNegative(evidence.isNegative());

        for (Xref ref : evidence.getIdentifiers()){
            clone.getXrefs().add(new InteractionXref(ref.getDatabase(), ref.getId(), ref.getVersion(), ref.getQualifier()));
        }

        for (Xref ref : evidence.getXrefs()){
            if (!XrefUtils.isXrefFromDatabase(ref, Xref.IMEX_MI, Xref.IMEX)
                    && !XrefUtils.doesXrefHaveQualifier(ref, Xref.PRIMARY_MI, Xref.PRIMARY)){
                clone.getXrefs().add(new InteractionXref(ref.getDatabase(), ref.getId(), ref.getVersion(), ref.getQualifier()));
            }

        }

        for (Annotation annotation : evidence.getAnnotations()){
            clone.getAnnotations().add(new InteractionAnnotation(annotation.getTopic(), annotation.getValue()));
        }

        for (ParticipantEvidence participant : evidence.getParticipants()){
            ParticipantEvidence r = ParticipantEvidenceCloner.cloneParticipant(participant, dao);
            clone.addParticipant(r);
        }

        for (Confidence confidence : evidence.getConfidences()){
            clone.getConfidences().add(new InteractionEvidenceConfidence(confidence.getType(), confidence.getValue()));
        }

        for (Parameter param : evidence.getParameters()){
            clone.getParameters().add(new InteractionEvidenceParameter(param.getType(), param.getValue(), param.getUnit(), param.getUncertainty()));
        }

        for (VariableParameterValueSet set : evidence.getVariableParameterValues()){
           VariableParameterValueSet setClone = new IntactVariableParameterValueSet(set);
            clone.getVariableParameterValues().add(setClone);
        }
        return clone;
    }
}


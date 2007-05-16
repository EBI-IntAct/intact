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
package uk.ac.ebi.intact.psixml.converter.shared;

import psidev.psi.mi.xml.model.*;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.CvIdentification;
import uk.ac.ebi.intact.model.CvInteraction;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.psixml.converter.AbstractIntactPsiConverter;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentConverter extends AbstractIntactPsiConverter<Experiment, ExperimentDescription> {

    public ExperimentConverter(IntactContext intactContext, Entry parentEntry) {
        super(intactContext, parentEntry);
    }

    public Experiment psiToIntact(ExperimentDescription psiObject) {
        String shortLabel = psiObject.getNames().getShortLabel();
        String fullName = psiObject.getNames().getFullName();

        Organism hostOrganism = psiObject.getHostOrganisms().iterator().next();
        BioSource bioSource = new OrganismConverter(getIntactContext(), getParentEntry()).psiToIntact(hostOrganism);

        InteractionDetectionMethod idm = psiObject.getInteractionDetectionMethod();
        CvInteraction cvInteractionDetectionMethod = new InteractionDetectionMethodConverter(getIntactContext(), getParentEntry()).psiToIntact(idm);

        ParticipantIdentificationMethod pim = psiObject.getParticipantIdentificationMethod();
        CvIdentification cvParticipantIdentification = new ParticipantIdentificationMethodConverter(getIntactContext(), getParentEntry()).psiToIntact(pim);

        Experiment experiment = new Experiment(getInstitution(), shortLabel, bioSource);
        experiment.setFullName(fullName);
        experiment.setCvInteraction(cvInteractionDetectionMethod);
        experiment.setCvIdentification(cvParticipantIdentification);

        return experiment;
    }

    public ExperimentDescription intactToPsi(Experiment intactObject) {
        throw new UnsupportedOperationException();
    }
}
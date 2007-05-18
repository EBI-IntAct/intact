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
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.psixml.converter.AbstractIntactPsiConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionConverter extends AbstractIntactPsiConverter<Interaction, psidev.psi.mi.xml.model.Interaction> {

    public InteractionConverter(IntactContext intactContext, Entry parentEntry) {
        super(intactContext, parentEntry);
    }

    public Interaction psiToIntact(psidev.psi.mi.xml.model.Interaction psiObject) {
        String shortLabel = psiObject.getNames().getShortLabel();
        String fullName = psiObject.getNames().getFullName();

        Collection<Experiment> experiments = getExperiments(psiObject);

        // only gets the first interaction type
        CvInteractionType interactionType = getInteractionType(psiObject);

        // only gets the first interactor type
        CvInteractorType interactorType = getInteractorType(psiObject);

        Interaction interaction = new InteractionImpl(experiments, interactionType, interactorType, shortLabel, getInstitution());
        interaction.setFullName(fullName);

        // components, created after the interaction, as we need the interaction to create them
        Collection<Component> components = getComponents(interaction, psiObject);
        interaction.setComponents(components);

        return interaction;
    }

    public psidev.psi.mi.xml.model.Interaction intactToPsi(Interaction intactObject) {
        throw new UnsupportedOperationException();
    }


    protected Collection<Experiment> getExperiments(psidev.psi.mi.xml.model.Interaction psiInteraction) {
        Collection<ExperimentDescription> expDescriptions = psiInteraction.getExperiments();

        List<Experiment> experiments = new ArrayList<Experiment>(expDescriptions.size());

        ExperimentConverter converter = new ExperimentConverter(getIntactContext(), getParentEntry());

        for (ExperimentDescription expDesc : expDescriptions) {
            Experiment experiment = converter.psiToIntact(expDesc);
            experiments.add(experiment);
        }

        return experiments;
    }

    /**
     * Get the first interaction type only
     */
    protected CvInteractionType getInteractionType(psidev.psi.mi.xml.model.Interaction psiInteraction) {
        InteractionType psiInteractionType = psiInteraction.getInteractionTypes().iterator().next();

        return new InteractionTypeConverter(getIntactContext(), getParentEntry()).psiToIntact(psiInteractionType);
    }


    /**
     * Gets the interactor type from the first participant only
     */
    protected CvInteractorType getInteractorType(psidev.psi.mi.xml.model.Interaction psiInteraction) {
        InteractorType psiInteractorType = psiInteraction.getParticipants().iterator().next().getInteractor().getInteractorType();

        return new InteractorTypeConverter(getIntactContext(), getParentEntry()).psiToIntact(psiInteractorType);
    }

    protected Collection<Component> getComponents(Interaction interaction, psidev.psi.mi.xml.model.Interaction psiInteraction) {
        List<Component> components = new ArrayList<Component>(psiInteraction.getParticipants().size());

        for (Participant participant : psiInteraction.getParticipants()) {
            Component component = ParticipantConverter.newComponent(getIntactContext(), participant, interaction, getParentEntry());
            components.add(component);
        }

        return components;
    }
}
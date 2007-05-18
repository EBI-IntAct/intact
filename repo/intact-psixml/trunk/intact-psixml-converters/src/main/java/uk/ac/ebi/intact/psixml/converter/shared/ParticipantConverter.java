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

import psidev.psi.mi.xml.model.Entry;
import psidev.psi.mi.xml.model.ExperimentalRole;
import psidev.psi.mi.xml.model.Participant;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.psixml.converter.AbstractIntactPsiConverter;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ParticipantConverter extends AbstractIntactPsiConverter<Component, Participant> {

    public ParticipantConverter(IntactContext intactContext, Entry parentEntry) {
        super(intactContext, parentEntry);
    }

    public Component psiToIntact(Participant psiObject) {
        Interaction interaction = new InteractionConverter(getIntactContext(), getParentEntry()).psiToIntact(psiObject.getInteraction());

        Component component = newComponent(getIntactContext(), psiObject, interaction, getParentEntry());

        return component;
    }

    public Participant intactToPsi(Component intactObject) {
        throw new UnsupportedOperationException();
    }

    /**
     * Only uses the first one
     */
    protected CvExperimentalRole getExperimentalRole(Participant psiObject) {
        ExperimentalRole role = psiObject.getExperimentalRoles().iterator().next();

        return new ExperimentalRoleConverter(getIntactContext(), getParentEntry()).psiToIntact(role);
    }

    static Component newComponent(IntactContext context, Participant psiObject, Interaction interaction, Entry parentEntry) {
        Interactor interactor = new InteractorConverter(context, parentEntry).psiToIntact(psiObject.getInteractor());
        CvBiologicalRole biologicalRole = new BiologicalRoleConverter(context, parentEntry).psiToIntact(psiObject.getBiologicalRole());

        // only the first experimental role
        ExperimentalRole role = psiObject.getExperimentalRoles().iterator().next();
        CvExperimentalRole experimentalRole = new ExperimentalRoleConverter(context, parentEntry).psiToIntact(role);

        Component component = new Component(context.getInstitution(), interaction, interactor, experimentalRole, biologicalRole);

        return component;
    }
}
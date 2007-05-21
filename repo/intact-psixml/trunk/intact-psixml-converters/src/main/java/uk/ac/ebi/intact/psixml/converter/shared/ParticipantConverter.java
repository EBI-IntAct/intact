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

import psidev.psi.mi.xml.model.BiologicalRole;
import psidev.psi.mi.xml.model.ExperimentalRole;
import psidev.psi.mi.xml.model.Names;
import psidev.psi.mi.xml.model.Participant;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.psixml.converter.AbstractIntactPsiConverter;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ParticipantConverter extends AbstractIntactPsiConverter<Component, Participant> {

    public ParticipantConverter(Institution institution) {
        super(institution);
    }

    public Component psiToIntact(Participant psiObject) {
        Interaction interaction = new InteractionConverter(getInstitution()).psiToIntact(psiObject.getInteraction());

        Component component = newComponent(getInstitution(), psiObject, interaction);

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

        return new ExperimentalRoleConverter(getInstitution()).psiToIntact(role);
    }

    static Component newComponent(Institution institution, Participant participant, Interaction interaction) {
        Interactor interactor = new InteractorConverter(institution).psiToIntact(participant.getInteractor());

        BiologicalRole psiBioRole = participant.getBiologicalRole();
        if (psiBioRole == null) {
            psiBioRole = unspecifiedBiologicalRole();
        }
        CvBiologicalRole biologicalRole = new BiologicalRoleConverter(institution).psiToIntact(psiBioRole);

        // only the first experimental role
        ExperimentalRole role = participant.getExperimentalRoles().iterator().next();
        CvExperimentalRole experimentalRole = new ExperimentalRoleConverter(institution).psiToIntact(role);

        Component component = new Component(institution, interaction, interactor, experimentalRole, biologicalRole);

        return component;
    }

    static BiologicalRole unspecifiedBiologicalRole() {
        BiologicalRole role = new BiologicalRole();

        Names names = new Names();
        names.setShortLabel("unspeficied role");
        names.setFullName("unspecified role");

        role.setNames(names);

        return role;
    }
}
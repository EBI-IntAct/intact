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
import psidev.psi.mi.xml.model.InteractorType;
import psidev.psi.mi.xml.model.Organism;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.CvInteractorType;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.psixml.converter.AbstractIntactPsiConverter;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractorConverter extends AbstractIntactPsiConverter<Interactor, psidev.psi.mi.xml.model.Interactor> {

    public InteractorConverter(IntactContext intactContext, Entry parentEntry) {
        super(intactContext, parentEntry);
    }

    public Interactor psiToIntact(psidev.psi.mi.xml.model.Interactor psiObject) {
        String shortLabel = psiObject.getNames().getShortLabel();
        String fullName = psiObject.getNames().getFullName();

        Interactor interactor = newInteractorAccordingToType(psiObject.getOrganism(), shortLabel, psiObject.getInteractorType());
        interactor.setFullName(fullName);

        return interactor;
    }

    public psidev.psi.mi.xml.model.Interactor intactToPsi(Interactor intactObject) {
        throw new UnsupportedOperationException();
    }

    protected Interactor newInteractorAccordingToType(Organism psiOrganism, String shortLabel, InteractorType psiInteractorType) {
        BioSource organism = new OrganismConverter(getIntactContext(), getParentEntry()).psiToIntact(psiOrganism);
        CvInteractorType interactorType = new InteractorTypeConverter(getIntactContext(), getParentEntry()).psiToIntact(psiInteractorType);

        String interactorTypeLabel = psiInteractorType.getNames().getShortLabel();

        Interactor interactor = null;

        if (interactorTypeLabel.equals("protein")) {
            interactor = new ProteinImpl(getInstitution(), organism, shortLabel, interactorType);
        } else {
            throw new RuntimeException("Interaction of unexpected type: " + interactorTypeLabel);
        }

        return interactor;
    }
}
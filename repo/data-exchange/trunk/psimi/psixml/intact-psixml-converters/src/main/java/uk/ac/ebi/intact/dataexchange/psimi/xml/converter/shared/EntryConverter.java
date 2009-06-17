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
package uk.ac.ebi.intact.dataexchange.psimi.xml.converter.shared;

import psidev.psi.mi.xml.model.*;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.AbstractIntactPsiConverter;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.ConverterContext;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.location.LocationItem;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.util.ConversionCache;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.util.IntactConverterUtils;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.util.PsiConverterUtils;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.IntactEntry;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Entry Converter.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class EntryConverter extends AbstractIntactPsiConverter<IntactEntry, Entry> {

    public EntryConverter() {
        super(null);
    }

    @Deprecated
    public EntryConverter(Institution institution) {
        super(institution);
    }

    public IntactEntry psiToIntact(Entry psiObject) {
        psiStartConversion(psiObject);

        InstitutionConverter institutionConverter = new InstitutionConverter();
        Institution institution = institutionConverter.psiToIntact(psiObject.getSource());

        super.setInstitution(institution);
 
        Collection<Interaction> interactions = new ArrayList<Interaction>();

        InteractionConverter interactionConverter = new InteractionConverter(institution);

        for (psidev.psi.mi.xml.model.Interaction psiInteraction : psiObject.getInteractions()) {
            Interaction interaction = interactionConverter.psiToIntact(psiInteraction);
            interactions.add(interaction);
        }

        IntactEntry ientry = new IntactEntry(interactions);
        ientry.setInstitution(getInstitution());
        
        IntactConverterUtils.populateAnnotations(psiObject, ientry, institution);

        if (psiObject.getSource().getReleaseDate() != null) {
            ientry.setReleasedDate(psiObject.getSource().getReleaseDate());
        }

        ConversionCache.clear();

        psiEndConversion(psiObject);
        ConverterContext.getInstance().getLocation().resetPosition();

        failIfInconsistentConversion(ientry, psiObject);

        return ientry;
    }

    public Entry intactToPsi(IntactEntry intactObject) {
        intactStartConversation(intactObject);

        Entry entry = new Entry();

        Interaction firstInteraction = intactObject.getInteractions().iterator().next();
        Institution institution = firstInteraction.getOwner();

        super.setInstitution(firstInteraction.getOwner());

        InstitutionConverter institutionConverter = new InstitutionConverter();
        entry.setSource(institutionConverter.intactToPsi(institution));

        InteractionConverter interactionConverter = new InteractionConverter(getInstitution());

        for (Interaction intactInteracton : intactObject.getInteractions()) {
            psidev.psi.mi.xml.model.Interaction interaction = interactionConverter.intactToPsi(intactInteracton);
            entry.getInteractions().add(interaction);

            if( ConverterContext.getInstance().isGenerateCompactXml() ) {

                for ( Component component : intactInteracton.getComponents() ) {
                    psidev.psi.mi.xml.model.Interactor interactor =
                            new InteractorConverter(getInstitution()).intactToPsi( component.getInteractor() );
                    if ( ! contains( entry.getInteractors(), interactor ) ) {
                        entry.getInteractors().add( interactor );
                    }
                }

//                for (Participant participant : interaction.getParticipants()) {
//                    if (!contains(entry.getInteractors(), participant.getInteractor())) {
//                        entry.getInteractors().add(participant.getInteractor());
//                    }
//                }
            }

            if( ConverterContext.getInstance().isGenerateCompactXml() ) {

                for ( uk.ac.ebi.intact.model.Experiment e : intactInteracton.getExperiments() ) {
                    ExperimentConverter experimentConverter = new ExperimentConverter(getInstitution());
                    final ExperimentDescription experimentDesc = experimentConverter.intactToPsi( e );
                    if (!contains(entry.getExperiments(), experimentDesc)) {
                        entry.getExperiments().add( experimentDesc );
                    }
                }

//                for (ExperimentDescription experimentDesc : interaction.getExperiments()) {
//                    if (!contains(entry.getExperiments(), experimentDesc)) {
//                        entry.getExperiments().add(experimentDesc);
//                    }
//                }
            }
        }

        ConversionCache.clear();
        
        intactEndConversion(intactObject);

        failIfInconsistentConversion(intactObject, entry);

        return entry;

    }

    public boolean contains(Collection<? extends HasId> idElements, HasId hasId) {
        for (HasId idElement : idElements) {
            if (idElement != null && idElement.getId() == hasId.getId()) {
                return true;
            }
        }
        return false;
    }

    protected void failIfInconsistentConversion(IntactEntry intactEntry, Entry psiEntry) {
        failIfInconsistentCollectionSize("interaction", intactEntry.getInteractions(), psiEntry.getInteractions());
        failIfInconsistentCollectionSize("experiment", intactEntry.getExperiments(), PsiConverterUtils.nonRedundantExperimentsFromPsiEntry(psiEntry));
        failIfInconsistentCollectionSize("interactor", intactEntry.getInteractors(), PsiConverterUtils.nonRedundantInteractorsFromPsiEntry(psiEntry));
    }

}
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.xml.model.*;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.UnsupportedConversionException;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.MessageLevel;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.util.ConversionCache;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.util.IntactConverterUtils;
import uk.ac.ebi.intact.dataexchange.psimi.xml.converter.util.PsiConverterUtils;
import uk.ac.ebi.intact.model.*;

/**
 * Experiment converter.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentConverter extends AbstractAnnotatedObjectConverter<Experiment, ExperimentDescription> {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(ExperimentConverter.class);

    public ExperimentConverter(Institution institution) {
        super(institution, Experiment.class, ExperimentDescription.class);
    }

    public Experiment psiToIntact(ExperimentDescription psiObject) {
        Experiment experiment = super.psiToIntact(psiObject);

        if (!isNewIntactObjectCreated()) {
            return experiment;
        }

        psiStartConversion(psiObject);

        String shortLabel;

        if (psiObject.getNames() != null) {
           shortLabel = IntactConverterUtils.getShortLabelFromNames(psiObject.getNames());
        } else {
           shortLabel = IntactConverterUtils.createExperimentTempShortLabel(); 
        }

        BioSource bioSource = null;

        if (psiObject.getHostOrganisms() != null && !psiObject.getHostOrganisms().isEmpty()) {
            Organism hostOrganism = psiObject.getHostOrganisms().iterator().next();
            bioSource = new OrganismConverter(experiment.getOwner()).psiToIntact(hostOrganism);
        }

        InteractionDetectionMethod idm = psiObject.getInteractionDetectionMethod();
        CvInteraction cvInteractionDetectionMethod = new InteractionDetectionMethodConverter(getInstitution()).psiToIntact(idm);

        experiment.setOwner(getInstitution());
        experiment.setShortLabel(shortLabel);

        if (bioSource != null) {
            experiment.setBioSource(bioSource);
        }

        IntactConverterUtils.populateNames(psiObject.getNames(), experiment);
        IntactConverterUtils.populateXref(psiObject.getXref(), experiment, new XrefConverter<ExperimentXref>(getInstitution(), ExperimentXref.class));

        // fail if the primary reference does not point to Pubmed and primary-reference
        boolean hasValidPrimaryRef = true;
        final Bibref bibref = psiObject.getBibref();
        if( bibref != null && bibref.getXref() != null ) {
            final DbReference primaryRef = bibref.getXref().getPrimaryRef();
            if ( ! hasValidPrimaryRef( primaryRef ) ) {
                final String message = "Bibref in ExperimentDescription [PSI Id=" + psiObject.getId() + "] " +
                                       "should have a primary-reference (refTypeAc=" + CvXrefQualifier.PRIMARY_REFERENCE_MI_REF + ") " +
                                       "with reference to Pubmed (dbAc=" + CvDatabase.PUBMED_MI_REF + ") or a DOI (dbAc=" + CvDatabase.DOI_MI_REF + "): " + primaryRef;
                log.warn(message);
                addMessageToContext(MessageLevel.WARN, message, true);

                hasValidPrimaryRef = false;
            }
        } else {
            final String message = "No bibref defined in ExperimentDescription [PSI Id=" + psiObject.getId() + "]. " +
                                   "It should have a primary-reference (refTypeAc=" + CvXrefQualifier.PRIMARY_REFERENCE_MI_REF + ") " +
                                   "with reference to Pubmed (dbAc=" + CvDatabase.PUBMED_MI_REF + ") or a DOI (dbAc=" + CvDatabase.DOI_MI_REF + "): " + primaryRef;
            log.warn(message);
                addMessageToContext(MessageLevel.WARN, message, true);

            hasValidPrimaryRef = false;
        }

        IntactConverterUtils.populateXref(psiObject.getBibref().getXref(), experiment, new XrefConverter<ExperimentXref>(getInstitution(), ExperimentXref.class));
        IntactConverterUtils.populateAnnotations(psiObject, experiment, getInstitution());
        
        experiment.setCvInteraction(cvInteractionDetectionMethod);

        ParticipantIdentificationMethod pim = psiObject.getParticipantIdentificationMethod();
        if (pim != null) {
            CvIdentification cvParticipantIdentification = new ParticipantIdentificationMethodConverter(getInstitution()).psiToIntact(pim);
            experiment.setCvIdentification(cvParticipantIdentification);
        }

        if( hasValidPrimaryRef ) {
            Publication publication = createPublication(psiObject);
            experiment.setPublication(publication);
        }

        psiEndConversion(psiObject);

        return experiment;
    }

    public ExperimentDescription intactToPsi(Experiment intactObject) {
        ExperimentDescription expDesc = super.intactToPsi(intactObject);

        if (!isNewPsiObjectCreated()) {
            return expDesc;
        }

        intactStartConversation(intactObject);

        Bibref bibref = new Bibref();

        try {
            PsiConverterUtils.populate(intactObject, bibref, this);
        } catch (UnsupportedConversionException e) {
            throw new UnsupportedConversionException("No Bibref could be found for Experiment with Xrefs: "+intactObject.getXrefs(), e);
        }

        InteractionDetectionMethodConverter detMethodConverter = new InteractionDetectionMethodConverter(getInstitution());
        InteractionDetectionMethod detMethod = (InteractionDetectionMethod) PsiConverterUtils.toCvType(intactObject.getCvInteraction(), detMethodConverter, this);

        expDesc.setBibref(bibref);
        expDesc.setInteractionDetectionMethod(detMethod);

        PsiConverterUtils.populate(intactObject, expDesc, this);

        if (intactObject.getCvIdentification() != null) {
            ParticipantIdentificationMethod identMethod = (ParticipantIdentificationMethod)
                    PsiConverterUtils.toCvType(intactObject.getCvIdentification(),
                                               new ParticipantIdentificationMethodConverter(getInstitution()),
                                               this );
            expDesc.setParticipantIdentificationMethod(identMethod);
        }

        if (intactObject.getBioSource() != null) {
            Organism organism = new OrganismConverter(getInstitution()).intactToPsi(intactObject.getBioSource());
            expDesc.getHostOrganisms().add(organism);
        }

        intactEndConversion(intactObject);

        return expDesc;
    }

    private boolean hasValidPrimaryRef( DbReference primaryRef ) {
        if (!CvXrefQualifier.PRIMARY_REFERENCE_MI_REF.equals(primaryRef.getRefTypeAc()) ||
                (!CvDatabase.PUBMED_MI_REF.equals(primaryRef.getDbAc())
                        && !CvDatabase.DOI_MI_REF.equals(primaryRef.getDbAc()))
                ) {
            return false;
        }
        return true;
    }

    private Publication createPublication(ExperimentDescription experiment) {
        final DbReference primaryRef = experiment.getBibref().getXref().getPrimaryRef();

        final String dbac = primaryRef.getDbAc();

        if (! hasValidPrimaryRef(primaryRef) ) {
            final String message = "Could not build a Publication from ExperimentDescription [PSI Id=" + experiment.getId() + "] " +
                                   "as it should have a primary-reference (refTypeAc=" + CvXrefQualifier.PRIMARY_REFERENCE_MI_REF + ") " +
                                   "with reference to Pubmed (dbAc=" + CvDatabase.PUBMED_MI_REF + ") or a DOI (dbAc=" + CvDatabase.DOI_MI_REF + "): " + primaryRef;
            log.warn(message);
            addMessageToContext(MessageLevel.WARN, message, true);
        }


        String pubId = primaryRef.getId();

        Publication publication = (Publication) ConversionCache.getElement("pub:"+pubId);

        if (publication != null) {
            return publication;
        }

        publication = new Publication(getInstitution(), pubId);
        ConversionCache.putElement("pub:"+pubId, publication);

        return publication;
    }
}
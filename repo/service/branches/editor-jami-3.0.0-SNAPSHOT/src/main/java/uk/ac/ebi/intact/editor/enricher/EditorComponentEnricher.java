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
package uk.ac.ebi.intact.editor.enricher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.FeatureEnricher;
import psidev.psi.mi.jami.enricher.OrganismEnricher;
import psidev.psi.mi.jami.enricher.ParticipantEvidenceEnricher;
import psidev.psi.mi.jami.enricher.exception.EnricherException;
import psidev.psi.mi.jami.enricher.impl.CompositeInteractorEnricher;
import psidev.psi.mi.jami.enricher.listener.EntityEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.FeatureEvidence;
import psidev.psi.mi.jami.model.ParticipantEvidence;
import uk.ac.ebi.intact.jami.dao.IntactDao;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * Editor enricher for participant evidences when importing files
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class EditorComponentEnricher implements ParticipantEvidenceEnricher<ParticipantEvidence>{

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(EditorComplexEnricher.class);

    @Resource(name = "intactParticipantEvidenceEnricher")
    private  ParticipantEvidenceEnricher intactParticipantEvidenceEnricher;
    @Resource(name = "intactDao")
    private IntactDao intactDao;

    private String importTag;

    public EditorComponentEnricher() {
    }

    public String getImportTag() {
        return importTag;
    }

    public void setImportTag(String importTag) {
        this.importTag = importTag;
    }

    @Override
    public OrganismEnricher getOrganismEnricher() {
        return intactParticipantEvidenceEnricher.getOrganismEnricher();
    }

    @Override
    public void setOrganismEnricher(OrganismEnricher enricher) {

    }

    @Override
    public CvTermEnricher<CvTerm> getCvTermEnricher() {
        return null;
    }

    @Override
    public void setCvTermEnricher(CvTermEnricher<CvTerm> enricher) {

    }

    @Override
    public CompositeInteractorEnricher getInteractorEnricher() {
        return null;
    }

    @Override
    public FeatureEnricher<FeatureEvidence> getFeatureEnricher() {
        return null;
    }

    @Override
    public EntityEnricherListener getParticipantEnricherListener() {
        return null;
    }

    @Override
    public void setInteractorEnricher(CompositeInteractorEnricher interactorEnricher) {

    }

    @Override
    public void setFeatureEnricher(FeatureEnricher<FeatureEvidence> enricher) {

    }

    @Override
    public void setParticipantEnricherListener(EntityEnricherListener listener) {

    }

    @Override
    public void enrich(Collection<ParticipantEvidence> objects) throws EnricherException {

    }

    @Override
    public void enrich(ParticipantEvidence objectToEnrich, ParticipantEvidence objectSource) throws EnricherException {

    }

    @Override
    public void enrich(ParticipantEvidence object) throws EnricherException {

    }
}

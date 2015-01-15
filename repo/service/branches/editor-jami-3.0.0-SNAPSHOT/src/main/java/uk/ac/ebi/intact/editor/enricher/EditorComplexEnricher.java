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
import psidev.psi.mi.jami.bridges.fetcher.InteractorFetcher;
import psidev.psi.mi.jami.enricher.*;
import psidev.psi.mi.jami.enricher.exception.EnricherException;
import psidev.psi.mi.jami.enricher.listener.InteractionEnricherListener;
import psidev.psi.mi.jami.enricher.listener.InteractorEnricherListener;
import psidev.psi.mi.jami.model.Complex;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.model.extension.InteractorAnnotation;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * Editor enricher for complexes when importing files
 *
 */
public class EditorComplexEnricher implements psidev.psi.mi.jami.enricher.ComplexEnricher{

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(EditorComplexEnricher.class);

    @Resource(name = "intactComplexEnricher")
    private ComplexEnricher intactComplexEnricher;
    @Resource(name = "intactDao")
    private IntactDao intactDao;
    @Resource(name = "editorModelledParticipantEnricher")
    private ParticipantEnricher editorModelledParticipantEnricher;
    @Resource(name = "editorMiEnricher")
    private CvTermEnricher<CvTerm> editorMiEnricher;
    @Resource(name = "editorSourceEnricher")
    private SourceEnricher editorSourceEnricher;
    private String importTag;

    public EditorComplexEnricher() {
    }

    @Override
    public ParticipantEnricher getParticipantEnricher() {
        return editorModelledParticipantEnricher;
    }

    @Override
    public InteractorFetcher<Complex> getInteractorFetcher() {
        return intactComplexEnricher.getInteractorFetcher();
    }

    @Override
    public InteractorEnricherListener<Complex> getListener() {
        return intactComplexEnricher.getListener();
    }

    @Override
    public CvTermEnricher<CvTerm> getCvTermEnricher() {
        return editorMiEnricher;
    }

    @Override
    public OrganismEnricher getOrganismEnricher() {
        return intactComplexEnricher.getOrganismEnricher();
    }

    @Override
    public void setListener(InteractorEnricherListener<Complex> listener) {

    }

    @Override
    public InteractionEnricherListener<Complex> getInteractionEnricherListener() {
        return intactComplexEnricher.getInteractionEnricherListener();
    }

    @Override
    public void setParticipantEnricher(psidev.psi.mi.jami.enricher.ParticipantEnricher enricher) {

    }

    @Override
    public void setCvTermEnricher(CvTermEnricher<CvTerm> enricher) {

    }

    @Override
    public void setOrganismEnricher(OrganismEnricher enricher) {

    }

    @Override
    public void setInteractionEnricherListener(InteractionEnricherListener<Complex> listener) {

    }

    @Override
    public SourceEnricher getSourceEnricher() {
        return editorSourceEnricher;
    }

    @Override
    public void setSourceEnricher(SourceEnricher enricher) {

    }

    @Override
    public void enrich(Complex object) throws EnricherException {
        if (intactComplexEnricher instanceof uk.ac.ebi.intact.dataexchange.enricher.standard.ComplexEnricher){
            ((uk.ac.ebi.intact.dataexchange.enricher.standard.ComplexEnricher) intactComplexEnricher).setParticipantEnricher(editorModelledParticipantEnricher);
            ((uk.ac.ebi.intact.dataexchange.enricher.standard.ComplexEnricher) intactComplexEnricher).setCvTermEnricher(editorMiEnricher);
            ((uk.ac.ebi.intact.dataexchange.enricher.standard.ComplexEnricher) intactComplexEnricher).setSourceEnricher(editorSourceEnricher);
        }

        intactComplexEnricher.enrich(object);

        if (getImportTag() != null && object != null){
            // check if object exists in database before adding a tag
            if (intactDao.getSynchronizerContext().getComplexSynchronizer().findAllMatchingAcs(object).isEmpty()){
                object.getAnnotations().add(new InteractorAnnotation(IntactUtils.createMITopic(null, "remark-internal"), getImportTag()));
            }
        }
    }

    @Override
    public void enrich(Collection<Complex> objects) throws EnricherException {
        intactComplexEnricher.enrich(objects);
    }

    @Override
    public void enrich(Complex objectToEnrich, Complex objectSource) throws EnricherException {
        intactComplexEnricher.enrich(objectToEnrich, objectSource);
    }

    public String getImportTag() {
        return importTag;
    }

    public void setImportTag(String importTag) {
        this.importTag = importTag;
    }
}

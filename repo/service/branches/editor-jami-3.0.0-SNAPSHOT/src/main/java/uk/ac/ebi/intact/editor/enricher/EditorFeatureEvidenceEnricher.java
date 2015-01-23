package uk.ac.ebi.intact.editor.enricher;

import psidev.psi.mi.jami.enricher.ComplexEnricher;
import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.exception.EnricherException;
import psidev.psi.mi.jami.enricher.listener.FeatureEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.FeatureEvidence;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * Editor feature evidence enricher
 */
public class EditorFeatureEvidenceEnricher implements psidev.psi.mi.jami.enricher.FeatureEnricher<FeatureEvidence> {
    private String importTag;

    @Resource(name = "intactComplexEnricher")
    private ComplexEnricher intactComplexEnricher;

    @Override
    public void setFeaturesWithRangesToUpdate(Collection<FeatureEvidence> features) {

    }

    @Override
    public FeatureEnricherListener<FeatureEvidence> getFeatureEnricherListener() {
        return null;
    }

    @Override
    public CvTermEnricher<CvTerm> getCvTermEnricher() {
        return null;
    }

    @Override
    public void setFeatureEnricherListener(FeatureEnricherListener<FeatureEvidence> listener) {

    }

    @Override
    public void setCvTermEnricher(CvTermEnricher<CvTerm> cvEnricher) {

    }

    @Override
    public void enrich(FeatureEvidence object) throws EnricherException {

    }

    @Override
    public void enrich(Collection<FeatureEvidence> objects) throws EnricherException {

    }

    @Override
    public void enrich(FeatureEvidence objectToEnrich, FeatureEvidence objectSource) throws EnricherException {

    }

    public String getImportTag() {
        return importTag;
    }

    public void setImportTag(String importTag) {
        this.importTag = importTag;
    }
}

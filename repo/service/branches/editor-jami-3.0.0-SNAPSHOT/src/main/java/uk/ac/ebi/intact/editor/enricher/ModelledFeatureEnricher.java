package uk.ac.ebi.intact.editor.enricher;

import psidev.psi.mi.jami.model.ModelledFeature;
import uk.ac.ebi.intact.dataexchange.enricher.standard.FeatureEnricher;

/**
 * Provides full enrichment of feature.
 *
 * - enrich minimal properties of feature (see MinimalFeatureEnricher)
 * - enrich interaction dependency
 * - enrich interaction effect
 * - enrich xrefs
 * - enrich aliases
 * - enrich annotations
 * - enrich linked features
 *
 *
 * @since 13/08/13
 */
public class ModelledFeatureEnricher extends FeatureEnricher<ModelledFeature> {

    public ModelledFeatureEnricher(){
        super();
    }
}

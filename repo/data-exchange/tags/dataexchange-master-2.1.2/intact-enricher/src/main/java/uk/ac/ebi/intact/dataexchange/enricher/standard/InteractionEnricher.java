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
package uk.ac.ebi.intact.dataexchange.enricher.standard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uk.ac.ebi.intact.dataexchange.enricher.EnricherContext;
import uk.ac.ebi.intact.dataexchange.enricher.EnricherException;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;
import uk.ac.ebi.intact.model.util.InteractionUtils;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
public class InteractionEnricher extends AnnotatedObjectEnricher<Interaction> {

    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(InteractionEnricher.class);

    @Autowired
    private EnricherContext enricherContext;

    @Autowired
    private CvObjectEnricher cvObjectEnricher;

    @Autowired
    private ExperimentEnricher experimentEnricher;

    @Autowired
    private ComponentEnricher componentEnricher;

    public InteractionEnricher() {
    }

    public void enrich(Interaction objectToEnrich) {
        for (Experiment experiment : objectToEnrich.getExperiments()) {
            experimentEnricher.enrich(experiment);
        }

        if (objectToEnrich.getComponents() == null) {
            throw new EnricherException("Interaction without components: "+objectToEnrich.getShortLabel());
        }

        for (Component component : objectToEnrich.getComponents()) {
             componentEnricher.enrich(component);
        }
        
        if (objectToEnrich.getCvInteractionType() != null) {
            cvObjectEnricher.enrich(objectToEnrich.getCvInteractionType());
        }

        if (enricherContext.getConfig().isUpdateInteractionShortLabels()) {
            try {
                String label = InteractionUtils.calculateShortLabel(objectToEnrich);
                objectToEnrich.setShortLabel(AnnotatedObjectUtils.prepareShortLabel(label));
            } catch (Exception e) {
                log.error("Label for interaction could not be calculated: "+objectToEnrich.getShortLabel(), e);
            }
        }

        super.enrich(objectToEnrich);
    }
}

/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.psimitab.converters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.tab.processor.PostProcessorStrategy;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;
import uk.ac.ebi.intact.psimitab.converters.expansion.ExpansionStrategy;
import uk.ac.ebi.intact.psimitab.converters.expansion.NotExpandableInteractionException;
import uk.ac.ebi.intact.psimitab.converters.expansion.SpokeWithoutBaitExpansion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


/**
 * Converter for intact-model-interaction to psimi-tab-binaryinteraction.
 *
 * @author Nadin Neuhauser
 * @version $Id$
 * @since 2.0.0
 */
public class Intact2BinaryInteractionConverter {

    private static final Log log = LogFactory.getLog( Intact2BinaryInteractionConverter.class );

    public static final Log logger = LogFactory.getLog( Intact2BinaryInteractionConverter.class );

    private ExpansionStrategy expansionStrategy;

    private InteractionConverter interactionConverter = new InteractionConverter();

    private PostProcessorStrategy<IntactBinaryInteraction> postProcessor;
                                    
    public Intact2BinaryInteractionConverter() {
        this(new SpokeWithoutBaitExpansion(), null);
    }

    public Intact2BinaryInteractionConverter(ExpansionStrategy expansionStrategy) {
        this(expansionStrategy, null);
    }

    public Intact2BinaryInteractionConverter(ExpansionStrategy expansionStrategy, PostProcessorStrategy<IntactBinaryInteraction> postProcessor) {
        this.expansionStrategy = expansionStrategy;
        this.postProcessor = postProcessor;
    }

    /////////////////////
    // Getters & Setters

    public ExpansionStrategy getExpansionStrategy() {
        return expansionStrategy;
    }

    public void setExpansionStrategy( ExpansionStrategy expansionStrategy ) {
        this.expansionStrategy = expansionStrategy;
    }

    public PostProcessorStrategy getPostProssesorStrategy() {
        return postProcessor;
    }

    public void setPostProssesorStrategy( PostProcessorStrategy postProssesorStrategy ) {
        this.postProcessor = postProssesorStrategy;
    }

    public Collection<IntactBinaryInteraction> convert( Interaction ... interactions ) throws NotExpandableInteractionException {
        return convert(Arrays.asList(interactions));
    }

    public Collection<IntactBinaryInteraction> convert( Collection<Interaction> interactions ) {
        if ( interactions == null ) {
            throw new IllegalArgumentException( "Interactions must not be null" );
        }

        if (expansionStrategy == null) {
            throw new NullPointerException("No expansion strategy defined");
        }

        Collection<IntactBinaryInteraction> result = new ArrayList<IntactBinaryInteraction>();

        for ( Interaction interaction : interactions ) {
            IntactBinaryInteraction bi;

            Collection<Interaction> expandedInteractions = null;
            try {
                expandedInteractions = expansionStrategy.expand(interaction);
            } catch ( NotExpandableInteractionException e ) {
                log.warn( "Interaction " + interaction.getAc() + " could not be expanded. Skipping." );
            }

            if( expandedInteractions != null ) {
                final boolean isExpanded = expandedInteractions.size() > 1;
                for (Interaction expandedInteraction : expandedInteractions) {

                    bi = interactionConverter.toBinaryInteraction(expandedInteraction, expansionStrategy, isExpanded);

                    if (bi != null) {
                        result.add(bi);
                    }
                }
            }
        }

        if ( ! result.isEmpty() ) {
            result = doPostProcessing( result );
        }
        
        return result;
    }


    /**
     * Apply post processing to the given collecition of interactions. if no processing was requested, the given
     * collection is returned.
     *
     * @param interactions the collection of interaction on which to apply post processing.
     * @return a non null collection of interactions.
     */
    private Collection<IntactBinaryInteraction> doPostProcessing( final Collection<IntactBinaryInteraction> interactions ) {

        if ( interactions == null ) {
            throw new IllegalArgumentException( "Interaction cannot be null." );
        }

        Collection<IntactBinaryInteraction> processedInteractions = null;

        // Run post processing (if requested)
        if ( postProcessor != null ) {
            if ( logger.isDebugEnabled() ) {
                logger.debug( "Running " + postProcessor.getClass().getSimpleName() + "..." );
            }
            processedInteractions = postProcessor.process( interactions );
            logger.debug( "Post processing completed." );
        } else {
            logger.debug( "No post processing requested." );
            processedInteractions = interactions;
        }

        return processedInteractions;
    }
}

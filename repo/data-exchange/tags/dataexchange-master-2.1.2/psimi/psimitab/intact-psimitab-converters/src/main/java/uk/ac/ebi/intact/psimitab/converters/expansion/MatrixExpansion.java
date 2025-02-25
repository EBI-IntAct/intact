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
package uk.ac.ebi.intact.psimitab.converters.expansion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.util.InteractionUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Process an interaction and expand it using the matrix model.
 *
 * @author Nadin Neuhauser
 * @version $Id$
 * @since 2.0.0
 */
public class MatrixExpansion extends BinaryExpansionStrategy {
    /**
     * Sets up a logger for that class.
     */
    public static final Log logger = LogFactory.getLog( MatrixExpansion.class );

    public static final String EXPANSION_NAME = "Matrix";

    /**
     * Apply the matrix expansion to the given interaction. Essentially, an interaction is created between any two
     * components.
     *
     * @param interaction the interaction to expand.
     * @return a non null collection of interaction, in case the expansion is not possible, we may return an empty
     *         collection.
     */
    public Collection<Interaction> expand( Interaction interaction ) {
        Collection<Interaction> interactions = new ArrayList<Interaction>();

        if ( InteractionUtils.isBinaryInteraction( interaction ) ) {
            logger.debug( "Interaction was binary, no further processing involved." );
            interactions.add( interaction );
        } else {

            Component[] components = interaction.getComponents().toArray( new Component[]{} );
            logger.debug( components.length + " participant(s) found." );

            for ( int i = 0; i < components.length; i++ ) {
                Component c1 = components[i];
                for ( int j = ( i + 1 ); j < components.length; j++ ) {
                    Component c2 = components[j];
                    // build a new interaction
                    Interaction newInteraction = buildInteraction( interaction, c1, c2 );
                    interactions.add( newInteraction );
                }
            }
            logger.debug( "After expansion: " + interactions.size() + " binary interaction(s) were generated." );
        }
        return interactions;
    }


    public String getName() {
        return EXPANSION_NAME;
    }
}

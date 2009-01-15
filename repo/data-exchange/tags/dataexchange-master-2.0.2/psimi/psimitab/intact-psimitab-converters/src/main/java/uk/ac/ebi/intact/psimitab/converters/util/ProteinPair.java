/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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
 * limitations under the License.
 */
package uk.ac.ebi.intact.psimitab.converters.util;

import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;

import java.util.Collection;

/**
 * A protein pair with attached collection of interactions they are known to interact in.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.2
 */
public class ProteinPair {

    String key;
    
    Collection<IntactBinaryInteraction> interactions;

    ProteinPair( String key, Collection<IntactBinaryInteraction> interactions ) {
        this.key = key;
        this.interactions = interactions;
    }

    public String getKey() {
        return key;
    }

    public Collection<IntactBinaryInteraction> getInteractions() {
        return interactions;
    }
}

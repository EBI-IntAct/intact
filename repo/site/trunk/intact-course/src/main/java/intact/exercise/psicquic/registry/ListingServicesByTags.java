/**
 * Copyright 2011 The European Bioinformatics Institute, and others.
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

package intact.exercise.psicquic.registry;

import org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;

/**
 * List the PSICQUIC Services by its tags, using the Registry.
 *
 * Exercise 2: Programmatic access to the Registry
 *
 * @see org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient
 * @see org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient#listServicesByTags(String)
 */
public class ListingServicesByTags {

    public static void main( String[] args ) throws Exception {
        // instantiate the registry client
        PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();

        // Question 3: Could you print the above information, but only for those services with the tag ‘imex curation’?

    }

}

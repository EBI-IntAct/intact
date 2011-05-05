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

package intact.exercise.chapter5.exercise2;

import org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;

/**
 * Question 6: Could you query all the active PSICQUIC services and print the partial and
 * total interaction counts for P04637 (TP53)?
 *
 * @see org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient
 * @see org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient#listActiveServices()
 * @see org.hupo.psi.mi.psicquic.registry.ServiceType
 * @see org.hupo.psi.mi.psicquic.wsclient.PsicquicSimpleClient
 * @see org.hupo.psi.mi.psicquic.wsclient.PsicquicSimpleClient#countByInteractor(String)
 */
public class Q6_QueryActiveServices {

    public static void main(String[] args) throws Exception {

        final String query = "P04637";  // TP53, P53

        long totalCount = 0;

        PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();

        // TODO start here - iterate through the active services and get the counts

    }
}

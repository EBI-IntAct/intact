/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.application.search3;

/**
 * Configuration constant names
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-Oct-2006</pre>
 */
public interface SearchEnvironment
{
    /**
     * Number of results shown per page, by default
     */
    static final String MAX_RESULTS_PER_PAGE = "uk.ac.ebi.intact.search.MAX_RESULTS_PER_PAGE";

    /**
     * The search link (relative to context root)
     */
    static final String HELP_LINK = "uk.ac.ebi.intact.search.HELP_LINK";
}

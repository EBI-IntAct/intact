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
package uk.ac.ebi.intact.context;

/**
 * Environment properties names
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04-Sep-2006</pre>
 */
public interface IntactEnvironment
{
    public static final String DATA_CONFIG_PARAM_NAME = "uk.ac.ebi.intact.DATA_CONFIG";

    public static final String INSTITUTION_LABEL = "uk.ac.ebi.intact.INSTITUTION_LABEL";
    public static final String INSTITUTION_FULL_NAME = "uk.ac.ebi.intact.INSTITUTION_FULL_NAME";
    public static final String INSTITUTION_POSTAL_ADDRESS = "uk.ac.ebi.intact.INSTITUTION_POSTAL_ADDRESS";
    public static final String INSTITUTION_URL = "uk.ac.ebi.intact.INSTITUTION_URL";

    public static final String AC_PREFIX_PARAM_NAME = "uk.ac.ebi.intact.AC_PREFIX";
    public static final String PRELOAD_COMMON_CVS_PARAM_NAME = "uk.ac.ebi.intact.PRELOAD_COMMON_CVOBJECTS";
    public static final String READ_ONLY_APP = "uk.ac.ebi.intact.READ_ONLY_APP";

}

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

package intact;

/**
 * Useful constants that are using in this course. All these values can also be found on the Ontology Lookup Service
 * web site:
 * <pre>
 * http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI.
 * </pre>
 */
public interface PsiMiConstants {

    // Databases

    public static final String PSI_MI_MIREF = "MI:0488";
    
    public static final String GO_MIREF = "MI:0448";

    public static final String INTACT_MIREF = "MI:0469";

    // Xref Type

    public static final String IDENTITY_MIREF = "MI:0356";

    // Experimental roles

    public static final String BAIT_MIREF = "MI:0496";
    
    public static final String PREY_MIREF = "MI:0498";
}

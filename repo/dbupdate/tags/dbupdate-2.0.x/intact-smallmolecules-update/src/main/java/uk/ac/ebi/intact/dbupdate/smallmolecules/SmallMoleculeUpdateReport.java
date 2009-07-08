/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.dbupdate.smallmolecules;

/**
 * Update report.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1
 */
public class SmallMoleculeUpdateReport {

    private int moleculeCount;
    private int totalFailureCount;
    private int multipleIdentityCount;
    private int noIdentityCount;
    private int unknownChebiIdCount;

    public SmallMoleculeUpdateReport() {
    }

    //////////////////
    // Getters

    public int getMoleculeCount() {
        return moleculeCount;
    }

    public int getTotalFailureCount() {
        return totalFailureCount;
    }

    public int getMultipleIdentityCount() {
        return multipleIdentityCount;
    }

    public int getNoIdentityCount() {
        return noIdentityCount;
    }

    public int getUnknownChebiIdCount() {
        return unknownChebiIdCount;
    }

    /////////////////
    // Adders

    public void incrementMoleculeCount() {
        moleculeCount++;
    }

    public void incrementMultipleIdentyCount() {
        multipleIdentityCount++;
        totalFailureCount++;
    }

    public void incrementNoIdentityCount() {
        noIdentityCount++;
        totalFailureCount++;
    }

    public void incrementUnknownChebiIdCount() {
        unknownChebiIdCount++;
        totalFailureCount++;
    }

    @Override
    public String toString() {
        return "SmallMolecule Update Report{" +
               "moleculeCount=" + moleculeCount +
               ", totalFailureCount=" + totalFailureCount +
               ", multipleIdentityCount=" + multipleIdentityCount +
               ", noIdentityCount=" + noIdentityCount +
               ", unknownChebiIdCount=" + unknownChebiIdCount +
               '}';
    }
}

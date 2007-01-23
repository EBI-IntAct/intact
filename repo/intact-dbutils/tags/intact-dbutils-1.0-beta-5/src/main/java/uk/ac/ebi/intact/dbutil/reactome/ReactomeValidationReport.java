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
package uk.ac.ebi.intact.dbutil.reactome;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents the report of a reactome export, containing the differences between reacome and intact
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ReactomeValidationReport implements Serializable
{

    public Collection<String> nonExistingReactomeIdsInIntact;
    public Collection<String> nonExistingIntactAcsInReactome;


    public ReactomeValidationReport()
    {
    }

    public ReactomeValidationReport(Collection<String> nonExistingReactomeAcsInIntact, Collection<String> nonExistingIntactIdsInReactome)
    {
        this.nonExistingReactomeIdsInIntact = nonExistingReactomeAcsInIntact;
        this.nonExistingIntactAcsInReactome = nonExistingIntactIdsInReactome;
    }


    public boolean isValid()
    {
        return nonExistingIntactAcsInReactome.isEmpty() && nonExistingReactomeIdsInIntact.isEmpty();
    }

    /**
     * @return Reactome ID that are used in IntAct but not existing in Reactome anymore
     */
    public Collection<String> getNonExistingReactomeIdsInIntact()
    {
        return nonExistingReactomeIdsInIntact;
    }

    public void setNonExistingReactomeIdsInIntact(Collection<String> nonExistingReactomeIdsInIntact)
    {
        this.nonExistingReactomeIdsInIntact = nonExistingReactomeIdsInIntact;
    }

    /**
     * @return Intact Interaction AC that are used in Reactome but not existing in IntAct anymore
     */
    public Collection<String> getNonExistingIntactAcsInReactome()
    {
        if (nonExistingIntactAcsInReactome == null)
        {
            nonExistingIntactAcsInReactome = new ArrayList<String>();
        }
        return nonExistingIntactAcsInReactome;
    }

    public void setNonExistingIntactAcsInReactome(Collection<String> nonExistingIntactAcsInReactome)
    {
        if (nonExistingIntactAcsInReactome == null)
        {
            nonExistingIntactAcsInReactome = new ArrayList<String>();
        }
        this.nonExistingIntactAcsInReactome = nonExistingIntactAcsInReactome;
    }
}

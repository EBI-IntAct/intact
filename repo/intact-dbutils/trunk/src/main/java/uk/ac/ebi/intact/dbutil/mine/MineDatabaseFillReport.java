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
package uk.ac.ebi.intact.dbutil.mine;

import java.util.List;
import java.util.ArrayList;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class MineDatabaseFillReport
{
    public List<String> nullTaxidInteractors;

    public MineDatabaseFillReport()
    {
        this.nullTaxidInteractors = new ArrayList<String>();
    }


    public List<String> getNullTaxidInteractors()
    {
        return nullTaxidInteractors;
    }

    public void setNullTaxidInteractors(List<String> nullTaxidInteractors)
    {
        this.nullTaxidInteractors = nullTaxidInteractors;
    }

    public void addNullTaxidInteraction(String interaction)
    {
         this.nullTaxidInteractors.add(interaction);
    }
}

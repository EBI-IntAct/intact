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
package uk.ac.ebi.intact.dbutil.update;

/**
     * Contains stats about the count of protein's biosources per experiment.
 */
class BioSourceStat
{
    //////////////////////////
    // Instance variable

    private String name;
    private String taxid;
    private int count = 0;

    //////////////////////////
    // Constructor

    public BioSourceStat( String name, String taxid ) {
        this.name = name;
        this.taxid = taxid;
    }

    ////////////////////
    // Getters
    public String getName() {
        return name;
    }

    public String getTaxid() {
        return taxid;
    }

    public int getCount() {
        return count;
    }

    ///////////////////////
    // Update stats
    public void increment() {
        count++;
    }

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append( getName() ).append( "(" ).append( getTaxid() ).append( "):" );
        sb.append( getCount() );
        return sb.toString();
    }
}

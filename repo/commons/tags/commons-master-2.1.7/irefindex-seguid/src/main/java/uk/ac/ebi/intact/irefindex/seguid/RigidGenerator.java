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
package uk.ac.ebi.intact.irefindex.seguid;


import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * A class that generates the RigIds
 * Reference for the methodology
 * iRefIndex: A consolidated protein interaction database with provenance
 * PMID: 18823568
 * http://www.biomedcentral.com/1471-2105/9/405
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class RigidGenerator {


    //Container for RigDataModels
    private List<RigDataModel> sequences = new ArrayList<RigDataModel>();


    public RigidGenerator() {
    }


    /**
     * Create a RigDataModel with the given sequence and taxid and add
     * it to the collection
     * @param sequence  protein sequence
     * @param taxid     taxonomy id
     */
    public void addSequence( String sequence, String taxid ) {

        RigDataModel rigDataModel = new RigDataModel( sequence, taxid );
        this.getSequences().add( rigDataModel );
    }

    /**
     * First get all the sequences to be processed,
     * Calcuate Rigid (seguid+taxid) for them
     * Sort them lexicographically
     * Concatinate them
     * Generate a Seguid for the resulting String
     *
     * @return  Rogid
     * @throws SeguidException  handled by SeguidException class
     */
    public String calculateRigid() throws SeguidException {

        final List<RigDataModel> sequencesToBeProcessed = this.getSequences();
        List<String> rigidCollection = new ArrayList<String>();

        if ( sequencesToBeProcessed != null && sequencesToBeProcessed.size() > 0 ) {

            RogidGenerator rogIdGenerator = new RogidGenerator();
            for ( RigDataModel rigDataModel : sequencesToBeProcessed ) {
                String rogid = rogIdGenerator.calculateRogid( rigDataModel.getSequence(), rigDataModel.getTaxid() );
                rigidCollection.add( rogid );
            }
            //sort them
            Collections.sort( rigidCollection );
            //concatenate them
            String allRigIds = StringUtils.concatenate( rigidCollection.toArray() );
            return rogIdGenerator.calculateSeguid( allRigIds );
        } else {
            return null;
        }

    }

    public List<RigDataModel> getSequences() {
        return sequences;
    }
}

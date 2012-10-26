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

/**
 * A simple data model to hold sequence and taxid
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class RigDataModel {

    private String sequence;
    private String taxid;

    public RigDataModel() {

    }

    public RigDataModel( String sequence, String taxid ) {
        this.sequence = sequence;
        this.taxid = taxid;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence( String sequence ) {
        this.sequence = sequence;
    }

    public String getTaxid() {
        return taxid;
    }

    public void setTaxid( String taxid ) {
        this.taxid = taxid;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof RigDataModel ) ) {
            return false;
        }

        RigDataModel model = (RigDataModel) o;

        if (sequence == null && model.getSequence() != null){
            return false;
        }
        else if (sequence != null && model.getSequence() == null){
            return false;
        }
        else if (sequence != null && model.getSequence() != null && !sequence.equals(model.getSequence())){
            return false;
        }
        else {
            if (taxid == null && model.getTaxid() != null){
                return false;
            }
            else if (taxid != null && model.getTaxid() == null){
                return false;
            }
            else if (taxid != null && model.getTaxid() != null && !taxid.equals(model.getTaxid())){
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int code = 29;
        code = 29 * code + sequence != null ? sequence.hashCode() : 0;
        code = 29 * code + taxid != null ? taxid.hashCode() : 0;

        return code;
    }
}

/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.confidence.model;

import java.util.Arrays;

/**
 * Class to store a binary interaction.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id$
 * @since 1.6.0
 *        <pre>
 *        11-Dec-2007
 *        </pre>
 */
public class BinaryInteraction implements Comparable {
    private Identifier firstId;
    private Identifier secondId;

    private Confidence confidence;

    public BinaryInteraction(Identifier firstIdentifier, Identifier secondIdentifier){
        this(firstIdentifier, secondIdentifier, Confidence.UNKNOWN);

    }

    public BinaryInteraction(Identifier firstIdentifier, Identifier secondIdentifier, Confidence confidence){
        if (firstIdentifier instanceof UniprotIdentifierImpl && secondIdentifier instanceof UniprotIdentifierImpl){
            firstId = firstIdentifier;
            secondId = secondIdentifier;
            this.confidence = confidence;
        }   else {
            throw new IllegalArgumentException( "Both identifiers must be uniprot identifiers!");
        }
    }

    public Identifier getFirstId() {
        return firstId;
    }

    public void setFirstId( Identifier firstId ) {
        this.firstId = firstId;
    }

    public Identifier getSecondId() {
        return secondId;
    }

    public void setSecondId( Identifier secondId ) {
        this.secondId = secondId;
    }

    public Confidence getConfidence(){
        return confidence;
    }

    public void setConfidence(Confidence confidence){
        this.confidence = confidence;
    }

    public String convertToString(){
        return firstId + ";" + secondId;
    }

    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof BinaryInteraction ) ) return false;

        BinaryInteraction that = ( BinaryInteraction ) o;

        boolean straight = true;
        if ( firstId != null ? !firstId.equals( that.firstId ) : that.firstId != null ) {
            straight = false;
        }
        if ( secondId != null ? !secondId.equals( that.secondId ) : that.secondId != null ) {
            straight = false;
        }

        boolean reverse = true;
        if ( firstId != null ? !firstId.equals( that.secondId ) : that.firstId != null ) {
            reverse = false;
        }
        if ( secondId != null ? !secondId.equals( that.firstId ) : that.secondId != null ) {
            reverse = false;
        }

        return straight || reverse;
    }

    public int hashCode() {
        int result;
        Identifier [] ids   = {firstId, secondId};
        Arrays.sort( ids);
        result = ( ids[0]!= null ? ids[0 ].hashCode() : 0 );
        result = 31 * result + ( ids[ 1 ]!= null ? ids[1 ].hashCode() : 0 );
        result = 31 * result + ( confidence != null ? confidence.hashCode() : 0 );

//        result = ( firstId != null ? firstId.hashCode() : 0 );
//        result = 31 * result + ( secondId != null ? secondId.hashCode() : 0 );
//        result = 31 * result + ( confidence != null ? confidence.hashCode() : 0 );
        return result;
    }

    public int compareTo( Object o ) {
        if (o instanceof BinaryInteraction){
            BinaryInteraction bi = (BinaryInteraction) o;
            if (this.equals( o )){
                return 0;
            } else if (this.getFirstId().equals( bi.getFirstId() )) {
                return this.getSecondId().getId().compareTo( bi.getSecondId().convertToString() );
            }  else {
                return this.getFirstId().getId().compareTo( bi.getFirstId().getId() );
            }
        }
       throw new IllegalArgumentException( "Bouth objects must be an instance of the same class! " + o.getClass() );
    }
}

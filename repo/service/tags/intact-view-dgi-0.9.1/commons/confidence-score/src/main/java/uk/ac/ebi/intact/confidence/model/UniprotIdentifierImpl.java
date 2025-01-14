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

import uk.ac.ebi.intact.bridges.blast.model.UniprotAc;

/**
 * decorator for UniprotAc in intact-blast modul
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id$
 * @since 1.6.0
 *        <pre>
 *        30-Nov-2007
 *        </pre>
 */
public class UniprotIdentifierImpl implements Identifier {
    private UniprotAc uniprotAc;

    public UniprotIdentifierImpl(String uniprotId){
        uniprotId = uniprotId.toUpperCase();
        this.uniprotAc = new UniprotAc( uniprotId);
    }
    public UniprotIdentifierImpl(UniprotAc uniprotAc){
        this.uniprotAc = uniprotAc;
    }

    public String getId() {
        return uniprotAc.getAcNr();
    }

    public static String getRegex() {
        return UniprotAc.getRegex();
    }

    public String convertToString() {
        return uniprotAc.toString();
    }

    @Override
	public String toString() {
		return uniprotAc.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UniprotIdentifierImpl) {
			UniprotIdentifierImpl ac = (UniprotIdentifierImpl) obj;
			return this.uniprotAc.equals(ac.uniprotAc);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.uniprotAc.hashCode();
	}

    public int compareTo( Object o ) {
        if (o instanceof UniprotIdentifierImpl) {
			UniprotIdentifierImpl ac = (UniprotIdentifierImpl) o;
            return this.getId().compareTo( ac.getId() );
		}
        throw new IllegalArgumentException( "Bouth objects must be an instance of the same class! " + o.getClass() );          
    }
}

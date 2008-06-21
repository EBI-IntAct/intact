/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.bridges.unisave;

import uk.ac.ebi.uniprot.unisave.*;

import java.util.List;

/**
 * UniSave client.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.3
 */
public class UnisaveService {

    private UnisavePortType unisavePortType;

    public UnisaveService() {
        unisavePortType = new Unisave( ).getUnisave(  );
    }

    List<EntryVersionInfo> getVersions( String identifier, boolean isSecondary ) {
        final VersionInfo versionInfo = unisavePortType.getVersionInfo( identifier, isSecondary );
        return versionInfo.getEntryVersionInfo();
    }

    FastaSequence getFastaSequence( EntryVersionInfo version ) {
        final Version fastaVersion = unisavePortType.getVersion( version.getEntryId(), true );
        final String entry = fastaVersion.getEntry();
        final String[] array = entry.split( "\n" );
        if(  array.length != 2 ) {
            throw new IllegalStateException( "expected to receive a fasta format: " + entry );
        }
        String header = array[0];
        if( header.startsWith( ">" )) {
            header = header.substring( 1 );
        } else {
            throw new IllegalStateException( "Bad header format: " + header );
        }
        String sequence = array[1];
        return new FastaSequence( header, sequence );
    }
}

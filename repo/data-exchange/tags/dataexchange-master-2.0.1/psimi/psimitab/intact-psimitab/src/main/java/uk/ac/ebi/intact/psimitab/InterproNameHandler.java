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
package uk.ac.ebi.intact.psimitab;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.psimitab.exception.NameNotFoundException;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The InterproNameHandler gets the Information from a entyFile using a Map.
 *
 * @author Nadin Neuhauser
 * @version $Id$
 * @since 2.0.0
 */
public class InterproNameHandler {

    private static final Log logger = LogFactory.getLog( InterproNameHandler.class );

    private static final Pattern INTERPRO_ENTRY_PATTERN = Pattern.compile( "IPR\\d{6}[ ].+" );

    private Map<String, String> interproMap;

    public InterproNameHandler( File entryFile ) throws NameNotFoundException {
        if ( interproMap == null || interproMap.isEmpty() ) {
            try {
                BufferedReader reader = new BufferedReader( new FileReader( entryFile ) );
                init( reader );
            } catch ( FileNotFoundException e ) {
                throw new NameNotFoundException( "File not found" );
            } catch ( IOException e ) {
                throw new NameNotFoundException( "Can not read " + entryFile.getAbsolutePath() );
            }
        }
    }

    public InterproNameHandler( InputStream stream ) throws NameNotFoundException {
        if ( interproMap == null || interproMap.isEmpty() ) {
            BufferedReader reader = new BufferedReader( new InputStreamReader( stream ) );
            try {
                init( reader );
            } catch ( IOException e ) {
                throw new NameNotFoundException( "Can not read " + stream.toString() );
            }
        }
    }

    public InterproNameHandler( URL url ) throws NameNotFoundException {
        try {
            InputStream stream = url.openStream();
            if ( interproMap == null || interproMap.isEmpty() ) {
                BufferedReader reader = new BufferedReader( new InputStreamReader( stream ) );
                try {
                    init( reader );
                } catch ( IOException e ) {
                    throw new NameNotFoundException( "Can not read " + stream.toString() );
                }
            }
        } catch ( IOException e ) {
            throw new NameNotFoundException( "Can not open stream for  " + url.getPath() );
        }
    }

    private void init( BufferedReader reader ) throws IOException {
        long start = System.currentTimeMillis();

        if ( logger.isDebugEnabled() ) logger.debug( "Starting to init InterproNameMap." );

        interproMap = new HashMap<String, String>();

        String line = null;
        while ( ( line = reader.readLine() ) != null ) {
            if ( INTERPRO_ENTRY_PATTERN.matcher( line ).matches() ) {
                int index = line.indexOf( " " );
                String interproTerm = line.substring( 0, index );
                String interproName = line.substring( index + 1, line.length() );

                interproMap.put( interproTerm, interproName );
            }
        }
        reader.close();

        if ( logger.isDebugEnabled() ) {
            logger.debug( "Number of Interpro entries " + interproMap.keySet().size() );
            logger.trace( "Time to init InterproNameMap " + ( System.currentTimeMillis() - start ) );
        }
    }

    public String getNameById( String interproTerm ) throws NameNotFoundException {
        long start = System.currentTimeMillis();
        if ( interproMap != null && !interproMap.isEmpty() ) {
            String result = interproMap.get( interproTerm );
            if ( result == null ) {
                logger.warn( "Could not find " + interproTerm );
            } else if ( logger.isTraceEnabled() ) {
                logger.trace( "Time to get InterproName " + interproTerm + " from map " + ( System.currentTimeMillis() - start ) );
            }
            return result;
        } else {
            logger.error( "Map is not initialized or is Empty." );
        }
        throw new NameNotFoundException( "Could not find " + interproTerm );
    }
}

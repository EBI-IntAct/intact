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
package uk.ac.ebi.intact.psimitab.converters.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import java.util.Iterator;
import java.util.Collection;

import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Iterator of ProteinPair.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.2
 */
public class ProteinPairIterator implements Iterator<ProteinPair> {
    
    private static final Log log = LogFactory.getLog( ProteinPairIterator.class );

    private Cache cache;
    private Iterator iterator;

    public ProteinPairIterator( Cache cache ) {
        if ( cache == null ) {
            throw new IllegalArgumentException( "You must give a non null cache" );
        }
        this.cache = cache;
        this.iterator = cache.getKeys().iterator();
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public ProteinPair next() {
        String key = ( String ) iterator.next();
        final Element element = cache.get( key );
        if( element == null ) {
            throw new NullPointerException( "Could not find an element in cache for key: '" + key + "'. " +
                                            "Could it be that some element expired from the cache ?" );
        }
        Collection<IntactBinaryInteraction> interactions = ( Collection<IntactBinaryInteraction> ) element.getValue();
        return new ProteinPair( key, interactions );
    }

    public void remove() {
        throw new UnsupportedOperationException( );
    }
}

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
package uk.ac.ebi.intact.bridges.wswublast.failure;

import java.util.HashMap;
import java.util.Map;

/**
 * WaitAndRetry implementation of the strategy.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 *        <pre>
 *                      26-Oct-2007
 *                      </pre>
 */
public class WaitAndRetry implements FailureStrategy {
    private static int nrTries = 0;
    private int maxTries;
    private Map<FailureMethods, Integer> map;

    public WaitAndRetry( int maxTries ) {
        this.maxTries = maxTries;
        this.map = new HashMap<FailureMethods, Integer>( 3 );
        map.put( FailureMethods.SUBMITJOB, 0 );
        map.put( FailureMethods.REFRESHJOB, 0 );
        map.put( FailureMethods.RUN_BLAST_ONLY_NR_A_TIME, 0 );
    }

    public void processFailure( String msg, Exception e ) {
        if ( msg.equalsIgnoreCase( "submitJob" ) ) {
            Integer i = map.get( FailureMethods.SUBMITJOB );
            if ( i < maxTries ) {
                try {
                    Thread.sleep( 5000 ); //5 sec
                } catch ( InterruptedException e1 ) {
                    e1.printStackTrace();
                }
            }
            i++;
            map.put(FailureMethods.SUBMITJOB, i );
        }
        if ( msg.equalsIgnoreCase( "refreshJob" ) ) {

        }
        //TODO: implement a proper failure handling
      //  e.printStackTrace();
    }

    public int getTries( FailureMethods key ) {
        if ( map.containsKey( key ) ) {
            return map.get( key );
        }
        return -1;
    }

    public void incTries( FailureMethods key, int nr ) {
        if ( map.containsKey( key ) ) {
            map.put( key, map.get(key) + nr );
        }
    }

    public void resetTries( FailureMethods key ) {
         if ( map.containsKey( key ) ) {
             map.put(key, 0);
         }
    }
}

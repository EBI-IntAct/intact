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
package uk.ac.ebi.intact.bridges.wswublast.mock;

import uk.ac.ebi.intact.bridges.wswublast.client.BlastClient;
import uk.ac.ebi.intact.bridges.wswublast.client.WuBlastClientException;
import uk.ac.ebi.intact.bridges.wswublast.model.BlastInput;
import uk.ac.ebi.intact.bridges.wswublast.model.BlastJobStatus;
import uk.ac.ebi.intact.bridges.wswublast.model.BlastOutput;
import uk.ac.ebi.intact.bridges.wswublast.model.Job;

import java.util.List;
import java.util.Set;

/**
 * TODO comment that class header
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 *        <pre>
 *                             25-Oct-2007
 *                             </pre>
 */
public class MockBlastClient extends BlastClient {
    private int nr = 0;

    /**
     * Constructor
     *
     * @param email : email for the web service client
     */
    public MockBlastClient( String email ) throws WuBlastClientException {
        super( email );
    }

    public BlastJobStatus checkStatus( Job job ) throws WuBlastClientException {
        if ( job.getBlastInput().getUniprotAc().getAcNr().equalsIgnoreCase( "P12345" ) ) {
            job.setStatus( BlastJobStatus.FINISHED );
            return job.getStatus();
        }
        if ( job.getBlastInput().getUniprotAc().getAcNr().equalsIgnoreCase( "P40348" ) ) {
            nr++;
            if ( nr == 2 ) {
                nr =0;
                job.setStatus( BlastJobStatus.RUNNING );
            } else {
                job.setStatus( BlastJobStatus.FAILURE );
            }
            return job.getStatus();
        }
        if ( job.getBlastInput().getUniprotAc().getAcNr().equalsIgnoreCase( "Q12345" ) ) {
            job.setStatus( BlastJobStatus.FINISHED );
            return job.getStatus();
        }
        if ( job.getBlastInput().getUniprotAc().getAcNr().equalsIgnoreCase( "S12345" ) ) {
            nr ++;
            if(nr ==2){
                job.setStatus(BlastJobStatus.NOT_FOUND);
                nr =0;
                return job.getStatus();
            }
            throw new WuBlastClientException( "S12345" );
        }
        return super.checkStatus( job );
    }

    public BlastOutput getResult( Job job ) throws WuBlastClientException {
        if ( job.getBlastInput().getUniprotAc().getAcNr().equalsIgnoreCase( "P40348" ) ) {
            throw new WuBlastClientException( "retry later" );
        }
        if ( job.getBlastInput().getUniprotAc().getAcNr().equalsIgnoreCase( "P12345" ) ) {
            job.setBlastResult( new BlastOutput( null, false ) );
            return job.getBlastResult();
        }
        if ( job.getBlastInput().getUniprotAc().getAcNr().equalsIgnoreCase( "Q12345" ) ) {
            job.setBlastResult( new BlastOutput( null, true ) );
            return job.getBlastResult();
        }
        if ( job.getBlastInput().getUniprotAc().getAcNr().equalsIgnoreCase( "S12345" ) ) {
            return null;
        }
        return null;
    }// ////////////////

    // public Methods
    public Job blast( BlastInput blastInput ) throws WuBlastClientException {
        if ( blastInput.getUniprotAc().getAcNr().equalsIgnoreCase( "P12345" ) ) {
            Job job = new Job( "testBlastId1", blastInput );
            checkStatus( job );
            return job;
        }
        if ( blastInput.getUniprotAc().getAcNr().equalsIgnoreCase( "P40348" ) ) {
            throw new WuBlastClientException( "P40348" );
        }
        if ( blastInput.getUniprotAc().getAcNr().equalsIgnoreCase( "Q12345" ) ) {
            Job job = new Job( "testBlastId2", blastInput );
            checkStatus( job );
            return job;
        }
        if ( blastInput.getUniprotAc().getAcNr().equalsIgnoreCase( "S12345" ) ) {
            Job job = new Job( "testBlastId3", blastInput );
            checkStatus( job );
            return job;
        }
        return null;
    }

    public List<Job> blast( Set<BlastInput> blastInputSet ) throws WuBlastClientException {
        return super.blast( blastInputSet );
    }
}

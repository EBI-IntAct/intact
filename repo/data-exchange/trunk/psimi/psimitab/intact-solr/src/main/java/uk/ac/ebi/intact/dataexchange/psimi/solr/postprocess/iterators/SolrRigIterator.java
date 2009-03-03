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
package uk.ac.ebi.intact.dataexchange.psimi.solr.postprocess.iterators;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import uk.ac.ebi.intact.dataexchange.psimi.solr.FieldNames;
import uk.ac.ebi.intact.dataexchange.psimi.solr.converter.SolrDocumentConverter;
import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;

import java.util.ArrayList;
import java.util.Collection;


/**
 * TODO comment that class header
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class SolrRigIterator extends SolrDocumentIterator {

    SolrDocument previousSolrDocument;

    boolean isCurrentSolrDocumentYetToBeAddedToList = false;
    private SolrDocumentConverter converter = new SolrDocumentConverter();

     public SolrRigIterator( SolrServer solrServer) {
        super( solrServer );
    }

    public SolrRigIterator( SolrServer solrServer, String query ) {
        super( solrServer, query );
    }

    public boolean hasNext() {
        return super.hasNext() || isCurrentSolrDocumentYetToBeAddedToList;
    }

    public Collection<SolrInputDocument> nextSolrDocumentGroup() throws SolrServerException {

        Collection<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();
        final Collection<IntactBinaryInteraction> interactionCollection = nextIntactBinaryInteractionGroup();
        for ( IntactBinaryInteraction intactBinaryInteraction : interactionCollection ) {
            final SolrInputDocument inputDocument = converter.toSolrDocument( intactBinaryInteraction );
            solrInputDocuments.add( inputDocument );
        }
      return solrInputDocuments;
    }

    public Collection<IntactBinaryInteraction> nextIntactBinaryInteractionGroup() {
        Collection<IntactBinaryInteraction> groupList = new ArrayList<IntactBinaryInteraction>();

        do {
            if ( hasNext() ) {
                if ( previousSolrDocument == null ) {
                    previousSolrDocument = getChunkSolrIterator().next();
                }

                IntactBinaryInteraction binaryInteraction = ( IntactBinaryInteraction ) converter.toBinaryInteraction( previousSolrDocument );
                groupList.add( binaryInteraction );
            }
        } while ( hasNextWithSameRig() );


        return groupList;
    }

    public boolean hasNextWithSameRig() {
        if (getChunkSolrIterator().hasNext() ) {
            SolrDocument currentSolrDocument = getChunkSolrIterator().next();
            String previousRig = ( String ) previousSolrDocument.getFieldValue( FieldNames.RIGID );
            String currentRig = ( String ) currentSolrDocument.getFieldValue( FieldNames.RIGID );
            previousSolrDocument = currentSolrDocument;

            if ( !getChunkSolrIterator().hasNext() ) {
                isCurrentSolrDocumentYetToBeAddedToList = true;
            }
            if ( previousRig != null && previousRig.equals( currentRig ) ) {
                return true;
            }
        }else{
            isCurrentSolrDocumentYetToBeAddedToList = false;
        }

        return false;
    }

}

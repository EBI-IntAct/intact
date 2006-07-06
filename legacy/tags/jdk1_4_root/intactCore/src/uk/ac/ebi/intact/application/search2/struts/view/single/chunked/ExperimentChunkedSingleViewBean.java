/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search2.struts.view.single.chunked;

import uk.ac.ebi.intact.application.search2.business.Constants;
import uk.ac.ebi.intact.application.search2.struts.view.html.HtmlBuilderManager;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Experiment;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * View bean responsible for the chunked display of a single experiment.
 * Only a sub part of its Interactions are displayed.
 *
 * @see uk.ac.ebi.intact.model.Experiment
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentChunkedSingleViewBean extends ChunkedSingleViewBean {

    /**
     * THe Integer parameter are needed to be able to access the constructor by reflexion.
     *
     * @param object
     * @param link
     * @param contextPath
     * @param maxChunk
     * @param selectedChunk
     */
    public ExperimentChunkedSingleViewBean( AnnotatedObject object, String link, String contextPath,
                                            Integer maxChunk, Integer selectedChunk ) {
        super( object, link, contextPath, maxChunk.intValue(), selectedChunk.intValue() );
    }

    /**
     * Graph buttons are shown.
     * @return whether or not the graph buttons are displayed
     */
    public boolean showGraphButtons() {
        return true;
    }

    public String getHelpSection() {
        return "experiment.single.chunked.view";
    }

    public void getHTML( Writer writer ) {

        // prepare the chunked Experiment
        Experiment experiment = (Experiment) getWrappedObject();
        Collection interactions = experiment.getInteractions();
        int originalInteractionCount = interactions.size();

        int chunkToDisplay = getSelectedChunk();
        if( chunkToDisplay == Constants.NO_CHUNK_SELECTED ){
            logger.info ( "No chunk selected, select the first one !" );
            chunkToDisplay = 1; // select the first one !
        }

        int start = ( chunkToDisplay - 1 ) * Constants.MAX_CHUNK_SIZE;
        int stop  = Math.min( start + Constants.MAX_CHUNK_SIZE,
                              originalInteractionCount );

        logger.info( "Chunk: start("+ start +"), stop("+ stop +")" );

        Collection interactionsSubList = null;
        if( List.class.isAssignableFrom( interactions.getClass() ) ) {
            interactionsSubList = ( (List) interactions ).subList( start, stop );
        } else {
            // copy the collection in a List
            interactionsSubList = new ArrayList( interactions );
            interactionsSubList = ( (List) interactionsSubList ).subList( start, stop );
        }

        Experiment shallowExperiment = Experiment.getShallowCopy( experiment );
        shallowExperiment.setInteractions( interactionsSubList );

        setWrappedObject( shallowExperiment );

        // Display the CHunk index header

        try {
            HtmlBuilderManager.getInstance().getChunkIndexHtml( writer,
                                                                getWrappedObject(),
                                                                null,
                                                                getHelpLink(),
                                                                getContextPath(),
                                                                getSelectedChunk(),
                                                                getMaxChunk(),
                                                                originalInteractionCount,
                                                                "interactions");
        } catch ( IOException e ) {
            try {
                writer.write( "Could not produce the chunk index" );
            } catch ( IOException e1 ) {
                e1.printStackTrace ();
            }
            e.printStackTrace ();
        }

        // display the page content
        super.getHTML ( writer );
    }
}
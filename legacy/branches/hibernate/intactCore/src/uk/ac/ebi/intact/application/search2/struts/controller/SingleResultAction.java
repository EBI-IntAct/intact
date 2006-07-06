/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search2.struts.controller;

import uk.ac.ebi.intact.application.search2.business.Constants;
import uk.ac.ebi.intact.application.search2.business.IntactUserIF;
import uk.ac.ebi.intact.application.search2.struts.view.AbstractViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.ViewBeanFactory;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Experiment;

import java.util.Collection;

/**
 * Performs the single view for Proteins.
 *
 * @author IntAct Team
 * @version $Id$
 */
public class SingleResultAction extends AbstractResulltAction {

    ///////////////////////////////////
    // Abstract methods implementation
    ///////////////////////////////////

    protected AbstractViewBean getAbstractViewBean ( Collection results, IntactUserIF user, String contextPath ) {

        logger.info( "single action: building view beans..." );
        AnnotatedObject firstItem = (AnnotatedObject) results.iterator().next();

        if( firstItem.getClass().equals( Experiment.class ) ){
            // if the object is an Experiment, check the count of interactions
            Experiment experiment = (Experiment) firstItem;
            int size = experiment.getInteractions().size(); // As this is proxied, it won't hang forever.
            if ( size > Constants.MAX_CHUNK_SIZE ) {
                // this experiment can't be displayed as such,
                // we forward to a specialised action.
                logger.info( "Experiment's interactions count: " + size + ". Chunk its display." );

                // calculate the maximum number of chunks
                int max = size / Constants.MAX_CHUNK_SIZE;
                if ( size % Constants.MAX_CHUNK_SIZE > 0 ) {
                    max++;
                }

                // the the one the user want to see displayed
                int selected = user.getSelectedChunk();
                if (selected == -1) {
                    selected = 1;
                    user.setSelectedChunk( 1 );
                } else if ( selected > max ) {
                    // just in case the user fiddle around with the URL ...
                    selected = max;
                    user.setSelectedChunk( max );
                }

                return ViewBeanFactory.getInstance().getChunkedSingleViewBean( firstItem, user.getHelpLink(),
                                                                               contextPath,
                                                                               max, selected  );
            }
        }

        return ViewBeanFactory.getInstance().getSingleViewBean ( firstItem, user.getHelpLink(), contextPath );
    }
}
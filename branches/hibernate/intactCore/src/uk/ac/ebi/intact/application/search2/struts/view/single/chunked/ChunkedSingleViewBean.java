/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search2.struts.view.single.chunked;

import uk.ac.ebi.intact.application.search2.struts.view.single.SingleViewBean;
import uk.ac.ebi.intact.model.AnnotatedObject;

/**
 * Class allowing to handle the chunked view for a single AnnotatedObject.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ChunkedSingleViewBean extends SingleViewBean {

    /**
     * Max count of chunk we can display out of the wrapped object
     */
    public int maxChunk;

    /**
     * The chunk we will wisplay
     */
    public int selectedChunk;


    /**
     * Constructs an instance of this class from given AnnotatedObject.
     *
     * @param object an AnnotatedObject to display.
     * @param link the link to the help page.
     * @param contextPath the application path
     * @param maxChunk Max count of chunk we can display out of the wrapped object
     * @param selectedChunk The chunk we will wisplay
     */
    public ChunkedSingleViewBean ( AnnotatedObject object, String link, String contextPath, int maxChunk, int selectedChunk ) {
        super ( object, link, contextPath );
        this.maxChunk = maxChunk;
        this.selectedChunk = selectedChunk;
    }


    public int getMaxChunk () {
        return maxChunk;
    }

    public int getSelectedChunk () {
        return selectedChunk;
    }
}
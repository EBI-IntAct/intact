/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search2.struts.view.single;

import uk.ac.ebi.intact.model.AnnotatedObject;

/**
 * View bean responsible for the display of a single Interaction.
 *
 * @see uk.ac.ebi.intact.model.Interaction
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionSingleViewBean extends SingleViewBean {

    /**
     * Construct an instance of this class for given object.
     *
     * @param object the <code>Protein</code> to construct the view for.
     * @param link the link to the help page.
     * @exception NullPointerException thrown if the object is null.
     */
    public InteractionSingleViewBean( AnnotatedObject object, String link, String contextPath ) {
        super( object, link, contextPath );
    }

    /**
     * Graph buttons are shown.
     * @return whether or not the graph buttons are displayed
     */
    public boolean showGraphButtons() {
        return true;
    }

    public String getHelpSection() {
        return "interaction.single.view";
    }

}

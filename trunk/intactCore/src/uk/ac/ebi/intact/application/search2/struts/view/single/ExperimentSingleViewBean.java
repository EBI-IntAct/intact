/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search2.struts.view.single;

import uk.ac.ebi.intact.application.search2.struts.view.AbstractViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.ViewBeanFactory;
import uk.ac.ebi.intact.model.AnnotatedObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * View bean responsible for the display of a single protein.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentSingleViewBean extends SingleViewBean {

    /**
     * Construct an instance of this class for given object.
     *
     * @param object the <code>Protein</code> to construct the view for.
     * @param link the link to the help page.
     * @exception NullPointerException thrown if the object is null.
     */
    public ExperimentSingleViewBean( AnnotatedObject object, String link ) {
        super( object, link );
    }

    /**
     * Graph buttons are shown.
     * @return whether or not the graph buttons are displayed
     */
    public boolean showGraphButtons() {
        return true;
    }

    public String getHTML() {

        Collection collection = new ArrayList(1);
        collection.add( getWrappedObject() );
        AbstractViewBean bean = ViewBeanFactory.getInstance().getViewBean(
                collection, getHelpLink() );
        bean.initHighlightMap();
        return bean.getHTML();
    }
}

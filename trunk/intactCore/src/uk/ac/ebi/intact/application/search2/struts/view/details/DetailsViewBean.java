/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search2.struts.view.details;

import uk.ac.ebi.intact.application.search2.struts.view.AbstractViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.html.HtmlBuilderManager;
import uk.ac.ebi.intact.model.AnnotatedObject;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Abstract class allowing to handle the view for a collection of AnnotatedObject.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class DetailsViewBean
        extends AbstractViewBean
        implements Serializable {

    //---- Attributes needed by all subclasses -----

    /**
     * Stores the object being wrapped. Mainly used for
     * alternative display options by other views. This should be overriden
     * by subclasses wishing to use alternative objects (eg BasicObject).
     */
    private Collection wrappedObjects;

    /**
     * Constructs an instance of this class from given collection.
     * @param objects a collection of objects to display.
     * @param link the link to the help page.
     *
     * <pre>
     * pre: forall(obj:objects -> obj instanceof AnnotatedObject)
     * </pre>
     */
    public DetailsViewBean(Collection objects, String link) {
        super( link );
        if( objects == null || objects.isEmpty() )
            throw new NullPointerException( "cannot create view bean without Collection of AnnotatedObject !" );
        this.wrappedObjects = objects;
    }

    //------------ methhods useful to all subclasses --------------------------

    /**
     * Typically subclasses will return a more 'interesting' instance than Object
     * @return Object the instance warpped by the bean
     */
    public Collection getWrappedObjects() {
        return this.wrappedObjects;
    }

    protected void setWrappedObjects(Collection wrappedObjects) {
        this.wrappedObjects = wrappedObjects;
    }

    // Implementing abstract methods

    public void initHighlightMap() {
        Set set = new HashSet( this.wrappedObjects.size() );
        for ( Iterator iterator = this.wrappedObjects.iterator(); iterator.hasNext(); ) {
            AnnotatedObject annobj  = (AnnotatedObject) iterator.next();
            set.add( annobj.getShortLabel() );
        }
        setHighlightMap(set);
    }

    public String getHTML() {
        String result = null;

        try {
            result = HtmlBuilderManager.getInstance().getHtml(getWrappedObjects(),
                    getHighlightMap(), getHelpLink());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            result = "Could not produce a view for a Protein";
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            result = "Could not produce a view for a Protein";
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            result = "Could not produce a view for a Protein";
        }
        return result;
    }

    /**
     * Graph buttons are shown.
     * @return whether or not the graph buttons are displayed
     */
    public boolean showGraphButtons() {
        return true;
    }
}

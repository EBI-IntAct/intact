/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search2.struts.view.single;

import uk.ac.ebi.intact.application.search2.struts.view.AbstractViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.html.HtmlBuilderManager;
import uk.ac.ebi.intact.model.AnnotatedObject;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;


/**
 * Class allowing to handle the view for a single AnnotatedObject.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class SingleViewBean extends AbstractViewBean
        implements Serializable {

    //---- Attributes needed by all subclasses -----

    /**
     * Stores the object being wrapped. Mainly used for
     * alternative display options by other views. This should be overriden
     * by subclasses wishing to use alternative objects (eg BasicObject).
     */
    private AnnotatedObject wrappedObject;

    /**
     * Constructs an instance of this class from given AnnotatedObject.
     * @param object an AnnotatedObject to display.
     * @param link the link to the help page.
     */
    public SingleViewBean( AnnotatedObject object, String link ) {
        super( link );
        if( object == null )
            throw new NullPointerException( "cannot create view bean without an AnnotatedObject !" );
        this.wrappedObject = object;
    }

    //------------ methhods useful to all subclasses --------------------------

    /**
     * Typically subclasses will return a more 'interesting' instance than Object
     * @return Object the instance warpped by the bean
     */
    public AnnotatedObject getWrappedObject() {
        return this.wrappedObject;
    }

    public String getHTML() {

        String result = null;

        try {
            result = HtmlBuilderManager.getInstance().getHtml(getWrappedObject(),
                    getHighlightMap(), getHelpLink());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            result = "Could not produce a view";
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            result = "Could not produce a view";
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            result = "Could not produce a view";
        }

        return result;
    }

    // Implements abstract method

    public void initHighlightMap() {
        Set set  = new HashSet( 1 );
        set.add( this.wrappedObject.getShortLabel() );
        setHighlightMap(set);
    }
}

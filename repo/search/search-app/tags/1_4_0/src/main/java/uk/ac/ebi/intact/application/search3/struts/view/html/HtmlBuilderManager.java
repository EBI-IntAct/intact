/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search3.struts.view.html;

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.search3.business.Constants;
import uk.ac.ebi.intact.model.AnnotatedObject;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>
 * This class gives the access to the HtmlBuilder methods by calling the appropriate one
 * according to the objects (one or a collection) given.
 * </p>
 *
 * <p>
 * If you give:
 *    <blockquote>
 *    - a single object (e.g. Experiment),
 *      the method HtmlBuilder (Experiment) will be called
 *    </blockquote>
 *    <blockquote>
 *    - a collection of object (e.g. Interaction)
 *      the method HtmlBuilder (Interaction) will be called iteratively on each item
 *      of the given collection.
 *    </blockquote>
 * </p>
 *
 * <p>
 * This is a singleton class.
 * </p>
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class HtmlBuilderManager {

    protected transient static final Logger logger = Logger.getLogger( Constants.LOGGER_NAME );

    private static HtmlBuilderManager ourInstance;

    ///////////////////////////
    // Instanciation methods
    ///////////////////////////

    // Made it private to stop from instantiating this class.
    private HtmlBuilderManager() {
    }

    /**
     * Returns the only instance of this class.
     * @return the only instance of this class; always non null value is returned.
     */
    public synchronized static HtmlBuilderManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new HtmlBuilderManager();
        }
        return ourInstance;
    }


    /**
     * Write the HTML code related the the collection of object given in the the writer.
     * It highlights all string given in the highlights set and integrates <code>link</code>
     * as an help link.
     *
     * @param writer where to write the produced HTML code
     * @param objects the collection of object to convert in HTML
     * @param highlights which String to highlight in the HTML content
     * @param link where is the help page.
     * @throws NoSuchMethodException if the method called by reflexion is not yet implemented
     * @throws InvocationTargetException If an exception occured in the method called by reflexion.
     * @throws IllegalAccessException
     */
    public void getHtml( Writer writer, Collection objects, Set highlights, String link, String contextPath)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        HtmlBuilder builder = new HtmlBuilder(writer, highlights, link, contextPath);
        for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
                AnnotatedObject  obj = (AnnotatedObject) iterator.next();
                this.buildHtml( builder, obj );
        }
    }


    /**
     * Write the HTML code related the the single object given in the the writer.
     * It highlights all string given in the highlights set and integrates <code>link</code>
     * as an help link.
     *
     * @param writer where to write the produced HTML code
     * @param object the single object to convert in HTML
     * @param highlights which String to highlight in the HTML content
     * @param link where is the help page.
     * @throws NoSuchMethodException if the method called by reflexion is not yet implemented
     * @throws InvocationTargetException If an exception occured in the method called by reflexion.
     * @throws IllegalAccessException
     */
    public void getHtml( Writer writer, Object object, Set highlights, String link, String contextPath )
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        HtmlBuilder builder = new HtmlBuilder(writer, highlights, link, contextPath);
        this.buildHtml(builder, object );
    }


    private void buildHtml( HtmlBuilder builder, Object object )
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        if (object.getClass().isAssignableFrom( AnnotatedObject.class ))
            logger.info ( ((AnnotatedObject) object).getShortLabel() + "  " +
                                 object.getClass().getName() );

        try {
            Class[] paras = new Class[]{ object.getClass() };
            Method m = HtmlBuilder.class.getMethod( "htmlView", paras );
            m.invoke(builder, new Object[]{ object });
        } catch ( InvocationTargetException e ) {
            e.getTargetException().printStackTrace();
            throw e;
        }
    }

}
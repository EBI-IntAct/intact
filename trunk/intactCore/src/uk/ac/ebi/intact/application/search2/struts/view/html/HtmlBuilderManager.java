/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
/**
 * This class is ...
 * TODO it
 * <p>
 * This is a singleton class.
 * </p>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
package uk.ac.ebi.intact.application.search2.struts.view.html;

import uk.ac.ebi.intact.application.search2.struts.view.details.BinaryDetailsViewBean;
import uk.ac.ebi.intact.model.AnnotatedObject;

import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


public class HtmlBuilderManager {

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


    public void getHtml(Writer writer, AnnotatedObject object, Set highlights, String link)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        HtmlBuilder builder = new HtmlBuilder(writer, highlights, link);
        this.buildHtml(builder, object, highlights);
    }


    public void getHtml(Writer writer, Collection objects, Set highlights, String link)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        HtmlBuilder builder = new HtmlBuilder(writer, highlights, link);
        for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
                AnnotatedObject  obj = (AnnotatedObject) iterator.next();
                this.buildHtml(builder, obj, highlights);
        }
    }

    public void getHtml(Writer writer,
                        BinaryDetailsViewBean.BinaryData object,
                        Set highlights,
                        String link)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        HtmlBuilder builder = new HtmlBuilder(writer, highlights, link);
        this.buildHtml(builder, object, highlights);

    }


    public void buildHtml(HtmlBuilder builder, Object object, Set highlights)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        // TODO use a proper logger
        if (object.getClass().isAssignableFrom( AnnotatedObject.class ))
            System.out.println ( ((AnnotatedObject) object).getShortLabel() + "  " +
                                 object.getClass().getName() );

        try {
            Class[] paras = new Class[]{object.getClass()};
            Method m = HtmlBuilder.class.getMethod("htmlView", paras);
            m.invoke(builder, new Object[]{object});
        } catch ( InvocationTargetException e ) {
            e.getTargetException().printStackTrace();
            throw e;
        }
    }
}
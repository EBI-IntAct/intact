/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search2.struts.view.details;

import uk.ac.ebi.intact.application.search2.struts.view.AbstractViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.html.HtmlBuilderManager;
import uk.ac.ebi.intact.model.AnnotatedObject;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Class allowing to handle the view for a collection of AnnotatedObject.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class DetailsViewBean extends AbstractViewBean
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
    public DetailsViewBean(Collection objects, String link, String contextPath) {
        super( link, contextPath );
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

    /**
     * Build a copy the Experiment object - don't copy the interactions.
     * @param experiment the Experiment to copy
     */
//    protected static Experiment createShallowExperiment ( Experiment experiment) {
//
//
//
//        Experiment ex = new Experiment ( experiment.getOwner(),
//                                         experiment.getShortLabel(),
//                                         experiment.getBioSource() );
//        ex.setAc(experiment.getAc());
//        ex.setAnnotation( experiment.getAnnotations() );
//        ex.setCurator( experiment.getCurator() );
//        ex.setCvInteraction( experiment.getCvInteraction() );
//        ex.setCvIdentification( experiment.getCvIdentification() );
//        ex.setEvidences( experiment.getEvidences() );
//        ex.setFullName( experiment.getFullName() );
//        ex.setReferences( experiment.getReferences() );
//        ex.setRelatedExperiment( experiment.getRelatedExperiment() );
//        ex.setXrefs( experiment.getXrefs() );
//        return ex;
//    }

    public void getHTML( Writer writer ) {

        try {
            HtmlBuilderManager.getInstance().getHtml(writer,
                                                     getWrappedObjects(),
                                                     getHighlightMap(),
                                                     getHelpLink(),
                                                     getContextPath());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            try {
                writer.write( "Could not produce a view for a Protein" );
            } catch ( IOException e1 ) {
                e1.printStackTrace ();
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            try {
                writer.write( "Could not produce a view for a Protein" );
            } catch ( IOException e1 ) {
                e1.printStackTrace ();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            try {
                writer.write( "Could not produce a view for a Protein" );
            } catch ( IOException e1 ) {
                e1.printStackTrace ();
            }
        }
    }

    /**
     * Graph buttons are shown.
     * @return whether or not the graph buttons are displayed
     */
    public boolean showGraphButtons() {
        return true;
    }

    // Just honouring the contract.
    public String getHelpSection() {
        return "interaction.details.view";
    }
}

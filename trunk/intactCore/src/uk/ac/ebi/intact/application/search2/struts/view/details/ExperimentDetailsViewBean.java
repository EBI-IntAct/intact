/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search2.struts.view.details;

import uk.ac.ebi.intact.application.search2.business.Constants;
import uk.ac.ebi.intact.application.search2.struts.view.html.HtmlBuilderManager;
import uk.ac.ebi.intact.model.Experiment;

import java.io.Writer;
import java.io.IOException;
import java.util.*;
import java.lang.reflect.InvocationTargetException;

/**
 * View bean for displaying a collection of Experiments. Since some may in fact
 * require a tabbed display if too large, different processing is needed compared
 * to detail views of other types.
 *
 * @see Experiment
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class ExperimentDetailsViewBean extends DetailsViewBean {

    /**
     * HashMap containing details needed to display HTML headers for any
     * detail views that need to be displayed using a tabbed format. Content
     * is dictated by what the HTML builder needs, but currently the format
     * of an entry needs to be:
     * <p>
     * AC -> TabMapEntry, where tabMapEntry has the following data:
     * {<tab number>, <max number of tabs>, <original item count>}
     * </p>
     * Currently this is only used for Experiment views when some may be too large
     * to display.
     */
    private HashMap tabbedViewInfo = new HashMap();



    /**
     * Main constructor.
     * @param objects The Collection of Experiments to be displayed in tabbed form.
     * @param link The independent help page link information.
     * @param contextPath The platform-dependent path needed to access web pages
     * @throws IllegalArgumentException if the first object in the Collection is not
     * an Experiment.
     */
    public ExperimentDetailsViewBean( Collection objects, String link,
                                      String contextPath) {
        //handles null or empty collection here..
        super( objects, link, contextPath);
        if(!objects.iterator().next().getClass().equals(Experiment.class))
            throw new IllegalArgumentException("Can't build Experiment viewbean - wrong type! "
                    + objects.iterator().next().getClass().getName());
    }

    /**
     * Graph buttons are shown.
     * @return whether or not the graph buttons are displayed
     */
    public boolean showGraphButtons() {
        return true;
    }

    public String getHelpSection() {
        //NB this may need updating to point to a different help section..
        return "experiment.single.chunked.view";
    }

    public void getHTML( Writer writer ) {

        //here we need to handle a detail Experiment view which may be 'mixed'
        //for both tabbed and ordinary Experiments.

        Collection experimentViews = generateViewData(); //holds the shallow views built
        try {

            //Need to go through each shallow Experiment view and then
            //for each one: if it needs a tab header get the HTML builder to do
            //it based on any tab info in the Map, then generate the 'usual'
            //HTML for the Experiment....
            for(Iterator it = experimentViews.iterator(); it.hasNext();) {

                Experiment exp = (Experiment)it.next();
                if(tabbedViewInfo.keySet().contains(exp.getAc())) {
                    //needs a tabbed view - generate the header for it
                    TabMapEntry entry = (TabMapEntry)tabbedViewInfo.get(exp.getAc());
                    HtmlBuilderManager.getInstance().getChunkIndexHtml( writer,
                                                                exp,
                                                                null,
                                                                getHelpLink(),
                                                                getContextPath(),
                                                                entry.tabNumber,
                                                                entry.maxTabNumber,
                                                                entry.fullSizeCount,
                                                                "interactions");
                }

                //now do usual HTML building for the SINGLE Experiment...
                HtmlBuilderManager.getInstance().getHtml(writer,
                                                         exp,
                                                         getHighlightMap(),
                                                         getHelpLink(),
                                                         getContextPath());
            }
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
            try {
                writer.write( "Could not produce an Experiment details view" );
            }
            catch ( IOException e1 ) {
                e1.printStackTrace ();
            }
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
            try {
                writer.write( "Could not produce an Experiment details view" );
            }
            catch ( IOException e1 ) {
                e1.printStackTrace ();
            }
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            try {
                writer.write( "Could not produce an Experiment details view" );
            }
            catch ( IOException e1 ) {
                e1.printStackTrace ();
            }
        }
        catch ( IOException e ) {
            //This will only be thrown from generation of the
            //tabbed header
            try {
                writer.write( "Could not generate the tabbed header list!" );
            }
            catch ( IOException e1 ) {
                e1.printStackTrace ();
            }
            e.printStackTrace ();
        }

    }

    //--------------------- private helper methods -----------------------------

    /**
     * This method takes all of the Experiments and processes each in turn. A
     * shallow view is built and the contents depends upon whether the Experiment
     * requires a tabbed view or not. If so then the apprropriate details are
     * added to the tab information Map, as a side effect.
     * @return Collection the list of generated shallow views for display.
     */
    private Collection generateViewData() {

        Collection experimentViews = new ArrayList(); //holds the shallow views built

        for (Iterator it = getWrappedObjects().iterator(); it.hasNext();) {
            Experiment exp = (Experiment) it.next();

            //need to use a shallow view of the Experiment to avoid big interaction graphs
            Experiment shallowExperiment = Experiment.getShallowCopy(exp);

            //first check the size to see if a tab view is needed
            int size = exp.getInteractions().size();
            if (size > Constants.MAX_CHUNK_SIZE) {
                //experiment needs a tabbed view
                logger.info("Experiment's interactions count: " + size + ". Chunk its display.");
                //int selected = Constants.NO_CHUNK_SELECTED; //initialize

                // calculate the maximum number of tabs that can be displayed
                //for this Experiment
                int max = size / Constants.MAX_CHUNK_SIZE;
                if (size % Constants.MAX_CHUNK_SIZE > 0) {
                    max++;
                }

                //now build the Experiment copy for display (start at first item)...
                //int start = (selected - 1) * Constants.MAX_CHUNK_SIZE;
                int start = 1;
                int stop = Math.min(start + Constants.MAX_CHUNK_SIZE,
                        size);

                logger.info("ExpDetails, tabbed view: start(" + start + "), stop(" + stop + ")");

                //1) get the required sublist for viewing and put into the
                //shallow Experiment view
                Collection interactionsSubList = getSubList(exp.getInteractions(), start, stop);
                shallowExperiment.setInteractions(interactionsSubList);

                //2) add the tabbing view info for this Experiment
                //to the tab map...
                logger.info("ExpDetails, tabbed view: adding tab info to tab map..");
                TabMapEntry entry = new TabMapEntry();
                entry.tabNumber = start;
                entry.fullSizeCount = size;
                entry.maxTabNumber = max;
                //tidy old view details just in case, and add the new..
                if(tabbedViewInfo.containsKey(exp.getAc())) tabbedViewInfo.remove(exp.getAc());
                tabbedViewInfo.put(exp.getAc(), entry);

                //now add the shallow view to the list being returned
                experimentViews.add(shallowExperiment);
            }
            else {
                //not a large experiment - just add it to the return list as it is
                experimentViews.add(exp);
            }

        }
        return experimentViews;

    }
    /**
     * Convenience method to get at a sublist of interactions. This is
     * required for tabbing displays. We need this because OJB does not
     * gives us what we want as a List, but rather as a Collection.
     * @param interactions The interactions we want a sublist of
     * @param start The starting position of the sublist
     * @param stop The last position of the sublist, inclusive
     * @return List the interactions in a list
     */
    private List getSubList(Collection interactions, int start, int stop) {

        List interactionList = null;
        if(!List.class.isAssignableFrom(interactions.getClass())) {
            // copy the collection in a List
            interactionList = new ArrayList( interactions );
        }
        else interactionList = (List)interactions;

        return interactionList.subList(start, stop);
    }

    //----------------------- inner classes ---------------------------------

    /**
     * Simple class for holding the details of a user tab request. Fields
     * are public for simplicity as this is a private inner class.
     */
    private class TabMapEntry {

        public int tabNumber;
        public int maxTabNumber;
        public int fullSizeCount;

    }
}
/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search2.struts.view.details;

import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.application.search2.struts.view.html.HtmlBuilderManager;

import java.io.Serializable;
import java.io.Writer;
import java.io.IOException;
import java.util.*;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class BinaryDetailsViewBean extends DetailsViewBean {

    /////////////////////////
    // Inner class
    /////////////////////////

    public class BinaryData implements Serializable {

        private HashMap data;

        public BinaryData (){
            this.data = new HashMap();
        }

        public HashMap getData () {
            return data;
        }
    }

    /**
     * Construct an instance of this class for given collection of object.
     *
     * @param objects the collection of objects to construct the view for.
     * @param link the link to the help page.
     * @exception NullPointerException thrown thrown if the collection if null or empty.
     */
    public BinaryDetailsViewBean( Collection objects, String link ) {
        super(objects, link);
    }


    private void addInteractor (Interactor query, BinaryData binaryData){

        HashMap results = binaryData.getData();

        if (null != query.getActiveInstance()) {
            Iterator i = query.getActiveInstance().iterator();

            // Check if the current query is already in the result set
            if (null != results.get(query)) {
                // This query has already been added to the result set before,
                // igonore.
                return;
            }

            // Create the hash map which holds interaction partners.
            HashMap currentResults = new HashMap();
            results.put(query, currentResults);


            // The current query could have more than one Component
            // linking it to the same Interaction, e.g. for Homodimers.
            // Therefore we need to keep track of Interactions already processed.
            HashSet processedInteractions = new HashSet();

            while (i.hasNext()){
                Component c1 = (Component) i.next();
                Interaction interaction = c1.getInteraction();
                if (! processedInteractions.contains(interaction)) {

                    // Keep list of processed Interactions
                    processedInteractions.add(interaction);

                    // Insert all Interactors of the current interaction
                    Iterator j = interaction.getComponent().iterator();
                    while (j.hasNext()) {
                        Component c2 = (Component) j.next();
                        Interactor result = c2.getInteractor();

                        /* The query should not be added as a result.
                        However, if e.g. the query interacts with itself,
                        it should be listed. Therefore, compare the components.
                        If getStoichiometry >= 2, we have a homodimer,
                        which should also be listed as a self-interaction.
                        */
                        if ((c1 != c2)
                                ||
                            (c2.getStoichiometry() >= 2.0)) {
                            // Now insert the current interaction appropriately
                            HashSet interactionsOfQueryAndResult =
                                    (HashSet) currentResults.get(result);
                            if (null == interactionsOfQueryAndResult) {
                                interactionsOfQueryAndResult = new HashSet();

                                // add a new, empty HashSet
                                currentResults.put(result, interactionsOfQueryAndResult);
                            }

                            interactionsOfQueryAndResult.add(interaction);
                        } // if
                    } // while
                } // if
            } // while
        } // if
    } // addInteractor


    public String buildHtml( HashMap results ){
        StringBuffer buf = new StringBuffer();

        // The resultset might be empty.
        if (null == results) {
            return "";
        }

        Iterator i = results.keySet().iterator();

        while (i.hasNext()){
            Interactor query = (Interactor) i.next();
            HashMap currentResults = (HashMap) results.get(query);

            Iterator j = currentResults.keySet().iterator();
            while(j.hasNext()){
                Interactor partner = (Interactor) j.next();
                buf.append("Query: " + query.getShortLabel()
                           +
                           " Interactor: " + partner.getShortLabel()
                           +
                           " Interactions: ");

                HashSet interactionsOfQueryAndResult = (HashSet) currentResults.get(partner);
                Iterator k = interactionsOfQueryAndResult.iterator();
                while (k.hasNext()){
                    Interaction act = (Interaction) k.next();
                    buf.append(act.getShortLabel() + ", ");
                }
                buf.append("<br>");
            }
        }

        return buf.toString();
    }

    public String buildTextVersion( HashMap results ){
        StringBuffer buf = new StringBuffer();

        // The resultset might be empty.
        if (null == results) {
            return "";
        }

        Iterator i = results.keySet().iterator();
        while (i.hasNext()){
            Interactor query = (Interactor) i.next();
            HashMap currentResults = (HashMap) results.get(query);

            Iterator j = currentResults.keySet().iterator();
            while(j.hasNext()){
                Interactor partner = (Interactor) j.next();
                buf.append("Query: " + query.getShortLabel()
                           +
                           " Interactor: " + partner.getShortLabel()
                           +
                           " Interactions: ");

                HashSet interactionsOfQueryAndResult = (HashSet) currentResults.get(partner);
                Iterator k = interactionsOfQueryAndResult.iterator();
                while (k.hasNext()){
                    Interaction act = (Interaction) k.next();
                    buf.append(act.getShortLabel() + ", ");
                }
                buf.append("<br>");
            }
        }

        return buf.toString();
    }



    /**
     * Building the Protein view in the comtext of the wrapped Proteins' Experiment.
     *
     * @param writer the writer to use to write the HTML data out.
     */
    public void getHTML( Writer writer ) {

        BinaryData binaryData = new BinaryData();

        for ( Iterator iterator = getWrappedObjects ().iterator(); iterator.hasNext (); ) {
            Protein protein = (Protein) iterator.next ();
            addInteractor( protein, binaryData );
        }

        setHighlightMap( new HashSet(1) );

        try {
            HtmlBuilderManager.getInstance().getHtml( writer, binaryData,
                                                      getHighlightMap(), getHelpLink());
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
}

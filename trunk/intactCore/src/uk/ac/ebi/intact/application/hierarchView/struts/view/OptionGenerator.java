/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.view;

import uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.struts.StrutsConstants;

import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;


/**
 * Allows to create some collection to populate option list in HTML form.
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */

public class OptionGenerator {

    private static Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

    /**
     * create a collection of LabelValueBean object from a properties file
     *
     * @return a collection of LabelValueBean object
     */
    public static ArrayList getHighlightmentSources () {

        ArrayList sources = new ArrayList ();

        // read the Highlighting.proterties file
        Properties properties = PropertyLoader.load (StrutsConstants.PROPERTY_FILE_HIGHLIGHTING);

        if (null != properties) {

            String sourceList = properties.getProperty ("highlightment.source.allowed");

            if ((null == sourceList) || (sourceList.length() < 1)) {
                logger.warn ("Unable to find the property: highlightment.source.allowed (" +
                             StrutsConstants.PROPERTY_FILE_HIGHLIGHTING +")");
                return null;
            }

            // parse source list
            String token = properties.getProperty ("highlightment.source.token");

            if ((null == token) || (token.length() < 1)) {
                logger.warn ("Unable to find the property: highlightment.source.token (" +
                             StrutsConstants.PROPERTY_FILE_HIGHLIGHTING +")");
                return null;
            }

            StringTokenizer st = new StringTokenizer (sourceList, token);

            while (st.hasMoreTokens()) {
                String sourceKey = st.nextToken();
                String propName = "highlightment.source." + sourceKey + ".label";
                String label = properties.getProperty (propName);

                if ((null == label) || (label.length() < 1)) {
                logger.warn ("Unable to find the property: "+ propName +" ("+
                             StrutsConstants.PROPERTY_FILE_HIGHLIGHTING +")");
                    continue;
                }

                sources.add (new LabelValueBean(label, sourceKey, ""));
            } // while
        } else {
            logger.warn("Unable to load the properties file: " + StrutsConstants.PROPERTY_FILE_HIGHLIGHTING);
        }

        return sources;

    } // getHighlightmentMethods


    /**
     * Create a collection of LabelValueBean object specific of an highlightment method
     * from a properties file
     *
     * @param anHighlightmentMethod
     * @return a collection of LabelValueBean object specific of an highlightment method
     */
    public static ArrayList getAuthorizedBehaviour (String anHighlightmentMethod) {

        ArrayList behaviours = new ArrayList ();

        // read the Highlighting.proterties file
        Properties properties = PropertyLoader.load (StrutsConstants.PROPERTY_FILE_HIGHLIGHTING);

        if (null != properties) {

            String behaviourList = properties.getProperty ("highlightment.behaviour." + anHighlightmentMethod + ".allowed");

            if ((null == behaviourList) || (behaviourList.length() < 1)) {
                logger.info ("No behaviour defined for the source called:" + anHighlightmentMethod);

                // As there are no specified allowed list of behaviour for this method,
                // we try to load the global definition of defined behaviour.
                behaviourList = properties.getProperty ("highlightment.behaviour.existing");

                if ((null == behaviourList) || (behaviourList.length() < 1)) {
                    logger.warn ("Unable to find the property: highlightment.behaviour.existing ("+
                                  StrutsConstants.PROPERTY_FILE_HIGHLIGHTING +")");
                    return null;
                }
            }

            // parse behaviour list
            String token = properties.getProperty ("highlightment.behaviour.token");

            if ((null == token) || (token.length() < 1)) {
                logger.warn ("Unable to find the property: highlightment.behaviour.token ("+
                             StrutsConstants.PROPERTY_FILE_HIGHLIGHTING +")");
                return null;
            }

            StringTokenizer st = new StringTokenizer (behaviourList, token);

            while (st.hasMoreTokens()) {
                String sourceKey = st.nextToken();
                String labelProp = "highlightment.behaviour." + sourceKey + ".label";
                String classProp = "highlightment.behaviour." + sourceKey + ".class";
                String label = properties.getProperty ( labelProp );
                String value = properties.getProperty ( classProp );

                if ((null == label) || (label.length() < 1) || (null == value) || (value.length() < 1)) {
                    logger.warn ("Unable to find either properties:  ("+ labelProp + ", " + classProp + " ("+
                                 StrutsConstants.PROPERTY_FILE_HIGHLIGHTING +")");
                    continue; // don't add this element
                }

                behaviours.add (new LabelValueBean(label, value, ""));
            } // while

        } // if

        return behaviours;

    } // getAuthorizedBehaviour



} // OptionGenerator

/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.business;

import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.ebi.intact.application.editor.exception.EmptyTopicsException;
import org.apache.commons.collections.CollectionUtils;

/**
 * This class provides the general editor services common to all the editors.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditorService {

    /**
     * The editor resource bundle.
     */
    private ResourceBundle myResources;

    /**
     * Intact topic Types.
     */
    private ResourceBundle myTopics;

    /**
     * The topics already sorted in an alphebetical order;
     * cached to save recompuation.
     */
    private List myTopicsCache = new ArrayList();

    /**
     * Reference to Newt proxy server URL.
     */
    private URL myNewtServerUrl;

    /**
     * Construts with the resource file.
     * @param name the name of the resource file.
     * @exception MissingResourceException thrown when the resource file is
     * not found.
     * @exception EmptyTopicsException thrown for an empty resource file.
     * @exception MalformedURLException thrown for incorrect Newt URL property.
     */
    public EditorService(String name) throws MissingResourceException,
            EmptyTopicsException, MalformedURLException {
        myResources = ResourceBundle.getBundle(name);
        myTopics = ResourceBundle.getBundle(myResources.getString("topics"));
        // Must have Intact Types to edit.
        if (!myTopics.getKeys().hasMoreElements()) {
            throw new EmptyTopicsException(
                    "Editor topic resource file can't be empty");
        }
        myNewtServerUrl = new URL(myResources.getString("newt.server.url"));

        // Cache the topics after sorting them.
        CollectionUtils.addAll(myTopicsCache, myTopics.getKeys());
        Collections.sort(myTopicsCache);
    }

    /**
     * Returns the class name associated with the given topic.
     * @param topic the topic to search in the Intact types resource.
     * @return the classname saved under <code>topic</code>.
     */
    public String getClassName(String topic) {
        return myTopics.getString(topic);
    }

    /**
     * Returns a collection of Intact types.
     * @return an <code>ArrayList</code> of Intact types. The list sorted on an
     * alphabetical order.
     */
    public Collection getIntactTypes() {
        return myTopicsCache;
    }

    /**
     * Returns reference to the Newt server proxy.
     * @return URL to the new Newt server proxy; null for an invalid URL.
     */
    public URL getNewtServerUrl() {
        return myNewtServerUrl;
    }

    /**
     * Returns the link to the search application.
     * @return the link to the search application as a String object.
     */
    public String getSerachLink() {
        return myResources.getString("search.url");
    }
}

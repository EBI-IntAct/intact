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
import uk.ac.ebi.intact.util.NewtServerProxy;
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
     * Reference to Newt proxy server instance.
     */
    private NewtServerProxy myNewtServer;

    /**
     * Construts with the resource file.
     * @param name the name of the resource file.
     * @exception MissingResourceException thrown when the resource file is
     * not found.
     * @exception EmptyTopicsException thrown for an empty resource file.
     */
    public EditorService(String name) throws MissingResourceException,
            EmptyTopicsException {
        myResources = ResourceBundle.getBundle(name);
        myTopics = ResourceBundle.getBundle(myResources.getString("topics"));
        // Must have Intact Types to edit.
        if (!myTopics.getKeys().hasMoreElements()) {
            throw new EmptyTopicsException(
                    "Editor topic resource file can't be empty");
        }
    }

    /**
     * Loads editor topic properties from a resources file.
     * @param name the name of the Intact types resource file.
     * @exception MissingResourceException thrown when the resource file is
     * not found.
     * @exception EmptyTopicsException thrown for an empty resource file.
     */
    public void loadTopicProperties(String name)
            throws MissingResourceException, EmptyTopicsException {
        myTopics = ResourceBundle.getBundle(name);
        // We must have Intact Types to search for; in other words, resource
        // bundle can't be empty.
        if (!myTopics.getKeys().hasMoreElements()) {
            throw new EmptyTopicsException(
                    "Editor topic resource file can't be empty");
        }
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
     * @return an <code>ArrayList</code> of Intact types. The list sorted in
     * alphabetical order.
     */
    public Collection getIntactTypes() {
        // The collection to return.
        List types = new ArrayList();
        CollectionUtils.addAll(types, myTopics.getKeys());
        Collections.sort(types);
        return types;
    }

    /**
     * Returns reference to the Newt server proxy.
     * @exception MalformedURLException thrown for incorrect URL property.
     */
    public NewtServerProxy getNewtServer() throws MalformedURLException {
        if (myNewtServer == null) {
            URL url = new URL(myResources.getString("newt.server.url"));
            myNewtServer = new NewtServerProxy(url);
        }
        return myNewtServer;
    }
}

/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.business;

import org.apache.commons.collections.CollectionUtils;
import uk.ac.ebi.intact.application.editor.exception.EmptyTopicsException;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * This class provides the general editor services common to all the users.
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
     * The search server URL.
     */
    private String mySearchUrl;

    /**
     * The help server URL.
     */
    private String myHelpUrl;

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
        // Remove Experiment and Interaction and move them to the top of the list.
        // Order is important: interaction first and then followed by Experiment as
        // we want the Experiment to be at the top.
        moveToFront("Interaction");
        moveToFront("Experiment");
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
     * alphabetical order. Since this reference refers to this class's
     * internal cache, handle this reference with care (do not modify contents).
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
     * Returns the relative link to the search application.
     * @param request the request object to get the context path.
     * This is only used once when this method is called for the first time.
     * For subsequent calls, the cached value is returned.
     * @return the relative path to the search page.
     */
    public String getSearchURL(HttpServletRequest request) {
        if (mySearchUrl == null) {
            String ctxtPath = request.getContextPath();
            String relativePath = ctxtPath.substring(0, ctxtPath.lastIndexOf("editor"));
            mySearchUrl = relativePath.concat(myResources.getString("search.url"));
        }
        return mySearchUrl;
    }

    /**
     * Returns the relative link to the help page.
     * @param request the request object to get the context path.
     * This is only used once when this method is called for the first time.
     * For subsequent calls, the cached value is returned.
     * @return the relative path to the help page.
     */
    public String getHelpURL(HttpServletRequest request) {
        if (myHelpUrl == null) {
            String ctxtPath = request.getContextPath();
            String relativePath = ctxtPath.substring(0, ctxtPath.lastIndexOf("editor"));
            myHelpUrl = relativePath.concat(myResources.getString("help.url"));
        }
        return myHelpUrl;
    }

    /**
     * Moves the given item to the front of the topics list.
     * @param item the item to move; this is only moved if it exists.
     */
    private void moveToFront(String item) {
        int pos = myTopicsCache.indexOf(item);
        if (pos != -1) {
            myTopicsCache.remove(pos);
            myTopicsCache.add(0, item);
        }
    }
}

/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.business;

import org.apache.commons.collections.CollectionUtils;
import uk.ac.ebi.intact.application.editor.exception.EmptyTopicsException;
import uk.ac.ebi.intact.application.commons.struts.taglibs.DocumentationTag;

import javax.servlet.ServletRequest;
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
     * Reference to the help server URL.
     */
    private URL myHelpUrl;

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
     * Returns the link to the search application.
     * @return the link to the search application as a String object.
     */
    public String getSearchLink() {
        return myResources.getString("search.url");
    }

    /**
     * Returns the URL to the help page.
     * @param request the request object to get the server name and host.
     * This is only used once when this method is called for the first time.
     * For subsequent calls, the cached  URL value is returned.
     * @return the URL to the help page or null is returned if a valid URL
     * cannot be constructed from <code>request</code>.
     */
    public String getHelpURL(ServletRequest request) {
        if (myHelpUrl == null) {
            try {
                myHelpUrl = new URL("http", request.getServerName(),
                        request.getServerPort(), DocumentationTag.getJspPath());
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return myHelpUrl.toExternalForm();
    }

    private void moveToFront(String item) {
        int pos = myTopicsCache.indexOf(item);
        if (pos != -1) {
            myTopicsCache.remove(pos);
            myTopicsCache.add(0, item);
        }
    }
}

/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.business;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.editor.exception.EmptyTopicsException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Experiment;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * This class provides the general editor services common to all the users.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditorService {

    // Class Data

    /**
     * Only instance of this class.
     */
    private static EditorService ourInstance;

    // Instance Data

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
     * The search server URL.
     */
    private String mySearchUrl;

    /**
     * The help server URL.
     */
    private String myHelpUrl;

    // Class Methods

    /**
     * Returns the only instance of this class using the default resources file.
     * However, if this instance has already been initialized by
     * {@link #getInstance(String)}, it will be returned instead (i.e, the default
     * resource is ignored).
     * @return the only instance of this class or null for any errors.
     * @see #getInstance(String)
     */
    public static EditorService getInstance() {
        try {
            return getInstance("uk.ac.ebi.intact.application.editor.EditorResources");
        }
        catch (EmptyTopicsException ete) {
            Logger.getLogger(EditorConstants.LOGGER).info(ete);
        }
        return null;
    }

    /**
     * Returns the only instance of this class using given resources file. This
     * method not synchronized because it is only invoked by EditorActionServlet at
     * the init stage.
     * @param name the resource file name.
     * @return the only instance of this class.
     * @throws EmptyTopicsException thrown for an empty resource file.
     */
    public static EditorService getInstance(String name) throws EmptyTopicsException {
        if (ourInstance == null) {
            ourInstance = new EditorService(name);
        }
        return ourInstance;
    }

    // Constructor private to return the only instance via getInstance method.

    /**
     * Construts with the resource file.
     * @param name the name of the resource file.
     * @exception MissingResourceException thrown when the resource file is
     * not found.
     * @exception EmptyTopicsException thrown for an empty resource file.
     */
    private EditorService(String name) throws MissingResourceException,
            EmptyTopicsException {
        myResources = ResourceBundle.getBundle(name);
        myTopics = ResourceBundle.getBundle(myResources.getString("topics"));
        // Must have Intact Types to edit.
        if (!myTopics.getKeys().hasMoreElements()) {
            throw new EmptyTopicsException(
                    "Editor topic resource file can't be empty");
        }
        // Cache the topics after sorting them.
        CollectionUtils.addAll(myTopicsCache, myTopics.getKeys());
        Collections.sort(myTopicsCache);

        // Remove Experiment and Interaction and move them to the top of the list.
        // Order is important: interaction first and then followed by Experiment as
        // we want the Experiment to be at the top.
        moveToFront(getTopic(Interaction.class));
        moveToFront(getTopic(Experiment.class));
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
     * Returns the topic name for given class.
     * @param clazz the Class object to extract the tipic name.
     * @return the class name without the package prefix. The returned
     * value equals to given class's class name if there is no package
     * information associated with <code>clazz</code>
     */
    public String getTopic(Class clazz) {
        String className = clazz.getName();
        int lastIdx = className.lastIndexOf('.');
        if (lastIdx != -1) {
            return className.substring(lastIdx + 1);
        }
        return className;
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
     * Retrieves the resource for given key from the editor resource file.
     * @param key the key to search for the resource.
     * @return the resource for <code>key</code> if it is found.
     */
    public String getResource(String key) {
        return myResources.getString(key);
    }

    /**
     * Retrieves the resource for given key from the editor resource file as an int.
     * @param key the key to search for the resource. This must be a key to an integer
     * property.
     * @return the resource for <code>key</code> if it is found as an integer.
     */
    public int getInteger(String key) {
        return Integer.parseInt(myResources.getString(key));
    }

    /**
     * A convenient method to return the interaction limit for JSPs. This method
     * is equivalent to calling {@link #getInteger(String)} with exp.interaction.limit
     * as the key.
     * @return the maximum number of interactions allowed to display in the experiment
     * editor.
     *
     * @see #getInteger(String)
     */
    public int getInteractionLimit() {
        return getInteger("exp.interaction.limit");
    }

    /**
     * A convenient method to return the interaction per page limit for JSPs.
     * This method is equivalent to calling {@link #getResource(String)} with
     * exp.interaction.page.limit as the key.
     * @return the maximum number of interactions allowed (per page) to display
     * in the experiment editor.
     *
     * @see #getResource(String)
     */
    public String getInteractionPageLimit() {
        return getResource("exp.interaction.page.limit");
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

/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework.util;

import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The menu factory to generate various menus.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditorMenuFactory {

    // Static data

    /**
     * The list item to indicate a selection is required. This item is always
     * displayed as the first item in the list.
     */
    public static final String SELECT_LIST_ITEM = "--- Select ---";

    /**
     * The names (edit/add) for the topic list.
     */
    public static final String TOPICS = "TopicNames";

    /**
     * The name for the database list.
     */
    public static final String DATABASES = "DatabaseNames";

    /**
     * The name for the qualifier list.
     */
    public static final String QUALIFIERS = "QualifierNames";

    /**
     *  The name for the host oragnism list.
     */
    public static final String ORGANISMS = "Organisms";

    /**
     * The name for the interaction list.
     */
    public static final String INTERACTIONS = "Interactions";

    /**
     * The name for the identification list.
     */
    public static final String IDENTIFICATIONS = "Identifications";

    /**
     * The name for the interaction type list.
     */
    public static final String INTERACTION_TYPES = "InteractionTypes";

    /**
     * The name for the experiment list.
     */
    public static final String EXPERIMENTS = "Experiments";

    /**
     * The name for the role list.
     */
    public static final String ROLES = "Roles";

    /**
     * Maps: List Name -> List type. Common to all the users and it is immutable.
     */
    private static final Map theirNameToType = new HashMap();

    /**
     * The prefix for Normalized Dag menu items.
     */
    private static final String DAG_PREFIX = "_";

    /**
     * The pattern to normalize dag menu items (dag menus).
     */
    private static final Pattern DAGMENU_PATTERN = Pattern.compile("\\.+");

    // Instance Data

    /**
     * Reference to the Intact Helper to build menus.
     */
    private IntactHelper myHelper;

    /**
     * Maps: Menu name -> edit menu list.
     */
    private Map myNameToEditItems = new HashMap();

    /**
     * Maps: Menu name -> add menu list.
     */
    private Map myNameToAddItems = new HashMap();

    // Static initializer.

    // Fill the maps with list names and their associated classes.
    static {
        theirNameToType.put(TOPICS, CvTopic.class);
        theirNameToType.put(DATABASES, CvDatabase.class);
        theirNameToType.put(QUALIFIERS, CvXrefQualifier.class);
        theirNameToType.put(ORGANISMS, BioSource.class);
        theirNameToType.put(INTERACTIONS, CvInteraction.class);
        theirNameToType.put(DAG_PREFIX + INTERACTIONS, CvInteraction.class);
        theirNameToType.put(IDENTIFICATIONS, CvIdentification.class);
        theirNameToType.put(DAG_PREFIX + IDENTIFICATIONS, CvIdentification.class);
        theirNameToType.put(INTERACTION_TYPES, CvInteractionType.class);
        theirNameToType.put(EXPERIMENTS, Experiment.class);
        theirNameToType.put(ROLES, CvComponentRole.class);
    }

    /**
     * Construst with an Intact helper.
     * @param helper the Intact Helper to build menus.
     */
    public EditorMenuFactory(IntactHelper helper) {
        myHelper = helper;
    }

    /**
     * Returns a menu for given name. This is retrieved from cache if it is
     * in the cache or else new menu is created and stored in the cache before
     * returning it.
     * @param name the name of the menu; the valid values are: {@link #TOPICS},
     * {@link #DATABASES}, {@link #QUALIFIERS}, {@link #ORGANISMS},
     * {@link #INTERACTIONS}, {@link #IDENTIFICATIONS}, {@link #INTERACTION_TYPES},
     * {@link #EXPERIMENTS} and {@link #ROLES}.
     * @param mode 0 for and edit menu and 1 for an add menu; the difference is
     * {@link #SELECT_LIST_ITEM} is added as the first entry for an add menu.
     * @return the menu for given <code>name</code>; <code><label/code> is
     * removed if it exists.
     * @throws SearchException for errors in contructing the menu.
     */
    public List getMenu(String name, int mode) throws SearchException {
        List list;
        if (mode == 0) {
            if (!myNameToEditItems.containsKey(name)) {
                list = makeList(name, mode);
                // Cache it for later use.
                myNameToEditItems.put(name, list);
            }
            else {
                // Retrieve it from cache.
                list = (List) myNameToEditItems.get(name);
            }
            return list;
        }
        if (!myNameToAddItems.containsKey(name)) {
            list = makeList(name, mode);
            // Cache it for later use.
            myNameToAddItems.put(name, list);
        }
        else {
            // Retrieve it from cache.
            list = (List) myNameToAddItems.get(name);
        }
        return list;
    }

    /**
     * Returns a menu for given name without leading '.' characters. This is
     * retrieved from cache if it is in the cache or else new menu is created
     * and stored in the cache before returning it. This is done by first
     * creating a nornmal menu for given name and then removing leading '.'
     * chacaters. So, two new menus are created when this method is called
     * for the first time.
     * @param name the name of the menu; the valid values are: {@link #TOPICS},
     * {@link #DATABASES}, {@link #QUALIFIERS}, {@link #ORGANISMS},
     * {@link #INTERACTIONS}, {@link #IDENTIFICATIONS}, {@link #INTERACTION_TYPES},
     * {@link #EXPERIMENTS} and {@link #ROLES}. Although all the above names are
     * valid, you should only use this method for Dag menu items (i.e,
     * {@link #INTERACTIONS} and {@link #IDENTIFICATIONS}).
     * @param mode 0 for and edit menu and 1 for an add menu; the difference is
     * {@link #SELECT_LIST_ITEM} is added as the first entry for an add menu.
     * @return the menu for given <code>name</code>; <code><label/code> is
     * removed if it exists.
     * @throws SearchException for errors in contructing the menu.
     */
    public List getDagMenu(String name, int mode) throws SearchException {
        return getMenu(DAG_PREFIX + name, mode);
    }

    /**
     * Returns the menus for an interaction.
     * @param mode 1 for add or 0 for edit; see the documentation for
     * {@link #getMenu(String, int)}
     * @return a map name -> menu; the valid names (keys) are: {@link #ORGANISMS},
     * {@link #INTERACTION_TYPES} and {@link #EXPERIMENTS}.
     * @throws SearchException for errors in constructing the menu.
     */
    public Map getInteractionMenus(int mode) throws SearchException {
        // The map to put menus.
        Map map = new HashMap();
        map.put(ORGANISMS, getMenu(ORGANISMS, mode));
        map.put(INTERACTION_TYPES, getMenu(INTERACTION_TYPES, mode));
        map.put(EXPERIMENTS, getMenu(EXPERIMENTS, mode));
        return map;
    }

    /**
     * True if <code>clazz</code> is a menu type
     * @param clazz the <code>Class</code> to compare.
     * @return true if <code>clazz</code> is one of designated menu type.
     */
    public boolean isMenuType(Class clazz) {
        return theirNameToType.containsValue(clazz);
    }

    /**
     * Removes given menu item from the internal cache;
     * @param clazz the <code>Class</code> of the menu to remove
     * from the internal cache.
     */
    public void removeMenu(Class clazz) {
        if (!theirNameToType.containsValue(clazz)) {
            // Doesn't contain;
            return;
        }
        for (Iterator iter = theirNameToType.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            if (entry.getValue().equals(clazz)) {
                String key = (String) entry.getKey();
                // Remove from both caches.
                myNameToEditItems.remove(key);
                myNameToAddItems.remove(key);
                // It is possible for a class to associate with more than obe
                // key as with normalized dag menus. So, ca't break from here.
            }
        }
    }

    // Helper methods

    /**
     * This method creates a list for given class.
     *
     * @param key the key to look in the map.
     * @param mode 0 for edit menus or 1 for add menus.
     * @return list made of short labels for given class type. A special
     * list with <code>theirEmptyListItem</code> is returned if there
     * are no items found for <code>clazz</code>.
     */
    private List makeList(String key, int mode) throws SearchException {
        // The collection to return.
        List list = new ArrayList();

        Vector v = null;
        Class clazz = (Class) theirNameToType.get(key);
        try {
            // Use a different construction for a CvDagObject.
            if (clazz.getSuperclass().equals(CvDagObject.class)) {
                v = CvDagObject.getMenuList(clazz, myHelper, true);
            }
            else {
                v = AnnotatedObject.getMenuList(clazz, myHelper, true);
            }
        }
        catch (IntactException ie) {
            throw new SearchException("Failed to get menu list for "
                    + clazz.getName());
        }
        // Guard against the null pointer.
        if ((v == null) || v.isEmpty()) {
            // Special list when we don't have any names.
            list.add(SELECT_LIST_ITEM);
            return list;
        }
        // -- select list -- for add menus.
        if (mode == 1) {
            list.add(SELECT_LIST_ITEM);
        }
        for (Iterator iter = v.iterator(); iter.hasNext();) {
            list.add(iter.next());
        }
        // Special case for dag menu items.
        if (key.startsWith(DAG_PREFIX)) {
            return normalize(list);
        }
        return list;
    }

    /**
     * Normalizes a dag menu by remving all the prefix '.' characeters from
     * the menu items.
     * @param list the menu list to normalize
     * @return the normalized list identical to the source list ecept for '.'
     * removed from all the list items.
     */
    private List normalize(List list) {
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            String listItem = iter.next().toString();
            Matcher match = DAGMENU_PATTERN.matcher(listItem);
            if (match.find()) {
                newList.add(match.replaceAll(""));
            }
            else {
                // No need to do any replacements.
                newList.add(listItem);
            }
        }
        return newList;
    }
}
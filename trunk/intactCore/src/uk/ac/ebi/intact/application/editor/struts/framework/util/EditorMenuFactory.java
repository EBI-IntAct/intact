/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework.util;

import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.util.MenuManager;
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
    public static final String TOPIC = "Topic";

    /**
     * The name for the database list.
     */
    public static final String DATABASE = "Database";

    /**
     * The name for the qualifier list.
     */
    public static final String QUALIFIER = "Qualifier";

    /**
     *  The name for the host oragnism list.
     */
    public static final String ORGANISM = "Organism";

    /**
     * The name for the interaction list.
     */
    public static final String INTERACTION = "Interaction";

    /**
     * The name for the identification list.
     */
    public static final String IDENTIFICATION = "Identification";

    /**
     * The name for the interaction type list.
     */
    public static final String INTERACTION_TYPE = "InteractionType";

    /**
     * The name for the experiment list.
     */
    public static final String EXPERIMENT = "Experiment";

    /**
     * The name for the role list.
     */
    public static final String ROLE = "Role";

    /**
     * The name for the CV cell list.
     */
    public static final String CELL = "Cell";

    /**
     * The name for the Tissue list.
     */
    public static final String TISSUE = "Tissue";

    /**
     * The name for the CvFeatureType list.
     */
    public static final String FEATURE_TYPE = "FeatureType";

    /**
     * The name for the CvFeatureIdentification list.
     */
    public static final String FEATURE_IDENTIFICATION = "FeatureIdentification";

    /**
     * Maps: Menu Name -> Menu type. Common to all the users and it is immutable.
     */
    private static final Map theirNameToType = new HashMap();

    /**
     * Maps: Menu Name -> default value.
     */
    private static final Map theirNameToDefValue = new HashMap();

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
     * Reference to the Menu manager.
     */
    private MenuManager myMenuManager;

    // Static initializer.

    static {
        // Fill the maps with list names and their associated classes.
        theirNameToType.put(TOPIC, CvTopic.class);
        theirNameToType.put(DATABASE, CvDatabase.class);
        theirNameToType.put(QUALIFIER, CvXrefQualifier.class);
        theirNameToType.put(ORGANISM, BioSource.class);
        theirNameToType.put(INTERACTION, CvInteraction.class);
        theirNameToType.put(DAG_PREFIX + INTERACTION, CvInteraction.class);
        theirNameToType.put(IDENTIFICATION, CvIdentification.class);
        theirNameToType.put(DAG_PREFIX + IDENTIFICATION, CvIdentification.class);
        theirNameToType.put(INTERACTION_TYPE, CvInteractionType.class);
        theirNameToType.put(DAG_PREFIX + INTERACTION_TYPE, CvInteractionType.class);
        theirNameToType.put(EXPERIMENT, Experiment.class);
        theirNameToType.put(ROLE, CvComponentRole.class);
        theirNameToType.put(CELL, CvCellType.class);
        theirNameToType.put(TISSUE, CvTissue.class);

        // These two are dag menus but not adding prefix because dag menus are now
        // treated like other menus.
        theirNameToType.put(FEATURE_TYPE, CvFeatureType.class);
        theirNameToType.put(FEATURE_IDENTIFICATION, CvFeatureIdentification.class);
    }

    /**
     * A utility method to normalizes a menu item
     * @param item the item to normalize.
     * @return if give <code>item</code> equals to {@link #SELECT_LIST_ITEM} null
     * is returned. For all other times, given <code>item</code> is returned.
     */
    public static String normalizeMenuItem(String item) {
        return SELECT_LIST_ITEM.equals(item) ? null : item;
    }

    /**
     * Construst with an Intact helper.
     * @param helper the Intact Helper to build menus.
     */
    public EditorMenuFactory(IntactHelper helper) {
        myMenuManager = MenuManager.getInstance();
        myHelper = helper;
    }

    /**
     * Returns a menu for given name. This is retrieved from cache if it is
     * in the cache or else new menu is created and stored in the cache before
     * returning it.
     * @param key the name of the menu; the valid values are: {@link #TOPIC},
     * {@link #DATABASE}, {@link #QUALIFIER}, {@link #ORGANISM},
     * {@link #INTERACTION}, {@link #IDENTIFICATION}, {@link #INTERACTION_TYPE},
     * {@link #EXPERIMENT} and {@link #ROLE}.
     * @param mode 0 for and edit menu and 1 for an add menu; the difference is
     * {@link #SELECT_LIST_ITEM} is added as the first entry for an add menu.
     * @return the menu for given <code>name</code>; <code><label/code> is
     * removed if it exists.
     * @throws SearchException for errors in contructing the menu.
     */
    public List getMenu(String key, int mode) throws SearchException {
        // The collection to return.
        List menu = null;

        Class clazz = (Class) theirNameToType.get(key);
        try {
            menu = myMenuManager.getMenuList(clazz, myHelper);
        }
        catch (IntactException ie) {
            throw new SearchException("Failed to get menu list for "
                    + clazz.getName());
        }
        if (menu.isEmpty()) {
            // Special list when we don't have any menu items.
            menu.add(SELECT_LIST_ITEM);
            return menu;
        }
        if (mode == 1) {
            // The default value for add menu.
            String  defvalue = SELECT_LIST_ITEM;
            // -- select list -- for add menus only if there is no default value.
            if (hasDefaultValue(key)) {
                // eg., Qualifier menu has a default value.
                defvalue = getDefaultValue(key);
                // Remove the default value to avoid adding it twice.
                menu.remove(defvalue);
            }
            menu.add(defvalue);
        }
        // Special case for dag menu items.
        if (key.startsWith(DAG_PREFIX)) {
            menu = normalize(menu);
        }
        String[] items = (String[]) menu.toArray(new String[0]);
        Arrays.sort(items);
        return Arrays.asList(items);
    }

    /**
     * Returns a menu for given name without leading '.' characters. This is
     * retrieved from cache if it is in the cache or else new menu is created
     * and stored in the cache before returning it. This is done by first
     * creating a nornmal menu for given name and then removing leading '.'
     * chacaters. So, two new menus are created when this method is called
     * for the first time.
     * @param name the name of the menu; the valid values are: {@link #TOPIC},
     * {@link #DATABASE}, {@link #QUALIFIER}, {@link #ORGANISM},
     * {@link #INTERACTION}, {@link #IDENTIFICATION}, {@link #INTERACTION_TYPE},
     * {@link #EXPERIMENT} and {@link #ROLE}. Although all the above names are
     * valid, you should only use this method for Dag menu items (i.e,
     * {@link #INTERACTION} and {@link #IDENTIFICATION}).
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
     * Returns the menus for the Interaction editor.
     * @param mode 1 for add or 0 for edit; see the documentation for
     * {@link #getMenu(String, int)}
     * @return a map name -> menu; the valid names (keys) are: {@link #ORGANISM},
     * {@link #INTERACTION_TYPE} and {@link #EXPERIMENT}.
     * @throws SearchException for errors in constructing the menu.
     */
    public Map getInteractionMenus(int mode) throws SearchException {
        // The map to put menus.
        Map map = new HashMap();
        map.put(ORGANISM, getMenu(ORGANISM, mode));
        map.put(INTERACTION_TYPE, getMenu(INTERACTION_TYPE, mode));
        map.put(EXPERIMENT, getMenu(EXPERIMENT, mode));
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

    // Helper methods

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

    /**
     * True if there is a default value associated with <code>name</code>.
     * @param name the menu name.
     * @return true if <code>name</code> has a default value; false is returned
     * otherwise.
     */
    private boolean hasDefaultValue(String name) {
        return theirNameToDefValue.containsKey(name);
    }

    /**
     * Returns the default value for <code>name</code>
     * @param name the menu name.
     * @return the default value for <code>name</code>; null if there is no
     * default value for <code>name</code>.
     */
    private String getDefaultValue(String name) {
        return (String) theirNameToDefValue.get(name);
    }
}
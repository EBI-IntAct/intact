/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework.util;

import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import uk.ac.ebi.intact.application.editor.util.IntactHelperUtil;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.ObjectBridgeQueryFactory;

import java.util.*;

/**
 * The menu factory to generate various menus.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditorMenuFactory {

    // Those variables are used when a called of the function getMenus(String EditorPageName) from the Class
    // AbstractEditViexBean is done in order to tell for which page this method is called to remove the relevant
    // cvTopic from the annotation section
    public static final String FEATURE_PAGE = "featurePage";

    public static final String SEQUENCE= "Sequence";

    public static final String CV_PAGE = "Cv";

    public static final String BIOSOURCE_PAGE = "BioSource";
    // Static data

    /**
     * The list item to indicate a selection is required. This item is always
     * displayed as the first item in the list.
     */
    public static final String SELECT_LIST_ITEM = "--- Select ---";

    /**
     * The names for the topic list.
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
     * The name for the Protein list (used for exemple by the function getMenu(String type) to filter the CvTopic menu
     */
     public static final String PROTEIN= "Protein";

    /**
     * The only instance of this class.
     */
    private static final EditorMenuFactory ourInstance = new EditorMenuFactory();

    /**
     * Maps: Menu Name -> Menu type. Common to all the users and it is immutable.
     */
    private static final Map ourNameToType = new HashMap();

    /**
     * The criteria to retrieve nucleic acid types
     */
    private static final Criteria ourNucleicAcidCriteria = new Criteria();

    /**
     * The criteria to retrieve protein types
     */
    private static final Criteria ourProteinCriteria = new Criteria();

    // Static initializer.

    static {
        // Fill the maps with list names and their associated classes.
        ourNameToType.put(TOPIC, CvTopic.class);
        ourNameToType.put(DATABASE, CvDatabase.class);
        ourNameToType.put(QUALIFIER, CvXrefQualifier.class);
        ourNameToType.put(ORGANISM, BioSource.class);
        ourNameToType.put(INTERACTION, CvInteraction.class);
        ourNameToType.put(IDENTIFICATION, CvIdentification.class);
        ourNameToType.put(INTERACTION_TYPE, CvInteractionType.class);
        ourNameToType.put(EXPERIMENT, Experiment.class);
        ourNameToType.put(ROLE, CvComponentRole.class);
        ourNameToType.put(CELL, CvCellType.class);
        ourNameToType.put(TISSUE, CvTissue.class);
        ourNameToType.put(FEATURE_TYPE, CvFeatureType.class);
        ourNameToType.put(FEATURE_IDENTIFICATION, CvFeatureIdentification.class);

        // Fill the criterias
        for (Iterator iter = CvInteractorType.getNucleicAcidMIs().iterator();iter.hasNext();) {
            ourNucleicAcidCriteria.addOrCriteria(buildMICriteria((String) iter.next()));
        }
        for (Iterator iter = CvInteractorType.getProteinMIs().iterator();iter.hasNext();) {
            ourProteinCriteria.addOrCriteria(buildMICriteria((String) iter.next()));
        }
    }

    // No instantiation from outside.
    private EditorMenuFactory() {}

    /**
     * Returns the only instance of this class.
     */
    public static EditorMenuFactory getInstance() {
        return ourInstance;
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
     * Converts the given list to add menu.
     * @param menu the menu to convert (not modified).
     * @return converted menu; {@link #SELECT_LIST_ITEM} is added as the first
     * item to the list.
     */
    public List convertToAddMenu(List menu) {
        // The menu to return.
        List modMenu = new LinkedList(menu);
        // The default value for add menu.
        String  defvalue = SELECT_LIST_ITEM;
        // Add as the first item in the list.
        modMenu.add(0, defvalue);
        return modMenu;
    }

    /**
     * Returns a menu for given name.
     * @param key the name of the menu; the valid values are: {@link #TOPIC},
     * {@link #DATABASE}, {@link #QUALIFIER}, {@link #ORGANISM},
     * {@link #INTERACTION}, {@link #IDENTIFICATION}, {@link #INTERACTION_TYPE},
     * {@link #EXPERIMENT} and {@link #ROLE}.
     * @param mode 0 for and edit menu and 1 for an add menu; the difference is
     * {@link #SELECT_LIST_ITEM} is added as the first entry for an add menu.
     * @return a list of menu items for given <code>name</code>.
     * @throws IntactException for errors in contructing the menu or unable to
     * create an Intact helper to access persistent system.
     */
    public List getMenu(String key, int mode) throws IntactException {
        // The class associated with the key.
        Class clazz = (Class) ourNameToType.get(key);
        List menu = getMenuList(clazz);
        if (menu.isEmpty()) {
            // Special list when we don't have any menu items.
            menu.add(SELECT_LIST_ITEM);
            return menu;
        }
        if (mode == 1) {
            menu = convertToAddMenu(menu);
        }
        return menu;
    }

    /**
     * Returns a list of menu itsm for the NucleicAcid editor.
     * @param mode 0 for and edit menu and 1 for an add menu; the difference is
     * {@link #SELECT_LIST_ITEM} is added as the first entry for an add menu.
     * @return a list of menu items for hte NucleicAcid editor.
     * @throws IntactException for errors in contructing the menu or unable to
     * create an Intact helper to access persistent system.
     */
    public List getNucleicAcidMenu(int mode) throws IntactException {
        return getPolymerMenu(mode, ourNucleicAcidCriteria);
    }

    /**
     * Returns a list of menu itsm for the Protein editor.
     * @param mode 0 for and edit menu and 1 for an add menu; the difference is
     * {@link #SELECT_LIST_ITEM} is added as the first entry for an add menu.
     * @return a list of menu items for hte Protein editor.
     * @throws IntactException for errors in contructing the menu or unable to
     * create an Intact helper to access persistent system.
     */
    public List getProteinMenu(int mode) throws IntactException {
        return getPolymerMenu(mode, ourProteinCriteria);
    }

    /**
     * True if <code>clazz</code> is a menu type
     * @param clazz the <code>Class</code> to compare.
     * @return true if <code>clazz</code> is one of designated menu type.
     */
    public boolean isMenuType(Class clazz) {
        return ourNameToType.containsValue(clazz);
    }

    // Helper methods

    /**
     * Return a List of all shortLabels of the class, e.g. for menus.
     *
     * @return a List of short labels as Strings.
     */
    private List getMenuList(Class targetClass) throws IntactException {
        // The menu to return.
        List menu = new ArrayList();

        // The query factory to get a query.
        ObjectBridgeQueryFactory qf = ObjectBridgeQueryFactory.getInstance();

        Query query = qf.getMenuBuildQuery(targetClass);

        Iterator iter = IntactHelperUtil.getDefaultIntactHelper().getIteratorByReportQuery(query);
        
        while (iter.hasNext()) {
            Object[] row = (Object[])iter.next();
            menu.add(row[1]);
        }
        return menu;
    }

    /**
     * Returns a list of menu itsm for the Polymer editor.
     * @param mode 0 for and edit menu and 1 for an add menu; the difference is
     * {@link #SELECT_LIST_ITEM} is added as the first entry for an add menu.
     * @return a list of menu items for hte Polymer editor.
     * @throws IntactException for errors in contructing the menu or unable to
     * create an Intact helper to access persistent system.
     */
    private List getPolymerMenu(int mode, Criteria criteria) throws IntactException {
        // The menu to return.
        List menu = new ArrayList();

        ReportQueryByCriteria query = QueryFactory.newReportQuery(
                CvInteractorType.class, criteria);
        // Limit to shortlabel
        query.setAttributes(new String[] { "shortlabel" });
        query.addOrderByAscending("shortLabel");

        Iterator iter = IntactHelperUtil.getDefaultIntactHelper().getIteratorByReportQuery(query);

        while (iter.hasNext()) {
            Object[] row = (Object[])iter.next();
            menu.add(row[0]);
        }
        if (menu.isEmpty()) {
            // Special list when we don't have any menu items.
            menu.add(SELECT_LIST_ITEM);
            return menu;
        }
        if (mode == 1) {
            menu = convertToAddMenu(menu);
        }
        return menu;
    }

    /**
     * @param mi
     * @return returns a criteria for matching primaryid with given mi (without
     * obsolete terms)
     */
    private static Criteria buildMICriteria(String mi) {
        Criteria crit = new Criteria();
        crit.addEqualTo("xrefs.primaryid", mi);
        crit.addNotIn("ac", ObjectBridgeQueryFactory.getInstance().getObsoleteQuery(
                CvInteractorType.class));
        return crit;
    }
}
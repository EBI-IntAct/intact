/** Java class "CvDagObject.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package uk.ac.ebi.intact.model;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

import uk.ac.ebi.intact.util.GoTools;

/**
 * Controlled vocabulary class for CVs which are organised in a Directed
 * Acyclic Graph (DAG). Each node many have multiple parents and multiple
 * children.
 *
 * @author hhe
 * @version $Id$
 */
public abstract class CvDagObject extends CvObject {

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     * Children are unique
     */
    private Collection children = new ArrayList(); // of type CvDagObject

    /**
     * TODO comments
     * Parents are unique
     */
    private Collection parents = new ArrayList(); // of type CvDagObject

    /**
     * no-arg constructor which will hopefully be removed later...
     */
    protected CvDagObject() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvDagObject instance. Requires at least a shortLabel and an
     * owner to be specified.
     * @param shortLabel The memorable label to identify this CvDagObject
     * @param owner The Institution which owns this CvDagObject
     * @exception NullPointerException thrown if either parameters are not specified
     */
    public CvDagObject(Institution owner, String shortLabel) {

        //super call sets up a valid CvObject
        super(owner, shortLabel);
    }


    ///////////////////////////////////////
    // access methods for associations
    public Collection getChildren() {
        return children;
    }

    // TODO are they unique ?
    public void addChild(CvDagObject cvDagObject) {

        if (! children.contains(cvDagObject)) {
            children.add(cvDagObject);
            cvDagObject.addParent(this);
        }
    }
    public void removeChild(CvDagObject cvDagObject) {
        boolean removed = children.remove(cvDagObject);
        if (removed) cvDagObject.removeParent(this);
    }
    public Collection getParents() {
        return parents;
    }
    public void addParent(CvDagObject cvDagObject) {

        if (! parents.contains(cvDagObject)) {
            parents.add(cvDagObject);
            cvDagObject.addChild(this);
        }
    }
    public void removeParent(CvDagObject cvDagObject) {
        boolean removed = parents.remove(cvDagObject);
        if (removed) cvDagObject.removeChild(this);
    }


    //////////////////////////////
    // * Specific DAG methods

    /**
     * Add the ancestors of the current node to the set of currentAncestors.
     * This assumes that you gives a non null Set.
     *
     * @param currentAncestors Ancestors collected so far.
     * @return currentAncestors, with all the ancestors of the current node added.
     */
    private Collection ancestors(Collection currentAncestors) {
        for (Iterator i = getParents().iterator(); i.hasNext();) {
            CvDagObject current = (CvDagObject) i.next();
            currentAncestors = current.ancestors(currentAncestors); // recursive call
        }
        currentAncestors.add(this);
        return currentAncestors;
    }

    /**
     * TODO comments
     *
     * @return All ancestors of the current object. Each node is listed only once.
     */
    public Collection ancestors(){
        return this.ancestors(new ArrayList());
    }

    /**
     * <p>
     * Produce a List of shortLabels which can be used to create a selection list,
     * eg for menus.
     * </p>
     * The structure of the tree will be represented by indentation, a shortLabel of
     * level two in the hierarchy will have two leading blanks.
     *
     * @param currentDepth the current indentation level.
     * @param menuList The list of labels collected so far.
     * @param current The start node. Labels for the node and its descendents will be added to the menuList.
     * @return A List of shortlabels.
     */
//    private static Set getMenuList(int currentDepth, Set menuList, CvDagObject current){
//
//        StringBuffer currentTerm = new StringBuffer();
//        for (int i = 0; i<currentDepth; i++) {
//            currentTerm.append('.');
//        }
//        currentTerm.append(current.getShortLabel());
//        menuList.add(currentTerm.toString());
//        for (Iterator iterator = current.children.iterator(); iterator.hasNext();) {
//            menuList = getMenuList(currentDepth+1, menuList, (CvDagObject) iterator.next());
//        }
//
//        return menuList;
//    }


    /**
     * Produce a List of shortLabels which can be used to create a selection list, eg for menus.
     * The structure of the tree will be represented by indentation, a shortLabel of level two in the hierarchy
     * will have two leading blanks.
     *
     * @param targetClass The class for which to return the menu list. It has to be a CvObject
     * @param helper Database access object
     * @param forceUpdate If true, an update of the list is forced.
     * @exception IllegalArgumentException if something different than a CvObject is given.
     *
     * @return List of Strings. Each string one shortlabel.
     */
//    public static Set getMenuList(Class targetClass, IntactHelper helper, boolean forceUpdate)
//            throws IntactException {
//        if (! CvObject.class.isAssignableFrom(targetClass)) {
//            throw new IllegalArgumentException ("The target class should be a CvObject!");
//        }
//
//        if ((menuList == null) || forceUpdate){
//            menuList = new HashSet();
//            // get an arbitrary element of the target class
//            Collection allElements = helper.search(targetClass.getName(),"ac","*");
//            Iterator i = allElements.iterator();
//
//            // Check for no items.
//            if (!i.hasNext()) {
//                return menuList;
//            }
//            CvDagObject current = (CvDagObject) i.next();
//
//            // get the root element
//            current = current.getRoot();
//
//            // update the list
//            menuList = getMenuList(0, menuList, current);
//        }
//
//        return menuList;
//    }

    /**
     * TODO coments
     *
     * @return the root node of the current term
     */
    public CvDagObject getRoot(){
        if (parents.size()>0){
            Iterator i = parents.iterator();
            return ((CvDagObject) i.next()).getRoot();
        } else {
            return this;
        }
    }

    /**
     * TODO comments
     *
     * @param currentDepth The current depth in the DAG. Translated into leading blanks.
     * @param aParent The current parents. All parents exept aParent are listed in the DAG line as additional parents.
     * @param goidDatabase The database xref from which to generate the goid
     * @return a single string containing the GO DAG flatfile representation of the current object and all its decendents.
     */
    public String toGoDag(int currentDepth, CvDagObject aParent, String goidDatabase){

        StringBuffer currentTree = new StringBuffer();

        // * Output the current term
        // leading blanks
        for (int i = 0; i<currentDepth; i++) {
            currentTree.append(' ');
        }

        // The GO prefix
        if (currentDepth == 0)
            currentTree.append("$");
        else
            currentTree.append("%");

        // write the current term
        currentTree.append(term2DagLine(goidDatabase));

        // additional parents
        for (Iterator iParents = parents.iterator(); iParents.hasNext();) {
            CvDagObject parent = (CvDagObject) iParents.next();
            if (aParent != parent) {
                currentTree.append(" % ");
                currentTree.append(parent.term2DagLine(goidDatabase));
            }
        }

        // end term line
        currentTree.append("\n");

        // Iterate over children to add subtrees.
        for (Iterator iterator = children.iterator(); iterator.hasNext();) {
            currentTree.append(((CvDagObject) iterator.next()).toGoDag(currentDepth+1, this, goidDatabase));
        }
        return currentTree.toString();
    }

    /**
     * TODO comments
     * @param goidDatabase The database xref from which to generate the goid
     * @return  a single GOid - GOterm pair in appropriate formatting for the GO DAG
     */
    private String term2DagLine(String goidDatabase) {

        StringBuffer termLine = new StringBuffer();

        // The term itself
        termLine.append(getFullName());

        // Write GO id
	String goid = GoTools.getGoid(this, goidDatabase);

	if (null != goid){
	    termLine.append(" ; " + goid);
	};

        // add synonyms here once they are defined

        return termLine.toString();
    }

    /**
     * Create the GO flat file representation of the current object and all its descendants.
     *
     * @return a single string containing the GO DAG flatfile representation
     * of the current object and all its descendants.
     */
    public String toGoDag(String goidDatabase){
        return toGoDag(0, this, goidDatabase);
    }

} // end CvDagObject






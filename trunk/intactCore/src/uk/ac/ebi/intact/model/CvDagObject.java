/** Java class "CvDagObject.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;

import java.util.*;

/**
 * <p>
 * Controlled vocabulary class for CVs which are organised in a Directed
 * Acyclic Graph (DAG). Each node many have multiple parents and multiple
 * children.
 * </p>
 *
 */
public abstract class CvDagObject extends CvObject {

    /**
     * Cache a Vector of all shortLabels of the class, e.g. for menus.
     *
     */
    protected static Vector menuList = null;

   ///////////////////////////////////////
   // associations

/**
 * <p>
 *
 * </p>
 */
    public Collection child = new Vector(); // of type CvDagObject
/**
 * <p>
 *
 * </p>
 */
    public Collection parent = new Vector(); // of type CvDagObject


   ///////////////////////////////////////
   // access methods for associations

    public Collection getChilds() {
        return child;
    }
    public void addChild(CvDagObject cvDagObject) {
        if (! this.child.contains(cvDagObject)) {
            this.child.add(cvDagObject);
            cvDagObject.addParent(this);
        }
    }
    public void removeChild(CvDagObject cvDagObject) {
        boolean removed = this.child.remove(cvDagObject);
        if (removed) cvDagObject.removeParent(this);
    }
    public Collection getParents() {
        return parent;
    }
    public void addParent(CvDagObject cvDagObject) {
        if (! this.parent.contains(cvDagObject)) {
            this.parent.add(cvDagObject);
            cvDagObject.addChild(this);
        }
    }
    public void removeParent(CvDagObject cvDagObject) {
        boolean removed = this.parent.remove(cvDagObject);
        if (removed) cvDagObject.removeChild(this);
    }

    // * Specific DAG methods

    /** Add the ancestors of the current node to the set of currentAncestors.
     *
     * @param currentAncestors Ancestors collected so far.
     * @return currentAncestors, with all the ancestors of the current node added.
     */
    private Set ancestors(Set currentAncestors){
       for (Iterator i = this.getParents().iterator(); i.hasNext();) {
           CvDagObject current = (CvDagObject) i.next();
           currentAncestors = current.ancestors(currentAncestors);
       }
        currentAncestors.add(this);
        return currentAncestors;
    }

    /**
     * @return All ancestors of the current object. Each node is listed only once.
     */
    public Set ancestors(){
        return this.ancestors(new HashSet());
    }

    /**
     * Produce a vector of shortLabels which can be used to create a selection list, eg for menus.
     * The structure of the tree will be represented by indentation, a shortLabel of level two in the hierarchy
     * will have two leading blanks.
     *
     * @param currentDepth the current indentation level.
     * @param menuList The list of labels collected so far.
     * @param current The start node. Labels for the node and its descendents will be added to the menuList.
     * @return A vector of shortlabels.
     */
    private static Vector getMenuList(int currentDepth, Vector menuList, CvDagObject current){

        StringBuffer currentTerm = new StringBuffer();
        for (int i = 0; i<currentDepth; i++) {
            currentTerm.append(' ');
        }
        currentTerm.append(current.getShortLabel());
        menuList.add(currentTerm);
        for (Iterator iterator = current.child.iterator(); iterator.hasNext();) {
            menuList = getMenuList(currentDepth+1, menuList, (CvDagObject) iterator.next());
        }

        return menuList;
    }

    /**
     * Produce a vector of shortLabels which can be used to create a selection list, eg for menus.
     * The structure of the tree will be represented by indentation, a shortLabel of level two in the hierarchy
     * will have two leading blanks.
     *
     * @param targetClass The class for which to return the menu list.
     * @param helper Database access object
     * @param forceUpdate If true, an update of the list is forced.
     *
     * @return Vector of Strings. Each string one shortlabel.
     */
    public static Vector getMenuList(Class targetClass, IntactHelper helper, boolean forceUpdate)
            throws IntactException {
        if ((menuList == null) || forceUpdate){
            menuList = new Vector();
            // get an arbitrary element of the target class
            Collection allElements = helper.search(targetClass.getName(),"ac","*");
            Iterator i = allElements.iterator();
            CvDagObject current = (CvDagObject) i.next();

            // get the root element
            current = current.getRoot();

            // update the list
            menuList = getMenuList(0, menuList, current);
        }

        return menuList;
    }

    /**
     *
     * @return the root node of the current term
     */
    public CvDagObject getRoot(){
        if (parent.size()>0){
            Iterator i = parent.iterator();
            return ((CvDagObject) i.next()).getRoot();
        } else {
            return this;
        }
    }

    /**
     *
     * @param currentDepth The current depth in the DAG. Translated into leading blanks.
     * @param aParent The current parent. All parents exept aParent are listed in the DAG line as additional parents.
     * @return a single string containing the GO DAG flatfile representation of the current object and all its decendents.
     */
    public String toGoDag(int currentDepth, CvDagObject aParent){

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
        currentTree.append(term2DagLine());

        // additional parents
        for (Iterator parents = parent.iterator(); parents.hasNext();) {
            CvDagObject parent = (CvDagObject) parents.next();
            if (aParent != parent) {
                currentTree.append(" % ");
                currentTree.append(parent.term2DagLine());
            }
        }

        // end term line
        currentTree.append("\n");

        // Iterate over children to add subtrees.
        for (Iterator iterator = child.iterator(); iterator.hasNext();) {
            currentTree.append(((CvDagObject) iterator.next()).toGoDag(currentDepth+1, this));
        }
        return currentTree.toString();
    }

    /**
     * @return  a single GOid - GOterm pair in appropriate formatting for the GO DAG
     */
    private String term2DagLine() {

        StringBuffer termLine = new StringBuffer();

        // The term itself
        termLine.append(getFullName());

        // the GO id
        for (Iterator iterator = xref.iterator(); iterator.hasNext();) {
            Xref xref = (Xref) iterator.next();
            if (xref.getCvDatabase().getShortLabel().equals("GO")){
                termLine.append(" ; " + xref.getPrimaryId());
                // There should be only one GO ID
                break;
            }
        }

        // add synonyms here once they are defined

        return termLine.toString();
    }

    /** Create the GO flat file representation of the current object and all its descendants.
     *
     * @return a single string containing the GO DAG flatfile representation
     * of the current object and all its descendants.
     */
    public String toGoDag(){
        return toGoDag(0, this);
    }

} // end CvDagObject






/** Java class "CvDagObject.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package uk.ac.ebi.intact.model;

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
     * @param currentDepth Current depth in the tree. Translated into leading blanks.
     * @return the current objects and all its descendents as an indented Tree.
     */
    private String toIndentedTree(int currentDepth){
        StringBuffer currentTree = new StringBuffer();
        for (int i = 0; i<currentDepth; i++) {
            currentTree.append(' ');
        }
        currentTree.append(this.getShortLabel() + "\n");
        for (Iterator iterator = child.iterator(); iterator.hasNext();) {
            currentTree.append(((CvDagObject) iterator.next()).toIndentedTree(currentDepth+1));
        }
        return currentTree.toString();
    }

    public String toIndentedTree(){
        return toIndentedTree(0);
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

    public String toGoDag(){
        return toGoDag(0, this);
    }

} // end CvDagObject






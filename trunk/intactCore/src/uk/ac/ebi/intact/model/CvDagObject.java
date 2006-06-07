/** Java class "CvDagObject.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
package uk.ac.ebi.intact.model;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Controlled vocabulary class for CVs which are organised in a Directed
 * Acyclic Graph (DAG). Each node many have multiple parents and multiple
 * children.
 *
 * @author hhe
 * @version $Id$
 */
@Entity
public abstract class CvDagObject extends CvObject {

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     * Children are unique
     */
    private Collection<CvDagObject> children;
    /**
     * TODO comments
     * Parents are unique
     */
    private Collection<CvDagObject> parents;

    ///////////////////////////
    // start modification (afrie)

    /**
     * specifies the left bound number if the DAG would be a tree
     */
    private long leftBound = -1;

    /**
     * specifies the right bound number if the DAG would be a tree
     */
    private long rightBound = -1;


    // end modification (afrie)
    /////////////////////////////

    /**
     * no-arg constructor which will hopefully be removed later...
     */
    public CvDagObject() {
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
    @ManyToMany
    @JoinTable(
        name="ia_cv2cv",
        joinColumns={@JoinColumn(name="parent_ac")},
        inverseJoinColumns={@JoinColumn(name="child_ac")}
    )
    public Collection<CvDagObject> getChildren() {
        return children;
    }


     /////////////////////////////
     // start modification (afrie)
     //
     // these methods are used in the CvDagObjectUtils class
     // the setter and getter are only used for testing


    /**
     * this method returns 'true' when children are present 'false' otherwise
     * @return result
     */
    public boolean hasChildren(){
        boolean result = false;

        if(this.getChildren().size()>0 && children != null){
            result = true;
        }
        return result;
    }


      /**
       * This method returns the left bound of this instance,
       * or '-1' if the bounds have not been calculated.
       *
       * @return int with the left bound, or '-1' if the
       *         bounds have not yet been calculated.
       */
      @Transient
      public long getLeftBound() {
          return leftBound;
      }

    /**
     * This method set the left bound of this instance
     *
     * @param leftBound  left bound of this instance
     */
      public void setLeftBound(long leftBound) {
          this.leftBound = leftBound;
      }

      /**
       * This method returns the right bound of this instance,
       * or '-1' if the bounds have not been calculated.
       *
       * @return int with the right bound, or '-1' if the
       *         bounds have not yet been calculated.
       */
      @Transient
      public long getRightBound() {
          return rightBound;
      }

      /**
       * This method sets the right bound of this instance
       * @param rightBound   right bound of this instance
       */
      public void setRightBound(long rightBound) {
          this.rightBound = rightBound;
      }

        // end modification (afrie)
    /////////////////////////////


    // TODO are they unique ?
    public void addChild(CvDagObject cvDagObject) {

        if (! children.contains(cvDagObject)) {
            children.add(cvDagObject);
            cvDagObject.addParent(this);
        }
    }
    public void removeChild(CvDagObject cvDagObject) {
        boolean removed = children.remove(cvDagObject);
        if (removed)
        {
            cvDagObject.removeParent(this);
        }
    }

    @ManyToMany
    @JoinTable(
        name="ia_cv2cv",
        joinColumns={@JoinColumn(name="child_ac", referencedColumnName = "ac")},
        inverseJoinColumns={@JoinColumn(name="parent_ac", referencedColumnName = "ac")}
    )
    public Collection<CvDagObject> getParents() {
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
        if (removed)
        {
            cvDagObject.removeChild(this);
        }
    }

    protected void setChildren(Collection<CvDagObject> children)
    {
        this.children = children;
    }

    protected void setParents(Collection<CvDagObject> parents)
    {
        this.parents = parents;
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
    private Collection<CvDagObject> ancestors(Collection<CvDagObject> currentAncestors) {
        for (CvDagObject current : getParents())
        {
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
    public Collection<CvDagObject> ancestors(){
        return this.ancestors(new ArrayList<CvDagObject>());
    }

    /**
     * TODO coments
     *
     * @return the root node of the current term
     */
    @Transient
    public CvDagObject getRoot(){
        if (parents.size()>0){
            Iterator<CvDagObject> i = parents.iterator();
            return i.next().getRoot();
        } else {
            return this;
        }
    }
}






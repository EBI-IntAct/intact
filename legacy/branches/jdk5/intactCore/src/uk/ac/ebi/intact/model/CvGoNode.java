/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.CvDagObject;
import uk.ac.ebi.intact.model.Editable;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.Xref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Controlled vocabulary class for GeneOntology. CVs which are organised in
 * a Directed Acyclic (DAG). Each node can have many multiple parents and multiple
 * children. Each node represents one specific GO ID (cv) in the GO hierarchy.<br>
 * Before this class can be used, GoTools.insertGoDag(CvGoNode.class, GoDagFlatfile)
 * must be executet.
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */
public class CvGoNode extends CvDagObject {

    // =======================================================================
    // Class and object attributes
    // =======================================================================

    /**
     * record nodes that have been visited during graph exploration.<br>
     * Used by "getAllChilds"
     */
    private HashSet visited;

    /**
     * level of hierarchy in dag. Level 0 is the original node (this).
     */
    private int level;

    // =======================================================================
    // Constructor
    // =======================================================================

    /**
     * This constructor should be replaced as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     */
    public CvGoNode() {
        super();
        level = 0;
    }

    // =======================================================================
    // Public interface
    // =======================================================================

    /**
     * @return The GO id, which belongs to this unique CvGoNode Object
     */
    public String getGoId() {
        Collection someXrefs = this.getXrefs();
        if (someXrefs.size() != 1) {
            System.out.println("warning: a CvGoNode can only have one specific Xref");
        }
        Xref xref = (Xref) someXrefs.iterator().next();
        return xref.getPrimaryId();
    }

    /**
     * This CvGoNode can have children - the equivalent method is implemented in the
     * super class CvDagObject by "getChilds" method<br>
     * This method should provide ALL children (subchildren) of the node,<br>
     * that means also the children of the children of the children etc.
     * @return all possible children of this node as a Collection of CvGoNodes
     */
    public Collection getAllChilds() {
        visited = new HashSet();
        Collection childs = new ArrayList();
        childs = this.calculateAllChilds(this, childs);
        return childs; // Collection of CvGoNodes
    }

    /**
     * @return Collection of Interactors, which were associated with this GO term / id
     */
    public Collection getInteractors(IntactHelper helper) throws IntactException {
        Collection interactors = new ArrayList();
        try {
            interactors.addAll(helper.getObjectsByXref(Interactor.class, this.getGoId()));
        } catch (IntactException e) {
            throw e;
        }
        return interactors; // Collection of Interactors
    }

    // === Common interface ==================================================

    /**
     * @return String represention of this node
     */
    public String toString() {
        StringBuffer string = new StringBuffer();
        string.append("\nCvGoNode object for " + this.getGoId());
        string.append("\n      -> childs: ");
        Iterator itChilds = this.getChildren().iterator();
        while (itChilds.hasNext()) {
            CvGoNode aCvGoNode = (CvGoNode) itChilds.next();
            string.append(aCvGoNode.getGoId() + ", ");
        }
        return string.toString();
    }

    // =======================================================================
    // Private Methods
    // =======================================================================

    /**
     * Recursive method wich calculates all children and subchildren of a node.
     * @param aParent
     * @param collection - to call method with an empty collection - it is used internally for recursive call.
     * @return all children and subchildren as CvGoNode objects
     */
    private Collection calculateAllChilds(CvGoNode aParent, Collection collection) {
        if (!visited.contains(aParent)) {
            // Iterate over children to add subtrees.
            level++; // Hierarchylevel
            Iterator itChilds = aParent.getChildren().iterator();
            while (itChilds.hasNext()) {
                // For each child recursive call
                CvGoNode node = (CvGoNode) itChilds.next();
                collection = calculateAllChilds(node, collection);
            }
            level--;
            // only children are in interested - root of request will be filtered out
            if (level > 0) {
                collection.add(aParent);
                visited.add(aParent);
            }
        }
        return collection; // CvGoNodes
    }
}





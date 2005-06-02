/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util.go;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.CvDagObject;
import uk.ac.ebi.intact.model.Alias;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.CvAliasType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Auxiliary class to insert GO Dag nodes from a GO dag formatted flat file.
 *
 * @author Henning Hermjakob (hhe@ebi.ac.uk), Sugath Mudali (smudali@ebi.ac.uk)
 */
public class DagNode {

    // ------------------------------------------------------------------------

    /**
     * Collects GO parent data.
     */
    private class GoParentData {

        private String myId;
        private String myTerm;
        private String myShortLabel;

        private GoParentData(String id, String term, String shortLabel) {
            myId = id;
            myTerm = term;
            myShortLabel = GoUtils.normalizeShortLabel(shortLabel);
        }

        // Override equals method.

        public boolean equals(Object object) {
            if (this == object) {
                // x.equals(x)
                return true;
            }
            if (getClass().isAssignableFrom(object.getClass())) {
                // Comparision is based on the id.
                return myId.equals(((GoParentData) object).myId);
            }
            return false;
        }

        public int hashCode() {
            return 17 * myId.hashCode();
        }
    }

    // ------------------------------------------------------------------------

    /**
     * The indentation level in the GO file
     */
    private int myIndentLevel;

    /**
     * The primary parent of the current node
     */
    private DagNode myParent;

    /**
     * The additional parents of the current node.
     */
    private List myAdditionalParents = new ArrayList();

    /**
     * GO id of the current node.
     */
    private String myGoId;

    /**
     * The Go term
     */
    private String myGoTerm;

    /**
     * The short label defined in the GO Dag file.
     */
    private String myGoShortLabel;

    /**
     * Contains a list of aliases (strings)
     */
    private List myAliases = new ArrayList();

    /**
     * Default constructor
     */
    public DagNode() {
    }

    public void setGoTerm(String goTerm) {
        myGoTerm = goTerm;
    }

    public String getGoTerm() {
        return myGoTerm;
    }

    public void setGoId(String goid) {
        myGoId = goid;
    }

    public String getGoId() {
        return myGoId;
    }

    public void setGoShortLabel(String shortLabel) {
        myGoShortLabel = GoUtils.normalizeShortLabel(shortLabel);
    }

    public void setGoShortLabelFromTerm() {
        setGoShortLabel(myGoTerm);
    }

    public String getGoShortLabel() {
        return myGoShortLabel;
    }

    public void setIndentLevel(int level) {
        myIndentLevel = level;
    }

    /**
     * Returns true if aNode is a parent of the current node. Note:
     * This relies on the GO flatfile structure, it is not a general
     * purpose method.
     */
    public boolean isParent(DagNode aNode) {
        if (aNode == null) {
            return false;
        }
        return (aNode.myIndentLevel == this.myIndentLevel - 1);
    }

    public void setParent(DagNode parent) {
        myParent = parent;
    }

    public void addParentData(String id, String term) {
        addParentData(id, term, term);
    }

    public void addParentData(String id, String term, String label) {
        myAdditionalParents.add(new GoParentData(id, term, label));
    }

    public void addAlias(String alias) {
        myAliases.add(alias);
    }

    /**
     * Save the current DAG node to the database.
     *
     * @throws IntactException for errors in searching the database.
     */
    public void storeDagNode(GoUtils goUtils) throws IntactException {

        CvDagObject targetNode = null;

        IntactHelper helper = goUtils.getHelper();
        String goidDatabase = goUtils.getGoIdDatabase();

        // Get parent and child (targetNode) from the database
        if (goidDatabase.equals("-")) {
            throw new IntactException("Can't store DAG node without goidDatabase and goid");
        }
        else {
            targetNode = (CvDagObject) goUtils.selectCvObject(myGoId, myGoShortLabel);
        }

        // if the target node is not defined, create it.
        if (targetNode == null) {
            // This shouldn't happen because def file has taken care of inserting
            // nodes. Only way, you get to this point is when you have an entry in
            // the dag file that wasn't found in the def file (error in saving files)
            throw new IntactException("Target node wasn't found for: " + myGoId
                    + " " + myGoTerm + " " + myGoShortLabel);
        }

        // Insert the direct parent
        if (myParent != null) {
            CvDagObject directParent = (CvDagObject) goUtils.selectCvObject(
                    myParent.myGoId, myParent.myGoShortLabel);
            if (directParent == null) {
                // Wants to check where this condition is true. This shouldn't be
                // true because def file has taken care of inserting all the parents.
                throw new IntactException("Direct parent wasn't found for: "
                        + myParent.myGoId + " " + myParent.myGoTerm + " "
                        + myParent.myGoShortLabel);
            }
            // Add the link between parent and child
            targetNode.addParent(directParent);
            helper.update(directParent);
            helper.update(targetNode);
        }

        // Insert additional parents
        for (Iterator iter = myAdditionalParents.iterator(); iter.hasNext();) {
            GoParentData parentData = (GoParentData) iter.next();
            String nextGoid = parentData.myId;
            String nextGoLabel = parentData.myShortLabel;
            CvDagObject additionalParent = null;
            if (goidDatabase.equals("-")) {
                throw new IntactException("Can't store DAG node without goidDatabase and goid");
            }
            else {
                additionalParent = (CvDagObject) goUtils.selectCvObject(nextGoid,
                        nextGoLabel);
            }

            if (additionalParent == null) {
                // Wants to check where this condition is true. This shouldn't be
                // true because def file has taken care of inserting all the parents.
                throw new IntactException("Additional parent wasn't found");
            }
            // Add the link between parent and child
            targetNode.addParent(additionalParent);
            helper.update(additionalParent);
        }
        // Add aliases.
        if (!myAliases.isEmpty()) {
            // Cache objects to create aliases.
            Institution owner = helper.getInstitution();
            CvAliasType aliasType = (CvAliasType) helper.getObjectByLabel(
                    CvAliasType.class, "go synonym");
            // Must have an alias type
            if (aliasType == null) {
                throw new IntactException("Alias type go synonym is missing");
            }

            for (Iterator iter = myAliases.iterator(); iter.hasNext(); ) {
                Alias alias = new Alias(owner, targetNode, aliasType, (String) iter.next());
                // Do the check to avoid creating duplicate aliases.
                if (!targetNode.getAliases().contains(alias)) {
                    targetNode.addAlias(alias);
                    helper.create(alias);
                }
            }
        }
        helper.update(targetNode);
    }

    public String toString() {
        return myGoId + ": " + myParent + ": " + myAdditionalParents;
    }
}

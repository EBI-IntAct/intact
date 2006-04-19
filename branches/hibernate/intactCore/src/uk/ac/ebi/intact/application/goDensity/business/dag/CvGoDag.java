/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.business.dag;

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.goDensity.business.data.DbTools;
import uk.ac.ebi.intact.application.goDensity.exception.DatabaseEmptyException;
import uk.ac.ebi.intact.application.goDensity.exception.KeyNotFoundException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.CvGoNode;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * CvGoDag represents a GO graph within IntAct.<br>
 * Furthermore, the method todb():void can be used, to store the GO-graph
 * in an external table, which can be used for fast & direct access (FastGoDag.java).
 * Before you can use this class, GO has to be imported (InsertGoFromFlatfile.java)
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */
public class CvGoDag {

    // =======================================================================
    // Class and object attributes
    // =======================================================================

    static Logger logger = Logger.getLogger("goDensity");

    /**
     * IntactHelper class to retrieve data from the IntAct model / database
     */
    private IntactHelper _helper;

    /**
     * root element of the dag graph
     */
    private CvGoNode _root;

    /**
     * record nodes that have been visited during graph exploration.<br>
     * Used by "getAllChilds"
     */
    private HashSet _visited = new HashSet();

    /**
     * root in DAG-Graph is 0. The depth for each node from root.
     * If a node has two parents with different depths, the
     * parent with bigger depths will be used (max. way)
     */
    private int _depth;

    /**
     * "INSERT INTO ia_goDens_GoDag VALUES (?, ?, ?, ?)"
     */
    private static PreparedStatement _psDag;

    /**
     * "INSERT INTO ia_goDens_GoDagDenorm VALUES (?, ?)"
     */
    private static PreparedStatement _psDagDenorm;

    /**
     * "INSERT INTO ia_goDens_GoProt VALUES (?, ?)");
     */
    private static PreparedStatement _psProt;

    // =======================================================================
    // Constructor
    // =======================================================================

    /**
     * Constructor retrieve the root element of GO hierarchy (of typ CvGoNode) from the database.
     * @throws DatabaseEmptyException if no go-dag-file is jet imported into db.
     */
    public CvGoDag() throws DatabaseEmptyException,
            IntactException,
            SQLException,
            KeyNotFoundException {

        _helper = new IntactHelper();
        _root = (CvGoNode) _helper.getObjectByXref(CvGoNode.class, "GO:0003673");

        _psDag = DbTools.getInstance().getCon().prepareStatement("INSERT INTO ia_goDens_GoDag VALUES (?, ?, ?, ?)");
        _psDagDenorm = DbTools.getInstance().getCon().prepareStatement("INSERT INTO ia_goDens_GoDagDenorm VALUES (?, ?)");
        _psProt = DbTools.getInstance().getCon().prepareStatement("INSERT INTO ia_goDens_GoProt VALUES (?, ?)");

        logger.info("root CvGoNode = " + _root);
    }

    // =======================================================================
    // Public interface
    // =======================================================================

    /**
     * stores the GO dag to a external table, which only stores the go hierarchy,
     * which is used by FastGoDag
     */
    public void toDb() {
        this.dbPopulate(_root);
        try {
            _psDag.close();
            _psProt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * get a single CvGoNode by a GoId String
     * @param goId
     * @return a CvGoNode for the related GoId
     * @throws KeyNotFoundException if no CvGoNode is present for this GoId request
     * Could happen, if e.g. only a part of the go-dag.ontology is imported ...
     */
    public CvGoNode getCvGoNode(String goId) throws KeyNotFoundException {
        CvGoNode node;
        try {
            node = (CvGoNode) _helper.getObjectByXref(CvGoNode.class, goId);
        } catch (IntactException e) {
            throw new KeyNotFoundException("go id " + goId + " was not found\n");
        }
        if (node == null)
            throw new KeyNotFoundException("go id " + goId + " was not found\n");
        return node;
    }

    /**
     * @param goId for which the children want to be known
     * @return children of an specific GoId are returned as a Collection of CvGoNodes.
     * @throws KeyNotFoundException if goId doesn't exist as an CvGoNode Object
     */
    public Collection getChilds(String goId) throws KeyNotFoundException {
        return this.getCvGoNode(goId).getChildren(); // type CvGoNodes
    }

    /**
     * @param goId for which all children and subchildren want to be known
     * @return ALL children of the goId - but be aware: only the goId Strings of the
     * children (primaryId) will be returned as a Collection
     * @throws KeyNotFoundException if goId doesn't exist
     */
    public Collection getAllChilds(String goId) throws KeyNotFoundException {
        return this.getCvGoNode(goId).getAllChilds(); // type CvGoNodes
    }

    /**
     * @param goId for which all associated Interactors want to be known
     * @return a Collection of Interactors which are associated with this node
     */
    public Collection getInteractors(String goId) {
        Collection result = new ArrayList();
        try {
            result = _helper.getObjectsByXref(Interactor.class, goId);
        } catch (IntactException e) {
            System.out.println("error by getObjectByXref for this goId (" + goId + ") " + e);
        }
        return result;
    }

    // === Common interface ==================================================

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append(_root.toString());
        result.append("\n      -> Interactors: ");

        try {
            Collection someInteractorsOfRoot = _root.getInteractors(_helper);
            Iterator itInteractorsOfRoot = someInteractorsOfRoot.iterator();
            while (itInteractorsOfRoot.hasNext()) {
                Interactor interactor = (Interactor) itInteractorsOfRoot.next();
                result.append(interactor.getAc() + ", ");
            }
        } catch ( IntactException e ) {
            e.printStackTrace();
        }

        Collection allChilds = null;
        try {
            allChilds = this.getAllChilds(_root.getGoId());
            Iterator itAllChilds = allChilds.iterator();
            while (itAllChilds.hasNext()) {
                CvGoNode node = (CvGoNode) itAllChilds.next();
                result.append("\n" + node.toString() + "\n      -> Interactors: ");
                try {
                    Iterator itSomeInteractors = node.getInteractors(_helper).iterator();
                    while (itSomeInteractors.hasNext()) {
                        result.append(((Interactor) itSomeInteractors.next()).getAc() + ", ");
                    }
                } catch ( IntactException e ) {
                    e.printStackTrace ();
                }
            }
        } catch (KeyNotFoundException e) {
            System.out.println("error: " + e);
            e.printStackTrace();
        }

        return result.toString();
    }

    // =======================================================================
    // Private Methods
    // =======================================================================

    /**
     * table will be filled with a go hierarchy, started at the level of the parameter node
     * @param node starting node to build the go dag and store it to external table in db for FastGoDag.class
     */
    private void dbPopulate(CvGoNode node) {
        logger.debug("dbPopulate(" + node + ");");
        String nodeGoId = node.getGoId();

        ArrayList denormGoIds = new ArrayList();
        try {
            denormGoIds = new ArrayList(this.getAllChilds(nodeGoId));
            denormGoIds.add(node);
            logger.debug("denormGoIds.add(allChilds) -> count of childs: " + denormGoIds.size());
        } catch (KeyNotFoundException e) {
            logger.error(e.toString());
        }

        if (!_visited.contains(node.getGoId())) {
            _visited.add(node.getGoId());
            try {

                _psDagDenorm.setString(1, nodeGoId);
                for (int i = 0; i < denormGoIds.size(); i++) {
                    _psDagDenorm.setString(2, ((CvGoNode) denormGoIds.get(i)).getGoId());
                    _psDagDenorm.executeUpdate();
                    logger.debug("_psDagDenorm added (" + nodeGoId + " & " + ((CvGoNode) denormGoIds.get(i)).getGoId());
                    System.out.println(".");
                }

                _psProt.setString(1, nodeGoId);
                Collection prots = this.getInteractors(nodeGoId);
                Iterator iterator = prots.iterator();
                while (iterator.hasNext()) {
                    Interactor interactor = (Interactor) iterator.next();
                    _psProt.setString(2, interactor.getAc());
                    _psProt.executeUpdate();
                    logger.debug("_psProt added (" + nodeGoId + " & " + interactor.getAc());
                    System.out.println(".");
                }

                Iterator itSomeChilds = node.getChildren().iterator();
                while (itSomeChilds.hasNext()) {
                    CvGoNode child = (CvGoNode) itSomeChilds.next();
                    _psDag.setString(1, nodeGoId);
                    _psDag.setString(2, child.getGoId());
                    _psDag.setInt(3, _depth);
                    _psDag.setInt(4, _depth + 1);
                    _psDag.executeUpdate();
                    logger.debug("_psDag added (" + nodeGoId + " & " + child.getGoId() + " & " + (_depth - 1) + " & " + _depth);
                    System.out.println(".");

                    _depth++;

                    // recursive call - so the whole graph will be explored
                    this.dbPopulate(child);
                }
            } catch (SQLException e) {
                logger.error("error inserting goIds to database: " + e);
            } // tryCatchFinally
        } // if !visited the node
        _depth--;
    }

    /*
    public static void main(String[] args) {
        try {
            CvGoDag dag = new CvGoDag();

            Collection childs = dag.getChilds("GO:0003673");
            Iterator it1 = childs.iterator();
            while (it1.hasNext()) {
                CvGoNode cvGoNode = (CvGoNode) it1.next();
                System.out.println("cvGoNode = " + cvGoNode);
            }

            Collection allchilds = dag.getAllChilds("GO:0003673");
            System.out.println("allchilds.size() = " + allchilds.size());

        } catch (DatabaseEmptyException e) {
            e.printStackTrace();
        } catch (IntactException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (KeyNotFoundException e) {
            e.printStackTrace();
        }
    }
    */

}

/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.goDensity.business.dag;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.goDensity.business.binaryInteractions.GoGoDensity;
import uk.ac.ebi.intact.application.goDensity.business.data.DbTools;
import uk.ac.ebi.intact.application.goDensity.business.data.Key2HashSet;
import uk.ac.ebi.intact.application.goDensity.business.data.TwoMaps;
import uk.ac.ebi.intact.application.goDensity.exception.GoIdNotInDagException;
import uk.ac.ebi.intact.application.goDensity.exception.KeyNotFoundException;
import uk.ac.ebi.intact.application.goDensity.exception.SameGraphPathException;
import uk.ac.ebi.intact.application.goDensity.setupGoDensity.Config;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.CvGoNode;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Represents GO-Graph (hierarchy) with fast and direct JDBC access to db.
 * Before you can use this class,<br>
 * - GoTools.insertGoDag(CvGoNode.class, new IntactHelper(), go.ontology);<br>
 * - CalcBinInteractionData.java<br>
 * must be executed to fill external ia_goDens_tables.<br>
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */
public class FastGoDag extends Key2HashSet {

    // =======================================================================
    // Class and object attributes
    // =======================================================================

    static Logger logger = Logger.getLogger("goDensity");

    /**
     * This class is a repository for the GO graph. All classes can
     * share one instance of this class -> singleton pattern.
     */
    private static FastGoDag _instance = null;

    /**
     * All children and subchildren of a specific node.
     * Nodes itself are represented as simple Strings.
     */
    private HashSet _allChildsOfaNode;

    /**
     * a Collection of nodes,
     * which have been already visited during graph exploration.
     */
    private HashSet _isAlreadyVisited;

    /**
     * represents on one side the goIds and on the other side the related
     * graph depths (root = 0) - the maximal depth will be stored, if there
     * are more suitable ways with different depth. Reason: Later, if precalculation
     * starts, it starts at the deepest point - necessary, if we find at some point a
     * recursive function for calculating the densities (from leaves to the root)
     */
    private TwoMaps _goIdDepth;

    /**
     * value, which stores the maximal depth of the graph, beginning at root
     */
    private int _maxGoIdDepth;

    /**
     * to store the shortlabels of all GO:IDs
     */
    private Hashtable _goShortlabels;

    /**
     * to store the fullnames of all GO:IDs
     */
    private Hashtable _goFullname;

    /**
     * count distinct Interactors in and under (of all subchildren) one specific node
     */
    private PreparedStatement _countDistProt1Node;

    /**
     * get all distinct Interactors of a specific node "a"
     * minus the intersection of Interactors of another node "b"
     */
    private PreparedStatement _getDistProt2aNodes;

    /**
     * get all distinct Interactors of a specific node "b"
     * minus the intersection of Interactors of another node "a"
     */
    private PreparedStatement _getDistProt2bNodes;

    /**
     * count all distinct binary Interactions in and under (subchildren) one specific node
     */
    private PreparedStatement _countIA1Node;

    /**
     * count all distinct binary Interactions in and under (subchildren) two specific nodes.
     * Only the binaryInteractions which belongs to both nodes, are counted.
     * Overlapping nodes under the two nodes are eliminated and binary Interactions which
     * belongs to this node are not counted!
     */
    private PreparedStatement _countIA2Nodes;

    /**
     * if the densities of binaryInteractions should be precalculated,
     * it will be stored in a extra table. Statement for writing into this table.
     */
    private PreparedStatement _densityWrite;

    /**
     * to query all Interactors (stored as Strings) which belongs to one specific node
     */
    private PreparedStatement _go2InteractorQuery;

    /**
     * parent - child relation to represent a graph.
     * If you ask for a parent you get all direkt children back (not subchildren)
     */
    private PreparedStatement _goParent2goChildQuery;

    // =======================================================================
    // getInstance (Singleton) - private Constructor
    // =======================================================================

    /**
     * get a instance of FastGoDag -> it's a singleton
     * @return instance of this class
     */
    static public synchronized FastGoDag getInstance() {
        if (null == _instance) {
            _instance = new FastGoDag();
            logger.debug("_instance = new FastGoDag()");
        }
        logger.debug("return an _instance of FastGoDag");
        return _instance;
    }

    /**
     * Constructor get the whole GO graph in memory. <br>
     * Before this class is doing its job,<br>
     * - GoTools.insertGoDag(CvGoNode.class, new IntactHelper(), go.ontology);<br>
     *   * - CalcBinInteractionData.java<br>
     *   * must be executed to fill external ia_goDens_tables.
     */
    private FastGoDag() {

        _goIdDepth = new TwoMaps();
        _maxGoIdDepth = 0;

        ResultSet result = null;

        try {

            setupStatements();

            _goParent2goChildQuery =
                    DbTools.getInstance().getCon().prepareStatement(
                            "SELECT parent, child, parentDepth, childDepth FROM ia_goDens_GoDag   ");

            logger.debug("start query for Parent2Child");
            result = _goParent2goChildQuery.executeQuery();

            result.next();
            this.calcGoIdMaxDepth(result.getString("parent"), result.getInt("parentDepth"));
            this.calcGoIdMaxDepth(result.getString("child"), result.getInt("childDepth"));
            this.add(result.getString("parent"), result.getString("child"));

            while (result.next()) {
                int depth = result.getInt("childDepth");
                this.calcGoIdMaxDepth(result.getString("child"), depth);
                if (_maxGoIdDepth < depth) {
                    _maxGoIdDepth = depth;
                }

                // Adjacencylist (the superclass of this class) will be filled with the dag.
                this.add(result.getString("parent"), result.getString("child"));
            }
            logger.debug("finished query for Parent2Child -> transfer started");
            result.close();

            try {
                this.calcGoTermShort();
                this.calcGoTermFullname();
            } catch (IntactException e) {
                logger.error("error: " + e);
                e.printStackTrace();
            }


        } catch (SQLException e) {
            e.printStackTrace();
            finalize();
        }
    }

    /**
     * close all sql-statements properly - should be called once FastGoDag instance
     * is not needed any more -> don't trust the automatic finalisation!
     */
    public void finalize() {
        try {
            _go2InteractorQuery.close();
            _goParent2goChildQuery.close();
            _countIA1Node.close();
            _countIA2Nodes.close();
            _countDistProt1Node.close();
            _getDistProt2aNodes.close();
            _getDistProt2bNodes.close();
            _densityWrite.close();
        } catch (SQLException e) {
        }
    }

    // =======================================================================
    // Public interface
    // =======================================================================

    /**
     * test if the connection to db is still available and if not,
     * it reconnect to the db - used my default where ever sql-statements are used.
     */
    public void refreshDbConnection() {
        try {
            if (DbTools.getInstance().getCon().isClosed()) {
                DbTools.getInstance().updateCon();
                this.setupStatements();
            }
        } catch (SQLException e) {
        }
    }

    /**
     * @param goId (node) for which the children want to be known.
     * @return a Collection of goIds (Strings) which represent the direct children of the node
     */
    public Collection getChilds(String goId) throws KeyNotFoundException {
        Collection someChilds = new ArrayList();
        someChilds = super.getValueByKey(goId);
        return someChilds; // Collection of Strings
    }

    /**
     * @param goId (node) for which all children and subchildren want to be known.
     * @return a Collection of goIds (Strings) which represent the direct and
     * indirect children of the node (subchildren).
     * So all (!) children of the node will be returned.
     */
    public Collection getAllChilds(String goId) {
        _allChildsOfaNode = new HashSet();
        _isAlreadyVisited = new HashSet();
        this.calcAllChilds(goId);
        return _allChildsOfaNode; // Collection of Strings
    }

    /**
     * test, if goId1 equals goId2 or
     * if goId1 is a child or subchild of goId2
     * @param goId2
     * @param goId1
     * @return true, if goId1 equals goId2 or
     *               if goId1 is a (sub)child of goId2 or
     */
    public boolean isaOrPartOf(String goId1, String goId2) {
        if (goId2.equals(goId1)) {
            return true;
        }
        if (this.getAllChilds(goId2).contains(goId1)) {
            return true;
        }
        return false;
    }

    /**
     * test, if goId1 is in the same graph path of goId2
     * @param goId2
     * @param goId1
     * @return true, if goId1 is in the same graph path as goId2
     */
    public boolean isInSameGraphPath(String goId1, String goId2) {
        if (this.getAllChilds(goId2).contains(goId1)) {
            return true;
        }
        if (this.getAllChilds(goId1).contains((goId2))) {
            return true;
        }
        return false;
    }

    /**
     * test all goIds of a Set of GoIds, which are in the same graph path
     * @param distinctGoIds which should be tested
     * @return a Set of GoId-Pairs, which are (!) in the same graph path
     */
    public Set isInSameGraphPath(Set distinctGoIds) {
        List goIds = new ArrayList(distinctGoIds);
        Set goIdsInSameGraphPath = new HashSet();
        int countCombinations = distinctGoIds.size() - 1;

        for (int i = 0; i <= countCombinations; i++) {
            String currentGoId1 = (String) goIds.get(i);
            for (int j = i; j <= countCombinations; j++) {
                String currentGoId2 = (String) goIds.get(j);
                if (this.isInSameGraphPath(currentGoId1, currentGoId2)) {
                    goIdsInSameGraphPath.add(currentGoId1 + " & " + currentGoId2);
                }
            }
        }
        return goIdsInSameGraphPath;
    }

    /**
     * Test, if one or more of the goIds is not in contained in dag.
     * @param distinctGoIds which should be tested
     * @return a Set of goIds, which are not included in dag
     */
    public Set isGoIdInDag(Set distinctGoIds) {
        Set goIdsNotInDag = new HashSet();
        Iterator itGoIds = distinctGoIds.iterator();
        while (itGoIds.hasNext()) {
            String goId = (String) itGoIds.next();
            if (!_goIdDepth.contains(goId)) {
                goIdsNotInDag.add(goId);
            }
        }
        return goIdsNotInDag;
    }

    /**
     * precalculation of the go densities by a set of go-ids
     * @param goIds for which densities should be precalculated
     */
    public void precalcDensity(Set goIds) throws GoIdNotInDagException {

        Set notInDag = this.isGoIdInDag(goIds);
        if (notInDag.size() > 0)
            throw new GoIdNotInDagException("Some goIds are not in imported graph: " + notInDag.toString());

        List goIdsList = new ArrayList(goIds);
        int countCombinations = goIdsList.size() - 1;

        for (int i = 0; i <= countCombinations; i++) {
            String goId1 = (String) goIdsList.get(i);

            for (int j = i; j <= countCombinations; j++) {
                String goId2 = (String) goIdsList.get(j);

                if (!this.isInSameGraphPath(goId1, goId2)) {
                    try {
                        _densityWrite.setString(1, goId1);
                        _densityWrite.setString(2, goId2);
                        int pos = countPossibleInteractions(goId1, goId2);
                        int is = countExistingInteractions(goId1, goId2);
                        _densityWrite.setInt(3, pos);
                        _densityWrite.setInt(4, is);
                        _densityWrite.executeUpdate();
                        logger.debug("density " + goId1 + " & " + goId2 + " = " + (float) is / (float) pos);
                        // sout for shell import to see whats going on
                        System.out.print(".");
                    } catch (SQLException e) {
                        System.out.println("Already in DB: goId1: " + goId1 + "goId2:" + goId2);
                        e.printStackTrace();
                    }
                } //if
            } //for inner
            System.out.println(".");
        } //for outer
    }

    /**
     * precalculate all densities for binaryInteractions between various go-nodes.
     * The precalculated data will be stored in an extra table.
     * @param untilDepth depth from root element. E.g. "2" means, every go-node from level 0 (root)
     * to level 2 (including) will be taken into account. If you set the value to any negative value,
     * the max depth of the graph will be used!
     */
    public void precalcDensity(int untilDepth) {
        this.refreshDbConnection();
        if (untilDepth < 0)
            untilDepth = _maxGoIdDepth;

        HashSet goIds = new HashSet();
        for (int depth = untilDepth; depth >= 0; depth--)
            goIds.addAll(_goIdDepth.getObjectByValue(new Integer(depth)));

        try {
            this.precalcDensity(goIds);
        } catch (GoIdNotInDagException e) {
            System.out.println("error: " + e);
        }
    }


    /**
     * For a Set of GoIds the<br>
     * - possible binary Interactions and <br>
     * - all real existing Interactions which (could) occur in or within the two goIds
     * are calculated and encapsulated in GoGoDensities.
     * @param distinctGoIds a Set of goIds in which we are interested in. Each possible
     * go-go-combination (out of the Set) will be covered by this method. <b>IMPORTANT:</b>
     * goIds must be distinct - I use a List for various reasons here - so you have
     * to take care about unique goIds before!!!
     * @return [][] of GoGoDensity, which include each possibe go-go-combination:
     * <p>
     * <pre>
     *  e.g: List is:  a, b, c, d       <br>
     *  2nd dimension                   <br>
     *   --------------------------     <br>
     *   0  a |  aa                     <br>
     *   1  b |  ba   bb                <br>
     *   2  c |  ca   cb   cc           <br>
     *   3  d |  da   db   dc   dd      <br>
     *   -----|---------------------    <br>
     *        |  a    b    c    d       <br>
     *        |  0    1    2    3   1st dimension
     * </pre>
     * </p>
     * @throws GoIdNotInDagException
     */
    public GoGoDensity[][] getBinaryInteractions(List distinctGoIds, boolean onlyPrecalcDens)
            throws GoIdNotInDagException, SameGraphPathException {

        this.refreshDbConnection();

        Set goIdsNotInDag = this.isGoIdInDag(new HashSet(distinctGoIds));
        if (goIdsNotInDag.size() > 0)
            throw new GoIdNotInDagException("goId: " + goIdsNotInDag.toString() + " is not contained in graph");

        Set goIdsInSameGraphPath = this.isInSameGraphPath(new HashSet(distinctGoIds));
        if (goIdsInSameGraphPath.size() > 0)
            throw new SameGraphPathException("go-Ids in same graph path: " + goIdsInSameGraphPath);

        int countCombinations = distinctGoIds.size() - 1;
        GoGoDensity[][] result = new GoGoDensity[countCombinations + 1][countCombinations + 1];

        // iterating over the List of distinctGoIds and generating every possilbe goId-goId combination:
        //  e.g: List is:  a, b, c, d
        //
        //  2nd dimension
        //  --------------------------
        //  0  a |  aa
        //  1  b |  ba   bb
        //  2  c |  ca   cb   cc
        //  3  d |  da   db   dc   dd
        //  -----|---------------------
        //       |  a    b    c    d
        //       |  0    1    2    3   1st dimension

        // if densities were already precalculated by precalcDensity(), the boolean can be set to true

        PreparedStatement densityQuery = null;

        try {
            densityQuery =
                    DbTools.getInstance().getCon().prepareStatement(
                            "SELECT pos_ia, is_ia FROM ia_goDens_density "
                            + "WHERE goid1 = ? AND goid2 = ? "
                            + "OR    goid2 = ? AND goid1 = ?");

            for (int i = 0; i <= countCombinations; i++) {
                String currentGoId1 = (String) distinctGoIds.get(i);

                for (int j = i; j <= countCombinations; j++) {
                    String currentGoId2 = (String) distinctGoIds.get(j);

                    densityQuery.setString(1, currentGoId1);
                    densityQuery.setString(2, currentGoId2);
                    densityQuery.setString(3, currentGoId1);
                    densityQuery.setString(4, currentGoId2);

                    ResultSet resultSet = densityQuery.executeQuery();

                    GoGoDensity bi;
                    if (resultSet.next()) {
                        // data from precalculation
                        int possibleInteractions = resultSet.getInt("pos_ia");
                        int existingInteractions = resultSet.getInt("is_ia");
                        bi = new GoGoDensity(
                                currentGoId1,
                                currentGoId2,
                                possibleInteractions,
                                existingInteractions);
                        logger.debug(currentGoId1 + " " + currentGoId2 + " PRE calculated");
                        result[i][j] = bi;
                        resultSet.close();
                    } else if (!onlyPrecalcDens) {
                        // online calculation if there was not precalculation done ...
                        bi = new GoGoDensity(
                                currentGoId1,
                                currentGoId2,
                                this.countPossibleInteractions(currentGoId1, currentGoId2),
                                this.countExistingInteractions(currentGoId1, currentGoId2));
                        logger.debug(currentGoId1 + " " + currentGoId2 + " LIVE calculated");
                        result[i][j] = bi;
                    } else {
                        // if not precalculated and it is not allowed to calculate LIVE
                        bi = new GoGoDensity(currentGoId1, currentGoId2, -1, 0);
                        logger.debug(currentGoId1 + " " + currentGoId2 + " NOT calculated");
                        result[i][j] = bi;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                densityQuery.close();
            } catch (SQLException e) {
            }
        }
        return result;
    }

    /**
     * @param goId (node) for which all Interactor_acs want to be known.
     * @return a Collection of Interactor ac's (as Strings)
     * which belong to this node (specific goId)
     * @exception KeyNotFoundException if goId was not found in db
     */
    public HashSet getInteractors(String goId) throws KeyNotFoundException {

        this.refreshDbConnection();
        HashSet someInteractors = new HashSet();

        try {
            _go2InteractorQuery.setString(1, goId);

            ResultSet result = _go2InteractorQuery.executeQuery();

            while (result.next()) {
                String interactor = result.getString("interactor");
                someInteractors.add(interactor.trim());
            }

            result.close();
        } catch (SQLException e) {
            throw new KeyNotFoundException("for " + goId + " was not Interactor found!");
        }

        return someInteractors; // Interactor acs as a Collection of Strings
    }

    /**
     * @param goId e.g. "GO:0003673"
     * @return Shortlabel
     */
    public String getGoTermShort(String goId) {
        String goTerm = (String) _goShortlabels.get(goId);
        if (goTerm != null)
            return goTerm;
        else
            return goId;
    }

    /**
     * @param goId
     * @return Fullname of CV
     */
    public String getGoTermFullname(String goId) {
        String goTerm = (String) _goFullname.get(goId);
        if (goTerm != null)
            return goTerm;
        else
            return goId;
    }

    // =======================================================================
    // Private Methods
    // =======================================================================

    /**
     * all goIds will be put to _goIdDepth, including the depth of the goId.
     * If a node can be reached by two different depth, the maximum depth will
     * be choosen, because if we try (later) to find a recursive function for
     * the precalculation, it is important to start processing with the leaves
     * of the graph.
     * @param goId goId
     * @param goIdDetph depth
     */
    private void calcGoIdMaxDepth(String goId, int goIdDetph) {
        if (!_goIdDepth.contains(goId)) {
            _goIdDepth.put(goId, new Integer(goIdDetph));
        } else {
            int depth = ((Integer) _goIdDepth.getObjectByKey(goId)).intValue();
            if (goIdDetph < depth) {
                _goIdDepth.put(goId, new Integer(goIdDetph));
            }
        }
    }

    /**
     * all Shortlabels (of GoIds)  in the graph are processed.
     * Afterwards they are available in _goShortlabes
     * @throws IntactException
     */
    private void calcGoTermShort() throws IntactException {
        IntactHelper helper = new IntactHelper();
        _goShortlabels = new Hashtable();
        Collection cvGoNodes = helper.getObjectsByXref(uk.ac.ebi.intact.model.CvGoNode.class, "*");
        Iterator it = cvGoNodes.iterator();
        while (it.hasNext()) {
            CvGoNode node = (CvGoNode) it.next();
            _goShortlabels.put(node.getGoId(), node.getShortLabel());
        }
    }

    /**
     * all Fullnames (of GoIds) in the graph are processed.
     * Afterwards they are available in _goFullname
     * @throws IntactException
     */
    private void calcGoTermFullname() throws IntactException {
        IntactHelper helper = new IntactHelper();
        _goFullname = new Hashtable();
        Collection cvGoNodes = helper.getObjectsByXref(uk.ac.ebi.intact.model.CvGoNode.class, "*");
        Iterator it = cvGoNodes.iterator();
        while (it.hasNext()) {
            CvGoNode node = (CvGoNode) it.next();
            _goFullname.put(node.getGoId(), node.getFullName());
        }
    }

    /**
     * count the real existing binary interactions
     * @param goId1
     * @param goId2
     * @return the cout of real existing binaryInteractions which are within or within some children
     * of goId1 and goId2 (intersection of common goIds are eliminated!!!). binaryInteractions are
     * only count unique, that means, if two proteins interact between a group of goIds, and interact
     * at another subgraph part again, they will not be counted twice, because it is only one real
     * exisiting interaction. The binary interactions are taken from the binaryinteraction table,
     * which was build by CalcBinInteractionData.java
     * @throws SQLException
     */
    private int countExistingInteractions(String goId1, String goId2) throws SQLException {
        this.refreshDbConnection();
        int result = 0;

        if (goId1.equals(goId2)) {
            _countIA1Node.setString(1, goId1);
            _countIA1Node.setString(2, goId2);
            ResultSet rs = _countIA1Node.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
            rs.close();

        } else {
            _countIA2Nodes.setString(1, goId1);
            _countIA2Nodes.setString(2, goId2);
            _countIA2Nodes.setString(3, goId2);
            _countIA2Nodes.setString(4, goId1);
            ResultSet rs = _countIA2Nodes.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
            rs.close();
        }

        return result;
    }

    /**
     * count the max possible binary interactions between go nodes
     * @param goId1
     * @param goId2
     * @return the maximum possible unique binaryInteractions between go nodes. In general the same
     * as countExistingInteractions(), but it calculates the maximum possible!
     * @see this.countExistingInteractions();
     * @throws SQLException
     */
    private int countPossibleInteractions(String goId1, String goId2) throws SQLException {
        this.refreshDbConnection();
        int result = 0;

        if (goId1.equals(goId2)) {
            _countDistProt1Node.setString(1, goId1);
            ResultSet rs = _countDistProt1Node.executeQuery();
            int ia = 0;
            if (rs.next())
                ia = rs.getInt(1);
            result = (int) (((ia * ia) / 2.0) + (ia / 2.0));
            if (result == Double.NaN) {
                result = 0;
            }

        } else {
            ArrayList interactors1 = new ArrayList();
            _getDistProt2aNodes.setString(1, goId1);
            _getDistProt2aNodes.setString(2, goId2);
            ResultSet rs = _getDistProt2aNodes.executeQuery();
            while (rs.next()) {
                interactors1.add(rs.getString(1));
            }
            rs.close();

            ArrayList interactors2 = new ArrayList();
            _getDistProt2bNodes.setString(1, goId2);
            _getDistProt2bNodes.setString(2, goId1);
            rs = _getDistProt2bNodes.executeQuery();
            while (rs.next()) {
                interactors2.add(rs.getString(1));
            }
            rs.close();

            // which Interactors are in both Sets?
            Collection equalInteractors = CollectionUtils.intersection(interactors1, interactors2);
            int ia1 = interactors1.size();
            int ia2 = interactors2.size();
            int iaEqual = equalInteractors.size();
            int toMuch =
                    (int) ((iaEqual * iaEqual) - (((iaEqual * iaEqual) / 2.0) + (iaEqual / 2.0)));
            result = (ia1 * ia2) - toMuch;

            if (result == Double.NaN)
                result = 0;
        }

        return result;
    }

    /**
     * recursive method for calculating all childs of a goterm
     * @param goId
     */
    private void calcAllChilds(String goId) {
        if (this.containsKey(goId) && !_isAlreadyVisited.contains(goId)) {
            _isAlreadyVisited.add(goId);

            Collection someChilds = new ArrayList();

            try {
                someChilds = this.getValueByKey(goId);
            } catch (KeyNotFoundException e) {
            }

            Iterator itChilds = someChilds.iterator();

            while (itChilds.hasNext()) {
                String child = (String) itChilds.next();
                _allChildsOfaNode.add(child);

                // recursive call to explore all children
                calcAllChilds(child);
            }
        }
    }

    private void setupStatements() throws SQLException {
        _countIA1Node =
                DbTools.getInstance().getCon().prepareStatement(
                        "SELECT count(*) FROM (SELECT DISTINCT bait, prey                       "
                        + "FROM ia_goDens_binary                                                "
                        + "     ,(select child from ia_goDens_godagdenorm where parent = ?      "
                        + "     )AS t1                                                          "
                        + "     ,(select child from ia_goDens_godagdenorm where parent = ?      "
                        + "     )AS t2                                                          "
                        + "WHERE (t1.child = ia_goDens_binary.gobait AND                        "
                        + "       t2.child = ia_goDens_binary.goprey)                           "
                        + ") AS foo                                                             ");

        if (Config.DB.equals("POSTGRESQL")) {
            _countIA2Nodes =
                    DbTools.getInstance().getCon().prepareStatement(
                            "SELECT count(*) FROM (SELECT DISTINCT bait, prey                       "
                            + "FROM ia_goDens_binary                                                "
                            + "     ,(select child from ia_goDens_godagdenorm where parent = ?      "
                            + "        EXCEPT                                                       "
                            + "        select child from ia_goDens_godagdenorm where parent = ?     "
                            + "     )AS t1                                                          "
                            + "     ,(select child from ia_goDens_godagdenorm where parent = ?      "
                            + "        EXCEPT                                                       "
                            + "        select child from ia_goDens_godagdenorm where parent = ?     "
                            + "     )AS t2                                                          "
                            + "WHERE (t1.child = ia_goDens_binary.gobait AND                        "
                            + "       t2.child = ia_goDens_binary.goprey)                           "
                            + "OR    (t1.child = ia_goDens_binary.goprey AND                        "
                            + "       t2.child = ia_goDens_binary.gobait)                           "
                            + ") AS foo                                                             ");
        }

        if (Config.DB.equals("ORACLE")) {
            _countIA2Nodes =
                    DbTools.getInstance().getCon().prepareStatement(
                            "SELECT count(*) FROM (SELECT DISTINCT bait, prey                       "
                            + "FROM ia_goDens_binary                                                "
                            + "     ,(select child from ia_goDens_godagdenorm where parent = ?      "
                            + "        MINUS                                                        "
                            + "        select child from ia_goDens_godagdenorm where parent = ?     "
                            + "     )AS t1                                                          "
                            + "     ,(select child from ia_goDens_godagdenorm where parent = ?      "
                            + "        MINUS                                                        "
                            + "        select child from ia_goDens_godagdenorm where parent = ?     "
                            + "     )AS t2                                                          "
                            + "WHERE (t1.child = ia_goDens_binary.gobait AND                        "
                            + "       t2.child = ia_goDens_binary.goprey)                           "
                            + "OR    (t1.child = ia_goDens_binary.goprey AND                        "
                            + "       t2.child = ia_goDens_binary.gobait)                           "
                            + ") AS foo                                                             ");
        }

        _countDistProt1Node =
                DbTools.getInstance().getCon().prepareStatement(
                        "SELECT count(*) FROM (SELECT DISTINCT interactor                       "
                        + "FROM ia_goDens_goprot                                                "
                        + "     ,(select child from ia_goDens_godagdenorm where parent = ?      "
                        + "      )AS t1                                                         "
                        + "WHERE t1.child = ia_goDens_goprot.goid                               "
                        + ") as foo                                                             ");

        _getDistProt2aNodes =
                DbTools.getInstance().getCon().prepareStatement(
                        "SELECT DISTINCT interactor                                             "
                        + "FROM ia_goDens_goprot                                                "
                        + "     ,(select child from ia_goDens_godagdenorm where parent = ?      "
                        + "       EXCEPT                                                        "
                        + "       select child from ia_goDens_godagdenorm where parent = ?      "
                        + "      )AS t1                                                         "
                        + "WHERE t1.child = ia_goDens_goprot.goid                               ");

        _getDistProt2bNodes =
                DbTools.getInstance().getCon().prepareStatement(
                        "SELECT DISTINCT interactor                                             "
                        + "FROM ia_goDens_goprot                                                "
                        + "     ,(select child from ia_goDens_godagdenorm where parent = ?      "
                        + "       EXCEPT                                                        "
                        + "       select child from ia_goDens_godagdenorm where parent = ?      "
                        + "      )AS t1                                                         "
                        + "WHERE t1.child = ia_goDens_goprot.goid                               ");

        _densityWrite =
                DbTools.getInstance().getCon().prepareStatement(
                        "INSERT INTO ia_goDens_density VALUES (?, ?, ?, ?)                      ");

        _go2InteractorQuery =
                DbTools.getInstance().getCon().prepareStatement(
                        "SELECT interactor FROM ia_goDens_goProt WHERE goid = ?                 ");
    }
}

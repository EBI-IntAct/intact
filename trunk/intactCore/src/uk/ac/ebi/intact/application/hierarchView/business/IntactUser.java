/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Constants;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.persistence.DAOFactory;
import uk.ac.ebi.intact.persistence.DAOSource;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.persistence.SearchException;
import uk.ac.ebi.intact.simpleGraph.Graph;
import uk.ac.ebi.intact.application.hierarchView.business.graph.InteractionNetwork;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

import org.apache.log4j.Logger;

/**
 * This class stores information about an Intact Web user session. Instead of
 * binding multiple objects, only an object of this class is bound to a session,
 * thus serving a single access point for multiple information.
 * <p>
 * This class implements the <tt>HttpSessionBindingListener</tt> interface for it
 * can be notified of session time outs.
 *
 * @author Originaly : Chris Lewington, Sugath Mudali (smudali@ebi.ac.uk)
 *         Modified by Samuel Kerrien (skerrien@ebi.ac.uk)
 */
public class IntactUser implements HttpSessionBindingListener {

    // LOGGER
    static Logger logger = Logger.getLogger(uk.ac.ebi.intact.application.hierarchView.business.Constants.LOGGER_NAME);

    private IntactHelper intactHelper;



    /**
     * Constructs an instance of this class with given mapping file and
     * the name of the data source class.
     *
     * @param mapping the name of the mapping file.
     * @param dsClass the class name of the Data Source.
     *
     * @exception DataSourceException for error in getting the data source; this
     *  could be due to the errors in repository files or the underlying
     *  persistent mechanism rejected <code>user</code> and
     *  <code>password</code> combination.
     * @exception IntactException thrown for any error in creating lists such
     *  as topics, database names etc.
     */
    public IntactUser (String mapping, String dsClass)
        throws DataSourceException, IntactException {
        DAOSource ds = DAOFactory.getDAOSource(dsClass);

        // Pass config details to data source - don't need fast keys as only
        // accessed once
        Map fileMap = new HashMap();
        fileMap.put (Constants.MAPPING_FILE_KEY, mapping);
        ds.setConfig (fileMap);

        // build a helper for use throughout a session
        this.intactHelper = new IntactHelper (ds);
        logger.info ("IntactHelper created.");
    }


    /**
     * Allows the user to retreive a collection of matching IntAct object
     * according to a criteria given in parameter.
     *
     * @param objectType  object type you want to retreive
     * @param searchParam the field you want to query on
     * @param searchValue the value you are looking for
     * @return a collection of <i>objectType</i> object
     * @throws SearchException in case the search fail
     */
    public Collection search (String objectType,
                              String searchParam,
                              String searchValue) throws SearchException {

        //now retrieve an object collection
        try {
            return intactHelper.search(objectType, searchParam, searchValue);
        }
        catch (IntactException ie) {
            throw new SearchException("Search Failed: " + ie.getNestedMessage());
        }
    } // search


    /**
     * Returns a subgraph centered on startNode.
     * The subgraph will contain all nodes which are up to graphDepth interactions away from startNode.
     * Only Interactions which belong to one of the Experiments in experiments will be taken into account.
     * If experiments is empty, all Interactions are taken into account.
     *
     * Graph depth:
     * This parameter limits the size of the returned interaction graph. All baits are shown with all
     * the interacting preys, even if they would normally be on the "rim" of the graph.
     * Therefore the actual diameter of the graph may be 2*(graphDepth+1).
     *
     * Expansion:
     * If an Interaction has more than two interactors, it has to be defined how pairwise interactions
     * are generated from the complex data. The possible values are defined in the beginning of this file.
     *
     * @param startNode - the start node of the subgraph.
     * @param graphDepth - depth of the graph
     * @param experiments - Experiments which should be taken into account
     * @param complexExpansion - Mode of expansion of complexes into pairwise interactions
     *
     * @return a InteractionNetwork object.
     *
     * @exception IntactException - thrown if problems are encountered
     */
    public InteractionNetwork subGraph (Interactor startNode,
                                        int graphDepth,
                                        Collection experiments,
                                        int complexExpansion) throws IntactException {

        logger.info("Starting graph generation (" + startNode.getAc() + ", depth=" + graphDepth);
        InteractionNetwork in = new InteractionNetwork ();
        Graph graph = in;

        graph = intactHelper.subGraph (startNode,
                                       graphDepth,
                                       experiments,
                                       complexExpansion,
			                           graph);

        logger.info("Graph generation complete\n" + graph);

        return (InteractionNetwork) graph;
    } // subGraph

    // Implements HttpSessionBindingListener

    /**
     * Will call this method when an object is bound to a session.
     * Not doing anything.
     */
    public void valueBound (HttpSessionBindingEvent event) {
    }

    /**
     * Will call this method when an object is unbound from a session.
     */
    public void valueUnbound (HttpSessionBindingEvent event) {
        try {
            this.intactHelper.closeStore();
            this.intactHelper = null;
            logger.info ("IntactHelper datasource closed (cause: removing attribute from session)");
        }
        catch(IntactException ie) {
            //failed to close the store - not sure what to do here yet....
            logger.error ("error when closing the IntactHelper store", ie);
        }
    }

} // IntactUser

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
import uk.ac.ebi.intact.simpleGraph.Graph;
import uk.ac.ebi.intact.application.hierarchView.business.graph.InteractionNetwork;
import uk.ac.ebi.intact.application.hierarchView.business.image.ImageBean;

import javax.servlet.http.HttpSessionBindingEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

import org.apache.log4j.Logger;

/**
 * This class stores information about an Intact Web user session. <br>
 * Instead of binding multiple objects, only an object of this class is bound to a session,
 * thus serving a single access point for multiple information.
 * <p>
 * This class implements the <tt>HttpSessionBindingListener</tt> interface for it
 * can be notified of session time outs.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */
public class IntactUser implements IntactUserI {

    static Logger logger = Logger.getLogger(uk.ac.ebi.intact.application.hierarchView.business.Constants.LOGGER_NAME);

    /**
     * datasource entry point
     */
    private IntactHelper intactHelper;

    /**
     * The current network with which the user is working
     */
    private InteractionNetwork interactionNetwork;

    /**
     * Set of highlight options (key/value) given by the user via the web interface
     */
    private Map highlightOptions;

    /**
     * data relatives to the image producing (Image byte code, SVG DOM, HTML MAP)
     */
    private ImageBean imageBean;

    /**
     * Collection of keys received from the current source.
     * The highlightment of the current interaction network is done according to these keys.
     */
    private Collection keys;

    // User's form fields
    private String AC;
    private String depth;
    private boolean hasNoDepthLimit;
    private String methodLabel;
    private String methodClass;
    private String behaviour;


    public String getAC() {
        return (this.AC);
    }

    public String getDepth() {
        return (this.depth);
    }

    public boolean getHasNoDepthLimit() {
        return (this.hasNoDepthLimit);
    }

    public String getMethodLabel() {
        return (this.methodLabel);
    }

    public String getMethodClass() {
        return (this.methodClass);
    }

    public String getBehaviour() {
        return behaviour;
    }

    public InteractionNetwork getInteractionNetwork() {
        return interactionNetwork;
    }
    public ImageBean getImageBean() {
        return imageBean;
    }

    public Collection getKeys() {
        return keys;
    }

    public IntactHelper getHelper () {
        return intactHelper;
    }



    public void setAC(String AC) {
        this.AC = AC;
    }

    public void setDepth (String depth) {
        this.depth = depth;
    }

    public void setHasNoDepthLimit (boolean flag) {
        this.hasNoDepthLimit = flag;
    }

    public void setMethodLabel (String methodLabel) {
        this.methodLabel = methodLabel;
    }

    public void setMethodClass (String methodClass) {
        this.methodClass = methodClass;
    }

    public void setBehaviour(String behaviour) {
        this.behaviour = behaviour;
    }

    public void setInteractionNetwork(InteractionNetwork in) {
        this.interactionNetwork = in;
    }

    public void setImageBean(ImageBean imageBean) {
        this.imageBean = imageBean;
    }

    public void setKeys(Collection keys) {
        this.keys = keys;
    }


    /**
     * Constructs an instance of this class with given mapping file and
     * the name of the data source class.
     *
     * @param repositoryfile the name of the mapping file.
     * @param datasourceClass the class name of the Data Source.
     *
     * @exception DataSourceException for error in getting the data source; this
     *  could be due to the errors in repository files or the underlying
     *  persistent mechanism rejected <code>user</code> and
     *  <code>password</code> combination.
     * @exception IntactException thrown for any error in creating lists such
     *  as topics, database names etc.
     */
    public IntactUser (String repositoryfile, String datasourceClass)
            throws DataSourceException, IntactException {

        init ();

        DAOSource dataSource = DAOFactory.getDAOSource (datasourceClass);

        // Pass config details to data source - don't need fast keys as only accessed once
        Map fileMap = new HashMap();
        fileMap.put (Constants.MAPPING_FILE_KEY, repositoryfile);
        dataSource.setConfig (fileMap);

        // build a helper for use throughout a session
        this.intactHelper = new IntactHelper (dataSource);
        logger.info ("IntactHelper created.");
    }

    /**
     * Set the default value of user's data
     */
    public void init () {

        this.AC = null;
        this.depth = null;
        this.hasNoDepthLimit = false;
        this.methodLabel = null;
        methodClass = null;
        behaviour = null;

        interactionNetwork = null;
        imageBean = null;
        keys = null;
        highlightOptions = new HashMap();
    }


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

        logger.info("Starting graph generation (" + startNode.getAc() + ", depth=" + graphDepth + ")");
        InteractionNetwork in = new InteractionNetwork (startNode.getAc());
        Graph graph = in;

        graph = intactHelper.subGraph (startNode,
                                       graphDepth,
                                       experiments,
                                       complexExpansion,
			                           graph);

        logger.info("Graph generation complete\n" + graph);

        return (InteractionNetwork) graph;
    } // subGraph



    public void resetHighlightOptions () {
          highlightOptions.clear();
    }

    public void addHighlightOption (String name, Object value) {
           highlightOptions.put(name, value);
    }

    public Object getHighlightOption (String name) {
        return highlightOptions.get (name);
    }


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
            logger.info ("IntactHelper datasource closed (cause: removing attribute from session)");
        }
        catch(IntactException ie) {
            //failed to close the store - not sure what to do here yet....
            logger.error ("error when closing the IntactHelper store", ie);
        }
    }

} // IntactUser

/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.graph;

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchView.exception.MultipleResultException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.persistence.SearchException;

import java.util.ArrayList;



/**
 * Allows to retreive an interraction network from intact API
 *
 * @author Samuel Kerrien
 * @version $Id$
 */
public class GraphHelper  {

    static transient Logger logger = Logger.getLogger (Constants.LOGGER_NAME);


    /**
     * Datasource
     */
    private IntactUserI user;


    /**
     * basic constructor - sets up (hard-coded) data source and an intact helper.
     */
    public GraphHelper (IntactUserI intactUser) {
        user = intactUser;
    } // GraphHelper

    /**
     * Create or extend an interaction network by using the given Interactor as
     * a central node. If a network is already existing, we fusion them.
     * @param in an interaction network (can be null).
     * @param interactor the Interactor from which we add an interaction network.
     * @param depth The level of BAIT-BAIT interaction in the interaction graph.
     * @return the corresponding interaciton network
     */
    public InteractionNetwork addInteractionNetwork( InteractionNetwork in, Interactor interactor, int depth )
            throws SearchException,
                   IntactException,
                   MultipleResultException  {

        InteractionNetwork tmp = null;

        if( in != null ) {
            /*
             * In the case that interactor is already registered as central in
             * the current network, no need to retrieve the network, just send
             * back the current one.
             */
            ArrayList interactors = in.getCentralInteractors();
            if ( interactors.contains( interactor ) )
                return in;

            /*
             * That interactor is not yet in the current interaction network,
             * so retreive that network and fusion them.
             */
            tmp = in;
        }

        in = new InteractionNetwork( interactor );

        in = this.user.subGraph (in,
                                 depth,
                                 null,
                                 uk.ac.ebi.intact.model.Constants.EXPANSION_BAITPREY);

        if (tmp != null) {
            logger.info ("Fusion interaction network");
            tmp.fusion( in );
            in = tmp;
            tmp = null; // must be reset 'cos we could chexk it in the next iteration.
        }

        return in;
    } // addInteractionNetwork

//    /**
//     * update the interaction in given in parameter, by reloading the whole set of
//     * entry point with the given depth.
//     *
//     * @param in the interaction in to update
//     * @param depth the depth to update to.
//     * @return the new interaction in.
//     * @throws IntactException when the graph generation is not going right.
//     */
//    public InteractionNetwork updateInteractionNetwork (InteractionNetwork in, int depth)
//            throws IntactException {
//
//        // Retreiving interactor from the database according to the given AC
//        logger.info( "Updating interaction network ..." );
//        InteractionNetwork newNetwork = null;
//        Collection centrals = in.getCentralInteractors();
//
//        for ( Iterator iterator = centrals.iterator (); iterator.hasNext (); ) {
//            Interactor interactor = (Interactor) iterator.next ();
//            InteractionNetwork tmp = new InteractionNetwork( interactor );
//
//            tmp = this.user.subGraph (tmp,
//                                      depth, // new depth
//                                      null,
//                                      uk.ac.ebi.intact.model.Constants.EXPANSION_BAITPREY);
//            if (newNetwork == null) {
//                // first in
//                newNetwork = tmp;
//            } else {
//                logger.info( "Fusion interaction in ..." );
//                newNetwork.fusion( tmp );
//            }
//
//            // add the central node to the new in
//            // TODO check if that's not done by the fusion
//            // TODO: this adds nodes to the same networks they come from ????!!!!!
//            HashMap nodes = newNetwork.getNodes();
//            Node aNode = (Node) nodes.get( interactor.getAc() );
//            newNetwork.addCentralProtein( aNode );
//        } // for each central proteins
//
//        // to finish, copy criterias
//        // TODO check if that's not done by the fusion
//        Collection criteria = in.getCriteria();
//        for ( Iterator iterator = criteria.iterator (); iterator.hasNext (); ) {
//            CriteriaBean criteriaBean = (CriteriaBean) iterator.next ();
//            newNetwork.addCriteria( criteriaBean);
//        }
//
//        return newNetwork;
//    }
}
/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */

package uk.ac.ebi.intact.application.hierarchview.highlightment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.application.hierarchview.business.Constants;
import uk.ac.ebi.intact.application.hierarchview.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchview.business.graph.Network;
import uk.ac.ebi.intact.application.hierarchview.business.image.DrawGraph;
import uk.ac.ebi.intact.application.hierarchview.business.image.ImageBean;
import uk.ac.ebi.intact.application.hierarchview.highlightment.behaviour.HighlightmentBehaviour;
import uk.ac.ebi.intact.application.hierarchview.highlightment.source.node.NodeHighlightmentSource;
import uk.ac.ebi.intact.service.graph.Node;

import javax.servlet.http.HttpSession;
import java.util.Collection;

/**
 * Allows to perfoem the highlightment according to data stored in the user
 * session
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk) & Alexandre Liban (aliban@ebi.ac.uk)
 * @version $Id$
 */

public class HighlightProteins {

    private static final Log logger = LogFactory.getLog( HighlightProteins.class );

    /**
     * Constructor Allow to modify the current graph to highlight a part of this.
     *
     * @param source         The highlighting source
     * @param behaviourClass The highlighting behaviour class name
     * @param session        The current session
     * @param in             The interaction network
     */
    public static void perform( NodeHighlightmentSource source, String behaviourClass, HttpSession session, Network in ) {
        /*
         * Put the default color and default visibility in the interaction
         * network before to highlight this one.
         */
        in.initNodes();
        in.initEdges();

        IntactUserI user = (IntactUserI) session.getAttribute(Constants.USER_KEY);

        // Search the protein to highlight
        Collection<Node> proteinsToHighlight = source.proteinToHightlight( user, in );

        // Interaction network's modification
        HighlightmentBehaviour highlightmentBehaviour;
        highlightmentBehaviour = HighlightmentBehaviour.getHighlightmentBehaviour( behaviourClass );

        // apply the highlight to the selected set of protein
        logger.info( "Proteins collection to be highlighted : " + proteinsToHighlight );
        highlightmentBehaviour.apply( proteinsToHighlight, in );

        // store data in the session
        // IntactUserI user = (IntactUserI) session.get( Constants.USER_KEY );
        if ( user == null ) {
            logger.info( "USER is null, exit the highlight process" );
            return;
        }

        String applicationPath = user.getApplicationPath();

        // Rebuild Image data
        //        GraphToSVG svgProducer = new GraphToSVG (in);
        DrawGraph imageProducer = new DrawGraph( user, in, applicationPath, user.getMinePath() );
        imageProducer.draw();
        ImageBean ib = imageProducer.getImageBean();

        // TODO : test is user OK
        user.setImageBean( null );
        user.setImageBean( ib );

        // TODO: needed ?! have to be tested !
        user.setInteractionNetwork( in );

    }
}
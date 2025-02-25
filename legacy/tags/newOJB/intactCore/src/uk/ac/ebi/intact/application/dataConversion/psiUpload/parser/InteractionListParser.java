/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.parser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.InteractionTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.DOMUtil;

import java.util.ArrayList;
import java.util.Collection;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionListParser {

    private Collection interactions = new ArrayList();

    private ExperimentListParser experimentList;
    private ParticipantListParser participantList;

    ////////////////////////
    // Constructor
    ////////////////////////
    public InteractionListParser( final ExperimentListParser experimentList, final ParticipantListParser participantList ) {
        this.experimentList = experimentList;
        this.participantList = participantList;
    }

    ///////////////////////
    // Getter
    ///////////////////////
    public Collection getInteractions() {
        return interactions;
    }


    /**
     * @param entry
     */
    public Collection process( final Element entry ) {

        final Element interactionList = DOMUtil.getFirstElement( entry, "interactionList" );
        final NodeList someInteractions = interactionList.getElementsByTagName( "interaction" );
        final int count = someInteractions.getLength();

        final Collection interactions = new ArrayList( count );

        for ( int i = 0; i < count; i++ ) {
            final Element interactionNode = (Element) someInteractions.item( i );

            final InteractionParser interaction = new InteractionParser( experimentList, participantList,
                                                                         interactionNode );
            InteractionTag interactionTag = interaction.process();
            if( interactionTag != null ) {
                interactions.add( interactionTag );
            }
        } // interactions

        return interactions;
    }
}

// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.psi1;

import org.w3c.dom.Element;
import org.w3c.dom.Text;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.UserSessionDownload;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Component2xmlI;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Feature2xmlFactory;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Protein2xmlFactory;
import uk.ac.ebi.intact.model.*;

import java.util.*;

/**
 * Implements the tranformation of an IntAct Component into PSI XML.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class Component2xmlPSI1 implements Component2xmlI {

    //////////////////////////////////////
    // Constants

    public static final String UNSPECIFIED = "unspecified";
    public static final String NEUTRAL = "neutral";
    public static final String BAIT = "bait";
    public static final String PREY = "prey";

    // Holds the label of the role allowed.
    private static Set roleAllowed = new HashSet( 4 );

    private static Map roleNameRemapping = new HashMap( 4 );

    public static final String DEFAULT_ROLE = UNSPECIFIED;

    static {

        roleAllowed.add( UNSPECIFIED );
        roleAllowed.add( NEUTRAL );
        roleAllowed.add( BAIT );
        roleAllowed.add( PREY );

        roleNameRemapping.put( CvComponentRole.ENZYME, BAIT );
        roleNameRemapping.put( CvComponentRole.ENZYME_TARGET, PREY );
        roleNameRemapping.put( CvComponentRole.NEUTRAL, NEUTRAL );
    }

    //////////////////////////////////////
    // Singleton's attribute and methods

    private static Component2xmlPSI1 ourInstance = new Component2xmlPSI1();

    public static Component2xmlPSI1 getInstance() {
        return ourInstance;
    }

    private Component2xmlPSI1() {
    }

    /////////////////////
    // Public methods

    /**
     * Generated an proteinParticipant out of an IntAct Component.
     *
     * @param session
     * @param parent    the Element to which we will add the proteinParticipant.
     * @param component the IntAct Component that we convert to PSI.
     *
     * @return the generated proteinParticipant Element.
     */
    public Element create( UserSessionDownload session, Element parent, Component component ) {

        // 1. Checking...
        if ( session == null ) {
            throw new IllegalArgumentException( "You must give a non null UserSessionDownload." );
        }

        if ( parent == null ) {
            throw new IllegalArgumentException( "You must give a non null parent to build an " + PROTEIN_PARTICIPANT_TAG_NAME + "." );
        } else {

            if ( !PARENT_TAG_NAME.equals( parent.getNodeName() ) ) {
                throw new IllegalArgumentException( "You must give a <" + PARENT_TAG_NAME + "> to build a " + PROTEIN_PARTICIPANT_TAG_NAME + "." );
            }
        }

        if ( component == null ) {
            throw new IllegalArgumentException( "You must give a non null Interaction to build an " + PROTEIN_PARTICIPANT_TAG_NAME + "." );
        }

        Interactor interactor = component.getInteractor();
        if ( false == ( interactor instanceof Protein ) ) {
            throw new UnsupportedOperationException( "Cannot export " + interactor.getClass().getName() +
                                                     " in PSI version 1." );
        }

        // NOTE: proteinInteractorRef proteinInteractor featureList confidence role isTaggedProtein isOverexpressedProtein

        // 2. Initialising the element...
        Element element = session.createElement( PROTEIN_PARTICIPANT_TAG_NAME );

        // 3. Generating proteinInteractorRef and proteinInteractor...
        Protein protein = (Protein) component.getInteractor();
        if ( false == session.isAlreadyDefined( protein ) ) {

            // get the global list of proteins
            Element interactorList = session.getInteractorListElement();

            // add the protein definition to the global list of proteins
            Protein2xmlFactory.getInstance( session ).create( session, interactorList, protein );
            session.declareAlreadyDefined( protein );
        }
        // add an proteinInteractorRef
        Protein2xmlFactory.getInstance( session ).createProteinInteracorReference( session, element, protein );

        // 4. Generating featureList...
        if ( false == component.getBindingDomains().isEmpty() ) {
            Element featureListElement = session.createElement( "featureList" );

            for ( Iterator iterator = component.getBindingDomains().iterator(); iterator.hasNext(); ) {
                Feature feature = (Feature) iterator.next();

                Feature2xmlFactory.getInstance( session ).create( session, featureListElement, feature );
            }
        }

        // 5. Generating confidence...
        // not for now ...

        // 6. Generating role...
        Element role = session.createElement( "role" );
        // only unspecified, neutal, bait and prey are allowed here.
        String theRole = component.getCvComponentRole().getShortLabel();
        if ( false == roleAllowed.contains( theRole ) ) {

            // check if we have a mapping for it.
            if ( roleNameRemapping.containsKey( theRole ) ) {

                String newRole = (String) roleNameRemapping.get( theRole );

                // add a message
                session.addMessage( "NOTE: CvComponentRole( '" + theRole + "' ) has been renamed '" + newRole + "'." +
                                    "(Component: " + component.getAc() + " - " +
                                    "Interaction: " + component.getInteraction().getAc() + ")" );
                theRole = newRole;

            } else {

                String newRole = DEFAULT_ROLE;

                // add message
                session.addMessage( "NOTE: CvComponentRole( '" + theRole + "' ) is nont allowed in PSI 1, it has been " +
                                    "set to the default '" + newRole + "'.(Component: " + component.getAc() + " - " +
                                    "Interaction: " + component.getInteraction().getAc() + ")" );
                theRole = newRole;
            }
        }

        Text roleText = session.createTextNode( theRole );
        role.appendChild( roleText );
        element.appendChild( role );

        // 7. Generating isTaggedProtein...
        // ???

        // 8. Generating isOverexpressedProtein...
        // ???

        // 9. Attaching the newly created element to the parent...
        parent.appendChild( element );

        return element;
    }
}
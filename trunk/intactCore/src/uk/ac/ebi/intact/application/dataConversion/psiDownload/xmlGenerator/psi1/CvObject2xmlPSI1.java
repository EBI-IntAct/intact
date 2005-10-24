// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.psi1;

import org.w3c.dom.Element;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.UserSessionDownload;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Cv2Source;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.CvObject2xmlCommons;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.CvObject2xmlI;
import uk.ac.ebi.intact.model.CvObject;

import java.util.Map;

/**
 * Process the common behaviour of an IntAct CvObject when exporting PSI version 1.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class CvObject2xmlPSI1 extends AnnotatedObject2xmlPSI1 implements CvObject2xmlI {

    //////////////////////////////////////
    // Singleton's attribute and methods

    private static CvObject2xmlPSI1 ourInstance = new CvObject2xmlPSI1();

    public static CvObject2xmlPSI1 getInstance() {
        return ourInstance;
    }

    private CvObject2xmlPSI1() {
    }

    ////////////////////////////////////
    // Cache management (encapsulated)

    /**
     * Checks if the given CvObject has already been generated as XML content. <br> If so, that content is cloned which
     * is faster than recreating it.
     *
     * @param cvObject the CvObject we want to check.
     *
     * @return the XML representation (as the root of a DOM tree) of the given CvObject, or null if it hasn't been
     *         generated  yet.
     */
    private Element getXmlFromCache( UserSessionDownload session, Element parent, CvObject cvObject ) {

        Map cache = session.getCvObjectCache();
        return CvObject2xmlCommons.getInstance().getXmlFromCache( cache, new Cv2Source( cvObject, parent.getNodeName() ) );
    }

    /**
     * Store in the cache the XML representation related to the given CvObject instance.
     *
     * @param session  the user session in which the cache is stored.
     * @param cvObject the cvObject we wanted to comvert to XML.
     * @param element  The DOM root (as an Element) of the XML representation of the given CvObject.
     */
    private void updateCache( UserSessionDownload session, Element parent, CvObject cvObject, Element element ) {

        Map cache = session.getCvObjectCache();
        CvObject2xmlCommons.getInstance().updateCache( cache, new Cv2Source( cvObject, parent.getNodeName() ), element );
    }

    ////////////////////////////
    // Encapsulated mehods

    /**
     * Create PSI Xrefs from an IntAct cvObject. <br> Put Xref(psi-mi, identity) as primaryRef, any other as
     * secondaryRef. <br> If not psi-mi available, take randomly an other one.
     *
     * @param session
     * @param parent   the DOM element to which we will add the newly generated PSI Xref.
     * @param cvObject the cvObject from which we get the Xref to generate.
     */
    private void createCvObjectXrefs( UserSessionDownload session, Element parent, CvObject cvObject ) {

        // if the user requested a mapping of the CVs to be applied, then do it here.
        if ( session.hasCvMapping() ) {
            CvObject toCvObject = session.getReverseCvMapping().getPSI2toPSI1( cvObject );
            if ( toCvObject != null && toCvObject != cvObject ) {
                System.out.println( "NOTE: '" + cvObject.getShortLabel() + "' remapped to '" + toCvObject.getShortLabel() + "'." );
                cvObject = toCvObject;
            }
        }

        CvObject2xmlCommons.getInstance().createCvObjectXrefs( session, parent, cvObject );
    }

    ///////////////////////////
    // Public methods

    /**
     * Generic call that generates the XML representation of the given CvObject.
     *
     * @param session
     * @param parent   the parent to which we wil attach the generated XML document.
     * @param cvObject the CvObject of which we generate the XML representation.
     *
     * @return the XML representation of the given CvObject.
     */
    public Element create( UserSessionDownload session, Element parent, CvObject cvObject ) {

        // checking...
        if ( session == null ) {
            throw new IllegalArgumentException( "You must give a non null UserSessionDownload." );
        }

        if ( parent == null ) {
            throw new IllegalArgumentException( "You must give a non null parent Element." );
        }

        if ( cvObject == null ) {
            throw new IllegalArgumentException( "You must give a non null cvObject." );
        }

        Element element = getXmlFromCache( session, parent, cvObject );

        if ( element == null ) {

            // get the tag name corresponding to the given instance of CvObject.
            String tagName = CvObject2xmlCommons.getInstance().getNodeName( session, parent, cvObject.getClass() );

            if ( tagName == null ) {
                throw new IllegalArgumentException( "The CvObject type: " + cvObject.getClass() + " is not supported." );
            }

            // creating the root element...
            element = session.createElement( tagName );

            // generating names...
            createNames( session, element, cvObject );

            // generating xrefs...
            Element xrefElement = session.createElement( "xref" );

            createCvObjectXrefs( session, xrefElement, cvObject );

            if ( xrefElement.hasChildNodes() ) {
                element.appendChild( xrefElement );
            }

            // updating the cache
            updateCache( session, parent, cvObject, element );
        }

        // attaching the element to the given parent...
        parent.appendChild( element );

        return element;
    }
}
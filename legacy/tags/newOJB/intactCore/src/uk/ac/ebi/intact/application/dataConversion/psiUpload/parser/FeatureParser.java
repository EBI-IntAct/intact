/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.parser;

import org.w3c.dom.Element;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.Message;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.MessageHolder;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Feature;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class FeatureParser {

    public Feature process( Element element ) throws IntactException {
        Feature feature = null;

//        System.out.println( "Working on " + element.getNodeName() );
        if( false == "feature".equals( element.getNodeName() ) ) {
            MessageHolder.getInstance().addParserMessage( new Message( element, "ERROR - We should be in feature tag." ) );
        }

//        Institution owner = null;
//        try {
//            owner = IntactHelperAccessor.getInstance().getOwner();
//        } catch ( UpdateProteinsI.UpdateException e ) {
//            e.printStackTrace();
//        }
//
//        // OPTIONAL - get xref
//        Element xrefElement = DOMUtil.getFirstElement( (Element) element, "xref" );
//        XrefTag xref = XrefParser.processPrimaryRef( xrefElement );
//
//
//        // OPTIONAL - get feature description
//        Element descriptionElement = DOMUtil.getFirstElement( (Element) element, "featureDescription" );
//        XrefTag descriptionXref = null;
//        CvFeatureType cvFeatureType = null;
//        if( descriptionElement != null ) {
//            Element descriptionNameElement = DOMUtil.getFirstElement( descriptionElement, "names" );
//            String descriptionShortLabel = DOMUtil.getShortLabel( descriptionNameElement );
//            String descriptionFullName = DOMUtil.getFullName( descriptionNameElement );
//
//            Element descriptionXrefElement = DOMUtil.getFirstElement( descriptionElement, "xref" );
//            descriptionXref = XrefParser.processPrimaryRef( descriptionXrefElement );
//            cvFeatureType = new CvFeatureType( owner, descriptionShortLabel );
//            cvFeatureType.setFullName( descriptionFullName );
//            // TODO: find out where to put that xref
//
//            System.out.println( "xref:" + xref );
//            System.out.println( "descriptionShortLabel:" + descriptionShortLabel );
//            System.out.println( "descriptionFullName:" + descriptionFullName );
//        }
//
//
//        // OPTIONAL - get feature detection
//        Element detectionElement = DOMUtil.getFirstElement( (Element) element, "featureDetection" );
//        CvFeatureIdentification cvFeatureIdentification = null;
//        XrefTag detectionXref = null;
//        if( detectionElement != null ) {
//            Element detectionNameElement = DOMUtil.getFirstElement( descriptionElement, "names" );
//            String detectionShortLabel = DOMUtil.getShortLabel( descriptionElement );
//            String detectionFullName = DOMUtil.getFullName( descriptionElement );
//            cvFeatureIdentification = new CvFeatureIdentification( owner, detectionShortLabel );
//            cvFeatureIdentification.setFullName( detectionFullName );
//
//            Element detectionXrefElement = DOMUtil.getFirstElement( detectionElement, "xref" );
//            detectionXref = XrefParser.processPrimaryRef( detectionXrefElement );
//            // TODO: find out where to put that xref
//
//            System.out.println( "detection:" );
//            System.out.println( "\tShortLabel:" + detectionShortLabel );
//            System.out.println( "\tFullName:" + detectionFullName );
//            System.out.println( "\txref:" + detectionXref );
//        }
//
//
//        /**
//         *  <location>
//         *     <begin position="1"   />
//         *     <end   position="569" />
//         *  </location>
//         *
//         *  <location>
//         *     <beginInterval begin="1" end="10"   />
//         *     <endInterval   begin="32" end="35"  />
//         *  </location>
//         */
//
//        // get location details
//        // TODO push for implementing uk.ac.ebi.model.Range
//        Element locationElement = DOMUtil.getFirstElement( (Element) element, "location" );
//        Element beginElement = DOMUtil.getFirstElement( locationElement, "begin" );
//        String begin = beginElement.getAttribute( "position" );
//        Element endElement = DOMUtil.getFirstElement( locationElement, "end" );
//        String end = endElement.getAttribute( "position" );
//
//        Element beginIntervalElement = DOMUtil.getFirstElement( locationElement, "beginInterval" );
//        String beginStart = beginIntervalElement.getAttribute( "begin" );
//        String beginStop = beginIntervalElement.getAttribute( "end" );
//        Element endIntervalElement = DOMUtil.getFirstElement( locationElement, "endInterval" );
//        String endStart = endIntervalElement.getAttribute( "begin" );
//        String endStop = endIntervalElement.getAttribute( "end" );
//
//        Element positionIntervalElement = DOMUtil.getFirstElement( (Element) element, "positionInterval" );
//        String positionBegin = positionIntervalElement.getAttribute( "begin" );
//        String positionEnd = positionIntervalElement.getAttribute( "end" );
//
//        Element positionElement = DOMUtil.getFirstElement( (Element) element, "position" );
//        String position = positionIntervalElement.getAttribute( "position" );
//
//        Element siteElement = DOMUtil.getFirstElement( (Element) element, "site" );
//        String site = positionIntervalElement.getAttribute( "position" );
//
//        // create the range
//        Range range = new Range( -1, -10 );  // TODO: ERROR

        // create the feature
//        feature = new Feature();
//        feature.setOwner( owner );
//        feature.setCvFeatureIdentification( cvFeatureIdentification );
//        feature.setCvFeatureType( cvFeatureType );
//        feature.addRange( range );

        return feature;
    }
}

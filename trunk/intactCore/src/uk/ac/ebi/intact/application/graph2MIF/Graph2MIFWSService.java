/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.graph2MIF;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import uk.ac.ebi.intact.application.graph2MIF.conversion.Graph2FoldedMIF;
import uk.ac.ebi.intact.application.graph2MIF.exception.GraphNotConvertableException;
import uk.ac.ebi.intact.application.graph2MIF.exception.MIFSerializeException;
import uk.ac.ebi.intact.application.graph2MIF.exception.NoGraphRetrievedException;
import uk.ac.ebi.intact.application.graph2MIF.exception.NoInteractorFoundException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.simpleGraph.Graph;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Graph2MIFWSService Implementation
 * <p/>
 * This is the implementation of The graph2MIF-WebService.
 * Interface defined in Graph2MIFWS.java
 *
 * @author Henning Mersch <hmersch@ebi.ac.uk>
 * @version $Id$
 */
public class Graph2MIFWSService implements Graph2MIFWS {

    /**
     * logger for proper information
     * see config/log4j.properties for more informtation
     */
    static Logger logger = Logger.getLogger( "graph2MIF" );

    public Graph2MIFWSService() {
    }

    /**
     * getMIF is the only method which is necessary for access.
     *
     * @param ac    String ac in IntAct
     * @param depth Integer of the depth the graph should be expanded
     * @return String including a XML-Document in PSI-MIF-Format
     * @throws IntactException     thrown if search for interactor failed
     * @throws uk.ac.ebi.intact.application.graph2MIF.exception.GraphNotConvertableException
     *                             thrown if Graph failed requirements of MIF.
     * @throws DataSourceException thrown if could not retrieve graph from interactor
     * @throws uk.ac.ebi.intact.application.graph2MIF.exception.NoGraphRetrievedException
     *                             thrown if DOM-Object could not be serialized
     * @throws uk.ac.ebi.intact.application.graph2MIF.exception.MIFSerializeException
     *                             thrown if IntactHelper could not be created
     * @throws uk.ac.ebi.intact.application.graph2MIF.exception.NoInteractorFoundException
     *                             thrown if no Interactor found for ac
     */
    public String getMIF( String ac, Integer depth, Boolean strictmif )
            throws IntactException,
                   GraphNotConvertableException,
                   NoGraphRetrievedException,
                   MIFSerializeException,
                   DataSourceException,
                   NoInteractorFoundException {

        // the serialised DOM-Object - so the returned string.
        String mif = "";
        logger.info( "here we go with AC:'" + ac + "', Depth:'" + depth + "'" );
        logger.info( "generating strict MIF: " + strictmif.booleanValue() );

        //create helper
        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            logger.info( "Helper created" );

            Graph graph = new Graph();

            // retrieve Graph
            graph = GraphFactory.getGraph( helper, ac, depth ); //NoGraphRetrievedExceptioni, IntactException and NoInteractorFoundException possible
            logger.info( "got graph:" );
            logger.info( graph );

            //convert graph to DOM Object
            Graph2FoldedMIF convert = new Graph2FoldedMIF( strictmif );
            Document mifDOM = null;
            mifDOM = convert.getMIF( graph ); // GraphNotConvertableException possible

            // serialize the DOMObject
            StringWriter w = new StringWriter();
            OutputFormat of = new OutputFormat( mifDOM, "UTF-8", true ); //(true|false) for (un)formated  output
            XMLSerializer xmls = new XMLSerializer( w, of );
            try {
                xmls.serialize( mifDOM );
            } catch ( IOException e ) {
                logger.warn( "IOException while serialize" + e.getMessage() );
                throw new MIFSerializeException();
            }
            mif = w.toString();

        } finally {
            if( helper != null ) {
                helper.closeStore();
                logger.info( "Helper closed." );
            }
        }

        //return the PSI-MIF-XML
        return mif;
    }
}

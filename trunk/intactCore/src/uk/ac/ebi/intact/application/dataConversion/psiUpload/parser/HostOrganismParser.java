/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.parser;

import org.w3c.dom.Element;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.HostOrganismTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.Message;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.MessageHolder;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class HostOrganismParser {

    /**
     * Extract data from a <code>organism</code> Element and produce an Intact <code>BioSource</code>
     * <p/>
     * <hostOrganism ncbiTaxId="9606">
     * <names>
     * <shortLabel>Human</shortLabel>
     * <fullName>Homo sapiens</fullName>
     * </names>
     * </hostOrganism>
     *
     * @return an intact <code>BioSource</code> or null if something goes wrong.
     * @see uk.ac.ebi.intact.model.BioSource
     */
    public static HostOrganismTag process( final Element root ) {

        final String nodeName = root.getNodeName();

        if( false == "hostOrganism".equals( nodeName ) &&
            false == "organism".equals( nodeName ) ) {
            MessageHolder.getInstance().addParserMessage( new Message( root, "ERROR - We should be in hostOrganism tag." ) );
        }

        final String taxid = root.getAttribute( "ncbiTaxId" );

        HostOrganismTag hostHorganism = null;
        try {
            hostHorganism = new HostOrganismTag( taxid );
        } catch ( IllegalArgumentException e ) {
            MessageHolder.getInstance().addParserMessage( new Message( root, e.getMessage() ) );
        }

        return hostHorganism;
    }
}

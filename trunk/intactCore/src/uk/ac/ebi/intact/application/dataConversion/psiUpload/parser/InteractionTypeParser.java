/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.parser;

import org.w3c.dom.Element;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.Constants;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.InteractionTypeTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.XrefTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.DOMUtil;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.Message;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.MessageHolder;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.CvInteractionType;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionTypeParser {

    /**
     * Process a <interactionType>.
     * <br>
     * In order to map it to the IntAct data, we look for the psi-mi <code>primaryRef</code>,
     * and look-up in intact using its <code>id</code>.
     * <br>
     * If that <code>CvInteractionType</code> exists, return it.
     * Else throw an Exception.
     * <p/>
     * <pre>
     * &lt;interactionType&gt;
     *      &lt;names&gt;
     *          &lt;shortLabel&gt;popipo&lt;/shortLabel&gt;
     *          &lt;fullName&gt;tralala&lt;/fullName&gt;
     *      &lt;/names&gt;
     *      &lt;xref&gt;
     *       <b>   &lt;primaryRef db="psi-mi" id="<font color="red">MI:0123</font>"
     *              secondary="" version="" /&gt; </b>
     *          &lt;secondaryRef db="pubmed" id="10504710"
     *              secondary="" version="" /&gt;
     *      &lt;/xref&gt;
     * &lt;/interactionType&gt;
     * </pre>
     * Here, we should be looking for a CvInteractionType that have a psi Xref and a primaryId equals to MI:0123.
     *
     * @param element &lt;interactionType&gt;.
     * @return an IntAct <code>CvInteractionType</code> if the XML contains a walid psi-mi descriptor.
     * @throws IntactException
     * @see CvInteractionType
     */
    public static InteractionTypeTag process( final Element element ) {

        if( false == "interactionType".equals( element.getNodeName() ) ) {
            MessageHolder.getInstance().addParserMessage( new Message( element, "ERROR - We should be in interactionType tag." ) );
        }

        final Element interactionTypeXref = DOMUtil.getFirstElement( element, "xref" );

        // Look at either primaryRef and secondaryRef
        final XrefTag xref = XrefParser.getXrefByDb( interactionTypeXref, Constants.PSI_DB_SHORTLABEL );

        InteractionTypeTag interactionType = null;
        try {
            interactionType = new InteractionTypeTag( xref );
        } catch ( IllegalArgumentException e ) {
            MessageHolder.getInstance().addParserMessage( new Message( element, e.getMessage() ) );
        }

        return interactionType;
    }
}

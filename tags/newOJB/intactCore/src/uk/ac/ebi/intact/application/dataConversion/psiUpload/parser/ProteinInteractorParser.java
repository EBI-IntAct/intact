/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.parser;

import org.w3c.dom.Element;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.OrganismTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.ProteinInteractorTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.XrefTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.CommandLineOptions;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.DOMUtil;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.LabelValueBean;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.Message;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.MessageHolder;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinInteractorParser {

    private Element root;
    private ParticipantListParser interactorList;


    public ProteinInteractorParser( final ParticipantListParser interactorList, final Element root ) {

        this.interactorList = interactorList;
        this.root = root;
    }

    /**
     * Extract data from a <code>proteinInteractor</code> Element and produce an Intact <code>Protein</code>
     * <p/>
     * <pre>
     *  <proteinInteractor id="hGHR">
     *      <names>
     *         <shortLabel>hGHR</shortLabel>
     * <p/>
     *         <fullName>Human growth hormone receptor</fullName>
     *      </names>
     * <p/>
     *      <xref>
     *         <primaryRef db="Swiss-Prot" id="P10912"/>
     *      </xref>
     * <p/>
     *      <organism ncbiTaxId="9606">
     *         <names>
     *            <shortLabel>Human</shortLabel>
     * <p/>
     *            <fullName>Homo sapiens</fullName>
     *         </names>
     *      </organism>
     * <p/>
     *      <sequence>MDLWQLLLTLALAGSSDAFSGSEATAAILSRAPWSLQSVNPGLKTNSSKEPKFTKCRSPERETFSCHWTDEVHHGTKNLGPIQLFYTRRNTQEWTQEWKECPDYVSAGENSCYFNSSFTSIWIPYCIKLTSNGGTVDEKCFSVDEIVQPDPPIALNWTLLNVSLTGIHADIQVRWEAPRNADIQKGWMVLEYELQYKEVNETKWKMMDPILTTSVPVYSLKVDKEYEVRVRSKQRNSGNYGEFSEVLYVTLPQMSQFTCEEDFYFPWLLIIIFGIFGLTVMLFVFLFSKQQRIKMLILPPVPVPKIKGIDPDLLKEGKLEEVNTILAIHDSYKPEFHSDDSWVEFIELDIDEPDEKTEESDTDRLLSSDHEKSHSNLGVKDGDSGRTSCCEPDILETDFNANDIHEGTSEVAQPQRLKGEADLLCLDQKNQNNSPYHDACPATQQPSVIQAEKNKPQPLPTEGAESTHQAAHIQLSNPSSLSNIDFYAQVSDITPAGSVVLSPGQKNKAGMSQCDMHPEMVSLCQENFLMDNAYFCEADAKKCIPVAPHIKVESHIQPSLNQEDIYITTESLTTAAGRPGTGEHVPGSEMPVPDYTSIHIVQSPQGLILNATALPLPDKEFLSSCGYVSTDQLNKIMP</sequence>
     *  </proteinInteractor>
     * </pre>
     *
     * @return an intact <code>Protein</code> or null if something goes wrong.
     * @see uk.ac.ebi.intact.model.Protein
     */
    public LabelValueBean process() {
        ProteinInteractorTag proteinInteractor = null;

        if( false == "proteinInteractor".equals( root.getNodeName() ) ) {
            MessageHolder.getInstance().addParserMessage( new Message( root, "We should be in proteinInteractor tag." ) );
        }

        final String id = root.getAttribute( "id" );

        // check if the proteinInteractor is existing in the global context
        if( interactorList != null ) {
            proteinInteractor = (ProteinInteractorTag) interactorList.getInteractors().get( id );
            if( proteinInteractor != null ) {
                final String msg = "WARNING - the protein id: " + id + " is defined several times. " +
                                   "The global definition will be used instead.";
                MessageHolder.getInstance().addParserMessage( new Message( root, msg ) );
                return new LabelValueBean( id, proteinInteractor );
            }
        }

        // CAUTION - MAY NOT BE THERE
        final Element biosourceElement = DOMUtil.getFirstElement( root, "organism" );
        OrganismTag hostOrganism = null;
        if( biosourceElement != null ) {
            hostOrganism = OrganismParser.process( biosourceElement );
        } else {
            // the PSI file lacks a taxid, check if the user requested us to put a default value in such case.
            if( CommandLineOptions.getInstance().hasDefaultInteractorTaxid() ) {
                final String defaultTaxid = CommandLineOptions.getInstance().getDefaultInteractorTaxid();
                hostOrganism = new OrganismTag( defaultTaxid );
            }
        }


        // CAUTION - MAY NOT BE THERE
        final Element xrefElement = DOMUtil.getFirstElement( root, "xref" );
        final XrefTag xref = XrefParser.processPrimaryRef( xrefElement );

        LabelValueBean lvb = null;

        try {
            lvb = new LabelValueBean( id, new ProteinInteractorTag( xref, hostOrganism ) );
        } catch ( IllegalArgumentException e ) {
            MessageHolder.getInstance().addParserMessage( new Message( root, e.getMessage() ) );
        }

        return lvb;
    }
}

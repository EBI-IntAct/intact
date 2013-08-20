package uk.ac.ebi.intact.protein.mapping.actions;

import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.factories.ReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;

import java.util.List;

/**
 * An IdentificationAction is an action to identify a protein using a sequence, name, identifier, organism, etc. It follows a logical process
 * respecting the curation rules
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Mar-2010</pre>
 */

public interface IdentificationAction {

    /**
     * Run the process that follows this action. Add new ActionReports where details about the action are stored in every time this method is called
     * @param context  : the context of the protein
     * @return The unique uniprot AC identifying the protein that this action could find, null otherwise
     * @throws uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException
     */
    public String runAction(IdentificationContext context) throws ActionProcessingException;

    /**
     *
     * @return the list of ActionReports the action contains
     */
    public List<MappingReport> getListOfActionReports();

    public ReportsFactory getReportsFactory();

    public void setReportsFactory(ReportsFactory factory);
    
}

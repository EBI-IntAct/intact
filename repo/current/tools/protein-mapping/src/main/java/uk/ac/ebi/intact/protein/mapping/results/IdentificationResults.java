package uk.ac.ebi.intact.protein.mapping.results;

import uk.ac.ebi.intact.protein.mapping.actions.ActionName;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;

import java.util.List;

/**
 * Interface for results of protein mapping
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/06/11</pre>
 */

public interface IdentificationResults {

    public String getFinalUniprotId();
    public boolean hasUniqueUniprotId();
    public List<MappingReport> getListOfActions();
    public boolean addActionReport(MappingReport report);
    public boolean removeActionReport(MappingReport report);
    public MappingReport getLastAction();
    public List<MappingReport> getActionsByName(ActionName name);
    public void setFinalUniprotId(String id);
}

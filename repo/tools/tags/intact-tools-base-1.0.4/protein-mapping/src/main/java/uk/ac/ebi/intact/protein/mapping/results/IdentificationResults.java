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

public interface IdentificationResults<T extends MappingReport> {

    public String getFinalUniprotId();
    public boolean hasUniqueUniprotId();
    public List<T> getListOfActions();
    public boolean addActionReport(T report);
    public boolean removeActionReport(T report);
    public T getLastAction();
    public List<T> getActionsByName(ActionName name);
    public void setFinalUniprotId(String id);
}

package uk.ac.ebi.intact.protein.mapping.model.actionReport;

import uk.ac.ebi.intact.protein.mapping.actions.ActionName;
import uk.ac.ebi.intact.protein.mapping.actions.status.Status;
import uk.ac.ebi.intact.protein.mapping.actions.status.StatusLabel;

import java.util.List;
import java.util.Set;

/**
 * Interface for Mapping reports
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/06/11</pre>
 */

public interface MappingReport {

    public ActionName getName();
    public List<String> getWarnings();
    public void addWarning(String warn);
    public Set<String> getPossibleAccessions();
    public void addPossibleAccession(String ac);
    public Status getStatus();
    public void setStatus(Status status);
    public StatusLabel getStatusLabel();
    public String getStatusDescription();
    public boolean isASwissprotEntry();
    public void setIsASwissprotEntry(boolean swissprot);
}

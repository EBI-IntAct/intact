package uk.ac.ebi.intact.protein.mapping.model.actionReport;

import uk.ac.ebi.intact.protein.mapping.results.PICRCrossReferences;

import java.util.Set;

/**
 * Interface to implement for PICR reports
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/06/11</pre>
 */

public interface PICRReport<T extends PICRCrossReferences> extends MappingReport{

    public Set<T> getCrossReferences();
    public void addCrossReference(String databaseName, String accession);
    public void addPICRCrossReference(T refs);
}

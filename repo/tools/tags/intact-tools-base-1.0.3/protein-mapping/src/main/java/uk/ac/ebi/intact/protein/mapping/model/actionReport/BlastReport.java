package uk.ac.ebi.intact.protein.mapping.model.actionReport;

import uk.ac.ebi.intact.protein.mapping.results.BlastResults;

import java.util.Set;

/**
 * Interface to implement for blast reports
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/06/11</pre>
 */

public interface BlastReport<T extends BlastResults> extends MappingReport{

    public Set<T> getBlastMatchingProteins();
    public void addBlastMatchingProtein(T prot);
    public String getQuerySequence();
    public void setQuerySequence(String sequence);
}

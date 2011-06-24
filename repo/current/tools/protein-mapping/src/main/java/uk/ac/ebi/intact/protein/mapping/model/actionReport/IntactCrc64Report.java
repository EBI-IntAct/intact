package uk.ac.ebi.intact.protein.mapping.model.actionReport;

/**
 * Interface to implement for IntactCrc64Report reports
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/06/11</pre>
 */

public interface IntactCrc64Report extends IntactReport{

    public String getQuerySequence();
    public void setQuerySequence(String sequence);
}

package uk.ac.ebi.intact.uniprot.model;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02-Aug-2010</pre>
 */

public interface UniprotProteinTranscript extends UniprotProteinLike {

    public boolean isNullSequenceAllowed();

    public String getParentXRefQualifier();

    public Integer getStart();

    public void setStart(Integer start);

    public Integer getEnd();

    public void setEnd(Integer end);

    public Collection<String> getSynomyms();

    public void setSynomyms( Collection<String> synomyms );

    public String getNote();

    public void setNote( String note );
}

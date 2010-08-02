package uk.ac.ebi.intact.uniprot.model;

import java.util.Collection;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02-Aug-2010</pre>
 */

public interface UniprotProteinTranscript {

    public boolean isNullSequenceAllowed();

    public String getParentXRefQualifier();

    public String getPrimaryAc();

    public void setPrimaryAc(String primaryAc);

    public String getSequence();

    public void setSequence(String sequence);

    public Organism getOrganism();

    public void setOrganism(Organism organism);

    public Integer getStart();

    public void setStart(Integer start);

    public Integer getEnd();

    public void setEnd(Integer end);

    public List<String> getSecondaryAcs();

    public void setSecondaryAcs( List<String> secondaryAcs );

    public Collection<String> getSynomyms();

    public void setSynomyms( Collection<String> synomyms );

    public String getNote();

    public void setNote( String note );

    public String getDescription();

    public void setDescription( String description );
}

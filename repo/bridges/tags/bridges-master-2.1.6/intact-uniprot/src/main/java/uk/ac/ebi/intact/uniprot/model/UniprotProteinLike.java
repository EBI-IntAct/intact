package uk.ac.ebi.intact.uniprot.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface UniprotProteinLike extends Serializable {

    String getId();

    String getPrimaryAc();

    void setPrimaryAc(String primaryAc);

    List<String> getSecondaryAcs();

    Organism getOrganism();

    void setOrganism(Organism organism);

    String getDescription();

    void setDescription(String description);

    String getSequence();

    void setSequence(String sequence);
}

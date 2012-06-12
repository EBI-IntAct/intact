package uk.ac.ebi.intact.dbupdate.smallmolecules;

import java.util.List;

/**
 * Interace to SmallMolecule update
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1
 */
public interface SmallMoleculeProcessor {

    public void updateByAcs(List<String> acs) throws SmallMoleculeUpdatorException;

    public SmallMoleculeUpdateReport getReport();

    public void setReport( SmallMoleculeUpdateReport report );
}
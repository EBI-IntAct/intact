package uk.ac.ebi.intact.dbupdate.smallmolecules;

import uk.ac.ebi.intact.model.SmallMolecule;

import java.util.List;

/**
 * Interace to SmallMolecule update
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public interface SmallMoleculeProcessor {

    public void updateByAcs(List<String> acs) throws SmallMoleculeUpdatorException;

}

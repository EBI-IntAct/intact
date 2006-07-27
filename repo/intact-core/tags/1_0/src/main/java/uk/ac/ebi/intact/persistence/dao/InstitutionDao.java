package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.model.Institution;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04-Jul-2006</pre>
 */
public interface InstitutionDao extends IntactObjectDao<Institution>
{
    /**
     * Returns the Institution configured in the properties file
     * @return the Institution configured in the properties file
     */
    Institution getInstitution();

    Institution getByShortLabel(String shortLabel);
}

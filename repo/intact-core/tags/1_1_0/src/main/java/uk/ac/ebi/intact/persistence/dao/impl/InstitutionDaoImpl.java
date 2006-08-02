package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Session;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.persistence.dao.InstitutionDao;
import uk.ac.ebi.intact.util.PropertyLoader;

import java.util.Properties;

/**
 * DAO for institutions
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-jul-2006</pre>
 */
@SuppressWarnings({"unchecked"})
public class InstitutionDaoImpl extends IntactObjectDaoImpl<Institution> implements InstitutionDao
{

    /**
     * Path of the configuration file which allow to retrieve the inforamtion related to the IntAct node we are running
     * on.
     */
    private static final String INSTITUTION_CONFIG_FILE = "/config/Institution.properties";

    public InstitutionDaoImpl(Session session)
    {
        super(Institution.class, session);
    }

    /**
     * @deprecated Use IntactContext.getCurrentInstance().getInstitution() instead
     * @return
     */
    @Deprecated
    public Institution getInstitution()
    {
        Institution institution = null;

        Properties props = PropertyLoader.load( INSTITUTION_CONFIG_FILE );
        if ( props != null ) {
            String shortlabel = props.getProperty( "Institution.shortLabel" );
            if ( shortlabel == null || shortlabel.trim().equals( "" ) ) {
                throw new IntactException( "Your institution is not properly configured, check out the configuration file:" +
                                           INSTITUTION_CONFIG_FILE + " and set 'Institution.shortLabel' correctly" );
            }

            // search for it (force it for LC as short labels must be in LC).
            shortlabel = shortlabel.trim();

            institution = getByShortLabel(shortlabel);
        }

        return institution;
    }

    public Institution getByShortLabel(String shortLabel)
    {
        return getByPropertyName("shortLabel", shortLabel);
    }


}

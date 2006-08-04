/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context;

import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.util.PropertyLoader;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.util.Properties;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public class RuntimeConfig
{
    private static final String APPLICATION_PARAM_NAME = RuntimeConfig.class.getName();

    /**
     * Path of the configuration file which allow to retrieve the inforamtion related to the IntAct node we are running
     * on.
     */
    private static final String INSTITUTION_CONFIG_FILE = "/config/Institution.properties";

    private Institution institution;
    private String acPrefix;

    public static RuntimeConfig getCurrentInstance(IntactSession session)
    {
        RuntimeConfig runtimeConfig
                = (RuntimeConfig)session.getApplicationAttribute(APPLICATION_PARAM_NAME);
        if (runtimeConfig == null)
        {
            runtimeConfig = new RuntimeConfig();
            session.setApplicationAttribute(APPLICATION_PARAM_NAME, runtimeConfig);
        }
        return runtimeConfig;
    }

     public Institution getInstitution() throws IntactException
     {
        if (institution == null)
        {
            institution = loadInstitutionFromProperties();
        }

        return institution;
    }


    public String getAcPrefix()
    {
        return acPrefix;
    }

    public void setAcPrefix(String acPrefix)
    {
        this.acPrefix = acPrefix;
    }

    /**
     * Allow the user not to know about the it's Institution, it has to be configured once in the properties file:
     * ${INTACTCORE_HOME}/config/Institution.properties and then when calling that method, the Institution is either
     * retreived or created according to its shortlabel.
     *
     * @return the Institution to which all created object will be linked.
     */
    private Institution loadInstitutionFromProperties() throws IntactException {
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
            institution = DaoFactory.getInstitutionDao().getByShortLabel( shortlabel );

            if ( institution == null ) {
                // doesn't exist, create it
                institution = new Institution( shortlabel );

                String fullname = props.getProperty( "Institution.fullName" );
                if ( fullname != null ) {
                    fullname = fullname.trim();
                    if ( !fullname.equals( "" ) ) {
                        institution.setFullName( fullname );
                    }
                }


                String lineBreak = System.getProperty( "line.separator" );
                StringBuffer address = new StringBuffer( 128 );
                appendLineFromProperty( address, props, "Institution.postalAddress.line1" );
                appendLineFromProperty( address, props, "Institution.postalAddress.line2" );
                appendLineFromProperty( address, props, "Institution.postalAddress.line3" );
                appendLineFromProperty( address, props, "Institution.postalAddress.line4" );
                appendLineFromProperty( address, props, "Institution.postalAddress.line5" );

                if ( address.length() > 0 ) {
                    address.deleteCharAt( address.length() - 1 ); // delete the last line break;
                    institution.setPostalAddress( address.toString() );
                }

                String url = props.getProperty( "Institution.url" );
                if ( url != null ) {
                    url = url.trim();
                    if ( !url.equals( "" ) ) {
                        institution.setUrl( url );
                    }
                }

                DaoFactory.getInstitutionDao().persist( institution );

            }

        } else {
            throw new IntactException( "Unable to read the properties from " + INSTITUTION_CONFIG_FILE );
        }

        return institution;
    }

    private void appendLineFromProperty(StringBuffer sb, Properties props, String propertyName)
    {
        String lineBreak = System.getProperty("line.separator");

        String line = props.getProperty(propertyName);
        if (line != null)
        {
            line = line.trim();
        }
        if (!line.equals(""))
        {
            sb.append(line).append(lineBreak);
        }
    }

}

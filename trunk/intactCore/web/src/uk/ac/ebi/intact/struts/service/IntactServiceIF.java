/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.service;

import uk.ac.ebi.intact.application.cvedit.struts.framework.exceptions.InvalidLoginException;
import uk.ac.ebi.intact.application.cvedit.struts.framework.exceptions.MissingIntactTypesException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.CvTopic;

import java.util.ResourceBundle;
import java.util.Map;
import java.util.Collection;
import java.beans.IntrospectionException;

import org.apache.struts.action.ActionErrors;

/**
 * The business interface for the Web Intact application. This defines
 * methods that a client (servlet) may call on the application.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public interface IntactServiceIF {

    /**
     * Authenticate the user's credentials.
     *
     * @param username the user name; must not be null.
     * @param password the password for the user; must not be null.
     * @return an intact user object.
     *
     * @exception InvalidLoginException thrown for un unauthorized login.
     */
//    public IntactUser authenticate(String username, String password)
//        throws InvalidLoginException;

    /**
     * Returns the class name associated with the give topic.
     *
     * @param topic the topic to search in the Intact types resource.
     * @return the classname saved under <code>topic</code>.
     */
    public String getClassName(String topic);
}

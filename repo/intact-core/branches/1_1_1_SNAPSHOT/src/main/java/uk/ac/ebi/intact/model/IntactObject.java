/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.Date;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 *
 * @see uk.ac.ebi.intact.model.IntactObjectImpl
 */
public interface IntactObject {

    String getAc();

    void setAc(String ac);

    Date getCreated();

    void setCreated(java.util.Date created);

    Date getUpdated();

    void setUpdated(java.util.Date updated);

    String getCreator();

    void setCreator(String createdUser);

    String getUpdator();

    void setUpdator(String userStamp);
}

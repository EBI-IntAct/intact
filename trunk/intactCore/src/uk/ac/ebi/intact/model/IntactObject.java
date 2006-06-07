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

    public String getAc();

    public void setAc(String ac);

    public Date getCreated();

    public void setCreated(java.util.Date created);

    public Date getUpdated();

    public void setUpdated(java.util.Date updated);

    public String toString();

}

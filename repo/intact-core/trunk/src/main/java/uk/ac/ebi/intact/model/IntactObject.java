/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 *
 * @see uk.ac.ebi.intact.model.IntactObjectImpl
 */
public interface IntactObject extends Auditable {

    static final String NEW_LINE = System.getProperty("line.separator");

    String getAc();

    void setAc(String ac);
}

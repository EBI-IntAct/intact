/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.Collection;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public interface Protein extends Polymer {

    public Protein getFormOf();

    public void setFormOf(Protein protein);

    public CvProteinForm getCvProteinForm();

    public void setCvProteinForm(CvProteinForm cvProteinForm);

    public void setModifications(Collection someModification);

    public Collection getModifications();
}

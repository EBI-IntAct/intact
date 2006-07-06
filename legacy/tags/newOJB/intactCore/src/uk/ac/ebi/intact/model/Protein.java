/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;

import java.util.Collection;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public interface Protein extends Interactor {

    public String getSequence();

    public void setSequence(IntactHelper helper, String aSequence, String crc64) throws IntactException;

    public void setSequence(IntactHelper helper, String aSequence) throws IntactException;

    public String getCrc64();

    public void setCrc64(String crc64);

    public Protein getFormOf();

    public void setFormOf(Protein protein);

    public CvProteinForm getCvProteinForm();

    public void setCvProteinForm(CvProteinForm cvProteinForm);

    public void setFeatures(Collection someFeature);

    public Collection getFeatures();

    public void addFeature(Feature feature);

    public void removeFeature(Feature feature);

    public void setModifications(Collection someModification);

    public Collection getModifications();

    public void addModification(Modification modification);

    public void removeModification(Modification modification);

    //attributes used for mapping BasicObjects - project synchron
    public String  getCvProteinFormAc();

    public void setCvProteinFormAc(String cvProteinFormAc);

    public String getFormOfAc();

    public void setFormOfAc(String ac);

    public boolean equals (Object o);

    public int hashCode ();

}

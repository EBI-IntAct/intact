/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model.proxy;

import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PBKey;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.CvProteinForm;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Modification;
import uk.ac.ebi.intact.model.Protein;

import java.lang.reflect.InvocationHandler;
import java.util.Collection;

/**
 *
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinProxy extends InteractorProxy implements Protein {

    public ProteinProxy()
    {
    }

    /**
     * @param uniqueId org.apache.ojb.broker.Identity
     */
    public ProteinProxy(PBKey key, Identity uniqueId)
    {
        super(key, uniqueId);
    }

    public ProteinProxy(InvocationHandler handler)
    {
        super(handler);
    }

    private Protein realSubject()
    {
        try
        {
            return (Protein) getRealSubject();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Implements Protein's methods
     */

    public String getSequence () {
        return realSubject().getSequence();
    }

    public void setSequence ( IntactHelper helper, String aSequence, String crc64 ) throws IntactException {
        realSubject().setSequence( helper, aSequence, crc64 );
    }

    public void setSequence ( IntactHelper helper, String aSequence ) throws IntactException {
        realSubject().setSequence( helper, aSequence );
    }

    public String getCrc64 () {
        return realSubject().getCrc64();
    }

    public void setCrc64 ( String crc64 ) {
        realSubject().setCrc64( crc64 );
    }

    public Protein getFormOf () {
        return realSubject().getFormOf();
    }

    public void setFormOf ( Protein protein ) {
        realSubject().setFormOf( protein );
    }

    public CvProteinForm getCvProteinForm () {
        return realSubject().getCvProteinForm();
    }

    public void setCvProteinForm ( CvProteinForm cvProteinForm ) {
        realSubject().setCvProteinForm( cvProteinForm );
    }

    public void setFeatures ( Collection someFeature ) {
        realSubject().setFeatures( someFeature );
    }

    public Collection getFeatures () {
        return realSubject().getFeatures();
    }

    public void addFeature ( Feature feature ) {
        realSubject().addFeature( feature );
    }

    public void removeFeature ( Feature feature ) {
        realSubject().removeFeature( feature );
    }

    public void setModifications ( Collection someModification ) {
        realSubject().setModifications( someModification );
    }

    public Collection getModifications () {
        return realSubject().getModifications();
    }

    public void addModification ( Modification modification ) {
        realSubject().addModification( modification );
    }

    public void removeModification ( Modification modification ) {
        realSubject().removeModification( modification );
    }

    //attributes used for mapping BasicObjects - project synchron
    public String getCvProteinFormAc () {
        return realSubject().getCvProteinFormAc();
    }

    public void setCvProteinFormAc ( String cvProteinFormAc ) {
        realSubject().setCvProteinFormAc( cvProteinFormAc );
    }

    public String getFormOfAc () {
        return realSubject().getFormOfAc();
    }

    public void setFormOfAc ( String ac ) {
        realSubject().setFormOfAc( ac );
    }

    public boolean equals ( Object o ) {
        return realSubject().equals( o );
    }

    public int hashCode () {
        return realSubject().hashCode();
    }
}
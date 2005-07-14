/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model.proxy;

import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PBKey;
import uk.ac.ebi.intact.model.*;

import java.lang.reflect.InvocationHandler;
import java.util.Collection;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionProxy extends InteractorProxy implements Interaction {

    public InteractionProxy() {
    }

    /**
     * @param uniqueId org.apache.ojb.broker.Identity
     */
    public InteractionProxy(PBKey key, Identity uniqueId) {
        super(key, uniqueId);
    }

    public InteractionProxy(InvocationHandler handler) {
        super(handler);
    }

    private Interaction realSubject() {
        try
        {
            return (Interaction) getRealSubject();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Implements Interaction's methods
     */

    public Float getKD () {
        return realSubject().getKD();
    }

    public void setKD ( Float kD ) {
        realSubject().setKD( kD);
    }

    public void setComponents ( Collection someComponent ) {
        realSubject().setComponents( someComponent );
    }

    public Collection getComponents () {
        return realSubject().getComponents();
    }

    public void addComponent ( Component component ) {
        realSubject().addComponent( component );
    }

    public void removeComponent ( Component component ) {
        realSubject().removeComponent( component );
    }

    public void setReleased ( Collection someReleased ) {
        realSubject().setReleased( someReleased );
    }

    public Collection getReleased () {
        return realSubject().getReleased();
    }

    public void addReleased ( Product product ) {
        realSubject().addReleased( product );
    }

    public void removeReleased ( Product product ) {
        realSubject().removeReleased( product );
    }

    public void setExperiments ( Collection someExperiment ) {
        realSubject().setExperiments( someExperiment );
    }

    public Collection getExperiments () {
        return realSubject().getExperiments();
    }

    public void addExperiment ( Experiment experiment ) {
        realSubject().addExperiment( experiment );
    }

    public void removeExperiment ( Experiment experiment ) {
        realSubject().removeExperiment( experiment );
    }

    public CvInteractionType getCvInteractionType () {
        return realSubject().getCvInteractionType() ;
    }

    public void setCvInteractionType ( CvInteractionType cvInteractionType ) {
        realSubject().setCvInteractionType( cvInteractionType );
    }

    //attributes used for mapping BasicObjects - project synchron
    public String getCvInteractionTypeAc () {
        return realSubject().getCvInteractionTypeAc();
    }

    public void setCvInteractionTypeAc ( String ac ) {
        realSubject().setCvInteractionTypeAc( ac );
    }

    public Component getBait () {
        return realSubject().getBait();
    }

//    public boolean equals ( Object o ) {
//        return realSubject().equals( o );
//    }
//
//    public int hashCode () {
//        return realSubject().hashCode();
//    }
}
/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model.proxy;

import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PBKey;
import uk.ac.ebi.intact.model.CvProteinForm;
import uk.ac.ebi.intact.model.Protein;

import java.lang.reflect.InvocationHandler;
import java.util.Collection;

/**
*
*
* @author Samuel Kerrien (skerrien@ebi.ac.uk)
* @version $Id$
*/
public class ProteinProxy extends PolymerProxy implements Protein {

   public ProteinProxy() {
   }

   /**
    * @param uniqueId org.apache.ojb.broker.Identity
    */
   public ProteinProxy(PBKey key, Identity uniqueId ) {
       super(key, uniqueId);
   }

   public ProteinProxy(InvocationHandler handler ) {
       super(handler);
   }

   private Protein realSubject() {
       try
       {
           return (Protein) getRealSubject();
       }
       catch (Exception e)
       {
           return null;
       }
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

   public void setModifications ( Collection someModification ) {
       realSubject().setModifications( someModification );
   }

   public Collection getModifications () {
       return realSubject().getModifications();
   }

   public boolean equals ( Object o ) {
       return realSubject().equals( o );
   }

   public int hashCode () {
       return realSubject().hashCode();
   }
} 

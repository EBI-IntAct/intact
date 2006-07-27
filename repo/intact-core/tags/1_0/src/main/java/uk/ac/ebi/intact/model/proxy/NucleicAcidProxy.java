/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model.proxy;

import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PBKey;
import uk.ac.ebi.intact.model.NucleicAcid;

import java.lang.reflect.InvocationHandler;

/**
*
*
* @author Samuel Kerrien (skerrien@ebi.ac.uk)
* @version $Id$
*/
public class NucleicAcidProxy extends PolymerProxy implements NucleicAcid {

   public NucleicAcidProxy() {
   }

   /**
    * @param uniqueId org.apache.ojb.broker.Identity
    */
   public NucleicAcidProxy(PBKey key, Identity uniqueId ) {
       super(key, uniqueId);
   }

   public NucleicAcidProxy(InvocationHandler handler ) {
       super(handler);
   }

   private NucleicAcid realSubject() {
       try
       {
           return (NucleicAcid) getRealSubject();
       }
       catch (Exception e)
       {
           return null;
       }
   }

   @Override
   public boolean equals ( Object o ) {
       if (o == null || realSubject() == null)
       {
           return false;
       }
       
       return realSubject().equals( o );
   }

   @Override
   public int hashCode () {
       return realSubject().hashCode();
   }
} 

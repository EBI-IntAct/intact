/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model.proxy;

import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.Identity;

import java.lang.reflect.InvocationHandler;

import uk.ac.ebi.intact.model.SmallMolecule;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class SmallMoleculeProxy extends InteractorProxy implements SmallMolecule
{
    public SmallMoleculeProxy() {
   }

   /**
    * @param uniqueId org.apache.ojb.broker.Identity
    */
   public SmallMoleculeProxy(PBKey key, Identity uniqueId ) {
       super(key, uniqueId);
   }

   public SmallMoleculeProxy(InvocationHandler handler ) {
       super(handler);
   }

   private SmallMolecule realSubject() {
       try
       {
           return (SmallMolecule) getRealSubject();
       }
       catch (Exception e)
       {
           return null;
       }
   }

   @Override
   public boolean equals ( Object o ) {
       return realSubject().equals( o );
   }

   @Override
   public int hashCode () {
       return realSubject().hashCode();
   }
}

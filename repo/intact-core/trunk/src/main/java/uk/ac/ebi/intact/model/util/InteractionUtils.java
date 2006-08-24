/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.*;

import java.util.Collection;
import java.util.Iterator;

/**
 * Util methods for interactions
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Aug-2006</pre>
 */
public class InteractionUtils
{

    private static final Log log = LogFactory.getLog(InteractionUtils.class);

    /**
     * Checks if the interaction is a binary interaction
     * @param interaction
     * @return
     */
    public static boolean isBinaryInteraction(Interaction interaction) {
        Collection<Component> components = interaction.getComponents();
        int componentCount = components.size();

       if (componentCount == 1)
       {
           Component component1 = components.iterator().next();
           if (component1.getStoichiometry() == 2)
           {
               log.debug("Binary interaction "+interaction.getAc()+". Stoichiometry 2, each component with stoichiometry 1");
               return true;
           }
       }
       else if (componentCount == 2)
       {
           Iterator<Component> iterator1 = components.iterator();

           Component component1 = iterator1.next();
           float stochio1 = component1.getStoichiometry();
           if (stochio1 == 1)
           {
               Component component2 = iterator1.next();
               if (component2.getStoichiometry() == 1)
               {
                   log.debug("Binary interaction "+interaction.getAc()+". Stoichiometry 2, each component with stoichiometry 1");
                   return true;
               }
           }
           else if (stochio1 == 0)
           {
               Component component2 = iterator1.next();
               if (component2.getStoichiometry() == 0)
               {
                   log.debug("Binary interaction "+interaction.getAc()+". Stoichiometry 0, components 2");
                   return true;
               }
           }
       }
        return false;
    }

    /**
     * Checks if the interaction is a self interaction
     * @param interaction
     * @return
     */
    public static boolean isSelfInteraction(Interaction interaction)
    {
        Collection<Component> components = interaction.getComponents();
        int componentCount = components.size();

        if (componentCount == 1)
        {
            Component comp = components.iterator().next();

            if (comp.getStoichiometry() == 0 &&
                    comp.getCvComponentRole().getShortLabel().equals(CvComponentRole.SELF))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a interaction contain other interactor types than Protein
     * @param interaction
     * @return
     */
    public static boolean containsNonProteinInteractors(Interaction interaction)
    {
        for (Component component : interaction.getComponents())
        {
            Interactor interactor = component.getInteractor();
            if (!(interactor instanceof ProteinImpl))
            {
                return true;
            }
        }

        return false;
    }

}

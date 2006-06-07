package uk.ac.ebi.intact.application.search3.struts.util;

import org.apache.commons.collections.CollectionUtils;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.util.*;

/**
 * Provides utility methods for Protein instances to calculate interactions.
 *
 * @author Michael Kleen
 * @version ProteinUtils.java Date: Feb 11, 2005 Time: 10:51:08 AM
 */
public class ProteinUtils {

    /**
     * ProteinUtils should not be instantiated.
     */
    private ProteinUtils() {
    }

    /**
     * Returns a collection of binary interactions in which all proteins are involved.
     *
     * @param someProteins a collection proteins, must no be null
     *
     * @return a collection of binary interactions in which all proteins are involved
     */
    public static Collection getBinaryInteractions( final Collection someProteins ) {

        // first we check the type to make clear nobody is putting rubbish in here
        final Collection myProteins = new ArrayList();
        for ( Iterator iterator = someProteins.iterator(); iterator.hasNext(); ) {
            final Object o = iterator.next();
            if ( !Protein.class.isAssignableFrom( o.getClass() ) ) {
                // someone put rubbish in, throw an exception
                throw new IllegalArgumentException( "Wrong datatype in protein collection" );
            } else {
                myProteins.add( (Protein) o );
            }
        } // for

        // let's start first to calculating all interactions in which all proteins involved
        Collection intersection = new HashSet();

        final Iterator iterator = myProteins.iterator();
        final Protein first = (Protein) iterator.next();
        intersection.addAll( getNnaryInteractions( first ) );

        while ( iterator.hasNext() ) {
            final Protein protein = (Protein) iterator.next();
            // get Nnary Interaction for every protein
            final Collection proteinInteraction = getNnaryInteractions( protein );
            // and calculate the intersection to the other proteins
            intersection = CollectionUtils.intersection( intersection, proteinInteraction );
        }

        Collection result = new HashSet();

        // now check for every interaction if it's binary
        for ( Iterator iterator1 = intersection.iterator(); iterator1.hasNext(); ) {

            final Interaction anInteraction = (Interaction) iterator1.next();
            final Collection someComponents = anInteraction.getComponents();

            if ( someComponents.size() <= 2 ) {
                // we got 1 or 2 Components, this could be an Self Interaction or a Binary Interaction
                // Let's check this with the summ of the stoichiometry, if it's 2 we are sucessfull
                int stoichiometry = 0;

                for ( Iterator iterator2 = someComponents.iterator(); iterator2.hasNext(); ) {
                    final Component aComponent = (Component) iterator2.next();
                    stoichiometry = stoichiometry + (int) aComponent.getStoichiometry();
                }

                if ( stoichiometry == 2 ) {
                    // it's a banary/self interaction
                    result.add( anInteraction );
                }
            }
        } // for

        if ( result.isEmpty() ) {
            // we found no interactions, so throw a empty set back
            return Collections.EMPTY_SET;
        }
        return result;
    }

    /**
     * Returns a collection containing the n-ary interactions of the give protein.
     *
     * @param anInteractor a protein, must no be null
     *
     * @return a collection containing the n-ary interactions of the given protein.
     */
    public static Collection<Interaction> getNnaryInteractions( final Interactor anInteractor ) {
        // first get all Components
        //TODO (BA) This needs to be paginated to avoid potential OutOfMemoryExceptions
        final Collection<Component> componentSet = anInteractor.getActiveInstances();
        final Set<Interaction> someInteractions = new HashSet<Interaction>();
        // now get all Interactions from the Components
        for (Component component : componentSet)
        {
            someInteractions.add(component.getInteraction());
        }

        return someInteractions;
    }


    /**
     * Returns a collection containing the self interactions of the give protein.
     * <p>
     * A Self Interaction is an Interaction which got 2 or lesser Compoenents and the sum of the stoichemetry is 2.
     *
     *
     * @return a collection containing the self interactions of the given protein.
     */
//    public static Collection getSelfInteractions( final Protein aProtein ) {      // 4 usage 1 in BinaryPorteinAction, 3 in PartnerViewBean
    public static Collection<Interaction> getSelfInteractions( final Interactor anInteractor ) {

        final Set<Interaction> result = new HashSet<Interaction>();
        final Collection<Interaction> someInteractions = getNnaryInteractions( anInteractor );

        // now check for every interaction
        for (Interaction anInteraction : someInteractions)
        {
            final Collection<Component> someComponents = anInteraction.getComponents();
            //TODO it should work with someComponents.size == 1
            if (someComponents.size() <= 2)
            {
                int stoichiometry = 0;

                for (Component aComponent : someComponents)
                {
                    if (anInteractor.equals(aComponent.getInteractor()))
                    {
                        stoichiometry = stoichiometry + (int) aComponent.getStoichiometry();
                    }
                } // for

                if (stoichiometry == 2)
                {
                    // it's a self interaction
                    result.add(anInteraction);
                }
            }
        } // for

        if ( result.isEmpty() ) {
            // we found no interactions, so throw a empty set back
            return Collections.EMPTY_SET;
        }
        return result;
    }

    /**
     * Returns a collection containing the intersection of n-ary interactions from 2 give proteins.
     *
     * @param firstInteractor  a protein, must no be null
     * @param secondInteractor a protein, must no be null
     *
     * @return a collection containing all n-nary  interactions of the 2 given proteins.
     */
//    public static Collection getNnaryInteractions( final Protein firstProtein,   // 2 usage in PartnersViewBean
    public static Collection<Interaction> getNnaryInteractions( final Interactor firstInteractor,
                                                   final Interactor secondInteractor ) {
        // getting all Interactions from firstProtein
        final Collection<Interaction> interactor1Interactions = getNnaryInteractions( firstInteractor );
        // getting all Interactions from secondProtein
        final Collection<Interaction> interactor2Interactions = getNnaryInteractions( secondInteractor );
        // get only these Interactions in which both are involved
        return CollectionUtils.intersection( interactor1Interactions, interactor2Interactions );
    }
}
package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.PublicationFetcher;
import psidev.psi.mi.jami.enricher.CuratedPublicationEnricher;
import psidev.psi.mi.jami.enricher.PublicationEnricher;
import psidev.psi.mi.jami.enricher.SourceEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullPublicationEnricher;
import psidev.psi.mi.jami.enricher.listener.PublicationEnricherListener;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.Publication;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.synchronizer.impl.PublicationSynchronizer;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Publication merger based on the jami publication enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class PublicationMergerEnrichOnly extends IntactDbMergerEnrichOnly<Publication, IntactPublication> implements CuratedPublicationEnricher {

    public PublicationMergerEnrichOnly(PublicationSynchronizer intactSynchronizer){
        super(IntactPublication.class, new FullPublicationEnricher(intactSynchronizer));
    }

    @Override
    protected PublicationEnricher getBasicEnricher() {
        return (PublicationEnricher) super.getBasicEnricher();
    }

    public PublicationFetcher getPublicationFetcher() {
        return getBasicEnricher().getPublicationFetcher();
    }

    public PublicationEnricherListener getPublicationEnricherListener() {
        return null;
    }

    public SourceEnricher getSourceEnricher() {
        return null;
    }

    @Override
    public IntactPublication merge(IntactPublication obj1, IntactPublication obj2) {
        // obj2 is mergedPub
        IntactPublication mergedPub = super.merge(obj1, obj2);

        // merge shortLabel
        if (mergedPub.getShortLabel() == null){
            mergedPub.setShortLabel(obj1.getShortLabel());
        }
        // merge curator
        if (mergedPub.getCurrentOwner() == null && obj1.getCurrentOwner() != null){
            mergedPub.setCurrentOwner(obj1.getCurrentOwner());
        }
        // merge reviewer
        if (mergedPub.getCurrentReviewer() == null && obj1.getCurrentReviewer() != null){
            mergedPub.setCurrentReviewer(obj1.getCurrentReviewer());
        }
        // merge status
        if (mergedPub.getCvStatus() == null && obj1.getCvStatus() != null){
            mergedPub.setCvStatus(obj1.getCvStatus());
        }
        // merge lifecycle
        if (obj1.areLifeCycleEventsInitialized()){
            mergeLifeCycleEvents(mergedPub.getLifecycleEvents(), obj1.getLifecycleEvents());
        }
        //merge experiments
        if (obj1.areExperimentsInitialized()){
            mergeExperiments(mergedPub, mergedPub.getExperiments(), obj1.getExperiments());
        }
        return mergedPub;
    }

    private void mergeExperiments(Publication pubToEnrich, Collection<Experiment> toEnrichExperiments, Collection<Experiment> sourceExperiments) {

        Iterator<Experiment> experimentIterator = sourceExperiments.iterator();
        while(experimentIterator.hasNext()){
            Experiment experiment = experimentIterator.next();
            boolean containsExperiment = false;
            for (Experiment experiment2 : toEnrichExperiments){
                // identical terms
                if (experiment == experiment2){
                    containsExperiment = true;
                    break;
                }
            }
            // add missing xref not in second list
            if (!containsExperiment){
                pubToEnrich.addExperiment(experiment);
            }
        }
    }

    /**
     * Takes two lists of lifecycle events and produces a list of those to add and those to remove.
     *
     * It will add all events from fetchedTerms that are not in collection to enrich. It will not remove anything from the list to enrich
     *
     */
    private void mergeLifeCycleEvents(List<LifeCycleEvent> toEnrichEvents, List<LifeCycleEvent> sourceEvents){

        Iterator<LifeCycleEvent> eventIterator = sourceEvents.iterator();
        int index = 0;
        while(eventIterator.hasNext()){
            LifeCycleEvent event = eventIterator.next();
            boolean containsEvent = false;
            for (LifeCycleEvent event2 : toEnrichEvents){
                // identical terms
                if (event == event2){
                    containsEvent = true;
                    break;
                }
            }
            // add missing xref not in second list
            if (!containsEvent){
                toEnrichEvents.add(index, event);
            }
            index++;
        }
    }
}

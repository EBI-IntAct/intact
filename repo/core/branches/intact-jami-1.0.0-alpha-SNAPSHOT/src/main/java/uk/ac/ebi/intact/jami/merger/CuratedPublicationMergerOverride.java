package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.PublicationFetcher;
import psidev.psi.mi.jami.enricher.CuratedPublicationEnricher;
import psidev.psi.mi.jami.enricher.PublicationEnricher;
import psidev.psi.mi.jami.enricher.SourceEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullCuratedPublicationUpdater;
import psidev.psi.mi.jami.enricher.listener.PublicationEnricherListener;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.Publication;
import uk.ac.ebi.intact.jami.model.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.extension.IntactCuratedPublication;
import uk.ac.ebi.intact.jami.synchronizer.impl.CuratedPublicationSynchronizer;

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

public class CuratedPublicationMergerOverride extends IntactDbMergerOverride<Publication, IntactCuratedPublication> implements CuratedPublicationEnricher {

    public CuratedPublicationMergerOverride(CuratedPublicationSynchronizer intactSynchronizer){
        super(IntactCuratedPublication.class, new FullCuratedPublicationUpdater(intactSynchronizer));
    }

    @Override
    protected PublicationEnricher getBasicEnricher() {
        return (PublicationEnricher) super.getBasicEnricher();
    }

    public PublicationFetcher getPublicationFetcher() {
        return getBasicEnricher().getPublicationFetcher();
    }

    public SourceEnricher getSourceEnricher() {
        return null;
    }

    public PublicationEnricherListener getPublicationEnricherListener() {
        return null;
    }

    @Override
    public IntactCuratedPublication merge(IntactCuratedPublication obj1, IntactCuratedPublication obj2) {
        IntactCuratedPublication mergedPub = super.merge(obj1, obj2);

        // merge curator
        if (mergedPub.getCurrentOwner() != obj1.getCurrentOwner()){
            mergedPub.setCurrentOwner(obj1.getCurrentOwner());
        }
        // merge reviewer
        if (mergedPub.getCurrentReviewer() != obj1.getCurrentReviewer()){
            mergedPub.setCurrentReviewer(obj1.getCurrentReviewer());
        }
        // merge status
        if (mergedPub.getStatus() != obj1.getStatus()){
            mergedPub.setStatus(obj1.getStatus());
        }
        // merge lifecycle
        if (obj1.areLifecycleEventsInitialized()){
            mergeLifeCycleEvents(mergedPub.getLifecycleEvents(), obj1.getLifecycleEvents());
        }
        //merge experiments
        if (obj1.areExperimentsInitialized()){
            mergeExperiments(mergedPub, mergedPub.getExperiments(), obj1.getExperiments());
        }

        return mergedPub;
    }

    private void mergeExperiments(Publication mergedPub, Collection<Experiment> toEnrichExperiments, Collection<Experiment> sourceExperiments) {

        Iterator<Experiment> experimentIterator = toEnrichExperiments.iterator();
        while(experimentIterator.hasNext()){
            Experiment experiment = experimentIterator.next();
            boolean containsExperiment = false;
            for (Experiment experiment2 : sourceExperiments){
                if (experiment == experiment2){
                    containsExperiment = true;
                    break;
                }
            }
            // remove events not in second list
            if (!containsExperiment){
                experimentIterator.remove();
            }
        }
        experimentIterator = sourceExperiments.iterator();
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
                mergedPub.addExperiment(experiment);
            }
        }
    }

    /**
     * Takes two lists of lifecycle events and produces a list of those to add and those to remove.
     *
     * It will add all events from fetchedTerms that are not in collection to enrich. It will remove events in toEnrichEvents that are not in
     * sourceEvents
     *
     */
    private void mergeLifeCycleEvents(List<LifeCycleEvent> toEnrichEvents, List<LifeCycleEvent> sourceEvents){


        Iterator<LifeCycleEvent> eventIterator = toEnrichEvents.iterator();
        while(eventIterator.hasNext()){
            LifeCycleEvent event = eventIterator.next();
            boolean containsEvent = false;
            for (LifeCycleEvent event2 : sourceEvents){
                if (event == event2){
                    containsEvent = true;
                    break;
                }
            }
            // remove events not in second list
            if (!containsEvent){
                eventIterator.remove();
            }
        }

        eventIterator = sourceEvents.iterator();
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

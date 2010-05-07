package uk.ac.ebi.intact.curationTools.model.contexts;

import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;

import java.util.HashSet;
import java.util.Set;

/**
 * This specific context contains the list of the results on swissprot as well as the Trembl accession used for the blast.
 * This context is used when a swissprot remapping process has been done and we want to check that the sequence of the swissprot entries are not in conflict
 * with the feature ranges of the protein in Intact.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05-May-2010</pre>
 */

public class FeatureRangeCheckingContext extends UpdateContext {

    /**
     * The list of results from the blast on swissprot
     */
    private Set<BlastProtein> resultsOfSwissprotRemapping = new HashSet<BlastProtein> ();

    /**
     * the Trembl accession : its sequence has been used for the blast on swissprot
     */
    private String tremblAccession;

    /**
     * create a new FeatureRangeCheckingContext from a previous context of type UpdateContext
     * @param context : previous context
     */
    public FeatureRangeCheckingContext(UpdateContext context) {
        super(context);
        this.tremblAccession = null;
        setIntactAccession(context.getIntactAccession());
    }

    /**
     *
     * @return the list of results from swissprot
     */
    public Set<BlastProtein> getResultsOfSwissprotRemapping() {
        return resultsOfSwissprotRemapping;
    }

    /**
     *
     * @return the trembl accession
     */
    public String getTremblAccession() {
        return tremblAccession;
    }

    /**
     * add a new result from swissprot
     * @param resultsOfSwissprotRemapping : other result from swissprot
     */
    public void setResultsOfSwissprotRemapping(Set<BlastProtein> resultsOfSwissprotRemapping) {
        this.resultsOfSwissprotRemapping = resultsOfSwissprotRemapping;
    }

    /**
     * set the trembl accession
     * @param tremblAccession : new trembl accesion
     */
    public void setTremblAccession(String tremblAccession) {
        this.tremblAccession = tremblAccession;
    }
}

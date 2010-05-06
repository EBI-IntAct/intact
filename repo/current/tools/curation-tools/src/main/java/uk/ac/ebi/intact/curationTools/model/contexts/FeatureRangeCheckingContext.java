package uk.ac.ebi.intact.curationTools.model.contexts;

import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05-May-2010</pre>
 */

public class FeatureRangeCheckingContext extends UpdateContext {

    private Set<BlastProtein> resultsOfSwissprotRemapping = new HashSet<BlastProtein> ();
    private String tremblAccession;

    public FeatureRangeCheckingContext(IdentificationContext context) {
        super(context);
        this.tremblAccession = null;
    }

    public Set<BlastProtein> getResultsOfSwissprotRemapping() {
        return resultsOfSwissprotRemapping;
    }

    public String getTremblAccession() {
        return tremblAccession;
    }

    public void setResultsOfSwissprotRemapping(Set<BlastProtein> resultsOfSwissprotRemapping) {
        this.resultsOfSwissprotRemapping = resultsOfSwissprotRemapping;
    }

    public void setTremblAccession(String tremblAccession) {
        this.tremblAccession = tremblAccession;
    }
}

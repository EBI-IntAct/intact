package uk.ac.ebi.intact.protein.mapping.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.ncbiblast.BlastResultFilter;
import uk.ac.ebi.intact.bridges.ncbiblast.BlastServiceException;
import uk.ac.ebi.intact.bridges.ncbiblast.ProteinNCBIBlastService;
import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
import uk.ac.ebi.intact.protein.mapping.factories.ReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.contexts.BlastContext;

/**
 * this class is the class to extend if the action needs a BlastService
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-May-2010</pre>
 */

public abstract class ActionNeedingBlastService extends IdentificationActionImpl {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( ActionNeedingBlastService.class );

    /**
     * The NCBI blast service
     */
    protected ProteinNCBIBlastService blastService;

    /**
     * The BLAST filter
     */
    protected BlastResultFilter blastFilter;

    /**
     * The maximum number of BlastProtein instances we allow to keep in memory
     */
    protected static final int maxNumberOfBlastProteins = 10;

    /**
     * The minimum identity : below this identity percent, we don't look at the BLAST results
     */
    protected static final float minimumIdentityThreshold = (float) 95;

    /**
     * Create the process
     */
    public ActionNeedingBlastService(ReportsFactory factory){
        super(factory);
        try {
            this.blastService = new ProteinNCBIBlastService("marine@ebi.ac.uk");
            this.blastFilter = new BlastResultFilter();

        } catch (BlastServiceException e) {
            log.error("Problem instantiating the blast client.",e);
        }
    }

    /**
     *
     * @param protein : the blast protein
     * @return the sequence coverage of the alignment for the query sequence
     */
    protected float getQuerySequenceCoveragePercentFor(BlastProtein protein, BlastContext context){
        if (protein == null){
            return 0;
        }
        return ((float) (protein.getEndQuery() - protein.getStartQuery() + 1)) / (float) context.getSequence().length() * 100;
    }

    /**
     *
     * @param protein : the blast protein
     * @return the sequence coverage of the alignment for the match sequence
     */
    protected float getMatchSequenceCoveragePercentFor(BlastProtein protein){
        if (protein == null){
            return 0;
        }
        return ((float) (protein.getEndMatch() - protein.getStartMatch() + 1)) / (float) protein.getSequence().length() * 100;
    }
}

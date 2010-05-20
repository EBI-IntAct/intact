package uk.ac.ebi.intact.curationTools.model.actionReport;

import uk.ac.ebi.intact.curationTools.model.results.BlastResults;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * This specific report aims at storing the results of a BLAST
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Apr-2010</pre>
 */
@Entity
@MappedSuperclass
@Table( name = "ia_blast_report" )
public class BlastReport extends ActionReport{

    /**
     * The list of BLASTProteins
     */
    protected Set<BlastResults> listOfProteins = new HashSet<BlastResults>();

    /**
     * The sequence used for the blast
     */
    protected String querySequence;

    /**
     * Create a new BlastReport
     * @param name : the name of the action
     */
    public BlastReport(ActionName name){
         super(name);
        this.querySequence = null;
    }

    /**
     *
     * @return the list of Blast results
     */
    @OneToMany
    @JoinColumn(name="blast_results_ac")
    public Set<BlastResults> getBlastMatchingProteins(){
        return this.listOfProteins;
    }

    public void setBlastMatchingProteins(Set<BlastResults> blastResults){
        this.listOfProteins = blastResults;
    }

    /**
     *  add a blast protein
     * @param prot : new blast result
     */
    public void addBlastMatchingProtein(BlastResults prot){
        this.listOfProteins.add(prot);
    }

    /**
     *
     * @return the sequence used for the blast
     */
    @Transient
    public String getQuerySequence() {
        return querySequence;
    }

    /**
     * set the sequence used for the blast
     * @param querySequence : the sequence
     */
    public void setQuerySequence(String querySequence) {
        this.querySequence = querySequence;
    }
}

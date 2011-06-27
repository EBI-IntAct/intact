package uk.ac.ebi.intact.protein.mapping.model.actionReport.impl;

import org.apache.commons.collections.CollectionUtils;
import uk.ac.ebi.intact.protein.mapping.actions.ActionName;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.BlastReport;
import uk.ac.ebi.intact.protein.mapping.results.BlastResults;
import uk.ac.ebi.intact.protein.mapping.results.impl.DefaultBlastResults;

import java.util.HashSet;
import java.util.Set;

/**
 * This specific report aims at storing the results of a BLAST
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01-Apr-2010</pre>
 */
public class DefaultBlastReport extends DefaultMappingReport implements BlastReport<DefaultBlastResults>{

    /**
     * The list of BLASTProteins
     */
    protected Set<DefaultBlastResults> listOfProteins = new HashSet<DefaultBlastResults>();

    /**
     * The sequence used for the blast
     */
    protected String querySequence;

    /**
     * Create a new DefaultBlastReport
     * @param name : the name of the action
     */
    public DefaultBlastReport(ActionName name){
        super(name);
        this.querySequence = null;
    }

    /**
     *
     * @return the list of Blast results
     */
    public Set<DefaultBlastResults> getBlastMatchingProteins(){
        return this.listOfProteins;
    }

    public void setBlastMatchingProteins(Set<DefaultBlastResults> blastResults){
        this.listOfProteins = blastResults;
    }

    /**
     *  add a blast protein
     * @param prot : new blast result
     */
    public void addBlastMatchingProtein(DefaultBlastResults prot){
        this.listOfProteins.add(prot);
    }

    /**
     *
     * @return the sequence used for the blast
     */
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

    @Override
    public boolean equals( Object o ) {
        if ( !super.equals(o) ) {
            return false;
        }

        final DefaultBlastReport report = (DefaultBlastReport) o;

        if ( querySequence != null ) {
            if (!querySequence.equals( report.getQuerySequence() )){
                return false;
            }
        }
        else if (report.getQuerySequence() != null){
            return false;
        }

        return true;
    }

    /**
     * This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     *
     * @return hash code of the object.
     */
    @Override
    public int hashCode() {

        int code = 29;

        code = 29 * code + super.hashCode();

        if ( querySequence != null ) {
            code = 29 * code + querySequence.hashCode();
        }

        return code;
    }

    @Override
    public boolean isIdenticalTo(Object o){

        if (!super.isIdenticalTo(o)){
            return false;
        }

        final DefaultBlastReport report = (DefaultBlastReport) o;

        if ( querySequence != null ) {
            if (!querySequence.equals( report.getQuerySequence() )){
                return false;
            }
        }
        else if (report.getQuerySequence() != null){
            return false;
        }

        return CollectionUtils.isEqualCollection(listOfProteins, report.getBlastMatchingProteins());
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(super.toString() + "\n");

        buffer.append("Blast : " + querySequence != null ? querySequence : "");

        if (!listOfProteins.isEmpty()){
            buffer.append(" [ ");

            for (BlastResults prot : listOfProteins) {
                buffer.append(prot.toString() + " ; ");
            }
        }

        buffer.append(" ]");

        return buffer.toString();
    }
}

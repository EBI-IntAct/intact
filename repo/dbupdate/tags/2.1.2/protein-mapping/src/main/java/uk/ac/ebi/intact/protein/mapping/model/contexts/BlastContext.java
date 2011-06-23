package uk.ac.ebi.intact.protein.mapping.model.contexts;

/**
 * This specific context of a protein contains the ensembl gene accession of a Trembl entry. It is mostly used in the swissprot remapping process
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30-Mar-2010</pre>
 */

public class BlastContext extends IdentificationContext {

    /**
     * the ensembl gene ID of a trembl entry
     */
    private String ensemblGene;

    /**
     * create a new BlastContext
     */
    public BlastContext(){
        super();
        this.ensemblGene = null;
    }

    /**
     * Create a new BlastContext from a previous context (will keep the variable of the previous context)
     * @param context : previous context to clone
     */
    public BlastContext(IdentificationContext context){
        setSequence(context.getSequence());
        setIdentifier(context.getIdentifier());
        setOrganism(context.getOrganism());
        setGene_name(context.getGene_name());
        setProtein_name(context.getProtein_name());
        setGlobalName(context.getGlobalName());

        this.ensemblGene = null;
    }

    /**
     *
     * @return the ensembl gene accession
     */
    public String getEnsemblGene() {
        return ensemblGene;
    }

    /**
     * set the ensembl gene
     * @param ensemblGene : the ensembl gene accession of the Trembl entry
     */
    public void setEnsemblGene(String ensemblGene) {
        this.ensemblGene = ensemblGene;
    }

    /**
     * clean the variables of this context
     */
    public void clean(){
        super.clean();

        this.ensemblGene = null;
    }

}

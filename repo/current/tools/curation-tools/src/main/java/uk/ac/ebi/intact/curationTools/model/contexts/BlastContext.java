package uk.ac.ebi.intact.curationTools.model.contexts;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30-Mar-2010</pre>
 */

public class BlastContext extends IdentificationContext {

    private String ensemblGene;

    public BlastContext(){
        super();
    }

    public BlastContext(IdentificationContext context){
        setSequence(context.getSequence());
        setIdentifier(context.getIdentifier());
        setOrganism(context.getOrganism());
        setGene_name(context.getGene_name());
        setProtein_name(context.getProtein_name());

        this.ensemblGene = null;
    }

    public String getEnsemblGene() {
        return ensemblGene;
    }

    public void setEnsemblGene(String ensemblGene) {
        this.ensemblGene = ensemblGene;
    }

    public void clean(){
        super.clean();

        this.ensemblGene = null;
    }

}

package uk.ac.ebi.intact.curationTools.model.contexts;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30-Mar-2010</pre>
 */

public class TaxIdContext extends IdentificationContext{

    private String deducedTaxId;

    public TaxIdContext(){
        super();
    }

    public TaxIdContext(IdentificationContext context){
        setSequence(context.getSequence());
        setIdentifier(context.getIdentifier());
        setOrganism(context.getOrganism());
        setGene_name(context.getGene_name());
        setProtein_name(context.getProtein_name());

        this.deducedTaxId = null;
    }

    public String getDeducedTaxId() {
        return deducedTaxId;
    }

    public void setDeducedTaxId(String deducedTaxId) {
        this.deducedTaxId = deducedTaxId;
    }

    public void clean(){
        super.clean();

        this.deducedTaxId = null;
    }
}

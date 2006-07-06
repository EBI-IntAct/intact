package uk.ac.ebi.intact.util.msd.generator.msdGenerator;

/**
 * Created by IntelliJ IDEA. User: krobbe Date: 28-Mar-2006 Time: 20:52:03 To change this template use File | Settings |
 * File Templates.
 */
public class MsdParticipant {

    /*  PdbChainMappingBean
    private String PDBcode;  //entry_id
    private String PDBChainCode;//chain
    private String SPTR_AC; //SPTR_AC
    private String SPTR_ID; //SPTR_ID
    private BigDecimal taxid;//NCBI_TAX_ID
    private BigDecimal uniprotStart;//BEG_SEQ
    private BigDecimal uniprotEnd; //END_SEQ */

    /*  PdbChainBean
    private String pdbChainCode; //chain_pdb_code
    private String pdbCode;//entry_id
    private String type;// type
    private BigDecimal taxid;//chain_tax_id
    private BigDecimal expressedIntaxid;//system_tax_id
    private String tissue;//tissue*/

    private MsdInteraction msdInteraction;
    private String pdbCode;
    private String uniprotAc;
    private String uniprotId;
    private String taxid;
    private String uniprotStart;
    private String uniprotEnd;
    private String type;
    private String expressedIntaxid;
    private String tissue;

    public MsdInteraction getPdb() {
        return msdInteraction;
    }

    public void setPdb( MsdInteraction msdInteraction ) {
        this.msdInteraction = msdInteraction;
    }

    public String getPdbCode() {
        return pdbCode;
    }

    public void setPdbCode( String pdbCode ) {
        this.pdbCode = pdbCode;
    }

    public String getUniprotAc() {
        return uniprotAc;
    }

    public void setUniprotAc( String uniprotAc ) {
        this.uniprotAc = uniprotAc;
    }

    public String getUniprotId() {
        return uniprotId;
    }

    public void setUniprotId( String uniprotId ) {
        this.uniprotId = uniprotId;
    }

    public String getTaxid() {
        return taxid;
    }

    public void setTaxid( String taxid ) {
        this.taxid = taxid;
    }

    public String getUniprotStart() {
        return uniprotStart;
    }

    public void setUniprotStart( String uniprotStart ) {
        this.uniprotStart = uniprotStart;
    }

    public String getUniprotEnd() {
        return uniprotEnd;
    }

    public void setUniprotEnd( String uniprotEnd ) {
        this.uniprotEnd = uniprotEnd;
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public String getExpressedIntaxid() {
        return expressedIntaxid;
    }

    public void setExpressedIntaxid( String expressedIntaxid ) {
        this.expressedIntaxid = expressedIntaxid;
    }

    public String getTissue() {
        return tissue;
    }

    public void setTissue( String tissue ) {
        this.tissue = tissue;
    }


}

package uk.ac.ebi.intact.util.msd.model;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA. User: krobbe Date: 22-Mar-2006 Time: 16:29:46 To change this template use File | Settings |
 * File Templates.
 */

// It corresponds to INTACT_MSD_CHAIN_DATA view

public class PdbChainBean {
    private String pdbChainCode; //chain_pdb_code
    private String pdbCode;//entry_id
    private String type;// type
    private BigDecimal taxid;//chain_tax_id
    private BigDecimal expressedIntaxid;//system_tax_id
    private String tissue;//tissue

    /*
 unused fields:
    private String plasmid;
    private String hostVector;
    private String hostVectorType;
    private String plasmidDetails;
*/

    /**
     * Getter of the pdbChainCode String pdbChainCode corresponds to chain_pdb_code field of INTACT_MSD_CHAIN_DATA view
     *
     * @return pdbChainCode
     */
    public String getPdbChainCode() {
        return pdbChainCode;
    }

    /**
     * Setter of the pdbChainCode String pdbChainCode corresponds to chain_pdb_code field of INTACT_MSD_CHAIN_DATA view
     *
     * @param pdbChainCode
     */
    public void setPdbChainCode( String pdbChainCode ) {
        this.pdbChainCode = pdbChainCode;
    }

    /**
     * Getter of the pdbCode String pdbChainCode corresponds to entry_id field of INTACT_MSD_CHAIN_DATA view
     *
     * @return pdbCode
     */
    public String getPdbCode() {
        return pdbCode;
    }

    /**
     * Setter of the pdbCode String pdbChainCode corresponds to entry_id field of INTACT_MSD_CHAIN_DATA view
     *
     * @param pdbCode
     */
    public void setPdbCode( String pdbCode ) {
        this.pdbCode = pdbCode;
    }

    /**
     * Getter of the type String type corresponds to the type field of INTACT_MSD_CHAIN_DATA view
     *
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Setter of the type String type corresponds to the type field of INTACT_MSD_CHAIN_DATA view
     *
     * @param type
     */
    public void setType( String type ) {
        this.type = type;
    }

    /**
     * Getter of the BigDecimal taxid taxid corresponds to the chain_tax_id of INTACT_MSD_CHAIN_DATA view
     *
     * @return taxid
     */
    public BigDecimal getTaxid() {
        return taxid;
    }

    /**
     * Setter of the BigDecimal taxid taxid corresponds to the chain_tax_id of INTACT_MSD_CHAIN_DATA view
     *
     * @param taxid
     */
    public void setTaxid( BigDecimal taxid ) {
        this.taxid = taxid;
    }

    /**
     * Getter of the BigDecimal expressedIntaxid taxid corresponds to the system_tax_id of INTACT_MSD_CHAIN_DATA view
     *
     * @return expressedIntaxid
     */
    public BigDecimal getExpressedIntaxid() {
        return expressedIntaxid;
    }

    /**
     * Setter of the BigDecimal expressedIntaxid taxid corresponds to the system_tax_id field of INTACT_MSD_CHAIN_DATA
     * view
     *
     * @param expressedIntaxid
     */
    public void setExpressedIntaxid( BigDecimal expressedIntaxid ) {
        this.expressedIntaxid = expressedIntaxid;
    }

    /**
     * Getter of the tissue String tissue corresponds to the tissue field of INTACT_MSD_CHAIN_DATA view
     *
     * @return tissue
     */
    public String getTissue() {
        return tissue;
    }

    /**
     * Setter of the tissue String tissue corresponds to the tissue field of INTACT_MSD_CHAIN_DATA view
     *
     * @param tissue
     */
    public void setTissue( String tissue ) {
        this.tissue = tissue;
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final PdbChainBean that = (PdbChainBean) o;

        if ( expressedIntaxid != null ? !expressedIntaxid.equals( that.expressedIntaxid ) : that.expressedIntaxid != null )
        {
            return false;
        }
        if ( pdbChainCode != null ? !pdbChainCode.equals( that.pdbChainCode ) : that.pdbChainCode != null ) {
            return false;
        }
        if ( pdbCode != null ? !pdbCode.equals( that.pdbCode ) : that.pdbCode != null ) {
            return false;
        }
        if ( taxid != null ? !taxid.equals( that.taxid ) : that.taxid != null ) {
            return false;
        }
        if ( tissue != null ? !tissue.equals( that.tissue ) : that.tissue != null ) {
            return false;
        }
        if ( type != null ? !type.equals( that.type ) : that.type != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = ( pdbChainCode != null ? pdbChainCode.hashCode() : 0 );
        result = 29 * result + ( pdbCode != null ? pdbCode.hashCode() : 0 );
        result = 29 * result + ( type != null ? type.hashCode() : 0 );
        result = 29 * result + ( taxid != null ? taxid.hashCode() : 0 );
        result = 29 * result + ( expressedIntaxid != null ? expressedIntaxid.hashCode() : 0 );
        result = 29 * result + ( tissue != null ? tissue.hashCode() : 0 );
        return result;
    }
}



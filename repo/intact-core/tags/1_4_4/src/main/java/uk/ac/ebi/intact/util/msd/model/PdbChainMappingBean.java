package uk.ac.ebi.intact.util.msd.model;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA. User: krobbe Date: 23-Mar-2006 Time: 17:26:47 To change this template use File | Settings |
 * File Templates.
 */

// It corresponds to INTACT_MSD_UNP_DATA view   (UniProt mapping)

public class PdbChainMappingBean {
    private String pdbCode;  //entry_id
    private String pdbChainCode;//chain
    private String uniprotAc; //SPTR_AC
    private String uniprotId; //SPTR_ID
    private BigDecimal taxid;//NCBI_TAX_ID
    private BigDecimal uniprotStart;//BEG_SEQ
    private BigDecimal uniprotEnd; //END_SEQ


    public String getPdbCode() {
        return pdbCode;
    }

    public void setPdbCode( String pdbCode ) {
        this.pdbCode = pdbCode;
    }

    public String getPdbChainCode() {
        return pdbChainCode;
    }

    public void setPdbChainCode( String pdbChainCode ) {
        this.pdbChainCode = pdbChainCode;
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

    public BigDecimal getTaxid() {
        return taxid;
    }

    public void setTaxid( BigDecimal taxid ) {
        this.taxid = taxid;
    }

    public BigDecimal getUniprotStart() {
        return uniprotStart;
    }

    public void setUniprotStart( BigDecimal start ) {
        this.uniprotStart = start;
    }

    public BigDecimal getUniprotEnd() {
        return uniprotEnd;
    }

    public void setUniprotEnd( BigDecimal end ) {
        this.uniprotEnd = end;
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final PdbChainMappingBean that = (PdbChainMappingBean) o;

        if ( pdbChainCode != null ? !pdbChainCode.equals( that.pdbChainCode ) : that.pdbChainCode != null ) {
            return false;
        }
        if ( pdbCode != null ? !pdbCode.equals( that.pdbCode ) : that.pdbCode != null ) {
            return false;
        }
        if ( taxid != null ? !taxid.equals( that.taxid ) : that.taxid != null ) {
            return false;
        }
        if ( uniprotAc != null ? !uniprotAc.equals( that.uniprotAc ) : that.uniprotAc != null ) {
            return false;
        }
        if ( uniprotEnd != null ? !uniprotEnd.equals( that.uniprotEnd ) : that.uniprotEnd != null ) {
            return false;
        }
        if ( uniprotId != null ? !uniprotId.equals( that.uniprotId ) : that.uniprotId != null ) {
            return false;
        }
        if ( uniprotStart != null ? !uniprotStart.equals( that.uniprotStart ) : that.uniprotStart != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = ( pdbCode != null ? pdbCode.hashCode() : 0 );
        result = 29 * result + ( pdbChainCode != null ? pdbChainCode.hashCode() : 0 );
        result = 29 * result + ( uniprotAc != null ? uniprotAc.hashCode() : 0 );
        result = 29 * result + ( uniprotId != null ? uniprotId.hashCode() : 0 );
        result = 29 * result + ( taxid != null ? taxid.hashCode() : 0 );
        result = 29 * result + ( uniprotStart != null ? uniprotStart.hashCode() : 0 );
        result = 29 * result + ( uniprotEnd != null ? uniprotEnd.hashCode() : 0 );
        return result;
    }
}

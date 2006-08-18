package uk.ac.ebi.intact.util.msd.model;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA. User: krobbe Date: 22-Mar-2006 Time: 15:50:13 To change this template use File | Settings |
 * File Templates.
 */

// It corresponds to INTACT_MSD_DATA view

public class PdbBean {

    /**
     * String corresponding to the entry_id field of the INTACT_MSD_DATA view.
     */
    private String pdbCode;
    private String title;//title
    private String experimentType;// experiment_type
    private BigDecimal resolution;  // res_val
    private BigDecimal rFree; //r_free
    private BigDecimal rWork; //r_work
    private String moleculeList;//comp_list
    private BigDecimal pmid;//pubmedid
    private String oligomericStateList; //oligomeric_state

    /**
     * Getter of the rFree BigDecimal rFree corresponds to the r_free field in INTACT_MSD_DATA view
     *
     * @return rFree
     */
    public BigDecimal getrFree() {
        return rFree;
    }

    /**
     * Setter of rFree BigDecimal rFree corresponds to the r_free field in INTACT_MSD_DATA view
     *
     * @param rFree
     */
    public void setrFree( BigDecimal rFree ) {
        this.rFree = rFree;
    }

    /**
     * Getter of rFree BigDecimal pmid corresponds to the pubmedid field in INTACT_MSD_DATA view
     *
     * @return pmid
     */
    public BigDecimal getPmid() {
        return pmid;
    }

    /**
     * Setter of the pmid BigDecimal pmid corresponds to the pubmedid field in INTACT_MSD_DATA view
     *
     * @param pmid
     */
    public void setPmid( BigDecimal pmid ) {
        this.pmid = pmid;
    }


    /**
     * Getter of resolution resolution corresponds to the res_val field of INTACT_MSD_DATA view
     *
     * @return resolution
     */
    public BigDecimal getResolution() {
        return resolution;
    }

    /**
     * Setter of resolution resolution corresponds to the res_val field of INTACT_MSD_DATA view
     *
     * @param resolution
     */
    public void setResolution( BigDecimal resolution ) {
        this.resolution = resolution;
    }

    /**
     * Getter of the rWork BigDecimal rWork corresponds to the r_work field of INTACT_MSD_DATA view
     *
     * @return rWork
     */
    public BigDecimal getrWork() {
        return rWork;
    }

    /**
     * Setter of the rWork BigDecimal rWork corresponds to the r_work field of INTACT_MSD_DATA view
     *
     * @param rWork
     */
    public void setrWork( BigDecimal rWork ) {
        this.rWork = rWork;
    }

    /**
     * Getter of the pdbCode String pdbCode corresponds to the entry_id field of INTACT_MSD_DATA view
     *
     * @return pdbCode
     */
    public String getPdbCode() {
        return pdbCode;
    }

    /**
     * Setter of the pdbCode String pdbCode corresponds to the entry_id field of INTACT_MSD_DATA view
     *
     * @param pdbCode
     */
    public void setPdbCode( String pdbCode ) {
        this.pdbCode = pdbCode;
    }

    /**
     * Getter of the experimentType String experimentType corresponds to the experiment_type field of INTACT_MSD_DATA
     * view
     *
     * @return experimentType
     */
    public String getExperimentType() {
        return experimentType;
    }

    /**
     * Setter of the experimentType String experimentType corresponds to the experiment_type field of INTACT_MSD_DATA
     * view
     *
     * @param experimentType
     */
    public void setExperimentType( String experimentType ) {
        this.experimentType = experimentType;
    }

    /**
     * Getter of the moleculeList String moleculeList corresponds to the comp_list field of INTACT_MSD_DATA view
     *
     * @return moleculeList
     */
    public String getMoleculeList() {
        return moleculeList;
    }

    /**
     * Setter of the moleculeList String moleculeList corresponds to the comp_list field of INTACT_MSD_DATA view
     *
     * @param moleculeList
     */
    public void setMoleculeList( String moleculeList ) {
        this.moleculeList = moleculeList;
    }

    /**
     * Getter of the oligomericStateList String oligomericStateList corresponds to the field oligomeric_state of
     * INTACT_MSD_DATA view
     *
     * @return oligomericStateList
     */
    public String getOligomericStateList() {
        return oligomericStateList;
    }

    /**
     * Setter of the oligomericStateList String oligomericStateList corresponds to the field oligomeric_state of
     * INTACT_MSD_DATA view
     *
     * @param oligomericStateList
     */
    public void setOligomericStateList( String oligomericStateList ) {
        this.oligomericStateList = oligomericStateList;
    }

    /**
     * Getter of the title String title correspond to the title fiels of INTACT_MSD_DATA view
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter of the title String title correspond to the title fiels of INTACT_MSD_DATA view
     *
     * @param title
     */
    public void setTitle( String title ) {
        this.title = title;
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final PdbBean pdbBean = (PdbBean) o;

        if ( experimentType != null ? !experimentType.equals( pdbBean.experimentType ) : pdbBean.experimentType != null ) {
            return false;
        }
        if ( moleculeList != null ? !moleculeList.equals( pdbBean.moleculeList ) : pdbBean.moleculeList != null ) {
            return false;
        }
        if ( oligomericStateList != null ? !oligomericStateList.equals( pdbBean.oligomericStateList ) : pdbBean.oligomericStateList != null )
        {
            return false;
        }
        if ( pdbCode != null ? !pdbCode.equals( pdbBean.pdbCode ) : pdbBean.pdbCode != null ) {
            return false;
        }
        if ( pmid != null ? !pmid.equals( pdbBean.pmid ) : pdbBean.pmid != null ) {
            return false;
        }
        if ( rFree != null ? !rFree.equals( pdbBean.rFree ) : pdbBean.rFree != null ) {
            return false;
        }
        if ( rWork != null ? !rWork.equals( pdbBean.rWork ) : pdbBean.rWork != null ) {
            return false;
        }
        if ( resolution != null ? !resolution.equals( pdbBean.resolution ) : pdbBean.resolution != null ) {
            return false;
        }
        if ( title != null ? !title.equals( pdbBean.title ) : pdbBean.title != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = ( pdbCode != null ? pdbCode.hashCode() : 0 );
        result = 29 * result + ( title != null ? title.hashCode() : 0 );
        result = 29 * result + ( experimentType != null ? experimentType.hashCode() : 0 );
        result = 29 * result + ( resolution != null ? resolution.hashCode() : 0 );
        result = 29 * result + ( rFree != null ? rFree.hashCode() : 0 );
        result = 29 * result + ( rWork != null ? rWork.hashCode() : 0 );
        result = 29 * result + ( moleculeList != null ? moleculeList.hashCode() : 0 );
        result = 29 * result + ( pmid != null ? pmid.hashCode() : 0 );
        result = 29 * result + ( oligomericStateList != null ? oligomericStateList.hashCode() : 0 );
        return result;
    }
}


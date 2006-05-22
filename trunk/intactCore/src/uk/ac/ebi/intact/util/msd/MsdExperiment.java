package uk.ac.ebi.intact.util.msd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: krobbe
 * Date: 24-Mar-2006
 * Time: 15:54:33
 * To change this template use File | Settings | File Templates.
 */
public class MsdExperiment {


    private String pmid;
    private String experimentType;
    private Collection pdbList;

    private static HashMap pmid2exp; // pmid ==> collection experiments
                                     //     if (pmid2exps.contains(pmid)){
                                     //         Collection exps = pmid2exps.get(pmid); ...

    /**public MsdExperiment (String pmid, String interactionType){

  }  **/


    /** Equals Method for MsdExperiment
     A MsdExperiment equals another MsdExperiment only if experimentType and pmid are the same
     The list of MsdInteraction can be different as the list can grow**/
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        final MsdExperiment that = (MsdExperiment) o;
        if (pmid != null ? !pmid.equals(that.pmid) : that.pmid != null){
            return false;
        }
        if (experimentType != null ? !experimentType.equals(that.experimentType) : that.experimentType != null){
            return false;
        }
        return true;
    }

    /**
    public int hashCode() {
        int result;
        result = (pmid != null ? pmid.hashCode() : 0);
        result = 29 * result + (experimentType != null ? experimentType.hashCode() : 0);
        return result;
    } **/


    public void addPdb (MsdInteraction pdb){
        if (this.pdbList == null) {
            this.pdbList = new ArrayList();
        }
        pdbList.add(pdb);
    }

    public void addPmid(String pmid, MsdExperiment exp){
        if(MsdExperiment.pmid2exp == null){
            MsdExperiment.pmid2exp= new HashMap();
        }
        if ( pmid2exp.containsKey(pmid)){

        }
        pmid2exp.put(pmid,exp);
    }


    public HashMap getPmid2exp() {
        return MsdExperiment.pmid2exp;
    }


    public String getExperimentType() {
        return experimentType;
    }


    public Collection getPdbList() {
        return pdbList;
    }

    public void setPdbList(ArrayList pdbList) {
        this.pdbList = pdbList;
    }



    public void setExperimentType(String experimentType) {
        this.experimentType = experimentType;
    }


    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public static void setPmid2Exp(HashMap pmid2exp) {
        MsdExperiment.pmid2exp = pmid2exp;
    }


    public String toString() {
        return "MsdExperiment{" +
                "pmid='" + pmid + '\'' +
                ", experimentType='" + experimentType + '\'' +
                ", pdbList=" + pdbList +
                '}';
    }
}





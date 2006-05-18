package uk.ac.ebi.intact.util.msd;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: krobbe
 * Date: 24-Mar-2006
 * Time: 15:54:33
 * To change this template use File | Settings | File Templates.
 */
public class Experiment {


    private String pmid;
    private String experimentType;
    private ArrayList pdbList;
    private static HashMap pmid2exp; // pmid ==> collection experiments
                                     // if(pmid2exps.contains(pmid)){
                                     //     Collection exps = pmid2exps.get(pmid); ...
                                     //
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Experiment that = (Experiment) o;

        if (experimentType != null ? !experimentType.equals(that.experimentType) : that.experimentType != null)
            return false;
        if (pdbList != null ? !pdbList.equals(that.pdbList) : that.pdbList != null) return false;
        if (pmid != null ? !pmid.equals(that.pmid) : that.pmid != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (pmid != null ? pmid.hashCode() : 0);
        result = 29 * result + (experimentType != null ? experimentType.hashCode() : 0);
        return result;
    }


    public void AddPDB(Pdb pdb){
        if (this.pdbList == null) {
            this.pdbList = new ArrayList();
        }
        System.out.println(this.pdbList.getClass());
        System.out.println("length "+this.pdbList.size());
        System.out.println("list:"+this.pdbList);
        this.pdbList.add(pdb);
        System.out.println("length "+this.pdbList.size());
        System.out.println("list:"+this.pdbList);
    }

    public void Addpmid(String pmid, Experiment exp){
        if(Experiment.pmid2exp == null){
            Experiment.pmid2exp= new HashMap();
        }
        exp=this;
        Experiment.pmid2exp.put(pmid,exp);
        System.out.println("exp:"+Experiment.pmid2exp);
        System.out.println("find"+Experiment.pmid2exp.values());
    }

    public HashMap getPmid2exp() {
        return Experiment.pmid2exp;
    }


    public String getExperimentType() {
        return experimentType;
    }


    public ArrayList getPdbList() {
        return pdbList;
    }

    public void setPdbList(ArrayList pdbList) {
        this.pdbList = pdbList;
    }



/*     public Experiment(){
}

*public Experiment(pmid){
    this.setPmid(pmid);
}
public Experiment(pmid,experimentType){
    this.setExperimentType(experimentType);
    this.setPmid(pmid);
}

public String getExperimentType() {
    return experimentType;
}*/


    public void setExperimentType(String experimentType) {
        this.experimentType = experimentType;
    }




    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public static void setPmid2exp(HashMap pmid2exp) {
        Experiment.pmid2exp = pmid2exp;
    }
}

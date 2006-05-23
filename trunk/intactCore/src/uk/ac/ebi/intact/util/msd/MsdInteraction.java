package uk.ac.ebi.intact.util.msd;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: krobbe
 * Date: 24-Mar-2006
 * Time: 15:51:38
 * To change this template use File | Settings | File Templates.
 */
public class MsdInteraction {
    private String pdbCode;
    private MsdExperiment msdExperiment;
    private String oligomericStateList;
    private String experimentType;
    private String pmid;
    private String resolution;
    private String rWork;
    private String rFree;
    private ArrayList chainList;
    private String title;
    private boolean hasNucleicAcid = false;

    public boolean getHasNucleicAcid() {
        return hasNucleicAcid;
    }

    public void setHasNucleicAcid(boolean hasNucleicAcid) {
        this.hasNucleicAcid = hasNucleicAcid;
    }

    public ArrayList getChainList() {
        return chainList;
    }

    public void setChainList(ArrayList chainList) {
        this.chainList = chainList;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getrFree() {
        return rFree;
    }

    public void setrFree(String rFree) {
        this.rFree = rFree;
    }




    public String getOligomericStateList() {
        return oligomericStateList;
    }

    public void setOligomericStateList(String oligomericStateList) {
        this.oligomericStateList = oligomericStateList;
    }



    public String getExperimentType() {
        return experimentType;
    }

    public void setExperimentType(String experimentType) {
        this.experimentType = experimentType;
    }




    public String getPdbCode() {
        return pdbCode;
    }

    public void setPdbCode(String pdbCode) {
        this.pdbCode = pdbCode;
    }

    public MsdExperiment getExperiment() {
        return msdExperiment;
    }

    public void setExperiment(MsdExperiment msdExperiment) {
        this.msdExperiment = msdExperiment;
    }

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getrWork() {
        return rWork;
    }

    public void setrWork(String rWork) {
        this.rWork = rWork;
    }


    public String toString() {
        return "MsdInteraction{" +
                "pdbCode='" + pdbCode + '\'' +
               // ", msdExperiment=" + msdExperiment +
                ", oligomericStateList='" + oligomericStateList + '\'' +
                ", experimentType='" + experimentType + '\'' +
                ", pmid='" + pmid + '\'' +
                ", resolution='" + resolution + '\'' +
                ", rWork='" + rWork + '\'' +
                ", rFree='" + rFree + '\'' +
                ", chainList=" + chainList +
                ", title='" + title + '\'' +
                ", hasNucleicAcid=" + hasNucleicAcid +
                '}';
    }
}

package uk.ac.ebi.intact.util.msd.generator.msdGenerator;

import uk.ac.ebi.intact.model.CvObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA. User: krobbe Date: 24-Mar-2006 Time: 15:54:33 To change this template use File | Settings |
 * File Templates.
 */
public class MsdExperiment {


    private String pmid;
    private String experimentType;
    private CvObject cvIntDetect;
    private Collection<MsdInteraction> msdInteractions;

    /**
     * Equals Method for MsdExperiment A MsdExperiment equals another MsdExperiment only if experimentType (thus the cv)
     * and pmid are the same. The list of MsdInteraction can be different as the list can grow*
     */
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final MsdExperiment that = (MsdExperiment) o;

        if ( cvIntDetect != null ? !cvIntDetect.equals( that.cvIntDetect ) : that.cvIntDetect != null ) {
            return false;
        }
        if ( experimentType != null ? !experimentType.equals( that.experimentType ) : that.experimentType != null ) {
            return false;
        }
        if ( pmid != null ? !pmid.equals( that.pmid ) : that.pmid != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = ( pmid != null ? pmid.hashCode() : 0 );
        result = 29 * result + ( experimentType != null ? experimentType.hashCode() : 0 );
        result = 29 * result + ( cvIntDetect != null ? cvIntDetect.hashCode() : 0 );
        return result;
    }


    public void addMsdInteraction( MsdInteraction msdInteraction ) {
        if ( this.msdInteractions == null ) {
            this.msdInteractions = new ArrayList();
        }
        msdInteractions.add( msdInteraction );
    }


    public String getExperimentType() {
        return experimentType;
    }


    public Collection<MsdInteraction> getMsdInteractions() {
        return msdInteractions;
    }

    public void setMsdInteractions( Collection msdInteractions ) {
        this.msdInteractions = msdInteractions;
    }


    public void setExperimentType( String experimentType ) {
        this.experimentType = experimentType;
    }


    public String getPmid() {
        return pmid;
    }

    public void setPmid( String pmid ) {
        this.pmid = pmid;
    }

    public CvObject getCvIntDetect() {
        return cvIntDetect;
    }

    public void setCvIntDetect( CvObject cvIntDetect ) {
        this.cvIntDetect = cvIntDetect;
    }

    public String toString() {
        return "MsdExperiment{" +
               "pmid='" + pmid + '\'' +
               ", experimentType='" + experimentType + '\'' +
               ", cv='" + cvIntDetect + '\'' +
               ", msdInteractions=" + msdInteractions +
               '}';
    }
}





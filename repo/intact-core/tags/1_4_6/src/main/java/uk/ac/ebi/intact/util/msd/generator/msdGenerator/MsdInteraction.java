package uk.ac.ebi.intact.util.msd.generator.msdGenerator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA. User: krobbe Date: 24-Mar-2006 Time: 15:51:38 To change this template use File | Settings |
 * File Templates.
 */
public class MsdInteraction {
    private String pdbCode;
    private String oligomericStateList;
    private String resolution;
    private String rWork;
    private String rFree;
    private Collection msdParticipants;
    private String title;
    private String moleculeList;
    private boolean hasNucleicAcid = false;

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final MsdInteraction that = (MsdInteraction) o;

        if ( hasNucleicAcid != that.hasNucleicAcid ) {
            return false;
        }
        if ( moleculeList != null ? !moleculeList.equals( that.moleculeList ) : that.moleculeList != null ) {
            return false;
        }
        if ( msdParticipants != null ? !msdParticipants.equals( that.msdParticipants ) : that.msdParticipants != null )
        {
            return false;
        }
        if ( oligomericStateList != null ? !oligomericStateList.equals( that.oligomericStateList ) : that.oligomericStateList != null )
        {
            return false;
        }
        if ( !pdbCode.equals( that.pdbCode ) ) {
            return false;
        }
        if ( rFree != null ? !rFree.equals( that.rFree ) : that.rFree != null ) {
            return false;
        }
        if ( rWork != null ? !rWork.equals( that.rWork ) : that.rWork != null ) {
            return false;
        }
        if ( resolution != null ? !resolution.equals( that.resolution ) : that.resolution != null ) {
            return false;
        }
        if ( title != null ? !title.equals( that.title ) : that.title != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = pdbCode.hashCode();
        result = 29 * result + ( oligomericStateList != null ? oligomericStateList.hashCode() : 0 );
        result = 29 * result + ( resolution != null ? resolution.hashCode() : 0 );
        result = 29 * result + ( rWork != null ? rWork.hashCode() : 0 );
        result = 29 * result + ( rFree != null ? rFree.hashCode() : 0 );
        result = 29 * result + ( msdParticipants != null ? msdParticipants.hashCode() : 0 );
        result = 29 * result + ( title != null ? title.hashCode() : 0 );
        result = 29 * result + ( moleculeList != null ? moleculeList.hashCode() : 0 );
        result = 29 * result + ( hasNucleicAcid ? 1 : 0 );
        return result;
    }

    public boolean getHasNucleicAcid() {
        return hasNucleicAcid;
    }

    public void setHasNucleicAcid( boolean hasNucleicAcid ) {
        this.hasNucleicAcid = hasNucleicAcid;
    }

    public Collection getMsdParticipants() {
        return msdParticipants;
    }

    public void setMsdParticipants( Collection msdParticipants ) {
        if ( this.msdParticipants == null ) {this.msdParticipants = new ArrayList();}
        this.msdParticipants = msdParticipants;
    }

    public void addParticipant( MsdParticipant msdParticipant ) {
        if ( this.msdParticipants == null ) { this.msdParticipants = new ArrayList();}
        this.msdParticipants.add( msdParticipant );
    }


    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }


    public String getrFree() {
        return rFree;
    }

    public void setrFree( String rFree ) {
        this.rFree = rFree;
    }


    public String getOligomericStateList() {
        return oligomericStateList;
    }

    public void setOligomericStateList( String oligomericStateList ) {
        this.oligomericStateList = oligomericStateList;
    }


    public String getPdbCode() {
        return pdbCode;
    }

    public void setPdbCode( String pdbCode ) {
        this.pdbCode = pdbCode;
    }


    public String getResolution() {
        return resolution;
    }

    public void setResolution( String resolution ) {
        this.resolution = resolution;
    }

    public String getrWork() {
        return rWork;
    }

    public void setrWork( String rWork ) {
        this.rWork = rWork;
    }

    public String getMoleculeList() {
        return moleculeList;
    }

    public void setMoleculeList( String moleculeList ) {
        this.moleculeList = moleculeList;
    }


    public String toString() {
        return "MsdInteraction{" +
               "pdbCode='" + pdbCode + '\'' +
               // ", msdExperiment=" + msdExperiment +
               ", oligomericStateList='" + oligomericStateList + '\'' +
               ", resolution='" + resolution + '\'' +
               ", rWork='" + rWork + '\'' +
               ", rFree='" + rFree + '\'' +
               ", chainList=" + msdParticipants +
               ", title='" + title + '\'' +
               ", hasNucleicAcid=" + hasNucleicAcid +
               '}';
    }
}
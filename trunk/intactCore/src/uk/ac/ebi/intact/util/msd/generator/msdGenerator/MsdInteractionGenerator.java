package uk.ac.ebi.intact.util.msd.generator.msdGenerator;

import uk.ac.ebi.intact.util.msd.model.PdbBean;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA. User: karine Date: 18 mai 2006 Time: 10:28:40 To change this template use File | Settings |
 * File Templates.
 */
public class MsdInteractionGenerator {

    public MsdInteraction createInt( PdbBean pdbBean, MsdExperiment msdExp ) {

        /**
         PdbBean :
         private String pdbCode;
         private String title;//title
         private String experimentType;// experiment_type
         private BigDecimal resolution;  // res_val
         private BigDecimal rFree; //r_free
         private BigDecimal rWork; //r_work
         private String moleculeList;//comp_list
         private BigDecimal pmid;//pubmedid
         private String oligomericStateList; //oligomeric_state
         **/
        MsdInteraction msdInteraction = new MsdInteraction();

        // add the msdInteraction to the collection of the experiment
        msdExp.addMsdInteraction( msdInteraction );

        //set PDBcode
        msdInteraction.setPdbCode( pdbBean.getPdbCode() );

        //set Title
        String title = pdbBean.getTitle();
        if ( title != null ) {
            msdInteraction.setTitle( title.trim() );
        } else {
            msdInteraction.setTitle( null );
        }

        //set Resolution
        BigDecimal resolution = pdbBean.getResolution();
        if ( resolution != null ) {
            msdInteraction.setResolution( resolution.toString() );
        } else {
            msdInteraction.setResolution( null );
        }

        //set rWork
        BigDecimal rWork = pdbBean.getrWork();
        if ( rWork != null ) {
            BigDecimal h = new BigDecimal( 100 );
            rWork = rWork.multiply( h );
            rWork = h.subtract( rWork );
            String rWorkS = rWork.toString();
            msdInteraction.setrWork( rWorkS );
        } else {
            msdInteraction.setrWork( null );
        }

        // set rFree
        BigDecimal rFree = pdbBean.getrFree();
        if ( rFree != null ) {
            BigDecimal h = new BigDecimal( 100 );
            rFree = rFree.multiply( h );
            rFree = h.subtract( rFree );
            String rFreeS = rFree.toString();
            msdInteraction.setrFree( rFreeS );
        } else {
            msdInteraction.setrFree( null );
        }

        // set oligomericStateList
        msdInteraction.setOligomericStateList( pdbBean.getOligomericStateList() );

        //set moleculeList
        String moleculeList = pdbBean.getMoleculeList();
        if ( moleculeList != null ) {
            msdInteraction.setMoleculeList( moleculeList.trim() );
        } else {
            msdInteraction.setMoleculeList( null );
        }

        // participant
        MsdParticipantGenerator msdParticipantGenerator = new MsdParticipantGenerator();
        //TODO msdParticipantGenerator.createChains(msdInteraction);

        return msdInteraction;


    }
}


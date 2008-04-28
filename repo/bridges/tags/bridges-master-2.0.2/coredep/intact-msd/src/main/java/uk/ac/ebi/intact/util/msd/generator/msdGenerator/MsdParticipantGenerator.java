package uk.ac.ebi.intact.util.msd.generator.msdGenerator;

import uk.ac.ebi.intact.util.msd.model.PdbChainBean;
import uk.ac.ebi.intact.util.msd.model.PdbChainMappingBean;
import uk.ac.ebi.intact.util.msd.util.MsdHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA. User: karine Date: 18 mai 2006 Time: 10:29:13 To change this template use File | Settings |
 * File Templates.
 */
public class MsdParticipantGenerator {

    public void createChains( MsdInteraction msdInteraction ) throws Exception, SQLException {

        if ( msdInteraction != null ) {

            Collection chainList = new ArrayList();
            MsdHelper helper = new MsdHelper();
            helper.addMapping( PdbChainBean.class, "SELECT entry_id as pdbCode, " +
                                                   "chain_pdb_code as pdbChainCode, " +
                                                   "type, " +
                                                   "system_tax_id as expressedIntaxid, " +
                                                   "tissue " +
                                                   "FROM INTACT_MSD_CHAIN_DATA " +
                                                   "where entry_id=?" );

            for ( Iterator<PdbChainBean> iterator = helper.getBeans( PdbChainBean.class, msdInteraction.getPdbCode() ).iterator(); iterator.hasNext(); )
            {
                PdbChainBean pdbChainBean = iterator.next();
                /**System.out.println(pdbChainBean.getClass());
                 System.out.println("chain: "+ pdbChainBean.getPdbChainCode());
                 System.out.println("pdb: "+ pdbChainBean.getPdbCode());
                 System.out.println("expressedIntaxid: "+ pdbChainBean.getExpressedIntaxid());**/

                // if it is a nucleic acid
                //RULE: find and withdrawn all MsdInteraction involving nucleic acids.
                if ( pdbChainBean.getType() == "Nucleic_Acid" ) {

                    msdInteraction.setHasNucleicAcid( true );
                    //rise an exception
                }


                if ( pdbChainBean.getType() == "Protein" ) {

                    MsdParticipant msdParticipant = new MsdParticipant();
                    msdInteraction.addParticipant( msdParticipant );
                    //msdParticipant.setPdb(msdInteraction);

/*  private String PDBcode;  //entry_id
private String PDBChainCode;//chain
private String SPTR_AC; //SPTR_AC
private String SPTR_ID; //SPTR_ID
private String taxid;//NCBI_TAX_ID
private String start;//BEG_SEQ
private String end; //END_SEQ */

                    MsdHelper helper2 = new MsdHelper();
                    helper2.addMapping( PdbChainMappingBean.class, "SELECT entry_id as pdbCode, " +
                                                                   "chain as pdbChainCode, " +
                                                                   "SPTR_AC as sptr_ac, SPTR_ID as sptr_id, " +
                                                                   "NCBI_TAX_ID as taxid, " +
                                                                   "BEG_SEQ as UniProtStart, END_SEQ as uniProtEnd " +
                                                                   "FROM INTACT_MSD_UNP_DATA " +
                                                                   //"where entry_id=? and chain='A'");
                                                                   "where entry_id=? and chain='" + pdbChainBean.getPdbChainCode() + "'" );

                    for ( Iterator<PdbChainMappingBean> iterator2 = helper2.getBeans( PdbChainMappingBean.class, msdInteraction.getPdbCode() ).iterator(); iterator2.hasNext(); )
                    {
                        PdbChainMappingBean pdbChainMappingBean = iterator2.next();
                        /*System.out.println(pdbChainMappingBean.getClass());
                        System.out.println("chain: "+ pdbChainMappingBean.getPdbChainCode());
                        System.out.println("pdb: "+ pdbChainMappingBean.getPdbCode());
                        System.out.println("uniprot: "+ pdbChainMappingBean.getUniprotAc());*/

                        // Create the chain if found in the mapping


                        if ( pdbChainMappingBean.getUniprotAc() != null ) {
                            msdParticipant.setUniprotAc( pdbChainMappingBean.getUniprotAc() );
                        } else {
                            msdParticipant.setUniprotAc( null );
                        }

                        if ( pdbChainMappingBean.getUniprotId() != null ) {
                            msdParticipant.setUniprotId( pdbChainMappingBean.getUniprotId() );
                        } else {
                            msdParticipant.setUniprotId( null );
                        }

                        if ( pdbChainMappingBean.getTaxid() != null ) {
                            msdParticipant.setTaxid( pdbChainMappingBean.getTaxid().toString() );
                        } else {
                            msdParticipant.setTaxid( null );
                        }

                        if ( pdbChainBean.getTissue() != null ) {
                            msdParticipant.setTissue( pdbChainBean.getTissue() );
                        } else {
                            msdParticipant.setTissue( null );
                        }

                        msdParticipant.setType( pdbChainBean.getType() );

                        if ( pdbChainBean.getExpressedIntaxid() != null ) {
                            msdParticipant.setExpressedIntaxid( pdbChainBean.getExpressedIntaxid().toString() );
                        } else {
                            msdParticipant.setExpressedIntaxid( null );
                        }

                        if ( msdInteraction.getPdbCode() != null ) {
                            msdParticipant.setPdbCode( msdInteraction.getPdbCode() );
                        } else {
                            msdParticipant.setPdbCode( null );
                        }

                        if ( pdbChainMappingBean.getUniprotEnd() != null ) {
                            msdParticipant.setUniprotEnd( pdbChainMappingBean.getUniprotEnd().toString() );
                        } else {
                            msdParticipant.setUniprotEnd( null );
                        }

                        if ( pdbChainMappingBean.getUniprotStart() != null ) {
                            msdParticipant.setUniprotStart( pdbChainMappingBean.getUniprotStart().toString() );
                        } else {
                            msdParticipant.setUniprotEnd( null );
                        }


                        helper2.close();
                    }
                }
            }


            helper.close();

        }

    }

    public static void main( String[] args ) throws Exception, SQLException {
        MsdExperimentGenerator msdExperimentGenerator = new MsdExperimentGenerator();
        MsdParticipantGenerator msdParticipantGenerator = new MsdParticipantGenerator();
        MsdExperiment exp;
        Collection<MsdExperiment> listExp = new ArrayList();
        exp = msdExperimentGenerator.createExp( "1B7R", listExp );
        System.out.println( exp.toString() );
        for ( Iterator<MsdInteraction> iter = exp.getMsdInteractions().iterator(); iter.hasNext(); ) {
            MsdInteraction msdInteraction = iter.next();
            System.out.println( msdInteraction.toString() );
            ;
            msdParticipantGenerator.createChains( msdInteraction );
        }
    }


}

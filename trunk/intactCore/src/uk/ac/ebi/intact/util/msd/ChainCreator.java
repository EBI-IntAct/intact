package uk.ac.ebi.intact.util.msd;

import uk.ac.ebi.intact.util.msd.model.PdbChainBean;
import uk.ac.ebi.intact.util.msd.model.PdbChainMappingBean;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: krobbe
 * Date: 28-Mar-2006
 * Time: 11:47:02
 * To change this template use File | Settings | File Templates.
 */
public class ChainCreator {


    public ChainCreator(){
    }

    private MsdInteraction pdb;

    
    public void createChains (MsdInteraction pdb) throws Exception, SQLException {

    if (pdb !=null){


    ArrayList chainList= new ArrayList(10);
    MsdHelper helper = new MsdHelper();
    this.pdb=pdb;
    helper.addMapping( PdbChainBean.class, "SELECT entry_id as pdbCode, "+
                                           "chain_pdb_code as pdbChainCode, "+
                                           "type, "+
                                           "system_tax_id as expressedIntaxid, "+
                                           "tissue "+
                                           "FROM INTACT_MSD_CHAIN_DATA " +
                                           "where entry_id=?");

    for (Iterator iterator = helper.getBeans(PdbChainBean.class,this.pdb.getPdbCode()).iterator(); iterator.hasNext();) {
            PdbChainBean pdbChainBean = (PdbChainBean) iterator.next();
            System.out.println(pdbChainBean.getClass());
            System.out.println("chain: "+ pdbChainBean.getPdbChainCode());
            System.out.println("pdb: "+ pdbChainBean.getPdbCode());
            System.out.println("expressedIntaxid: "+ pdbChainBean.getExpressedIntaxid());

            // create the MsdParticipant instance if it is a nucleic acid
            //RULE1: find and withdrawn all MsdInteraction involving nucleic acids.
            if (pdbChainBean.getType()=="Nucleic_Acid"){

                MsdParticipant msdParticipant =new MsdParticipant();
                    msdParticipant.setPdb(pdb);

                if (pdb.getPdbCode()!=null){
                    msdParticipant.setPdbCode(pdb.getPdbCode());
                    }else msdParticipant.setPdbCode(null);

                msdParticipant.setType(pdbChainBean.getType());
                pdb.setHasNucleicAcid(true);
            }


            if (pdbChainBean.getType()=="Protein"){

                    MsdParticipant msdParticipant =new MsdParticipant();
                    msdParticipant.setPdb(pdb);




/*  private String PDBcode;  //entry_id
    private String PDBChainCode;//chain
    private String SPTR_AC; //SPTR_AC
    private String SPTR_ID; //SPTR_ID
    private String taxid;//NCBI_TAX_ID
    private String start;//BEG_SEQ
    private String end; //END_SEQ */

            MsdHelper helper2 = new MsdHelper();
            helper2.addMapping( PdbChainMappingBean.class, "SELECT entry_id as pdbCode, "+
                                                   "chain as pdbChainCode, "+
                                                   "SPTR_AC as sptr_ac, SPTR_ID as sptr_id, "+
                                                   "NCBI_TAX_ID as taxid, "+
                                                   "BEG_SEQ as UniProtStart, END_SEQ as uniProtEnd "+
                                                   "FROM INTACT_MSD_UNP_DATA " +
                                                   //"where entry_id=? and chain='A'");
                                                  "where entry_id=? and chain='"+pdbChainBean.getPdbChainCode()+"'");

            for (Iterator iterator2 = helper2.getBeans(PdbChainMappingBean.class,this.pdb.getPdbCode()).iterator(); iterator2.hasNext();) {
                    PdbChainMappingBean pdbChainMappingBean = (PdbChainMappingBean) iterator2.next();
                    System.out.println(pdbChainMappingBean.getClass());
                    System.out.println("chain: "+ pdbChainMappingBean.getPdbChainCode());
                    System.out.println("pdb: "+ pdbChainMappingBean.getPdbCode());
                    System.out.println("uniprot: "+ pdbChainMappingBean.getUniprotAc());

                    // Create the chain if found in the mapping



                    if (pdbChainMappingBean.getUniprotAc()!=null){
                    msdParticipant.setUniprotAc(pdbChainMappingBean.getUniprotAc());
                    } else  msdParticipant.setUniprotAc(null);

                    if (pdbChainMappingBean.getUniprotId()!=null){
                    msdParticipant.setUniprotId(pdbChainMappingBean.getUniprotId());
                    }else msdParticipant.setUniprotId(null);

                    if (pdbChainMappingBean.getTaxid()!=null){
                    msdParticipant.setTaxid(pdbChainMappingBean.getTaxid().toString());
                        }else msdParticipant.setTaxid(null);

                    if (pdbChainBean.getTissue()!=null){
                    msdParticipant.setTissue(pdbChainBean.getTissue());
                    }else msdParticipant.setTissue(null);

                    msdParticipant.setType(pdbChainBean.getType());

                    if (pdbChainBean.getExpressedIntaxid()!=null){
                    msdParticipant.setExpressedIntaxid(pdbChainBean.getExpressedIntaxid().toString());
                        }else msdParticipant.setExpressedIntaxid(null);

                    if (pdb.getPdbCode()!=null){
                    msdParticipant.setPdbCode(pdb.getPdbCode());
                    }else msdParticipant.setPdbCode(null);

                    if (pdbChainMappingBean.getUniprotEnd()!=null){
                    msdParticipant.setUniprotEnd(pdbChainMappingBean.getUniprotEnd().toString());
                        }else msdParticipant.setUniprotEnd(null);

                    if (pdbChainMappingBean.getUniprotStart()!=null){
                    msdParticipant.setUniprotStart(pdbChainMappingBean.getUniprotStart().toString());
                        }else msdParticipant.setUniprotEnd(null);



            helper2.close();
            }
        }
    }

        // create the chain instance and add them to the MsdInteraction instance in an Array.
        // For each chain do both mapping

    helper.close();

    }

    }

    public static void main(String[] args) throws Exception, SQLException {
        PdbAndExpCreator pdbAndExpcreator = new  PdbAndExpCreator();
        ArrayList PDBList=pdbAndExpcreator.createPDBandExp ("1B7R");
        MsdInteraction pdb=(MsdInteraction)PDBList.get(0);
        System.out.println("pdb:"+pdb.getPdbCode());
        System.out.println("exp:"+pdb.getExperiment());
        ChainCreator chainCreator = new ChainCreator();
        chainCreator.createChains(pdb);
    }
}

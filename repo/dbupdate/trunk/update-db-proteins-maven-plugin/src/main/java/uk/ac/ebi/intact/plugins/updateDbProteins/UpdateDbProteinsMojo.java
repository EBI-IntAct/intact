/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.plugins.updateDbProteins;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.uniprot.service.UniprotService;
import uk.ac.ebi.intact.uniprot.service.UniprotRemoteService;
import uk.ac.ebi.intact.util.protein.ProteinService;
import uk.ac.ebi.intact.util.protein.ProteinServiceFactory;
import uk.ac.ebi.intact.util.protein.utils.UniprotServiceResult;
import uk.ac.ebi.intact.util.biosource.BioSourceServiceFactory;
import uk.ac.ebi.intact.util.taxonomy.NewtTaxonomyService;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.ProteinUtils;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.config.impl.AbstractHibernateDataConfig;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

/**
 * Example mojo. This mojo is executed when the goal "mygoal" is called.
 * Change this comments and the goal name accordingly
 *
 * @goal update-proteins
 *
 * @phase process-resources
 */
public class UpdateDbProteinsMojo
        extends IntactHibernateMojo
{
    /**
         * @parameter expression="${project.build.directory}/hibernate/config/hibernate.cfg.xml"
         * @required
         */
        private File hibernateConfig;


    Map<String,Integer> errorType2count = new HashMap<String,Integer>();
    Map<String, Collection<String>> errorMessages = new HashMap();

    Collection<String> spliceVariantAcs = new ArrayList<String>();

    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * An example file
     *
     * @parameter default-value="${project.build.directory}/dummy.txt"
     */
    private File dummyFile;

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo()
            throws MojoExecutionException, MojoFailureException, IOException
    {
//        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        File outputDir = super.getDirectory();

        StringBuffer generalLog = new StringBuffer();
        generalLog.append("---------------------------------------------------------------------");
        generalLog.append("                           Uniprot Update");
        generalLog.append("---------------------------------------------------------------------\n\n");

        generalLog.append("\n\n-----");
        generalLog.append("Index");
        generalLog.append("-----");

        System.out.println("Uniprot Update");
        System.out.println("--------------\n\n");

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        UniprotService uniprotService = new UniprotRemoteService();
        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new NewtTaxonomyService() ) );
        int spliceVariantCount = 0;
        int noUniprotUpdateCount = 0;
        int updatedProteinCount = 0;
        int errorCount = 0;


        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        int proteinCount = proteinDao.countAll();
        int iterationCount = proteinCount/200;
        int proteinCountInLastIteration = proteinCount - (200*iterationCount);
        commitTransaction();


        for(int i=0; i<=iterationCount ; i++){
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();
            String proteinsUpdatedInChunk = new String();
            StringBuffer proteinChunkLog = new StringBuffer();
            proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
            Collection<ProteinImpl> proteins;
            if(i == iterationCount){
                proteins = proteinDao.getAll((i*200),(i*200) + proteinCountInLastIteration);
            }else{
                proteins = proteinDao.getAll((i*200),(i*200) + 200);
            }
            CvDatabase uniprot = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvDatabase.class, CvDatabase.UNIPROT_MI_REF);
            CvXrefQualifier identity = IntactContext.getCurrentInstance().getCvContext().getByMiRef(CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF);
            for(Protein protein : proteins){
                System.out.print("\nUpdating " +  protein.getAc() + " : " );
                proteinsUpdatedInChunk = proteinsUpdatedInChunk + protein.getAc();
                proteinChunkLog.append("\n\n---------------------------------------------------------------------------------");
                proteinChunkLog.append("Updating protein[" + protein.getAc() + "," + protein.getShortLabel()+"] :");
                proteinChunkLog.append("---------------------------------------------------------------------------------");
                // if protein does not have a not UniprotUpdate annotation and is not a splice variant then update it.
                // We don't need to update the splice variants as they will be updated when updating their master proteins.
                if(ProteinUtils.isFromUniprot(protein)){
                    if(!isSpliceVariant(protein)){
                        InteractorXref uniprotIdentity = ProteinUtils.getUniprotXref(protein);
                        proteinChunkLog.append("Protein xref identity : " + uniprotIdentity.getPrimaryId());
                        UniprotServiceResult uniprotServiceResult = service.retrieve(uniprotIdentity.getPrimaryId());
                        Collection<Protein> updatedProteins = uniprotServiceResult.getProteins();
                        Map<String,String> errors = uniprotServiceResult.getErrors();
                        if(updatedProteins.size() == 1 && errors.size() == 0){
                            System.out.println(" updated, " + uniprotIdentity.getPrimaryId() + ".");
                            updatedProteinCount++;
                            proteinChunkLog.append("Protein updated to uniprot entry " + uniprotIdentity.getPrimaryId());
                        }else if(updatedProteins.size() == 1 && errors.size()>=1){
                            String exceptionMessage = "An error occured, but the protein was updated. Error message is : \n";
                            Set<String> errorTypes = errors.keySet();
                            for(String errorType : errorTypes){
                                exceptionMessage = exceptionMessage + errors.get(errorType);
                            }
                            throw new IntactException(exceptionMessage);
                        }else if(updatedProteins.size()==0 && errors.size()>=1){
                            errorCount++;
                            proteinChunkLog.append("The protein was not updated as an error occured :\n");
                            Set<String> errorTypes = errors.keySet();
                            for(String errorType : errorTypes){
                                System.out.println(" " + errorType);
                                Integer errorTypeCount = errorType2count.get(errorType);
                                if(errorTypeCount == null){
                                    errorType2count.put(errorType,(Integer)1);
                                }else{
                                    errorTypeCount++;
                                    errorType2count.put(errorType, errorTypeCount);
                                }
                                proteinChunkLog.append(errors.get(errorType));
                            }
                        }
                    } else {
                        System.out.println(" splice variant.");
                        spliceVariantCount++;
                        proteinChunkLog.append("The protein was not udpated because it is a splice variant and will be updated with it's master protein.");
                    }
                }else{
                    System.out.println(" no uniprot udpate.");
                    noUniprotUpdateCount++;
                    proteinChunkLog.append("The protein was not udpated because it is not a UniProt protein.");
                }
            }
            generalLog.append("proteinUpdate-" + i + ".log : " + proteinsUpdatedInChunk);
            BufferedWriter out = new BufferedWriter(new FileWriter("proteinUpdate-" + i + ".log"));
            out.write(proteinChunkLog.toString());
            out.close();


            commitTransaction();
        }

        generalLog.append("\n\n------------------");
        generalLog.append("General statistics");
        generalLog.append("------------------");
        generalLog.append("\nNumber of proteins in IntAct : " + proteinCount);
        generalLog.append("Number of NoUniprot Update proteins : " + noUniprotUpdateCount);
        generalLog.append("Number of splice variants : " + spliceVariantCount);
        generalLog.append("Number of updated proteins " + updatedProteinCount);
        generalLog.append("Number of proteins that couldn't be udpate due to errors" + errorCount);

        generalLog.append("\nDifferent type of error count : ");
        generalLog.append("------------------------------- ");
        Set<String> errorTypes = errorType2count.keySet();
        for(String errorType : errorTypes){
            Integer count = errorType2count.get(errorType);
            generalLog.append(errorType + " : " + count);
        }

        BufferedWriter out = new BufferedWriter(new FileWriter("general.log"));
        out.write(generalLog.toString());
        out.close();
        commitTransaction();

        // TODO: put your logic here
    }

    public File getHibernateConfig() {
        return hibernateConfig;
    }

    private void commitTransaction(){
        try {
            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        } catch (IntactTransactionException e) {
            //If commiting the transaction failed (ex : a shortlabel was longer then 20 characters), try to rollback.
            try {
                IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCurrentTransaction().rollback();
            } catch (IntactTransactionException e1) {
                // If rollback was not successfull do what you want : printStackTrace, throw Exception...
                System.out.println(e1);
            }
            // If commit is it could not commit do what you want : printStackTrace, throw Exception...
            System.out.println(e);
        }finally{
            // Commiting the transaction close as well the session if everything goes fine but in case of an exception
            // sent at commit time then the session would not be closed, so it's really important that you close it here
            // otherwise you might get again this fishy connection and have very nasty bugs.
            Session hibernateSession = getSession();
            if ( hibernateSession.isOpen() ) {
                hibernateSession.close();
            }
        }

    }

    private Session getSession() {

        AbstractHibernateDataConfig abstractHibernateDataConfig = ( AbstractHibernateDataConfig ) IntactContext.getCurrentInstance().getConfig().getDefaultDataConfig();
        SessionFactory factory = abstractHibernateDataConfig.getSessionFactory();
        Session session = factory.getCurrentSession();
        return session;
    }
    public boolean isSpliceVariant(Protein protein){
        if(spliceVariantAcs.contains(protein.getAc())){
            Collection<InteractorXref> xrefs = protein.getXrefs();
            for(InteractorXref xref : xrefs){
                CvObjectXref qualifierIdentity = CvObjectUtils.getPsiMiIdentityXref(xref.getCvXrefQualifier());
                if(qualifierIdentity != null && CvXrefQualifier.ISOFORM_PARENT_MI_REF.equals(qualifierIdentity.getPrimaryId())){
                    spliceVariantAcs.add(protein.getAc());
                    return true;
                }
            }
        }
        return false;
    }

    public MavenProject getProject()
    {
        return project;
    }

    
}

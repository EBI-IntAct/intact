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
import uk.ac.ebi.intact.util.protein.utils.ProteinToDeleteManager;
import uk.ac.ebi.intact.util.biosource.BioSourceServiceFactory;
import uk.ac.ebi.intact.bridges.taxonomy.NewtTaxonomyService;
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

    private int spliceVariantCount = 0;
    private int noUniprotUpdateCount = 0;
    private int updatedProteinCount = 0;
    private int errorCount = 0;
    private int proteinCount = 0;
    private static final int CHUNK_SIZE = 50;


    public static final String NEW_LINE = "\n";

    Map<String,Integer> errorType2count = new HashMap<String,Integer>();
    Map<String, Collection<String>> errorMessages = new HashMap<String, Collection<String>>();

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
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        // INSTANTIATE THE UNIPROT REMOTE SERVICE
        UniprotService uniprotService = new UniprotRemoteService();
        ProteinService service = ProteinServiceFactory.getInstance().buildProteinService( uniprotService );
        service.setBioSourceService( BioSourceServiceFactory.getInstance().buildBioSourceService( new NewtTaxonomyService() ) );

        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
        proteinCount = proteinDao.countAll();
        int iterationCount = proteinCount/CHUNK_SIZE;

//        System.out.println("\n\n\n\n\n\n\n\n ABOUT TO UPDATE P12385");
        System.out.println("\n\n\n\n\n\n\n\n ABOUT TO UPDATE Q5T482");
        UniprotServiceResult uniprotServResult = service.retrieve("Q5T482");
        System.out.println("uniprotServResult.getProteins().size() = " + uniprotServResult.getProteins().size());
        System.out.println("uniprotServResult.getErrors().size() = " + uniprotServResult.getErrors().size());
        Map<String,String> errorsTest = uniprotServResult.getErrors();
        StringBuilder errorTestLog = new StringBuilder(512);
        Set<String> errorTestTypes = errorsTest.keySet();
        for(String errorType : errorTestTypes){
            System.out.println(" " + errorType);
            Integer errorTypeCount = errorType2count.get(errorType);
            if(errorTypeCount == null){
                errorType2count.put(errorType,1);
            }else{
                errorTypeCount++;
                errorType2count.put(errorType, errorTypeCount);
            }
            errorTestLog.append(errorsTest.get(errorType)).append(NEW_LINE);
        }
        // Log messages.
        errorTestLog.append("MESSAGES :\n");
        errorTestLog.append("--------\n");
        Collection<String> messagesTest = uniprotServResult.getMessages();
        for(String message : messagesTest){
            errorTestLog.append(message).append(NEW_LINE);
        }
        System.out.println(errorTestLog);

        System.out.println("Q5T482 UPDATED\n\n\n\n\n\n\n\n");
        System.out.println("BEFORE ASSERT");
        commitTransaction();
//        int x = 1;
//        if(x == 1){
//            return;
//        }



        for(int i=0; i<=iterationCount ; i++){
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();

            proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
            Collection<ProteinImpl> proteins;

            proteins = proteinDao.getAll((i*CHUNK_SIZE), CHUNK_SIZE);

            for(Protein protein : proteins){

                System.out.print("\nUpdating " +  protein.getAc() + " : " );
                // if protein does not have a no-uniprot-update annotation and is not a splice variant then update it.
                // We don't need to update the splice variants as they will be updated when updating their master proteins.
                if(ProteinUtils.isFromUniprot(protein)){
                    if(ProteinUtils.getUniprotXref(protein) == null){
                        StringBuilder errorLog = new StringBuilder(512);
                        String errorFileName = protein.getShortLabel() + "-error.log";
                        errorLog.append("Updating protein[").append(protein.getAc()).append(",")
                                .append(protein.getShortLabel()).append(",")
                                .append("] :").append(NEW_LINE);
                        errorLog.append("The protein was not updated as it has no uniprot identity xref but no " +
                                " no-uniprot-update annotation.").append(NEW_LINE);
                        System.out.println(errorLog);

                        //WRITE ERROR TO FILE (ex : P12345-error.log)
                        BufferedWriter out = new BufferedWriter(new FileWriter(errorFileName));
                        out.write(errorLog.toString());
                        out.flush();
                        out.close();

                        continue;
                    }
                    if(!isSpliceVariant(protein)){

                        InteractorXref uniprotIdentity = ProteinUtils.getUniprotXref(protein);
                        UniprotServiceResult uniprotServiceResult = service.retrieve(uniprotIdentity.getPrimaryId());
                        Collection<Protein> updatedProteins = uniprotServiceResult.getProteins();
                        Map<String,String> errors = uniprotServiceResult.getErrors();
                        //TODO : check if it can return more then 1 prot when the updated prot has splice variants.
                        if(updatedProteins.size() >= 1 && errors.size() == 0){
                            StringBuilder log = new StringBuilder();
                            //TODO : check that there is 1 protein udpated and 1 or more NO UNIPROT UPDATE protein
                            //TODO : DISPLAY SPLICE VARIANT IF ANY

                            //OTHERWISE ADD LOG
                            log.append(NEW_LINE).append(NEW_LINE).append(NEW_LINE).append("Updating protein[")
                                    .append(protein.getAc()).append(",").append(protein.getShortLabel()).append(",")
                                    .append(uniprotIdentity.getPrimaryId()).append("] :").append(NEW_LINE);
                            System.out.println(" updated, " + uniprotIdentity.getPrimaryId() + ".");
                            updatedProteinCount++;
                            log.append("Protein updated.").append(NEW_LINE);
                            Collection<String> messages = uniprotServiceResult.getMessages();
                            for(String message : messages){
                                log.append(message).append(NEW_LINE);
                            }
                            System.out.println(log);
                        }else if(updatedProteins.size()>=0 && errors.size()>=1){
                            errorCount++;
                            // Log errors.
                            StringBuilder errorLog = new StringBuilder(512);
                            String errorFileName = uniprotIdentity.getPrimaryId() + "-error.log";
                            errorLog.append("Updating protein[").append(protein.getAc()).append(",")
                                    .append(protein.getShortLabel()).append(",").append(uniprotIdentity.getPrimaryId())
                                    .append("] :").append(NEW_LINE);
                            errorLog.append("The protein was not updated as an error occured :").append(NEW_LINE);

                            Set<String> errorTypes = errors.keySet();
                            for(String errorType : errorTypes){
                                System.out.println(" " + errorType);
                                Integer errorTypeCount = errorType2count.get(errorType);
                                if(errorTypeCount == null){
                                    errorType2count.put(errorType,1);
                                }else{
                                    errorTypeCount++;
                                    errorType2count.put(errorType, errorTypeCount);
                                }
                                errorLog.append(errors.get(errorType)).append(NEW_LINE);
                            }
                            // Log messages.
                            errorLog.append("MESSAGES :\n");
                            errorLog.append("--------\n");
                            Collection<String> messages = uniprotServiceResult.getMessages();
                            for(String message : messages){
                                errorLog.append(message).append(NEW_LINE);
                            }
                            System.out.println(errorLog);

                            //WRITE ERROR TO FILE (ex : P12345-error.log)
                            BufferedWriter out = new BufferedWriter(new FileWriter(errorFileName));
                            out.write(errorLog.toString());
                            out.flush();
                            out.close();
                        }
                    } else {
                        //DO NOT UPDATE WHEN THE PROTEIN IS A SPLICE VARIANT AS IT WILL BE UPDATED WHEN ITS PARENT IS
                        //UPDATED
                        System.out.println("Splice variant [" + protein.getAc()+ ","+ protein.getShortLabel() + "] not updated.");
                        spliceVariantCount++;
//                        log.append("Splice variant [").append(protein.getAc()).append(",").append(protein.getShortLabel()).append("] not udpated ").append(NEW_LINE);
                    }
                }else{
                    //DO NOT UPDATE WHEN THE PROTEIN HAS A NO UNIPROT UPDATE ANNOTATION.
                    System.out.println("not updated, no uniprot udpate.");
                    noUniprotUpdateCount++;
//                    log.append("No uniprot update protein[").append(protein.getAc()).append(",").append(protein.getShortLabel()).append("] not udpated ").append(NEW_LINE);
                }
            }

            //PRINT STATISTIC
            System.out.println(createStatistic());

            commitTransaction();
        }

        System.out.println("DELETING PROTEINS");
        System.out.println("-----------------");
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        Collection<String> acsToDelete = ProteinToDeleteManager.getAcToDelete();
        proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
// todo : I have commented this peace of code as we can't delete proteins if we dont' check first if they have splice variant
        //involved in an interaction. Add the check an un-comment the delete lines. I have deleted those manually (see sql on confluence)
//        for(String ac : acsToDelete){
//            Protein protein = proteinDao.getByAc(ac);
//            System.out.println("Deleting protein [" + protein.getAc() + "," + protein.getShortLabel() + ","
//                    + ProteinUtils.getUniprotXref(protein) + "]");
//            proteinDao.delete((ProteinImpl) protein);
//        }
        commitTransaction();

    }



    private String createStatistic(){
        StringBuilder statLog = new StringBuilder();
        statLog.append(NEW_LINE).append(NEW_LINE).append("------------------");
        statLog.append("General statistics").append(NEW_LINE);
        statLog.append("------------------").append(NEW_LINE);
        statLog.append(NEW_LINE).append("Number of proteins in IntAct : ").append(proteinCount).append(NEW_LINE);
        statLog.append("Number of NoUniprot Update proteins : ").append(noUniprotUpdateCount).append(NEW_LINE);
        statLog.append("Number of splice variants : ").append(spliceVariantCount).append(NEW_LINE);
        statLog.append("Number of updated proteins ").append(updatedProteinCount).append(NEW_LINE);
        statLog.append("Number of proteins that couldn't be udpate due to errors").append(errorCount).append(NEW_LINE);

        statLog.append("\nDifferent type of error count : ").append(NEW_LINE);
        statLog.append("------------------------------- ").append(NEW_LINE);
        Set<String> errorTypes = errorType2count.keySet();
        for(String errorType : errorTypes){
            Integer count = errorType2count.get(errorType);
            statLog.append(errorType).append(" : ").append(count).append(NEW_LINE);
        }
        return statLog.toString();
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
                throw new IntactException("Problem at commit time, couldn't rollback : " + e1);
            }
            // If commit is it could not commit do what you want : printStackTrace, throw Exception...
            throw new IntactException("Problem at commit time, rollback done : " + e);
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
        return factory.getCurrentSession();
    }

    public boolean isSpliceVariant(Protein protein){
        if(spliceVariantAcs.contains(protein.getAc())){
            return true;
        }else{
            Collection<InteractorXref> xrefs = protein.getXrefs();
            for(InteractorXref xref : xrefs){
                if(xref.getCvXrefQualifier() != null){
                    CvObjectXref qualifierIdentity = CvObjectUtils.getPsiMiIdentityXref(xref.getCvXrefQualifier());
                    if(qualifierIdentity != null && CvXrefQualifier.ISOFORM_PARENT_MI_REF.equals(qualifierIdentity.getPrimaryId())){
                        spliceVariantAcs.add(protein.getAc());
                        return true;
                    }
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


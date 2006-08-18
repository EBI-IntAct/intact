/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.CvMapping;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.UserSessionDownload;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Interaction2xmlFactory;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Interaction2xmlI;
import uk.ac.ebi.intact.application.dataConversion.util.DisplayXML;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

/**
 * Generates PSI XML files from the intact objects stored in the database
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10-Aug-2006</pre>
 */
public abstract class NewFileGenerator
{

    private static final Log log = LogFactory.getLog(NewFileGenerator.class);

    /**
     * Writes a file containing the PSI XML, for the information contained in the ExperimentListItem
     *
     * @param eli The ExperimentListItem, which contaisn information about the experiment to be fetched, the pagination and files
     * @param psiVersion The version of PSI to use
     * @param cvMapping The cv mapping
     * @param baseDir The base dir where to put the files
     * @param validate whether to validate the xml
     * @throws IOException thrown if there is some problem writing to the file
     * @return a psiValidatorReport
     */
    public static PsiValidatorReport writePsiData(ExperimentListItem eli,
                                    PsiVersion psiVersion,
                                    CvMapping cvMapping,
                                    File baseDir, boolean validate) throws IOException
    {
        File xmlFile = new File(baseDir, eli.getFilename());

        Document doc = generatePsiData(eli,psiVersion,cvMapping);

        return writeFile(doc, xmlFile, validate);
    }

    /**
     * Writes a file containing the PSI XML, with the interactions provided
     *
     * @param interactions
     * @param psiVersion
     * @param cvMapping
     * @param xmlTargetFile
     * @param validate
     * @return
     * @throws IOException
     */
    public static PsiValidatorReport writePsiData(Collection<Interaction> interactions,
                                       PsiVersion psiVersion,
                                       CvMapping cvMapping,
                                       File xmlTargetFile, boolean validate) throws IOException
    {
       UserSessionDownload session = new UserSessionDownload(psiVersion);

        if (cvMapping != null)
        {
            session.setReverseCvMapping(cvMapping);
        }

        Document doc =  generatePsiData(interactions, session);

        return writeFile(doc, xmlTargetFile, validate);
    }


    /**
     * Converts a list of experiments to PSI XML, providing the experiment labels
     *
     * @param eli The ExperimentListItem, which contaisn information about the experiment to be fetched, the pagination and files
     * @param psiVersion The version of PSI to use
     * @param cvMapping The cv mapping
     * @return The Document containing the PSI XML
     */
    public static Document generatePsiData(ExperimentListItem eli, PsiVersion psiVersion, CvMapping cvMapping)
    {
        UserSessionDownload session = new UserSessionDownload(psiVersion);

        if (cvMapping != null)
        {
            session.setReverseCvMapping(cvMapping);
        }

        Collection<Interaction> interactions = getInteractionsForExperimentListItem(eli);

        return generatePsiData(interactions, session);
    }

    public static Document generatePsiData(Collection<Interaction> interactions, PsiVersion psiVersion, CvMapping cvMapping)
    {
       UserSessionDownload session = new UserSessionDownload(psiVersion);

        if (cvMapping != null)
        {
            session.setReverseCvMapping(cvMapping);
        }

        return generatePsiData(interactions, session);
    }

     /**
     * Convert a list of interactions into PSI XML
     *
     * @param interactions a list of interactions to export in PSI XML
     * @param session     the PSI doanload session.
     *
     * @return the generated XML Document
     */
    private static Document generatePsiData( Collection<Interaction> interactions, UserSessionDownload session ) {


        Interaction2xmlI interaction2xml = Interaction2xmlFactory.getInstance(session);


         // Psi 1 do not tolerate Nucleic Acid as Participant of an Interaction. So if psi verstion is psi1 we
         // filter out all the Interactions having a Nucleic Acid as a participant. Then we check, if there is no
         // any more interactions linked to the experiment, we do not process it.

         if (PsiVersion.getVersion1().getVersion().equals(session.getPsiVersion().getVersion()))
         {
             filterInteractions(interactions);
         }

         if (interactions.size() != 0)
         {
             // in order to have them in that order, experimentList, then interactorList, at last interactionList.
             session.getExperimentListElement();
             session.getInteractorListElement();

             int count = 0;
             for (Interaction interaction : interactions)
             {

                 interaction2xml.create(session, session.getInteractionListElement(), interaction);

                 count++;

                 if ((count % 50) == 0)
                 {
                     log.debug("Interaction: " + count);
                 }
             } // interactions

         }

         return session.getPsiDocument();
    }

    private static PsiValidatorReport writeFile(Document doc, File xmlFile, boolean validate) throws IOException
    {
        // create the parent dir if it does not exist
        if (!xmlFile.getParentFile().exists())
        {
            log.info("Creating dir: "+xmlFile.getParentFile());
            xmlFile.getParentFile().mkdirs();
        }

        Writer writer = new FileWriter(xmlFile);
        DisplayXML.write(doc, writer, "   ");

        writer.close();

        if (validate)
        {
            return PsiValidator.validate(xmlFile);
        }

        return new PsiValidatorReport();
    }

    /**
     * It take an interactions Collection and remove from it all the interactions having a NucleicAcid as component.
     * This is used in case psi version is psi1 as psi1 do not allow Nucleic Acid as Interaction's participant.
     * @param interactions Collection of interactions
     */
    public static void filterInteractions(Collection<Interaction> interactions){

        for (Iterator<Interaction> iterator = interactions.iterator(); iterator.hasNext();) {
            Interaction interaction = iterator.next();
            for (Component component : interaction.getComponents()) {
                Interactor interactor = component.getInteractor();
                if ( interactor instanceof NucleicAcid){
                    iterator.remove();
                    break;
                }
                else if (interactor instanceof SmallMoleculeImpl)
                {
                    iterator.remove();
                     break;
                }
            }
        }
    }

    public static Collection<Interaction> getInteractionsForExperimentListItem(ExperimentListItem eli)
    {
        Integer firstInteraction = null;
        Integer maxInteractions = null;

        // if there is pagination, get the first and the max result
        if (eli.getLargeScaleChunkSize() != null)
        {
            firstInteraction = (eli.getChunkNumber() - 1) * eli.getLargeScaleChunkSize();
            maxInteractions = eli.getLargeScaleChunkSize();
        }

        Collection<String> experimentLabels = eli.getExperimentLabels();

        return IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInteractionDao()
                .getInteractionByExperimentShortLabel(experimentLabels.toArray(new String[experimentLabels.size()]),
                        firstInteraction, maxInteractions);
    }

}

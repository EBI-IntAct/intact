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
package uk.ac.ebi.intact.test;

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;
import uk.ac.ebi.intact.persistence.dao.BioSourceDao;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;

import java.util.Collection;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class RandomSeeder
{
    public static void main(String[] args)
    {
        IntactContext ctx = IntactContext.getCurrentInstance();
        ctx.getDataContext().beginTransaction();

        Random random = new Random();

        CvDatabase cvDatabase = createCvObject(CvDatabase.class, "db-"+randomLabel(), ctx);
        CvInteractorType proteinInteractorType = createCvObject(CvInteractorType.class, CvInteractorType.PROTEIN, ctx);
        CvInteractionType interactionType = createCvObject(CvInteractionType.class, CvInteractionType.PHYSICAL_INTERACTION, ctx);
        CvXrefQualifier identityXrefQual = createCvObject(CvXrefQualifier.class, CvXrefQualifier.IDENTITY, ctx);

        CvComponentRole bait = createCvObject(CvComponentRole.class, CvComponentRole.BAIT, ctx);
        CvComponentRole prey = createCvObject(CvComponentRole.class, CvComponentRole.PREY, ctx);

        int biosourceNum = 25;
        int maxProtsPerExp = 25;
        int maxInteractionPerExp = 200;

        for (int i=0; i<biosourceNum;i++)
        {
            BioSource organism = createBioSource(ctx, randomLabel(), cvDatabase, proteinInteractorType, identityXrefQual);

            List<Experiment> experiments = new ArrayList<Experiment>();
            Experiment exp = createExperiment(ctx, "exp-"+randomLabel(), organism);
            experiments.add(exp);

            List<Protein> prots = new ArrayList<Protein>();

            for (int j=0; j<random.nextInt(maxProtsPerExp-2)+2;j++)
            {
                prots.add(createProtein(ctx, "prot-"+randomLabel(), organism));
            }

            for (int j=0; j<random.nextInt(maxInteractionPerExp); j++)
            {
                Interaction interaction = createInteracton(ctx, "int-" + randomLabel(), experiments, interactionType, proteinInteractorType);
                interaction.setExperiments(experiments);

                List<Component> components = new ArrayList<Component>();
                components.add(createComponent(ctx, interaction, prots.get(random.nextInt(prots.size())), bait));
                components.add(createComponent(ctx, interaction, prots.get(random.nextInt(prots.size())), prey));
            }
        }

        ctx.getDataContext().commitTransaction();
    }

    public static CvDatabase createCvDatabase(IntactContext ctx, String label)
    {
        Institution institution = ctx.getInstitution();
       DaoFactory daoFactory = ctx.getDataContext().getDaoFactory();

        CvDatabase cvDatabase = new CvDatabase(institution, label);
        cvDatabase.setFullName("Mock CvDatabase Object");

        daoFactory.getCvObjectDao(CvDatabase.class).persist(cvDatabase);

        return cvDatabase;
    }

    public static <T extends CvObject> T createCvObject(Class<T> clazz, String label, IntactContext ctx)
    {
       Institution institution = ctx.getInstitution();
       DaoFactory daoFactory = ctx.getDataContext().getDaoFactory();

        T cv = null;
        try
        {
            cv = clazz.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        cv.setOwner(institution);
        cv.setShortLabel(label);

        daoFactory.getCvObjectDao(clazz).saveOrUpdate(cv);

        return cv;
    }

    public static CvObjectXref createXrefForCvObject(IntactContext ctx, CvObject cv, CvDatabase cvDb, String primaryId, CvXrefQualifier cvXrefQual)
    {
        Institution institution = ctx.getInstitution();
        DaoFactory daoFactory = ctx.getDataContext().getDaoFactory();

        CvObjectXref xref = new CvObjectXref(institution, cvDb, primaryId, cvXrefQual);
        cv.addXref(xref);

        daoFactory.getXrefDao(CvObjectXref.class).saveOrUpdate(xref);

        return xref;
    }


    public static BioSource createBioSource(IntactContext ctx, String label, CvDatabase cvDatabase, CvInteractorType cvInteractorType, CvXrefQualifier cvXrefQualifier)
    {
        Institution institution = ctx.getInstitution();
        DaoFactory daoFactory = ctx.getDataContext().getDaoFactory();

        BioSource organism = new BioSource(institution, label, String.valueOf(new Random().nextInt(5000)));
        daoFactory.getBioSourceDao().persist(organism);

        ProteinImpl prot = new ProteinImpl(institution, organism, randomLabel(), cvInteractorType);
        prot.setFullName("Test protein in XrefTest");
        prot.setCrc64("InvalidCRC_Test");

        InteractorXref xref = new InteractorXref(institution, cvDatabase,  "testPrimaryId-Xref", cvXrefQualifier);
        prot.addXref(xref);

        daoFactory.getProteinDao().saveOrUpdate(prot);

        return organism;
    }


    public static Experiment createExperiment(IntactContext ctx, String label, BioSource organism)
    {
        Institution institution = ctx.getInstitution();
        DaoFactory daoFactory = ctx.getDataContext().getDaoFactory();

        Experiment exp = new Experiment(institution, label, organism);

        daoFactory.getExperimentDao().persist(exp);

        return exp;
    }

    public static Protein createProtein(IntactContext ctx, String label, BioSource organism)
     {
         Institution institution = ctx.getInstitution();
         DaoFactory daoFactory = ctx.getDataContext().getDaoFactory();

         ProteinImpl protein = new ProteinImpl(institution,
                 organism,
                 label,
                 ctx.getCvContext().getByLabel(CvInteractorType.class, CvInteractorType.PROTEIN));

         StringBuilder sb = new StringBuilder(1500);
         for (int i=0; i<1000; i++)
         {
             sb.append("A");
         }

         for (int i=0; i<500; i++)
         {
             sb.append("B");
         }

         protein.setSequence(sb.toString());
         try
         {
             daoFactory.getProteinDao().saveOrUpdate(protein);
         }
         catch (Exception e)
         {
             e.printStackTrace();
         }

         return protein;
    }

    public static Component createComponent(IntactContext ctx,
                                            Interaction interaction,
                                            Interactor interactor,
                                            CvComponentRole cvComponentRole)
    {
        Institution institution = ctx.getInstitution();
        DaoFactory daoFactory = ctx.getDataContext().getDaoFactory();

        Component comp = new Component(institution, interaction, interactor, cvComponentRole);
        daoFactory.getComponentDao().persist(comp);

        return comp;
    }

    public static Interaction createInteracton(IntactContext ctx,
                                               String label,
                                               List<Experiment> experiments,
                                               CvInteractionType interactionType,
                                               CvInteractorType interactorType)
    {
        Institution institution = ctx.getInstitution();
        DaoFactory daoFactory = ctx.getDataContext().getDaoFactory();

        InteractionImpl inter = new InteractionImpl(experiments, new ArrayList<Component>(), interactionType, interactorType, label, institution);

        daoFactory.getInteractionDao().persist(inter);

        return inter;
    }

    public static String randomLabel()
    {
        return new ShortLabelRandomGenerator().generateLabel(5,10);
    }
}

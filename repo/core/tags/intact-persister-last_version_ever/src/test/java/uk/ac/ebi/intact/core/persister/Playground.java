/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.persister;

import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.Institution;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class Playground {

    @Test
    public void play1() throws Exception {

        BioSource bioSource = new IntactMockBuilder(new Institution("lala")).createBioSourceRandom();

        EntityManager entityManager = Persistence.createEntityManagerFactory("intact-core-mem").createEntityManager();

        entityManager.getTransaction().begin();

//        PersisterVisitor visitor = new PersisterVisitor();
//        DefaultTraverser traverser = new DefaultTraverser();
//        traverser.traverse(bioSource, visitor);
//        visitor.persistAll();

        bioSource.getOwner().getXrefs().clear();
        bioSource.getXrefs().clear();

        entityManager.persist(bioSource.getOwner());
        entityManager.persist(bioSource);

        entityManager.getTransaction().commit();
        entityManager.close();

 
        //System.out.println("B: "+getDaoFactory().getBioSourceDao().countAll());
        //System.out.println("CV: "+getDaoFactory().getCvObjectDao().countAll());
        //System.out.println("I: "+getDaoFactory().getInstitutionDao().countAll());

    }



}
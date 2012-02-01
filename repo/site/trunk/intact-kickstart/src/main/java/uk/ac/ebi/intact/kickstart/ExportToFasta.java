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
package uk.ac.ebi.intact.kickstart;

import org.springframework.transaction.TransactionStatus;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.model.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Example that shows how to export interacting polymer sequences to Fasta format.
 *
 * @version $Id$
 */
public class ExportToFasta {

    public static void main(String[] args) throws Exception {

        // Initialize the IntactContext, using the default configuration found in the file hsql.spring.xml..
        IntactContext.initContext(new String[] {"/META-INF/kickstart.spring.xml"});

        // Once an IntactContext has been initialized, we can access to it by getting the current instance
        IntactContext intactContext = IntactContext.getCurrentInstance();

        DataContext dataContext = intactContext.getDataContext();

        // The DaoFactory is the central access point to all the DAOs (Data Access Objects)
        DaoFactory daoFactory = dataContext.getDaoFactory();

        // When using DAOs, we need to begin a transaction first
        final TransactionStatus transactionStatus = dataContext.beginTransaction();

        // We get an iterator that contains all the interactions from the database
        Iterator<InteractionImpl> interactions = daoFactory.getInteractionDao().getAllIterator();

        // We will store all the proteins in a Set, because two interactions can refer to the same protein.
        // Hence, we avoid duplicated proteins.
        Set<Protein> proteins = new HashSet<Protein>();

        while (interactions.hasNext()) {
            Interaction interaction =  interactions.next();

            // An interaction contains components.
            // A component represents an interactor in the context of an interaction and they are
            // unique for that interaction.
            Collection<Component> components = interaction.getComponents();

            for (Component component : components) {
                // We get the interactor referenced by the component
                Interactor interactor = component.getInteractor();

                // Check if the interactor is a Protein and if so, add it to the Set
                if (interactor instanceof Protein) {
                    Protein protein = (Protein) interactor;

                    // Add each protein to the Set
                    proteins.add(protein);
                }
            }
        }

        // We iterate through our Set of proteins to print out the Fasta
        for (Protein protein : proteins) {
            System.out.println(">"+protein.getShortLabel());
            System.out.println(protein.getSequence());
        }

        // Don't forget to commit the transaction
        dataContext.commitTransaction(transactionStatus);

        // Note that we could have just gotten all the database proteins using the ProteinDao,
        // but for educational purposes it is good to understand how the information is organized.
    }
}
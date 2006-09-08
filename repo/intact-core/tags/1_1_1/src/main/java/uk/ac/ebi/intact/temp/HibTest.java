/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.temp;

import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;
import uk.ac.ebi.intact.persistence.dao.ExperimentDao;
import uk.ac.ebi.intact.model.Experiment;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class HibTest {
    public static void main(String[] args) {
        IntactTransaction intactTransaction = DaoFactory.beginTransaction();
        ExperimentDao expDao = DaoFactory.getExperimentDao();
        Experiment exp = expDao.getByAc("EBI-476945");
        System.out.println("exp.getShortLabel() = " + exp.getShortLabel());
        intactTransaction.commit();

        System.out.println("After commit = exp.getShortLabel() = " + exp.getShortLabel());
    }
}

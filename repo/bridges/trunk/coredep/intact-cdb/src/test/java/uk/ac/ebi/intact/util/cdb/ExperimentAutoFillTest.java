/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.cdb;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.Experiment;

/**
 * TODO comment this
 *
 * @author Bruno Aranda
 * @version $Id$
 * @since TODO
 */
public class ExperimentAutoFillTest extends IntactBasicTestCase {

    @Test
    public void experimentAutoFill_default() throws Exception {

        ExperimentAutoFill eaf = new ExperimentAutoFill("15084279");

        Assert.assertEquals("Drebrin is a novel connexin-43 binding partner that links gap junctions to the submembrane cytoskeleton.",
                            eaf.getFullname());

        beginTransaction();
        Assert.assertEquals("butkevich-2004", eaf.getShortlabel(false));
        Assert.assertEquals("butkevich-2004-1", eaf.getShortlabel(true));
        commitTransaction();
    }

    @Test
    public void testGetFullname() throws Exception {

        Experiment exp = getMockBuilder().createExperimentEmpty("butkevich-2004-2");
        exp.getPublication().setShortLabel("15084279");
        PersisterHelper.saveOrUpdate(exp);

        ExperimentAutoFill eaf = new ExperimentAutoFill("15084279");

        beginTransaction();
        Assert.assertEquals("butkevich-2004", eaf.getShortlabel(false));
        Assert.assertEquals("butkevich-2004-3", eaf.getShortlabel(true));
        commitTransaction();
    }

}

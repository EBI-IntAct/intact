/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.cdb;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Experiment;

import java.util.Iterator;

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

        Assert.assertEquals("butkevich-2004", eaf.getShortlabel(false));
        Assert.assertEquals("butkevich-2004-1", eaf.getShortlabel(true));
    }

    @Test
    public void testGetFullname() throws Exception {

        Experiment exp = getMockBuilder().createExperimentEmpty("butkevich-2004-2");
        exp.getPublication().setShortLabel("15084279");
        PersisterHelper.saveOrUpdate(exp);

        ExperimentAutoFill eaf = new ExperimentAutoFill("15084279");

        beginTransaction();
        Assert.assertEquals("butkevich-2004", eaf.getShortlabel(false));
        Assert.assertEquals("butkevich-2004-1", eaf.getShortlabel(true));
        commitTransaction();
    }

    @Test
    public void liu2007_completeExample() throws Exception {

        persistAndAutofillExperiment("unknown", "17560331", 1);
        persistAndAutofillExperiment("unknown", "17560331", 2);
        persistAndAutofillExperiment("unknown", "17560331", 3);
        persistAndAutofillExperiment("unknown", "17560331", 3);
        persistAndAutofillExperiment("unknown", "17690294", 5);
        persistAndAutofillExperiment("unknown", "17923091", 6);

        int i=0;
        for (Experiment exp : getDaoFactory().getExperimentDao().getAll()) {
            if (i==0) Assert.assertEquals("liu-2007-1", exp.getShortLabel());
            if (i==1) Assert.assertEquals("liu-2007-2", exp.getShortLabel());
            if (i==2) Assert.assertEquals("liu-2007-3", exp.getShortLabel());
            if (i==3) Assert.assertEquals("liu-2007a-1", exp.getShortLabel());
            if (i==4) Assert.assertEquals("liu-2007b-1", exp.getShortLabel());

            i++;
        }
    }

    private void persistAndAutofillExperiment(String shortlabel, String pubId, int taxId) throws Exception {
        Experiment exp = getMockBuilder().createExperimentEmpty(shortlabel, pubId);
        exp.getBioSource().setTaxId(String.valueOf(taxId));

        ExperimentAutoFill eaf = new ExperimentAutoFill(pubId);
        exp.setShortLabel(eaf.getShortlabel(true));

        PersisterHelper.saveOrUpdate(exp);
    }

    @Test (expected = InvalidPubmedException.class)
    public void experimentAutoFill_wrongPubmedId() throws Exception {
         new ExperimentAutoFill("unassigned2");
    }

}

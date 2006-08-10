/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.application.dataConversion;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;

/**
 * Test for <code>ExperimentListItemTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 08/10/2006
 */
public class ExperimentListItemTest extends TestCase {

    public ExperimentListItemTest(String name) {
        super(name);
    }

    private static final String LABEL_1 = "test-2006-1";
    private static final String LABEL_2 = "test-2006-2";

    private ExperimentListItem mockWithOneLabel;
    private ExperimentListItem mockWithOneLabelLarge;
    private ExperimentListItem mockWithManyLabels;

    public void setUp() throws Exception {
        super.setUp();

        List<String> labels = new ArrayList<String>();
        labels.add(LABEL_1);

        mockWithOneLabel = new ExperimentListItem(labels, "onelabel", true, null, null);
        mockWithOneLabelLarge = new ExperimentListItem(labels, "onelabellarge", false, 2, 2000);

        List<String> labels2 = new ArrayList<String>();
        labels2.add(LABEL_1);
        labels2.add(LABEL_2);

        mockWithManyLabels = new ExperimentListItem(labels2, "manylabel", false, 3, null);
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetFilename() throws Exception {
        assertEquals("onelabel_negative.xml", mockWithOneLabel.getFilename());
        assertEquals("onelabellarge_test-2006-1_02.xml", mockWithOneLabelLarge.getFilename());
        assertEquals("manylabel-03.xml", mockWithManyLabels.getFilename());
    }

    public void testGetPattern() throws Exception {
        assertEquals("test-2006-1", mockWithOneLabel.getPattern());
        assertEquals("test-2006-1", mockWithOneLabelLarge.getPattern());
        assertEquals("test-2006-1,test-2006-2", mockWithManyLabels.getPattern());
    }

    public void testGetChunkNumber() throws Exception {
        assertNull(mockWithOneLabel.getChunkNumber());
        assertEquals(Integer.valueOf(3), mockWithManyLabels.getChunkNumber());
    }

    public void testGetLargeScaleChunkSize() throws Exception {
        assertNull(mockWithOneLabel.getLargeScaleChunkSize());
        assertEquals(Integer.valueOf(2000), mockWithOneLabelLarge.getLargeScaleChunkSize());
    }

    public void testGetName() throws Exception {
        assertEquals("onelabel", mockWithOneLabel.getName());
        assertEquals("manylabel", mockWithManyLabels.getName());
    }

    public void testGetExperimentLabels() throws Exception {
        assertEquals(1, mockWithOneLabel.getExperimentLabels().size());
        assertEquals(2, mockWithManyLabels.getExperimentLabels().size());
    }

    public void testGetInteractionRange() throws Exception {
        assertEquals("", mockWithOneLabel.getInteractionRange());
        assertEquals(" [2001,4000]", mockWithOneLabelLarge.getInteractionRange());
    }

    public void testToString() throws Exception {
        assertEquals("onelabel_negative.xml test-2006-1", mockWithOneLabel.toString());
        assertEquals("onelabellarge_test-2006-1_02.xml test-2006-1 [2001,4000]", mockWithOneLabelLarge.toString());
        assertEquals("manylabel-03.xml test-2006-1,test-2006-2", mockWithManyLabels.toString());
    }    

    public static Test suite() {
        return new TestSuite(ExperimentListItemTest.class);
    }
}

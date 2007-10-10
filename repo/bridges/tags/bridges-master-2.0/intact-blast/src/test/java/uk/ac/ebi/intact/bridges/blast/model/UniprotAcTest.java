/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.blast.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

 /**
 * TODO comment this ... someday
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since <pre>28 Sep 2007</pre>
 */
public class UniprotAcTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link uk.ac.ebi.intact.bridges.blast.model.UniprotAc#UniprotAc(java.lang.String)}.
	 */
	@Test
	public final void testUniprotAc() {
		UniprotAc uniAc1 = new UniprotAc("P12345");
		assertNotNull(uniAc1);
		UniprotAc uniAc2 = new UniprotAc("P12345-2");
		assertNotNull(uniAc2);
		UniprotAc uniAc3 = new UniprotAc("P12345-10");
		assertNotNull(uniAc3);
	}

}

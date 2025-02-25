/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.confidence.global;

import junit.framework.Assert;
import org.apache.commons.collections.ListUtils;
import uk.ac.ebi.intact.bridges.blast.model.UniprotAc;
import uk.ac.ebi.intact.confidence.model.InteractionSimplified;
import uk.ac.ebi.intact.confidence.model.ProteinSimplified;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * General test data.
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since
 * 
 * <pre>
 * 22 Aug 2007
 * </pre>
 */
public class GlobalTestData {

	private List<InteractionSimplified>	binaryInteractions;
	private List<InteractionSimplified>	complexInteractions;
	private List<InteractionSimplified>	compelxWithoutBaitInteractions;
	private List<String>				binaryProteins;

	private GlobalTestData() {
		initBinary();
		initComplex();
		initComplexWithoutBait();
	}

	private static class SingletonHolder {
		private final static GlobalTestData	INSTANCE	= new GlobalTestData();
	}

	public static GlobalTestData getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public static File getTargetDirectory() {
		String outputDirPath = GlobalTestData.class.getResource("/").getFile();
		Assert.assertNotNull(outputDirPath);
		File outputDir = new File(outputDirPath);
		// we are in confidence-score\target\test-classes , move 1 up
		outputDir = outputDir.getParentFile();
		Assert.assertNotNull(outputDir);
		Assert.assertTrue(outputDir.getAbsolutePath(), outputDir.isDirectory());
		Assert.assertEquals("target", outputDir.getName());
		return outputDir;
	}

	public HashMap<String, File> getRightPahts() {
		HashMap<String, File> paths = new HashMap<String, File>(3);
		String osName = System.getProperty("os.name");

		String pathBlastArchive = "";
		String pathUniprotDb = "";
		File pathWorkDir = null;

		if (osName.startsWith("Linux")) {
			pathBlastArchive = "/net/nfs7/vol22/sp-pro5/20071216_iarmean";
			pathUniprotDb = "/net/nfs7/vol22/sp-pro5/20071216_iarmean";
			pathWorkDir = new File("/net/nfs6/vol1/homes/iarmean/tmp");

		} else if (osName.startsWith("Windows")) {
			pathBlastArchive = "E:/20071016_iarmean";
			pathUniprotDb = "E:/tmp";
			pathWorkDir = new File("E:/tmp");
		}
		File pathBlast = new File(pathBlastArchive);
		File pathUniprot = new File(pathUniprotDb);

		testDir(pathBlast);
		testDir(pathUniprot);
		testDir(pathWorkDir);
		paths.put("blastArchieve", pathBlast);
		paths.put("uniprot", pathUniprot);
		paths.put("workDir", pathWorkDir);
		// paths.add(Arrays.asList(pathBlast, pathUniprot, pathWorkDir));
		return paths;
	}

	private void testDir(File workDir) {
		if (!workDir.exists()) {
			throw new IllegalArgumentException("WorkDir must exist! " + workDir.getPath());
		}
		if (!workDir.isDirectory()) {
			throw new IllegalArgumentException("WorkDir must be a directory! " + workDir.getPath());
		}
		if (!workDir.canWrite()) {
			throw new IllegalArgumentException("WorkDir must be writable! " + workDir.getPath());
		}
	}

	private void initBinary() {
		ProteinSimplified comp1 = new ProteinSimplified(new UniprotAc("P12345"), "neutral");
		ProteinSimplified comp2 = new ProteinSimplified(new UniprotAc("Q9W486"), "neutral");
		ProteinSimplified comp3 = new ProteinSimplified(new UniprotAc("P43609"), "neutral");
		ProteinSimplified comp4 = new ProteinSimplified(new UniprotAc("S12345"), "neutral");
		InteractionSimplified intS1 = new InteractionSimplified("EBI-1234", Arrays.asList(comp1, comp2));
		InteractionSimplified intS2 = new InteractionSimplified("EBI-2345", Arrays.asList(comp3, comp4));
		binaryProteins = Arrays.asList(comp1.getUniprotAc().getAcNr(), comp2.getUniprotAc().getAcNr(), comp3.getUniprotAc().getAcNr(), comp4
				.getUniprotAc().getAcNr());
		binaryInteractions = Arrays.asList(intS1, intS2);
	}

	private void initComplex() {
		ProteinSimplified comp11 = new ProteinSimplified(new UniprotAc("P12345"), "bait");
		ProteinSimplified comp12 = new ProteinSimplified(new UniprotAc("Q12345"), "neutral");
		ProteinSimplified comp13 = new ProteinSimplified(new UniprotAc("R12345"), "neutral");
		ProteinSimplified comp14 = new ProteinSimplified(new UniprotAc("S12345"), "neutral");

		InteractionSimplified intS11 = new InteractionSimplified("EBI-3456", Arrays.asList(comp11, comp12, comp13,
				comp14));
		comp11.setRole("neutral");
		comp14.setRole("bait");
		InteractionSimplified intS12 = new InteractionSimplified("EBI-4567", Arrays.asList(comp11, comp12, comp13,
				comp14));

		complexInteractions = Arrays.asList(intS11, intS12);
	}

	private void initComplexWithoutBait() {
		ProteinSimplified comp12 = new ProteinSimplified(new UniprotAc("Q12345"), "neutral");
		ProteinSimplified comp13 = new ProteinSimplified(new UniprotAc("R12345"), "neutral");
		ProteinSimplified comp14 = new ProteinSimplified(new UniprotAc("S12345"), "neutral");
		ProteinSimplified comp11 = new ProteinSimplified(new UniprotAc("P12345"), "neutral");
		ProteinSimplified comp111 = new ProteinSimplified(new UniprotAc("P12345"), "neutral");
		ProteinSimplified comp112 = new ProteinSimplified(new UniprotAc("Q12345"), "neutral");
		ProteinSimplified comp113 = new ProteinSimplified(new UniprotAc("R12345"), "neutral");
		InteractionSimplified intS111 = new InteractionSimplified("EBI-5678", Arrays.asList(comp12, comp14, comp11,
				comp113));
		InteractionSimplified intS112 = new InteractionSimplified("EBI-6789", Arrays.asList(comp13, comp11, comp111,
				comp112));

		compelxWithoutBaitInteractions = Arrays.asList(intS111, intS112);
	}

	public List<InteractionSimplified> getBinaryInteractions() {
		return ListUtils.unmodifiableList(binaryInteractions);
	}

	public List<InteractionSimplified> getComplexInteractions() {
		return ListUtils.unmodifiableList(complexInteractions);
	}

	public List<InteractionSimplified> getCompelxWithoutBaitInteractions() {
		return ListUtils.unmodifiableList(compelxWithoutBaitInteractions);
	}

	public List<String> getBinaryProteins() {
		return ListUtils.unmodifiableList(binaryProteins);
	}

}

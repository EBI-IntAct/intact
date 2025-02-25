/**
 * 
 */
package uk.ac.ebi.intact.interolog.prediction;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import uk.ac.ebi.intact.interolog.proteome.integr8.TreeSpecies;

/**
 * Class created to have an easy access in command line to predict interactions for one species.
 * You just have to give the mitab file with the source interactions 
 * and the porc file with the orthologous clusters (ftp://ftp.ebi.ac.uk/pub/databases/integr8/porc/proc_gene.dat).
 * 
 * @author mmichaut
 * @version $Id$
 * @since 27 nov. 07
 */
@SuppressWarnings("static-access")
public class RunForOneSpecies {
	
	
	// ----------------------------------
	//
	//      Options for command line
	//
	// ----------------------------------
	
	
	public static Option help = OptionBuilder
	.withLongOpt("help")
	.withDescription("print this message")
	.create('h');
	
	public static Option logfile   = OptionBuilder
	.withArgName( "file" )
	.hasArg()
	.withDescription(  "use given file for log" )
	.create( 'l' );
	
	public static Option mitabFile = OptionBuilder
	.withLongOpt("mitab-file")
	.withDescription("MITAB File (Release 2.5) with source interactions")
	//.isRequired() // <-- it is indeed required but it does not print help with -h if we put required here
	.hasArg()
	.withArgName("file")
	.create('i');
	
	public static Option outputDir = OptionBuilder
	.withLongOpt("output-directory")
	.withDescription("Directory where all files will be created")
	//.isRequired()
	.hasArg()
	.withArgName("file")
	.create('o');
	
	public static Option porcFile = OptionBuilder
	.withLongOpt("porc-file")
	.withDescription("PORC file with orthologous clusters")
	.hasArg()
	.withArgName("file")
	.create('p');
	
	public static Option taxid = OptionBuilder
	.withLongOpt("taxid")
	.withDescription("NCBI taxonomy identifier of the species")
	//.isRequired()
	.hasArg()
	.withArgName("int")
	.create('t');
	
	public static Option nodeFile = OptionBuilder
	.withLongOpt("node-file")
	.withDescription("NCBI Taxonomy file with taxonomy nodes")
	//.isRequired()
	.hasArg()
	.withArgName("file")
	.create('n');
	
	public static Option checkTaxid = OptionBuilder
	.withLongOpt("check-taxid")
	.withDescription("If protein accession numbers and taxids are checked between interaction and porc data")
	.create('c');
	
	public static Option nbInterMaxForXml = OptionBuilder
	.withLongOpt("max-nb-inter-xml")
	.withDescription("Maximum nb of interactions to generate a XML file")
	//.isRequired()
	.hasArg()
	.withArgName("int")
	.create('m');
	
	public static Option xml = OptionBuilder
	.withLongOpt("xml-files")
	.withDescription("If output XML files are required")
	.create('x');
	
	public static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Interoporc [OPTIONS]", "Options:", options, 
				"\n(C) EBI-CEA 2008, More information on http://biodev.extra.cea.fr/interoporc/\n", false);
	}
	
	
	

	/**
	 * @param args
	 * @throws InterologPredictionException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws InterologPredictionException, FileNotFoundException {
		Options options = new Options();
		CommandLine line = null;

		// init.
		options.addOption(help);
		options.addOption(logfile);
		options.addOption(mitabFile);
		options.addOption(outputDir);
		options.addOption(porcFile);
		options.addOption(taxid);
		options.addOption(nodeFile);
		options.addOption(checkTaxid);
		options.addOption(nbInterMaxForXml);
		options.addOption(xml);

		// check 0
		if(args.length == 0) {
			printHelp(options);
			return;
		}

		// parse commande line
		CommandLineParser parser = new PosixParser();
		try {
			line = parser.parse(options, args);
		}
		catch(ParseException ex) {
			System.err.println("Command line parsing failed, reason :" + ex.getMessage());
			return;
		}

		// handle options
		if (line.hasOption("h")) {
			printHelp(options);
			return;
		}
		
		// log file
		if (line.hasOption("l")) {
			File propertiesFile = new File(line.getOptionValue("l"));
			if (!propertiesFile.exists()) {
				throw new FileNotFoundException("File "+line.getOptionValue("l")+" not found");
			} else {
				InterologPrediction.log = InterologPrediction.initLog(propertiesFile.getAbsolutePath());
			}
		}
		
		// output dir
		File dir = new File("./");
		if (line.hasOption("o")) {
			dir = new File(line.getOptionValue("o"));
		}
		InterologPrediction up = new InterologPrediction(dir);
		
		// mitab file = source interactions
		if (!line.hasOption("i")) {
			System.err.println("The option -i is required (the source interactions in MITAB25 format).");
		}
		up.setMitab(new File(line.getOptionValue("i")));
		
		// porc file
		if (line.hasOption("p")) {
			up.setPorc(new File(line.getOptionValue("p")));
		}
		
		// target taxid (the species in which we want the predictions
		if (!line.hasOption("t")) {
			System.err.println("The option -t is required (the NCBI taxid of the organism in which we will predict interactions).");
		}
		long taxid = Long.parseLong(line.getOptionValue("t"));
		Collection<Long> taxids = new HashSet<Long>(1);
		taxids.add(taxid);
		up.setUserTaxidsToDownCast(taxids);
		up.setDownCastOnChildren(true);
		
		if (line.hasOption("c")) {
			up.setCheckTaxid(true);
		}
		
		if (line.hasOption("n")) {
			File nodesFile = new File(line.getOptionValue("n"));
			TreeSpecies.parse(nodesFile);
			if (!line.hasOption("c")) {
				InterologPrediction.log.warn("Since you have given a taxonomy file, taxid will be checked (option -c)");
				up.setCheckTaxid(true);
			}
		}
		
		// output in xml
		boolean generateXml = false;
		if (line.hasOption("x")) {
			generateXml = true;
		}
		
		int nbInterMaxForXml = 15000;
		if (line.hasOption("m")) {
			nbInterMaxForXml = Integer.parseInt(line.getOptionValue("m"));
		}
		up.setNbInterMaxForXml(nbInterMaxForXml);
		up.setGenerateXml(generateXml);
		
		// run the tool
		up.run();

	}

}

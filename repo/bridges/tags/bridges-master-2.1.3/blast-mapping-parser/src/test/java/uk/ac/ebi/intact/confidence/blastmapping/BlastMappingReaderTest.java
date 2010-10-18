/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.confidence.blastmapping;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import uk.ac.ebi.intact.confidence.blastmapping.jaxb.EBIApplicationResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Test class for BlastMappingReader.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version 0.1
 * @since <pre>
 *        6 Sep 2007
 *        </pre>
 */
public class BlastMappingReaderTest extends TestCase {

    /*
      * (non-Javadoc)
      *
      * @see junit.framework.TestCase#setUp()
      */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
      * (non-Javadoc)
      *
      * @see junit.framework.TestCase#tearDown()
      */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for
     * {@link uk.ac.ebi.intact.confidence.blastmapping.BlastMappingReader#read(String)}.
     *
     * @throws BlastMappingException
     */
    public final void testReadString() throws BlastMappingException {
        BlastMappingReader reader = new BlastMappingReader();
        EBIApplicationResult ear = reader
                .read( "<?xml version=\"1.0\"?> "
                       + "<EBIApplicationResult xmlns=\"http://www.ebi.ac.uk/schema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://www.ebi.ac.uk/schema/ApplicationResult.xsd\">"
                       + "<Header>"
                       + "<program name=\"WU-blastp\" version=\"2.0MP-WashU [04-May-2006]\" citation=\"PMID:12824421\"/>"
                       + "<commandLine command=\"/ebi/extserv/bin/wu-blast/blastp &quot;uniprot&quot; /ebi/extserv/blast-work/interactive/blast-20070906-15383562.input E=10 B=50 V=100 -mformat=7,/ebi/extserv/blast-work/interactive/blast-20070906-15383562_app.xml -mformat=1 -matrix BLOSUM62 -sump  -echofilter -filter seg -cpus 8 -sort_by_pvalue -putenv=&apos;WUBLASTMAT=/ebi/extserv/bin/wu-blast/matrix&apos; -putenv=&quot;WUBLASTDB=$IDATA_CURRENT/blastdb&quot; -putenv=&apos;WUBLASTFILTER=/ebi/extserv/bin/wu-blast/filter&apos; \"/>"
                       + "<parameters>"
                       + "<sequences total=\"1\">"
                       + "<sequence number=\"1\" name=\"Sequence\" type=\"p\" length=\"557\"/>"
                       + "</sequences>"
                       + "<databases total=\"1\" sequences=\"5053795\" letters=\"1658214983\">"
                       + "<database number=\"1\" name=\"uniprot\" type=\"p\" created=\"2007-08-20T23:45:28+01:00\"/>"
                       + "</databases>"
                       + "<scores>100</scores>"
                       + "<alignments>50</alignments>"
                       + "<matrix>BLOSUM62</matrix>"
                       + "<expectationUpper>10</expectationUpper>"
                       + "<statistics>sump</statistics>"
                       + "<filter>seg</filter>"
                       + "</parameters>"
                       + "<timeInfo start=\"2007-09-06T15:38:43+01:00\" end=\"2007-09-06T15:39:31+01:00\" search=\"PT48S\"/>"
                       + "</Header>"
                       + "<SequenceSimilaritySearchResult>"
                       + "<hits total=\"50\">"
                       + "<hit number=\"1\" database=\"uniprot\" id=\"RSC8_YEAST\" ac=\"P43609\" length=\"557\" description=\"Chromatin structure-remodeling complex protein RSC8 (Remodel the structure of chromatin complex subunit 8) (SWI3 homolog).\">"
                       + "<alignments total=\"1\">"
                       + "<alignment number=\"1\">"
                       + "<score>2589</score>"
                       + "<bits>916.4</bits>"
                       + "<expectation>4.5e-268</expectation>"
                       + "<probability>4.5e-268</probability>"
                       + "<identity>91</identity>"
                       + "<positives>91</positives>"
                       + "<querySeq start=\"1\" end=\"557\">MSDTEKDKDVPMVDXXXXXXXXXXXXXXXXXFPHLAQEQAKEESATLGAEVAHKKINYEQEAQKLEEKALRFLAKQTHPVIIPSFASWFDISKIHEIEKRSNPDFFNDSSRFKTPKAYKDTRNFIINTYRLSPYEYLTITAVRRNVAMDVASIVKIHAFLEKWGLINYQIDPRTKPSLIGPSFTGHFQVVLDTPQGLKPFLPENVIKQEVEGGDGAEPQVKKEFPVNLTIKKNVYDSAQDFNALQDESRNSRQIHKVYICHTCGNESINVRYHNLRARDTNLCSRCFQEGHFGANFQSSDFIRLENNGNSVKKNWSDQEMLLLLEGIEMYEDQWEKIADHVGGHKRVEDCIEKFLSLPIEDNYIREVVGSTLNXXXXXXXXXXXXXXXLMECVNDAVQTLLQGDDKLGKVSDKSREISEKYIEESQAIIQELVKLTMEKLESKFTKLCDLETQLEMEKLKYVKESEKMLNDRLSLSKQILDLNKSLEELNVSKKLVLISEQVDSGIQLVEKDQEGDDEDGNTATXXXXXXXXXXXXXXXXXDSIAKLQPQVYKPWSL</querySeq>"
                       + "<pattern>MSDTEKDKDVPMVD                 FPHLAQEQAKEESATLGAEVAHKKINYEQEAQKLEEKALRFLAKQTHPVIIPSFASWFDISKIHEIEKRSNPDFFNDSSRFKTPKAYKDTRNFIINTYRLSPYEYLTITAVRRNVAMDVASIVKIHAFLEKWGLINYQIDPRTKPSLIGPSFTGHFQVVLDTPQGLKPFLPENVIKQEVEGGDGAEPQVKKEFPVNLTIKKNVYDSAQDFNALQDESRNSRQIHKVYICHTCGNESINVRYHNLRARDTNLCSRCFQEGHFGANFQSSDFIRLENNGNSVKKNWSDQEMLLLLEGIEMYEDQWEKIADHVGGHKRVEDCIEKFLSLPIEDNYIREVVGSTLN               LMECVNDAVQTLLQGDDKLGKVSDKSREISEKYIEESQAIIQELVKLTMEKLESKFTKLCDLETQLEMEKLKYVKESEKMLNDRLSLSKQILDLNKSLEELNVSKKLVLISEQVDSGIQLVEKDQEGDDEDGNTAT                 DSIAKLQPQVYKPWSL</pattern>"
                       + "<matchSeq start=\"1\" end=\"557\">MSDTEKDKDVPMVDSHEATEEPPTTSTNTPSFPHLAQEQAKEESATLGAEVAHKKINYEQEAQKLEEKALRFLAKQTHPVIIPSFASWFDISKIHEIEKRSNPDFFNDSSRFKTPKAYKDTRNFIINTYRLSPYEYLTITAVRRNVAMDVASIVKIHAFLEKWGLINYQIDPRTKPSLIGPSFTGHFQVVLDTPQGLKPFLPENVIKQEVEGGDGAEPQVKKEFPVNLTIKKNVYDSAQDFNALQDESRNSRQIHKVYICHTCGNESINVRYHNLRARDTNLCSRCFQEGHFGANFQSSDFIRLENNGNSVKKNWSDQEMLLLLEGIEMYEDQWEKIADHVGGHKRVEDCIEKFLSLPIEDNYIREVVGSTLNGKGGDSRDGSVSGSKLMECVNDAVQTLLQGDDKLGKVSDKSREISEKYIEESQAIIQELVKLTMEKLESKFTKLCDLETQLEMEKLKYVKESEKMLNDRLSLSKQILDLNKSLEELNVSKKLVLISEQVDSGIQLVEKDQEGDDEDGNTATGHGVKRVGKEGEEVGEGDSIAKLQPQVYKPWSL</matchSeq>"
                       + "</alignment>"
                       + "</alignments>"
                       + "</hit>"
                       + "<hit number=\"2\" database=\"uniprot\" id=\"Q7KPY3_DROME\" ac=\"Q7KPY3\" length=\"1189\" description=\"Moira.\">"
                       + "<alignments total=\"3\">"
                       + "<alignment number=\"1\">"
                       + "<score>409</score>"
                       + "<bits>149.0</bits>"
                       + "<expectation>3.4e-54</expectation>"
                       + "<probability>3.4e-54</probability>"
                       + "<identity>49</identity>"
                       + "<positives>72</positives>"
                       + "<querySeq start=\"57\" end=\"203\">NYEQEAQKLEEKALRFLAKQTHPVIIPSFASWFDISKIHEIEKRSNPDFFNDSSRFKTPKAYKDTRNFIINTYRLSPYEYLTITAVRRNVAMDVASIVKIHAFLEKWGLINYQIDPRTKPSLIGPSFTGHFQVVLDTPQGLKPFLPE</querySeq>"
                       + "<pattern>N ++ +   +E     + +QTH +I+PS+++WFD + IH IEKR+ P+FFN  ++ KTP+ Y   RNF+I+TYRL+P EYLT TA RRN+A DV +I+++HAFLE+WGLINYQID   +P+ +GP  T HF ++ DTP GL+   P+</pattern>"
                       + "<matchSeq start=\"407\" end=\"553\">NTQEFSSSAKEDMEDNVTEQTHHIIVPSYSAWFDYNSIHVIEKRAMPEFFNSKNKSKTPEIYMAYRNFMIDTYRLNPTEYLTTTACRRNLAGDVCAIMRVHAFLEQWGLINYQIDADVRPTPMGPPPTSHFHILSDTPSGLQSINPQ</matchSeq>"
                       + "</alignment>"
                       + "<alignment number=\"2\">"
                       + "<score>168</score>"
                       + "<bits>64.2</bits>"
                       + "<expectation>3.4e-54</expectation>"
                       + "<probability>3.4e-54</probability>"
                       + "<identity>50</identity>"
                       + "<positives>73</positives>"
                       + "<querySeq start=\"310\" end=\"366\">SVKKNWSDQEMLLLLEGIEMYEDQWEKIADHVGGHKRVEDCIEKFLSLPIEDNYIRE</querySeq>"
                       + "<pattern>S+ + W+DQE LLLLEG+EM++D W K+ +HVG   + ++CI  FL LPIED Y+ +</pattern>"
                       + "<matchSeq start=\"630\" end=\"685\">SMAREWTDQETLLLLEGLEMHKDDWNKVCEHVGSRTQ-DECILHFLRLPIEDPYLED</matchSeq>"
                       + "</alignment>"
                       + "<alignment number=\"3\">"
                       + "<score>59</score>"
                       + "<bits>25.8</bits>"
                       + "<expectation>3.4e-54</expectation>"
                       + "<probability>3.4e-54</probability>"
                       + "<identity>21</identity>"
                       + "<positives>53</positives>"
                       + "<querySeq start=\"407\" end=\"472\">LGKVSDKSREISEKYIEESQAIIQELVKLTMEKLESKFTKLCDLETQLEMEKLKYVKESEKMLNDR</querySeq>"
                       + "<pattern>L   + K++ ++     + ++++  LV+  M+KLE K     +LE  +E E+     + ++++ +R</pattern>"
                       + "<matchSeq start=\"934\" end=\"999\">LASAAVKAKHLAALEERKIKSLVALLVETQMKKLEIKLRHFEELEATMEREREGLEYQRQQLITER</matchSeq>"
                       + "</alignment>" + "</alignments>" + "</hit>" + "</hits>" + "</SequenceSimilaritySearchResult>"
                       + "</EBIApplicationResult>" );
        Assert.assertNotNull( ear );
    }

    /**
     * Test method for
     * {@link uk.ac.ebi.intact.confidence.blastmapping.BlastMappingReader#read(File)}.
     *
     * @throws BlastMappingException
     */
    public final void testReadFile() throws BlastMappingException {
        BlastMappingReader reader = new BlastMappingReader();
        EBIApplicationResult ear = reader.read( new File( BlastMappingReaderTest.class.getResource( "P43609.xml" ).getPath() ) );
        Assert.assertNotNull( ear );
    }

    @Test
    public final void testReadFile2() throws BlastMappingException {
        BlastMappingReader reader = new BlastMappingReader();
        EBIApplicationResult ear = reader.read( new File( BlastMappingReaderTest.class.getResource( "O94942.xml" ).getPath() ) );
        Assert.assertNotNull( ear );
    }

    @Test
    public final void testReadFileEBIApplicationError1() throws BlastMappingException {
        BlastMappingReader reader = new BlastMappingReader();
        EBIApplicationResult ear = reader.read( new File( BlastMappingReaderTest.class.getResource( "P36096.xml" ).getPath() ) );
        Assert.assertNull( ear );
    }

    @Test
    public final void testReadFileEBIApplicationError2() throws BlastMappingException {
        BlastMappingReader reader = new BlastMappingReader();
        EBIApplicationResult ear = reader.read( new File( BlastMappingReaderTest.class.getResource( "Q9D1K4.xml" ).getPath() ) );
        Assert.assertNull( ear );
    }

    /**
     * Test method for
     * {@link uk.ac.ebi.intact.confidence.blastmapping.BlastMappingReader#read(InputStream)}.
     *
     * @throws BlastMappingException
     */
    public final void testReadInputStream() throws BlastMappingException {
        BlastMappingReader reader = new BlastMappingReader();
        EBIApplicationResult ear;
        try {
            ear = reader.read( new FileInputStream( BlastMappingReaderTest.class.getResource( "P43609.xml" ).getPath() ) );
            Assert.assertNotNull( ear );
        } catch ( FileNotFoundException e) {
			fail();
		}	
	}

}

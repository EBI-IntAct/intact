/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.irefindex.seguid;

import org.junit.Assert;
import org.junit.Test;

/**
 * This class is used to test the RogidGenerator
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class RogidGeneratorTest {

    @Test
    public void generateSeguid() throws Exception {
        String sequence = "arndceqghil";
        RogidGenerator rogidGenerator = new RogidGenerator();
        final String seguid = rogidGenerator.calculateSeguid( sequence );
        Assert.assertEquals( seguid, "qF2cjL7/GegxGY6uEvgUFBhfcs0" );
    }


    @Test
    public void generateRogid() throws Exception {
        String sequence = "arndceqghil";
        String taxid = "3702";

        RogidGenerator rogidGenerator = new RogidGenerator();
        final String rogid = rogidGenerator.calculateRogid( sequence, taxid );
        Assert.assertEquals( rogid, "agaxiUJROmjYnzKOyjUC7+bxqLE3702" );

    }


    @Test
    public void testWithP60785() throws Exception {
        String P60785 = getProteinSequence( "P60785" );
        String taxid = "83333";

        RogidGenerator rogidGenerator = new RogidGenerator();
        final String rogid = rogidGenerator.calculateRogid( P60785, taxid );
        Assert.assertEquals( rogid, "Cbg5sIpcGEf4PyIcOCHItl8bUsU83333" );

    }

    @Test
    public void testWithP33025() throws Exception {
        String P33025 = getProteinSequence( "P33025" );
        String taxid = "83333";

        RogidGenerator rogidGenerator = new RogidGenerator();
        final String rogid = rogidGenerator.calculateRogid( P33025, taxid );
        Assert.assertEquals( rogid, "PMVAB7g3HC7RSmr21gc3I9pwpjQ83333" );

    }

    /**
     * The algorithm generates the Rogid for any given string...it
     * doesn't validate the sequence...so it is assumed that
     * we pass the right protein sequence
     *
     * @throws Exception
     */
    @Test
    public void testWithInvalid() throws Exception {
        String invalidSequence = "atcg?lakdjsf&asdlfkjaf";
        String taxid = "83333";

        RogidGenerator rogidGenerator = new RogidGenerator();
        final String rogid = rogidGenerator.calculateRogid( invalidSequence, taxid );
        Assert.assertEquals( rogid, "1otqoa4FhqxO0cbjNonPaWLGyAM83333" );

    }

    protected static String getProteinSequence( String uniprotId ) {

        //>sp|P60785|LEPA_ECOLI GTP-binding protein lepA OS=Escherichia coli (strain K12) GN=lepA PE=1 SV=1
        String P60785 = "MKNIRNFSIIAHIDHGKSTLSDRIIQICGGLSDREMEAQVLDSMDLERERGITIKAQSVT" +
                        "LDYKASDGETYQLNFIDTPGHVDFSYEVSRSLAACEGALLVVDAGQGVEAQTLANCYTAM" +
                        "EMDLEVVPVLNKIDLPAADPERVAEEIEDIVGIDATDAVRCSAKTGVGVQDVLERLVRDI" +
                        "PPPEGDPEGPLQALIIDSWFDNYLGVVSLIRIKNGTLRKGDKVKVMSTGQTYNADRLGIF" +
                        "TPKQVDRTELKCGEVGWLVCAIKDIHGAPVGDTLTLARNPAEKALPGFKKVKPQVYAGLF" +
                        "PVSSDDYEAFRDALGKLSLNDASLFYEPESSSALGFGFRCGFLGLLHMEIIQERLEREYD" +
                        "LDLITTAPTVVYEVETTSREVIYVDSPSKLPAVNNIYELREPIAECHMLLPQAYLGNVIT" +
                        "LCVEKRGVQTNMVYHGNQVALTYEIPMAEVVLDFFDRLKSTSRGYASLDYNFKRFQASDM" +
                        "VRVDVLINGERVDALALITHRDNSQNRGRELVEKMKDLIPRQQFDIAIQAAIGTHIIARS" +
                        "TVKQLRKNVLAKCYGGDISRKKKLLQKQKEGKKRMKQIGNVELPQEAFLAILHVGKDNK";


        //>sp|P33025|YEIN_ECOLI Uncharacterized protein yeiN OS=Escherichia coli (strain K12) GN=yeiN PE=4 SV=1
        String P33025 = "MSELKISPELLQISPEVQDALKNKKPVVALESTIISHGMPFPQNAQTAIEVEETIRKQGA" +
                        "VPATIAIIGGVMKVGLSKEEIELLGREGHNVTKVSRRDLPFVVAAGKNGATTVASTMIIA" +
                        "ALAGIKVFATGGIGGVHRGAEHTFDISADLQELANTNVTVVCAGAKSILDLGLTTEYLET" +
                        "FGVPLIGYQTKALPAFFCRTSPFDVSIRLDSASEIARAMVVKWQSGLNGGLVVANPIPEQ" +
                        "FAMPEHTINAAIDQAVAEAEAQGVIGKESTPFLLARVAELTGGDSLKSNIQLVFNNAILA" +
                        "SEIAKEYQRLAG";

        //>sp|P67080|YGGS_ECOLI UPF0001 protein yggS OS=Escherichia coli (strain K12) GN=yggS PE=1 SV=1
        String P67080 = "MNDIAHNLAQVRDKISAAATRCGRSPEEITLLAVSKTKPASAIAEAIDAGQRQFGENYVQ" +
                        "EGVDKIRHFQELGVTGLEWHFIGPLQSNKSRLVAEHFDWCHTIDRLRIATRLNDQRPAEL" +
                        "PPLNVLIQINISDENSKSGIQLAELDELAAAVAELPRLRLRGLMAIPAPESEYVRQFEVA" +
                        "RQMAVAFAGLKTRYPHIDTLSLGMSDDMEAAIAAGSTMVRIGTAIFGARDYSKK";


        if ( "P60785".equals( uniprotId ) ) {
            return P60785;
        }

        if ( "P33025".equals( uniprotId ) ) {
            return P33025;
        }

        if ( "P67080".equals( uniprotId ) ) {
            return P67080;
        }

        return "";
    }


}

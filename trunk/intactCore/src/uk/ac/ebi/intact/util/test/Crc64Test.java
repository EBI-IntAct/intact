/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util.test;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import uk.ac.ebi.intact.util.Crc64;

public class Crc64Test extends TestCase {


    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     * @param name the name of the test.
     */
    public Crc64Test(String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(Crc64Test.class);
    }

    public void testGetGoResponse() {
        testCrc64();
    }

    private void testCrc64() {

        /**
         * From P05067
         * http://srs.ebi.ac.uk/srs7bin/cgi-bin/wgetz?-e+[SWALL-acc:P05067]+-vn+2+-ascii
         */
        String sequence = "MLPGLALLLLAAWTARALEVPTDGNAGLLAEPQIAMFCGRLNMHMNVQNGKWDSDPSGTK" +
                          "TCIDTKEGILQYCQEVYPELQITNVVEANQPVTIQNWCKRGRKQCKTHPHFVIPYRCLVG" +
                          "EFVSDALLVPDKCKFLHQERMDVCETHLHWHTVAKETCSEKSTNLHDYGMLLPCGIDKFR" +
                          "GVEFVCCPLAEESDNVDSADAEEDDSDVWWGGADTDYADGSEDKVVEVAEEEEVAEVEEE" +
                          "EADDDEDDEDGDEVEEEAEEPYEEATERTTSIATTTTTTTESVEEVVREVCSEQAETGPC" +
                          "RAMISRWYFDVTEGKCAPFFYGGCGGNRNNFDTEEYCMAVCGSAMSQSLLKTTQEPLARD" +
                          "PVKLPTTAASTPDAVDKYLETPGDENEHAHFQKAKERLEAKHRERMSQVMREWEEAERQA" +
                          "KNLPKADKKAVIQHFQEKVESLEQEAANERQQLVETHMARVEAMLNDRRRLALENYITAL" +
                          "QAVPPRPRHVFNMLKKYVRAEQKDRQHTLKHFEHVRMVDPKKAAQIRSQVMTHLRVIYER" +
                          "MNQSLSLLYNVPAVAEEIQDEVDELLQKEQNYSDDVLANMISEPRISYGNDALMPSLTET" +
                          "KTTVELLPVNGEFSLDDLQPWHSFGADSVPANTENEVEPVDARPAADRGLTTRPGSGLTN" +
                          "IKTEEISEVKMDAEFRHDSGYEVHHQKLVFFAEDVGSNKGAIIGLMVGGVVIATVIVITL" +
                          "VMLKKKQYTSIHHGVVEVDAAVTPEERHLSKMQQNGYENPTYKFFEQMQN";

        String expectedCrc64 = "A12EE761403740F5";


        String crc64    = Crc64.getCrc64( sequence );
        String crc64bis = Crc64.getCrc64( sequence );

        /**
         * Check that the generated CRC64 is the same than the one generated in the swiss prot entry
         */
        assertEquals( crc64, expectedCrc64 );

        /**
         * Check that the CRC64 generation is deterministic.
         */
        assertEquals( crc64, crc64bis );
    }
}

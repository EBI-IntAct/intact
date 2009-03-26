/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.bridges.picr;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;
import uk.ac.ebi.picr.model.CrossReference;
import uk.ac.ebi.picr.model.UPEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniparc.UniParcEntry;

import java.util.List;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PicrClientTest {

    private PicrClient client;

    @Before
    public void before() {
        client = new PicrClient();
    }

    @After
    public void after() {
        client = null;
    }

    @Test
    public void getUPIForAccession() {
        String upi = client.getUPIForAccession("NP_417804", PicrSearchDatabase.REFSEQ);
        Assert.assertEquals("UPI000003EADC", upi);
    }

    @Test
    public void getUPIForSequence() {
        String seq = "MRFAIVVTGPAYGTQQASSAFQFAQALIADGHELSSVFFYREGVYNANQLTSPASDEFDLVRAWQQLNAQHGVALNICVAAALRRGVVDETEAGRLGLASSNLQQGFTLSGLGALAEASLTCDRVVQF";

        String upi = client.getUPIForSequence(seq, null, PicrSearchDatabase.values());
        Assert.assertEquals("UPI000003EADC", upi);
     }

    @Test
    public void getUPEntriesForSequence() {
        String seq = "MRFAIVVTGPAYGTQQASSAFQFAQALIADGHELSSVFFYREGVYNANQLTSPASDEFDLVRAWQQLNAQHGVALNICVAAALRRGVVDETEAGRLGLASSNLQQGFTLSGLGALAEASLTCDRVVQF";

        UPEntry upEntry = client.getUPEntriesForSequence(seq, null, PicrSearchDatabase.values());
        Assert.assertEquals("UPI000003EADC", upEntry.getUPI());
     }
}

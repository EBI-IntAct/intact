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
package uk.ac.ebi.intact.psimitab.search;


import psidev.psi.mi.search.Searcher;
import psidev.psi.mi.search.SearchResult;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Assert;

import java.io.InputStream;

/**
 * IntactInteractorIndexWriter Tester.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactInteractorIndexWriterTest {

    private IntactInteractorIndexWriter indexWriter;
    private Directory directory;

    @Before
    public void before() throws Exception {
        indexWriter = new IntactInteractorIndexWriter();
        directory = new RAMDirectory();
        //directory = FSDirectory.getDirectory( "C:\\testIndex" );
    }

    @After
    public void after() throws Exception {
        indexWriter = null;
        directory.close();
        directory = null;
    }

    @Test
    public void index3() throws Exception {
        InputStream is = IntactInteractorIndexWriterTest.class.getResourceAsStream("/mitab_samples/imatinib_small.txt");
        indexWriter.index(directory, is, true, true);

        assertSearchResultCount(3, "*");
    }

    @Test
    public void index4() throws Exception {
        InputStream is = IntactInteractorIndexWriterTest.class.getResourceAsStream("/mitab_samples/aspirin.tsv");
        indexWriter.index(directory, is, true, true);

        assertSearchResultCount(17, "*");

        assertSearchResultCount(5, "aspirine");

        // check column by column

        // protein identifier A
        assertSearchResultCount(1, "idA:P23219");
        assertSearchResultCount(2, "P23219");
        // compound identifier A
        assertSearchResultCount(7, "DB00371");

        // protein identifier B
        assertSearchResultCount(2, "P60045");
        // compound identifier B
        assertSearchResultCount(5, "DB00497");

        // alternative identifier A
        assertSearchResultCount(7, "Cirpon");

        // alternative identifier B
        assertSearchResultCount(5, "Acetophen");

        // aliases A
        assertSearchResultCount(2, "GABRA6");

        // aliases B
        assertSearchResultCount(2, "GABRA1");

        // properties A
        assertSearchResultCount(0, "1PXX_A");

        // properties B
        assertSearchResultCount(0, "APRD00387");

        // properties of interactions
        assertSearchResultCount(17, "18048412");
        assertSearchResultCount(16, "9606");
    }


    @Test
    public void annotationSearchTest() throws Exception {
        //String header = "#ID(s) interactor A	ID(s) interactor B	Alt. ID(s) interactor A	Alt. ID(s) interactor B	Alias(es) interactor A	Alias(es) interactor B	Interaction detection method(s)	Publication 1st author(s)	Publication Identifier(s)	Taxid interactor A	Taxid interactor B	Interaction type(s)	Source database(s)	Interaction identifier(s)	Confidence value(s)	Experimental role(s) interactor A	Experimental role(s) interactor B	Biological role(s) interactor A	Biological role(s) interactor B	Properties interactor A	Properties interactor B	Type(s) interactor A	Type(s) interactor B	HostOrganism(s)	Expansion method(s)	Dataset name(s)	Annotation(s) interactor A	Annotation(s) interactor B	Parameter(s) interactor A	Parameter(s) interactor B	Parameter(s) interaction";
        //String line1 = "pubchem:5291|drugbank:DB00619|intact:DGI-337878	uniprotkb:P08183|intact:DGI-296810	intact:Imatinib Mesylate(drug brand name)|intact:Imatinib Methansulfonate(drug brand name)|intact:Imatinib(commercial name)|intact:Gleevec(drug brand name)|intact:Glivec(drug brand name)	uniprotkb:MDR1(gene name synonym)|uniprotkb:PGY1(gene name synonym)|uniprotkb:ATP-binding cassette sub-family B member 1(gene name synonym)|uniprotkb:P-glycoprotein 1(gene name synonym)|uniprotkb:mdr1_human	-	uniprotkb:ABCB1	psi-mi:\"MI:0045\"(experimental interac)	-	pubmed:18048412	taxid:-3(unknown)	taxid:9606(human)	psi-mi:\"MI:0407\"(direct interaction)	psi-mi:\"MI:1002\"(DrugBank)	intact:DGI-337971	-	psi-mi:\"MI:1094\"(drug)	psi-mi:\"MI:1095\"(drug target)	psi-mi:\"MI:0499\"(unspecified role)	psi-mi:\"MI:0499\"(unspecified role)	drugbank:APRD01028|pubmed:12869662|pubmed:11175855|pubmed:16779792|pubmed:14988091|pubmed:15980865|din:02253283	ensembl:ENSG00000085563|refseq:NP_000918.2|go:\"GO:0009986\"(cell surface)|go:\"GO:0016021\"(integral to membrane)|go:\"GO:0005624\"(membrane fraction)|go:\"GO:0005524\"(ATP binding)|go:\"GO:0042626\"(ATPase activity, coupled to transmembrane movement of substances)|go:\"GO:0005515\"(protein binding)|go:\"GO:0042493\"(response to drug)|go:\"GO:0006810\"(transport)|interpro:IPR003593(AAA+ ATPase, core)|interpro:IPR011527(ABC transporter, transmembrane region, type 1)|interpro:IPR001140(ABC transporter, transmembrane region)|interpro:IPR003439(ABC transporter-like)|uniprotkb:Q12755|uniprotkb:Q14812	psi-mi:\"MI:0328\"(small molecule)	psi-mi:\"MI:0326\"(protein)	taxid:-3(unknown)	-	DrugBank - a knowledgebase for drugs, drug actions and drug targets.	biotech prep:\"Imatinib is a drug used to treat certain types of cancer. It is currently marketed by Novartis as Gleevec (USA) or Glivec (Europe/Australia) as its mesylate salt, imatinib mesilate (INN). It is occasionally referred to as CGP57148B or STI571 (especially in older publications). It is used in treating chronic myelogenous leukemia (CML), gastrointestinal stromal tumors (GISTs) and a number of other malignancies.It is the first member of a new class of agents that act by inhibiting particular tyrosine kinase enzymes, instead of non-specifically inhibiting rapidly dividing cells.\"|drug type:Small Molecule; Approved|drug category:Antineoplastic Agents; Protein Kinase Inhibitors|disease indication:\"For the treatment of newly diagnosed adult patients with Philadelphia chromosome positive chronic myeloid leukemia (CML). Also indicated for the treatment of pediatric patients with Ph+ chronic phase CML whose disease has recurred after stem cell transplant or who are resistant to interferon-alpha therapy. Also indicated with unresectable and/or metastatic malignant gastrointestinal stromal tumors (GIST).\"|pharmacology:\"Imatinib is an antineoplastic agent used to treat chronic myelogenous leukemia. Imatinib is a 2-phenylaminopyrimidine derivative that functions as a specific inhibitor of a number of tyrosine kinase enzymes. In chronic myelogenous leukemia, the Philadelphia chromosome leads to a fusion protein of Abl with Bcr (breakpoint cluster region), termed Bcr-Abl. As this is now a continuously active tyrosine kinase, Imatinib is used to decrease Bcr-Abl activity.\"|mechanism of action:\"Imatinib mesylate is a protein-tyrosine kinase inhibitor that inhibits the Bcr-Abl tyrosine kinase, the constitutive abnormal tyrosine kinase created by the Philadelphia chromosome abnormality in chronic myeloid leukemia (CML). It inhibits proliferation and induces apoptosis in Bcr-Abl positive cell lines as well as fresh leukemic cells from Philadelphia chromosome positive chronic myeloid leukemia. Imatinib also inhibits the receptor tyrosine kinases for platelet derived growth factor (PDGF) and stem cell factor (SCF) - called c-kit. Imatinib was identified in the late 1990s by Dr Brian J. Druker. Its development is an excellent example of rational drug design. Soon after identification of the bcr-abl target, the search for an inhibitor began. Chemists used a high-throughput screen of chemical libraries to identify the molecule 2-phenylaminopyrimidine. This lead compound was then tested and modified by the introduction of methyl and benzamide groups to give it enhanced binding properties, resulting in imatinib.\"|comment:\"drug absorption (MI:2045): Imatinib is well absorbed with mean absolute bioavailability is 98% with maximum levels achieved within 2-4 hours of dosing\"|toxicity attribute name:\"Side effects include nausea, vomiting, diarrhea, loss of appetite, dry skin, hair loss, swelling (especially in the legs or around the eyes) and muscle cramps\"|comment:\"plasma protein binding (MI:2047):Very high (95%)\"|drug metabolism:Primarily hepatic via CYP3A4. Other cytochrome P450 enzymes, such as CYP1A2, CYP2D6, CYP2C9, and CYP2C19, play a minor role in its metabolism. The main circulating active metabolite in humans is the N-demethylated piperazine derivative, formed predominantly by CYP3A4.|comment:\"elimination half life (MI:2049): 18 hours for Imatinib, 40 hours for its major active metabolite, the N-desmethyl derivative\"|dosage form:Tablet Oral|dosage form:Capsule Oral|organisms affected:Humans and other mammals|food interaction:Take with food to reduce the incidence of gastric irritation. Follow with a large glass of water. A lipid rich meal will slightly reduce and delay absorption. Avoid grapefruit and grapefruit juice throughout treatment, grapefruit can significantly increase serum levels of this product.|drug interaction:Acetaminophen Increased hepatic toxicity of both agents|drug interaction:Anisindione Imatinib increases the anticoagulant effect|drug interaction:Dicumarol Imatinib increases the anticoagulant effect|drug interaction:Acenocoumarol Imatinib increases the anticoagulant effect|drug interaction:Warfarin Imatinib increases the anticoagulant effect|drug interaction:Aprepitant Aprepitant may change levels of chemotherapy agent|drug interaction:Atorvastatin Increases the effect and toxicity of atorvastatin|drug interaction:Carbamazepine Carbamazepine decreases levels of imatinib|drug interaction:Cerivastatin Imatinib increases the effect and toxicity of statin|drug interaction:Cyclosporine Imatinib increases the effect and toxicity of cyclosporine|drug interaction:Dexamethasone Dexamethasone decreases levels of imatinib|drug interaction:Lovastatin Imatinib increases the effect and toxicity of statin|drug interaction:Simvastatin Imatinib increases the effect and toxicity of statin|drug interaction:St. John's Wort St. John's Wort decreases levels of imatinib|drug interaction:Rifampin Rifampin decreases levels of imatinib|drug interaction:Pimozide Increases the effect and toxicity of pimozide|drug interaction:Phenobarbital Phenobarbital decreases levels of imatinib|drug interaction:Nifedipine Imatinib increases the effect and toxicity of nifedipine|drug interaction:Clarithromycin The macrolide increases levels of imatinib|drug interaction:Erythromycin The macrolide increases levels of imatinib|drug interaction:Josamycin The macrolide increases levels of imatinib|drug interaction:Ketoconazole The imidazole increases the levels of imatinib|drug interaction:Itraconazole The imidazole increases the levels of imatinib|drug interaction:Ethotoin The hydantoin decreases the levels of imatinib|drug interaction:Fosphenytoin The hydantoin decreases the levels of imatinib|drug interaction:Mephenytoin The hydantoin decreases the levels of imatinib|drug interaction:Phenytoin The hydantoin decreases the levels of imatinib|inchi id:\"InChI=1/C29H31N7O/c1-21-5-10-25(18-27(21)34-29-31-13-11-26(33-29)24-4-3-12-30-19-24)32-28(37)23-8-6-22(7-9-23)20-36-16-14-35(2)15-17-36/h3-13,18-19H,14-17,20H2,1-2H3,(H,32,37)(H,31,33,34)/f/h32,34H\"|url:\"http://www.rxlist.com/cgi/generic3/gleevec.htm\"|comment:\"melting point (MI:2026): 226 oC (mesylate salt)\"|comment:\"average molecular weight (MI:2155): 493.6027\"|comment:\"monoisotopic molecular weight (MI:2156): 493.2590\"|comment:\"experimental h2o solubility (MI:2157): Very soluble in water at pH < 5.5 (mesylate salt)\"|comment:\"predicted h2o solubility (MI:2158): 1.46e-02 mg/mL [ALOGPS]\"	-	-	-	-";

        InputStream is = IntactInteractorIndexWriterTest.class.getResourceAsStream( "/mitab_samples/imatinib_small.txt" );
        indexWriter.index( directory, is, true, true );
        
        //annotationB
        assertSearchResultCount( 2, "annotationB:Novartis" );
        //annotationA
        assertSearchResultCount( 1, "annotationA:biotech prep" );

    }


    private void assertSearchResultCount( final int expectedCount, String searchQuery ){
        Assert.assertEquals( expectedCount, Searcher.search(searchQuery, directory).getTotalCount());
    }
}

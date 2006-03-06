/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock;


/**
 * That class .
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class MockXmlContent {

    public static final String NEW_LINE = System.getProperty( "line.separator" );

    public static String file =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEW_LINE +
            "<entrySet level=\"1\" version=\"1\" xmlns=\"net:sf:psidev:mi\"" + NEW_LINE +
            "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"net:sf:psidev:mi http://psidev.sourceforge.net/mi/xml/src/MIF.xsd\">" + NEW_LINE +
            "    <entry>" + NEW_LINE +
            "        <source releaseDate=\"2004-03-01\"/>" + NEW_LINE +
            "        <experimentList>" + NEW_LINE +
            "            <experimentDescription id=\"EBI-12\">" + NEW_LINE +
            "                <names>" + NEW_LINE +
            "                    <shortLabel>gavin-2002</shortLabel>" + NEW_LINE +
            "                    <fullName>Functional organization of the yeast" + NEW_LINE +
            "                        proteome by systematic analysis of protein complexes.</fullName>" + NEW_LINE +
            "                </names>" + NEW_LINE +
            "                <bibref>" + NEW_LINE +
            "                    <xref>" + NEW_LINE +
            "                        <primaryRef db=\"pubmed\" id=\"11805826\"" + NEW_LINE +
            "                            secondary=\"\" version=\"\"/>" + NEW_LINE +
            "                    </xref>" + NEW_LINE +
            "                </bibref>" + NEW_LINE +
            "                <hostOrganism ncbiTaxId=\"4932\">" + NEW_LINE +
            "                    <names>" + NEW_LINE +
            "                        <shortLabel>s cerevisiae</shortLabel>" + NEW_LINE +
            "                        <fullName>Saccharomyces cerevisiae</fullName>" + NEW_LINE +
            "                    </names>" + NEW_LINE +
            "                </hostOrganism>" + NEW_LINE +
            "                <interactionDetection>" + NEW_LINE +
            "                    <names>" + NEW_LINE +
            "                        <shortLabel>tandem affinity puri</shortLabel>" + NEW_LINE +
            "                        <fullName>tandem affinity purification</fullName>" + NEW_LINE +
            "                    </names>" + NEW_LINE +
            "                    <xref>" + NEW_LINE +
            "                        <primaryRef db=\"pubmed\" id=\"10504710\"" + NEW_LINE +
            "                            secondary=\"\" version=\"\"/>" + NEW_LINE +
            "                        <secondaryRef db=\"psi-mi\" id=\"MI:0109\"" + NEW_LINE +
            "                            secondary=\"\" version=\"\"/>" + NEW_LINE +
            "                    </xref>" + NEW_LINE +
            "                </interactionDetection>" + NEW_LINE +
            "                <participantDetection>" + NEW_LINE +
            "                    <names>" + NEW_LINE +
            "                        <shortLabel>peptide massfingerpr</shortLabel>" + NEW_LINE +
            "                        <fullName>peptide massfingerprinting</fullName>" + NEW_LINE +
            "                    </names>" + NEW_LINE +
            "                    <xref>" + NEW_LINE +
            "                        <primaryRef db=\"pubmed\" id=\"11752590\"" + NEW_LINE +
            "                            secondary=\"\" version=\"\"/>" + NEW_LINE +
            "                        <secondaryRef db=\"psi-mi\" id=\"MI:0082\"" + NEW_LINE +
            "                            secondary=\"\" version=\"\"/>" + NEW_LINE +
            "                        <secondaryRef db=\"pubmed\" id=\"10967324\"" + NEW_LINE +
            "                            secondary=\"\" version=\"\"/>" + NEW_LINE +
            "                    </xref>" + NEW_LINE +
            "                </participantDetection>" + NEW_LINE +
            "            </experimentDescription>" + NEW_LINE +
            "        </experimentList>" + NEW_LINE +
            "" + NEW_LINE +
            "" + NEW_LINE +
            "        <interactorList>" + NEW_LINE +
            "            <proteinInteractor id=\"EBI-111\">" + NEW_LINE +
            "                <names>" + NEW_LINE +
            "                    <shortLabel>q08004</shortLabel>" + NEW_LINE +
            "                    <fullName>Chromosome XII reading frame ORF YLR074C</fullName>" + NEW_LINE +
            "                </names>" + NEW_LINE +
            "                <xref>" + NEW_LINE +
            "                    <primaryRef db=\"interpro\" id=\"IPR003604\"" + NEW_LINE +
            "                        secondary=\"Znf_U1\" version=\"\"/>" + NEW_LINE +
            "                    <secondaryRef db=\"sgd\" id=\"S0004064\"" + NEW_LINE +
            "                        secondary=\"BUD20\" version=\"\"/>" + NEW_LINE +
            "                    <secondaryRef db=\"go\" id=\"GO:0005634\"" + NEW_LINE +
            "                        secondary=\"C:nucleus\" version=\"\"/>" + NEW_LINE +
            "                    <secondaryRef db=\"go\" id=\"GO:0000282\"" + NEW_LINE +
            "                        secondary=\"P:bud site selection\" version=\"\"/>" + NEW_LINE +
            "                    <secondaryRef db=\"uniprotkb\" id=\"Q08004\"" + NEW_LINE +
            "                        secondary=\"q08004\" version=\"\"/>" + NEW_LINE +
            "                    <secondaryRef db=\"interpro\" id=\"IPR007087\"" + NEW_LINE +
            "                        secondary=\"Znf_C2H2\" version=\"\"/>" + NEW_LINE +
            "                </xref>" + NEW_LINE +
            "                <organism ncbiTaxId=\"4932\">" + NEW_LINE +
            "                    <names>" + NEW_LINE +
            "                        <shortLabel>s cerevisiae</shortLabel>" + NEW_LINE +
            "                        <fullName>Saccharomyces cerevisiae</fullName>" + NEW_LINE +
            "                    </names>" + NEW_LINE +
            "                </organism>" + NEW_LINE +
            "                <sequence>MGRYSVKRYKTKRRTRDLDLIYNDLSTKESVQKLLNQPLDETKPGLGQHYCIHCAKYMETAIALKTHLKGKVHKRRVKELRGVPYTQEVSDAAAGYNLNKFLNRVQEITQSVGPEKESNEALLKEHLDSTLANVKTTEPTLPWAAADAEANTAAVTEAESTASAST</sequence>" + NEW_LINE +
            "            </proteinInteractor>" + NEW_LINE +
            "" + NEW_LINE +
            "" + NEW_LINE +
            "            <proteinInteractor id=\"EBI-222\">" + NEW_LINE +
            "                <names>" + NEW_LINE +
            "                    <shortLabel>if6_yeast</shortLabel>" + NEW_LINE +
            "                    <fullName>Eukaryotic translation initiation factor 6</fullName>" + NEW_LINE +
            "                </names>" + NEW_LINE +
            "                <xref>" + NEW_LINE +
            "                    <primaryRef db=\"interpro\" id=\"IPR002769\"" + NEW_LINE +
            "                        secondary=\"eIF6\" version=\"\"/>" + NEW_LINE +
            "                    <secondaryRef db=\"sgd\" id=\"S0006220\"" + NEW_LINE +
            "                        secondary=\"TIF6\" version=\"\"/>" + NEW_LINE +
            "                    <secondaryRef db=\"go\" id=\"GO:0005737\"" + NEW_LINE +
            "                        secondary=\"C:cytoplasm\" version=\"\"/>" + NEW_LINE +
            "                    <secondaryRef db=\"go\" id=\"GO:0005634\"" + NEW_LINE +
            "                        secondary=\"C:nucleus\" version=\"\"/>" + NEW_LINE +
            "                    <secondaryRef db=\"go\" id=\"GO:0030489\"" + NEW_LINE +
            "                        secondary=\"P:processing of 27S pre-rRNA\" version=\"\"/>" + NEW_LINE +
            "                    <secondaryRef db=\"go\" id=\"GO:0042273\"" + NEW_LINE +
            "                        secondary=\"P:ribosomal large subunit biog\" version=\"\"/>" + NEW_LINE +
            "                    <secondaryRef db=\"uniprotkb\" id=\"Q12522\"" + NEW_LINE +
            "                        secondary=\"if6_yeast\" version=\"\"/>" + NEW_LINE +
            "                </xref>" + NEW_LINE +
            "                <organism ncbiTaxId=\"4932\">" + NEW_LINE +
            "                    <names>" + NEW_LINE +
            "                        <shortLabel>s cerevisiae</shortLabel>" + NEW_LINE +
            "                        <fullName>Saccharomyces cerevisiae</fullName>" + NEW_LINE +
            "                    </names>" + NEW_LINE +
            "                </organism>" + NEW_LINE +
            "                <sequence>MATRTQFENSNEIGVFSKLTNTYCLVAVGGSENFYSAFEAELGDAIPIVHTTIAGTRIIGRMTAGNRRGLLVPTQTTDQELQHLRNSLPDSVKIQRVEERLSALGNVICCNDYVALVHPDIDRETEELISDVLGVEVFRQTISGNILVGSYCSLSNQGGLVHPQTSVQDQEELSSLLQVPLVAGTVNRGSSVVGAGMVVNDYLAVTGLDTTAPELSVIESIFRLQDAQPESISGNLRDTLIETYS</sequence>" + NEW_LINE +
            "            </proteinInteractor>" + NEW_LINE +
            "" + NEW_LINE +
            "" + NEW_LINE +
            "            <proteinInteractor id=\"EBI-333\">" + NEW_LINE +
            "                <names>" + NEW_LINE +
            "                    <shortLabel>yev6_yeast</shortLabel>" + NEW_LINE +
            "                    <fullName>Hypothetical 29.7 kDa protein in RSP5-LCP5" + NEW_LINE +
            "                        intergenic region</fullName>" + NEW_LINE +
            "                </names>" + NEW_LINE +
            "                <xref>" + NEW_LINE +
            "                    <primaryRef db=\"interpro\" id=\"IPR001047\"" + NEW_LINE +
            "                        secondary=\"Ribosomal_S8E\" version=\"\"/>" + NEW_LINE +
            "                    <secondaryRef db=\"sgd\" id=\"S0000928\"" + NEW_LINE +
            "                        secondary=\"KRE32\" version=\"\"/>" + NEW_LINE +
            "                    <secondaryRef db=\"go\" id=\"GO:0005634\"" + NEW_LINE +
            "                        secondary=\"C:nucleus\" version=\"\"/>" + NEW_LINE +
            "                    <secondaryRef db=\"go\" id=\"GO:0042273\"" + NEW_LINE +
            "                        secondary=\"P:ribosomal large subunit biog\" version=\"\"/>" + NEW_LINE +
            "                    <secondaryRef db=\"uniprotkb\" id=\"P40078\"" + NEW_LINE +
            "                        secondary=\"yev6_yeast\" version=\"\"/>" + NEW_LINE +
            "                </xref>" + NEW_LINE +
            "                <organism ncbiTaxId=\"4932\">" + NEW_LINE +
            "                    <names>" + NEW_LINE +
            "                        <shortLabel>s cerevisiae</shortLabel>" + NEW_LINE +
            "                        <fullName>Saccharomyces cerevisiae</fullName>" + NEW_LINE +
            "                    </names>" + NEW_LINE +
            "                </organism>" + NEW_LINE +
            "                <sequence>MPQNDYIERHIKQHGKRLDHEERKRKREARESHKISERAQKLTGWKGKQFAKKRYAEKVSMRKKIKAHEQSKVKGSSKPLDTDGDALPTYLLDREQNNTAKAISSSIKQKRLEKADKFSVPLPKVRGISEEEMFKVIKTGKSRSKSWKRMITKHTFVGEGFTRRPVKMERIIRPSALRQKKANVTHPELGVTVFLPILAVKKNPQSPMYTQLGVLTKGTIIEVNVSELGMVTAGGKVVWGKYAQVTNEPDRDGCVNAVLLV</sequence>" + NEW_LINE +
            "            </proteinInteractor>" + NEW_LINE +
            "        </interactorList>" + NEW_LINE +
            "" + NEW_LINE +
            "" + NEW_LINE +
            "        <interactionList>" + NEW_LINE +
            "            <interaction>" + NEW_LINE +
            "                <experimentList>" + NEW_LINE +
            "                    <experimentRef ref=\"EBI-12\"/>" + NEW_LINE +
            "                </experimentList>" + NEW_LINE +
            "                <participantList>" + NEW_LINE +
            "                    <proteinParticipant>" + NEW_LINE +
            "                        <proteinInteractorRef ref=\"EBI-111\"/>" + NEW_LINE +
            "                        <role>bait</role>" + NEW_LINE +
            "                    </proteinParticipant>" + NEW_LINE +
            "                    <proteinParticipant>" + NEW_LINE +
            "                        <proteinInteractorRef ref=\"EBI-222\"/>" + NEW_LINE +
            "                        <role>prey</role>" + NEW_LINE +
            "                    </proteinParticipant>" + NEW_LINE +
            "                </participantList>" + NEW_LINE +
            "                <interactionType>" + NEW_LINE +
            "                    <names>" + NEW_LINE +
            "                        <shortLabel>tandem affinity puri</shortLabel>" + NEW_LINE +
            "                        <fullName>tandem affinity purification</fullName>" + NEW_LINE +
            "                    </names>" + NEW_LINE +
            "                    <xref>" + NEW_LINE +
            "                        <primaryRef db=\"pubmed\" id=\"10504710\"" + NEW_LINE +
            "                            secondary=\"\" version=\"\"/>" + NEW_LINE +
            "                        <secondaryRef db=\"psi-mi\" id=\"MI:0109\"" + NEW_LINE +
            "                            secondary=\"\" version=\"\"/>" + NEW_LINE +
            "                    </xref>" + NEW_LINE +
            "                </interactionType>" + NEW_LINE +
            "            </interaction>" + NEW_LINE +
            "" + NEW_LINE +
            "            <interaction>" + NEW_LINE +
            "                <experimentList>" + NEW_LINE +
            "                    <experimentRef ref=\"EBI-12\"/>" + NEW_LINE +
            "                </experimentList>" + NEW_LINE +
            "                <participantList>" + NEW_LINE +
            "                    <proteinParticipant>" + NEW_LINE +
            "                        <proteinInteractorRef ref=\"EBI-222\"/>" + NEW_LINE +
            "                        <role>bait</role>" + NEW_LINE +
            "                    </proteinParticipant>" + NEW_LINE +
            "                    <proteinParticipant>" + NEW_LINE +
            "                        <proteinInteractorRef ref=\"EBI-333\"/>" + NEW_LINE +
            "                        <role>prey</role>" + NEW_LINE +
            "                    </proteinParticipant>" + NEW_LINE +
            "                </participantList>" + NEW_LINE +
            "                <interactionType>" + NEW_LINE +
            "                    <names>" + NEW_LINE +
            "                        <shortLabel>tandem affinity puri</shortLabel>" + NEW_LINE +
            "                        <fullName>tandem affinity purification</fullName>" + NEW_LINE +
            "                    </names>" + NEW_LINE +
            "                    <xref>" + NEW_LINE +
            "                        <primaryRef db=\"pubmed\" id=\"10504710\"" + NEW_LINE +
            "                            secondary=\"\" version=\"\"/>" + NEW_LINE +
            "                        <secondaryRef db=\"psi-mi\" id=\"MI:0109\"" + NEW_LINE +
            "                            secondary=\"\" version=\"\"/>" + NEW_LINE +
            "                    </xref>" + NEW_LINE +
            "                </interactionType>" + NEW_LINE +
            "            </interaction>" + NEW_LINE +
            "" + NEW_LINE +
            "        </interactionList>" + NEW_LINE +
            "    </entry>" + NEW_LINE +
            "</entrySet>";


    public static String EXPERIMENT_DESCRIPTION_1 =
            "<experimentDescription id=\"EBI-12\">" + NEW_LINE +
            "     <names>" + NEW_LINE +
            "         <shortLabel>gavin-2002</shortLabel>" + NEW_LINE +
            "         <fullName>Functional organization of the yeast proteome by systematic analysis of protein complexes.</fullName>" + NEW_LINE +
            "     </names>" + NEW_LINE +
            "    <xref>" + NEW_LINE +
            "          <primaryRef db=\"go\" id=\"GO:0000000\"" + NEW_LINE +
            "              secondary=\"blabla\" version=\"versionX\"/>" + NEW_LINE +
            "          <secondaryRef db=\"psi-mi\" id=\"MI:0082\"" + NEW_LINE +
            "              secondary=\"\" version=\"\"/>" + NEW_LINE +
            "          <secondaryRef db=\"foo-mi\" id=\"FOO:0082\"" + NEW_LINE +
            "              secondary=\"\" version=\"\"/>" + NEW_LINE +
            "     </xref>" + NEW_LINE +
            "     <bibref>" + NEW_LINE +
            "         <xref>" + NEW_LINE +
            "             <primaryRef db=\"pubmed\" id=\"11805826\" secondary=\"\" version=\"\"/>" + NEW_LINE +
            "             <secondaryRef db=\"pubmed\" id=\"11809999\" secondary=\"\" version=\"\"/>" + NEW_LINE +
            "         </xref>" + NEW_LINE +
            "     </bibref>" + NEW_LINE +
            "     <hostOrganism ncbiTaxId=\"4932\">" + NEW_LINE +
            "         <names>" + NEW_LINE +
            "             <shortLabel>s cerevisiae</shortLabel>" + NEW_LINE +
            "             <fullName>Saccharomyces cerevisiae</fullName>" + NEW_LINE +
            "         </names>" + NEW_LINE +
            "     </hostOrganism>" + NEW_LINE +
            "     <interactionDetection>" + NEW_LINE +
            "         <names>" + NEW_LINE +
            "             <shortLabel>tandem affinity puri</shortLabel>" + NEW_LINE +
            "             <fullName>tandem affinity purification</fullName>" + NEW_LINE +
            "         </names>" + NEW_LINE +
            "         <xref>" + NEW_LINE +
            "             <secondaryRef db=\"pubmed\" id=\"10504710\"" + NEW_LINE +
            "                 secondary=\"\" version=\"\"/>" + NEW_LINE +
            "             <primaryRef db=\"psi-mi\" id=\"MI:0109\"" + NEW_LINE +
            "                 secondary=\"\" version=\"\"/>" + NEW_LINE +
            "         </xref>" + NEW_LINE +
            "     </interactionDetection>" + NEW_LINE +
            "     <participantDetection>" + NEW_LINE +
            "         <names>" + NEW_LINE +
            "             <shortLabel>peptide massfingerpr</shortLabel>" + NEW_LINE +
            "             <fullName>peptide massfingerprinting</fullName>" + NEW_LINE +
            "         </names>" + NEW_LINE +
            "         <xref>" + NEW_LINE +
            "             <secondaryRef db=\"pubmed\" id=\"11752590\"" + NEW_LINE +
            "                 secondary=\"\" version=\"\"/>" + NEW_LINE +
            "             <primaryRef db=\"psi-mi\" id=\"MI:0082\"" + NEW_LINE +
            "                 secondary=\"\" version=\"\"/>" + NEW_LINE +
            "             <secondaryRef db=\"pubmed\" id=\"10967324\"" + NEW_LINE +
            "                 secondary=\"\" version=\"\"/>" + NEW_LINE +
            "         </xref>" + NEW_LINE +
            "     </participantDetection>" + NEW_LINE +
            "     <attributeList>" + NEW_LINE +
            "         <attribute name=\"comment\">a first comment.</attribute>" + NEW_LINE +
            "         <attribute name=\"test\">A first test</attribute>" + NEW_LINE +
            "         <attribute name=\"remark\">Oh! a remark</attribute>" + NEW_LINE +
            "     </attributeList>" + NEW_LINE +
            "</experimentDescription>";


    public static final String FEATURE_VALID =
            "<feature>" + NEW_LINE +
            "     <xref>" + NEW_LINE +
            "         <primaryRef db=\"interpro\" id=\"IPR001977\" secondary=\"Depp_CoAkinase\"/>" + NEW_LINE +
            "     </xref>" + NEW_LINE +
            "     <featureDescription>" + NEW_LINE +
            "         <names>" + NEW_LINE +
            "             <shortLabel>my feature</shortLabel>" + NEW_LINE +
            "             <fullName>my feature bla bla bla</fullName>" + NEW_LINE +
            "         </names>" + NEW_LINE +
            "         <xref>" + NEW_LINE +
            "             <primaryRef db=\"psi-mi\" id=\"MI:1234\" secondary=\"formylation reaction\"/>" + NEW_LINE +
            "         </xref>" + NEW_LINE +
            "     </featureDescription>" + NEW_LINE +
            "     <location>" + NEW_LINE +
            "         <beginInterval begin=\"2\" end=\"5\"/>" + NEW_LINE +
            "         <end position=\"9\"/>" + NEW_LINE +
            "     </location>" + NEW_LINE +
            "     <featureDetection>" + NEW_LINE +
            "         <names>" + NEW_LINE +
            "             <shortLabel>western blot</shortLabel>" + NEW_LINE +
            "         </names>" + NEW_LINE +
            "         <xref>" + NEW_LINE +
            "             <primaryRef db=\"psi-mi\" id=\"MI:0113\"/>" + NEW_LINE +
            "         </xref>" + NEW_LINE +
            "     </featureDetection>" + NEW_LINE +
            "</feature>";

    public static final String FEATURE_MINIMAL =
            "<feature>" + NEW_LINE +
            "     <featureDescription>" + NEW_LINE +
            "         <names>" + NEW_LINE +
            "             <shortLabel>my feature</shortLabel>" + NEW_LINE +
            "         </names>" + NEW_LINE +
            "         <xref>" + NEW_LINE +
            "             <primaryRef db=\"psi-mi\" id=\"MI:1234\" secondary=\"formylation reaction\"/>" + NEW_LINE +
            "         </xref>" + NEW_LINE +
            "     </featureDescription>" + NEW_LINE +
            "     <location>" + NEW_LINE +
            "         <beginInterval begin=\"2\" end=\"5\"/>" + NEW_LINE +
            "         <end position=\"9\"/>" + NEW_LINE +
            "     </location>" + NEW_LINE +
            "     <featureDetection>" + NEW_LINE +
            "         <xref>" + NEW_LINE +
            "             <primaryRef db=\"psi-mi\" id=\"MI:0113\"/>" + NEW_LINE +
            "         </xref>" + NEW_LINE +
            "     </featureDetection>" + NEW_LINE +
            "</feature>";

    public static final String FEATURE_NOT_VALID_1 =
            "<feature>" + NEW_LINE +
            "     <location>" + NEW_LINE +
            "         <beginInterval begin=\"2\" end=\"5\"/>" + NEW_LINE +
            "         <end position=\"9\"/>" + NEW_LINE +
            "     </location>" + NEW_LINE +
            "     <featureDetection>" + NEW_LINE +
            "         <xref>" + NEW_LINE +
            "             <primaryRef db=\"psi-mi\" id=\"MI:0113\"/>" + NEW_LINE +
            "         </xref>" + NEW_LINE +
            "     </featureDetection>" + NEW_LINE +
            "</feature>";

    public static final String FEATURE_NOT_VALID_2 =
            "<feature>" + NEW_LINE +
            "     <featureDescription>" + NEW_LINE +
            "         <names>" + NEW_LINE +
            "             <shortLabel>my feature</shortLabel>" + NEW_LINE +
            "         </names>" + NEW_LINE +
            "         <xref>" + NEW_LINE +
            "             <primaryRef db=\"psi-mi\" id=\"MI:1234\" secondary=\"formylation reaction\"/>" + NEW_LINE +
            "         </xref>" + NEW_LINE +
            "     </featureDescription>" + NEW_LINE +
            "     <featureDetection>" + NEW_LINE +
            "         <xref>" + NEW_LINE +
            "             <primaryRef db=\"psi-mi\" id=\"MI:0113\"/>" + NEW_LINE +
            "         </xref>" + NEW_LINE +
            "     </featureDetection>" + NEW_LINE +
            "</feature>";

    public static final String FEATURE_NOT_VALID_3 =
            "<feature>" + NEW_LINE +
            "     <featureDescription>" + NEW_LINE +
            "         <names>" + NEW_LINE +
            "             <shortLabel>my feature</shortLabel>" + NEW_LINE +
            "         </names>" + NEW_LINE +
            "         <xref>" + NEW_LINE +
            "             <primaryRef db=\"psi-mi\" id=\"MI:1234\" secondary=\"formylation reaction\"/>" + NEW_LINE +
            "         </xref>" + NEW_LINE +
            "     </featureDescription>" + NEW_LINE +
            "     <location>" + NEW_LINE +
            "         <beginInterval begin=\"2\" end=\"5\"/>" + NEW_LINE +
            "         <end position=\"9\"/>" + NEW_LINE +
            "     </location>" + NEW_LINE +
            "</feature>";


    public static final String LOCATION_1 =
            "<location>" + NEW_LINE +
            "      <begin position=\"2\"/>" + NEW_LINE +
            "      <endInterval begin=\"10\" end=\"13\"/>" + NEW_LINE +
            "</location>";

    public static final String LOCATION_2 =
            "<location>" + NEW_LINE +
            "      <beginInterval begin=\"10\" end=\"13\" />" + NEW_LINE +
            "      <end position=\"99\" />" + NEW_LINE +
            "</location>";

    public static final String LOCATION_3 =
            "<location>" + NEW_LINE +
            "      <position position=\"7\"/>" + NEW_LINE +
            "</location>";

    public static final String LOCATION_4 =
            "<location>" + NEW_LINE +
            "     <site position=\"122\"/>" + NEW_LINE +
            "</location>";

    public static final String LOCATION_5 =
            "<location>" + NEW_LINE +
            "     <site position=\"0\"/>" + NEW_LINE +
            "</location>";

    public static final String LOCATION_WRONG_1 =
            "<location>" + NEW_LINE +
            "      <begin position=\"abc\"/>" + NEW_LINE +
            "      <end position=\"10\" />" + NEW_LINE +
            "</location>";

    public static final String LOCATION_WRONG_2 =
            "<location>" + NEW_LINE +
            "     <site position=\"abc\"/>" + NEW_LINE +
            "</location>";

    public static final String LOCATION_WRONG_3 =
            "<location>" + NEW_LINE +
            "     <position position=\"abc\"/>" + NEW_LINE +
            "</location>";

    public static final String LOCATION_WRONG_4 =
            "<location>" + NEW_LINE +
            "      <begin position=\"11\" />" + NEW_LINE +
            "      <end   position=\"10\" />" + NEW_LINE +
            "</location>";

    public static final String LOCATION_WRONG_5 =
            "<location>" + NEW_LINE +
            "   <position position=\"-1\"/>" + NEW_LINE +
            "</location>";

    public static final String LOCATION_WRONG_6 =
            "<location>" + NEW_LINE +
            "      <beginInterval begin=\"13\" end=\"1\" />" + NEW_LINE +
            "      <end   position=\"10\" />" + NEW_LINE +
            "</location>";

    public static final String LOCATION_WRONG_7 =
            "<location>" + NEW_LINE +
            "      <beginInterval begin=\"4\" end=\"7\" />" + NEW_LINE +
            "      <endInterval   begin=\"9\" end=\"7\" />" + NEW_LINE +
            "</location>";


    public static final String XREF_1 =
            "<xref>" + NEW_LINE +
            "    <primaryRef db=\"pubmed\" id=\"11805826\"" + NEW_LINE +
            "        secondary=\"mySecondaryId\" version=\"version1\" />" + NEW_LINE +
            "    <secondaryRef db=\"sgd\" id=\"S0004064\"" + NEW_LINE +
            "        secondary=\"BUD20\" version=\"version2\" />" + NEW_LINE +
            "    <secondaryRef db=\"go\" id=\"GO:0005634\"" + NEW_LINE +
            "        secondary=\"C:nucleus\" version=\"version2\" />" + NEW_LINE +
            "</xref>";

    public static final String HOST_ORGANISM_1 =
            "<hostOrganism ncbiTaxId=\"4932\">" + NEW_LINE +
            "    <names>" + NEW_LINE +
            "        <shortLabel>s cerevisiae</shortLabel>" + NEW_LINE +
            "        <fullName>Saccharomyces cerevisiae</fullName>" + NEW_LINE +
            "    </names>" + NEW_LINE +
            "    <tissue>" + NEW_LINE +
            "        <names>" + NEW_LINE +
            "            <shortLabel>1234</shortLabel>" + NEW_LINE +
            "            <fullName>ldfiglsfd</fullName>" + NEW_LINE +
            "        </names>" + NEW_LINE +
            "        <xref>" + NEW_LINE +
            "            <primaryRef db=\"psi-mi\" id=\"MI:123\" secondary=\"\" version=\"\"/>" + NEW_LINE +
            "        </xref>" + NEW_LINE +
            "   </tissue>" + NEW_LINE +
            "   <cellType>" + NEW_LINE +
            "        <names>" + NEW_LINE +
            "            <shortLabel>9876</shortLabel>" + NEW_LINE +
            "            <fullName>skufgh</fullName>" + NEW_LINE +
            "        </names>" + NEW_LINE +
            "        <xref>" + NEW_LINE +
            "           <primaryRef db=\"psi-mi\" id=\"MI:987\" secondary=\"\" version=\"\"/>" + NEW_LINE +
            "        </xref>" + NEW_LINE +
            "   </cellType>" +
            "</hostOrganism>";

    public static final String HOST_ORGANISM_2 =
            "<hostOrganism ncbiTaxId=\"4932\">" + NEW_LINE +
            "    <names>" + NEW_LINE +
            "        <shortLabel>s cerevisiae</shortLabel>" + NEW_LINE +
            "        <fullName>Saccharomyces cerevisiae</fullName>" + NEW_LINE +
            "    </names>" + NEW_LINE +
            "</hostOrganism>";

    public static final String HOST_ORGANISM_3 =
            "<hostOrganism ncbiTaxId=\"4932\">" + NEW_LINE +
            "    <names>" + NEW_LINE +
            "        <shortLabel>s cerevisiae</shortLabel>" + NEW_LINE +
            "        <fullName>Saccharomyces cerevisiae</fullName>" + NEW_LINE +
            "    </names>" + NEW_LINE +
            "    <tissue>" + NEW_LINE +
            "        <names>" + NEW_LINE +
            "            <shortLabel>1234</shortLabel>" + NEW_LINE +
            "            <fullName>ldfiglsfd</fullName>" + NEW_LINE +
            "        </names>" + NEW_LINE +
            "        <xref>" + NEW_LINE +
            "            <primaryRef db=\"psi-mi\" id=\"MI:123\" secondary=\"\" version=\"\"/>" + NEW_LINE +
            "        </xref>" + NEW_LINE +
            "   </tissue>" + NEW_LINE +
            "</hostOrganism>";

    public static final String HOST_ORGANISM_3b =
            "<hostOrganism ncbiTaxId=\"4932\">" + NEW_LINE +
            "    <names>" + NEW_LINE +
            "        <shortLabel>s cerevisiae</shortLabel>" + NEW_LINE +
            "    </names>" + NEW_LINE +
            "    <tissue>" + NEW_LINE +
            "        <names>" + NEW_LINE +
            "            <shortLabel>1234</shortLabel>" + NEW_LINE +
            "        </names>" + NEW_LINE +
            "   </tissue>" + NEW_LINE +
            "</hostOrganism>";

    public static final String HOST_ORGANISM_4 =
            "<hostOrganism ncbiTaxId=\"4932\">" + NEW_LINE +
            "    <names>" + NEW_LINE +
            "        <shortLabel>s cerevisiae</shortLabel>" + NEW_LINE +
            "        <fullName>Saccharomyces cerevisiae</fullName>" + NEW_LINE +
            "    </names>" + NEW_LINE +
            "   <cellType>" + NEW_LINE +
            "        <names>" + NEW_LINE +
            "            <shortLabel>1234</shortLabel>" + NEW_LINE +
            "            <fullName>ldfiglsfd</fullName>" + NEW_LINE +
            "        </names>" + NEW_LINE +
            "        <xref>" + NEW_LINE +
            "           <primaryRef db=\"psi-mi\" id=\"MI:987\" secondary=\"\" version=\"\"/>" + NEW_LINE +
            "        </xref>" + NEW_LINE +
            "   </cellType>" +
            "</hostOrganism>";


    public static final String ORGANISM_1 =
            "<organism ncbiTaxId=\"4932\">" + NEW_LINE +
            "    <names>" + NEW_LINE +
            "        <shortLabel>s cerevisiae</shortLabel>" + NEW_LINE +
            "        <fullName>Saccharomyces cerevisiae</fullName>" + NEW_LINE +
            "    </names>" + NEW_LINE +
            "    <tissue>" + NEW_LINE +
            "        <names>" + NEW_LINE +
            "            <shortLabel>1234</shortLabel>" + NEW_LINE +
            "            <fullName>ldfiglsfd</fullName>" + NEW_LINE +
            "        </names>" + NEW_LINE +
            "        <xref>" + NEW_LINE +
            "            <primaryRef db=\"psi-mi\" id=\"MI:123\" secondary=\"\" version=\"\"/>" + NEW_LINE +
            "        </xref>" + NEW_LINE +
            "   </tissue>" + NEW_LINE +
            "   <cellType>" + NEW_LINE +
            "        <names>" + NEW_LINE +
            "            <shortLabel>9876</shortLabel>" + NEW_LINE +
            "            <fullName>dstjtj</fullName>" + NEW_LINE +
            "        </names>" + NEW_LINE +
            "        <xref>" + NEW_LINE +
            "           <primaryRef db=\"psi-mi\" id=\"MI:987\" secondary=\"\" version=\"\"/>" + NEW_LINE +
            "        </xref>" + NEW_LINE +
            "   </cellType>" +
            "</organism>";

    public static final String ORGANISM_2 =
            "<organism ncbiTaxId=\"4932\">" + NEW_LINE +
            "    <names>" + NEW_LINE +
            "        <shortLabel>s cerevisiae</shortLabel>" + NEW_LINE +
            "        <fullName>Saccharomyces cerevisiae</fullName>" + NEW_LINE +
            "    </names>" + NEW_LINE +
            "</organism>";

    public static final String ORGANISM_3 =
            "<organism ncbiTaxId=\"4932\">" + NEW_LINE +
            "    <names>" + NEW_LINE +
            "        <shortLabel>s cerevisiae</shortLabel>" + NEW_LINE +
            "        <fullName>Saccharomyces cerevisiae</fullName>" + NEW_LINE +
            "    </names>" + NEW_LINE +
            "    <tissue>" + NEW_LINE +
            "        <names>" + NEW_LINE +
            "            <shortLabel>1234</shortLabel>" + NEW_LINE +
            "            <fullName>ldfiglsfd</fullName>" + NEW_LINE +
            "        </names>" + NEW_LINE +
            "        <xref>" + NEW_LINE +
            "            <primaryRef db=\"psi-mi\" id=\"MI:123\" secondary=\"\" version=\"\"/>" + NEW_LINE +
            "        </xref>" + NEW_LINE +
            "   </tissue>" + NEW_LINE +
            "</organism>";

    public static final String ORGANISM_4 =
            "<organism ncbiTaxId=\"4932\">" + NEW_LINE +
            "    <names>" + NEW_LINE +
            "        <shortLabel>s cerevisiae</shortLabel>" + NEW_LINE +
            "        <fullName>Saccharomyces cerevisiae</fullName>" + NEW_LINE +
            "    </names>" + NEW_LINE +
            "   <cellType>" + NEW_LINE +
            "        <names>" + NEW_LINE +
            "            <shortLabel>1234</shortLabel>" + NEW_LINE +
            "            <fullName>ldfiglsfd</fullName>" + NEW_LINE +
            "        </names>" + NEW_LINE +
            "        <xref>" + NEW_LINE +
            "           <primaryRef db=\"psi-mi\" id=\"MI:987\" secondary=\"\" version=\"\"/>" + NEW_LINE +
            "        </xref>" + NEW_LINE +
            "   </cellType>" +
            "</organism>";


    public static final String PROTEIN_INTERACTOR_1 =
            "<proteinInteractor id=\"EBI-111\">" + NEW_LINE +
            "    <names>" + NEW_LINE +
            "        <shortLabel>q08004</shortLabel>" + NEW_LINE +
            "        <fullName>Chromosome XII reading frame ORF YLR074C</fullName>" + NEW_LINE +
            "    </names>" + NEW_LINE +
            "    <xref>" + NEW_LINE +
            "        <primaryRef db=\"uniprotkb\" id=\"P12345\"" + NEW_LINE +
            "            secondary=\"blablabla\" version=\"2.46\"/>" + NEW_LINE +
            "        <secondaryRef db=\"sgd\" id=\"S0004064\"" + NEW_LINE +
            "            secondary=\"BUD20\" version=\"\"/>" + NEW_LINE +
            "        <secondaryRef db=\"go\" id=\"GO:0005634\"" + NEW_LINE +
            "            secondary=\"C:nucleus\" version=\"\"/>" + NEW_LINE +
            "    </xref>" + NEW_LINE +
            "    <organism ncbiTaxId=\"4932\">" + NEW_LINE +
            "        <names>" + NEW_LINE +
            "            <shortLabel>s cerevisiae</shortLabel>" + NEW_LINE +
            "            <fullName>Saccharomyces cerevisiae</fullName>" + NEW_LINE +
            "        </names>" + NEW_LINE +
            "    </organism>" + NEW_LINE +
            "    <sequence>MGRYSVKRYKTKRRTRDLDLIYNDLSTKESVQKLLNQPLDETKPGLGQHYCIHCAKYMETAIALKTHLKGKVHKRRVKELRGVPYTQEVSDAAAGYNLNKFLNRVQEITQSVGPEKESNEALLKEHLDSTLANVKTTEPTLPWAAADAEANTAAVTEAESTASAST</sequence>" + NEW_LINE +
            "</proteinInteractor>";


    public static final String PROTEIN_INTERACTOR_2 =
            "<proteinInteractor id=\"EBI-222\">" + NEW_LINE +
            "    <names>" + NEW_LINE +
            "        <shortLabel>if6_yeast</shortLabel>" + NEW_LINE +
            "        <fullName>Eukaryotic translation initiation factor 6</fullName>" + NEW_LINE +
            "    </names>" + NEW_LINE +
            "    <xref>" + NEW_LINE +
            "        <primaryRef db=\"uniprotkb\" id=\"Q12522\"" + NEW_LINE +
            "            secondary=\"if6_yeast\" version=\"\"/>" + NEW_LINE +
            "        <secondaryRef db=\"interpro\" id=\"IPR002769\"" + NEW_LINE +
            "            secondary=\"eIF6\" version=\"\"/>" + NEW_LINE +
            "        <secondaryRef db=\"sgd\" id=\"S0006220\"" + NEW_LINE +
            "            secondary=\"TIF6\" version=\"\"/>" + NEW_LINE +
            "        <secondaryRef db=\"go\" id=\"GO:0005737\"" + NEW_LINE +
            "            secondary=\"C:cytoplasm\" version=\"\"/>" + NEW_LINE +
            "        <secondaryRef db=\"go\" id=\"GO:0005634\"" + NEW_LINE +
            "            secondary=\"C:nucleus\" version=\"\"/>" + NEW_LINE +
            "        <secondaryRef db=\"go\" id=\"GO:0030489\"" + NEW_LINE +
            "            secondary=\"P:processing of 27S pre-rRNA\" version=\"\"/>" + NEW_LINE +
            "        <secondaryRef db=\"go\" id=\"GO:0042273\"" + NEW_LINE +
            "            secondary=\"P:ribosomal large subunit biog\" version=\"\"/>" + NEW_LINE +
            "    </xref>" + NEW_LINE +
            "    <organism ncbiTaxId=\"4932\">" + NEW_LINE +
            "        <names>" + NEW_LINE +
            "            <shortLabel>s cerevisiae</shortLabel>" + NEW_LINE +
            "            <fullName>Saccharomyces cerevisiae</fullName>" + NEW_LINE +
            "        </names>" + NEW_LINE +
            "    </organism>" + NEW_LINE +
            "    <sequence>MATRTQFENSNEIGVFSKLTNTYCLVAVGGSENFYSAFEAELGDAIPIVHTTIAGTRIIGRMTAGNRRGLLVPTQTTDQELQHLRNSLPDSVKIQRVEERLSALGNVICCNDYVALVHPDIDRETEELISDVLGVEVFRQTISGNILVGSYCSLSNQGGLVHPQTSVQDQEELSSLLQVPLVAGTVNRGSSVVGAGMVVNDYLAVTGLDTTAPELSVIESIFRLQDAQPESISGNLRDTLIETYS</sequence>" + NEW_LINE +
            "</proteinInteractor>";

    public static final String PROTEIN_INTERACTOR_WITHOUT_BIOSOURCE =
            "<proteinInteractor id=\"EBI-333\">" + NEW_LINE +
            "    <names>" + NEW_LINE +
            "        <shortLabel>if6_yeast</shortLabel>" + NEW_LINE +
            "        <fullName>Eukaryotic translation initiation factor 6</fullName>" + NEW_LINE +
            "    </names>" + NEW_LINE +
            "    <xref>" + NEW_LINE +
            "        <primaryRef db=\"uniprotkb\" id=\"P12345\"" + NEW_LINE +
            "            secondary=\"blablabla\" version=\"2.46\"/>" + NEW_LINE +
            "        <secondaryRef db=\"go\" id=\"GO:0042273\"" + NEW_LINE +
            "            secondary=\"P:ribosomal large subunit biog\" version=\"\"/>" + NEW_LINE +
            "    </xref>" + NEW_LINE +
            "    <sequence>MATRTQFENSNEIGVFSKLTNTYCLVAVGGSENFYSAFEAELGDAIPIVHTTIAGTRIIGRMTAGNRRGLLVPTQTTDQELQHLRNSLPDSVKIQRVEERLSALGNVICCNDYVALVHPDIDRETEELISDVLGVEVFRQTISGNILVGSYCSLSNQGGLVHPQTSVQDQEELSSLLQVPLVAGTVNRGSSVVGAGMVVNDYLAVTGLDTTAPELSVIESIFRLQDAQPESISGNLRDTLIETYS</sequence>" + NEW_LINE +
            "</proteinInteractor>";


    /**
     * Interaction with no reference, everything (experiment and interactor) are described in the scope.
     */
    public static final String INTERACTION_1 =
            "<interaction>" + NEW_LINE +
            "    <names>" + NEW_LINE +
            "        <shortLabel>intShortlabel</shortLabel>" + NEW_LINE +
            "        <fullName>intFullname</fullName>" + NEW_LINE +
            "    </names>" + NEW_LINE +
            "    <xref>" + NEW_LINE +
            "        <primaryRef db=\"pubmed\" id=\"11805826\"" + NEW_LINE +
            "            secondary=\"mySecondaryId\" version=\"version1\" />" + NEW_LINE +
            "        <secondaryRef db=\"sgd\" id=\"S0006220\"" + NEW_LINE +
            "            secondary=\"TIF6\" version=\"\"/>" + NEW_LINE +
            "    </xref>" + NEW_LINE +
            "    <experimentList>" + NEW_LINE +

            "        " + EXPERIMENT_DESCRIPTION_1 + NEW_LINE + // full experiment

            "    </experimentList>" + NEW_LINE +
            "    <participantList>" + NEW_LINE +
            "        <proteinParticipant>" + NEW_LINE +

            "            " + PROTEIN_INTERACTOR_1 + NEW_LINE + // full interactor 1

            "            <role>bait</role>" + NEW_LINE +

            "            <featureList>" + NEW_LINE +

            FEATURE_VALID + NEW_LINE +

            FEATURE_MINIMAL + NEW_LINE +

            "            </featureList>" + NEW_LINE +

            "        </proteinParticipant>" + NEW_LINE +
            "        <proteinParticipant>" + NEW_LINE +

            "            " + PROTEIN_INTERACTOR_2 + NEW_LINE + // full interactor 2

            "            <role>prey</role>" + NEW_LINE +
            "        </proteinParticipant>" + NEW_LINE +
            "    </participantList>" + NEW_LINE +
            "    <interactionType>" + NEW_LINE +
            "        <names>" + NEW_LINE +
            "            <shortLabel>tandem affinity puri</shortLabel>" + NEW_LINE +
            "            <fullName>tandem affinity purification</fullName>" + NEW_LINE +
            "        </names>" + NEW_LINE +
            "        <xref>" + NEW_LINE +
            "            <secondaryRef db=\"pubmed\" id=\"10504710\"" + NEW_LINE +
            "                          secondary=\"\" version=\"\"/>" + NEW_LINE +
            "            <primaryRef db=\"psi-mi\" id=\"MI:0109\"" + NEW_LINE +
            "                        secondary=\"\" version=\"\"/>" + NEW_LINE +
            "        </xref>" + NEW_LINE +
            "    </interactionType>" + NEW_LINE +
            "    <attributeList>" + NEW_LINE +
            "        <attribute name=\"comment\">a first comment.</attribute>" + NEW_LINE +
            "        <attribute name=\"test\">A first test</attribute>" + NEW_LINE +
            "        <attribute name=\"expressedIn\">EBI-222:rat</attribute>" + NEW_LINE +
            "        <attribute name=\"remark\">Oh! a remark</attribute>" + NEW_LINE +
            "    </attributeList>" + NEW_LINE +
            "    <confidence unit=\"arbitrary\" value=\"high\"/>" + NEW_LINE +
            "</interaction>";

    public static final String INTERACTION_TYPE_1 =
            "<interactionType>" + NEW_LINE +
            "    <names>" + NEW_LINE +
            "        <shortLabel>popipo</shortLabel>" + NEW_LINE +
            "        <fullName>tralala</fullName>" + NEW_LINE +
            "    </names>" + NEW_LINE +
            "    <xref>" + NEW_LINE +
            "        <primaryRef db=\"psi-mi\" id=\"MI:xxx\"" + NEW_LINE +
            "            secondary=\"\" version=\"\"/>" + NEW_LINE +
            "        <secondaryRef db=\"pubmed\" id=\"10504710\"" + NEW_LINE +
            "            secondary=\"\" version=\"\"/>" + NEW_LINE +
            "    </xref>" + NEW_LINE +
            "</interactionType>";

    public static final String OCCURENCE_OF_TAG_AT_DIFFERENT_LEVEL =
            "<man>" + NEW_LINE +
            "   <color>blue</color>" + NEW_LINE +
            "   <hair>" + NEW_LINE +
            "        <color>red</color>" + NEW_LINE +
            "   </hair>" + NEW_LINE +
            "</man>";

    public static final String OCCURENCE_OF_TAG_AT_DIFFERENT_LEVEL_2 =
            "<man>" + NEW_LINE +
            "   <hair>" + NEW_LINE +
            "        <color>red</color>" + NEW_LINE +
            "   </hair>" + NEW_LINE +
            "   <color>blue</color>" + NEW_LINE +
            "</man>";

    public static final String ANNOTATION_1 = "<attribute name=\"comment\">my comment</attribute>";
    public static final String ANNOTATION_2 = "<attribute name=\"remark\">my remark</attribute>";
    public static final String ANNOTATION_3 = "<attribute name=\"remark\"></attribute>";
    public static final String ANNOTATION_4 = "<attribute name=\"\">blablabla</attribute>";

    public static final String CELL_TYPE =
            "<cellType>" + NEW_LINE +
            "     <names>" + NEW_LINE +
            "         <shortLabel>myCellType</shortLabel>" + NEW_LINE +
            "         <fullName>tralala</fullName>" + NEW_LINE +
            "     </names>" + NEW_LINE +
            "     <xref>" + NEW_LINE +
            "        <primaryRef db=\"psi-mi\" id=\"MI:987\" secondary=\"\" version=\"\"/>" + NEW_LINE +
            "     </xref>" + NEW_LINE +
            "</cellType>";

    public static final String TISSUE =
            "<tissue>" + NEW_LINE +
            "    <names>" + NEW_LINE +
            "        <shortLabel>myTissue</shortLabel>" + NEW_LINE +
            "        <fullName>tralala</fullName>" + NEW_LINE +
            "    </names>" + NEW_LINE +
            "    <xref>" + NEW_LINE +
            "        <primaryRef db=\"psi-mi\" id=\"MI:123\" secondary=\"\" version=\"\"/>" + NEW_LINE +
            "    </xref>" + NEW_LINE +
            "</tissue>";

}

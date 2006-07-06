/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.test.mocks;

/**
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public final class MockEntryText {

    private static final String NEW_LINE = System.getProperty( "line.separator" );

    public static String Q03767 =
            "ID   NUM1_YEAST     STANDARD;      PRT;  2748 AA." + NEW_LINE +
            "AC   Q00402; Q03767;" + NEW_LINE +
            "DT   01-DEC-1992 (Rel. 24, Created)" + NEW_LINE +
            "DT   10-OCT-2003 (Rel. 42, Last sequence update)" + NEW_LINE +
            "DT   10-OCT-2003 (Rel. 42, Last annotation update)" + NEW_LINE +
            "DE   Nuclear migration protein NUM1." + NEW_LINE +
            "GN   NUM1 OR YDR150W OR YD8358.06." + NEW_LINE +
            "OS   Saccharomyces cerevisiae (Baker's yeast)." + NEW_LINE +
            "OC   Eukaryota; Fungi; Ascomycota; Saccharomycotina; Saccharomycetes;" + NEW_LINE +
            "OC   Saccharomycetales; Saccharomycetaceae; Saccharomyces." + NEW_LINE +
            "OX   NCBI_TaxID=4932;" + NEW_LINE +
            "RN   [1]" + NEW_LINE +
            "RP   SEQUENCE FROM N.A." + NEW_LINE +
            "RC   STRAIN=ATCC 28383 / FL100;" + NEW_LINE +
            "RX   MEDLINE=92079907; PubMed=1745235;" + NEW_LINE +
            "RA   Kormanec J., Schaaff-Gerstenschlaeger I., Zimmermann F.K.," + NEW_LINE +
            "RA   Perecko D., Kuentzel H.;" + NEW_LINE +
            "RT   \"Nuclear migration in Saccharomyces cerevisiae is controlled by the" + NEW_LINE +
            "RT   highly repetitive 313 kDa NUM1 protein.\";" + NEW_LINE +
            "RL   Mol. Gen. Genet. 230:277-287(1991)." + NEW_LINE +
            "RN   [2]" + NEW_LINE +
            "RP   SEQUENCE FROM N.A." + NEW_LINE +
            "RC   STRAIN=S288c / FY1678;" + NEW_LINE +
            "RX   MEDLINE=97313263; PubMed=9169867;" + NEW_LINE +
            "RA   Jacq C., Alt-Moerbe J., Andre B., Arnold W., Bahr A., Ballesta J.P.G.," + NEW_LINE +
            "RA   Bargues M., Baron L., Becker A., Biteau N., Bloecker H., Blugeon C.," + NEW_LINE +
            "RA   Boskovic J., Brandt P., Brueckner M., Buitrago M.J., Coster F.," + NEW_LINE +
            "RA   Delaveau T., del Rey F., Dujon B., Eide L.G., Garcia-Cantalejo J.M.," + NEW_LINE +
            "RA   Goffeau A., Gomez-Peris A., Granotier C., Hanemann V., Hankeln T.," + NEW_LINE +
            "RA   Hoheisel J.D., Jaeger W., Jimenez A., Jonniaux J.-L., Kraemer C.," + NEW_LINE +
            "RA   Kuester H., Laamanen P., Legros Y., Louis E.J., Moeller-Rieker S.," + NEW_LINE +
            "RA   Monnet A., Moro M., Mueller-Auer S., Nussbaumer B., Paricio N.," + NEW_LINE +
            "RA   Paulin L., Perea J., Perez-Alonso M., Perez-Ortin J.E., Pohl T.M.," + NEW_LINE +
            "RA   Prydz H., Purnelle B., Rasmussen S.W., Remacha M., Revuelta J.L.," + NEW_LINE +
            "RA   Rieger M., Salom D., Saluz H.P., Saiz J.E., Saren A.-M., Schaefer M.," + NEW_LINE +
            "RA   Scharfe M., Schmidt E.R., Schneider C., Scholler P., Schwarz S.," + NEW_LINE +
            "RA   Urrestarazu L.A., Verhasselt P., Vissers S., Voet M., Volckaert G.," + NEW_LINE +
            "RA   Wagner G., Wambutt R., Wedler E., Wedler H., Woelfl S., Harris D.E.," + NEW_LINE +
            "RA   Bowman S., Brown D., Churcher C.M., Connor R., Dedman K., Gentles S.," + NEW_LINE +
            "RA   Hamlin N., Hunt S., Jones L., McDonald S., Murphy L., Niblett D.," + NEW_LINE +
            "RA   Odell C., Oliver K., Rajandream M.A., Richards C., Shore L.," + NEW_LINE +
            "RA   Walsh S.V., Barrell B.G., Dietrich F.S., Mulligan J.T., Allen E.," + NEW_LINE +
            "RA   Araujo R., Aviles E., Berno A., Carpenter J., Chen E., Cherry J.M.," + NEW_LINE +
            "RA   Chung E., Duncan M., Hunicke-Smith S., Hyman R.W., Komp C.," + NEW_LINE +
            "RA   Lashkari D., Lew H., Lin D., Mosedale D., Nakahara K., Namath A.," + NEW_LINE +
            "RA   Oefner P., Oh C., Petel F.X., Roberts D., Schramm S., Schroeder M.," + NEW_LINE +
            "RA   Shogren T., Shroff N., Winant A., Yelton M.A., Botstein D.," + NEW_LINE +
            "RA   Davis R.W., Johnston M., Andrews S., Brinkman R., Cooper J., Ding H.," + NEW_LINE +
            "RA   Du Z., Favello A., Fulton L., Gattung S., Greco T., Hallsworth K.," + NEW_LINE +
            "RA   Hawkins J., Hillier L.W., Jier M., Johnson D., Johnston L.," + NEW_LINE +
            "RA   Kirsten J., Kucaba T., Langston Y., Latreille P., Le T., Mardis E.," + NEW_LINE +
            "RA   Menezes S., Miller N., Nhan M., Pauley A., Peluso D., Rifkin L.," + NEW_LINE +
            "RA   Riles L., Taich A., Trevaskis E., Vignati D., Wilcox L., Wohldman P.," + NEW_LINE +
            "RA   Vaudin M., Wilson R., Waterston R., Albermann K., Hani J., Heumann K.," + NEW_LINE +
            "RA   Kleine K., Mewes H.-W., Zollner A., Zaccaria P.;" + NEW_LINE +
            "RT   \"The nucleotide sequence of Saccharomyces cerevisiae chromosome IV.\";" + NEW_LINE +
            "RL   Nature 387:75-78(1997)." + NEW_LINE +
            "CC   -!- FUNCTION: Controls nuclear migration. NUM1 specifically controls" + NEW_LINE +
            "CC       the interaction of the bud neck cytoskeleton with the pre-" + NEW_LINE +
            "CC       divisional G2 nucleus perhaps by recognizing G2-specific" + NEW_LINE +
            "CC       cytoplasmic microtubuli or other components of the nuclear" + NEW_LINE +
            "CC       envelope." + NEW_LINE +
            "CC   -!- MISCELLANEOUS: Additional regions of lower homology to the repeat" + NEW_LINE +
            "CC       consensus (always starting with proline) are found in both" + NEW_LINE +
            "CC       flanking domains of the tandem repeats." + NEW_LINE +
            "CC   -!- SIMILARITY: Contains 1 PH domain." + NEW_LINE +
            "CC   --------------------------------------------------------------------------" + NEW_LINE +
            "CC   This SWISS-PROT entry is copyright. It is produced through a collaboration" + NEW_LINE +
            "CC   between  the Swiss Institute of Bioinformatics  and the  EMBL outstation -" + NEW_LINE +
            "CC   the European Bioinformatics Institute.  There are no  restrictions on  its" + NEW_LINE +
            "CC   use  by  non-profit  institutions as long  as its content  is  in  no  way" + NEW_LINE +
            "CC   modified and this statement is not removed.  Usage  by  and for commercial" + NEW_LINE +
            "CC   entities requires a license agreement (See http://www.isb-sib.ch/announce/" + NEW_LINE +
            "CC   or send an email to license@isb-sib.ch)." + NEW_LINE +
            "CC   --------------------------------------------------------------------------" + NEW_LINE +
            "DR   EMBL; X61236; CAA43554.1; -." + NEW_LINE +
            "DR   EMBL; Z50046; CAA90372.1; -." + NEW_LINE +
            "DR   GermOnline; 140641; -." + NEW_LINE +
            "DR   SGD; S0002557; NUM1." + NEW_LINE +
            "DR   GO; GO:0005934; C:bud tip; IDA." + NEW_LINE +
            "DR   GO; GO:0005938; C:cell cortex; IDA." + NEW_LINE +
            "DR   GO; GO:0015631; F:tubulin binding; IPI." + NEW_LINE +
            "DR   GO; GO:0000226; P:microtubule cytoskeleton organization and b...; IMP." + NEW_LINE +
            "DR   GO; GO:0000065; P:nuclear migration (sensu Saccharomyces); IMP." + NEW_LINE +
            "DR   InterPro; IPR005529; DUF321." + NEW_LINE +
            "DR   InterPro; IPR001849; PH." + NEW_LINE +
            "DR   Pfam; PF03778; DUF321; 13." + NEW_LINE +
            "DR   Pfam; PF00169; PH; 1." + NEW_LINE +
            "DR   SMART; SM00233; PH; 1." + NEW_LINE +
            "DR   PROSITE; PS50003; PH_DOMAIN; 1." + NEW_LINE +
            "KW   Repeat." + NEW_LINE +
            "FT   DOMAIN      593   1384       12.5 X TANDEM REPEATS." + NEW_LINE +
            "FT   REPEAT      593    656       1." + NEW_LINE +
            "FT   REPEAT      657    727       2." + NEW_LINE +
            "FT   REPEAT      728    798       3." + NEW_LINE +
            "FT   REPEAT      799    862       4." + NEW_LINE +
            "FT   REPEAT      863    926       5." + NEW_LINE +
            "FT   REPEAT      927    990       6." + NEW_LINE +
            "FT   REPEAT      991   1054       7." + NEW_LINE +
            "FT   REPEAT     1055   1118       8." + NEW_LINE +
            "FT   REPEAT     1119   1182       9." + NEW_LINE +
            "FT   REPEAT     1183   1246       10." + NEW_LINE +
            "FT   REPEAT     1247   1310       11." + NEW_LINE +
            "FT   REPEAT     1311   1374       12." + NEW_LINE +
            "FT   REPEAT     1375   1384       13 (INCOMPLETE)." + NEW_LINE +
            "FT   DOMAIN     2573   2683       PH." + NEW_LINE +
            "FT   CONFLICT   1570   1570       A -> V (in Ref. 1)." + NEW_LINE +
            "FT   CONFLICT   1822   1822       E -> K (in Ref. 1)." + NEW_LINE +
            "FT   CONFLICT   1960   1962       KAS -> RHL (in Ref. 1)." + NEW_LINE +
            "FT   CONFLICT   1971   1972       KD -> RN (in Ref. 1)." + NEW_LINE +
            "FT   CONFLICT   2049   2049       S -> N (in Ref. 1)." + NEW_LINE +
            "FT   CONFLICT   2637   2637       V -> A (in Ref. 1)." + NEW_LINE +
            "SQ   SEQUENCE   2748 AA;  313030 MW;  EB4E48F950621142 CRC64;" + NEW_LINE +
            "     MSHNNRHKKN NDKDSSAGQY ANSIDNSLSQ ESVSTNGVTR MANLKADECG SGDEGDKTKR" + NEW_LINE +
            "     FSISSILSKR ETKDVLPEFA GSSSHNGVLT ANSSKDMNFT LELSENLLVE CRKLQSSNEA" + NEW_LINE +
            "     KNEQIKSLKQ IKESLSDKIE ELTNQKKSFM KELDSTKDLN WDLESKLTNL SMECRQLKEL" + NEW_LINE +
            "     KKKTEKSWND EKESLKLLKT DLEILTLTKN GMENDLSSQK LHYDKEISEL KERILDLNNE" + NEW_LINE +
            "     NDRLLISVSD LTSEINSLQS NRTERIKIQK QLDDAKASIS SLKRKVQKKY YQKQHTSDTT" + NEW_LINE +
            "     VTSDPDSEGT TSEEDIFDIV IEIDHMIETG PSVEDISEDL VKKYSEKNNM ILLSNDSYKN" + NEW_LINE +
            "     LLQKSESASK PKDDELMTKE VAENLNMIAL PNDDNYSKKE FSLESHIKYL EASGYKVLPL" + NEW_LINE +
            "     EEFENLNESL SNPSYNYLKE KLQALKKIPI DQSTFNLLKE PTIDFLLPLT SKIDCLIIPT" + NEW_LINE +
            "     KDYNDLFESV KNPSIEQMKK CLEAKNDLQS NICKWLEERN GCKWLSNDLY FSMVNKIETP" + NEW_LINE +
            "     SKQYLSDKAK EYDQVLIDTK ALEGLKNPTI DFLREKASAS DYLLLKKEDY VSPSLEYLVE" + NEW_LINE +
            "     HAKATNHHLL SDSAYEDLVK CKENPDMEFL KEKSAKLGHT VVSNEAYSEL EKKLEQPSLE" + NEW_LINE +
            "     YLVEHAKATN HHLLSDSAYE DLVKCKENPD MEFLKEKSAK LGHTVVSNEA YSELQRKYSE" + NEW_LINE +
            "     LEKEVEQPSL AYLVEHAKAT DHHLLSDSAY EDLVKCKENP DVEFLKEKSA KLGHTVVSSE" + NEW_LINE +
            "     EYSELQRKYS ELEKEVEQPS LAYLVEHAKA TDHHLLSDSA YEELVKCKEN PDMEFLKEKS" + NEW_LINE +
            "     AKLGHTVVSN EAYSELEKKL EQPSLAYLVE HAKATDHHLL SDSAYEDLVK CKENSDVEFL" + NEW_LINE +
            "     KEKSAKLGHT VVSNEAYSEL EKKLEQPSLA YLVEHAKATD HHLLSDSAYE DLVKCKENPD" + NEW_LINE +
            "     MEFLKEKSAK LGHTVVSNEA YSELEKKLEQ PSLEYLVEHA KATNHHLLSD SAYEDLVKCK" + NEW_LINE +
            "     ENPDMEFLKE KSAKLGHTVV SNEAYSELEK KLEQPSLEYL VEHAKATNHH LLSDSAYEEL" + NEW_LINE +
            "     VKCKENPDVE FLKEKSAKLG HTVVSNEAYS ELEKKLEQPS LEYLVEHAKA TNHHLLSDSA" + NEW_LINE +
            "     YEELVKCKEN PDVEFLKEKS AKLGHTVVSN EAYSELEKKL EQPSLAYLVE HAKATDHHLL" + NEW_LINE +
            "     SDSAYEDLVK CKENPDVEFL KEKSAKLGHT VVSNEAYSEL EKKLEQPSLA YLVEHAKATD" + NEW_LINE +
            "     HHLLSDSAYE DLVKCKENPD MEFLKEKSAK LGHTVVSNEA YSELEKKLEQ PSLEYLVEHA" + NEW_LINE +
            "     KATNHHLLSD SAYEDLVKCK ENPDMEFLKE KSAKLGHTVV SNKEYSELEK KLEQPSLEYL" + NEW_LINE +
            "     VKHAEQIQSK IISISDFNTL ANPSMEDMAS KLQKLEYQIV SNDEYIALKN TMEKPDVELL" + NEW_LINE +
            "     RSKLKGYHII DTTTYNELVS NFNSPTLKFI EEKAKSKGYR LIEPNEYLDL NRIATTPSKE" + NEW_LINE +
            "     EIDNFCKQIG CYALDSKEYE RLKNSLENPS KKFIEENAAL LDLVLVDKTE YQAMKDNASN" + NEW_LINE +
            "     KKSLIPSTKA LDFVTMPAPQ LASAEKSSLQ KRTLSDIENE LKALGYVAIR KENLPNLEKP" + NEW_LINE +
            "     IVDNASKNDV LNLCSKFSLV PLSTEEYDNM RKEHTKILNI LGDPSIDFLK EKCEKYQMLI" + NEW_LINE +
            "     ISKHDYEEKQ EAIENPGYEF ILEKASALGY ELVSEVELDR MKQMIDSPDI DYMQEKAARN" + NEW_LINE +
            "     EMVLLRNEEK EALQKKIEYP SLTFLIEKAA GMNKILVDQI EYDETIRKCN HPTRMELEES" + NEW_LINE +
            "     CHHLNLVLLD QNEYSTLREP LENRNVEDLI NTLSKLNYIA IPNTIYQDLI GKYENPNFDY" + NEW_LINE +
            "     LKDSLNKMDY VAISRQDYEL MVAKYEKPQL DYLKISSEKI DHIVVPLSEY NLMVTNYRNP" + NEW_LINE +
            "     SLSYLKEKAV LNNHILIKED DYKNILAVSE HPTVIHLSEK ASLLNKVLVD KDDFATMSRS" + NEW_LINE +
            "     IEKPTIDFLS TKALSMGKIL VNESTHKRNE KLLSEPDSEF LTMKAKEQGL IIISEKEYSE" + NEW_LINE +
            "     LRDQIDRPSL DVLKEKAAIF DSIIVENIEY QQLVNTTSPC PPITYEDLKV YAHQFGMELC" + NEW_LINE +
            "     LQKPNKLSGA ERAERIDEQS INTTSSNSTT TSSMFTDALD DNIEELNRVE LQNNEDYTDI" + NEW_LINE +
            "     ISKSSTVKDA TIFIPAYENI KNSAEKLGYK LVPFEKSNIN LKNIEAPLFS KDNDDTSVAS" + NEW_LINE +
            "     SIDLDHLSRK AEKYGMTLIS DQEFEEYHIL KDNAVNLNGG MEEMNNPLSE NQNLAAKTTN" + NEW_LINE +
            "     TAQEGAFQNT VPHNDMDNEE VEYGPDDPTF TVRQLKKPAG DRNLILTSRE KTLLSRDDNI" + NEW_LINE +
            "     MSQNEAVYGD DISDSFVDES QEIKNDVDII KTQAMKYGML CIPESNFVGA SYASAQDMSD" + NEW_LINE +
            "     IVVLSASYYH NLMSPEDMKW NCVSNEELQA EVKKRGLQIA LTTKEDKKGQ ATASKHEYVS" + NEW_LINE +
            "     HKLNNKTSTV STKSGAKKGL AEAAATTAYE DSESHPQIEE QSHRTNHHKH HKRQQSLNSN" + NEW_LINE +
            "     STSKTTHSSR NTPASRRDIV ASFMSRAGSA SRTASLQTLA SLNEPSIIPA LTQTVIGEYL" + NEW_LINE +
            "     FKYYPRLGPF GFESRHERFF WVHPYTLTLY WSASNPILEN PANTKTKGVA ILGVESVTDP" + NEW_LINE +
            "     NPYPTGLYHK SIVVTTETRT IKFTCPTRQR HNIWYNSLRY LLQRNMQGIS LEDIADDPTD" + NEW_LINE +
            "     NMYSGKIFPL PGENTKSSSK RLSASRRSVS TRSLRHRVPQ SRSFGNLR" + NEW_LINE +
            "//";


    public static final String O01367 =
            "ID   HOW_DROME      STANDARD;      PRT;   405 AA." + NEW_LINE +
            "AC   O01367; O02392; P91680; Q8IN11; Q8T999; Q94539;" + NEW_LINE +
            "DT   10-OCT-2003 (Rel. 42, Created)" + NEW_LINE +
            "DT   10-OCT-2003 (Rel. 42, Last sequence update)" + NEW_LINE +
            "DT   10-OCT-2003 (Rel. 42, Last annotation update)" + NEW_LINE +
            "DE   Held out wings protein (KH-domain protein KH93F) (Putative RNA-binding" + NEW_LINE +
            "DE   protein) (Muscle-specific protein) (Wings held out protein) (Struthio" + NEW_LINE +
            "DE   protein) (Quaking-related 93F)." + NEW_LINE +
            "GN   HOW OR WHO OR STRU OR KH93F OR QKR93F OR CG10293." + NEW_LINE +
            "OS   Drosophila melanogaster (Fruit fly)." + NEW_LINE +
            "OC   Eukaryota; Metazoa; Arthropoda; Hexapoda; Insecta; Pterygota;" + NEW_LINE +
            "OC   Neoptera; Endopterygota; Diptera; Brachycera; Muscomorpha;" + NEW_LINE +
            "OC   Ephydroidea; Drosophilidae; Drosophila." + NEW_LINE +
            "OX   NCBI_TaxID=7227;" + NEW_LINE +
            "RN   [1]" + NEW_LINE +
            "RP   SEQUENCE FROM N.A. (ISOFORM ZYGOTIC), FUNCTION, INDUCTION, TISSUE" + NEW_LINE +
            "RP   SPECIFICITY, AND MUTAGENESIS OF ARG-185." + NEW_LINE +
            "RX   MEDLINE=97236479; PubMed=9118803;" + NEW_LINE +
            "RA   Baehrecke E.H.;" + NEW_LINE +
            "RT   \"who encodes a KH RNA binding protein that functions in muscle" + NEW_LINE +
            "RT   development.\";" + NEW_LINE +
            "RL   Development 124:1323-1332(1997)." + NEW_LINE +
            "RN   [2]" + NEW_LINE +
            "RP   SEQUENCE FROM N.A. (ISOFORM ZYGOTIC), FUNCTION, AND SUBCELLULAR" + NEW_LINE +
            "RP   LOCATION." + NEW_LINE +
            "RC   STRAIN=Oregon-R;" + NEW_LINE +
            "RX   MEDLINE=97313250; PubMed=9169854;" + NEW_LINE +
            "RA   Zaffran S., Astier M., Gratecos D., Semeriva M.;" + NEW_LINE +
            "RT   \"The held out wings (how) Drosophila gene encodes a putative RNA-" + NEW_LINE +
            "RT   binding protein involved in the control of muscular and cardiac" + NEW_LINE +
            "RT   activity.\";" + NEW_LINE +
            "RL   Development 124:2087-2098(1997)." + NEW_LINE +
            "RN   [3]" + NEW_LINE +
            "RP   SEQUENCE FROM N.A. (ISOFORMS MATERNAL AND ZYGOTIC), FUNCTION, TISSUE" + NEW_LINE +
            "RP   SPECIFICITY, AND DEVELOPMENTAL STAGE." + NEW_LINE +
            "RC   TISSUE=Embryo;" + NEW_LINE +
            "RX   MEDLINE=98008900; PubMed=9344542;" + NEW_LINE +
            "RA   Lo P.C.H., Frasch M.;" + NEW_LINE +
            "RT   \"A novel KH-domain protein mediates cell adhesion processes in" + NEW_LINE +
            "RT   Drosophila.\";" + NEW_LINE +
            "RL   Dev. Biol. 190:241-256(1997)." + NEW_LINE +
            "RN   [4]" + NEW_LINE +
            "RP   SEQUENCE FROM N.A. (ISOFORM ZYGOTIC), TISSUE SPECIFICITY, AND" + NEW_LINE +
            "RP   DEVELOPMENTAL STAGE." + NEW_LINE +
            "RC   STRAIN=Canton-S; TISSUE=Embryo;" + NEW_LINE +
            "RX   MEDLINE=97473527; PubMed=9332381;" + NEW_LINE +
            "RA   Fyrberg C., Becker J., Barthmaier P., Mahaffey J., Fyrberg E.;" + NEW_LINE +
            "RT   \"A Drosophila muscle-specific gene related to the mouse quaking" + NEW_LINE +
            "RT   locus.\";" + NEW_LINE +
            "RL   Gene 197:315-323(1997)." + NEW_LINE +
            "RN   [5]" + NEW_LINE +
            "RP   SEQUENCE FROM N.A." + NEW_LINE +
            "RC   STRAIN=Berkeley;" + NEW_LINE +
            "RX   MEDLINE=20196006; PubMed=10731132;" + NEW_LINE +
            "RA   Adams M.D., Celniker S.E., Holt R.A., Evans C.A., Gocayne J.D.," + NEW_LINE +
            "RA   Amanatides P.G., Scherer S.E., Li P.W., Hoskins R.A., Galle R.F.," + NEW_LINE +
            "RA   George R.A., Lewis S.E., Richards S., Ashburner M., Henderson S.N.," + NEW_LINE +
            "RA   Sutton G.G., Wortman J.R., Yandell M.D., Zhang Q., Chen L.X.," + NEW_LINE +
            "RA   Brandon R.C., Rogers Y.-H.C., Blazej R.G., Champe M., Pfeiffer B.D.," + NEW_LINE +
            "RA   Wan K.H., Doyle C., Baxter E.G., Helt G., Nelson C.R., Miklos G.L.G.," + NEW_LINE +
            "RA   Abril J.F., Agbayani A., An H.-J., Andrews-Pfannkoch C., Baldwin D.," + NEW_LINE +
            "RA   Ballew R.M., Basu A., Baxendale J., Bayraktaroglu L., Beasley E.M.," + NEW_LINE +
            "RA   Beeson K.Y., Benos P.V., Berman B.P., Bhandari D., Bolshakov S.," + NEW_LINE +
            "RA   Borkova D., Botchan M.R., Bouck J., Brokstein P., Brottier P.," + NEW_LINE +
            "RA   Burtis K.C., Busam D.A., Butler H., Cadieu E., Center A., Chandra I.," + NEW_LINE +
            "RA   Cherry J.M., Cawley S., Dahlke C., Davenport L.B., Davies P.," + NEW_LINE +
            "RA   de Pablos B., Delcher A., Deng Z., Mays A.D., Dew I., Dietz S.M.," + NEW_LINE +
            "RA   Dodson K., Doup L.E., Downes M., Dugan-Rocha S., Dunkov B.C., Dunn P.," + NEW_LINE +
            "RA   Durbin K.J., Evangelista C.C., Ferraz C., Ferriera S., Fleischmann W.," + NEW_LINE +
            "RA   Fosler C., Gabrielian A.E., Garg N.S., Gelbart W.M., Glasser K.," + NEW_LINE +
            "RA   Glodek A., Gong F., Gorrell J.H., Gu Z., Guan P., Harris M.," + NEW_LINE +
            "RA   Harris N.L., Harvey D.A., Heiman T.J., Hernandez J.R., Houck J.," + NEW_LINE +
            "RA   Hostin D., Houston K.A., Howland T.J., Wei M.-H., Ibegwam C.," + NEW_LINE +
            "RA   Jalali M., Kalush F., Karpen G.H., Ke Z., Kennison J.A., Ketchum K.A.," + NEW_LINE +
            "RA   Kimmel B.E., Kodira C.D., Kraft C., Kravitz S., Kulp D., Lai Z.," + NEW_LINE +
            "RA   Lasko P., Lei Y., Levitsky A.A., Li J.H., Li Z., Liang Y., Lin X.," + NEW_LINE +
            "RA   Liu X., Mattei B., McIntosh T.C., McLeod M.P., McPherson D.," + NEW_LINE +
            "RA   Merkulov G., Milshina N.V., Mobarry C., Morris J., Moshrefi A.," + NEW_LINE +
            "RA   Mount S.M., Moy M., Murphy B., Murphy L., Muzny D.M., Nelson D.L.," + NEW_LINE +
            "RA   Nelson D.R., Nelson K.A., Nixon K., Nusskern D.R., Pacleb J.M.," + NEW_LINE +
            "RA   Palazzolo M., Pittman G.S., Pan S., Pollard J., Puri V., Reese M.G.," + NEW_LINE +
            "RA   Reinert K., Remington K., Saunders R.D.C., Scheeler F., Shen H.," + NEW_LINE +
            "RA   Shue B.C., Siden-Kiamos I., Simpson M., Skupski M.P., Smith T.," + NEW_LINE +
            "RA   Spier E., Spradling A.C., Stapleton M., Strong R., Sun E.," + NEW_LINE +
            "RA   Svirskas R., Tector C., Turner R., Venter E., Wang A.H., Wang X.," + NEW_LINE +
            "RA   Wang Z.-Y., Wassarman D.A., Weinstock G.M., Weissenbach J.," + NEW_LINE +
            "RA   Williams S.M., Woodage T., Worley K.C., Wu D., Yang S., Yao Q.A.," + NEW_LINE +
            "RA   Ye J., Yeh R.-F., Zaveri J.S., Zhan M., Zhang G., Zhao Q., Zheng L.," + NEW_LINE +
            "RA   Zheng X.H., Zhong F.N., Zhong W., Zhou X., Zhu S., Zhu X., Smith H.O.," + NEW_LINE +
            "RA   Gibbs R.A., Myers E.W., Rubin G.M., Venter J.C.;" + NEW_LINE +
            "RT   \"The genome sequence of Drosophila melanogaster.\";" + NEW_LINE +
            "RL   Science 287:2185-2195(2000)." + NEW_LINE +
            "RN   [6]" + NEW_LINE +
            "RP   REVISIONS, AND ALTERNATIVE SPLICING." + NEW_LINE +
            "RX   MEDLINE=22426069; PubMed=12537572;" + NEW_LINE +
            "RA   Misra S., Crosby M.A., Mungall C.J., Matthews B.B., Campbell K.S.," + NEW_LINE +
            "RA   Hradecky P., Huang Y., Kaminker J.S., Millburn G.H., Prochnik S.E.," + NEW_LINE +
            "RA   Smith C.D., Tupy J.L., Whitfield E.J., Bayraktaroglu L., Berman B.P.," + NEW_LINE +
            "RA   Bettencourt B.R., Celniker S.E., de Grey A.D.N.J., Drysdale R.A.," + NEW_LINE +
            "RA   Harris N.L., Richter J., Russo S., Schroeder A.J., Shu S.Q.," + NEW_LINE +
            "RA   Stapleton M., Yamada C., Ashburner M., Gelbart W.M., Rubin G.M.," + NEW_LINE +
            "RA   Lewis S.E.;" + NEW_LINE +
            "RT   \"Annotation of the Drosophila melanogaster euchromatic genome: a" + NEW_LINE +
            "RT   systematic review.\";" + NEW_LINE +
            "RL   Genome Biol. 3:RESEARCH0083.1-RESEARCH0083.22(2002)." + NEW_LINE +
            "RN   [7]" + NEW_LINE +
            "RP   SEQUENCE FROM N.A. (ISOFORM ZYGOTIC)." + NEW_LINE +
            "RC   STRAIN=Berkeley; TISSUE=Embryo;" + NEW_LINE +
            "RX   MEDLINE=22426066; PubMed=12537569;" + NEW_LINE +
            "RA   Stapleton M., Carlson J.W., Brokstein P., Yu C., Champe M.," + NEW_LINE +
            "RA   George R.A., Guarin H., Kronmiller B., Pacleb J.M., Park S., Wan K.H.," + NEW_LINE +
            "RA   Rubin G.M., Celniker S.E.;" + NEW_LINE +
            "RT   \"A Drosophila full-length cDNA resource.\";" + NEW_LINE +
            "RL   Genome Biol. 3:RESEARCH0080.1-RESEARCH0080.8(2002)." + NEW_LINE +
            "CC   -!- FUNCTION: Required for integrin-mediated cell-adhesion in wing" + NEW_LINE +
            "CC       blade. Vital role in steroid regulation of muscle development and" + NEW_LINE +
            "CC       to control heart rate. Required during embryogenesis, in late" + NEW_LINE +
            "CC       stages of somatic muscle development, for myotube migration and" + NEW_LINE +
            "CC       during metamorphosis for muscle reorganization." + NEW_LINE +
            "CC   -!- SUBCELLULAR LOCATION: Nuclear." + NEW_LINE +
            "CC   -!- ALTERNATIVE PRODUCTS:" + NEW_LINE +
            "CC       Event=Alternative splicing; Named isoforms=2;" + NEW_LINE +
            "CC       Name=Zygotic; Synonyms=A;" + NEW_LINE +
            "CC         IsoId=O01367-1; Sequence=Displayed;" + NEW_LINE +
            "CC       Name=Maternal; Synonyms=B;" + NEW_LINE +
            "CC         IsoId=O01367-2; Sequence=VSP_050197;" + NEW_LINE +
            "CC   -!- TISSUE SPECIFICITY: During embryogenesis, expression is seen in" + NEW_LINE +
            "CC       mesodermal precursors of somatic, visceral and pharyngeal muscle." + NEW_LINE +
            "CC       Later in embryogenesis, expression is restricted to heart and" + NEW_LINE +
            "CC       muscle attachment sites of the epidermis. During onset of" + NEW_LINE +
            "CC       metamorphosis, expression is seen in muscle and muscle attachment" + NEW_LINE +
            "CC       cells." + NEW_LINE +
            "CC   -!- DEVELOPMENTAL STAGE: Expressed both maternally and zygotically" + NEW_LINE +
            "CC       during embryonic, larval and pupal development." + NEW_LINE +
            "CC   -!- INDUCTION: By 20-hydroxyecdysone at the onset of metamorphosis." + NEW_LINE +
            "CC   -!- MISCELLANEOUS: Mutants exhibit wing blisters and flight" + NEW_LINE +
            "CC       impairment." + NEW_LINE +
            "CC   -!- SIMILARITY: Contains 1 KH domain." + NEW_LINE +
            "CC   --------------------------------------------------------------------------" + NEW_LINE +
            "CC   This SWISS-PROT entry is copyright. It is produced through a collaboration" + NEW_LINE +
            "CC   between  the Swiss Institute of Bioinformatics  and the  EMBL outstation -" + NEW_LINE +
            "CC   the European Bioinformatics Institute.  There are no  restrictions on  its" + NEW_LINE +
            "CC   use  by  non-profit  institutions as long  as its content  is  in  no  way" + NEW_LINE +
            "CC   modified and this statement is not removed.  Usage  by  and for commercial" + NEW_LINE +
            "CC   entities requires a license agreement (See http://www.isb-sib.ch/announce/" + NEW_LINE +
            "CC   or send an email to license@isb-sib.ch)." + NEW_LINE +
            "CC   --------------------------------------------------------------------------" + NEW_LINE +
            "DR   EMBL; U85651; AAB51251.1; -." + NEW_LINE +
            "DR   EMBL; U72331; AAB17350.1; -." + NEW_LINE +
            "DR   EMBL; AF003106; AAB60946.1; -." + NEW_LINE +
            "DR   EMBL; AF003107; AAB60947.1; -." + NEW_LINE +
            "DR   EMBL; U87150; AAB47553.1; -." + NEW_LINE +
            "DR   EMBL; AE003737; AAF55952.1; -." + NEW_LINE +
            "DR   EMBL; AE003737; AAN13901.1; -." + NEW_LINE +
            "DR   EMBL; AY069862; AAL40007.1; -." + NEW_LINE +
            "DR   FlyBase; FBgn0017397; how." + NEW_LINE +
            "DR   GO; GO:0005634; C:nucleus; IDA." + NEW_LINE +
            "DR   GO; GO:0003723; F:RNA binding; IDA." + NEW_LINE +
            "DR   GO; GO:0007155; P:cell adhesion; IMP." + NEW_LINE +
            "DR   GO; GO:0016477; P:cell migration; IMP." + NEW_LINE +
            "DR   GO; GO:0016203; P:muscle attachment; IMP." + NEW_LINE +
            "DR   GO; GO:0007521; P:muscle cell fate determination; IDA." + NEW_LINE +
            "DR   GO; GO:0008016; P:regulation of heart rate; IMP." + NEW_LINE +
            "DR   InterPro; IPR004087; KH_dom." + NEW_LINE +
            "DR   InterPro; IPR004088; KH_type_1." + NEW_LINE +
            "DR   Pfam; PF00013; KH; 1." + NEW_LINE +
            "DR   SMART; SM00322; KH; 1." + NEW_LINE +
            "DR   PROSITE; PS50084; KH_TYPE_1; 1." + NEW_LINE +
            "KW   Developmental protein; Nuclear protein; RNA-binding;" + NEW_LINE +
            "KW   Alternative splicing." + NEW_LINE +
            "FT   DOMAIN       11     73       Gln-rich." + NEW_LINE +
            "FT   DOMAIN      142    210       KH." + NEW_LINE +
            "FT   VARSPLIC    370    405       VGAIKQQRRLATNREHPYQRATVGVPAKPAGFIEIQ -> G" + NEW_LINE +
            "FT                                GLFAR (in isoform Maternal)." + NEW_LINE +
            "FT                                /FTId=VSP_050197." + NEW_LINE +
            "FT   MUTAGEN     185    185       R->C: In allele how-E44; embryonic" + NEW_LINE +
            "FT                                lethal." + NEW_LINE +
            "FT   CONFLICT     33     33       Q -> QQA (in Ref. 3)." + NEW_LINE +
            "FT   CONFLICT     46     46       Q -> QAQ (in Ref. 7)." + NEW_LINE +
            "FT   CONFLICT     52     52       P -> S (in Ref. 7)." + NEW_LINE +
            "FT   CONFLICT    338    339       QT -> RA (in Ref. 4)." + NEW_LINE +
            "FT   CONFLICT    370    370       Missing (in Ref. 2, 4 and 7)." + NEW_LINE +
            "FT   CONFLICT    384    384       E -> A (in Ref. 4)." + NEW_LINE +
            "SQ   SEQUENCE   405 AA;  44325 MW;  DCA3A29A12D1A55E CRC64;" + NEW_LINE +
            "     MSVCESKAVV QQQLQQHLQQ QAAAAVVAVA QQQQAQAQAQ AQAQAQQQQQ APQVVVPMTP" + NEW_LINE +
            "     QHLTPQQQQQ STQSIADYLA QLLKDRKQLA AFPNVFTHVE RLLDEEIARV RASLFQINGV" + NEW_LINE +
            "     KKEPLTLPEP EGSVVTMNEK VYVPVREHPD FNFVGRILGP RGMTAKQLEQ ETGCKIMVRG" + NEW_LINE +
            "     KGSMRDKKKE DANRGKPNWE HLSDDLHVLI TVEDTENRAT VKLAQAVAEV QKLLVPQAEG" + NEW_LINE +
            "     EDELKKRQLM ELAIINGTYR DTTAKSVAVC DEEWRRLVAA SDSRLLTSTG LPGLAAQIRA" + NEW_LINE +
            "     PAAAPLGAPL ILNPRMTVPT TAASILSAQA APTAAFDQTG HGMIFAPYDY ANYAALAGNP" + NEW_LINE +
            "     LLTEYADHSV GAIKQQRRLA TNREHPYQRA TVGVPAKPAG FIEIQ" + NEW_LINE +
            "//";

    public static final String P13953 =
            "ID   D2DR_MOUSE     STANDARD;      PRT;   444 AA." + NEW_LINE +
            "AC   P13953;" + NEW_LINE +
            "DT   01-JAN-1990 (Rel. 13, Created)" + NEW_LINE +
            "DT   01-JAN-1990 (Rel. 13, Last sequence update)" + NEW_LINE +
            "DT   28-FEB-2003 (Rel. 41, Last annotation update)" + NEW_LINE +
            "DE   D(2) dopamine receptor." + NEW_LINE +
            "GN   DRD2." + NEW_LINE +
            "OS   Mus musculus (Mouse), and" + NEW_LINE +
            "OS   Rattus norvegicus (Rat)." + NEW_LINE +
            "OC   Eukaryota; Metazoa; Chordata; Craniata; Vertebrata; Euteleostomi;" + NEW_LINE +
            "OC   Mammalia; Eutheria; Rodentia; Sciurognathi; Muridae; Murinae; Mus." + NEW_LINE +
            "OX   NCBI_TaxID=10090, 10116;" + NEW_LINE +
            "RN   [1]" + NEW_LINE +
            "RP   SEQUENCE OF 1-241 AND 271-444 FROM N.A." + NEW_LINE +
            "RC   SPECIES=Rat; TISSUE=Brain;" + NEW_LINE +
            "RX   MEDLINE=89082643; PubMed=2974511;" + NEW_LINE +
            "RA   Bunzow J.R., van Tol H.H.M., Grandy D.K., Albert P., Salon J.," + NEW_LINE +
            "RA   Christie M., Machida C.A., Neve K.A., Civelli O.;" + NEW_LINE +
            "RT   \"Cloning and expression of a rat D2 dopamine receptor cDNA.\";" + NEW_LINE +
            "RL   Nature 336:783-787(1988)." + NEW_LINE +
            "RN   [2]" + NEW_LINE +
            "RP   SEQUENCE FROM N.A." + NEW_LINE +
            "RC   SPECIES=Rat;" + NEW_LINE +
            "RX   MEDLINE=90081866; PubMed=2531846;" + NEW_LINE +
            "RA   Eidne K.A., Taylor P.L., Zabavnik J., Saunders P.T.K., Inglis J.D.;" + NEW_LINE +
            "RT   \"D2 receptor, a missing exon.\";" + NEW_LINE +
            "RL   Nature 342:865-865(1989)." + NEW_LINE +
            "RN   [3]" + NEW_LINE +
            "RP   SEQUENCE FROM N.A." + NEW_LINE +
            "RC   SPECIES=Rat;" + NEW_LINE +
            "RX   MEDLINE=90081872; PubMed=2531847;" + NEW_LINE +
            "RA   Giros B., Sokoloff P., Martres M.-P., Riou J.-F., Emorine L.J.," + NEW_LINE +
            "RA   Schwartz J.-C.;" + NEW_LINE +
            "RT   \"Alternative splicing directs the expression of two D2 dopamine" + NEW_LINE +
            "RT   receptor isoforms.\";" + NEW_LINE +
            "RL   Nature 342:923-926(1989)." + NEW_LINE +
            "RN   [4]" + NEW_LINE +
            "RP   SEQUENCE FROM N.A." + NEW_LINE +
            "RC   SPECIES=Rat;" + NEW_LINE +
            "RX   MEDLINE=90081873; PubMed=2480527;" + NEW_LINE +
            "RA   Monsma F.J. Jr., McVittie L.D., Gerfen C.R., Mahan L.C., Sibley D.R.;" + NEW_LINE +
            "RT   \"Multiple D2 dopamine receptors produced by alternative RNA" + NEW_LINE +
            "RT   splicing.\";" + NEW_LINE +
            "RL   Nature 342:926-929(1989)." + NEW_LINE +
            "RN   [5]" + NEW_LINE +
            "RP   SEQUENCE FROM N.A." + NEW_LINE +
            "RC   SPECIES=Rat;" + NEW_LINE +
            "RX   MEDLINE=90235966; PubMed=2139615;" + NEW_LINE +
            "RA   Rao D.D., McKelvy J., Kebabian J., MacKenzie R.G.;" + NEW_LINE +
            "RT   \"Two forms of the rat D2 dopamine receptor as revealed by the" + NEW_LINE +
            "RT   polymerase chain reaction.\";" + NEW_LINE +
            "RL   FEBS Lett. 263:18-22(1990)." + NEW_LINE +
            "RN   [6]" + NEW_LINE +
            "RP   SEQUENCE FROM N.A." + NEW_LINE +
            "RC   SPECIES=Rat; STRAIN=Sprague-Dawley;" + NEW_LINE +
            "RA   Taylor P.L., Inglis J.D., Eidne K.A.;" + NEW_LINE +
            "RL   Submitted (OCT-1990) to the EMBL/GenBank/DDBJ databases." + NEW_LINE +
            "RN   [7]" + NEW_LINE +
            "RP   SEQUENCE OF 242-270 FROM N.A." + NEW_LINE +
            "RC   SPECIES=Rat;" + NEW_LINE +
            "RX   MEDLINE=90147685; PubMed=2137336;" + NEW_LINE +
            "RA   Miller J.C., Wang Y., Filer D.;" + NEW_LINE +
            "RT   \"Identification by sequence analysis of a second rat brain cDNA" + NEW_LINE +
            "RT   encoding the dopamine (D2) receptor.\";" + NEW_LINE +
            "RL   Biochem. Biophys. Res. Commun. 166:109-112(1990)." + NEW_LINE +
            "RN   [8]" + NEW_LINE +
            "RP   SEQUENCE OF 242-270 FROM N.A." + NEW_LINE +
            "RC   SPECIES=Rat;" + NEW_LINE +
            "RX   MEDLINE=90201380; PubMed=2138567;" + NEW_LINE +
            "RA   O'Dowd B.F., Nguyen T., Tirpak A., Jarvie K.R., Israel Y., Seeman P.," + NEW_LINE +
            "RA   Niznik H.B.;" + NEW_LINE +
            "RT   \"Cloning of two additional catecholamine receptors from rat brain.\";" + NEW_LINE +
            "RL   FEBS Lett. 262:8-12(1990)." + NEW_LINE +
            "RN   [9]" + NEW_LINE +
            "RP   SEQUENCE FROM N.A. (ISOFORM A)." + NEW_LINE +
            "RC   SPECIES=Mouse;" + NEW_LINE +
            "RX   MEDLINE=91122293; PubMed=1991517;" + NEW_LINE +
            "RA   Montmayeur J.P., Bausero P., Amlaiky N., Maroteaux L., Hen R.," + NEW_LINE +
            "RA   Borrelli E.;" + NEW_LINE +
            "RT   \"Differential expression of the mouse D2 dopamine receptor isoforms.\";" + NEW_LINE +
            "RL   FEBS Lett. 278:239-243(1991)." + NEW_LINE +
            "RN   [10]" + NEW_LINE +
            "RP   MUTAGENESIS OF SER-193; SER-194; SER-197 AND SER-420." + NEW_LINE +
            "RC   SPECIES=Rat;" + NEW_LINE +
            "RX   MEDLINE=92333328; PubMed=1321233;" + NEW_LINE +
            "RA   Cox B.A., Henningsen R.A., Spanoyannis A., Neve R.L., Neve K.A.;" + NEW_LINE +
            "RT   \"Contributions of conserved serine residues to the interactions of" + NEW_LINE +
            "RT   ligands with dopamine D2 receptors.\";" + NEW_LINE +
            "RL   J. Neurochem. 59:627-635(1992)." + NEW_LINE +
            "RN   [11]" + NEW_LINE +
            "RP   INTERACTION WITH NEURABIN II." + NEW_LINE +
            "RX   MEDLINE=99321921; PubMed=10391935;" + NEW_LINE +
            "RA   Smith F.D., Oxford G.S., Milgram S.L.;" + NEW_LINE +
            "RT   \"Association of the D2 dopamine receptor third cytoplasmic loop with" + NEW_LINE +
            "RT   spinophilin, a protein phosphatase-1-interacting protein.\";" + NEW_LINE +
            "RL   J. Biol. Chem. 274:19894-19900(1999)." + NEW_LINE +
            "RN   [12]" + NEW_LINE +
            "RP   3D-STRUCTURE MODELING." + NEW_LINE +
            "RX   MEDLINE=93038566; PubMed=1358063;" + NEW_LINE +
            "RA   Livingstone C.D., Strange P.G., Naylor L.H.;" + NEW_LINE +
            "RT   \"Molecular modelling of D2-like dopamine receptors.\";" + NEW_LINE +
            "RL   Biochem. J. 287:277-282(1992)." + NEW_LINE +
            "CC   -!- FUNCTION: This is one of the five types (D1 to D5) of receptors" + NEW_LINE +
            "CC       for dopamine. The activity of this receptor is mediated by G" + NEW_LINE +
            "CC       proteins which inhibit adenylyl cyclase." + NEW_LINE +
            "CC   -!- SUBUNIT: INTERACTS WITH NEURABIN II." + NEW_LINE +
            "CC   -!- SUBCELLULAR LOCATION: Integral membrane protein." + NEW_LINE +
            "CC   -!- ALTERNATIVE PRODUCTS:" + NEW_LINE +
            "CC       Event=Alternative splicing; Named isoforms=2;" + NEW_LINE +
            "CC       Name=Long;" + NEW_LINE +
            "CC         IsoId=P13953-1; Sequence=Displayed;" + NEW_LINE +
            "CC       Name=Short;" + NEW_LINE +
            "CC         IsoId=P13953-2; Sequence=VSP_001871;" + NEW_LINE +
            "CC   -!- SIMILARITY: Belongs to family 1 of G-protein coupled receptors." + NEW_LINE +
            "CC   --------------------------------------------------------------------------" + NEW_LINE +
            "CC   This SWISS-PROT entry is copyright. It is produced through a collaboration" + NEW_LINE +
            "CC   between  the Swiss Institute of Bioinformatics  and the  EMBL outstation -" + NEW_LINE +
            "CC   the European Bioinformatics Institute.  There are no  restrictions on  its" + NEW_LINE +
            "CC   use  by  non-profit  institutions as long  as its content  is  in  no  way" + NEW_LINE +
            "CC   modified and this statement is not removed.  Usage  by  and for commercial" + NEW_LINE +
            "CC   entities requires a license agreement (See http://www.isb-sib.ch/announce/" + NEW_LINE +
            "CC   or send an email to license@isb-sib.ch)." + NEW_LINE +
            "CC   --------------------------------------------------------------------------" + NEW_LINE +
            "DR   EMBL; X53278; CAA37373.1; -." + NEW_LINE +
            "DR   EMBL; M32241; AAA41074.1; -." + NEW_LINE +
            "DR   EMBL; M36831; AAA41075.1; -." + NEW_LINE +
            "DR   EMBL; X56065; CAA39543.1; -." + NEW_LINE +
            "DR   EMBL; X55674; CAA39209.1; -." + NEW_LINE +
            "DR   PIR; S08146; S08146." + NEW_LINE +
            "DR   PIR; S13921; DYMSD2." + NEW_LINE +
            "DR   MGD; MGI:94924; Drd2." + NEW_LINE +
            "DR   InterPro; IPR000276; GPCR_Rhodpsn." + NEW_LINE +
            "DR   Pfam; PF00001; 7tm_1; 1." + NEW_LINE +
            "DR   PRINTS; PR00237; GPCRRHODOPSN." + NEW_LINE +
            "DR   PROSITE; PS00237; G_PROTEIN_RECEP_F1_1; 1." + NEW_LINE +
            "DR   PROSITE; PS50262; G_PROTEIN_RECEP_F1_2; 1." + NEW_LINE +
            "KW   G-protein coupled receptor; Transmembrane; Glycoprotein;" + NEW_LINE +
            "KW   Multigene family; Alternative splicing." + NEW_LINE +
            "FT   DOMAIN        1     37       Extracellular (Potential)." + NEW_LINE +
            "FT   TRANSMEM     38     60       1 (Potential)." + NEW_LINE +
            "FT   DOMAIN       61     71       Cytoplasmic (Potential)." + NEW_LINE +
            "FT   TRANSMEM     72     97       2 (Potential)." + NEW_LINE +
            "FT   DOMAIN       98    108       Extracellular (Potential)." + NEW_LINE +
            "FT   TRANSMEM    109    130       3 (Potential)." + NEW_LINE +
            "FT   DOMAIN      131    151       Cytoplasmic (Potential)." + NEW_LINE +
            "FT   TRANSMEM    152    174       4 (Potential)." + NEW_LINE +
            "FT   DOMAIN      175    186       Extracellular (Potential)." + NEW_LINE +
            "FT   TRANSMEM    187    210       5 (Potential)." + NEW_LINE +
            "FT   DOMAIN      211    374       Cytoplasmic (Potential)." + NEW_LINE +
            "FT   TRANSMEM    375    398       6 (Potential)." + NEW_LINE +
            "FT   DOMAIN      399    406       Extracellular (Potential)." + NEW_LINE +
            "FT   TRANSMEM    407    430       7 (Potential)." + NEW_LINE +
            "FT   DOMAIN      431    444       Cytoplasmic (Potential)." + NEW_LINE +
            "FT   DOMAIN      211    374       INTERACTS WITH NEURABIN II." + NEW_LINE +
            "FT   SITE        193    193       IMPLICATED IN CATECHOL AGONIST BINDING." + NEW_LINE +
            "FT   SITE        194    194       IMPLICATED IN RECEPTOR ACTIVATION." + NEW_LINE +
            "FT   SITE        197    197       IMPLICATED IN RECEPTOR ACTIVATION." + NEW_LINE +
            "FT   CARBOHYD      5      5       N-linked (GlcNAc...) (Potential)." + NEW_LINE +
            "FT   CARBOHYD     17     17       N-linked (GlcNAc...) (Potential)." + NEW_LINE +
            "FT   CARBOHYD     23     23       N-linked (GlcNAc...) (Potential)." + NEW_LINE +
            "FT   DISULFID    107    182       By similarity." + NEW_LINE +
            "FT   VARSPLIC    242    270       Missing (in isoform Short)." + NEW_LINE +
            "FT                                /FTId=VSP_001871." + NEW_LINE +
            "FT   MUTAGEN     193    193       S->A: MODERATE DECREASE IN LIGAND" + NEW_LINE +
            "FT                                BINDING. 200-FOLD REDUCTION OF AGONIST-" + NEW_LINE +
            "FT                                MEDIATED CYCLIC AMP INHIBITION." + NEW_LINE +
            "FT   MUTAGEN     194    194       S->A: SMALL DECREASE IN AGONIST BINDING." + NEW_LINE +
            "FT                                COMPLETE LOSS OF AGONIST-MEDIATED CYCLIC" + NEW_LINE +
            "FT                                AMP INHIBITION." + NEW_LINE +
            "FT   MUTAGEN     197    197       S->A: SMALL DECREASE IN AGONIST BINDING." + NEW_LINE +
            "FT                                18-FOLD REDUCTION OF AGONIST-MEDIATED" + NEW_LINE +
            "FT                                CYCLIC AMP INHIBITION." + NEW_LINE +
            "FT   MUTAGEN     420    420       S->A: MODERATE DECREASE IN LIGAND" + NEW_LINE +
            "FT                                BINDING." + NEW_LINE +
            "FT   CONFLICT     99     99       E -> D (in Ref. 5)." + NEW_LINE +
            "FT   CONFLICT    173    173       G -> R (in Ref. 5)." + NEW_LINE +
            "FT   CONFLICT    180    180       N -> G (in Ref. 5)." + NEW_LINE +
            "SQ   SEQUENCE   444 AA;  50903 MW;  216E56CEE5CA32FB CRC64;" + NEW_LINE +
            "     MDPLNLSWYD DDLERQNWSR PFNGSEGKAD RPHYNYYAML LTLLIFIIVF GNVLVCMAVS" + NEW_LINE +
            "     REKALQTTTN YLIVSLAVAD LLVATLVMPW VVYLEVVGEW KFSRIHCDIF VTLDVMMCTA" + NEW_LINE +
            "     SILNLCAISI DRYTAVAMPM LYNTRYSSKR RVTVMIAIVW VLSFTISCPL LFGLNNTDQN" + NEW_LINE +
            "     ECIIANPAFV VYSSIVSFYV PFIVTLLVYI KIYIVLRKRR KRVNTKRSSR AFRANLKTPL" + NEW_LINE +
            "     KGNCTHPEDM KLCTVIMKSN GSFPVNRRRM DAARRAQELE MEMLSSTSPP ERTRYSPIPP" + NEW_LINE +
            "     SHHQLTLPDP SHHGLHSNPD SPAKPEKNGH AKIVNPRIAK FFEIQTMPNG KTRTSLKTMS" + NEW_LINE +
            "     RRKLSQQKEK KATQMLAIVL GVFIICWLPF FITHILNIHC DCNIPPVLYS AFTWLGYVNS" + NEW_LINE +
            "     AVNPIIYTTF NIEFRKAFMK ILHC" + NEW_LINE +
            "//";
}

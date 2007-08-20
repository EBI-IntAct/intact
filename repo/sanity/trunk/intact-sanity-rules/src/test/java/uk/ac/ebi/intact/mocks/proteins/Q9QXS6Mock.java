/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks.proteins;

import uk.ac.ebi.intact.mocks.AliasMock;
import uk.ac.ebi.intact.mocks.CvAliasType.GeneNameMock;
import uk.ac.ebi.intact.mocks.InstitutionMock;
import uk.ac.ebi.intact.mocks.IntactObjectSetter;
import uk.ac.ebi.intact.mocks.XrefMock;
import uk.ac.ebi.intact.mocks.biosources.MouseMock;
import uk.ac.ebi.intact.mocks.cvDatabases.UniprotMock;
import uk.ac.ebi.intact.mocks.cvInteractorTypes.ProteinInteractorTypeMock;
import uk.ac.ebi.intact.mocks.cvXrefQualifiers.IdentityMock;
import uk.ac.ebi.intact.model.InteractorAlias;
import uk.ac.ebi.intact.model.InteractorXref;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.util.Crc64;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class Q9QXS6Mock {
    private static String SHORTLABEL = "dreb_mouse";
    private static String FULLNAME = "Drebrin";
    private static String ALIAS = "Dbn1";
    private static String IDENTITY_XREF_ID = "Q9QXS6";
    private static String SEQUENCE = "AGVSFSGHRLELLAAYEEVIREESAADWALYTYEDGSDDLKLAASGEGGLQELSGHFENQKVMYGFCSVK" +
            "DSQAALPKYVLINWVGEDVPDARKCACASHVAKVAEFFQGVDVIVNASSVEDIDAGAIGQRLSNGLARLSSPVLHRLRLREDENAEPVGTTYQKTDAAVE" +
            "MKRINREQFWEQAKKEEELRKEEERKKALDARLRFEQERMEQERQEQEERERRYREREQQIEEHRRKQQSLEAEEAKRRLKEQSIFGDQRDEEEESQMKK" +
            "SESEVEEAAAIIAQRPDNPREFFRQQERVASASGGSCDAPAPAPFNHRPGRPYCPFIKASDSGPSSSSSSSSSPPRTPFPYITCHRTPNLSSSLPCSHLD" +
            "SHRRMAPTPIPTRSPSDSSTASTPIAEQIERALDEVTSSQPPPPPPPPPPTQEAQETTPSLDEELSKEAKVTAAPEVWAGCAAEPPQAQEPPLLQSSPLE" +
            "DSMCTESPEQAALAAPAEPAASVTSVADVHAADTIETTTATTDTTIANNVTPAAASLIDLWPGNGEEASTLQAEPRVPTPPSGAEASLAEVPLLNEAAQE" +
            "PLPPVGEGCANLLNFDELPEPPATFCDPEEEVGETLAASQVLTMPSALEEVDQVLEQELEPEPHLLTNGETTQKEGTQASEGYFSQSQEEEFAQSEEPCA" +
            "KVPPPVFYNKPPEIDITCWDADPVPEEEEGFEGGD";

    public static Protein getMock(){
        Protein protein = new ProteinImpl(InstitutionMock.getMock(), MouseMock.getMock(),SHORTLABEL, ProteinInteractorTypeMock.getMock());

        protein = (Protein) IntactObjectSetter.setBasicObject(protein);

        protein.setSequence(SEQUENCE);
        protein.setFullName(FULLNAME);
        protein.setCrc64(Crc64.getCrc64(SEQUENCE));

        InteractorXref xref = XrefMock.getMock(InteractorXref.class, UniprotMock.getMock(), IdentityMock.getMock(), IDENTITY_XREF_ID);
        protein.addXref(xref);

        InteractorAlias alias =  AliasMock.getMock(InteractorAlias.class, GeneNameMock.getMock(),protein);
        alias.setName(ALIAS);
        protein.addAlias(alias);

        return protein;
    }
}
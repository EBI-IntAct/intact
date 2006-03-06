/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 *//**
  * That class keeps some required IntAct object such as some controlled vacabulary terms.
  * This is a singleton.
  *
  * @author Samuel Kerrien (skerrien@ebi.ac.uk)
  * @version $Id$
  */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.checker;

import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.Message;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.MessageHolder;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.CvAliasType;

public class ControlledVocabularyRepository {

    private static boolean initialisationDone = false;

    private static CvTopic authorConfidenceTopic = null;
    private static CvTopic noUniprotUpdate = null;
    private static CvXrefQualifier primaryReferenceXrefQualifier = null;
    private static CvXrefQualifier identityXrefQualifier = null;
    private static CvXrefQualifier seeAlsoXrefQualifier;
    private static CvAliasType geneName;


    public static void check( IntactHelper helper ) {
        initialise( helper );
    }


    /////////////////////////
    // Getters
    public static CvTopic getAuthorConfidenceTopic() {
        return authorConfidenceTopic;
    }

    public static CvTopic getNoUniprotUpdateTopic() {
        return noUniprotUpdate;
    }

    public static CvXrefQualifier getPrimaryXrefQualifier() {
        return primaryReferenceXrefQualifier;
    }

    public static CvXrefQualifier getSeeAlsoXrefQualifier() {
        return seeAlsoXrefQualifier;
    }

    public static CvXrefQualifier getIdentityQualifier() {
        return identityXrefQualifier;
    }

    public static CvAliasType getGeneNameAliasType() {
        return geneName;
    }

    /////////////////////////
    // Init
    private static void initialise( final IntactHelper helper ) {

        if ( initialisationDone == false ) {

            // load CVs by shortlabel
            authorConfidenceTopic = (CvTopic) getCvObjectByLabel( helper, CvTopic.class, CvTopic.AUTHOR_CONFIDENCE );
            noUniprotUpdate = (CvTopic) getCvObjectByLabel( helper, CvTopic.class, CvTopic.NON_UNIPROT );

            // load CVs by MI reference
            primaryReferenceXrefQualifier = (CvXrefQualifier) getCvObjectByMi( helper, CvXrefQualifier.class, CvXrefQualifier.PRIMARY_REFERENCE_MI_REF );
            seeAlsoXrefQualifier = (CvXrefQualifier) getCvObjectByMi( helper, CvXrefQualifier.class, CvXrefQualifier.SEE_ALSO_MI_REF );
            identityXrefQualifier = (CvXrefQualifier) getCvObjectByMi( helper, CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF );
            geneName = (CvAliasType) getCvObjectByMi( helper, CvAliasType.class, CvAliasType.GENE_NAME_MI_REF );

            initialisationDone = true;
        }
    } // init

    private static CvObject getCvObjectByLabel( final IntactHelper helper, Class clazz, final String name ) {
        CvObject object = null;

        String shortClassName = clazz.getName().substring( clazz.getName().lastIndexOf( '.' ) + 1,
                                                           clazz.getName().length() );
        try {
            object = (CvObject) helper.getObjectByLabel( clazz, name );
            if ( object == null ) {
                final String msg = "Could not find "+ shortClassName +" by shortlabel: " + name;
                MessageHolder.getInstance().addCheckerMessage( new Message( msg ) );
            } else {
                System.out.println( "Found "+ shortClassName +" by shortlabel: " + name );
            }
        } catch ( IntactException e ) {
            final String msg = "An error occured while searching for "+ shortClassName +" by the shortlabel: " + name;
            MessageHolder.getInstance().addCheckerMessage( new Message( msg ) );
            e.printStackTrace();
        }

        return object;
    }

    private static CvObject getCvObjectByMi( final IntactHelper helper, Class clazz, final String mi ) {
        CvObject object = null;

        String shortClassName = clazz.getName().substring( clazz.getName().lastIndexOf( '.' ) + 1,
                                                           clazz.getName().length() );
        try {
            object = (CvObject) helper.getObjectByXref( clazz, mi );
            if ( object == null ) {
                final String msg = "Could not find "+ shortClassName +" by MI reference: " + mi;
                MessageHolder.getInstance().addCheckerMessage( new Message( msg ) );
            } else {
                System.out.println( "Found "+ shortClassName +" ("+ object.getShortLabel() +") by MI reference: " + mi );
            }
        } catch ( IntactException e ) {
            final String msg = "An error occured while searching for "+ shortClassName +"  having the MI reference: " + mi;
            MessageHolder.getInstance().addCheckerMessage( new Message( msg ) );
            e.printStackTrace();
        }

        return object;
    }
}
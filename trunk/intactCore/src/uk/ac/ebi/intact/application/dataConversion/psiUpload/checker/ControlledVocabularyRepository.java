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

public class ControlledVocabularyRepository {

    private boolean initialised = false;

    private static boolean initialisationDone = false;

    private static CvTopic authorConfidence = null;
    private static CvXrefQualifier primaryRef = null;


    public static void check( IntactHelper helper ) {
        initialise( helper );
    }

    /////////////////////////
    // Getters
    public static CvTopic getAuthorConfidence() {
        return authorConfidence;
    }

    public static CvXrefQualifier getPrimaryXrefQualifier() {
        return primaryRef;
    }


    /////////////////////////
    // Init
    private static void initialise( final IntactHelper helper ) {

        if( initialisationDone == false ) {

            initialisationDone = true;

            ////////////////////////////
            // author confidence
            String name = "author-confidence";
            try {
                authorConfidence = (CvTopic) helper.getObjectByLabel( CvTopic.class, name );
                if( authorConfidence == null ) {
                    final String msg = "Could not find CvTopic by shortlabel: " + name;
                    MessageHolder.getInstance().addCheckerMessage( new Message( msg ) );
                } else {
                    System.out.println( "Found CvTopic with shortlabel: " + name );
                }
            } catch ( IntactException e ) {
                final String msg = "An error occured while searching for CvTopic having the shortlabel: " + name;
                MessageHolder.getInstance().addCheckerMessage( new Message( msg ) );
                e.printStackTrace();
            }

            ///////////////////////////
            // Primary reference
            name = "primary-reference";
            try {
                primaryRef = (CvXrefQualifier) helper.getObjectByLabel( CvXrefQualifier.class,
                                                                        name );
                if( primaryRef == null ) {
                    final String msg = "Could not find CvXrefQualifier by shortlabel: " + name;
                    MessageHolder.getInstance().addCheckerMessage( new Message( msg ) );
                } else {
                    System.out.println( "Found CvXrefQualifier with shortlabel: " + name );
                }
            } catch ( IntactException e ) {
                final String msg = "An error occured while searching for CvXrefQualifier having the shortlabel: " + name;
                MessageHolder.getInstance().addCheckerMessage( new Message( msg ) );
                e.printStackTrace();
            }
        }
    } // init
}

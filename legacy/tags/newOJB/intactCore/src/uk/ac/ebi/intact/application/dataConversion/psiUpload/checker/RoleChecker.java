/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.checker;

import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.Message;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.MessageHolder;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.CvComponentRole;

import java.util.HashMap;
import java.util.Map;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public final class RoleChecker {

    // will avoid to have to search again later !
    private static final Map cache = new HashMap();


    public static CvComponentRole getCvComponentRole( String role ) {
        return (CvComponentRole) cache.get( role );
    }

    public static void check( final String role,
                              final IntactHelper helper ) {

        if( !cache.keySet().contains( role ) ) {
            CvComponentRole cvComponentRole = null;
            try {

                if( !( "bait".equals( role ) ||
                       "prey".equals( role ) ||
                       "neutral".equals( role ) ) ) {

                    final String msg = "The role: " + role +
                                       " is not supported by PSI. It should be either bait, pery or neutral";
                    MessageHolder.getInstance().addCheckerMessage( new Message( msg ) );
                }


                cvComponentRole = (CvComponentRole) helper.getObjectByLabel( CvComponentRole.class, role );

                if( cvComponentRole != null ) {
                    System.out.println( "Found CvComponentRole with shortlabel: " + role );
                } else {
                    MessageHolder.getInstance().addCheckerMessage( new Message( "Could not find CvComponentRole " +
                                                                                "by shortlabel: " + role ) );
                }
            } catch ( IntactException e ) {
                MessageHolder.getInstance().addCheckerMessage( new Message( "An error occured while searching " +
                                                                            "for CvComponentRole " +
                                                                            "having the shortlabel: " + role ) );
                e.printStackTrace();
            }

            cache.put( role, cvComponentRole );
        }
    }
}

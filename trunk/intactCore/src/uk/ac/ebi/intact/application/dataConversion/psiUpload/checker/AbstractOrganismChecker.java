/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.checker;

import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.Message;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.MessageHolder;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.util.BioSourceFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractOrganismChecker {

    // will avoid to have to search again later !
    protected static final Map cache = new HashMap();

    public static BioSource getBioSource( String taxid ) {
        return (BioSource) cache.get( taxid );
    }

    protected static BioSource checkOnTaxid( final String taxid,
                                             final BioSourceFactory bioSourceFactory ) {

        BioSource bioSource = (BioSource) cache.get( taxid );

        if( bioSource != null ) {

            return bioSource;
        } else {

            try {
                bioSource = bioSourceFactory.getValidBioSource( taxid );

                if( bioSource == null ) {
                    MessageHolder.getInstance().addCheckerMessage( new Message( "Could not find BioSource for " +
                                                                                "the taxid: " + taxid ) );
                } else {
                    System.out.println( "Found BioSource by taxid " + taxid +
                                        ". Shortlabel is " + bioSource.getShortLabel() );
                }
            } catch ( IntactException e ) {
                MessageHolder.getInstance().addCheckerMessage( new Message( "An error occured while searching for " +
                                                                            "BioSource having the taxid: " + taxid ) );
                e.printStackTrace();
            }

            cache.put( taxid, bioSource );
        }

        return bioSource;
    }
}

/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.checker;

import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.CellTypeTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.TissueTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.Constants;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.Message;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.MessageHolder;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.CvTissue;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.CvCellType;
import uk.ac.ebi.intact.util.BioSourceFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractOrganismChecker {

    // will avoid to have to search again later !
    protected static final Map cache = new HashMap();

    protected static BioSource getBioSource( final String taxid,
                                             final CellTypeTag cellType,
                                             final TissueTag tissue ) {
        String id = buildID( taxid, cellType, tissue );
        return (BioSource) cache.get( id );
    }

    /**
     * Build a unique String that describe the conbination {taxid, cellType, tissue}.
     *
     * @param taxid the taxid of an organism.
     * @param cellType the cell Type of an organism.
     * @param tissue the tissue of an organism.
     *
     * @return the unique key.
     */
    private static String buildID( final String taxid,
                                   final CellTypeTag cellType,
                                   final TissueTag tissue ) {

        StringBuffer sb = new StringBuffer( 16 );
        sb.append( taxid );

        if( null != cellType ) {
            sb.append( '#' );
            sb.append( cellType.getPsiDefinition().getId() );
        }

        if( null != tissue ) {
            sb.append( '@' );
            sb.append( tissue.getPsiDefinition().getId() );
        }

        return sb.toString();
    }

    protected static BioSource check( final String taxid,
                                      final CellTypeTag cellType,
                                      final TissueTag tissue,
                                      final IntactHelper helper,
                                      final BioSourceFactory bioSourceFactory ) {

        final String cacheId = buildID( taxid, cellType, tissue );
        BioSource bioSource = (BioSource) cache.get( cacheId );

        if( bioSource == null ) {
            if( null == tissue && null == cellType ) {

                // original BioSource wanted !
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

                cache.put( cacheId, bioSource );

            } else {

                Collection biosources = null;
                try {
                    biosources = helper.search( BioSource.class.getName(), "taxId", taxid );
                } catch ( IntactException e ) {
                    MessageHolder.getInstance().addCheckerMessage( new Message( "Could not find BioSource for " +
                                                                                "the taxid: " + taxid + " in IntAct. " +
                                                                                "Error was: " + e.getMessage()) );
                }

                // Now try to find the one that has the same Tissue and CellType.
                boolean found = false;
                for ( Iterator iterator = biosources.iterator(); iterator.hasNext() && !found; ) {
                    bioSource = (BioSource) iterator.next();
                    if( hasCellType( bioSource, cellType ) && hasTissue( bioSource, tissue ) ) {
                        found = true;
                    }
                }

                if( false == found ) {
                    // error, the requested BioSource can't be found
                    StringBuffer sb = new StringBuffer( 128 );

                    sb.append( "Could not find in IntAct the BioSource having the following caracteristics: " );
                    sb.append( "taxid: ").append( taxid );
                    sb.append( ", CellType: ").append( cellType );
                    sb.append( ", Tissue: ").append( tissue );

                    MessageHolder.getInstance().addCheckerMessage( new Message( sb.toString() ) );
                }
            }
        }

        return bioSource;
    }


    /**
     * Check if the given BioSource has the given CvTissue.
     * The comparison is based on the PSI ID first and if it doesn't exists, we check the shortlabel.
     *
     * @param bioSource the BioSource for which we want to know if it has the CvTissue.
     * @param tissue the Description of the CvTissue we are looking for in the BioSource.
     *
     * @return true if the BioSource has the given CvTissue.
     */
    private static boolean hasTissue( BioSource bioSource, TissueTag tissue ) {

        boolean answer = false;

        CvTissue bsTissue = bioSource.getCvTissue();
        if( bsTissue == null && tissue == null ) {
            answer = true;
        } else if( bsTissue != null && tissue != null ) {
            // we'll check first the PSI-MI definition and if not present, go for the shortlabel.
            String psiId = tissue.getPsiDefinition().getId();

            String intactId = null;
            for ( Iterator iterator = bsTissue.getXrefs().iterator(); iterator.hasNext() && null == intactId; ) {
                Xref xref = (Xref) iterator.next();
                if( Constants.PSI_DB_SHORTLABEL.equals( xref.getCvDatabase().getShortLabel() ) ) {
                     if( ControlledVocabularyRepository.getPrimaryXrefQualifier().equals(  xref.getCvXrefQualifier() ) ) {
                         // found it !
                         intactId = xref.getPrimaryId(); // PSI ID
                     }
                }
            } // xrefs

            // as there is not yet PSI Xrefs for CvTissue, we may have to rely on the shortlabel.
            if( intactId == null ) {

                // check on shortlabel.
                if( tissue.getShortlabel() != null ) {

                    if( tissue.getShortlabel().equalsIgnoreCase( bsTissue.getShortLabel() ) ) {
                        answer = true;
                    }
                }

            } else {

                // we found a PSI Xref ... check if it matches.
                if( psiId.equalsIgnoreCase( intactId ) ) {
                    answer = true;
                }
            }

        }

        return answer;
    }


    /**
     * Check if the given BioSource has the given CvCellType.
     * The comparison is based on the PSI ID first and if it doesn't exists, we check the shortlabel.
     *
     * @param bioSource the BioSource for which we want to know if it has the CvCellType.
     * @param cellType the Description of the CvCellType we are looking for in the BioSource.
     *
     * @return true if the BioSource has the given CvCellType.
     */
    private static boolean hasCellType( BioSource bioSource, CellTypeTag cellType ) {
        boolean answer = false;

        CvCellType bsCellType = bioSource.getCvCellType();
        if( bsCellType == null && cellType == null ) {
            answer = true;
        } else if( bsCellType != null && cellType != null ) {
            // we'll check first the PSI-MI definition and if not present, go for the shortlabel.
            String psiId = cellType.getPsiDefinition().getId();

            String intactId = null;
            for ( Iterator iterator = bsCellType.getXrefs().iterator(); iterator.hasNext() && null == intactId; ) {
                Xref xref = (Xref) iterator.next();
                if( Constants.PSI_DB_SHORTLABEL.equals( xref.getCvDatabase().getShortLabel() ) ) {
                     if( ControlledVocabularyRepository.getPrimaryXrefQualifier().equals(  xref.getCvXrefQualifier() ) ) {
                         // found it !
                         intactId = xref.getPrimaryId();
                     }
                }
            } // xrefs

            // as there is not yet PSI Xrefs for CvCellType, we may have to rely on the shortlabel.
            if( intactId == null ) {

                // check on shortlabel.
                if( cellType.getShortlabel() != null ) {

                    if( cellType.getShortlabel().equalsIgnoreCase( bsCellType.getShortLabel() ) ) {
                        answer = true;
                    }
                }

            } else {

                // we found a PSI Xref ... check if it matches.
                if( psiId.equalsIgnoreCase( intactId ) ) {
                    answer = true;
                }
            }
        }

        return answer;
    }
}
/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.checker;

import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.CellTypeTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.Constants;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.TissueTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.CommandLineOptions;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.Message;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.MessageHolder;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.CvCellType;
import uk.ac.ebi.intact.model.CvTissue;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.util.BioSourceFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * That class handles the common behaviour of OrganismChecker and HostOrganismChecker.
 * eg. find if a protein having such tissue and/or cell type exists.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractOrganismChecker {

    // will avoid to have to search again later !
    protected static final Map cache = new HashMap();

    private final static boolean DEBUG = CommandLineOptions.getInstance().isDebugEnabled();

    protected static BioSource getBioSource( final String taxid,
                                             final CellTypeTag cellType,
                                             final TissueTag tissue ) {

        String id = buildID( taxid, cellType, tissue );
        return (BioSource) cache.get( id );
    }

    /**
     * Build a unique String that describe the conbination {taxid, cellType, tissue}.
     *
     * @param taxid    the taxid of an organism.
     * @param cellType the cell Type of an organism.
     * @param tissue   the tissue of an organism.
     * @return the unique key.
     */
    private static String buildID( final String taxid,
                                   final CellTypeTag cellType,
                                   final TissueTag tissue ) {

        StringBuffer sb = new StringBuffer( 16 );
        sb.append( taxid );

        if( null != cellType ) {
            sb.append( '#' );
            if( null != cellType.getPsiDefinition() ) {
                sb.append( cellType.getShortlabel() );
            } else {
                sb.append( cellType.getPsiDefinition().getId() );
            }
        }

        if( null != tissue ) {
            sb.append( '@' );
            if( tissue.getPsiDefinition() == null ) {
                sb.append( tissue.getShortlabel() );
            } else {
                sb.append( tissue.getPsiDefinition().getId() );
            }
        }

        return sb.toString();
    }

    protected static BioSource check( final String taxid,
                                      final CellTypeTag cellType,
                                      final TissueTag tissue,
                                      final IntactHelper helper,
                                      final BioSourceFactory bioSourceFactory ) {

        if( DEBUG ) {
            System.out.println( "Looking for: " + taxid + ", cellType:" + cellType + ", Tissue:" + tissue );
        }

        final String cacheId = buildID( taxid, cellType, tissue );
        BioSource bioSource = (BioSource) cache.get( cacheId );

        // TODO enhance here because in case there is no mapping for an ID we will keep searching for it. Once is enough.

        if( bioSource == null ) {
            if( DEBUG ) {
                System.out.println( "No found in cache" );
            }

            if( null == tissue && null == cellType ) {

                // original BioSource wanted !
                try {
                    bioSource = bioSourceFactory.getValidBioSource( taxid );

                    if( bioSource == null ) {
                        MessageHolder.getInstance().addCheckerMessage( new Message( "Could not find BioSource for " +
                                                                                    "the taxid: " + taxid ) );
                    } else {

                        StringBuffer sb = new StringBuffer( 128 );
                        sb.append( "Found BioSource by taxid " ).append( taxid );
                        sb.append( ". Shortlabel is " ).append( bioSource.getShortLabel() );

                        if( null != bioSource.getCvCellType() ) {
                            sb.append( ", Celltype shortlabel: " ).append( bioSource.getCvCellType().getShortLabel() );
                        } else {
                            sb.append( ", No CellType" );
                        }

                        if( null != bioSource.getCvTissue() ) {
                            sb.append( ", Tissue shortlabel: " ).append( bioSource.getCvTissue().getShortLabel() );
                        } else {
                            sb.append( ", No Tissue" );
                        }

                        System.out.println( sb.toString() );
                    }
                } catch ( IntactException e ) {
                    MessageHolder.getInstance().addCheckerMessage( new Message( "An error occured while searching for " +
                                                                                "BioSource having the taxid: " + taxid ) );
                    e.printStackTrace();
                }

            } else {

                Collection biosources = null;
                try {
                    if( DEBUG ) {
                        System.out.println( "Look for all BioSources having taxid: " + taxid );
                    }

                    biosources = helper.search( BioSource.class.getName(), "taxId", taxid );

                    if( DEBUG ) {
                        System.out.println( biosources.size() + " found." );
                    }
                } catch ( IntactException e ) {
                    String msg = "Could not find BioSource for the taxid: " + taxid + ". Error was: " + e.getMessage();
                    MessageHolder.getInstance().addCheckerMessage( new Message( msg ) );
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
                    sb.append( "taxid: " ).append( taxid );
                    sb.append( ", CellType: " ).append( cellType );
                    sb.append( ", Tissue: " ).append( tissue );

                    MessageHolder.getInstance().addCheckerMessage( new Message( sb.toString() ) );
                } else {
                    if( DEBUG ) {
                        System.out.println( "BioSource found: " + bioSource );
                    }
                }
            }

            // cache the result of the search.
            cache.put( cacheId, bioSource );
        } else {
            if( DEBUG ) {
                System.out.println( "Found in cache" );
            }
        }

        return bioSource;
    }


    /**
     * Check if the given BioSource has the given CvTissue.
     * The comparison is based on the PSI ID first and if it doesn't exists, we check the shortlabel.
     *
     * @param bioSource the BioSource for which we want to know if it has the CvTissue.
     * @param tissue    the Description of the CvTissue we are looking for in the BioSource.
     * @return true if the BioSource has the given CvTissue.
     */
    private static boolean hasTissue( BioSource bioSource, TissueTag tissue ) {

        boolean answer = false;

        CvTissue bsTissue = bioSource.getCvTissue();
        if( bsTissue == null && tissue == null ) {
            answer = true;
        } else if( bsTissue != null && tissue != null ) {

            // TODO temporarily, we check also the shortlabel of the cellType, that should disappear when we have
            // TODO proper ID as Xref in PSI.

            if( tissue.getPsiDefinition() != null ) {
                String intactId = null;
                String psiId = tissue.getPsiDefinition().getId();

                for ( Iterator iterator = bsTissue.getXrefs().iterator(); iterator.hasNext() && null == intactId; ) {
                    Xref xref = (Xref) iterator.next();
                    if( Constants.PSI_DB_SHORTLABEL.equals( xref.getCvDatabase().getShortLabel() ) ) {
                        if( ControlledVocabularyRepository.getPrimaryXrefQualifier().equals( xref.getCvXrefQualifier() ) ) {
                            // found it !
                            intactId = xref.getPrimaryId(); // PSI ID
                        }
                    }
                } // xrefs

                // we found a PSI Xref ... check if it matches.
                if( psiId != null && psiId.equalsIgnoreCase( intactId ) ) {
                    answer = true;
                }

            } else {

                // as there is not yet PSI Xrefs for CvTissue, we may have to rely on the shortlabel.
                if( tissue.getShortlabel() != null ) {

                    if( tissue.getShortlabel().equalsIgnoreCase( bsTissue.getShortLabel() ) ) {
                        answer = true;
                    }
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
     * @param cellType  the Description of the CvCellType we are looking for in the BioSource.
     * @return true if the BioSource has the given CvCellType.
     */
    private static boolean hasCellType( BioSource bioSource, CellTypeTag cellType ) {

        boolean answer = false;

        CvCellType bsCellType = bioSource.getCvCellType();

        if( bsCellType == null && cellType == null ) {

            answer = true;

        } else if( bsCellType != null && cellType != null ) {
            // we'll check first the PSI-MI definition and if not present, go for the shortlabel.

            // TODO temporarily, we check also the shortlabel of the cellType, that should disappear when we have
            // TODO proper ID as Xref in PSI.

            if( cellType.getShortlabel() != null ) {

                if( cellType.getShortlabel().equalsIgnoreCase( bsCellType.getShortLabel() ) ) {
                    answer = true;
                }

            } else if( null != cellType.getPsiDefinition() ) {

                String intactId = null;
                for ( Iterator iterator = bsCellType.getXrefs().iterator(); iterator.hasNext() && null == intactId; ) {
                    Xref xref = (Xref) iterator.next();
                    if( Constants.PSI_DB_SHORTLABEL.equals( xref.getCvDatabase().getShortLabel() ) ) {
                        if( ControlledVocabularyRepository.getPrimaryXrefQualifier().equals( xref.getCvXrefQualifier() ) ) {
                            // found it !
                            intactId = xref.getPrimaryId();
                        }
                    }
                } // xrefs

                // we found a PSI Xref ... check if it matches.
                String psiId = cellType.getPsiDefinition().getId();
                if( psiId.equalsIgnoreCase( intactId ) ) {
                    answer = true;
                }
            }
        }

        return answer;
    }
}
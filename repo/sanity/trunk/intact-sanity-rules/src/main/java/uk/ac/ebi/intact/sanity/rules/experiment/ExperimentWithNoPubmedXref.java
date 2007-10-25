/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.experiment;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.RuleGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Checks on experiment having no pubmed primary-reference or pmid like 'to_be_assigned'.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk), Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */

@SanityRule( target = Experiment.class, group = {RuleGroup.INTACT, RuleGroup.IMEX} )

public class ExperimentWithNoPubmedXref implements Rule<Experiment> {

    public static final String TO_BE_ASSIGNED = "to_be_assigned";

    public Collection<GeneralMessage> check( Experiment experiment ) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        Collection<ExperimentXref> xrefs = getAllPrimaryRefToPubmedAndDoi( experiment );
        if ( xrefs.isEmpty() ) {
            messages.add( new GeneralMessage( MessageDefinition.EXPERIMENT_WITHOUT_PRIMARY_REF, experiment ) );
        } else {
            switch ( xrefs.size() ) {
                case 1:
                    final Xref xref = xrefs.iterator().next();
                    final String id = xref.getPrimaryId();
                    if ( id != null && id.startsWith( TO_BE_ASSIGNED ) ) {
                        messages.add( new GeneralMessage( MessageDefinition.EXPERIMENT_WITH_PUBMED_TO_BE_ASSIGNED,
                                                          experiment ) );
                    }
                    break;

                default:
                    // more than one Xref
                    messages.add( new GeneralMessage( MessageDefinition.EXPERIMENT_WITH_MULTIPLE_PRIMARY_REF,
                                                      experiment ) );
            }
        }
        return messages;
    }

    private Collection<ExperimentXref> getAllPrimaryRefToPubmedAndDoi( Experiment experiment ) {
        Collection<ExperimentXref> xrefs = null;

        for ( ExperimentXref xref : experiment.getXrefs() ) {
            CvObjectXref cvDatabaseIdentity = CvObjectUtils.getPsiMiIdentityXref( xref.getCvDatabase() );
            if ( cvDatabaseIdentity != null &&
                 ( CvDatabase.PUBMED_MI_REF.equals( cvDatabaseIdentity.getPrimaryId() )
                   ||
                   CvDatabase.DOI_MI_REF.equals( cvDatabaseIdentity.getPrimaryId() )
                 ) ) {
                CvObjectXref cvXrerQualifierIdentity = CvObjectUtils.getPsiMiIdentityXref( xref.getCvXrefQualifier() );
                if ( cvXrerQualifierIdentity != null && CvXrefQualifier.PRIMARY_REFERENCE_MI_REF.equals( cvXrerQualifierIdentity.getPrimaryId() ) ) {
                    if ( xrefs == null ) {
                        xrefs = new ArrayList<ExperimentXref>();
                    }
                    xrefs.add( xref );
                }
            }
        }

        if ( xrefs == null ) return Collections.EMPTY_LIST;

        return xrefs;
    }
}
/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.protein;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageLevel;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.rules.util.CommonMethods;
import uk.ac.ebi.intact.sanity.rules.util.MethodArgumentValidator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */

@SanityRule(target = Protein.class)

public class ProteinIdentityCount implements Rule<Protein> {

    private static final String NO_UNIPROT_DESCRIPTION = "These Proteins have no xref identity to UniProt.";
    private static final String NO_UNIPROT_SUGGESTION = "Edit the Protein and add an identity xref to UniProt.";

    private static final String MULTIPLE_IDENTITY_DESCRIPTION = "These Proteins have multiple xref identity to UniProt.";
    private static final String MULTIPLE_IDENTITY_SUGGESTION = "";


    public Collection<GeneralMessage> check(Protein protein) throws SanityRuleException {
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();

        if(!CommonMethods.isNoUniprotUpdate(protein)){
            int uniprotIdentityCount = 0;
            Collection<InteractorXref> xrefs = protein.getXrefs();

            for(InteractorXref xref : xrefs){
                CvXrefQualifier qualifier = xref.getCvXrefQualifier();
                if(qualifier != null){
                    CvObjectXref qualifierPsiMiXref = CvObjectUtils.getPsiMiIdentityXref(qualifier);
                    if(qualifierPsiMiXref != null && CvXrefQualifier.IDENTITY_MI_REF.equals(qualifierPsiMiXref.getPrimaryId())){
                        CvObjectXref databasePsiMiXref = CvObjectUtils.getPsiMiIdentityXref(xref.getCvDatabase());
                        if(databasePsiMiXref != null && CvDatabase.UNIPROT_MI_REF.equals(databasePsiMiXref.getPrimaryId())){
                            uniprotIdentityCount++;
                        }
                    }
                }
            }
            if(uniprotIdentityCount == 0){
                messages.add(new GeneralMessage(NO_UNIPROT_DESCRIPTION, MessageLevel.MAJOR, NO_UNIPROT_SUGGESTION, protein));
            }else if(uniprotIdentityCount > 1){
                messages.add(new GeneralMessage(MULTIPLE_IDENTITY_DESCRIPTION, MessageLevel.MAJOR, MULTIPLE_IDENTITY_SUGGESTION, protein));
            }
        }

        return messages;
    }

    public static String getMultipleIdentityDescription() {
        return MULTIPLE_IDENTITY_DESCRIPTION;
    }

    public static String getMultipleIdentitySuggestion() {
        return MULTIPLE_IDENTITY_SUGGESTION;
    }

    public static String getNoUniprotDescription() {
        return NO_UNIPROT_DESCRIPTION;
    }

    public static String getNoUniprotSuggestion() {
        return NO_UNIPROT_SUGGESTION;
    }
}
/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.biosource;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.annotation.SanityRule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
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

@SanityRule(target = BioSource.class)

public class NoNewtIdentity  implements Rule {
    // The 2 next Collection are not usefull yet as for now all biosources should have at least 1 xref identity to newt.
    // But, in case this rule would evolve and be changed to "All bioSource should have at least 1 xref identity
    // to Newt or to X_database", this could be implemented without too much pain :
    // If X_database has a psi-mi id then just add in the static block it's psi-mi to the databaseMis collection. If 
    // X_database does not have a psi-mi id, just add it's shortlabel to the databaseShortlabels inside the static
    // block. Then change the DESCRIPTION and SUGGESTION String messages to something more appropriate.
    private static Collection<String> databaseMis = new ArrayList();
    private static Collection<String> databaseShortlabels = new ArrayList();

    private static final String DESCRIPTION = "BioSource having no identity xref to newt";
    private static final String SUGGESTION = "According to the bioSource taxid, " +
                                                              "add an identity xref to Newt";

    static{
        databaseMis.add(CvDatabase.NEWT_MI_REF);
    }

    public Collection<GeneralMessage> check(IntactObject intactObject) throws SanityRuleException {
        MethodArgumentValidator.isValidArgument(intactObject, BioSource.class);
        BioSource bioSource = (BioSource) intactObject;
        Collection<GeneralMessage> messages = new ArrayList();

        Collection<BioSourceXref> xrefs = bioSource.getXrefs();
        int validIdentityXref = 0;
        for(BioSourceXref bioSourceXref : xrefs){
            CvObjectXref cvQualifierIdentityXref = CvObjectUtils.getPsiMiIdentityXref(bioSourceXref.getCvXrefQualifier());
            if(cvQualifierIdentityXref != null && CvXrefQualifier.IDENTITY_MI_REF.equals(cvQualifierIdentityXref.getPrimaryId())){
                CvObjectXref cvDatabaseIdentityXref = CvObjectUtils.getPsiMiIdentityXref(bioSourceXref.getCvDatabase());
                if (cvDatabaseIdentityXref != null){
                    if(databaseMis.contains(cvDatabaseIdentityXref.getPrimaryId())){
                        validIdentityXref++;
                    }
                }else{
                    if(!databaseShortlabels.contains(bioSourceXref.getCvDatabase().getShortLabel())){
                        validIdentityXref++;
                    }
                }
            }
        }

        if(validIdentityXref == 0){
            messages.add(new GeneralMessage(DESCRIPTION, GeneralMessage.AVERAGE_LEVEL, SUGGESTION, bioSource));
        }

        return messages;
    }


    public static String getDescription() {
        return DESCRIPTION;
    }

    public static String getSuggestion() {
        return SUGGESTION;
    }
}
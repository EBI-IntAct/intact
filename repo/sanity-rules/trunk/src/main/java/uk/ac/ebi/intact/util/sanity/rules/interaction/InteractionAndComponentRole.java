/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.rules.interaction;

import uk.ac.ebi.intact.util.sanity.rules.Rule;
import uk.ac.ebi.intact.util.sanity.rules.util.MethodArgumentValidator;
import uk.ac.ebi.intact.util.sanity.rules.messages.GeneralMessage;
import uk.ac.ebi.intact.util.sanity.exception.SanityCheckerException;
import uk.ac.ebi.intact.util.sanity.annotation.SanityRule;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */

@SanityRule(target = Interaction.class)


public class InteractionAndComponentRole   implements Rule {

    private static final String NO_CATEGORY_DESCRIPTION = "ReportTopic.INTERACTION_WITH_NO_CATEGORIES";
    private static final String MIXED_CATEGORIES_DESCRIPTION = "ReportTopic.INTERACTION_WITH_MIXED_COMPONENT_CATEGORIES";
    private static final String NO_PREY_DESCRIPTION = "ReportTopic.INTERACTION_WITH_NO_PREY";
    private static final String NO_BAIT_DESCRIPTION = "ReportTopic.INTERACTION_WITH_NO_BAIT";
    private static final String NO_FLUOROPHORE_DONOR_DESCRIPTION = "ReportTopic.INTERACTION_WITH_NO_FLUOROPHORE_DONOR";
    private static final String NO_ELECTRON_ACCEPTOR_DESCRIPTION = "ReportTopic.INTERACTION_WITH_NO_ELECTRON_ACCEPTOR";
    private static final String NO_ELECTRON_DONOR_DESCRIPTION = "ReportTopic.INTERACTION_WITH_NO_ELECTRON_DONOR";
    private static final String NO_ENZYME_DESCRIPTION = "ReportTopic.INTERACTION_WITH_NO_ENZYME";
    private static final String NO_ENZYME_TARGET_DESCRIPTION = "ReportTopic.INTERACTION_WITH_NO_ENZYME_TARGET";
    private static final String MORE_THAN_2_SELF_PROTEIN_DESCRIPTION = "ReportTopic.INTERACTION_WITH_MORE_THAN_2_SELF_PROTEIN";
    private static final String ONLY_1_NEUTRAL_DESCRIPTION = "ReportTopic.INTERACTION_WITH_ONLY_ONE_NEUTRAL";
    private static final String SUGGESTION = "";

    public Collection<GeneralMessage> check(IntactObject intactObject) throws SanityCheckerException {
        MethodArgumentValidator.isValidArgument(intactObject, Interaction.class);
        Collection<GeneralMessage> messages = new ArrayList<GeneralMessage>();
        Interaction interaction = (Interaction) intactObject;
        Collection<Component> components = interaction.getComponents();

        int preyCount = 0;
        int baitCount = 0;
        int enzymeCount = 0;
        int enzymeTargetCount = 0;
        int neutralCount = 0;
        int selfCount = 0;
        int unspecifiedCount = 0;
        int fluorophoreAcceptorCount = 0;
        int fluorophoreDonorCount = 0;
        int electronAcceptorCount = 0;
        int electronDonorCount = 0;
        int inhibitorCount = 0;
        int inhibitedCount = 0;

        float selfStoichiometry = 0;
        float neutralStoichiometry = 0;


        for(Component component : components){
            CvObjectXref cvRoleIdentityXref = CvObjectUtils.getPsiMiIdentityXref(component.getCvComponentRole());
            String cvRoleMiRef = cvRoleIdentityXref.getPrimaryId();
            if(CvComponentRole.BAIT_PSI_REF.equals(cvRoleMiRef)){
                baitCount++;
            }else if(CvComponentRole.PREY_PSI_REF.equals(cvRoleMiRef)){
                preyCount++;
            }else if(CvComponentRole.ENZYME_PSI_REF.equals(cvRoleMiRef)){
                enzymeCount++;
            }else if(CvComponentRole.ENZYME_TARGET_PSI_REF.equals(cvRoleMiRef)){
                enzymeTargetCount++;
            }else if(CvComponentRole.NEUTRAL_PSI_REF.equals(cvRoleMiRef)){
                neutralCount++;
                neutralStoichiometry = component.getStoichiometry();
            }else if(CvComponentRole.SELF_PSI_REF.equals(cvRoleMiRef)){
                selfCount++;
                selfStoichiometry = component.getStoichiometry();
            }else if(CvComponentRole.UNSPECIFIED_PSI_REF.equals(cvRoleMiRef)){
                unspecifiedCount++;
            }else if(CvComponentRole.FLUROPHORE_ACCEPTOR_MI_REF.equals(cvRoleMiRef)){
                fluorophoreAcceptorCount++;
            }else if(CvComponentRole.FLUROPHORE_DONOR_MI_REF.equals(cvRoleMiRef)){
                fluorophoreDonorCount++;
            }else if(CvComponentRole.ELECTRON_ACCEPTOR_MI_REF.equals(cvRoleMiRef)){
                electronAcceptorCount++;
            }else if(CvComponentRole.ELECTRON_DONOR_MI_REF.equals(cvRoleMiRef)){
                electronDonorCount++;
            }else if(CvComponentRole.INHIBITOR_PSI_REF.equals(cvRoleMiRef)){
                inhibitorCount++;
            }else if(CvComponentRole.INHIBITED_PSI_REF.equals(cvRoleMiRef)){
                inhibitedCount++;
            }
        }


            int baitPrey = ( baitCount + preyCount > 0 ? 1 : 0 );
            int enzymeTarget = ( enzymeCount + enzymeTargetCount > 0 ? 1 : 0 );
            int neutral = ( neutralCount > 0 ? 1 : 0 );
            int self = ( selfCount > 0 ? 1 : 0 );
            int unspecified = ( unspecifiedCount > 0 ? 1 : 0 );
            int fluorophoreAcceptorDonor = ( fluorophoreAcceptorCount + fluorophoreDonorCount > 0 ? 1 : 0 );
            int electronAcceptorDonor = ( electronAcceptorCount + electronDonorCount > 0 ? 1 : 0 );
            int inhibitedInhibitor = ( inhibitorCount + inhibitedCount > 0 ? 1 : 0 );

            // The only mixed category allowed is "neutral + inhibitedInhibitor"
            // For exemple : baitPrey + neutral should not be superior to 1 but baitPrey + neutral + inhibitedInhibitor
            // can be superior to one. Therefore we need to do 2 counts of mixed categories one with all the categories
            // but not neutral and one with all categoriew but not inhibitedInhibitor. 
            int categoryCountWithoutInhibCat = baitPrey + neutral + enzymeTarget + self + unspecified + fluorophoreAcceptorDonor +
                    electronAcceptorDonor;
            int categoryCountWithoutNeutralCat = baitPrey + inhibitedInhibitor + enzymeTarget + self + unspecified + fluorophoreAcceptorDonor +
                    electronAcceptorDonor;

            boolean isAMixedCategoryInteraction = false;
            switch ( categoryCountWithoutInhibCat ) {
                case 0:
                    messages.add(new GeneralMessage(NO_CATEGORY_DESCRIPTION, GeneralMessage.HIGH_LEVEL, SUGGESTION, interaction));
                    break;
                case 1:
                    if ( baitPrey == 1 ) {
                        if ( baitCount == 0 ) {
                            messages.add(new GeneralMessage(NO_BAIT_DESCRIPTION, GeneralMessage.HIGH_LEVEL, SUGGESTION, interaction));
                        } else if ( preyCount == 0 ) {
                            messages.add(new GeneralMessage(NO_PREY_DESCRIPTION, GeneralMessage.HIGH_LEVEL, SUGGESTION, interaction));
                        }
                    } else if ( fluorophoreAcceptorDonor == 1 ) {
                        if ( fluorophoreDonorCount == 0 ) {
                            messages.add(new GeneralMessage(NO_FLUOROPHORE_DONOR_DESCRIPTION, GeneralMessage.HIGH_LEVEL, SUGGESTION, interaction));
                        }
                    }  else if ( electronAcceptorDonor == 1 ) {
                        if ( electronAcceptorCount == 0 ) {
                            messages.add(new GeneralMessage(NO_ELECTRON_ACCEPTOR_DESCRIPTION, GeneralMessage.HIGH_LEVEL, SUGGESTION, interaction));
                        } else if ( electronDonorCount == 0 ) {
                            messages.add(new GeneralMessage(NO_ELECTRON_DONOR_DESCRIPTION, GeneralMessage.HIGH_LEVEL, SUGGESTION, interaction));
                        }
                    } else if ( enzymeTarget == 1 ) {
                        if ( enzymeCount == 0 ) {
                            messages.add(new GeneralMessage(NO_ENZYME_DESCRIPTION, GeneralMessage.HIGH_LEVEL, SUGGESTION, interaction));
                        } else if ( enzymeTargetCount == 0 ) {
                            messages.add(new GeneralMessage(NO_ENZYME_TARGET_DESCRIPTION, GeneralMessage.HIGH_LEVEL, SUGGESTION, interaction));
                        }
                    }
                    else if ( self == 1 ) {
                        if ( selfCount > 1 ) {
                            messages.add(new GeneralMessage(MORE_THAN_2_SELF_PROTEIN_DESCRIPTION, GeneralMessage.HIGH_LEVEL, SUGGESTION, interaction));
                        } else {
                            if ( selfStoichiometry < 1F ) {

                            }
                        }
                    } else {
                        if ( neutralCount == 1 && inhibitedInhibitor == 0 ) {
                            if ( neutralStoichiometry == 1 ) {
                                messages.add(new GeneralMessage(ONLY_1_NEUTRAL_DESCRIPTION, GeneralMessage.HIGH_LEVEL, SUGGESTION, interaction));
                            }
                        }
                    }
                    break;
                default:
                    isAMixedCategoryInteraction = true;
                    messages.add(new GeneralMessage(MIXED_CATEGORIES_DESCRIPTION, GeneralMessage.HIGH_LEVEL, SUGGESTION, interaction));
                    break;
            }
        // It was no mixed interaction counting all categories but not InhibitedInhibitor, we now check if there's no
        // mixed interaction counting all categories but not Neutral
        if(categoryCountWithoutNeutralCat > 1 && isAMixedCategoryInteraction == false){
            messages.add(new GeneralMessage(MIXED_CATEGORIES_DESCRIPTION, GeneralMessage.HIGH_LEVEL, SUGGESTION, interaction));
        }

        return messages;
    }


    public static String getMixedCategoriesDescription() {
        return MIXED_CATEGORIES_DESCRIPTION;
    }

    public static String getMoreThan2SelfProteinDescription() {
        return MORE_THAN_2_SELF_PROTEIN_DESCRIPTION;
    }

    public static String getNoBaitDescription() {
        return NO_BAIT_DESCRIPTION;
    }

    public static String getNoCategoryDescription() {
        return NO_CATEGORY_DESCRIPTION;
    }

    public static String getNoElectronAcceptorDescription() {
        return NO_ELECTRON_ACCEPTOR_DESCRIPTION;
    }

    public static String getNoElectronDonorDescription() {
        return NO_ELECTRON_DONOR_DESCRIPTION;
    }

    public static String getNoEnzymeDescription() {
        return NO_ENZYME_DESCRIPTION;
    }

    public static String getNoEnzymeTargetDescription() {
        return NO_ENZYME_TARGET_DESCRIPTION;
    }

    public static String getNoFluorophoreDonorDescription() {
        return NO_FLUOROPHORE_DONOR_DESCRIPTION;
    }

    public static String getNoPreyDescription() {
        return NO_PREY_DESCRIPTION;
    }

    public static String getOnly1NeutralDescription() {
        return ONLY_1_NEUTRAL_DESCRIPTION;
    }

    public static String getSuggestion() {
        return SUGGESTION;
    }
}
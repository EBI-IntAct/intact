/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.persister;

import uk.ac.ebi.intact.application.dataConversion.psiUpload.checker.*;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.*;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.CommandLineOptions;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.SearchReplace;

import java.util.*;

/**
 * That class make the data persitent in the Intact database.
 * <br>
 * That class takes care of an Interaction.
 * <br>
 * It assumes that the data are already parsed and passed the validity check successfully.
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public final class InteractionPersister {

    ////////////////////////////////
    // Constants

    private static int MAX_LENGTH_INTERACTION_SHORTLABEL = 20;

    private static boolean DEBUG = CommandLineOptions.getInstance().isDebugEnabled();


    ////////////////////////////////
    // Private classes

    /**
     * Carries the result of the search for the non already existing interaction
     * based on the data found in the PSI file.
     * it contains:
     * - a new shortlabel if the interaction doesn't exists.
     * - the experiment to link to.
     */
    private static class ExperimentWrapper {

        private String shortlabel = null;
        private final ExperimentDescriptionTag experiment;

        public ExperimentWrapper( ExperimentDescriptionTag experiment ) {
            this.experiment = experiment;
        }

        public ExperimentDescriptionTag getExperiment() {
            return experiment;
        }

        public void setShortlabel( String shortlabel ) {
            this.shortlabel = shortlabel;
        }

        public String getShortlabel() {
            return shortlabel;
        }

        public boolean hasShortlabel() {
            return shortlabel != null;
        }

        public String toString() {
            return "ExperimentWrapper{" +
                   "experiment=" + experiment.getShortlabel() +
                   ", shortlabel='" + shortlabel + "'" +
                   "}";
        }
    }


    /////////////////////////////
    // Methods

    public static Collection persist( final InteractionTag interactionTag,
                                      final IntactHelper helper )
            throws IntactException {

        Collection interactions = new ArrayList( 1 );

        // Generating shortlabels
        Collection e = interactionTag.getExperiments();
        Collection experiments = new ArrayList( e.size() );
        for ( Iterator iterator = e.iterator(); iterator.hasNext(); ) {
            ExperimentDescriptionTag experimentDescription = (ExperimentDescriptionTag) iterator.next();
            experiments.add( new ExperimentWrapper( experimentDescription ) );
        }

        if( DEBUG ) {
            System.out.println( "Before createShortlabel() " );
            for ( Iterator iterator = experiments.iterator(); iterator.hasNext(); ) {
                ExperimentWrapper experimentWrapper = (ExperimentWrapper) iterator.next();
                System.out.println( experimentWrapper );
            }
        }

        createShortlabel( interactionTag, experiments, helper );

        if( DEBUG ) {
            System.out.println( "After createShortlabel() " );
            for ( Iterator iterator = experiments.iterator(); iterator.hasNext(); ) {
                ExperimentWrapper experimentWrapper = (ExperimentWrapper) iterator.next();
                System.out.println( experimentWrapper );
            }
        }

        /**
         * KNOWN BUG 1 (might occur only if therre is several experiment in one simgle PSI <entry>)
         * -----------
         * TODO fix it !
         * It's nice to know if the interaction already exist but this is a but too simple.
         * actually, a PSI interaction can be linked to two experiments E1 and E2,
         * that interaction can be existing in 1 IntAct experiment (E1) but not referenced yet
         * in E2. In such case, throwing an exception is too simplistic because some link could be
         * created (with E2) but we kind of skip it in the current state of the application.
         * This is fine as long s we handle a SINGLE experiment per PSI XML file.
         *
         * Currently, we ALWAYS create a new Interaction, if an Interaction already exists and not all experiemnt
         * are referenced in it, we have to update it.
         *
         * Solution 1
         * ----------
         * TODO implement it !
         * (1) Forget about the exception InteractionAlreadyExistsException
         * (2) We need to send back either (maybe as a Result object):
         *        - the new shortlabel if the interaction doesn't exists
         *        - or the interaction itself if it already exists
         *        - in any case the set of experiment that is not linked yet to that interaction
         *          (hence all if it doesn't exists)
         */

        /**
         * According to the curation rule we have to use as gene name:
         *  - if there: first gene name (flagged as uniprot/identity in IntAct)
         *  - else, use the shortlabel of the protein (without the organism suffix)
         *
         * Protein without gene name:
         *    > Q8IWZ2
         *    > Q9NVQ0
         *    > Q9NWG8
         *    > Q96HB3
         */


        // LOOP HERE OVER THE RESULT COLLECTION(shortlabel, experiment)
        for ( Iterator iterator = experiments.iterator(); iterator.hasNext(); ) {
            ExperimentWrapper experimentWrapper = (ExperimentWrapper) iterator.next();
            ExperimentDescriptionTag psiExperiment = experimentWrapper.getExperiment();
            Collection myExperiments = new ArrayList( 1 );
            Experiment intactExperiment = ExperimentDescriptionPersister.persist( psiExperiment, helper );
            myExperiments.add( intactExperiment );
            final String shortlabel = experimentWrapper.getShortlabel();


            /* Components
             * The way to create an Interaction is NOT TRIVIAL: we have firstly to create an
             * Interaction Object with an empty collection of Component. and Then we have to
             * use the interaction.addComponent( Component c ) to fill it up.
             */
            final Collection components = new ArrayList( interactionTag.getParticipants().size() );

            // CvInteractionType
            final String cvInteractionTypeId = interactionTag.getInteractionType().getPsiDefinition().getId();
            CvInteractionType cvInteractionType = InteractionTypeChecker.getCvInteractionType( cvInteractionTypeId );

            // Creation of the Interaction
            Interaction interaction = new InteractionImpl( myExperiments,
                                                           components,
                                                           cvInteractionType,
                                                           shortlabel,
                                                           helper.getInstitution() );

            interaction.setFullName( interactionTag.getFullname() );
            helper.create( interaction );

            interactions.add( interaction );
            System.out.println( "Interaction " + shortlabel + " created under experiment " +
                                intactExperiment.getShortLabel() );

            // Annotations
            final Collection annotations = interactionTag.getAnnotations();
            for ( Iterator iterator2 = annotations.iterator(); iterator2.hasNext(); ) {
                final AnnotationTag annotationTag = (AnnotationTag) iterator2.next();
                final CvTopic cvTopic = AnnotationChecker.getCvTopic( annotationTag.getType() );

                // search for an annotation to re-use, instead of creating a new one.
                Annotation annotation = searchIntactAnnotation( annotationTag, helper );

                if( annotation == null ) {
                    // doesn't exist, then create a new Annotation
                    annotation = new Annotation( helper.getInstitution(), cvTopic );
                    annotation.setAnnotationText( annotationTag.getText() );
                    helper.create( annotation );
                }

                interaction.addAnnotation( annotation );
            }

            // Confidence data
            final ConfidenceTag confidence = interactionTag.getConfidence();
            if( confidence != null ) {
                // TODO look after that unit parameter, we might have to adapt the CvTopic accordingly later.
                final CvTopic authorConfidence = ControlledVocabularyRepository.getAuthorConfidenceTopic();

                // check if that annotation could not be shared.
                Collection _annotations = helper.search( Annotation.class.getName(),
                                                         "description",
                                                         confidence.getValue() );
                Annotation annotation = null;
                for ( Iterator iterator3 = _annotations.iterator(); iterator3.hasNext() && annotation == null; ) {
                    Annotation _annotation = (Annotation) iterator3.next();
                    if( authorConfidence.equals( _annotation.getCvTopic() ) ) {
                        annotation = _annotation;
                    }
                }

                if( annotation == null ) {
                    // create it !
                    annotation = new Annotation( helper.getInstitution(), authorConfidence );
                    annotation.setAnnotationText( confidence.getValue() );
                    helper.create( annotation );
                }

                interaction.addAnnotation( annotation );
            }

            helper.update( interaction );

            // Now process the components...
            final Collection participants = interactionTag.getParticipants();
            for ( Iterator iterator4 = participants.iterator(); iterator4.hasNext(); ) {
                ProteinParticipantTag proteinParticipant = (ProteinParticipantTag) iterator4.next();
                ProteinParticipantPersister.persist( proteinParticipant, interaction, helper );
            }
        }
        return interactions;
    }


    private static class GeneNameIgnoreCaseComparator implements Comparator {

        ////////////////////////////////////////////////
        // Implementation of the Comparable interface
        ////////////////////////////////////////////////

        /**
         * Compares this object with the specified object for order.  Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object.<p>
         *
         * @param o1 the Object to be compared.
         * @param o2 the Object to compare with.
         * @return a negative integer, zero, or a positive integer as this object
         *         is less than, equal to, or greater than the specified object.
         * @throws ClassCastException if the specified object's type prevents it
         *                            from being compared to this Object.
         */
        public final int compare( final Object o1, final Object o2 ) {

            final String s1 = ( (String) o1 ).toLowerCase();
            final String s2 = ( (String) o2 ).toLowerCase();

            // the current string comes first if it's before in the alphabetical order
            if( !( s1.equals( s2 ) ) ) {
                return s1.compareTo( s2 );
            } else {
                return 0;
            }
        }

    }


    /**
     * Create an IntAct shortlabel for a given interaction (ie. a set of [protein, role] ).
     * <p/>
     * - Stategy -
     * <p/>
     * Protein's role can be either: bait, prey or neutral
     * the interaction shortlabel has the following patter: X-Y-Z
     * with a limit in length of 20 caracters.
     * TODO: do not hard code that ! Could be fetched from the DatabaseInspector.
     * <p/>
     * X is (in order of preference):
     * 1. the gene name of the bait protein
     * 2. the gene name of a prey protein (the first one in alphabetical order)
     * 3. the gene name of a neutral protein (the first one in alphabetical order)
     * <p/>
     * Y is :
     * 1. the gene name of a prey protein (the first one in alphabetical order or second
     * if the first has been used already)
     * 2. the gene name of a neutral protein (the first one in alphabetical order or second
     * if the first has been used already)
     * Z is :
     * an Integer that gives the number of occurence in intact.
     * <p/>
     * eg.
     * 1. bait(baaa), prey(paaa, pbbb, pccc), neutral(naaa)
     * should gives us: baaa-paaa-1
     * <p/>
     * 2. bait(baaa), prey(), neutral(naaa)
     * should gives us: baaa-naaa-1
     * <p/>
     * 3. bait(), prey(paaa, pbbb, pccc), neutral(naaa)
     * should gives us: paaa-pbbb-1
     * <p/>
     * 4. bait(), prey(paaa), neutral(naaa)
     * should gives us: paaa-naaa-1
     *
     * @param interaction
     * @param helper
     * @throws IntactException
     */
    private static void createShortlabel( final InteractionTag interaction,
                                          final Collection experiments,
                                          final IntactHelper helper )
            throws IntactException {

        Collection baits = new ArrayList( 2 );
        Collection preys = new ArrayList( 2 );
        Collection neutrals = new ArrayList( 2 );

        /**
         * Search for a gene name in the set, if none exist, take the protein ID.
         */
        final Collection proteins = interaction.getParticipants();
        for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
            ProteinParticipantTag proteinParticipant = (ProteinParticipantTag) iterator.next();
            final String role = proteinParticipant.getRole();
            final ProteinHolder proteinHolder = getProtein( proteinParticipant );

            // the gene name is held in the master protein, not the splice variant.
            final String geneName = getGeneName( proteinHolder.getProtein() );

            if( role.equals( "prey" ) ) { // most numerous role, cut down the number of test
                preys.add( geneName );
            } else if( role.equals( "bait" ) ) {
                baits.add( geneName );
            } else if( role.equals( "neutral" ) ) {
                neutrals.add( geneName );
            } else {
                // we should never get in here if RoleChecker plays its role !
            }
        } // for proteins

        String baitShortlabel = getLabelFromCollection( baits, true ); // fail on error
        String preyShortlabel = getLabelFromCollection( preys, false ); // don't fail on error
        if( preyShortlabel == null ) {
            preyShortlabel = getLabelFromCollection( neutrals, true ); // fail on error
        }

        createInteractionShortLabels( interaction, experiments, baitShortlabel, preyShortlabel, helper );
    }


    /**
     * Search for the first string (in alphabetical order).
     *
     * @param geneNames   a collection of non ordered gene names.
     * @param failOnError if <code>true</code> throw an IntactException when no gene name is found,
     *                    if <code>false</code> sends back <code>null</code>.
     * @return either a String or null according to the failOnError parameter.
     * @throws IntactException thrown when the failOnError parameter is true and no string can be returned.
     * @see uk.ac.ebi.intact.application.dataConversion.psiUpload.persister.InteractionPersister.GeneNameIgnoreCaseComparator
     */
    private static String getLabelFromCollection( Collection geneNames, boolean failOnError ) throws IntactException {
        String shortlabel = null;

        if( geneNames == null ) {
            throw new IllegalArgumentException( "You must give a non null collection of gene name." );
        }

        switch ( geneNames.size() ) {
            case 0:
                // ERROR, we should have a bait.
                // This should have been detected during step 1 or 2.
                if( failOnError ) {
                    throw new IntactException( "Could not find gene name for that interaction." );
                }
                break;
            case 1:
                shortlabel = (String) geneNames.iterator().next();
                break;

            default:
                // more than one ... need sorting
                Object[] _geneNames = geneNames.toArray();
                Arrays.sort( _geneNames, new GeneNameIgnoreCaseComparator() );
                shortlabel = (String) _geneNames[ 0 ];
                break;
        }

        return shortlabel;
    }


    /**
     * Create an interaction shortlabel out of two shortlabels.
     * <br>
     * Take care about the maximum length of the field.
     * <br>
     * It checks as well if the generated shortlabel as already been associated to an other Interaction.
     *
     * @param psiInteraction The interaction we are investigating on.
     * @param experiments Collection in which after processing we have all ExperimentWrapper (shortlabel +
     *                    experimentDescription) in which the interaction hasn't been created yet.
     * @param bait the label for the bait (could be gene name or SPTR entry AC)
     * @param prey the label for the prey (could be gene name or SPTR entry AC)
     * @param helper data access
     */
    private static void createInteractionShortLabels( final InteractionTag psiInteraction,
                                                      final Collection experiments,
                                                      String bait,
                                                      String prey,
                                                      final IntactHelper helper )
            throws IntactException {

        // convert bad characters ('-', ' ', '.') to '_'
        bait = bait.toLowerCase();
        bait = SearchReplace.replace( bait, "-", "_" );
        bait = SearchReplace.replace( bait, " ", "_" );
        bait = SearchReplace.replace( bait, ".", "_" );

        prey = prey.toLowerCase();
        prey = SearchReplace.replace( prey, "-", "_" );
        prey = SearchReplace.replace( prey, " ", "_" );
        prey = SearchReplace.replace( prey, ".", "_" );

        int count = 0;
        String _bait = bait;
        String _prey = prey;
        boolean allLabelFound = false;
        String label = null;
        String suffix = null;

        // check out the curation rules to know how to create an interaction shortlabel.
        // http://www3.ebi.ac.uk/internal/seqdb/curators/intact/Intactcurationrules_000.htm

        while ( !allLabelFound ) {

            if( count == 0 ) {
                suffix = null;
                label = _bait + "-" + _prey;
            } else {
                suffix = "-" + count;
                label = _bait + "-" + _prey + suffix;
            }

            count = ++count;

            // check if truncation needed.
            // if so, remove one character from the longest between bait and prey ... until the length is right.
            while ( label.length() > MAX_LENGTH_INTERACTION_SHORTLABEL ) {
                if( _bait.length() > _prey.length() ) {
                    _bait = _bait.substring( 0, _bait.length() - 1 ); // truncate, remove last charachter (from bait)
                } else {
                    _prey = _prey.substring( 0, _prey.length() - 1 ); // truncate, remove last charachter (from prey)
                }

                if( suffix == null ) {
                    label = _bait + "-" + _prey;
                } else {
                    label = _bait + "-" + _prey + suffix;
                }
            } // while

            // we have the right label's size now ... search for existing one !
            if( DEBUG ) {
                System.out.println( "Search interaction by label: " + label );
            }
            Collection interactions = helper.search( Interaction.class.getName(), "shortlabel", label );
            if( interactions.size() == 0 ) {

                if( DEBUG ) {
                    System.out.println( "No interaction found with the label: " + label );
                }

                // Give the remaining experiment a shortlabel.
                // takes care of gaps in the shortlabel sequence (label-1, label-2, label-3 ...).
                // could create new gaps if some already exists.
                boolean atLeastOneInteractionWithoutShortlabel = false;
                boolean oneExperimentHasAlreadyBeenUpdated = false;
                for ( Iterator iterator = experiments.iterator(); iterator.hasNext() && !atLeastOneInteractionWithoutShortlabel; ) {
                    ExperimentWrapper experimentWrapper = (ExperimentWrapper) iterator.next();
                    // we want to associate only one shortlabel per loop and check if there is at least one
                    // more experiment to update.
                    if( DEBUG ) {
                        System.out.println( "Work on " + experimentWrapper );
                    }
                    if( oneExperimentHasAlreadyBeenUpdated ) {
                        if( !experimentWrapper.hasShortlabel() ) {
                            atLeastOneInteractionWithoutShortlabel = true; // exit the loop.
                            if( DEBUG ) {
                                System.out.println( "At least one more experiment to which we have to give a shortlabel" );
                            }
                        } else {
                            if( DEBUG ) {
                                System.out.println( "has already a shortlabel" );
                            }
                        }
                    } else {
                        if( !experimentWrapper.hasShortlabel() ) {
                            experimentWrapper.setShortlabel( label );
                            oneExperimentHasAlreadyBeenUpdated = true;
                            if( DEBUG ) {
                                System.out.println( "Experiment " + experimentWrapper.getExperiment().getShortlabel()
                                                    + " has been given the interaction shortlabel: " + label );
                            }
                        } else {
                            if( DEBUG ) {
                                System.out.println( "none has been set up to now and the current one has already a shortlabel" );
                            }
                        }
                    }
                }

                if( DEBUG ) {
                    if( atLeastOneInteractionWithoutShortlabel == true ) {
                        System.out.println( "All experiment have been given an interaction shortlabel." );
                    }
                }

                allLabelFound = !atLeastOneInteractionWithoutShortlabel;
            } else {

                if( DEBUG ) {
                    System.out.println( interactions.size() + " interactions found with the label: " + label );
                }

                /**
                 * An interaction already exists in an experiment if:
                 *       (1) The shortlabel has the prefix bait-prey
                 *       (2) if components involved (Protein + Role) are identical.
                 *           If the components are somehow different, a new Interaction should be created
                 *           with the same prefix and a suffix that is not already in use.
                 *           eg.
                 *                We have 3 Proteins:
                 *                    - P1: gene-name -> gene1
                 *                    - P1-1: gene-name -> gene1 (got from P1)
                 *                    - P2: gene-name -> gene2
                 *
                 *                We have 2 interactions
                 *                    - Interaction 1 have interaction between P1(bait) and P2(prey)
                 *                      gives us the shortlabel gene1-gene2-1
                 *                    - Interaction 1 have interaction between P1-1(bait) and P2(prey)
                 *                      gives us the shortlabel gene1-gene2-2
                 *                      (!) the components involved are different even if the gene name are identical
                 *                          hence, we get same interaction name with suffixes 1 and 2.
                 */

                for ( Iterator iterator = interactions.iterator(); iterator.hasNext(); ) {
                    Interaction intactInteraction = (Interaction) iterator.next();

                    alreadyExistsInIntact( psiInteraction, experiments, intactInteraction ); // update experiments !

                } // intact interaction
            }
        } // while
    }


    /**
     * Allows to check if the data carried by an InteractionTag are already existing in the IntAct node.
     * <pre>
     * <b>Reminder</b>:
     *  - an interaction (as a set of components) must be unique in the experiment scope.
     *  - the shortlabel of an interaction msut be unique in the database scope.
     * <p/>
     * <b>Logic sketch<b>:
     *       If there is an experiment in commons between the data declare in PSI and those existing in Intact
     *           - Compare the conponent of the two interactions
     *                  - if the same: the PSI interaction has already an instance in IntAct for that experiment
     *                  - if not the same: compare with the next interaction (if there is one)
     *                          - if no more interaction: the PSI interaction doesn't have an instance in IntAct
     *                                                    for that experiment
     * </pre>
     *
     * @param psi         the PSI data materialised as an Object
     * @param intact      an Intact Interaction.
     * @param experiments it reflects the collection of experiments linked to the PSI interaction and we will
     *                    update it in order to leave only those for which we need to create a new interaction.
     */
    private static void alreadyExistsInIntact( final InteractionTag psi,
                                               final Collection experiments,
                                               final Interaction intact ) {

        // this is in theory a pretty heavy computation but in practice an interaction doesn't have much experiment
        // and few interaction have a lot of components.

        if( DEBUG ) {
            System.out.println( "Compare interactions: " + psi + "\n and " + intact );
        }

        Collection psiExperiments = psi.getExperiments(); // TODO could be experiments
        for ( Iterator iterator = psiExperiments.iterator(); iterator.hasNext(); ) {
            ExperimentDescriptionTag psiExperiment = (ExperimentDescriptionTag) iterator.next();

            Collection intactExperiments = intact.getExperiments();
            for ( Iterator iterator1 = intactExperiments.iterator(); iterator1.hasNext(); ) {
                Experiment intactExperiment = (Experiment) iterator1.next();

                if( DEBUG ) {
                    System.out.println( "Check their experiment: psi(" +
                                        psiExperiment.getShortlabel() +
                                        ") and intact(" +
                                        intactExperiment.getShortLabel() + ")" );
                }

                // compare two experiments using their shortlabel - TODO is this enough ?
                if( intactExperiment.getShortLabel().equals( psiExperiment.getShortlabel() ) ) {
                    // they are the same ... check on the conponents

                    if( DEBUG ) {
                        System.out.println( "They are equals ! Check on their participants..." );
                    }

                    Collection psiComponents = psi.getParticipants();
                    boolean allComponentFound = true;
                    for ( Iterator iterator2 = psiComponents.iterator(); iterator2.hasNext() && allComponentFound; ) {
                        ProteinParticipantTag psiComponent = (ProteinParticipantTag) iterator2.next();

                        ProteinHolder holder = getProtein( psiComponent );
                        final Protein psiProtein;
                        if( holder.isSpliceVariantExisting() ) {
                            psiProtein = holder.getSpliceVariant();
                        } else {
                            psiProtein = holder.getProtein();
                        }

                        final CvComponentRole psiRole = RoleChecker.getCvComponentRole( psiComponent.getRole() );

                        if( DEBUG ) {
                            System.out.println( "PSI: " + psiProtein.getShortLabel() + " (" + psiRole.getShortLabel() + ")" );
                        }

                        Collection intactComponents = intact.getComponents();
                        boolean found = false;
                        for ( Iterator iterator3 = intactComponents.iterator(); iterator3.hasNext() && !found; ) {
                            Component intactComponent = (Component) iterator3.next();

                            if( DEBUG ) {
                                System.out.print( "\tINTACT: " + intactComponent.getInteractor().getShortLabel() +
                                                  " (" + intactComponent.getCvComponentRole().getShortLabel() + "): " );
                            }

                            if( psiRole.equals( intactComponent.getCvComponentRole() ) &&
                                psiProtein.equals( intactComponent.getInteractor() ) ) {
                                if( DEBUG ) {
                                    System.out.println( "EQUALS" );
                                }
                                found = true;

                            } else {
                                // special case, a same protein can be bait and prey in the same interaction.
                                // Hence, we have to browse the whole intact Component set until we find the
                                // component or all have been checked.
                                if( DEBUG ) {
                                    System.out.println( "DIFFERENT" );
                                }
                            }
                        } // intact components

                        if( !found ) {
                            // no need to carry on to check the psi component set because now, we know that
                            // at least one is not found.
                            allComponentFound = false;
                        }

                    } // psi components

                    if( allComponentFound ) {
                        // there is already an instance of that interaction in intact
                        if( DEBUG ) {
                            System.out.println( "All component have been found, hence there is an instance of " +
                                                "that interaction in intact" );
                        }

                        // create a warning message
                        StringBuffer sb = new StringBuffer( 256 );
                        sb.append( "WARNING" ).append( '\n' );
                        sb.append( "An interaction having the shortlabel " ).append( intact.getShortLabel() );
                        sb.append( '\n' );
                        sb.append( "and involving as components: " );
                        for ( Iterator iterator2 = psi.getParticipants().iterator(); iterator2.hasNext(); ) {
                            ProteinParticipantTag psiComponent = (ProteinParticipantTag) iterator2.next();
                            sb.append( '[' );
                            sb.append( psiComponent.getProteinInteractor().getUniprotXref().getId() );
                            sb.append( ',' );
                            sb.append( psiComponent.getRole() );
                            sb.append( ']' ).append( ' ' );
                        }
                        sb.append( '\n' );
                        sb.append( "already exists in IntAct under the experiment " );
                        sb.append( intactExperiment.getShortLabel() );
                        sb.append( '\n' );

                        System.out.println( sb.toString() );

                        // update the experiment collection (remove the corresponding item).
                        ExperimentWrapper experimentWrapper = null;
                        boolean found = false;
                        for ( Iterator iterator2 = experiments.iterator(); iterator2.hasNext() && !found; ) {
                            experimentWrapper = (ExperimentWrapper) iterator2.next();
                            if( experimentWrapper.getExperiment().equals( psiExperiment ) ) {
                                found = true;
                            }
                        }
                        if( found == true ) {
                            experiments.remove( experimentWrapper );
                        }
                    }

                    // else ... just carry on searching.

                } else {
                    if( DEBUG ) {
                        System.out.println( "Experiment shortlabel are different ... don't check the components" );
                    }
                }
            } // intact experiments
        } // psi experiments

        // no instance of that interaction have been found in intact.
    }


    /**
     * Search in IntAct for an Annotation having the a specific type and annotationText.
     *
     * @param annotationTag the description of the Annotation we are looking for.
     * @param helper        the access to the IntAct database
     * @return the found Annotation or null if not found.
     * @throws IntactException
     */
    private static Annotation searchIntactAnnotation( final AnnotationTag annotationTag,
                                                      final IntactHelper helper )
            throws IntactException {

        final String text = annotationTag.getText();
        Collection annotations = helper.search( Annotation.class.getName(), "annotationText", text );
        Annotation annotation = null;

        if( annotations != null ) {
            for ( Iterator iterator = annotations.iterator(); iterator.hasNext() && annotation == null; ) {
                Annotation anAnnotation = (Annotation) annotations.iterator().next();
                if( annotationTag.getType().equals( anAnnotation.getCvTopic().getShortLabel() ) ) {
                    annotation = anAnnotation;
                }
            }
        }

        return annotation;
    }


    /**
     * introspect a Protein object and pick up it's gene name.
     *
     * @param protein the protein for which we want the gene name.
     * @return the Protein's gene name.
     */
    private static String getGeneName( final Protein protein ) {

        String geneName = null;

        final Collection aliases = protein.getAliases();
        for ( Iterator iterator = aliases.iterator(); iterator.hasNext() && geneName == null; ) {
            final Alias alias = (Alias) iterator.next();
            if( alias.getCvAliasType().getShortLabel().equals( "gene-name" ) ) {
                geneName = alias.getName();
            }
        }

        if( geneName == null ) {
            geneName = protein.getShortLabel();

            // remove any _organism in case it exists
            int index = geneName.indexOf( '_' );
            if( index != -1 ) {
                geneName = geneName.substring( 0, index );
            }

            System.err.println( "NOTICE: protein " + protein.getShortLabel() +
                                " does not have a gene name, we will use it's SPTR ID: " + geneName );
        }

        return geneName;
    }


    /**
     * Get an Intact Protein out of a ProteinInteractorTag.
     *
     * @param proteinParticipant
     * @return the IntAct Protein correcponding to the given ProteinParticipantTag.
     */
    private static ProteinHolder getProtein( final ProteinParticipantTag proteinParticipant ) {

        final ProteinInteractorTag proteinInteractor = proteinParticipant.getProteinInteractor();
        final BioSource bioSource = OrganismChecker.getBioSource( proteinInteractor.getOrganism() );
        final String proteinId = proteinInteractor.getUniprotXref().getId();

        return ProteinInteractorChecker.getProtein( proteinId, bioSource );
    }
}
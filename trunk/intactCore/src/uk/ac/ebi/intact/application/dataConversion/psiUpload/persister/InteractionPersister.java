/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.persister;

import uk.ac.ebi.intact.application.dataConversion.psiUpload.checker.*;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.*;
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

    private static class InteractionAlreadyExistsException extends Exception {

        public InteractionAlreadyExistsException( String message ) {
            super( message );
        }
    }

    private static int MAX_LENGTH_INTERACTION_SHORTLABEL = 20;

    public static void persist( final InteractionTag interactionTag,
                                final IntactHelper helper )
            throws IntactException {

        // TODO how to find out if an interaction is already existing ... and then what should we do, update, skip it ?
        // We have decided, for the time being, just to allow to create new interaction in an existing experiment.
        // If an interaction is found already existing (based on the shortlabel and link to the experiment), the
        // creation process is aborted.

        // Shortlabel
        String shortlabel = interactionTag.getShortlabel();
        if( shortlabel == null || "".equals( shortlabel.trim() ) ) {
            // Generating a shortlabel
            try {
                shortlabel = createShortlabel( interactionTag, helper );
                System.out.print( "New Interaction: " + shortlabel + " status: " );
            } catch ( InteractionAlreadyExistsException e ) {
                System.err.println( e.getMessage() );
                return;
            }
        }

        // Experiments
        final Collection experiments = new ArrayList( interactionTag.getExperiments().size() );
        for ( Iterator iterator = interactionTag.getExperiments().iterator(); iterator.hasNext(); ) {
            ExperimentDescriptionTag experimentDescription = (ExperimentDescriptionTag) iterator.next();
            experiments.add( ExperimentDescriptionPersister.persist( experimentDescription, helper ) );
        }

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
        Interaction interaction = new InteractionImpl( experiments,
                                                       components,
                                                       cvInteractionType,
                                                       shortlabel,
                                                       helper.getInstitution() );

        interaction.setFullName( interactionTag.getFullname() );
        helper.create( interaction );

        // Annotations
        final Collection annotations = interactionTag.getAnnotations();
        for ( Iterator iterator = annotations.iterator(); iterator.hasNext(); ) {
            final AnnotationTag annotationTag = (AnnotationTag) iterator.next();
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
            final CvTopic authorConfidence = ControlledVocabularyRepository.getAuthorConfidence();

            // check if that annotation could not be shared.
            Collection _annotations = helper.search( Annotation.class.getName(),
                                                     "description",
                                                     confidence.getValue() );
            Annotation annotation = null;
            for ( Iterator iterator = _annotations.iterator(); iterator.hasNext() && annotation == null; ) {
                Annotation _annotation = (Annotation) iterator.next();
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
        for ( Iterator iterator = participants.iterator(); iterator.hasNext(); ) {
            ProteinParticipantTag proteinParticipant = (ProteinParticipantTag) iterator.next();
            ProteinParticipantPersister.persist( proteinParticipant, interaction, helper );
        }

        System.out.println( "created" );
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
     * @return
     * @throws IntactException
     */
    private static String createShortlabel( final InteractionTag interaction,
                                            final IntactHelper helper )
            throws IntactException, InteractionAlreadyExistsException {

        Collection baits = new ArrayList();
        Collection preys = new ArrayList();
        Collection neutrals = new ArrayList();

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
                // TODO log that in the MessageHolder !
                System.err.println( "Could not handle the role: " + role + ". " +
                                    "Currently, only bait, prey and neutral are supported" );
            }
        } // for proteins

        String baitShortlabel = getLabelFromCollection( baits, true );
        String preyShortlabel = getLabelFromCollection( preys, false );
        if( preyShortlabel == null ) {
            preyShortlabel = getLabelFromCollection( neutrals, true );
        }

        if( baitShortlabel == null || preyShortlabel == null ) {
            System.out.println( "Could not find either a bait, prey or neutral protein's gene name " +
                                "in the interaction: " + interaction );
        }

        return createInteractionShortLabel( interaction, baitShortlabel, preyShortlabel, helper );
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
     * @param bait
     * @param prey
     * @return
     */
    private static String createInteractionShortLabel( final InteractionTag interaction,
                                                       String bait,
                                                       String prey,
                                                       final IntactHelper helper )
            throws IntactException, InteractionAlreadyExistsException {

        // convert bad caracters ('-', ' ', '.') to '_'
        bait = bait.toLowerCase();
        bait = SearchReplace.replace( bait, "-", "_" );
        bait = SearchReplace.replace( bait, " ", "_" );
        bait = SearchReplace.replace( bait, ".", "_" );

        prey = prey.toLowerCase();
        prey = SearchReplace.replace( prey, "-", "_" );
        prey = SearchReplace.replace( prey, " ", "_" );
        prey = SearchReplace.replace( prey, ".", "_" );

        int count = 0;
        String sCount = "" + count;
        String _bait = bait;
        String _prey = prey;
        boolean foundLabel = false;
        String label = null;

        while ( !foundLabel ) {

            sCount = "" + ( ++count );
            label = _bait + "-" + _prey + "-" + sCount;

            // check if truncation needed.
            while ( label.length() > MAX_LENGTH_INTERACTION_SHORTLABEL ) {
                if( _bait.length() > _prey.length() ) {
                    _bait = _bait.substring( 0, _bait.length() - 1 ); // truncate, remove last charachter
                } else {
                    _prey = _prey.substring( 0, _prey.length() - 1 ); // truncate, remove last charachter
                }

                label = _bait + "-" + _prey + "-" + sCount;
            } // while

            // we have the right label's size now ... search for existing one !
            Collection interactions = helper.search( Interaction.class.getName(), "shortlabel", label );
            if( interactions.size() == 0 ) {
                // This label is not used yet, exit the loop
                foundLabel = true;
            } else {

                // check if that's in the current experiment
                Collection psiExperiments = interaction.getExperiments();
                boolean interactionAlreadyExists = false;
                String intactInteractionShortlabel = null;
                String intactExperimentShortlabel = null;
                for ( Iterator iterator = psiExperiments.iterator(); iterator.hasNext()
                                                                     && interactionAlreadyExists == false; ) {
                    ExperimentDescriptionTag psiExperiment = (ExperimentDescriptionTag) iterator.next();
                    String shortlabel = psiExperiment.getShortlabel();

                    for ( Iterator iterator1 = interactions.iterator(); iterator1.hasNext()
                                                                        && interactionAlreadyExists == false; ) {
                        Interaction intactInteraction = (Interaction) iterator1.next();
                        for ( Iterator iterator2 = intactInteraction.getExperiments().iterator();
                              iterator2.hasNext() && interactionAlreadyExists == false; ) {
                            Experiment intactExperiment = (Experiment) iterator2.next();
                            if( intactExperiment.getShortLabel().equals( shortlabel ) ) {
                                interactionAlreadyExists = true;
                                intactInteractionShortlabel = intactInteraction.getShortLabel();
                                intactExperimentShortlabel = intactExperiment.getShortLabel();
                            }
                        }
                    }
                }

                // if so abort !
                if( interactionAlreadyExists ) {
                    throw new InteractionAlreadyExistsException( "An interaction having the shortlabel " +
                                                                 intactInteractionShortlabel +
                                                                 " already exists in that experiments: " +
                                                                 intactExperimentShortlabel );
                }
            }
        } // while

        return label;
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
            System.err.println( "NOTICE: protein " + protein.getShortLabel() + " does not have a gene name, we will " +
                                "use it's SPTR ID." );
            geneName = protein.getShortLabel();
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
        final BioSource bioSource = OrganismChecker.getBioSource( proteinInteractor.getOrganism().getTaxId() );
        final String proteinId = proteinInteractor.getUniprotXref().getId();

        return ProteinInteractorChecker.getProtein( proteinId, bioSource );
    }
}

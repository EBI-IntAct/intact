/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.psimitab;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.tab.converter.tab2xml.XmlConversionException;
import psidev.psi.mi.tab.converter.xml2tab.CrossReferenceConverter;
import psidev.psi.mi.tab.converter.xml2tab.InteractionConverter;
import psidev.psi.mi.tab.converter.xml2tab.TabConversionException;
import psidev.psi.mi.tab.converter.xml2tab.InteractorConverter;
import psidev.psi.mi.tab.expansion.ExpansionStrategy;
import psidev.psi.mi.tab.model.*;
import psidev.psi.mi.tab.model.Interactor;
import psidev.psi.mi.tab.model.builder.Column;
import psidev.psi.mi.xml.model.*;
import psidev.psi.mi.xml.model.InteractionDetectionMethod;
import psidev.psi.mi.xml.model.Organism;
import uk.ac.ebi.intact.psimitab.model.Parameter;
import uk.ac.ebi.intact.psimitab.model.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;
import java.util.regex.Pattern;
import java.io.IOException;

import net.sf.ehcache.CacheManager;

/**
 * Interaction converter.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactInteractionConverter extends InteractionConverter<IntactBinaryInteraction> {

    public static final String EMPTY_COLUMN = String.valueOf( Column.EMPTY_COLUMN );

    private static final Log log = LogFactory.getLog( IntactInteractionConverter.class );

    private static final Pattern EXPERIMENT_LABEL_PATTERN = Pattern.compile( "[a-z-_]+-\\d{4}[a-z]?-\\d+" );

    private static final String IDENTITY_MI_REF = "MI:0356";

    private OntologyNameFinder finder;

    private IntactInteractorConverter interactorConverter;

    //////////////////
    // Constructors

    public IntactInteractionConverter() {
        this.interactorConverter = new IntactInteractorConverter();
    }

    public IntactInteractionConverter( OntologyNameFinder finder ) {
        this();
        this.finder = finder;
    }

    protected BinaryInteraction newBinaryInteraction(Interactor interactorA, Interactor interactorB) {
        return new IntactBinaryInteraction((ExtendedInteractor)interactorA, (ExtendedInteractor)interactorB);
    }

    public InteractorConverter<?> getInteractorConverter() {
        return interactorConverter;
    }

    @Override
    public BinaryInteraction toMitab( Interaction interaction ) throws TabConversionException {

        IntactBinaryInteraction binaryInteraction = (IntactBinaryInteraction) super.toMitab(interaction);

        try {
            process(binaryInteraction, interaction);
        } catch ( IOException e ) {
            throw new TabConversionException( "An error occured while processing binary interaction: " +
                                              binaryInteraction, e );
        }

        return binaryInteraction;
    }

    protected void updateHostOrganism(IntactBinaryInteraction binaryInteraction, Organism hostOrganism, int index) {
        if ( binaryInteraction.hasHostOrganism() ) {
            CrossReference o = binaryInteraction.getHostOrganism().get( index );

            int taxid = Integer.parseInt( o.getIdentifier() );
            hostOrganism.setNcbiTaxId( taxid );

            Names organismNames = new Names();
            organismNames.setShortLabel( o.getDatabase() );
            if ( o.hasText() ) organismNames.setFullName( o.getText() );
            hostOrganism.setNames( organismNames );
        }
    }

    /**
     * Process additional Column information from PSI-MI XML 2.5 to IntactBinaryInteraction.
     *
     * @param bi
     * @param interaction
     */
    public void process( IntactBinaryInteraction bi, Interaction interaction ) throws IOException {

        if ( interaction.getParticipants().size() != 2 ) {
            if ( log.isDebugEnabled() )
                log.debug( "interaction (id:" + interaction.getId() + ") could not be converted to MITAB25 as it does not have exactly 2 participants." );
        }

        Iterator<Participant> pi = interaction.getParticipants().iterator();
        Participant pA = pi.next();
        Participant pB = pi.next();

        final ExtendedInteractor interactorA = bi.getInteractorA();
        final ExtendedInteractor interactorB = bi.getInteractorB();

        if ( pA.hasExperimentalRoles() ) {
            CrossReference experimentalRoleA = extractExperimentalRole( pA );

            if ( interactorA.hasExperimentalRoles() ) {
                bi.getInteractorA().getExperimentalRoles().add( experimentalRoleA );
            } else {
                List<CrossReference> xrefs = new ArrayList<CrossReference>();
                xrefs.add( experimentalRoleA );
                bi.getInteractorA().setExperimentalRoles( xrefs );
            }
        }

        if ( pB.hasExperimentalRoles() ) {
            CrossReference experimentalRoleB = extractExperimentalRole( pB );

            if ( interactorB.hasExperimentalRoles() ) {
                interactorB.getExperimentalRoles().add( experimentalRoleB );
            } else {
                List<CrossReference> xrefs = new ArrayList<CrossReference>();
                xrefs.add( experimentalRoleB );
                interactorB.setExperimentalRoles( xrefs );
            }
        }

        if ( pA.hasBiologicalRole() ) {
            CrossReference biologicalRoleA = extractBiologicalRole( pA );

            if ( interactorA.hasBiologicalRoles() ) {
                interactorA.getBiologicalRoles().add( biologicalRoleA );
            } else {
                List<CrossReference> xrefs = new ArrayList<CrossReference>();
                xrefs.add( biologicalRoleA );
                interactorA.setBiologicalRoles( xrefs );
            }
        }

        if ( pB.hasBiologicalRole() ) {
            CrossReference biologicalRoleB = extractBiologicalRole( pB );

            if ( bi.hasBiologicalRolesInteractorB() ) {
                interactorB.getBiologicalRoles().add( biologicalRoleB );
            } else {
                List<CrossReference> xrefs = new ArrayList<CrossReference>();
                xrefs.add( biologicalRoleB );
                interactorB.setBiologicalRoles( xrefs );
            }
        }

        if ( pA.getInteractor().getInteractorType() != null ) {
            CrossReference typeA = extractInteractorType( pA );
            interactorA.setInteractorType( typeA );
        }

        if ( pB.getInteractor().getInteractorType() != null ) {
            CrossReference typeB = extractInteractorType( pB );
            interactorB.setInteractorType( typeB );
        }

        if ( ! pA.getInteractor().getXref().getAllDbReferences().isEmpty() ) {
            List<CrossReference> propertiesA = extractProperties( pA );
            if ( !interactorA.hasProperties() ) interactorA.setProperties( new ArrayList<CrossReference>() );
            interactorA.getProperties().addAll( propertiesA );
        }

        if ( ! pB.getInteractor().getXref().getAllDbReferences().isEmpty() ) {
            List<CrossReference> propertiesB = extractProperties( pB );
            if ( !interactorB.hasProperties() ) interactorB.setProperties( new ArrayList<CrossReference>() );
            interactorB.getProperties().addAll( propertiesB );
        }

        if ( interaction.hasExperiments() ) {
            for ( ExperimentDescription description : interaction.getExperiments() ) {
                if ( description.hasHostOrganisms() ) {
                    Organism hostOrganism = description.getHostOrganisms().iterator().next();

                    String id = Integer.toString( hostOrganism.getNcbiTaxId() );
                    String db = hostOrganism.getNames().getShortLabel();
                    CrossReference organismRef = new CrossReferenceImpl( db, id );
                    //String text = hostOrganism.getNames().getFullName();
                    //CrossReference organismRef = new CrossReferenceImpl(db, id ,text);

                    if ( bi.hasHostOrganism() ) {
                        bi.getHostOrganism().add( organismRef );
                    } else {
                        List<CrossReference> hos = new ArrayList<CrossReference>();
                        hos.add( organismRef );
                        bi.setHostOrganism( hos );
                    }
                }
            }
        }

        for ( ExperimentDescription experiment : interaction.getExperiments() ) {
            for ( Attribute attribute : experiment.getAttributes() ) {
                if ( attribute.getName().equals( "dataset" ) ) {
                    String dataset = attribute.getValue();

                    if ( bi.hasDatasetName() ) {
                        bi.getDataset().add( dataset );
                    } else {
                        List<String> datasets = new ArrayList<String>();
                        datasets.add( dataset );
                        bi.setDataset( datasets );
                    }
                }
            }
        }

        List<Author> authors = new ArrayList<Author>();
        for ( ExperimentDescription experiment : interaction.getExperiments() ) {
            String label = null;
            if (experiment.getNames() != null) {
                label = experiment.getNames().getShortLabel();
            }
            if ( isWellFormattedExperimentShortlabel( label ) ) {
                final StringBuilder sb = new StringBuilder();
                final String[] values = label.split( "-" );
                sb.append( StringUtils.capitalize( values[0] ) );
                sb.append( " et al. " );
                sb.append( '(' );
                sb.append( values[1] );
                sb.append( ')' );

                authors.add( new AuthorImpl( sb.toString() ) );
                bi.setAuthors( authors );
            } else {
                // if Interaction is inferred by currator
                InteractionDetectionMethod method = experiment.getInteractionDetectionMethod();
                if ( method != null && method.getNames() != null ) {
                    String shortLabel = method.getNames().getShortLabel();
                    if ( shortLabel != null && shortLabel.equals( "inferred by curator" ) ) {
                        String experimentFullName;
                        if ( experiment.getNames() != null && ( experimentFullName = experiment.getNames().getFullName() ) != null ) {
                            authors.add( new AuthorImpl( experimentFullName ) );
                            bi.setAuthors( authors );
                        }
                    }
                }
            }
        }

        // annotations
        for (Attribute attribute : pA.getAttributes()) {
            Annotation annotation = new Annotation(attribute.getName(), attribute.getValue());
            bi.getInteractorA().getAnnotations().add(annotation);
        }

        for (Attribute attribute : pB.getAttributes()) {
            Annotation annotation = new Annotation(attribute.getName(), attribute.getValue());
            bi.getInteractorB().getAnnotations().add(annotation);
        }

        // parameters
        for (psidev.psi.mi.xml.model.Parameter xmlParam : pA.getParameters()) {
            Parameter param = new Parameter(xmlParam.getTerm(), xmlParam.getFactor(), xmlParam.getBase(), xmlParam.getExponent(), xmlParam.getUnit());
            bi.getInteractorA().getParameters().add(param);
        }

        for (psidev.psi.mi.xml.model.Parameter xmlParam : pB.getParameters()) {
            Parameter param = new Parameter(xmlParam.getTerm(), xmlParam.getFactor(), xmlParam.getBase(), xmlParam.getExponent(), xmlParam.getUnit());
            bi.getInteractorB().getParameters().add(param);
        }

        for (psidev.psi.mi.xml.model.Parameter xmlParam : interaction.getParameters()) {
            Parameter param = new Parameter(xmlParam.getTerm(), xmlParam.getFactor(), xmlParam.getBase(), xmlParam.getExponent(), xmlParam.getUnit());
            bi.getParameters().add(param);
        }
    }

    @Override
    protected void populateInteractionFromMitab(Interaction interaction, BinaryInteraction<?> binaryInteraction, int index) {
        // mitab -> xml

        IntactBinaryInteraction ibi = (IntactBinaryInteraction) binaryInteraction;

        // host organism
        final int hostCount = ibi.getHostOrganism().size();
        if( index <= hostCount ) {
            CrossReference orgXref = ibi.getHostOrganism().get( index );
            Organism hostOrganism = new Organism();
            hostOrganism.setNcbiTaxId( Integer.parseInt(orgXref.getIdentifier()) );
            Names organismNames = new Names();
            organismNames.setShortLabel( orgXref.getText() );
            hostOrganism.setNames( organismNames );

            final ExperimentDescription experiment;
            if( interaction.hasExperiments() ) {
                experiment = interaction.getExperiments().iterator().next();
            } else {
                experiment = new ExperimentDescription( );
                interaction.getExperiments().add( experiment );
            }
            experiment.getHostOrganisms().add(hostOrganism);
        } else {
            log.warn( "Could not fetch a host organism at index " + index + " only " + hostCount + " found." );
        }

        // expansion method
        final int expansionCount = ibi.getExpansionMethods().size();

        if (expansionCount > 1) {
            if (log.isErrorEnabled()) log.error("More than 1 expansion method found: "+ibi);
        } else if (expansionCount == 1){
            String expMethod = ibi.getExpansionMethods().iterator().next();
            if( ! EMPTY_COLUMN.equals( expMethod ) ) {
                Attribute attr = new Attribute("expansion", expMethod);
                interaction.getAttributes().add(attr);
            }
        }

        // dataset -- there could be many: add them all
        for (String dataset : ibi.getDataset()) {
            Attribute attr = new Attribute("dataset", dataset);
            interaction.getAttributes().add(attr);
        }

        // parameters --  there could be many: add them all
        for (uk.ac.ebi.intact.psimitab.model.Parameter parameter : ibi.getParameters()) {
            psidev.psi.mi.xml.model.Parameter param = new psidev.psi.mi.xml.model.Parameter(parameter.getType(), parameter.getFactor());
            param.setUnit(parameter.getUnit());
            param.setBase(parameter.getBase());
            param.setExponent(parameter.getExponent());
            interaction.getParameters().add(param);
        }
    }

        /**
     * Checks if the ExperimentLabel is how expected.
     *
     * @param label
     * @return
     */
    private boolean isWellFormattedExperimentShortlabel( String label ) {
        if ( label == null ) {
            return false;
        }
        return EXPERIMENT_LABEL_PATTERN.matcher( label ).matches();
    }

    /**
     * Extracts the relevant information for the psimitab experimental role.
     *
     * @param participant
     * @return experimental role
     */
    private CrossReference extractExperimentalRole( Participant participant ) {

        ExperimentalRole role = participant.getExperimentalRoles().iterator().next();
        final String[] values = role.getXref().getPrimaryRef().getId().split( ":" );
        String id = values[1];
        String db = values[0];
        String text = role.getNames().getShortLabel();

        return new CrossReferenceImpl( db, id, text );
    }

    /**
     * Extracts the relevant information for the psimitab biological role.
     *
     * @param participant
     * @return experimental role
     */
    private CrossReference extractBiologicalRole( Participant participant ) {

        BiologicalRole role = participant.getBiologicalRole();
        final String[] values = role.getXref().getPrimaryRef().getId().split( ":" );
        String id = values[1];
        String db = values[0];
        String text = role.getNames().getShortLabel();

        return new CrossReferenceImpl( db, id, text );
    }

    /**
     * Extracts the relevant informations for the psimitab interactor type.
     *
     * @param participant
     * @return interactor type
     */
    private CrossReference extractInteractorType( Participant participant ) {
        final String[] values = participant.getInteractor().getInteractorType().getXref().getPrimaryRef().getId().split( ":" );
        String id = values[1];
        String db = values[0];
        String text = participant.getInteractor().getInteractorType().getNames().getShortLabel();

        return new CrossReferenceImpl( db, id, text );
    }

    /**
     * Extracts the relevant informations for the psimitab propterties.
     *
     * @param participant
     * @return list of properties
     */
   /* private List<CrossReference> extractProperties( Participant participant ) throws IOException {
        List<CrossReference> properties = new ArrayList<CrossReference>();
        for ( DbReference dbref : participant.getInteractor().getXref().getSecondaryRef() ) {
            String id, db, text = null;

            id = dbref.getId();
            db = dbref.getDb();

            if( finder!= null && finder.isOntologySupported( db ) ) {
                text = finder.getNameByIdentifier( id );
            }

            if ( dbref.getRefTypeAc() == null ) {
                properties.add( new CrossReferenceImpl( db, id, text ) );
            } else {
                if ( !dbref.getRefTypeAc().equals( IDENTITY_MI_REF ) ) {
                    properties.add( new CrossReferenceImpl( db, id, text ) );
                }
            }
        }
        return properties;
    }*/
    private List<CrossReference> extractProperties(Participant participant) throws IOException {
        List<CrossReference> properties = new ArrayList<CrossReference>();
        for (DbReference dbref : participant.getInteractor().getXref().getAllDbReferences()) {
            String id, db, text = null;

            id = dbref.getId();
            db = dbref.getDb();

            if (finder != null && finder.isOntologySupported(db)) {
                text = finder.getNameByIdentifier(id);
            } else {
                if (dbref.getRefType() != null) {
                    text = dbref.getRefType();
                } else if (dbref.getSecondary() != null) {
                    text = dbref.getSecondary();
                }
            }
            properties.add(new CrossReferenceImpl(db, id, text));
        }
        return properties;
    }

}
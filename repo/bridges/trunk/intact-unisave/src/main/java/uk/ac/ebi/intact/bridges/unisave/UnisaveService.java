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
package uk.ac.ebi.intact.bridges.unisave;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.uniprot.unisave.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

/**
 * UniSave client that accesses the UniSave web service.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.3
 */
public class UnisaveService {

    private static final Log log = LogFactory.getLog( UnisaveService.class );

    private UnisavePortType unisavePortType;

    public UnisaveService() {
        unisavePortType = new Unisave().getUnisave();
    }

    /**
     * Searches for all entry version given its identifier.
     *
     * @param identifier  the identifier of the entry we are interested in.
     * @param isSecondary
     *
     * @return list of all versions of the given entry from the most recent to the oldest.
     *
     * @throws UnisaveServiceException if the identifier cannot be found in UniSave.
     */
    public List<EntryVersionInfo> getVersions( String identifier, boolean isSecondary ) throws UnisaveServiceException {
        final VersionInfo versionInfo;
        try {
            versionInfo = unisavePortType.getVersionInfo( identifier, isSecondary );
        } catch ( Exception e ) {
            throw new UnisaveServiceException( e.getMessage(), e );
        }
        final List<EntryVersionInfo> list = versionInfo.getEntryVersionInfo();
        if( list.isEmpty() ) {
            throw new UnisaveServiceException( "Failed to find any version for "+ (isSecondary?"secondary":"primary") +" identifier " + identifier );
        }
        return list;
    }

    public String getLastSequenceAtTheDate(String identifier, boolean isSecondary, Date date) throws UnisaveServiceException {

        if (date == null){
            throw new IllegalArgumentException("The date cannot be null.");
        }

        List<EntryVersionInfo> listOfVersions = getVersions(identifier, isSecondary);

        EntryVersionInfo lastEntryVersionBeforeDate = null;

        try {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(date);
            XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

            for (EntryVersionInfo version : listOfVersions){
                XMLGregorianCalendar calendarRelease = version.getReleaseDate();

                if (DatatypeConstants.LESSER == calendarRelease.compare(date2) || DatatypeConstants.EQUAL == calendarRelease.compare(date2)){
                    if (lastEntryVersionBeforeDate == null){
                        lastEntryVersionBeforeDate = version;
                    }
                    else if(DatatypeConstants.GREATER == calendarRelease.compare(lastEntryVersionBeforeDate.getReleaseDate())){
                        lastEntryVersionBeforeDate = version;
                    }
                }

            }

        } catch (DatatypeConfigurationException e) {
            throw new UnisaveServiceException("The date " + date.toString() + " cannot be converted into XMLGregorianCalendar.", e);
        }

        if (lastEntryVersionBeforeDate == null){
            return null;
        }

        FastaSequence fasta = getFastaSequence(lastEntryVersionBeforeDate);
        return fasta.getSequence();
    }

    public String getSequenceFor(String identifier, boolean isSecondary, int sequenceVersion) throws UnisaveServiceException {

        List<EntryVersionInfo> listOfVersions = getVersions(identifier, isSecondary);

        for (EntryVersionInfo versionInfo : listOfVersions){
            if (versionInfo.getSequenceVersion() == sequenceVersion){
                FastaSequence fasta = getFastaSequence(versionInfo);
                return fasta.getSequence();
            }
        }

        return null;
    }

    /**
     * Get the map of sequences (and their sequence version in uniprot) existing in unisave before this date
     * @param identifier
     * @param isSecondary
     * @param date
     * @return
     * @throws UnisaveServiceException
     */
    public Map<Integer, String> getAllSequencesBeforeDate(String identifier, boolean isSecondary, Date date) throws UnisaveServiceException {

        if (date == null){
            throw new IllegalArgumentException("The date cannot be null.");
        }

        List<EntryVersionInfo> listOfVersions = getVersions(identifier, isSecondary);
        Map<Integer, String> oldSequences = new HashMap<Integer, String>();

        try {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(date);
            XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

            for (EntryVersionInfo version : listOfVersions){
                XMLGregorianCalendar calendarRelease = version.getReleaseDate();

                if (DatatypeConstants.LESSER == calendarRelease.compare(date2) || DatatypeConstants.EQUAL == calendarRelease.compare(date2)){
                    if (!oldSequences.keySet().contains(version.getSequenceVersion())){
                        FastaSequence fasta = getFastaSequence(version);
                        oldSequences.put(version.getSequenceVersion(), fasta.getSequence());
                    }
                }

            }

        } catch (DatatypeConfigurationException e) {
            throw new UnisaveServiceException("The date " + date.toString() + " cannot be converted into XMLGregorianCalendar.", e);
        }

        return oldSequences;
    }

    /**
     * Retrieve a fasta sequence corresponding to a given EntryVersion.
     *
     * @param version the version for which we want the sequence
     * @return a fasta sequence.
     * @throws UnisaveServiceException if the version given doesn't have an entryId that can be found in UniSave.
     */
    public FastaSequence getFastaSequence( EntryVersionInfo version ) throws UnisaveServiceException {
        final Version fastaVersion;
        try {
            fastaVersion = unisavePortType.getVersion( version.getEntryId(), true );
        } catch ( Exception e ) {
            throw new UnisaveServiceException( "Failed upon trying to get FASTA sequence for entry id " + version.getEntryId(), e );
        }
        final String entry = fastaVersion.getEntry();
        final String[] array = entry.split( "\n" );
        if ( array.length < 2 ) {
            throw new IllegalStateException( "expected to receive a fasta format: " + entry );
        }
        String header = array[0];
        if ( header.startsWith( ">" ) ) {
            header = header.substring( 1 );
        } else {
            throw new IllegalStateException( "Bad FASTA header format: " + header );
        }

        String sequence;
        if ( array.length > 2 ) {
            // optimized buffer size considering that all sub sequence are of equal length.
            StringBuilder sb = new StringBuilder( ( array.length - 1 ) * array[1].length() );
            for ( int i = 1; i < array.length; i++ ) {
                String seq = array[i];
                sb.append( seq );
            }
            sequence = sb.toString();
        } else {
            sequence = array[1];
        }

        return new FastaSequence( header, sequence );
    }

    /**
     * Returns the sequence version of a sequence for a certain uniprot ac.
     * Returns -1 if the sequence cannot be found for this uniprot ac
     * @param identifier
     * @param isSecondary
     * @param sequence
     * @return
     * @throws UnisaveServiceException
     */
    public int getSequenceVersion(String identifier, boolean isSecondary, String sequence) throws UnisaveServiceException{

        // 1. get all versions ordered from the most recent to the oldest
        if ( log.isDebugEnabled() ) {
            log.debug( "Collecting version(s) for entry by " + ( isSecondary ? "secondary" : "primary" ) + " ac: " + identifier );
        }

        final List<EntryVersionInfo> versions = getVersions( identifier, isSecondary );

        if ( log.isDebugEnabled() ) {
            log.debug( "Found " + versions.size() + " version(s)" );
        }

        // 3. for each protein sequence version
        int currentSequenceVersion = -1;

        for ( EntryVersionInfo versionInfo : versions) {

            FastaSequence fasta = getFastaSequence(versionInfo);

            if ( fasta.getSequence().equalsIgnoreCase(sequence) ) {
                currentSequenceVersion = versionInfo.getSequenceVersion();
                break;
            } // if
        } // versions

        return currentSequenceVersion;
    }

    /**
     * Collects all available sequence update fro mUniSave.
     *
     * @param identifier  identifier of the uniprot entry
     * @param isSecondary is the identifier secondary ?
     * @param sequence    sequence for which we want the subsequent updates
     * @return a non null ordered list. If the given sequence is found in the entry history, this sequence at least is
     *         returned. If there were say, 2 updates since that protein sequence, the list would contain 3 versions.
     *         If we fail to find a match for the given sequence in UniSave, the list would contain all existing
     *         sequence update available.
     *         The list of ordered from the oldest to the most recent sequence.
     *
     * @throws UnisaveServiceException if the identifier cannot be found in UniSave.
     */
    public List<SequenceVersion> getAvailableSequenceUpdate( String identifier, boolean isSecondary, String sequence ) throws UnisaveServiceException {

        LinkedList<SequenceVersion> sequenceUpdates = new LinkedList<SequenceVersion>();

        // 1. get all versions ordered from the most recent to the oldest
        if ( log.isDebugEnabled() ) {
            log.debug( "Collecting version(s) for entry by " + ( isSecondary ? "secondary" : "primary" ) + " ac: " + identifier );
        }

        final List<EntryVersionInfo> versions = getVersions( identifier, isSecondary );

        if ( log.isDebugEnabled() ) {
            log.debug( "Found " + versions.size() + " version(s)" );
        }

        // 3. for each protein sequence version
        int currentSequenceVersion = -1;

        boolean done = false;
        for ( Iterator<EntryVersionInfo> iterator = versions.iterator(); iterator.hasNext() && !done; ) {
            EntryVersionInfo version = iterator.next();

            if ( log.isDebugEnabled() ) {
                log.debug( "Processing version entry: " + version.getEntryId() + " sequence version " + version.getSequenceVersion() );
            }

            if ( currentSequenceVersion == -1 || currentSequenceVersion != version.getSequenceVersion() ) {
                currentSequenceVersion = version.getSequenceVersion();

                if ( log.isDebugEnabled() ) {
                    log.debug( "Retrieving FASTA sequence for sequence version " + currentSequenceVersion );
                }

                final FastaSequence fastaSequence = getFastaSequence( version );

                if ( log.isDebugEnabled() ) {
                    log.debug( "Sequence length retrieved: " + fastaSequence.getSequence().length() );
                }

                // check that the sequence is different from the one previously added (if any), if so add it
                boolean addSequence = false;
                if ( !sequenceUpdates.isEmpty() ) {
                    final String previousSequence = sequenceUpdates.iterator().next().getSequence().getSequence();
                    if ( !previousSequence.equals( fastaSequence.getSequence() ) ) {
                        final int d = StringUtils.getLevenshteinDistance( previousSequence, fastaSequence.getSequence() );
                        if ( log.isDebugEnabled() ) {
                            log.debug( "Levenshtein distance was: " + d );
                        }
                        addSequence = true;
                    }
                } else {
                    // that's the first one
                    addSequence = true;
                }

                if ( addSequence ) {
                    // add at beginning so the first element if the given sequence, the followings reflecting subsequence updates.
                    sequenceUpdates.addFirst( new SequenceVersion( fastaSequence, currentSequenceVersion ) );
                }

                if ( log.isDebugEnabled() ) {
                    log.debug( "Adding this sequence to the list of updates available" );
                }

                if ( fastaSequence.getSequence().equals( sequence ) ) {
                    // 3b. if is the same as the sequence provided, stop and return the list of update available from UniSave
                    if ( log.isDebugEnabled() ) {
                        log.debug( "The given sequence matches version " + currentSequenceVersion + ". Abort processing and return current list (size: " + sequenceUpdates.size() + ")" );
                    }

                    done = true;
                }
            } // if
        } // versions

        return sequenceUpdates;
    }
}

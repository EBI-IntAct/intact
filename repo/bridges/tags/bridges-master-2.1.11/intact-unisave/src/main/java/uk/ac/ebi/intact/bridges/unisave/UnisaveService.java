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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private String unisaveUrlRestJson = "http://www.ebi.ac.uk/uniprot/unisave/rest";
    private DefaultHttpClient httpClient = new DefaultHttpClient();

    public UnisaveService() {

    }

    private Object getDataFromWebService(String query){
        HttpGet request = new HttpGet(query);
        request.addHeader("accept", "application/json");
        try {
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }
            return JSONValue.parse(new BufferedReader(new InputStreamReader((response.getEntity().getContent()))));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String buildQuery(Type type, String id, String version) throws UnisaveServiceException {
        StringBuilder builder = new StringBuilder().append(this.unisaveUrlRestJson);
        switch (type){
            case ENTRIES:
                builder.append(Type.ENTRIES.valueOf()).append(id);
                break;
            case ENTRY_VERSION:
                if(version != null){
                    builder.append(Type.ENTRY_VERSION.valueOf()).append(id).append("/").append(version);
                }
                else {
                    throw new UnisaveServiceException("To use the " + Type.ENTRY_VERSION.valueOf() +
                            " method in the Rest you have to provide a not null version");
                }
                break;
            case ENTRIES_INFO:
                builder.append(Type.ENTRIES_INFO.valueOf()).append(id);
                break;
            case ENTRY_INFO_VERSION:
                if (version != null){
                    builder.append(Type.ENTRY_INFO_VERSION.valueOf()).append(id).append("/").append(version);
                }
                else {
                    throw new UnisaveServiceException("To use the " + Type.ENTRY_INFO_VERSION.valueOf() +
                            " method in the Rest you have to provide a not null version");
                }
                break;
            default:
                return null;
        }
        return builder.toString();
    }

    private String getSequence(String content) {
        content = content.substring(content.lastIndexOf("SEQUENCE"));
        content = content.substring(content.indexOf("\n")).trim();
        content = content.replaceAll(" ", "");
        content = content.replaceAll("\n", "");
        return content.replaceAll("\t", "").replaceAll("//","");
    }


    private String getFastHeader(String content) {
        int init = content.indexOf("FT ");
        int last = content.lastIndexOf("FT ");
        if(init == -1) return null;
        last = content.indexOf("\n", last); //real last
        return content.substring(init, last);
    }

    private String getContentForSequenceVersion(String identifier, int sequence_version) throws UnisaveServiceException {
        JSONArray array = (JSONArray) getDataFromWebService(buildQuery(Type.ENTRIES, identifier, null));
        JSONObject jo = null;
        for(int i = 0; i < array.size(); ++i){
            jo = (JSONObject) array.get(i);
            if (sequence_version == Integer.valueOf(String.valueOf(jo.get("sequence_version")))){
                return (String)jo.get("content");
            }
        }
        return null;
    }

    public String getSequenceFor(String identifier, boolean isSecondary, int sequence_version) throws UnisaveServiceException{
        String content = getContentForSequenceVersion(identifier, sequence_version);
        if (content != null) return getSequence(content);
        else return null;
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
    public List<Integer> getVersions( String identifier, boolean isSecondary ) throws UnisaveServiceException {
        JSONArray array = (JSONArray) getDataFromWebService(buildQuery(Type.ENTRIES_INFO, identifier, null));
        List<Integer> list = new ArrayList<Integer>();
        JSONObject jo = null;
        for(int i = 0; i < array.size(); ++i) {
            jo = (JSONObject) array.get(i);
            list.add(Integer.valueOf(String.valueOf(jo.get("entry_version"))));
        }

        if( list.isEmpty() ) {
            throw new UnisaveServiceException( "Failed to find any version for "+ (isSecondary?"secondary":"primary") +" identifier " + identifier );
        }
        return list;
    }

    public String getLastSequenceAtTheDate(String identifier, boolean isSecondary, Date date) throws UnisaveServiceException {

        if (date == null){
            throw new IllegalArgumentException("The date cannot be null.");
        }

        JSONArray array = (JSONArray) getDataFromWebService(buildQuery(Type.ENTRIES, identifier, null));
        JSONObject jo = null;
        DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        Date auxDate = null;
        int version = -1;
        for(int i = 0; i < array.size(); ++i) {
            jo = (JSONObject) array.get(i);
            try {
                auxDate = formatter.parse((String) jo.get("firstReleaseDate"));
                if(auxDate.before(date)){
                    return getSequence(String.valueOf(jo.get("content")));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            };
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

        Map<Integer, String> oldSequences = new HashMap<Integer, String>();
        JSONArray array = (JSONArray) getDataFromWebService(buildQuery(Type.ENTRIES, identifier, null));
        JSONObject jo = null;
        DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        Date auxDate = null;
        for(int i = 0; i < array.size(); ++i){
            jo = (JSONObject) array.get(i);
            try {
                auxDate = formatter.parse((String) jo.get("firstReleaseDate") );
                if(auxDate.before(date)){
                    oldSequences.put(Integer.valueOf(String.valueOf(jo.get("sequence_version"))),
                                    getSequence((String) jo.get("content")));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
    public FastaSequence getFastaSequence( String identifier, int version ) throws UnisaveServiceException {
        String content = getContentForSequenceVersion(identifier, version);
        if (content != null) return new FastaSequence( getFastHeader(content), getSequence(content) );
        else return null;
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
        JSONArray array = (JSONArray) getDataFromWebService(buildQuery(Type.ENTRIES, identifier, null));

        if ( log.isDebugEnabled() ) {
            log.debug( "Collecting version(s) for entry by " + ( isSecondary ? "secondary" : "primary" ) + " ac: " + identifier );
            log.debug( "Found " + array.size() + " version(s)" );
        }
        JSONObject jo = null;
        for(int i = 0; i < array.size(); ++i){
            jo = (JSONObject) array.get(i);
            String content = (String)jo.get("content");
            if (sequence.equalsIgnoreCase(getSequence(content))){
                return Integer.valueOf(String.valueOf(jo.get("sequence_version")));
            }
        }
        return -1;
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

        JSONArray array = (JSONArray) getDataFromWebService(buildQuery(Type.ENTRIES, identifier, null));
        JSONObject jo = null;
        int parameterSequenceVersion = getSequenceVersion(identifier, isSecondary, sequence);
        int currentSequenceVersion = -1;
        for(int i = 0 ; i < array.size() ; ++i){
            jo = (JSONObject) array.get(i);
            if(parameterSequenceVersion < Integer.parseInt(String.valueOf(jo.get("sequence_version")))){
                if(currentSequenceVersion != Integer.parseInt(String.valueOf(jo.get("sequence_version")))){
                    //New version of the sequence
                    currentSequenceVersion = Integer.parseInt(String.valueOf(jo.get("sequence_version")));
                    FastaSequence fastaSequence = getFastaSequence(identifier, currentSequenceVersion);
                    sequenceUpdates.add(new SequenceVersion(fastaSequence, currentSequenceVersion));
                }
            }
        }
        return sequenceUpdates;
    }

    private enum Type {
        ENTRIES ("/json/entries/"),
        ENTRY_VERSION ("/json/entry/"),
        ENTRIES_INFO ("/json/entryinfos/"),
        ENTRY_INFO_VERSION ("/json/entryinfo/");

        private final String value;

        Type(String value){ this.value = value;}

        private String valueOf(){return this.value;}

    }

}

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
package uk.ac.ebi.intact.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.Oracle9Dialect;
import uk.ac.ebi.intact.config.hibernate.SequenceAuxiliaryDatabaseObject;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * This class is responsible of creating database sequences that are not directly
 * configured in the model mappings, because it cannot do it that way.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SequenceManager {

    private static final Log log = LogFactory.getLog( SequenceManager.class );

    private Dialect dialect;

    public SequenceManager(DataConfig dataConfig) {
        Configuration config = (Configuration) dataConfig.getConfiguration();
        this.dialect = Dialect.getDialect(config.getProperties());
    }

    /**
     * Checks if a sequence exists.
     * @param entityManager The entity manager to use
     * @param sequenceName The name of the sequence
     * @return True if the sequence exists
     */
    public boolean sequenceExists(EntityManager entityManager, String sequenceName) {
        List<String> existingSequences = getExistingSequenceNames(entityManager);

        for (String existingSequence : existingSequences) {
            if (existingSequence.equalsIgnoreCase(sequenceName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates a sequence in the database, if it does not exist already. Uses the default initial value, which is 1.
     * @param entityManager The entity manager to use
     * @param sequenceName The name of the new sequence
     * @throws SequenceCreationException
     */
    public void createSequenceIfNotExists(EntityManager entityManager, String sequenceName) throws SequenceCreationException {
        createSequenceIfNotExists(entityManager, sequenceName, 1);
    }

    /**
     * Creates a sequence in the database, if it does not exist already.
     * @param entityManager The entity manager to use
     * @param sequenceName The name of the new sequence
     * @param initialValue The initial value of the sequence. This will be the first value given by the sequence
     * when the next value is invoked
     * @throws SequenceCreationException Will happen if there are problems creating the sequence in the database
     */
    public void createSequenceIfNotExists(EntityManager entityManager, String sequenceName, int initialValue) throws SequenceCreationException {
        if (!sequenceExists(entityManager, sequenceName)) {
            if (log.isInfoEnabled()) log.info("Sequence could not be found and it is going to be created: "+sequenceName);

            String sql = new SequenceAuxiliaryDatabaseObject(sequenceName, initialValue).sqlCreateString(dialect);

            try {
                final Query createSeqQuery = entityManager.createNativeQuery(sql);
                createSeqQuery.executeUpdate();
            } catch (Exception e) {
                throw new SequenceCreationException("Exception creating the sequence: "+sequenceName+" (initial value: "+initialValue+")");
            }
        }
    }

    /**
     * Gets the names of the existing sequences in the database.
     * @param entityManager The entity manager to use
     * @return the names of the sequences
     */
    public List<String> getExistingSequenceNames(EntityManager entityManager) {
        Query query ;

        if ((dialect instanceof Oracle8iDialect) || (dialect instanceof Oracle9Dialect)) {
            query = entityManager.createNativeQuery(getQuerySequencesString());
        } else {
            query = entityManager.createNativeQuery(dialect.getQuerySequencesString());
        }


        List<String> existingSequences = query.getResultList();
        return existingSequences;
    }

    /**
     * Gets the next value for the provided sequence
     * @param entityManager The entity manager to use
     * @param sequenceName The sequence name to query
     * @return The next value for that sequence; null if the sequence does not exist;
     */
    public Long getNextValueForSequence( EntityManager entityManager, String sequenceName ) {
        if ( !sequenceExists( entityManager, sequenceName ) ) {
            throw new IllegalArgumentException( "Sequence does not exist: " + sequenceName +
                                                ". Sequences found in the database: " + getExistingSequenceNames( entityManager ) );
        }

        Query query = entityManager.createNativeQuery( dialect.getSequenceNextValString( sequenceName ) );
        Object resultObject = query.getSingleResult();

        if ( resultObject != null ) {
            Number nextSeq = (Number) resultObject;
            return nextSeq.longValue();
        }
        
        return null;
    }

    /**
   * Returns the query sequence for oracle as 'select sequence_name  from user_sequences'
   * doesn't return any sequences unless logged as root user
   * @return the query
   */
  private String getQuerySequencesString() {
      return "select sequence_name from all_sequences";
  }

}

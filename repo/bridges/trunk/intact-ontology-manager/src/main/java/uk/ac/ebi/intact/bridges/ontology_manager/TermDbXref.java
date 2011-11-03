package uk.ac.ebi.intact.bridges.ontology_manager;

/**
 * Db cross references of a cv term
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>01/11/11</pre>
 */

public class TermDbXref {

    private String database;
    private String databaseId;
    private String accession;
    private String qualifier;
    private String qualifierId;

    public TermDbXref(String db, String accession, String qualifier){
        if (db == null){
            throw new IllegalArgumentException("The database cannot be null");
        }
        if (qualifier == null){
            throw new IllegalArgumentException("The db xref qualifier cannot be null");
        }
        if (accession == null){
            throw new IllegalArgumentException("The database accession cannot be null");
        }
        this.database = db;
        this.accession = accession;
        this.qualifier = qualifier;
    }

    public TermDbXref(String db, String dbAc, String accession, String qualifier, String qualifierAc){
        if (dbAc == null){
            throw new IllegalArgumentException("The database Id cannot be null");
        }
        if (qualifierAc == null){
            throw new IllegalArgumentException("The db xref qualifier ID cannot be null");
        }
        if (accession == null){
            throw new IllegalArgumentException("The database accession cannot be null");
        }
        this.database = db;
        this.databaseId = dbAc;
        this.accession = accession;
        this.qualifier = qualifier;
        this.qualifierId = qualifierAc;
    }

    public String getDatabase() {
        return database;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public String getAccession() {
        return accession;
    }

    public String getQualifier() {
        return qualifier;
    }

    public String getQualifierId() {
        return qualifierId;
    }
}

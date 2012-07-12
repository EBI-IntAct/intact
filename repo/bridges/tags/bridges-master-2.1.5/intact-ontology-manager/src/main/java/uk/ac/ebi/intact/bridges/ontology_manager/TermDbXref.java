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

    @Override
    public boolean equals(Object o){

        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof TermAnnotation ) ) {
            return false;
        }

        TermDbXref termXref = (TermDbXref) o;

        if (this.database != null){
            if (!this.database.equalsIgnoreCase(termXref.getDatabase())){
                return false;
            }
        }
        else if (termXref.getDatabase()!= null){
            return false;
        }

        if (this.databaseId != null){
            if (!this.databaseId.equalsIgnoreCase(termXref.getDatabaseId())){
                return false;
            }
        }
        else if (termXref.getDatabaseId() != null){
            return false;
        }

        if (this.accession != null){
            if (!this.accession.equalsIgnoreCase(termXref.getAccession())){
                return false;
            }
        }
        else if (termXref.getAccession() != null){
            return false;
        }

        if (this.qualifier != null){
            if (!this.qualifier.equalsIgnoreCase(termXref.getQualifier())){
                return false;
            }
        }
        else if (termXref.getQualifier()!= null){
            return false;
        }

        if (this.qualifierId != null){
            if (!this.qualifierId.equalsIgnoreCase(termXref.getQualifierId())){
                return false;
            }
        }
        else if (termXref.getQualifierId() != null){
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = this.database != null ? this.database.hashCode() : 0;

        result = result * 31 + this.databaseId != null ? this.databaseId.hashCode() : 0;
        result = result * 31 + this.accession != null ? this.accession.hashCode() : 0;
        result = result * 31 + this.qualifier != null ? this.qualifier.hashCode() : 0;
        result = result * 31 + this.qualifierId != null ? this.qualifierId.hashCode() : 0;

        return result;
    }
}

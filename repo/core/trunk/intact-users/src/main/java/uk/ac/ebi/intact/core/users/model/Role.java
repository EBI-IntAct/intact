package uk.ac.ebi.intact.core.users.model;

import org.apache.commons.lang.StringUtils;

import javax.persistence.*;

/**
 * A role that can be assigned to a user.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@Entity
@Table( name = "ia_role" )
@javax.persistence.SequenceGenerator( name="SEQ_USER", sequenceName="my_sequence", initialValue = 1 )
public class Role {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="SEQ_USER")
    private long pk;

    @Column( unique = true, nullable = false )
    private String name;

    //////////////////
    // Constructors
    
    protected Role() {
    }

    public Role( String name ) {
        if ( name == null || name.trim().length() == 0 ) {
            throw new IllegalArgumentException( "You must give a non empty/null name" );
        }

        this.name = name.trim().toUpperCase();
    }

    ///////////////////////////
    // Getters and Setters

    public String getName() {
        return name;
    }

    //////////////////////////
    // Object's override

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Role ) ) return false;

        Role role = ( Role ) o;

        if ( !name.equals( role.name ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "Role" );
        sb.append( "{name='" ).append( name ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}

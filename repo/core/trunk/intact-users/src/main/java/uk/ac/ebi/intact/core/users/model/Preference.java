package uk.ac.ebi.intact.core.users.model;

import org.apache.commons.lang.StringUtils;

import javax.persistence.*;

/**
 * A user preference.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@Entity
@Table( name = "ia_preference" )
@javax.persistence.SequenceGenerator(
    name="SEQ_STORE",
    sequenceName="my_sequence"
)
public class Preference {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_STORE")
    private long pk;

    @Basic
    private String key;

    @Basic
    private String value;

    @ManyToOne( targetEntity = User.class )
    @JoinColumn( name = "user_id" )
    private User user;

    //////////////////
    // Constructors

    public Preference( String key ) {
        if ( StringUtils.isEmpty(key) ) {
            throw new IllegalArgumentException( "You must give a non empty/null key" );
        }

        this.key = key;
    }

    ///////////////////////////
    // Getters and Setters

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public User getUser() {
        return user;
    }

    public void setUser( User user ) {
        this.user = user;
    }

    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Preference ) ) return false;

        Preference that = ( Preference ) o;

        if ( !key.equals( that.key ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "Preference" );
        sb.append( "{key='" ).append( key ).append( '\'' );
        sb.append( ", value='" ).append( value ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}

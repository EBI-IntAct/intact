package uk.ac.ebi.intact.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * New class which factors out owner and evidences from IntactObject.
 * This allows objects which do not have an owner (eg: Institution)
 * to inherit from IntactObject.
 *
 * @author intact team
 * @version $Id$
 */
public abstract class BasicObjectImpl extends IntactObjectImpl implements BasicObject {

    // TODO: synchron? this should moved somewhere else.
    private String ownerAc;

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments + plural name
     */
    private Collection evidences = new ArrayList();

    /**
     * TODO comments
     */
    private Institution owner;


    /**
     * Protected constructor for use by subclasses
     */
    protected BasicObjectImpl() {
        super();
    }

    protected BasicObjectImpl( Institution owner ) {

        this();
        setOwner( owner );
    }


    ///////////////////////////////////////
    // access methods for associations

    public void setEvidences( Collection someEvidence ) {
        this.evidences = someEvidence;
    }

    public Collection getEvidences() {
        return evidences;
    }

    public void addEvidence( Evidence evidence ) {
        if( !this.evidences.contains( evidence ) ) this.evidences.add( evidence );
    }

    public void removeEvidence( Evidence evidence ) {
        this.evidences.remove( evidence );
    }

    public Institution getOwner() {
        return owner;
    }

    public void setOwner( Institution institution ) {

        if( institution == null ) {
            throw new NullPointerException( "valid " + getClass().getName() + " must have an owner (Institution) !" );
        }

        this.owner = institution;
        // TODO: synchron ?
        this.ownerAc = institution.getAc();
    }

    ///////////////////////////////////////
    // access methods for associations

    public String getOwnerAc() {
        return ownerAc;
    }

    public void setOwnerAc( String ac ) {
        this.ownerAc = ac;
    }


    ///////////////////////////////////////
    // instance methods

    public String toString() {
        return super.toString() + "; owner=" + owner.getAc();
    }
}
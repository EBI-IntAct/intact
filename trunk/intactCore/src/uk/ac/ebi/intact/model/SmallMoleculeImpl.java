/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class SmallMoleculeImpl extends InteractorImpl implements SmallMolecule, Editable{
    public SmallMoleculeImpl(){
    }

    public SmallMoleculeImpl( String shortLabel, Institution owner, CvInteractorType type ) {
        super( shortLabel, owner, type );
    }
}

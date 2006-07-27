/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21-Jul-2006</pre>
 */
@Entity
@DiscriminatorValue("BioSource")
public class InteractorXref extends Xref
{

    private static final Log log = LogFactory.getLog(InteractorXref.class);


    public InteractorXref()
    {
    }

    public InteractorXref(Institution anOwner, CvDatabase aDatabase, String aPrimaryId, String aSecondaryId, String aDatabaseRelease, CvXrefQualifier aCvXrefQualifier)
    {
        super(anOwner, aDatabase, aPrimaryId, aSecondaryId, aDatabaseRelease, aCvXrefQualifier);
    }

    public InteractorXref(Institution anOwner, CvDatabase aDatabase, String aPrimaryId, CvXrefQualifier aCvXrefQualifier)
    {
        super(anOwner, aDatabase, aPrimaryId, aCvXrefQualifier);
    }
      /*
    @ManyToOne(targetEntity = InteractorImpl.class)
    @JoinColumn(name = "parent_ac")
    public AnnotatedObject getParent()
    {
        return super.getParent();
    }    */

}

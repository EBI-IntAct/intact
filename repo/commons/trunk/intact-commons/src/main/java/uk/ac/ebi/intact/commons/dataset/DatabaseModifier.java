package uk.ac.ebi.intact.commons.dataset;

/**
 * This is an interface for some classes that modify the database during the creation of TestDatasets
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface DatabaseModifier
{
    void modifyDatabase();
}

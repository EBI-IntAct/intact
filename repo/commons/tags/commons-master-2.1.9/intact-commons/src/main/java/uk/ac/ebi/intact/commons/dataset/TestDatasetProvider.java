package uk.ac.ebi.intact.commons.dataset;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Deprecated
public interface TestDatasetProvider<T extends TestDataset>
{
    T getTestDataset(String id);
}
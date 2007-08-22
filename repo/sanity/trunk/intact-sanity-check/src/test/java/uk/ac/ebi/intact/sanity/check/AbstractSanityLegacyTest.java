package uk.ac.ebi.intact.sanity.check;

import org.junit.Before;
import uk.ac.ebi.intact.core.unit.IntactAbstractTestCase;
import uk.ac.ebi.intact.sanity.check.config.Curator;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;
import uk.ac.ebi.intact.sanity.check.config.SuperCurator;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractSanityLegacyTest extends IntactAbstractTestCase {

    private SanityCheckConfig sanityCheckConfig;

    @Before
    public void beforeSanityCheck() throws Exception {
        List<Curator> curators = new ArrayList<Curator>();

        SuperCurator superJohn = new SuperCurator(100, "John");
        superJohn.setAdmin(true);

        Curator curatorAnne = new Curator("Anne");
        Curator curatorSa = new Curator("sa");

        curators.add(superJohn);
        curators.add(curatorAnne);
        curators.add(curatorSa);

        this.sanityCheckConfig = new SanityCheckConfig(curators);

        sanityCheckConfig.setEditorUrl("http://www.ebi.ac.uk/intact/editor");
        sanityCheckConfig.setEmailSubjectPrefix("[TEST] ");
    }

    public final SanityCheckConfig getSanityCheckConfig() {
        return sanityCheckConfig;
    }
}
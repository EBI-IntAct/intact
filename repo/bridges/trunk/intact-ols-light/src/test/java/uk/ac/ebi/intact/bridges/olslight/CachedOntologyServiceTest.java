package uk.ac.ebi.intact.bridges.olslight;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

/**
 * CachedOntologyService Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.1.4
 */
public class CachedOntologyServiceTest {

    @Test
    public void getTermName() throws Exception {
        final OntologyService service = new CachedOntologyService( new OlsLightService() );

        Assert.assertEquals( "interaction detection method", service.getTermName( OntologyId.PSI_MI, "MI:0001" ) );
        Assert.assertEquals( "growth", service.getTermName( OntologyId.GENE_ONTOLOGY, "GO:0040007" ) );
        Assert.assertEquals( "acylated residue", service.getTermName( OntologyId.PSI_MOD, "MOD:00649" ) );
        Assert.assertEquals( "Homo sapiens (Human)", service.getTermName( OntologyId.TAXONOMY, "9606" ) );
    }

    @Test
    public void getTermDirectChildren() throws Exception {
        final OntologyService service = new CachedOntologyService( new OlsLightService() );
        Map<String, String> children;

        children = service.getTermDirectChildren( OntologyId.PSI_MI, "MI:0954" );
        Assert.assertEquals( 2, children.size() );

        children = service.getTermDirectChildren( OntologyId.GENE_ONTOLOGY, "GO:0016209" );
        Assert.assertEquals( 6, children.size() );
    }

    @Test
    @Ignore
    public void getTermChildren() throws Exception {
        final OntologyService service = new CachedOntologyService( new OlsLightService() );
        Map<String, String> children;

        // curation quality (MI:0954)
        //    |
        //    |- curation coverage
        //    |    |- full coverage
        //    |    |- partial coverage
        //    |- curation depth
        //    |    |- imex curation
        //    |    |- mimix curation
        //    |    |- rapid curation

        // note: it doesn't include the parent term
        children = service.getTermChildren( OntologyId.PSI_MI, "MI:0954", 1 );
        Assert.assertEquals( 2, children.size() );

        children = service.getTermChildren( OntologyId.PSI_MI, "MI:0954", 2 );
        // currently a bug in OLS as the children of 'curation depth' are not pulled (Jose is looking into it)
        Assert.assertEquals( 7, children.size() );
    }
}

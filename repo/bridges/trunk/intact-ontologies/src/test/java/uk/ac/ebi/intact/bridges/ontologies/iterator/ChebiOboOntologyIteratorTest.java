package uk.ac.ebi.intact.bridges.ontologies.iterator;

import org.junit.Assert;
import org.junit.Test;
import org.obo.dataadapter.OBOParseException;

import java.io.IOException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: ntoro
 * Date: 12/11/2012
 * Time: 10:40
 * To change this template use File | Settings | File Templates.
 */
public class ChebiOboOntologyIteratorTest {

	@Test
	public void testProcessFile() throws IOException, OBOParseException {
		URL url = ChebiOboOntologyIteratorTest.class.getResource("/META-INF/chebi196.obo");

		OboOntologyIterator iterator = new OboOntologyIterator("chebi",url);

		int count = 0;

		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}

		Assert.assertEquals(96185, count);
	}

	@Test
	public void testProcessFileLatestVersion() throws IOException, OBOParseException {
		URL url = ChebiOboOntologyIteratorTest.class.getResource("/META-INF/chebi197.obo");

		OboOntologyIterator iterator = new OboOntologyIterator("chebi",url);

		int count = 0;

		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}

		Assert.assertEquals(96185, count);
	}
}

package uk.ac.ebi.intact.application.graph2MIF;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.application.graph2MIF.GraphNotConvertableException;
import uk.ac.ebi.intact.application.graph2MIF.NoGraphRetrievedException;
import uk.ac.ebi.intact.application.graph2MIF.NoInteractorFoundException;
import uk.ac.ebi.intact.application.graph2MIF.MIFSerializeException;


/**
 *  Graph2MIFWS Interface
 *
 *  This is the interface of The graph2MIF-WebService.
 *  Implementation in Graph2MIFWSService.java
 *
 *  @author Henning Mersch <hmersch@ebi.ac.uk>
 */
public interface Graph2MIFWS {

    /**
	 * getMIF is the only method which is necessary.
	 * @param ac String ac in IntAct
	 * @param depth Integer of the depth the graph should be expanded
	 * @return String including a XML-Document in PSI-MIF-Format
	 * @exception IntactException thrown if search for interactor failed
	 * @exception GraphNotConvertableException thrown if Graph failed requirements of MIF.
	 * @exception DataSourceException thrown if could not retrieve graph from interactor
	 * @exception NoGraphRetrievedException thrown if DOM-Object could not be serialized
	 * @exception MIFSerializeException thrown if IntactHelper could not be created
	 * @exception NoInteractorFoundException thrown if no Interactor found for ac
	 */
    String getMIF(String ac, Integer depth, Boolean strictmif)
	       throws IntactException,
	              GraphNotConvertableException,
				  DataSourceException,
				  NoGraphRetrievedException,
				  MIFSerializeException,
                  NoInteractorFoundException;

}

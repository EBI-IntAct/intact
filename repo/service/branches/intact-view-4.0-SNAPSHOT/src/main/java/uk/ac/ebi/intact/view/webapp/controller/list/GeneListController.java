package uk.ac.ebi.intact.view.webapp.controller.list;

import org.apache.myfaces.orchestra.conversation.annotations.ConversationName;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/08/12</pre>
 */
@Controller
@Scope( "conversation.access" )
@ConversationName( "general" )
public class GeneListController extends InteractorListController {
}

/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.persistence.SearchException;
import uk.ac.ebi.intact.application.hierarchView.business.graph.InteractionNetwork;
import uk.ac.ebi.intact.application.hierarchView.business.image.ImageBean;

import javax.servlet.http.HttpSessionBindingListener;
import java.util.Collection;
import java.io.Serializable;

/**
 * This interface stores information about an Intact Web user session. Instead of
 * binding multiple objects, only an object of this class is bound to a session,
 * thus serving a single access point for multiple information.
 * <p>
 * This class implements the <tt>HttpSessionBindingListener</tt> interface for it
 * can be notified of session time outs.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */
public interface IntactUserIF extends Serializable, HttpSessionBindingListener {

    public String getAC();
    public String getDepth();
    public boolean getHasNoDepthLimit();
    public String getMethodLabel();
    public String getMethodClass();
    public String getBehaviour();
    public InteractionNetwork getInteractionNetwork();
    public ImageBean getImageBean();
    public Collection getKeys();

    public void setAC(String AC);
    public void setDepth (String depth);
    public void setHasNoDepthLimit (boolean flag);
    public void setMethodLabel (String methodLabel);
    public void setMethodClass (String methodClass);
    public void setBehaviour(String behaviour);
    public void setInteractionNetwork(InteractionNetwork in);
    public void setImageBean(ImageBean imageBean);
    public void setKeys(Collection keys);


    /**
     * Set the default value of user's data
     */
    public void initUserData ();

    /**
     * Allows the user to retreive a collection of matching IntAct object
     * according to a criteria given in parameter.
     *
     * @param objectType  object type you want to retreive
     * @param searchParam the field you want to query on
     * @param searchValue the value you are looking for
     * @return a collection of <i>objectType</i> object
     * @throws SearchException in case the search fail
     */
    public Collection search (String objectType,
                              String searchParam,
                              String searchValue) throws SearchException ;


    /**
     * Returns a subgraph centered on startNode.
     * The subgraph will contain all nodes which are up to graphDepth interactions away from startNode.
     * Only Interactions which belong to one of the Experiments in experiments will be taken into account.
     * If experiments is empty, all Interactions are taken into account.
     *
     * Graph depth:
     * This parameter limits the size of the returned interaction graph. All baits are shown with all
     * the interacting preys, even if they would normally be on the "rim" of the graph.
     * Therefore the actual diameter of the graph may be 2*(graphDepth+1).
     *
     * Expansion:
     * If an Interaction has more than two interactors, it has to be defined how pairwise interactions
     * are generated from the complex data. The possible values are defined in the beginning of this file.
     *
     * @param startNode - the start node of the subgraph.
     * @param graphDepth - depth of the graph
     * @param experiments - Experiments which should be taken into account
     * @param complexExpansion - Mode of expansion of complexes into pairwise interactions
     *
     * @return a InteractionNetwork object.
     *
     * @exception IntactException - thrown if problems are encountered
     */
    public InteractionNetwork subGraph (Interactor startNode,
                                        int graphDepth,
                                        Collection experiments,
                                        int complexExpansion) throws IntactException ;

} // IntactUser

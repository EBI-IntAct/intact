/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.simpleGraph;

import uk.ac.ebi.intact.model.Component;

import java.util.Map;

/** An edge in the binary graph representation of an Interaction network.
 */

public interface EdgeI extends BasicGraphI {

    public void setNode1(NodeI aNode);

    public NodeI getNode1();

    public void setNode2(NodeI aNode);

    public NodeI getNode2();

    public void setComponent1(Component aComponent);

    public Component getComponent1();

    public void setComponent2(Component aComponent);

    public Component getComponent2();


}

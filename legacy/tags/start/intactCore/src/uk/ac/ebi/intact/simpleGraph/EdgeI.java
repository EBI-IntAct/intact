/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.simpleGraph;

import java.util.Map;

public interface EdgeI extends BasicGraphI {

    public void setNode1(NodeI aNode);

    public NodeI getNode1();

    public void setNode2(NodeI aNode);

    public NodeI getNode2();

}

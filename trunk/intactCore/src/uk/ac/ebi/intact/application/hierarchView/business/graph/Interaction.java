package uk.ac.ebi.intact.application.hierarchView.business.graph;

import uk.ac.ebi.intact.simpleGraph.*;

public class Interaction extends Edge {

  private Protein protein1;
  private Protein protein2;

  public Interaction (Protein p1, Protein p2)
  {
    this.protein1 = p1;
    this.protein2 = p2;
  }

  public String getId()
  {
    return null;
  }

  public void setId(String anId){}

  public String getLabel()
  {
    return null;
  }
  
  public void setLabel(String aLabel){}

  public void setNode1(NodeI aNode)
  {
    this.protein1 = (Protein) aNode;
  }

  public NodeI getNode1()
  {
    return (NodeI) protein1;
  }

  public void setNode2(NodeI aNode)
  {
    this.protein2 = (Protein) aNode;
  }

  public NodeI getNode2()
  {
    return (NodeI) protein2;
  }


} // Interaction

package uk.ac.ebi.intact.application.hierarchView.business.tulip.webService;

/**
  * Purpose : <br>
  * Allows to store for a single protein its coordinates. 
  *
  * @author Samuel KERRIEN (skerrien@ebi.ac.uk)
  */

public class ProteinCoordinate implements java.io.Serializable {

  /* -------------------------------------------------------- Instance Variables */

  /**
   * the protein's id
   */
  private int id;

  /**
   * protein's coordinates
   */
  private float x;
  private float y;


  /** -------------------------------------------------------- Constructors */

  public ProteinCoordinate (int id, float x, float y) {
    this.id = id;
    this.x  = x;
    this.y  = y;
  }

  public ProteinCoordinate (int id) {
    this.id = id;    
  }


  /** -------------------------------------------------------- Methods - Accessors */

  public int   getId () {return this.id; }
  public float getX ()  {return this.x;  }
  public float getY ()  {return this.y;  }

  public void setId (int anId) {this.id = anId;}
  public void setX  (float x) {this.x = x;}
  public void setY  (float y) {this.y = y;}

} // ProteinCoordinate












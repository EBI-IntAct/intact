package uk.ac.ebi.intact.application.hierarchView.business.graph;


public class ImageDimension
{
  // ---------------------------------------------------------------- Constants
  public static float DEFAULT_BORDER = 5f;

  // ---------------------------------------------------------------- Instance Variables
  private float xmin;
  private float xmax;
  private float ymin;
  private float ymax;
  private float border;

  // ---------------------------------------------------------------- Constructors
  /**
   * Create an ImageDimension with a specified border
   *
   * @param aBorder the border
   */
  public ImageDimension (float aBorder) {
    xmin = xmax = ymin = ymax = 0;
    border = aBorder;
  }
  
  public ImageDimension() {
    this (DEFAULT_BORDER);
  }

  // ---------------------------------------------------------------- Accessors
  public float length () { return xmax - xmin; }

  public float height () { return ymax - ymin; }

  public float xmin ()   { return xmin; }

  public float ymin ()   { return ymin; }

  public float border ()  { return border; }

  
  // ---------------------------------------------------------------- Other methods
  /**
   * Widen the size if the new coordinate is out of the usable space.
   * 
   * @param x the X coordinate
   * @param y the Y coordinate
   */
  public void adjust (float x, float y) {
    if (x < xmin) xmin = x;
    if (y < ymin) ymin = y;
    if (x > xmax) xmax = x;
    if (y > ymax) ymax = y;
  } // adjust


  /** 
   * AjusteCadre ajuste la taille en fonction de la taille des noeuds. 
   * Elle n'est efficace que si les noeuds on deja ete positionnés 
   *
   * @param length length of the conponent
   * @param height heifth of the component 
   * @param x x coordinate
   * @param y y coordinate
   */
  public void adjustCadre (float length, float height, float x, float y) {
    float tmp = 0;
    if ((tmp = x  - length/2) < xmin)
      xmin = tmp;
    if ((tmp = x  + length/2) > xmax)
      xmax = tmp;
    if ((tmp = y  - height/2) < ymin)
      ymin = tmp;
    if ((tmp = y  + height/2) > ymax)
      ymax = tmp;
  } // adjustCadre

} // ImageDimension

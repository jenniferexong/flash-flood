class Lotus implements Surface {
  // Constants
  public static final float IMG_WIDTH = 260;          // width of original image
  public static final float IMG_HEIGHT = 136;         // height of original image
  public static final float IMG_SURFACE = 60;         // distance to surface of lotus from top of image
  public static final float IMG_LEFT = 30;            // distance to left of lotus from left of image
  public static final float IMG_RIGHT = 240;          // distance to right of lotus from left of image
  public static final float IMG_BOT = 80;             // distance to bottom of lotus from top of image
  public static final float SCALE_DOWN = 1;           // amount to scale image down

  Animation animation;
  PVector pos, savedPos; // position of bottom left of image
  float vxSpeed = 1.2;

  /** Constructor */
  Lotus(float x, float y) {
    pos = new PVector(x, y);
    savedPos = new PVector(x, y);
    animation = new Animation("lotus-animation", "lotus-", 10, IMG_WIDTH, IMG_HEIGHT, 1);
  }

  /** Saves current position of lotus */
  void saveState() {
    savedPos = new PVector(pos.x, pos.y);
  }

  /** Sets position of lotus to the recently saved position */
  void previousState() {
    pos.set(savedPos.x, savedPos.y);
  }

  /** Updates the position of the lotus so it floats on the water and moves left */
  void update(Water water) { 
    animation.update();
    pos.x -= vxSpeed;
    if (water.riseRate != 0) 
      if (pos.x < - IMG_WIDTH)   pos.set(width, pos.y);
    pos.y -= water.riseRate;
  }

  void display() {
    animation.display(this);
  }

  void translateXY(float x, float y) {
    pos.set(pos.x + x, pos.y + y);
  }

  // Methods to return values
  float surface() {
    return(pos.y - IMG_BOT + IMG_SURFACE);
  }

  float bottom() {
    return(pos.y);
  }

  float left() {
    return(pos.x + IMG_LEFT);
  }

  float right() {
    return(pos.x + IMG_RIGHT);
  }

  boolean isEndPoint() { 
    return false;
  }
}

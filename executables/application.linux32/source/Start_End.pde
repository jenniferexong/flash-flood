/** The start point of the game - displayed as a small tree stump */
class Start implements Surface {

  public static final float IMG_WIDTH = 340;        // width of original image
  public static final float IMG_HEIGHT = 274;       // height of original image
  public static final float IMG_SURFACE = 50;       // height to surface of stump from top of image
  public static final float IMG_LEFT = 40;          // distance to left of stump from left of image
  public static final float IMG_RIGHT = 275;        // distance to right of stump from left of image
  public static final float IMG_BOT = 80;           // distance to bottom of stump from top of image
  public static final float SCALE_DOWN = 2;         // amount to scale down the image
  // Scaling values down
  public static final float SURFACE = IMG_SURFACE/SCALE_DOWN;
  public static final float WIDTH = IMG_WIDTH/SCALE_DOWN;
  public static final float HEIGHT = IMG_HEIGHT/SCALE_DOWN;
  public static final float BOT = IMG_BOT/SCALE_DOWN;
  public static final float STUMP_LEFT = IMG_LEFT/SCALE_DOWN;
  public static final float STUMP_RIGHT = IMG_RIGHT/SCALE_DOWN;

  PVector pos;       // Position of top left of image
  PVector savedPos;
  PImage img = loadImage("start.png");

  /** Constructor - creates a new start object at a position */
  Start(float x, float y) { 
    pos = new PVector(x, y);
    savedPos = new PVector(x, y);
    img.resize((int)WIDTH, (int)HEIGHT);
  }

  /** Saves position of start */
  void saveState() {
    savedPos = new PVector(pos.x, pos.y);
  }

  /** Returns previously saved state of the flower */
  void previousState() {
    pos.set(savedPos.x, savedPos.y);
  }

  /** Draws stump if it is visible on the screen */
  void display() {
    if (pos.x > - WIDTH && pos.x < width && pos.y > - HEIGHT && pos.y < height);
    image(img, pos.x, pos.y);
  }

  /** Moves position of the stump */
  void translateXY(float x, float y) {
    pos.set(pos.x + x, pos.y + y);
  }

  // Methods to return values
  float surface() {
    return(pos.y + SURFACE);
  }

  float bottom() {
    return(pos.y + BOT);
  }

  float left() {
    return(pos.x + STUMP_LEFT);
  }

  float right() {
    return(pos.x + STUMP_RIGHT);
  }

  boolean isEndPoint() { 
    return false;
  }
}

/** The endpoint of the game - displayed as a large yellow flower */
class End implements Surface {

  public static final float IMG_LEFTOFFSET = 370;             // Start position of the flower
  public static final float IMG_TOPOFFSET = 5800;     

  public static final float IMG_WIDTH = 886;                  // width of original image
  public static final float IMG_HEIGHT = 513;                 // height of original image
  public static final float IMG_LEFT = 260;                   // distance to left of flower from left of image
  public static final float IMG_RIGHT = 660;                  // distance to right of flower from left of image
  public static final float IMG_SURFACE = 210;                // height to surface of flower from top of image
  public static final float IMG_BOT = 280;                    // distance to bottom of flower from top of image
  public static final float SCALE_DOWN = 2;
  // scaling values down
  public static final float WIDTH = IMG_WIDTH/SCALE_DOWN;
  public static final float HEIGHT = IMG_HEIGHT/SCALE_DOWN;
  public static final float SURFACE = IMG_SURFACE/SCALE_DOWN; 
  public static final float BOT = IMG_BOT/SCALE_DOWN;        
  public static final float FLOWER_LEFT = IMG_LEFT/SCALE_DOWN;
  public static final float FLOWER_RIGHT = IMG_RIGHT/SCALE_DOWN;

  PVector pos;             // position of top left of flower image
  PImage img;
  PVector savedPos;
  boolean realEnd = true;

  /** Constructor - Fully functioning end point */
  End(String mode) { 
    pos = new PVector(IMG_LEFTOFFSET/SCALE_DOWN, height - (IMG_TOPOFFSET/SCALE_DOWN));
    savedPos = new PVector(pos.x, pos.y);
    img = loadImage("endFlower-" + mode + ".png");
    img.resize((int)(WIDTH), (int)(HEIGHT));
  }

  /** Constructor - Used in the tutorial */
  End(float posX, float posY) {
    this.pos = new PVector(posX, posY);
    img = loadImage("endFlower-easy.png");
    img.resize((int)(WIDTH), (int)(HEIGHT));
  }

  /** Saves position of flower */
  void saveState() {
    savedPos = new PVector(pos.x, pos.y);
  }

  /** Returns previously saved state of the flower */
  void previousState() {
    pos.set(savedPos.x, savedPos.y);
  }

  /** Draws flower if it is visible on the screen */
  void display() {
    if (pos.x > - WIDTH && pos.x < width && pos.y > - HEIGHT && pos.y < height)
      image(img, pos.x, pos.y);
  }

  /** Moves position of flower based on bug's movement */
  void translateXY(float x, float y) {
    pos.set(pos.x + x, pos.y + y);
    if (pos.x < -width)  pos.set(width, pos.y);
    if (pos.x > width)   pos.set(-width, pos.y);
  }

  /** Allows flower to float on the water */
  void update(Water water, Bug bug) {
    if (bottom() >= water.top) {
      pos.set(pos.x, water.top - BOT);
      if (bug.currentSurface == this)  bug.pos.set(bug.pos.x, surface() + bug.bugFeet);
    }
  }

  // Methods to get values
  float surface() {
    return(pos.y + SURFACE);
  }

  float bottom() {
    return(pos.y + BOT);
  }

  float left() {
    return(pos.x + FLOWER_LEFT);
  }

  float right() {
    return(pos.x + FLOWER_RIGHT);
  }

  PVector position() { 
    return pos;
  }

  boolean isEndPoint() { 
    return realEnd;
  }
}

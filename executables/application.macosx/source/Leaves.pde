class BasicLeaf implements Leaf, Surface {

  // Constants
  public static final float IMG_WIDTH = 226;                       // width of original image  
  public static final float IMG_HEIGHT = 58;                       // height of original image  
  public static final float IMG_SURFACE = 27;                      // distance from top of image to the surface of leaf
  public static final float SCALE_DOWN = 1.5;                      // amount to scale down original image    
  public static final float LEAF_W = IMG_WIDTH/SCALE_DOWN;         // width of leaf
  public static final float LEAF_H = IMG_HEIGHT/SCALE_DOWN;        // height of leaf
  public static final float LEAF_SURFACE = IMG_SURFACE/SCALE_DOWN; // distance from top of leaf to its surface

  PImage img;      // displayed image
  PVector pos;     // top left position of leaf
  float vx, vy;    // velocity

  /** 
   *  Constructor 
   *  - Creates a new leaf with randomised speed
   */
  BasicLeaf(float xStart, float yStart, boolean dark) {
    img = dark? basicLeafDark : basicLeaf;
    vx = random(-2, -1);
    vy = random(0.5, 1.3);
    pos = new PVector(xStart, yStart);
    img.resize((int) LEAF_W, (int) LEAF_H);
  }

  /** 
   *  Constructor 
   *  - Creates a new leaf with specific speed and position
   */
  BasicLeaf(PVector pos, float vx, float vy, PImage image) {
    this.pos = new PVector(pos.x, pos.y);
    this.vx = vx;
    this.vy = vy;
    img = image;
  }

  /** Returns a new leaf with the same position and speed as 'this' */
  Leaf duplicate() {
    return(new BasicLeaf(this.pos, this.vx, this.vy, this.img));
  }

  /** Moves position of leaf */
  void translateXY(float x, float y) {
    pos.set(pos.x + x, pos.y + y);
  }

  /** Draws the leaf if it is visible on the screen */
  void display() { 
    if (pos.x > -LEAF_W && pos.x < width && pos.y > -LEAF_H && pos.y < height) 
      image(img, pos.x, pos.y);
  }

  /** Updates position of leaf */
  void fall() {
    pos.set(pos.x + vx, pos.y + vy);
  }

  /** Checks if the leaf is dead (in the water) */
  boolean dead(float waterLevel) {
    return(pos.y > waterLevel + 100);
  }

  /** Accelerates the speed at which the leaf falls */
  void accelerate() {
    vy *= 3;
  }

  void setSpeedZero() {
    vx = 0;
    vy = 0;
  }

  /** Gives looping effect to leaf when it goes off the screen */
  void loop(float y) {
    if (pos.y > y + LEAF_H) pos.set(pos.x, 0 - LEAF_H);
    if (pos.x < - LEAF_W) pos.set(width, pos.y);
  }

  int increaseTime() {
    return 0;
  }

  boolean isEndPoint() { 
    return false;
  }

  // Methods to get values
  PVector velocity() {
    return(new PVector(vx, vy));
  }

  float left() { 
    return(pos.x + 2);
  }

  float right() { 
    return(pos.x + LEAF_W - 7);
  }

  float surface() { 
    return(pos.y + LEAF_SURFACE);
  }

  float bottom() { 
    return(pos.y + LEAF_H);
  }
}

/** Brown Leaf object that breaks over time if stood on by the bug */
class BreakLeaf implements Leaf, Surface {

  // Constants
  public static final float IMG_WIDTH = 184;                        // width of original image  
  public static final float IMG_HEIGHT = 58;                        // height of original image  
  public static final float IMG_SURFACE = 27;                       // distance from top of image to surface of leaf
  public static final float SCALE_DOWN = 1.5;                       // amount to scale down original image           
  public static final float LEAF_W = IMG_WIDTH/SCALE_DOWN;          // width of leaf
  public static final float LEAF_H = IMG_HEIGHT/SCALE_DOWN;         // height of leaf
  public static final float LEAF_SURFACE = IMG_SURFACE/SCALE_DOWN;  // distance from top of leaf to its surface 
  public static final int DEAD_TIME = 30;                           // time taken for leaf to fully break

  PVector pos;           // top left position of leaf
  float vx, vy;          // velocity
  int breakTimer = 0;    
  int currentImage = 0;

  /** 
   *  Constructor
   *  - Creates a new leaf with randomised velocity
   */
  BreakLeaf(float xStart, float yStart) {
    vx = random(-2, -1);
    vy = random(0.3, 1.0);
    pos = new PVector(xStart, yStart);
  }

  /** 
   *  Constructor
   *  - Creates a new leaf with specific velocity
   */
  BreakLeaf(PVector pos, float vx, float vy, int currentImage, int breakTimer) {
    this.pos = new PVector(pos.x, pos.y);
    this.vx = vx;
    this.vy = vy;
    this.currentImage = currentImage;
    this.breakTimer = breakTimer;
  }

  /** Returns a new leaf with the same position, speed and breakage as 'this' */
  Leaf duplicate() {
    return(new BreakLeaf(this.pos, this.vx, this.vy, this.currentImage, this.breakTimer));
  }

  /** Moves position of leaf */
  void translateXY(float x, float y) {
    pos.set(pos.x + x, pos.y + y);
  }

  /** BreakLeaf is dead if it is in the water, or if it has fully broken */
  boolean dead(float waterLevel) {
    return(pos.y > waterLevel + 100 || breakTimer >= DEAD_TIME);
  }

  void display() {
    if (pos.x > -LEAF_W && pos.x < width && pos.y > -LEAF_H && pos.y < height) 
      image(breakLeafImages.get(currentImage), pos.x, pos.y);
  }

  /** Updates position of leaf */
  void fall() {
    pos.set(pos.x + vx, pos.y + vy);
  }

  /** Accelerates the speed at which the leaf falls (BreakLeafs will not accelerate)*/
  void accelerate() {
    vy *= 1;
  }

  void setSpeedZero() {
    vx = 0;
    vy = 0;
  }

  /** Gives looping effect to leaf when it goes off the screen */
  void loop(float y) {
    if (pos.y > y + LEAF_H) pos.set(pos.x, 0 - LEAF_H);
    if (pos.x < - LEAF_W) pos.set(width, pos.y);
  }

  /** Increases breakTimer: A different image representing its current break level is displayed based on the breakTimer */
  int increaseTime() {
    breakTimer ++;
    int step = DEAD_TIME / 5;
    if (breakTimer == step || breakTimer == step * 2 || breakTimer == step * 3 || breakTimer == step * 4) {
      currentImage ++;
    }
    return breakTimer;
  }

  boolean isEndPoint() { 
    return false;
  }

  // Methods to get values
  float left() {
    return(pos.x + 2);
  }

  float right() {
    return(pos.x + LEAF_W - 7);
  }

  float surface() {
    return(pos.y + LEAF_SURFACE);
  }

  float bottom() {
    return(pos.y + LEAF_H);
  }

  PVector velocity() {
    return(new PVector(vx, vy));
  }
}

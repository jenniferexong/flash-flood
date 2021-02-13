class Bug {
  // Constants
  public static final float IMG_WIDTH = 146;             // width of original image
  public static final float IMG_HEIGHT = 278;            // height of original image
  public static final float IMG_FEET = 18;               // distance from bottom of image to bug's ground level
  public static final float IMG_FEETLEFT_RIGHT = 52;     // left of bugs feet from left of image (facing right)
  public static final float IMG_FEETRIGHT_RIGHT = 114;   // right of bugs feet from left of image (facing right)
  public static final float IMG_FEETLEFT_LEFT = 32;      // left of bugs feet from left of image (facing left)
  public static final float IMG_FEETRIGHT_LEFT = 94;     // right of bugs feet from left of image (facing left)
  public static final float SCALE_DOWN = 2;              // factor to scale down image

  public static final float AX_AIR = 1.5;   // horizontal acceleration of the bug when it is in the air
  public static final float AY = 0.065;     // vertical acceleration of the bug when it is in the air
  public static final float VX_GROUND = 1;  // horizontal velocity of bug when walking on a surface
  public static final float VX_MAX = 2;     // maximum horizontal velocity
  public static final float VX_MIN = -3;    // minimum horizontal velocity

  HashMap<String, PImage> img = new HashMap<String, PImage>();
  HashMap<String, Float> feetLeft = new HashMap<String, Float>();  // location of the left of bug's feet
  HashMap<String, Float> feetRight = new HashMap<String, Float>(); // location of the right of bug's feet
  String direction = "right";                                      // current direction of bug
  String savedDirection = "right";
  float bugW, bugH, bugFeet; 

  PVector pos;       // position of bottom left corner of bug image
  PVector savedPos;
  float vxAir = 0;   // horizontal velocity when in the air
  float vy = 0;      // vertical velocity

  Surface currentSurface = null;
  Leaf currentLeaf = null;
  Leaf savedLeaf;
  boolean jumped = false;

  /* Constructor - Stationary bug used only for display purposes (in score screen) */
  Bug(float x, float y) {
    bugH = IMG_HEIGHT;
    pos = new PVector(x, y);
  }

  /* Constructor */
  Bug(PVector position) {
    bugW = IMG_WIDTH/SCALE_DOWN;
    bugH = IMG_HEIGHT/SCALE_DOWN;
    bugFeet = IMG_FEET/SCALE_DOWN;
    // loading images ans resizing images
    img.put("right", loadImage("bug-right.png"));
    img.put("left", loadImage("bug-left.png"));
    img.get("right").resize((int)bugW, (int)bugH);
    img.get("left").resize((int)bugW, (int)bugH);

    feetLeft.put("right", IMG_FEETLEFT_RIGHT/SCALE_DOWN);
    feetLeft.put("left", IMG_FEETLEFT_LEFT/SCALE_DOWN);
    feetRight.put("right", IMG_FEETRIGHT_RIGHT/SCALE_DOWN);
    feetRight.put("left", IMG_FEETRIGHT_LEFT/SCALE_DOWN);

    pos = new PVector(position.x, position.y);
    savedPos = new PVector(position.x, position.y);
  }

  /** Saves current state of Bug and returns a duplicate leaf of currentLeaf */
  Leaf saveState() {
    savedDirection = direction;
    savedLeaf = null;
    savedPos = new PVector(pos.x, pos.y);
    if (currentLeaf != null) {
      savedLeaf = currentLeaf.duplicate();
    }
    return savedLeaf;
  }

  /** Returns state of Bug to its most recently saved state */
  void previousState() {
    direction = savedDirection;
    currentSurface = null;
    pos.set(savedPos.x, savedPos.y);
    if (savedLeaf != null) {
      currentSurface = savedLeaf;
      currentLeaf = savedLeaf;
    }
  }

  void display() {
    if (isOnSurface()) {
      bugWalk.get(direction).display(this);
    } else if (!isOnSurface()) {
      bugFly.update(); // updating the flying animation by 3 frames
      bugFly.update();
      bugFly.update();
      bugFly.display(this);
    }
  }

  /** Moving the bug based on keys which are currently pressed */
  void move(String dir) {
    // Don't allow movement control if bug is at the end or on a lotus
    if (currentSurface != null && (currentSurface.isEndPoint() || currentSurface instanceof Lotus)) return;

    if (dir.equals("up")) {
      if (isOnSurface()) { // jumping from a surface
        vy = - 6;
        vxAir = 0;
        jumped = true;
        currentSurface = null;
        currentLeaf = null;
      }
    } else if (dir.equals("right")) {
      if (direction.equals("left")) {
        direction = "right";
        pos.set(pos.x - 10, pos.y);
      }
      if (isOnSurface()) { 
        // if on a surface
        bugWalk.get(direction).update();
        pos.set(pos.x + VX_GROUND, pos.y);
      } else {
        // in the air
        if (jumped) {
          vxAir = 1.5;
          jumped = false;
        } else if (vxAir < 0) {
          vxAir = 0.5;
        } else vxAir += AX_AIR;
      }
    } else if (dir.equals("left")) {
      if (direction.equals("right")) {
        pos.set(pos.x + 10, pos.y);
        direction = "left";
      }
      if (isOnSurface()) { // if on a surface
        bugWalk.get(direction).update();
        pos.set(pos.x - VX_GROUND, pos.y);
      } else { // in in the air
        if (jumped) {
          vxAir = -3;
          jumped = false;
        } else if (vxAir > 0) {
          vxAir = -1.5;
        } else vxAir -= AX_AIR;
      }
    }
  }

  /** Updating position of bug based on its current velocity */
  void update() {
    if (currentSurface == null) { // incally) vy != 0
      if (vxAir > VX_MAX)  vxAir = VX_MAX;
      if (vxAir < VX_MIN)  vxAir = VX_MIN;
      vy += AY;
    }
    if (currentLeaf != null && currentLeaf instanceof BreakLeaf) 
      currentLeaf.increaseTime(); // increase break-time of leaf that bug is currently on
  }

  /** 
   *  Checks if bug is falling through a surface
   *  If yes, set vy to 0, and return float value that will move the bug so that it is standing exactly on the surface
   */
  float through(Surface surface) {
    if (!isFalling()) return 0; // 0 means not falling through surface
    float bugLeft = pos.x + feetLeft.get(direction);
    float bugRight = pos.x + feetRight.get(direction);
    boolean through = (bot() > surface.surface() && bot() < surface.bottom()) && 
      ((bugLeft > surface.left() && bugLeft < surface.right()) || 
      (bugRight > surface.left() && bugRight < surface.right()));

    if (through) {
      float offset = (surface.bottom() - surface.surface()) - (surface.bottom() - bot()); // how much to translate back
      currentSurface = surface;
      return offset;
    }
    return 0;
  }

  /** 
   *  Checks if the bug should fall off its current surface
   *  @force - if true, force the bug to fall off (ie when BreakLeaf breaks)
   */
  void fallOff(boolean force) {
    float bugLeft = pos.x + feetLeft.get(direction);
    float bugRight = pos.x + feetRight.get(direction);
    boolean on = (bot() >= currentSurface.surface() && bot() <= currentSurface.bottom()) && 
      ((bugLeft >= currentSurface.left() && bugLeft <= currentSurface.right()) || 
      (bugRight >= currentSurface.left() && bugRight <= currentSurface.right()));
    if (on) vy = 0;
    if (!on) {
      vy = 1;
      if (direction.equals("right")) vxAir = 1;
      if (direction.equals("left")) vxAir = -2.0;
      currentSurface = null;
      currentLeaf = null;
    } else if (force) {
      vy = currentLeaf.velocity().y;
      vxAir = currentLeaf.velocity().x;
      currentSurface = null;
      currentLeaf = null;
    }
  }

  void setVYZero() {
    vy = 0;
    vxAir = 0;
    jumped = false;
  }

  /** Make bug bounce off a lotus */
  void bounce() {
    vy = -9;
    currentSurface = null;
  }

  void setLeaf(Leaf l) {
    currentLeaf = l;
  }

  /** Returns horizontal speed of bug */
  float horizSpeed() {
    if (currentLeaf != null)  return(currentLeaf.velocity().x);
    else {
      if (vy == 0)  return(0);
      else          return(vxAir);
    }
  }

  /** Returns vertical speed of bug */
  float vertSpeed() {
    if (currentLeaf != null)  return(currentLeaf.velocity().y);
    else                      return(vy);
  }

  boolean isOnSurface() {
    return(currentSurface != null);
  }

  boolean isFalling() {
    return(vy > 0);
  }

  boolean died(float waterSurface) {
    return(pos.y > (waterSurface + 20));
  }

  float bot() {
    return(pos.y - (IMG_FEET/SCALE_DOWN));
  }

  String toString() {
    String leaf = null;
    if (currentLeaf == null) leaf = "no leaf, ";
    else leaf = "leaf, ";
    String jump = null;
    //println(pos.x +", " + pos.y);
    if (jumped) jump = "jumped, ";
    else jump = "not jumped, ";
    String surface = null;
    if (isOnSurface()) surface = "surface, ";
    else surface = "no surface , ";
    String desc = leaf + jump + surface + "vy: " + vy + "vx: " + horizSpeed();
    return(desc);
  }
}

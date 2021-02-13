import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class FlashFlood extends PApplet {

/** Main */

// Constants
public static final PVector BIG_BUTTON = new PVector(250, 50);
public final int EASY_BGCOL = color(135, 205, 181);
public final int HARD_BGCOL = color(113, 157, 170);

// Images
PImage easyBackground, hardBackground;
PImage basicLeaf, basicLeafDark;
ArrayList<PImage> breakLeafImages = new ArrayList<PImage>();

// Animations
HashMap<String, Animation> bugWalk = new HashMap<String, Animation>();
Animation bugFlyBestTimes;
Animation bugFly;

HashMap<String, Boolean> keys = new HashMap<String, Boolean>();
PFont font;
Scores scoresList;
Screen currentScreen;

public void setup() {
  
  frameRate(100);
  font = loadFont("NanumPen-200.vlw");
  textFont(font);

  // Loading images
  easyBackground = loadImage("easy-background.jpg");
  hardBackground = loadImage("hard-background.jpg");
  easyBackground.resize(width, 0);
  hardBackground.resize(width, 0);
  basicLeaf = loadImage("basic-leaf.png");
  basicLeafDark = loadImage("basic-leaf-dark.png");
  basicLeaf.resize((int) BasicLeaf.LEAF_W, (int) BasicLeaf.LEAF_H);
  basicLeafDark.resize((int) BasicLeaf.LEAF_W, (int) BasicLeaf.LEAF_H);
  for (int i = 1; i <= 5; i ++) {
    PImage image = loadImage("breakleaf-animation/break-leaf" + i +".png");
    image.resize((int) BreakLeaf.LEAF_W, (int) BreakLeaf.LEAF_H);
    breakLeafImages.add(image);
  }

  // Creating animations - loads all the images needed in each animation
  bugWalk.put("right", new Animation("walking-animation", "bugwalk-right", 26, Bug.IMG_WIDTH, Bug.IMG_HEIGHT, 2));
  bugWalk.put("left", new Animation("walking-animation", "bugwalk-left", 26, Bug.IMG_WIDTH, Bug.IMG_HEIGHT, 2));
  bugFlyBestTimes = new Animation("flying-animation", "bugfly-right", 10, Bug.IMG_WIDTH, Bug.IMG_HEIGHT, 1);
  bugFly = new Animation("flying-animation", "bugfly-right", "bugfly-left", 10, Bug.IMG_WIDTH, Bug.IMG_HEIGHT, 2);

  //putting values into key hashmap
  keys.put("up", false);
  keys.put("left", false);
  keys.put("right", false);

  scoresList = new Scores();
  currentScreen = new Menu();
}

public void draw() {
  currentScreen.run();
  Screen newScreen = currentScreen.gameOver();
  if (newScreen != null)  currentScreen = newScreen;
}

/** 
 *  Checks if the mouse has been pressed on a button on a Screen: 
 *  If a button was pressed, a new Screen is returned and run() in the draw() method
 */
public void mousePressed() {
  Screen newScreen = currentScreen.onClick(mouseX, mouseY);
  if (newScreen != null)  currentScreen = newScreen;
}

/** 
 *   Allows keys to control bug movement - Only needed during gameplay/tutorial
 */
public void keyPressed() {
  if (!(currentScreen instanceof Game) && !(currentScreen instanceof Tutorial)) return;
  if ((key == 'w' || key == 'W' || keyCode == UP))       keys.put("up", true);
  else if (key == 'd' || key == 'D' || keyCode == RIGHT) keys.put("right", true);
  else if (key == 'a' || key == 'A' || keyCode == LEFT)  keys.put("left", true);
}

public void keyReleased() {
  if (!(currentScreen instanceof Game) && !(currentScreen instanceof Tutorial)) return;
  if (key == 'w' || key == 'W' || keyCode == UP)  keys.put("up", false);
  else if (key == 'd' || key == 'D' || keyCode == RIGHT) {
    bugWalk.get("right").resetFrame(); 
    keys.put("right", false);
  } else if (key == 'a' || key == 'A' || keyCode == LEFT) {
    bugWalk.get("left").resetFrame();
    keys.put("left", false);
  }
}
class Animation {
  PImage[] frames;
  HashMap<String, PImage[]> directionalFrames = new HashMap<String, PImage[]>();
  int imgCount;
  int frame;

  /** 
   *   Constructor
   *   - Loads all the images needed for an animation sequence and puts them into an array 
   */
  Animation(String folder, String imgName, int count, float w, float h, float scaleDown) {
    imgCount = count;
    frames = new PImage[imgCount];

    for (int i = 0; i < imgCount; i++) {
      String filename = imgName + (i + 1) + ".png";
      frames[i] = loadImage(folder + "/" + filename);
      frames[i].resize((int)(w/scaleDown), (int)(h/scaleDown));
    }
  }

  /** 
   *   Constructor
   *   - Loads all the images needed for an animation sequence and puts them into an array 
   *   - Used for flying animation: displays the same frame number no matter what direction the bug faces
   */
  Animation(String folder, String imgNameR, String imgNameL, int count, float w, float h, float scaleDown) {
    imgCount = count;
    directionalFrames.put("right", new PImage[imgCount]);

    for (int i = 0; i < imgCount; i++) {
      String filename = imgNameR + (i+1) + ".png";
      directionalFrames.get("right")[i] = loadImage(folder + "/" + filename);
      directionalFrames.get("right")[i].resize((int)(w/scaleDown), (int)(h/scaleDown));
    }

    directionalFrames.put("left", new PImage[imgCount]);
    for (int i = 0; i < imgCount; i++) {
      String filename = imgNameL + (i+1) + ".png";
      directionalFrames.get("left")[i] = loadImage(folder + "/" + filename);
      directionalFrames.get("left")[i].resize((int)(w/scaleDown), (int)(h/scaleDown));
    }
  }

  /** Displays a frame of the animation based on the bug's position */
  public void display(Bug bug) {
    if (frames != null)  image(frames[frame], bug.pos.x, bug.pos.y - bug.bugH);
    else                 image(directionalFrames.get(bug.direction)[frame], bug.pos.x, bug.pos.y - bug.bugH);
  }

  /** Displays a frame of the animation based on the lotus' position */
  public void display(Lotus lotus) {
    image(frames[frame], lotus.pos.x, lotus.pos.y - lotus.IMG_BOT);
  }

  /** Increments the frame number of the animation */
  public void update() { 
    frame = (frame+1) % imgCount;
  }

  /** Resets animation to the beginning of the sequence */
  public void resetFrame() {
    frame = 0;
  }
}
/**
 *  Creates a background object that moves:
 *  - Base image is a horizontally looping image, so when it is translated and redrawn, it has the effect of an infinite background.
 */
class Background {
  int baseColour;                   // Colour of the sky
  PImage background;                  // Horizontally looping image
  PVector bgPos1, bgPos2;             // Current position of top-left of section of background displayed
  PVector savedBgPos1, savedBgPos2;   // Position of background last time it was saved

  /** Constructor */
  Background(String difficulty, int baseCol) {
    if (difficulty.equals("easy"))  background = easyBackground; 
    if (difficulty.equals("hard"))  background = hardBackground;
    background.resize(1000, 0);
    // Split background vertically into two halves
    bgPos1 = new PVector(0, 3450 - height); 
    bgPos2 = new PVector(width/2, 3450 - height);
    savedBgPos1 = new PVector(0, 3450 - height); 
    savedBgPos2 = new PVector(width/2, 3450 - height);
    baseColour = baseCol;
  }

  /** 
   *  Moves the position of the background 
   *  - If one half is off the screen, set its position to be on the other side to give a looping effect
   */
  public void translateXY(float x, float y) {
    bgPos1.set(bgPos1.x + x, bgPos1.y + y);
    bgPos2.set(bgPos2.x + x, bgPos2.y + y);
    if (bgPos1.x < -(width/2))   bgPos1.set(bgPos1.x + width, bgPos1.y);
    if (bgPos1.x > width)        bgPos1.set(bgPos1.x - width, bgPos1.y);
    if (bgPos2.x < -(width/2))   bgPos2.set(bgPos2.x + width, bgPos2.y);
    if (bgPos2.x > width)        bgPos2.set(bgPos2.x - width, bgPos2.y);
  }

  /** 
   *  Background is displayed in three parts:
   *   - bgSection3 is the section of the image that is cut off one side 
   *   - Gives infinite background effect
   */
  public void display() {
    background(baseColour);
    PImage bgSection1 = background.get(0, (int)bgPos1.y, width/2, height);
    PImage bgSection2 = background.get(width/2, (int)bgPos2.y, width/2, height);
    PImage bgSection3 = null;
    float bgSection3x = 0;
    if (bgPos1.x < 0) {
      bgSection3 = bgSection1.get(0, 0, (int)-bgPos1.x, height);
      bgSection3x = bgPos2.x + (width/2);
    } else if (bgPos2.x < 0) {
      bgSection3 = bgSection2.get(0, 0, (int)-bgPos2.x, height);
      bgSection3x = bgPos1.x + (width/2);
    } else if (bgPos1.x > width/2) {
      bgSection3 = bgSection1.get((int)((width/2) - (bgPos1.x - width/2)), 0, (int)(bgPos1.x - width/2), height);
      bgSection3x = 0;
    } else if (bgPos2.x > width/2) {
      bgSection3 = bgSection2.get((int)((width/2) - (bgPos2.x - width/2)), 0, (int)(bgPos2.x - width/2), height);
      bgSection3x = 0;
    }

    image(bgSection1, bgPos1.x, 0);
    image(bgSection2, bgPos2.x, 0);
    if (bgSection3 != null) image(bgSection3, bgSection3x, 0);
  }

  /** Saves current position of background */
  public void saveState() {
    savedBgPos1 = new PVector(bgPos1.x, bgPos1.y);
    savedBgPos2 = new PVector(bgPos2.x, bgPos2.y);
  }

  /** Sets position back to when it was last saved */
  public void previousState() {
    bgPos1.set(savedBgPos1.x, savedBgPos1.y);
    bgPos2.set(savedBgPos2.x, savedBgPos2.y);
  }
}
/** Screen to display best times achieved by players */
class BestTimes implements Screen {
  Button button;
  PImage background;
  Bug displayBug;

  /** Constructor */
  BestTimes() {
    displayBug = new Bug(width/2 - (Bug.IMG_WIDTH/2), height/2 + (Bug.IMG_HEIGHT/2));
    float yStart = height - 100;
    button = new Button("Back", width/2, yStart, BIG_BUTTON.x, BIG_BUTTON.y, false);
    background = easyBackground;
    background = background.get(0, 620, width, height);
  }

  public void run() {
    image(background, 0, 0);
    // updating the animation of the stationary bug
    bugFlyBestTimes.update();
    bugFlyBestTimes.display(displayBug);
    // drawing "Scores" with an outlined effect
    textAlign(CENTER, TOP);
    textSize(90);
    fill(0xffFAF79F, 150);
    text("Best Times", width/2 + 2, 100 + 2);
    text("Best Times", width/2 - 2, 100 - 2);
    text("Best Times", width/2 + 2, 100 - 2);
    text("Best Times", width/2 - 2, 100 + 2);
    fill(0);
    text("Best Times", width/2, 100);
    button.display();
    scoresList.display();   // displaying all the scores
  }

  // Returns a new Menu Screen if the mouse was clicked on the 'back' button
  public Screen onClick(float x, float y) {
    if (button.onClick(x, y).equals("Back"))  return(new Menu());
    return null;
  }

  public Screen gameOver() { 
    return null;
  }

  public Scoreboard getScore() { 
    return null;
  }
}

/** Used to store players' scores */
class Scores {
  final String FILENAME = "scores.txt";
  HashMap<String, int[]> scores = new HashMap<String, int[]>();         // Array of "easy" and "hard" scores
  HashMap<String, Integer> nextSpace = new HashMap<String, Integer>();  // Index of the next score to be added

  /**  Constructor - Reads scores from files and stores them in an array of Integers */
  Scores() {
    String[] easyLines = loadStrings("easy-" + FILENAME);
    String[] hardLines = loadStrings("hard-" + FILENAME);
    scores.put("easy", new int[easyLines.length + 20]);
    scores.put("hard", new int[hardLines.length + 20]);
    nextSpace.put("easy", easyLines.length);
    nextSpace.put("hard", hardLines.length);
    for (int i = 0; i < easyLines.length; i ++) {
      scores.get("easy")[i] = (Integer.valueOf(easyLines[i]));
      scores.put("easy", sort(scores.get("easy"), getLengthOfArray(scores.get("easy"))));
    }
    for (int i = 0; i < hardLines.length; i ++) {
      scores.get("hard")[i] = (Integer.valueOf(hardLines[i]));
      scores.put("hard", sort(scores.get("hard"), getLengthOfArray(scores.get("hard"))));
    }
  }

  /** Adds a score to the array, sorts it in increasing order, then saves the array of numbers to a file */
  public void addScore(int score, boolean hard) { 
    if (!hard) {
      scores.get("easy")[nextSpace.get("easy")] = score;
      nextSpace.put("easy", nextSpace.get("easy") + 1);
      scores.put("easy", sort(scores.get("easy"), getLengthOfArray(scores.get("easy"))));
    } else if (hard) {
      scores.get("hard")[nextSpace.get("hard")] = score;
      nextSpace.put("hard", nextSpace.get("hard") + 1);
      scores.put("hard", sort(scores.get("hard"), getLengthOfArray(scores.get("hard"))));
    }
    saveToFile();
  }

  /** Displays the scores */
  public void display() {
    float top = 120;
    float easyX = 200;
    float hardX = width - 190;
    textAlign(CENTER, TOP);

    // drawing titles
    fill(0, 0, 0, 150);
    textSize(40);
    text("Easy", easyX, top);
    text("Hard", hardX, top);

    // display all scores
    textSize(40);
    top = 220;
    for (int i = 0; i < getLengthOfArray(scores.get("easy")); i ++) {
      fill(0);
      text(scores.get("easy")[i], easyX + 2, top + 2);
      fill(0xffFFFF8E);
      text(scores.get("easy")[i], easyX, top);
      top += 25;
    }
    top = 220;
    for (int i = 0; i < getLengthOfArray(scores.get("hard")); i ++) {
      fill(0);
      text(scores.get("hard")[i], hardX + 2, top + 2);
      fill(0xffFFFF8E);
      text(scores.get("hard")[i], hardX, top);
      top += 25;
    }
  }

  /** Saves the array of scores to a file - one line for each number */
  public void saveToFile() {
    int easyScoresLength = getLengthOfArray(scores.get("easy"));
    int hardScoresLength = getLengthOfArray(scores.get("hard"));
    String[] easyScores = new String[easyScoresLength];
    String[] hardScores = new String[hardScoresLength];
    for (int i = 0; i < easyScoresLength; i ++)  easyScores[i] = String.valueOf(scores.get("easy")[i]);
    for (int i = 0; i < hardScoresLength; i ++)  hardScores[i] = String.valueOf(scores.get("hard")[i]);
    saveStrings(dataPath("easy-scores.txt"), easyScores);
    saveStrings(dataPath("hard-scores.txt"), hardScores);
  }

  /** Helper method to get the number of array slots that are filled with a non-zero integer */
  public int getLengthOfArray(int[] scores) {
    int count = 0;
    for (int i = 0; i < scores.length; i ++) 
      if (scores[i] != 0) count ++;
    return count;
  }
}
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

  public static final float AX_AIR = 1.5f;   // horizontal acceleration of the bug when it is in the air
  public static final float AY = 0.065f;     // vertical acceleration of the bug when it is in the air
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
  public Leaf saveState() {
    savedDirection = direction;
    savedLeaf = null;
    savedPos = new PVector(pos.x, pos.y);
    if (currentLeaf != null) {
      savedLeaf = currentLeaf.duplicate();
    }
    return savedLeaf;
  }

  /** Returns state of Bug to its most recently saved state */
  public void previousState() {
    direction = savedDirection;
    currentSurface = null;
    pos.set(savedPos.x, savedPos.y);
    if (savedLeaf != null) {
      currentSurface = savedLeaf;
      currentLeaf = savedLeaf;
    }
  }

  public void display() {
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
  public void move(String dir) {
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
          vxAir = 1.5f;
          jumped = false;
        } else if (vxAir < 0) {
          vxAir = 0.5f;
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
          vxAir = -1.5f;
        } else vxAir -= AX_AIR;
      }
    }
  }

  /** Updating position of bug based on its current velocity */
  public void update() {
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
  public float through(Surface surface) {
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
  public void fallOff(boolean force) {
    float bugLeft = pos.x + feetLeft.get(direction);
    float bugRight = pos.x + feetRight.get(direction);
    boolean on = (bot() >= currentSurface.surface() && bot() <= currentSurface.bottom()) && 
      ((bugLeft >= currentSurface.left() && bugLeft <= currentSurface.right()) || 
      (bugRight >= currentSurface.left() && bugRight <= currentSurface.right()));
    if (on) vy = 0;
    if (!on) {
      vy = 1;
      if (direction.equals("right")) vxAir = 1;
      if (direction.equals("left")) vxAir = -2.0f;
      currentSurface = null;
      currentLeaf = null;
    } else if (force) {
      vy = currentLeaf.velocity().y;
      vxAir = currentLeaf.velocity().x;
      currentSurface = null;
      currentLeaf = null;
    }
  }

  public void setVYZero() {
    vy = 0;
    vxAir = 0;
    jumped = false;
  }

  /** Make bug bounce off a lotus */
  public void bounce() {
    vy = -9;
    currentSurface = null;
  }

  public void setLeaf(Leaf l) {
    currentLeaf = l;
  }

  /** Returns horizontal speed of bug */
  public float horizSpeed() {
    if (currentLeaf != null)  return(currentLeaf.velocity().x);
    else {
      if (vy == 0)  return(0);
      else          return(vxAir);
    }
  }

  /** Returns vertical speed of bug */
  public float vertSpeed() {
    if (currentLeaf != null)  return(currentLeaf.velocity().y);
    else                      return(vy);
  }

  public boolean isOnSurface() {
    return(currentSurface != null);
  }

  public boolean isFalling() {
    return(vy > 0);
  }

  public boolean died(float waterSurface) {
    return(pos.y > (waterSurface + 20));
  }

  public float bot() {
    return(pos.y - (IMG_FEET/SCALE_DOWN));
  }

  public String toString() {
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
/** Buttons to return Screen when pressed */
class Button {

  final int HOVER_COLOUR = color(0xff69C42D);
  final float RAD = 15; 

  float centreX, centreY, w, h;
  String text;
  int outline = color(15, 90, 17);
  boolean small = false;

  /** Constructor */
  Button(String text, float x, float y, float w, float h, boolean small) {
    centreX = x;
    centreY = y;
    this.text = text;
    this.w = w;
    this.h = h;
    this.small = small;
    if (small) outline = color(0);
  }

  /** Draws the button */
  public void display() {
    if (small) {
      fill(255, 255, 255, 0);
      strokeWeight(2);
    } else {
      fill(255, 255, 255, 180);
      strokeWeight(5);
    }

    stroke(onHover() ? HOVER_COLOUR : outline);
    rectMode(CENTER);
    rect(centreX, centreY, w, h, RAD, RAD, RAD, RAD);
    textAlign(CENTER, TOP);
    fill(onHover() ? HOVER_COLOUR : outline);
    textSize(small? h - 8 : h - 10);
    text(text, centreX, centreY - (h/2) + 10);
  }

  /** Checks if mouse is over the button */
  public boolean onHover() {
    return(on(mouseX, mouseY));
  }

  /** Checks if mouse has clicked on the button, returning the button's text on click */
  public String onClick(float x, float y) {
    if (on(x, y)) return text;
    else return "null";
  }

  /** Checks if a position is on the button */
  public boolean on(float x, float y) {
    float left = centreX - (w/2);
    float right = centreX + (w/2);
    float top = centreY - (h/2);
    float bot = centreY + (h/2);
    return(x >= left && x <= right && y >= top && y <= bot);
  }
}
/** Playable gamemode: Easy and hard mode */
class Game implements Screen {

  final PVector BUG_START = new PVector(200, 400); // start position of bug

  // Fields
  boolean hardMode;
  boolean respawned = false;
  int respawnCount = 3;
  int respawnTimer = millis();
  int timer;
  int rate = 100;
  float translateYOffset = 0;
  int basicLeafTimer;
  int basicLeafRate;
  int breakLeafTimer;
  float breakLeafRate = 1000;

  Bug bug;
  Rain rain;
  Water water;
  Background background;
  Scoreboard scoreboard;
  // Surfaces
  ArrayList<Surface> surfaces = new ArrayList<Surface>();
  ArrayList<Lotus> lotuses = new ArrayList<Lotus>();
  ArrayList<Leaf> leaves = new ArrayList<Leaf>();
  ArrayList<Leaf> savedLeaves = new ArrayList<Leaf>();
  Start start;
  End end;

  /**  Constructor - Creates the bug and all surfaces */
  Game(boolean hardMode) {
    keys.put("up", false);
    this.hardMode = hardMode;
    basicLeafRate = hardMode ? 1500 : 1000;
    bug = new Bug(BUG_START); // create new bug
    water = new Water(height - 20, width, height * 2);
    rain = new Rain();
    background = new Background(hardMode ? "hard" : "easy", hardMode ? HARD_BGCOL : EASY_BGCOL);
    scoreboard = new Scoreboard(this);
    start = new Start(BUG_START.x - 10, BUG_START.y - bug.bugFeet - Start.SURFACE);
    end = new End(hardMode ? "hard" : "easy");
    lotuses.add(new Lotus(width, water.top));
    if (!hardMode) lotuses.add(new Lotus((width/2) - Lotus.IMG_WIDTH/2, water.top));
    if (hardMode) water.riseRate = 0.7f;
    bug.currentSurface = start;
    surfaces.addAll(lotuses);
    surfaces.add(start);
    surfaces.add(end);
  }

  /** Runs the game */
  public void run() {

    // Pauses the game and counts down respawn timer
    if (respawned) {
      this.respawnDelay();
      return;
    }
    // Ending the game
    if (bug.currentSurface == end) {
      if (water.riseRate != 10 && water.top > height) water.top = height;
      lotuses.clear();
      rain.clearRain();
      water.riseRate = 10;
      updateAll();
      displayAll();
      return;
    }

    translateYOffset = 0;

    // If bug is on a lotus, make the bug bounce off it
    for (Lotus l : lotuses) {
      if (bug.currentSurface == l) {
        bug.bounce();
        l.pos.x += width * 2;
      }
    }

    // controlling bug movement
    for (String dir : keys.keySet()) 
      if (keys.get(dir)) bug.move(dir);

    updateAll();
    translateAll(-bug.horizSpeed(), -bug.vertSpeed()); // translate everything based on bug's velocity

    //checking for landing on a surface (start, end, or lotus);
    for (Surface s : surfaces) 
      if (!bug.isOnSurface()) translateYOffset = bug.through(s);

    //checking for landing on a leaf
    for (Leaf leaf : leaves) {
      if (!bug.isOnSurface()) {
        translateYOffset = bug.through(leaf);
        if (translateYOffset != 0) {
          bug.currentLeaf = leaf;
          if (hardMode) bug.currentLeaf.accelerate();
        }
      }
    }

    if (bug.isOnSurface())   bug.fallOff(false);

    // If bug landed on something, save the current state of the game
    if (translateYOffset != 0) { 
      bug.setVYZero();
      translateAll(0, translateYOffset);
      if (!(bug.currentSurface instanceof Lotus))  saveState();
    }

    spawnLeaf();

    // If a leaf lands in the water, remove it from leaves arraylist
    for (int i = 0; i < leaves.size(); i ++) {
      Leaf leaf = leaves.get(i);
      if (leaf.dead(water.top)) {
        if (leaf == bug.currentLeaf)   bug.fallOff(true);
        leaves.remove(leaf);
        i --;
      }
    }

    // displaying game based on real time
    if (millis() - timer >= rate) {
      displayAll();
      timer = millis();
    }
  }

  public void updateAll() {
    bug.update();
    water.rise();
    end.update(water, bug);
    rain.update(water);
    for (Lotus lotus : lotuses)  lotus.update(water);
    for (Leaf leaf : leaves)     leaf.fall();
    scoreboard.update();
  }

  /** Move display position of everything */
  public void translateAll(float x, float y) {
    background.translateXY(x, -y);
    start.translateXY(x, y);
    end.translateXY(x, y);
    for (Lotus lotus : lotuses)  lotus.translateXY(x, y);
    for (Leaf l : leaves)        l.translateXY(x, y);
    water.translateY(y);
    rain.translateXY(x, y);
  }

  /** Drawing everything */
  public void displayAll() {
    background.display();
    start.display();
    for (Leaf l : leaves)        l.display();
    water.display();
    for (Lotus lotus : lotuses)  lotus.display();
    end.display();
    bug.display();
    rain.display();
    scoreboard.display();
  }

  /** Generating leaves with randomised start positions */
  public void spawnLeaf() {
    float minX = width;
    float maxX = width + 50;
    float minY = -150;
    float maxY = height/2;
    if (millis() - basicLeafTimer >= basicLeafRate) {
      leaves.add(new BasicLeaf(random(minX, maxX), random(minY, maxY), hardMode));
      basicLeafTimer = millis();
    }
    if (!hardMode) return;
    if (millis() - breakLeafTimer >= breakLeafRate) {
      leaves.add(new BreakLeaf(random(minX, maxX), random(minY, maxY)));
      breakLeafTimer = millis();
    }
  }

  public boolean loseLife() {
    if (bug.died(water.top)) {
      previousState();
      respawnCount = 3;
      respawned = true;
      return true;
    }
    return(false);
  }

  /** Saves the current state of the game */
  public void saveState() {
    start.saveState();
    end.saveState();
    water.saveState();
    background.saveState();
    for (Lotus lotus : lotuses) lotus.saveState();
    savedLeaves.clear();
    for (Leaf leaf : leaves) 
      if (bug.currentLeaf == null || (bug.currentLeaf != null && leaf != bug.currentLeaf)) 
        savedLeaves.add(leaf.duplicate());
    Leaf newCurrentLeaf = bug.saveState();
    if (newCurrentLeaf != null)  savedLeaves.add(newCurrentLeaf);
  }

  /** Restores the most recently saved state of the game */
  public void previousState() {
    bug.previousState();
    start.previousState();
    end.previousState();
    water.previousState();
    background.previousState();
    rain = new Rain();
    for (Lotus lotus : lotuses)  lotus.previousState();
    leaves.clear();
    for (Leaf l : savedLeaves) {
      Leaf newLeaf = l.duplicate();
      if (l == bug.savedLeaf)  bug.setLeaf(newLeaf);
      leaves.add(newLeaf);
    }
  }

  /** Returns the GameOver Screen if the bug has no lives left, or has won the game */
  public Screen gameOver() {
    if ((bug.currentSurface == end && water.top <= 0)) {
      scoresList.addScore(scoreboard.time, hardMode);
      scoreboard.success = true;
      return new GameOver(scoreboard);
    } else if (scoreboard.lives == 0) return new GameOver(scoreboard);
    return null;
  }

  /** Pauses the screen and displays a respawn countdown */
  public void respawnDelay() {
    if (millis() - respawnTimer >= 1000) {
      displayAll();
      if (respawnCount == 0) {
        respawned = false;
        return;
      }
      textAlign(CENTER);
      fill(0);
      textSize(200);
      text(respawnCount, width/2 + 4, height/2 + 4);
      fill(255, 255, 255);
      textSize(200);
      text(respawnCount, width/2, height/2);
      respawnCount --;
      respawnTimer = millis();
    }
  }

  public Screen onClick(float x, float y) {
    for (Button b : scoreboard.buttons) {
      if (b.onClick(x, y).equals("Menu"))         return new Menu();
      else if (b.onClick(x, y).equals("Restart")) return new Game(hardMode);
    }
    return null;
  }

  public float distToEnd() { 
    return bug.bot() - end.bottom();
  }

  public float waterDistToEnd() { 
    return water.top - end.bottom();
  }

  public Scoreboard getScore() { 
    return scoreboard;
  }
}
interface Screen {
  public void run();
  public Scoreboard getScore();
  public Screen onClick(float x, float y);
  public Screen gameOver();
}

interface Surface {
  public float left();
  public float right();
  public float surface();
  public float bottom();
  public boolean isEndPoint();
}

interface Mode extends Screen {
  public void previousState();
  public float distToEnd();
  public float waterDistToEnd();
  public boolean loseLife();
}

interface Leaf extends Surface {
  public void translateXY(float x, float y);
  public void fall();
  public void display();
  public void accelerate();
  public void setSpeedZero();
  public Leaf duplicate();
  public int increaseTime();
  public float left();
  public float right();
  public float surface();
  public float bottom();
  public boolean dead(float maxY);
  public PVector velocity();
}
class BasicLeaf implements Leaf, Surface {

  // Constants
  public static final float IMG_WIDTH = 226;                       // width of original image  
  public static final float IMG_HEIGHT = 58;                       // height of original image  
  public static final float IMG_SURFACE = 27;                      // distance from top of image to the surface of leaf
  public static final float SCALE_DOWN = 1.5f;                      // amount to scale down original image    
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
    vy = random(0.5f, 1.3f);
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
  public Leaf duplicate() {
    return(new BasicLeaf(this.pos, this.vx, this.vy, this.img));
  }

  /** Moves position of leaf */
  public void translateXY(float x, float y) {
    pos.set(pos.x + x, pos.y + y);
  }

  /** Draws the leaf if it is visible on the screen */
  public void display() { 
    if (pos.x > -LEAF_W && pos.x < width && pos.y > -LEAF_H && pos.y < height) 
      image(img, pos.x, pos.y);
  }

  /** Updates position of leaf */
  public void fall() {
    pos.set(pos.x + vx, pos.y + vy);
  }

  /** Checks if the leaf is dead (in the water) */
  public boolean dead(float waterLevel) {
    return(pos.y > waterLevel + 100);
  }

  /** Accelerates the speed at which the leaf falls */
  public void accelerate() {
    vy *= 3;
  }

  public void setSpeedZero() {
    vx = 0;
    vy = 0;
  }

  /** Gives looping effect to leaf when it goes off the screen */
  public void loop(float y) {
    if (pos.y > y + LEAF_H) pos.set(pos.x, 0 - LEAF_H);
    if (pos.x < - LEAF_W) pos.set(width, pos.y);
  }

  public int increaseTime() {
    return 0;
  }

  public boolean isEndPoint() { 
    return false;
  }

  // Methods to get values
  public PVector velocity() {
    return(new PVector(vx, vy));
  }

  public float left() { 
    return(pos.x + 2);
  }

  public float right() { 
    return(pos.x + LEAF_W - 7);
  }

  public float surface() { 
    return(pos.y + LEAF_SURFACE);
  }

  public float bottom() { 
    return(pos.y + LEAF_H);
  }
}

/** Brown Leaf object that breaks over time if stood on by the bug */
class BreakLeaf implements Leaf, Surface {

  // Constants
  public static final float IMG_WIDTH = 184;                        // width of original image  
  public static final float IMG_HEIGHT = 58;                        // height of original image  
  public static final float IMG_SURFACE = 27;                       // distance from top of image to surface of leaf
  public static final float SCALE_DOWN = 1.5f;                       // amount to scale down original image           
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
    vy = random(0.3f, 1.0f);
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
  public Leaf duplicate() {
    return(new BreakLeaf(this.pos, this.vx, this.vy, this.currentImage, this.breakTimer));
  }

  /** Moves position of leaf */
  public void translateXY(float x, float y) {
    pos.set(pos.x + x, pos.y + y);
  }

  /** BreakLeaf is dead if it is in the water, or if it has fully broken */
  public boolean dead(float waterLevel) {
    return(pos.y > waterLevel + 100 || breakTimer >= DEAD_TIME);
  }

  public void display() {
    if (pos.x > -LEAF_W && pos.x < width && pos.y > -LEAF_H && pos.y < height) 
      image(breakLeafImages.get(currentImage), pos.x, pos.y);
  }

  /** Updates position of leaf */
  public void fall() {
    pos.set(pos.x + vx, pos.y + vy);
  }

  /** Accelerates the speed at which the leaf falls (BreakLeafs will not accelerate)*/
  public void accelerate() {
    vy *= 1;
  }

  public void setSpeedZero() {
    vx = 0;
    vy = 0;
  }

  /** Gives looping effect to leaf when it goes off the screen */
  public void loop(float y) {
    if (pos.y > y + LEAF_H) pos.set(pos.x, 0 - LEAF_H);
    if (pos.x < - LEAF_W) pos.set(width, pos.y);
  }

  /** Increases breakTimer: A different image representing its current break level is displayed based on the breakTimer */
  public int increaseTime() {
    breakTimer ++;
    int step = DEAD_TIME / 5;
    if (breakTimer == step || breakTimer == step * 2 || breakTimer == step * 3 || breakTimer == step * 4) {
      currentImage ++;
    }
    return breakTimer;
  }

  public boolean isEndPoint() { 
    return false;
  }

  // Methods to get values
  public float left() {
    return(pos.x + 2);
  }

  public float right() {
    return(pos.x + LEAF_W - 7);
  }

  public float surface() {
    return(pos.y + LEAF_SURFACE);
  }

  public float bottom() {
    return(pos.y + LEAF_H);
  }

  public PVector velocity() {
    return(new PVector(vx, vy));
  }
}
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
  float vxSpeed = 1.2f;

  /** Constructor */
  Lotus(float x, float y) {
    pos = new PVector(x, y);
    savedPos = new PVector(x, y);
    animation = new Animation("lotus-animation", "lotus-", 10, IMG_WIDTH, IMG_HEIGHT, 1);
  }

  /** Saves current position of lotus */
  public void saveState() {
    savedPos = new PVector(pos.x, pos.y);
  }

  /** Sets position of lotus to the recently saved position */
  public void previousState() {
    pos.set(savedPos.x, savedPos.y);
  }

  /** Updates the position of the lotus so it floats on the water and moves left */
  public void update(Water water) { 
    animation.update();
    pos.x -= vxSpeed;
    if (water.riseRate != 0) 
      if (pos.x < - IMG_WIDTH)   pos.set(width, pos.y);
    pos.y -= water.riseRate;
  }

  public void display() {
    animation.display(this);
  }

  public void translateXY(float x, float y) {
    pos.set(pos.x + x, pos.y + y);
  }

  // Methods to return values
  public float surface() {
    return(pos.y - IMG_BOT + IMG_SURFACE);
  }

  public float bottom() {
    return(pos.y);
  }

  public float left() {
    return(pos.x + IMG_LEFT);
  }

  public float right() {
    return(pos.x + IMG_RIGHT);
  }

  public boolean isEndPoint() { 
    return false;
  }
}
/** Main menu of Flash Flood */
class Menu implements Screen {

  ArrayList<Button> buttons = new ArrayList<Button>();
  ArrayList<BasicLeaf> leaves = new ArrayList<BasicLeaf>();
  PImage background;

  /** Constructor */
  Menu() {
    float yStart = height/2 - 110;
    // Creating buttons
    buttons.add(new Button("Tutorial", width/2, yStart, BIG_BUTTON.x, BIG_BUTTON.y, false));
    buttons.add(new Button("Easy", width/2, yStart + 80, BIG_BUTTON.x, BIG_BUTTON.y, false));
    buttons.add(new Button("Hard", width/2, yStart + 160, BIG_BUTTON.x, BIG_BUTTON.y, false));
    buttons.add(new Button("Best Times", width/2, yStart + 280, BIG_BUTTON.x, BIG_BUTTON.y, false));
    buttons.add(new Button("Quit", width/2, yStart + 360, BIG_BUTTON.x, BIG_BUTTON.y, false));
    // Creating leaves to be displayed in the background
    for (int i = 0; i < 5; i ++)  leaves.add(new BasicLeaf(random(0, width), random(0, height), false));
    background = easyBackground;
    background = background.get(0, 620, width, height);
  }

  public void run() {
    image(background, 0, 0);
    // Display title text 'Flash Flood'
    textAlign(CENTER, TOP);
    textSize(120);
    fill(0, 0, 0, 50);
    fill(255, 255, 255, 200);
    text("Flash Flood", width/2, 100);
    // Drawing leaves falling in the background
    for (BasicLeaf l : leaves) {
      l.loop(height);
      l.fall();
      l.display();
    }
    for (Button b : buttons)   b.display();
  }

  /** Checks if the mouse clicked on a button and returns a new Screen if it did */
  public Screen onClick(float x, float y) {
    for (Button b : buttons) {
      if (b.onClick(x, y).equals("Tutorial"))          return(new Tutorial());
      else if (b.onClick(x, y).equals("Easy"))         return(new Game(false));
      else if (b.onClick(x, y).equals("Hard"))         return(new Game(true));
      else if (b.onClick(x, y).equals("Best Times"))   return(new BestTimes());
      else if (b.onClick(x, y).equals("Quit"))         exit();
    }
    return null;
  }

  public Screen gameOver() { 
    return null;
  }

  public Scoreboard getScore() { 
    return null;
  }
}

/** Screen displayed upon losing or winning the game */
class GameOver implements Screen {

  ArrayList<Button> buttons = new ArrayList<Button>();
  ArrayList<BreakLeaf> leaves = new ArrayList<BreakLeaf>();
  Scoreboard scoreboard;
  PImage background;
  float yStart = 200;

  GameOver(Scoreboard scoreboard) {
    this.scoreboard = scoreboard;
    // Creating buttons
    float buttonsStart = height/2 - 50;
    buttons.add(new Button("Menu", width/2, buttonsStart + 100, BIG_BUTTON.x, BIG_BUTTON.y, false));
    buttons.add(new Button("Quit", width/2, buttonsStart + 200, BIG_BUTTON.x, BIG_BUTTON.y, false));
    for (int i = 0; i < 5; i ++)   leaves.add(new BreakLeaf(random(0, width), random(0, height)));
    // Getting background
    background = hardBackground;
    background = background.get(0, 620, width, height);
  }

  public void run() {
    image(background, 0, 0);
    // brown leaves falling in the background
    displayScore();
    for (BreakLeaf l : leaves) {
      l.loop(height);
      l.fall();
      l.display();
    }
    for (Button b : buttons)  b.display();
  }

  /** Displays the time taken for the player to win, if they won */
  public void displayScore() {
    String message = null;
    if (scoreboard.success) {
      textAlign(CENTER, TOP);
      textSize(60);
      fill(0xffFAF79F);
      float timeY = yStart + 80;
      text("Your time: " + scoreboard.time, width/2, timeY);
      message = "You Won!";
    } else if (!scoreboard.success)  
      message = "You Lost!";
    textSize(100);
    // Drawing text with an outline effect
    fill(0xffFAF79F, 150);
    text(message, width/2 + 2, yStart + 2);
    text(message, width/2 - 2, yStart - 2);
    text(message, width/2 + 2, yStart - 2);
    text(message, width/2 - 2, yStart + 2);
    fill(0);
    text(message, width/2, yStart);
  }

  /** Check if a button was pressed and returns a new Screen if one was */
  public Screen onClick(float x, float y) {
    for (Button b : buttons) {
      if (b.onClick(x, y).equals("Menu"))       return(new Menu());
      else if (b.onClick(x, y).equals("Quit"))  exit();
    }
    return null;
  }

  public Scoreboard getScore() {
    return null;
  }

  public Screen gameOver() {
    return null;
  }
}
/** Creates an arraylist of Drops to give a raining effect to a Screen */
class Rain {
  final int DROP_COUNT = 200;

  ArrayList<RainDrop> drops = new ArrayList<RainDrop>();

  /** Constructor */
  Rain() {
    for (int i = 0; i < DROP_COUNT; i ++)  drops.add(new RainDrop());
  }

  /** Updates the position of the rain drops */
  public void update(Water water) {
    for (int i = 0; i < drops.size(); i ++)  drops.get(i).update(water);
  }

  /** Draws all the rain drops */
  public void display() {
    for (int i = 0; i < drops.size(); i ++)  drops.get(i).display();
  }

  /** Moves display position of each drop */
  public void translateXY(float x, float y) {
    for (int i = 0; i < drops.size(); i ++)  drops.get(i).translateXY(x, y);
  }

  /** Removes all rain drops from the arraylist */
  public void clearRain() {
    drops.clear();
  }
}

class RainDrop {
  final int COL = color(196, 230, 235, 40);  // Colour of a raindrop
  final float SIZE = 4;
  final float AY = 0.1f;

  PVector pos;
  float vy = 0.5f;
  float dropLength;

  /** Creates a raindrop with randomised position and size (length) */
  RainDrop() {
    pos = new PVector(random(0, width), random(-height, height));
    dropLength = random(8, 15);
  }

  /** Updates position of raindrop - resetting its height if it falls off the screen or hits the water surface */
  public void update(Water water) {
    vy += AY;        // adding vertical acceleration
    pos.y += vy;
    if (pos.y + dropLength > water.top || pos.y + dropLength > height) {
      pos.y = random(-height, 0);
      vy = 1;
    }
    if (pos.x > width)  pos.x -= width;
    if (pos.x < 0)      pos.x += width;
  }

  public void translateXY(float x, float y) {
    pos.set(pos.x + x, pos.y + y);
  }

  /** Draw the raindrop */
  public void display() {
    strokeWeight(SIZE);
    stroke(COL);
    line(pos.x, pos.y, pos.x, pos.y + dropLength);
  }
}
/** 
 *  Displays an HUD at the top of the Game screen 
 *  - Has two buttons: Menu, Restart
 *  - Displays player's current lives
 *  - Displays the current height of the water and player
 *  - Displays the time passed (in seconds) since the player started the current game
 */
class Scoreboard {
  // Constants
  final float HT = 60;                     // height of strip
  final int BGCOL = color(0, 0, 0, 50);  // colour of strip
  final int TIME_COL = color(255);       // colour of time text
  final int TIME_SIZE = 70;
  final float TIME_X = width - 55;
  final float TIME_Y = 6;
  final float LIVES_X = 170;
  final float LIVES_TOP = 15;
  final float LIVES_GAP = 35;
  final float DIST = 600;
  final float DIST_X = 300;
  final float DIST_Y = HT/2;
  final float START_DIST = 2440;  
  final float BUTTON_X = 40;
  final float BUTTON_Y = 30;
  final float BUTTON_W = 50;
  final float BUTTON_H = 30;

  Game game;
  ArrayList<Button> buttons = new ArrayList<Button>();
  int timer;
  int time = 0;
  int lives = 3;
  boolean success = false;
  PImage heart;

  /** Constructor */
  Scoreboard(Game game) {
    // Buttons
    buttons.add(new Button("Menu", BUTTON_X, BUTTON_Y, BUTTON_W, BUTTON_H, true));
    buttons.add(new Button("Restart", BUTTON_X + BUTTON_W + 20, BUTTON_Y, BUTTON_W + 20, BUTTON_H, true));
    this.game = game;
    timer = millis();
    heart = loadImage("heart.png");
  }

  /** Update the timer and check if the bug has lost a life */
  public void update() {
    if (millis() - timer >= 1000) {
      time ++;
      timer = millis();
    }
    if (game.loseLife())  lives --;
  }

  public void display() {
    //background
    noStroke();
    fill(BGCOL);
    rect(0, 0, width, HT);

    //drawing lives
    float x = LIVES_X;
    for (int i = 0; i < lives; i ++) {
      image(heart, x, LIVES_TOP);
      x += LIVES_GAP;
    }
    displayBugDist();
    displayTimer();
    for (Button b : buttons)    b.display();
  }

  /** Displays a horizontal bar representing the water and bug's current height */
  public void displayBugDist() {
    stroke(0, 0, 0, 40);
    strokeWeight(10);
    line(DIST_X, DIST_Y, DIST_X + DIST, DIST_Y);
    //calculating percent travelled to end
    float bugPercent = (START_DIST - game.distToEnd()) / START_DIST;
    float waterPercent = (START_DIST - game.waterDistToEnd()) / START_DIST;
    if (bugPercent < 0)     bugPercent = 0;
    if (bugPercent > 1)     bugPercent = 1;
    if (waterPercent < 0)   waterPercent = 0;
    if (waterPercent > 1)   waterPercent = 1;
    float bugL = DIST * bugPercent;
    float waterL = DIST * waterPercent;

    // Drawing bug height
    stroke(0xffD0FF64, 100);
    line(DIST_X, DIST_Y, DIST_X + bugL, DIST_Y);

    // Drawing water height
    if (waterL != 0) {
      stroke(0xff7CC8E3, 100);
      line(DIST_X, DIST_Y, DIST_X + waterL, DIST_Y);
    }

    // Purple circle representing bug
    fill(184, 142, 209);
    strokeWeight(2);
    stroke(0);
    ellipseMode(CENTER);
    ellipse(DIST_X + bugL, DIST_Y, 20, 20);
  }

  /** Displays the game timer */
  public void displayTimer() {
    textAlign(CENTER, TOP);
    fill(0);
    textSize(TIME_SIZE);
    text(time, TIME_X + 4, TIME_Y + 4);
    fill(TIME_COL);
    textSize(TIME_SIZE);
    text(time, TIME_X, TIME_Y);
  }
}
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
  public void saveState() {
    savedPos = new PVector(pos.x, pos.y);
  }

  /** Returns previously saved state of the flower */
  public void previousState() {
    pos.set(savedPos.x, savedPos.y);
  }

  /** Draws stump if it is visible on the screen */
  public void display() {
    if (pos.x > - WIDTH && pos.x < width && pos.y > - HEIGHT && pos.y < height);
    image(img, pos.x, pos.y);
  }

  /** Moves position of the stump */
  public void translateXY(float x, float y) {
    pos.set(pos.x + x, pos.y + y);
  }

  // Methods to return values
  public float surface() {
    return(pos.y + SURFACE);
  }

  public float bottom() {
    return(pos.y + BOT);
  }

  public float left() {
    return(pos.x + STUMP_LEFT);
  }

  public float right() {
    return(pos.x + STUMP_RIGHT);
  }

  public boolean isEndPoint() { 
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
  public void saveState() {
    savedPos = new PVector(pos.x, pos.y);
  }

  /** Returns previously saved state of the flower */
  public void previousState() {
    pos.set(savedPos.x, savedPos.y);
  }

  /** Draws flower if it is visible on the screen */
  public void display() {
    if (pos.x > - WIDTH && pos.x < width && pos.y > - HEIGHT && pos.y < height)
      image(img, pos.x, pos.y);
  }

  /** Moves position of flower based on bug's movement */
  public void translateXY(float x, float y) {
    pos.set(pos.x + x, pos.y + y);
    if (pos.x < -width)  pos.set(width, pos.y);
    if (pos.x > width)   pos.set(-width, pos.y);
  }

  /** Allows flower to float on the water */
  public void update(Water water, Bug bug) {
    if (bottom() >= water.top) {
      pos.set(pos.x, water.top - BOT);
      if (bug.currentSurface == this)  bug.pos.set(bug.pos.x, surface() + bug.bugFeet);
    }
  }

  // Methods to get values
  public float surface() {
    return(pos.y + SURFACE);
  }

  public float bottom() {
    return(pos.y + BOT);
  }

  public float left() {
    return(pos.x + FLOWER_LEFT);
  }

  public float right() {
    return(pos.x + FLOWER_RIGHT);
  }

  public PVector position() { 
    return pos;
  }

  public boolean isEndPoint() { 
    return realEnd;
  }
}
/** 
 *  Runs an interactive Screen that teaches the player how to play the game: 
 *  - 3 BasicLeafs and 3 BreakLeafs that the player can interact with, however they are stationary
 *  - Stationary water that will reset the screen upon touching
 *  - Stationary lotus that will not move upon landing (it will make the bug bounce off it);
 *  - Text that displays the controls and basic information about the game
 */
class Tutorial implements Screen {

  final PVector BUG_START = new PVector(width/2 + 80, height/2 + 150);

  int timer;
  int rate = 100;
  Bug bug;
  Lotus lotus;
  Water water;
  End spawn;
  Background background;
  Button button;
  ArrayList<Leaf> leaves;
  float translateYOffset = 0;
  PImage information;

  /** Constructor */
  Tutorial() {
    information = loadImage("tutorial-text.png");
    information.resize(width/3, 0);
    button = new Button("Back", width/2, 80, BIG_BUTTON.x, BIG_BUTTON.y, false);
    // Creating bug, water, and surfaces
    bug = new Bug(BUG_START);
    spawn = new End(BUG_START.x - 200, BUG_START.y - bug.bugFeet - End.SURFACE);
    water = new Water(height - 100, width, height * 2);
    lotus = new Lotus(100, water.top);
    background = new Background("easy", EASY_BGCOL);
    background.bgPos1.set(background.bgPos1.x, 1000);
    background.bgPos2.set(background.bgPos2.x, 1000);
    leaves = new ArrayList<Leaf>() {
      { 
        add(new BreakLeaf(350, 360));
        add(new BreakLeaf(800, 330)); 
        add(new BreakLeaf(420, 270));
        add(new BasicLeaf(650, 290, false)); 
        add(new BasicLeaf(850, 450, false)); 
        add(new BasicLeaf(760, 150, false));
      }
    };
    // Adjusting values to make objects stationary
    for (Leaf l : leaves)  l.setSpeedZero();
    water.riseRate = 0;
    lotus.vxSpeed = 0;
    spawn.realEnd = false;
    bug.currentSurface = spawn;
  }

  /** Runs the tutorial - Similar to running the Game */
  public void run() {
    if (bug.currentSurface == lotus)  bug.bounce();

    // Controlling bug movement
    for (String dir : keys.keySet()) 
      if (keys.get(dir))   bug.move(dir);

    translateYOffset = 0;
    bug.update();
    lotus.update(water);   // updating animation of lotus

    translateAll(-bug.horizSpeed(), -bug.vertSpeed());

    // Checking if the bug will fall through the surface of an object
    if (!bug.isOnSurface())  translateYOffset = bug.through(spawn); 
    if (!bug.isOnSurface())  translateYOffset = bug.through(lotus);
    for (int i = 0; i < leaves.size(); i ++) {
      Leaf leaf = leaves.get(i);
      if (!bug.isOnSurface()) {
        translateYOffset = bug.through(leaf);
        if (translateYOffset != 0)   bug.currentLeaf = leaf;
      }
    }

    // Check if the bug should fall off its current surface
    if (bug.isOnSurface())    bug.fallOff(false);

    if (translateYOffset != 0) { // if bug landed on something
      bug.setVYZero();
      translateAll(0, translateYOffset);
    }

    for (int i = 0; i < leaves.size(); i ++) {
      Leaf leaf = leaves.get(i);
      if (leaf.dead(water.top)) {
        if (leaf == bug.currentLeaf)  bug.fallOff(true); 
        leaves.remove(leaf);
        i --;
      }
    }

    /** Redraw everything */
    if (millis() - timer >= rate) {
      displayAll();
      timer = millis();
    }
  }

  /** Adjusts the display position */
  public void translateAll(float x, float y) {
    background.translateXY(x, -y);
    spawn.translateXY(x, y);
    water.translateY(y);
    lotus.translateXY(x, y);
    for (Leaf l : leaves)  l.translateXY(x, y);
  }

  /** Display all objects */
  public void displayAll() {
    background.display();
    water.display();
    lotus.display();
    spawn.display();
    for (Leaf l : leaves)   l.display();
    bug.display();
    button.display();

    // Display information text on top
    float margin = 30;
    image(information, margin, margin);
  }

  /** Checks if the Back button was pressed */
  public Screen onClick(float x, float y) {
    if (button.onClick(x, y).equals("Back"))  return(new Menu()); 
    return null;
  }

  /** Resets the Tutorial if the bug falls in the water and dies */
  public Screen gameOver() {
    if (bug.died(water.top)) return new Tutorial();
    return null;
  }

  public Scoreboard getScore() { 
    return null;
  }
}
/**
 *  Creates a Water object
 *  Properties:
 *  - Water level rises over time
 *  - If the bug hits the surface, it will die
 *  - Lotuses and the end flower float on the surface of the water
 */
class Water {
  final int COLOUR = color(0xff369EB2, 50);

  float top, depth, waterWidth;
  float savedTop, savedDepth;
  float riseRate = 0.5f;

  /** Constructor */
  Water(float t, float w, float d) {
    depth = d;
    savedDepth = depth;
    waterWidth = w;
    top = t;
    savedTop = top;
  }

  public void display() {
    // Drawing rectangle to represent the water
    noStroke();
    fill(COLOUR);
    rectMode(CORNER);
    rect(0, top, waterWidth, depth);
    // drawing white line showing the surface of the water
    strokeWeight(3);
    stroke(255, 255, 255, 100);
    line(0, top, waterWidth, top);
  }

  /** Saves the current state of the water (depth and position of surface of water) */
  public void saveState() {
    savedTop = top;
    savedDepth = depth;
  }

  /** Returns water to its saved state */
  public void previousState() {
    top = savedTop;
    depth = savedDepth;
  }

  /** Moves the top of the water upward to give the effect of rising water */
  public void rise() {
    top -= riseRate;
  }

  /** Adjusts the display position of the water */
  public void translateY(float y) {
    top += y;
  }
}
  public void settings() {  size(1000, 700); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "FlashFlood" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

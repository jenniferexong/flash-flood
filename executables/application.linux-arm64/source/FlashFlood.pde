/** Main */

// Constants
public static final PVector BIG_BUTTON = new PVector(250, 50);
public final color EASY_BGCOL = color(135, 205, 181);
public final color HARD_BGCOL = color(113, 157, 170);

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

void setup() {
  size(1000, 700);
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

void draw() {
  currentScreen.run();
  Screen newScreen = currentScreen.gameOver();
  if (newScreen != null)  currentScreen = newScreen;
}

/** 
 *  Checks if the mouse has been pressed on a button on a Screen: 
 *  If a button was pressed, a new Screen is returned and run() in the draw() method
 */
void mousePressed() {
  Screen newScreen = currentScreen.onClick(mouseX, mouseY);
  if (newScreen != null)  currentScreen = newScreen;
}

/** 
 *   Allows keys to control bug movement - Only needed during gameplay/tutorial
 */
void keyPressed() {
  if (!(currentScreen instanceof Game) && !(currentScreen instanceof Tutorial)) return;
  if ((key == 'w' || key == 'W' || keyCode == UP))       keys.put("up", true);
  else if (key == 'd' || key == 'D' || keyCode == RIGHT) keys.put("right", true);
  else if (key == 'a' || key == 'A' || keyCode == LEFT)  keys.put("left", true);
}

void keyReleased() {
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

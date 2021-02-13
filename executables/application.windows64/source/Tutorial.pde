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
  void run() {
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
  void translateAll(float x, float y) {
    background.translateXY(x, -y);
    spawn.translateXY(x, y);
    water.translateY(y);
    lotus.translateXY(x, y);
    for (Leaf l : leaves)  l.translateXY(x, y);
  }

  /** Display all objects */
  void displayAll() {
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
  Screen onClick(float x, float y) {
    if (button.onClick(x, y).equals("Back"))  return(new Menu()); 
    return null;
  }

  /** Resets the Tutorial if the bug falls in the water and dies */
  Screen gameOver() {
    if (bug.died(water.top)) return new Tutorial();
    return null;
  }

  Scoreboard getScore() { 
    return null;
  }
}

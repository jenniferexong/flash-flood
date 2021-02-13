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
    if (hardMode) water.riseRate = 0.7;
    bug.currentSurface = start;
    surfaces.addAll(lotuses);
    surfaces.add(start);
    surfaces.add(end);
  }

  /** Runs the game */
  void run() {

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

  void updateAll() {
    bug.update();
    water.rise();
    end.update(water, bug);
    rain.update(water);
    for (Lotus lotus : lotuses)  lotus.update(water);
    for (Leaf leaf : leaves)     leaf.fall();
    scoreboard.update();
  }

  /** Move display position of everything */
  void translateAll(float x, float y) {
    background.translateXY(x, -y);
    start.translateXY(x, y);
    end.translateXY(x, y);
    for (Lotus lotus : lotuses)  lotus.translateXY(x, y);
    for (Leaf l : leaves)        l.translateXY(x, y);
    water.translateY(y);
    rain.translateXY(x, y);
  }

  /** Drawing everything */
  void displayAll() {
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
  void spawnLeaf() {
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

  boolean loseLife() {
    if (bug.died(water.top)) {
      previousState();
      respawnCount = 3;
      respawned = true;
      return true;
    }
    return(false);
  }

  /** Saves the current state of the game */
  void saveState() {
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
  void previousState() {
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
  Screen gameOver() {
    if ((bug.currentSurface == end && water.top <= 0)) {
      scoresList.addScore(scoreboard.time, hardMode);
      scoreboard.success = true;
      return new GameOver(scoreboard);
    } else if (scoreboard.lives == 0) return new GameOver(scoreboard);
    return null;
  }

  /** Pauses the screen and displays a respawn countdown */
  void respawnDelay() {
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

  Screen onClick(float x, float y) {
    for (Button b : scoreboard.buttons) {
      if (b.onClick(x, y).equals("Menu"))         return new Menu();
      else if (b.onClick(x, y).equals("Restart")) return new Game(hardMode);
    }
    return null;
  }

  float distToEnd() { 
    return bug.bot() - end.bottom();
  }

  float waterDistToEnd() { 
    return water.top - end.bottom();
  }

  Scoreboard getScore() { 
    return scoreboard;
  }
}

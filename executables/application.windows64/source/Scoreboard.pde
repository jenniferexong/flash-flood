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
  final color BGCOL = color(0, 0, 0, 50);  // colour of strip
  final color TIME_COL = color(255);       // colour of time text
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
  void update() {
    if (millis() - timer >= 1000) {
      time ++;
      timer = millis();
    }
    if (game.loseLife())  lives --;
  }

  void display() {
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
  void displayBugDist() {
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
    stroke(#D0FF64, 100);
    line(DIST_X, DIST_Y, DIST_X + bugL, DIST_Y);

    // Drawing water height
    if (waterL != 0) {
      stroke(#7CC8E3, 100);
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
  void displayTimer() {
    textAlign(CENTER, TOP);
    fill(0);
    textSize(TIME_SIZE);
    text(time, TIME_X + 4, TIME_Y + 4);
    fill(TIME_COL);
    textSize(TIME_SIZE);
    text(time, TIME_X, TIME_Y);
  }
}
